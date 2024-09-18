// Wavemaker class
class Wavemaker {
    constructor() {
        this.jstart = 0;
        this.H = 0;
        this.d = 1;
        this.L = 0;
        this.T = 0;

        this.graph1 = new PlotMPanel();
        this.panel1 = new InputMPanel(this.graph1);

        this.init();
    }

    init() {
        // Initialize app layout
        const container = document.createElement('div');
        container.style.display = 'flex';

        // Spacer
        const label1 = document.createElement('div');
        label1.style.margin = '10px';

        // Add components to the container
        container.appendChild(label1); // spacer
        container.appendChild(this.panel1.getElement());
        container.appendChild(this.graph1.getElement());

        // Append to the app container
        document.getElementById('app-container').appendChild(container);
    }
}

// InputMPanel class
class InputMPanel {
    constructor(panel) {
        this.panel = panel;
        this.Hin = null;
        this.Tin = null;
        this.din = null;
        this.ref = new Refract();
        this.H = 0;
        this.T = 0;
        this.d = 0;
        this.L = 0;
        this.Stroke = 0;
        this.jstep = 0;
        this.stop = false;
        this.label1 = null;
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

        layout.appendChild(this.createLabel('Choose Plot'));
        this.pltype = this.createDropdown(['Wavemaker', 'Velocity Modes']);
        layout.appendChild(this.pltype);

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

        this.Stop = this.createButton('Stop', this.stopWave.bind(this));
        layout.appendChild(this.Stop);

        this.element.appendChild(layout);

        // Event listeners for dropdowns
        this.wmtype.onchange = () => {
            this.wavemaker = this.wmtype.value === 'Piston';
        };
        this.pltype.onchange = () => {
            this.plotw = this.pltype.value === 'Wavemaker';
        };
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
        try {
            this.H = parseFloat(this.Hin.value);
            this.T = parseFloat(this.Tin.value);
            this.d = parseFloat(this.din.value);

            if (isNaN(this.H) || isNaN(this.T) || isNaN(this.d) || this.H <= 0 || this.T <= 0 || this.d <= 0) {
                alert('Please enter valid positive numbers for Wave Height, Wave Period, and Local Depth.');
                return;
            }

            if (this.H > 0.8 * this.d) {
                this.H = 0.8 * this.d;
                this.Hin.value = this.H.toFixed(2) + ', breaking';
            }

            const klin = this.ref.waveNumber(this.d, this.T);
            this.L = 2 * Math.PI / klin;

            this.stop = false;
            this.panel.setStop(this.stop);
            this.panel.doWave(this.H, this.L, this.d, this.T, this.wavemaker, this.plotw);

        } catch (e) {
            console.error(e);
        }
    }

    stopWave() {
        this.stop = true;
        this.panel.setStop(this.stop);
    }

    reset() {
        this.Hin.value = '1.0';
        this.Tin.value = '3.0';
        this.din.value = '4.0';
        this.stop = true;
        this.panel.setStop(this.stop);
    }
}

// PlotMPanel class
class PlotMPanel {
    constructor() {
        this.first = false;
        this.stop = false;
        this.makerplot = null;
        this.H = 0;
        this.d = 0;
        this.L = 0;
        this.T = 0;
        this.wmtype = true;
        this.pltype = true;
        this.animationFrameId = null;

        this.canvas = document.createElement('canvas');
        this.canvas.width = 600; // Adjust the size as needed
        this.canvas.height = 400; // Adjust the size as needed
        this.ctx = this.canvas.getContext('2d');

        this.setBackground('lightgray');
    }

    getElement() {
        return this.canvas;
    }

    setBackground(color) {
        this.canvas.style.backgroundColor = color;
    }

    setStop(stop) {
        this.stop = stop;
        if (stop && this.animationFrameId !== null) {
            cancelAnimationFrame(this.animationFrameId);
            this.animationFrameId = null;
        }
    }

    doWave(H, L, d, T, wmtype, pltype) {
        this.H = H;
        this.L = L;
        this.d = d;
        this.T = T;
        this.wmtype = wmtype;
        this.pltype = pltype;

        if (!this.first) {
            this.makerplot = new MakerPlot(this.canvas.width, this.canvas.height);
            this.first = true;
        }

        this.makerplot.initialize(H, L, d, T, wmtype, pltype);
        this.stop = false;
        this.animate();
    }

    animate() {
        if (!this.stop) {
            this.makerplot.doWave();
            this.draw();
            this.animationFrameId = requestAnimationFrame(this.animate.bind(this));
        }
    }

    draw() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.makerplot.draw(this.ctx);
    }
}

// MakerPlot class
class MakerPlot {
    constructor(width, height) {
        this.length = width;
        this.totalh = height;
        this.x = new Array(this.length + 2);
        this.y = new Array(this.length + 2);
        this.uc = new Array(31);
        this.u1 = new Array(31);
        this.u2 = new Array(31);
        this.u3 = new Array(31);
        this.ks = new Array(5);
        this.Cn = new Array(4);
        this.ncount = 30;
        this.offset = 5;
        this.pw = 5; // half paddle width
        this.jstep = 0;
        this.position = this.length / 30;
        this.f1 = 1;
        this.factor = 1;
        this.scale = 1;
        this.amplitude = 0;
        this.famplitude = 0;
        this.waveLength = '';
        this.stroke = '';
        this.power = '';
        this.ucmax = '';
        this.u1max = '';
        this.u2max = '';
        this.u3max = '';
    }

