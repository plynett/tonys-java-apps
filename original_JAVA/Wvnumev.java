class Wvnumev
    {   
       
        static final double G= 9.817;
      
  
       float wvnumev(float h, float T, int n) 
        {
	 double sig, om, betan,og,an,p1,p2,p3,r1,r2,k1,kpp,fpp;
         double fdpp,kp,tkpp,pi,kh,k,eps,fp,fdp,u,a,d;
         pi=Math.PI;
         eps=0.0001;
         k=0;
         om=2*pi/T;
         sig=om*om*h/G;
         betan=(n*pi*pi-4*n+2)*pi/(2*(8*n-n*pi*pi-2));
         og=1/betan + 4*n/((2*n-1)*pi);
         an=(2*n-1)*betan*og;
         p1=an+sig;
         p2=sig*betan+an*(1/og -pi/2);
         p3=-an*pi/(og*2);
         r1=(-p2+Math.sqrt(p2*p2-4.*p1*p3))/(2*p1);
         r2=(-p2-Math.sqrt(p2*p2-4.*p1*p3))/(2*p1);
         k1=r1;
         if (r1<0) 
           k1=r2;
         else if (r1>r2)
           k1=r2;
         
         kpp=k1+(n-0.5)*pi;
         tkpp=Math.tan(kpp);
         fpp=sig*G+G*kpp*tkpp;
         fdpp=G*tkpp + G*kpp/(Math.cos(kpp)*Math.cos(kpp));
         kp=kpp-fpp/fdpp;

         for (int i=1;i<24;i++)
          {
           fp=(om*om)*h+G*kp*Math.tan(kp);
           fdp=G*Math.tan(kp) + G*kp/(Math.cos(kp)*Math.cos(kp));
           d=kp-kpp;
           a=(2.*fdp+fdpp-3.*(fp-fpp)/d)/d;
           u=fp/fdp;
           kh=kp-u*(1.+u*a/fdp);
           if (kh<0)
             kh=Math.abs(kh);
          
          if ( Math.abs((kh-kp)/kh)<eps)
            {k=kh/h;
            break;
            };
          kpp=kp;
          kp=kh;
          fpp=fp;
          fdpp=fdp;

         }

 	return (float) k;
    }
           
}
