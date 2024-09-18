/*  Linear Wave Theory Plotting
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.util.Vector;
import Refract;
import Hyper;

public class LinearPlot extends Applet 
 {
   InputPanel panel1;
   PlotPanel graph1;
   public static int jstart = 0;
   public static float H=0, d=1, L, T;
   public void init()
  
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotPanel();
       add("Center",graph1);

       panel1 = new InputPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of LinearPlot

    class  InputPanel extends Panel  
     {  
      PlotPanel panel;
      TextField Hin, Tin, din;
      Refract ref;
      public static float H,T, d, L;
      public static int jstep = 0;
      Button Calculate, Stop;
      public boolean stop = false;
      Label label1;
  
      public InputPanel(PlotPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(6,2));
        add(label1 = new Label("Input Wave Data:"));
        add(new Label("      "));
        add(new Label("Wave Height (m)?"));
        add(Hin = new TextField("" ));
        add(new Label("Wave Period?"));
        add(Tin = new TextField("" ));
        add(new Label("Local Depth?"));
        add(din = new TextField("" ));
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
          if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
               { H = Float.valueOf(Hin.getText()).floatValue();
                 T = Float.valueOf(Tin.getText()).floatValue();
                 d = Float.valueOf(din.getText()).floatValue();
                 if(H>(float)(.8*d))
                  {H=(float)(.8*d);
                   Hin.setText(H+", breaking");
                  }
                 klin =   ref.waveNumber(d, T);
                 L= (float) 6.2831852/klin;
                 C=L/T;
                 stop = false;
                 panel.set(stop);
                 panel.do_wave(H, L, d, T );

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

} // end InputPanel
    class  PlotPanel extends Panel implements Runnable
     {
       WavePlot waveplot;
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T;
       boolean first = false,   stop=false;
       Thread thread;
       
       public PlotPanel()
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
            waveplot = new WavePlot(bounds().width,bounds().height);
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
        
        public void do_wave(float H, float L,float d, float T  )
         {
          waveplot.initialize(H,L,d,T);
          waveplot.do_wave();
          thread = new Thread(this);
          thread.start();
          }
      public void run()
     {
      
       while(!stop)
        {
         waveplot.do_wave( );
         repaint();
         try {thread.sleep(150);} 
         catch(Exception e){ }
        }
     }
   
   }//end of class PlotPanel

     class WavePlot
      {
       public int length, amplitude, totalh;
       int x[],y[];
       public float H, d, T,L;
       int depth[],uc[],ut[],vt[],vc[],xp[],yp[];
       float dz ;
       double dt;
       int dep;
       float famplitude;
       int ncount=10;
       String waveLength = " ";
       String bottom = " ";
       String umax = " ";
       String vmax = " ";
       public int jstep=0;
       float umag[],vmag[];
       int offset=5;
       int Depth, temp;
       
       
        public WavePlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y = new int[length+2];
          uc = new int[ncount];
          ut = new int[ncount];
          vc = new int[ncount];
          vt = new int[ncount];
          xp = new int[ncount];
          yp = new int[ncount];
          umag = new float[ncount];
          vmag = new float[ncount];
          depth = new int[ncount];
   
          

          for (int i = 0; i<length;i++)
             { 
              x[i]=0;    
              y[i]=i;         
             }
      
        }
        public void initialize( float he, float el, float de, float te )
         { 
             L=el;
            if(L>1.)
              {temp=(int) (100.*L);
               el=(float)(temp/100.);
              }
            else
              el=(float)( (int)(1000.*L)/1000.);
            waveLength = "L= "+el+" m";
            H=he;
            d=de;
            T=te;
            dt=te/30.;
//            bottom = "h="+d;
            famplitude = (H/2)/(d+H/2)*(float) (totalh-offset);
            amplitude = (int) famplitude;
//            System.out.println("height:"+totalh+", amplitude:"+amplitude);
            dz=(totalh-amplitude-offset)/(ncount-1);

         }





       public void do_wave()
         {  
            double chd, cosh,k,dep,arg,cos,sinh,sin;
            double sigma, factor;
            jstep=jstep+1;
            sigma=2.*Math.PI/T;
            k =2.*Math.PI/L;
            for (int i = 0; i<length;i++)
             { 
              arg=2.*Math.PI*i/length -sigma*jstep*dt ;
              x[i]=offset+amplitude-(int) (amplitude*Math.cos(arg));
               }
//          calculate velocity
 
            chd=Hyper.cosh(k*d);
            cos=Math.cos(-sigma*jstep*dt );
            sin=Math.sin(-sigma*jstep*dt );

            for (int i=0; i<ncount; i++)
             {
              dep =(ncount-1-i)*d/(ncount-1);
              depth[i]=offset+amplitude+(int)(i*dz);
 
              umag[i]=(float) (totalh*Hyper.cosh(k*dep) /(chd*15.));
              vmag[i]= (float)(totalh*Hyper.sinh(k*dep) /(chd*15.));
//              System.out.println("umag,vmag "+umag[i]+" "+vmag[i]);
              uc[i]=(int)( umag[i]*cos );
              xp[i]=(int)(umag[i]*sin);
              ut[i]=-uc[i];
              vc[i]=(int)(vmag[i]*sin);
              yp[i]=-(int)(vmag[i]*cos);
              vt[i]=-vc[i];
              }
              factor=9.81*k*15 *(H/2.)/(sigma*totalh);
              
              umax="u_max= "+((int)(umag[0]*factor*100.)/100.)+" m/s";
              vmax="v_max= "+((int)(vmag[0]*factor*100.)/100.)+" m/s";

//            System.out.println(" in WavePlot; a and d"+ amplitude + " "+d);
          }
           

         public void Draw(Graphics g)
         {
          int xl,yl,Umag,Vmag;
          g.setColor(Color.black);
          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          g.drawLine(0,yl,xl,yl);
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
//          g.drawLine(xl/2,amplitude+offset,xl/2,totalh);
          g.setColor(Color.blue);
//          for(int i =1;i<length;i++)
//            g.drawLine(i-1, x[i-1],i,x[i]); // wave profile
          
          x[length]=totalh;
          x[length+1]=totalh;
          y[length]=length-1;
          y[length+1]=0;
          g.fillPolygon(y,x,length+2 );


          for(int i=0;i<ncount;i++)
            {
             g.setColor(Color.white);
             Depth=depth[i];
              g.drawLine(0,Depth,  uc[i],Depth-vc[i]);
              g.drawLine(xl/2,Depth,xl/2+ut[i],Depth-vt[i]);
              g.drawLine(xl,Depth, xl+ uc[i],Depth-vc[i]);
              Vmag=(int)vmag[i];
              Umag=(int)umag[i];
              g.drawOval(  xl/2-Umag ,Depth-Vmag, 2*Umag , 2*Vmag);
              g.drawOval( -Umag ,Depth-Vmag,2*Umag,2*Vmag);
              g.drawOval( xl-Umag, Depth-Vmag, 2*Umag,2*Vmag );
              g.fillOval(-xp[i],Depth+yp[i],4,4);
              g.fillOval(xl/2+xp[i],Depth-yp[i],4,4);
              g.fillOval(xl-xp[i],Depth+yp[i],4,4);
             }   
          xl=length-130;
          yl=amplitude+80;
          g.setColor(Color.yellow);
          g.drawString(waveLength,xl ,yl);
          yl=yl+20;
          g.drawString(umax,xl,yl);
          yl=yl+20;
          g.drawString(vmax,xl,yl);
 
         }
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class WavePlot

         
          
        
