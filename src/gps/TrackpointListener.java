package jaron.gps;

import java.util.EventListener;

public interface TrackpointListener extends EventListener  {
  public void trackpointChanged(Trackpoint trackpoint);
}
