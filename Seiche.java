/*    */ import java.applet.Applet;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import java.awt.Label;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Seiche
/*    */   extends Applet
/*    */ {
/*    */   InputSPanel panel1;
/*    */   PlotSPanel graph1;
/*    */   public static int jstart;
/*    */   public static float H;
/* 16 */   public static float d = 1.0F;
/*    */   public static float L;
/*    */   
/*    */   public void init() {
/* 20 */     setLayout(new BorderLayout());
/* 21 */     Label label = new Label("                    Basin ");
/* 22 */     add("North", label);
/* 23 */     this.graph1 = new PlotSPanel();
/* 24 */     add("Center", (Component)this.graph1);
/*    */     
/* 26 */     this.panel1 = new InputSPanel(this.graph1);
/* 27 */     add("West", (Component)this.panel1);
/*    */   }
/*    */   
/*    */   public static float T;
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Seiche.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */