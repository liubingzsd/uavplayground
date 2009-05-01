package jaron.autopilot;

import jaron.components.Signal;

import java.util.Date;

/**
 * The <code>MotionController</code> class provides the functionality for
 * stabilizing and guiding a vehicle. It uses a motion sensor (through the
 * <code>FlightData</code> component) and expects its output for roll an pitch
 * angles to be within -180 to 180 degrees - where 0 is level flight.<br>
 * The motion controller stabilizes the vehicle as long as there is no external
 * stick input for elevator or aileron.<br>
 * The pitch and roll at which the vehicle is stabilized preset externally. This
 * is used to guide the vehicle by an external component like the
 * <code>MissionController</code>
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class MotionController extends Thread {
  /**
   * The default update frequency of the <code>MotionController</code>
   * is set to {@value DEFAULT_UPDATE_FREQUENCY} Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 10;
  private static final int DEFAULT_ATTITUDE_CORRECTION_RATE = 30; // degrees per second
  private static final float MAX_ATTITUDE_ANGLE = 60; // in degrees
  private static final float STICK_DEADBAND = 0.0f; // assuming that stick input is +-1
  private static final int DO_IDLE = 0;
  private static final int DO_STABILIZE = 1;

  private double defaultRollAngle = 0;    // current default angle in degrees (0 = level)
  private double defaultPitchAngle = 0;   // current default angle in degrees (0 = level)
  private double rollCorrectionRate = DEFAULT_ATTITUDE_CORRECTION_RATE;  // degrees per second
  private double pitchCorrectionRate = DEFAULT_ATTITUDE_CORRECTION_RATE; // degrees per second
  private Stabilization.PID pidPitch = new Stabilization.PID();
  private Stabilization.PID pidRoll = new Stabilization.PID();
  private Signal aileronInput = new Signal();     // from a stick control
  private Signal elevatorInput = new Signal();    // from a stick control
  private Signal aileronOutput = new Signal();    // to an actuator
  private Signal elevatorOutput = new Signal();   // to an actuator
  private Signal pitchAngle = new Signal();       // from a motion sensor(s)
  private Signal rollAngle = new Signal();        // from a motion sensor(s)
  private Signal rollAnglePreset = new Signal();  // from a mission controller (degrees, positive values = right tilt)
  private Signal pitchAnglePreset = new Signal(); // from a mission controller (degrees, positive values = backwards tilt)
  private Signal rollTrim = new Signal();         // unused: from a trim slider (degrees, positive values = right tilt)
  private Signal pitchTrim = new Signal();        // unused: from a trim slider (degrees, positive values = backwards tilt)
  private Signal pitchGainP = new Signal();       // external pitch PID processor settings (P, I , D, I-min, I-max)
  private Signal pitchGainI = new Signal();
  private Signal pitchGainD = new Signal();
  private Signal pitchMinI = new Signal();
  private Signal pitchMaxI = new Signal();
  private Signal rollGainP = new Signal();        // external roll PID processor settings (P, I , D, I-min, I-max)
  private Signal rollGainI = new Signal();
  private Signal rollGainD = new Signal();
  private Signal rollMinI = new Signal();
  private Signal rollMaxI = new Signal();
  private int stabilizationMode = DO_IDLE;
  /**
   * Creates an new <code>MotionController</code> object.
   */
  public MotionController() {
    setDaemon(true);
    start();
  }

  /**
   * Returns a reference to the aileron input. This enables the internal signal
   * for aileron to be externally set thus the motion controller's correction
   * is overridden by an external controller (e.g. a stick control).
   * 
   * @return aileron input signal
   */
  public Signal getAileronInput() {
    return aileronInput;
  }

  /**
   * Returns a reference to the elevator input. This enables the internal signal
   * for elevator to be externally set thus the motion controller's correction
   * is overridden by an external controller (e.g. a stick control).
   * 
   * @return elevator input signal
   */
  public Signal getElevatorInput() {
    return elevatorInput;
  }

  /**
   * Returns a reference to the aileron output.
   * 
   * @return aileron output signal
   */
  public Signal getAileronOutput() {
    return aileronOutput;
  }

  /**
   * Returns a reference to the elevator output.
   * 
   * @return elevator output signal
   */
  public Signal getElevatorOutput() {
    return elevatorOutput;
  }

  /**
   * Returns a reference to the pitch angle input. This enables the internal
   * signal for pitch angle to be externally set.
   * 
   * @return pitch angle input signal
   */
  public Signal getPitchAnglePreset() {
    return pitchAnglePreset;
  }


  /**
   * Returns a reference to the roll angle input. This enables the internal
   * signal for roll angle to be externally set.
   * 
   * @return roll angle input signal
   */
  public Signal getRollAnglePreset() {
    return rollAnglePreset;
  }

  /**
   * Returns a reference to the pitch angle input. This enables the internal
   * signal for pitch angle to be externally set.
   * 
   * @return pitch angle input signal
   */
  public Signal getPitchAngle() {
    return pitchAngle;
  }

  /**
   * Returns a reference to the roll angle output.
   * 
   * @return roll angle output signal
   */
  public Signal getRollAngle() {
    return rollAngle;
  }

  /**
   * Returns a reference to the roll angle input. This enables the internal
   * signal for roll angle to be externally set by a roll trim.
   * 
   * @return roll angle input signal
   */
  public Signal getRollTrim() {
    return rollTrim;
  }

  /**
   * Returns a reference to the pitch angle output.
   * 
   * @return roll angle pitch signal
   */
  public Signal getPitchTrim() {
    return pitchTrim;
  }

  /**
   * Returns a reference to the pitch stabilization PID parameter gain-P. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the gain-P parameter signal
   */
  public Signal getPitchGainP() {
    return pitchGainP;
  }

  /**
   * Returns a reference to the pitch stabilization PID parameter gain-I. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the gain-I parameter signal
   */
  public Signal getPitchGainI() {
    return pitchGainI;
  }

  /**
   * Returns a reference to the pitch stabilization PID parameter gain-D. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the gain-D parameter signal
   */
  public Signal getPitchGainD() {
    return pitchGainD;
  }

  /**
   * Returns a reference to the pitch stabilization PID parameter min-I. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the min-I parameter signal
   */
  public Signal getPitchMinI() {
    return pitchMinI;
  }

  /**
   * Returns a reference to the pitch stabilization PID parameter max-I. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the max-I parameter signal
   */
  public Signal getPitchMaxI() {
    return pitchMaxI;
  }

  /**
   * Returns a reference to the roll stabilization PID parameter gain-P. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the gain-P parameter signal
   */
  public Signal getRollGainP() {
    return rollGainP;
  }

  /**
   * Returns a reference to the roll stabilization PID parameter gain-I. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the gain-I parameter signal
   */
  public Signal getRollGainI() {
    return rollGainI;
  }

  /**
   * Returns a reference to the roll stabilization PID parameter gain-D. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the gain-D parameter signal
   */
  public Signal getRollGainD() {
    return rollGainD;
  }

  /**
   * Returns a reference to the roll stabilization PID parameter min-I. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the min-I parameter signal
   */
  public Signal getRollMinI() {
    return rollMinI;
  }

  /**
   * Returns a reference to the pitch stabilization PID parameter max-I. This
   * enables the adjustment of this parameter during runtime from outside the
   * motion controller.
   * 
   * @see Stabilization.PID
   * 
   * @return the max-I parameter signal
   */
  public Signal getRollMaxI() {
    return rollMaxI;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    Date timer = new Date();
    
    while(true) {
      // calculate the elapsed time since the last pass (in seconds)
      double timeElapsed = (new Date().getTime() - timer.getTime()) / 1000f;
      
      // roll is controlled by the motion controller if there is no aileron
      // input (from the stick).
      // otherwise the aileron input value is sent to the aileron output (servo
      // controller ) without any change.
      double aileronValue = aileronInput.getValue();
      if (stabilizationMode == DO_STABILIZE) {
        // the dead band setting avoids drifting (caused by the stick potentiometer) 
        if (aileronValue <= STICK_DEADBAND && aileronValue >= -STICK_DEADBAND) {
          // no aileron input -> recalculate the default roll angle.
          // the roll correction rate determines how fast level flight (0 degrees)
          // will be reached.
          // the roll angle value determines the target tilt angle (in degrees).
          if (defaultRollAngle > 0) {
            defaultRollAngle = Math.max(defaultRollAngle - ((rollCorrectionRate *  timeElapsed)), 0);
          }
          else {
            defaultRollAngle = Math.min(defaultRollAngle + ((rollCorrectionRate *  timeElapsed)),  0);
          }
          // the PID processor settings could have been changed externally
          pidRoll.pGain = rollGainP.getValue();
          pidRoll.iGain = rollGainI.getValue();
          pidRoll.dGain = rollGainD.getValue();
          pidRoll.iMin = rollMinI.getValue();
          pidRoll.iMax = rollMaxI.getValue();
          // the PID processor calculates the roll correction value according to
          // the difference between the default and the current roll angle
          double rollError = rollAngle.getValue() - defaultRollAngle + rollAnglePreset.getValue() - rollTrim.getValue();
          double rollCorrection = Stabilization.updatePID(pidRoll, rollError);
          // clip the correction angle before it is converted to a servo value
          rollCorrection = Math.max(rollCorrection, -MAX_ATTITUDE_ANGLE);
          rollCorrection = Math.min(rollCorrection, MAX_ATTITUDE_ANGLE);
          // convert the correction angle (degrees) to an aileron output value
          aileronValue = (rollCorrection / MAX_ATTITUDE_ANGLE) * -1;
        }
        else {
          defaultRollAngle = rollAngle.getValue();
        }
      }
      // the aileron value is sent to the aileron input listeners
      aileronOutput.setValue(aileronValue);

      
      // pitch is controlled by the motion controller if there is no elevator
      // input (from the stick).
      // otherwise the elevator input value is sent to the elevator output (servo
      // controller ) without any change.
      double elevatorValue = elevatorInput.getValue();
      if (stabilizationMode == DO_STABILIZE) {
        // the dead band setting avoids drifting (caused by the stick potentiometer) 
        if (elevatorValue <= STICK_DEADBAND && elevatorValue >= -STICK_DEADBAND) {
          // no elevator input -> recalculate the default pitch angle.
          // the pitch correction rate determines how fast level flight (0 degrees)
          // will be reached.
          // the pitch angle value determines the target angle of attack (in degrees).
          if (defaultPitchAngle > 0) {
            defaultPitchAngle = Math.max(defaultPitchAngle - (pitchCorrectionRate * timeElapsed), 0);
          }
          else {
            defaultPitchAngle = Math.min(defaultPitchAngle + (pitchCorrectionRate * timeElapsed), 0);
          }
          // the PID processor settings could have been changed externally
          pidPitch.pGain = pitchGainP.getValue();
          pidPitch.iGain = pitchGainI.getValue();
          pidPitch.dGain = pitchGainD.getValue();
          pidPitch.iMin = pitchMinI.getValue();
          pidPitch.iMax = pitchMaxI.getValue();
          // the PID processor calculates the pitch correction value according to
          // the difference between the default and the current pitch angle
          double pitchError = pitchAngle.getValue() - defaultPitchAngle - pitchAnglePreset.getValue() - pitchTrim.getValue();
          double pitchCorrection = Stabilization.updatePID(pidPitch, pitchError);
          // clip the correction angle before it is converted to a servo value
          pitchCorrection = Math.max(pitchCorrection, -MAX_ATTITUDE_ANGLE);
          pitchCorrection = Math.min(pitchCorrection, MAX_ATTITUDE_ANGLE);
          // convert the correction angle (degrees) to an elevator output value
          elevatorValue = pitchCorrection / MAX_ATTITUDE_ANGLE;
        }
        else {
          defaultPitchAngle = pitchAngle.getValue();
        }
      }
      // the elevator value is sent to the elevator input listeners
      elevatorOutput.setValue(elevatorValue);

      timer = new Date();
    
      try { sleep((long )(1000 / DEFAULT_UPDATE_FREQUENCY)); } catch(InterruptedException e) {} 
    }
  }
  
  /**
   * Starts the motion controller.
   */
  public void startStabilizing() {
    stabilizationMode = DO_STABILIZE;
  }
  
  /**
   * Stops the motion controller. 
   */
  public void stopStabilizing() {
    stabilizationMode = DO_IDLE;
  }
}
