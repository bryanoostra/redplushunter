/* @author Job Zwiers
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2006/05/24 09:00:23 $
 */

// Last modification by: $Author: swartjes $
// $Log: Mapping.java,v $
// Revision 1.1  2006/05/24 09:00:23  swartjes
// Parlevink and Parlegraph code.
//
// Revision 1.1  2005/11/08 16:03:14  swartjes
// Added Parlegraph (HMI) for knowledge visualisation.
//
// Revision 1.1  2005/08/22 16:00:39  zwiers
// initial version
//



package parlevink.util;


/** 
 * 
 */
public class Mapping {


   public interface DoubleMap1 {     
      /** 
       * maps a double to double
       */
      public double map1(double x);   
   }

   public interface TableDoubleMap1 extends DoubleMap1 {     
      public boolean setData(double[] x, double[] fx);
   }

   public interface DoubleMap2 {     
      /** 
       * maps a double to double
       */
      public double map2(double x, double y);   
   }


   public interface TableDoubleMap2 extends DoubleMap2 {     
      public boolean setData(double[] x, double[] y, double[][] fxy);
   }  


   /**
    * implementation of DoubleMap1, based upon table lookup and interpolation
    */
   public static TableDoubleMap1 createTableDoubleMap1(final double[] xKnots,  final double[] values ) {
      TableDoubleMap1 dm = new TableDoubleMap1() {
      
         private double minX, maxX;
         private double[] x = xKnots;
         private double[] value = values;
         private int xIndexLow, xIndexHigh;
         private double xLow, xHigh;      
         private int xDim;
                  
         /**
          * defines the data used for table lookup
          * The data arrays are not copied, so updates made to these arrays will affect
          * the mapping.
          */
         public boolean setData(final double[] xKnots, final double[] values ) {
            if (xKnots.length != values.length) {
               Console.println("TableMapping.setData:  knots and values arrays should have compatible lengths");
               return false;
             }
             if (xKnots.length < 2) {
               Console.println("TableMapping.setData:  data arrays should have length >= 2");
               return false;
             }
             xDim = xKnots.length;
             x = xKnots;
             value = values;  
             minX = x[0];          maxX = x[xDim-1];
             xIndexLow = 0;            xIndexHigh = 1;
             xLow = x[xIndexLow];  xHigh = x[xIndexHigh];
             return true;
         }
         
        /**
         * calculate interpolated value for argument arg, based upon current knots and values. 
         */
        public double map1(double xarg) {
           if (x == null || value == null) return 0;
           double xalpha;
           //Console.println("map1: arg = " + arg + ", maxKnot = " + maxKnot + ", len = " + len);
           if (xarg <= minX) xarg = minX;
           if (xarg >= maxX) xarg = maxX; 
           if (xarg < xLow || xarg > xHigh) {
              if ( xarg < xLow) { // implies xIndexLow > 0
                 xIndexHigh = xIndexLow; xIndexLow = 0; 
              } else { // xarg > xHigh // implies xIndexHigh < xDim-1
                 xIndexLow = xIndexHigh; xIndexHigh = xDim-1;
              }
              xLow = x[xIndexLow];    xHigh = x[xIndexHigh];
              while (xIndexHigh - xIndexLow > 1) {
                  int xIndexMid = (xIndexHigh+xIndexLow)/2;
                  double xMid = x[xIndexMid];
                  if (xarg <= xMid) {
                     xIndexHigh = xIndexMid; xHigh = xMid;  
                  } else {
                     xIndexLow = xIndexMid; xLow = xMid;
                  }
              }
           }
           xalpha = (xarg-xLow) / (xHigh-xLow);
           
           return (1-xalpha)*value[xIndexLow] +  (xalpha)*value[xIndexHigh];    
        }
   
      };
      dm.setData(xKnots, values);
      return dm;
   } // createTableDoubleMap1()



