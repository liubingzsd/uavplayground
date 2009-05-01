package jaron.autopilot;

import jaron.gps.Waypoint;

/**
 * The <code>Navigation</code> class provides the functionality for navigating
 * a vehicle using navigation waypoints.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class Navigation {
  /**
   * Returns the distance between two coordinates.
   * 
   * @param lat1 the start latitude in decimal form
   * @param lon1 the start longitude in decimal form
   * @param lat2 the destination latitude in decimal form
   * @param lon2 the destination longitude in decimal form
   * @return the distance in meters
   */
  public static double getDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
    double l1 = Math.toRadians(lat1);
    double l2 = Math.toRadians(lat2);
    double dg = Math.toRadians(lon2 - lon1);

    return 1852 * 60 * Math.toDegrees(Math.acos(Math.sin(l1) * Math.sin(l2) +  Math.cos(l1) * Math.cos(l2) * Math.cos(dg)));
  }
  
  /**
   * Returns the course defined by two coordinates.
   * 
   * @param lat1 the start latitude in decimal form
   * @param lon1 the start longitude in decimal form
   * @param lat2 the destination latitude in decimal form
   * @param lon2 the destination longitude in decimal form
   * @return course in radians
   */
  public static double getCourseInRadians(double lat1, double lon1, double lat2, double lon2) {
    double l1 = Math.toRadians(lat1);
    double l2 = Math.toRadians(lat2);
    double dg = Math.toRadians(lon2 - lon1);

    double y = Math.sin(dg) * Math.cos(l2);
    double x = Math.cos(l1)* Math.sin(l2) - Math.sin(l1) * Math.cos(l2) * Math.cos(dg);
    
    return (Math.atan2(y, x) + (2 * Math.PI)) % (2 * Math.PI);
  }  
  
  /**
   * Returns the course defined by two coordinates.
   * 
   * @param lat1 the start latitude in decimal form
   * @param lon1 the start longitude in decimal form
   * @param lat2 the destination latitude in decimal form
   * @param lon2 the destination longitude in decimal form
   * @return course in degrees
   */
  public static double getCourseInDegrees(double lat1, double lon1, double lat2, double lon2) {
    return Math.toDegrees(getCourseInRadians(lat1, lon1, lat2, lon2));
  }
  
  /**
   * Returns the destination point given ba a start point the initial bearing
   * (deg) and the distance (m).
   * 
   * Great Circle Navigation: http://williams.best.vwh.net/avform.htm#LL
   * 
   * @param start start point as a waypoint coordinate
   * @param bearing bearing in degrees
   * @param distance distance in meters
   * @return the destination waypoint
   */
  public static Waypoint getDestinationPoint(Waypoint start, double bearing, double distance) {
    double R = 6372797.560856; // earth's mean radius in m
    double lat1 = Math.toRadians(start.getLatitude());
    double lon1 = Math.toRadians(start.getLongitude());
    double brng = Math.toRadians(bearing);

    double lat2 = Math.asin( Math.sin(lat1) * Math.cos(distance / R) + 
                  Math.cos(lat1) * Math.sin(distance / R) * Math.cos(brng));
    double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(distance / R) * 
                  Math.cos(lat1), Math.cos(distance / R) - Math.sin(lat1) * 
                  Math.sin(lat2));
    lon2 = (lon2 + Math.PI) % (2 * Math.PI) - Math.PI;  // normalize to -180...+180
    
    return new Waypoint(Math.toDegrees(lat2), Math.toDegrees(lon2));
  }
}
