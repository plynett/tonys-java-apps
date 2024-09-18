document.addEventListener("DOMContentLoaded", function() {
    const waveCalculator = document.getElementById('wave-calculator');
    initializeUI(waveCalculator);
});

function initializeUI(container) {
    const canvas = document.createElement('canvas');
    canvas.id = 'tideCanvas';
    canvas.width = 600;
    canvas.height = 400;
    container.appendChild(canvas);

    const form = document.createElement('form');
    form.id = 'tideForm';
    form.style.marginTop = '20px';
    form.innerHTML = `
        <label>Number of Harmonics? <input type="number" id="numHarmonics" value="18" min="1" max="18"></label>
        <label>Days to Plot? <input type="number" id="daysToPlot" value="30" min="1" max="60"></label>
        <select id="plotType">
            <option value="sum">Sum</option>
            <option value="single">Single</option>
        </select>
        <button type="button" onclick="calculateTides()">Calculate</button>
        <button type="button" onclick="resetForm()">Reset</button>
    `;
    container.appendChild(form);

    const ctx = canvas.getContext('2d');
    window.calculateTides = function() {
        const numHarmonics = document.getElementById('numHarmonics').value;
        const daysToPlot = document.getElementById('daysToPlot').value;
        const plotType = document.getElementById('plotType').value;
        drawTide(ctx, numHarmonics, daysToPlot, plotType, harmonicsData);
    };

    window.resetForm = function() {
        document.getElementById('numHarmonics').value = '18';
        document.getElementById('daysToPlot').value = '30';
        document.getElementById('plotType').value = 'sum';
        ctx.clearRect(0, 0, canvas.width, canvas.height);
    };

    // Predefined harmonic data for demonstration.
    var harmonicsData = [
        [2.019, 245.8, 28.984], // M(2)
        [0.434, 226.3, 28.439], // N(2)
        [0.356, 265.8, 30.000], // S(2)
        [0.340, 126.5, 15.041], // K(1)
        [0.287, 119.7, 13.943], // O(1)
        [0.217, 150.0, 0.041],  // SA
        [0.121, 41.6, 0.082],   // SSA
        [0.114, 123.5, 14.958], // P(1)
        [0.098, 268.4, 29.528], // L(2)
        [0.096, 267.6, 30.082], // K(2)
        [0.088, 232.1, 28.512], // NU(2)
        [0.041, 255.6, 57.968], // M(4)
        [0.039, 243.8, 27.968], // MU(2)
        [0.036, 235.4, 29.959], // T(2)
        [0.023, 130.0, 15.585], // J(1)
        [0.023, 272.1, 86.952], // M(6)
        [0.021, 123.0, 14.492], // M(1)
        [0.015, 266.9, 58.984]  // MS(4)
    ];
}

function drawTide(ctx, n, nd, type, harmonicsData) {
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
    const totalHours = 24 * nd;
    const dt = totalHours / ctx.canvas.width;
    let maxTideHeight = 0;
    let tideData = new Array(ctx.canvas.width).fill(0);

    for (let i = 0; i < n; i++) {
        const amp = harmonicsData[i][0];
        const phase = harmonicsData[i][1] * Math.PI / 180;
        const speed = harmonicsData[i][2] * Math.PI / 180;

        for (let x = 0; x < ctx.canvas.width; x++) {
            const t = x * dt;
            tideData[x] += amp * Math.cos(speed * t + phase);
        }
    }
    maxTideHeight = Math.max(...tideData.map(Math.abs));

    const scaleY = ctx.canvas.height / (2 * maxTideHeight);

    // Draw Axes
    ctx.beginPath();
    ctx.moveTo(0, ctx.canvas.height);
    ctx.lineTo(ctx.canvas.width, ctx.canvas.height);
    ctx.moveTo(0, 0);
    ctx.lineTo(0, ctx.canvas.height);
    ctx.strokeStyle = 'black';
    ctx.stroke();

    // Plot Tide Data
    ctx.beginPath();
    ctx.moveTo(0, ctx.canvas.height / 2 - tideData[0] * scaleY);
    tideData.forEach((height, index) => {
        ctx.lineTo(index, ctx.canvas.height / 2 - height * scaleY);
    });
    ctx.strokeStyle = 'blue';
    ctx.lineWidth = 2;
    ctx.stroke();

    // Draw Labels
    ctx.fillStyle = 'black';
    ctx.fillText("Time", ctx.canvas.width/2-10, ctx.canvas.height - 5);
    ctx.save();
    ctx.translate(10, ctx.canvas.height / 2-10);
    ctx.rotate(-Math.PI / 2);
    //ctx.fillText("Tide Height", 0, 0);
    ctx.restore();
}
