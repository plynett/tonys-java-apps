/*  Beach Fill Calculation
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.util.Vector;
import ErrorFunction;

public class OneLine extends Applet 
 {
   InputOLPanel panel1;
   PlotOLPanel graph1;
   
   public void init()
  
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotOLPanel();
       add("Center",graph1);

       panel1 = new InputOLPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of Wavemaker

    class  InputOLPanel extends Panel  
     {  
      PlotOLPanel panel;
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
  
      public InputOLPanel(PlotOLPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(6,2));
/*        add(new Label("Choose Wavemaker"));
        wmtype = new Choice();
        wmtype.addItem("Piston");
        wmtype.addItem("Flap");
        add(wmtype);
*/
      
        
        
        add(label1 = new Label("Beach Fill Data:"));
        add(new Label(" "));
        add(new Label("Fill Length (m)?"));
        add(Hin = new TextField("1000.0" ));
        add(new Label("Cross-shore Width (m)?"));
        add(Tin = new TextField("100.0" ));
         add(new Label("Shoreline Diffusivity (m^2/s)?"));
        add(din = new TextField("0.001" ));
 
        add(Calculate = new Button("Calculate"));
        add(Stop = new Button("Stop"));
       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
     
      float klin;
      float Cg, Ks, Kr, angle, C ;
    
      ref= new Refract();
      try 
         {
          if(e.id == Event.ACTION_EVENT)
            {
/*             if(  e.target.equals(wmtype))
               if ("Flap".equals (e.arg) )
                   wavemaker= false;
               else
                   wavemaker=true;
 
  */             
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

} // end InputOLPanel
    class  PlotOLPanel extends Panel implements Runnable
     {
       OneLinePlot makerplot;
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T;
       boolean first = false,   stop=false;
       Thread thread;
       int jstep;
       
       public PlotOLPanel()
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
            makerplot = new OneLinePlot(bounds().width,bounds().height);

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
   
   }//end of class PlotOLPanel

     class OneLinePlot
      {
       public int length, amplitude, totalh;
       int x[];
       public float el,Y,G,L,T;
       int depth[],uc[],ut[],vt[],vc[];
       int y[],xo[];
       float dz ;
       ErrorFunction errorf;
       double dt,k;
       int dep;
       float famplitude;
       int ncount=12;
       String Mstring = " ";
       String Max = " ";
       String time= " ";
       String Half = " ";
        public int temp;
       public int jstep;
       double fact;
    
       float umag[],vmag[];
       int toffset=5,boffset =10;
       int Depth;
       public int pw=5; //half paddlewidth
       
        public OneLinePlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          uc = new int[ncount];
          ut = new int[ncount];
          vc = new int[ncount];
          vt = new int[ncount];
          y = new int[length+2];
          xo= new int[length+2];
//          double e1,e2,e3;
          umag = new float[ncount];
          vmag = new float[ncount];
          depth = new int[ncount];
          errorf= new ErrorFunction();
   
          for (int i = 0; i<length;i++)
             { 
              x[i]=0;             
             }
         }
        public void initialize( float he, float eel, float de )
         { 
          
            el=he;
            
            double xi;
            Y=eel;
            G=de;
            jstep=0;
             dt=86400.;  //one day in seconds
            famplitude = (float) (totalh-toffset-boffset);
            amplitude = (int) famplitude;
        
            for (int i = 0; i<length;i++)
              {               
               xi=(double) (i*(1.5*el)/length);
               y[i]=i;
               fact=1.0e07;
               xo[i]=toffset+amplitude-
                 (int) (0.5*amplitude*(errorf.erf(fact*(2.*xi/el+1.))-errorf.erf(fact*(2.*xi/el-1.))));
               
             }
         
         }
  
         public void do_wave()
         {  
            double xi;
            int M;
            double part;
            double half;
                           
            time="time = " + jstep + " days";
          
            fact=el/(4.*Math.sqrt(G*jstep*dt));
            for (int i = 0; i<length;i++)
             { 
               xi=(double) (i*(1.5*el)/length);

               x[i]=toffset+amplitude-(int) (0.5*amplitude*(errorf.erf(fact*(2.*xi/el+1.))-errorf.erf(fact*(2.*xi/el-1.))));
               
             }
            // percent fill in project area
             fact=2.*fact;
             part= (1./(Math.sqrt(Math.PI)*fact))*(Math.exp(-fact*fact)-1.);
             M= (int) (100.*(part +errorf.erf(fact)));
             Mstring="percent= "+M;
             Max="max = "+(int)((int)((toffset+amplitude-x[0])*Y*100./amplitude)/100.)+" m";
             half=((int)(Math.pow((0.46*el),2.)*100./(G*86400*365))/100.);
             Half="half-life="+half+" yrs";
             jstep=jstep+1;

          }
         public void Draw(Graphics g)
         {
          int xl,yl ;
          g.setColor(Color.black);
          g.drawLine(0,amplitude+toffset,length,amplitude+toffset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          g.setColor(Color.yellow);   //color beach
          g.fillRect(0,amplitude+toffset,length,amplitude+toffset); 
          //box panel
          xl=length-1;
          yl=totalh-1;
          g.setColor(Color.black);
          g.drawLine(0,yl,xl,yl);
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
 
          g.setColor(Color.magenta);
          x[length]=amplitude+toffset;
          y[length]=length;
          y[length+1]=0;
          x[length+1]=amplitude+toffset;
          g.fillPolygon(y,x,length+2);
          
          xl=length-130;
          yl=yl-90;
          g.setColor(Color.black);
          g.drawString(time,xl,yl);
          yl=yl+20;
          g.drawString(Max,xl,yl);
          yl=yl+20;
          g.drawString(Mstring,xl,yl);
          yl=yl+20;
          g.drawString(Half,xl,yl);
          g.setColor(Color.blue);
          yl=30;
          g.drawString("Ocean",xl,yl);
          g.setColor(Color.white);
          xl=20;
          yl=totalh-30;
          g.drawString("Fill",xl,yl);
          
          g.setColor(Color.black);

           for(int i =1;i<length;i++)
            g.drawLine(y[i-1], xo[i-1],y[i],xo[i]); // original fill contour
         }
         

        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class OneLinePlot

         
          
        
