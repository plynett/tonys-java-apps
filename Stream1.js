class Stream1 {
    constructor() {
        this.output = null;
        this.H = 0;
        this.T = 0;
        this.d = 0;
        this.L = 0;
        this.eta = []; // Free surface displacement
        this.theta = []; // Phase angles
        this.NN = 0; // Number of X[] coefficients
        this.NTHTS = 0; // Number of theta values
        this.dth = 0;
        this.xf1 = [];
        this.xf2 = [];
        this.errorH = 0; // Error in wave height
        this.errorQ = 0; // Error in DFSBC
        this.totalerror = 0;
        this.UU = 0;
        this.omega = 0; // Current
        this.X = []; // X coefficients array
        this.dqdx = [];
        this.detadx = [];
        this.dqbar = [];
        this.msl = 0;
        this.damp = 0;
    }

    Stream1(output) {
        this.output = output;
        let ref = new Refract();
        let der = new Derive();
        this.NN = output.NN;
        this.damp = output.damp;
        this.NTHTS = output.NTHTS;

        this.X = new Array(this.NN + 2).fill(0);
        this.dqdx = new Array(this.NTHTS).fill(0).map(() => new Array(this.NN + 2).fill(0));
        this.detadx = new Array(this.NTHTS).fill(0).map(() => new Array(this.NN + 2).fill(0));
        this.dqbar = new Array(this.NN + 2).fill(0);

        this.H = Wave.H;
        this.T = Wave.T;
        this.d = Wave.d;
        this.UU = 0;

        this.eta = new Array(this.NTHTS).fill(0);
        let klin = ref.waveNumber(this.d, this.T);
        this.L = 2 * Math.PI / klin;

        this.X[0] = this.L;
        this.X[1] = -this.L * this.H / (2 * this.T * Hyper.sinh((2 * Math.PI * (this.d + this.H / 2)) / this.L));

        this.dth = Math.PI / (this.NTHTS - 1);
        this.xf1 = new Array(this.NTHTS).fill(0).map(() => new Array(this.NN + 1).fill(0));
        this.xf2 = new Array(this.NTHTS).fill(0).map(() => new Array(this.NN + 1).fill(0));

        this.theta = new Array(this.NTHTS).fill(0);
        this.theta[0] = 0;
        for (let i = 1; i < this.NTHTS; i++) {
            this.theta[i] = this.theta[i - 1] + this.dth;
        }

        for (let i = 0; i < this.NTHTS; i++) {
            for (let n = 1; n <= this.NN; n++) {
                this.xf1[i][n] = Math.cos(this.theta[i] * n);
                this.xf2[i][n] = Math.sin(this.theta[i] * n);
            }
        }

        for (let iter = 0; iter < 30; iter++) {
            this.fscalc(der);
            this.cff();

            this.totalerror = Math.abs(this.errorH) + Math.abs(this.msl) + Math.abs(this.errorQ);
            if (this.totalerror < 0.0001) break;
        }

        output.X = [...this.X];
        output.eta = [...this.eta];
        output.theta = [...this.theta];
        output.msl = this.msl;
        output.errorH = this.errorH;
        output.errorQ = this.errorQ;
    }

    fscalc(der) {
        for (let i = 0; i < this.NTHTS; i++) {
            this.eta[i] = this.fsi(i, this.eta[i], this.omega, der);
        }

        this.errorH = this.H - (this.eta[0] - this.eta[this.NTHTS - 1]);
    }

    fsi(i, x, omega, der) {
        let epsilon = 0.001;
        let xpp = x;
        this.func(xpp, i, omega, der);
        let ypp = der.y;
        let ydpp = der.yp;
        let xp = xpp - ypp / ydpp;
        for (let n = 0; n < 20; n++) {
            this.func(xp, i, omega, der);
            let yp = der.y;
            let ydp = der.yp;

            if (yp === 0) return xp;

            let d = xp - xpp;
            let a = (2 * ydp + ydpp - 3 * (yp - ypp) / d) / d;
            let u = yp / ydp;
            x = xp - u * (1 + (u * a) / ydp);
            if (Math.abs((x - xp) / x) - epsilon > 0) {
                xpp = xp;
                xp = x;
                ypp = yp;
                ydpp = ydp;
            } else {
                return x;
            }
        }
        return x;
    }

    func(et, i, omega, der) {
        let con = (2 * Math.PI) / this.X[0];
        let C = this.X[0] / this.T - this.UU;
        let elev = this.d + et;
        let y = this.X[this.NN + 1] - C * et + (omega * elev * elev) / 2;
        let yp = -C + omega * elev;

        for (let n = 1; n <= this.NN; n++) {
            let zn = con * n;
            y -= this.X[n] * Hyper.sinh(zn * elev) * this.xf1[i][n];
            yp -= this.X[n] * Hyper.cosh(zn * elev) * this.xf1[i][n] * zn;
        }

        der.y = y;
        der.yp = yp;
    }
    cff() {
        let NTHTM1 = this.NTHTS - 1;
        let g = 9.81;
        let con = 2 * Math.PI / this.X[0];
        let di = this.dth / (3 * Math.PI);
        let qbar = 0.0;
        let dvdeta = 0.0;
        let dudeta = 0.0;
    
        let dcdx = new Array(this.NN + 2).fill(0);
        dcdx[0] = 1.0 / this.T;
    
        let dudx = new Array(this.NN + 2).fill(0);
        let dvdx = new Array(this.NN + 2).fill(0);
        let q = new Array(this.NTHTS).fill(0);
    
        // Loop over theta points
        for (let i = 0; i < this.NTHTS; i++) {
            let u = 0.0;
            let v = 0.0;
            dudx[0] = 0.0;
            dvdx[0] = 0.0;
    
            let toth = this.d + this.eta[i];
            let uu = 0.0;
            if (this.omega > 0) {
                uu = this.UU + this.omega * toth;
            }
    
            for (let n = 1; n <= this.NN; n++) {
                let zn = con * n;
                let coef1 = this.xf1[i][n] * this.X[n];
                let coef2 = this.xf2[i][n] * this.X[n];
                let sh = Hyper.sinh(zn * toth);
                let ch = Hyper.cosh(zn * toth);
    
                u -= zn * ch * coef1;
                v -= zn * sh * coef2;
                this.detadx[i][0] -= (zn * toth / this.X[0]) * ch * coef1;
                dvdx[n] = -zn * sh * this.xf2[i][n];
                dvdeta -= zn * zn * ch * coef2;
                this.detadx[i][n] = sh * this.xf1[i][n];
                dudx[0] += (zn * zn * toth / this.X[0] * sh + zn / this.X[0] * ch) * coef1;
                dudx[n] = -zn * ch * this.xf1[i][n];
                dudeta -= zn * zn * sh * coef1;
                dvdx[0] += (zn * zn * toth / this.X[0] * ch + zn * sh / this.X[0]) * coef2;
            }
    
            let cc = u + uu - this.X[0] / this.T;
            let dqdeta = 1.0;
            this.detadx[i][this.NN + 1] = -1.0;
            dudx[this.NN + 1] = 0.0;
            dvdx[this.NN + 1] = 0.0;
            let dqdu = cc / g;
            let dqdc = -dqdu;
            let dqdv = v / g;
    
            for (let n = 0; n <= this.NN + 1; n++) {
                this.detadx[i][n] /= cc;
                this.dqdx[i][n] =
                    dqdeta * this.detadx[i][n] +
                    dqdu * (dudx[n] + dudeta * this.detadx[i][n]) +
                    dqdv * (dvdx[n] + dvdeta * this.detadx[i][n]) +
                    dqdc * dcdx[n];
            }
    
            q[i] = this.eta[i] + (cc * cc + v * v) / (2 * g);
        }
    
        // Calculate qbar
        for (let i = 1; i < NTHTM1; i += 2) {
            qbar += q[i - 1] + 4.0 * q[i] + q[i + 1];
        }
        qbar *= di;
    
        // Calculate dqbar
        for (let n = 0; n <= this.NN + 1; n++) {
            this.dqbar[n] = 0.0;
            for (let i = 1; i < NTHTM1; i += 2) {
                this.dqbar[n] += this.dqdx[i - 1][n] + 4.0 * this.dqdx[i][n] + this.dqdx[i + 1][n];
            }
            this.dqbar[n] *= di;
        }
    
        // Calculate error in qbar
        this.errorQ = 0.0;
        let error = new Array(this.NTHTS).fill(0);
        for (let i = 0; i < this.NTHTS; i++) {
            error[i] = q[i] - qbar;
        }
        for (let i = 1; i < NTHTM1; i += 2) {
            this.errorQ += Math.pow(error[i - 1], 2) + 4.0 * Math.pow(error[i], 2) + Math.pow(error[i + 1], 2);
        }
        this.errorQ = Math.sqrt(this.errorQ * di);
    
        // Setup a (NN+3) x (NN+1) array for b[n][k]
        let b = new Array(this.NN + 4).fill(0).map(() => new Array(this.NN + 4).fill(0));
        let dd = new Array(this.NN + 4).fill(0);
    
        for (let n = 0; n <= this.NN + 1; n++) {
            for (let m = 0; m <= this.NN + 3; m++) {
                b[m][n] = 0.0;
                if (m < this.NN + 2) {
                    for (let i = 1; i < NTHTM1; i += 2) {
                        b[m][n] +=
                            (this.dqdx[i - 1][n] - this.dqbar[n]) * (this.dqdx[i - 1][m] - this.dqbar[m]) +
                            4.0 * (this.dqdx[i][n] - this.dqbar[n]) * (this.dqdx[i][m] - this.dqbar[m]) +
                            (this.dqdx[i + 1][n] - this.dqbar[n]) * (this.dqdx[i + 1][m] - this.dqbar[m]);
                    }
                    b[m][n] *= this.dth / 3.0;
                } else if (m === this.NN + 2) {
                    for (let i = 1; i < NTHTM1; i += 2) {
                        b[m][n] += this.detadx[i - 1][n] + 4.0 * this.detadx[i][n] + this.detadx[i + 1][n];
                    }
                    b[m][n] *= this.dth / 3.0;
                } else if (m === this.NN + 3) {
                    b[m][n] = this.detadx[0][n] - this.detadx[NTHTM1][n];
                }
            }
        }
    
        // lambda1 coefficients
        let n = this.NN + 2;
        for (let m = 0; m <= this.NN + 3; m++) {
            dd[m] = 0.0;
            b[m][n] = 0.0;
            if (m < this.NN + 2) {
                for (let i = 1; i < NTHTM1; i += 2) {
                    b[m][n] += this.detadx[i - 1][m] + 4.0 * this.detadx[i][m] + this.detadx[i + 1][m];
                    dd[m] += (qbar - q[i - 1]) * (this.dqdx[i - 1][m] - this.dqbar[m]) +
                        4.0 * (qbar - q[i]) * (this.dqdx[i][m] - this.dqbar[m]) +
                        (qbar - q[i + 1]) * (this.dqdx[i + 1][m] - this.dqbar[m]);
                }
                b[m][n] *= this.dth / 3.0;
                dd[m] *= this.dth / 3.0;
            } else if (m === this.NN + 2) {
                for (let i = 1; i < NTHTM1; i += 2) {
                    dd[m] += this.eta[i - 1] + 4.0 * this.eta[i] + this.eta[i + 1];
                }
                dd[m] *= this.dth / 3.0;
                this.msl = dd[m] / Math.PI;
                dd[m] = -dd[m];
            } else if (m === this.NN + 3) {
                this.errorH = this.H + this.eta[NTHTM1] - this.eta[0];
                dd[m] = this.errorH;
            }
        }
    
        // lambda2 coefficients
        n = this.NN + 3;
        for (let m = 1; m <= this.NN + 3; m++) {
            b[m][n] = 0.0;
            if (m < this.NN + 2) {
                b[m][n] = this.detadx[0][m] - this.detadx[NTHTM1][m];
            }
        }
    
        // Solve the matrix using the previously defined Gauss function
        new Gauss().Gauss({ a: b, b: dd, n: this.NN + 4 });
    
        // Apply the damping factor and update X
        for (let n = 0; n <= this.NN + 1; n++) {
            this.X[n] += dd[n] * this.damp;
        }
    }
    
}


