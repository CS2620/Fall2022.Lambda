public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    new IP("download.jpg")
        .translate(100, 100)
        .save("done.png");
  }
}