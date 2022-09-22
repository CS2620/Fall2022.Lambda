public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    new IP("download.jpg")
        .toHistogram()
        .save("histrogram.png");

    //rgb_to_hsv(100, 255, 50);

  }

  public static void hsv_to_rgb(int h, int s, int v) {
    float r = -1;
    float g = -1;
    float b = -1;

    int hue = h / 60;
    float f =  h/60.0f - hue;

    switch (hue) {
      case 0:
        r = v;
        b = v*(1-s);
        g = v * (1 - (1-f)*s);
        break;
      case 1:
        g = v;
        b = v*(1-s);
        r = v * 1 - (f*s);
        break;
      case 2:
        g = v;
        r = v*(1-s);
        b = v * (1 - (1-f)*s);
        break;
      case 3:
        g = v;
        r = v*(1-s);
        b = v * 1 - (f*s);
        break;
      case 4:
        g = v;
        r = v*(1-s);
        b = v * (1 - (1-f)*s);
        break;
      case 5:
        break;
    }

  }

  public static void rgb_to_hsv(int r, int g, int b) {

    float h = -1;
    float s = -1;
    float v = -1;

    v = Math.max(r, Math.max(g, b));
    int min = Math.min(r, Math.min(g, b));
    float diff = v - min;

    if (v == 0)
      s = 0;
    else
      s = diff / v;

    s *= 255;

    if (r == v) {
      h = 0;
      h += (g - b) / diff * 60;
    } else if (g == v) {
      h = 120;
      h += (b - r) / diff * 60;
    } else {
      h = 240;
      h += (r - g) / diff * 60;
    }

    System.out.println(h + ", " + s + ", " + v);
  }

}