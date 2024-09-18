class Gauss {
    gauss(array) {
        let a = [];
        let b = [];
        let n = array.n;
        let sum;

        // Copy the right-hand side (rhs) and the coefficient matrix
        b = array.b.slice();
        for (let i = 0; i < n; i++) {
            a[i] = array.a[i].slice();
        }

        // Augment the a matrix with b
        for (let k = 0; k < n; k++) {
            a[k][n] = b[k];
        }

        // Perform the Gaussian elimination
        if (!this.upTriangle(a, n)) {
            console.log("Dependent equations!");
            return false;
        }

        // Back substitution to solve the system
        for (let i = n - 1; i >= 0; i--) {
            sum = 0;
            for (let j = i + 1; j < n; j++) {
                sum += a[i][j] * b[j];
            }
            if (a[i][i] !== 0) {
                b[i] = (a[i][n] - sum) / a[i][i];
            } else {
                return false;
            }
        }

        // Copy the solution back to array.b
        for (let i = 0; i < n; i++) {
            array.b[i] = b[i];
        }

        return true;
    }

    upTriangle(a, n) {
        for (let k = 0; k < n; k++) {
            if (this.findPivot(a, k, n)) {
                this.processCol(a, k, n);
            } else {
                return false;
            }
        }
        return true;
    }

    processCol(a, k, n) {
        for (let i = k + 1; i < n; i++) {
            let m = -a[i][k] / a[k][k];
            for (let j = k; j <= n; j++) {
                a[i][j] += m * a[k][j];
            }
        }
    }

    findPivot(a, k, n) {
        if (a[k][k] === 0) {
            let j = this.findNonZero(a, k, n);
            if (j < 0) {
                return false;
            } else {
                this.swapRows(a, k, j, n);
            }
        }
        return true;
    }

    findNonZero(a, k, n) {
        for (let i = k; i < n; i++) {
            if (a[i][k] !== 0) {
                return i;
            }
        }
        return -1;
    }

    swapRows(a, k, j, n) {
        for (let i = k; i <= n; i++) {
            let temp = a[k][i];
            a[k][i] = a[j][i];
            a[j][i] = temp;
        }
    }
}
