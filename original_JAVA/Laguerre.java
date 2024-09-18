/*  Laguerre Polynomials

*/
    class Laguerre
      {

       public double Laguerre(int n, double z)
       {
        double ln, sum, factn,factk; 
        int i,k;
        if(n==0)
           {ln =1.0;
           }
        else 
           {ln =1.0;
            factn=factorial(n); 
            for(k=1;k<n+1;k++)
               {factk=factorial(k);
               ln += Math.pow((-1.),k)*factn*Math.pow(z,k)/(factk*factk*factorial(n-k));
               }
           }
        return ln;
      }
      public double factorial(int a)
      {
       double fact;
       if(a==0)
          fact=1.0;
       else
          {fact=a;
           for(int n =a-1;n !=0;n--)
               fact*=n;
          }
       return fact;
       }
}
