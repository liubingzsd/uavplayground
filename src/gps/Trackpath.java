package jaron.gps;

import java.util.ArrayList;

/**
 * The <code>Trackpath</code> class represents a GPS track that consist
 * of an unlimited number of track points.
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.1
 */
public class Trackpath {
  private ArrayList<Trackpoint> trackpoints = new ArrayList<Trackpoint>();
  private String name = "UAV Playground";
  private String duration = "00:00:00";
  private double distance = 0;
  private double vMax = 0;
  private double vAverage = 0;
  private int satMin = 0;
  private int satMax = 0;
  
  /**
   * Adds a trackpoint to the track path.
   * 
   * @param trackpoint  the track point to be added
   */
  public void addTrackpoint(Trackpoint trackpoint) {
    int n = trackpoints.size();

    if (n > 0) {
      Trackpoint first = trackpoints.get(0);

      // calculate the overall track duration
      double diff = trackpoint.getTimestamp().getTime() - first.getTimestamp().getTime();
      int hours = (int )(diff / (1000 * 60 * 60));
      int minutes = (int )(diff % (1000 * 60 * 60)) / (1000 * 60);
      int seconds = (int )((diff % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
      duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
      trackpoint.setDuration(duration);
      
      // calculate the track overall distance
      distance = GPSUtil.getDistance(first, trackpoint);
      
      // calculate avrage and maximum speed
      vAverage = (vAverage + trackpoint.getGroundSpeed()) / 2;
      vMax = Math.max(vMax, trackpoint.getGroundSpeed());
      
      // calculate the satellite receiving
      satMax = Math.max(satMax, trackpoint.getSatellites());
    }
    else {
      vAverage = vMax = trackpoint.getGroundSpeed();
      satMin = satMax = trackpoint.getSatellites();
    }
    trackpoint.setDistance(distance);
    trackpoints.add(trackpoint);
  }

  /**
   * Returns the overall track duration.
   * 
   * @return  duration of the track
   */
  public String getDuration() {
    return duration;
  }

  /**
   * Returns the overall distance of the track.
   * 
   * @return track distance
   */
  public double getDistance() {
    return distance;
  }

  /**
   * Returns the maximum speed that was achieved during the track.
   * 
   * @return maximum track speed
   */
  public double getVMax() {
    return vMax;
  }

  /**
   * Returns the average speed that was achieved during the track.
   * 
   * @return average track speed
   */
  public double getVAverage() {
    return vAverage;
  }

  /**
   * Returns the minimal count of satellites that where reachable during the
   * track.
   * 
   * @return minimal reachable satellites
   */
  public int getSatMin() {
    return satMin;
  }

  /**
   * Returns the maximum count of satellites that where reachable during the
   * track.
   * 
   * @return maximum reachable satellites
   */
  public int getSatMax() {
    return satMax;
  }

  /**
   * Returns the track name.
   * 
   * @return track name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Returns a list of all the track points of the track.
   * 
   * @return track list containing all the track points.
   */
  public ArrayList<Trackpoint> getTrackpoints() {
    return trackpoints;
  }
}
