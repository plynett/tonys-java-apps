/*    */ import java.applet.Applet;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import java.awt.Label;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Inlet
/*    */   extends Applet
/*    */ {
/*    */   InputIPanel panel1;
/*    */   PlotIPlot graph1;
/*    */   
/*    */   public void init() {
/* 22 */     setLayout(new BorderLayout());
/* 23 */     Label label = new Label(" ");
/* 24 */     add("North", label);
/* 25 */     this.graph1 = new PlotIPlot();
/* 26 */     add("Center", (Component)this.graph1);
/*    */     
/* 28 */     this.panel1 = new InputIPanel(this.graph1);
/* 29 */     add("West", (Component)this.panel1);
/*    */   }
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Inlet.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */