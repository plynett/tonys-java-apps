class Streamless {
    constructor() {
        this.eta = [];
        this.theta = [];
        this.panel1 = null;
        this.graph1 = null;
        this.init();
    }

    init() {
        const container = document.getElementById('wave-calculator');
        container.style.display = 'flex';

        // Create graph panel
        this.graph1 = new PlotStPanel();
        container.appendChild(this.graph1.getElement());

        // Create input panel
        this.panel1 = new StreamPanel(this.graph1);
        container.appendChild(this.panel1.getElement());
    }
}

class StreamPanel {
    constructor(graphPanel) {
        this.graphPanel = graphPanel;
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
        this.Hin = this.createTextField('Wave Height (m)?', '8');
        this.Tin = this.createTextField('Wave Period?', '10');
        this.din = this.createTextField('Local Depth?', '12');
        this.NNin = this.createTextField('Theory Order?', '5');
        this.dampin = this.createTextField('Damping', '.3');

        // Calculate and Reset buttons
        this.calculateBtn = this.createButton('Calculate', () => this.calculate());
        this.resetBtn = this.createButton('Reset', () => this.reset());

        // Append elements to the panel
        panel.appendChild(this.Hin.container);
        panel.appendChild(this.Tin.container);
        panel.appendChild(this.din.container);
        panel.appendChild(this.NNin.container);
        panel.appendChild(this.dampin.container);
        panel.appendChild(this.calculateBtn);
        panel.appendChild(this.resetBtn);

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
        const H = parseFloat(this.Hin.input.value);
        const T = parseFloat(this.Tin.input.value);
        const d = parseFloat(this.din.input.value);
        const damp = parseFloat(this.dampin.input.value);
        const NN = parseInt(this.NNin.input.value);

        Wave.setValues(H, T, d);  // Set the wave parameters before calling Stream1

        if (isNaN(H) || isNaN(T) || isNaN(d) || isNaN(NN) || isNaN(damp)) {
            alert('Please enter valid inputs.');
            return;
        }

        const output = new Streamout(NN, 41); // Assume NTHTS = 41
        output.damp = damp;

        const stream = new Stream1();
        stream.Stream1(output);

        Wave.L = output.X[0];
        Wave.etac = output.eta[0];

        this.graphPanel.do_wave(output);
        this.getCoefsForTA(output);
    }

    reset() {
        this.Hin.input.value = '';
        this.Tin.input.value = '';
        this.din.input.value = '';
        this.NNin.input.value = '';
        this.dampin.input.value = '.3';
    }

    getCoefsForTA(output) {
        let coefText = '';
        coefText += "Stream Function Coefficients: \n";
        coefText += "----------------------\n";

        for (let i = 0; i < output.NN + 2; i++) {
            let coef = String(i).padEnd(15, ' ') + output.X[i] + '\n';
            coefText += coef;
        }

        coefText += "\nErrors:\n------\n";
        coefText += `Wave Height Error: ${output.errorH} m\n`;
        coefText += `Mean Sea Level Error: ${output.msl} m\n`;
        coefText += `Bernoulli RMS Error: ${output.errorQ} m\n`;

        coefText += "\nFree Surface Displacement\n";
        coefText += "--------------------\n";
        coefText += "x (m)     Theta (radians)   Eta(theta)\n";
        coefText += "---------------------\n";

        for (let i = 0; i < output.NTHTS; i++) {
            let L = output.X[0];
            let theta = output.theta[i];
            let eta = output.eta[i];
            let coef = String((theta * L * 100 / (2 * Math.PI)) / 100).padEnd(12, ' ') +
                String(theta).padEnd(30, ' ') +
                eta.toFixed(2) + '\n';
            coefText += coef;
        }

        console.log(coefText);
    }

    getElement() {
        return this.element;
    }
}

class PlotStPanel {
    constructor() {
        this.canvas = document.createElement('canvas');
        this.canvas.width = 600;
        this.canvas.height = 550;
        this.ctx = this.canvas.getContext('2d');
        this.first = false;
        this.stop = false;
    }

    getElement() {
        return this.canvas;
    }

    do_wave(output) {
        this.waveplot = new StreamPlot(this.canvas.width, this.canvas.height);
        this.waveplot.initialize(output);
        this.waveplot.do_wave(output);
        this.draw();
    }

    draw() {
        this.waveplot.Draw(this.ctx);
    }
}

class StreamPlot {
    constructor(width, height) {
        this.length = width;
        this.totalh = height;
        this.x = new Array(this.length + 2).fill(0);
        this.y = new Array(this.length + 2).fill(0);
        this.click = 'white';
        this.offset = 5;
        this.NN = 0;
        this.NTHTS = 0;
    }

    initialize(output) {
        this.NTHTS = output.NTHTS;
        this.eta = [...output.eta];
        this.theta = [...output.theta];
        this.NN = output.NN;
        this.X = [...output.X];
        this.L = this.X[0];

        for (let i = 0; i < this.NTHTS; i++) {
            this.y[i] = (this.theta[i] * this.length) / Math.PI;
        }

        let etac = this.eta[0];
        this.H = etac - this.eta[this.NTHTS - 1];
        this.famplitude = (etac / (Wave.d + etac)) * (this.totalh - this.offset);
        this.amplitude = Math.floor(this.famplitude);

        for (let i = 0; i < this.NTHTS; i++) {
            this.x[i] = this.amplitude + this.offset - Math.floor((this.famplitude * this.eta[i]) / etac);
        }

        this.dz = (this.totalh - this.amplitude - this.offset) / 12;
        if (this.H > 0) {
            this.click = 'black';
        }
    }

    do_wave(output) {
        this.waveLength = `L = ${this.L} m`;
    }

    Draw(ctx) {
        ctx.clearRect(0, 0, this.length, this.totalh);

        // Draw axes
        ctx.beginPath();
        ctx.moveTo(0, this.amplitude + this.offset);
        ctx.lineTo(this.length, this.amplitude + this.offset);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(0, 0);
        ctx.lineTo(0, this.totalh);
        ctx.stroke();

        // Draw polygon
        ctx.fillStyle = 'blue';
        ctx.beginPath();
        for (let i = 0; i < this.NTHTS + 2; i++) {
            ctx.lineTo(this.y[i], this.x[i]);
        }
        ctx.closePath();
        ctx.fill();

        // Display wave length
        ctx.fillStyle = 'white';
        ctx.fillText(this.waveLength, this.length - 150, this.totalh - 25);

        // Show L/4 and L/2
        ctx.fillStyle = 'black';
        ctx.fillText("L/4", this.length / 2 - 10, this.amplitude + this.offset - 7);
        ctx.fillText("L/2", this.length - 20, this.amplitude + this.offset - 7);

        // Click for velocities
        ctx.fillStyle = this.click;
        ctx.fillText("Click for velocities", this.length - 260, 20);
    }
}

// Initialize the app
window.onload = () => {
    new Streamless();
};
