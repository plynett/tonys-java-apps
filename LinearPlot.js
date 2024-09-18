// LinearPlot.js

// Define the main applet class
class LinearPlot {
    constructor() {
        this.H = 0;
        this.d = 1;
        this.L = 0;
        this.T = 0;
        this.jstart = 0;
        this.init();
    }

    init() {
        // Create main container
        const appContainer = document.getElementById('app-container');
        appContainer.style.display = 'flex';

        // Create input panel
        this.inputPanel = new InputPanel(this);
        appContainer.appendChild(this.inputPanel.element);

        // Create plot panel
        this.plotPanel = new PlotPanel();
        appContainer.appendChild(this.plotPanel.canvas);
    }
}

// Define the InputPanel class
class InputPanel {
    constructor(mainApp) {
        this.mainApp = mainApp;
        this.stop = false;
        this.createElements();
    }

    createElements() {
        this.element = document.createElement('div');
        this.element.style.width = '200px';
        this.element.style.padding = '10px';
    
        const label = document.createElement('h3');
        label.innerText = 'Input Wave Data:';
        this.element.appendChild(label);
    
        // Wave Height input (with default value)
        this.element.appendChild(this.createLabel('Wave Height (m)?'));
        this.Hin = this.createInput('2'); // Default value of 1.5 meters for Wave Height
        this.element.appendChild(this.Hin);
    
        // Wave Period input (with default value)
        this.element.appendChild(this.createLabel('Wave Period?'));
        this.Tin = this.createInput('6'); // Default value of 10 seconds for Wave Period
        this.element.appendChild(this.Tin);
    
        // Local Depth input (with default value)
        this.element.appendChild(this.createLabel('Local Depth?'));
        this.din = this.createInput('10'); // Default value of 30 meters for Local Depth
        this.element.appendChild(this.din);
    
        // Buttons
        const calculateButton = document.createElement('button');
        calculateButton.innerText = 'Calculate';
        calculateButton.onclick = () => this.handleCalculate();
        this.element.appendChild(calculateButton);
    
        const stopButton = document.createElement('button');
        stopButton.innerText = 'Stop';
        stopButton.onclick = () => this.handleStop();
        this.element.appendChild(stopButton);
    }    

    createLabel(text) {
        const label = document.createElement('label');
        label.innerText = text;
        return label;
    }

    createInput(defaultValue = '') {
        const input = document.createElement('input');
        input.type = 'text';
        input.style.width = '100%';
        input.style.marginBottom = '10px';
        input.value = defaultValue;  // Set the default value here
        return input;
    }
    

    handleCalculate() {
        try {
            let H = parseFloat(this.Hin.value);
            let T = parseFloat(this.Tin.value);
            let d = parseFloat(this.din.value);

            if (isNaN(H) || isNaN(T) || isNaN(d)) {
                alert('Please enter valid numbers for all fields.');
                return;
            }

            if (H > 0.8 * d) {
                H = 0.8 * d;
                this.Hin.value = `${H}, breaking`;
            }

            const ref = new Refract();
            const klin = ref.waveNumber(d, T);
            const L = 2 * Math.PI / klin;

            this.stop = false;
            this.mainApp.plotPanel.setStop(this.stop);
            this.mainApp.plotPanel.doWave(H, L, d, T);

        } catch (error) {
            console.error(error);
        }
    }

    handleStop() {
        this.stop = true;
        this.mainApp.plotPanel.setStop(this.stop);
    }
}

// Define the PlotPanel class
class PlotPanel {
    constructor() {
        this.canvas = document.createElement('canvas');
        this.canvas.width = 600;
        this.canvas.height = 300;
        this.canvas.style.border = '1px solid black';

        this.ctx = this.canvas.getContext('2d');
        this.first = false;
        this.stop = false;
        this.thread = null;
    }

    setStop(stop) {
        this.stop = stop;
        if (this.stop && this.thread) {
            clearInterval(this.thread);
            this.thread = null;
        }
    }

    doWave(H, L, d, T) {
        this.wavePlot = new WavePlot(this.canvas.width, this.canvas.height);
        this.wavePlot.initialize(H, L, d, T);
        this.wavePlot.doWave();

        if (this.thread) {
            clearInterval(this.thread);
        }

        this.thread = setInterval(() => {
            if (!this.stop) {
                this.wavePlot.doWave();
                this.paint();
            } else {
                clearInterval(this.thread);
            }
        }, 150);
    }

    paint() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.wavePlot.draw(this.ctx);
    }
}

// Define the WavePlot class
class WavePlot {
    constructor(width, height) {
        this.length = width;
        this.totalh = height;
        this.x = [];
        this.y = [];
        this.ncount = 10;
        this.jstep = 0;

        this.uc = [];
        this.ut = [];
        this.vc = [];
        this.vt = [];
        this.xp = [];
        this.yp = [];
        this.umag = [];
        this.vmag = [];
        this.depth = [];
    }

