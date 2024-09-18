/* Wave object to hold wave data: H, T, d

Robert A. Dalrymple, December 3, 1996

*/

    public class Wave
    
   {
      public  static double H;
      public  static double T;
      public  static double d;
      public  static double L;
      public  static double etac;

       Wave(double He,double Te, double de)
      {
        H=He;
        T=Te;
        d=de;
      }
    }
