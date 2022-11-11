package ip;
// Some code based on https://www.geeksforgeeks.org/program-change-rgb-color-model-hsv-color-model/
  
import java.awt.Color;

public class Colors {

  

  public static void runTests(){
    boolean doQuickTests = false;
    if (doQuickTests) {
      test(255, 0, 0);
      test(0, 255, 0);
      test(0, 0, 255);
      test(0, 0, 0);
      test(128, 128, 128);
      test(255, 255, 255);
      test(128, 255, 255);
      test(128, 128, 255);
    }

    boolean doLongTests = true;
    if (doLongTests) {
      System.out.println("Testing color conversion functions.");
      long start = System.currentTimeMillis();
      int count = 0;
      for (int a = 0; a < 256; a += 1) {
        for (int b = 0; b < 256; b += 1) {
          for (int c = 0; c < 256; c += 1) {
            test(a, b, c);
            count++;
          }
        }
      }
      long end = System.currentTimeMillis();
      long diff = (end - start);
      System.out.println("Did " + count + " tests in " + diff + " milliseconds");
      System.out.println("Done testing color conversion functions");
    }
  }

  private static boolean close(float a, float b) {
    return Math.abs(a - b) < .001f;
  }

  private static void print(float[] hsv) {
    System.out.println(hsv[0] + ", " + hsv[1] + ", " + hsv[2]);
  }

  static float[] rgb_to_hsv(Color color){
    return rgb_to_hsv(color.getRed(), color.getGreen(), color.getBlue());
  }

  static float[] rgb_to_hsv(float r, float g, float b) {
    float cmax = Math.max(r, Math.max(g, b));
    float cmin = Math.min(r, Math.min(g, b));
    float diff = cmax - cmin;

    float h = 0;

    if (diff != 0) {
      if (cmax == r) {
        h = 0;
        h += 60 * (g - b) / diff;
      } else if (cmax == g) {
        h = 120;
        h += 60 * (b - r) / diff;
      } else {
        h = 240;
        h += 60 * (r - g) / diff;
      }
      while (h < 0)
        h += 360;
      h %= 360;
    }

    float s = 0;

    if (cmax > 0)
      s = diff / cmax * 255;
    float v = cmax;

    return new float[] { h, s, v };

  }

  // public static float[] hsvToRgb(Color color){
  //   return hsvToRgb(color.getRed(), color.getGreen(), color.getBlue());
  // }

  public static float[] hsvToRgb(float hue, float saturation, float value) {

    saturation /= 255.0f;
    value /= 255.0f;
    int h = (int) (hue / 60.0f);
    float f = hue / 60.0f - h;
    float p = value * (1 - saturation);
    float q = value * (1 - f * saturation);
    float t = value * (1 - (1 - f) * saturation);

    value *= 255.0f;
    f *= 255.0f;
    p *= 255.0f;
    q *= 255.0f;
    t *= 255.0f;

    switch (h) {
      case 0:
        return new float[] { value, t, p };
      case 1:
        return new float[] { q, value, p };
      case 2:
        return new float[] { p, value, t };
      case 3:
        return new float[] { p, q, value };
      case 4:
        return new float[] { t, p, value };
      case 5:
        return new float[] { value, p, q };
      default:
        throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
            + saturation + ", " + value);
    }
  }

  static float[] rgb_to_hsv2(float r, float g, float b) {

    // R, G, B values are divided by 255
    // to change the range from 0..255 to 0..1

    // h, s, v = hue, saturation, value
    float cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
    float cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
    float diff = cmax - cmin; // diff of cmax and cmin.
    float h = -1, s = -1;

    // if cmax and cmax are equal then h = 0
    if (cmax == cmin)
      h = 0;

    // if cmax equal r then compute h
    else if (cmax == r)
      h = (60 * ((g - b) / diff) + 360) % 360;

    // if cmax equal g then compute h
    else if (cmax == g)
      h = (60 * ((b - r) / diff) + 120) % 360;

    // if cmax equal b then compute h
    else if (cmax == b)
      h = (60 * ((r - g) / diff) + 240) % 360;

    // if cmax equal zero
    if (cmax == 0)
      s = 0;
    else
      s = (diff / cmax) * 255;

    // compute v
    float v = cmax;
    // System.out.println("(" + h + " " + s + " " + v + ")");
    return new float[] { h, s, v };

  }

  public static float[] hsvToRgb2(float hue, float saturation, float value) {

    hue /= 360.0f;
    saturation/=255.0f;
    value /= 255.0f;
    int h = (int) (hue * 6);
    float f = hue * 6 - h;
    float p = value * (1 - saturation);
    float q = value * (1 - f * saturation);
    float t = value * (1 - (1 - f) * saturation);

    value *= 255;
    t *= 255;
    p *= 255;
    q *= 255;

    switch (h) {
      case 0:
        return new float[] { value, t, p };
      case 1:
        return new float[] { q, value, p };
      case 2:
        return new float[] { p, value, t };
      case 3:
        return new float[] { p, q, value };
      case 4:
        return new float[] { t, p, value };
      case 5:
        return new float[] { value, p, q };
      default:
        throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
            + saturation + ", " + value);
    }
  }

  private static void test(int i, int j, int k) {
    float[] hsv = rgb_to_hsv(i, j, k);
    float[] hsv2 = rgb_to_hsv2(i, j, k);

    float[] rgb = hsvToRgb(hsv[0], hsv[1], hsv[2]);
    float[] rgb2 = hsvToRgb2(hsv[0], hsv[1], hsv[2]);

    if (
      close(hsv[0], hsv2[0]) && close(hsv[1], hsv[1]) && close(hsv[2], hsv[2])
     && close(i, rgb[0]) && close(j, rgb[1]) && close(k, rgb[2])
     && close(i, rgb2[0]) && close(j, rgb2[1]) && close(k, rgb2[2])
     )
      return;
    System.out.println();
    print(new float[] { i, j, k });
    print(hsv);
    print(hsv2);
    print(rgb);
  }
  
}
