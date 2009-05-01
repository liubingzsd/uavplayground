package jaron.gps;

import java.util.Date;

/**
 * The <code>Trackpoint</code> class represents a GPS track point.<br>
 * A list or array of track points represent a GPS track.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public class Trackpoint {
  private double altitude;
  private double groundSpeed;
	private Latitude latitude;
	private Longitude longitude;
  private Date timestamp;
	private int satellites;
	private double distance = 0;
	private double course = 0;
  private String duration = "00:00:00";

  /**
   * Sets the duration of the track until this track point.<br>
   * This value is normally calculated and set by a <code>Trackpath<code>
   * object. 
   * 
   * @param duration  the track duration
   */
  public void setDuration(String duration) {
    this.duration = duration;
  }
  
  /**
   * Returns the duration of the track until this track point.
   * 
   * @return  the track duration so far
   */
  public String getDuration() {
    return duration;
  }
  
  /**
   * Sets the distance of the track until this track point.<br>
   * This value is normally calculated and set by a <code>Trackpath<code>
   * object. 
   * 
   * @param distance  the track distance in kilometers
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }
  
  /**
   * Returns the distance of the track until this track point.
   * 
   * @return  the track distance in kilometers
   */
  public double getDistance() {
    return distance;
  }
  
  /**
   * Returns a <code>Latitude</code> object containig a latitude coordinate
   * of a geographical location.
   * 
   * @return  a latitude coordinate
   */
  public Latitude getLatitude() {
		return latitude;
	}
  
	/**
	 * Sets the geographical latitude coordinate of this track point.
	 * 
	 * @param latitude   the latitude coordinate
	 */
	public void setLatitude(Latitude latitude) {
		this.latitude = latitude;
	}
	
  /**
   * Returns a <code>Longitude</code> object containig a longitude coordinate
   * of a geographical location.
   * 
   * @return  a longitude coordinate
   */
	public Longitude getLongitude() {
		return longitude;
	}
	
  /**
   * Sets the geographical longitude coordinate of this track point.
   * 
   * @param longitude   the longitude coordinate
   */
	public void setLongitude(Longitude longitude) {
		this.longitude = longitude;
	}
	
  /**
   * Returns the current altitude at this track point.
   * 
   * @return  current altitude in meters
   */
  public double getAltitude() {
    return altitude;
  }
  
	/**
	 * Sets the current altitude of this track point.
	 * 
	 * @param altitude the new altitude in meters
	 */
	public void setAltitude(double altitude) {
	  this.altitude = altitude;
	}
	
	/**
	 * Returns the current time when of this track point.
	 * 
	 * @return the current time
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Sets the current time of this track point.
	 * 
	 * @param time the new time
	 */
	public void setTimestamp(Date time) {
		this.timestamp = time;
	}
	
	/**
	 * Returns the ground speed reached at this track point.
	 * 
	 * @return ground speed in kilometer per hour
	 */
	public double getGroundSpeed() {
		return groundSpeed;
	}
  
  /**
   * Sets the ground speed reached at this track point.
   * 
   * @param speed  ground speed in kilometers per hour
   */
  public void setGroundSpeed(double speed) {
    this.groundSpeed = speed;
  }
  
  /**
   * Sets the ground speed reached at this track point.
   * 
   * @param speed  ground speed in knots
   */
  public void setGroundSpeedKnots(double speed) {
    this.groundSpeed = speed * 1.852;
  }
	
  /**
   * Returns the number of satellites that where reachable at this track point.
   * 
   * @return  number of satellites
   */
  public int getSatellites() {
    return satellites;
  }
  
  /**
   * Sets the number of satellites that where reachable at this track point.
   * 
   * @param satellites  new number of satellites
   */
  public void setSatellites(int satellites) {
    this.satellites = satellites;
  }
  
  /**
   * Returns the course on which this trackpoint was reached. 
   * 
   * @return  course in degrees
   */
  public double getCourse() {
    return course;
  }

  /**
   * Sets the current course.
   * 
   * @param course in degrees
   */
  public void setCourse(double course) {
    this.course = course;
  }
}
