  public class Streamout
  { 
    public int NN, NTHTS;
    public double [] X;
    public double [] eta;
    public double [] theta;
    public double errorH,errorQ,msl;
    public double damp;
   
    Streamout(int nn, int nthts)
    {
      X=new double[nn+2];
      eta=new double[nthts];
      theta=new double[nthts];
      NN=nn;
      NTHTS=nthts;
     }
   }
