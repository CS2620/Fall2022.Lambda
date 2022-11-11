package ip;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import helps.MutableInt;
import helps.MyMath;
import helps.lambdas.IBlend;
import helps.lambdas.IBlendColor;
import helps.lambdas.IIPLambda;
import helps.lambdas.ILayerLambda;

/**
 * The Image Container class
 * 
 */
public class ICon {
  List<Layer> layers = new ArrayList<>();

  int width;
  int height;
  Color backgroundColor = Color.BLACK;
  int currentLayer = 0;

  public ICon(String filename) {
    this(new IP(filename));
  }

  public ICon(String filename, int offsetX, int offsetY) {
    this(new Layer(filename, offsetX, offsetY));
  }

  public ICon(IP ip) {
    this(new Layer(ip));
  }

  public ICon(Layer layer) {
    layers.add(layer);
    width = layer.ip.bufferedImage.getWidth();
    height = layer.ip.bufferedImage.getHeight();
  }

  public ICon save(String filename) {
    var ip = this.flatten();
    ip.save(filename);
    return this;
  }

  public ICon addLayer(String filename) {
    return addLayer(new Layer(filename));
  }

  public ICon addLayer(Layer layer) {
    layers.add(layer);
    currentLayer = layers.size() - 1;
    return this;
  }

  public IP flatten() {
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

    // Fill in the background
    Graphics g = bi.getGraphics();
    g.setColor(this.backgroundColor);
    g.fillRect(0, 0, width, height);
    g.dispose();

    for (int i = 0; i < layers.size(); i++) {
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          var layer = layers.get(i);
          int ix = x - layer.offsetX;
          int iy = y - layer.offsetY;
          var ip = layer.ip;
          if (!ip.isValidCoordinate(ix, iy))
            continue;
          int rgb = ip.bufferedImage.getRGB(ix, iy);

          // Here we need to merge based on transparency
          int currentPixelInt = bi.getRGB(x, y);

          Color currentPixel = new Color(currentPixelInt);
          int currentRed = currentPixel.getRed();
          int currentGreen = currentPixel.getGreen();
          int currentBlue = currentPixel.getBlue();

          Color incomingPixel = new Color(rgb);
          int incomingRed = incomingPixel.getRed();
          int incomingGreen = incomingPixel.getGreen();
          int incomingBlue = incomingPixel.getBlue();

          int[] incomingColors = new int[] { incomingRed, incomingGreen, incomingBlue };
          int[] currentColors = new int[] { currentRed, currentGreen, currentBlue };
          int[] blendedColors = new int[] { incomingRed, incomingGreen, incomingBlue };

          switch (layer.blendMode) {
            case Normal:
              break;
            case Add:
              blendedColors = blend(incomingColors, currentColors, (a, b) -> a + b);
              break;
            case Subtract:
              blendedColors = blend(incomingColors, currentColors, (a, b) -> a - b);
              break;
            case SubtractAbs:
              blendedColors = blend(incomingColors, currentColors, (a, b) -> Math.abs(a - b));
              break;
            case Darken:
              blendedColors = blend(incomingColors, currentColors, (a, b) -> Math.min(a, b));
              break;
            case Lighten:
              blendedColors = blend(incomingColors, currentColors, (a, b) -> Math.max(a, b));
              break;
            case DarkerColor:
              blendedColors = blend(incomingColors, currentColors, (a, b, k1, k2) -> k1 > k2 ? b : a);
              break;
            case LighterColor:
              blendedColors = blend(incomingColors, currentColors, (a, b, k1, k2) -> k1 < k2 ? b : a);
              break;
            case Multiply:
              blendedColors = blend(incomingColors, currentColors, (a, b, k1, k2) -> (int) (b * (k1 / 255.0f)));
              break;
            case Divide:
              blendedColors = blend(incomingColors, currentColors,
                  (a, b) -> (int) ((b / 255.0f) / (a / 255.0f) * 255f));
              break;
            default:
              System.out.println("Encountered an unknown blend mode");
          }

          blendedColors = MyMath.clamp(blendedColors);

          float weight = layer.alpha;

          float combinedR = (1 - weight) * currentRed + weight * blendedColors[0];
          float combinedG = (1 - weight) * currentGreen + weight * blendedColors[1];
          float combinedB = (1 - weight) * currentBlue + weight * blendedColors[2];

          bi.setRGB(x, y, new Color((int) combinedR, (int) combinedG, (int) combinedB).getRGB());
        }
      }
    }

    return new IP(bi);

  }

  private int[] blend(int[] incomingColors, int[] currentColors, IBlend blendAlgorithm) {
    int[] toReturn = new int[3];
    toReturn[0] = blendAlgorithm.blend(incomingColors[0], currentColors[0]);
    toReturn[1] = blendAlgorithm.blend(incomingColors[1], currentColors[1]);
    toReturn[2] = blendAlgorithm.blend(incomingColors[2], currentColors[2]);

    return toReturn;
  }

  private int[] blend(int[] incomingColors, int[] currentColors, IBlendColor blendAlgorithm) {
    int[] toReturn = new int[3];
    int k1 = luminance(incomingColors);
    int k2 = luminance(currentColors);
    toReturn[0] = blendAlgorithm.blend(incomingColors[0], currentColors[0], k1, k2);
    toReturn[1] = blendAlgorithm.blend(incomingColors[1], currentColors[1], k1, k2);
    toReturn[2] = blendAlgorithm.blend(incomingColors[2], currentColors[2], k1, k2);

    return toReturn;
  }

  private int luminance(int[] currentColors) {
    return Math.max(currentColors[0], Math.max(currentColors[1], currentColors[1]));
  }

  public Layer getCurrentLayer() {
    return layers.get(currentLayer);
  }

  public ICon selectLayer(int index) {
    if (index < 0 || index >= layers.size())
      throw new IndexOutOfBoundsException();
    this.currentLayer = index;
    return this;
  }

  public ICon exec(IIPLambda lambda) {
    lambda.lambda(getCurrentLayer().ip);
    return this;
  }

  public ICon generateLayer(ILayerLambda lambda) {
    addLayer(lambda.lambda(getCurrentLayer().clone()));
    return this;
  }

  public ICon addToCanvasSize(int deltaX, int deltaY) {
    width += deltaX;
    height += deltaY;
    return this;
  }

  public ICon moveLayer(int deltaX, int deltaY) {
    getCurrentLayer().offsetX += deltaX;
    getCurrentLayer().offsetY += deltaY;
    return this;
  }

  public ICon setBackgroundColor(Color inColor) {
    backgroundColor = inColor;
    return this;
  }

  public ICon setAsWidth(MutableInt ai) {
    ai.setValue(width);
    return this;
  }

  public ICon setAsHeight(MutableInt ai) {
    ai.setValue(height);
    return this;
  }

  public ICon setLayerAlpha(float f) {
    getCurrentLayer().alpha = f;
    return this;
  }

  public ICon setLayerBlendmode(BlendMode blendMode) {
    getCurrentLayer().blendMode = blendMode;
    return this;
  }

  public ICon appendHistogram() {

    int height = this.getCurrentLayer().ip.height();
    int width = this.getCurrentLayer().ip.width();

    this.addToCanvasSize(0, 100)
        .generateLayer(l -> l.exec(i -> i.toHistogram()))
        .moveLayer(0, height)
        .exec(ip -> ip.scaleLinear(width / 255.0f, 1))
        .setBackgroundColor(Color.MAGENTA);

    return this;
  }

}
