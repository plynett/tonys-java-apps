/* This applet shows several edgewaves on a planar bathymetry  
    Other required classes:   Bathymetry
*/
import java.awt.*;
import java.applet.*;
import java.lang.*;



 public class EdgeTheory extends Applet
  {
   InputETPanel panel1;
   PlotETPlot graph1;
   public static int jstart = 0;
   public void init()
  
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotETPlot();
       add("Center",graph1);

       panel1 = new InputETPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of LinearPlot

    class  InputETPanel extends Panel  
     {  
      PlotETPlot panel;
      TextField xmaxin, Tin, din;
      public static double T, slope, L;
      public static int jstep = 0;
      Button Calculate, Stop;
      public boolean stop = false;
      Label label1, label2, label3, label4;
      Wave wave;
  
      public InputETPanel(PlotETPlot panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(6,2));
        add(label1 = new Label("Input Edge Wave Data:"));
        add(new Label(" "));
        add(label2 = new Label("Wave Period (sec)?"));
        
        add(Tin = new TextField("     " ));
        Tin.setText("3.0");
        add(label3 =new Label("Mean Slope?"));
        add(din = new TextField("      " ));
        din.setText(".1");
        add(label4 = new Label("Max. Off. Distance (m)?"));
        add(xmaxin=new TextField("      "));
        xmaxin.setText("5.0");
        add(Calculate = new Button("Calculate"));
        add(Stop = new Button("Stop"));
       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
     
      double [] h;
      double dx,xmax;
      int n;
      Bathymetry bath;
     

    
      n = 200;              //******************************
//      dx=.2;
      try 
         {
          if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
                { 
                  T = Double.valueOf(Tin.getText()).doubleValue();
                  wave.T=T;
                  xmax=Double.valueOf(xmaxin.getText()).doubleValue(); //xmax
                  dx=xmax/(n-1);
                  slope = Double.valueOf(din.getText()).doubleValue(); //bottom slope
                  h=new double[n];
                  for(int i = 0; i<n;i++)
                       {h[i]=slope*dx*(double)i;
//                        System.out.println(i*dx+", "+h[i]);
                        }
                  
                  bath = new Bathymetry();
                  bath.Bathymetry(n,dx,h,slope);
                   
//                  System.out.println("bathy copied");
                   stop = false;
                  panel.set(stop);
                  panel.do_wave( slope, T,  bath, xmax);
                  panel.repaint();
                }
              else if (e.target == Stop)
                 { stop = true;
                 panel.set(stop);
                 }
  
            }
          
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }

} // end InputETPanel
    class  PlotETPlot extends Panel implements Runnable
     {
       WaveETPlot waveplot;
       Bathymetry bath;
       Image wImage;
       Graphics wGraphics;
       public double d, T;
       boolean first = false,   stop=false;
       Thread thread;
       
       public PlotETPlot()
        {
          setBackground(Color.white);
         }

       public void set(boolean stopp) 
        {
         stop=stopp;
        }

       public Dimension minimumSize()
        {
         return new Dimension(300,100);
        }
     
       public void paint(Graphics g)
        {
         if(!first)
           {
            waveplot = new WaveETPlot(bounds().width,bounds().height);
//            waveplot.Initialize(0,1);
            wImage = createImage(bounds().width, bounds().height);
            wGraphics = wImage.getGraphics();
            first = true;
           }

         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         waveplot.Draw(wGraphics);
         g.drawImage(wImage,0,0,this);
         }
    
        public void update(Graphics g)
         {
          paint(g);
         }
        
        public void do_wave( double d, double T, Bathymetry bath, double xmax)
         {
          waveplot.initialize(d,T,bath,xmax);
          waveplot.do_wave();
          thread = new Thread(this);
          thread.start();

         }
      public void run()
     {
      
       while(!stop)
        {
         waveplot.do_wave();
         repaint();
         try {thread.sleep(150);} 
         catch(Exception e){ }
        }
     }
   
   }//end of class PlotETPlot

     class WaveETPlot
      {
       public int length, amplitude, totalh, del;
       int x[];
       public double d, T;
       float dz ;
       double dt;
       double dx,xmax;
       int dep;
       float famplitude;
       int ncount=12;
        public int jstep=0;
       int offset=5;
       double [] depth;
       int temp;
       Bathymetry bath;
     
       int npts;
       int iwave;
       double [][] ewave;
       double [] xx;
       double [] lambda={0.,0.,0.,0.,0};
       double [][] vect;
       
       
       
       
        public WaveETPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length];
          xx = new double[length];
  
          depth = new double[length];
         
          
        
        }
        public void initialize( double de, double te, Bathymetry bath ,double xm)
         {  int i,n;
            double  dmax,g;
            Laguerre la;
            d=de;
            T=te;
            dt=te/30.;
            npts=bath.NPTS;
            vect=new double[5][npts];
            dx=bath.DX;
            jstep=0;
            xmax=xm;
            la = new Laguerre();
            for(i=0;i<npts;i++)
               {depth[i]=bath.h[i];
                xx[i]=i*dx;
               }
            // for plotting
            dmax=depth[npts-1];
            iwave=totalh/3;
            del=offset+iwave;
            for(i=0;i<npts;i++)
               {x[i]=(int)(xx[i]*length/xmax);
                depth[i]=offset+iwave+(depth[i]*(totalh-offset-iwave))/(dmax) ;
               }
           // exact solution
            
            g=Refract.G;
            ewave = new double[5][npts];
            for(n=0;n<5;n++)
               {lambda[n]=Math.pow((2.*Math.PI/T),2)/(g*(2*n+1)*bath.slope);
                for(i=0;i<npts;i++)
                    ewave[n][i]= -Math.exp(-lambda[n]*xx[i])*la.Laguerre(n,2.*lambda[n]*xx[i])*iwave;
               }
        
 
         }


       public void do_wave()
         {  
       
            int N,  i,j;
            double sigma,scale;
            jstep=jstep+1;
            sigma=2.*Math.PI/T;
             for(i=0;i<4;i++)
              {for(j=0;j<npts;j++)
                 vect[i][j]=ewave[i][j]*Math.cos(sigma*jstep*dt);
              }
         }
           

         public void Draw(Graphics g)
         {
          int i, j,xl,yl,Umag,Vmag;
          g.setColor(Color.black);
//          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          g.drawLine(0,yl,xl,yl);
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
          g.drawLine(0,iwave+offset,xl,iwave+offset);
          g.setColor(Color.lightGray);
          for(i=1;i<npts;i++)
            g.drawLine(x[i-1],(int)depth[i-1],x[i],(int)depth[i]);
         
          xl=length-150;
          yl=10;
        
          g.setColor(Color.red);
          String lamb="Mode 0 = "+((int)(lambda[0]*100000.))/100000.;
          g.drawString(lamb,xl,yl);
          for (i=1;i<npts;i++)
            g.drawLine(x[i-1],del+(int) vect[0][i-1],x[i],del+ (int) vect[0][i]);
           

          g.setColor(Color.blue);
          lamb="Mode 1 = "+((int)(lambda[1]*100000.))/100000.;
          g.drawString(lamb,xl,yl+15);
          for(j=1;j<npts-1;j++)
                 g.drawLine(x[j-1],del+(int) vect[1][j-1],x[j],del+(int)vect[1][j]);
             
          g.setColor(Color.green);
          lamb="Mode 2 = "+((int)(lambda[2]*100000.))/100000.;
          g.drawString(lamb,xl,yl+30);
          for(j=1;j<npts-1;j++)
                 g.drawLine(x[j-1],del+(int) vect[2][j-1],x[j],del+(int)vect[2][j]);

/*          g.setColor(Color.yellow);
          lamb="lambda_3 = "+((int)(lambda[3]*100000.))/100000.;
          for(j=1;j<npts-1;j++)
                 g.drawLine(x[j-1],del+(int) vect[3][j-1],x[j],del+(int)vect[3][j]);
*/
          g.setColor(Color.black);
          String offshore="x = "+xmax;
          g.drawString(offshore,length-65,del+11);
          } 
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class WaveETPlot

         
          
        
