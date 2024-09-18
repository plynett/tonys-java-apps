// Wvnumev.js

class Wvnumev {
    static wvnumev(h, T, n) {
        const G = 9.817; // Gravity constant
        const pi = Math.PI;
        const eps = 0.0001; // Convergence tolerance
        let k = 0;

        const om = 2 * pi / T;
        const sig = (om * om * h) / G;
        const betan = (n * pi * pi - 4 * n + 2) * pi / (2 * (8 * n - n * pi * pi - 2));
        const og = 1 / betan + (4 * n) / ((2 * n - 1) * pi);
        const an = (2 * n - 1) * betan * og;

        const p1 = an + sig;
        const p2 = sig * betan + an * (1 / og - pi / 2);
        const p3 = (-an * pi) / (og * 2);
        const r1 = (-p2 + Math.sqrt(p2 * p2 - 4 * p1 * p3)) / (2 * p1);
        const r2 = (-p2 - Math.sqrt(p2 * p2 - 4 * p1 * p3)) / (2 * p1);
        let k1 = r1;
        if (r1 < 0) k1 = r2;
        else if (r1 > r2) k1 = r2;

        let kpp = k1 + (n - 0.5) * pi;
        let fpp = sig * G + G * kpp * Math.tan(kpp);
        let fdpp = G * Math.tan(kpp) + G * kpp / (Math.cos(kpp) * Math.cos(kpp));

        let kp = kpp - fpp / fdpp;

        for (let i = 1; i < 24; i++) {
            const fp = om * om * h + G * kp * Math.tan(kp);
            const fdp = G * Math.tan(kp) + G * kp / (Math.cos(kp) * Math.cos(kp));
            const d = kp - kpp;
            const a = (2 * fdp + fdpp - 3 * (fp - fpp) / d) / d;
            const u = fp / fdp;
            const kh = kp - u * (1 + (u * a) / fdp);

            if (kh < 0) kh = Math.abs(kh);

            if (Math.abs((kh - kp) / kh) < eps) {
                k = kh / h;
                break;
            }

            kpp = kp;
            kp = kh;
            fpp = fp;
            fdpp = fdp;
        }

        return k;
    }
}
