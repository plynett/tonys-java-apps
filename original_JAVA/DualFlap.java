/*  Dual Flap Wavemaker Calculation
             Robert A. Dalrymple, May 1997
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.util.Vector;
import Refract;
import Hyper;

public class DualFlap extends Applet 
 {
   InputDPanel panel1;
   PlotDPanel graph1;
   public static int jstart = 0;
   public static float H=0, d=1, L, T;
   public void init()
  
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotDPanel();
       add("Center",graph1);

       panel1 = new InputDPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of Wavemaker

    class  InputDPanel extends Panel  
     {  
      PlotDPanel panel;
      TextField Hin, Tin, din,h1iin,h1sin,min;
      Refract ref;
      public static float H,T, d, L,Stroke,h1i,h2i,h1s, h2s;
      public  int mode=1;
      public static int jstep = 0;
      Button Calculate, Stop;
      public boolean stop = false;
      Choice wmtype;
      Label label0, label1;
    
      public InputDPanel(PlotDPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(9,2));
        add(label0 = new Label("Wavemaker Geometry:"));
        add(new Label("  "));
        add(new Label("Elevation of hinge 1 (m)?"));
        add(h1iin = new TextField(".2"));
        add(new Label("Elevation of hinge 2 (m)?"));
        add(h1sin = new TextField("1.2"));
        add(new Label("Total Water Depth?"));
        add(din = new TextField("1.5" ));
        add(new Label("Wavemaker Mode"));
        wmtype = new Choice();
        wmtype.addItem("1  Upper paddle only");
        wmtype.addItem("2  Monoflap mode");
        wmtype.addItem("3  Upper flap piston");
        add(wmtype);
        
        add(label1 = new Label("Desired Wave Data:"));
        add(new Label(" "));
        add(new Label("Wave Height (m)?"));
        add(Hin = new TextField(".35" ));
        add(new Label("Wave Period?"));
        add(Tin = new TextField("1.5" ));
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
            {  if(  e.target.equals(wmtype))
                  {mode=1+wmtype.getSelectedIndex();
//                   System.out.println("mode is "+mode);
                  }
              
             if( e.target instanceof Button ) 
            { if(e.target == Calculate)
               { H = Float.valueOf(Hin.getText()).floatValue();
                 T = Float.valueOf(Tin.getText()).floatValue();
                 d = Float.valueOf(din.getText()).floatValue();
                 h1i= Float.valueOf(h1iin.getText()).floatValue();
                 h1s= Float.valueOf(h1sin.getText()).floatValue();
                 h2s=d-h1s;
                 h2i=d-h1i;
                 if(h2s>h2i)
//                 input error
                   {stop= true;
                    panel.set(stop);
                    System.out.println("paddle elevations wrong!");
                    panel.do_wave(H,L,d,T,mode,h2s,h2i);
                   }
//                 mode=(int) Float.valueOf(min.getText()).floatValue();
                 if(H>(float)(.8*d))
                  {H=(float)(.8*d);
                   Hin.setText(H+", breaking");
                  }
                 klin =   ref.waveNumber(d, T);
                 L= (float) 6.2831852/klin;
                 stop = false;
                 panel.set(stop);
                 panel.do_wave(H, L, d, T,mode, h2s, h2i);

                }
             else if (e.target == Stop)
               { stop = true;
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

} // end InputDPanel
    class  PlotDPanel extends Panel implements Runnable
     {
       MakerDPlot makerplot;
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T, h2s, h2i;
       public int mode;
       boolean first = false,   stop=false;
       Thread thread;
       
       public PlotDPanel()
        {
          setBackground(Color.lightGray);
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
            makerplot = new MakerDPlot(bounds().width,bounds().height);

            wImage = createImage(bounds().width, bounds().height);
            // wImage is the offscreen image buffer
            wGraphics = wImage.getGraphics();
	    // wGraphics is the graphics object used for drawing into 
            // the offscreen image buffer
            first = true;
           }

	 // the following two commands draw the offscreen image
         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         makerplot.Draw(wGraphics);
	 // Now the offscreen image is drawn onscreen
         g.drawImage(wImage,0,0,this);
         }
    
        public void update(Graphics g)
         {
          paint(g);
         }
        
        public void do_wave(float H, float L,float d, float T, int mode, float h2s, float h2i )
         {
          makerplot.initialize(H,L,d,T,mode, h2s, h2i);
          makerplot.do_wave();
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
   
   }//end of class PlotDPanel

     class MakerDPlot
      {
       public int length, amplitude, totalh;
       int x[],y[]; //surface positions
       public float H, d, T,L;
       public int mode;
       public double Stroke,S, Power;
       public float h2s,h2i,h1s,h1i;
       float dz ;
       double dt,k;
       int dep;
       float famplitude;
       int ncount=12;
       String waveLength = " ";
       String bottom = " ";
       String stroke = " ";
       String power =" ";
       public int temp;
       public int jstep=0;
       int position; //mean paddle location - (half the paddle width)
       int offset=5;
       int Depth;
       public int pw=5; //half paddlewidth
       
       
        public MakerDPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y=  new int[length+2];
     
          

          for (int i = 0; i<length;i++)
             { 
              x[i]=0; 
              y[i]=i;            
             }
      
        }
        public void initialize(float he, float el, float de, float te,int modee, float h2se, float h2ie )
         { 
            L=el;
            k=2.*Math.PI/L;
//          truncate wavelength for printing
            if(L>1)
               el=(float)(((int)(el*100.))/100.);
            else
               el=(float)(((int)(el*1000.))/1000.);
            waveLength = "L= "+el+" m";
            H=he;
            d=de;
            T=te;
            dt=te/30.;
            mode=modee;
            h2s=h2se;
            h2i=h2ie;
            h1s=d-h2s;
            h1i=d-h2i;
//            bottom = "h="+d;
            famplitude = (H/2)/(d+H/2)*(float) (totalh-offset);
            amplitude = (int) famplitude;
//            System.out.println("height:"+totalh+", amplitude:"+amplitude);
            dep=(totalh-amplitude-offset);

         }
  
         public void do_wave()
         {  
            double shd, chd, cosh,k,arg,cos,sinh,sin;
            double sigma, factor,sh2d,ch2d,chdif,chdif2;
            jstep=jstep+1;
            sigma=2.*Math.PI/T;
            position=length/30;
            for (int i = 0; i<length;i++)
             { 
              arg=2.*Math.PI*(i+position+pw)/length -sigma*jstep*dt ;
              x[i]=offset+amplitude-(int) (amplitude*Math.cos(arg));
               }
//          calculate wavemaker stroke
          
            k=2.*Math.PI/L;
            chd=Hyper.cosh(k*d);
            shd=Hyper.sinh(k*d);
            ch2d=Hyper.cosh(2.*k*d);
            sh2d=Hyper.sinh(2.*k*d);
//            System.out.println("k,d,chd,shd,sh2d:"+k+", "+d+", "+chd+", "+shd+", "+sh2d);
            switch (mode) 
              {case 1:
                 chdif=Hyper.cosh(k*(d-h2s));
                 Stroke=H*k*h2s*(sh2d+2.*k*d)/(4.*shd*((k*h2s)*shd-chd+chdif));
                 break;
               case 2:
                 chdif=Hyper.cosh(k*(d-h2i));
                 Stroke=H*k*h2i*(sh2d+2.*k*d)/(4.*shd*((k*h2i)*shd-chd+chdif));
                 break;
               case 3:
                 chdif=Hyper.cosh(k*(d-h2s));
                 chdif2=Hyper.cosh(k*(d-h2i));
                 Stroke=H*k*h2i*(sh2d+2.*k*d)/(4.*shd*k*(h2i-h2s)*shd+chdif2-chdif);
                 Stroke=Stroke*(1.-h2s/h2i);
                 break;
               default:
                 break;
               }
//            System.out.println("H, Stroke, h2i,h2s: "+H+", "+Stroke+", "+h2i+", "+h2s);
            if(Stroke >1.)
                Stroke=((int) (Stroke*100.))/100. ;
            else
                Stroke=((int) (Stroke*1000.))/1000.;
            stroke="Stroke= "+Stroke+" m";
            cos=Math.cos(sigma*jstep*dt );
            sin=Math.sin(sigma*jstep*dt );
            S=position*sin;  //stroke is arbitrarily same as position

            Power=(10000./8)*H*H*(0.5*(1+2.*k*d/sh2d))*L/T;
//          truncate
            Power=( (int)(Power*100.))/100.;
            power="Power= "+Power+" W";
            
 
          }
           

         public void Draw(Graphics g)
         {
          int xl,yl,hinge1,hinge2,twopw, pos_hinge2;
          int xpts[]={0,position+2*pw,0,0,0,0,0,0};
          int ypts[]={totalh,totalh,0,0,0,0,0,0};
          int bxpts[]={0,0,position,0,0,0};
          int bypts[]={0,totalh,totalh,0,0,0};
          twopw=2*pw;
          pos_hinge2=position;
//        draw axes          
          g.setColor(Color.black);
          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          g.drawLine(0,yl,xl,yl); // draw bottom line

//        draw wave profile
          g.setColor(Color.blue);
          x[length]=totalh;
          x[length+1]=totalh;
          y[length]=length-1;
          y[length+1]=0;
          g.fillPolygon(y,x,length+2);
//        hinge vertical locations in window units (integer)
          hinge2=(int)(totalh-h1s*dep/d);  //upper
          hinge1=(int)(totalh-h1i*dep/d);
         
//   draw wavemaker
          g.setColor(Color.black);
          switch (mode)
            { case 5:
  
                g.fillRect(position+(int)S,0,twopw,totalh);
                g.setColor(Color.lightGray); //erase behind paddle
                g.fillRect(0,0,position+(int)(S),totalh);
                break;
              case 1:
                xpts[2]=position+twopw;
                xpts[3]=position+twopw+(int)S;
                ypts[2]=hinge2;
                ypts[4]=0;
                ypts[5]=totalh;
                g.setColor(Color.black);
                g.fillPolygon(xpts,ypts,5);

                g.setColor(Color.lightGray);
                bxpts[3]=position;
                bxpts[4]=position+(int) S;
                bypts[3]=hinge2;
                bypts[4]=0;
                g.fillPolygon(bxpts,bypts,5);
                break;                
              case 2:
                g.setColor(Color.black);
                xpts[0]=position;
                xpts[2]=position+twopw;
                xpts[3]=position+(int) S+twopw;
                xpts[4]=position+(int) S;
                xpts[5]=position;
                ypts[2]=hinge1;
                ypts[5]=hinge1;
                g.fillPolygon(xpts,ypts,5);
                g.setColor(Color.lightGray);
                bxpts[3]=position;
                bxpts[4]=position+(int) S;
                bypts[3]=hinge1;
                g.fillPolygon(bxpts,bypts,5);
                pos_hinge2=position+(int) ((h2i-h2s)*S/h2i);
                break;
               case 3:
                g.setColor(Color.black);
                xpts[2]=position+twopw;
                xpts[3]=position+twopw+(int) S;
                xpts[4]=xpts[3];
                xpts[5]=0;
                ypts[2]=hinge1;
                ypts[3]=hinge2;
                g.fillPolygon(xpts,ypts,6);
                g.setColor(Color.lightGray);
                bxpts[3]=position;
                bxpts[4]=position+(int)S;
                bxpts[5]=bxpts[4];
                bypts[3]=hinge1;
                bypts[4]=hinge2;
                g.fillPolygon(bxpts,bypts,6);
                pos_hinge2=position+(int) S;
                break;
               default:
                break;
             }
//        draw hinges
          g.setColor(Color.red); //draw hinge
          g.fillOval(pos_hinge2,hinge2,twopw,twopw);
          g.fillOval(position,hinge1,twopw,twopw); // lower
//          System.out.println(totalh+", "+hinge1+", "+hinge2+", "+d  );    

//   print output
          xl=length-130;
          yl=amplitude+50;
          g.setColor(Color.yellow);
          g.drawString(waveLength,xl ,yl);
          yl=yl+30;
          g.drawString(stroke,xl,yl);
          yl=yl+30;
          g.drawString(power,xl,yl);
          }

        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class MakerDPlot

         
          
        
