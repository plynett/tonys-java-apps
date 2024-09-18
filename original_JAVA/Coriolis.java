/*
 * Coriolis.java
 *
 * Created on March 10, 2001, 12:40 AM
 */


/**
 *
 * @author  *
 * @version 
 */

import java.util.*;
import java.awt.*;
import javax.swing.*;
/* import com.bruceeckel.swing.*; */

class Coord {
   Coord(double xx, double yy){
       x=xx; y=yy;
   }
   double x,y;
}

class DrawTrajectory extends JPanel {
   DrawTrajectory(Coriolis cor) {
       coriolis=cor;
       maxW_half=maxWidth/2;
       RecomputeCoords();
       // repaint();
   }
   
   public void RestoreDefaults() {
       r=500;
       vertvel=7;
       vert_angle=0;
       RecomputeCoords();
   }
   
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       g.setColor(Color.white);
       g.drawLine(maxW_half-5,(int)(maxHeight-3-scale),
                   maxW_half+5,(int)(maxHeight-3-scale));
       int i;
       for(i=0;i<ct.size()-1;i++)
           g.drawLine((int)(plotarray[i].x),(int)(plotarray[i].y), 
                      (int)(plotarray[i+1].x),(int)(plotarray[i+1].y));
       g.setColor(Color.red);
       g.drawArc((int)(maxW_half-r*scale),
           (int)(maxHeight-3-2*r*scale),
            (int)(2*r*scale),(int)(2*r*scale),0,360);
       g.drawLine(maxW_half,0,maxW_half,maxHeight-1);
       g.drawLine(0,(int)(maxHeight-3-r*scale),maxWidth,
                (int)(maxHeight-3-r*scale));

   }

   
   public void ModVelocity(int newvel) {
       vertvel=newvel;
       RecomputeCoords();
   }
   
   public void ModAngle(int newangle) {
       vert_angle=newangle;
       RecomputeCoords();
   }
 
   public void ModRadius(int newradius) {
       r=newradius;
       RecomputeCoords();
   }
 
   
   void RecomputeCoords() {
      ct.clear();   // clear old coords  
      // set time step
      double dt = 0.01;
      // compute required rotational velocity (to maintain Earth "gravity")
      double omega = Math.sqrt(9.81/r);  

      double abx=0,aby=0; // coords before rotation
      double abpx=0,abpy=0;   // final coordinates of the moving object ("primes").
 
      // set auxiliary variables;
      double alpha=0,ax=0,ay=0,bx=0,by=1.0;

      // set initial time;
      double t=0;
      double cosa,sina;

      double vertvelx = vertvel*Math.sin((double)vert_angle*Math.PI/180);
      double vertvely = vertvel*Math.cos((double)vert_angle*Math.PI/180);
      double diffx,diffy;
      do {
         alpha=omega*t;    // rotation angle
         cosa=Math.cos(alpha);
         sina=Math.sin(alpha);
         ax=r*sina; ay=r*(1-cosa);  // this is our point on the circle
      
         bx=alpha*(r-1.0)+vertvelx*t;  // omega*r*t;
         by=vertvely*t+1.0;  
    
         abx=bx-ax; aby=by-ay;
 
         // now rotate vector ab to be in the rotational frome of reference;
         abpx=abx*cosa+aby*sina;
         abpy=aby*cosa-abx*sina;
     
         // save coordinates to the array
         ct.add(new Coord(abpx,abpy));   
         diffx=abpx; diffy=abpy-r;
         // increase time
         t+=dt;
      } while (Math.sqrt(diffx*diffx+diffy*diffy)<=r);  
                    // while object inside the station
      FillDrawingArray();
      repaint();
     
   } 


   void FindMaxMin() {
      int i;
      xmax=0;ymax=0;
      for(i=0;i<ct.size();i++)
        {
           if(Math.abs(((Coord)ct.get(i)).x)>xmax)
             xmax=Math.abs(((Coord)ct.get(i)).x);
           if(((Coord)ct.get(i)).y>ymax)
             ymax=((Coord)ct.get(i)).y;      
        }
      coriolis.UpdateMaxes(xmax,ymax);
      
   //  cout<<"Max height: "<<ymax<<endl;
  //   cout<<"Max distance: "<<xmax<<endl;
   }

   void FillDrawingArray() {
      FindMaxMin();
      plotarray = new Coord[ct.size()];
      double a=ymax;
      scale = (maxHeight-5)/a;
      if(2*xmax>ymax) {
          a=2*xmax;
          scale = (maxWidth-5)/a;
      }
    
       int i;
       for(i=0;i<ct.size();i++)
          {
             plotarray[i] = new Coord(0,0);  // important in Java!!
             plotarray[i].x = maxW_half+((Coord)ct.get(i)).x*scale;
             plotarray[i].y = maxHeight-3-((Coord)ct.get(i)).y*scale; 
          }
      repaint();   
   }     
   
   private ArrayList ct = new ArrayList();  // for double coords
   private int maxWidth=400, maxHeight=400;
   private int maxW_half;
   int		   	vertvel=7;
   int			r=500;      // station radius
   double		xmax,ymax;  // for simulation
   int 		        vert_angle=0;  // angle of throw; 0=vertical;
   Coord[]		plotarray;   // to be plotted
   double               scale;
   Coriolis             coriolis;  // for communication woth coriolis

}

