/*    */ import java.applet.Applet;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import java.awt.Label;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinearPlot
/*    */   extends Applet
/*    */ {
/*    */   InputPanel panel1;
/*    */   PlotPanel graph1;
/*    */   public static int jstart;
/*    */   public static float H;
/* 17 */   public static float d = 1.0F;
/*    */   public static float L;
/*    */   
/*    */   public void init() {
/* 21 */     setLayout(new BorderLayout());
/* 22 */     Label label = new Label(" ");
/* 23 */     add("North", label);
/* 24 */     this.graph1 = new PlotPanel();
/* 25 */     add("Center", (Component)this.graph1);
/*    */     
/* 27 */     this.panel1 = new InputPanel(this.graph1);
/* 28 */     add("West", (Component)this.panel1);
/*    */   }
/*    */   
/*    */   public static float T;
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\LinearPlot.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */