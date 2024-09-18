// Bathymetry class definition
class Bathymetry {
  constructor() {
      this.NPTS = 0;
      this.DX = 0;
      this.h = [];
      this.slope = 0;
  }

  Bathymetry(npts, dx, he, sl) {
      this.NPTS = npts;
      this.DX = dx;
      this.slope = sl;
      this.h = [...he];  // Copy the profile array
  }
}

// JavaScript version of the Beach class and related components
class Beach {
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
      this.graph1 = new PlotBPlot();
      container.appendChild(this.graph1.createPlot());

      // Create input panel
      this.panel1 = new InputBPanel(this.graph1);
      container.appendChild(this.panel1.createPanel());

      // Append to wave-calculator div in the HTML
      document.getElementById('wave-calculator').appendChild(container);
  }
}

// InputBPanel class
class InputBPanel {
  constructor(panel) {
      this.panel = panel;
      this.n = 100;
  }

  createPanel() {
      const panel = document.createElement('div');
      panel.style.display = 'grid';
      panel.style.gridTemplateColumns = '1fr 1fr';
      panel.style.gap = '10px';
      panel.style.width = '300px';

      const label = document.createElement('h3');
      label.innerText = 'Input Profile/Sand Data:';
      panel.appendChild(label);
      panel.appendChild(document.createElement('div')); // Spacer for alignment

      // Native Grain Size input
      panel.appendChild(this.createLabel('Native Grain size (mm)?'));
      this.din = this.createInput('0.3');
      panel.appendChild(this.din);

      // Fill Grain Size input
      panel.appendChild(this.createLabel('Fill Grain size (mm)?'));
      this.dfin = this.createInput('0.4');
      panel.appendChild(this.dfin);

      // Final Fill Width input
      panel.appendChild(this.createLabel('Final Fill Width (m)?'));
      this.Win = this.createInput('50.0');
      panel.appendChild(this.Win);

      // Berm Height input
      panel.appendChild(this.createLabel('Berm Height (m)?'));
      this.Bin = this.createInput('1.0');
      panel.appendChild(this.Bin);

      // Depth of Closure input
      panel.appendChild(this.createLabel('Depth of Closure (m)?'));
      this.hin = this.createInput('6.0');
      panel.appendChild(this.hin);

      // Buttons
      const calculateButton = this.createButton('Calculate', () => this.calculate());
      panel.appendChild(calculateButton);

      const resetButton = this.createButton('Reset', () => this.resetForm());
      panel.appendChild(resetButton);

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
          const B = parseFloat(this.Bin.value); // Berm height
          const h = parseFloat(this.hin.value); // Depth of closure
          const d = parseFloat(this.din.value); // Native grain size
          const df = parseFloat(this.dfin.value); // Fill grain size
          const W = parseFloat(this.Win.value); // Final fill width

          if (isNaN(B) || isNaN(h) || isNaN(d) || isNaN(df) || isNaN(W)) {
              alert('Please enter valid values for all fields.');
              return;
          }

          const AN = this.Acalc(d);
          const AF = this.Acalc(df);
          let xmaxn = Math.pow(h / AN, 1.5);
          let xmaxf = W + Math.pow(h / AF, 1.5);
          let xmax;
          let volume;

          const n = this.n;
          const hn = new Array(n).fill(0);
          const hf = new Array(n).fill(0);

          if (AF > AN) {
              const yi = W / (1 - Math.pow(AN / AF, 1.5));
              const hi = AN * Math.pow(yi, 2 / 3);
              if (hi < h) {
                  xmax = xmaxn;
                  volume = B * W + 0.6 * AN * Math.pow(W, 5 / 3) / Math.pow(1 - Math.pow(AN / AF, 1.5), 2 / 3);
                  this.calculateProfiles(n, xmaxn, W, AN, AF, hn, hf);
              } else {
                  xmax = xmaxf;
                  volume = B * W + 0.6 * h * xmaxn * (Math.pow(W / xmaxn + Math.pow(AN / AF, 1.5), 5 / 3) - Math.pow(AN / AF, 1.5));
                  this.calculateProfiles(n, xmaxf, W, AN, AF, hn, hf);
              }
          } else if (AF === AN) {
              xmax = xmaxn + W;
              volume = W * (B + h);
              this.calculateProfiles(n, xmax, W, AN, AF, hn, hf);
          } else {
              xmax = xmaxf;
              volume = B * W + 0.6 * h * xmaxn * (Math.pow(W / xmaxn + Math.pow(AN / AF, 1.5), 5 / 3) - Math.pow(AN / AF, 1.5));
              this.calculateProfiles(n, xmaxf, W, AN, AF, hn, hf);
          }

          // Create bathymetry objects and pass them to the plot panel
          const bathn = new Bathymetry();
          bathn.Bathymetry(n, xmax / (n - 1), hn, AN);

          const bathf = new Bathymetry();
          bathf.Bathymetry(n, xmax / (n - 1), hf, AF);

          this.panel.doWave(bathn, bathf, xmax, Math.floor(volume));

      } catch (error) {
          console.error(error);
      }
  }

  calculateProfiles(n, xmax, W, AN, AF, hn, hf) {
      const dx = xmax / (n - 1);
      for (let i = 0; i < n; i++) {
          const x = i * dx;
          hn[i] = AN * Math.pow(x, 0.6666);
          if (x < W) {
              hf[i] = -parseFloat(this.Bin.value);
          } else {
              hf[i] = AF * Math.pow(x - W, 0.6666);
          }
      }
  }

  Acalc(d) {
      return 0.0165 * Math.pow(d, 3) - 0.2118 * Math.pow(d, 2) + 0.5028 * d - 0.0008;
  }

  resetForm() {
      this.Bin.value = '';
      this.hin.value = '';
      this.din.value = '';
      this.Win.value = '';
      this.dfin.value = '';
  }
}

