package jaron.pde;

import jaron.gui.ActuatorXY;
import jaron.gui.Colors;
import jaron.gui.Panel;
import jaron.gui.Rect;

import processing.core.PApplet;

/**
 * The <code>Joystick</code> class provides a virtual joystick for the
 * Processing Development Environment (PDE).<br>
 * Have a look at the example of the {@link jaron.uavsim.UAVsim} for the usage of the
 * <code>Joystick</code>.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class Joystick extends ActuatorXY {
  private PApplet applet;
  private String labelX;
  private String labelY;
  private Panel panel;

  /**
   * Creates a new <code>Joystick</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param labelX    the label that is displayed at the bottom
   * @param labelY    the label that is displayed at the right
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height
   * @param width     the component's width
   */
  public Joystick(PApplet applet, String labelX, String labelY, int left, int top, int width, int height) {
    super(left, top, width, height, (width+height)/10, (width+height)/10);
    this.applet = applet;
    this.labelX = labelX;
    this.labelY = labelY;
    setSpringX(true);
    setSpringY(true);
    panel = new Panel(left, top, width, height);
    panel.setContent(this);
    panel.setLabelBottomHeight(Fonts.LINE_HEIGHT + Fonts.LINE_SPACING);
    panel.setLabelRightWidth(Fonts.LINE_HEIGHT + Fonts.LINE_SPACING);
  }

  /**
   * Creates a new <code>Joystick</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height
   * @param width     the component's width
   */
  public Joystick(PApplet applet, int left, int top, int width, int height) {
    this(applet, "", "", left, top, width, height);
  }

  /**
   * Creates a new <code>Joystick</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param labelX    the label that is displayed at the bottom
   * @param labelY    the label that is displayed at the right
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Joystick(PApplet applet, String labelX, String labelY, int left, int top) {
    this(applet, labelX, labelY, left, top, 150, 150);
  }

  /**
   * Creates a new <code>Joystick</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Joystick(PApplet applet, int left, int top) {
    this(applet, left, top, 150, 150);
  }

  /**
   * Draws the joystick to the screen. This method should usually be called from the
   * <code>draw</code> method of the Processing Development Environment. This
   * ensures that the joystick is updated periodically.
   */
  public void draw() {
    // get the panel's rectangles for easy access
    Rect frame = panel.getFrame();
    Rect content = panel.getContent();
    
    // initialize the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);

    // draw the bounds frame
    applet.rect(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
    // draw the content frame
    applet.rect(content.getLeft(), content.getTop(), content.getWidth(), content.getHeight());
    // draw the label's frame
    applet.rect(panel.labelBottom.getLeft(), panel.labelBottom.getTop(), panel.labelBottom.getWidth(), panel.labelBottom.getHeight());
    applet.rect(panel.labelRight.getLeft(), panel.labelRight.getTop(), panel.labelRight.getWidth(), panel.labelRight.getHeight());
    // draw a closing line
    applet.line(frame.getLeft() + content.getWidth(), frame.getTop(), frame.getLeft() + content.getWidth(), frame.getTop() + frame.getHeight());
    
    // draw the control (stick)
    if (isMousePressed()) applet.fill(Colors.CONTROL_CLICKED);
    else if (isMouseOver()) applet.fill(Colors.CONTROL_MOUSE_OVER);
    else  applet.fill(Colors.CONTROL);
    double midX = getSignalX().getLow() + (getSignalX().getBandwidth() / 2);
    double valueX = (getSignalX().getValue() - midX);
    double maxSignalX = (getSignalX().getBandwidth()/2);
    double degX =  Math.toRadians(35 * valueX / maxSignalX);
    double x = Math.cos(degX) * control.getWidth();
    double offsetX = control.getWidth() - x;
    float centerX = content.getLeft() + (content.getWidth() / 2);
    if (degX < 0) offsetX = 0;
    double midY = getSignalY().getLow() + (getSignalY().getBandwidth() / 2);
    double valueY = (getSignalY().getValue() - midY);
    double maxSignalY = (getSignalY().getBandwidth()/2);
    double degY =  Math.toRadians(35 * valueY / maxSignalY);
    double y = Math.cos(degY) * control.getHeight();
    double offsetY = control.getHeight() - y;
    float centerY = content.getTop() + (content.getHeight() / 2);
    if (degY < 0) offsetY = 0;

    if (centerY > control.getTop() + (float )(offsetY + y)) {
      // bottom right
      applet.line(centerX, centerY, control.getLeft() + (float )(offsetX + x), control.getTop() + (float )(offsetY + y));
      // bottom left
      applet.line(centerX, centerY, control.getLeft() + (float )offsetX, control.getTop() + (float )(offsetY + y));
    }
    if (centerY < control.getTop()) {
      // top right
      applet.line(centerX, centerY, control.getLeft() + (float )offsetX, control.getTop() + (float )offsetY);
      // top left
      applet.line(centerX, centerY, control.getLeft() + (float )(offsetX + x), control.getTop() + (float )offsetY);
    }
    if (centerX > control.getLeft() + (float )(offsetX + x)) {
      // bottom right
      applet.line(centerX, centerY, control.getLeft() + (float )(offsetX + x), control.getTop() + (float )(offsetY + y));
      // top left
      applet.line(centerX, centerY, control.getLeft() + (float )(offsetX + x), control.getTop() + (float )offsetY);
    }
    if (centerX < control.getLeft()) {
      // bottom left
      applet.line(centerX, centerY, control.getLeft() + (float )offsetX, control.getTop() + (float )(offsetY + y));
      // top right
      applet.line(centerX, centerY, control.getLeft() + (float )offsetX, control.getTop() + (float )offsetY);
    }
    applet.rect(control.getLeft() + (float )offsetX, control.getTop() + (float )offsetY, (float )x, (float )y);

    // draw the bottom label (x-axis)
    applet.pushMatrix();
    applet.translate(panel.labelBottom.getLeft(), panel.labelBottom.getTop());
    // draw the label's text
    applet.fill(Colors.TEXT);
    applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.LEFT);
    applet.text(labelX, Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    // draw the label's value
    applet.textFont(Fonts.getFontPlain(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.RIGHT);
    applet.text(String.format("%1.2f", getValueX()), content.getWidth() - Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    applet.popMatrix();

    // draw the right label (y-axis)
    applet.pushMatrix();
    applet.translate(panel.labelRight.getLeft(), panel.labelRight.getTop() + panel.labelRight.getHeight());
    // draw the label's text
    applet.rotate(PApplet.radians(270));
    applet.fill(Colors.TEXT);
    applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.LEFT);
    applet.text(labelY, Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    // draw the labesl's value
    applet.textFont(Fonts.getFontPlain(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.RIGHT);
    applet.text(String.format("%1.2f", getValueY()), panel.labelRight.getHeight() - Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    applet.popMatrix();

    // restore the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
  }

  /**
   * Sets the joystick's label for the x axis. The label is a short description
   * of the x axis' function.
   * 
   * @param label   a short description of the x axis' functionality
   */
  public void setLabelX(String label) {
    this.labelX = label;
  }

  /**
   * Sets the joystick's label for the y axis. The label is a short description
   * of the y axis' function.
   * 
   * @param label   a short description of the y axis' functionality
   */
  public void setLabelY(String label) {
    this.labelX = label;
  }
}
