package jaron.gui;

import jaron.components.Signal;

/**
 * The <code>Axis</code> class represents an axis that can be used in an actuator
 * (see {@link jaron.gui.ActuatorXY}).<br>
 * Its main purpose is the conversion of a signal value into a value that is used
 * in a coordinate system.
 * 
 * @author      jarontec gmail com
 * @version     1.0
 * @since       1.0
 */
public class Axis extends Signal {
  private Signal power;
  private int path;
  private int start;
  private int position;

  /**
   * Creates an new <code>Axis</code> object.
   * 
   * @param start the starting position
   * @param path the path (distance between start and end)
   */
  public Axis(int start, int path) {
    super();
    this.start = start;
    this.path = path;
    
    // the hook for switching the axis on and off
    power = new Signal();
    power.setBandwidth(0, 1);
    power.setValue(power.getHigh());  // default is on

    setBandwidth(-1, 1);
    setValue(0);
  }
  
  /**
   * Returns the path of the axis.
   * 
   * @return the path (distance between start and end)
   */
  public int getPath() {
    return path;
  }
  
  /**
   * Returns the current position of the axis' actuator.
   * 
   * @return the current position of the actuator
   */
  public int getPosition() {
    return position;
  }

  /**
   * Returns a reference to a <code>Signal</code> object containing a value that
   * represents the current status of the axis. The stauts is either on
   * (<code>getSignal</code> equals <code>getHigh</code>) or off
   * (<code>getSignal</code> equals <code>getLow</code>).<br>
   * In its off status the axis doesn't react on inputs.
   * 
   * @return      a <code>Signal</code> object containing the current axis status
   * @see         Signal
   */
  public Signal getPowerSignal() {
    return power;
  }
  
  /**
   * Returns the starting position of the axis.
   * 
   * @return  the starting position
   */
  public int getStart() {
    return start;
  }
  
  /**
   * Moves the axis to the specified location.
   * 
   * @param location the new starting position
   */
  public void setLocation(int location) {
    this.position = this.position + (location - this.start);
    this.start = location;
  }
  
  /**
   * Sets a new value for the axis' path.
   * 
   * @param path  a new value for the path (distance between start and end)
   */
  public void setPath(int path) {
    this.path = path;
    // update position
    setValue(getValue());
  }

  /**
   * Sets the position of the axis' actuator.
   * 
   * @param position  the actuator's new position
   */
  public void setPosition(int position) {
    //check if the aixs' state is ON
    if (power.getValue() == power.getHigh()) {
      // keep the position within the path
      this.position = Math.min(position, start + path);
      this.position = Math.max(this.position, start);
      // recalculate and set the value of the signal
      double amplitude = (position - start) * getBandwidth() / path;
      setValue((getLow() + amplitude));
    }
  }

  /**
   * Sets the starting position of the axis.
   * 
   * @param start   the new starting position
   */
  public void setStart(int start) {
    this.start = start;
  }
  
  /**
   * Sets the value of the axis' signal. If the axis' power is set to
   * off then the value won't be changed.
   * 
   * @param value   a new value of axis
   * @see           Signal
   */
  public void setValue(double value) {
    //check if the axis' state is ON
    if (power.getValue() == power.getHigh()) {
      // keep the value within the bandwidth
      double newValue = value;
      if (getHigh() > getLow()) {
        newValue = Math.min(value, getHigh());
        newValue = Math.max(newValue, getLow());
      }
      else {
        newValue = Math.max(value, getHigh());
        newValue = Math.min(newValue, getLow());
      }
      // recalculate the axis' position
      position = (int )(path * (newValue - getLow()) / getBandwidth()) + start;
      // set the new value
      super.setValue(newValue);
    }
  }
}
