class ErrorFunction {
  // Error function equivalent in JavaScript
  erf(x) {
      let er;
      if (x < 0) {
          er = -this.gammpf(0.5, x * x);
      } else {
          er = this.gammpf(0.5, x * x);
      }
      return er;
  }

  // Incomplete gamma function equivalent
  gammpf(a, x) {
      if (x < 0 || a < 0) {
          console.error("bad argument in gammp");
      }
      if (x < a + 1) {
          return this.gser(a, x);
      } else {
          return 1 - this.gcf(a, x);
      }
  }

  gammqf(a, x) {
      if (x < 0 || a < 0) {
          console.error("bad argument in gammq");
      }
      if (x < a + 1) {
          return 1 - this.gser(a, x);
      } else {
          return this.gcf(a, x);
      }
  }

  gser(a, x) {
      let itmax = 100;
      let eps = 3e-7;
      let ap, del, sum;

      let gln = this.gammlnf(a);
      if (x < 0) {
          console.error("x < 0 in gser");
          return 0;
      }
      ap = a;
      sum = 1 / a;
      del = sum;
      for (let n = 0; n < itmax; n++) {
          ap += 1;
          del = del * x / ap;
          sum += del;
          if (Math.abs(del) < Math.abs(sum) * eps) break;
      }
      return sum * Math.exp(-x + a * Math.log(x) - gln);
  }

  gcf(a, x) {
      let itmax = 100;
      let eps = 3e-7;
      let fpmin = 1e-30;
      let b = x + 1 - a;
      let c = 1 / fpmin;
      let d = 1 / b;
      let h = d;

      let gln = this.gammlnf(a);
      for (let i = 0; i < itmax; i++) {
          let an = -i * (i - a);
          b += 2;
          d = an * d + b;
          if (Math.abs(d) < fpmin) d = fpmin;
          c = b + an / c;
          if (Math.abs(c) < fpmin) c = fpmin;
          d = 1 / d;
          let del = d * c;
          h *= del;
          if (Math.abs(del - 1) < eps) break;
      }
      return Math.exp(-x + a * Math.log(x) - gln) * h;
  }

  gammlnf(xx) {
      let cof = [
          76.18009172947146, -86.50532032941677, 24.01409824083091,
          -1.231739572450155, 0.001208650973866179, -5.395239384953e-6
      ];
      let y = xx;
      let tmp = xx + 5.5;
      tmp = (xx + 0.5) * Math.log(tmp) - tmp;
      let ser = 1.000000000190015;

      for (let j = 0; j < 6; j++) {
          y += 1;
          ser += cof[j] / y;
      }
      return tmp + Math.log(2.5066282746310005 * ser / xx);
  }
}
