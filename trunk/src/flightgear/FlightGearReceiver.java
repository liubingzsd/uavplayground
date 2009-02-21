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
 * waits for the incoming connection of FG (at port 5555 by default).<br>
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
 * The following is a description of how you would setup FG 1.9 and a Java
 * application. It assumes that you've installed FlightGear in a directory that is referred
 * here as <code>%PROGRAMFILES%\FlightGear</code>.<br>
 * <br>
 * <strong>Create an FG configuration file</strong><br>
 * <br>
 * <code>%PROGRAMFILES%\FlightGear\data\Protocol\FlightGearReceiver-Protocol.xml</code><br>
 * <br>
 * <strong>Copy this XML code into the FG configuration file</strong>
 * <pre>
&lt;?xml version="1.0"?>
&lt;PropertyList&gt;
 &lt;generic&gt;
  &lt;output&gt;
   &lt;line_separator&gt;\n&lt;/line_separator&gt;
   &lt;var_separator&gt;&lt;/var_separator&gt;
   &lt;preamble&gt;&lt;/preamble&gt;
   &lt;postamble&gt;&lt;/postamble&gt;
   &lt;binary_mode&gt;false&lt;/binary_mode&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-flight-elevator&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;?xml version="1.0"?&amp;gt;&amp;lt;data&amp;gt;&amp;lt;controls-flight-elevator&amp;gt;%f&amp;lt;/controls-flight-elevator&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/flight/elevator&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-flight-aileron&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;controls-flight-aileron&amp;gt;%f&amp;lt;/controls-flight-aileron&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/flight/aileron&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-flight-rudder&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;controls-flight-rudder&amp;gt;%f&amp;lt;/controls-flight-rudder&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/flight/rudder&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;controls-engines-engine-throttle&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;controls-engines-engine-throttle&amp;gt;%f&amp;lt;/controls-engines-engine-throttle&amp;gt;&lt;/format&gt;
    &lt;node&gt;/controls/engines/engine/throttle&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-pitch-deg&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-pitch-deg&amp;gt;%f&amp;lt;/orientation-pitch-deg&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/pitch-deg&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-roll-deg&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-roll-deg&amp;gt;%f&amp;lt;/orientation-roll-deg&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/roll-deg&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-pitch-rate-degps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-pitch-rate-degps&amp;gt;%f&amp;lt;/orientation-pitch-rate-degps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/pitch-rate-degps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-roll-rate-degps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-roll-rate-degps&amp;gt;%f&amp;lt;/orientation-roll-rate-degps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/roll-rate-degps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-yaw-rate-degps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-yaw-rate-degps&amp;gt;%f&amp;lt;/orientation-yaw-rate-degps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/yaw-rate-degps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;orientation-heading-deg&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;orientation-heading-deg&amp;gt;%f&amp;lt;/orientation-heading-deg&amp;gt;&lt;/format&gt;
    &lt;node&gt;/orientation/heading-deg&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;velocities-airspeed-kt&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;velocities-airspeed-kt&amp;gt;%f&amp;lt;/velocities-airspeed-kt&amp;gt;&lt;/format&gt;
    &lt;node&gt;/velocities/airspeed-kt&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;velocities-vertical-speed-fps&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;velocities-vertical-speed-fps&amp;gt;%f&amp;lt;/velocities-vertical-speed-fps&amp;gt;&lt;/format&gt;
    &lt;node&gt;/velocities/vertical-speed-fps&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;position-altitude-ft&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;format&gt;&amp;lt;position-altitude-ft&amp;gt;%f&amp;lt;/position-altitude-ft&amp;gt;&amp;lt;/data&amp;gt;&lt;/format&gt;
    &lt;node&gt;/position/altitude-ft&lt;/node&gt;
   &lt;/chunk&gt;
  &lt;/output&gt;
 &lt;/generic&gt;
&lt;/PropertyList&gt;
 *</pre>
 * <strong>Create a Java application and run it</strong>
 * <pre>
import jaron.flightgear.*;
import jaron.pde.*;

Display display;
FlightGearReceiver receiver;

public void setup() {
  // Setup UAV Playground components
  receiver = new FlightGearReceiver(5555);
  display = new Display(this, 10, 10);
  // Connect the display to the FlightGear receiver
  receiver.addSignalListener(FlightGearReceiver.kRollRate, display.createTextLine("Roll rate"));
  receiver.addSignalListener(FlightGearReceiver.kPitchRate, display.createTextLine("Pitch rate"));
  receiver.addSignalListener(FlightGearReceiver.kYawRate, display.createTextLine("Yaw rate"));
  receiver.addSignalListener(FlightGearReceiver.kAirSpeed, display.createTextLine("Airspeed"));
  receiver.addSignalListener(FlightGearReceiver.kAltitude, display.createTextLine("Altitude"));
  receiver.addSignalListener(FlightGearReceiver.kVerticalSpeed, display.createTextLine("Vert. speed"));
  receiver.addSignalListener(FlightGearReceiver.kPitch, display.createTextLine("Pitch"));
  receiver.addSignalListener(FlightGearReceiver.kRoll, display.createTextLine("Roll"));
}

public void draw() {
  display.draw();
}

public void destroy() {
  receiver.setDebug(true);
  receiver.shutDown();
}</pre>
 * <strong>Start up FlightGear</strong> (replace the IP address 127.0.0.1 with the
 * address of the machine the Java application runs on)<br>
 * <pre>"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--generic=socket,out,3,127.0.0.1,5555,tcp,FlightGearReceiver-Protocol" ^
 "--timeofday=noon"</pre>
 *  
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.0
 */
public class FlightGearReceiver extends Thread {
  /**
   * The default port that is used for the incoming connection. At the moment
   * this is port 5555 but this could change in the future.
   */
  public static final int kDefaultPort = 5555;

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
  private int readDelay = 100;
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
   * <code>kDefaultPort</code>.
   * 
   * @see FlightGearReceiver#kDefaultPort
   */
  public FlightGearReceiver() {
    this(kDefaultPort);
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
      try { sleep(readDelay); } catch(InterruptedException e) {} 
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