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
/*    */ public class Beach
/*    */   extends Applet
/*    */ {
/*    */   InputBPanel panel1;
/*    */   PlotBPlot graph1;
/*    */   
/*    */   public void init() {
/* 22 */     setLayout(new BorderLayout());
/* 23 */     Label label = new Label(" ");
/* 24 */     add("North", label);
/* 25 */     this.graph1 = new PlotBPlot();
/* 26 */     add("Center", (Component)this.graph1);
/*    */     
/* 28 */     this.panel1 = new InputBPanel(this.graph1);
/* 29 */     add("West", (Component)this.panel1);
/*    */   }
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Beach.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */