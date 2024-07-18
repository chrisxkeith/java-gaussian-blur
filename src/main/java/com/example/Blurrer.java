package com.example;

// import javax.json.Json;

public class Blurrer {
  public static final int RED_MASK   = 0x00ff0000;
  public static final int GREEN_MASK = 0x0000ff00;
  public static final int BLUE_MASK  = 0x000000ff;

  private int blurRadius;
  private int blurKernelSize;
  private int[] blurKernel;
  private int[][] blurMult;

  public int[] pixels;

  /** Actual dimensions of pixels array, taking into account the 2x setting. */
  public int pixelWidth;
  public int pixelHeight;

  public void dumpBlurKernel() {
      System.out.println("Hello, World!");
  }

  public void buildBlurKernel(float r) {
      int radius = (int) (r * 3.5f);
      if (radius < 1) radius = 1;
      if (radius > 248) radius = 248;
      if (blurRadius != radius) {
        blurRadius = radius;
        blurKernelSize = 1 + blurRadius<<1;
        blurKernel = new int[blurKernelSize];
        blurMult = new int[blurKernelSize][256];
  
        int bk,bki;
        int[] bm,bmi;
  
        for (int i = 1, radiusi = radius - 1; i < radius; i++) {
          blurKernel[radius+i] = blurKernel[radiusi] = bki = radiusi * radiusi;
          bm=blurMult[radius+i];
          bmi=blurMult[radiusi--];
          for (int j = 0; j < 256; j++)
            bm[j] = bmi[j] = bki*j;
        }
        bk = blurKernel[radius] = radius * radius;
        bm = blurMult[radius];
        for (int j = 0; j < 256; j++)
          bm[j] = bk*j;
      }
    }

  public void blurRGB(float r) {
      int sum, cr, cg, cb;
      int read, ri, ym, ymi, bk0;
      int[] r2 = new int[pixels.length];
      int[] g2 = new int[pixels.length];
      int[] b2 = new int[pixels.length];
      int yi = 0;
  
      buildBlurKernel(r);
  
      for (int y = 0; y < pixelHeight; y++) {
        for (int x = 0; x < pixelWidth; x++) {
          cb = cg = cr = sum = 0;
          read = x - blurRadius;
          if (read < 0) {
            bk0 = -read;
            read = 0;
          } else {
            if (read >= pixelWidth) {
              break;
            }
            bk0 = 0;
          }
          for (int i = bk0; i < blurKernelSize; i++) {
            if (read >= pixelWidth) {
              break;
            }
            int c = pixels[read + yi];
            int[] bm = blurMult[i];
            cr += bm[(c & RED_MASK) >> 16];
            cg += bm[(c & GREEN_MASK) >> 8];
            cb += bm[c & BLUE_MASK];
            sum += blurKernel[i];
            read++;
          }
          ri = yi + x;
          r2[ri] = cr / sum;
          g2[ri] = cg / sum;
          b2[ri] = cb / sum;
        }
        yi += pixelWidth;
      }
  
      yi = 0;
      ym = -blurRadius;
      ymi = ym * pixelWidth;
  
      for (int y = 0; y < pixelHeight; y++) {
        for (int x = 0; x < pixelWidth; x++) {
          cb = cg = cr = sum = 0;
          if (ym < 0) {
            bk0 = ri = -ym;
            read = x;
          } else {
            if (ym >= pixelHeight) {
              break;
            }
            bk0 = 0;
            ri = ym;
            read = x + ymi;
          }
          for (int i = bk0; i < blurKernelSize; i++) {
            if (ri >= pixelHeight) {
              break;
            }
            int[] bm = blurMult[i];
            cr += bm[r2[read]];
            cg += bm[g2[read]];
            cb += bm[b2[read]];
            sum += blurKernel[i];
            ri++;
            read += pixelWidth;
          }
          pixels[x+yi] = 0xff000000 | (cr/sum)<<16 | (cg/sum)<<8 | (cb/sum);
        }
        yi += pixelWidth;
        ymi += pixelWidth;
        ym++;
      }
    }
}
