/* This application determines the equilibrium beach profile 


    Other required classes:   

*/
import java.applet.*;
import java.awt.*;
import java.lang.*;



 public class Inlet extends Applet
  {
   InputIPanel panel1;
   PlotIPlot graph1;

   public void init()
  
     { 
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotIPlot();
       add("Center",graph1);

       panel1 = new InputIPanel(graph1);
       add("West",panel1);
     }
                             
}      //end of  Inlet

    class  InputIPanel extends Panel  
     {  
      PlotIPlot panel;
      TextField Ain, Oin, din, lin, Win;
    
      double ao, B, W, el, d, A;
      Button Calculate, Reset;
      Bathymetry bath1, bath2;
      Label label1, label2, label3, label4, label5, label6;
      double K, sig, Ac, g, Ken, Kex, f;
  
      public InputIPanel(PlotIPlot panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(7,2));
        add(label1 = new Label("Input Inlet Data:"));
        add(new Label(" "));
        add(label2 = new Label("Inlet Depth(m)?"));
        add(din = new TextField("6.0" ));
        add(label5 = new Label("Inlet Width ?"));
        add(Win = new TextField("100." ));
        add(label4 = new Label("Inlet Length?"));
        add(lin=new TextField("1000."));
        add(label6 = new Label("Bay Planform Area? (km^2)"));
        add(Ain = new TextField("50."));
        add(label3 =new Label("Ocean Tide Amplitude?"));
        add(Oin = new TextField("1.0" ));
        add(Calculate = new Button("Calculate"));
        add(Reset = new Button("Reset"));
       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
       double denom, Rh;
       try 
         {
          if(e.id == Event.WINDOW_DESTROY)
             System.exit(0);
          else if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
                { 
                  ao = Double.valueOf(Oin.getText()).doubleValue();
                  A=Double.valueOf(Ain.getText()).doubleValue();
                  A=A*1000.*1000.;
                  d = Double.valueOf(din.getText()).doubleValue();//depth
                  el = Double.valueOf(lin.getText()).doubleValue();//length
                  W = Double.valueOf(Win.getText()).doubleValue();// width
                  g=9.81;
                  sig=2.*Math.PI/45144.;
                  Ken=.2;
                  Kex=1.0;
                  Rh=d*W/(W+2.*d);
                  f=.01;
                  denom=Math.sqrt(Ken+Kex+f*el/(4.*Rh));
                  K=W*d*Math.sqrt(2.*g*ao)/(sig*ao*A*denom);
                  tide();
                   
                  panel.do_wave(K, bath1, bath2);
                  panel.repaint();
                }
              else if (e.target == Reset)
                 { 
                  Ain.setText("");
                  lin.setText("");
                  din.setText("");
                  Win.setText("");
                  Oin.setText("");
                 }
  
            }
          
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }
     public void tide()
