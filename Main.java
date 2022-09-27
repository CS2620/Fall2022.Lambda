import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    // new IP("./images/_test1.jpg")
    // .scaleNN(2, 1)
    // .save("./out/scaleTest.png");

    AtomicInteger width = new AtomicInteger(-1);
    AtomicInteger height = new AtomicInteger(-1);

    new ICon("./images/_test2.jpg")
        .setAsWidth(width)
        .setAsHeight(height)
        .addToCanvasSize(0, 100)
        .generateLayer(l -> l.exec(i -> i.toHistogram()))
        .moveLayer(0, height.get())
        .exec(ip -> ip.scaleLinear(width.get() / 255.0f, 1))
        .setBackgroundColor(Color.MAGENTA)
        .save("./out/done.png");

    if (false)// True to run the color space conversion tests
      Colors.runTests();

    if (true)// True to generate histogram images
    {
    for (String filename : new String[] { "_test1"/*, "_test2", "_test3", "_test4", "_test5"*/ }) {
        new ICon("./images/" + filename + ".jpg")
            .setAsWidth(width)
            .setAsHeight(height)
            .addToCanvasSize(0, 100*4)
            .generateLayer(l -> l.exec(i -> i.toHistogram()))
            .moveLayer(0, height.get())
            .exec(ip -> ip.scaleLinear(width.get() / 255.0f, 1))
            .setBackgroundColor(Color.MAGENTA)
            .save("./out/" + filename + "histogram.png");
      }
    }

  }

}