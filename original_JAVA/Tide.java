/* This application shows the Fourier theory of tides.
*/
import java.applet.*;
import java.awt.*;
import java.lang.*;



 public class Tide extends Applet
  {
   InputTPanel panel1;
   PlotTPlot graph1;
   TextArea ta;
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
        graph1 = new PlotTPlot();
        gbc.anchor=GridBagConstraints.CENTER;
        gblayout.setConstraints(graph1,gbc);
        add(graph1);
        gbc.gridwidth=GridBagConstraints.RELATIVE;

        TextArea ta = new TextArea(10,40);
        ta.setEditable(true);
        ta.setText("     Breakwater Harbor Delaware  \n");
        ta.insertText("    Tidal Harmonics  \n\n",ta.getText().length());
        ta.insertText("           Tide       Amplitude    Phase     Speed(o/hr) \n\n",ta.getText().length());
        ta.insertText("\t M(2) \t 2.019 \t 245.8 \t 28.984 \n",ta.getText().length());
        ta.insertText("\t N(2) \t 0.434 \t 226.3\t 28.439 \n",ta.getText().length());
        ta.insertText("\t S(2) \t 0.356 \t 265.8\t 30.000 \n",ta.getText().length());
        ta.insertText("\t K(1) \t 0.340 \t 126.5\t 15.041 \n",ta.getText().length());
        ta.insertText("\t O(1) \t 0.287 \t 119.7\t 13.943 \n",ta.getText().length());
        ta.insertText("\t SA \t 0.217 \t 150.0 \t 00.041 \n",ta.getText().length());
        ta.insertText("\t SSA \t 0.121 \t 41.6 \t 00.082 \n",ta.getText().length());
        ta.insertText("\t P(1) \t 0.114 \t 123.5 \t 14.958 \n",ta.getText().length());
        ta.insertText("\t L(2) \t 0.098 \t 268.4 \t 29.528  \n",ta.getText().length());
        ta.insertText("\t K(2) \t 0.096 \t 267.6 \t 30.082  \n",ta.getText().length());
        ta.insertText("\t NU(2) \t 0.088 \t 232.1 \t 28.512 \n",ta.getText().length());
        ta.insertText("\t M(4) \t 0.041 \t 255.6 \t 57.968  \n",ta.getText().length());
        ta.insertText("\t MU(2)\t 0.039 \t 243.8 \t 27.968  \n",ta.getText().length());
        ta.insertText("\t T(2)\t 0.036 \t 235.4 \t 29.959  \n",ta.getText().length());
//        ta.insertText("\t MN(4)\t 0.024 \t 244.1 \t 
        ta.insertText("\t J(1) \t 0.023 \t 130.0 \t 15.585  \n",ta.getText().length());
        ta.insertText("\t M(6) \t 0.023 \t 272.1 \t 86.952  \n",ta.getText().length());
        ta.insertText("\t M(1) \t 0.021 \t 123.0 \t 14.4921  \n",ta.getText().length());
        ta.insertText("\t MS(4) \t 0.015 \t 266.9 \t 58.984  \n",ta.getText().length());

        gbc.anchor=GridBagConstraints.EAST;
        gblayout.setConstraints(ta,gbc);
        add(ta);

        panel1 = new InputTPanel(graph1,ta);
        gbc.anchor=GridBagConstraints.WEST;
        gblayout.setConstraints(panel1,gbc);
        add(panel1);
      }
                             
}      //end of  Inlet

    class  InputTPanel extends Panel  
     {  
      PlotTPlot panel;
      TextField din, tin;
      TextArea ta;
      Label label1, label2, label3;
      int n, nd;
      Button Calculate, Reset;
      Choice plottype;
      boolean ptype=true;
     
  
      public InputTPanel(PlotTPlot panel, TextArea ta)
    
       {
        this.panel = panel;
        this.ta = ta;
        setLayout(new GridLayout(4,2));
        add(label1 = new Label("Number of Harmonics?"));
        add(din = new TextField(" 18 "));
        add(label2 = new Label("Days to Plot?"));
        add(tin = new TextField(" 30 "));
        plottype = new Choice();
        plottype.addItem("Sum");
        plottype.addItem("Single");
        add(plottype);
        add(Calculate = new Button("Calculate"));
        add(label3 = new Label("   "));
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
//        System.out.println(e.target+", "+e.arg+", "+e.id);
          if(e.id == Event.WINDOW_DESTROY)
             System.exit(0);
          else if(e.id ==Event.ACTION_EVENT)
            {if ( e.target.equals(plottype))
                 if("Sum".equals (e.arg) ) 
                   ptype=true;
                 else
                   ptype=false;

            else if( e.target instanceof Button ) 
              { if(e.target == Calculate)
                { 
                  n = (int) Float.valueOf(din.getText()).floatValue();//harmonics
                  nd = (int) Float.valueOf(tin.getText()).floatValue();//days
                 
                  panel.do_wave(n, nd, ta, ptype);
                  panel.repaint();
                }
              else if (e.target == Reset)
                 { 
                  din.setText("");
                  tin.setText("");
                 }
  
              }
            }
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }
       

} // end InputTPanel
    class  PlotTPlot extends Panel 
     {
       WaveTPlot waveplot;
       TextArea ta;
       int n,nd;
       Image wImage;
       Graphics wGraphics;
       boolean first = false;
     
 
       
       public PlotTPlot()
        {
          setBackground(Color.white);
         }

 
       public Dimension minimumSize()
        {
         return new Dimension(600,400);
        }
     
       public void paint(Graphics g)
        {
         if(!first)
           {
             waveplot = new WaveTPlot(bounds().width,bounds().height);
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
        public void do_wave(int n, int nd, TextArea ta, boolean ptype )
         {
          waveplot.initialize(n, nd, ta, ptype);
          waveplot.do_wave();
 
         }
    
   }//end of class PlotTPlot

     class WaveTPlot
      {
      
       int x[];
       double xx[];
       int n,nd;
       int length, totalh;
       double [] amp;
       double [] phase;
       double [] speed;
       int dep, iwave;
       double dmax;
       float famplitude;
       int amplitude;
       int offset=1;
       int nstart, nstop;
       double [] tideo;
       int [] tideoi;
       String  harm = " ";
       String  selection= " ";
       String  f = "\t";
       String Ndays=" ";
       String site=" ";
       boolean pltype;
   
    
       
       
       public WaveTPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length];
          xx = new double[length];
          tideo = new double[length];
          tideoi= new int[length];
   
        }

        public void initialize(int n, int nd, TextArea ta, boolean ptype)
         {  int i, j, ntmp, ntmp1,ntmp2,ntmp3, ntmp4,npts;
            String junk1;
            pltype=ptype;
            amp=new double [n];
            phase=new double[n];
            speed = new double[n];
            harm = ta.getText();
 
//          parse tidal components
            ntmp4=0;
            ntmp=harm.indexOf("\n");
            site=harm.substring(0,ntmp);
            for (i=0;i<n;i++)
              {
               ntmp1=harm.indexOf(f,ntmp4+1);
               ntmp2=harm.indexOf(f,ntmp1+2);
               ntmp3=harm.indexOf(f,ntmp2+2);
               ntmp4=harm.indexOf(f,ntmp3+2);
               junk1=harm.substring(ntmp1+1,ntmp2);
               amp[i]=Double.valueOf(harm.substring(ntmp2+1,ntmp3)).doubleValue();
               phase[i]=Double.valueOf(harm.substring(ntmp3,ntmp4)).doubleValue();
               speed[i]=Double.valueOf(harm.substring(ntmp4,ntmp4+8)).doubleValue();
               speed[i]=speed[i]*Math.PI/180.;
//               System.out.println("Tide: "+junk1+"; "+amp[i]+"; "+phase[i]+"; "+speed[i]);
               }
                  
//          compute the tide
            double t;
            double totalt,dt;
            if(nd > 60) nd=60;
            Ndays=nd +" days plotted";
            totalt=24.*nd;
            dt=totalt/length;
            npts=length;
            dmax=0.;
            if(pltype)
              {for (j=0;j<npts;j++)  //time 
               {t=j*dt;
                xx[j]=t;
                tideo[j]=0.0;
                for (i=0;i<n;i++)  //harmonics
                  {tideo[j]=tideo[j]+amp[i]*Math.cos(speed[i]*t+phase[i]);
                  }
                if(tideo[j]>dmax)
                    dmax=tideo[j];
                }
               }
            else
              {for (j=0;j<npts;j++)  //time 
                 {t=j*dt;
                  xx[j]=t;
                  tideo[j]=amp[n-1]*Math.cos(speed[n-1]*t+phase[n-1]);
                  }
               dmax=amp[0];
              }
            // for plotting (first find the maximum)
            dmax=1.5*dmax;
            iwave=totalh/2;
//            System.out.println("dmax; iwave: "+dmax+", "+iwave);
//            System.out.println("length= "+length);
            for(i=0;i<npts;i++)
               {
                x[i]=(int)(xx[i]*length/totalt);
//                System.out.println("x,xx "+x[i]+", "+xx[i]);
               tideoi[i]=offset+iwave-
                    (int)((tideo[i]*(totalh-offset-iwave))/(dmax)) ;
//               System.out.println(x[i]+", "+tideoi[i]);
               }
 
 
         }

         public void do_wave()
         {
         }

         public void Draw(Graphics g)
         {
          int i, j,xl,yl,npts;
          g.setColor(Color.black);
          g.drawLine(0,0,0,totalh);                 //z axis
          xl=length-1;
          yl=totalh-1;
          x[0]=1;
          g.drawLine(xl,0,xl,xl);
          g.drawLine(0,0,xl,0);

//          System.out.println("length: "+length);
          g.drawLine(0,iwave+offset,xl,iwave+offset);
          g.setColor(Color.blue);
//          g.drawLine(length-110,15,length-85,15);
          g.drawString("Tides:"+site,length/2-70,15);
          g.drawString(Ndays,length/2-5,30);
          npts=length;
          for ( i = 1; i<npts; i++)
             g.drawLine(x[i-1],tideoi[i-1],x[i],tideoi[i]);
          g.setColor(Color.black);
          g.drawLine(0,yl,xl,yl);
        
         } 
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class WaveTPlot

         
          
        
