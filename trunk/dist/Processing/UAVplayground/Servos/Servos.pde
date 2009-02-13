/**
 * This is a simple example that demonstrates the usage
 * of the UAV Playground.
 * It consist of two virtual joysticks that control some
 * virtual servos.
 */
 
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
}
