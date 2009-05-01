package jaron.autopilot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import jaron.components.Signal;
import jaron.gps.Direction;
import jaron.gps.Latitude;
import jaron.gps.Longitude;
import jaron.gps.Trackpoint;
import jaron.gps.TrackpointListener;

/**
 * The <code>FlightGearGpsReceiver</code> class provides a TCP/IP connection to the
 * FlightGear flight simulator (FG) via the FG NMEA output interface.<br>
 * <code>FlightGearGpsReceiver</code> runs as a thread and by instantiating it
 * waits for the incoming connection from FG (at port {@value DEFAULT_PORT} by
 * default).<br> The instantiating has to be done before FG starts up, or else
 * FG reports a connection error and terminates.<br>
 * Be aware that running FG and a Java application that uses the
 * <code>FlightGearGpsReceiver</code> needs a computer with sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent by FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 *   
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
// TODO High-pass filter the calculated course signal (aileron jitter)
public class FlightGearGpsReceiver extends Thread {
  /**
   * The default update frequency of the <code>FlightGearGpsReceiver</code>
   * is set to {@value DEFAULT_UPDATE_FREQUENCY} Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 5;
  
  /**
   * The default port that is used for the incoming connection. At the moment
   * this is port {@value DEFAULT_PORT} but this could change in the future.
   */
  public static final int DEFAULT_PORT = 5557;
  
  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;
  private ServerSocket server = null;
  private Socket client = null;
  private EventListenerList trackpathListeners = new EventListenerList();
  private Signal latitude = new Signal();
  private Signal longitude = new Signal();
  private Signal courseOverGround = new Signal();
  private Signal speedOverGround = new Signal();
  private Signal altitudeAbsolute = new Signal();
  private Signal satellites = new Signal();
  private double pastLatitude = 0;
  private double pastLongitude = 0;

  /**
   * Creates a new <code>FlightGearGpsReceiver</code> and starts a <code>Thread</code>
   * that listens for incoming connections.
   * 
   * @param port  the port the receiver listens to
   */
  public FlightGearGpsReceiver(int port) {
    // start the socket server who waits for the connecting client
    try {
      server = new ServerSocket(port); // from now on the server listens to incomming connections
    } catch (IOException e) {
      System.out.println("IOException in FlightGearGpsReceiver(): " + e.getMessage());
    }
    setDaemon(true);
    start();
  }

  /**
   * Creates a new <code>FlightGearGpsReceiver</code> and starts a <code>Thread</code>
   * that listens for incoming connections. It listens on the default port
   * <code>DEFAULT_PORT</code>.
   * 
   * @see FlightGearGpsReceiver#DEFAULT_PORT
   */
  public FlightGearGpsReceiver() {
    this(DEFAULT_PORT);
  }
  
  /**
   * Adds a listener to the <code>EventListener</code> mechanism. So whenever
   * a trackpoint is added to the trackpath, the listener will be informed about
   * that event.
   * 
   * @param listener  the listener to be added
   */
  public void addTrackpointListener(TrackpointListener listener) {
    // from now on the listener listens to trackpoint changes
    trackpathListeners.add(TrackpointListener.class, listener);
  }
  
  /**
   * Notifies all the listeners that added themselves to the <code>EventListener</code>
   * mechanism about the received NMEA data.
   * 
   * @param trackpoint  the new trackpoint
   */
  protected synchronized void notifyTrackpointListeners(Trackpoint trackpoint) {
    for (TrackpointListener l : trackpathListeners.getListeners(TrackpointListener.class)) 
      l.trackpointChanged(trackpoint);
  } 

  /**
   * Returns the latitude signal which is used for the event handling mechanism.
   * 
   * @return the latitude signal
   * 
   * @see Signal
   */
  public Signal getLatitude() {
    return latitude;
  }

  /**
   * Returns the longitude signal which is used for the event handling mechanism.
   * 
   * @return the longitude signal
   * 
   * @see Signal
   */
  public Signal getLongitude() {
    return longitude;
  }

  /**
   * Returns the course over ground signal which is used for the event handling
   * mechanism.
   * 
   * @return the course over ground signal
   * 
   * @see Signal
   */
  public Signal getCourseOverGround() {
    return courseOverGround;
  }

  /**
   * Returns the speed over ground signal which is used for the event handling
   * mechanism.
   * 
   * @return the speed over ground signal
   * 
   * @see Signal
   */
  public Signal getSpeedOverGround() {
    return speedOverGround;
  }

  /**
   * Returns the absolute altitude signal which is used for the event handling
   * mechanism.
   * 
   * @return the absolute altitude signal
   * 
   * @see Signal
   */
  public Signal getAltitudeAbsolute() {
    return altitudeAbsolute;
  }

  /**
   * Returns the satellite signal (number of satellites) which is used for the
   * event handling mechanism.
   * 
   * @return the number of satellite signal
   * 
   * @see Signal
   */
  public Signal getSatellites() {
    return satellites;
  }

  /**
   * Disconnects the connection to the client.
   */
  private void disconnectClient() {
    try {
      if (client != null) {
        client.close();
      }
    } catch (IOException e) {
      System.out.println("IOException in FlightGearGpsReceiver::disconnectClient(): " + e.getMessage());
    }
    finally {
      client = null;
    }
  }

  /**
   * Sets the update frequency for the <code>FlightGearGpsReceiver</code>.
   * The default update frequency is set to {@value DEFAULT_UPDATE_FREQUENCY} Hz.
   * 
   * @param updateFrequency the new frequency in Hz
   */
  public void setUpdateFrequency(float updateFrequency) {
    this.updateFrequency = updateFrequency;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    while(true) {
      try {
        // check if there is a client connected to our server
        if (client != null) {
          BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
          String s = new String();
          Trackpoint trackpoint = new Trackpoint();
          HashMap<String, String> nmea = new HashMap<String, String>();

          // FlightGear sends a stream of $GPRMC, $GPGGA and $GPGSA NMEA data (in that order)
          // this data is parsed and converted to a Trackpoint object
          // after that the Trackpoint object is sent to the EventListeners
          while ((s = is.readLine()) != null) {
            String[] params = s.split(",");
            if(params[0].equals("$GPRMC")) {
              nmea = new HashMap<String, String>();
              nmea.put("GPRMC", s);
              trackpoint.setLatitude(new Latitude(params[3], Direction.fromValue( params[4])));
              trackpoint.setLongitude(new Longitude(params[5], Direction.fromValue( params[6])));
              String time = params[1];
              String date = params[9];
              Calendar cal = GregorianCalendar.getInstance();
              cal.set(Calendar.HOUR, Integer.parseInt(time.substring(0, 2)));
              cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(2, 4)));
              cal.set(Calendar.SECOND, Integer.parseInt(time.substring(4, 6)));
              cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
              cal.set(Calendar.MONTH, Integer.parseInt(date.substring(2, 4)));
              cal.set(Calendar.YEAR, Integer.parseInt(date.substring(4, 6)));
              trackpoint.setTimestamp(cal.getTime());
              trackpoint.setGroundSpeedKnots(Double.parseDouble(params[7]));
            }
            else if(params[0].equals("$GPGGA")) {
              nmea.put("GPGGA", s);
              trackpoint.setSatellites(Integer.parseInt(params[7]));
              trackpoint.setAltitude(Double.parseDouble(params[9]));
            }
            // $GPGSA implies the end of the NMEA data sequence
            else if(params[0].equals("$GPGSA")) {
              nmea.put("GPGSA", s);
              break;
            }
          }
          // FlightGear sends junk data at initialization and this is filtered out here
          if (trackpoint.getLatitude().getDegrees() != 0) {
            latitude.setValue(trackpoint.getLatitude().getDecimal());
            longitude.setValue(trackpoint.getLongitude().getDecimal());
            if (pastLatitude != 0 && pastLongitude != 0) {
              courseOverGround.setValue(Navigation.getCourseInDegrees(pastLatitude, pastLongitude, latitude.getValue(), longitude.getValue()));
            }
            speedOverGround.setValue(trackpoint.getGroundSpeed());
            altitudeAbsolute.setValue(trackpoint.getAltitude());
            satellites.setValue(trackpoint.getSatellites());
            notifyTrackpointListeners(trackpoint);
            pastLatitude = latitude.getValue();
            pastLongitude = longitude.getValue();
          }
        }
        // if no client is connected then check if there is a client waiting for a connection
        else if (server != null) {
          client = server.accept(); // waits until a connection is established
        }
      } catch (IOException e) {
          System.out.println("IOException in FlightGearNMEAReceiver::run(): IOException: " + e.getMessage());
          disconnectClient();
      }
      try { sleep((long )(1000 / updateFrequency)); } catch(InterruptedException e) {} 
    }
  }

  /**
   * Disconnects the FlightGear client.
   */
  public void shutDown() {
    disconnectClient();
  }
}