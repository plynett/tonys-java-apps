/*  A Java applet to show wave properties 
          Robert A. Dalrymple    (begun Nov 7, 1996)

*/
import java.awt.*;
import java.lang.String;
import java.lang.Math;
import Hyper;
import Refract;

public class WaveTheoryApp extends Frame  
{
   Panel  panel1, results;
   Label label1;
   TextField Hin, Tin, din, angle0;
   Button Calculate, Reset;
   Refract ref;
   int ntheory = 0;
   TextField lout, Cgout, Ksout,Krout,angleout,fin,Hshal;
   TextField nout, Cout, kout;
  
   public WaveTheoryApp()
   {
     setTitle("CACR Wave Calculator");
     setLayout(new BorderLayout());
     panel1 = new Panel();
     panel1.setLayout(new GridLayout(9,2));
     panel1.add(new Label("Deep Water Values:"));
     panel1.add(new Label(" "));
     panel1.add(new Label("Wave Height (m)?"));
     panel1.add(Hin = new TextField("     " ));
     panel1.add(new Label("Wave Period?"));
     panel1.add(Tin = new TextField("     " ));
     panel1.add(new Label("or Frequency (Hz)?"));
     panel1.add(fin = new TextField("     "));
     panel1.add(new Label("Wave Angle (o)?"));
     panel1.add(angle0= new TextField("     0  "));
     panel1.add(new Label("Local Depth?"));
     panel1.add(din = new TextField("      " ));
     panel1.add(new Label("  "));
     panel1.add(new Label("  "));
     panel1.add(new Label("  "));
     panel1.add(new Label("  "));
     panel1.add(Calculate = new Button("Calculate"));
     panel1.add(Reset = new Button("Reset"));
     add("West",panel1);
     results = new Panel();
     results.setLayout(new GridLayout(9,2));
     results.add(new Label(" L (m) ="));
     lout = new TextField("           ");
     lout.setEditable(false);
     results.add(lout);
     results.add(new Label(" k=2 pi/L = "));
     kout = new TextField("           ");
     kout.setEditable(false);
     results.add(kout);
     results.add(new Label(" C =L/T = "));
     Cout = new TextField("           ");
     Cout.setEditable(false);
     results.add(Cout);
     results.add(new Label("  Cg  ="));
     Cgout=new TextField("           ");
     Cgout.setEditable(false);
     results.add(Cgout);
     results.add(new Label("  n= Cg/C = "));
     nout= new TextField("            ");
     nout.setEditable(false);
     results.add(nout);
     results.add(new Label("  Ks = "));
     Ksout= new TextField("            ");
     Ksout.setEditable(false);
     results.add(Ksout);
     results.add(new Label("  Kr = "));
     Krout=new TextField("              ");
     Krout.setEditable(false);
     results.add(Krout);
     results.add(new Label(" Angle = "));
     angleout= new TextField("           ");
     angleout.setEditable(false);
     results.add(angleout);
     results.add(new Label("   H = "));
     Hshal= new TextField("          ");
     Hshal.setEditable(false);
     results.add(Hshal);
     add("Center",results);
      
   
   }
 public boolean handleEvent(Event e) 
   {
     
    float H0,T, d, L, klin, Angle;
    float Cg, Ks, Kr, C, n, thet0,H1 ;
    String tin;
    
    ref= new Refract();

    try {
         if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
               { H0 = Float.valueOf(Hin.getText()).floatValue();
                 tin=Tin.getText();
                 if(tin.equals("     "))
                      {T=(float)(1./Float.valueOf(fin.getText()).floatValue());
                      }
                 else 
                      {T = Float.valueOf(tin).floatValue();
                      }
                 d = Float.valueOf(din.getText()).floatValue();
                 thet0=Float.valueOf(angle0.getText()).floatValue();
                 klin =   ref.waveNumber(d, T);
                 kout.setText(String.valueOf(klin));
                 L= (float) 6.2831852/klin;
                 lout.setText(String.valueOf(L));
                 C=L/T;
                 Cout.setText(String.valueOf(C));
                 Angle = ref.theta(thet0, klin, T);
                 angleout.setText(String.valueOf(Angle));
                 Cg = ref.groupVelocity(T, d, klin);
                 n = Cg/C;
                 nout.setText(String.valueOf(n));
                 Cgout.setText(String.valueOf(Cg));
                 Ks = ref.shoalingCoef(T, Cg) ;
                 Ksout.setText(String.valueOf(Ks));
                 Kr = ref.refractionCoef(thet0, Angle );
                 Krout.setText(String.valueOf(Kr));
                 H1=Ks*Kr*H0;
                 if(H1 > (float) .8*d)
                    {H1 =(float) .8*d;
                     Hshal.setText(String.valueOf(H1)+", breaking");
                    }
                 else  
                    {
                     Hshal.setText(String.valueOf(H1));
                    }
                 
                }
              else if(e.target == Reset)
                {Hin.setText(" ");
                 Tin.setText(" ");
                 din.setText("  ");
                 angle0.setText("  ");
                 lout.setText("  ");
                 kout.setText("   ");
                 Ksout.setText("  ");
                 Krout.setText("  ");
                 kout.setText("   ");
                 nout.setText("   ");
                 Cout.setText("   ");
                 fin.setText("  ");
                 angleout.setText("  ");
                 Cgout.setText("   ");
                 Hshal.setText("    ");
                 }
               }
           
      }
    catch (NumberFormatException e1) 
     {
      System.out.println(e1);
     }

    return false;
  }

   public static void main(String args[])
     {
       Frame f = new Frame();
       f= new WaveTheoryApp();
       f.resize(500,400);
       f.show();
     }
   
}
