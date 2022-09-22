public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    new IP("download.jpg")
        .toHistogram()
        .save("histrogram.png");

    Colors.runTests();

  }

}