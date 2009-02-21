package jaron.uavsim;

import jaron.flightgear.FlightGearReceiver;
import jaron.flightgear.FlightGearSender;
import jaron.flightgear.FlightGearNMEAReceiver;
import jaron.google.GoogleEarthKMLProvider;
import jaron.gui.Colors;
import jaron.pde.ArtificialHorizon;
import jaron.pde.Display;
import jaron.pde.Graph;
import jaron.pde.Joystick;
import jaron.pde.RadioButton;
import jaron.pde.Slider;

import processing.core.PApplet;

/**
 * The <code>UAVsim</code> class is the main application for the
 * UAVsim. It uses some functionality of the Processing Development Environment
 * (PDE) and therefore the <code>core</code>.jar library must be included in the
 * build path. But besides of this it's just a regular Java application.<br>
 * Be aware that running FlightGear (FG) and the <code>UAVsim</code> on the
 * same machine demands for sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent by FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 * <br>
 * The following is a description of how you would setup FG 1.9 and the <code>UAVsim</code>
 * PDE application. It assumes that you've installed FlightGear in a directory that
 * is referred here as <code>%PROGRAMFILES%\FlightGear</code>.<br>
 * <br>
 * <strong>Create an FG configuration file</strong><br>
 * <br>
 * <code>%PROGRAMFILES%\FlightGear\data\Protocol\UAVsim-Protocol.xml</code><br>
 * <br>
 * <strong>Copy this XML code into the FG configuration file</strong>
 * <pre>
&lt;?xml version="1.0"?>
&lt;PropertyList&gt;
 &lt;generic&gt;
  &lt;input&gt;
   &lt;line_separator&gt;\n&lt;/line_separator&gt;
   &lt;var_separator&gt;tab&lt;/var_separator&gt;
   &lt;binary_mode&gt;false&lt;/binary_mode&gt;
   &lt;chunk&gt;
    &lt;name&gt;elevator&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/flight/elevator&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;aileron&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/flight/aileron&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;rudder&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/flight/rudder&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;throttle&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/engines/engine/throttle&lt;/node&gt;
   &lt;/chunk&gt;
  &lt;/input&gt;
  &lt;output&gt;
   &lt;line_separator&gt;\n&lt;/line_separator&gt;
   &lt;var_separator&gt;&lt;/var_separator&gt;
   &lt;preamble&gt;&lt;/preamble&gt;
   &lt;postamble&gt;&lt;/postamble&gt;
   &lt;binary_mode&gt;false&lt;/binary_mode&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-flight-elevator&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;?xml version="1.0"?&amp;gt;&amp;lt;data&amp;gt;&amp;lt;controls-flight-elevator&amp;gt;%f&amp;lt;/controls-flight-elevator&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/flight/elevator&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-flight-aileron&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;controls-flight-aileron&amp;gt;%f&amp;lt;/controls-flight-aileron&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/flight/aileron&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-flight-rudder&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;controls-flight-rudder&amp;gt;%f&amp;lt;/controls-flight-rudder&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/flight/rudder&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-engines-engine-throttle&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;controls-engines-engine-throttle&amp;gt;%f&amp;lt;/controls-engines-engine-throttle&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/engines/engine/throttle&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-pitch-deg&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-pitch-deg&amp;gt;%f&amp;lt;/orientation-pitch-deg&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/pitch-deg&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-roll-deg&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-roll-deg&amp;gt;%f&amp;lt;/orientation-roll-deg&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/roll-deg&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-pitch-rate-degps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-pitch-rate-degps&amp;gt;%f&amp;lt;/orientation-pitch-rate-degps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/pitch-rate-degps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-roll-rate-degps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-roll-rate-degps&amp;gt;%f&amp;lt;/orientation-roll-rate-degps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/roll-rate-degps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-yaw-rate-degps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-yaw-rate-degps&amp;gt;%f&amp;lt;/orientation-yaw-rate-degps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/yaw-rate-degps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-heading-deg&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-heading-deg&amp;gt;%f&amp;lt;/orientation-heading-deg&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/heading-deg&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;velocities-airspeed-kt&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;velocities-airspeed-kt&amp;gt;%f&amp;lt;/velocities-airspeed-kt&amp;gt;&lt;/format&gt;
    &lt;node&gt;/velocities/airspeed-kt&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;velocities-vertical-speed-fps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;velocities-vertical-speed-fps&amp;gt;%f&amp;lt;/velocities-vertical-speed-fps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/velocities/vertical-speed-fps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;position-altitude-ft&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;position-altitude-ft&amp;gt;%f&amp;lt;/position-altitude-ft&amp;gt;&amp;lt;/data&amp;gt;&lt;/format&gt;
    &lt;node&gt;/position/altitude-ft&lt;/node&gt;
   &lt;/chunk&gt;
  &lt;/output&gt;
 &lt;/generic&gt;
&lt;/PropertyList&gt;
 * </pre>
 * <strong>Create the Processing Development Environment application and run it</strong>
 * <pre>
import jaron.flightgear.*;
import jaron.google.*;
import jaron.gps.*;
import jaron.gui.*;
import jaron.pde.*;
import jaron.uavsim.*;

// Network connection defaults
String kFlightGearGenericIP ="127.0.0.1";
int kFlightGearGenericPort = 5556;
int kFlightGearNMEAPort = 5557;
int kUAVsimPort = 5555;
int kKMLProviderPort = 8080;

// Autopilot PID processor default settings
double kElevatorP = -2;
double kElevatorI = -0.3;
double kElevatorD = -0.75;
double kAileronP = -1;
double kAileronI = -0.05;
double kAileronD = -0.75;

// Some labels/identifiers
String kLabelPitch = "Pitch (FlightGear)";
String kLabelElevator ="Elevator (Autopilot/PID)";
String kLabelRoll = "Roll (FlightGear)";
String kLabelAileron ="Aileron (Autopilot/PID";

// GUI defaults
String kWindowTitle = "UAV Playground - UAVsim";
int kWindowWidth = 330;
int kWindowHeight = 620;
int kFrameRate = 30;

// These are all the components that are used for the UAVsim
ArtificialHorizon horizon = null;
Autopilot autopilot = null;
Display display = null;
FlightGearReceiver receiver = null;
FlightGearSender sender = null;
Graph graphPitch = null;
Graph graphRoll = null;
Joystick stickRight = null;
Joystick stickLeft = null;
RadioButton switchAutopilot = null;
RadioButton switchSticks = null;
Slider pElevator = null;
Slider iElevator = null;
Slider dElevator = null;
Slider pAileron = null;
Slider iAileron = null;
Slider dAileron = null;
FlightGearNMEAReceiver nmeaReceiver = null;
GoogleEarthKMLProvider kmlProvider = null;

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
  setupFlightGear();
  setupDisplay();
  setupGraphs();
  setupHorizon();
  setupSticks();
  setupAutopilot();
  setupGPS();
  
  // Setup the UAV Playground component dependencies
  setupDependencies();
}
  
// Flight Gear initialization
void setupFlightGear() {
  // Setup and start the FlightGear data receiver
  receiver = new FlightGearReceiver(kUAVsimPort);

  // Setup and start the FlightGear data sender
  sender = new FlightGearSender(kFlightGearGenericIP, kFlightGearGenericPort);
}

void setupDisplay() {
  // Setup a display to display some incoming FlightGear data
  display = new Display(this, 170, 10);
}

void setupGraphs() {
  // Setup a graph to display pitch and elevator values
  graphPitch = new Graph(this, 10, 170);
  graphPitch.addGraph(kLabelPitch, Colors.kColorBlack);
  graphPitch.addGraph(kLabelElevator, Colors.kColorRed);
  graphPitch.getSignal(kLabelPitch).setBandwidth(45, -45);

  // Setup a graph to display roll and aileron values
  graphRoll = new Graph(this, 170, 170);
  graphRoll.addGraph(kLabelRoll, Colors.kColorBlack);
  graphRoll.addGraph(kLabelAileron, Colors.kColorRed);
  graphRoll.getSignal(kLabelRoll).setBandwidth(-45, 45);
}

void setupHorizon() {
  // Setup the artificial horizon
  horizon = new ArtificialHorizon(this, 10, 10);
}

void setupSticks() {
  // Setup the left stick (throttle and rudder)
  stickLeft = new Joystick(this, "Rudder", "Throttle", 10, 460);
  // Set 'dual rate' bandwidth for rudder (FG uses +-1)
  stickLeft.setBandwidthX(-0.5, 0.5);
  // Set bandwidth for throttle (FG uses 0-1)
  stickLeft.setBandwidthY(0, 1);
  // The throttle has no spring
  stickLeft.setSpringY(false);

  // Setup the right stick (elevator and aileron)
  stickRight = new Joystick(this, "Aileron", "Elevator", 170, 460);
  // Set 'dual rate' bandwidth for aileron (FG uses +-1)
  stickRight.setBandwidthX(-0.5, 0.5);
  // Set 'dual rate' bandwidth for elevator (FG uses +-1)
  stickRight.setBandwidthY(-0.5, 0.5);

  // Setup an on/off switch and connect the sticks to it
  switchSticks = new RadioButton(this, "Manual", 10, 330);
  switchSticks.setValue(switchSticks.getHigh()); // switch it on by default
  switchSticks.addSignalListener(stickLeft.getPowerSignal());
  switchSticks.addSignalListener(stickRight.getPowerSignal());
}

void setupAutopilot() {
  // Setup the sliders for the elevator gains
  pElevator = new Slider(this, "P - Elevator", 80, 330);
  pElevator.setBandwidthY(-2, 2);
  pElevator.setValue(kElevatorP); // gain here
  iElevator = new Slider(this, "I - Elevator", 120, 330);
  iElevator.setBandwidthY(-0.3, 0.3);
  iElevator.setValue(kElevatorI); // gain here
  dElevator = new Slider(this, "D - Elevator", 160, 330);
  dElevator.setBandwidthY(-1, 1);
  dElevator.setValue(kElevatorD); // gain here

  // Setup the sliders for the aileron gains
  pAileron = new Slider(this, "P - Aileron", 200, 330);
  pAileron.setBandwidthY(-2, 2);
  pAileron.setValue(kAileronP); // gain here
  iAileron = new Slider(this, "I - Aileron", 240, 330);
  iAileron.setBandwidthY(-0.3, 0.3);
  iAileron.setValue(kAileronI); // gain here
  dAileron = new Slider(this, "D - Aileron", 280, 330);
  dAileron.setBandwidthY(-1, 1);
  dAileron.setValue(kAileronD); // gain here
  
  // Start and initialize the autopilot
  autopilot = new Autopilot();
  autopilot.getPitch().setValue(0);
  autopilot.getRoll().setValue(0);
  autopilot.getElevator().setValue(0);
  autopilot.getAileron().setValue(0);
  // Reset gains of the autopilot
  autopilot.getPElevator().setValue(kElevatorP);
  autopilot.getIElevator().setValue(kElevatorI);
  autopilot.getDElevator().setValue(kElevatorD);
  autopilot.getPAileron().setValue(kAileronP);
  autopilot.getDAileron().setValue(kAileronI);
  autopilot.getDAileron().setValue(kAileronD);

  // Connect the autopilot to the gain sliders
  pElevator.addSignalListener(autopilot.getPElevator());
  iElevator.addSignalListener(autopilot.getIElevator());
  dElevator.addSignalListener(autopilot.getDElevator());
  pAileron.addSignalListener(autopilot.getPAileron());
  iAileron.addSignalListener(autopilot.getIAileron());
  dAileron.addSignalListener(autopilot.getDAileron());

  // Setup an on/off switch and connect the autopilot to it
  switchAutopilot = new RadioButton(this, "Autopilot", 10, 410);
  switchAutopilot.addSignalListener(autopilot.getPower());
}

void setupGPS() {
  // Setup the Google KML writer
  kmlProvider = new GoogleEarthKMLProvider(kKMLProviderPort);
  kmlProvider.setWritePlacemaks(false);

  // Setup the FlightGear NMEA receiver and connect the KML writer to it
  nmeaReceiver = new FlightGearNMEAReceiver(kFlightGearNMEAPort);
  nmeaReceiver.addTrackpointListener(kmlProvider);
}

void setupDependencies() {
  // Connect the artificial horizon to the FlightGear receiver
  receiver.addSignalListener(FlightGearReceiver.kPitch, horizon.getPitch());
  receiver.addSignalListener(FlightGearReceiver.kRoll, horizon.getRoll());

  // Connect the autopilot to the the FlightGear receiver
  receiver.addSignalListener(FlightGearReceiver.kPitch, autopilot.getPitch());
  receiver.addSignalListener(FlightGearReceiver.kRoll, autopilot.getRoll());
  
  // Connect the display to the FlightGear receiver
  receiver.addSignalListener(FlightGearReceiver.kRollRate, display.createTextLine("Roll rate"));
  receiver.addSignalListener(FlightGearReceiver.kPitchRate, display.createTextLine("Pitch rate"));
  receiver.addSignalListener(FlightGearReceiver.kYawRate, display.createTextLine("Yaw rate"));
  receiver.addSignalListener(FlightGearReceiver.kAirSpeed, display.createTextLine("Airspeed"));
  receiver.addSignalListener(FlightGearReceiver.kAltitude, display.createTextLine("Altitude"));
  receiver.addSignalListener(FlightGearReceiver.kVerticalSpeed, display.createTextLine("Vert. speed"));
  receiver.addSignalListener(FlightGearReceiver.kPitch, display.createTextLine("Pitch"));
  receiver.addSignalListener(FlightGearReceiver.kRoll, display.createTextLine("Roll"));

  // Connect the roll and pitch graph to the FlightGear receiver
  receiver.addSignalListener(FlightGearReceiver.kRoll, graphRoll.getSignal(kLabelRoll));
  receiver.addSignalListener(FlightGearReceiver.kPitch, graphPitch.getSignal(kLabelPitch));

  // Connect the FlightGear sender to the sticks
  stickRight.addListenerX(sender.getAileron());
  stickRight.addListenerY(sender.getElevator());
  stickLeft.addListenerX(sender.getRudder());
  stickLeft.addListenerY(sender.getThrottle());

  // Connect FlightGear sender to the autopilot
  autopilot.addElevatorListener(sender.getElevator());
  autopilot.addAileronListener(sender.getAileron());

  // Connect the artificial horizon to the autopilot
  autopilot.addElevatorListener(horizon.getElevator());
  autopilot.addAileronListener(horizon.getAileron());

  // Connect the elevator and aileron graph to the autopilot
  autopilot.addElevatorListener(graphPitch.getSignal(kLabelElevator));
  autopilot.addAileronListener(graphRoll.getSignal(kLabelAileron));
}

// The PDE draw method
void draw() {
  stickRight.draw();
  stickLeft.draw();
  horizon.draw();
  pElevator.draw();
  iElevator.draw();
  dElevator.draw();
  pAileron.draw();
  iAileron.draw();
  dAileron.draw();
  switchAutopilot.draw();
  switchSticks.draw();
  graphPitch.draw();
  graphRoll.draw();
  display.draw();
}

// The PDE mouseDragged method
void mouseDragged() {
  stickRight.mouseDragged(mouseX, mouseY);
  stickLeft.mouseDragged(mouseX, mouseY);
  pElevator.mouseDragged(mouseX, mouseY);
  iElevator.mouseDragged(mouseX, mouseY);
  dElevator.mouseDragged(mouseX, mouseY);
  pAileron.mouseDragged(mouseX, mouseY);
  iAileron.mouseDragged(mouseX, mouseY);
  dAileron.mouseDragged(mouseX, mouseY);
 }

// The PDE mousePressed method
void mousePressed() {
  stickRight.mousePressed(mouseX, mouseY);
  stickLeft.mousePressed(mouseX, mouseY);
  pElevator.mousePressed(mouseX, mouseY);
  iElevator.mousePressed(mouseX, mouseY);
  dElevator.mousePressed(mouseX, mouseY);
  pAileron.mousePressed(mouseX, mouseY);
  iAileron.mousePressed(mouseX, mouseY);
  dAileron.mousePressed(mouseX, mouseY);
  switchAutopilot.mousePressed(mouseX, mouseY);
  switchSticks.mousePressed(mouseX, mouseY);
}

// The PDE mouseReleased method
void mouseReleased() {
  stickRight.mouseReleased(mouseX, mouseY);
  stickLeft.mouseReleased(mouseX, mouseY);
  pElevator.mouseReleased(mouseX, mouseY);
  iElevator.mouseReleased(mouseX, mouseY);
  dElevator.mouseReleased(mouseX, mouseY);
  pAileron.mouseReleased(mouseX, mouseY);
  iAileron.mouseReleased(mouseX, mouseY);
  dAileron.mouseReleased(mouseX, mouseY);
  switchAutopilot.mouseReleased(mouseX, mouseY);
  switchSticks.mouseReleased(mouseX, mouseY);
}

// The PDE mouseMoved method
void mouseMoved() {
  stickRight.mouseMoved(mouseX, mouseY);
  stickLeft.mouseMoved(mouseX, mouseY);
  pElevator.mouseMoved(mouseX, mouseY);
  iElevator.mouseMoved(mouseX, mouseY);
  dElevator.mouseMoved(mouseX, mouseY);
  pAileron.mouseMoved(mouseX, mouseY);
  iAileron.mouseMoved(mouseX, mouseY);
  dAileron.mouseMoved(mouseX, mouseY);
  switchAutopilot.mouseMoved(mouseX, mouseY);
  switchSticks.mouseMoved(mouseX, mouseY);
}

// Destroy is called on applet exit
void destroy() {
  receiver.shutDown();
  sender.shutDown();
  nmeaReceiver.shutDown();
}</pre>
 * <strong>Start up FlightGear</strong> (replace the IP address 127.0.0.1 with the
 * address of the machine the Java application runs on)<br>
 * <pre>"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--nmea=socket,out,0.5,127.0.0.1,5557,tcp" ^
 "--generic=socket,in,10,127.0.0.1,5556,tcp,UAVsim-Protocol" ^
 "--generic=socket,out,3,127.0.0.1,5555,tcp,UAVsim-Protocol" ^
 "--timeofday=noon"</pre>
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.0
 */
