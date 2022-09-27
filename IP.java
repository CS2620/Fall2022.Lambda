import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class IP extends IPBase {
    public IP(String filename) {
        super();
        try {
            this.bufferedImage = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IP(BufferedImage bi){
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

        var _bw = bufferedImage.getWidth();
        var _bh = bufferedImage.getHeight();
        var nw = (int) (_bw * scale + .5);
        var nh = (int) (_bh * scale + .5);
        return updateImageAndSize(nw, nh, (bi, bw, bh, x, y) -> {

            float originalX = (x / scale);
            float originalY = (y / scale);

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

        var _bw = bufferedImage.getWidth();
        var _bh = bufferedImage.getHeight();
        var nw = (int) (_bw * scale + .5);
        var nh = (int) (_bh * scale + .5);
        return updateImageAndSize(nw, nh, (bi, bw, bh, x, y) -> {

            float originalX = (x / scale);
            float originalY = (y / scale);

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
        for (int i = 0; i < 256; i++) {
            if (histogram[i] > max) {
                max = histogram[i];
            }
        }

        for (int i = 0; i < 256; i++) {
            histogram[i] /= max;

        }

        Graphics g = intermediate.getGraphics();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 255, height);

        for (int i = 0; i < 256; i++) {
            g.setColor(Color.WHITE);
            g.fillRect(i, 0, 1, (int)(histogram[i]*height));
        }

        g.dispose();

        // intermediate.setRGB(x, y, new Color((int) rgb[0], (int) rgb[1], (int)
        // rgb[2]).getRGB());
        bufferedImage = intermediate;

        return this;
    }
}
