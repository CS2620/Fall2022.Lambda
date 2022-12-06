package ip;

public class OCRFeatures {
  float percentWhite = 0;
  String file = "";
  public float distanceTo(OCRFeatures myOCR) {
    return Math.abs(this.percentWhite - myOCR.percentWhite);
  }
}
