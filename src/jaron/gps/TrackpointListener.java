package jaron.gps;

import java.util.EventListener;

/**
 * The trackpoint event listening interface.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public interface TrackpointListener extends EventListener  {
  public void trackpointChanged(Trackpoint trackpoint);
}