    initialize(H, L, d, T) {
        this.H = H;
        this.L = L;
        this.d = d;
        this.T = T;
        this.dt = T / 30;
        this.offset = 5;

        const tempL = L > 1 ? Math.floor(L * 100) / 100 : Math.floor(L * 1000) / 1000;
        this.waveLength = `L= ${tempL} m`;

        this.famplitude = (H / 2) / (d + H / 2) * (this.totalh - this.offset);
        this.amplitude = this.famplitude;
        this.dz = (this.totalh - this.amplitude - this.offset) / (this.ncount - 1);
    }

    doWave() {
        const sigma = (2 * Math.PI) / this.T;
        const k = (2 * Math.PI) / this.L;

        // Calculate wave profile
        for (let i = 0; i < this.length; i++) {
            const arg = (2 * Math.PI * i / this.length) - sigma * this.jstep * this.dt;
            this.x[i] = this.offset + this.amplitude - (this.amplitude * Math.cos(arg));
            this.y[i] = i;
        }

        // Calculate particle velocities
        const chd = Hyper.cosh(k * this.d);
        const cos = Math.cos(-sigma * this.jstep * this.dt);
        const sin = Math.sin(-sigma * this.jstep * this.dt);

        for (let i = 0; i < this.ncount; i++) {
            const dep = ((this.ncount - 1 - i) * this.d) / (this.ncount - 1);
            this.depth[i] = this.offset + this.amplitude + (i * this.dz);

            this.umag[i] = (this.totalh * Hyper.cosh(k * dep)) / (chd * 15);
            this.vmag[i] = (this.totalh * Hyper.sinh(k * dep)) / (chd * 15);

            this.uc[i] = this.umag[i] * cos;
            this.xp[i] = this.umag[i] * sin;
            this.ut[i] = -this.uc[i];
            this.vc[i] = this.vmag[i] * sin;
            this.yp[i] = -this.vmag[i] * cos;
            this.vt[i] = -this.vc[i];
        }

        const factor = (9.81 * k * 15 * (this.H / 2)) / (sigma * this.totalh);

        this.umax = `u_max= ${Math.floor(this.umag[0] * factor * 100) / 100} m/s`;
        this.vmax = `v_max= ${Math.floor(this.vmag[0] * factor * 100) / 100} m/s`;

        this.jstep += 1;
    }

    draw(g) {
        const xl = this.length - 1;
        const yl = this.totalh - 1;

        // Draw axes
        g.strokeStyle = 'black';
        g.beginPath();
        g.moveTo(0, this.amplitude + this.offset);
        g.lineTo(this.length, this.amplitude + this.offset); // x-axis
        g.moveTo(0, 0);
        g.lineTo(0, this.totalh); // z-axis
        g.stroke();

        // Draw wave profile
        g.fillStyle = 'blue';
        g.beginPath();
        g.moveTo(this.y[0], this.x[0]);
        for (let i = 1; i < this.length; i++) {
            g.lineTo(this.y[i], this.x[i]);
        }
        g.lineTo(this.length, this.totalh);
        g.lineTo(0, this.totalh);
        g.closePath();
        g.fill();

        // Draw particle velocities and ovals
        for (let i = 0; i < this.ncount; i++) {
            const Depth = this.depth[i];

            // Velocity lines
            g.strokeStyle = 'white';
            g.beginPath();
            g.moveTo(0, Depth);
            g.lineTo(this.uc[i], Depth - this.vc[i]);
            g.stroke();

            g.beginPath();
            g.moveTo(xl / 2, Depth);
            g.lineTo(xl / 2 + this.ut[i], Depth - this.vt[i]);
            g.stroke();

            g.beginPath();
            g.moveTo(xl, Depth);
            g.lineTo(xl + this.uc[i], Depth - this.vc[i]);
            g.stroke();

            // Elliptical paths
            g.strokeStyle = 'black';
            g.beginPath();
            g.ellipse(xl / 2, Depth, this.umag[i], this.vmag[i], 0, 0, 2 * Math.PI);
            g.stroke();

            g.beginPath();
            g.ellipse(0, Depth, this.umag[i], this.vmag[i], 0, 0, 2 * Math.PI);
            g.stroke();

            g.beginPath();
            g.ellipse(xl, Depth, this.umag[i], this.vmag[i], 0, 0, 2 * Math.PI);
            g.stroke();

            // Particle positions
            g.fillStyle = 'red';
            g.beginPath();
            g.arc(0 - this.xp[i], Depth + this.yp[i], 2, 0, 2 * Math.PI);
            g.fill();

            g.beginPath();
            g.arc(xl / 2 + this.xp[i], Depth - this.yp[i], 2, 0, 2 * Math.PI);
            g.fill();

            g.beginPath();
            g.arc(xl - this.xp[i], Depth + this.yp[i], 2, 0, 2 * Math.PI);
            g.fill();
        }

        // Draw wave length and velocities
        g.fillStyle = 'yellow';
        g.font = '16px Arial';
        g.fillText(this.waveLength, this.length - 130, this.amplitude + 80);
        g.fillText(this.umax, this.length - 130, this.amplitude + 100);
        g.fillText(this.vmax, this.length - 130, this.amplitude + 120);
    }
}

// Instantiate the applet
window.onload = function() {
    new LinearPlot();
};
