package jaron.flightgear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import jaron.gps.Direction;
import jaron.gps.Latitude;
import jaron.gps.Longitude;
import jaron.gps.NMEAListener;
import jaron.gps.Trackpoint;
import jaron.gps.TrackpointListener;

/**
 * The <code>FlightGearNMEAReceiver</code> class provides a TCP/IP connection to the
 * FlightGear flight simulator (FG) via the FG NMEA output interface.<br>
 * <code>FlightGearNMEAReceiver</code> runs as a thread and by instantiating it
 * waits for the incoming connection from FG (at port {@value DEFAULT_PORT} by default).<br>
 * The instantiating has to be done before FG starts up, else FG reports a
 * connection error and terminates.<br>
 * Be aware that running FG and a Java application that uses the
 * <code>FlightGearNMEAReceiver</code> needs a computer with sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent by FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 * <br>
 * Have a look at the {@link jaron.uavsim.UAVsim}. example on how to use the
 * <code>FlightGearNMEAReceiver</code>
 *   
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.1
 */
public class FlightGearNMEAReceiver extends Thread {
  /**
   * The default update frequency of the <code>FlightGearNMEAReceiver</code>
   * is {@value DEFAULT_UPDATE_FREQUENCY}Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 1f;
  
  /**
   * The default port that is used for the incoming connection. At the moment
   * this is port 5557 but this could change in the future.
   */
  public static final int DEFAULT_PORT = 5557;
  
  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;
  private ServerSocket server = null;
  private Socket client = null;
  private Boolean debug = false;
  private EventListenerList trackpathListeners = new EventListenerList();
  private EventListenerList nmeaListeners = new EventListenerList();

  /**
   * Creates a new <code>FlightGearNMEAReceiver</code> and starts a <code>Thread</code>
   * that listens for incoming connections.
   * 
   * @param port  the port the receiver listens to
   */
  public FlightGearNMEAReceiver(int port) {
    // start the socket server who waits for the connecting client
    try {
      server = new ServerSocket(port); // from now on the server listens to incomming connections
    } catch (IOException e) {
      System.out.println("IOException in FlightGearNMEAReceiver(): IOException: " + e.getMessage());
    }
    setDaemon(true);
    start();
  }

  /**
   * Creates a new <code>FlightGearNMEAReceiver</code> and starts a <code>Thread</code>
   * that listens for incoming connections. It listens on the default port
   * <code>DEFAULT_PORT</code>.
   * 
   * @see FlightGearNMEAReceiver#DEFAULT_PORT
   */
  public FlightGearNMEAReceiver() {
    this(DEFAULT_PORT);
  }
  
  /**
   * Adds a listener to the <code>EventListener</code> mechanism. So whenever
   * a set of NMEA data is received, the listener will be informed about
   * that.
   * 
   * @param listener  the listener to be added
   */
  public void addNMEAListener(NMEAListener listener) {
    // from now on the listener listens to NMEA data changes
    nmeaListeners.add(NMEAListener.class, listener);
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
   * Disconnects the connection to the client.
   */
  private void disconnectClient() {
    try {
      if (client != null) {
        client.close();
        if (debug) System.out.println("FlightGearNMEAReceiver::disconnectClient(): Client is disconnected.");
      }
    } catch (IOException e) {
      System.out.println("IOException in FlightGearNMEAReceiver::disconnectClient(): " + e.getMessage());
    }
    finally {
      client = null;
    }
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
   * Notifies all the listeners that added themselves to the <code>EventListener</code>
   * mechanism about the new NMEA data that was received.
   * 
   * @param trackpoint  the new trackpoint
   */
  protected synchronized void notifyNMEAListeners(HashMap<String, String> nmea) {
    for (NMEAListener l : nmeaListeners.getListeners(NMEAListener.class)) 
      l.nmeaReceived(nmea);
  } 
  
  /**
   * Sets the update frequency for the <code>FlightGearNMEAReceiver</code>.
   * The default update frequency is set to {@value DEFAULT_UPDATE_FREQUENCY}Hz.
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
          if (debug) System.out.println("FlightGearNMEAReceiver::run(): Connection to client established, waiting for data");
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
            notifyTrackpointListeners(trackpoint);
            notifyNMEAListeners(nmea);
          }
          if (debug) System.out.println("FlightGearNMEAReceiver::run(): Data received");
        }
        // if no client is connected then check if there is a client waiting for a connection
        else if (server != null) {
          if (debug) System.out.println("FlightGearNMEAReceiver::run(): Waiting for client");
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
   * Sets the debugging flag which determines if the debugging informations should
   * be printed to the console. This is for debugging purpose only.
   * 
   * @param debug    set to <code>true</code> if additional debugging information
   *                 should be printed
   */
  public void setDebug(Boolean debug) {
    this.debug = debug;
  }

  /**
   * Shuts this receiver down and disconnects the client.
   */
  public void shutDown() {
    disconnectClient();
    if (debug) System.out.println("FlightGearNMEAReceiver::shutDown(): Receiver is shut down.");
  }
}