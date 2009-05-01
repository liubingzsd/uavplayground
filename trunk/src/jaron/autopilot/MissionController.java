package jaron.autopilot;

import java.util.ArrayList;

import jaron.components.Signal;
import jaron.gps.Waypoint;

/**
 * The <code>MissionController</code> class provides the functionality for 
 * guiding a vehicle through a mission that is defined by a starting point
 * (home) and an unlimited number of navigation waypoints.
 *   
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
// TODO Use true air speed instead of ground speed
public class MissionController extends Thread {
  /**
   * Used to set the circling direction.
   */
  public static final int CIRCLE_CLOCKWISE = 1;
  /**
   * Used to set the circling direction.
   */
  public static final int CIRCLE_ANTICLOCKWISE = -1;
  private static final int DO_IDLE = 0;
  private static final int DO_NAVIGATE = 1;
  /**
   * Used to determine what has to be done after the mission is completed. 
   */
  public static final int CIRCLE_AT_HOME = 2;
  /**
   * Used to determine what has to be done after the mission is completed. 
   */
  public static final int RESTART_MISSION = 3;
  private static final int TARGET_RADIUS = 200;
  private static final int CIRCLING_RADIUS = 300;
  private static final float DEFAULT_UPDATE_FREQUENCY = 10;
  /**
   * The default for the minimum speed that is necessary to calculate valid
   * navigation data (set to {@value MINIMUM_SPEED} km/h).
   */
  private static final float MINIMUM_SPEED = 1f;

  /**
   * The default for the maximum roll angle (set to {@value MAXAXIMUM_ROLL_ANGLE}
   * degrees).
   */
  private static final double MAXAXIMUM_ROLL_ANGLE = 40f;

  // Thread
  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;

  // Input/output
  private Signal latitude = new Signal();         // from a gps receiver
  private Signal longitude = new Signal();        // from a gps receiver
  private Signal courseOverGround = new Signal(); // from a gps receiver
  private Signal speedOverGround = new Signal();  // from a gps receiver
  private Signal targetCourse = new Signal();     // output internally used
  private Signal pitchAnglePreset = new Signal(); // output to motion controller (unused in current version)
  private Signal rollAnglePreset = new Signal();  // output to motion controller
  private Signal courseGainP = new Signal();      // external course PID processor settings (P, I , D, I-min, I-max)
  private Signal courseGainI = new Signal();
  private Signal courseGainD = new Signal();
  private Signal courseMaxI = new Signal();
  private Signal courseMinI = new Signal();
  private Signal currentWaypoint = new Signal();

  // Navigation
  private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
  private int currentWaypointIndex = 0;
  private double homeLatitude;
  private double homeLongitude;
  private int navigationMode = DO_IDLE;
  private double circlingRadius = CIRCLING_RADIUS;
  private int circlingDirection = CIRCLE_CLOCKWISE;
  private double targetRadius = TARGET_RADIUS;
  private int missionCompletedAction = CIRCLE_AT_HOME;

  // Mission
  private float minimumSpeed = MINIMUM_SPEED;
  private double maximumRollAngle = MAXAXIMUM_ROLL_ANGLE;
  Stabilization.PID pidCourse = new Stabilization.PID();

  /**
   * Creates a new <code>MissionController</code> object and starts it as a
   * <code>Thread</code>.
   */
  public MissionController() {
    setDaemon(true);
    start();
  }

  /**
   * Adds a new navigation waypoint to the list of the target waypoints.
   * 
   * @param latitude the latitude coordinate in decimal form
   * @param longitude the longitude coordinate in decimal form
   */
  public void addWaypoint(double latitude, double longitude) {
    waypoints.add(new Waypoint(latitude, longitude));
  }
  
  /**
   * Sets the radius for circling around a waypoint.
   * 
   * @param circlingRadius radius in meters
   */
  public void setCirclingRadius(double circlingRadius) {
    this.circlingRadius = circlingRadius;
  }

  /**
   * Sets the circling direction for circling around a waypoint.
   * 
   * @see MissionController#CIRCLE_CLOCKWISE
   * @see MissionController#CIRCLE_ANTICLOCKWISE
   * 
   * @param circlingDirection circle direction
   */
  public void setCirclingDirection(int circlingDirection) {
    this.circlingDirection = circlingDirection;
  }

  /**
   * Sets the home coordinates of the vehicle. This is for example used to let
   * the vehicle return to a certain point after a malfunction.
   *  
   * @param latitude in decimal form
   * @param longitude in decimal form
   */
  public void setHome(double latitude, double longitude) {
    this.homeLatitude = latitude;
    this.homeLongitude = longitude;
  }

  /**
   * Sets the maximum roll angle (bank angle) that the mission controller should
   * use when it guides the vehicle through a curve.
   * 
   * @param maximumRollAngle roll angle in degrees
   */
  public void setMaximumRollAngle(double maximumRollAngle) {
    this.maximumRollAngle = maximumRollAngle;
  }

  /**
   * Sets the action that has to be performed after the mission is completed.
   * 
   * @see MissionController#CIRCLE_AT_HOME
   * @see MissionController#RESTART_MISSION
   * 
   * @param missionCompletedAction either of the predefined actions
   */
  public void setMissionCompletedAction(int missionCompletedAction) {
    this.missionCompletedAction = missionCompletedAction;
  }

  /**
   * Sets the current navigation mode.
   * 
   * @see MissionController#DO_NAVIGATE
   * @see MissionController#CIRCLE_AT_HOME
   * @see MissionController#RESTART_MISSION
   * 
   * @param navigationMode either of the predefined modes
   */
  public void setNavigationMode(int navigationMode) {
    this.navigationMode = navigationMode;
  }

  /**
   * Sets the radius within which a target waypoint is supposed to be hit. If
   * set to 0 the vehicle is heading to a certain waypoint until it reaches
   * the target and then it will be heading to the next waypoint. Every value
   * greater than 0 causes the vehicle to change its heading before it reaches
   * the target waypoint.
   * 
   * @param targetRadius target radius in meters
   */
  public void setTargetRadius(double targetRadius) {
    this.targetRadius = targetRadius;
  }
  
  /**
   * Returns the current waypoint signal which is used for the event handling
   * mechanism.
   * 
   * @return the current waypoint signal
   * 
   * @see Signal
   */
  public Signal getCurrentWaypoint() {
    return currentWaypoint;
  }
  
  /**
   * Returns the course P gain signal which is used for the event handling
   * mechanism.
   * 
   * @return the course P gain signal
   * 
   * @see Signal
   */
  public Signal getCourseGainP() {
    return courseGainP;
  }
  
  /**
   * Returns the course I gain signal which is used for the event handling
   * mechanism.
   * 
   * @return the course I gain signal
   * 
   * @see Signal
   */
  public Signal getCourseGainI() {
    return courseGainI;
  }
  
  /**
   * Returns the course D gain signal which is used for the event handling
   * mechanism.
   * 
   * @return the course D gain signal
   * 
   * @see Signal
   */
  public Signal getCourseGainD() {
    return courseGainD;
  }

  /**
   * Returns the course maximum I signal which is used for the event handling
   * mechanism.
   * 
   * @return the course maximum I signal
   * 
   * @see Signal
   */
  public Signal getCourseMaxI() {
    return courseMaxI;
  }

  /**
   * Returns the course minimum I signal which is used for the event handling
   * mechanism.
   * 
   * @return the course minimum I signal
   * 
   * @see Signal
   */
  public Signal getCourseMinI() {
    return courseMinI;
  }

  /**
   * Returns the latitude signal which is used for the event handling
   * mechanism.
   * 
   * @return the latitude signal
   * 
   * @see Signal
   */
  public Signal getLatitude() {
    return latitude;
  }

  /**
   * Returns the latitude signal which is used for the event handling
   * mechanism.
   * 
   * @return the longitude signal
   * 
   * @see Signal
   */
  public Signal getLongitude() {
    return longitude;
  }

  /**
   * Returns the course over ground signal which is used for the event handling
   * mechanism.
   * 
   * @return the course over ground signal
   * 
   * @see Signal
   */
  public Signal getCourseOverGround() {
    return courseOverGround;
  }

  /**
   * Returns the speed over ground signal which is used for the event handling
   * mechanism.
   * 
   * @return the speed over ground signal
   * 
   * @see Signal
   */
  public Signal getSpeedOverGround() {
    return speedOverGround;
  }

  /**
   * Returns the target course signal which is used for the event handling
   * mechanism.
   * 
   * @return the target course signal
   * 
   * @see Signal
   */
  public Signal getTargetCourse() {
    return targetCourse;
  }

  /**
   * Returns the pitch angle preset signal which is used for the event handling
   * mechanism.
   * 
   * @return the pitch angle preset signal
   * 
   * @see Signal
   */
  public Signal getPitchAnglePreset() {
    return pitchAnglePreset;
  }

  /**
   * Returns the roll angle preset signal which is used for the event handling
   * mechanism.
   * 
   * @return the roll angle preset signal
   * 
   * @see Signal
   */
  public Signal getRollAnglePreset() {
    return rollAnglePreset;
  }

  /**
   * Sets the update frequency for the <code>MotionController</code>.
   * 
   * @param updateFrequency the new frequency in Hz
   */
  public void setUpdateFrequency(float updateFrequency) {
    this.updateFrequency = updateFrequency;
  }

  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    while(true) {
      if (navigationMode != DO_IDLE) {
        updateNavigation();
        updateGuidance();
      }
      try { sleep((long )(1000 / updateFrequency)); } catch(InterruptedException e) {} 
    }
  }
  
  /**
   * Called periodically this method updates the mission's navigation data.
   */
  private void updateNavigation() {
    // by default the target course is the current course
    double course = courseOverGround.getValue();

    if (navigationMode == DO_NAVIGATE && waypoints.size() > 0) {
      // get the current target waypoint
      Waypoint waypoint = waypoints.get(currentWaypointIndex);
      // get the distnace to the target waypoint...
      double distance = Navigation.getDistanceInMeters(latitude.getValue(), longitude.getValue(), waypoint.getLatitude(), waypoint.getLongitude());
      // ...and check if the vehicle is within the target waypoint radius
      if (distance <= targetRadius) {
        // switch to the next waypoint
        ++currentWaypointIndex;
      }
      if (currentWaypointIndex < waypoints.size()) {
        // calculate the new target course
        waypoint = waypoints.get(currentWaypointIndex);
        course  = Navigation.getCourseInDegrees(latitude.getValue(), longitude.getValue(), waypoint.getLatitude(), waypoint.getLongitude());
      }
      else {
        // all waypoints are reached -> set mission completed
        navigationMode = missionCompletedAction;
      }
      currentWaypoint.setValue(currentWaypointIndex + 1);
    }
    else if (navigationMode == CIRCLE_AT_HOME) {
      // calculate the new course (inspired by http://tom.pycke.be/mav/101/circle-navigation)
      double tSeconds = 1; // timer for the ahead calculation (in seconds)
      double vAngular = speedOverGround.getValue() / circlingRadius;
      double alpha = Navigation.getCourseInRadians(latitude.getValue(), longitude.getValue(), homeLatitude, homeLongitude) + Math.PI;
      Waypoint dest = Navigation.getDestinationPoint(new Waypoint(homeLatitude, homeLongitude), Math.toDegrees(alpha + (vAngular * tSeconds) * circlingDirection), circlingRadius);
      course = Navigation.getCourseInDegrees(latitude.getValue(), longitude.getValue(), dest.getLatitude(), dest.getLongitude());
    }
    else if (navigationMode == RESTART_MISSION) {
      currentWaypointIndex = 0;
      navigationMode = DO_NAVIGATE;
    }
    else {
      // do nothing -> current course is target course
    }
    
    // update flight data
    targetCourse.setValue(course);
  }
  
  /**
   * Called periodically this method updates the mission's vehicle guidance.
   */
  private void updateGuidance() {
    // under a certain ground speed the navigation data is inaccurate
    if (speedOverGround.getValue() > minimumSpeed) {
      // the PID processor settings could have been changed externally
      pidCourse.iMax = courseMaxI.getValue();
      pidCourse.iMin = courseMinI.getValue();
      pidCourse.pGain = courseGainP.getValue();
      pidCourse.iGain = courseGainI.getValue();
      pidCourse.dGain = courseGainD.getValue();

      // calculate how much the current course differs from the target course...
      double courseError = targetCourse.getValue() - courseOverGround.getValue();
      // ...and then determine which way around the vehicle should turn
      if (Math.abs(courseError) > 180) {
        if (courseError < -180) {
          courseError += 360;
        } else {
          courseError -= 360;
        }
      }
      
      // convert the course error into a roll angle value...
      double tiltAngle = maximumRollAngle * courseError / 180;
      tiltAngle = Stabilization.updatePID(pidCourse, tiltAngle);
      tiltAngle = Math.max(tiltAngle, -maximumRollAngle);
      tiltAngle = Math.min(tiltAngle, maximumRollAngle);
      // ...and "send" the roll command to the motion controller
      rollAnglePreset.setValue(-tiltAngle);
    }
  }
  
  /**
   * Starts the mission.
   */
  public void startMission() {
    currentWaypointIndex = 0;
    navigationMode = DO_NAVIGATE;
    currentWaypoint.setValue(currentWaypointIndex + 1);
  }
  
  /**
   * Stops the mission.
   */
  public void stopMission() {
    currentWaypointIndex = 0;
    pitchAnglePreset.setValue(0);
    rollAnglePreset.setValue(0);
    navigationMode = DO_IDLE;
    currentWaypoint.setValue(0);
  }
  
  /**
   * Stops the mission and the vehicle is guided to its home coordinates.
   */
  public void goHome() {
    currentWaypointIndex = 0;
    navigationMode = CIRCLE_AT_HOME;
    currentWaypoint.setValue(0);
  }
}