public class UAVsim extends PApplet {
  // Network connection defaults
  static final String kFlightGearGenericIP ="127.0.0.1";
  static final int kFlightGearGenericPort = 5556;
  static final int kFlightGearNMEAPort = 5557;
  static final int kUAVsimPort = 5555;
  static final int kKMLProviderPort = 8080;

  // Autopilot PID processor default settings
  static final double kElevatorP = -2;
  static final double kElevatorI = -0.3;
  static final double kElevatorD = -0.75;
  static final double kAileronP = -1;
  static final double kAileronI = -0.05;
  static final double kAileronD = -0.75;

  // Some labels/identifiers
  static final String kLabelPitch = "Pitch (FlightGear)";
  static final String kLabelElevator ="Elevator (Autopilot/PID)";
  static final String kLabelRoll = "Roll (FlightGear)";
  static final String kLabelAileron ="Aileron (Autopilot/PID";

  // GUI defaults
  static final String kWindowTitle = "UAV Playground - UAVsim";
  static final int kWindowWidth = 330;
  static final int kWindowHeight = 620;
  static final int kFrameRate = 30;

  // These are all the components that are used for the UAVsim
  ArtificialHorizon horizon = null;
  Autopilot autopilot = null;
  Display display = null;
  FlightGearReceiver receiver = null;
  FlightGearSender sender = null;
  Graph graphPitch = null;
  Graph graphRoll = null;
  Joystick stickRight = null;
  Joystick stickLeft = null;
  RadioButton switchAutopilot = null;
  RadioButton switchSticks = null;
  Slider pElevator = null;
  Slider iElevator = null;
  Slider dElevator = null;
  Slider pAileron = null;
  Slider iAileron = null;
  Slider dAileron = null;
  FlightGearNMEAReceiver nmeaReceiver = null;
  GoogleEarthKMLProvider kmlProvider = null;

  // The PDE setup method
  public void setup() {
    // Setup the PDE display panel
    size(kWindowWidth, kWindowHeight);
    background(Colors.kColorGrayWindow);
    if (frame != null) frame.setTitle(kWindowTitle);
    
    // Setup the PDE graphics options
    frameRate(kFrameRate);
    smooth();

    // Setup UAV Playground components
    setupFlightGear();
    setupDisplay();
    setupGraphs();
    setupHorizon();
    setupSticks();
    setupAutopilot();
    setupGPS();
    
    // Setup the UAV Playground component dependencies
    setupDependencies();
  }
    
  // Flight Gear initialization
  void setupFlightGear() {
    // Setup and start the FlightGear data receiver
    receiver = new FlightGearReceiver(kUAVsimPort);

    // Setup and start the FlightGear data sender
    sender = new FlightGearSender(kFlightGearGenericIP, kFlightGearGenericPort);
  }

  void setupDisplay() {
    // Setup a display to display some incoming FlightGear data
    display = new Display(this, 170, 10);
  }

  void setupGraphs() {
    // Setup a graph to display pitch and elevator values
    graphPitch = new Graph(this, 10, 170);
    graphPitch.addGraph(kLabelPitch, Colors.kColorBlack);
    graphPitch.addGraph(kLabelElevator, Colors.kColorRed);
    graphPitch.getSignal(kLabelPitch).setBandwidth(45, -45);

    // Setup a graph to display roll and aileron values
    graphRoll = new Graph(this, 170, 170);
    graphRoll.addGraph(kLabelRoll, Colors.kColorBlack);
    graphRoll.addGraph(kLabelAileron, Colors.kColorRed);
    graphRoll.getSignal(kLabelRoll).setBandwidth(-45, 45);
  }