    initialize(H, L, d, T, wmtype, pltype) {
        this.H = H;
        this.L = L;
        this.d = d;
        this.T = T;
        this.wmtype = wmtype;
        this.pltype = pltype;

        const k = (2 * Math.PI) / L;
        this.k = k;
        this.sigma = (2 * Math.PI) / T;
        this.dt = T / 30;

        // Hyperbolic functions
        const chd = Hyper.cosh(k * d);
        const shd = Hyper.sinh(k * d);
        const ch2d = Hyper.cosh(2 * k * d);
        const sh2d = Hyper.sinh(2 * k * d);

        this.chd = chd;
        this.shd = shd;
        this.ch2d = ch2d;
        this.sh2d = sh2d;

        // Evanescent modes
        for (let jk = 1; jk < 4; jk++) {
            this.ks[jk] = Wvnumev.wvnumev(d, T, jk);
        }

        // Amplitude calculation
        this.famplitude = (H / 2) / (d + H / 2) * (this.totalh - this.offset);
        this.amplitude = this.famplitude;

        // Stroke and A calculation
        if (wmtype) {
            // Piston
            this.Stroke = (H * (sh2d + 2 * k * d)) / (2 * (ch2d - 1));
            this.A = (-2 * this.sigma * this.Stroke * shd) / (k * (sh2d + 2 * k * d));
            for (let i = 1; i < 4; i++) {
                const ksd = this.ks[i] * d;
                this.Cn[i] = (-2 * this.sigma * this.Stroke * Math.sin(ksd)) / (this.ks[i] * (Math.sin(2 * ksd) + 2 * ksd));
            }
        } else {
            // Flap
            this.Stroke = (H * k * d * (sh2d + 2 * k * d)) / (4 * shd * ((k * d) * shd - chd + 1));
            this.A = (-2 * this.sigma * this.Stroke * ((k * d) * shd - chd + 1)) / (k * k * d * (sh2d + 2 * k * d));
            for (let i = 1; i < 4; i++) {
                const ksd = this.ks[i] * d;
                this.Cn[i] = (-2 * this.sigma * this.Stroke * (1 - ksd * Math.sin(ksd) - Math.cos(ksd))) / (this.ks[i] * this.ks[i] * d * (Math.sin(2 * ksd) + 2 * ksd));
            }
        }

        // Power calculation
        this.Power = (10000 / 8) * H * H * (0.5 * (1 + 2 * k * d / shd)) * L / T;

        // Scaling factors
        const ucm = Math.abs(this.A * k * Hyper.cosh(k * d));
        let scale = ucm;
        for (let i = 1; i < 4; i++) {
            const uim = Math.abs(this.Cn[i] * this.ks[i]);
            if (uim > scale) scale = uim;
        }
        this.scale = scale;
        this.f1 = this.length / (2 * scale);
        this.factor = this.totalh / d;

        // Max velocities
        this.ucmax = `uc = ${ucm.toFixed(3)} m/s`;
        this.u1max = `u1 = ${(Math.abs(this.Cn[1] * this.ks[1])).toFixed(3)} m/s`;
        this.u2max = `u2 = ${(Math.abs(this.Cn[2] * this.ks[2])).toFixed(3)} m/s`;
        this.u3max = `u3 = ${(Math.abs(this.Cn[3] * this.ks[3])).toFixed(3)} m/s`;

        this.waveLength = `L = ${L.toFixed(2)} m`;
        this.stroke = `Stroke = ${this.Stroke.toFixed(2)} m`;
        this.power = `Power = ${this.Power.toFixed(2)} W`;

        this.jstep = 0;
    }

    doWave() {
        this.jstep += 0.1;

        const sigma_dt = (2 * Math.PI * this.jstep) / 30;

        if (this.pltype) {
            // Wave profile calculations
            for (let i = 0; i < this.length; i++) {
                const arg = (2 * Math.PI * (i + this.position + this.pw) / this.length) - sigma_dt;
                this.x[i] = this.amplitude + this.offset - (this.amplitude * Math.cos(arg));
            }            
        } else {
            // Velocity profile calculations
            const dz = this.d / 29;
            for (let jp = 0; jp < 30; jp++) {
                const z = -jp * dz;
                const cos_sigma = Math.cos(this.sigma * this.jstep * this.dt);
                this.uc[jp] = this.f1 * this.A * this.k * Hyper.cosh(this.k * (this.d + z)) * cos_sigma;
                this.u1[jp] = this.f1 * this.Cn[1] * this.ks[1] * Math.cos(this.ks[1] * (this.d + z)) * cos_sigma;
                this.u2[jp] = this.f1 * this.Cn[2] * this.ks[2] * Math.cos(this.ks[2] * (this.d + z)) * cos_sigma;
                this.u3[jp] = this.f1 * this.Cn[3] * this.ks[3] * Math.cos(this.ks[3] * (this.d + z)) * cos_sigma;
            }
        }
    }

    draw(ctx) {
        ctx.clearRect(0, 0, this.length, this.totalh);
        ctx.strokeStyle = 'black';
        ctx.lineWidth = 2;
    
        if (this.pltype) {
            // Draw still water level
            ctx.beginPath();
            ctx.moveTo(0, this.totalh - (this.amplitude + this.offset));
            ctx.lineTo(this.length, this.totalh - (this.amplitude + this.offset));
            ctx.stroke();
    
            // Draw wave profile
            ctx.strokeStyle = 'blue';
            for (let i = 1; i < this.length; i++) {
                ctx.beginPath();
                ctx.moveTo(i - 1, this.totalh - this.x[i - 1]);
                ctx.lineTo(i, this.totalh - this.x[i]);
                ctx.stroke();
            }

            // Wavemaker motion
            const S = this.position * Math.sin(this.sigma * this.jstep * this.dt);
            if (this.wmtype) {
                // Piston motion
                ctx.fillStyle = 'black';
                ctx.fillRect(this.position - S, 0, 2 * this.pw, this.totalh);

                // Erase behind paddle
                ctx.fillStyle = 'white';
                ctx.fillRect(0, 0, this.position - S, this.totalh);
            } else {
                // Flap motion
                const flapAngle = (Math.PI / 6) * Math.sin(this.sigma * this.jstep * this.dt);
                ctx.save();
                ctx.translate(this.position, this.totalh);
                ctx.rotate(-flapAngle);
                ctx.fillStyle = 'black';
                ctx.fillRect(0, -this.totalh, 20, this.totalh);
                ctx.restore();

                // Erase behind paddle
                ctx.fillStyle = 'white';
                ctx.fillRect(0, 0, this.position, this.totalh);
            }

            // Display output values
            ctx.fillStyle = 'red';
            ctx.font = '16px Arial';
            ctx.fillText(this.waveLength, this.length - 150, this.amplitude + 50);
            ctx.fillText(this.stroke, this.length - 150, this.amplitude + 80);
            ctx.fillText(this.power, this.length - 150, this.amplitude + 110);

        } else {
            // Draw velocity profiles
            // Draw axes
            ctx.strokeStyle = 'black';
            ctx.beginPath();
            ctx.moveTo(0, 1);
            ctx.lineTo(this.length, 1);
            ctx.moveTo(0, this.totalh - 1);
            ctx.lineTo(this.length, this.totalh - 1);
            ctx.moveTo(0, 0);
            ctx.lineTo(0, this.totalh);
            ctx.moveTo(this.length - 1, 0);
            ctx.lineTo(this.length - 1, this.totalh);
            ctx.stroke();

            // Draw velocities
            const centerX = this.length / 2;
            const factor = this.totalh / this.d;

            for (let i = 1; i < 30; i++) {
                const y1 = (i - 1) * (this.d / 29) * factor;
                const y2 = i * (this.d / 29) * factor;

                ctx.strokeStyle = 'red';
                ctx.beginPath();
                ctx.moveTo(centerX + this.uc[i - 1], this.totalh - y1);
                ctx.lineTo(centerX + this.uc[i], this.totalh - y2);
                ctx.stroke();

                ctx.strokeStyle = 'green';
                ctx.beginPath();
                ctx.moveTo(centerX + this.u1[i - 1], this.totalh - y1);
                ctx.lineTo(centerX + this.u1[i], this.totalh - y2);
                ctx.stroke();

                ctx.strokeStyle = 'blue';
                ctx.beginPath();
                ctx.moveTo(centerX + this.u2[i - 1], this.totalh - y1);
                ctx.lineTo(centerX + this.u2[i], this.totalh - y2);
                ctx.stroke();

                ctx.strokeStyle = 'cyan';
                ctx.beginPath();
                ctx.moveTo(centerX + this.u3[i - 1], this.totalh - y1);
                ctx.lineTo(centerX + this.u3[i], this.totalh - y2);
                ctx.stroke();
            }

            // Draw vertical axis at center
            ctx.strokeStyle = 'black';
            ctx.beginPath();
            ctx.moveTo(centerX, 0);
            ctx.lineTo(centerX, this.totalh);
            ctx.stroke();

            // Display max velocities
            ctx.fillStyle = 'red';
            ctx.font = '16px Arial';
            ctx.fillText(this.ucmax, this.length - 150, this.amplitude + 50);

            ctx.fillStyle = 'green';
            ctx.fillText(this.u1max, this.length - 150, this.amplitude + 80);

            ctx.fillStyle = 'blue';
            ctx.fillText(this.u2max, this.length - 150, this.amplitude + 110);

            ctx.fillStyle = 'cyan';
            ctx.fillText(this.u3max, this.length - 150, this.amplitude + 140);
        }
    }
}

// Initialize Wavemaker
window.onload = () => {
    new Wavemaker();
};
