class Wavemaker {
    constructor() {
        this.panel1 = new InputMPanel(this);
        this.graph1 = new PlotMPanel();
        this.jstart = 0;
        this.H = 0;
        this.d = 1;
        this.L = 0;
        this.T = 0;
        this.init();
    }

    init() {
        // Initialize app layout
        const container = document.createElement('div');
        container.style.display = 'flex';
        container.appendChild(this.panel1.getElement());
        container.appendChild(this.graph1.getElement());
    
        // Instead of appending to document.body, append to a specific element by ID
        document.getElementById('app-container').appendChild(container);
    }
    

    reset() {
        this.panel1.reset();
        this.graph1.reset();
    }
}

class InputMPanel {
    constructor(parent) {
        this.parent = parent;
        this.H = 0;
        this.T = 0;
        this.d = 0;
        this.L = 0;
        this.Stroke = 0;
        this.jstep = 0;
        this.stop = false;
        this.wavemaker = true; // means piston
        this.plotw = true; // means plot wavemaker

        this.element = document.createElement('div');
        this.element.style.margin = '10px';
        this.createInputPanel();
    }

    createInputPanel() {
        const layout = document.createElement('div');
        layout.style.display = 'grid';
        layout.style.gridTemplateColumns = '150px 150px';
        layout.style.gap = '10px';

        layout.appendChild(this.createLabel('Choose Wavemaker'));
        this.wmtype = this.createDropdown(['Piston', 'Flap']);
        layout.appendChild(this.wmtype);

     //   layout.appendChild(this.createLabel('Choose Plot'));
        this.pltype = 'Wavemaker'; //this.createDropdown(['Wavemaker', 'Velocity Modes']);
     //   layout.appendChild(this.pltype);

        layout.appendChild(this.createLabel('Desired Wave Data:'));
        layout.appendChild(document.createElement('div')); // spacer

        layout.appendChild(this.createLabel('Wave Height (m)?'));
        this.Hin = this.createTextField('1.0');
        layout.appendChild(this.Hin);

        layout.appendChild(this.createLabel('Wave Period?'));
        this.Tin = this.createTextField('3.0');
        layout.appendChild(this.Tin);

        layout.appendChild(this.createLabel('Local Depth?'));
        this.din = this.createTextField('4.0');
        layout.appendChild(this.din);

        this.Calculate = this.createButton('Calculate', this.calculateWave.bind(this));
        layout.appendChild(this.Calculate);

        this.Reset = this.createButton('Reset', this.resetWave.bind(this));
        layout.appendChild(this.Reset);

        this.element.appendChild(layout);
    }

    createLabel(text) {
        const label = document.createElement('label');
        label.innerText = text;
        return label;
    }

    createDropdown(options) {
        const select = document.createElement('select');
        options.forEach(opt => {
            const option = document.createElement('option');
            option.innerText = opt;
            select.appendChild(option);
        });
        return select;
    }

    createTextField(defaultValue) {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = defaultValue;
        return input;
    }

    createButton(text, callback) {
        const button = document.createElement('button');
        button.innerText = text;
        button.onclick = callback;
        return button;
    }

    getElement() {
        return this.element;
    }

    calculateWave() {
        this.H = parseFloat(this.Hin.value);
        this.T = parseFloat(this.Tin.value);
        this.d = parseFloat(this.din.value);
        if (this.H > 0.8 * this.d) {
            this.H = 0.8 * this.d;
            this.Hin.value = this.H + ", breaking";
        }

        const ref = new Refract();
        const klin = ref.waveNumber(this.d, this.T);
        this.L = 6.2831852 / klin;

        this.wavemaker = this.wmtype.value === 'Piston';
        this.plotw = this.pltype.value === 'Wavemaker';

        this.parent.graph1.doWave(this.H, this.L, this.d, this.T, this.wavemaker, this.plotw);
    }

    resetWave() {
        this.parent.reset();
    }

    reset() {
        this.Hin.value = '1.0';
        this.Tin.value = '3.0';
        this.din.value = '4.0';
        this.parent.graph1.setStop(true); // Stop the wave animation
    }
}


