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
/*    */ public class OneLine
/*    */   extends Applet
/*    */ {
/*    */   InputOLPanel panel1;
/*    */   PlotOLPanel graph1;
/*    */   
/*    */   public void init() {
/* 19 */     setLayout(new BorderLayout());
/* 20 */     Label label = new Label(" ");
/* 21 */     add("North", label);
/* 22 */     this.graph1 = new PlotOLPanel();
/* 23 */     add("Center", (Component)this.graph1);
/*    */     
/* 25 */     this.panel1 = new InputOLPanel(this.graph1);
/* 26 */     add("West", (Component)this.panel1);
/*    */   }
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\OneLine.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */