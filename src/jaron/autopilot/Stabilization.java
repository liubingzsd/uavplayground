package jaron.autopilot;

/**
 * The <code>Stabilization</code> class provides a PID controller algorithm.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class Stabilization {
  /**
   * Updates the PID controller according to the settings and the states that
   * are provided via the pid parameter.
   * 
   * @param pid       PID settings and current PID state
   * @param error     the difference between the target value and the current value
   * @return          the proposed correction value to reach the target value
   */
  public static double updatePID(PID pid, double error) {
    double pValue, dValue, iValue;

    // calculate the sum of the recent errors...
    pid.iState += error;
    //...but keep it within a certain range
    if (pid.iState > pid.iMax) pid.iState = pid.iMax;
    else if (pid.iState < pid.iMin) pid.iState = pid.iMin;

    // the proportional value determines the reaction to the current error
    // -> linear correction (independent of deviation or time)
    //    -> amplified (gain > 1) or reduced reaction (gain < 1)
    pValue = pid.pGain * error;   

    // the integral value determines the reaction based on the sum of recent errors
    // -> non linear correction  (depending on deviation over time)
    //    -> slow reaction for recent errors near the target position
    //    -> fast reaction for past errors far from the target position
    iValue = pid.iGain * pid.iState;
    
    // the derivative value determines the reaction based on the rate at which
    // the error has been changing
    // -> correction depending on deviation since last call
    //    -> slows down the correction near target position
    dValue = pid.dGain * (error - pid.dState);
    pid.dState = error;
    
    return pValue + iValue + dValue;
  }

  /**
   * The <code>PID</code> class provides the PID gains and holds the current
   * state of the PID controller.
   */
  public static class PID {
    public double pGain;      // proportional gain
    public double iGain;      // integral gain
    public double dGain;      // derivative gain
    public double iMax, iMin; // maximum and minimum allowable integral state
    public double dState;     // last position input
    public double iState;     // integral state
  }
}
