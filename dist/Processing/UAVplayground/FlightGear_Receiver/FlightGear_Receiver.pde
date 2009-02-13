/**
 * This is a simple example that demonstrates the usage
 * of the UAV Playground FlightGearReceiver.
 * All it does is displaying some of FlightGear's data.
 * 
 * Have a look at the end of this code for some information
 * about setting up and starting FlightGear.
 */

import jaron.flightgear.*;
import jaron.gui.*;
import jaron.pde.*;

// Network connection defaults
static int inputPort = 5555;

// PDE GUI defaults
static final String kWindowTitle = "UAV Playground - FlightGearReceiver";
static final int kWindowWidth = 170;
static final int kWindowHeight = 170;
static final int kFrameRate = 30;

// These are all the components that are used for this application
Display display;
FlightGearReceiver receiver;

// The PDE setup method
void setup() {
  // Setup the PDE display panel
  size(kWindowWidth, kWindowHeight);
  background(Colors.kColorGrayWindow);
  if (frame != null) frame.setTitle(kWindowTitle);
  
  // Setup the PDE graphics options
  frameRate(kFrameRate);
  smooth();

  // Setup UAV Playground components
  receiver = new FlightGearReceiver(inputPort);
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

// The PDE draw method
void draw() {
  display.draw();
}

// Destroy is called on applet exit
void destroy() {
  receiver.setDebug(true);
  receiver.shutDown();
}

/**
 * The following is a description of how you would setup and run FlightGear 1.9
 * to be used width this application.
 * It assumes that you've installed FlightGear in a directory that is referred
 * here as %PROGRAMFILES%\FlightGear.
 * 
 * 01 - Create a configuration file: %PROGRAMFILES%\FlightGear\data\Protocol\FlightGearReceiver-Protocol.xml
 * 
 * 02 - Copy this XML code into the configuration file:

<?xml version="1.0"?>
<PropertyList>
 <generic>
  <output>
   <line_separator>\n</line_separator>
   <var_separator></var_separator>
   <preamble></preamble>
   <postamble></postamble>
   <binary_mode>false</binary_mode>
   <chunk>
    <name>controls-flight-elevator</name>
    <type>float</type>
    <format>&lt;?xml version="1.0"?&gt;&lt;data&gt;&lt;controls-flight-elevator&gt;%f&lt;/controls-flight-elevator&gt;</format>
    <node>/controls/flight/elevator</node>
   </chunk>
   <chunk>
    <name>controls-flight-aileron</name>
    <type>float</type>
    <format>&lt;controls-flight-aileron&gt;%f&lt;/controls-flight-aileron&gt;</format>
    <node>/controls/flight/aileron</node>
   </chunk>
   <chunk>
    <name>controls-flight-rudder</name>
    <type>float</type>
    <format>&lt;controls-flight-rudder&gt;%f&lt;/controls-flight-rudder&gt;</format>
    <node>/controls/flight/rudder</node>
   </chunk>
   <chunk>
    <name>controls-engines-engine-throttle</name>
    <type>float</type>
    <format>&lt;controls-engines-engine-throttle&gt;%f&lt;/controls-engines-engine-throttle&gt;</format>
    <node>/controls/engines/engine/throttle</node>
   </chunk>
   <chunk>
    <name>orientation-pitch-deg</name>
    <type>float</type>
    <format>&lt;orientation-pitch-deg&gt;%f&lt;/orientation-pitch-deg&gt;</format>
    <node>/orientation/pitch-deg</node>
   </chunk>
   <chunk>
    <name>orientation-roll-deg</name>
    <type>float</type>
    <format>&lt;orientation-roll-deg&gt;%f&lt;/orientation-roll-deg&gt;</format>
    <node>/orientation/roll-deg</node>
   </chunk>
   <chunk>
    <name>orientation-pitch-rate-degps</name>
    <type>float</type>
    <format>&lt;orientation-pitch-rate-degps&gt;%f&lt;/orientation-pitch-rate-degps&gt;</format>
    <node>/orientation/pitch-rate-degps</node>
   </chunk>
   <chunk>
    <name>orientation-roll-rate-degps</name>
    <type>float</type>
    <format>&lt;orientation-roll-rate-degps&gt;%f&lt;/orientation-roll-rate-degps&gt;</format>
    <node>/orientation/roll-rate-degps</node>
   </chunk>
   <chunk>
    <name>orientation-yaw-rate-degps</name>
    <type>float</type>
    <format>&lt;orientation-yaw-rate-degps&gt;%f&lt;/orientation-yaw-rate-degps&gt;</format>
    <node>/orientation/yaw-rate-degps</node>
   </chunk>
   <chunk>
    <name>orientation-heading-deg</name>
    <type>float</type>
    <format>&lt;orientation-heading-deg&gt;%f&lt;/orientation-heading-deg&gt;</format>
    <node>/orientation/heading-deg</node>
   </chunk>
   <chunk>
    <name>velocities-airspeed-kt</name>
    <type>float</type>
    <format>&lt;velocities-airspeed-kt&gt;%f&lt;/velocities-airspeed-kt&gt;</format>
    <node>/velocities/airspeed-kt</node>
   </chunk>
   <chunk>
    <name>velocities-vertical-speed-fps</name>
    <type>float</type>
    <format>&lt;velocities-vertical-speed-fps&gt;%f&lt;/velocities-vertical-speed-fps&gt;</format>
    <node>/velocities/vertical-speed-fps</node>
   </chunk>
   <chunk>
    <name>position-altitude-ft</name>
    <type>float</type>
    <format>&lt;position-altitude-ft&gt;%f&lt;/position-altitude-ft&gt;&lt;/data&gt;</format>
    <node>/position/altitude-ft</node>
   </chunk>
  </output>
 </generic>
</PropertyList>
  
 * 
 * 03 - Start this application (FlightGear_Receiver)
 * 
 * 04 - Start FlightGear with the following script (replace the IP address
 *      127.0.0.1 with the address of the machine the this application runs on):

"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--generic=socket,out,3,127.0.0.1,5555,tcp,FlightGearReceiver-Protocol" ^
 "--timeofday=noon"
 
 */
