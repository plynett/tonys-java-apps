/* Data object for Jacobi.java
           R.A. Dalrymple December 11, 1996
*/
   public class Eigenmatrix
    { public  double [][] a;
      public  int N;
      public double [] d;
  
   
      void Eigenmatrix(double[][]ae, double [] de, int ne)
       { int i;
         N= ne;
         a= new double[N][N];
         for(i=1;i<N;i++)
              {
              System.arraycopy(ae[i],0,a[i],0,ae[i].length);
              // note that the index is shifted one.
              }
         d= new double[N];
         System.arraycopy(de,0,d,0,de.length);
       }
     }
