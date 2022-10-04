//See https://photoblogstop.com/photoshop/photoshop-blend-modes-explained
//See also 11.3.3 from https://opensource.adobe.com/dc-acrobat-sdk-docs/standards/pdfstandards/pdf/PDF32000_2008.pdf
public enum BlendMode {
  /**
   * The upper layer color is used without consideration of the lower layer
   */
  Normal, 

  /**
   * For each channel, add the upper and lower layer values
   */
  Add, 

  /**
   * For each channel, subtract the uppper from the lower
   */
  Subtract, 

  /**
   * For each channel, keep the lesser of the two values
   */
  Darken,

  /**
   * For each channel, keep the greater of the two values
   */
  Lighten,

  DarkerColor,
  LighterColor, Multiply, Divide,

}