public class Coriolis extends javax.swing.JApplet {

    /** Creates new form Coriolis */
    public Coriolis() {
        initComponents ();
        
        drawtr = new DrawTrajectory(this);
        drawtr.setBorder(new javax.swing.border.EtchedBorder());
        drawtr.setBackground(java.awt.Color.black);
        getContentPane().add(drawtr);
        drawtr.setBounds(10, 10, 400, 400);
     }

     public void UpdateMaxes(double maxx, double maxy) {
         jTextField4.setText(""+maxx);
         jTextField5.setText(""+maxy);
     }
     /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
     private void initComponents() {//GEN-BEGIN:initComponents
         jSlider1 = new javax.swing.JSlider();
         jSlider2 = new javax.swing.JSlider();
         jSlider3 = new javax.swing.JSlider();
         jTextField1 = new javax.swing.JTextField();
         jTextField2 = new javax.swing.JTextField();
         jTextField3 = new javax.swing.JTextField();
         jLabel1 = new javax.swing.JLabel();
         jLabel2 = new javax.swing.JLabel();
         jTextField4 = new javax.swing.JTextField();
         jTextField5 = new javax.swing.JTextField();
         jButton1 = new javax.swing.JButton();
         getContentPane().setLayout(null);
         
         jSlider1.setBorder(new javax.swing.border.TitledBorder("Vel (m/s)"));
         jSlider1.setMinorTickSpacing(10);
         jSlider1.setPaintLabels(true);
         jSlider1.setPaintTicks(true);
         jSlider1.setOrientation(javax.swing.SwingConstants.VERTICAL);
         jSlider1.setMajorTickSpacing(50);
         jSlider1.setMaximum(400);
         jSlider1.setValue(7);
         jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
             public void stateChanged(javax.swing.event.ChangeEvent evt) {
                 ChangeVelocity(evt);
             }
         }
         );
         
         getContentPane().add(jSlider1);
         jSlider1.setBounds(10, 450, 90, 260);
         
         
         jSlider2.setBorder(new javax.swing.border.TitledBorder("Velocity Direction in Deg (0 = vert.)"));
         jSlider2.setMinorTickSpacing(10);
         jSlider2.setPaintLabels(true);
         jSlider2.setPaintTicks(true);
         jSlider2.setMinimum(-90);
         jSlider2.setMajorTickSpacing(20);
         jSlider2.setMaximum(90);
         jSlider2.setValue(0);
         jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
             public void stateChanged(javax.swing.event.ChangeEvent evt) {
                 ChangeAngle(evt);
             }
         }
         );
         
         getContentPane().add(jSlider2);
         jSlider2.setBounds(110, 440, 300, 80);
         
         
         jSlider3.setBorder(new javax.swing.border.TitledBorder("Space Station Radius (m)"));
         jSlider3.setMinorTickSpacing(50);
         jSlider3.setPaintLabels(true);
         jSlider3.setPaintTicks(true);
         jSlider3.setMinimum(2);
         jSlider3.setMajorTickSpacing(250);
         jSlider3.setMaximum(2000);
         jSlider3.setValue(500);
         jSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
             public void stateChanged(javax.swing.event.ChangeEvent evt) {
                 ChangeStationRadius(evt);
             }
         }
         );
         
         getContentPane().add(jSlider3);
         jSlider3.setBounds(110, 560, 300, 80);
         
         
         jTextField1.setText("7");
         jTextField1.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jTextField1ActionPerformed(evt);
             }
         }
         );
         
         getContentPane().add(jTextField1);
         jTextField1.setBounds(40, 420, 40, 20);
         
         
         jTextField2.setText("0");
         jTextField2.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jTextField2ActionPerformed(evt);
             }
         }
         );
         
         getContentPane().add(jTextField2);
         jTextField2.setBounds(230, 420, 40, 20);
         
         
         jTextField3.setText("500");
         jTextField3.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jTextField3ActionPerformed(evt);
             }
         }
         );
         
         getContentPane().add(jTextField3);
         jTextField3.setBounds(230, 540, 40, 20);
         
         
         jLabel1.setText("Max Hor. Dist (m):");
         
         getContentPane().add(jLabel1);
         jLabel1.setLocation(110, 650);
         jLabel1.setSize(jLabel1.getPreferredSize());
         
         
         jLabel2.setText("Max Vert Dist (m):");
         
         getContentPane().add(jLabel2);
         jLabel2.setLocation(110, 680);
         jLabel2.setSize(jLabel2.getPreferredSize());
         
         
         jTextField4.setEditable(false);
         jTextField4.setText("jTextField4");
         jTextField4.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jTextField4ActionPerformed(evt);
             }
         }
         );
         
         getContentPane().add(jTextField4);
         jTextField4.setBounds(250, 650, 160, 20);
         
         
         jTextField5.setEditable(false);
         jTextField5.setText("jTextField5");
         jTextField5.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jTextField5ActionPerformed(evt);
             }
         }
         );
         
         getContentPane().add(jTextField5);
         jTextField5.setBounds(250, 680, 160, 20);
         
         
         jButton1.setText("Defaults");
         jButton1.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                 jButton1ActionPerformed(evt);
             }
         }
         );
         
         getContentPane().add(jButton1);
         jButton1.setLocation(310, 525);
         jButton1.setSize(jButton1.getPreferredSize());
         
     }//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
