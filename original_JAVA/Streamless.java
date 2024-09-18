/*  Stream Function Wave Theory in Java
*/

import java.awt.*;
import java.applet.*;
import java.lang.*;
import Gauss;
import Refract;
import Hyper;

public class Streamless extends Applet
   {
     public double eta[], theta[];
     StreamPanel panel1;
     PlotStPanel graph1;
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
//        Font theFont = new Font("Helvetica",Font.PLAIN,18);
//        setFont(theFont);
//        Label label1 = new Label("Stream Function Wave Theory ");
//        gbc.anchor=GridBagConstraints.NORTH;
//        gblayout.setConstraints(label1,gbc);
//        add(label1);
        graph1 = new PlotStPanel();
        gbc.anchor=GridBagConstraints.CENTER;
        gblayout.setConstraints(graph1,gbc);
        add(graph1);
        gbc.gridwidth=GridBagConstraints.RELATIVE;

        TextArea ta = new TextArea(10,20);
        ta.setEditable(false);
        gbc.anchor=GridBagConstraints.EAST;
        gblayout.setConstraints(ta,gbc);
        add(ta);

        panel1 = new StreamPanel(graph1,ta);
        gbc.anchor=GridBagConstraints.WEST;
        gblayout.setConstraints(panel1,gbc);
        add(panel1);
       }
}
     class  StreamPanel extends Panel  
     {  
      PlotStPanel panel1;
//      DataoutPanel panel2;
      TextArea ta;
      TextField Hin, Tin, din, NNin, dampin;
      Refract ref;
      double H,T, d, L, damp;
      public static int jstep = 0;
      Button Calculate, Reset;
      public boolean stop = false;
      int NN,NTHTS=41;
  
//     public StreamPanel(PlotStPanel panel1, DataoutPanel panel2)

       public StreamPanel(PlotStPanel panel1, TextArea ta)

       {
        this.panel1 = panel1;
        this.ta = ta;
        setLayout(new GridLayout(7,2));
        add(new Label("Input Wave Data:"));
        add(new Label(" "));
        add(new Label("Wave Height (m)?"));
        add(Hin = new TextField("" ));
        add(new Label("Wave Period?"));
        add(Tin = new TextField("" ));
        add(new Label("Local Depth?"));
        add(din = new TextField("" ));
        add(new Label("Theory Order?"));
        add(NNin =new TextField(""));
        add(new Label("Damping"));
        add(dampin = new TextField(".3"));
        add(Calculate = new Button("Calculate"));
        add(Reset = new Button("Reset"));
       }

    public Insets insets()
          {
            return new Insets(10,10,10,10);
          }
    public boolean handleEvent(Event e) 
     {
      Stream1 stream;
      Wave wave;
      Streamout output;
      
    
      ref= new Refract();
      
    
      try 
         {
          if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
               { H = Double.valueOf(Hin.getText()).doubleValue();
                 T = Double.valueOf(Tin.getText()).doubleValue();
                 d = Double.valueOf(din.getText()).doubleValue();
                 damp=Double.valueOf(dampin.getText()).doubleValue();
                 NN =(int) (Float.valueOf(NNin.getText()).doubleValue());   
                 ta.setText("Computing....  please wait");
                 Hin.setEditable(false);
                 Tin.setEditable(false);
                 din.setEditable(false);
                 NNin.setEditable(false);
                 dampin.setEditable(false);

                 wave= new Wave(H, T ,d);
                 output= new Streamout(NN, NTHTS);
                 output.damp= damp;
                 stream= new Stream1();
                 stream.Stream1 (output);
                 Wave.L=output.X[0];
                 Wave.etac=output.eta[0];
                 panel1.do_wave(output);
//                 panel2.initialize(output);
                 getCoefsforta(output);
                 
                }
            else if (e.target == Reset)
               { stop = true;
                 Hin.setEditable(true);
                 Tin.setEditable(true);
                 din.setEditable(true);
                 NNin.setEditable(true);
                 dampin.setEditable(true);
                 return true;
               }
            }
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }

      return false;
     }
  
     public void getCoefsforta(Streamout output) 
      {
       
        double L,eta,theta;
        ta.setText("    \n");
        ta.insertText("     Stream Function Coefficients: \n",ta.getText().length());
        ta.insertText("     ----------------------\n",ta.getText().length());
        ta.insertText("    \n",ta.getText().length());
        for(int i = 0; i<(output.NN+2); i++)
          {String coef= String.valueOf(i);
           while(coef.length() <15)
              coef+=" ";

           coef += String.valueOf(output.X[i])+"\n";
           ta.insertText(coef,ta.getText().length());
          }
        ta.insertText("    \n",ta.getText().length());
        ta.insertText("     Errors: \n",ta.getText().length());
        ta.insertText("     ------ \n",ta.getText().length());
        ta.insertText("   \n",ta.getText().length());
        ta.insertText("Wave Height Error:     "+output.errorH+" m\n",ta.getText().length());
        ta.insertText("Mean Sea Level Error: "+output.msl+" m\n",ta.getText().length());
        ta.insertText("Bernoulli RMS Error:   "+output.errorQ+" m\n",ta.getText().length());
        ta.insertText("   \n",ta.getText().length());
        ta.insertText("   \n",ta.getText().length());
        ta.insertText("    Free Surface Displacement\n",ta.getText().length());
        ta.insertText("    --------------------\n",ta.getText().length());
        ta.insertText("   \n",ta.getText().length());
        ta.insertText("x (m)     Theta (radians)   Eta(theta) \n",ta.getText().length()); 
        ta.insertText("---------------------\n",ta.getText().length());
        ta.insertText(" \n",ta.getText().length());
        for(int i = 0; i<output.NTHTS;i++)
           {L=output.X[0];
            theta = output.theta[i];
            eta = output.eta[i];
            String coef = String.valueOf( (float)(((int)(theta*L*100./(2.*Math.PI)))/100.));
            while(coef.length() <12)
              coef+=" ";

            coef += String.valueOf( (float)(((int)(output.theta[i]*100.))/100.));
            while(coef.length() <30)
              coef+=" ";

           coef += String.valueOf((float)(((int)(output.eta[i]*100.))/100.))+"\n";
           ta.insertText(coef,ta.getText().length());
           }
 
        ta.setEditable(false);
      }

} // end StreamPanel
              
   
    class  PlotStPanel extends Panel  
     {
       StreamPlot waveplot;
     
       Image wImage;
       Graphics wGraphics;
       public float H, d, L, T;
       boolean first = false,   stop=false;
       
       Streamout output;
       int totalh,length;
       
       public PlotStPanel()
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
            waveplot = new StreamPlot(bounds().width,bounds().height);
            totalh=bounds().height;
            length=bounds().width;
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
        
        public void do_wave(Streamout output)
         {
          waveplot.initialize(output);
          waveplot.do_wave(output);
          repaint();
  
         }
 
    public boolean handleEvent(Event e) 
     {
      double xx,zp,zz,L,etac,d;
      int x,z,amplitude;
      try 
         {
            if( e.id == Event.MOUSE_DOWN)
           {
            x=e.x;
            z=e.y;
            int offset=5;
            //  calculate position in x,z
            L=Wave.L;
            etac=Wave.etac;
            d=Wave.d;
            xx=(double)(x*L/(2.*length));
            amplitude = (int)( etac/(d+etac)*(totalh-offset));
            zp=(double) (d*(offset+amplitude-z));
            zz= (double)( zp /(totalh-offset-amplitude));
            waveplot.veloc(x,z,xx,zz);
            repaint();
            return true;
           } 
         }
      catch (NumberFormatException e1) 
         {
          System.out.println(e1);
          }
      return false;
     }

   }//end of class PlotStPanel

     class StreamPlot
      {
       public int length, amplitude, totalh;
       int x[],y[];
       public float H, d, T,L;
       double LL;
        float dz ;
       double dt;
       int dep;
       float famplitude;
       int ncount=12;
       String waveLength = " ";
       String Us = " ";
       String Vs = " ";
       Color click;
 
       double [] theta;
       double [] eta;
       double [] X;
       double u,v;
       int xp = 0;
       int zp = 0;;
 
 
       int offset=5;
       int Depth, temp;
       int NN,NTHTS;
       
       
        public  StreamPlot(int width, int height)
        {
          length = width;
          totalh  = height;
          x = new int[length+2];
          y = new int[length+2];  
          click=Color.white;
    
          

          for (int i = 0; i<length;i++)
             { 
              x[i]=0;          
             }
      
        }
        public void initialize( Streamout output )
         { 
            double dth,etac;
            int i;
            NTHTS=output.NTHTS;
            eta= new double[NTHTS+2];
            theta = new double[NTHTS+2];
            d=(float)Wave.d;
            NN=output.NN;
            X = new double[NN+2];
            System.arraycopy(output.X,0,X,0,NN+2);
            LL= X[0];
            L=(float) LL;
            L=(float)(((int)(L*100.))/100.);
            for(i=0;i<NTHTS;i++)
               {eta[i]= output.eta[i];
                theta[i]=output.theta[i];
               }
            for(i=0;i<NTHTS;i++)
               y[i]=(int)(theta[i]*length/Math.PI);
            etac=eta[0];
            H=(float) (etac -eta[NTHTS-1]);
            famplitude = (float)( etac/(d+etac)*(totalh-offset));
            amplitude = (int) famplitude;
            for(i=0;i<NTHTS;i++)
               {x[i]=amplitude+offset-(int)(famplitude*eta[i]/etac);
               }
            dz=(totalh-amplitude-offset)/(ncount-1);
            if(H>0.) 
               click=Color.black;

         }
        public void do_wave(Streamout output)
         {  
            int j;
//             for(j=0;j<NTHTS;j++);
//               x[j]=amplitude-(int)(famplitude* output.eta[j]);
              waveLength= "L = "+L+" m";

          
            }
         public void veloc(int x, int z, double xx, double zz)
          {
           double  zn,xn;
           xp=x;
           zp=z;
           u=0.0;
           v=0.0;
           for(int n=1; n<NN+1;n++)
             {zn=2.*n*Math.PI*(d+zz)/X[0];
              xn=2.*n*Math.PI/X[0];
              u -= xn*X[n]*Hyper.cosh(zn)*Math.cos(xn*xx);
              v -= xn*X[n]*Hyper.sinh(zn)*Math.sin(xn*xx);
             }
           if(Math.abs(u)> 1.) 
               u=(double)(((int)(u*100.))/100.);
           else
               u=(double)(((int)(u*1000.))/1000.);

           if(Math.abs(v)>1.)
               v=(double)(((int)(v*100.))/100.);
           else
               v=(double)(((int)(v*1000.))/1000.);

           Us="U = "+u;
           Vs="V = "+v;
           
          }
           

         public void Draw(Graphics g)
         {
          int xl,yl,xpp,zpp;
          g.setColor(Color.blue);
          x[NTHTS]=totalh;
          x[NTHTS+1]=totalh;
          y[NTHTS]=length;
          y[NTHTS+1]=0;
          g.fillPolygon(y,x,NTHTS+2);
          // print wave length;
          xl=length-150;
          yl=totalh-25;
          g.setColor(Color.white);
          g.drawString( waveLength,xl,yl);
          // show L/4 and L/2
          g.setColor(Color.black);
          xl=length/2-10;
          yl=amplitude+offset-7;
          g.drawString("L/4",xl,yl);
          xl=length-20;
          g.drawString("L/2",xl,yl);
          //axes
          g.drawLine(0,amplitude+offset,length,amplitude+offset); //x axis
          g.drawLine(0,0,0,totalh);                 //z axis
          // box the rest of the plot
          xl=length-1;
          yl=totalh-1;
          g.drawLine(0,yl,xl,yl);  //bottom line
          g.drawLine(xl,0,xl,xl);  //right side
//          g.drawLine(0,0,xl,0);    //top
           g.drawLine(xl/2,amplitude+2*offset,xl/2,amplitude);
          g.setColor(click);
          g.drawString("Click for velocities",length-260,20);

          // plot velocities
          g.setColor(Color.white);
          g.fillOval(xp,zp,4,4);
          g.setColor(Color.lightGray);
          xpp=xp;
          zpp=zp;
          if(length-xp <70)
               xpp=xp-70;
          if(totalh-zp <13)
               zpp=zp-20;
          else if (zp<13)
               zpp=zp+20;
          g.drawString(Us,xpp+6,zpp);
          g.drawString(Vs,xpp+6,zpp+13);
          }
        public void update(Graphics g)
         {
          Draw(g);  
         }
      }//end of class StreamPlot------------------------------------------

 
