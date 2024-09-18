// JavaScript version of the Inlet class
class Inlet {
    constructor() {
        this.panel1 = null;
        this.graph1 = null;
    }

    init() {
        // Set up layout and elements
        const container = document.createElement('div');
        container.style.display = 'flex';
        container.style.flexDirection = 'column';

        // Spacer
        const spacer = document.createElement('div');
        spacer.textContent = ' ';
        container.appendChild(spacer);

        // Create the graph container
        this.graph1 = new PlotIPlot();
        container.appendChild(this.graph1.createPlot());

        // Create input panel
        this.panel1 = new InputIPanel(this.graph1);
        container.appendChild(this.panel1.createPanel());

        // Append to wave-calculator div in the HTML
        document.getElementById('wave-calculator').appendChild(container);
    }
}

// InputIPanel class
class InputIPanel {
    constructor(panel) {
        this.panel = panel;
        this.Ain = null;
        this.Oin = null;
        this.din = null;
        this.lin = null;
        this.Win = null;

        this.ao = 0;
        this.B = 0;
        this.W = 0;
        this.el = 0;
        this.d = 0;
        this.A = 0;

        this.Calculate = null;
        this.Reset = null;
        this.K = 0;
        this.sig = 0;
        this.Ac = 0;
        this.g = 0;
        this.Ken = 0;
        this.Kex = 0;
        this.f = 0;
    }

    createPanel() {
        const panel = document.createElement('div');
        panel.style.display = 'grid';
        panel.style.gridTemplateColumns = '1fr 1fr';
        panel.style.gap = '10px';

        const elements = [
            { label: "Input Inlet Data:", input: false },
            { label: "", input: false },
            { label: "Inlet Depth(m)?", input: "6.0" },
            { label: "Inlet Width ?", input: "100." },
            { label: "Inlet Length?", input: "1000." },
            { label: "Bay Planform Area? (km^2)", input: "50." },
            { label: "Ocean Tide Amplitude?", input: "1.0" },
        ];

        elements.forEach(({ label, input }) => {
            const labelElement = document.createElement('label');
            labelElement.textContent = label;
            panel.appendChild(labelElement);

            if (input) {
                const inputElement = document.createElement('input');
                inputElement.type = 'text';
                inputElement.value = input;
                panel.appendChild(inputElement);

                if (label.includes('Inlet Depth')) this.din = inputElement;
                if (label.includes('Inlet Width')) this.Win = inputElement;
                if (label.includes('Inlet Length')) this.lin = inputElement;
                if (label.includes('Bay Planform Area')) this.Ain = inputElement;
                if (label.includes('Ocean Tide Amplitude')) this.Oin = inputElement;
            } else {
                panel.appendChild(document.createElement('div'));
            }
        });

        // Add buttons
        this.Calculate = document.createElement('button');
        this.Calculate.textContent = 'Calculate';
        this.Calculate.onclick = () => this.calculateTide();
        panel.appendChild(this.Calculate);

        this.Reset = document.createElement('button');
        this.Reset.textContent = 'Reset';
        this.Reset.onclick = () => this.resetForm();
        panel.appendChild(this.Reset);

        return panel;
    }

    calculateTide() {
        // Handle calculations
        try {
            this.ao = parseFloat(this.Oin.value);
            this.A = parseFloat(this.Ain.value) * 1000 * 1000;
            this.d = parseFloat(this.din.value);
            this.el = parseFloat(this.lin.value);
            this.W = parseFloat(this.Win.value);
            this.g = 9.81;
            this.sig = 2 * Math.PI / 45144;
            this.Ken = 0.2;
            this.Kex = 1.0;

            const Rh = this.d * this.W / (this.W + 2 * this.d);
            this.f = 0.01;
            const denom = Math.sqrt(this.Ken + this.Kex + this.f * this.el / (4 * Rh));
            this.K = this.W * this.d * Math.sqrt(2 * this.g * this.ao) / (this.sig * this.ao * this.A * denom);

            this.panel.do_wave(this.K);
        } catch (e) {
            console.error(e);
        }
    }

    resetForm() {
        this.Ain.value = '';
        this.lin.value = '';
        this.din.value = '';
        this.Win.value = '';
        this.Oin.value = '';
    }
}

// PlotIPlot class
class PlotIPlot {
    constructor() {
        this.canvas = null;
        this.ctx = null;
        this.bath1 = null;
        this.bath2 = null;
    }

    createPlot() {
        this.canvas = document.createElement('canvas');
        this.canvas.width = 600;
        this.canvas.height = 550;
        this.canvas.style.border = '1px solid black';
        this.ctx = this.canvas.getContext('2d');

        return this.canvas;
    }

    do_wave(K) {
        // Variables for eta values
        const etao = new Array(240).fill(0);
        const etab = new Array(240).fill(0);
    
        let dt = Math.PI / 30;
        etab[0] = 0;
        
        // Runge-Kutta calculation
        for (let i = 0; i < 239; i++) {
            const t = (i + 1) * dt;
            etao[i] = Math.sin(t);
            let arg = etao[i] - etab[i];
            let yp1 = dt * K * Math.sqrt(Math.abs(arg)) * arg / Math.abs(arg);
            const etaohalf = Math.sin(t + dt / 2);
            arg = etaohalf - (etab[i] + yp1 / 2);
            let yp2 = dt * K * Math.sqrt(Math.abs(arg)) * arg / Math.abs(arg);
            arg = etaohalf - (etab[i] + yp2 / 2);
            let yp3 = dt * K * Math.sqrt(Math.abs(arg)) * arg / Math.abs(arg);
            arg = Math.sin(t + dt) - (etab[i] + yp3);
            let yp4 = dt * K * Math.sqrt(Math.abs(arg)) * arg / Math.abs(arg);
            etab[i + 1] = etab[i] + (yp1 + 2 * yp2 + 2 * yp3 + yp4) / 6;
        }
    
        const n = 60;
        const etaotrunc = new Array(60);
        const etabtrunc = new Array(60);
    
        let etabmax = 0;
        let imax = 0;
        
        // Truncate results to the last 60 values
        for (let i = 179; i < 239; i++) {
            etaotrunc[i - 179] = etao[i];
            etabtrunc[i - 179] = etab[i];
            if (etab[i] > etabmax) {
                etabmax = etab[i];
                imax = i;
            }
        }
        imax = imax - 179;
        
        const phase = imax * dt - Math.PI / 2;
        
        // Call Bathymetry to create the bath1 and bath2 objects with calculated data
        this.bath1 = new Bathymetry();
        this.bath2 = new Bathymetry();
        this.bath1.Bathymetry(n, dt, etaotrunc, phase);
        this.bath2.Bathymetry(n, dt, etabtrunc, etabmax);

        // Draw the waves
        this.drawWave(K);
    }

    drawWave(K) {
        const ctx = this.ctx;
        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height); // Clear canvas

        const iwave = this.canvas.height / 2;
        const dmax = 1.0;
        const offset = 5;

        const x = [];
        const tideoi = [];
        const tidebi = [];

        const npts = this.bath1.NPTS;
        const length = this.canvas.width;
        const xmax = 2 * Math.PI;

        // Calculate coordinates for plotting
        for (let i = 0; i < npts; i++) {
            x[i] = (i * this.bath1.DX * length) / xmax;
            tideoi[i] = offset + iwave + ((-this.bath1.h[i] * (this.canvas.height - 2 * offset - iwave)) / dmax);
            tidebi[i] = offset + iwave + ((-this.bath2.h[i] * (this.canvas.height - 2 * offset - iwave)) / dmax);
        }

        // Draw Ocean wave (in blue)
        ctx.strokeStyle = 'blue';
        ctx.beginPath();
        for (let i = 1; i < npts; i++) {
            ctx.moveTo(x[i - 1], tideoi[i - 1]);
            ctx.lineTo(x[i], tideoi[i]);
        }
        ctx.stroke();

        // Draw Bay wave (in gray)
        ctx.strokeStyle = 'gray';
        ctx.beginPath();
        for (let i = 1; i < npts; i++) {
            ctx.moveTo(x[i - 1], tidebi[i - 1]);
            ctx.lineTo(x[i], tidebi[i]);
        }
        ctx.stroke();

        // Display K value, phase, and response
        ctx.fillStyle = 'black';
        ctx.fillText(`K = ${K.toFixed(2)}`, length - 110, 45);
        ctx.fillText(`Phase = ${this.bath1.slope.toFixed(2)} deg`, length - 110, 60);
        ctx.fillText(`Response = ${this.bath2.slope.toFixed(2)}`, length - 110, 75);
    }
}

class Bathymetry {
    constructor() {
        this.NPTS = 0;
        this.DX = 0;
        this.h = [];
        this.slope = 0;
    }

    Bathymetry(n, dt, eta, slopeValue) {
        this.NPTS = n;
        this.DX = dt;
        this.h = [...eta]; // Copy eta values
        this.slope = slopeValue;
    }
}

// Initialize the application
window.onload = () => {
    const inletApp = new Inlet();
    inletApp.init();
};
