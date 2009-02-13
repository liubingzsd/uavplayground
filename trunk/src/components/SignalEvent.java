package jaron.components;

import java.util.EventObject;

/**
 * The <code>SignalEvent</code> class is the transportation vehicle for
 * a signal value that is sent from or sent to a <code>Signal</code> object
 * via the <code>EventListener</code> mechanism.
 * 
 * @author      jarontec gmail com
 * @version     1.0
 * @since       1.0
 */
public class SignalEvent extends EventObject  {
  private double value = 0; 

  /**
   * Creates a <code>SignalEvent</code> object that can be sent via the
   * <code>EventListener</code> mechanism.
   * 
   * @param source  the event notifier
   * @param value   the changed value
   */
  public SignalEvent(Object source, double value) { 
    super(source); 
    this.value = value;
  } 

  /**
   * Returns the event's value.
   * 
   * @return        the event's value
   */
  public double getValue() { 
    return value; 
  } 
}
