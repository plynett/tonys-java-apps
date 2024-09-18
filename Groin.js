// JavaScript version of the Groin class and related components

class Groin {
    constructor() {
        this.init();
    }

    init() {
        // Set up layout and elements
        const container = document.createElement('div');
        container.style.display = 'flex';

        // Spacer
        const spacer = document.createElement('div');
        spacer.textContent = ' ';
        container.appendChild(spacer);

        // Create the graph container
        this.graph1 = new PlotGPanel();
        container.appendChild(this.graph1.createPlot());

        // Create input panel
        this.panel1 = new InputGPanel(this.graph1);
        container.appendChild(this.panel1.createPanel());

        // Append to wave-calculator div in the HTML
        document.getElementById('wave-calculator').appendChild(container);
    }
}

// InputGPanel class
class InputGPanel {
    constructor(panel) {
        this.panel = panel;
        this.stop = false;
        this.el = 0;
        this.Y = 0;
        this.G = 0;
        this.jstep = 0;
    }

    createPanel() {
        const panel = document.createElement('div');
        panel.style.display = 'grid';
        panel.style.gridTemplateColumns = '1fr 1fr';
        panel.style.gap = '10px';
        panel.style.width = '300px';

        const label = document.createElement('h3');
        label.innerText = 'Groin Data:';
        panel.appendChild(label);
        panel.appendChild(document.createElement('div')); // Spacer for alignment

        // Groin Length input
        panel.appendChild(this.createLabel('Groin Length (m)?'));
        this.Hin = this.createInput('200.0');
        panel.appendChild(this.Hin);

        // Breaking Wave Angle input
        panel.appendChild(this.createLabel('Breaking Wave Angle (&deg;)?'));
        this.Tin = this.createInput('20.');
        panel.appendChild(this.Tin);

        // Shoreline Diffusivity input
        panel.appendChild(this.createLabel('Shoreline Diffusivity (m&sup2;/s)?'));
        this.din = this.createInput('0.001');
        panel.appendChild(this.din);

        // Buttons
        const calculateButton = this.createButton('Calculate', () => this.calculate());
        panel.appendChild(calculateButton);

        const stopButton = this.createButton('Stop', () => this.stopSimulation());
        panel.appendChild(stopButton);

        return panel;
    }

    createLabel(text) {
        const label = document.createElement('label');
        label.innerHTML = text; // Handles special characters like °
        return label;
    }

    createInput(defaultValue) {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = defaultValue;
        input.style.width = '100%';
        return input;
    }

    createButton(text, onClick) {
        const button = document.createElement('button');
        button.innerText = text;
        button.onclick = onClick;
        return button;
    }

    calculate() {
        try {
            this.el = parseFloat(this.Hin.value);
            this.Y = parseFloat(this.Tin.value);
            this.G = parseFloat(this.din.value);

            if (isNaN(this.el) || isNaN(this.Y) || isNaN(this.G)) {
                alert('Please enter valid values for all fields.');
                return;
            }

            this.Hin.disabled = true;
            this.Tin.disabled = true;
            this.din.disabled = true;
            this.stop = false;

            this.panel.setStop(false);
            this.Y = (this.Y * Math.PI) / 180.0; // Convert angle to radians
            this.panel.doWave(this.el, this.Y, this.G);

        } catch (error) {
            console.error(error);
        }
    }

    stopSimulation() {
        this.stop = true;
        this.Hin.disabled = false;
        this.Tin.disabled = false;
        this.din.disabled = false;
        this.panel.setStop(true);
    }
}

// PlotGPanel class
class PlotGPanel {
    constructor() {
        this.first = false;
        this.stop = false;
        this.thread = null;
        this.makerplot = new GroinPlot(600, 450);
    }

    createPlot() {
        this.canvas = document.createElement('canvas');
        this.canvas.width = 600;
        this.canvas.height = 450;
        this.ctx = this.canvas.getContext('2d');
        return this.canvas;
    }

    setStop(stopp) {
        this.stop = stopp;
        if (this.stop && this.thread) {
            clearInterval(this.thread);
            this.thread = null;
        }
    }

    doWave(el, Y, G) {
        this.makerplot.initialize(el, Y, G);
        this.makerplot.doWave();

        if (this.thread) {
            clearInterval(this.thread);
        }

        this.thread = setInterval(() => {
            if (!this.stop) {
                this.makerplot.doWave();
                this.paint();
            } else {
                clearInterval(this.thread);
            }
        }, 150);
    }

    paint() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.makerplot.draw(this.ctx);
    }
}

// GroinPlot class
class GroinPlot {
    constructor(width, height) {
        this.length = width;
        this.totalh = height;
        this.x = new Array(this.length + 2).fill(0);
        this.y = new Array(this.length + 2).fill(0);
        this.xo = new Array(this.length + 2).fill(0);
        this.errorf = new ErrorFunction();
        this.jstep = 0;
        this.dt = 86400; // One day in seconds
        this.toffset = 5;
        this.boffset = 5;
        this.amplitude = 0;
        this.totallength = 0;
    }

    initialize(el, delta, G) {
        this.el = el;
        this.delta = delta;
        this.G = G;
        this.amplitude = Math.floor(this.totalh - this.toffset - this.boffset);
        this.bypassingtime = (Math.PI * el * el) / (4.0 * G * Math.pow(Math.tan(delta), 2));
        this.totallength = 6.0 * Math.sqrt(Math.PI * el * el);
        this.xlength = `width = ${(Math.floor(this.totallength * 100)) / 100} m`;
        const xm = Math.floor((2.84576 * el) / Math.tan(delta) * 100) / 100;
        this.x100 = `x_m = ${xm} m`;

        for (let i = 0; i < this.length; i++) {
            this.y[i] = i;
            this.x[i] = this.toffset + el;
        }
    }

    doWave() {
        const fact = Math.sqrt(4.0 * this.G * this.jstep * this.dt);
        const scale = this.amplitude / (2 * this.el);
        this.Q = Math.floor(Math.tan(this.delta) * this.G * this.jstep * this.dt * 100) / 100;
        this.Qdraw = `Q = ${this.Q} `;
        this.time = `time = ${this.jstep} days`;

        for (let i = 0; i < this.length; i++) {
            let xi;
            if (i > this.length / 2) {
                xi = ((i - this.length / 2) * this.totallength) / this.length;
                this.x[i] = this.toffset + this.amplitude / 2 + scale *
                    ((fact / Math.sqrt(Math.PI)) * Math.exp(-xi * xi / (fact * fact)) -
                    xi * (1 - this.errorf.erf(xi / fact))) * Math.tan(this.delta);
            } else {
                xi = ((this.length / 2 - i) * this.totallength) / this.length;
                this.x[i] = this.toffset + this.amplitude / 2 - scale *
                    ((fact / Math.sqrt(Math.PI)) * Math.exp(-xi * xi / (fact * fact)) -
                    xi * (1 - this.errorf.erf(xi / fact))) * Math.tan(this.delta);
            }
        }

        if (this.jstep * this.dt > this.bypassingtime) {
            this.jstep = this.jstep; // Bypassing sand occurs, stops progress
        } else {
            this.jstep += 1;
        }
    }

    draw(g) {
        const xl = this.length - 1;
        const yl = this.totalh - 1;
    
        // Draw axes
        g.strokeStyle = 'black';
        g.beginPath();
        g.moveTo(0, this.amplitude / 2 + this.toffset);
        g.lineTo(this.length, this.amplitude / 2 + this.toffset); // x-axis
        g.moveTo(0, 0);
        g.lineTo(0, this.totalh); // z-axis
        g.stroke();
    
        // Draw the beach and fill areas (simulate fillPolygon)
        g.fillStyle = 'magenta';
        g.beginPath();
        g.moveTo(this.y[0], this.x[0]); // Start from the first point
    
        for (let i = 1; i < this.length; i++) {
            g.lineTo(this.y[i], this.x[i]); // Draw lines between points
        }
    
        g.lineTo(this.length, this.amplitude + this.toffset); // Connect to bottom right
        g.lineTo(0, this.amplitude + this.toffset); // Connect to bottom left
        g.closePath(); // Close the path
        g.fill(); // Fill the polygon
    
        // Draw textual info on the canvas
        g.fillStyle = 'black';
        let yl_text = 20;
        g.fillText('Ocean', this.length - 130, yl_text);
        yl_text += 15;
        g.fillText(this.xlength, this.length - 130, yl_text);
        yl_text += 15;
        g.fillText(this.time, this.length - 130, yl_text);
        yl_text += 15;
        g.fillText(`${this.Qdraw} m^2`, this.length - 130, yl_text);  // Replaced "m2" with "m²"
        yl_text += 15;
        g.fillText(this.x100, this.length - 130, yl_text);
    
        // Draw the groin
        g.strokeStyle = 'black';
        g.beginPath();
        g.moveTo(this.length / 2, this.toffset);
        g.lineTo(this.length / 2, this.toffset + this.amplitude);
        g.stroke();
    }
    
    
}

// Instantiate the applet
window.onload = function() {
    new Groin();
};
