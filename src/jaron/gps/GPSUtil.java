package jaron.gps;

/**
 * The <code>GPSUtil</code> class provides some utility functionality
 * for the GPS data handling. 
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public class GPSUtil {
  /**
   * Returns the distance between two locations.
   * 
   * @param lat1 the latitude coordinate of the first location
   * @param lon1 the longitude coordinate of the first location
   * @param lat2 the latitude coordinate of the second location
   * @param lon2 the longitude coordinate of the second location
   * @return  the calculated distance in kilometers
   */
  public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
    /*
    Source: http://www.movable-type.co.uk/scripts/latlong.html

    var R = 6371; // km
    var dLat = (lat2-lat1).toRad();
    var dLon = (lon2-lon1).toRad(); 
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(lat1.toRad()) * Math.cos(lat2.toRad()) * 
            Math.sin(dLon/2) * Math.sin(dLon/2); 
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
    var d = R * c;
     */
    int R = 6371;
    double dLat = Math.toRadians((lat2 - lat1));
    double dLon = Math.toRadians((lon2 - lon1)); 
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * 
    Math.sin(dLon / 2) * Math.sin(dLon / 2); 
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); 
    double d = R * c;
    return d;
  }

  /**
   * Returns the distance between two locations.
   * 
   * @param lat1 a <code>Latitude</code> object containing the latitude coordinate of the first location
   * @param lon1 a <code>Latitude</code> object containing the longitude coordinate of the first location
   * @param lat2 a <code>Latitude</code> object containing the latitude coordinate of the second location
   * @param lon2 a <code>Latitude</code> object containing the longitude coordinate of the second location
   * @return  the calculated distance in kilometers
   */
  public static double getDistance(Latitude lat1, Longitude lon1, Latitude lat2, Longitude lon2){
    return getDistance(lat1.getDecimal(), lon1.getDecimal(), lat2.getDecimal(), lon2.getDecimal());
  }
  
  /**
   * Returns the distance between two locations.
   * 
   * @param p1 a <code>Trackpoint</code> object containing the first location
   * @param p2 a <code>Trackpoint</code> object containing the second location
   * @return  the calculated distance in kilometers
   */
  public static double getDistance(Trackpoint p1, Trackpoint p2) {
    return getDistance(p1.getLatitude().getDecimal(), p1.getLongitude().getDecimal(), p2.getLatitude().getDecimal(), p2.getLongitude().getDecimal());
  }
}
