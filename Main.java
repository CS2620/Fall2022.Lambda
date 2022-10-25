import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    MutableFloat mf = new MutableFloat(0);
    MutableInt colorCount = new MutableInt(0);

    // new ICon("./images/_test1.jpg")
    // .exec(l -> {
    // colorCount.setValue(l.getColorCount());
    // return null;
    // });
    // System.out.println(colorCount.getValue());

    // var f = new ICon("./images/_test1.jpg")
    // .flatten().getColorsOrderedByFrequency();

    // for (int i = 0; i < f.length && i < 10; i++) {
    // Color c = new Color((Integer) f[i]);
    // System.out.println(c);
    // }
    // System.out.println(colorCount.getValue());

    // Use a reduced color palette
    new ICon("./images/_test1.jpg")
        .exec(l -> {
          l.updateToPallette(l.getColorsOrderedByFrequency(10));
          return null;
        })
        .save("./out/palette.png");

    //Use a reduced color palette based on k-means
    new ICon("./images/_test1.jpg")
        .exec(l -> {
          l.updateToPallette(l.kMeansColors(128));
          return null;
        })
        .save("./out/k_means.png");

    // new ICon("./images/_test1.jpg")
    // .exec(i -> i.toGrayscale())
    // .exec(i -> i.bitSlice(0b10000000))
    // .addLayer("./images/_test1.jpg")
    // .exec(i -> i.toGrayscale())
    // .setLayerBlendmode(BlendMode.SubtractAbs)
    // .flatten()
    // .exec(l -> {
    // mf.setValue(l.averagePixelError());
    // return null;
    // });
    // .save("./out/mf.png");

    // System.out.println(Integer.toBinaryString(0b10000000));
    // System.out.println(mf.value);

    // .save("./out/difference_image.png");

    // if(true){
    // for(int inc = 0; inc < 8; inc++){
    // int a = inc;
    // new ICon("./images/_test1.jpg")
    // .exec(i -> i.toGrayscale())
    // .exec(i->i.bitSlice((int)Math.pow(2, a)))
    // .save("./out/bitSlicing" + a + ".png");
    // }

    // }

    // Get the error values for cummulative bit slices
    if (false) {
      for (int inc = 0; inc < 8; inc++) {
        int a = 0b11111111;
        a >>= inc;
        a <<= inc;
        int b = a;
        int c = inc;
        new ICon("./images/_test1.jpg")
            .exec(i -> i.toGrayscale())
            .exec(i -> i.bitSlice(b))
            .addLayer("./images/_test1.jpg")
            .exec(i -> i.toGrayscale())
            .setLayerBlendmode(BlendMode.SubtractAbs)
            .save("./out/bitSlicing_error_" + c + ".png")
            .flatten()
            .exec(i -> {
              mf.setValue(i.averagePixelError());
              return null;
            });

        System.out.println();
        System.out.println(b);
        System.out.println(c);
        String binaryString = Integer.toBinaryString(b);
        System.out.println(binaryString);
        System.out.println(mf);
      }

    }

    if (true) {
      for (int inc = 0; inc < 8; inc++) {
        int a = 0b11111111;
        a >>= inc;
        a <<= inc;
        int b = a;
        int c = inc;
        new ICon("./images/_test1.jpg")
            //.exec(i -> i.toGrayscale())
            .exec(i -> i.bitSlice(b))
            .save("./out/bitSlicing_" + c + ".png");

      }

    }

    if (false) {
      for (int inc = 0; inc < 8; inc++) {
        int a = (int) (Math.pow(2, inc) - 1);
        int b = a;
        int c = inc;
        new ICon("./images/_test1.jpg")
            // .exec(i -> i.toGrayscale())
            .exec(i -> i.bitSlice(b))
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