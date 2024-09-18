/*  A Java applet to compute the depth of closure
          Robert A. Dalrymple    (April 21, 1996)

*/
import java.applet.Applet;
import java.awt.*;
import java.lang.String;
import java.lang.Math;
import Hyper;


public class Depth extends Applet  
{
   Panel  panel1, results;
   Label label1;
   TextField Hin, Tin, din;
   Button Calculate, Reset;
   TextField dcout;
   Choice wave;
   int wavetype=1;

   public void init()
   {
     
     resize( 500,300);
     setLayout(new BorderLayout());
     panel1 = new Panel();
     panel1.setLayout(new GridLayout(6,2));
     panel1.add(new Label(" Input Values:"));
     panel1.add(new Label(" "));
     wave=new Choice();
     wave.addItem("Breaker Height (m)?");
     wave.addItem("Offshore Height (m)?");
     wave.addItem("H_e (m)?");
     panel1.add(wave);
     panel1.add(Hin = new TextField("1." ));
     panel1.add(new Label("Wave Period?"));
     panel1.add(Tin = new TextField("8." ));

     panel1.add(new Label("Sand Specific Gravity?"));
     panel1.add(din = new TextField("2.65" ));
     panel1.add(new Label(" Depth of Closure (m)"));
     panel1.add(dcout= new TextField(""));
     dcout.setEditable(false);
     panel1.add(Calculate = new Button("Calculate"));
     panel1.add(Reset = new Button("Reset"));
     add("Center",panel1);
       
   
   }
 public boolean handleEvent(Event e) 
   {
     
    double Hb,T, hstar;
  
    double g=9.81;
    double k=0.0;
    double A=0.0;
    double G,d,HoL,db;
    double rho = 1000.;
    double s=2.65;
    double p=.3;
    double kappa=.78;
    double sigma,xkh,th,sh,ch,fprime,f,dxkh,Lo,brac;
    double dc=0.0;
    String depthc;


    try {
         if(e.id == Event.ACTION_EVENT)
          {   //open action_event
           if(e.target.equals(wave))
               {
                if ("Breaker Height (m)?".equals (e.arg) )
                   {wavetype= 1;
//                     System.out.println(wavetype);
                   }
                else if("Offshore Height (m)?".equals (e.arg) )
                   {wavetype= 2;
//                     System.out.println(wavetype);
                   }
                else 
                   {wavetype= 3;
//                   System.out.println(wavetype);
                   }
              }
           if(e.target instanceof Button)  
            {  //open button
              if(e.target == Calculate)
               { //open calculate
                 Hb = Double.valueOf(Hin.getText()).doubleValue();
                 T = Double.valueOf(Tin.getText()).doubleValue();
                 sigma=2.*Math.PI/T;
                 d = Double.valueOf(din.getText()).doubleValue();
                 if(wavetype != 3)
                  {
                   if(wavetype == 1)
                     {
                      A=Math.pow(Hb/2.,2)*Math.pow(sigma,4);
                      A=A/(.03*g*g*(d-1.));
                     }
                   else if(wavetype == 2)
                     {Lo=g*T*T/(2.*Math.PI);
                      A=329*Hb*Hb/((d-1)*Lo*Lo);
//                    System.out.println("offshore data");
                      }
                   else if (wavetype == 3)
                  
                  System.out.println("A= "+A);
                   k=2.*Math.PI/(Math.sqrt(g*Hb)*T);
                   xkh=k*Hb/kappa;
                   do {              
                   th=Hyper.tanh(xkh);
                   sh=Hyper.sinh(xkh);
                   ch=Hyper.cosh(xkh);
                   if(wavetype == 1)
                     {
                      f=xkh*sh*sh*th-A;
                      fprime= sh*sh*th+2.* xkh*(sh*sh+th*th);
                     }
                   else
                     {
                      brac=(1.+2.*xkh/(2.*sh*ch));
                      f=xkh*sh*sh*th*th*brac-A;
                      fprime=sh*sh*th*th*brac+2.*xkh*th*th*(sh*ch)*brac;
                      fprime=fprime+xkh*th*th*th*(2.*brac+1);
                      fprime=fprime-xkh*xkh*th*th*(2.*ch*ch-1)/(ch*ch);
//                    System.out.println(xkh+", "+f+", "+fprime);
                      }
                   dxkh= -f/fprime;
                   xkh=xkh+dxkh;
                   } while (Math.abs(dxkh/xkh) > .00000001);
//                   System.out.println("f, kh:  "+f+", "+xkh);
                   dc=(g/(sigma*sigma))*xkh*Hyper.tanh(xkh);
                   dcout.setText(String.valueOf(dc));
                  } //close wavetype !=3
                  else if (wavetype ==3)
                    {
                     HoL=Hb*Hb/(g*T*T);
                     dc=2.28*Hb - 68.5*HoL;
                     db=1.75*Hb-57.9*HoL;
                     depthc="d_H = "+String.valueOf(dc)+", d_B = "+String.valueOf(db);
                     dcout.setText(depthc);
                    }
                 }  //close calculate
              else if(e.target == Reset)
                 {Hin.setText("");
                  Tin.setText("");
                  dcout.setText("");
                 }
               }//end button
         }  //end action_event
      } //end try
    catch (NumberFormatException e1) 
     {
      System.out.println(e1);
     }

    return false;
  }
}
