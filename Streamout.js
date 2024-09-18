class Streamout {
  constructor(NN, NTHTS) {
      this.NN = NN;               // Number of X[] coefficients
      this.NTHTS = NTHTS;         // Number of theta values
      this.X = new Array(NN + 2).fill(0);     // Coefficients
      this.eta = new Array(NTHTS).fill(0);    // Free surface displacements
      this.theta = new Array(NTHTS).fill(0);  // Phase angles
      this.errorH = 0;            // Error in wave height
      this.errorQ = 0;            // Error in Bernoulli condition
      this.msl = 0;               // Mean Sea Level error
      this.damp = 0;              // Damping factor
  }
}
