public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    new IP("download.jpg")
        .rotate90()
        .save("done.png");
  }
}