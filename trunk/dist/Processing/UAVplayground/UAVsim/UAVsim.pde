/**
 * The UAVsim is a demonstration application for the UAV
 * Playground. In combination with the FlightGear flight simulator and Google
 * Earth it demonstrates the stabilization and navigation capabilities that
 * are built into the UAV Playground.
 *
 * The UAV Playground distribution contains all the files that you need to try
 * out this example application.
 * 01 - Follow the installation instructions in the file UAVplayground-ReadMe-Installation.txt
 * 02 - Start this application (UAVsim)
 * 03 - Start FlightGear by double-clicking on Start-FlightGear-UAVsim.bat
 * 04 - [Start Google Earth by double-clicking on Start-GoogleEarth-Tracking.kml] (optional)
 * 05 - Use the virtual joysticks to fly the plane manually
 * 06 - Click on on the button labeled "Navigate" and the autopilot navigates the plane to the
 *      preset waypoints
 * 07 - If you click on on the button labeled "Go Home" then the autopilot navigates the plane
 *      to the preset home coordinates and lets it circle there
 */

import jaron.autopilot.FlightData;
import jaron.autopilot.FlightGearGpsReceiver;
import jaron.autopilot.FlightGearMotionSensor;
import jaron.autopilot.FlightGearServoController;
import jaron.autopilot.MissionController;
import jaron.autopilot.MotionController;
import jaron.google.GoogleEarthKMLProvider;
import jaron.gui.Colors;
import jaron.pde.ArtificialHorizon;
import jaron.pde.Display;
import jaron.pde.Graph;
import jaron.pde.Joystick;
import jaron.pde.Label;
import jaron.pde.RadioButton;
import jaron.pde.Slider;

import processing.core.PApplet;

public class UAVsim extends PApplet {
  // GUI defaults
  static final String WINDOW_TITLE = "UAVsim";
  static final int WINDOW_WIDTH = 410;
  static final int WINDOW_HEIGHT = 710;
  static final int FRAME_RATE = 30;

  // Stabilization PID processor default settings
  static final double PITCH_P = 2.0f;
  static final double PITCH_I = 0.3f;
  static final double PITCH_D = 0.75f;
  static final double PITCH_M = 1.0f;
  static final double ROLL_P = 1.0f;
  static final double ROLL_I = 0.05f;
  static final double ROLL_D = 0.75f;
  static final double ROLL_M = 1.0f;
  
  // Course PID processor default settings
  static final double NAVIGATION_P = 6.0f;
  static final double NAVIGATION_I = 0.01f;
  static final double NAVIGATION_D = 0.2f;
  static final double NAVIGATION_M = 1.0f;
  
  // The navigation coordinates (edit here)
  double home[] = {37.613631, -122.357389};
  double waypoints[][] = {
      {37.627128, -122.389497},
      {37.608794, -122.379219},
      {37.627634, -122.366452}
  };
  
  // The components that are used for the autopilot
  FlightData flightData;
  FlightGearServoController servoController;
  FlightGearMotionSensor motionSensor;
  FlightGearGpsReceiver gpsReceiver;
  MissionController missionController;
  MotionController motionController;

  // This component is used to visualize the mission in Google Earth
  GoogleEarthKMLProvider googleEarth;
  
  // The PDE GUI components
  Label labelFlightData;
  Label labelStabilization;
  Label labelNavigation;
  Label labelControls;
  ArtificialHorizon artificialHorizon;
  Display flightDataDisplay;
  Slider pitchGainP;
  Slider pitchGainI;
  Slider pitchGainD;
  Slider pitchMaxI;
  Slider pitchMinI;
  Slider rollGainP;
  Slider rollGainI;
  Slider rollGainD;
  Slider rollMaxI;
  Slider rollMinI;
  Graph graphPitch;
  Graph graphRoll;
  Slider courseGainP;
  Slider courseGainI;
  Slider courseGainD;
  Slider courseMaxI;
  Slider courseMinI;
  Display missionDisplay;
  RadioButton switchStabilize;
  RadioButton switchNavigation;
  RadioButton switchHome;
  Joystick stickRight;
  Joystick stickLeft;
  
  /**
   * This is the PDE setup method that is called once at startup.
   * 
   * @see processing.core.PApplet#setup()
   */
  public void setup() {
    // Setup the PDE environment
    size(WINDOW_WIDTH, WINDOW_HEIGHT);
    background(Colors.GRAY_WINDOW);
    if (frame != null) frame.setTitle(WINDOW_TITLE);
    frameRate(FRAME_RATE);
    smooth();

    // Setup the application
    setupAutopilot();
    setupMission();
    setupGoogleEarth();
    setupUserInterface();
  }

  /**
   * Initializes the autopilot components. All the input and output data is
   * interchanged via the <code>FlightData</code> component. 
   */
  public void setupAutopilot() {
    // The flight data component
    flightData = new FlightData();
    
    // Setup the motion sensor(s)
    motionSensor = new FlightGearMotionSensor();
    // Motion sensor(s) output data
    motionSensor.getPitchAngle().addSignalListener(flightData.getPitchAngle());
    motionSensor.getRollAngle().addSignalListener(flightData.getRollAngle());
    motionSensor.getAirSpeed().addSignalListener(flightData.getAirSpeed());
    motionSensor.getVerticalSpeed().addSignalListener(flightData.getVerticalSpeed());
    motionSensor.getPitchAngularRate().addSignalListener(flightData.getPitchAngularRate());
    motionSensor.getRollAngularRate().addSignalListener(flightData.getRollAngularRate());
    motionSensor.getYawAngularRate().addSignalListener(flightData.getYawAngularRate());

    // Setup the GPS receiver
    gpsReceiver = new FlightGearGpsReceiver();
    // GPS receiver output data
    gpsReceiver.getLatitude().addSignalListener(flightData.getLatitude());
    gpsReceiver.getLongitude().addSignalListener(flightData.getLongitude());
    gpsReceiver.getCourseOverGround().addSignalListener(flightData.getCourseOverGround());
    gpsReceiver.getSpeedOverGround().addSignalListener(flightData.getSpeedOverGround());
    gpsReceiver.getAltitudeAbsolute().addSignalListener(flightData.getAltitudeAbsolute());
    gpsReceiver.getSatellites().addSignalListener(flightData.getSatellites());
    
    // Setup the actuators/servos
    servoController = new FlightGearServoController();
    // Actuator input data
    flightData.getAileronOutput().addSignalListener(servoController.getAileron());
    flightData.getElevatorOutput().addSignalListener(servoController.getElevator());
    flightData.getRudderOutput().addSignalListener(servoController.getRudder());
    flightData.getThrottleOutput().addSignalListener(servoController.getThrottle());
    
    // Setup the motion controller
    motionController = new MotionController();
    // Motion controller input data
    flightData.getAileronInput().addSignalListener(motionController.getAileronInput());
    flightData.getElevatorInput().addSignalListener(motionController.getElevatorInput());
    flightData.getPitchAngle().addSignalListener(motionController.getPitchAngle());
    flightData.getRollAngle().addSignalListener(motionController.getRollAngle());
    flightData.getPitchAnglePreset().addSignalListener(motionController.getPitchAnglePreset());
    flightData.getRollAnglePreset().addSignalListener(motionController.getRollAnglePreset());
    flightData.getPitchTrim().addSignalListener(motionController.getPitchTrim());
    flightData.getRollTrim().addSignalListener(motionController.getRollTrim());
    // Motion controller output data
    motionController.getAileronOutput().addSignalListener(flightData.getAileronOutput());
    motionController.getElevatorOutput().addSignalListener(flightData.getElevatorOutput());
    // Motion controller pitch an roll PID presets
    motionController.getPitchMaxI().setValue(PITCH_M);
    motionController.getPitchMinI().setValue(-PITCH_M);
    motionController.getPitchGainP().setValue(PITCH_P);
    motionController.getPitchGainI().setValue(PITCH_I);
    motionController.getPitchGainD().setValue(PITCH_D);
    motionController.getRollMaxI().setValue(ROLL_M);
    motionController.getRollMinI().setValue(-ROLL_M);
    motionController.getRollGainP().setValue(ROLL_P);
    motionController.getRollGainI().setValue(ROLL_I);
    motionController.getRollGainD().setValue(ROLL_D);
    
    // Setup the mission controller
    missionController = new MissionController();
    // Mission controller input data
    flightData.getLatitude().addSignalListener(missionController.getLatitude());
    flightData.getLongitude().addSignalListener(missionController.getLongitude());
    flightData.getCourseOverGround().addSignalListener(missionController.getCourseOverGround());
    flightData.getSpeedOverGround().addSignalListener(missionController.getSpeedOverGround());
    // Mission controller output data
    missionController.getTargetCourse().addSignalListener(flightData.getTargetCourse());
    missionController.getPitchAnglePreset().addSignalListener(flightData.getPitchAnglePreset());
    missionController.getRollAnglePreset().addSignalListener(flightData.getRollAnglePreset());
    // Mission controller course PID presets
    missionController.getCourseMaxI().setValue(NAVIGATION_M);
    missionController.getCourseMinI().setValue(-NAVIGATION_M);
    missionController.getCourseGainP().setValue(NAVIGATION_P);
    missionController.getCourseGainI().setValue(NAVIGATION_I);
    missionController.getCourseGainD().setValue(NAVIGATION_D);
    // Mission controller current waypoint index
    missionController.getCurrentWaypoint().addSignalListener(flightData.getCurrentWaypointIndex());
  }
  
