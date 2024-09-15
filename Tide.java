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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Tide
/*    */   extends Applet
/*    */ {
/*    */   InputTPanel panel1;
/*    */   PlotTPlot graph1;
/*    */   TextArea ta;
/*    */   GridBagLayout gblayout;
/*    */   GridBagConstraints gbc;
/*    */   
/*    */   public void init() {
/* 27 */     this.gblayout = new GridBagLayout();
/* 28 */     setLayout(this.gblayout);
/* 29 */     this.gbc = new GridBagConstraints();
/* 30 */     this.gbc.fill = 1;
/* 31 */     this.gbc.weightx = 1.0D;
/* 32 */     this.gbc.weighty = 1.0D;
/* 33 */     this.gbc.gridwidth = 0;
/* 34 */     this.graph1 = new PlotTPlot();
/* 35 */     this.gbc.anchor = 10;
/* 36 */     this.gblayout.setConstraints((Component)this.graph1, this.gbc);
/* 37 */     add((Component)this.graph1);
/* 38 */     this.gbc.gridwidth = -1;
/*    */     
/* 40 */     TextArea textArea = new TextArea(10, 40);
/* 41 */     textArea.setEditable(true);
/* 42 */     textArea.setText("     Breakwater Harbor Delaware  \n");
/* 43 */     textArea.insertText("    Tidal Harmonics  \n\n", textArea.getText().length());
/* 44 */     textArea.insertText("           Tide       Amplitude    Phase     Speed(o/hr) \n\n", textArea.getText().length());
/* 45 */     textArea.insertText("\t M(2) \t 2.019 \t 245.8 \t 28.984 \n", textArea.getText().length());
/* 46 */     textArea.insertText("\t N(2) \t 0.434 \t 226.3\t 28.439 \n", textArea.getText().length());
/* 47 */     textArea.insertText("\t S(2) \t 0.356 \t 265.8\t 30.000 \n", textArea.getText().length());
/* 48 */     textArea.insertText("\t K(1) \t 0.340 \t 126.5\t 15.041 \n", textArea.getText().length());
/* 49 */     textArea.insertText("\t O(1) \t 0.287 \t 119.7\t 13.943 \n", textArea.getText().length());
/* 50 */     textArea.insertText("\t SA \t 0.217 \t 150.0 \t 00.041 \n", textArea.getText().length());
/* 51 */     textArea.insertText("\t SSA \t 0.121 \t 41.6 \t 00.082 \n", textArea.getText().length());
/* 52 */     textArea.insertText("\t P(1) \t 0.114 \t 123.5 \t 14.958 \n", textArea.getText().length());
/* 53 */     textArea.insertText("\t L(2) \t 0.098 \t 268.4 \t 29.528  \n", textArea.getText().length());
/* 54 */     textArea.insertText("\t K(2) \t 0.096 \t 267.6 \t 30.082  \n", textArea.getText().length());
/* 55 */     textArea.insertText("\t NU(2) \t 0.088 \t 232.1 \t 28.512 \n", textArea.getText().length());
/* 56 */     textArea.insertText("\t M(4) \t 0.041 \t 255.6 \t 57.968  \n", textArea.getText().length());
/* 57 */     textArea.insertText("\t MU(2)\t 0.039 \t 243.8 \t 27.968  \n", textArea.getText().length());
/* 58 */     textArea.insertText("\t T(2)\t 0.036 \t 235.4 \t 29.959  \n", textArea.getText().length());
/*    */     
/* 60 */     textArea.insertText("\t J(1) \t 0.023 \t 130.0 \t 15.585  \n", textArea.getText().length());
/* 61 */     textArea.insertText("\t M(6) \t 0.023 \t 272.1 \t 86.952  \n", textArea.getText().length());
/* 62 */     textArea.insertText("\t M(1) \t 0.021 \t 123.0 \t 14.4921  \n", textArea.getText().length());
/* 63 */     textArea.insertText("\t MS(4) \t 0.015 \t 266.9 \t 58.984  \n", textArea.getText().length());
/*    */     
/* 65 */     this.gbc.anchor = 13;
/* 66 */     this.gblayout.setConstraints(textArea, this.gbc);
/* 67 */     add(textArea);
/*    */     
/* 69 */     this.panel1 = new InputTPanel(this.graph1, textArea);
/* 70 */     this.gbc.anchor = 17;
/* 71 */     this.gblayout.setConstraints((Component)this.panel1, this.gbc);
/* 72 */     add((Component)this.panel1);
/*    */   }
/*    */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Tide.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */