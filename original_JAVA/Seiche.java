/*  Seiche Calculations
             Robert A. Dalrymple, Nov 1996
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import Refract;
import Hyper;

public class Seiche extends Applet 
 {
   InputSPanel panel1;
   PlotSPanel graph1;
   public static int jstart = 0;
   public static float H=0, d=1, L, T;

   public void init() 
     {
       setLayout(new BorderLayout());
       Label label1 = new Label("                    Basin ");
       add("North",label1);  //spacer
       graph1 = new PlotSPanel();
       add("Center",graph1);

       panel1 = new InputSPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of Seiche----------------------------

    class  InputSPanel extends Panel  
     {  
      PlotSPanel panel;
      TextField  Lin, din,min;
      Refract ref;
      public static float m, d, L;
      public static int jstep = 0;
      Button Calculate, Stop;
      public boolean stop = false;
      Label label1;
//      Choice wmtype;
//      boolean wavemaker = true; //means piston
  
      public InputSPanel(PlotSPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(6,2));
/*        add(new Label("Choose Wavemaker"));
        wmtype = new Choice();
        wmtype.addItem("Piston");
        wmtype.addItem("Flap");
        add(wmtype);  */
        add(label1 = new Label("Basin Data:"));
        add(new Label(" "));
        add(new Label("Length (m)?"));
        add(Lin = new TextField("     " ));
        add(new Label("Depth?"));
        add(din = new TextField("     " ));
        add(new Label("Modal Number?"));
        add(min = new TextField("      " ));
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
 /*            if(  e.target.equals(wmtype))
               if ("Flap".equals (e.arg) )
                   wavemaker= false;
               else
                   wavemaker=true;
 */
               
            if( e.target instanceof Button ) 
            { if(e.target == Calculate)
               { m = Float.valueOf(min.getText()).floatValue();
                 if(m ==0)
                    { m=1;
                     min.setText("     1");
                    }
                 L = Float.valueOf(Lin.getText()).floatValue();
                 d = Float.valueOf(din.getText()).floatValue();
                  

                 stop = false;
                 panel.set(stop);
                 panel.do_wave( L, d, m );

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

} // end InputSPanel
    class  PlotSPanel extends Panel implements Runnable
     {
       SeichePlot seicheplot;
       Image wImage;
       Graphics wGraphics;
       public float  d, L, T;
       boolean first = false,   stop=false;
       Thread thread;
       
       public PlotSPanel()
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
            seicheplot = new SeichePlot(bounds().width,bounds().height);

            wImage = createImage(bounds().width, bounds().height);
            wGraphics = wImage.getGraphics();
            first = true;
           }

         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         seicheplot.Draw(wGraphics);
         g.drawImage(wImage,0,0,this);
         }
    
        public void update(Graphics g)
         {
          paint(g);
 //         System.out.println("doing update in PlotPanel");
         }
        
        public void do_wave(float L,float d, float m  )
         {
          seicheplot.initialize(L,d,m);
//          System.out.println("sending "+L+d+m);
          seicheplot.do_wave();
          thread = new Thread(this);
          thread.start();
//          repaint();
         }
      public void run()
     {
      
       while(!stop)
        {
         seicheplot.do_wave( );
         repaint();
//         System.out.println("in run");
         try {thread.sleep(150);} 
         catch(Exception e){ }
        }
     }
   
   }//end of class PlotSPanel

     class SeichePlot
      {
       public int length, amplitude, totalh;
       public int x[],y[];  //plotting locations for velocity
       public float  H,d, T,L,mode;
       public double k;
       int depth[],uc[],ut[],vt[],vc[],um[],vm[],up[],vp[];
       float dz ;
       double dt;
       int dep;
       float famplitude;
       int ncount=8;
       String seicheperiod = " ";
       String percent = " ";
       public double temp;
       public int jstep=0;
       float umag[],vmag[];
       int offset=5;
       int Depth;
       int x1,x2,x3,x4,x5,x6,x7,x8;
        
        public SeichePlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y = new int[length+2];  
          uc = new int[ncount];
          ut = new int[ncount];
          vc = new int[ncount];
          vt = new int[ncount];
          um = new int[ncount];
          vm = new int[ncount];
          vp = new int[ncount];
          up = new int[ncount];
          umag = new float[ncount];
          vmag = new float[ncount];
          depth = new int[ncount];
   
          

          for (int i = 0; i<length;i++)
             { 
              x[i]=0;  
              y[i]=i;           
             }
      
        }
        public void initialize( float el, float de , float me)
         { 
            float Tl,per;

            L=el;
            mode=me;
            d=de;

            k=mode*Math.PI/L;
//      Compute seiche period
            Tl=(float) (2.*L/(mode*Math.sqrt(9.81*d))); //shallow
            T=(float)(2.*L/(mode*Math.sqrt(9.81*Hyper.tanh(k*d)/k)));
//            System.out.println("Tl,T: "+Tl+","+T);
            per=(float)(100*(T-Tl)/T);
            dt=T/20.;
            temp=((int)(T*100))/100.;
            seicheperiod = "T (mode "+mode+") = "+temp+" s" ;
  //          System.out.println("T (mode "+mode+") = "+T+" s");
 //            bottom = "h="+d;
            percent="Percent = "+per;
            H=(float)(1.);
            famplitude = (H/2)/(d+H/2)*(float) (totalh-offset);
            amplitude = (int) famplitude;
            dz=(totalh-amplitude-offset)/(ncount-1);

         }
  
         public void do_wave()
         {  
            double shd, chd, cosh,k,dep,argx,argt,cos,sinh,sin,cosx1,sinx1;
            double sinx2,cosx2, sinx3,cosx3, sinx4,cosx4;
            double sigma, factor,sh2d,ch2d,xl;
            jstep=jstep+1;
            sigma=2.*Math.PI/T;
//            System.out.println("freq: "+sigma);
          
            for (int i = 0; i<length;i++)
             { 
              argx=2.*mode*Math.PI*(i )/(2.*length);
              argt= sigma*jstep*dt ;
              x[i]=offset+amplitude-(int) (amplitude*Math.cos(argx)*Math.cos(argt));
               }
 //          calculate velocity
            k=2.*Math.PI/(2.*L);
            chd=Hyper.cosh(mode*k*d);
            sin=Math.sin(sigma*jstep*dt );
            xl=length;
            sinx1=Math.sin(0.);  // left wall point
            cosx1=Math.cos(0.);
            sinx2=Math.sin(Math.PI/4.); //next point
            cosx2=Math.cos(Math.PI/4.);
            sinx3=Math.sin(2.*Math.PI/4.);
            cosx3=Math.cos(2.*Math.PI/4.);
            sinx4=Math.sin(3.*Math.PI/4.);
            cosx4=Math.cos(3.*Math.PI/4.);
            
            for (int i=0; i<ncount; i++)
             {
              dep =(ncount-1-i)*d/(ncount-1);
              depth[i]=offset+amplitude+(int)(i*dz);
       
              umag[i]= (float)((totalh/10.)*Hyper.cosh(mode*k*dep) / chd );
              vmag[i]= (float)((totalh/10.)*Hyper.sinh(mode*k*dep) / chd );
//              umag[i]=(float)(totalh/12.);
//              vmag[i]=(float)(totalh/12.);
//              System.out.println("umag,vmag "+umag[i]+" "+vmag[i]);
              uc[i]= (int)(umag[i]*sin*sinx1);
              vc[i]=-(int)(vmag[i]*sin*cosx1);
              ut[i]= (int)(umag[i]*sin*sinx2);
              vt[i]=-(int)(vmag[i]*sin*cosx2);
              um[i]= (int)(umag[i]*sin*sinx3);
              vm[i]=-(int)(vmag[i]*sin*cosx3);
              up[i]= (int)(umag[i]*sin*sinx4);
              vp[i]=-(int)(vmag[i]*sin*cosx4);
              }
       }
        public void Draw(Graphics g)
         {
          int xl,yl,Umag,Vmag, imode,Imode,s;
          g.setColor(Color.black);
          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          g.drawLine(0,yl,xl,yl);
          g.setColor(Color.black);
          g.drawLine(xl,0,xl,xl);
//          g.drawLine(0,0,xl,0);
 //         for(int i =1;i<length;i++)
//            g.drawLine(i-1, x[i-1],i,x[i]); // wave profile
          g.setColor(Color.blue);
          x[length]=totalh;
          x[length+1]=totalh;
          y[length]=length-1;
          y[length+1]=0;
          g.fillPolygon(y,x,length+2 );
// draw velocities
          g.setColor(Color.white);
          Imode= (int) mode +1;
   
          for (imode=1;imode< Imode;imode++)
          for(int i=0;i<ncount;i++)
            {
             Depth=depth[i];
         
             x1=(int)((imode-1)*2.*xl/(mode)+ 0);
             x2=(int)((imode-1)*2.*xl/(mode)+ xl/(4.*mode)  );
             x3=(int)((imode-1)*2.*xl/(mode)+ 2.*xl/(4.*mode));
             x4=(int)((imode-1)*2.*xl/(mode)+ 3.*xl/(4.*mode));
             x5=(int)((imode-1)*2.*xl/(mode)+ 4.*xl/(4.*mode));
             x6=(int)((imode-1)*2.*xl/(mode)+ 5.*xl/(4.*mode));
             x7=(int)((imode-1)*2.*xl/(mode)+ 6.*xl/(4.*mode));
             x8=(int)((imode-1)*2.*xl/(mode)+ 7.*xl/(4.*mode));
             g.drawLine(x1,Depth, x1+  uc[i],Depth- vc[i]);
             g.drawLine(x2,Depth, x2+  ut[i],Depth- vt[i]);
             g.drawLine(x3,Depth, x3+  um[i],Depth- vm[i]);
             g.drawLine(x4,Depth, x4+  up[i],Depth- vp[i]);
             g.drawLine(x5,Depth, x5-  uc[i],Depth+ vc[i]);
             g.drawLine(x6,Depth, x6-  ut[i],Depth+ vt[i]);
             g.drawLine(x7,Depth, x7-  um[i],Depth+ vm[i]);
             g.drawLine(x8,Depth, x8-  up[i],Depth+ vp[i]);

            }
 //   print output
          xl=length-140;
          yl=amplitude+80;
          g.setColor(Color.yellow);
          g.drawString(seicheperiod,xl ,yl);
          yl=yl+20;
          g.drawString(percent,xl,yl);
           }

        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class SeichePlot

/*                          Add various basin shapes.
*/
