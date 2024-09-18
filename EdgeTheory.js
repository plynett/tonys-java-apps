class EdgeTheory {
    constructor() {
        this.init();
    }

    init() {
        const container = document.getElementById('wave-calculator');
        container.style.display = 'flex';

        // Create input panel
        this.inputPanel = new InputETPanel(this);
        container.appendChild(this.inputPanel.getElement());

        // Create graph panel for wave plot
        this.graphPanel = new PlotETPlot();
        container.appendChild(this.graphPanel.getElement());
    }

    reset() {
        this.graphPanel.reset();
        this.inputPanel.reset();
    }
}

class InputETPanel {
    constructor(parent) {
        this.parent = parent;
        this.createPanel();
    }

    createPanel() {
        const panel = document.createElement('div');
        panel.style.margin = '10px';
        panel.style.width = '250px';
        panel.style.display = 'grid';
        panel.style.gridTemplateColumns = '1fr';
        panel.style.gap = '10px';

        // Input fields
        this.Tin = this.createTextField('Wave Period (sec)?', '3.0');
        this.din = this.createTextField('Mean Slope?', '0.1');
        this.xmaxin = this.createTextField('Max Offshore Distance (m)?', '5.0');

        // Calculate Button
        this.calculateBtn = this.createButton('Calculate', () => this.calculate());

        // Stop Button
        this.stopBtn = this.createButton('Stop', () => this.stop());

        panel.appendChild(this.Tin.container);
        panel.appendChild(this.din.container);
        panel.appendChild(this.xmaxin.container);
        panel.appendChild(this.calculateBtn);
        panel.appendChild(this.stopBtn);

        this.element = panel;
    }

    createTextField(label, defaultValue) {
        const container = document.createElement('div');
        container.style.display = 'flex';
        container.style.flexDirection = 'column';

        const inputLabel = document.createElement('label');
        inputLabel.innerText = label;

        const input = document.createElement('input');
        input.type = 'text';
        input.value = defaultValue;

        container.appendChild(inputLabel);
        container.appendChild(input);

        return { container, input };
    }

    createButton(label, onClick) {
        const button = document.createElement('button');
        button.innerText = label;
        button.onclick = onClick;
        return button;
    }

    calculate() {
        const T = parseFloat(this.Tin.input.value);
        const slope = parseFloat(this.din.input.value);
        const xmax = parseFloat(this.xmaxin.input.value);

        // Validate inputs
        if (isNaN(T) || isNaN(slope) || isNaN(xmax)) {
            alert("Please enter valid numbers.");
            return;
        }

        const bath = new Bathymetry(slope, xmax);
        this.parent.graphPanel.doWave(T, slope, bath, xmax);
    }

    stop() {
        this.parent.graphPanel.setStop(true);
    }

    reset() {
        this.Tin.input.value = '3.0';
        this.din.input.value = '0.1';
        this.xmaxin.input.value = '5.0';
    }

    getElement() {
        return this.element;
    }
}

class PlotETPlot {
    constructor() {
        this.canvas = document.createElement('canvas');
        this.canvas.width = 600;
        this.canvas.height = 400;
        this.ctx = this.canvas.getContext('2d');
        this.first = false;
        this.stop = false;
    }

    getElement() {
        return this.canvas;
    }

    setStop(stopp) {
        this.stop = stopp;
    }

    doWave(T, slope, bath, xmax) {
        this.wavePlot = new WaveETPlot(this.canvas.width, this.canvas.height, xmax);
        this.wavePlot.initialize(slope, T, bath);
        this.animate();
    }

    animate() {
        if (!this.stop) {
            this.wavePlot.doWave();
            this.wavePlot.draw(this.ctx);
            requestAnimationFrame(() => this.animate());
        }
    }

    reset() {
        this.jstep = 0;  // Reset time step
        this.depth = [];  // Clear depth array
        this.vect = Array.from({ length: 5 }, () => []);  // Clear vectors
        this.ctx.clearRect(0, 0, this.length, this.height);  // Clear canvas
    }
    
}

class Bathymetry {
    constructor(slope, xmax) {
        this.npts = 200;
        this.dx = xmax / (this.npts - 1);
        this.h = Array.from({ length: this.npts }, (_, i) => slope * this.dx * i);
        this.slope = slope;
    }
}

class WaveETPlot {
    constructor(width, height, xmax) {
        this.length = width;
        this.height = height;
        this.npts = 200;
        this.xmax = xmax;
        this.jstep = 0;
        this.iwave = height / 3;
        this.offset = 5;
        this.depth = [];
        this.vect = Array.from({ length: 5 }, () => []);
        this.lambda = [0, 0, 0, 0, 0];
    }

    initialize(slope, T, bath) {
        this.T = T;
        this.dt = T / 300;  // Set the time step (smaller for smoother animation)
        this.jstep = 0;
    
        // Store the base amplitude
        this.ewave = Array.from({ length: 5 }, () => []);  // Base wave amplitude
    
        const g = 9.81;
        for (let n = 0; n < 5; n++) {
            this.lambda[n] = (2 * Math.PI / T) ** 2 / (g * (2 * n + 1) * slope);
    
            // Calculate and store the base amplitude of the wave
            for (let i = 0; i < bath.npts; i++) {
                this.ewave[n][i] = -Math.exp(-this.lambda[n] * bath.h[i]);
                this.vect[n][i] = this.ewave[n][i];  // Initialize the vect array with the base amplitude
            }
        }
    }
    
    

    doWave() {
        this.jstep += 1;
        const sigma = 2 * Math.PI / this.T;
    
        // Modulate the wave phase over time without modifying the base amplitude
        for (let n = 0; n < 4; n++) {
            for (let i = 0; i < this.npts; i++) {
                this.vect[n][i] = this.ewave[n][i] * Math.cos(sigma * this.jstep * this.dt);
            }
        }
    }
    

    draw(ctx) {
        ctx.clearRect(0, 0, this.length, this.height);
    
        // Draw axis lines
        ctx.strokeStyle = 'black';
        ctx.beginPath();
        ctx.moveTo(0, this.height);
        ctx.lineTo(this.length, this.height);
        ctx.stroke();
    
        const iwave = this.iwave;
        for (let n = 0; n < 3; n++) {
            ctx.strokeStyle = ['red', 'blue', 'green'][n];
            for (let i = 1; i < this.npts; i++) {
                ctx.beginPath();
                ctx.moveTo((i - 1) * this.length / this.npts, iwave + this.vect[n][i - 1] * iwave);
                ctx.lineTo(i * this.length / this.npts, iwave + this.vect[n][i] * iwave);
                ctx.stroke();
            }
        }
    
        // Draw wavenumber text (Mode 0, Mode 1, Mode 2)
        ctx.fillStyle = 'red';
        ctx.fillText(`Mode 0 = ${this.lambda[0].toFixed(5)}`, this.length - 150, 20);
    
        ctx.fillStyle = 'blue';
        ctx.fillText(`Mode 1 = ${this.lambda[1].toFixed(5)}`, this.length - 150, 35);
    
        ctx.fillStyle = 'green';
        ctx.fillText(`Mode 2 = ${this.lambda[2].toFixed(5)}`, this.length - 150, 50);
    
        // Draw offshore distance text
        ctx.fillStyle = 'black';
        ctx.fillText(`x = ${this.xmax}`, this.length - 65, iwave + 15);
    }
    
}

// Initialize the app
window.onload = () => {
    new EdgeTheory();
};
