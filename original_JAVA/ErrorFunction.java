/*  erf class  --transcribed from Press et al.

*/

     public class ErrorFunction
     {
      public double erf(double x)
       {double er;
        double gammpf;
        if(x<0)
           er=-gammpf(.5,x*x);
        else
           er=gammpf(.5,x*x);
        return er;
       }
      public double gammpf(double a, double x)
       {
        //incomplete gamma function
        double gammp;
        
        if(x < 0. || a <0) 
            System.out.println("bad arg in gammp");
        if(x< a+1)
            gammp=gser(a,x);
         
        else
            gammp=1.-gcf(a,x);
            
        return gammp;
       }

     public double gammqf(double a, double x)
       {
        double gammq;

        if(x<0 || a<0) 
           System.out.println("bad arg in gammq");
        
        if(x<a+1)
           gammq=1.0000000-gser(a,x);     
           
        else
           gammq=gcf(a,x);
           
           
        return gammq;
        }

      public double gser(double a, double x)
        {
         double gamser,gln;
         int itmax=100;
         double eps=3.e-7;
         int n;
         double ap,del,sum,gammln;
   
         gln=gammlnf(a);
         if(x<0)
             {System.out.println("x<0 in gser");
              gamser=0.;
              return gamser;
             }
         ap=a;
         sum=1./a;
         del=sum;
         for (n=0;n<itmax;n++)
           {ap=ap+1;
            del=del*x/ap;
            sum +=del;
            if(Math.abs(del)<Math.abs(sum)*eps)
              break;
           }
         gamser=sum*Math.exp(-x+a*Math.log(x)-gln);
         return gamser;
        }

      public double gcf(double a, double x)
        {
         int itmax=100;
         double gammcf,gln;
         double eps=3.e-7;
         double fpmin=1.e-30;
         int i;
         double an,b,c,d,del,h,gammlnf;
   
         gln=gammlnf(a);
         b=x+1.-a;
         c=1./fpmin;
         d=1./b;
         h=d;
   
         for(i=0;i<itmax;i++)
          {an=-i*(i-a);
           b=b+2;
           d=an*d+b;
           if(Math.abs(d)<fpmin) 
              d=fpmin;

           c=b+an/c;
           if(Math.abs(c)<fpmin)
              c=fpmin;

           d=1./d;
           del=d*c;
           h=h*del;
           if(Math.abs(del-1.)<eps)
               break;
          }
         gammcf=Math.exp(-x+a*Math.log(x)-gln)*h;
         return gammcf;
        }

       public double gammlnf(double xx)
        {
         double gammln;
         int j;
         double ser,stp=2.5066282746310005,tmp,x,y;
         double []cof={76.18009172947146,-86.50532032941677,24.01409824083091,
             -1.231739572450155,.1208650973866179e-02,-.5395239384953e-05};

         x=xx;
         y=x;
         tmp=x+5.5000000000000;

         tmp=(x+.500000000000000)*Math.log(tmp)-tmp;
         ser=1.000000000190015;
         for(j=0;j<6;j++)
            {y=y+1.0000000000000000;
            ser +=cof[j]/y;
           }
         gammln=tmp+Math.log(stp*ser/x);
         return gammln;
        }

         
       
         
}



           
          
  
          

         
       
   
