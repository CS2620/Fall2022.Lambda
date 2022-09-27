public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    // new IP("download.jpg")
    //     .toHistogram()
    //     .save("histrogram.png");

    new ICon("download.jpg", -50,  -50).save("done.png");

    if(false)//True to run the color space conversion tests
        Colors.runTests();

  }

}