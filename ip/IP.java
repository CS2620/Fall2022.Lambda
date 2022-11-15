package ip;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import helps.MyMath;
import helps.lambdas.IColorToColor;
import helps.lambdas.IFloatToFloat;
import helps.lambdas.IIPLambda;
import helps.lambdas.IIntToInt;

//Suppress the fact that we aren't checking generics
@SuppressWarnings("unchecked")
public class IP extends IPBase {

    public IP(String filename) {
        super();
        try {
            this.bufferedImage = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IP(BufferedImage bi) {
        super();
        this.bufferedImage = bi;

    }

    public IP(IPBase base) {
        super();
        this.bufferedImage = base.bufferedImage;
    }

    public IP toGrayscale() {
        return updatePixels(c -> {
            int gray = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
            return new Color(gray, gray, gray);
        });
    }

    public IP remap(IColorToColor lambda) {
        return updatePixels(c -> lambda.toColor(c));
    }

    public IP remapValueFloat(IFloatToFloat lambda){
        
        for(float i = 0; i < 1; i+=.1f){
            System.out.println(i +" -> " + lambda.run(i));
        }
        
        return updatePixels(c -> {
            float[] hsv = Colors.rgb_to_hsv(c);
            float value = hsv[2];
            float valueFloat = value/255f;

            valueFloat = lambda.run(valueFloat);
            value = (int)(valueFloat * 255f);
            if(value < 0) value = 0;
            if(value > 255) value = 255;

            float[] newRGB = Colors.hsvToRgb(hsv[0], hsv[1], value);

            return new Color((int) newRGB[0], (int) newRGB[1], (int) newRGB[2]);
        });
    }

    public IP remapValueInt(IIntToInt lambda) {
        return updatePixels(c -> {
            float[] hsv = Colors.rgb_to_hsv(c);
            float value = hsv[2];

            value = lambda.run((int)value);
            if(value < 0) value = 0;
            if(value > 255) value = 255;

            float[] newRGB = Colors.hsvToRgb(hsv[0], hsv[1], value);

            return new Color((int) newRGB[0], (int) newRGB[1], (int) newRGB[2]);
        });
    }

    public IP toGrayscaleHSV() {
        return updatePixels(c -> {
            int gray = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
            return new Color(gray, gray, gray);
        });
    }

    public IP rotate180() {

        return updateImage((bi, bw, bh, x, y) -> {
            int x1 = bw - x - 1;
            int y1 = bh - y - 1;
            return new Color(bi.getRGB(x1, y1));
        });
    }

    public IP rotate90() {
        int _bw = bufferedImage.getWidth();
        int _bh = bufferedImage.getHeight();

        return updateImageAndSize(_bh, _bw, (bi, bw, bh, x, y) -> {
            int y1 = bw - x - 1;
            int x1 = bh - y - 1;
            return new Color(bi.getRGB(x1, y1));
        });
    }

    public IP translateForward(int dx, int dy) {
        int bw = bufferedImage.getWidth();
        int bh = bufferedImage.getHeight();
        BufferedImage intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);

        Graphics g = intermediate.getGraphics();
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, bw, bh);

        for (int y = 0; y < bh; y++) {
            for (int x = 0; x < bw; x++) {

                Color color = new Color(bufferedImage.getRGB(x, y));

                int y1 = y + dy;
                int x1 = x + dx;

                if (x1 >= bw || y1 >= bh || x1 < 0 || y1 < 0)
                    continue;

                intermediate.setRGB(x1, y1, color.getRGB());
            }
        }

        bufferedImage = intermediate;
        return this;
    }

    public IP translate(int dx, int dy) {

        return updateImage((bi, bw, bh, x, y) -> {
            int x1 = x - dx;
            int y1 = y - dy;
            if (MyMath.inBounds(bw, bh, x1, y1)) {
                return new Color(bi.getRGB(x1, y1));
            }
            return Color.MAGENTA;
        });
    }

    public IP scaleLinear(float scale) {
        return scaleLinear(scale, scale);
    }

    public IP scaleLinear(float xs, float ys) {

        var _bw = bufferedImage.getWidth();
        var _bh = bufferedImage.getHeight();
        var nw = (int) (_bw * xs + .5);
        var nh = (int) (_bh * ys + .5);
        return updateImageAndSize(nw, nh, (bi, bw, bh, x, y) -> {

            float originalX = (x / xs);
            float originalY = (y / ys);

            Color color;
            if (!MyMath.inBounds(bw, bh, (int) originalX, (int) originalY))
                color = Color.MAGENTA;
            else {
                color = getBilinear(bufferedImage, originalX, originalY, Color.WHITE);
            }
            return color;
        });
    }

    public IP scaleNN(float scale) {

        return this.scaleNN(scale, scale);
    }

    public IP scaleNN(float xs, float ys) {

        var _bw = bufferedImage.getWidth();
        var _bh = bufferedImage.getHeight();
        var nw = (int) (_bw * xs + .5);
        var nh = (int) (_bh * ys + .5);
        return updateImageAndSize(nw, nh, (bi, bw, bh, x, y) -> {

            float originalX = (x / xs);
            float originalY = (y / ys);

            Color color;
            if (!MyMath.inBounds(bw, bh, (int) originalX, (int) originalY))
                color = Color.MAGENTA;
            else {
                color = getNN(bufferedImage, originalX, originalY, Color.WHITE);
            }
            return color;
        });
    }

    public IP rotate(float degrees, boolean linearInterpolation) {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();
        var intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                // Rotations
                /*
                 * 1 - Radians or degrees? Degrees
                 * 2 - What are we rotating about? Upper left-hand
                 * 3 - Rotate up or down? Down (e.g. positive rotations are clockwise)
                 */

                float radians = (float) (degrees / 360 * Math.PI * 2);
                float r = MyMath.length(x, y);
                float theta = MyMath.getAngle(x, y);

                float newAngle = theta - radians;
                float newX = MyMath.getX(r, newAngle);
                float newY = MyMath.getY(r, newAngle);

                Color color;
                if (newX >= bw || newY >= bh || newX < 0 || newY < 0)
                    color = Color.MAGENTA;
                else {
                    var interpolationY = getBilinear(bufferedImage, newX, newY, Color.WHITE);
                    if (linearInterpolation)
                        color = interpolationY;
                    else
                        color = getNN(bufferedImage, (int) newX, (int) newY, Color.WHITE);
                }
                intermediate.setRGB(x, y, color.getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
    }

    public IP changeHue(int degrees) {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();
        var intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));

                float[] hsv = Colors.rgb_to_hsv(original.getRed(), original.getGreen(), original.getBlue());

                hsv[0] += degrees;
                while (hsv[0] < 0) {
                    hsv[0] += 360;
                }
                hsv[0] %= 360;

                float[] rgb = Colors.hsvToRgb(hsv[0], hsv[1], hsv[2]);

