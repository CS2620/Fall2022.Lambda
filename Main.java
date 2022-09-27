public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    // new IP("download.jpg")
    //     .toHistogram()
    //     .save("histrogram.png");

    new ICon("./testImages/_test2.jpg").addLayer("./testImages/_test1.jpg").save("done.png");

    if(false)//True to run the color space conversion tests
        Colors.runTests();

  }

}