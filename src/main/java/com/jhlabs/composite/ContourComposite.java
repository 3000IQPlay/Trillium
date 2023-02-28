/*    */ package com.jhlabs.composite;
/*    */ 
/*    */

import java.awt.*;
import java.awt.image.ColorModel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ContourComposite
/*    */   implements Composite
/*    */ {
/*    */   private final int offset;
/*    */   
/*    */   public ContourComposite(int offset) {
/* 33 */     this.offset = offset;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
/* 38 */     return new ContourCompositeContext(this.offset, srcColorModel, dstColorModel);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 43 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 48 */     /*    */     /* 50 */     /*    */
    return o instanceof ContourComposite;
/*    */     
/* 53 */
    /*    */   }
/*    */ }


/* Location:              C:\Users\Home\Downloads\Nightmare.jar!\com\jhlabs\composite\ContourComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */