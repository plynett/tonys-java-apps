/*  Wavemaker Calculation
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.util.Vector;
import Refract;
import Hyper;
import Wvnumev;

public class Wavemaker extends Applet 
 {
   InputMPanel panel1;
   PlotMPanel graph1;
   public static int jstart = 0;
   public static float H=0, d=1, L, T;
   public void init()
  
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotMPanel();
       add("Center",graph1);

       panel1 = new InputMPanel(graph1);
       add("West",panel1);
     }
                            
}      //end of Wavemaker

    class  InputMPanel extends Panel  
     {  
      PlotMPanel panel;
      TextField Hin, Tin, din;
      Refract ref;
      public static float H,T, d, L,Stroke;
      public static int jstep = 0;
      Button Calculate, Stop;
      public boolean stop = false;
      Label label1;
      Choice wmtype,pltype;
      boolean wavemaker = true; //means piston
      boolean plotw = true; // means plot wavemaker
  
      public InputMPanel(PlotMPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(7,2));
        add(new Label("Choose Wavemaker"));
        wmtype = new Choice();
        wmtype.addItem("Piston");
        wmtype.addItem("Flap");
        add(wmtype);
        add(new Label("Choose Plot"));
        pltype = new Choice();
        pltype.addItem("Wavemaker");
        pltype.addItem("Velocity Modes");
        add(pltype);
        add(label1 = new Label("Desired Wave Data:"));
        add(new Label(" "));
        add(new Label("Wave Height (m)?"));
        add(Hin = new TextField("1.0" ));
        add(new Label("Wave Period?"));
        add(Tin = new TextField("3.0" ));
        add(new Label("Local Depth?"));
        add(din = new TextField("4.0" ));
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
             if(  e.target.equals(wmtype))
               if ("Flap".equals (e.arg) )
                   wavemaker= false;
               else
                   wavemaker=true;

            if( e.target.equals(pltype))
               if ("Wavemaker".equals (e.arg))
                   plotw=true;
               else
                   plotw=false;
               
            if( e.target instanceof Button ) 
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
                 stop = false;
                 panel.set(stop);
                 panel.do_wave(H, L, d, T, wavemaker, plotw);

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

} // end InputMPanel
    class  PlotMPanel extends Panel implements Runnable
     {
       MakerPlot makerplot;
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T;
       boolean first = false,   stop=false;
       Thread thread;
       
       public PlotMPanel()
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
            makerplot = new MakerPlot(bounds().width,bounds().height);

            wImage = createImage(bounds().width, bounds().height);
            wGraphics = wImage.getGraphics();
            first = true;
           }

         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         setBackground(Color.white);
         makerplot.Draw(wGraphics);
         g.drawImage(wImage,0,0,this);
         }
    
        public void update(Graphics g)
         {
          paint(g);
         }
        
        public void do_wave(float H, float L,float d, float T,boolean wmtype, boolean pltype)
         {
          makerplot.initialize(H,L,d,T,wmtype, pltype);
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
   
   }//end of class PlotMPanel

     class MakerPlot
      {
       Wvnumev wvnum;
       public int length, amplitude, totalh;
       int x[],y[]; //surface positions
       public float H, d, T,L;
       public double Stroke,S, Power,sigma,A;
       int depth[],uc[],u1[],u2[],u3[];
       double Cn[];
       float us[];
       float z[];
       float dz ;
       double dt,k,shd,chd,dzz;
       int dep;
       float famplitude;
       int ncount=12;
       String waveLength = " ";
       String bottom = " ";
       String stroke = " ";
       String power =" ";
       String ucmax=" ";
       String u1max=" ";
       String u2max=" ";
       String u3max=" ";
       public int temp;
       public int jstep=0;
       public boolean wmtype=false;
       public boolean ptype=true;
       int position; //mean paddle location - (half the paddle width)
       float umag[],vmag[];
       int offset=5;
       int Depth;
       public int pw=5; //half paddlewidth
       double scale,factor,f1;
       public float ks[];
       public double ucm,u1m,u2m,u3m;
      

       
        public MakerPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y=  new int[length+2];
          uc = new int[31];
          u1 = new int[31];
          u2 = new int[31];
          u3 = new int[31];
          umag = new float[ncount];
          vmag = new float[ncount];
          depth = new int[ncount];
   
          

          for (int i = 0; i<length;i++)
             { 
              x[i]=0; 
              y[i]=i;            
             }
      
        }
        public void initialize( float he, float el, float de, float te,boolean wmaker, boolean pltw )
         { 
           
            double sh2d,ch2d,ksd;
            wmtype=wmaker;
            ptype=pltw;
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
            sigma=2.*Math.PI/T;
//          Calculate evanescent modes
            wvnum= new Wvnumev();
            ks=new float[5];
            for (int jk=1;jk<4;jk++)
              {ks[jk]=wvnum.wvnumev(d,T,jk);
//               System.out.println("ks["+jk+"]= "+ks[jk]);
               }

            famplitude = (H/2)/(d+H/2)*(float) (totalh-offset);
            amplitude = (int) famplitude;
//            System.out.println("height:"+totalh+", amplitude:"+amplitude);
            dz=(totalh-amplitude-offset)/(ncount-1);
            dzz=d/29;
//          calculate wavemaker stroke
            Cn=new double[4];
            k=2.*Math.PI/L;
            chd=Hyper.cosh(k*d);
            shd=Hyper.sinh(k*d);
            ch2d=Hyper.cosh(2.*k*d);
            sh2d=Hyper.sinh(2.*k*d);
            if(wmtype) 
              {Stroke=H*(sh2d+2.*k*d)/(2.*(ch2d-1.));
               A=-2*sigma*Stroke*shd /(k*(sh2d+2.*k*d));
               for(int i=1;i<4;i++)
                 {ksd=ks[i]*d;
                  Cn[i]=-2*sigma*Stroke*Math.sin(ksd) 
                        /(ks[i]*(Math.sin(2.*ksd)+2*ksd));
                 }
              }
            else
              {Stroke=H*k*d*(sh2d+2.*k*d)/(4.*shd*((k*d)*shd-chd+1.));
               A=-2*sigma*Stroke*(k*d*shd-chd+1.)/(k*k*d*(sh2d+2.*k*d));
               for(int i=1;i<4;i++)
                 {ksd=ks[i]*d;
                 Cn[i]=-2*sigma*Stroke*(1-ksd*Math.sin(ksd)-Math.cos(ksd))
                        /(ks[i]*ks[i]*d*(Math.sin(2.*ksd)+2*ksd));
//                  System.out.println("Cn["+i+"]= "+Cn[i]);
                 }
              }
            if(Stroke >1.)
                Stroke=((int) (Stroke*100.))/100. ;
            else
                Stroke=((int) (Stroke*1000.))/1000.;
            Power=(10000./8)*H*H*(0.5*(1+2.*k*d/sh2d))*L/T;
//          truncate
            Power=( (int)(Power*100.))/100.;
//          determine how to scale velocities
            ucm= Math.abs(A*k*Hyper.cosh(k*d)) ;
            u1m= Math.abs(Cn[1]*ks[1]);
            u2m= Math.abs(Cn[2]*ks[2]);
            u3m= Math.abs(Cn[3]*ks[3]);
           
            if  (ucm > u1m)
              scale=ucm;
            else if ( u1m > u2m )
              scale=u1m;
            else
              scale=u2m;

            ucm= ( (int)(ucm*1000.))/1000.;
            u1m= ( (int)(u1m*1000.))/1000.;
            u2m= ( (int)(u2m*1000.))/1000.;
            u3m= ( (int)(u3m*1000.))/1000.;
            ucmax="uc = "+ucm+" m/s";
            u1max="u1 = "+u1m+" m/s";
            u2max="u2 = "+u2m+" m/s";
            u3max="u3 = "+u3m+" m/s";
            
            f1=length/(2*scale);
            factor=totalh/d;
//            System.out.println(length/2+", "+scale+", "+f1+", "+u1m+", "+u2m+", "+ucm);

         }
  
         public void do_wave()
         {  
            double arg,cos,sin;
           
             
            jstep=jstep+1;
            position=length/30;
            for (int i = 0; i<length;i++)
             { 
              arg=2.*Math.PI*(i+position+pw)/length -sigma*jstep*dt ;
              x[i]=offset+amplitude-(int) (amplitude*Math.cos(arg));
               }
            stroke="Stroke= "+Stroke+" m";
            cos=Math.cos(sigma*jstep*dt );
            sin=Math.sin(sigma*jstep*dt );
            S=position*sin;  //stroke is arbitrarily same as position
            power="Power= "+Power+" W";
//          calculate velocity profiles
//           System.out.println("d, dzz:"+d+", "+dzz);
//           System.out.println(A+","+k+", "+d+ks[1]);
           z=new float[31];
           for (int jp=0;jp<30;jp++)   
             {z[jp]=(float)(-jp*dzz);
              uc[jp]=(int)(f1*A*k*Hyper.cosh(k*(d+z[jp]))*cos);
              u1[jp]=(int)(f1*Cn[1]*ks[1]*Math.cos(ks[1]*(d+z[jp]))*cos);
              u2[jp]=(int)(f1*Cn[2]*ks[2]*Math.cos(ks[2]*(d+z[jp]))*cos);
              u3[jp]=(int)(f1*Cn[3]*ks[3]*Math.cos(ks[3]*(d+z[jp]))*cos);
//              System.out.println("z,uc,u1,u2: "+z[jp]+", "+uc[jp]+", "+u1[jp]+", "+u2[jp]);
             }    
        
          }
           

         public void Draw(Graphics g)
         {
          int xl,yl;
          int xpts[]={position,position+2*pw,0,0};
          int ypts[]={totalh,totalh,0,0};
          int bxpts[]={0,0,position,0};
          int bypts[]={0,totalh,totalh,0};
          
          g.setColor(Color.black);
          if (ptype)
            {g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
            g.drawLine(0,0,0,totalh);                 //z axis
            xl=length-1;
            yl=totalh-1;
            g.drawLine(0,yl,xl,yl);
            g.setColor(Color.black);
 //         g.drawLine(xl,0,xl,xl);
//          g.drawLine(0,0,xl,0);
            g.setColor(Color.blue);
//          for(int i =1;i<length;i++)
 //           g.drawLine(i-1, x[i-1],i,x[i]); // wave profile
            x[length]=totalh;
            x[length+1]=totalh;
            y[length]=length-1;
            y[length+1]=0;
            g.fillPolygon(y,x,length+2);
          
//          draw wavemaker
            g.setColor(Color.black);
            if(wmtype)
              { g.fillRect(position+(int)S,0,2*pw,totalh);//piston
                g.setColor(Color.lightGray); //erase behind paddle
                g.fillRect(0,0,position+(int)(S),totalh);
               }
             else
              { g.setColor(Color.black);
                xpts[2]=position+(int) S+2*pw;
                xpts[3]=position+(int) S;
                g.fillPolygon(xpts,ypts,4);
                g.setColor(Color.lightGray);
                bxpts[3]=position+(int) S;
                g.fillPolygon(bxpts,bypts,4);
              }
//        print output
          xl=length-130;
          yl=amplitude+50;
          g.setColor(Color.red);
          g.drawString(waveLength,xl ,yl);
          yl=yl+30;
          g.drawString(stroke,xl,yl);
          yl=yl+30;
          g.drawString(power,xl,yl);
          }
          else  // draw the velocities
          {
//          box the graph
            g.drawLine(0,1,length,1); //top x axis
            xl=length-1;
            yl=totalh-1;
            g.drawLine(0,yl,xl,yl);  // bottom
            g.drawLine(0,0,0,yl);  //left
            g.drawLine(xl,0,xl,yl); //right
       
//          draw velocities
            xl=length/2;
           
           
            for (int i=1;i<30;i++)
                 {g.setColor(Color.red);
                  g.drawLine(xl+ uc[i-1], -(int)(z[i-1]*factor),xl+ uc[i], -(int)(z[i]*factor));
                  g.setColor(Color.green); 
                  g.drawLine(xl+ u1[i-1],  -(int)(z[i-1]*factor),xl+ u1[i], -(int)(z[i]*factor));
                  g.setColor(Color.blue); 
                  g.drawLine(xl+ u2[i-1],  -(int)(z[i-1]*factor),xl+ u2[i], -(int)(z[i]*factor)); 
                  g.setColor(Color.cyan);
                  g.drawLine(xl+ u3[i-1],  -(int)(z[i-1]*factor),xl+ u3[i], -(int)(z[i]*factor));
                 }
            g.setColor(Color.black);
            g.drawLine(length/2,0,length/2,totalh-1);                 //z axis
//          print output
            xl=length-130;
            yl=amplitude+50;
            g.setColor(Color.red);
            g.drawString(ucmax,xl ,yl);
            yl=yl+30;
            g.setColor(Color.green);
            g.drawString(u1max,xl,yl);
            g.setColor(Color.blue);
            yl=yl+30;
            g.drawString(u2max,xl,yl);
            g.setColor(Color.cyan);
            yl=yl+30;
            g.drawString(u3max,xl,yl);


          }
          }

        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class MakerPlot

         
          
        