  void setupHorizon() {
    // Setup the artificial horizon
    horizon = new ArtificialHorizon(this, 10, 10);
  }

  void setupSticks() {
    // Setup the left stick (throttle and rudder)
    stickLeft = new Joystick(this, "Rudder", "Throttle", 10, 460);
    // Set 'dual rate' bandwidth for rudder (FG uses +-1)
    stickLeft.setBandwidthX(-0.5, 0.5);
    // Set bandwidth for throttle (FG uses 0-1)
    stickLeft.setBandwidthY(0, 1);
    // The throttle has no spring
    stickLeft.setSpringY(false);

    // Setup the right stick (elevator and aileron)
    stickRight = new Joystick(this, "Aileron", "Elevator", 170, 460);
    // Set 'dual rate' bandwidth for aileron (FG uses +-1)
    stickRight.setBandwidthX(-0.5, 0.5);
    // Set 'dual rate' bandwidth for elevator (FG uses +-1)
    stickRight.setBandwidthY(-0.5, 0.5);

    // Setup an on/off switch and connect the sticks to it
    switchSticks = new RadioButton(this, "Manual", 10, 330);
    switchSticks.setValue(switchSticks.getHigh()); // switch it on by default
    switchSticks.addSignalListener(stickLeft.getPowerSignal());
    switchSticks.addSignalListener(stickRight.getPowerSignal());
  }

  void setupAutopilot() {
    // Setup the sliders for the elevator gains
    pElevator = new Slider(this, "P - Elevator", 80, 330);
    pElevator.setBandwidthY(-2, 2);
    pElevator.setValue(kElevatorP); // gain here
    iElevator = new Slider(this, "I - Elevator", 120, 330);
    iElevator.setBandwidthY(-0.3, 0.3);
    iElevator.setValue(kElevatorI); // gain here
    dElevator = new Slider(this, "D - Elevator", 160, 330);
    dElevator.setBandwidthY(-1, 1);
    dElevator.setValue(kElevatorD); // gain here

    // Setup the sliders for the aileron gains
    pAileron = new Slider(this, "P - Aileron", 200, 330);
    pAileron.setBandwidthY(-2, 2);
    pAileron.setValue(kAileronP); // gain here
    iAileron = new Slider(this, "I - Aileron", 240, 330);
    iAileron.setBandwidthY(-0.3, 0.3);
    iAileron.setValue(kAileronI); // gain here
    dAileron = new Slider(this, "D - Aileron", 280, 330);
    dAileron.setBandwidthY(-1, 1);
    dAileron.setValue(kAileronD); // gain here
    
    // Start and initialize the autopilot
    autopilot = new Autopilot();
    autopilot.getPitch().setValue(0);
    autopilot.getRoll().setValue(0);
    autopilot.getElevator().setValue(0);
    autopilot.getAileron().setValue(0);
    // Reset gains of the autopilot
    autopilot.getPElevator().setValue(kElevatorP);
    autopilot.getIElevator().setValue(kElevatorI);
    autopilot.getDElevator().setValue(kElevatorD);
    autopilot.getPAileron().setValue(kAileronP);
    autopilot.getDAileron().setValue(kAileronI);
    autopilot.getDAileron().setValue(kAileronD);

    // Connect the autopilot to the gain sliders
    pElevator.addSignalListener(autopilot.getPElevator());
    iElevator.addSignalListener(autopilot.getIElevator());
    dElevator.addSignalListener(autopilot.getDElevator());
    pAileron.addSignalListener(autopilot.getPAileron());
    iAileron.addSignalListener(autopilot.getIAileron());
    dAileron.addSignalListener(autopilot.getDAileron());

    // Setup an on/off switch and connect the autopilot to it
    switchAutopilot = new RadioButton(this, "Autopilot", 10, 410);
    switchAutopilot.addSignalListener(autopilot.getPower());
  }
  
