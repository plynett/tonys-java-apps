/*    */ import java.applet.Applet;
/*    */ import java.awt.Component;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.TextArea;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Streamless
/*    */   extends Applet
/*    */ {
/*    */   public double[] eta;
/*    */   public double[] theta;
/*    */   StreamPanel panel1;
/*    */   PlotStPanel graph1;
/*    */   GridBagLayout gblayout;
/*    */   GridBagConstraints gbc;
/*    */   
/*    */   public void init() {
/* 23 */     this.gblayout = new GridBagLayout();
/* 24 */     setLayout(this.gblayout);
/* 25 */     this.gbc = new GridBagConstraints();
/* 26 */     this.gbc.fill = 1;
/* 27 */     this.gbc.weightx = 1.0D;
/* 28 */     this.gbc.weighty = 1.0D;
/* 29 */     this.gbc.gridwidth = 0;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 36 */     this.graph1 = new PlotStPanel();
/* 37 */     this.gbc.anchor = 10;
/* 38 */     this.gblayout.setConstraints((Component)this.graph1, this.gbc);
/* 39 */     add((Component)this.graph1);
/* 40 */     this.gbc.gridwidth = -1;
/*    */     
/* 42 */     TextArea textArea = new TextArea(10, 20);
/* 43 */     textArea.setEditable(false);
/* 44 */     this.gbc.anchor = 13;
/* 45 */     this.gblayout.setConstraints(textArea, this.gbc);
/* 46 */     add(textArea);
/*    */     
/* 48 */     this.panel1 = new StreamPanel(this.graph1, textArea);
/* 49 */     this.gbc.anchor = 17;
/* 50 */     this.gblayout.setConstraints((Component)this.panel1, this.gbc);
/* 51 */     add((Component)this.panel1);
/*    */   }
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Streamless.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */