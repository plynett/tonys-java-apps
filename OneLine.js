// JavaScript version of the OneLine class and related components

class OneLine {
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
      this.graph1 = new PlotOLPanel();
      container.appendChild(this.graph1.createPlot());

      // Create input panel
      this.panel1 = new InputOLPanel(this.graph1);
      container.appendChild(this.panel1.createPanel());

      // Append to wave-calculator div in the HTML
      document.getElementById('wave-calculator').appendChild(container);
  }
}

// InputOLPanel class
class InputOLPanel {
  constructor(panel) {
      this.panel = panel;
      this.stop = false;
      this.el = 0;
      this.Y = 0;
      this.G = 0;
      this.jstep = 0;
  }

// Updated createPanel() method for InputOLPanel class

createPanel() {
  const panel = document.createElement('div');
  panel.style.display = 'grid';
  panel.style.gridTemplateColumns = 'auto auto'; // Two columns layout, similar to original Java
  panel.style.gap = '10px';
  panel.style.width = '300px';

  // Beach Fill Data label
  const label = document.createElement('h3');
  label.innerText = 'Beach Fill Data:';
  panel.appendChild(label);
  panel.appendChild(document.createElement('div')); // Spacer for alignment

  // Fill Length input
  panel.appendChild(this.createLabel('Fill Length (m)?'));
  this.Hin = this.createInput('1000.0');
  panel.appendChild(this.Hin);

  // Cross-shore Width input
  panel.appendChild(this.createLabel('Cross-shore Width (m)?'));
  this.Tin = this.createInput('100.0');
  panel.appendChild(this.Tin);

  // Shoreline Diffusivity input
  panel.appendChild(this.createLabel('Shoreline Diffusivity (m&sup2;/s)?'));
  this.din = this.createInput('0.001');
  panel.appendChild(this.din);

  // Spacer to align buttons
  panel.appendChild(document.createElement('div')); // Spacer for layout

  // Buttons
  const calculateButton = this.createButton('Calculate', () => this.calculate());
  panel.appendChild(calculateButton);

  const stopButton = this.createButton('Stop', () => this.stopSimulation());
  panel.appendChild(stopButton);

  return panel;
}

  createLabel(text) {
      const label = document.createElement('label');
      label.innerHTML = text;  // For special characters like m²
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


// PlotOLPanel class
class PlotOLPanel {
  constructor() {
      this.first = false;
      this.stop = false;
      this.thread = null;
      this.makerplot = new OneLinePlot(600, 450);
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

// OneLinePlot class
class OneLinePlot {
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
      this.boffset = 10;
      this.amplitude = 0;
  }

  initialize(el, Y, G) {
      this.el = el;
      this.Y = Y;
      this.G = G;
      this.amplitude = Math.floor(this.totalh - this.toffset - this.boffset);

      // Initialize the fill shape based on the error function
      for (let i = 0; i < this.length; i++) {
          const xi = (i * 1.5 * this.el) / this.length;
          const fact = 1.0e7;

          this.xo[i] = this.toffset + this.amplitude - 
              0.5 * this.amplitude * 
              (this.errorf.erf(fact * (2 * xi / this.el + 1)) - this.errorf.erf(fact * (2 * xi / this.el - 1)));
          this.y[i] = i;  // Update y values as x-coordinate
      }
  }

  doWave() {
      const fact = this.el / (4 * Math.sqrt(this.G * this.jstep * this.dt));
      for (let i = 0; i < this.length; i++) {
          const xi = (i * 1.5 * this.el) / this.length;

          this.x[i] = this.toffset + this.amplitude -
              0.5 * this.amplitude * 
              (this.errorf.erf(fact * (2 * xi / this.el + 1)) - this.errorf.erf(fact * (2 * xi / this.el - 1)));
      }

      // Update the text-based calculations for display
      const part = (1 / (Math.sqrt(Math.PI) * fact)) * (Math.exp(-fact * fact) - 1);
      const M = Math.floor(100 * (part + this.errorf.erf(fact)));

      this.Max = `Max = ${Math.floor(((this.toffset + this.amplitude - this.x[0]) * this.Y * 100 / this.amplitude) / 100)} m`;
      this.Half = `Half-life = ${(Math.pow(0.46 * this.el, 2) / (this.G * 86400 * 365)).toFixed(2)} yrs`;
      this.Percent = `Percent = ${M}%`;
      this.Time = `Time = ${this.jstep} days`;

      this.jstep += 1;
  }

  draw(g) {
      // Draw the x-axis and z-axis (like in the original Java)
      g.strokeStyle = 'black';
      g.beginPath();
      g.moveTo(0, this.amplitude + this.toffset);
      g.lineTo(this.length, this.amplitude + this.toffset); // x-axis
      g.moveTo(0, 0);
      g.lineTo(0, this.totalh); // z-axis
      g.stroke();

      // Fill the beach and fill areas
      g.fillStyle = 'yellow';  // Beach color
      g.fillRect(0, this.amplitude + this.toffset, this.length, this.amplitude + this.toffset);

      g.fillStyle = 'magenta';  // Fill color
      g.beginPath();
      for (let i = 0; i < this.length; i++) {
          g.lineTo(this.y[i], this.x[i]);
      }
      g.lineTo(this.length, this.amplitude + this.toffset);
      g.lineTo(0, this.amplitude + this.toffset);
      g.closePath();
      g.fill();

      // Draw textual info on the canvas
      g.fillStyle = 'black';
      g.fillText(this.Time, this.length - 130, this.totalh - 90);
      g.fillText(this.Max, this.length - 130, this.totalh - 70);
      g.fillText(this.Percent, this.length - 130, this.totalh - 50);
      g.fillText(this.Half, this.length - 130, this.totalh - 30);
  }
}


// Instantiate the applet
window.onload = function() {
  new OneLine();
};