   /**
    * implementation of DoubleMap1, based upon table lookup and interpolation
    */
   public static TableDoubleMap2 createTableDoubleMap2(final double[] xKnots, final double[] yKnots, final double[][] values ) {
      TableDoubleMap2 dm = new TableDoubleMap2() {
      
         private double minX, maxX, minY, maxY;
         private double[] x = xKnots;
         private double[] y = yKnots;
         private double[][] value = values;
         private int xIndexLow, xIndexHigh, yIndexLow, yIndexHigh;
         private double xLow, xHigh, yLow, yHigh;      
         private int xDim, yDim;
                  
         /**
          * defines the data used for table lookup
          * The data arrays are not copied, so updates made to these arrays will affect
          * the mapping.
          */
         public boolean setData(final double[] xKnots, final double[] yKnots, final double[][] values ) {
            if (xKnots.length != values.length || yKnots.length != values[0].length  ) {
               Console.println("TableMapping.setData:  knots and values arrays should have compatible lengths");
               return false;
             }
             if (xKnots.length < 2 || yKnots.length < 2) {
               Console.println("TableMapping.setData:  data arrays should have length >= 2");
               return false;
             }
             xDim = xKnots.length;
             yDim = yKnots.length;
             x = xKnots;
             y = yKnots;
             value = values;  
             minX = x[0];          maxX = x[xDim-1];
             minY = y[0];          maxY = y[yDim-1];
             xIndexLow = 0;            xIndexHigh = 1;
             yIndexLow = 0;            yIndexHigh = 1;
             xLow = x[xIndexLow];  xHigh = x[xIndexHigh];
             yLow = y[yIndexLow];  yHigh = y[yIndexHigh];
             return true;
         }
         
        /**
         * calculate interpolated value for argument arg, based upon current knots and values. 
         */
        public double map2(double xarg, double yarg) {
           if (x == null || y == null || value == null) return 0;
           double xalpha, yalpha;
           //Console.println("map1: arg = " + arg + ", maxKnot = " + maxKnot + ", len = " + len);
           if (xarg <= minX) xarg = minX;
           if (xarg >= maxX) xarg = maxX; 
           if (xarg < xLow || xarg > xHigh) {
              if ( xarg < xLow) { // implies xIndexLow > 0
                 xIndexHigh = xIndexLow; xIndexLow = 0; 
              } else { // xarg > xHigh // implies xIndexHigh < xDim-1
                 xIndexLow = xIndexHigh; xIndexHigh = xDim-1;
              }
              xLow = x[xIndexLow];    xHigh = x[xIndexHigh];
              while (xIndexHigh - xIndexLow > 1) {
                  int xIndexMid = (xIndexHigh+xIndexLow)/2;
                  double xMid = x[xIndexMid];
                  if (xarg <= xMid) {
                     xIndexHigh = xIndexMid; xHigh = xMid;  
                  } else {
                     xIndexLow = xIndexMid; xLow = xMid;
                  }
              }
           }
           xalpha = (xarg-xLow) / (xHigh-xLow);
           
           if (yarg < minY) yarg = minY;
           if (yarg > maxY) yarg = maxY;
           if (yarg < yLow || yarg > yHigh) {
              if ( yarg < yLow) {
                 yIndexHigh = yIndexLow; yIndexLow = 0; 
              } else { // yarg > yHigh
                 yIndexLow = yIndexHigh; yIndexHigh = yDim-1;
              }
              yLow = y[yIndexLow];    yHigh = y[yIndexHigh];
              while (yIndexHigh - yIndexLow > 1) {
                  int yIndexMid = (yIndexHigh+yIndexLow)/2;
                  double yMid = y[yIndexMid];
                  if (yarg <= yMid) {
                     yIndexHigh = yIndexMid; yHigh = yMid;  
                  } else {
                     yIndexLow = yIndexMid; yLow = yMid;
                  }
              }
           }
           // knotLow <= arg <= knotHigh : interpolate
           yalpha = (yarg-yLow) / (yHigh-yLow);
           return (1-xalpha)*(1-yalpha)*value[xIndexLow][yIndexLow] +
                  (1-xalpha)*(yalpha)*value[xIndexLow][yIndexHigh] +
                  (xalpha)*(1-yalpha)*value[xIndexHigh][yIndexLow] +
                  (xalpha)*(yalpha)*value[xIndexHigh][yIndexHigh]; 
    
        }
   
      };
      dm.setData(xKnots, yKnots, values);
      return dm;
   } // createTableDoubleMap2()

  
}