class Gauss {
    Gauss({ a, b, n }) {
        let i, j, k;
        let sum;
        let aa = Array.from(a, row => row.slice());
        
        // Gaussian elimination
        for (k = 0; k < n; k++) {
            aa[k][n] = b[k];
        }

        if (!this.uptriangle(aa, n)) {
            console.error("Dependent equations!");
            return false;
        }

        for (i = n - 1; i >= 0; i--) {
            sum = 0;
            for (j = i + 1; j < n; j++) {
                sum += aa[i][j] * b[j];
            }
            if (aa[i][i] !== 0) {
                b[i] = (aa[i][n] - sum) / aa[i][i];
            } else {
                return false;
            }
        }

        return true;
    }

    uptriangle(a, n) {
        for (let k = 0; k < n; k++) {
            if (this.findpivot(a, k, n)) {
                this.process_col(a, k, n);
            } else {
                return false;
            }
        }
        return true;
    }

    process_col(a, k, n) {
        for (let i = k + 1; i < n; i++) {
            let m = -a[i][k] / a[k][k];
            for (let j = k; j <= n; j++) {
                a[i][j] += m * a[k][j];
            }
        }
    }

    findpivot(a, k, n) {
        if (a[k][k] === 0) {
            let j = this.findnonzero(a, k, n);
            if (j < 0) return false;
            this.swaprows(a, k, j, n);
        }
        return true;
    }

    findnonzero(a, k, n) {
        for (let i = k; i < n; i++) {
            if (a[i][k] !== 0) return i;
        }
        return -1;
    }

    swaprows(a, k, j, n) {
        for (let i = k; i <= n; i++) {
            [a[k][i], a[j][i]] = [a[j][i], a[k][i]];
        }
    }
}



class Derive {
    constructor() {
        this.y = 0;   // Function value
        this.yp = 0;  // Derivative value
    }
}

class Wave {
    static H = 0;  // Wave height
    static T = 0;  // Wave period
    static d = 0;  // Local depth
    static L = 0;  // Wave length
    static etac = 0;  // Surface elevation

    static setValues(H, T, d) {
        Wave.H = H;
        Wave.T = T;
        Wave.d = d;
    }
}