// Add your handling code here:
      RestoreDefaults();
  }//GEN-LAST:event_jButton1ActionPerformed

  private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
// Add your handling code here:
  }//GEN-LAST:event_jTextField5ActionPerformed

  private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
// Add your handling code here:
  }//GEN-LAST:event_jTextField4ActionPerformed

  private void ChangeStationRadius(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ChangeStationRadius
// Add your handling code here:
    jTextField3.setText(""+((javax.swing.JSlider)evt.getSource()).getValue());
    drawtr.ModRadius(((javax.swing.JSlider)evt.getSource()).getValue());
  }//GEN-LAST:event_ChangeStationRadius

  private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
// Add your handling code here:
     try {
       jSlider3.setValue((int)Double.parseDouble(((javax.swing.JTextField)
                                        evt.getSource()).getText()));
     } catch (NumberFormatException e) {
       System.err.println("Invalid Station Radius Value");
     }
  }//GEN-LAST:event_jTextField3ActionPerformed

  private void ChangeAngle(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ChangeAngle
// Add your handling code here:
    jTextField2.setText(""+((javax.swing.JSlider)evt.getSource()).getValue());
    drawtr.ModAngle(((javax.swing.JSlider)evt.getSource()).getValue());
  }//GEN-LAST:event_ChangeAngle

  private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
// Add your handling code here:
     try {
       jSlider2.setValue((int)Double.parseDouble(((javax.swing.JTextField)
                                        evt.getSource()).getText()));
     } catch (NumberFormatException e) {
       System.err.println("Invalid Angle Value");
     }

  }//GEN-LAST:event_jTextField2ActionPerformed

  private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
// Add your handling code here:     
     try {
       jSlider1.setValue((int)Double.parseDouble(((javax.swing.JTextField)
                                        evt.getSource()).getText()));
     } catch (NumberFormatException e) {
       System.err.println("Invalid Velocity Value");
     }
  }//GEN-LAST:event_jTextField1ActionPerformed

  private void ChangeVelocity(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ChangeVelocity
// Add your handling code here:
      jTextField1.setText(""+((javax.swing.JSlider)evt.getSource()).getValue()); 
      drawtr.ModVelocity(((javax.swing.JSlider)evt.getSource()).getValue());
  }//GEN-LAST:event_ChangeVelocity

  private void RestoreDefaults() {
      jSlider1.setValue(7);
      jSlider2.setValue(0);
      jSlider3.setValue(500);
      drawtr.RestoreDefaults();
  }
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JSlider jSlider1;
  private javax.swing.JSlider jSlider2;
  private javax.swing.JSlider jSlider3;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JTextField jTextField2;
  private javax.swing.JTextField jTextField3;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JTextField jTextField4;
  private javax.swing.JTextField jTextField5;
  private javax.swing.JButton jButton1;
 

   }