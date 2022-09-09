import java.util.HashMap;
import java.util.Map;

public class Test {
  public static void handleArguments(String[] args) {
    Map<String, IP> allImages = new HashMap<>();
    String source = "./testImages/download.jpg";
    allImages.put("Rotate 180", new IP(source).rotate180());
    allImages.put("Rotate 90", new IP(source).rotate90());
    allImages.put("To Grayscale", new IP(source).toGrayscale());
    allImages.put("TranslateForward", new IP(source).translateForward(100, 100));
    allImages.put("Translate", new IP(source).translate(100, 00));
    allImages.put("ScaleLinear", new IP(source).scaleLinear(2));
    allImages.put("ScaleNN", new IP(source).scaleNN(2));
    allImages.put("Translate 45 Linear", new IP(source).rotate(45, true));
    allImages.put("Translate 45 NN", new IP(source).rotate(45, false));

    if (args[0].equals("test")) {
      int countPassed = 0;
      int countTotal = 0;
      for (String key : allImages.keySet()) {
        //System.out.println(key);
        var saveName = filizeName(key);
        boolean result = allImages.get(key).compareTo(saveName);
        countTotal++;
        if (result) {
          System.out.println(key + " - passed");
          countPassed++;
        } else {
          System.out.println(key + " - failed");
        }
      }
      System.out.println("Passed " + countPassed + " of " + countTotal + " tests.");

    } else if (args[0].equals("generateTests")) {
      for (String key : allImages.keySet()) {
       //System.out.println(key);
        var saveName = filizeName(key);
        allImages.get(key).save(saveName);
      }
    } else {
      System.err.println("Unrecognized argument");
    }
  }

  private static String filizeName(String name) {
    String tempName = name.replaceAll(" ", "_");
    return "./testImages/" + tempName + ".png";
  }
  
}