  /**
   * Setup the mission.
   */
  public void setupMission() {
    // The navigation coordinates 
    missionController.setHome(home[0], home[1]);
    for (int i=0; i<waypoints.length; ++i) {
      missionController.addWaypoint(waypoints[i][0], waypoints[i][1]);
    }

    // Set the radius within which a target waypoint is supposed to be hit
    missionController.setTargetRadius(200);
    // Set the radius for circling around a waypoint (home)
    missionController.setCirclingRadius(300);
    // Set the circling direction (CIRCLE_CLOCKWISE/CIRCLE_ANTICLOCKWISE)
    missionController.setCirclingDirection(MissionController.CIRCLE_ANTICLOCKWISE);
    // Set the maximum bank angle for roll
    missionController.setMaximumRollAngle(40);
    // Determine what follows after the mission is completed (CIRCLE_AT_HOME/RESTART_MISSION) 
    missionController.setMissionCompletedAction(MissionController.RESTART_MISSION);
  }
  
  /**
   * Setup the Google Eartch KML provider to serve the gps tracking information.
   */
  public void setupGoogleEarth() {
    // Prepare the KML provider for Google Earth
    googleEarth = new GoogleEarthKMLProvider();
    googleEarth.setWritePlacemaks(false);
    gpsReceiver.addTrackpointListener(googleEarth);
  }
  
