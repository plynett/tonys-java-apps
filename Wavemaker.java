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
/*    */ public class Wavemaker
/*    */   extends Applet
/*    */ {
/*    */   InputMPanel panel1;
/*    */   PlotMPanel graph1;
/*    */   public static int jstart;
/*    */   public static float H;
/* 18 */   public static float d = 1.0F;
/*    */   public static float L;
/*    */   
/*    */   public void init() {
/* 22 */     setLayout(new BorderLayout());
/* 23 */     Label label = new Label(" ");
/* 24 */     add("North", label);
/* 25 */     this.graph1 = new PlotMPanel();
/* 26 */     add("Center", (Component)this.graph1);
/*    */     
/* 28 */     this.panel1 = new InputMPanel(this.graph1);
/* 29 */     add("West", (Component)this.panel1);
/*    */   }
/*    */   
/*    */   public static float T;
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Wavemaker.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */