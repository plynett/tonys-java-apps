/*  Calculate Wind Surge 
             Robert A. Dalrymple, Feb 1999
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;

 
public class WindSurge extends Applet 
 {
   InputSrgPanel panela;
   SrgPlotPanel grapha;
   public void init()
     {
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       grapha = new SrgPlotPanel();
       add("Center",grapha);

       panela = new InputSrgPanel(grapha);
       add("West",panela);
     }
                            
}      //end of SrgPlot

    class  InputSrgPanel extends Panel  
     {  
      SrgPlotPanel panel;
      TextField Uin, hin, lin, win;
      Button Calculate;
      public static float U, h, l,w;
      Choice boundary;
      boolean bound = true; //closed boundary
      Label label1;
  
      public InputSrgPanel(SrgPlotPanel panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(7,2));
        add(label1 = new Label("Input  Data:"));
        add(new Label(" "));
        add(new Label("Upwind Boundary:"));
        boundary= new Choice();
        boundary.addItem("Closed");
        boundary.addItem("Open");
        add(boundary);
        add(new Label("Wind Speed (km/hr)?"));
        add(Uin = new TextField("100.0" ));
        add(new Label("Water Depth(m)?"));
        add(hin = new TextField("3.0" ));
        add(new Label("Length of Basin(km)?"));
        add(lin = new TextField("30.0" ));
        add(new Label("End Wall Height(m)?"));
        add(win = new TextField("3.0"));
        add(Calculate = new Button("Calculate"));
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
            if(e.target.equals(boundary))
                {if("Open".equals (e.arg))
                   bound=false;
                 else
                   bound=true;
                }
           if (e.target instanceof Button)  
             { if(e.target == Calculate)
               { U = Float.valueOf(Uin.getText()).floatValue();
                 h = Float.valueOf(hin.getText()).floatValue();
                 l = Float.valueOf(lin.getText()).floatValue();
                 w = Float.valueOf(win.getText()).floatValue();
                }
               panel.do_wave(U,h,l,w,bound);
               panel.repaint();              
             } // end Calculate
           } //end ACTION_EVENT
         } //end try
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }

} // end InputSrgPanel
    class  SrgPlotPanel extends Panel 
     {
       SrgPlot srgplot;
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T;
       boolean first = false,   stop=false;
       Thread thread;
       
       public SrgPlotPanel()
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
            srgplot = new SrgPlot(bounds().width,bounds().height);
            wImage = createImage(bounds().width, bounds().height);
            wGraphics = wImage.getGraphics();
            first = true;
           }

         wGraphics.clearRect(0,0,bounds().width,bounds().height);
         srgplot.Draw(wGraphics);
         g.drawImage(wImage,0,0,this);
         }
    
        public void update(Graphics g)
         {
          paint(g);
         }
        
        public void do_wave(float U, float h,float l, float w, boolean btype )
         {
          srgplot.initialize(U,h,l,w, btype);
          srgplot.do_wave();
      }
   
   }//end of class SrgPlotPanel

     class SrgPlot
      {
       public int length, totalh;
       public int x[], totdep[];
       public double U,h,l,w;
       public double dx;
       double famplitude;
       public boolean boundtype;
       String etal = "";
       String eta0 = " ";
       String cout =" ";
       String xout =" ";
       public double a, c, xstar=0.0;
       int term, overtopping;
       int offset=5;
        
       
        public SrgPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          totdep = new int[length+2];
    
         
          for (int i = 0; i<length+2;i++)
             { 
              x[i]=0;       
              totdep[i]=296;     
             }
      
        }
        public void initialize( float Ue, float he, float le, float we, boolean btype )
         { 
          double fp, f, dc, vdk;
          U=Ue;
          U=U*1000./3600.; /*km/hr to m/sec */
          vdk=0.0000012;   /* Van Dorn coefficient D+D, p 157 */
          if (U > 5.6)
              {vdk=vdk+0.00000225*(1-5.6/U)*(1-5.6/U);
              }
          a=2*1.15*vdk*U*U/9.81;
          h=he;
          w=we;
          l=le;
          l=l*1000.;  /* km to m */
          boundtype= btype;
          dx= le/length;
          famplitude=totalh/(h+w);
          term=1;
          overtopping=1;
