package jaron.pde;

import jaron.gui.Colors;
import jaron.gui.Rect;
import processing.core.PApplet;

/**
 * The <code>Label</code> class provides the functionality for displaying a line
 * of Text in the Processing Development Environment.<br>
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class Label {
  /**
   * Text alignment.
   */
  public static final int HORIZONTAL = 0;
  /**
   * Text alignment.
   */
  public static final int VERTICAL = 1;
  private int textOrientation = HORIZONTAL;
  private PApplet applet;
  private Rect frame;
  private int thicknessFrame = 1;
  private int colorFrame = Colors.BLACK;
  private int colorBackground = Colors.GRAY_BACKGROUND;
  private int colorText = Colors.BLACK;
  private String text;

  /**
   * Creates a new <code>Label</code> object for the Processing Development Environment
   * (PDE) at a certain position and with a certain width and height.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param text      the text to be displayed
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height
   * @param width     the component's width
   */
  public Label(PApplet applet, String text, int left, int top, int width, int height) {
    this.applet = applet;
    this.text = text;
    frame = new Rect(left, top, width, height);
  }

  /**
   * Creates a new <code>Label</code> object for the Processing Development Environment
   * (PDE) at a certain position.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param text      the text to be displayed
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Label(PApplet applet, String text, int left, int top) {
    this(applet, text, left, top, 150, 20);
  }

  /**
   * Sets the label's background color.
   * 
   * @param colorBackground the background color
   * 
   * @see Colors
   */
  public void setColorBackground(int colorBackground) {
    this.colorBackground = colorBackground;
  }

  /**
   * Sets the color of the label's outer frame.
   * 
   * @param colorFrame the frame color
   * 
   * @see Colors
   */
  public void setColorFrame(int colorFrame) {
    this.colorFrame = colorFrame;
  }

  /**
   * Sets the label's text color.
   * 
   * @param colorText the text color
   * 
   * @see Colors
   */
  public void setColorText(int colorText) {
    this.colorText = colorText;
  }

  /**
   * Sets the text orientation. Possible orientations are horizontal (default)
   * or vertical.
   * 
   * @param textOrientation vertical or horizontal orientation
   * 
   * @see Label#HORIZONTAL
   * @see Label#VERTICAL
   */
  public void setTextOrientation(int textOrientation) {
    this.textOrientation = textOrientation;
  }

  /**
   * Sets the thickness of the label's outer frame.
   * @param thicknessFrame frame thickness in pixels
   */
  public void setThicknessFrame(int thicknessFrame) {
    this.thicknessFrame = thicknessFrame;
  }

  /**
   * Draws the label to the screen. This method should usually be called from
   * the <code>draw</code> method of the Processing Development Environment.
   */
  public void draw() {
    // draw the frame
    applet.fill(colorBackground);
    applet.stroke(colorFrame);
    applet.strokeWeight(thicknessFrame);
    applet.rect(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
    
    // draw text
    applet.pushMatrix();
    applet.fill(colorText);
    applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.LEFT);
    if (textOrientation == HORIZONTAL) {
      applet.translate(frame.getLeft(), frame.getTop());
    }
    else {
      applet.translate(frame.getLeft(), frame.getTop() + frame.getHeight());
      applet.rotate(PApplet.radians(270));
    }
    applet.text(text, Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    applet.popMatrix();
  }
}