package ip;
import java.awt.Color;
import java.awt.image.BufferedImage;

import helps.lambdas.IIPLambda;


public class Layer {
  IP ip;
  int offsetX = 0;
  int offsetY = 0;
  float alpha = 1; //0 is full transparency, 1 is full opacity
  BlendMode blendMode = BlendMode.Normal;
  Color backgroundColor = Color.BLACK;

  public Layer(String filename){
    this(new IP(filename));
  }

  public Layer(String filename, int offsetX, int offsetY){
    this(new IP(filename), offsetX, offsetY);
  }

  public Layer(BufferedImage bi, int offsetX, int offsetY){
    this(new IP(bi), offsetX, offsetY, 1, Color.BLACK);
  }

  public Layer(IP ip){
    this(ip, 0, 0, 1, Color.BLACK);
  }

  public Layer(IP ip, int offsetX, int offsetY){
    this(ip, offsetX, offsetY, 1, Color.BLACK);
  }

  public Layer(Layer layer){
    this(layer.ip.clone(), layer.offsetX, layer.offsetY, layer.alpha, layer.backgroundColor);
  }

  public Layer(IP ip, int offsetX, int offsetY, float alpha, Color backgroundColor){
    this.ip = ip;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.alpha = alpha;
    this.backgroundColor = backgroundColor;
  }

  public Layer clone(){
    return new Layer(this);
  }

  public Layer exec(IIPLambda lambda){
    lambda.lambda(ip);
    return this;
  }
}