//          System.out.println("boundtype "+boundtype);
          if (boundtype)  //do closed first
           { if (l > 9*h*h/(4.*a))
               /* bottom exposed*/
                 {  term=0;
                  c=Math.pow(1.5*a*h*l,0.6667)-a*l;
                  xstar=-c/a;
                 }
              else
                {c=h*h-h/2;
                 for (int i =0; i<10; i++)
                    {fp=1.5*Math.sqrt(a*l+c)-1.5*Math.sqrt(c);
                      f=Math.pow(a*l+c,1.5)-Math.pow(c,1.5)-1.5*a*h*l;
                      c=c -(.5*f)/fp;
                     }
                }
             if (c > (h+w)*(h+w) -a*l)
              {overtopping=0;
                c=Math.pow(h+w,2)-a*l;
                if ( Math.pow(h+w,2) < a*l)
                   {term=0;
                     xstar=l-Math.pow(h+w,2)/a; 
//                   System.out.println(" drybottom: xstar: "+xstar);
                   } 
               }     
           } //end boundtype true
          else   
            {c=h*h;
             xstar=0;
            } //ends all of boundtype
 
           if (xstar > 0)
              xout="xstar = "+((int)(xstar*100.))/100. + "m";
           else
              xout=" ";

           cout="c = "+((int)(c*10000.))/10000.;
         }//end intialize

       public void do_wave()
         {  
           int ilast=1;
           double localx, eta;
//           System.out.println("in do_wave: a,c,length,l,term,h: "+a + ", "+c+", "+length+", "+l+", "+term+", "+h);
//            System.out.println("in do_wave: xstar: "+xstar);
            for (int i = 0; i<length;i++)
             {                  
//             calculate surge height
              x[i]=i;
              ilast=i;
              if (term == 1 )  
                   { 
                    eta= famplitude*(Math.sqrt(a*x[i]*l/length+c)-h);
                   }
               else
                   {localx=x[i]*l/length;
                     if ( localx < xstar)
                        {
                         eta=-famplitude*h;
//                         System.out.println("x,eta[i]: "+x[i]*l/length+", "+eta[i]);
                        }
                      else
                        eta=famplitude*(Math.sqrt(a*x[i]*l/length+c) -h);
                   }
               totdep[i]=(int)(totalh- (famplitude*h+eta));
//               System.out.println("x[i]*l/length,totdep: " +x[i]*l/length+", "+totdep[i]);

              }
 
           etal=" eta(l)=  "+((int)((totalh-famplitude*h-totdep[ilast])*100./famplitude)/100.) +" m";
            if (overtopping == 0)
                   {
                    etal=etal+", overtopping";
                   }
            eta0="eta(0)= "+((int)((totalh-famplitude*h-totdep[0])*100/famplitude)/100.)+" m,"; 
//           System.out.println(" in SrgPlot; a and c"+ a  + " "+c);
          }
           

         public void Draw(Graphics g)
         {
          int xl,yl, mwl;
          xl=length-1;
          yl=totalh-1;
          g.setColor(Color.white);
//          g.fillRect(0,0,length,totalh);
          g.setColor(Color.blue);
          totdep[length]=totalh;
          totdep[length+1]=totalh;
          x[length]=length;
          x[length+1]=0;
//          for ( int i=1;i<length+2;i++)
//            { 
//            System.out.println("x,totdep: "+x[i]+", "+totdep[i]);
//            }
          g.fillPolygon(x,totdep,length+2);
//        draw basin
          g.setColor(Color.black);
          mwl=(int)(totalh-h*famplitude);
          if(boundtype)
            { g.drawLine(0,totalh,0,mwl);
             g.drawLine(1,totalh,1,mwl);}
 
          g.drawLine(0,totalh,length+1,totalh);
          g.drawLine(0,totalh-1,length+1,totalh-1);
          g.drawLine(length,totalh,length,mwl);
          g.drawLine(length-1,totalh,length-1,mwl);
          g.setColor(Color.red);  //draw red wall at downstream end
          g.drawLine(length,mwl,length,(int)(totalh-(h+w)*famplitude));
          g.drawLine(length-1,mwl,length-1,(int)(totalh-(h+w)*famplitude));
          g.drawLine(length-2,mwl,length-2,(int)(totalh-(h+w)*famplitude));
          g.setColor(Color.black);
          g.drawString(etal,120,20);
          g.drawString(eta0,20,20);
          g.drawString(cout,20,40);
          g.drawString(xout,120,40);
 
         }
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class SrgPlot

         
          
        
