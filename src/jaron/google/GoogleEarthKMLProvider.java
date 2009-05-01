package jaron.google;

import jaron.gps.Trackpath;
import jaron.gps.Trackpoint;
import jaron.gps.TrackpointListener;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Locale;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * The <code>GoogleEarthKMLProvider</code> class provides a KLM structure
 * containing a GPS track and its information. The structure can be requested
 * by Google Earth via a HTTP connection.<br>
 * The track information that is contained in the KML structure is collected
 * from a GPS provider/device that supports the <code>TrackpointListener</code>
 * interface.<br>
 * By opening the following KML file, Google Earth starts polling the HTTP
 * server every 2 seconds and displays the real time tracking information.<br>
 * <pre>
&lt;?xml version="1.0" encoding="UTF-8"?>
&lt;kml xmlns="http://earth.google.com/kml/2.1"&gt;
  &lt;NetworkLink&gt;
    &lt;name&gt;UAV Playground Network Link&lt;/name&gt;
    &lt;Link&gt;
      &lt;href&gt;http://127.0.0.1:8080/&lt;/href&gt;
      &lt;refreshMode&gt;onInterval&lt;/refreshMode&gt;
      &lt;refreshInterval&gt;2&lt;/refreshInterval&gt;
    &lt;/Link&gt;
  &lt;/NetworkLink&gt;
