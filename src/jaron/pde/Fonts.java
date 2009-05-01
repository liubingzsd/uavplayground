package jaron.pde;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * The <code>Fonts</code> class is a helper class for the Processing Development
 * Environment (PDE) font handling. Its methods are all accessed in a static way.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class Fonts {
  /**
   * The standard font size.
   */
  public static final int FONT_SIZE = 12;
  
  /**
   * The spacing between lines of text. 
   */
  public static final int LINE_SPACING = 4;
  
  /**
   * The height of a single line of text. Its value is the result of the font size
   * plus the line spacing. 
   */
  public static final int LINE_HEIGHT = FONT_SIZE + LINE_SPACING;
  
  private static PFont fontBold = null;
  private static PFont fontPlain = null;
  
  /**
   * Returns a <code>PFont</code> object of the bold font style.
   * 
   * @param applet  a reference to the the PDE applet
   * @return        a <code>PFont</code> object
   */
  public static PFont getFontBold(PApplet applet) {
    if (fontBold == null) {
      fontBold = applet.loadFont("data/Dialog.bold-12.vlw");
    }
    return fontBold;
  }
  
  /**
   * Returns a <code>PFont</code> object of the normal font style.
   * 
   * @param applet  a reference to the the PDE applet
   * @return        a <code>PFont</code> object
   */
  public static PFont getFontPlain(PApplet applet) {
    if (fontPlain == null) {
      fontPlain = applet.loadFont("data/Dialog.plain-12.vlw");
    }
    return fontPlain;
  }
}
