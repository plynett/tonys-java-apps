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
/*    */ public class Groin
/*    */   extends Applet
/*    */ {
/*    */   InputGPanel panel1;
/*    */   PlotGPanel graph1;
/*    */   
/*    */   public void init() {
/* 18 */     setLayout(new BorderLayout());
/* 19 */     Label label = new Label(" ");
/* 20 */     add("North", label);
/* 21 */     this.graph1 = new PlotGPanel();
/* 22 */     add("Center", (Component)this.graph1);
/*    */     
/* 24 */     this.panel1 = new InputGPanel(this.graph1);
/* 25 */     add("West", (Component)this.panel1);
/*    */   }
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Groin.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */