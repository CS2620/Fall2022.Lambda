public class Main {

  public static void main(String[] args) {
    if (args.length > 0) {
      Test.handleArguments(args);
      return;
    }

    // new IP("download.jpg")
    // .toHistogram()
    // .save("histrogram.png");

    // new ICon("./images/_test2.jpg")
    //     .addLayer("./images/_test1.jpg")
    //     .selectLayer(0)
    //     .exec(x->x.toGrayscale())
    //     .save("./out/done.png");

    new ICon("./images/_test1.jpg")
      .generateLayer(l->l.exec(i->i.toHistogram()))
      .save("./out/done.png");

    if (false)// True to run the color space conversion tests
      Colors.runTests();

  }

}