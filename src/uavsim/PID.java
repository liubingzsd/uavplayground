package jaron.uavsim;

/**
 * The <code>PID</code> class implements a Proportional-Integral-Differential (PID)
 * algorithm.<br>
 * Credit goes to Tim Wescott.
 * 
 * @author      jarontec gmail com
 * @version     1.0
 * @since       1.0
 */
public class PID {

  /**
   * Updates the PID controller.
   * 
   * @param pid       input/output data
   * @param error     difference between the target position and the current position
   * @param position  target position
   * @return          command
   */
  public double updatePID(SPid pid, double error, double position) {
    double pTerm, dTerm, iTerm;
    // calculate the proportional term
    pTerm = pid.pGain * error;   
    // calculate the integral state with appropriate limiting
    pid.iState += error;
    if (pid.iState > pid.iMax) pid.iState = pid.iMax;
    else if (pid.iState < pid.iMin) pid.iState = pid.iMin;
    iTerm = pid.iGain * pid.iState;  // calculate the integral term
    dTerm = pid.dGain * (position - pid.dState);
    pid.dState = position;
    return pTerm + iTerm - dTerm;
  }

  /**
   * The <code>SPid</code> class provides the data for the PID controller.
   */
  public class SPid {
    double dState;        // Last position input
    double iState;        // Integrator state
    double iMax, iMin;    
    // Maximum and minimum allowable integrator state
    double iGain;         // integral gain
    double pGain;         // integral gain
    double dGain;         // integral gain
  }
}
