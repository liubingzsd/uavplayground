package jaron.autopilot;

import jaron.components.Signal;

/**
 * The <code>FlightData</code> class provides the functionality for the input
 * and output data handling within the autopilot. Every autopilot component
 * provides its output data to the <code>FlightData</code> component and an
 * unlimited number of components can receive this data as their input data.
 * The event handling mechanism of the <code>Signal</code> class is used to
 * publish the data whenever a certain value has changed.
 * 
 * @see Signal
 *   
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class FlightData {
  // GPS data
  private Signal latitude = new Signal();
  private Signal longitude = new Signal();
  private Signal courseOverGround = new Signal();
  private Signal speedOverGround = new Signal();
  private Signal altitudeAbsolute = new Signal();
  private Signal satellites = new Signal();

  // MissionControl data
  private Signal targetCourse = new Signal();
  private Signal pitchAnglePreset = new Signal(); // unused in current version
  private Signal rollAnglePreset = new Signal();
  private Signal currentWaypointIndex = new Signal();
  
  // StickControls data
  private Signal aileronInput = new Signal();
  private Signal elevatorInput = new Signal();
  private Signal rudderInput = new Signal();
  private Signal throttleInput = new Signal();
  private Signal aux1Input = new Signal();        // unused in current version
  private Signal aux2Input = new Signal();        // unused in current version
  private Signal gearInput = new Signal();        // unused in current version
  
  // MotionController / Actuators
  private Signal aileronOutput = new Signal();
  private Signal elevatorOutput = new Signal();
  private Signal rudderOutput = new Signal();     // unused in current version
  private Signal throttleOutput = new Signal();   // unused in current version
  private Signal rollTrim = new Signal();
  private Signal pitchTrim = new Signal();
  
  // MotionSensor
  private Signal pitchAngle = new Signal();
  private Signal rollAngle = new Signal();
  private Signal yawAngle = new Signal();         // unused in current version
  private Signal airSpeed = new Signal();         // unused in current version
  private Signal verticalSpeed = new Signal();         // unused in current version
  private Signal pitchAngularRate = new Signal(); // unused in current version
  private Signal rollAngularRate = new Signal();  // unused in current version
  private Signal yawAngularRate = new Signal();   // unused in current version

  public Signal getCurrentWaypointIndex() {
    return currentWaypointIndex;
  }
  public void setCurrentWaypointIndex(Signal currentWaypointIndex) {
    this.currentWaypointIndex = currentWaypointIndex;
  }
  public Signal getVerticalSpeed() {
    return verticalSpeed;
  }
  public void setVerticalSpeed(Signal verticalSpeed) {
    this.verticalSpeed = verticalSpeed;
  }
  public Signal getRollTrim() {
    return rollTrim;
  }
  public void setRollTrim(Signal rollTrim) {
    this.rollTrim = rollTrim;
  }
  public Signal getPitchTrim() {
    return pitchTrim;
  }
  public void setPitchTrim(Signal pitchTrim) {
    this.pitchTrim = pitchTrim;
  }
  public Signal getAltitudeAbsolute() {
    return altitudeAbsolute;
  }
  public void setAltitudeAbsolute(Signal altitudeAbsolute) {
    this.altitudeAbsolute = altitudeAbsolute;
  }
  public Signal getSatellites() {
    return satellites;
  }
  public void setSatellites(Signal satellites) {
    this.satellites = satellites;
  }
  public Signal getLatitude() {
    return latitude;
  }
  public void setLatitude(Signal latitude) {
    this.latitude = latitude;
  }
  public Signal getLongitude() {
    return longitude;
  }
  public void setLongitude(Signal longitude) {
    this.longitude = longitude;
  }
  public Signal getCourseOverGround() {
    return courseOverGround;
  }
  public void setCourseOverGround(Signal courseOverGround) {
    this.courseOverGround = courseOverGround;
  }
  public Signal getSpeedOverGround() {
    return speedOverGround;
  }
  public void setSpeedOverGround(Signal speedOverGround) {
    this.speedOverGround = speedOverGround;
  }
  public Signal getTargetCourse() {
    return targetCourse;
  }
  public void setTargetCourse(Signal targetCourse) {
    this.targetCourse = targetCourse;
  }
  public Signal getPitchAnglePreset() {
    return pitchAnglePreset;
  }
  public void setPitchAnglePreset(Signal pitchAnglePreset) {
    this.pitchAnglePreset = pitchAnglePreset;
  }
  public Signal getRollAnglePreset() {
    return rollAnglePreset;
  }
  public void setRollAnglePreset(Signal rollAnglePreset) {
    this.rollAnglePreset = rollAnglePreset;
  }
  public Signal getAileronInput() {
    return aileronInput;
  }
  public void setAileronInput(Signal aileronInput) {
    this.aileronInput = aileronInput;
  }
  public Signal getElevatorInput() {
    return elevatorInput;
  }
  public void setElevatorInput(Signal elevatorInput) {
    this.elevatorInput = elevatorInput;
  }
  public Signal getRudderInput() {
    return rudderInput;
  }
  public void setRudderInput(Signal rudderInput) {
    this.rudderInput = rudderInput;
  }
  public Signal getThrottleInput() {
    return throttleInput;
  }
  public void setThrottleInput(Signal throttleInput) {
    this.throttleInput = throttleInput;
  }
  public Signal getAux1Input() {
    return aux1Input;
  }
  public void setAux1Input(Signal aux1Input) {
    this.aux1Input = aux1Input;
  }
  public Signal getAux2Input() {
    return aux2Input;
  }
  public void setAux2Input(Signal aux2Input) {
    this.aux2Input = aux2Input;
  }
  public Signal getGearInput() {
    return gearInput;
  }
  public void setGearInput(Signal gearInput) {
    this.gearInput = gearInput;
  }
  public Signal getAileronOutput() {
    return aileronOutput;
  }
  public void setAileronOutput(Signal aileronOutput) {
    this.aileronOutput = aileronOutput;
  }
  public Signal getElevatorOutput() {
    return elevatorOutput;
  }
  public void setElevatorOutput(Signal elevatorOutput) {
    this.elevatorOutput = elevatorOutput;
  }
  public Signal getRudderOutput() {
    return rudderOutput;
  }
  public void setRudderOutput(Signal rudderOutput) {
    this.rudderOutput = rudderOutput;
  }
  public Signal getThrottleOutput() {
    return throttleOutput;
  }
  public void setThrottleOutput(Signal throttleOutput) {
    this.throttleOutput = throttleOutput;
  }
  public Signal getPitchAngle() {
    return pitchAngle;
  }
  public void setPitchAngle(Signal pitchAngle) {
    this.pitchAngle = pitchAngle;
  }
  public Signal getRollAngle() {
    return rollAngle;
  }
  public void setRollAngle(Signal rollAngle) {
    this.rollAngle = rollAngle;
  }
  public Signal getYawAngle() {
    return yawAngle;
  }
  public void setYawAngle(Signal yawAngle) {
    this.yawAngle = yawAngle;
  }
  public Signal getAirSpeed() {
    return airSpeed;
  }
  public void setAirSpeed(Signal airSpeed) {
    this.airSpeed = airSpeed;
  }
  public Signal getPitchAngularRate() {
    return pitchAngularRate;
  }
  public void setPitchAngularRate(Signal pitchAngularRate) {
    this.pitchAngularRate = pitchAngularRate;
  }
  public Signal getRollAngularRate() {
    return rollAngularRate;
  }
  public void setRollAngularRate(Signal rollAngularRate) {
    this.rollAngularRate = rollAngularRate;
  }
  public Signal getYawAngularRate() {
    return yawAngularRate;
  }
  public void setYawAngularRate(Signal yawAngularRate) {
    this.yawAngularRate = yawAngularRate;
  }
}