// PlotBPlot class
class PlotBPlot {
  constructor() {
      this.first = false;
      this.makerplot = new WaveBPlot(600, 450);
  }

  createPlot() {
      this.canvas = document.createElement('canvas');
      this.canvas.width = 600;
      this.canvas.height = 450;
      this.ctx = this.canvas.getContext('2d');
      return this.canvas;
  }

  doWave(bathn, bathf, xmax, volume) {
      this.makerplot.initialize(bathn, bathf, xmax, volume);
      this.makerplot.doWave();
      this.paint();
  }

  paint() {
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      this.makerplot.draw(this.ctx);
  }
}

// WaveBPlot class
class WaveBPlot {
  constructor(width, height) {
      this.length = width;
      this.totalh = height;
      this.x = new Array(this.length).fill(0);
      this.depth = new Array(this.length).fill(0);
      this.depthf = new Array(this.length).fill(0);
      this.iwave = Math.floor(this.totalh / 3);
      this.offset = 5;
  }

  initialize(bathn, bathf, xmax, volume) {
      const npts = bathn.NPTS;
      const dx = bathn.DX;

      this.ANvalue = `A_N = ${bathn.slope}`;
      this.AFvalue = `A_F = ${bathf.slope}`;
      this.Xmax = `x_max = ${Math.floor(xmax * 100) / 100}`;
      this.Vol = `Volume = ${Math.floor(volume * 100) / 100} m^3/m`;

      const dmax = bathn.h[npts - 1];

      for (let i = 0; i < npts; i++) {
          this.x[i] = Math.floor((i * dx * this.length) / xmax);
          this.depth[i] = this.offset + this.iwave + Math.floor((bathn.h[i] * (this.totalh - this.offset - this.iwave)) / dmax);
          this.depthf[i] = this.offset + this.iwave + Math.floor((bathf.h[i] * (this.totalh - this.offset - this.iwave)) / dmax);
      }
  }

  doWave() {}

  draw(g) {
      g.clearRect(0, 0, this.length, this.totalh);

      // Draw axes
      g.strokeStyle = 'black';
      g.beginPath();
      g.moveTo(0, this.iwave + this.offset);
      g.lineTo(this.length, this.iwave + this.offset); // x-axis
      g.moveTo(0, 0);
      g.lineTo(0, this.totalh); // z-axis
      g.stroke();

      // Draw ocean
      g.fillStyle = 'blue';
      g.fillRect(0, this.iwave + this.offset, this.length, this.totalh);

      // Draw beach fill (magenta)
      g.fillStyle = 'magenta';
      g.beginPath();

      // Start at the first valid fill profile point
      g.moveTo(this.x[0], this.depthf[0]);

      // Loop through the points of the fill beach profile
      for (let i = 1; i < this.x.length; i++) {
          if(this.x[i]>0){
            g.lineTo(this.x[i], this.depthf[i]); // Draw each point of the fill beach
          }
      }

      // Move to the bottom-right corner (length, total height)
      g.lineTo(this.length, this.totalh);

      // Close polygon at the bottom-left corner (0, total height)
      g.lineTo(0, this.totalh); // Correctly connect to the lower-left corner

      // Complete the shape by closing the polygon
      g.closePath();
      g.fill();


      // Draw original beach (brown)
      g.fillStyle = '#D4A460'; // brown color
      g.beginPath();
      g.moveTo(this.x[0], this.depth[0]);
      for (let i = 1; i < this.x.length; i++) {
          g.lineTo(this.x[i], this.depth[i]);
      }
      g.lineTo(this.length, this.totalh); // Move to bottom-right corner
      g.lineTo(0, this.totalh);           // Close polygon along bottom of canvas
      g.closePath();
      g.fill();

      // Display calculated values
      g.fillStyle = 'black';
      g.fillText(this.ANvalue, this.length / 10, 20);
      g.fillText(this.AFvalue, this.length / 2, 20);
      g.fillText(this.Xmax, this.length / 10, 35);
      g.fillText(this.Vol, this.length / 2, 35);
      g.fillText("Original Beach", 10, this.totalh - 50);
      g.fillStyle = 'white';
      g.fillText("Ocean", this.length - 75, this.iwave + this.offset + 30);
  }

}

// Instantiate the applet
window.onload = function() {
  new Beach();
};
