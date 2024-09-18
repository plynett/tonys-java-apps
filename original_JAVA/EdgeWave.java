   
   public class EdgeWave
   {
     double [] h;        //water depth as function of x
     double lambda, dx,T;
     double [] k;        //wave number from linear theory
     double [] kc2;       //modified wave number
     double [] x;        //offshore distance
     double  C;        // local celerity
     double  Cg;       // group velocity
     double [] sCCg;
     int   npts;         //number of offshore points
     double [][] b;
     Refract ref;
     Eigenmatrix eig;
     Jacobi jac;
     Bathymetry bath;

     public void EdgeWave(Bathymetry Bath, Eigenmatrix eig)
     {
       
       int i,j;
       double dx2;
       npts = Bath.NPTS;
       System.out.println("No. points from Bathymetry: "+npts);
       k = new double[npts];
       sCCg = new double[npts];
       kc2 = new double[npts];
       dx = Bath.DX;
//       System.out.println("in EdgeWave: npts and dx: "+ npts +", "+dx);
       h= new double[npts];
//       System.out.println("Bathymetry, h, in EdgeWave:");
       for(i=0;i<npts;i++)
            {h[i]=Bath.h[i];
//             System.out.println(h[i]);
            }
       T = Wave.T;
//       System.out.println("Period= "+T);
       ref = new Refract();
       k = new double[npts];
       // Note for the first point depth is zero.
       System.out.println("i, h, k, C, Cg, sCCg");
       for( i=1;i<npts;i++)
          {k[i] = ref.waveNumber(h[i], T);
           C = 2.*Math.PI/(k[i]*T);
           Cg= ref.groupVelocity(T, h[i],k[i]);
           sCCg[i]= Math.sqrt(C*Cg);
           System.out.println(i+", "+h[i]+", "+k[i]+", "+C+", "+Cg+", "+sCCg[i]);
           }
       sCCg[0]=-100.;
       double hx, hxx;
       System.out.println("kc2s");
       hx=(h[2]-h[0])/(2.*dx);
       hxx=(h[2]-2.*h[1]+h[0])/(dx*dx);
       kc2[1]=Math.pow(k[1],2)- hxx/(2.*h[1])+Math.pow(hx/h[1],2)/4.;
       System.out.println("1, "+kc2[1]);
       for ( i=2;i<(npts-1);i++)
           {kc2[i]=Math.pow(k[i],2)-(sCCg[i+1]-2.*sCCg[i]+sCCg[i-1])/(dx*dx*sCCg[i]);
             System.out.println(i+", "+kc2[i]);
           }
       b = new double[npts-1][npts-1];
//     zero out the zeroth row and column
       for(j=0;j<npts-1;j++)
          {b[0][j]=0.0;
           b[j][0]=0.0;
          }
//     row one  (Note defining the computational array from 1 to npts-2)   
       dx2=dx*dx;
       b[1][1]=kc2[1]*dx2-2.0;
       b[1][2]= 1.0;
       for(j=3;j<npts-1;j++)
            {b[1][j]=0.0;
            }
//     rest of the rows, except the last
       for( i=2;i<npts-2;i++)  // rows
         {for( j=1;j<npts-1;j++)  //columns
             {b[i][j]=0.0;
              if(j == (i+1))
                 b[i][j]=1.0;
              else if ((i >0) && (j==i-1))
                 b[i][j]=1.0;
              else if (i==j)
                 b[i][j]=kc2[i]*dx2-2.0;
              }
          }
//     do the last row
       for(j=0;j<npts-1;j++)
            b[npts-2][j]=0.0;
       b[npts-2][npts-3]=1.0;
       b[npts-2][npts-2]=kc2[npts-2]*dx2-2.0; 
//     Again, note that b is defined for j=1,npts-2 (skipping 0 and npts-1)
       double [] d;
       d= new double[npts-1];
       for(i=0;i<npts-1;i++)
         d[i]=0.0;

       eig.Eigenmatrix(b,d, npts-1);
       

       System.out.println("Eigenmatrix loaded");
        for(i=0;i<npts-1;i++)
           {for(j=0;j<npts-1;j++)
              { if(b[i][j] != 0.0)
                    System.out.println(i+", "+j+" = "+b[i][j]);
              }
            }
 
       jac = new Jacobi();
       jac.Jacobi(eig);
        jac.eigsrt(eig); 
               
     } 
  
 }      
            
            
         
      
