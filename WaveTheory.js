// WaveTheory.js

window.onload = function() {
    // Create the calculator UI
    createCalculator();
};

function createCalculator() {
    let container = document.getElementById('wave-calculator');

    let form = document.createElement('div');
    form.style.display = 'grid';
    form.style.gridTemplateColumns = '200px 200px';
    form.style.gridRowGap = '10px';
    form.style.gridColumnGap = '10px';

    // Deep Water Values Title
    let title = document.createElement('h3');
    title.innerText = 'Deep Water Values:';
    title.style.gridColumn = '1 / span 2';
    form.appendChild(title);

    // Wave Height (m)
    form.appendChild(createLabel('Wave Height (m)?'));
    let Hin = createInput('1.0');
    form.appendChild(Hin);

    // Period or Frequency Choice
    let torf = createSelect(['Period', 'Frequency (Hz)']);
    form.appendChild(torf);
    let Tin = createInput('12.0');
    form.appendChild(Tin);

    // Wave Angle (degrees)
    form.appendChild(createLabel('Wave Angle (&deg;)?'));
    let angle0 = createInput('0.0');
    form.appendChild(angle0);

    // Local Depth
    form.appendChild(createLabel('Local Depth?'));
    let din = createInput('5.0');
    form.appendChild(din);

    // Buttons
    let calculateButton = document.createElement('button');
    calculateButton.type = 'button';
    calculateButton.innerText = 'Calculate';
    form.appendChild(calculateButton);

    let resetButton = document.createElement('button');
    resetButton.type = 'button';
    resetButton.innerText = 'Reset';
    form.appendChild(resetButton);

    // Placeholder elements to align the grid
    form.appendChild(document.createElement('div'));
    form.appendChild(document.createElement('div'));

    container.appendChild(form);

    // Results Section
    let results = document.createElement('div');
    results.style.display = 'grid';
    results.style.gridTemplateColumns = '200px 200px';
    results.style.gridRowGap = '10px';
    results.style.gridColumnGap = '10px';
    results.style.marginTop = '20px';

    let resultFields = {};

    let resultLabels = [
        { label: 'L (m) =', key: 'lout' },
        { label: 'k = 2&pi;/L =', key: 'kout' },
        { label: 'C = L/T =', key: 'Cout' },
        { label: 'Cg =', key: 'Cgout' },
        { label: 'n = Cg/C =', key: 'nout' },
        { label: 'Ks =', key: 'Ksout' },
        { label: 'Kr =', key: 'Krout' },
        { label: 'Angle =', key: 'angleout' },
        { label: 'H =', key: 'Hshal' },
        { label: 'u_b =', key: 'ubout' },
    ];

    resultLabels.forEach(item => {
        let label = createLabel(item.label);
        let output = createOutput();
        results.appendChild(label);
        results.appendChild(output);
        resultFields[item.key] = output;
    });

    container.appendChild(results);

    // Event Listeners
    calculateButton.addEventListener('click', function() {
        calculateWaveParameters({
            Hin: Hin.value,
            Tin: Tin.value,
            din: din.value,
            angle0: angle0.value,
            period: torf.value === 'Period',
        }, resultFields);
    });

    resetButton.addEventListener('click', function() {
        Hin.value = '';
        Tin.value = '';
        din.value = '';
        angle0.value = '0.0';
        torf.value = 'Period';
        for (let key in resultFields) {
            resultFields[key].value = '';
        }
    });
}

function createLabel(text) {
    let label = document.createElement('label');
    label.innerHTML = text;
    return label;
}

function createInput(defaultValue = '') {
    let input = document.createElement('input');
    input.type = 'text';
    input.value = defaultValue;
    return input;
}

function createSelect(options) {
    let select = document.createElement('select');
    options.forEach(opt => {
        let option = document.createElement('option');
        option.value = opt;
        option.text = opt;
        select.add(option);
    });
    return select;
}

function createOutput() {
    let output = document.createElement('input');
    output.type = 'text';
    output.readOnly = true;
    return output;
}

function calculateWaveParameters(inputs, outputs) {
    try {
        let H0 = parseFloat(inputs.Hin);
        if (isNaN(H0)) throw 'Invalid Wave Height';

        let T;
        if (inputs.period) {
            T = parseFloat(inputs.Tin);
        } else {
            let freq = parseFloat(inputs.Tin);
            if (isNaN(freq) || freq === 0) throw 'Invalid Frequency';
            T = 1 / freq;
        }
        if (isNaN(T) || T === 0) throw 'Invalid Period';

        let d = parseFloat(inputs.din);
        if (isNaN(d)) throw 'Invalid Local Depth';

        let thet0 = parseFloat(inputs.angle0);
        if (isNaN(thet0)) thet0 = 0.0;

        let refract = new Refract();

        let k = refract.waveNumber(d, T);
        outputs['kout'].value = k.toFixed(6);

        let L = 2 * Math.PI / k;
        outputs['lout'].value = L.toFixed(6);

        let C = L / T;
        outputs['Cout'].value = C.toFixed(6);

        let Angle = refract.theta(thet0, k, T);
        outputs['angleout'].value = Angle.toFixed(6);

        let Cg = refract.groupVelocity(T, d, k);
        outputs['Cgout'].value = Cg.toFixed(6);

        let n = Cg / C;
        outputs['nout'].value = n.toFixed(6);

        let Ks = refract.shoalingCoef(T, Cg);
        outputs['Ksout'].value = Ks.toFixed(6);

        let Kr = refract.refractionCoef(thet0, Angle);
        outputs['Krout'].value = Kr.toFixed(6);

        let H1 = Ks * Kr * H0;
        if (H1 > 0.8 * d) {
            H1 = 0.8 * d;
            outputs['Hshal'].value = H1.toFixed(6) + ', breaking';
        } else {
            outputs['Hshal'].value = H1.toFixed(6);
        }

        let ub = (2 * Math.PI / T) * H1 / (2 * Hyper.sinh(k * d));
        outputs['ubout'].value = ub.toFixed(6);

    } catch (error) {
        alert('Error: ' + error);
    }
}
