/**
 * This is a simple example that demonstrates some functionality
 * of the UAV Playground.
 * It consist of two virtual joysticks that control some
 * virtual servos.
 */
 
import jaron.gui.*;
import jaron.pde.*;

// The UAV Playground components that are used by this application
Joystick stickRight;
Joystick stickLeft;
Servo servoRudder;
Servo servoThrottle;
Servo servoElevator;
Servo servoAileron;

// The PDE setup method that is called once at startup
void setup() {
  // Setup the PDE environment
  size(360, 325);
  background(Colors.GRAY_WINDOW);
  if (frame != null) frame.setTitle("UAV Playground - Servos");
  frameRate(30);
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

// The PDE draw method is called periodically
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
