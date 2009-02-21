package jaron.pde;

import jaron.components.Signal;
import jaron.gui.Colors;
import jaron.gui.Panel;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * The <code>ArtificialHorizon</code> class provides a virtual horizon like device for
 * the Processing Development Environment.<br>
 * It has two inputs for elevator and aileron deflection and two inputs for
 * pitch and roll angle. It usually gets the values for these inputs by another
 * object or objects through the <code>EventListener</code> mechanism.<br>
 * Reversing the effective direction for elevator, aileron, pitch and roll is done by
 * swapping the high and the low values of the corresponding signals.<br>
 * <br>
 * Have a look at the example of the {@link jaron.uavsim.UAVsim} for the usage of the
 * <code>ArtificialHorizon</code>.
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.0
 */
// Testing after removing the reversing flags
public class ArtificialHorizon extends Panel {
  private static int kColorGround = Colors.kColorBrown;
  private static int kColorSky = Colors.kColorBlueLight;
  private static int kColorPlane = Colors.kColorBlack;
  private static int kColorIndicator = Colors.kColorRed;

  private PApplet applet;
  private Signal roll;
  private Signal pitch;
  private Signal elevator;
  private Signal aileron;
  private int maxDeflection = 120; // for elevator and aileron (from low to high signal)
  private Boolean debug = false;

  /**
   * Creates a new <code>ArtificialHorizon</code> object for the Processing Development
   * Environment (PDE) at a certain position and with a certain width and height.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height
   * @param width     the component's width
   */
  public ArtificialHorizon(PApplet applet, int left, int top, int width, int height) {
    super(left, top, width, height);
    this.applet = applet;
    roll = new Signal();
    roll.setValue(0F);
    pitch = new Signal();
    pitch.setValue(0F);
    elevator = new Signal();
    aileron = new Signal();
    elevator.setValue(0F);
    aileron.setValue(0F);
  }

  /**
   * Creates a new <code>ArtificialHorizon</code> object for the Processing Development
   * Environment (PDE) at a certain position.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public ArtificialHorizon(PApplet applet, int left, int top) {
    this(applet, left, top, 150, 150);
  }

  /**
   * Draws the artificial horizon to the screen. This method should usually be called
   * from the <code>draw</code> method of the Processing Development Environment. This
   * ensures that the horizon is updated periodically.
   */
  public void draw() {
    // prepare for 'clipping' (drawing  into an offline graphics port)
    PGraphics pg = applet.createGraphics((int )content.getWidth(), (int )content.getHeight(), PApplet.JAVA2D);
    pg.beginDraw();
    
    // get the current angles for pitch and roll and convert them to radian degrees
    double pitchAngle = Math.toRadians(pitch.getValue());
    double rollAngle = Math.toRadians(roll.getValue());
    // get the current values for elevator and aileron and convert them to angles (according to the maximum deflection for the flaps)
    double maxFlapDefelction = maxDeflection / 2; // max deflection in one direction
    double midElevator = elevator.getLow() + (elevator.getBandwidth() / 2);
    double elevatorVlaue = (elevator.getValue() - midElevator);
    double maxElevatorSignal = (elevator.getBandwidth()/2);
    double elevatorAngle =  Math.toRadians(maxFlapDefelction * elevatorVlaue / maxElevatorSignal);
    double midAileron = aileron.getLow() + (aileron.getBandwidth() / 2);
    double aileronVlaue = (aileron.getValue() - midAileron);
    double maxAileronSignal = (aileron.getBandwidth()/2);
    double aileronAngle =  Math.toRadians(maxFlapDefelction * aileronVlaue / maxAileronSignal);

    // initialize the graphics environment
    applet.fill(Colors.kColorBackground);
    applet.stroke(Colors.kColorStroke);
    applet.strokeWeight(1);

    // draw the horizon
    double radius = (content.getHeight() / 2);
    pg.pushMatrix();
    pg.translate(0, (float )radius);
    // draw the ground (background)
    pg.fill(kColorGround);
    pg.rect(0, (float )-radius, content.getWidth(), content.getHeight());
    // draw the sky (pitch input)
    pg.fill(kColorSky);
    if (Math.cos(pitchAngle) > 0) {
      pg.rect(0, (float )(Math.sin(pitchAngle) * radius), content.getWidth(), -content.getHeight());
    }
    else {
      pg.rect(0, (float )-(Math.sin(pitchAngle) * radius), content.getWidth(), content.getHeight());
    }
    pg.popMatrix();
    
    // draw the indicator (elevator and aileron input)
    double elevatorOffset = Math.sin(elevatorAngle) * radius;
    int wingLength = content.getWidth() / 4;
    int rudderHeight = wingLength / 2;
    int indicatorOversize = wingLength / 4;
    int fuselageRadius = wingLength / 3;
    pg.fill(kColorIndicator);
    pg.stroke(kColorIndicator);
    pg.pushMatrix();
    pg.translate(content.getWidth() / 2, content.getHeight() / 2 + (float )elevatorOffset);
    pg.rotate((float )aileronAngle);
    pg.strokeWeight(3);
    pg.line(-wingLength - indicatorOversize, 0, wingLength + indicatorOversize, 0);
    pg.line(0, 0, 0, - (rudderHeight + indicatorOversize));
    pg.strokeWeight(1);
    pg.ellipse(0, 0, fuselageRadius, fuselageRadius);
    pg.popMatrix();

    // draw the airplane (roll input)
    pg.fill(kColorPlane);
    pg.stroke(kColorPlane);
    pg.pushMatrix();
    pg.translate(content.getWidth() / 2, content.getHeight() / 2);
    pg.rotate((float )rollAngle);
    pg.strokeWeight(3);
    pg.line(-wingLength, 0, wingLength, 0);
    pg.line(0, 0, 0, - rudderHeight);
    pg.strokeWeight(1);
    pg.ellipse(0, 0, fuselageRadius, fuselageRadius);
    pg.popMatrix();

    // do the 'clipping' (copy the offline graphics port into the applet's graphics port) 
    pg.endDraw();
    applet.image(pg, content.getLeft(), content.getTop());

    // draw a frame around the whole component
    applet.noFill();
    applet.strokeWeight(1);
    applet.rect(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());
    
    if (debug) System.out.println("ArtificialHorizon::draw(): Roll = " + roll.getValue() + ", Pitch = " + pitch.getValue());
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current deflection of the aileron.<br>
   * The default bandwidth of the signal is +-1 and can be changed through the
   * <code>setHigh</code> and <code>setLow</code> methods of the Signal class.
   * 
   * @return      a <code>Signal</code> object containing the current aileron value
   * @see         Signal
   */
  public Signal getAileron() {
    return aileron;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current deflection of the elevator.<br>
   * The default bandwidth of the signal is +-1 and can be changed through the
   * <code>setHigh</code> and <code>setLow</code> methods of the Signal class.
   * 
   * @return      a <code>Signal</code> object containing the current elevator value
   * @see         Signal
   */
  public Signal getElevator() {
    return elevator;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current pitch angle in degrees (not radians).<br>
   * 
   * @return      a <code>Signal</code> object containing the current pitch angle
   * @see         Signal
   */
  public Signal getPitch() {
    return pitch;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current roll angle in degrees (not radians).<br>
   * 
   * @return      a <code>Signal</code> object containing the current roll angle
   * @see         Signal
   */
  public Signal getRoll() {
    return roll;
  }
  
  /**
   * Sets the debugging flag which determines if the debugging informations should
   * be printed to the console. This is for debugging purpose only.
   * 
   * @param debug    set to <code>true</code> if additional debugging information
   *                 should be printed
   */
  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  /**
   * Sets the maximum deflection that is used for the conversion of the aileron/elevator
   * values into degrees. The default value is 120 degrees for full left to full right
   * deflection.
   * 
   * @param maxDeflection maximum deflection in degrees
   */
  public void setMaxDeflection(int maxDeflection) {
    this.maxDeflection = maxDeflection;
  }
}
