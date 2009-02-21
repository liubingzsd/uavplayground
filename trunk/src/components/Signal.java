package jaron.components;

import javax.swing.event.EventListenerList;

/**
 * The <code>Signal</code> class contains a single double value (signal) and
 * implements an <code>EventListener</code> mechanism that allows its registered
 * listeners to be informed if the signal's value has changes.
 * By implementing the <code>SignalListener</code> interface it can also receive
 * signal change events from other signals.
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.0
 */
public class Signal implements SignalListener {
  private EventListenerList listeners = new EventListenerList();
  private double value;
  private double high;
  private double low;

  /**
   * Creates a <code>Signal</code> object that contains a single double
   * value and implements the <code>EventListener</code> mechanism. By
   * default the signal's low value is set to -1 and high value is 1.
   * 
   * @param value    a default value for the signal
   */
  public Signal(double value) {
    high = 1;
    low = -1;
    this.value = value;
  }

  /**
   * Creates a <code>Signal</code> object that contains a single double
   * value and implements the <code>EventListener</code> mechanism. By
   * default the signal's value is 0, its low value is set to -1 and the
   * high value is 1.
   */
  public Signal() {
    this(0);
  }
  
  /**
   * Adds the listener to the <code>EventListener</code> mechanism. So whenever
   * the value of the signal changes, the listener will be informed.
   * 
   * @param listener  the listener to be added
   */
  public void addSignalListener(SignalListener listener) {
    // from now on the listener listens to signal changes
    listeners.add(SignalListener.class, listener);
    // notify the new listener about the current value of the signal
    listener.setValue(getValue());
  }
  
  /**
  * Returns the signal's bandwidth which is the high value minus the low value.
  * By default the bandwidth is 2.
  * 
  * @return    a <code>double</code> representing the signal's current bandwidth
  */
 public double getBandwidth() {
   return high - low;
 }

  /**
   * Returns the high value of the signal's bandwidth. By default this value
   * is set to +1.
   * 
   * @return  the current high signal value
   */
  public double getHigh() {
    return high;
  }

  /**
   * Returns the low value of the signal's bandwidth. By default this value
   * is set to -1.
   * 
   * @return  the current low signal value
   */
  public double getLow() {
    return low;
  }

  /**
   * Returns the current value of the signal.
   * 
   * @return  the current signal value
   */
  public double getValue() {
    return value;
  }

  /**
   * Notifies all the listeners that added themselves to the <code>EventListener</code>
   * mechanism about a change in the signal's value.
   * 
   * @param event     an <code>SignalEvent</code> object containig the changed value of the signal
   */
  protected synchronized void notifySignalChange(SignalEvent event) {
    // all the listeners are getting informed about a signal change
    for (SignalListener l : listeners.getListeners(SignalListener.class)) 
      l.signalChanged(event); 
  } 

  /**
   * Sets the low and the high values of the signal. This is equal to calling
   * <code>setLow</code> and <code>setHigh</code>.
   * 
   * @param low   the low end of the bandwidth
   * @param high  the high end of the bandwidth
   */
  public void setBandwidth(double low, double high) {
    setLow(low);
    setHigh(high);
  }
  
  /**
   * Sets the high value of the signal's bandwidth.
   * 
   * @param value  the signal's new high value
   */
  public void setHigh(double value) {
    this.high = value;
  }

  /**
   * Sets the low value of the signal's bandwidth.
   * 
   * @param value  the signal's new low value
   */
  public void setLow(double value) {
    this.low = value;
  }

  /* (non-Javadoc)
   * @see jaron.uavsim.SignalListener#setValue(double)
   */
  public void setValue(double value) { 
    this.value = value;
    // notify all the listeners that are listening to this signal
    notifySignalChange(new SignalEvent(this, getValue()));
  }

  /* (non-Javadoc)
   * @see jaron.uavsim.SignalListener#signalChanged(jaron.uavsim.SignalEvent)
   */
  public void signalChanged(SignalEvent event) {
    setValue(event.getValue());
  }

  /**
   * Removes the listener from the <code>EventListener</code> mechanism. So in
   * the future the listener won't get informed about signal value changes anymore.
   * 
   * @param listener  the listener to be removed
   */
  public void removeSignalListener(SignalListener listener) { 
    // from now on the listener isn't informed anymore about signal changes
    listeners.remove(SignalListener.class, listener);
  } 
}