  /**
   * Setup the user interface.
   */
  public void setupUserInterface() {
    // Setup the labels that describe the panel's functionallity
    labelFlightData = new Label(this, "Flight Data", 10, 10, 20, 150);
    labelFlightData.setColorBackground(Colors.GRAY_DARK);
    labelFlightData.setColorFrame(Colors.BLACK);
    labelFlightData.setColorText(Colors.WHITE);
    labelFlightData.setTextOrientation(Label.VERTICAL);
    labelStabilization = new Label(this, "Stabilization", 10, 170, 20, 250);
    labelStabilization.setColorBackground(Colors.GRAY_DARK);
    labelStabilization.setColorFrame(Colors.BLACK);
    labelStabilization.setColorText(Colors.WHITE);
    labelStabilization.setTextOrientation(Label.VERTICAL);
    labelNavigation = new Label(this, "Navigation", 10, 430, 20, 120);
    labelNavigation.setColorBackground(Colors.GRAY_DARK);
    labelNavigation.setColorFrame(Colors.BLACK);
    labelNavigation.setColorText(Colors.WHITE);
    labelNavigation.setTextOrientation(Label.VERTICAL);
    labelControls = new Label(this, "Controls", 10, 560, 20, 140);
    labelControls.setColorBackground(Colors.GRAY_DARK);
    labelControls.setColorFrame(Colors.BLACK);
    labelControls.setColorText(Colors.WHITE);
    labelControls.setTextOrientation(Label.VERTICAL);

    // Setup the artificial horizon and connect it to the flight data
    artificialHorizon = new ArtificialHorizon(this, 40, 10);
    flightData.getPitchAngle().addSignalListener(artificialHorizon.getPitch());
    flightData.getElevatorOutput().addSignalListener(artificialHorizon.getElevator());
    flightData.getRollAngle().addSignalListener(artificialHorizon.getRoll());
    flightData.getAileronOutput().addSignalListener(artificialHorizon.getAileron());

    // Setup a display and connect it to the flight data
    flightDataDisplay = new Display(this, 200, 10, 200, 150);
    flightData.getAirSpeed().addSignalListener(flightDataDisplay.createTextLine("Airspeed [km/h]"));
    flightData.getAltitudeAbsolute().addSignalListener(flightDataDisplay.createTextLine("Altitude (barom.) [m]"));
    flightData.getPitchAngle().addSignalListener(flightDataDisplay.createTextLine("Pitch [deg]"));
    flightData.getPitchAngularRate().addSignalListener(flightDataDisplay.createTextLine("Pitch rate [deg/sec]"));
    flightData.getRollAngle().addSignalListener(flightDataDisplay.createTextLine("Roll [deg]"));
    flightData.getRollAngularRate().addSignalListener(flightDataDisplay.createTextLine("Roll rate [deg/sec]"));
    flightData.getThrottleOutput().addSignalListener(flightDataDisplay.createTextLine("Throttle"));
    flightData.getVerticalSpeed().addSignalListener(flightDataDisplay.createTextLine("Vert. speed [m/s]"));
    flightData.getYawAngularRate().addSignalListener(flightDataDisplay.createTextLine("Yaw rate [deg/sec]"));

    // Setup the sliders for the pitch PID gains
    pitchGainP = new Slider(this, "Pitch Gain-P", 40, 170);
    pitchGainP.setBandwidthY(0, 4);
    pitchGainP.setValue(PITCH_P);
    pitchGainI = new Slider(this, "Pitch Gain-I", 80, 170);
    pitchGainI.setBandwidthY(0, 2);
    pitchGainI.setValue(PITCH_I);
    pitchGainD = new Slider(this, "Pitch Gain-D", 120, 170);
    pitchGainD.setBandwidthY(0, 2);
    pitchGainD.setValue(PITCH_D);
    pitchMaxI = new Slider(this, "Pitch Max-I", 160, 170);
    pitchMaxI.setBandwidthY(0, 2);
    pitchMaxI.setValue(PITCH_M);
    pitchMinI = new Slider(this, "Pitch Min-I", 200, 170);
    pitchMinI.setBandwidthY(0, -2);
    pitchMinI.setValue(-PITCH_M);
    // Pitch PID to motion controller dependencies
    pitchMaxI.addSignalListener(motionController.getPitchMaxI());
    pitchMinI.addSignalListener(motionController.getPitchMinI());
    pitchGainP.addSignalListener(motionController.getPitchGainP());
    pitchGainI.addSignalListener(motionController.getPitchGainI());
    pitchGainD.addSignalListener(motionController.getPitchGainD());

    // Setup the sliders for the roll PID gains
    rollGainP = new Slider(this, "Roll Gain-P", 40, 300);
    rollGainP.setBandwidthY(0, 4);
    rollGainP.setValue(ROLL_P);
    rollGainI = new Slider(this, "Roll Gain-I", 80, 300);
    rollGainI.setBandwidthY(0, 2);
    rollGainI.setValue(ROLL_I);
    rollGainD = new Slider(this, "Roll Gain-D", 120, 300);
    rollGainD.setBandwidthY(0, 2);
    rollGainD.setValue(ROLL_D);
    rollMaxI = new Slider(this, "Roll Max-I", 160, 300);
    rollMaxI.setBandwidthY(0, 2);
    rollMaxI.setValue(ROLL_M);
    rollMinI = new Slider(this, "Roll Min-I", 200, 300);
    rollMinI.setBandwidthY(0, -2);
    rollMinI.setValue(-ROLL_M);
    // Roll PID to motion controller dependencies
    rollMaxI.addSignalListener(motionController.getRollMaxI());
    rollMinI.addSignalListener(motionController.getRollMinI());
    rollGainP.addSignalListener(motionController.getRollGainP());
    rollGainI.addSignalListener(motionController.getRollMinI());
    rollGainD.addSignalListener(motionController.getRollGainD());

    // Setup a graph to display the pitch angle and the elevator output
    String kLabelPitch = "Pitch angle";
    String kLabelElevator ="Elevator signal";
    graphPitch = new Graph(this, 250, 170, 150, 120);
    graphPitch.addGraph(kLabelPitch, Colors.BLACK);
    graphPitch.getSignal(kLabelPitch).setBandwidth(45, -45);
    graphPitch.addGraph(kLabelElevator, Colors.RED);
    // Connect the pitch graph to the flight data
    flightData.getPitchAngle().addSignalListener(graphPitch.getSignal(kLabelPitch));
    flightData.getElevatorOutput().addSignalListener(graphPitch.getSignal(kLabelElevator));

    // Setup a graph to display the roll angle and the aileron output
    String kLabelRoll = "Roll angle";
    String kLabelAileron ="Aileron signal";
    graphRoll = new Graph(this, 250, 300, 150, 120);
    graphRoll.addGraph(kLabelRoll, Colors.BLACK);
    graphRoll.getSignal(kLabelRoll).setBandwidth(-45, 45);
    graphRoll.addGraph(kLabelAileron, Colors.RED);
    // Connect the roll graph to the flight data
    flightData.getRollAngle().addSignalListener(graphRoll.getSignal(kLabelRoll));
    flightData.getAileronOutput().addSignalListener(graphRoll.getSignal(kLabelAileron));
    
    // Setup the sliders for the course PID gains
    courseGainP = new Slider(this, "Course Gain-P", 40, 430, 40, 120);
    courseGainP.setBandwidthY(0, 20);
    courseGainP.setValue(NAVIGATION_P);
    courseGainI = new Slider(this, "Course Gain-I", 80, 430, 40, 120);
    courseGainI.setBandwidthY(0, 5);
    courseGainI.setValue(NAVIGATION_I);
    courseGainD = new Slider(this, "Course Gain-D", 120, 430, 40, 120);
    courseGainD.setBandwidthY(0, 5);
    courseGainD.setValue(NAVIGATION_D);
    courseMaxI = new Slider(this, "Course Max-I", 160, 430);
    courseMaxI.setBandwidthY(0, 20);
    courseMaxI.setValue(NAVIGATION_M);
    courseMinI = new Slider(this, "Course Min-I", 200, 430, 40, 120);
    courseMinI.setBandwidthY(0, -20);
    courseMinI.setValue(-NAVIGATION_M);
    // Course PID to mission controller dependencies
    courseGainP.addListenerY(missionController.getCourseGainP());
    courseGainI.addListenerY(missionController.getCourseGainI());
    courseGainD.addListenerY(missionController.getCourseGainD());
    courseMaxI.addListenerY(missionController.getCourseMaxI());
    courseMinI.addListenerY(missionController.getCourseMinI());
    
    // Mission data display
    missionDisplay = new Display(this, 250, 430, 150, 120);
    flightData.getAltitudeAbsolute().addSignalListener(missionDisplay.createTextLine("Altitude (GPS)"));
    flightData.getCourseOverGround().addSignalListener(missionDisplay.createTextLine("Course"));
    flightData.getTargetCourse().addSignalListener(missionDisplay.createTextLine("Course to target"));
    flightData.getLatitude().addSignalListener(missionDisplay.createTextLine("Latitude"));
    flightData.getLongitude().addSignalListener(missionDisplay.createTextLine("Longitude"));
    flightData.getSpeedOverGround().addSignalListener(missionDisplay.createTextLine("Speed [km/h]"));
    flightData.getCurrentWaypointIndex().addSignalListener(missionDisplay.createTextLine("Target waypoint ID"));

    // On/off switch for the autopilot navigation modus
    switchNavigation = new RadioButton(this, "Navigate", 40, 610) {
      public void switchOn() {
        super.switchOn();
        switchStabilize.switchOn();
        switchHome.switchOff();
        missionController.startMission();
      }
      public void switchOff() {
        super.switchOff();
        missionController.stopMission();
      }
    };

    // On/off switch for the autopilot return to home modus
    switchHome = new RadioButton(this, "Go Home", 40, 660) {
      public void switchOn() {
        super.switchOn();
        switchNavigation.switchOff();
        switchStabilize.switchOn();
        missionController.goHome();
      }
      public void switchOff() {
        super.switchOff();
        missionController.stopMission();
      }
    };

    // On/off switch for the autopilot stabilization modus
    switchStabilize = new RadioButton(this, "Stabilize", 40, 560) {
      public void switchOn() {
        super.switchOn();
        missionController.stopMission();
        motionController.startStabilizing();
      }
      public void switchOff() {
        if (!switchNavigation.isOn() && !switchHome.isOn()) {
          super.switchOff();
          motionController.stopStabilizing();
        }
      }
    };
    switchStabilize.switchOn();

    // Setup the left stick (throttle and rudder)
    stickLeft = new Joystick(this, "Rudder", "Throttle", 110, 560, 140, 140);
    // Set 'dual rate' bandwidth for rudder (FG uses +-1)
    stickLeft.setBandwidthX(-0.5, 0.5);
    // Throttle preset
    stickLeft.setValueY(0.7);
    // Set bandwidth for throttle (FG uses 0-1)
    stickLeft.setBandwidthY(0, 1);
    // The throttle has no spring
    stickLeft.setSpringY(false);
    // Rudder and throttle are direct controlled via the servo controller
    stickLeft.addListenerX(flightData.getRudderOutput());
    stickLeft.addListenerY(flightData.getThrottleOutput());

    // Setup the right stick (elevator and aileron)
    stickRight = new Joystick(this, "Aileron", "Elevator", 260, 560, 140, 140);
    // Set 'dual rate' bandwidth for aileron (FG uses +-1)
    stickRight.setBandwidthX(-0.5, 0.5);
    // Set 'dual rate' bandwidth for elevator (FG uses +-1)
    stickRight.setBandwidthY(-0.5, 0.5);
    // Aileron and elevator control are filtered by the motion controller
    stickRight.addListenerX(flightData.getAileronInput());
    stickRight.addListenerY(flightData.getElevatorInput());

  }

  /**
   * The PDE draw method.
   * 
   * @see processing.core.PApplet#draw()
   */
  public void draw() {
    labelFlightData.draw();
    labelStabilization.draw();
    labelNavigation.draw();
    labelControls.draw();
    artificialHorizon.draw();
    flightDataDisplay.draw();
    pitchGainP.draw();
    pitchGainI.draw();
    pitchGainD.draw();
    pitchMaxI.draw();
    pitchMinI.draw();
    rollGainP.draw();
    rollGainI.draw();
    rollGainD.draw();
    rollMaxI.draw();
    rollMinI.draw();
    graphPitch.draw();
    graphRoll.draw();
    courseGainP.draw();
    courseGainI.draw();
    courseGainD.draw();
    courseMaxI.draw();
    courseMinI.draw();
    missionDisplay.draw();
    switchStabilize.draw();
    switchNavigation.draw();
    switchHome.draw();
    stickRight.draw();
    stickLeft.draw();
  }

  /**
   * The PDE mouseDragged method.
   * 
   * @see processing.core.PApplet#mouseDragged()
   */
  public void mouseDragged() {
    stickRight.mouseDragged(mouseX, mouseY);
    stickLeft.mouseDragged(mouseX, mouseY);
    pitchMaxI.mouseDragged(mouseX, mouseY);
    pitchMinI.mouseDragged(mouseX, mouseY);
    pitchGainP.mouseDragged(mouseX, mouseY);
    pitchGainI.mouseDragged(mouseX, mouseY);
    pitchGainD.mouseDragged(mouseX, mouseY);
    rollMaxI.mouseDragged(mouseX, mouseY);
    rollMinI.mouseDragged(mouseX, mouseY);
    rollGainP.mouseDragged(mouseX, mouseY);
    rollGainI.mouseDragged(mouseX, mouseY);
    rollGainD.mouseDragged(mouseX, mouseY);
    courseMaxI.draw();
    courseMinI.draw();
    courseGainP.draw();
    courseGainI.draw();
    courseGainD.draw();
    courseMaxI.mouseDragged(mouseX, mouseY);
    courseMinI.mouseDragged(mouseX, mouseY);
    courseGainP.mouseDragged(mouseX, mouseY);
    courseGainI.mouseDragged(mouseX, mouseY);
    courseGainD.mouseDragged(mouseX, mouseY);
  }

