/*     */ import java.applet.Applet;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Button;
/*     */ import java.awt.Event;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Sand
/*     */   extends Applet
/*     */ {
/*     */   Panel panel1;
/*     */   Panel results;
/*     */   Label label1;
/*     */   TextField Hin;
/*     */   TextField Tin;
/*     */   TextField Kin;
/*     */   TextField Bin;
/*     */   TextField hstarin;
/*     */   
/*     */   public void init() {
/*  26 */     resize(500, 300);
/*  27 */     setLayout(new BorderLayout());
/*  28 */     this.panel1 = new Panel();
/*  29 */     this.panel1.setLayout(new GridLayout(7, 2));
/*  30 */     this.panel1.add(new Label(" Input Values:"));
/*  31 */     this.panel1.add(new Label(" "));
/*  32 */     this.Hbreak = "Breaker Height";
/*  33 */     this.panel1.add(new Label(this.Hbreak));
/*  34 */     this.panel1.add(this.Hin = new TextField("0.5"));
/*     */ 
/*     */     
/*  37 */     this.panel1.add(new Label("Breaking Angle (o)?"));
/*  38 */     this.panel1.add(this.angle0 = new TextField("10.0"));
/*  39 */     this.panel1.add(new Label(" K ?"));
/*  40 */     this.panel1.add(this.Kin = new TextField(".7"));
/*  41 */     this.panel1.add(new Label(" Berm height, B? "));
/*  42 */     this.panel1.add(this.Bin = new TextField("1.0"));
/*  43 */     this.panel1.add(new Label(" Depth of closure?"));
/*  44 */     this.panel1.add(this.hstarin = new TextField("6.0"));
/*  45 */     this.panel1.add(this.Calculate = new Button("Calculate"));
/*  46 */     this.panel1.add(this.Reset = new Button("Reset"));
/*  47 */     add("West", this.panel1);
/*     */     
/*  49 */     this.results = new Panel();
/*  50 */     this.results.setLayout(new GridLayout(7, 2));
/*  51 */     this.results.add(new Label("  Output:"));
/*  52 */     this.results.add(new Label("   "));
/*  53 */     this.results.add(new Label(" Q (m3/sec) ="));
/*  54 */     this.qout = new TextField("           ");
/*  55 */     this.qout.setEditable(false);
/*  56 */     this.results.add(this.qout);
/*  57 */     this.results.add(new Label("   "));
/*  58 */     this.results.add(new Label("   "));
/*  59 */     this.results.add(new Label(" Q (m3/yr) ="));
/*  60 */     this.qyout = new TextField("           ");
/*  61 */     this.qyout.setEditable(false);
/*  62 */     this.results.add(this.qyout);
/*  63 */     this.results.add(new Label("   "));
/*  64 */     this.results.add(new Label("   "));
/*  65 */     this.results.add(new Label(" G (m2/sec) ="));
/*  66 */     this.Gout = new TextField("          ");
/*  67 */     this.Gout.setEditable(false);
/*  68 */     this.results.add(this.Gout);
/*  69 */     this.results.add(new Label("   "));
/*  70 */     this.results.add(new Label("   "));
/*  71 */     add("Center", this.results);
/*     */   }
/*     */   TextField angle0; Button Calculate; Button Reset; Refract ref; int ntheory;
/*     */   TextField qout;
/*     */   TextField Gout;
/*     */   TextField qyout;
/*     */   String Hbreak;
/*     */   
/*     */   public boolean handleEvent(Event paramEvent) {
/*  80 */     double d1 = 9.81D;
/*     */     
/*  82 */     double d2 = 1000.0D;
/*  83 */     double d3 = 2.65D;
/*  84 */     double d4 = 0.3D;
/*  85 */     double d5 = 0.78D;
/*     */ 
/*     */     
/*     */     try {
/*  89 */       if (paramEvent.target instanceof Button && paramEvent.id == 1001) {
/*  90 */         if (paramEvent.target == this.Calculate) {
/*  91 */           double d6 = Double.valueOf(this.Hin.getText()).doubleValue();
/*     */           
/*  93 */           double d7 = Double.valueOf(this.Kin.getText()).doubleValue();
/*  94 */           double d11 = Double.valueOf(this.angle0.getText()).doubleValue();
/*  95 */           d11 = Math.PI * d11 / 180.0D;
/*  96 */           double d13 = Double.valueOf(this.Bin.getText()).doubleValue();
/*  97 */           double d8 = Double.valueOf(this.hstarin.getText()).doubleValue();
/*  98 */           double d9 = d6 / d5;
/*  99 */           double d10 = d2 * d1 * d6 * d6 / 8.0D;
/* 100 */           double d12 = Math.sqrt(d1 * d9);
/* 101 */           double d14 = d7 * d10 * d12 / d2 * d1 * (d3 - 1.0D) * (1.0D - d4);
/* 102 */           double d15 = d14 * Math.cos(d11) * Math.sin(d11);
/* 103 */           d15 = (int)(d15 * 1000.0D) / 1000.0D;
/* 104 */           this.qout.setText(String.valueOf(d15));
/*     */           
/* 106 */           double d17 = d15 * 3.1536E7D;
/* 107 */           d17 = (int)(d17 * 100.0D) / 100.0D;
/* 108 */           this.qyout.setText(String.valueOf(d17));
/* 109 */           double d16 = 2.0D * d14 * Math.cos(2.0D * d11) / (d8 + d13);
/* 110 */           d16 = (int)(d16 * 1000.0D) / 1000.0D;
/* 111 */           this.Gout.setText(String.valueOf(d16));
/*     */ 
/*     */         
/*     */         }
/* 115 */         else if (paramEvent.target == this.Reset) {
/* 116 */           this.Hin.setText("");
/*     */           
/* 118 */           this.Kin.setText("");
/* 119 */           this.angle0.setText("");
/* 120 */           this.qout.setText("");
/* 121 */           this.qyout.setText("");
/* 122 */           this.Gout.setText("");
/* 123 */           this.Bin.setText("");
/* 124 */           this.hstarin.setText("");
/*     */         }
/*     */       
/*     */       }
/*     */     }
/* 129 */     catch (NumberFormatException numberFormatException) {
/*     */       
/* 131 */       System.out.println(numberFormatException);
/*     */     } 
/*     */     
/* 134 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\SD_mount\Dropbox\projects\Tonys_Java_Apps\Tony's Java Apps\personal.egr.uri.edu\grilli\APLET\!\Sand.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.1.3
 */