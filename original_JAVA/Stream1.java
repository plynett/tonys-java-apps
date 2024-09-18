import java.awt.*;
import java.lang.*;
import java.io.*;
import Gauss;
import Refract;
import Hyper;

   public class Stream1  
   {
       Streamout output;
       double H,T,d,L;
       double eta[];      //free surface displacement
       double theta[];    //phase angles
       int NN;            //number of X[] coefficients
       int NTHTS;         //number of theta values (even now)
       double dth;
       double xf1[][],xf2[][];
       double errorH;     // error in wave height
       double errorQ;     // error in DFSBC
       double totalerror;
       double UU, omega;  // current  
       double [] X = new double[NN+2];  //0,1,...,NN,NN+1
       double [][] dqdx= new double[NTHTS][NN+2];
       double [][] detadx = new double[NTHTS][NN+2];
       double [] dqbar= new double[NN+2];
       double msl;
       double damp;
              
      void Stream1(Streamout output)
     {
       int i,n,iter;          //counters
       double klin;
       Refract ref;
       Derive der;
       der= new Derive();
       NN=output.NN;
       damp=output.damp;
       NTHTS=output.NTHTS;
//       System.out.println("NN, NTHTS: "+NN+", "+NTHTS);
       X = new double[NN+2];  //0,1,...,NN,NN+1
       dqdx= new double[NTHTS][NN+2];
       detadx = new double[NTHTS][NN+2];
       dqbar= new double[NN+2];
       // set up the local variables taken from  Wave
       H=Wave.H;
       T=Wave.T;
       d=Wave.d;
       UU=0.0;       
       eta = new double[NTHTS];
       ref= new Refract();
       klin = (double) ref.waveNumber((float) d, (float) T);
       L=2.*Math.PI/klin;
       X[0]=L;
       X[1]=-L*H/(2.*T*Hyper.sinh(2.*Math.PI*(d+H/2.)/L));

//       System.out.println("Computing X:");
       for(i=0;i<NN+2;i++)
         {if(i>2)
               X[i]=0.;
//          System.out.println("X["+i+"]= "+X[i]);
         }

       X[NN]= 0.;
       for(i=0;i<NTHTS;i++)
         {eta[i]=H/2.*Math.cos(Math.PI*i/NTHTS);
         }
      

        dth= Math.PI/(NTHTS-1.);
 
  
        xf1 = new double[NTHTS][NN+1];
        xf2 = new double[NTHTS][NN+1];
    
        theta = new double[NTHTS];
        theta[0]=0;
  
        for(i=1;i<NTHTS;i++)
          { 
          theta[i]=theta[i-1]+dth;
//          System.out.println("i,theta[i]"+i+", "+theta[i]);
           }

//        for(i=0;i<(NN+2);i++) 
//            System.out.println(i+", "+X[i]);

        for(i=0;i<NTHTS;i++)  
         {
          for (n=1;n<NN+1;n++)
           {
           xf1[i][n]=Math.cos(theta[i]*n);
           xf2[i][n]=Math.sin(theta[i]*n);
           }
          }
 
       //Start the iterations
       

       for(iter=0;iter<30 ;iter++)
        {
//            System.out.println("iter: "+iter);
           fscalc(der);
           cff();
        
           totalerror=Math.abs(errorH)+Math.abs(msl)+Math.abs(errorQ);
           if(totalerror<0.0001) 
                 break;

         }   
         System.arraycopy(X,0,output.X,0,NN+2);
         System.arraycopy(eta,0,output.eta,0,NTHTS);
         System.arraycopy(theta,0,output.theta,0,NTHTS);
         output.msl=msl;
         output.errorH=errorH;
         output.errorQ=errorQ;
  
      }  //end of stream--------------------------------------------------------
 
       public void fscalc(Derive der )
       {
        int i;
        for(i=0;i<NTHTS;i++)
          { 
           eta[i]=fsi(i,eta[i],omega,der);
          }

        errorH=H-(eta[0]-eta[NTHTS-1]);
         
       }  // end of fscalc------------------------------------------------------------

      public double fsi(int i, double x, double omega, Derive der )
       {
        int n;
        double epsilon=.001;
        double xpp, xp, yp, u, d,a;
        double ypp,ydpp,ydp;
        xpp=x;
        func(xpp,i,omega,der);
        ypp=der.y;
        ydpp=der.yp;
        xp=xpp-ypp/ydpp;
        for(n=0;n<20; n++)
          {
           func(xp,i,omega,der);  //func calculates y, yp
           yp=der.y;
           ydp=der.yp;
           if(yp==0.0)
                 return xp;

           d=xp-xpp;
           a=(2.*ydp+ydpp-3.*(yp-ypp)/d)/d;
           u=yp/ydp;
           x=xp-u*(1.+u*a/ydp);
           if( (Math.abs((x-xp)/x)-epsilon) >0)
              {
               xpp=xp;
               xp=x;
               ypp=yp;
               ydpp=ydp;
              }
           else 
              return x;
          }
           return x;
        }  //end fsi--------------------------------------------------------------

      public void func(double et, int i, double omega , Derive der )
       {
         int n;
         double C, con,zn,elev,y,yp;

         con= 2.*Math.PI/X[0];
         C=X[0]/T-UU;
         elev=d+et;
         y=X[NN+1]-C*et+omega*elev*elev/2.;
         yp=-C+omega*elev;
         for(n=1;n<NN+1;n++)
           {
            zn=con*(double)n;
             y -= X[n]*Hyper.sinh(zn*elev)*xf1[i][n];
             yp -= X[n]*Hyper.cosh(zn*elev)*xf1[i][n]*zn;
            }
 
         der.y=y;
         der.yp=yp;
       }  //end of func-------------------------------------------------------------

       
        public void cff()
       {
        /* Lesson learned:  only define arrays just before being needed. */
       
        int i,k,m,n;
        int NTHTM1;
        double ErrorH;
        double con;
        double g=9.81;

        double di;
        double dudeta,dvdeta;

        double toth,xn,zn,coef1,coef2,u,v;
        double dqdu, dqdc,dqdv;

        double cc, qbar,dqdeta;
        double uu,u0=0.,sh,ch;
        int NNP1;
         
      
//        for(i=0;i<NN+1;i++)
//           System.out.println("X["+i+"]= "+X[i]);

        con=2.*Math.PI/X[0];
     
        NTHTM1=NTHTS-1;
        qbar=0.0;
        di=dth/(3.0*Math.PI);
       
        double [] dcdx = new double[NN+2];
        dcdx[0]=1.0/T;
        for(n=1;n<NN+2;n++)
          {
          dcdx[n]=0.;
          }
        NNP1=NN+1;

        double [] dudx = new double[NN+2];

        double [] dvdx = new double[NN+2];
        double [] q    = new double[NTHTS];

  
          for(i=0;i<NTHTS;i++)
           {
            u=0.;
            v=0.;
            dudx[0]=0.;
            dvdx[0]=0.;
            
            dvdeta=0.;
            dudeta=0;
            detadx[i][0]=eta[i]/T;
            toth=d+eta[i];
            uu= 0;
            if(omega >0)
               uu=u0+omega*toth;
            
            for(n=1;n<NN+1;n++)
               {
                zn=con*n;
                coef1=xf1[i][n]*X[n];
                coef2=xf2[i][n]*X[n];
                sh=Hyper.sinh(zn*toth);
                ch=Hyper.cosh(zn*toth);
                u -= zn*ch*coef1;
                v -= zn*sh*coef2;
                detadx[i][0] -=zn*toth/X[0]*ch*coef1;
                dvdx[n]=-zn*sh*xf2[i][n];
                dvdeta -=zn*zn*ch*coef2;
                detadx[i][n]=sh*xf1[i][n];
                dudx[0] +=(zn*zn*toth/X[0]*sh+zn/X[0]*ch)*coef1;
                dudx[n]=-zn*ch*xf1[i][n];
                dudeta-=zn*zn*sh*coef1;
                dvdx[0]+=(zn*zn*toth/X[0]*ch+zn*sh/X[0])*coef2;
               }
            cc=u+uu-X[0]/T;
            dqdeta=1.0;
            detadx[i][NN+1]=-1.;
            dudx[NN+1]=0.0;
            dvdx[NN+1]=0.0;
            dqdu=cc/g;
            dqdc=-dqdu;
            dqdv=v/g;
            for(n=0;n<NN+2;n++)
                {detadx[i][n]/=cc;
                 dqdx[i][n]=dqdeta*detadx[i][n]+dqdu*(dudx[n]+dudeta*detadx[i][n])
                    +dqdv*(dvdx[n]+dvdeta*detadx[i][n])+dqdc*dcdx[n];
                }
 
            q[i]=eta[i]+(cc*cc+v*v)/(2.*g);
           } //finishes i loop
//             System.out.println("detadx[1][n]");
//            for (n=0;n<NN+2;n++)
//                System.out.println(detadx[1][n]);

//            System.out.println("dqdx");
//            for (n=0;n<NN+2;n++)
//                System.out.println(dqdx[1][n]);

            for (i=1;i<NTHTM1;i+=2)
                {qbar +=q[i-1]+4.*q[i]+q[i+1];               
                }

           qbar=qbar*di;
//           System.out.println("dqbar[n]");
           for(n=0;n<NN+2;n++)
               {dqbar[n]=0.;
                for(i=1;i<NTHTM1;i+=2)
                  dqbar[n] +=dqdx[i-1][n]+4.*dqdx[i][n]+dqdx[i+1][n];
 
                dqbar[n]*=di;
//                System.out.println(dqbar[n]);
               }
           errorQ=0.0;
           double [] error= new double[NTHTS];
           for(i=0;i<NTHTS;i++)
               {error[i]=q[i]-qbar;
//               System.out.println(q[i]+", "+error[i]);
               }
           for(i=1;i<NTHTM1;i+=2)
               errorQ +=Math.pow(error[i-1],2.)+4.*(Math.pow(error[i],2.))+Math.pow(error[i+1],2.);

           errorQ=Math.sqrt(errorQ*di);
 
//           System.out.println("Qbar= "+qbar);
//           System.out.println("rms error in qbar = "+errorQ);
            
            
//     setup an nn+3 x nnp1 array for b[n][k] 

            double [][] b=new double[NN+4][NN+4];
            double [] dd=new double[NN+4];
            for(n=0;n<NN+2;n++)
               {
                for(m=0;m<NN+4;m++)
                  {
                    
                   b[m][n]=0.0;
                   if(m<NN+2)
                      { for(i=1;i<NTHTM1;i+=2)
                           {b[m][n]+=(dqdx[i-1][n]-dqbar[n])*(dqdx[i-1][m]-dqbar[m])+
                            4.*(dqdx[i][n]-dqbar[n])*(dqdx[i][m]-dqbar[m]) +
                            (dqdx[i+1][n]-dqbar[n])*(dqdx[i+1][m]-dqbar[m]);
                            }
                        b[m][n]*=(dth/3.0);
                       }
                   else if(m==NN+2)    //mean sea level
                       { for(i=1;i<NTHTM1;i+=2)
                          b[m][n]+=detadx[i-1][n]+4.*detadx[i][n]+detadx[i+1][n];
                         b[m][n]*=(dth/3.0);
                        }
                   else if(m==NN+3)   // wave height
                        b[m][n]=detadx[0][n]-detadx[NTHTM1][n];
                  }
               }   //finishes m,n loop

//          lambda1 coefficients
            n=NN+2;
            for(m=0;m<NN+4;m++)
               {dd[m]=0.0;
                b[m][n]=0.0;
                if(m<NN+2)
                  {
                    for(i=1;i<NTHTM1;i+=2)
                      {b[m][n]+=detadx[i-1][m]+4.0*detadx[i][m]+detadx[i+1][m];
                       dd[m]+=(qbar-q[i-1])*(dqdx[i-1][m]-dqbar[m])+4.0*(qbar-q[i])*
                        (dqdx[i][m]-dqbar[m])+(qbar-q[i+1])*(dqdx[i+1][m]-dqbar[m]);
                      }
                     b[m][n]*=(dth/3.0);
                    dd[m]*=(dth/3.0);
                  }
                else if(m==NN+2)
                  {for (i=1;i<NTHTM1;i+=2)
                     dd[m]+=eta[i-1]+4.*eta[i]+eta[i+1];

                   dd[m]*=(dth/3.0);
                   msl=dd[m]/Math.PI;
//                   System.out.println("MSL error"+msl);
                   dd[m]=-dd[m];
                  }
               else if (m==NN+3)
                  { ErrorH=H+eta[NTHTM1]-eta[0];
                    dd[m]=ErrorH;
//                   System.out.println("Error in H: "+ErrorH);
                  }
               }

//         lambda2 coefficients
           n=NN+3;
           for(m=1;m<NN+4;m++)
               { 
                b[m][n]=0.0;
                if(m<NN+2)
                   {b[m][n]=detadx[0][m]-detadx[NTHTM1][m];
                    }
                }
 
//           System.out.println("d(m):");
//           for(m=0;m<NN+4;m++)
//               System.out.println(dd[m]);

           Gauss(b,dd,NN+4); 
//           System.out.println("Array inverted");
    //       
//           System.out.println("damping= "+damp);
           for(n=0;n<NN+2;n++)

             {
              X[n]+=dd[n]*damp;
             }


        

   } //end of cff-----------------------------------------------

    public boolean Gauss(double [][]aa, double b[], int n )
   
     {
       int i,j,k;
       double sum;
       double [][] a = new double[n][n+1];
      
       for(i=0;i<n;i++)
         System.arraycopy(aa[i],0,a[i],0,n);

        for (k=0; k<n;k++)  //augment the a matrix with b
          a[k][n]=b[k];

       if(uptriangle(a,n) == false)
         {System.out.println("Dependent equations!");
          return false;
          }
       for(i=n-1; i>=0; i--)
          {sum=0;
           for(j=i+1;j<=n-1;j++)
             sum+=a[i][j]*b[j];
           if(a[i][i]!=0)
               b[i]=(a[i][n]-sum)/a[i][i];
          
               
            else
              return false;
           }
     
       return true;
      }
     


    public boolean uptriangle(double a[][], int n)
     {
       int i, j, k;
       for (k=0; k<n; k++)
        { if (findpivot(a,k,n) == true)
             process_col(a,k,n);
          else
             return false;
         }
         return true;
      }

     public void process_col(double a[][], int k, int n)
      { int i,j;
        double m;
  
        for (i=k+1; i<n; i++)
         {
          m=-a[i][k]/a[k][k];
          for (j=k; j<=n; j++)
             a[i][j]+=m*a[k][j];
          }
      }

      public boolean findpivot(double a[][], int k, int n)
       { int j;
         if(a[k][k]==0)
           {j=findnonzero(a,k,n);
            if(j<0)
               return false;  
            else
               swaprows(a,k,j,n);
            }
          return true;
       }

      public int findnonzero(double a[][], int k, int n) 
       {
         int i;
         for(i=k;i<n;i++)
            if(a[i][k] !=0)
               return(i);
            return(-1);
        }

      public void swaprows(double a[][], int k, int j, int n) 
        {
          int i;
          double temp;
          for(i=k;i<=n;i++)
            {
             temp=a[k][i];
             a[k][i]=a[j][i];
             a[j][i]=temp;
            }
         }
 
 



  

      
} //end stream class ---------------------------------------------------------------

  
    class Derive
    {
      double y,yp;
      
     
    }
   

 
