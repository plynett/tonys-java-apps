// Hyper.js
class Hyper {
    static cosh(x) {
        return (Math.exp(x) + Math.exp(-x)) / 2.0;
    }
    static sinh(x) {
        return (Math.exp(x) - Math.exp(-x)) / 2.0;
    }
    static tanh(x) {
        return this.sinh(x) / this.cosh(x);
    }
    static sech(x) {
        return 1.0 / this.cosh(x);
    }
    static cotanh(x) {
        return this.cosh(x) / this.sinh(x);
    }
}