  void setupGPS() {
    // Setup the Google KML writer
    kmlProvider = new GoogleEarthKMLProvider(kKMLProviderPort);
    kmlProvider.setWritePlacemaks(false);

    // Setup the FlightGear NMEA receiver and connect the KML writer to it
    nmeaReceiver = new FlightGearNMEAReceiver(kFlightGearNMEAPort);
    nmeaReceiver.addTrackpointListener(kmlProvider);
  }

  void setupDependencies() {
    // Connect the artificial horizon to the FlightGear receiver
    receiver.addSignalListener(FlightGearReceiver.kPitch, horizon.getPitch());
    receiver.addSignalListener(FlightGearReceiver.kRoll, horizon.getRoll());

    // Connect the autopilot to the the FlightGear receiver
    receiver.addSignalListener(FlightGearReceiver.kPitch, autopilot.getPitch());
    receiver.addSignalListener(FlightGearReceiver.kRoll, autopilot.getRoll());
    
    // Connect the display to the FlightGear receiver
    receiver.addSignalListener(FlightGearReceiver.kRollRate, display.createTextLine("Roll rate"));
    receiver.addSignalListener(FlightGearReceiver.kPitchRate, display.createTextLine("Pitch rate"));
    receiver.addSignalListener(FlightGearReceiver.kYawRate, display.createTextLine("Yaw rate"));
    receiver.addSignalListener(FlightGearReceiver.kAirSpeed, display.createTextLine("Airspeed"));
    receiver.addSignalListener(FlightGearReceiver.kAltitude, display.createTextLine("Altitude"));
    receiver.addSignalListener(FlightGearReceiver.kVerticalSpeed, display.createTextLine("Vert. speed"));
    receiver.addSignalListener(FlightGearReceiver.kPitch, display.createTextLine("Pitch"));
    receiver.addSignalListener(FlightGearReceiver.kRoll, display.createTextLine("Roll"));

    // Connect the roll and pitch graph to the FlightGear receiver
    receiver.addSignalListener(FlightGearReceiver.kRoll, graphRoll.getSignal(kLabelRoll));
    receiver.addSignalListener(FlightGearReceiver.kPitch, graphPitch.getSignal(kLabelPitch));

    // Connect the FlightGear sender to the sticks
    stickRight.addListenerX(sender.getAileron());
    stickRight.addListenerY(sender.getElevator());
    stickLeft.addListenerX(sender.getRudder());
    stickLeft.addListenerY(sender.getThrottle());

    // Connect FlightGear sender to the autopilot
    autopilot.addElevatorListener(sender.getElevator());
    autopilot.addAileronListener(sender.getAileron());

    // Connect the artificial horizon to the autopilot
    autopilot.addElevatorListener(horizon.getElevator());
    autopilot.addAileronListener(horizon.getAileron());

    // Connect the elevator and aileron graph to the autopilot
    autopilot.addElevatorListener(graphPitch.getSignal(kLabelElevator));
    autopilot.addAileronListener(graphRoll.getSignal(kLabelAileron));
  }

