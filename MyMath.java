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
}
