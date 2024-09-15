import java.lang.Math;

public class Hyper {

   public static double cosh(double x) {
        return (Math.exp(x)+Math.exp(-x))/2.0;
    }
    public static double sinh(double x) {
        return (Math.exp(x)-Math.exp(-x))/2.0;
    }
    public static double tanh(double x) {
        return sinh(x)/cosh(x);
    } 
    public static double sech(double x) {
        return 1./cosh(x);
    }  
    public static double cotanh(double x) {
        return cosh(x)/sinh(x);
    }                    
}
