package jaron.components;

/**
 * The <code>TwoWaySwitch</code> class implements a switch with an input and
 * two output signals (channels).
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class TwoWaySwitch extends Signal {
  private Signal input;
  private Signal output1;
  private Signal output2;

  /**
   * Creates a <code>TwoWaySwitch</code> object. By default the switch's
   * output signal is <code>Output1</code>.
   */
  public TwoWaySwitch() {
    // these are the input/output channels
    input = new Signal();
    output1 = new Signal();
    output2 = new Signal();
    
    // default state is low
    setValue(getLow());
    
    // default output is output1
    input.addSignalListener(output1);
  }
  
  /**
   * Returns the input signal.
   * 
   * @return the switch's input signal
   */
  public Signal getInput() {
    return input;
  }

  /**
   * Returns the first output signal <code>Output1</code>.
   * 
   * @return the switch's first output signal
   */
  public Signal getOutput1() {
    return output1;
  }

  /**
   * Returns the second output signal <code>Output2</code>.
   * 
   * @return the switch's second output signal
   */
  public Signal getOutput2() {
    return output2;
  }

  /**
   * Toggles the switch's output form <code>Output1</code> to
   * <code>Output2</code> and vice versa. 
   */
  public void toggleSwitch() {
    if (getValue() == getLow()) {
      input.removeSignalListener(output1);
      input.addSignalListener(output2);
      setValue(getHigh());
    }
    else {
      input.removeSignalListener(output2);
      input.addSignalListener(output1);
      setValue(getLow());
    }
  }
}
