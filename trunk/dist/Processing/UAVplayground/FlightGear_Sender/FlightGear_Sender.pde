/**
 * This is a simple example that demonstrates UAV Playground
 * FlightGearSender. It controls FlightGear's aileron, elevator,
 * rudder and throttle via two virtual joysticks.
 * 
 * The UAV Playground distribution contains all the files that you need to try
 * out this example application.
 * 01 - Follow the installation instructions in the file UAVplayground-ReadMe-Installation.txt
 * 02 - Start this application (FlightGear_Sender)
 * 03 - Start FlightGear by double-clicking on Start-FlightGear-FligthGearSender.bat
 */

import jaron.flightgear.*;
import jaron.gui.*;
import jaron.pde.*;

// Network connection defaults
static String FlightGearIP ="127.0.0.1";
static int FlightGearPort = 5556;

// PDE GUI defaults
static final String kWindowTitle = "UAV Playground - FlightGearSender";
static final int kWindowWidth = 330;
static final int kWindowHeight = 170;
static final int kFrameRate = 30;

// The UAV Playground components that are used by this application
FlightGearSender sender;
Joystick stickRight;
Joystick stickLeft;

// The PDE setup method that is called once at startup
void setup() {
  // Setup the PDE environment
  size( 330, 170);
  background(Colors.GRAY_WINDOW);
  if (frame != null) frame.setTitle("UAV Playground - FlightGearSender");
  frameRate(30);
  smooth();

  // Setup and start the FlightGear data sender
  sender = new FlightGearSender(FlightGearIP, FlightGearPort);
  
  // Setup the left stick (throttle and rudder)
  stickLeft = new Joystick(this, "Rudder", "Throttle", 10, 10);
  // Set bandwidth for throttle (FlightGear uses 0-1)
  stickLeft.setBandwidthY(0, 1);
  // The throttle has no spring
  stickLeft.setSpringY(false);

  // Setup the right stick (elevator and aileron)
  stickRight = new Joystick(this, "Aileron", "Elevator", 170, 10);

  // Connect the FlightGear sender to the sticks
  stickRight.addListenerX(sender.getAileron());
  stickRight.addListenerY(sender.getElevator());
  stickLeft.addListenerX(sender.getRudder());
  stickLeft.addListenerY(sender.getThrottle());
}

// The PDE draw method is called periodically
void draw() {
  stickRight.draw();
  stickLeft.draw();
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

// Destroy is called on application exit
void destroy() {
  sender.setDebug(true);
  sender.shutDown();
}
