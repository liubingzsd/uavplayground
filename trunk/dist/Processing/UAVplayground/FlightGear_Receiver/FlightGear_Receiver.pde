/**
 * This is a simple example that demonstrates the UAV Playground
 * FlightGearReceiver. All it does is displaying some of FlightGear's
 * data.
 * 
 * The UAV Playground distribution contains all the files that you need to try
 * out this example application.
 * 01 - Follow the installation instructions in the file UAVplayground-ReadMe-Installation.txt
 * 02 - Start this application (FlightGear_Receiver)
 * 03 - Start FlightGear by double-clicking on Start-FlightGear-FlightGearReceiver.bat
 */

import jaron.flightgear.*;
import jaron.gui.*;
import jaron.pde.*;

// The UAV Playground components that are used by this application
Display display;
FlightGearReceiver receiver;

// The PDE setup method that is called once at startup
void setup() {
  // Setup the PDE environment
  size(170, 170);
  background(Colors.GRAY_WINDOW);
  frameRate(30);
  smooth();

  // Setup UAV Playground components
  receiver = new FlightGearReceiver();
  display = new Display(this, 10, 10);
  // Connect the display to the FlightGear receiver
  receiver.addSignalListener(FlightGearReceiver.kRollRate, display.createTextLine("Roll rate"));
  receiver.addSignalListener(FlightGearReceiver.kPitchRate, display.createTextLine("Pitch rate"));
  receiver.addSignalListener(FlightGearReceiver.kYawRate, display.createTextLine("Yaw rate"));
  receiver.addSignalListener(FlightGearReceiver.kAirSpeed, display.createTextLine("Airspeed"));
  receiver.addSignalListener(FlightGearReceiver.kAltitude, display.createTextLine("Altitude"));
  receiver.addSignalListener(FlightGearReceiver.kVerticalSpeed, display.createTextLine("Vert. speed"));
  receiver.addSignalListener(FlightGearReceiver.kPitch, display.createTextLine("Pitch"));
  receiver.addSignalListener(FlightGearReceiver.kRoll, display.createTextLine("Roll"));
}

// The PDE draw method is called periodically
void draw() {
  display.draw();
}

// Destroy is called on application exit
void destroy() {
  receiver.setDebug(true);
  receiver.shutDown();
}
