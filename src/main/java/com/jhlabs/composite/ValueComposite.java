/*    */ package com.jhlabs.composite;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.CompositeContext;
/*    */ import java.awt.RenderingHints;
/*    */ import java.awt.image.ColorModel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ValueComposite
/*    */   extends RGBComposite
/*    */ {
/*    */   public ValueComposite(float alpha) {
/* 26 */     super(alpha);
/*    */   }
/*    */ 
/*    */   
/*    */   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
/* 31 */     return new Context(this.extraAlpha, srcColorModel, dstColorModel);
/*    */   }
/*    */   
/*    */   static class Context
/*    */     extends RGBComposite.RGBCompositeContext {
/* 36 */     private final float[] sHSB = new float[3];
/* 37 */     private final float[] dHSB = new float[3];
/*    */ 
/*    */     
/*    */     public Context(float alpha, ColorModel srcColorModel, ColorModel dstColorModel) {
/* 41 */       super(alpha, srcColorModel, dstColorModel);
/*    */     }
/*    */ 
/*    */     
/*    */     public void composeRGB(int[] src, int[] dst, float alpha) {
/* 46 */       int w = src.length;
/*    */       
/* 48 */       for (int i = 0; i < w; i += 4) {
/*    */         
/* 50 */         int sr = src[i];
/* 51 */         int dir = dst[i];
/* 52 */         int sg = src[i + 1];
/* 53 */         int dig = dst[i + 1];
/* 54 */         int sb = src[i + 2];
/* 55 */         int dib = dst[i + 2];
/* 56 */         int sa = src[i + 3];
/* 57 */         int dia = dst[i + 3];
/*    */         
/* 59 */         Color.RGBtoHSB(sr, sg, sb, this.sHSB);
/* 60 */         Color.RGBtoHSB(dir, dig, dib, this.dHSB);
/* 61 */         this.dHSB[2] = this.sHSB[2];
/* 62 */         int doRGB = Color.HSBtoRGB(this.dHSB[0], this.dHSB[1], this.dHSB[2]);
/* 63 */         int dor = (doRGB & 0xFF0000) >> 16;
/* 64 */         int dog = (doRGB & 0xFF00) >> 8;
/* 65 */         int dob = doRGB & 0xFF;
/* 66 */         float a = alpha * sa / 255.0F;
/* 67 */         float ac = 1.0F - a;
/* 68 */         dst[i] = (int)(a * dor + ac * dir);
/* 69 */         dst[i + 1] = (int)(a * dog + ac * dig);
/* 70 */         dst[i + 2] = (int)(a * dob + ac * dib);
/* 71 */         dst[i + 3] = (int)(sa * alpha + dia * ac);
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\ValueComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */