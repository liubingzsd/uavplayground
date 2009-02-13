/**
 * The UAVsim implements a simple PID controller that controls
 * the pitch and roll axis of an airplane in the FlightGear
 * flight simulator.
 * In the UAVsim's manual mode FlightGear is controlled via two
 * virtual joysticks.
 *
 * Have a look at the end of this code for some information
 * about setting up and starting FlightGear.
 */
 
import jaron.flightgear.*;
import jaron.gui.*;
import jaron.pde.*;
import jaron.uavsim.*;

// Network connection defaults
static String FlightGearIP ="127.0.0.1";
static int FlightGearPort = 5556;
static int UAVsimPort = 5555;

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

// PDE GUI defaults
static final String kWindowTitle = "UAV Playground - UAVsim";
static final int kWindowWidth = 330;
static final int kWindowHeight = 620;
static final int kFrameRate = 30;

// These are all the components that are used for the UAVsim
ArtificialHorizon horizon;
Autopilot autopilot;
Display display;
FlightGearReceiver receiver;
FlightGearSender sender;
Graph graphPitch;
Graph graphRoll;
Joystick stickRight;
Joystick stickLeft;
RadioButton switchAutopilot;
RadioButton switchSticks;
Slider pElevator;
Slider iElevator;
Slider dElevator;
Slider pAileron;
Slider iAileron;
Slider dAileron;

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
  
  // Setup the UAV Playground component dependencies
  setupDependencies();
}
  
// Flight Gear initialization
void setupFlightGear() {
  // Setup and start the FlightGear data receiver
  receiver = new FlightGearReceiver(UAVsimPort);

  // Setup and start the FlightGear data sender
  sender = new FlightGearSender(FlightGearIP, FlightGearPort);
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
  pElevator = new Slider(this, "P - Elevator", 75, 330);
  pElevator.setBandwidthY(-2, 2);
  pElevator.setValue(kElevatorP); // gain here
  iElevator = new Slider(this, "I - Elevator", 115, 330);
  iElevator.setBandwidthY(-0.3, 0.3);
  iElevator.setValue(kElevatorI); // gain here
  dElevator = new Slider(this, "D - Elevator", 155, 330);
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
  receiver.setDebug(true);
  receiver.shutDown();
  
  sender.setDebug(true);
  sender.shutDown();
}

/**
 * The following is a description of how you would setup and run FlightGear 1.9
 * to be used width this application.
 * It assumes that you've installed FlightGear in a directory that is referred
 * here as %PROGRAMFILES%\FlightGear.
 * 
 * 01 - Create a configuration file: %PROGRAMFILES%\FlightGear\data\Protocol\UAVsim-Protocol.xml
 * 
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
 * 03 - Start this application (UAVsim)
 * 
 * 04 - Start FlightGear with the following script (replace the IP address
 *      127.0.0.1 with the address of the machine the this application runs on):

"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--generic=socket,in,10,127.0.0.1,5556,tcp,UAVsim-Protocol" ^
 "--generic=socket,out,3,127.0.0.1,5555,tcp,UAVsim-Protocol" ^
 "--timeofday=noon"
 
 */