                intermediate.setRGB(x, y, new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]).getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
    }

    public IP changeSaturation(int amount) {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();
        var intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));

                float[] hsv = Colors.rgb_to_hsv(original.getRed(), original.getGreen(), original.getBlue());

                hsv[1] += amount;
                if (hsv[1] < 0) {
                    hsv[1] = 0;
                }
                if (hsv[1] > 255) {
                    hsv[1] = 255;
                }

                float[] rgb = Colors.hsvToRgb(hsv[0], hsv[1], hsv[2]);

                intermediate.setRGB(x, y, new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]).getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
    }

    public IP changeValue(int amount) {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();
        var intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));

                float[] hsv = Colors.rgb_to_hsv(original.getRed(), original.getGreen(), original.getBlue());

                hsv[2] += amount;
                if (hsv[2] < 0) {
                    hsv[2] = 0;
                }
                if (hsv[2] > 255) {
                    hsv[2] = 255;
                }

                float[] rgb = Colors.hsvToRgb(hsv[0], hsv[1], hsv[2]);

                intermediate.setRGB(x, y, new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]).getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
    }

    public IP toRed() {
        return updatePixels(c -> {
            return new Color(c.getRed(), c.getRed(), c.getRed());
        });
    }

    public IP toGreen() {
        return updatePixels(c -> {
            return new Color(c.getGreen(), c.getGreen(), c.getGreen());
        });
    }

    public IP toBlue() {
        return updatePixels(c -> {
            return new Color(c.getBlue(), c.getBlue(), c.getBlue());
        });
    }

    public IP toHistogram() {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();

        int height = 100;
        var intermediate = new BufferedImage(255, height, BufferedImage.TYPE_INT_ARGB);

        float[] histogram = new float[256];
        for (var i = 0; i < 256; i++) {
            histogram[0] = 0;
        }

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));

                float[] hsv = Colors.rgb_to_hsv(original.getRed(), original.getGreen(), original.getBlue());

                histogram[(int) hsv[2]]++;

            }
        }

        // Normalize
        float max = 0;
        float secondMax = 0;
        for (int i = 0; i < 256; i++) {
            if (histogram[i] > max) {
                secondMax = max;
                max = histogram[i];
            }
        }

        if(max > secondMax * 2 && secondMax != 0){
            max = secondMax;
        }

        for (int i = 0; i < 256; i++) {
            histogram[i] /= max;
        }

        Graphics g = intermediate.getGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 255, height);

        for (int i = 0; i < 256; i++) {
            g.setColor(Color.BLACK);
            g.fillRect(i, 0, 1, (int) ((1 - histogram[i]) * height));
        }

        g.dispose();

        // intermediate.setRGB(x, y, new Color((int) rgb[0], (int) rgb[1], (int)
        // rgb[2]).getRGB());
        bufferedImage = intermediate;

        return this;
    }

    public IP bitSlice(int i) {

        updatePixels(c -> {
            int r = c.getRed();
            int r2 = r & i;
            r2 *= 255 / (float) i;

            int g = c.getGreen();
            int g2 = g & i;
            g2 *= 255 / (float) i;

            int b = c.getBlue();
            int b2 = b & i;
            b2 *= 255 / (float) i;
            return new Color(r2, g2, b2);
        });

        return this;
    }

    public float averagePixelError() {
        int sumError = 0;
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));

                sumError += original.getRed() + original.getGreen() + original.getBlue();

            }
        }

        float error = sumError / (float) (bw * bh);
        error /= 3; // Keeps it within [0,255]
        return error;
    }

    public IP exec(IIPLambda lambda) {
        lambda.lambda(this);
        return this;
    }

    public Color[] getColorsOrderedByFrequency(int maxValue) {
        var done = MyMath.sortValueReverse(getColorCounts());
        Color[] toReturn = new Color[Math.min(done.size(), maxValue)];

        for (int i = 0; i < toReturn.length; i++) {
            Map.Entry<Integer, Integer> me = (Map.Entry<Integer, Integer>) done.get(i);
            toReturn[i] = new Color(me.getKey());
        }

        return toReturn;

    }

    public Hashtable<Integer, Integer> getColorCounts() {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();

        Hashtable<Integer, Integer> colors = new Hashtable<>();

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));
                int intCode = original.getRGB();

                if (!colors.containsKey(intCode)) {
                    colors.put(intCode, 0);
                }
                colors.put(intCode, colors.get(intCode) + 1);
            }
        }

        return colors;
    }

    public int getColorCount() {

        return getColorCounts().size();
    }

    public IP updateToPalette(Color[] palette) {

        updatePixels(c -> {
            Color proposed = findClosestPaletteColor(c, palette);
            return proposed;
        });

        return this;
    }

    public Color findClosestPaletteColor(Color original, Color[] colorPalette) {
        int minIndex = findClosestPaletteColorIndex(original, colorPalette);
        return colorPalette[minIndex];
    }

    public int findClosestPaletteColorIndex(Color original, Color[] colorPalette) {
        int minDistance = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < colorPalette.length; i++) {
            int distance = MyMath.LInfDistance(original, colorPalette[i]);
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }
        return minIndex;
    }

    public Color[] kMeansColors(int count) {

        System.out.println("Doing k means with a size of " + count);

        Color[] paletteColors = new Color[count];
        ArrayList<Color>[] foundColors = new ArrayList[count];

        for (int i = 0; i < paletteColors.length; i++) {
            paletteColors[i] = MyMath.randomColor();
            foundColors[i] = new ArrayList<Color>();
        }

        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();

        ArrayList<Color> toChooseFrom = new ArrayList<>();
        for (int j = 0; j < 10; j++) {

            for (var y = 0; y < bh; y++) {
                for (var x = 0; x < bw; x++) {
                    if (x != y)
                        continue;
                    Color original = new Color(bufferedImage.getRGB(x, y));

                    toChooseFrom.add(original);
                }
            }
        }

        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < paletteColors.length; i++) {
                foundColors[i] = new ArrayList<Color>();
            }

            for (var k = 0; k < toChooseFrom.size(); k++) {
                Color original = toChooseFrom.get(k);

                int bestIndex = findClosestPaletteColorIndex(original, paletteColors);
                foundColors[bestIndex].add(original);
            }

            for (int i = 0; i < count; i++) {
                ArrayList<Color> colors = foundColors[i];
                if (colors.size() == 0) {
                    paletteColors[i] = MyMath.randomColor();
                    continue;
                }
                long averageR = 0;
                long averageG = 0;
                long averageB = 0;

                for (Color color : colors) {
                    averageR += color.getRed();
                    averageG += color.getGreen();
                    averageB += color.getBlue();
                }

                float r = averageR / (float) colors.size();
                float g = averageG / (float) colors.size();
                float b = averageB / (float) colors.size();

                // Move the color to the mean position
                paletteColors[i] = new Color((int) r, (int) g, (int) b);
            }
        }

        return paletteColors;
    }

    public IP updateToPaletteDithered(Color[] palette) {
        int bw = bufferedImage.getWidth();
        int bh = bufferedImage.getHeight();
        BufferedImage intermediate = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_ARGB);

        int errorRed = 0;
        int errorGreen = 0;
        int errorBlue = 0;

        Triple[][] errors = new Triple[bw][bh];

        for (int y = 0; y < bh; y++) {
            for (int x = 0; x < bw; x++) {
                errors[x][y] = new Triple();
            }
        }

        for (int y = 0; y < bh; y++) {
            for (int x = 0; x < bw; x++) {
                Color color = new Color(bufferedImage.getRGB(x, y));

                int[] colors = new int[3];
                Triple errorTriple = errors[x][y];
                colors[0] = color.getRed() - errorTriple.r;
                colors[1] = color.getGreen() - errorTriple.g;
                colors[2] = color.getBlue() - errorTriple.b;

                Color paletteColor = findClosestPaletteColor(colors, palette);

                errorRed = paletteColor.getRed() - colors[0];
                errorGreen = paletteColor.getGreen() - colors[1];
                errorBlue = paletteColor.getBlue() - colors[2];

                Triple newError = new Triple(errorRed, errorGreen, errorBlue);

                updateErrorMatrix(bw, bh, errors, x + 1, y + 0, 7 / 16f, newError);
                updateErrorMatrix(bw, bh, errors, x + 1, y + 1, 1 / 16f, newError);
                updateErrorMatrix(bw, bh, errors, x + 0, y + 1, 5 / 16f, newError);
                updateErrorMatrix(bw, bh, errors, x - 1, y + 1, 3 / 16f, newError);

                intermediate.setRGB(x, y, paletteColor.getRGB());
            }
        }

        bufferedImage = intermediate;
        return this;
    }

    private void updateErrorMatrix(int bw, int bh, Triple[][] errors, int i, int j, float scale, Triple triple) {
        if (i < 0 || j < 0 || i >= bw || j >= bh)
            return;
        errors[i][j].r += triple.r * scale;
        errors[i][j].g += triple.g * scale;
        errors[i][j].b += triple.b * scale;

    }

    private Color findClosestPaletteColor(int[] original, Color[] colorPalette) {
        int minDistance = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < colorPalette.length; i++) {
            int distance = MyMath.L1Distance(original, colorPalette[i]);
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }
        return colorPalette[minIndex];
    }

    public void saveAsPPM(String string) {

        StringBuffer toWrite = new StringBuffer("P3\n");
        toWrite.append(this.bufferedImage.getWidth() + "\n");
        toWrite.append(this.bufferedImage.getHeight() + "\n");
        toWrite.append("256\n");

        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();

        for (var y = 0; y < bh; y++) {
            for (var x = 0; x < bw; x++) {

                Color original = new Color(bufferedImage.getRGB(x, y));

                toWrite.append(original.getRed() + " ");
                toWrite.append(original.getGreen() + " ");
                toWrite.append(original.getBlue() + "\n");
            }
        }

        String path = string;
        try {
            Files.write(Paths.get(path), toWrite.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void saveAsBMP(String string) {

        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();

        ArrayList<Integer> bytes = new ArrayList<>();

        bytes.add(0x42);
        bytes.add(0x4D);
        bytes.addAll(Arrays.asList(new Integer[] { 0x46, 0x00, 0x00, 0x00 }));

        bytes.addAll(Arrays.asList(new Integer[] { 0x00, 0x00 }));// Application Specific 1
        bytes.addAll(Arrays.asList(new Integer[] { 0x00, 0x00 }));// Application Specific 2
        bytes.addAll(Arrays.asList(new Integer[] { 0x36, 0x00, 0x00, 0x00 }));// Offset where the pixel array can be
                                                                              // found

        bytes.addAll(Arrays.asList(new Integer[] { 0x28, 0x00, 0x00, 0x00 }));// Number of bytes in the DIB header (from
                                                                              // this point)
        bytes.addAll(Arrays.asList(new Integer[] { bw, 0x00, 0x00, 0x00 }));// Width of the bitmap in pixels
        bytes.addAll(Arrays.asList(new Integer[] { bh, 0x00, 0x00, 0x00 }));// Height of the bitmap in pixels. Positive
                                                                            // for bottom to top pixel order.
        bytes.addAll(Arrays.asList(new Integer[] { 0x01, 0x00 }));// Number of color planes being used
        bytes.addAll(Arrays.asList(new Integer[] { 0x20, 0x00 }));// Number of bits per pixel
        bytes.addAll(Arrays.asList(new Integer[] { 0x00, 0x00, 0x00, 0x00 }));// BI_RGB, no pixel array compression used
        bytes.addAll(Arrays.asList(new Integer[] { 0x10, 0x00, 0x00, 0x00 }));// Size of the raw bitmap data (including
                                                                              // padding)
        bytes.addAll(Arrays.asList(new Integer[] { 0x13, 0x0B, 0x00, 0x00 }));// Print resolution of the image,
                                                                              // horizontal
        bytes.addAll(Arrays.asList(new Integer[] { 0x13, 0x0B, 0x00, 0x00 }));// Print resolution of the image, vertical
        bytes.addAll(Arrays.asList(new Integer[] { 0x00, 0x00, 0x00, 0x00 }));// Number of colors in the palette
        bytes.addAll(Arrays.asList(new Integer[] { 0x00, 0x00, 0x00, 0x00 }));// 0 means all colors are important

        for (var y = bh - 1; y >= 0; y--) {
            for (var x = 0; x < bw; x++) {
                Color original = new Color(bufferedImage.getRGB(x, y));

                bytes.addAll(Arrays
                        .asList(new Integer[] { original.getBlue(), original.getGreen(), original.getRed(), 0xFF }));

            }
        }
        try (FileOutputStream fos = new FileOutputStream(string)) {
            for (var i : bytes) {
                int a = i;
                fos.write(new byte[] { (byte) a });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IP crop(int startX, int startY, int w, int h) {
        BufferedImage intermediate = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color color = new Color(bufferedImage.getRGB(x+startX, y+startY));

                

                intermediate.setRGB(x, y, color.getRGB());
            }
        }

        bufferedImage = intermediate;
        return this;
    }

}
