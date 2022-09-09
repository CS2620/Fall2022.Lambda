import java.awt.Color;
import java.awt.image.BufferedImage;

public interface IUpdateLambda {
  Color toColor(BufferedImage bi, int bw, int bh, int x, int y);
  
}
