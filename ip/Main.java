package ip;

import java.awt.Color;

import helps.MutableFloat;
import helps.MutableInt;
import helps.lambdas.IColorToColor;

//Many examples are guarded by if(false) statements
//This suppressed warning means that we are doing this on purpose
@SuppressWarnings("unused")

/**
 * The entry point for our program
 */
public class Main {

  public static void main(String[] args) {
    // If there are any arguments, that means we are going to be running our test
    // suite
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    // If we get here, we are not doing a test run, but instead are doing things
    // manually.----------------------------------------

    // Numbers that we will pass into methods that we need updated
    MutableFloat mf = new MutableFloat(0);
    MutableInt colorCount = new MutableInt(0);
    MutableInt width = new MutableInt(-1);
    MutableInt height = new MutableInt(-1);

    // Actual image
    // processing-----------------------------------------------------------------------------------------------------

    //Grayscale and dither
    if(true){
      new ICon("./images/bull.jpg")
      .exec(i->i.toGrayscale())
      .exec(l -> {
            l.updateToPaletteDithered(new Color[] { Color.BLACK, Color.WHITE });
            return null;
          })
          .save("./out/dithered_bw.png");
    }


    if(false){//Kernels
      float[][] identityKernel = new float[3][3];
      identityKernel[0] = new float[]{0,0,0};
      identityKernel[1] = new float[]{0,1,0};
      identityKernel[2] = new float[]{0,0,0};
      Kernel kernel = new Kernel(identityKernel);

      new ICon("./images/DebugTiny.png")
      .exec(i->i.toGrayscale())
      .exec(i->i.applyKernel(kernel))
      .save("./out/DebugTiny_identity_kernel.png");


      //Trival Box Blur
      float[][] boxKernelFloats = new float[3][3];
      boxKernelFloats[0] = new float[]{1,1,1};
      boxKernelFloats[1] = new float[]{1,1,1};
      boxKernelFloats[2] = new float[]{1,1,1};
      Kernel boxKernel = new Kernel(boxKernelFloats).normalize();

      new ICon("./images/DebugTiny.png")
      .exec(i->i.toGrayscale())
      .exec(i->i.applyKernel(boxKernel))
      .save("./out/DebugTiny_box_kernel.png");

      //Larger Box Blur
      new ICon("./images/_test1.jpg")
      .exec(i->i.toGrayscale())
      .exec(i->i.applyKernel(boxKernel))
      .save("./out/test1_box_kernel.png");

      // Edge Detection
      float[][] edgeKernelVFloats = new float[3][1];
      edgeKernelVFloats[0] = new float[]{1};
      edgeKernelVFloats[1] = new float[]{0};
      edgeKernelVFloats[2] = new float[]{-1};
      Kernel edgeKernelV = new Kernel(edgeKernelVFloats);
      new ICon("./images/_test1.jpg")
      .exec(i->i.toGrayscale())
      .exec(i->i.applyKernel(edgeKernelV))
      .save("./out/_test1_edge_kernelV.png");

      //Edge Detection the other way
      float[][] edgeKernelHFloats = new float[1][3];
      edgeKernelHFloats[0] = new float[]{1,0,-1};
      Kernel edgeKernelH = new Kernel(edgeKernelHFloats);
      new ICon("./images/_test1.jpg")
      .exec(i->i.toGrayscale())
      .exec(i->i.applyKernel(edgeKernelH))
      .save("./out/_test1_edge_kernelH.png");

      //Sharpening
      float tune = 2f;
      float[][] sharpenKernelFloats = new float[3][3];
      sharpenKernelFloats[0] = new float[]{0,-1f/5f*tune,0};
      sharpenKernelFloats[1] = new float[]{-1f/5f*tune,1+tune-1f/5f*tune,-1f/5f*tune};
      sharpenKernelFloats[2] = new float[]{0,-1f/5f*tune,0};
      Kernel shapenKernel = new Kernel(sharpenKernelFloats);

      new ICon("./images/_test1.jpg")
      .exec(i->i.toGrayscale())
      .exec(i->i.applyKernel(shapenKernel))
      .save("./out/_test1_shapen_kernel.png");

      new ICon("./images/_test1.jpg")
      .exec(i->i.toGrayscale())
      .save("./out/_test1_bw.png");

      





    }


    if(false){//Haar transform

      new IP("./images/DebugTinyBW.png")
      .faceDetect()
      .save("./out/DebugTinyIntegralImage.png");
      // float[][] ii = new IP("./images/face.png").toIntegralImage();

    }
    if(false){
      //OCR Generation
      new IP("./images/alphabet.png")
      .cropORC();
    }
    if(false){
      //OCR
      new IP("./images/Z.png")
      .recognizeORC();
    }
    if(false){
      //OCR
      new IP("./images/A2.png")
      .recognizeORC();
    }
    if(false){
      //OCR
      new IP("./images/A3.png")
      .recognizeORC();
    }
    if(false){
      //OCR
      new IP("./images/A4.png")
      .recognizeORC();
    }

    if (false) { // Recenter as if it were a distribution
      //Try to auto-adjust the image based on the histogram

      new ICon("./images/_test1_under.png")
      .exec(i->i.toGrayscale())
      .appendHistogram()
      .save("./out/tiger_grayscale_under.png");

      new ICon("./images/_test1_under.png")
      .exec(i->i.toGrayscale())
      .exec(i -> i.centerHistogram())
      .appendHistogram()
      .save("./out/tiger_recenter_under.png");

      // new ICon("./images/_test1_over.png")
      // .exec(i -> i.centerHistogram())
      // .appendHistogram()
      // .save("./out/tiger_recenter_over.png");

      // new ICon("./images/_test1_over.png")
      // .appendHistogram()
      // .save("./out/tiger_over_h.png");

      // new ICon("./images/_test1_under.png")
      //     .appendHistogram()
      //     .save("./out/tiger_under_h.png");
      // new ICon("./images/_test1.jpg")
      //     .exec(i -> i.toGrayscale())
      //     .appendHistogram()
      //     .save("./out/tiger_grayscale_h.png");

      // new ICon("./images/_test1_under.png")
      //     .exec(i -> i.toGrayscale())
      //     .exec(i -> i.histogramEqualization())
      //     .appendHistogram()
      //     .save("./out/tiger_he.png");
    }

    if (false) { // Generate under/over with histograms
      // Generate under-exposed image
      new ICon("./images/_test1.jpg")
          .exec(i -> i.remapValueInt(x -> x / 2))
          .save("./out/_test1_under.png");

      // Generate over-exposed image
      new ICon("./images/_test1.jpg")
          .exec(i -> i.remapValueInt(x -> x / 2 + 127))
          .save("./out/_test1_over.png");

      // Show the over exposed image with its histogram
      new ICon("./images/_test1_over.png")
          .appendHistogram()
          .save("./images/_test1_over_h.png");

      // Show the under-exposed image with its histogram
      new ICon("./images/_test1_under.png")
          .appendHistogram()
          .save("./images/_test1_under_h.png");
    }

    if (false) { // Image Cropping
      // Crop the image to a small size
      new ICon("./images/_test1.jpg")
          .setAsHeight(height)
          .setAsWidth(width)
          .addLayer("./images/_test1.jpg")
          .exec(i -> i.crop(50, 50, 100, 100))
          .setCanvasSize(100, 100)
          .save("./out/cropped1.png");

      // Patchwork grayscale on an image

      new ICon("./images/_test1.jpg")
          .setAsHeight(height)
          .setAsWidth(width)
          .addLayer("./images/_test1.jpg")
          .exec(i -> i.crop(0, 0, 100, 100))
          .exec(i -> i.toGrayscale())
          .addLayer("./images/_test1.jpg")
          .exec(i -> i.crop(100, 100, 50, 50))
          .exec(i -> i.toGrayscale())
          .moveLayer(100, 100)
          .save("./out/cropped.png");
    }

    if (false) { // Histogram remapping
      new ICon("./images/_test1.jpg")
          .exec(i -> i.remap(c -> {
            float[] hsv = Colors.rgb_to_hsv(c);
            float value = hsv[2];

            value = 255 - value;

            float[] newRGB = Colors.hsvToRgb(hsv[0], hsv[1], value);

            return new Color((int) newRGB[0], (int) newRGB[1], (int) newRGB[2]);
          }))
          .save("./out/invert.png");
    }

    String grayscalePath = "./images/lung_infection.jpg";

    // ICon grayscale = new ICon(grayscalePath).exec(i -> i.toGrayscale());

    // ICon grayscaleHistogram = new ICon(grayscalePath)
    // .exec(i -> i.toGrayscale())
    // .appendHistogram().save("./out/grayscale.png");

    // grayscale
    // .exec(i -> i.remapValue(v -> 255 - v))
    // .save("./out/remapInverse.png");

    // grayscale
    // .exec(i -> i.remapValue(v -> {
    // return (int)(Math.log(v/255.0f + 1)*255);
    // }))
    // .save("./out/remapLog.png");

    // new ICon(grayscalePath)
    // .exec(i -> i.toGrayscale())
    // .exec(i -> i.remapValueFloat(v -> {
    // return (float) Math.pow(v, .5);
    // }))
    // .appendHistogram()
    // .save("./out/remapGammaRoot.png");

    // new ICon(grayscalePath)
    // .exec(i -> i.toGrayscale())
    // .exec(i -> i.remapValueFloat(v -> {
    // return (float) Math.pow(v, 2);
    // }))
    // .appendHistogram()
    // .save("./out/remapGammaSquared.png");

    // new ICon(grayscalePath)
    //     .exec(i -> i.toGrayscale())
    //     .exec(i -> i.remapValueFloat(v -> {
    //       float startX = .3f;
    //       float startY = .01f;
    //       float stopX = .9f;
    //       float stopY = .99f;
    //       float startSlope = startY / startX;
    //       float stopSlope = stopY / stopX;
    //       float middleSlope = (stopY - startY) / (stopX - startX);
    //       if (v < startX)
    //         return v * startSlope;
    //       if (v > stopX) {
    //         return (v - stopX) * stopSlope + stopX;
    //       }
    //       return (v - startX) * middleSlope + startX;
    //     }))
    //     .appendHistogram()
    //     .save("./out/remapPiecewise.png");

    /*
     * new ICon("./images/" + filename + ".jpg")
     * .setAsWidth(width)
     * .setAsHeight(height)
     * .addToCanvasSize(0, 100)
     * .generateLayer(l -> l.exec(i -> i.toHistogram()))
     * .moveLayer(0, height.getValue())
     * .exec(ip -> ip.scaleLinear(width.getValue() / 255.0f, 1))
     * .setBackgroundColor(Color.MAGENTA)
     * .save("./out/" + filename + "histogram.png");
     */

    // Examples of useage. To save time, these are guarded by if statements.
    // ----------------------------------------------------------
    // Change the if statements to if(true) to run them.

    // Fun with image formats
    if (false) {

      new IP("./images/_test1.jpg")
          .saveAsPPM("./out/ppm.ppm");

      new IP("./images/_test1_small.jpg")
          .saveAsBMP("./out/bmp.bmp");
    }

    // Fun with color reduction and dithering
    if (false) {

      // Get the number of colors in the image
      new ICon("./images/_test1.jpg")
          .exec(l -> {
            colorCount.setValue(l.getColorCount());
            return null;
          });
      System.out.println();
      System.out.println("The given image has " + colorCount.getValue() + " distinct colors.");

      // Get the most common colors
      var f = new ICon("./images/_test1.jpg")
          .flatten().getColorsOrderedByFrequency(10);

      System.out.println();
      System.out.println("The following are the most common colors in the given image:");
      for (int i = 0; i < f.length && i < 10; i++) {
        Color c = f[i];
        System.out.println(c);
      }
      int countColors = 256;

      // Use a reduced color palette
      new ICon("./images/_test1.jpg")
          .exec(l -> {
            l.updateToPalette(l.getColorsOrderedByFrequency(countColors));
            return null;
          })
          .save("./out/palette.png");

      // Use a random color palette
      new ICon("./images/_test1.jpg")
          .exec(l -> {
            l.updateToPalette(helps.MyMath.getRandomColors(countColors));
            return null;
          })
          .save("./out/palette-random.png");

      // Use a reduced color palette based on k-means
      new ICon("./images/_test1.jpg")
          .exec(l -> {
            l.updateToPalette(l.kMeansColors(countColors));
            return null;
          })
          .save("./out/k_means.png");

      // Simple Dithering
      new ICon("./images/_test1.jpg")
          .exec(l -> {
            l.updateToPaletteDithered(new Color[] { Color.BLACK, Color.WHITE });
            return null;
          })
          .save("./out/dithered_bw.png");

      // Simple Dithering Color
      new ICon("./images/_test1.jpg")
          .exec(l -> {
            l.updateToPaletteDithered(l.kMeansColors(countColors));
            return null;
          })
          .save("./out/dithered_color.png");

    }

    // Look at the error image generated by bit slicing.
    // Uncomment the save statements to save the error image.
    if (false) {
      new ICon("./images/_test1.jpg")
          .exec(i -> i.toGrayscale())
          .exec(i -> i.bitSlice(0b10000000))
          .addLayer("./images/_test1.jpg")
          .exec(i -> i.toGrayscale())
          .setLayerBlendmode(BlendMode.SubtractAbs)
          .flatten()
          .exec(l -> {
            mf.setValue(l.averagePixelError());
            return null;
          });
      // .save("./out/mf.png");

      System.out.println(Integer.toBinaryString(0b10000000));
      System.out.println(mf.value);

      // .save("./out/difference_image.png");

    }

    // Generate bit-sliced image at each distinct bit
    if (false) {
      for (int inc = 0; inc < 8; inc++) {
        int a = inc;
        new ICon("./images/_test1.jpg")
            .exec(i -> i.toGrayscale())
            .exec(i -> i.bitSlice((int) Math.pow(2, a)))
            .save("./out/bitSlicing" + a + ".png");
      }
    }

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

    // More bitslicing
    if (false) {
      for (int inc = 0; inc < 8; inc++) {
        int a = 0b11111111;
        a >>= inc;
        a <<= inc;
        int b = a;
        int c = inc;
        new ICon("./images/_test1.jpg")
            // .exec(i -> i.toGrayscale())
            .exec(i -> i.bitSlice(b))
            .save("./out/bitSlicing_" + c + ".png");

      }

    }

    // Get the cummulative bit sliced images
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

    // Examples of using blend modes
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

    // Generate images with their respective histograms appended.
    if (false)// True to generate histogram images
    {
      for (String filename : new String[] { "_test1", "_test2", "_test3", "_test4", "_test5" }) {
        new ICon("./images/" + filename + ".jpg")
            .setAsWidth(width)
            .setAsHeight(height)
            .addToCanvasSize(0, 100)
            .generateLayer(l -> l.exec(i -> i.toHistogram()))
            .moveLayer(0, height.getValue())
            .exec(ip -> ip.scaleLinear(width.getValue() / 255.0f, 1))
            .setBackgroundColor(Color.MAGENTA)
            .save("./out/" + filename + "histogram.png");
      }
    }

  }

}