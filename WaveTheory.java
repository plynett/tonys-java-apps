/*     */ import java.applet.Applet;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Button;
/*     */ import java.awt.Choice;
/*     */ import java.awt.Event;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Label;
/*     */ import java.awt.Panel;
/*     */ import java.awt.TextField;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WaveTheory
/*     */   extends Applet
/*     */ {
/*     */   Panel panel1;
/*     */   Panel results;
/*     */   Label label1;
/*     */   TextField Hin;
/*     */   TextField Tin;
/*     */   TextField din;
/*     */   TextField angle0;
/*     */   Button Calculate;
/*     */   Button Reset;
/*     */   Refract ref;
/*     */   Choice torf;
/*     */   int ntheory;
/*     */   
/*     */   public void init() {
/*  30 */     resize(450, 450);
/*  31 */     setLayout(new BorderLayout());
/*  32 */     this.panel1 = new Panel();
/*  33 */     this.panel1.setLayout(new GridLayout(9, 2));
/*  34 */     this.panel1.add(new Label("Deep Water Values:"));
/*  35 */     this.panel1.add(new Label(" "));
/*  36 */     this.panel1.add(new Label("Wave Height (m)?"));
/*  37 */     this.panel1.add(this.Hin = new TextField(""));
/*  38 */     this.torf = new Choice();
/*  39 */     this.torf.addItem("Period");
/*  40 */     this.torf.addItem("Frequency (Hz)");
/*  41 */     this.panel1.add(this.torf);
/*  42 */     this.panel1.add(this.Tin = new TextField(""));
/*  43 */     this.panel1.add(new Label("Wave Angle (o)?"));
/*  44 */     this.panel1.add(this.angle0 = new TextField("0.0"));
/*  45 */     this.panel1.add(new Label("Local Depth?"));
/*  46 */     this.panel1.add(this.din = new TextField(""));
/*  47 */     this.panel1.add(new Label(""));
/*  48 */     this.panel1.add(new Label(""));
/*  49 */     this.panel1.add(new Label(""));
/*  50 */     this.panel1.add(new Label(""));
/*  51 */     this.panel1.add(this.Calculate = new Button("Calculate"));
/*  52 */     this.panel1.add(this.Reset = new Button("Reset"));
/*  53 */     this.panel1.add(new Label(""));
/*  54 */     this.panel1.add(new Label(""));
/*  55 */     add("West", this.panel1);
/*  56 */     this.results = new Panel();
/*  57 */     this.results.setLayout(new GridLayout(10, 2));
/*  58 */     this.results.add(new Label(" L (m) ="));
/*  59 */     this.lout = new TextField("           ");
/*  60 */     this.lout.setEditable(false);
/*  61 */     this.results.add(this.lout);
/*  62 */     this.results.add(new Label(" k=2 pi/L = "));
/*  63 */     this.kout = new TextField("           ");
/*  64 */     this.kout.setEditable(false);
/*  65 */     this.results.add(this.kout);
/*  66 */     this.results.add(new Label(" C =L/T = "));
/*  67 */     this.Cout = new TextField("           ");
/*  68 */     this.Cout.setEditable(false);
/*  69 */     this.results.add(this.Cout);
/*  70 */     this.results.add(new Label("  Cg  ="));
/*  71 */     this.Cgout = new TextField("           ");
/*  72 */     this.Cgout.setEditable(false);
/*  73 */     this.results.add(this.Cgout);
/*  74 */     this.results.add(new Label("  n= Cg/C = "));
/*  75 */     this.nout = new TextField("            ");
/*  76 */     this.nout.setEditable(false);
/*  77 */     this.results.add(this.nout);
/*  78 */     this.results.add(new Label("  Ks = "));
/*  79 */     this.Ksout = new TextField("            ");
/*  80 */     this.Ksout.setEditable(false);
/*  81 */     this.results.add(this.Ksout);
/*  82 */     this.results.add(new Label("  Kr = "));
/*  83 */     this.Krout = new TextField("              ");
/*  84 */     this.Krout.setEditable(false);
/*  85 */     this.results.add(this.Krout);
/*  86 */     this.results.add(new Label(" Angle = "));
/*  87 */     this.angleout = new TextField("           ");
/*  88 */     this.angleout.setEditable(false);
/*  89 */     this.results.add(this.angleout);
/*  90 */     this.results.add(new Label("   H = "));
/*  91 */     this.Hshal = new TextField("          ");
/*  92 */     this.Hshal.setEditable(false);
/*  93 */     this.results.add(this.Hshal);
/*  94 */     this.results.add(new Label("  u_b= "));
/*  95 */     this.ubout = new TextField("        ");
/*  96 */     this.ubout.setEditable(false);
/*  97 */     this.results.add(this.ubout);
/*  98 */     add("Center", this.results);
/*     */   }
/*     */ 
/*     */   
/*     */   TextField lout;
/*     */   TextField Cgout;
/*     */   TextField Ksout;
/*     */   TextField Krout;
/*     */   TextField angleout;
/*     */   TextField fin;
/*     */   
/*     */   public boolean handleEvent(Event paramEvent) {
/* 110 */     this.ref = new Refract();
/*     */     
/*     */     try {
/* 113 */       if (paramEvent.id == 1001) {
/* 114 */         if (paramEvent.target.equals(this.torf)) {
/* 115 */           if ("Period".equals(paramEvent.arg)) {
/* 116 */             this.period = true;
/*     */           } else {
/*     */             
/* 119 */             this.period = false;
/*     */           } 
/*     */         }
/*     */         
/* 123 */         if (paramEvent.target instanceof Button) {
/* 124 */           if (paramEvent.target == this.Calculate) {
/* 125 */             float f2, f1 = Float.valueOf(this.Hin.getText()).floatValue();
/* 126 */             String str = this.Tin.getText();
/* 127 */             if (this.period) {
/* 128 */               f2 = Float.valueOf(str).floatValue();
/*     */             } else {
/*     */               
/* 131 */               f2 = (float)(1.0D / Float.valueOf(str).floatValue());
/*     */             } 
/* 133 */             float f3 = Float.valueOf(this.din.getText()).floatValue();
/* 134 */             float f12 = Float.valueOf(this.angle0.getText()).floatValue();
/* 135 */             float f5 = this.ref.waveNumber(f3, f2);
/* 136 */             this.kout.setText(String.valueOf(f5));
/* 137 */             float f4 = 6.283185F / f5;
/* 138 */             this.lout.setText(String.valueOf(f4));
/* 139 */             float f10 = f4 / f2;
/* 140 */             this.Cout.setText(String.valueOf(f10));
/* 141 */             float f6 = this.ref.theta(f12, f5, f2);
/* 142 */             this.angleout.setText(String.valueOf(f6));
/* 143 */             float f7 = this.ref.groupVelocity(f2, f3, f5);
/* 144 */             float f11 = f7 / f10;
/* 145 */             this.nout.setText(String.valueOf(f11));
/* 146 */             this.Cgout.setText(String.valueOf(f7));
/* 147 */             float f8 = this.ref.shoalingCoef(f2, f7);
/* 148 */             this.Ksout.setText(String.valueOf(f8));
/* 149 */             float f9 = this.ref.refractionCoef(f12, f6);
/* 150 */             this.Krout.setText(String.valueOf(f9));
/* 151 */             float f13 = f8 * f9 * f1;
/* 152 */             if (f13 > 0.8F * f3) {
/* 153 */               f13 = 0.8F * f3;
/* 154 */               this.Hshal.setText(String.valueOf(String.valueOf(f13)) + ", breaking");
/*     */             }
/*     */             else {
/*     */               
/* 158 */               this.Hshal.setText(String.valueOf(f13));
/*     */             } 
/* 160 */             double d = 6.283185307179586D / f2 * f13 / 2.0D * Hyper.sinh((f5 * f3));
/* 161 */             this.ubout.setText(String.valueOf(d));
/*     */           
/*     */           }
/* 164 */           else if (paramEvent.target == this.Reset) {
/* 165 */             this.Hin.setText("");
/* 166 */             this.Tin.setText("");
/* 167 */             this.din.setText("");
/* 168 */             this.angle0.setText("");
/* 169 */             this.lout.setText("");
/* 170 */             this.kout.setText("");
/* 171 */             this.Cout.setText("");
/* 172 */             this.Cgout.setText("");
/* 173 */             this.nout.setText("");
/* 174 */             this.Ksout.setText("");
/* 175 */             this.Krout.setText("");
/* 176 */             this.kout.setText("");
/* 177 */             this.angleout.setText("");
/* 178 */             this.Hshal.setText("");
/* 179 */             this.ubout.setText("");
/*     */           }
/*     */         
/*     */         }
/*     */       }
/*     */     
/* 185 */     } catch (NumberFormatException numberFormatException) {
/*     */       
/* 187 */       System.out.println(numberFormatException);
/*     */     } 
/*     */     
/* 190 */     return false;
/*     */   }
/*     */   
/*     */   TextField Hshal;
/*     */   TextField nout;
/*     */   TextField Cout;
/*     */   TextField kout;
/*     */   TextField ubout;
/*     */   boolean period = true;
/*     */ }
