import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * The Image Container class
 * 
 */
public class ICon {
  List<Layer> layers = new ArrayList<>();

  int width;
  int height;
  Color backgroundColor = Color.BLACK;

  public ICon(String filename){
    this(new IP(filename));
  }

  public ICon(String filename, int offsetX, int offsetY){
    this(new Layer(filename, offsetX, offsetY));
  }

  public ICon(IP ip){
    this(new Layer(ip));
  }

  public ICon(Layer layer){
    layers.add(layer);
    width = layer.ip.bufferedImage.getWidth();
    height = layer.ip.bufferedImage.getHeight();
  }

  public ICon save(String filename){
    var ip = this.flatten();
    ip.save(filename);
    return this;
  }

  public IP flatten(){
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    
    //Fill in the background
    Graphics g = bi.getGraphics();
    g.setColor(this.backgroundColor);
    g.fillRect(0, 0, width, height);
    g.dispose();

    for(int i = 0; i < layers.size(); i++){
      for(int y = 0; y < height; y++){
        for(int x = 0; x < width; x++){
          var layer = layers.get(i);
          int ix = x - layer.offsetX;
          int iy = y - layer.offsetY;
          var ip = layer.ip;
          if(!ip.isValidCoordinate(ix, iy)) continue;
          int rgb = ip.bufferedImage.getRGB(ix,iy);
          bi.setRGB(x,y,rgb);
        }
      }
    }

    return new IP(bi);
    
  }
  
}
