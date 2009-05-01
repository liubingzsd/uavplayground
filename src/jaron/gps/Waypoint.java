package jaron.gps;

/**
 * The <code>Waypoint</code> class provides a container for a latitude and
 * an a longitude decimal value.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public class Waypoint {
  double latitude;
  double longitude;
  
  public Waypoint(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public double getLatitude() {
    return latitude;
  }
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }
  public double getLongitude() {
    return longitude;
  }
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
}
