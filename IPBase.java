import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class IPBase {
  BufferedImage bufferedImage;

  public IP updatePixels(IColorToColor lambda) {
    for (int y = 0; y < bufferedImage.getHeight(); y++) {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {

        Color color = new Color(bufferedImage.getRGB(x, y));
        Color newColor = lambda.toColor(color);

        bufferedImage.setRGB(x, y, newColor.getRGB());
      }
    }
    return new IP(this);
  }

  public IP updateImage(IUpdateLambda lambda) {
    int bw = bufferedImage.getWidth();
    int bh = bufferedImage.getHeight();

    BufferedImage intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < bh; y++) {
      for (int x = 0; x < bw; x++) {
        Color color = lambda.toColor(bufferedImage, bw, bh, x, y);

        intermediate.setRGB(x, y, color.getRGB());
      }
    }

    bufferedImage = intermediate;
    return new IP(this);
  }

  public IP updateImageAndSize(int nw, int nh, IUpdateLambda lambda) {
    int bw = bufferedImage.getWidth();
    int bh = bufferedImage.getHeight();

    BufferedImage intermediate = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < nh; y++) {
      for (int x = 0; x < nw; x++) {
        Color color = lambda.toColor(bufferedImage, nw, nh, x, y);

        intermediate.setRGB(x, y, color.getRGB());
      }
    }

    bufferedImage = intermediate;
    return new IP(this);
  }

  public IP save(String filename) {
    try {
      ImageIO.write(bufferedImage, "PNG", new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new IP(this);

  }

  public boolean compareTo(IP other) {
    var bw = bufferedImage.getWidth();
    var bh = bufferedImage.getHeight();
    if (this.width() != other.width() || this.height() != this.height())
      return false;

    for (var y = 0; y < bh; y++) {
      for (var x = 0; x < bw; x++) {
        var one = bufferedImage.getRGB(x, y);
        var two = other.bufferedImage.getRGB(x, y);
        var colorOne = new Color(one);
        var colorTwo = new Color(two);
        if (colorOne.getRed() != colorTwo.getRed())
          return false;
        if (colorOne.getGreen() != colorTwo.getGreen())
          return false;
        if (colorOne.getBlue() != colorTwo.getBlue())
          return false;
      }
    }
    return true;
  }

  public boolean compareTo(String filename) {
    IP toCompare = new IP(filename);
    var result = this.compareTo(toCompare);
    return result;
  }

  protected Color getBilinear(BufferedImage inImage, float x, float y, Color border) {
    var color00 = new Color(bufferedImage.getRGB((int) x, (int) y));
    Color color10;
    Color color01;
    Color color11;

    if ((int) x + 1 < inImage.getWidth())
      color10 = new Color(bufferedImage.getRGB((int) x + 1, (int) y));
    else
      color10 = Color.WHITE;
    if (y + 1 < inImage.getHeight()) {
      color01 = new Color(bufferedImage.getRGB((int) x, (int) y + 1));
    } else
      color01 = Color.WHITE;

    if (x + 1 < inImage.getWidth() && y + 1 < inImage.getHeight())
      color11 = new Color(bufferedImage.getRGB((int) x + 1, (int) y + 1));
    else {
      color11 = Color.WHITE;
    }

    float percentX = x - (int) x;
    float percentY = y - (int) y;

    var interpolationX1 = interpolate(color00, color10, percentX);
    var interpolationX2 = interpolate(color01, color11, percentX);

    var interpolationY = interpolate(interpolationX1, interpolationX2, percentY);
    return interpolationY;
  }

  protected Color getNN(BufferedImage inImage, float x, float y, Color border) {
    return getNN(inImage, (int) x, (int) y, border);
  }

  protected Color getNN(BufferedImage inImage, int x, int y, Color border) {
    if (MyMath.inBounds(inImage.getWidth(), inImage.getHeight(), x, y)) {
      return new Color(inImage.getRGB(x, y));
    }
    return border;
  }

  protected Color interpolate(Color one, Color two, float percent) {

    float r = (1 - percent) * one.getRed() + (percent) * two.getRed();
    float g = (1 - percent) * one.getGreen() + (percent) * two.getGreen();
    float b = (1 - percent) * one.getBlue() + (percent) * two.getBlue();

    return new Color((int) r, (int) g, (int) b);
  }

  public int width() {
    return bufferedImage.getWidth();
  }

  public int height() {
    return bufferedImage.getHeight();
  }

  /**
   * Determine if the given coordinate is within the image
   * @param x The x value to test
   * @param y The y value to test
   * @return True if (x,y) is within ([0,width),[0,height)), false othewise
   */
  public boolean isValidCoordinate(int x, int y){
    return x > 0 && y > 0 && x < bufferedImage.getWidth() && y < bufferedImage.getHeight();

  }

}
