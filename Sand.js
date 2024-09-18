// JavaScript version of the Sand class
class Sand {
    constructor() {
        this.createElements();
    }

    createElements() {
        // Create main container
        const container = document.createElement('div');
        container.style.display = 'flex';
        container.style.justifyContent = 'space-between';

        // Input panel
        const inputPanel = document.createElement('div');
        inputPanel.style.display = 'grid';
        inputPanel.style.gridTemplateColumns = '1fr 1fr';
        inputPanel.style.gap = '10px';
        inputPanel.style.width = '45%';

        inputPanel.appendChild(this.createLabel('Input Values:'));
        inputPanel.appendChild(document.createElement('div')); // Empty space

        inputPanel.appendChild(this.createLabel('Breaker Height (m)?'));
        this.Hin = this.createInput('0.5');
        inputPanel.appendChild(this.Hin);

        inputPanel.appendChild(this.createLabel('Breaking Angle (&deg;)?'));  // Degree symbol
        this.angle0 = this.createInput('10.0');
        inputPanel.appendChild(this.angle0);

        inputPanel.appendChild(this.createLabel('K?'));
        this.Kin = this.createInput('0.7');
        inputPanel.appendChild(this.Kin);

        inputPanel.appendChild(this.createLabel('Berm Height, B? (m)'));
        this.Bin = this.createInput('1.0');
        inputPanel.appendChild(this.Bin);

        inputPanel.appendChild(this.createLabel('Depth of Closure? (m)'));
        this.hstarin = this.createInput('6.0');
        inputPanel.appendChild(this.hstarin);

        // Buttons
        const calculateButton = this.createButton('Calculate', () => this.calculate());
        inputPanel.appendChild(calculateButton);

        const resetButton = this.createButton('Reset', () => this.reset());
        inputPanel.appendChild(resetButton);

        // Output panel
        const outputPanel = document.createElement('div');
        outputPanel.style.display = 'grid';
        outputPanel.style.gridTemplateColumns = '1fr 1fr';
        outputPanel.style.gap = '10px';
        outputPanel.style.width = '45%';

        outputPanel.appendChild(this.createLabel('Output:'));
        outputPanel.appendChild(document.createElement('div')); // Empty space

        outputPanel.appendChild(this.createLabel('Q (m&sup3;/sec) ='));  // Cubic meters
        this.qout = this.createOutput();
        outputPanel.appendChild(this.qout);

        outputPanel.appendChild(this.createLabel('Q (m&sup3;/yr) ='));  // Cubic meters
        this.qyout = this.createOutput();
        outputPanel.appendChild(this.qyout);

        outputPanel.appendChild(this.createLabel('G (m&sup2;/sec) ='));  // Square meters
        this.Gout = this.createOutput();
        outputPanel.appendChild(this.Gout);

        // Append to container
        container.appendChild(inputPanel);
        container.appendChild(outputPanel);

        // Append container to wave calculator div
        document.getElementById('wave-calculator').appendChild(container);
    }

    createLabel(text) {
        const label = document.createElement('label');
        label.innerHTML = text;  // Use innerHTML to support HTML entities
        return label;
    }

    createInput(defaultValue) {
        const input = document.createElement('input');
        input.type = 'text';
        input.value = defaultValue;
        input.style.width = '100%';
        return input;
    }

    createOutput() {
        const output = document.createElement('input');
        output.type = 'text';
        output.readOnly = true;
        output.style.width = '100%';
        return output;
    }

    createButton(text, onClick) {
        const button = document.createElement('button');
        button.innerText = text;
        button.onclick = onClick;
        return button;
    }

    calculate() {
        const g = 9.81;
        const rho = 1000;
        const s = 2.65;
        const p = 0.3;
        const kappa = 0.78;

        try {
            let Hb = parseFloat(this.Hin.value);
            let K = parseFloat(this.Kin.value);
            let thetb = parseFloat(this.angle0.value) * (Math.PI / 180); // Convert to radians
            let B = parseFloat(this.Bin.value);
            let hstar = parseFloat(this.hstarin.value);

            if (isNaN(Hb) || isNaN(K) || isNaN(thetb) || isNaN(B) || isNaN(hstar)) {
                alert('Please enter valid values for all fields.');
                return;
            }

            let db = Hb / kappa;
            let Eb = (rho * g * Hb * Hb) / 8;
            let Cb = Math.sqrt(g * db);
            let Cq = (K * Eb * Cb) / (rho * g * (s - 1) * (1 - p));
            let Q = Cq * Math.cos(thetb) * Math.sin(thetb);
            Q = Math.round(Q * 1000) / 1000;  // Truncate to 3 decimal places
            this.qout.value = Q;

            let Qy = Q * 31536000;  // Convert to m³/year
            Qy = Math.round(Qy * 100) / 100;  // Truncate to 2 decimal places
            this.qyout.value = Qy;

            let G = (2 * Cq * Math.cos(2 * thetb)) / (hstar + B);
            G = Math.round(G * 1000) / 1000;  // Truncate to 3 decimal places
            this.Gout.value = G;

        } catch (error) {
            console.error(error);
            alert('An error occurred during calculation.');
        }
    }

    reset() {
        this.Hin.value = '';
        this.Kin.value = '';
        this.angle0.value = '';
        this.qout.value = '';
        this.qyout.value = '';
        this.Gout.value = '';
        this.Bin.value = '';
        this.hstarin.value = '';
    }
}

// Initialize the application
window.onload = () => {
    new Sand();
};
