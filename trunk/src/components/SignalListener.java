package jaron.components;

import java.util.EventListener;

/**
 * The <code>SignalListener</code> interface forces its implementing classes
 * to implement all the methods that are used by the <code>EventListener</code>
 * mechanism.
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.0
 */
public interface SignalListener extends EventListener  {
  /**
   * Notifies the listener about a signal change event that occurred in the
   * <code>EventListener</code> mechanism.
   * 
   * @param event     the event that occurred
   */
  public void signalChanged(SignalEvent event);

  /**
   * Sets the signal's value. This method is used by an event notifier that
   * notifies all its listeners about a change of the signal value.<br>
   * The method can also be used outside the <code>EventListener</code>
   * mechanism to set the signal's value.
   * 
   * @param value     the notifier's signal value that has changed or just the new signal value
   */
  public void setValue(double value);
}
