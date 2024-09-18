/* This application determines the equilibrium beach profile 


    Other required classes: Bathymetry, Wave 

              R. Dalrymple Jan 1997
*/
import java.awt.*;
import java.lang.*;



 public class BeachApp extends Frame
  {
   InputBPanel panel1;
   PlotBPlot graph1;

   public BeachApp()
  
     { setTitle("Beach Fill Calculator");
       setLayout(new BorderLayout());
       Label label1 = new Label(" ");
       add("North",label1);  //spacer
       graph1 = new PlotBPlot();
       add("Center",graph1);

       panel1 = new InputBPanel(graph1);
       add("West",panel1);
     }
   public static void main(String args[])
     {
       Frame f = new Frame();
       f= new BeachApp();
       f.resize(650,350);
       f.show();
     }
                            
}      //end of  BeachApp

    class  InputBPanel extends Panel  
     {  
      PlotBPlot panel;
      TextField hin, Bin, din, dfin, Win;
      Bathymetry bathn, bathf;
      double AN,AF, B,h,d,df, W;
      Button Calculate, Reset;
      Label label1, label2, label3, label4, label5, label6;
      Wave wave;
      double xmaxn,xmaxf,yi, hi, volume,xmax;
      double [] hn;
      double [] hf;
      double x,slope,dx;
      int n;

  
      public InputBPanel(PlotBPlot panel)
    
       {
        this.panel = panel;
        setLayout(new GridLayout(7,2));
        add(label1 = new Label("Input Profile/Sand Data:"));
        add(new Label(" "));
        add(label2 = new Label("Native Grain size (mm)?"));
        add(din = new TextField("     " ));
        din.setText(" .3 ");
        add(label5 = new Label("Fill Grain size (mm)?"));
        add(dfin = new TextField("     " ));
        dfin.setText(" .4 ");
        add(label6 = new Label("Final Fill Width(m)?"));
        add(Win = new TextField("    "));
        Win.setText("50.0");
        add(label3 =new Label("Berm Height(m)?"));
        add(Bin = new TextField("      " ));
        Bin.setText(" 1");
        add(label4 = new Label("Depth of Closure (m)?"));
        add(hin=new TextField("      "));
        hin.setText("6.0");
        add(Calculate = new Button("Calculate"));
        add(Reset = new Button("Reset"));
       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
     
       try 
         {
          if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
                { 
                  B = Double.valueOf(Bin.getText()).doubleValue();//Berm
                  wave.T=B;
                  h=Double.valueOf(hin.getText()).doubleValue(); //h_c
                  dx=xmax/(n-1);
                  d = Double.valueOf(din.getText()).doubleValue();//d
//                  System.out.println("d = "+d);
                  df = Double.valueOf(dfin.getText()).doubleValue();//df
                  W = Double.valueOf(Win.getText()).doubleValue();//fill width
                  AN=Acalc(d); 
                  AF=Acalc(df); 
                  n=100;
                  hn=new double[n];
                  hf= new double [n];
                  xmaxn=Math.pow(h/AN,1.5);
                  xmaxf=W+Math.pow(h/AF,1.5);
                  if(AF>AN)  //Coarser fill material
                    {System.out.println("AF>AN");
                     yi=W/(1-Math.pow((AN/AF),1.5));
                     hi=AN*Math.pow(yi,(2./3.));
                     if(hi<h)  //Intersecting Profile
                       {dx=xmaxn/(n-1);
                        xmax=xmaxn;
                        volume=B*W+0.6*AN*Math.pow(W,(5./3.))/Math.pow((1-Math.pow((AN/AF),1.5)),(2./3.));
                        volume=(double)((int)volume);//truncate to whole numbers
                        System.out.println("yi, hi: "+yi+", "+hi);
                        for(int i=0;i<n;i++)
                         {x=i*dx;
                          hn[i]=AN*Math.pow(x,0.6666);
                          if(x<W)
                            hf[i]=-B;
                          else
                            hf[i]=AF*Math.pow((x-W),0.6666);
                         }//end for loop
                       } // end intersecting profile
                     else  //non-intersecting profile but AF>AN
                      {dx=xmaxf/(n-1);
                       xmax=xmaxf;
                       volume=B*W+0.6*h*xmaxn*(Math.pow( ((W/xmaxn)+Math.pow((AN/AF),1.5)),(5./3.))-Math.pow((AN/AF),1.5));
                       volume=(double)((int)volume);//truncate to whole numbers
                       for(int i=0;i<n;i++)
                        {x=i*dx;
                         hn[i]=AN*Math.pow(x,0.6666);
                         if(x<W)
                          hf[i]=-B;
                         else
                          hf[i]=AF*Math.pow((x-W),0.6666);
                          System.out.println("x,hn,hf: "+x+", "+hn[i]+", "+hf[i]);
                        } //end for loop
            
                       }  // end else loop
                    }// end if for AF>AN
                  else if(AF==AN)
                    {System.out.println("AF=AN");
                     volume=W*(B+h);
                     volume=(double)((int)volume);//truncate to whole numbers
                     xmax=xmaxn+W;
                     dx=xmax/(n-1);
                     for(int i=0;i<n;i++)
                        {x=i*dx;
                         hn[i]=AN*Math.pow(x,0.6666);
                         if(x<W)
                          hf[i]=-B;
                         else
                          hf[i]=AF*Math.pow((x-W),0.6666);
                          
                        } //end for loop
 
                    } //end AF=AN if
                                     
                  bathn = new Bathymetry();
                  AN=(double)((int)(100.*AN))/100.;
                  AF=(double)((int)(100.*AF))/100.;
                 
                  bathn.Bathymetry(n,dx,hn,AN);
                  bathf = new Bathymetry();
                  bathf.Bathymetry(n,dx,hf,AF);
                   
//                  System.out.println("bathy copied; n= "+n);
                          
                  panel.do_wave( bathn, bathf, xmax, volume);
                  panel.repaint();
                }
              else if (e.target == Reset)
                 { 
                  Bin.setText("     ");
                  hin.setText("     ");
                  din.setText("     ");
                  Win.setText("     ");
                  dfin.setText("     ");
                 }
  
            }
          
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }
    public double Acalc(double d)
      {
       double A;
   
       A=0.0165*Math.pow(d,3)-0.2118*Math.pow(d,2)+0.5028*d-0.0008;
       return A;
     }


} // end InputBPanel
    class  PlotBPlot extends Panel 
     {
       WaveBPlot waveplot;
       Bathymetry bathn, bathf;
       Image wImage;
       double AN,AF, volume;
       Graphics wGraphics;
       boolean first = false;
     
 
       
       public PlotBPlot()
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
             waveplot = new WaveBPlot(bounds().width,bounds().height);
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
        public void do_wave( Bathymetry bathn, Bathymetry bathf, double xmax, double volume)
         {
          waveplot.initialize(bathn, bathf, xmax, volume);
          waveplot.do_wave();
 
         }
    
   }//end of class PlotBPlot

     class WaveBPlot
      {
       public int length, amplitude, totalh, del;
       int x[];
       int npts;
       public double d, T;
       float dz ;
       double dt;
       double dx,xmax;
       double AN,AF;
       int dep;
       float famplitude;
       int ncount=12;
        public int jstep=0;
       int offset=5;
       int [] depth;
       int [] depthf;
       double [] depn;
       double [] depf;
       int temp;
       Bathymetry bathn, bathf;
       String ANvalue=" ";
       String AFvalue=" ";
       String Xmax=" ";
       String Dmax=" ";
       String Vol=" ";
       double volume;
       int iwave;
  
       double [] xx;
       
       
       public WaveBPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length];
          xx = new double[length];
  
          depth = new int[length];
          depthf= new int[length];
          depn = new double[length];
          depf = new double[length];
         
          
        
        }
        public void initialize( Bathymetry bathn, Bathymetry bathf, double xm, double volume)
         {  int i,n;
            double dmax;
            npts=bathn.NPTS;
//             System.out.println("npts: "+npts);
            dx=bathn.DX;
//            System.out.println("dx: "+dx);
            AN=bathn.slope;
            AF=bathf.slope;
            ANvalue="A_N= "+AN;
            AFvalue="A_F= "+AF;
            Vol="Volume = "+volume+" m^3/m";
            xmax=xm;
            Xmax="x_max= "+xmax;
              for(i=0;i<npts;i++)
               {depn[i]=bathn.h[i];
                depf[i]=bathf.h[i];
                xx[i]=i*dx;
//                 System.out.println("x,depn, depf: "+xx[i]+", "+depn[i]+", "+depf[i]);
               }
            // for plotting
            dmax=depn[npts-1];
            Dmax="h_max= "+dmax;
            iwave=totalh/3;
            del=offset+iwave;
            for(i=0;i<npts;i++)
               {x[i]=(int)(xx[i]*length/xmax);
               depth[i]=offset+iwave+
                    (int)((depn[i]*(totalh-offset-iwave))/(dmax)) ;
               depthf[i]=offset+iwave+
                    (int)((depf[i]*(totalh-offset-iwave))/(dmax)) ;
//               System.out.println(x[i]+", "+depth[i]);
               }
 
         }
         public void do_wave()
         {
         }

         public void Draw(Graphics g)
         {
          int i, j,xl,yl;
          Color brown;
          g.setColor(Color.blue);
//          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.setColor(Color.black);
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          x[0]=1;
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);
          g.drawLine(0,iwave+offset,xl,iwave+offset);
          g.setColor(Color.blue);
          g.fillRect(1,iwave+offset,xl,yl);
          brown = new Color(214,164,96);
          depthf[npts]=totalh-1;
          depthf[npts+1]=totalh-1;
          g.setColor(Color.magenta);
          g.fillPolygon(x,depthf,npts+2);
          depth[npts]=totalh;
          depth[npts+1]=totalh;
          x[npts]=length-1;
          x[npts+1]=1;
          g.setColor(brown);
          g.fillPolygon(x,depth,npts+2);
          g.setColor(Color.black);
          g.drawString(ANvalue,length/10,20);
          g.drawString(AFvalue,length/2,20);
          g.drawString(Xmax,length/10,35);
          g.drawString(Vol,length/2,35);
//          g.drawString(Dmax,length/10,50);
          g.setColor(Color.black);
          g.drawLine(0,yl,xl,yl);
         
         } 
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class WaveBPlot

         
          
        
