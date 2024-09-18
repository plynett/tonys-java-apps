/*  A Java applet to compute sediment transport properties 

*/
import java.applet.Applet;
import java.awt.*;
import java.lang.String;
import java.lang.Math;
import Hyper;
import Refract;

public class Sand extends Applet  
{
   Panel  panel1, results;
   Label label1;
   TextField Hin, Tin, Kin, Bin, hstarin, angle0;
   Button Calculate, Reset;
   Refract ref;
   int ntheory = 0;
   TextField qout, Gout,qyout;
   String Hbreak;
  
   public void init()
   {
     
     resize( 500,300);
     setLayout(new BorderLayout());
     panel1 = new Panel();
     panel1.setLayout(new GridLayout(7,2));
     panel1.add(new Label(" Input Values:"));
     panel1.add(new Label(" "));
     Hbreak="Breaker Height";
     panel1.add(new Label(Hbreak));
     panel1.add(Hin = new TextField("0.5" ));
//     panel1.add(new Label("Wave Period?"));
//     panel1.add(Tin = new TextField("6.0" ));
     panel1.add(new Label("Breaking Angle (o)?"));
     panel1.add(angle0= new TextField("10.0"));
     panel1.add(new Label(" K ?"));
     panel1.add(Kin = new TextField(".7" ));
     panel1.add(new Label(" Berm height, B? "));
     panel1.add(Bin= new TextField("1.0"));
     panel1.add(new Label(" Depth of closure?"));
     panel1.add(hstarin= new TextField("6.0"));
     panel1.add(Calculate = new Button("Calculate"));
     panel1.add(Reset = new Button("Reset"));
     add("West",panel1);

     results = new Panel();
     results.setLayout(new GridLayout(7,2));
     results.add(new Label("  Output:"));
     results.add(new Label("   "));
     results.add(new Label(" Q (m3/sec) ="));
     qout = new TextField("           ");
     qout.setEditable(false);
     results.add(qout);
     results.add(new Label("   "));
     results.add(new Label("   "));
     results.add(new Label(" Q (m3/yr) ="));
     qyout = new TextField("           ");
     qyout.setEditable(false);
     results.add(qyout);
     results.add(new Label("   "));
     results.add(new Label("   "));
     results.add(new Label(" G (m2/sec) ="));
     Gout=new TextField("          ");
     Gout.setEditable(false);
     results.add(Gout);
     results.add(new Label("   "));
     results.add(new Label("   "));
     add("Center",results);
      
   
   }
 public boolean handleEvent(Event e) 
   {
     
    double Hb,T, K, hstar, Angle;
    double  db, Eb,thetb,Cb, B,Cq,Q;
    double g=9.81;
    double G;
    double rho = 1000.;
    double s=2.65;
    double p=.3;
    double kappa=.78;
    

    try {
         if( (e.target instanceof Button) && (e.id == Event.ACTION_EVENT)) 
           { if(e.target == Calculate)
               { Hb = Double.valueOf(Hin.getText()).doubleValue();
//                 T = Double.valueOf(Tin.getText()).doubleValue();
                 K = Double.valueOf(Kin.getText()).doubleValue();
                 thetb = Double.valueOf(angle0.getText()).doubleValue();
                 thetb = Math.PI*thetb/180.;
                 B = Double.valueOf(Bin.getText()).doubleValue(); 
                 hstar = Double.valueOf(hstarin.getText()).doubleValue();  
                 db=Hb/kappa;
                 Eb=rho*g*Hb*Hb/8.;
                 Cb=Math.sqrt(g*db);
                 Cq= K *Eb *Cb /(rho*g*(s-1.)*(1.-p)) ;
                 Q=Cq*Math.cos(thetb)*Math.sin(thetb);
                 Q=((int)(Q*1000.0))/1000.;
                 qout.setText(String.valueOf(Q));
                 double Qy;
                 Qy=Q*31536000;
                 Qy=  ((int)(Qy*100.))/100.0;
                 qyout.setText(String.valueOf(Qy));
                 G=2.*Cq*Math.cos(2.*thetb)/(hstar+B);
                 G= ((int) (G*1000.0))/1000.0;
                 Gout.setText(String.valueOf(G));
                 
                  
                }
              else if(e.target == Reset)
                {Hin.setText("");
//                 Tin.setText("");
                 Kin.setText("");
                 angle0.setText("");
                 qout.setText("");
                 qyout.setText("");
                 Gout.setText("");
                 Bin.setText("");
                 hstarin.setText("");
                 }
               }
           
      }
    catch (NumberFormatException e1) 
     {
      System.out.println(e1);
     }

    return false;
  }
}
