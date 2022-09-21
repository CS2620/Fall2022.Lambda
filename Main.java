public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    new IP("download.jpg")
        .scaleNN(2)
        .save("done.png");

  }
  
}