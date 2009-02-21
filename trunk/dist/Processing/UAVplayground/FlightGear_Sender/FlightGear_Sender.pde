/**
 * This is a simple example that demonstrates UAV Playground
 * FlightGearSender. It controls FlightGear's aileron, elevator,
 * rudder and throttle via two virtual joysticks.
 * 
 * The UAV Playground distribution contains all the files that you ned to try
 * out this example application. If you don't have the distribution then first
 * jump to the bottom of this code.
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

// These are all the components that are used for this application
FlightGearSender sender;
Joystick stickRight;
Joystick stickLeft;

// The PDE setup method
void setup() {
  // Setup the PDE display panel
  size(kWindowWidth, kWindowHeight);
  background(Colors.kColorGrayWindow);
  if (frame != null) frame.setTitle(kWindowTitle);
  
  // Setup the PDE graphics options
  frameRate(kFrameRate);
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

// The PDE draw method
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

// Destroy is called on applet exit
void destroy() {
  sender.setDebug(true);
  sender.shutDown();
}

/**
 * 01 - Create a configuration file: %PROGRAMFILES%\FlightGear\data\Protocol\FlightGearSender-Protocol.xml
 * 02 - Copy this XML code into the configuration file:

<?xml version="1.0"?>
<PropertyList>
 <generic>
  <input>
   <line_separator>\n</line_separator>
   <var_separator>tab</var_separator>
   <binary_mode>false</binary_mode>
   <chunk>
    <name>elevator</name>
    <type>float</type>
    <node>/controls/flight/elevator</node>
   </chunk>
   <chunk>
    <name>aileron</name>
    <type>float</type>
    <node>/controls/flight/aileron</node>
   </chunk>
   <chunk>
    <name>rudder</name>
    <type>float</type>
    <node>/controls/flight/rudder</node>
   </chunk>
   <chunk>
    <name>throttle</name>
    <type>float</type>
    <node>/controls/engines/engine/throttle</node>
   </chunk>
  </input>
 </generic>
</PropertyList>
  
 * 03 - Create a file Start-FlightGear-FligthGearSender.bat
 * 04 - Copy this into the batch file (edit ip address if necessary):

"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--generic=socket,in,10,127.0.0.1,5556,tcp,FlightGearSender-Protocol" ^
 "--timeofday=noon"
 
 */
