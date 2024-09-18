import Hyper;
class Refract {    
        static double k0, thet, n, Kr, conver, Ks, g ,   L, C, Cg, xkh0;
        static double cth,dxkh,th,ch,xkh,xkhzero,k, f, fprime;
        static final double G= 9.817;
  
       float waveNumber(float h, float T) 
        {
	 

 	xkh0=(Math.pow(2.*Math.PI/T,2))*h/G;
	cth=Hyper.cotanh(Math.pow(xkh0,.75));
	xkh=xkh0*(Math.pow(cth,.666));
	xkhzero=xkh/h;
	do {
	th=Hyper.tanh(xkh);
	ch=Hyper.cosh(xkh);
	f=xkh0-xkh*th;
	fprime= - xkh*Math.pow(1/ch,2) -th;
	dxkh= -f/fprime;
	xkh=xkh+dxkh;
	} while (Math.abs(dxkh/xkh) > .00000001);
	k=xkh/h;
	return (float) k;
    }
        double waveNumber(double h, double T) 
        {
	 

 	xkh0=(Math.pow(2.*Math.PI/T,2))*h/G;
	cth=Hyper.cotanh(Math.pow(xkh0,.75));
	xkh=xkh0*(Math.pow(cth,.666));
	xkhzero=xkh/h;
	do {
	th=Hyper.tanh(xkh);
	ch=Hyper.cosh(xkh);
	f=xkh0-xkh*th;
	fprime= - xkh*Math.pow(1/ch,2) -th;
	dxkh= -f/fprime;
	xkh=xkh+dxkh;
	} while (Math.abs(dxkh/xkh) > .00000001);
	k=xkh/h;
	return  k;
    }
         float groupVelocity(float T, float h, float k)
        {
          n =0.5*(1.+2.*k*h/Hyper.sinh((double) 2.*k*h));
          L =2.*Math.PI/k;
          C = L/T;
          Cg = n*C;
          return (float) Cg;
        }
         double groupVelocity(double T, double h, double k)
        {
          n =0.5*(1.+2.*k*h/Hyper.sinh( 2.*k*h));
          L =2.*Math.PI/k;
          C = L/T;
          Cg = n*C;
          return  Cg;
        }

        float theta(float thet0, float k, float T)
        {
          k0 = Math.pow((2.*Math.PI/T),2)/G;
          conver =   Math.PI/180.;
    
          thet =(Math.asin(k0*Math.sin( conver*thet0)/k))/conver;
          return (float) thet;
        }
         double theta(double thet0, double k, double T)
        {
          k0 = Math.pow((2.*Math.PI/T),2)/G;
          conver =   Math.PI/180.;
    
          thet =(Math.asin(k0*Math.sin( conver*thet0)/k))/conver;
          return thet;
        }
 
        float refractionCoef(float thet0, float thet)
        {
          Kr =Math.sqrt(Math.cos(conver*thet0)/Math.cos(conver*thet));
          return (float) Kr;
        }

        float shoalingCoef( float T, float fCg)
        {
          Ks =Math.sqrt(G*T/(4.*Math.PI*fCg));
          return  (float) Ks ;
        }
        double refractionCoef(double thet0, double thet)
        {
          Kr =Math.sqrt(Math.cos(conver*thet0)/Math.cos(conver*thet));
          return  Kr;
        }

        double shoalingCoef( double T, double dCg)
        {
          Ks =Math.sqrt(G*T/(4.*Math.PI*dCg));
          return   Ks ;
        }
}
