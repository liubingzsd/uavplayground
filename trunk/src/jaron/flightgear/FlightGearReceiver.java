package jaron.flightgear;

import jaron.components.Signal;
import jaron.components.SignalListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The <code>FlightGearReceiver</code> class provides a TCP/IP connection to the
 * FlightGear flight simulator (FG) via the FG generic output interface.<br>
 * <code>FlightGearReceiver</code> runs as a thread and by instantiating it
 * waits for the incoming connection of FG (at port {@value DEFAULT_PORT} by default).<br>
 * The instantiating has to be done before FG starts up, else FG reports a
 * connection error and terminates.<br>
 * At the moment the <code>FlightGearReceiver</code> is limited to receiving
 * numeric data.<br>
 * Be aware that running FG and a Java application that uses the
 * <code>FlightGearReceiver</code> needs a computer with sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent by FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 * <br>
 *  
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class FlightGearReceiver extends Thread {
  /**
   * The default update frequency of the <code>FlightGearReceiver</code>
   * is {@value DEFAULT_UPDATE_FREQUENCY}Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 10;
  
  /**
   * The default port that is used for the incoming connection. At the moment
   * this is port 5555 but this could change in the future.
   */
  public static final int DEFAULT_PORT = 5555;

  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kElevator = "controls-flight-elevator";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kAileron = "controls-flight-aileron";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kRudder = "controls-flight-rudder";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kThrottle = "controls-engines-engine-throttle";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kPitch = "orientation-pitch-deg";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kRoll = "orientation-roll-deg";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kPitchRate = "orientation-pitch-rate-degps";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kRollRate = "orientation-roll-rate-degps";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kYawRate = "orientation-yaw-rate-degps";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kAirSpeed = "velocities-airspeed-kt";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kVerticalSpeed = "velocities-vertical-speed-fps";
  /**
   * A string constant that is used to reference a FG XML ouput value.
   */
  public static final String kAltitude = "position-altitude-ft";
  
  private FlightGearParser parser = new FlightGearXMLParser();
  private HashMap<String, Signal> signals = new HashMap<String, Signal>();
  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;
  private ServerSocket server = null;
  private Socket client = null;
  private Boolean debug = false;

  /**
   * Creates a new <code>FlightGearReceiver</code> and starts a <code>Thread</code>
   * that listens for incoming connections.
   * 
   * @param port  the port the receiver listens to
   */
  public FlightGearReceiver(int port) {
    // start the socket server who waits for the connecting client
    try {
      server = new ServerSocket(port); // from now on the server listens to incomming connections
    } catch (IOException e) {
      System.out.println("IOException in FlightGearReceiver(): " + e.getMessage());
    }
    setDaemon(true);
    start();
  }

  /**
   * Creates a new <code>FlightGearReceiver</code> and starts a <code>Thread</code>
   * that listens for incoming connections. It listens on the default port
   * <code>DEFAULT_PORT</code>.
   * 
   * @see FlightGearReceiver#DEFAULT_PORT
   */
  public FlightGearReceiver() {
    this(DEFAULT_PORT);
  }

  /**
   * Adds a listener to a FlightGear (FG) signal that is identified by a key. The
   * key is the designator that is used in the FG configuration file for a certain
   * output data.<br>
   * In case of a change of the signal's value, all the listeners are informed
   * through the <code>EventListener</code> mechanism.<br>
   * 
   * @param key         a string that identifies the FG ouput data
   * @param listener    the listener to be added
   * @see SignalListener
   */
  public void addSignalListener(String key, SignalListener listener) {
    Signal signal = signals.get(key);
    if (signal == null) signal = new Signal();
    signal.addSignalListener(listener);
    signals.put(key, signal);
  }
  
  /**
   * Disconnects the connection to the client.
   */
  private void disconnectClient() {
    try {
      if (client != null) {
        client.close();
        if (debug) System.out.println("FlightGearReceiver::disconnectClient(): Client is disconnected.");
      }
    } catch (IOException e) {
      System.out.println("IOException in FlightGearReceiver::disconnectClient(): " + e.getMessage());
    }
    finally {
      client = null;
    }
  }
  
  /**
   * Sets the update frequency for the <code>FlightGearReceiver</code>.
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
          if (debug) System.out.println("FlightGearReceiver::run(): Connection to client established, waiting for data");
          BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
          String s = is.readLine();
          // a hack to prevalidate the xml code (against timing issues)
          if(s.matches("<\\?xml.*</data>")) {
            if (parser.parse(s)) {
              // fire the signal update events
              for (String key : signals.keySet()) {
                Signal signal = signals.get(key);
                signal.setValue(parser.getDouble(key));
              }
              if (debug) System.out.println("FlightGearReceiver::run(): Data published");
            }
          }
          else {
            if (debug) System.out.println("FlightGearReceiver::run(): Data corrupted!");
          }

          if (debug) System.out.println("FlightGearReceiver::run(): Data received");
        }
        // if no client is connected then check if there is a client waiting for a connection
        else if (server != null) {
          if (debug) System.out.println("FlightGearReceiver::run(): Waiting for client");
          client = server.accept(); // waits until a connection is established
        }
      } catch (IOException e) {
          System.out.println("IOException in FlightGearReceiver::run(): " + e.getMessage());
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
    if (debug) System.out.println("FlightGearReceiver::shutDown(): Receiver is shut down.");
  }
}