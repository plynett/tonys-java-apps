      public class Bathymetry
      {
        int NPTS;
        double DX;
        double [] h;
        double slope;

       void Bathymetry(int npts, double dx, double[] he, double sl)
       {int i;

        NPTS = npts;
        h = new double [NPTS];
        DX = dx;
        slope = sl;
        for(i=0;i<NPTS;i++)
               h[i]=he[i];

     
       }
  }
     