  // The PDE draw method
  public void draw() {
    stickRight.draw();
    stickLeft.draw();
    horizon.draw();
    pElevator.draw();
    iElevator.draw();
    dElevator.draw();
    pAileron.draw();
    iAileron.draw();
    dAileron.draw();
    switchAutopilot.draw();
    switchSticks.draw();
    graphPitch.draw();
    graphRoll.draw();
    display.draw();
  }

  // The PDE mouseDragged method
  public void mouseDragged() {
    stickRight.mouseDragged(mouseX, mouseY);
    stickLeft.mouseDragged(mouseX, mouseY);
    pElevator.mouseDragged(mouseX, mouseY);
    iElevator.mouseDragged(mouseX, mouseY);
    dElevator.mouseDragged(mouseX, mouseY);
    pAileron.mouseDragged(mouseX, mouseY);
    iAileron.mouseDragged(mouseX, mouseY);
    dAileron.mouseDragged(mouseX, mouseY);
   }

  // The PDE mousePressed method
  public void mousePressed() {
    stickRight.mousePressed(mouseX, mouseY);
    stickLeft.mousePressed(mouseX, mouseY);
    pElevator.mousePressed(mouseX, mouseY);
    iElevator.mousePressed(mouseX, mouseY);
    dElevator.mousePressed(mouseX, mouseY);
    pAileron.mousePressed(mouseX, mouseY);
    iAileron.mousePressed(mouseX, mouseY);
    dAileron.mousePressed(mouseX, mouseY);
    switchAutopilot.mousePressed(mouseX, mouseY);
    switchSticks.mousePressed(mouseX, mouseY);
  }