  /**
   * The PDE mousePressed method.
   * 
   * @see processing.core.PApplet#mousePressed()
   */
  public void mousePressed() {
    stickRight.mousePressed(mouseX, mouseY);
    stickLeft.mousePressed(mouseX, mouseY);
    pitchMaxI.mousePressed(mouseX, mouseY);
    pitchMinI.mousePressed(mouseX, mouseY);
    pitchGainP.mousePressed(mouseX, mouseY);
    pitchGainI.mousePressed(mouseX, mouseY);
    pitchGainD.mousePressed(mouseX, mouseY);
    rollMaxI.mousePressed(mouseX, mouseY);
    rollMinI.mousePressed(mouseX, mouseY);
    rollGainP.mousePressed(mouseX, mouseY);
    rollGainI.mousePressed(mouseX, mouseY);
    rollGainD.mousePressed(mouseX, mouseY);
    courseMaxI.mousePressed(mouseX, mouseY);
    courseMinI.mousePressed(mouseX, mouseY);
    courseGainP.mousePressed(mouseX, mouseY);
    courseGainI.mousePressed(mouseX, mouseY);
    courseGainD.mousePressed(mouseX, mouseY);
    switchStabilize.mousePressed(mouseX, mouseY);
    switchNavigation.mousePressed(mouseX, mouseY);
    switchHome.mousePressed(mouseX, mouseY);
  }

