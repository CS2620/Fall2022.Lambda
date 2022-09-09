import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class IP {

    BufferedImage bufferedImage;

    public IP(String filename) {
        try {
            this.bufferedImage = ImageIO.read(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IP updatePixels(IColorToColor lambda) {
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {

                Color color = new Color(bufferedImage.getRGB(x, y));
                Color newColor = lambda.toColor(color);

                bufferedImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return this;
    }

    public IP toGrayscale() {
        return updatePixels(c -> {
            int gray = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
            return new Color(gray, gray, gray);
        });
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
        return this;
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
        return this;
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
            if(MyMath.inBounds(bw,bh, x1, y1)){
                return new Color(bi.getRGB(x1, y1));
            }
            return Color.MAGENTA;
        });
    }

    public IP scaleLinear(float scale) {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();
        var nw = (int) (bw * scale + .5);
        var nh = (int) (bh * scale + .5);
        var intermediate = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);

        for (var y = 0; y < nh; y++) {
            for (var x = 0; x < nw; x++) {

                float originalX = (x / scale);
                float originalY = (y / scale);

                Color color;
                if (!MyMath.inBounds(bw, bh, (int)originalX, (int)originalY))
                    color = Color.MAGENTA;
                else {
                    color = getBilinear(bufferedImage, originalX, originalY, Color.WHITE);
                }
                intermediate.setRGB(x, y, color.getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
    }

    public IP scaleNN(float scale) {
        var bw = bufferedImage.getWidth();
        var bh = bufferedImage.getHeight();
        var nw = (int) (bw * scale + .5);
        var nh = (int) (bh * scale + .5);
        var intermediate = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);

        for (var y = 0; y < nh; y++) {
            for (var x = 0; x < nw; x++) {

                var originalX = (int) (x / scale);
                var originalY = (int) (y / scale);

                Color color;
                if (originalX >= bw || originalY >= bh || originalX < 0 || originalY < 0)
                    color = Color.MAGENTA;

                else
                    color = getNN(bufferedImage, originalX, originalY, Color.CYAN);

                intermediate.setRGB(x, y, color.getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
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

                    var color00 = new Color(bufferedImage.getRGB((int) newX, (int) newY));
                    Color color10;
                    Color color01;
                    Color color11;

                    if (newX + 1 < bw)
                        color10 = new Color(bufferedImage.getRGB((int) newX + 1, (int) newY));
                    else
                        color10 = Color.WHITE;
                    if (newY + 1 < bh) {
                        color01 = new Color(bufferedImage.getRGB((int) newX, (int) newY + 1));
                    } else
                        color01 = Color.WHITE;

                    if (newX + 1 < bw && newY + 1 < bh)
                        color11 = new Color(bufferedImage.getRGB((int) newX + 1, (int) newY + 1));
                    else {
                        color11 = Color.WHITE;
                    }

                    float percentX = newX - (int) newX;
                    float percentY = newY - (int) newY;

                    var interpolationX1 = interpolate(color00, color10, percentX);
                    var interpolationX2 = interpolate(color01, color11, percentX);

                    var interpolationY = interpolate(interpolationX1, interpolationX2, percentY);
                    if (linearInterpolation)
                        color = interpolationY;
                    else
                        color = color00;
                }
                intermediate.setRGB(x, y, color.getRGB());
            }
        }

        bufferedImage = intermediate;

        return this;
    }

    public IP save(String filename) {
        try {
            ImageIO.write(bufferedImage, "PNG", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return this;

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

    private Color getBilinear(BufferedImage inImage, float x, float y, Color border){
        var color00 = new Color(bufferedImage.getRGB((int) x, (int) y));
        Color color10;
        Color color01;
        Color color11;

        if ((int)x + 1 < inImage.getWidth())
            color10 = new Color(bufferedImage.getRGB((int) x + 1, (int) y));
        else
            color10 = Color.WHITE;
        if (y + 1 < inImage.getHeight()) {
            color01 = new Color(bufferedImage.getRGB((int) x, (int) y + 1));
        } else
            color01 = Color.WHITE;

        if (x + 1 < inImage.getWidth() && y + 1 < inImage.getHeight())
            color11 = new Color(bufferedImage.getRGB((int) x + 1, (int) y+ 1));
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

    private Color getNN(BufferedImage inImage, int x, int y, Color border){
        if(MyMath.inBounds(inImage.getWidth(), inImage.getHeight(), x, y)){
            return new Color(inImage.getRGB(x, y));
        }
        return border;
    }

    private Color interpolate(Color one, Color two, float percent) {

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

}