//    do a R-K calculation for the tide and then stick it in bath2.
     {double [] etao;
      double [] etab;
      double [] etaotrunc;
      double [] etabtrunc;
      double dt, t, yp1, yp2, yp3, yp4, arg,etaohalf;
      double etabmax;
      int n,imax;
     

      etao = new double[240];
      etab = new double[240];
      dt=Math.PI/30.;
      etab[0]=0.;
      for(int i = 0; i<239; i++)
        {t=(i+1)*dt;
         etao[i]=Math.sin(t);
         arg=etao[i]-etab[i];
         yp1=dt*K*Math.sqrt(Math.abs(arg))*arg/Math.abs(arg);
         etaohalf=Math.sin(t+dt/2.);
         arg=etaohalf-(etab[i]+yp1/2.);
         yp2=dt*K*Math.sqrt(Math.abs(arg))*arg/Math.abs(arg);
         arg=etaohalf-(etab[i]+yp2/2.);
         yp3=dt*K*Math.sqrt(Math.abs(arg))*arg/Math.abs(arg);         
         arg=Math.sin(t+dt)-(etab[i]+yp3);
         yp4=dt*K*Math.sqrt(Math.abs(arg))*arg/Math.abs(arg); 
         etab[i+1]=etab[i]+(yp1+2*yp2+2.*yp3+yp4)/6.;
        }
      n=60;
      etaotrunc = new double[60];
      etabtrunc = new double[60];
      etabmax=0.0;
      imax=0;
      for (int i=179;i<239;i++)
         {etaotrunc[i-179]=etao[i];
          etabtrunc[i-179]=etab[i];
          if(etab[i]>etabmax)
               {etabmax=etab[i];
                imax=i;
               }
         }
      imax=imax-179;
      double phase;
      phase=imax*dt-Math.PI/2.0;
      bath1 = new Bathymetry();
      bath2 = new Bathymetry();
      bath1.Bathymetry(n,dt,etaotrunc,phase);
      bath2.Bathymetry(n,dt,etabtrunc,etabmax);
     }
      

} // end InputIPanel
    class  PlotIPlot extends Panel 
     {
       WaveIPlot waveplot;
       Bathymetry bath1, bath2;
       Image wImage;
       double K;
       Graphics wGraphics;
       boolean first = false;
     
 
       
       public PlotIPlot()
        {
          setBackground(Color.white);
         }

 
       public Dimension minimumSize()
        {
         return new Dimension(300,100);
        }
     
       public void paint(Graphics g)
        {
         if(!first)
           {
             waveplot = new WaveIPlot(bounds().width,bounds().height);
//           waveplot.Initialize(0,1);
            wImage = createImage(bounds().width, bounds().height);
            wGraphics = wImage.getGraphics();
            first= true;
           }
         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         waveplot.Draw(wGraphics);
         g.drawImage(wImage,0,0,this);
         }
    
          public void update(Graphics g)
           {
            paint(g);
           }
//        
        public void do_wave(double K, Bathymetry bath1, Bathymetry bath2 )
         {
          waveplot.initialize(K, bath1, bath2);
          waveplot.do_wave();
 
         }
    
   }//end of class PlotIPlot

     class WaveIPlot
      {
      
       int x[];
       int npts;
       int length, totalh;
       double dt;
       int dep;
       float famplitude;
       int amplitude;
       int offset=5;
       int [] tideoi;
       int [] tidebi;
       double [] tideo;
       double [] tideb;
       double phase, etabmax;
       Bathymetry bath1, bath2;
       String Kvalue = " ";
       String Phasevalue = " ";
       String Response = " ";
       int iwave;
  
       double [] xx;
       
       
       public WaveIPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length];
          xx = new double[length];
  
          tideoi = new int[length];
          tidebi = new int[length];
          tideo = new double[length];
          tideb = new double[length];
         
          
        
        }
        public void initialize(double K, Bathymetry bath1, Bathymetry bath2)
         {  int i,n;
            double dmax,xmax;
            npts=bath1.NPTS;
            K= ( (int)(K*100.))/100.;
            Kvalue = "K = "+K;
            dt=bath1.DX;
            phase = bath1.slope;
            phase=( (int)(phase*18000./Math.PI))/100.; //conv degree and trunc
            Phasevalue="Phase = "+phase+" deg";
            etabmax=bath2.slope;
            etabmax=( (int)(etabmax*100.))/100.;
            Response="Response = "+etabmax;
            xmax = 2.*Math.PI;
      
            
              for(i=0;i<npts;i++)
               {tideo[i]=-bath1.h[i];
                tideb[i]=-bath2.h[i];
                xx[i]=i*dt;
                }
            // for plotting
            dmax=1.0;
            iwave=totalh/2;
            for(i=0;i<npts;i++)
               {x[i]=(int)(xx[i]*length/xmax);
               tideoi[i]=offset+iwave+
                    (int)((tideo[i]*(totalh-2.*offset-iwave))/(dmax)) ;
               tidebi[i]=offset+iwave+
                    (int)((tideb[i]*(totalh-2.*offset-iwave))/(dmax)) ;

               }
 
         }
         public void do_wave()
         {
         }

         public void Draw(Graphics g)
         {
          int i, j,xl,yl;
          g.setColor(Color.black);
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          x[0]=1;
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
          g.drawLine(0,iwave+offset,xl,iwave+offset);
          g.setColor(Color.blue);
          g.drawLine(length-110,15,length-85,15);
          g.drawString("Ocean",length-80,15);
          for ( i = 1; i<npts; i++)
             g.drawLine(x[i-1],tideoi[i-1],x[i],tideoi[i]);
          g.setColor(Color.darkGray);
          g.drawLine(length-110,30,length-85,30);
          g.drawString("Bay", length-80,30);
          for ( i = 1; i<npts; i++)
             g.drawLine(x[i-1],tidebi[i-1],x[i],tidebi[i]);
          g.setColor(Color.black);
          g.drawString(Kvalue,length-110,45);
          g.drawString(Phasevalue, length-110,60);
          g.drawString(Response, length-110,75);
          g.setColor(Color.black);
          g.drawLine(0,yl,xl,yl);
         
         } 
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class WaveIPlot

         
          
        