class PlotMPanel {
    constructor() {
        this.H = 0;
        this.d = 0;
        this.L = 0;
        this.T = 0;
        this.first = false;
        this.stop = false;
        this.animationId = null; // To store the animation frame ID
        this.makerplot = new MakerPlot();
        this.canvas = document.createElement('canvas');
        this.canvas.width = 600;
        this.canvas.height = 400;
        this.ctx = this.canvas.getContext('2d');
    }

    getElement() {
        return this.canvas;
    }

    setStop(stopp) {
        this.stop = stopp;
    }

    doWave(H, L, d, T, wmtype, pltype) {
        // Stop any ongoing animation before starting a new one
        if (this.animationId !== null) {
            cancelAnimationFrame(this.animationId);
            this.animationId = null;
        }
        
        this.makerplot.initialize(H, L, d, T, wmtype, pltype);
        this.animate();
    }

    animate() {
        if (!this.stop) {
            this.makerplot.doWave();
            this.makerplot.draw(this.ctx);
            this.animationId = requestAnimationFrame(this.animate.bind(this)); // Store the frame ID
        }
    }

    reset() {
        this.stop = true;  // stop animation
        this.makerplot = new MakerPlot();  // reset maker plot
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height); // clear canvas
        this.stop = false;  // Allow further animation
    }
}


class MakerPlot {
    constructor() {
        this.length = 600;
        this.amplitude = 200;
        this.totalh = 400;
        this.x = [];
        this.y = [];
        this.uc = [];
        this.u1 = [];
        this.u2 = [];
        this.u3 = [];
        this.ks = [];
        this.famplitude = 0;
        this.offset = 5;
        this.pw = 5;
        this.jstep = 0;
        this.position = 0;
        this.Stroke = 0;
        this.Power = 0;
    }

    initialize(H, L, d, T, wmtype, pltype) {
        this.H = H;
        this.L = L;
        this.d = d;
        this.T = T;
        this.wmtype = wmtype;
        this.pltype = pltype;

        const ref = new Refract();
        this.k = 2 * Math.PI / L;

        this.Stroke = H * this.k * d / 2;
        this.Power = (1000 / 8) * H * H * (1 + 2 * this.k * d) * L / T;

        this.jstep = 0;
        this.amplitude = (H / 2) / (d + H / 2) * this.totalh;
    }

    doWave() {
        this.jstep += 1 / 30;
        this.position = this.length / 30;
        const sigma = 2 * Math.PI / this.T;
        for (let i = 0; i < this.length; i++) {
            const arg = 2 * Math.PI * (i + this.position + this.pw) / this.length - sigma * this.jstep;
            this.x[i] = this.offset + this.amplitude - (this.amplitude * Math.cos(arg));
        }
    }

    draw(ctx) {
        ctx.clearRect(0, 0, this.length, this.totalh);
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(0, this.amplitude + this.offset);
        ctx.lineTo(this.length, this.amplitude + this.offset);
        ctx.stroke();

        for (let i = 1; i < this.length; i++) {
            ctx.strokeStyle = 'blue';
            ctx.beginPath();
            ctx.moveTo(i - 1, this.x[i - 1]);
            ctx.lineTo(i, this.x[i]);
            ctx.stroke();
        }

        // Wavemaker motion
        ctx.fillStyle = 'black';
        const S = this.position * Math.sin(2 * Math.PI * this.jstep / this.T);
        if (this.wmtype) {
            // Piston motion: fill from x=0 to piston location
            ctx.fillRect(0, 0, this.position + S + 2 * this.pw, this.totalh);
        } else {
            // Flap motion
            const flapAngle = Math.PI / 6 * Math.sin(2 * Math.PI * this.jstep / this.T);
            ctx.save();
            ctx.translate(this.position, this.totalh);
            ctx.rotate(-flapAngle);
            ctx.fillRect(0, -this.totalh, 20, this.totalh);
            ctx.restore();
        }
    }
}

// Initialize Wavemaker
window.onload = () => {
    new Wavemaker();
};
