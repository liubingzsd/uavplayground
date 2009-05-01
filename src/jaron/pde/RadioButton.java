package jaron.pde;

import jaron.components.Signal;
import jaron.gui.Colors;
import jaron.gui.Panel;

import java.awt.geom.Rectangle2D;

import processing.core.PApplet;

/**
 * The <code>RadioButton</code> class provides a virtual on-off switch for
 * the Processing Development Environment (PDE).<br>
 * By extending the <code>Signal</code> class it can hook into the
 * <code>EventListener</code> mechanism and thus receive and send numerical
 * values.<br>
 * <br>
 * Have a look at the example of the {@link jaron.uavsim.UAVsim} for the usage of the
 * <code>RadioButton</code>.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class RadioButton extends Signal {
  protected PApplet applet;
  protected Panel panel;
  protected String label = "";
  protected Boolean mousePressed = false;
  protected Boolean mouseOver = false;
  
  /**
   * Creates a new <code>RadioButton</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param label     the label that is displayed at the bottom
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param width     the component's width
   * @param height    the component's height
   */
  public RadioButton(PApplet applet, String label, int left, int top, int width, int height) {
    super(0);
    this.applet = applet;
    setLabel(label);
    setBandwidth(0, 1);
    setValue(getLow()); // default state is OFF
    panel = new Panel(left, top, width, height);
    panel.setLabelBottomHeight(Fonts.LINE_HEIGHT + Fonts.LINE_SPACING);
  }
  
  /**
   * Creates a new <code>RadioButton</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param width     the component's width
   * @param height    the component's height
   */
  public RadioButton(PApplet applet, int left, int top, int width, int height) {
    this(applet, "", left, top, width, height);
  }

  /**
   * Creates a new <code>RadioButton</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param label     the label that is displayed at the bottom
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public RadioButton(PApplet applet, String label, int left, int top) {
    this(applet, label, left, top, 60, 40);
  }

  /**
   * Creates a new <code>RadioButton</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public RadioButton(PApplet applet, int left, int top) {
    this(applet, left, top, 60, 40);
  }

  /**
   * Draws the button to the screen. This method should usually be called from the
   * <code>draw</code> method of the Processing Development Environment. This
   * ensures that the button is updated periodically.
   */
  public void draw() {
    // initialize the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);

    // draw the button
    if (getValue() == getHigh()) {
      if (!isMouseOver()) applet.fill(Colors.CONTROL_ON);
      else applet.fill(Colors.CONTROL_HIGHLIGHTED);
    }
    else if (mouseOver) applet.fill(Colors.CONTROL_OFF);
    else applet.fill(Colors.CONTROL_OFF);
    applet.rect(panel.frame.getLeft(), panel.frame.getTop(), panel.frame.getWidth(), panel.frame.getHeight());

    // draw the bottom label
    applet.pushMatrix();
    applet.translate(panel.labelBottom.getLeft(), panel.labelBottom.getTop());
    // draw the label's frame
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.rect(0, 0, panel.labelBottom.getWidth(), panel.labelBottom.getHeight());
    // draw the label's text
    applet.fill(Colors.TEXT);
    applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.LEFT);
    applet.text(label, Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    applet.popMatrix();

    // restore the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
  }
  
  /**
   * Returns true if the mouse is hovering over the butten.
   * 
   * @return  true if the mouse hovers over the button
   */
  public Boolean isMouseOver() {
    return mouseOver;
  }
  
  /**
   * Handles a <code>mouseMoved</code> event that occurred in the GUI.<br>
   * This method should usually be called from the <code>mouseMoved</code> method of
   * the GUI (e.g. in the Processing Development Environment). This ensures that the user
   * interaction is received and processed by the control.
   * 
   * @param x     the current x value of the mouse
   * @param y     the current y value of the mouse
   */
  public void mouseMoved(int x, int y) {
    if (panel.content.contains(x, y)) {
      mouseOver = true;
    }
    else {
      mouseOver = false;
    }
  }

  /**
   * Acts on a <code>mousePressed</code> event that occurred in the Processing Development
   * Environment. This method should usually be called from the <code>mousePressed</code>
   * method of the PDE. This ensures that the user interaction is received and processed
   * by the control.
   * 
   * @param x     the current x value of the mouse
   * @param y     the current y value of the mouse
   */
  public void mousePressed(int x, int y) {
    Rectangle2D frame = new Rectangle2D.Double(panel.content.getLeft(), panel.content.getTop(), panel.content.getWidth(), panel.content.getHeight());
    if (frame.contains(x, y)) {
      mousePressed = true;
      if (getValue() == getLow()) {
        switchOn();
      }
      else {
        switchOff();
      }
    }
  }
  
  /**
   * Acts on a <code>mouseReleased</code> event that occurred in the Processing Development
   * Environment. This method should usually be called from the <code>mouseReleased</code>
   * method of the PDE. This ensures that the user interaction is received and processed
   * by the control.
   * 
   * @param x     the current x value of the mouse
   * @param y     the current y value of the mouse
   */
  public void mouseReleased(int x, int y) {
    if (mousePressed) {
      mousePressed = false;
    }
  }

  /**
   * Sets the button's label that is displayed underneath the button.
   * 
   * @param label   a <code>String</code> describing the button's functionality in short
   */
  public void setLabel(String label) {
    this.label = label;
  }
  
  /**
   * Switches this button on.
   */
  public void switchOn() {
    setValue(getHigh());
  }
  
  /**
   * Switches this button off.
   */
  public void switchOff() {
    setValue(getLow());
  }
  
  /**
   * Retuns true if the radio button is switched on and false if it is turned
   * off.
   * 
   * @return true or false
   */
  public boolean isOn() {
    return getValue() == getHigh();
  }
}
