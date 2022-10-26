package ip;

import java.util.HashMap;
import java.util.Map;

public class Test {
  public static void handleArguments(String[] args) {
    Map<String, IP> allImages = new HashMap<>();
    String source1 = "./images/_test1.jpg";
    String source2 = "./images/_test2.jpg";
    allImages.put("IP-Rotate 180", new IP(source1).rotate180());
    allImages.put("IP-Rotate 90", new IP(source1).rotate90());
    allImages.put("IP-To Grayscale", new IP(source1).toGrayscale());
    allImages.put("IP-TranslateForward", new IP(source1).translateForward(100, 100));
    allImages.put("IP-Translate", new IP(source1).translate(100, 00));
    allImages.put("IP-ScaleLinear", new IP(source1).scaleLinear(2));
    allImages.put("IP-ScaleNN", new IP(source1).scaleNN(2));
    allImages.put("IP-Translate 45 Linear", new IP(source1).rotate(45, true));
    allImages.put("IP-Translate 45 NN", new IP(source1).rotate(45, false));
    allImages.put("ICon-Basic", new ICon(source1).flatten());
    allImages.put("ICon-Offset", new ICon(new Layer(source1, 50, 50)).flatten());
    allImages.put("ICon-MultipleLayers", new ICon(new Layer(source2)).addLayer(source1).flatten());
    allImages.put("ICon-LayersTopGrayscale", new ICon(new Layer(source2)).addLayer(source1).exec(x->x.toGrayscale()).flatten());
    allImages.put("ICon-LayersBottomGrayscale", new ICon(new Layer(source2)).addLayer(source1).selectLayer(0).exec(x->x.toGrayscale()).flatten());

    if (args[0].equals("test") || args[0].equals("-t")) {
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

    } else if (args[0].equals("generateTests") || args[0].equals("-g")) {
      for (String key : allImages.keySet()) {
       //System.out.println(key);
        var saveName = filizeName(key);
        allImages.get(key).save(saveName);
      }
    } else {
      System.err.println("Unrecognized argument. The valid arguments are `test or -t` or 'generateTests or -g`");
    }
  }

  private static String filizeName(String name) {
    String tempName = name.replaceAll(" ", "_");
    return "./testImages/" + tempName + ".png";
  }
  
}
