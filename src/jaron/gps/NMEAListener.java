package jaron.gps;

import java.util.EventListener;
import java.util.HashMap;

/**
 * The NMEA event listening interface.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public interface NMEAListener extends EventListener {
  public void nmeaReceived(HashMap<String, String> nmea);
}
