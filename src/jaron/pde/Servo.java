package jaron.pde;

import jaron.components.Signal;
import jaron.gui.Colors;
import jaron.gui.Panel;

import processing.core.PApplet;

/**
 * The <code>Servo</code> class provides a virtual servo for the Processing
 * Development Environment (PDE).<br>
 * By extending the <code>Signal</code> class it can hook into the
 * <code>EventListener</code> mechanism and thus receive and send numeric
 * values.<br>
 * Reversing the servo's direction is done by swapping the high and the low signal
 * values.<br>
 * <br>
 * This is a Processing Development Development application that demonstrates
 * the usage of the <code>Servo</code>.
 * <pre>
import jaron.gui.*;
import jaron.pde.*;

// PDE GUI defaults
static final String kWindowTitle = "UAV Playground - Servos";
static final int kWindowWidth = 360;
static final int kWindowHeight = 325;
static final int kFrameRate = 30;

// These are all the components that are used for this application
Joystick stickRight;
Joystick stickLeft;
Servo servoRudder;
Servo servoThrottle;
Servo servoElevator;
Servo servoAileron;

// The PDE setup method
void setup() {
  // Setup the PDE display panel
  size(kWindowWidth, kWindowHeight);
  background(Colors.kColorGrayWindow);
  if (frame != null) frame.setTitle(kWindowTitle);
  
  // Setup the PDE graphics options
  frameRate(kFrameRate);
  smooth();

  // Setup the left stick (throttle and rudder)
  stickLeft = new Joystick(this, "Rudder", "Throttle", 15, 160);
  // Set bandwidth for throttle (FG uses 0-1)
  stickLeft.setBandwidthY(0, 1);
  // The throttle has no spring
  stickLeft.setSpringY(false);
  // Setup the right stick (elevator and aileron)
  stickRight = new Joystick(this, "Aileron", "Elevator", 195, 160);

  // Setup the servos
  servoRudder = new Servo(this, "Rudder", 15, 15);
  servoThrottle = new Servo(this, "Throttle", 110, 15);
  servoThrottle.setBandwidth(0, 1);
  servoAileron = new Servo(this, "Aileron", 195, 15);
  servoElevator = new Servo(this, "Elevator", 290, 15);
  
  // The servos are listening to the sticks
  stickLeft.addListenerX(servoRudder);
  stickLeft.addListenerY(servoThrottle);
  stickRight.addListenerX(servoAileron);
  stickRight.addListenerY(servoElevator);
}

// The PDE draw method
void draw() {
  stickRight.draw();
  stickLeft.draw();
  servoRudder.draw();
  servoThrottle.draw();
  servoElevator.draw();
  servoAileron.draw();
}

// The PDE mouseDragged method
void mouseDragged() {
  stickRight.mouseDragged(mouseX, mouseY);
  stickLeft.mouseDragged(mouseX, mouseY);
}

// The PDE mousePressed method
void mousePressed() {
  stickRight.mousePressed(mouseX, mouseY);
  stickLeft.mousePressed(mouseX, mouseY);
}

// The PDE mouseReleased method
void mouseReleased() {
  stickRight.mouseReleased(mouseX, mouseY);
  stickLeft.mouseReleased(mouseX, mouseY);
}

// The PDE mouseMoved method
void mouseMoved() {
  stickRight.mouseMoved(mouseX, mouseY);
  stickLeft.mouseMoved(mouseX, mouseY);
} * </pre>
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class Servo extends Signal {
  private PApplet applet;
  private String label = "";
  private int maxDeflection = 120; // max. degrees from full left to full right
  private Panel panel;

  /**
   * Creates a new <code>Servo</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param label     the label that is displayed at the bottom
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height
   * @param width     the component's width
   */
  public Servo(PApplet applet, String label, int left, int top, int width, int height) {
    super();
    this.applet = applet;
    setLabel(label);
    setValue(0);
    panel = new Panel(left, top, width, height);
    panel.setLabelBottomHeight((2 * Fonts.LINE_HEIGHT) + Fonts.LINE_SPACING);
  }

  /**
   * Creates a new <code>Servo</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height
   * @param width     the component's width
   */
  public Servo(PApplet applet, int left, int top, int width, int height) {
    this(applet, "", left, top, width, height);
  }
  
  /**
   * Creates a new <code>Servo</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param label     the label that is displayed at the bottom
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Servo(PApplet applet, String label, int left, int top) {
    this(applet, label, left, top, 55, 120);
  }
  
  /**
   * Creates a new <code>Servo</code> object for the Processing Development
   * Environment (PDE).
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Servo(PApplet applet, int left, int top) {
    this(applet, left, top, 55, 120);
  }

  /**
   * Draws the servo to the screen. This method should usually be called from the
   * <code>draw</code> method of the Processing Development Environment. This
   * ensures that the servo is updated periodically.
   */
  public void draw() {
    // initialize the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
    
    // servo case
    applet.fill(Colors.COMPONENT);
    applet.rect(panel.frame.getLeft(), panel.frame.getTop(), panel.frame.getWidth(), panel.frame.getHeight());

    // calculate servo horn deflection in radian degree
    double radius = Math.min(panel.content.getWidth(), panel.content.getHeight()) / 2;
    double midPos = getLow() + (getBandwidth() / 2);
    double value = (getValue() - midPos);
    double maxSignal = (getBandwidth()/2);
    double angle =  Math.toRadians((maxDeflection / 2) * value / maxSignal);

    // servo horn rotated by the current deflection
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.pushMatrix();
    applet.translate((float )(panel.content.getLeft() + radius), (float )(panel.content.getTop() + radius));
    applet.rotate((float )angle);
    applet.ellipse(0, 0, (float)(radius * 2.3d), (float )(radius * 2.3d));
    applet.ellipse(0, 0, (float)(radius / 2.5d), (float)(radius / 2.5d));
    applet.fill(Colors.RED);
    applet.ellipse(0, (float)(-radius * 0.8d), (float)(radius / 5), (float)(radius / 5));
    applet.fill(Colors.COMPONENT);
    applet.ellipse((float)(radius * 0.8d), 0, (float)(radius / 5), (float)(radius / 5));
    applet.ellipse(0, (float)(radius * 0.8d), (float)(radius / 5), (float)(radius / 5));
    applet.ellipse((float)(-radius * 0.8d), 0, (float)(radius / 5), (float)(radius / 5));
    applet.popMatrix();

    // draw the bottom label
    applet.pushMatrix();
    applet.translate(panel.labelBottom.getLeft(), panel.labelBottom.getTop());
    // draw the label's frame
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.rect(0, 0, panel.labelBottom.getWidth(), panel.labelBottom.getHeight());
    // draw the label's text
    applet.fill(Colors.TEXT);
    applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.RIGHT);
    applet.text(label, panel.labelBottom.getWidth() - Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
    // draw the labesl's value
    applet.textFont(Fonts.getFontPlain(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.RIGHT);
    applet.text(String.format("%1.3f", getValue()), panel.labelBottom.getWidth() - Fonts.LINE_SPACING, 2 * Fonts.LINE_HEIGHT);
    applet.popMatrix();

    // restore the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
  }

  /**
   * Sets the servos's label that is displayed at the bottom the servo.
   * 
   * @param label   a <code>String</code> describing the servos's functionality in short
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * Sets the maximum deflection of the servo in degrees. The default value is 120 degrees
   * for full left to full right deflection.
   * 
   * @param maxDeflection maximum deflection in degrees
   */
  public void setMaxDeflection(int maxDeflection) {
    this.maxDeflection = maxDeflection;
  }
}