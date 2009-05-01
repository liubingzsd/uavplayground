package jaron.pde;

import processing.core.PApplet;

import jaron.components.Signal;
import jaron.gui.Colors;
import jaron.gui.Panel;

import java.util.TreeMap;

/**
 * The <code>Display</code> class provides a graphical component for the Processing Development
 * Environment (PDE). Depending on its selected height it can display n lines of data. A line
 * consists of a label, describing the data, an the data (<code>double</code>) itself. New lines
 * are added via <code>createTextLine</code> which returns an object of type <code>Signal</code>. The
 * signal can be hooked to other signals via the <code>EventListener</code> mechanism.<br>
 * <br>
 * Have a look at the example of the {@link jaron.uavsim.UAVsim} for the usage of the
 * <code>Display</code>.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class Display extends Panel {
  private PApplet applet;
  private TreeMap<String, Signal> lines =  new TreeMap<String, Signal>();

  /**
   * Creates a new <code>Display</code> object for the Processing Development Environment
   * (PDE) at a certain position and with a certain width and height.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height including the labels
   * @param width     the component's width including the labels
   */
  public Display(PApplet applet, int left, int top, int width, int height) {
    super(left, top, width, height);
    this.applet = applet;
  }

  /**
   * Creates a new <code>Display</code> object for the Processing Development Environment
   * (PDE) at a certain position.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Display(PApplet applet, int left, int top) {
    this(applet, left, top, 150, 150);
  }

  /**
   * Draws the Display to the screen. All the lines that have been added with
   * <code>createTextLine</code> are listed from top to bottom in alphabetical
   * order.
   */
  public void draw() {
    // initialize the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
    
    // draw the display frame
    applet.rect(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
    
    //draw all the lines of text
    applet.pushMatrix();
    applet.translate(content.getLeft(), content.getTop());
    applet.fill(Colors.TEXT);
    int y = Fonts.LINE_HEIGHT;
    for (String key : lines.keySet()) {
      Signal line = lines.get(key);
      applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
      applet.textAlign(PApplet.LEFT);
      applet.text(key, Fonts.LINE_SPACING, y);
      applet.textFont(Fonts.getFontPlain(applet), Fonts.FONT_SIZE);
      applet.textAlign(PApplet.RIGHT);
      applet.text(String.format("%1.2f", line.getValue()), (int )content.getWidth() - Fonts.LINE_SPACING, y);
      y += Fonts.LINE_HEIGHT;
    }
    applet.popMatrix();

    // restore the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
    applet.textAlign(PApplet.LEFT);
  }

  /**
   * Creates and returns a new line. The line is of type <code>Signal</code> and is
   * later used to display a line of text.
   *  
   * @param   label the label of the line's value
   * @return  a new <code>Signal</code> object representing a line's value
   */
  public Signal createTextLine(String label) {
    Signal line = new Signal();
    lines.put(label, line);
    return line;
  }
}
