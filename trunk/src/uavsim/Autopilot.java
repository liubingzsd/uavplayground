package jaron.uavsim;

import jaron.components.Signal;
import jaron.components.SignalListener;

/**
 * The <code>Autopilot</code> class in its current state provides a simple PID
 * controller that calculates the deflection of the elevator and aileron according
 * to the pitch and roll degree.<br>
 * The autopilot starts itself as a thread when instantiated and recalculates
 * its data at a frequency of 5Hz.<br>
 * By implementing the data as signals the autopilot can be a listener (e.g.
 * pitch and roll input from FlightGear) and a notifier (e.g. aileron and elevator
 * output to FlightGear).<br> 
 * <br>
 * Have a look at the example of the {@link jaron.uavsim.UAVsim} for the usage of the
 * <code>Autopilot</code>.
 * 
 * @author      jarontec gmail com
 * @version     1.0
 * @since       1.0
 */
public class Autopilot extends Thread {
  private int delay = 200; // 200ms = 5Hz (approx.) thread execution frequency
  private int maxDeflection = 60; // max. degrees rudder deflection on one side (used for elevator and aileron output)
  private PID pid = new PID();
  private PID.SPid pidElevator = pid.new SPid();
  private PID.SPid pidAileron = pid.new SPid();
  private Signal elevator;
  private Signal aileron;
  private Signal pitch;
  private Signal roll;
  private Signal power;
  private Signal pElevator;
  private Signal iElevator;
  private Signal dElevator;
  private Signal pAileron;
  private Signal iAileron;
  private Signal dAileron;
  private Boolean debug = false;
  
  /**
   * Creates a new <code>Autopilot</code> and starts a <code>Thread</code>
   * that recalculates the autopilots data frequently.
   */
  public Autopilot() {
    elevator = new Signal();
    aileron = new Signal();
    pitch = new Signal();
    roll = new Signal();

    power = new Signal();
    power.setBandwidth(0, 1);

    pElevator = new Signal();
    iElevator = new Signal();
    dElevator = new Signal();
    pAileron = new Signal();
    iAileron = new Signal();
    dAileron = new Signal();
    
    pidElevator.iMax = 1;
    pidElevator.iMin = -1;

    pidAileron.iMax = 1;
    pidAileron.iMin = -1;

    setDaemon(true);
    start();
  }
  
  /**
   * Adds a listener to the aileron signal event handling. In case of
   * a change of the autopilot's aileron value, its listeners are
   * informed through the <code>EventListener</code> mechanism.<br>
   * 
   * @param listener    the listener to be added
   * @see SignalListener
   */
  public void addAileronListener(SignalListener listener) {
    aileron.addSignalListener(listener);
  }

  /**
   * Adds a listener to the elevator signal event handling. In case of
   * a change of the autopilot's elevator value, its listeners are
   * informed through the <code>EventListener</code> mechanism.<br>
   * 
   * @param listener    the listener to be added
   * @see SignalListener
   */
  public void addElevatorListener(SignalListener listener) {
    elevator.addSignalListener(listener);
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the calculated deflection for the aileron.<br>
   * 
   * @return      the calculated aileron value
   * @see         Signal
   */
  public Signal getAileron() {
    return aileron;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the differential gain of the ailerons.<br>
   * 
   * @return      the differential gain of the ailerons
   * @see         Signal
   */
  public Signal getDAileron() {
    return dAileron;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the differential gain of the elevator.<br>
   * 
   * @return      the differential gain of the elevator
   * @see         Signal
   */
  public Signal getDElevator() {
    return dElevator;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the calculated deflection for the elevator.<br>
   * 
   * @return      the calculated elevator value
   * @see         Signal
   */
  public Signal getElevator() {
    return elevator;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the integral gain of the ailerons.<br>
   * 
   * @return      the integral gain of the ailerons
   * @see         Signal
   */
  public Signal getIAileron() {
    return iAileron;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the integral gain of the elevator.<br>
   * 
   * @return      the integral gain of the elevator
   * @see         Signal
   */
  public Signal getIElevator() {
    return iElevator;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current pitch in degrees.<br>
   * 
   * @return      the current pitch in degrees
   * @see         Signal
   */
  public Signal getPitch() {
    return pitch;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the proportional gain of the elevator.<br>
   * 
   * @return      the proportional gain of the elevator
   * @see         Signal
   */
  public Signal getPElevator() {
    return pElevator;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the proportional gain of the ailerons.<br>
   * 
   * @return      the proportional gain of the ailerons
   * @see         Signal
   */
  public Signal getPAileron() {
    return pAileron;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current status of the autopilot. The stauts is either on
   * (<code>getSignal</code> equals <code>getHigh</code>) or off
   * (<code>getSignal</code> equals <code>getLow</code>).<br> In its off status
   * the autopilot doesn't react on input actions.
   * 
   * @return      the current autopilot status
   * @see         Signal
   */
  public Signal getPower() {
    return power;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current roll in degrees.<br>
   * 
   * @return      the current roll in degrees
   * @see         Signal
   */
  public Signal getRoll() {
    return roll;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    while(true) {
      if (power.getValue() == power.getHigh()) {
        // the gains could have been changed by the user so let's get those values
        pidElevator.pGain = pElevator.getValue();
        pidElevator.iGain = iElevator.getValue();
        pidElevator.dGain = dElevator.getValue();
        pidAileron.pGain = pAileron.getValue();
        pidAileron.iGain = iAileron.getValue();
        pidAileron.dGain = dAileron.getValue();

        double pitchSignal = pitch.getValue();
        if (debug) System.out.println("Pitch = " + pitchSignal);
        // clip the incoming pitch signal
        pitchSignal = Math.max(pitchSignal, -maxDeflection);
        pitchSignal = Math.min(pitchSignal, maxDeflection);
        // convert the pitch signal (degrees) to an elevator signal
        pitchSignal = (pitch.getBandwidth() / 2) * pitchSignal / maxDeflection;
        double elevatorSignal = elevator.getValue();
        if (debug) System.out.println("Elevator (before PID) = " + elevatorSignal);
        // calculate and set the the new elevator signal
        elevatorSignal = pid.updatePID(pidElevator, -pitchSignal, pitchSignal);
        if (debug) System.out.println("Elevator (after PID) = " + elevatorSignal);
        elevator.setValue(elevatorSignal);

        double rollSignal = roll.getValue();
        if (debug) System.out.println("Roll = " + rollSignal);
        // clip the incoming roll signal
        rollSignal = Math.max(rollSignal, -maxDeflection);
        rollSignal = Math.min(rollSignal, maxDeflection);
        // convert the roll signal (degrees) to an aileron signal
        rollSignal = (roll.getBandwidth() / 2) * rollSignal / maxDeflection;
        double aileronSignal = aileron.getValue();
        if (debug) System.out.println("Aileron (before PID) = " + aileronSignal);
        // calculate and set the the new aileron signal
        aileronSignal = pid.updatePID(pidAileron, -rollSignal, rollSignal);
        if (debug) System.out.println("Aileron (after PID) = " + aileronSignal);
        aileron.setValue(-aileronSignal);
      }
      try { sleep(delay); } catch(InterruptedException e) {} 
    }
  }
}