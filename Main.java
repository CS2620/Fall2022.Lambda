import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    new ICon("./images/_test1.jpg")
        .exec(i -> i.toGrayscale())
        .exec(i->i.bitSlice(0b01000000))
        .save("./out/bitSlicing.png");

    if(true){
      for(int inc = 0; inc < 8; inc++){
        int a = inc;
        new ICon("./images/_test1.jpg")
        .exec(i -> i.toGrayscale())
        .exec(i->i.bitSlice((int)Math.pow(2, a)))
        .save("./out/bitSlicing" + a + ".png");
      }
      
    }

    if(true){
      for(int inc = 0; inc < 8; inc++){
        int a = 0b11111111;
        a >>= inc;
        a <<= inc;
        int b = a;
        int c = inc;
        new ICon("./images/_test1.jpg")
        .exec(i -> i.toGrayscale())
        .exec(i->i.bitSlice(b))
        .save("./out/bitSlicing_" + c + ".png");
      }
      
    }

    if(true){
      for(int inc = 0; inc < 8; inc++){
        int a = (int)(Math.pow(2, inc)-1);
        int b = a;
        int c = inc;
        new ICon("./images/_test1.jpg")
        .exec(i -> i.toGrayscale())
        .exec(i->i.bitSlice(b))
        .save("./out/bitSlicing__" + c + ".png");
      }
      
    }

    AtomicInteger width = new AtomicInteger(-1);
    AtomicInteger height = new AtomicInteger(-1);

    if (false) {

      new ICon("./images/_test2.jpg")
          .addLayer("./images/gradient.jpg")
          .setLayerAlpha(1f)
          .setLayerBlendmode(BlendMode.Divide)
          // .setAsWidth(width)
          // .setAsHeight(height)
          // .addToCanvasSize(0, 100)
          // .generateLayer(l -> l.exec(i -> i.toHistogram()))
          // .moveLayer(0, height.get())
          // .exec(ip -> ip.scaleLinear(width.get() / 255.0f, 1))
          // .setBackgroundColor(Color.MAGENTA)
          .save("./out/done.png");

    }

    if (false)// True to run the color space conversion tests
      Colors.runTests();

    if (false)// True to generate histogram images
    {
      for (String filename : new String[] { "_test1", "_test2", "_test3", "_test4", "_test5" }) {
        new ICon("./images/" + filename + ".jpg")
            .setAsWidth(width)
            .setAsHeight(height)
            .addToCanvasSize(0, 100)
            .generateLayer(l -> l.exec(i -> i.toHistogram()))
            .moveLayer(0, height.get())
            .exec(ip -> ip.scaleLinear(width.get() / 255.0f, 1))
            .setBackgroundColor(Color.MAGENTA)
            .save("./out/" + filename + "histogram.png");
      }
    }

  }

}