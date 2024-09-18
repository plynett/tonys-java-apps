// Refract.js
class Refract {
    constructor() {
        this.G = 9.817;
        this.conver = Math.PI / 180.0;
    }

    waveNumber(h, T) {
        let xkh0 = (Math.pow(2 * Math.PI / T, 2)) * h / this.G;
        let cth = Hyper.cotanh(Math.pow(xkh0, 0.75));
        let xkh = xkh0 * Math.pow(cth, 0.6666667);
        let dxkh;

        do {
            let th = Math.tanh(xkh);
            let ch = Hyper.cosh(xkh);
            let f = xkh0 - xkh * th;
            let fprime = -xkh * Math.pow(1 / ch, 2) - th;
            dxkh = -f / fprime;
            xkh = xkh + dxkh;
        } while (Math.abs(dxkh / xkh) > 1e-8);

        let k = xkh / h;
        return k;
    }

    groupVelocity(T, h, k) {
        let n = 0.5 * (1.0 + (2.0 * k * h) / Hyper.sinh(2.0 * k * h));
        let L = 2.0 * Math.PI / k;
        let C = L / T;
        let Cg = n * C;
        return Cg;
    }

    theta(thet0, k, T) {
        let k0 = Math.pow((2.0 * Math.PI / T), 2) / this.G;
        let thet = Math.asin((k0 * Math.sin(this.conver * thet0)) / k) / this.conver;
        return thet;
    }

    refractionCoef(thet0, thet) {
        let Kr = Math.sqrt(Math.cos(this.conver * thet0) / Math.cos(this.conver * thet));
        return Kr;
    }

    shoalingCoef(T, Cg) {
        let Ks = Math.sqrt((this.G * T) / (4.0 * Math.PI * Cg));
        return Ks;
    }
}
