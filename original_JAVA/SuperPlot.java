/* Superposition of Waves
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import java.util.Vector;
import Refract;
import Hyper;

public class SuperPlot extends Applet 
 {
   InputSuPanel panel1;
   PlotSuPanel graph1;
   public static int jstart = 0;
   public static float H1,d,T1,s1, L, T;
   public static float H2,T2,s2;
   GridBagLayout gblayout;
   GridBagConstraints gbc;

   public void init()
  
     {
        gblayout=new GridBagLayout();
        setLayout(gblayout);
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth=GridBagConstraints.REMAINDER;
        graph1 = new PlotSuPanel();
        gbc.anchor=GridBagConstraints.CENTER;
        gblayout.setConstraints(graph1,gbc);
        add(graph1);
        gbc.gridwidth=GridBagConstraints.RELATIVE;

    
       
        panel1 = new InputSuPanel(graph1);
//        gbc.anchor=GridBagConstraints.EAST:
        gblayout.setConstraints(panel1,gbc);
        add(panel1);
      
     }
                            
}      //end of LinearPlot

    class  InputSuPanel extends Panel  
     {  
      PlotSuPanel panel;
      TextField H1in, T1in, s1in, din, xout;
      TextField H2in, T2in, s2in;
      TextField H3in, T3in, s3in;
      TextField H4in, T4in, s4in;
      public  float H1,T1, s1, d, L;
      public  float H2, T2, s2;
      public  float H3, T3, s3;
      public  float H4, T4, s4;
      public static int jstep = 0;
      Button Calculate, Stop;
      Choice Components;
      Scrollbar sbar;
      public boolean stop = false;
      public boolean comps = false;
      Label label1;
      int xw;
  
      public InputSuPanel(PlotSuPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(7,4));
        xw=300;
        add(label1 = new Label("Data:"));
        add(new Label("   Height"));
        add(new Label("   Period"));
        add(new Label("   +1./-1.? "));

        add(new Label("Wave 1:"));
        add(H1in = new TextField("1.0" ));
        add(T1in = new TextField("3.0" ));
        add(s1in = new TextField("+1." ));

        add(new Label("Wave 2:"));
        add(H2in = new TextField("1.0" ));
        add(T2in = new TextField("2.9" ));
        add(s2in = new TextField("+1." ));

        add(new Label("Wave 3:"));
        add(H3in = new TextField("0." ));
        add(T3in = new TextField("0." ));
        add(s3in = new TextField("+1." ));
 
        add(new Label("Wave 4:"));
        add(H4in = new TextField("0." ));
        add(T4in = new TextField("0." ));
        add(s4in = new TextField("+1."));


        add(new Label("Local Depth?"));
        add(din = new TextField("3." ));
        Components = new Choice();
        Components.addItem("Superpose");
        Components.addItem("Components");
        add(Components);
        add(Calculate = new Button("Calculate"));
 
        add(new Label("Plot Length:"));
        sbar= new Scrollbar(Scrollbar.HORIZONTAL);
        sbar.setValues(300,100,100,5000);
        add(sbar);
        add(xout= new TextField(" x_max= 300"));
        xout.setEditable(false);
	add(Stop = new Button("Stop"));

       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
      try 
         {if (e.target instanceof Scrollbar)
              {xw=sbar.getValue();
               xout.setText("x_max = "+String.valueOf(xw)+" m");
               panel.do_wave(H1, H2, H3, H4, d, T1,T2, T3, T4,comps,xw );
               panel.repaint();
              }
           else if (e.id == Event.ACTION_EVENT)
            {if (e.target.equals(Components))
             if("Superpose".equals (e.arg))
               comps=false;
             else
               comps=true;
          
           else if( (e.target instanceof Button)) 
              { if(e.target == Calculate)
               { H1 = Float.valueOf(H1in.getText()).floatValue();
                 T1 = Float.valueOf(T1in.getText()).floatValue();
                 s1 = Float.valueOf(s1in.getText()).floatValue();
                 if(Math.abs(s1) != 1)
                   {s1=s1/Math.abs(s1); //Normalize to one
                    s1in.setText(s1+" ");
                    }
                 H2 = Float.valueOf(H2in.getText()).floatValue();
                 T2 = Float.valueOf(T2in.getText()).floatValue();
                 s2 = Float.valueOf(s2in.getText()).floatValue();
                 if(Math.abs(s2) != 1)
                   {s2=s2/Math.abs(s2);
                    s2in.setText(s2+" ");
                    }
                 H3 = Float.valueOf(H3in.getText()).floatValue();
                 T3 = Float.valueOf(T3in.getText()).floatValue();
                 s3 = Float.valueOf(s3in.getText()).floatValue();
                 if(Math.abs(s3) != 1)
                   {s3=s3/Math.abs(s3);
                    s3in.setText(s3+" ");
                    }
                 H4 = Float.valueOf(H4in.getText()).floatValue();
                 T4 = Float.valueOf(T4in.getText()).floatValue();
                 s4 = Float.valueOf(s4in.getText()).floatValue();
                 if(Math.abs(s3) != 1)
                   {s4=s4/Math.abs(s4);
                    s4in.setText(s4+" ");
                    }
                 d =  Float.valueOf(din.getText()).floatValue();
                 T1=T1*s1;
                 T2=T2*s2;
                 T3=T3*s3;
                 T4=T4*s4;
                 stop = false;
                 panel.set(stop);
                 panel.do_wave(H1, H2, H3, H4, d, T1,T2, T3, T4,comps,xw );

                 panel.repaint();
                }  //end Calculate
            else if (e.target == Stop)
               { stop = true;
                 panel.set(stop);
               }  //end Stop
            } // end Button
           } //end Action Event
         }  //end try
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }

} // end InputSuPanel
    class  PlotSuPanel extends Panel implements Runnable
     {
       WaveSuPlot waveplot;
       Image wImage;
       Graphics wGraphics;
       public float H1, d, L, T1, s1;
       float H2, T2, s2;
       float H3, T3, s3;
       float H4, T4, s4;
       boolean first = false,   stop=false;
       boolean comps;
       Thread thread;
       
       public PlotSuPanel()
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
            waveplot = new WaveSuPlot(bounds().width,bounds().height);
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
        
        public void do_wave(float H1, float H2, float H3, float H4, float d, float T1, float T2, float T3, float T4, boolean comps , int xw )
         {
          waveplot.initialize(H1,H2, H3, H4, d,T1,T2, T3, T4,comps, xw);
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
   
   }//end of class PlotSuPanel

     class WaveSuPlot
      {
       public int length, amplitude, totalh;
       int x[],y[];
       int x1[],x2[],x3[],x4[];
       public float H, d, T, Ht;
       double L1,L2, L3, L4;
       double k1,k2, k3,k4;
       Refract ref;
       float H1, T1, H2, T2, H3, T3, H4, T4;
       int depth[],uc[],ut[],vt[],vc[],xp[],yp[];
       float dz ;
       double dt,xmax,time;
       int dep, xw;
       float famplitude;
       int ncount=10;
       String waveLength = " ";
       String bottom = " ";
       String umax = " ";
       String vmax = " ";
       public int jstep;
       float umag[],vmag[];
       int offset=5;
       int Depth, temp;
       int origin;
       boolean comps;
       
       
        public WaveSuPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y = new int[length+2];
          x1 = new int[length+2];
          x2 = new int[length+2];
          x3 = new int[length+2];
          x4 = new int[length+2];
          uc = new int[ncount];
          ut = new int[ncount];
          vc = new int[ncount];
          vt = new int[ncount];
          xp = new int[ncount];
          yp = new int[ncount];
          umag = new float[ncount];
          vmag = new float[ncount];
          depth = new int[ncount];
          ref = new Refract();
          

          for (int i = 0; i<length;i++)
             { 
              x[i]=0;    
              y[i]=i;         
             }
      
        }
        public void initialize( float he1, float he2, float he3, float he4, float de, float te1, float te2, float te3, float te4, boolean compsss, int xwww )
         { float el;
           comps=compsss;
//           System.out.println("initialize:comps: "+String.valueOf(comps));
           T1=te1;
           T2=te2;
           T3=te3;
           T4=te4;
           xw=xwww;
            dt=Math.abs(te1)/30.;
            jstep=0;
            d=de;
            k1=ref.waveNumber(d,T1);
            if(T2 !=0 )
                k2=ref.waveNumber(d,T2);
            if(T3 !=0)
               k3=ref.waveNumber(d,T3);
            if(T4 !=0)
               k4=ref.waveNumber(d,T4);

            H1=he1;
            H2=he2;
            H3=he3;
            H4=he4;
            d=de;
 //            bottom = "h="+d;
            Ht=H1+H2+H3+H4;
            famplitude = (Ht/2)/(d+Ht/2)*(float) (totalh-offset);
            amplitude = (int) famplitude;
//            System.out.println("height:"+totalh+", amplitude:"+amplitude);
            dz=(totalh-amplitude-offset)/(ncount-1);

         }





       public void do_wave()
         {  
            double chd, cosh, dep,arg,arg2,arg3,arg4,cos,sinh,sin;
            double sigma1, sigma2, sigma3,sigma4,factor;
            jstep=jstep+1;
                 
            sigma1=2.*Math.PI/T1;
            sigma2=2.*Math.PI/T2;
            sigma3=2.*Math.PI/T3;
            sigma4=2.*Math.PI/T4;
            xmax=xw;
//          Water surface elevation
            for (int i = 0; i<length;i++)
             { 
              time=jstep*dt;
              arg=k1*i*xmax/length -sigma1*time ;
              arg2=k2*i*xmax/length-sigma2*time;
              arg3=k3*i*xmax/length-sigma3*time;  
              arg4=k4*i*xmax/length-sigma4*time;
              origin=offset+amplitude;
              x2[i]=0;
              x3[i]=0;
              x4[i]=0;
              x1[i]=-(int) (amplitude*H1/Ht*Math.cos(arg));
              if(H2 > 0 & T2 !=0 )
                     {x2[i]=-(int)(amplitude*H2/Ht*Math.cos(arg2));}
              if(H3 >0 & T3 !=0)
                     {x3[i]=-(int)(amplitude*H3/Ht*Math.cos(arg3));}
              if(H4 >0 & T4 !=0)
                     {x4[i]=-(int)(amplitude*H4/Ht*Math.cos(arg4));}
              x[i]=origin+x1[i]+x2[i]+x3[i]+x4[i];
              if(comps == true)
                 {
                  x1[i]+=origin;
                  x2[i]+=origin;
                  x3[i]+=origin;
                  x4[i]+=origin;
                  }
            
                   
               }
//          calculate velocity
 
/*            chd1=Hyper.cosh(k1*d);
            chd2=Hyper.cosh(k2*d);
            cos=Math.cos(-sigma1*jstep*dt );
            sin=Math.sin(-sigma1*jstep*dt );

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
              factor=9.81*k*15 *(H/2.)/(sigma1*totalh);
              
              umax="u_max= "+((int)(umag[0]*factor*100.)/100.)+" m/s";
              vmax="v_max= "+((int)(vmag[0]*factor*100.)/100.)+" m/s";

//            System.out.println(" in WaveSuPlot; a and d"+ amplitude + " "+d);
*/
          }
           

         public void Draw(Graphics g)
         {
          int xl,yl,Umag,Vmag;
          float ttime;
          g.setColor(Color.black);
          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          g.drawLine(0,yl,xl,yl);
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
          g.setColor(Color.blue);
  
          if(comps == false)
             {
              x[length]=totalh;
              x[length+1]=totalh;
              y[length]=length-1;
              y[length+1]=0;
              g.fillPolygon(y,x,length+2 );
              }
           else
               {if(H1 > 0)
                  for (int i=1; i<length;i++)
                  {g.setColor(Color.red);
                  g.drawLine(i-1,x1[i-1],i,x1[i]);
                  }
                if(H2 >0)
                  for (int i=1; i<length;i++)
                   {g.setColor(Color.green);
                    g.drawLine(i-1,x2[i-1],i,x2[i]);
                   }
                if(H3 >0)
                  for (int i=1; i<length;i++)
                  {
                   g.setColor(Color.gray);
                   g.drawLine(i-1,x3[i-1],i,x3[i]);
                   }
                if(H4 >0)
                  for (int i=1; i<length;i++)
                  {
                   g.setColor(Color.blue);
                   g.drawLine(i-1,x4[i-1],i,x4[i]);
                   }
                 g.setColor(Color.black);
                 g.drawLine(0,origin,length,origin); 
                 }  //end else
               

/*
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
*/
          if( comps == true)
            {xl= 50;
             yl=amplitude+40;
             g.setColor(Color.red);
             g.drawLine(xl-30,yl,xl-10,yl);
             g.drawString("Wave 1",xl,yl);
             g.setColor(Color.green);
             yl+=20;
             g.drawLine(xl-30,yl,xl-10,yl);
             g.drawString("Wave 2",xl,yl);
             yl=amplitude+40;
             g.setColor(Color.gray);
             g.drawLine(xl+50,yl,xl+70,yl);
             g.drawString("Wave 3",xl+80,yl);
             yl+=20;
             g.setColor(Color.blue);
             g.drawLine(xl+50,yl,xl+70,yl);
             g.drawString("Wave 4",xl+80,yl);

            }
          xl=length-130;
          yl=amplitude+50;
          if( comps == true)
             g.setColor(Color.black);
          else 
             g.setColor(Color.white);
//          g.drawString(waveLength,xl ,yl);
//          g.drawString("x_max= "+xmax+" m",xl,yl);
//          yl=yl+20;
//          g.drawString(umax,xl,yl);
          ttime=(float)( (int) (time*10)/10.);
          g.drawString("t= "+ttime+" s",xl,yl);
//          yl=yl+20;
//          g.drawString(vmax,xl,yl);
 
         }
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class WaveSuPlot

         
          
        