  /**
   * The PDE mouseReleased method.
   * 
   * @see processing.core.PApplet#mouseReleased()
   */
  public void mouseReleased() {
    stickRight.mouseReleased(mouseX, mouseY);
    stickLeft.mouseReleased(mouseX, mouseY);
    pitchMaxI.mouseReleased(mouseX, mouseY);
    pitchMinI.mouseReleased(mouseX, mouseY);
    pitchGainP.mouseReleased(mouseX, mouseY);
    pitchGainI.mouseReleased(mouseX, mouseY);
    pitchGainD.mouseReleased(mouseX, mouseY);
    rollMaxI.mouseReleased(mouseX, mouseY);
    rollMinI.mouseReleased(mouseX, mouseY);
    rollGainP.mouseReleased(mouseX, mouseY);
    rollGainI.mouseReleased(mouseX, mouseY);
    rollGainD.mouseReleased(mouseX, mouseY);
    courseMaxI.mouseReleased(mouseX, mouseY);
    courseMinI.mouseReleased(mouseX, mouseY);
    courseGainP.mouseReleased(mouseX, mouseY);
    courseGainI.mouseReleased(mouseX, mouseY);
    courseGainD.mouseReleased(mouseX, mouseY);
    switchStabilize.mouseReleased(mouseX, mouseY);
    switchNavigation.mouseReleased(mouseX, mouseY);
    switchHome.mouseReleased(mouseX, mouseY);
  }

  /**
   * The PDE mouseMoved method.
   * 
   * @see processing.core.PApplet#mouseMoved()
   */
  public void mouseMoved() {
    stickRight.mouseMoved(mouseX, mouseY);
    stickLeft.mouseMoved(mouseX, mouseY);
    pitchMaxI.mouseMoved(mouseX, mouseY);
    pitchMinI.mouseMoved(mouseX, mouseY);
    pitchGainP.mouseMoved(mouseX, mouseY);
    pitchGainI.mouseMoved(mouseX, mouseY);
    pitchGainD.mouseMoved(mouseX, mouseY);
    rollMaxI.mouseMoved(mouseX, mouseY);
    rollMinI.mouseMoved(mouseX, mouseY);
    rollGainP.mouseMoved(mouseX, mouseY);
    rollGainI.mouseMoved(mouseX, mouseY);
    rollGainD.mouseMoved(mouseX, mouseY);
    courseMaxI.mouseMoved(mouseX, mouseY);
    courseMinI.mouseMoved(mouseX, mouseY);
    courseGainP.mouseMoved(mouseX, mouseY);
    courseGainI.mouseMoved(mouseX, mouseY);
    courseGainD.mouseMoved(mouseX, mouseY);
    switchStabilize.mouseMoved(mouseX, mouseY);
    switchNavigation.mouseMoved(mouseX, mouseY);
    switchHome.mouseMoved(mouseX, mouseY);
  }

  /**
   * The destroy method is called on application exit.
   * 
   * @see processing.core.PApplet#destroy()
   */
 public void destroy() {
    servoController.shutDown();
    motionSensor.shutDown();
    gpsReceiver.shutDown();
  }

  /**
   * Through the main method this applet can be started as an application.
   * 
   * @param args unused
   */
  public static void main(String args[]) {
    PApplet.main(new String[] { "jaron.uavsim.UAVsim" });
  }
}