&lt;/kml&gt;</pre>
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public class GoogleEarthKMLProvider implements TrackpointListener {
  /**
   * A preset value to be used with the <code>setTrackAltitudeMode</code> method.
   */
  public static final String ALTITUDE_MODE_ABSOLUTE = "absolute";

  /**
   * A preset value to be used with the <code>setTrackAltitudeMode</code> method.
   */
  public static final String CLAMP_TO_GROUND = "clampToGround";
  
  /**
   * A preset value to be used with the <code>setTrackAltitudeMode</code> method.
   */
  public static final String RELATIVE_TO_GROUND = "relativeToGround";
  
  /**
   * The default port {@value DEFAULT_PORT} that is used for the HTTP server.
   */
  public static final int DEFAULT_PORT = 8080;
  
  private Trackpath trackpath = new Trackpath();
  private Boolean writeTrack = true;
  private Boolean writePlacemaks = true;
  private String trackAltitudeMode = ALTITUDE_MODE_ABSOLUTE;
  private String placemarkAltitudeMode = ALTITUDE_MODE_ABSOLUTE;
  
  /**
   * Creates a new <code>GoogleEarthKMLProvider</code> and starts its HTTP
   * server through which the KML tracking data is provided.
   * 
   * @param port the port to be used for the HTTP connection
   */
  public GoogleEarthKMLProvider(int port) {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext( "/", new KMLCodeProvider() );
      server.start();
    } catch (IOException e) {
      System.out.println("IOException in GoogleEarthKMLProvider(): " + e.getMessage());
    }
  }
  
  /**
   * Creates a new <code>GoogleEarthKMLProvider</code> and starts its HTTP
   * server through which the KML tracking data is provided.
   */
  public GoogleEarthKMLProvider() {
    this(DEFAULT_PORT);
  }

  /**
   * The <code>KMLCodeProvider</code> class is internally used by the HTTP
   * server to handle a certain client request.
   */
  private class KMLCodeProvider implements HttpHandler  { 
    public void handle(HttpExchange httpExchange) throws IOException { 
      httpExchange.getResponseHeaders().add( "Content-type", "text/html" ); 
      String response = getKML(); 
      httpExchange.sendResponseHeaders(200, response.length()); 
      OutputStream os = httpExchange.getResponseBody(); 
      os.write(response.getBytes()); 
      os.close(); 
    } 
  }
  
  /**
   * Sets the track write flag. If set to true (default) the track information
   * will be included in the requested KML output.
   *  
   * @param flag  true if the track information should part of the KML output 
   */
  public void setWriteTrack(Boolean flag) {
    this.writeTrack = flag;
  }

  /**
   * Sets the placemaks write flag. If set to true (default) the plcaemaks
   * will be included in the requested KML output.
   *  
   * @param flag  true if the placemaks should part of the KML output 
   */
  public void setWritePlacemaks(Boolean flag) {
    this.writePlacemaks = flag;
  }

  /**
   * Sets the KML track altitude mode. For more information about the track
   * altitude mode read the KML reference.
   *  
   * @param trackAltitudeMode a KML &lt;altitudeMode&gt; value
   */
  public void setTrackAltitudeMode(String trackAltitudeMode) {
    this.trackAltitudeMode = trackAltitudeMode;
  }

  /**
   * Sets the KML placemarks altitude mode. For more information about the
   * placemarks altitude mode read the KML reference.
   *  
   * @param placemarkAltitudeMode a KML &lt;altitudeMode&gt; value
   */
  public void setPlacemarkAltitudeMode(String placemarkAltitudeMode) {
    this.placemarkAltitudeMode = placemarkAltitudeMode;
  }
  
  /**
   * Returns the KML track information. The track information is collected
   * from a GPS provider/device that supports the <code>TrackpointListener</code>
   * interface.<br>
   * 
   * @return  KML track information
   */
  public String getKML() {
    StringBuilder xml = new StringBuilder();
    // header
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");   
    xml.append("<kml xmlns=\"http://earth.google.com/kml/2.2\">\n");
    xml.append("<Document>\n");
    xml.append("<open>1</open>\n");
    // styles
    xml.append("<Style id=\"icon1\">\n");
    xml.append("<IconStyle>\n");
    xml.append("<scale>0.5</scale>\n");
    xml.append("</IconStyle>\n");
    xml.append("</Style>\n");
    xml.append("<Style id=\"color1\">\n");
    xml.append("<LineStyle>\n");
    xml.append("<color>ff0060ff</color>\n");
    xml.append("<colorMode>normal</colorMode>\n");
    xml.append("<width>4</width>\n");
    xml.append("</LineStyle>\n");
    xml.append("</Style>\n");
    // track name
    xml.append("<name>" + trackpath.getName() + "</name>\n");
    // track description
    xml.append("<Snippet>Click link for track information</Snippet>\n");
    xml.append("<description><![CDATA[<h3>Track&nbsp;statistics</h3>\n");
    xml.append("<table>\n");
    xml.append("<tr><td>Duration: </td><td>" + trackpath.getDuration() + "</td></tr>\n");
    xml.append("<tr><td>Distance: </td><td>" + String.format(Locale.US, "%.3f", trackpath.getDistance()) + "&nbsp;km</td></tr>\n");
    xml.append("<tr><td>Max.&nbsp;speed: </td><td>" + String.format(Locale.US, "%.2f", trackpath.getVMax()) + "&nbsp;km/h</td></tr>\n");
    xml.append("<tr><td>Av.&nbsp;speed: </td><td>" + String.format(Locale.US, "%.2f", trackpath.getVAverage()) + "&nbsp;km/h</td></tr>\n");
    xml.append("<tr><td>Max.&nbsp;satellites: </td><td>" + trackpath.getSatMax() + "</td></tr>\n");
    xml.append("<tr><td>Min.&nbsp;satellites: </td><td>" + trackpath.getSatMin() + "</td></tr>\n");
    xml.append("</table>\n");
    xml.append("]]></description>\n");
    // placemarks
    if (writePlacemaks) {
      xml.append("<Folder>\n");
      xml.append("<name>Trackpoints</name>\n");
      xml.append("<open>0</open>\n");
      for (Trackpoint t : trackpath.getTrackpoints()) {
        xml.append("<Placemark>\n");
        xml.append("<name>" + String.format(Locale.US, "%.1f", t.getAltitude()) + " m - " + String.format(Locale.US, "%.2f", t.getGroundSpeed()) + " km/h</name>\n");
        xml.append("<styleUrl>#icon1</styleUrl>\n");
        xml.append("<description><![CDATA[<h3>Track&nbsp;point</h3>\n");
        xml.append("<table>\n");
        xml.append("<tr><td>Duration: </td><td>" + t.getDuration() + "</td></tr>\n");
        xml.append("<tr><td>Distance: </td><td>" + String.format(Locale.US, "%.3f", t.getDistance()) + "&nbsp;km</td></tr>\n");
        xml.append("<tr><td>Speed: </td><td>" + String.format(Locale.US, "%.2f", t.getGroundSpeed()) + "&nbsp;km/h</td></tr>\n");
        xml.append("<tr><td>Altitude: </td><td>" + String.format(Locale.US, "%.1f", t.getAltitude()) + "&nbsp;m</td></tr>\n");
        xml.append("<tr><td>Satellites: </td><td>" + t.getSatellites() + "</td></tr>\n");
        xml.append("</table>\n");
        xml.append("]]></description>\n");
        xml.append("<Point>\n");
        xml.append("<altitudeMode>" + placemarkAltitudeMode + "</altitudeMode>\n");
        xml.append("<coordinates>" + t.getLongitude().getDecimal() + "," + t.getLatitude().getDecimal() + "," + t.getAltitude() + "</coordinates>\n");
        xml.append("</Point>\n");
        xml.append("</Placemark>\n");
      }
      xml.append("</Folder>\n");
    }
    // track
    if (writeTrack) {
      xml.append("<Folder>\n");
      xml.append("<name>Tracks</name>\n");
      xml.append("<open>0</open>\n");
      xml.append("<Placemark>\n");
      xml.append("<name>GPS Track</name>\n");
      xml.append("<styleUrl>#color1</styleUrl>\n");
      xml.append("<LineString>\n");
      xml.append("<extrude>0</extrude>\n");
      xml.append("<tessellate>1</tessellate>\n");
      xml.append("<altitudeMode>" + trackAltitudeMode + "</altitudeMode>\n");
      xml.append("<coordinates>\n");
      for (Trackpoint t : trackpath.getTrackpoints()) {
        xml.append(t.getLongitude().getDecimal() + "," + t.getLatitude().getDecimal() + "," + t.getAltitude() + "\n");
      }
      xml.append("</coordinates>\n");
      xml.append("</LineString>\n");
      xml.append("</Placemark>\n");
      xml.append("</Folder>\n");
    }
    // footer
    xml.append("</Document>\n");    
    xml.append("</kml>\n");

    return xml.toString();
  }
  
  /* (non-Javadoc)
   * @see jaron.gps.TrackpointListener#trackpointChanged(jaron.gps.Trackpoint)
   */
  public void trackpointChanged(Trackpoint trackpoint) {
    trackpath.addTrackpoint(trackpoint);
  }
}
