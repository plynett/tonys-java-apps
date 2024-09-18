/*  Gauss elimination in Java
     Robert A. Dalrymple, Nov 20, 1996
        Takes matrix a, rhs b, and number of eqns n
        returns ans in b.
*/
 public class Gauss 
   {

    public boolean Gauss(StArray array)
   
     {
       double a[][];
       double b[];
       int i,j,k,n;
       double sum;

       n=array.n;
       b=new double[n];
       a= new double[n][n+1];
       System.arraycopy(array.b,0,b,0,array.b.length);
       for(i=0;i<n;i++)
         System.arraycopy(array.a[i],0,a[i],0,n);

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
       for(i=0;i<n;i++)
           array.b[i]=b[i];

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
}
