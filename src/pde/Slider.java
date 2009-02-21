package jaron.pde;

import jaron.components.Signal;
import jaron.components.SignalListener;
import jaron.gui.ActuatorXY;
import jaron.gui.Colors;
import jaron.gui.Panel;
import jaron.gui.Rect;

import processing.core.PApplet;

/**
 * The <code>Slider</code> class provides a virtual slider for the Processing
 * Development Environment (PDE).<br>
 * Have a look at the example of the {@link jaron.uavsim.UAVsim} for the usage of the
 * <code>Slider</code>.
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.0
 */
public class Slider extends ActuatorXY {
  PApplet applet;
  String label;
  private Panel panel;

  /**
   * Creates a new <code>Slider</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet        a reference to the PDE applet that provides the drawing environment
   * @param label         the label that is displayed at right side
   * @param left          the component's position from the left
   * @param top           the component's position from top
   * @param height        the component's height
   * @param width         the component's width
   */
  public Slider(PApplet applet, String label, int left, int top, int width, int height) {
    super(left, top, width, height, width, width);
    this.applet = applet;
    this.label = label;
    
    // setup a new panel
    panel = new Panel(left, top, width, height);
    panel.setContent(this);
    panel.setLabelBottomHeight(Fonts.kLineHeight + Fonts.kLineSpacing);
    panel.setLabelRightWidth(Fonts.kLineHeight + Fonts.kLineSpacing);

    // resize the control to fit the content width and lock the x axis
    setControlSize(panel.content.getWidth(), panel.content.getWidth());
    setLockedX(true);
  }

  /**
   * Creates a new <code>Slider</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet        a reference to the PDE applet that provides the drawing environment
   * @param left          the component's position from the left
   * @param top           the component's position from top
   * @param height        the component's height
   * @param width         the component's width
   */
  public Slider(PApplet applet, int left, int top, int width, int height) {
    this(applet, "", left, top, width, height);
  }

  /**
   * Creates a new <code>Slider</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet        a reference to the PDE applet that provides the drawing environment
   * @param label         the label that is displayed at right side
   * @param left          the component's position from the left
   * @param top           the component's position from top
   */
  public Slider(PApplet applet, String label, int left, int top) {
    this(applet, label, left, top, 40, 120);
  }

  /**
   * Creates a new <code>Slider</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet        a reference to the PDE applet that provides the drawing environment
   * @param left          the component's position from the left
   * @param top           the component's position from top
   */
  public Slider(PApplet applet, int left, int top) {
    this(applet, left, top, 40, 120);
  }

  /**
   * Adds a listener to the slider. In case of a change of the slider's value,
   * all the listeners are informed through the <code>EventListener</code>
   * mechanism.
   * 
   * @param listener    the listener to be added to slider
   * @see SignalListener
   */
  public void addSignalListener(SignalListener listener) {
    getSignalY().addSignalListener(listener);
  }

  /**
   * Draws the slider to the screen. This method should usually be called from the
   * <code>draw</code> method of the Processing Development Environment. This
   * ensures that the slider is updated periodically.
   */
  public void draw() {
    // get the panel's rectangles for easy access
    Rect frame = panel.getFrame();
    Rect content = panel.getContent();
    
    // initialize the graphics environment
    applet.fill(Colors.kColorBackground);
    applet.stroke(Colors.kColorStroke);
    applet.strokeWeight(1);

    // draw the bounds frame
    applet.rect(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
    // draw the content frame
    applet.rect(content.getLeft(), content.getTop(), content.getWidth(), content.getHeight());
    // draw the label's frame
    applet.rect(panel.labelBottom.getLeft(), panel.labelBottom.getTop(), panel.labelBottom.getWidth(), panel.labelBottom.getHeight());
    applet.rect(panel.labelRight.getLeft(), panel.labelRight.getTop(), panel.labelRight.getWidth(), panel.labelRight.getHeight());
    
    // draw the slider
    if (isMousePressed()) applet.fill(Colors.kColorControlClicked);
    else if (isMouseOver()) applet.fill(Colors.kColorControlMouseOver);
    else  applet.fill(Colors.kColorControl);
    applet.rect(control.getLeft(), control.getTop(), control.getWidth(), control.getHeight());
    applet.line(control.getLeft(), control.getTop() + (control.getHeight() / 2), control.getLeft() + control.getWidth(),  control.getTop() + (control.getHeight() / 2));

    // draw the right label (label + current signal value)
    applet.pushMatrix();
    applet.translate(panel.labelRight.getLeft(), panel.labelRight.getTop());
    // draw the label's text
    applet.translate(0, panel.labelRight.getHeight());
    applet.rotate(PApplet.radians(270));
    applet.fill(Colors.kColorText);
    applet.textFont(Fonts.getFontBold(applet), Fonts.kFontSize);
    applet.textAlign(PApplet.LEFT);
    applet.text(label, Fonts.kLineSpacing, Fonts.kLineHeight);
    // draw the labesl's value
    applet.textFont(Fonts.getFontPlain(applet), Fonts.kFontSize);
    applet.popMatrix();

    // draw the bottom label (low signal value)
    applet.pushMatrix();
    applet.translate(panel.labelBottom.getLeft(), panel.labelBottom.getTop());
    // draw the label's text
    applet.fill(Colors.kColorText);
    applet.textFont(Fonts.getFontBold(applet), Fonts.kFontSize);
    applet.textAlign(PApplet.LEFT);
    applet.text("", Fonts.kLineSpacing, Fonts.kLineHeight);
    // draw the labesl's value
    applet.textFont(Fonts.getFontPlain(applet), Fonts.kFontSize);
    applet.textAlign(PApplet.RIGHT);
    applet.text(String.format("%1.2f", getValueY()), panel.labelBottom.getWidth() - Fonts.kLineSpacing, Fonts.kLineHeight);
    applet.popMatrix();

    // restore the graphics environment
    applet.fill(Colors.kColorBackground);
    applet.stroke(Colors.kColorStroke);
    applet.strokeWeight(1);
  }

  /**
   * Returns a <code>Signal</code> object containing a value that represents the
   * current value of the slider.<br>
   * 
   * @return      a <code>Signal</code> object containing the current y-axis value
   * @see         Signal
   */
  public Signal getSignal() {
    return getSignalY();
  }

  /**
   * Sets the slider's label that is displayed at the right side.
   * 
   * @param label   the label
   *                in short
   */
  public void setLabel(String label) {
    this.label = label;
  }
  
  /**
   * Sets the slider's value.
   * 
   * @param value the new value
   * @see Signal
   */
  public void setValue(double value) {
    setValueY(value);
  }
}