  // The PDE mouseReleased method
  public void mouseReleased() {
    stickRight.mouseReleased(mouseX, mouseY);
    stickLeft.mouseReleased(mouseX, mouseY);
    pElevator.mouseReleased(mouseX, mouseY);
    iElevator.mouseReleased(mouseX, mouseY);
    dElevator.mouseReleased(mouseX, mouseY);
    pAileron.mouseReleased(mouseX, mouseY);
    iAileron.mouseReleased(mouseX, mouseY);
    dAileron.mouseReleased(mouseX, mouseY);
    switchAutopilot.mouseReleased(mouseX, mouseY);
    switchSticks.mouseReleased(mouseX, mouseY);
  }

  // The PDE mouseMoved method
  public void mouseMoved() {
    stickRight.mouseMoved(mouseX, mouseY);
    stickLeft.mouseMoved(mouseX, mouseY);
    pElevator.mouseMoved(mouseX, mouseY);
    iElevator.mouseMoved(mouseX, mouseY);
    dElevator.mouseMoved(mouseX, mouseY);
    pAileron.mouseMoved(mouseX, mouseY);
    iAileron.mouseMoved(mouseX, mouseY);
    dAileron.mouseMoved(mouseX, mouseY);
    switchAutopilot.mouseMoved(mouseX, mouseY);
    switchSticks.mouseMoved(mouseX, mouseY);
  }

  // Destroy is called on applet exit
  public void destroy() {
    receiver.shutDown();
    sender.shutDown();
    nmeaReceiver.shutDown();
  }
  // through the main method this applet can be started as an application
  public static void main(String args[]) {
    System.out.println(
        "FlightGear generic output IP address = " + kFlightGearGenericIP +
        "\nFlightGear generic output port = " + kFlightGearGenericPort +
        "\nFlightGear NMEA output port = " + kFlightGearNMEAPort +
        "\nUAVsim generic output port = " + kUAVsimPort +
        "\nUAVsim KML output port = " + kKMLProviderPort);

    PApplet.main(new String[] { "jaron.uavsim.UAVsim" });
  }
}