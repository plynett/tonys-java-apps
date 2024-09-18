/*  Groin Calculation

*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import ErrorFunction;

public class Groin extends Applet 
 {
   InputGPanel panel1;
   PlotGPanel graph1;
   
   public void init()
  
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotGPanel();
       add("Center",graph1);

       panel1 = new InputGPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of Groin

    class  InputGPanel extends Panel  
     {  
      PlotGPanel panel;
      TextField Hin, Tin, din;
      Refract ref;
      public static float el,Y, G;
      public static int jstep = 0;
      Button Calculate, Stop;
      public boolean stop = false;
      Label label1;
/*      Choice wmtype;
      boolean wavemaker = true; //means piston
*/
  
      public InputGPanel(PlotGPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(6,2));
/*        add(new Label("Choose Wavemaker"));
        wmtype = new Choice();
        wmtype.addItem("Piston");
        wmtype.addItem("Flap");
        add(wmtype);
*/
      
        
        
        add(label1 = new Label("Groin Data:"));
        add(new Label(" "));
        add(new Label("Groin Length (m)?"));
        add(Hin = new TextField("200.0" ));
        add(new Label("Breaking Wave Angle (o)"));
        add(Tin = new TextField("20." ));
        add(new Label("Shoreline Diffusivity (m^2/s)?"));
        add(din = new TextField("0.001" ));
//   Hin is groin length, Tin is wave angle in degrees; d in is shoreline diffusivity
        add(Calculate = new Button("Calculate"));
        add(Stop = new Button("Stop"));
       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
    
      try 
         {
          if(e.id == Event.ACTION_EVENT)
            {
             if( e.target instanceof Button ) 
              { if(e.target == Calculate)
               { el = Float.valueOf(Hin.getText()).floatValue();
                 Y = Float.valueOf(Tin.getText()).floatValue();
                 G = Float.valueOf(din.getText()).floatValue();
                 Hin.setEditable(false);
                 Tin.setEditable(false);
                 din.setEditable(false);
                 stop = false;
                 panel.set(stop);
                 Y=(float)(Y*Math.PI/180.0);
                 panel.do_wave(el,Y ,G);

                }
              else if (e.target == Stop)
               { stop = true;
                 Hin.setEditable(true);
                 Tin.setEditable(true);
                 din.setEditable(true);
                 panel.set(stop);
               }
             }       //end of Buttons
           }  //end ACTION_EVENT
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }

} // end InputGPanel
    class  PlotGPanel extends Panel implements Runnable
     {
       GroinPlot makerplot;
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T;
       boolean first = false,   stop=false;
       Thread thread;
       int jstep;
       
       public PlotGPanel()
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
            makerplot = new GroinPlot(bounds().width,bounds().height);

            wImage = createImage(bounds().width, bounds().height);
            wGraphics = wImage.getGraphics();
            first = true;
           }

         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         makerplot.Draw(wGraphics);
         g.drawImage(wImage,0,0,this);
         }
    
        public void update(Graphics g)
         {
          paint(g);
//          System.out.println("doing update in PlotPanel");
         }
        
        public void do_wave(float el, float Y ,float G )
         {
          makerplot.initialize((float) el,(float)Y,(float) G);

          makerplot.do_wave();
//          System.out.println("doing  in PlotPanel");
          thread = new Thread(this);
          thread.start();

         }
      public void run()
     {
      
       while(!stop)
        {
         makerplot.do_wave( );
         repaint();
         try {thread.sleep(150);} 
         catch(Exception e){ }
        }
     }
   
   }//end of class PlotGPanel

     class GroinPlot
      {
       public int length, amplitude, totalh;
       int x[];
       public float el,delta,G,L,T;
       int depth[];
       int y[],xo[];
       float dz ;
       ErrorFunction errorf;
       double dt,k;
       int dep;
       float famplitude;
       int ncount=12;
       String time= " ";
       String xlength=" ";
       double bypassingtime;
      
       public int temp;
       public int jstep;
       double totallength;
       double fact;
    
       float umag[],vmag[];
       int toffset=5,boffset =5;
       int Depth;
       double Q;
       String Qdraw,x100;
    
       
        public GroinPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y = new int[length+2];
          xo= new int[length+2];
//          double e1,e2,e3;
           depth = new int[ncount];
          errorf= new ErrorFunction();
   
          for (int i = 0; i<length;i++)
             { 
              x[i]=0;             
             }
         } // end of groinplot

        public void initialize( float he, float eel, float de )
         { 
            double xm;

            el=he;
            double xi;
            delta=eel;
            G=de;
            jstep=0;
            dt=86400.;  //one day in seconds
            famplitude = (float) (totalh-toffset-boffset);
            amplitude = (int) famplitude;
            bypassingtime=(Math.PI*el*el)/(4.*G*Math.pow(Math.tan(delta),2));
            totallength=6.0*Math.sqrt(Math.PI*el*el);
            xlength="width = "+((int)(totallength*100.))/100.+" m";
            xm=2.84576*el/Math.tan(delta);
            xm=((int)xm*100.0)/100.;
            x100="x_m = " + xm+" m";
        
            for (int i = 0; i<length;i++)
              {               
//               xi=(double) (i*totallength)/length);
               y[i]=i;
               x[i]=(int)(toffset+el);
               
              }
         
         }  // end of initialize
  
         public void do_wave()
         {  
            double xi;
            int M;
            double scale;
            double half;
                           
            time="time = " + jstep + " days";
          
            fact=Math.sqrt(4.*G*jstep*dt);
            scale=amplitude/(2*el);
            Q=((int)(Math.tan(delta)*G*jstep*dt*100.))/100.;
            Qdraw="Q = "+Q+" m^2";
            for (int i = 0; i<length;i++)
             { 
               if(i > length/2)
                {
                 xi=(double) ((i-length/2)*totallength)/length;
                 x[i]=(int) (toffset+amplitude/2+scale*( (fact/Math.sqrt(Math.PI))*Math.exp(-xi*xi/(fact*fact)) -
                        xi*(1.-errorf.erf(xi/fact)))*Math.tan(delta));
                }
                else
                {xi=(double)((length/2-i)*totallength)/length;
                 x[i]=(int) (toffset+amplitude/2-scale*( (fact/Math.sqrt(Math.PI))*Math.exp(-xi*xi/(fact*fact)) -
                        xi*(1.-errorf.erf(xi/fact)))*Math.tan(delta));
                }

             }
             if(jstep*dt > bypassingtime)
                jstep=jstep;
             else
              jstep=jstep+1;

          }  // end of do_wave

         public void Draw(Graphics g)
         {
          int xl,yl,del ;
          g.setColor(Color.black);
          g.drawLine(0,amplitude/2+toffset,length,amplitude/2+toffset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
           //box panel
          xl=length-1;
          yl=totalh-1;
          g.setColor(Color.black);
          g.drawLine(0,yl,xl,yl);
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
          // groin
       
          // draw beach
          g.setColor(Color.magenta);
          x[length]=amplitude+toffset+boffset;
          y[length]=length;
          y[length+1]=0;
          x[length+1]=amplitude+toffset+boffset;
          g.fillPolygon(y,x,length+2);
          
          xl=length-130;
          del=15;
          g.setColor(Color.blue);
          yl=20;
          g.drawString("Ocean",xl,yl);
          yl=yl+del;
          g.setColor(Color.black);
          g.drawString(xlength,xl,yl);
          yl=yl+del;
          g.drawString(time,xl,yl);
          yl=yl+del;
          g.drawString(Qdraw,xl,yl);
          yl=yl+del;
          g.drawString(x100,xl,yl);
          // draw groin
           
          g.drawLine(length/2,toffset,length/2,toffset+amplitude);
          } //end of Draw
         

        public void update(Graphics g)
         {
          Draw(g);  
         }

      }//end of class GroinPlot

         
          
        
