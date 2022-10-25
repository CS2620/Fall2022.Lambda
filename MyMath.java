import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

public class MyMath {
  public static float interpolate(float one, float two, float percent) {
    float value = (1 - percent) * one + (percent) * two;
    return value;
  }

  public static float length(float x, float y) {
    return (float) Math.sqrt(x * x + y * y);
  }

  public static float getAngle(float x, float y) {
    return (float) Math.atan2(y, x);
  }

  public static float getX(float distance, float angle) {
    return (float) Math.cos(angle) * distance;
  }

  public static float getY(float distance, float angle) {
    return (float) Math.sin(angle) * distance;
  }

  public static boolean inBounds(int w, int h, int x, int y) {
    return (x >= 0 && y >= 0 && x < w && y < h);
  }

  public static boolean inBounds(int w, int h, float x, float y) {
    return inBounds(w, h, (int) x, (int) y);
  }

  public static int[] clamp(int[] blendedColors) {
    return new int[] {
        Math.max(0, Math.min(255, blendedColors[0])),
        Math.max(0, Math.min(255, blendedColors[1])),
        Math.max(0, Math.min(255, blendedColors[2])),
    };
  }

  // https://stackoverflow.com/a/5176861/10047920
  public static ArrayList<Map.Entry<?, Integer>> sortValue(Hashtable<?, Integer> t) {

    // Transfer as List and sort it
    ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
    Collections.sort(l, new Comparator<Map.Entry<?, Integer>>() {

      public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
        return o1.getValue().compareTo(o2.getValue());
      }
    });

    return l;
  }

  // https://stackoverflow.com/a/5176861/10047920
  public static ArrayList<Map.Entry<?, Integer>> sortValueReverse(Hashtable<?, Integer> t) {

    // Transfer as List and sort it
    ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
    Collections.sort(l, new Comparator<Map.Entry<?, Integer>>() {

      public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
        return o2.getValue().compareTo(o1.getValue());
      }
    });

    return l;
  }

  public static int L1Distance(Color original, Color color) {
    return Math.abs(original.getRed() - color.getRed()) +
        Math.abs(original.getGreen() - color.getGreen()) +
        Math.abs(original.getBlue() - color.getBlue());

  }

  public static int L2Distance(Color original, Color color) {
    return (int) Math.sqrt(Math.pow(original.getRed() - color.getRed(), 2) +
        Math.pow(original.getGreen() - color.getGreen(), 2) +
        Math.pow(original.getBlue() - color.getBlue(), 2));

  }

  public static int LInfDistance(Color original, Color color) {
    int r = Math.abs(original.getRed() - color.getRed());
    int g = Math.abs(original.getGreen() - color.getGreen());
    int b = Math.abs(original.getBlue() - color.getBlue());
    return (int) Math.max(r, Math.max(g, b));
  }

  public static Color trueRandomColor() {
    return new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
  }

  private static int index = 0;
  private static Color[] randomColors = null;

  public static Color randomColor() {

    // System.out.println("Geting random color " + index);

    if (randomColors == null) {
      randomColors = new Color[100];

      randomColors[0] = Color.BLACK;
      randomColors[1] = Color.WHITE;
      randomColors[2] = Color.RED;
      randomColors[3] = Color.GREEN;
      randomColors[4] = Color.BLUE;
      randomColors[5] = Color.CYAN;
      randomColors[6] = Color.MAGENTA;
      randomColors[7] = Color.YELLOW;



      for (int i = 0; i < randomColors.length; i++) {
        if(randomColors[i] != null) continue;
        // randomColors[i] = new Color(i / (float) randomColors.length, i / (float) randomColors.length,
        //     i / (float) randomColors.length);
        randomColors[i] = trueRandomColor();
      }
    }

    Color toReturn = randomColors[index];
    index++;
    index %= randomColors.length;
    // if(index > 7)
    // {
    //   System.out.println("High random color" + index);
    // }
    return toReturn;

  }
}
