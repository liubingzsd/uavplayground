package jaron.autopilot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import jaron.components.Signal;
import jaron.flightgear.FlightGearXMLParser;

/**
 * The <code>FlightGearMotionSensor</code> connects to the FlightGear flight
 * simulator and provides its motion data.
 * <code>FlightGearMotionSensor</code> runs as a thread and by instantiating it
 * waits for the incoming connection from FG (at port {@value DEFAULT_PORT} by
 * default).<br> The instantiating has to be done before FG starts up, or else
 * FG reports a connection error and terminates.<br>
 * Be aware that running FG and a Java application that uses the
 * <code>FlightGearMotionSensor</code> needs a computer with sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent by FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class FlightGearMotionSensor extends Thread {
  /**
   * The default update frequency of the <code>FlightGearGpsReceiver</code>
   * is set to {@value DEFAULT_UPDATE_FREQUENCY} Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 10;
  /**
   * The default port that is used for the incoming connection. At the moment
   * this is port {@value DEFAULT_PORT} but this could change in the future.
   */
  public static final int DEFAULT_PORT = 5555;
  private static final String AIR_SPEED = "velocities-airspeed-kt";
  private static final String VERTICAL_SPEED = "velocities-vertical-speed-fps";
  private static final String ANGLE_PITCH = "orientation-pitch-deg";
  private static final String ANGLE_ROLL = "orientation-roll-deg";
  private static final String ANGULAR_RATE_PITCH = "orientation-pitch-rate-degps";
  private static final String ANGULAR_RATE_ROLL = "orientation-roll-rate-degps";
  private static final String ANGULAR_RATE_YAW = "orientation-yaw-rate-degps";

  private FlightGearXMLParser parser = new FlightGearXMLParser();
  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;
  private ServerSocket server = null;
  private Socket client = null;
  private Signal pitchAngle = new Signal();
  private Signal rollAngle = new Signal();
  private Signal airSpeed = new Signal();
  private Signal verticalSpeed = new Signal();
  private Signal pitchAngularRate = new Signal();
  private Signal rollAngularRate = new Signal();
  private Signal yawAngularRate = new Signal();

  /**
   * Creates a new <code>FlightGearMotionSensor</code> and starts a <code>Thread</code>
   * that listens for incoming connections.
   * 
   * @param port  the port the receiver listens to
   */
  public FlightGearMotionSensor(int port) {
    try {
      server = new ServerSocket(port); // start socket server -> listen to incoming connections
    } catch (IOException e) {
      System.out.println("IOException in FlightGearMotionSensor(): " + e.getMessage());
    }
    setDaemon(true);
    start();
  }

  /**
   * Creates a new <code>FlightGearMotionSensor</code> and starts a <code>Thread</code>
   * that listens for incoming connections. It listens on the default port
   * <code>DEFAULT_PORT</code>.
   * 
   * @see FlightGearMotionSensor#DEFAULT_PORT
   */
  public FlightGearMotionSensor() {
    this(DEFAULT_PORT);
  }
  
  /**
   * Returns the pitch angle signal which is used for the event handling mechanism.
   * 
   * @return the pitch angle signal
   * 
   * @see Signal
   */
  public Signal getPitchAngle() {
    return pitchAngle;
  }

  /**
   * Returns the roll angle signal which is used for the event handling mechanism.
   * 
   * @return the roll angle signal
   * 
   * @see Signal
   */
  public Signal getRollAngle() {
    return rollAngle;
  }

  /**
   * Returns the airspeed signal which is used for the event handling mechanism.
   * 
   * @return the airspeed signal
   * 
   * @see Signal
   */
  public Signal getAirSpeed() {
    return airSpeed;
  }

  /**
   * Returns the vertical speed signal which is used for the event handling mechanism.
   * 
   * @return the vertical speed signal
   * 
   * @see Signal
   */
  public Signal getVerticalSpeed() {
    return verticalSpeed;
  }

  /**
   * Returns the angular pitch rate signal which is used for the event handling mechanism.
   * 
   * @return the angular pitch rate signal
   * 
   * @see Signal
   */
  public Signal getPitchAngularRate() {
    return pitchAngularRate;
  }

  /**
   * Returns the angular roll rate signal which is used for the event handling mechanism.
   * 
   * @return the angular roll rate signal
   * 
   * @see Signal
   */
  public Signal getRollAngularRate() {
    return rollAngularRate;
  }

  /**
   * Returns the angular yaw rate signal which is used for the event handling mechanism.
   * 
   * @return the angular yaw rate signal
   * 
   * @see Signal
   */
  public Signal getYawAngularRate() {
    return yawAngularRate;
  }

  /**
   * Sets the update frequency for the <code>FlightGearMotionSensor</code>.
   * 
   * @param updateFrequency the new frequency in Hz
   */
  public void setUpdateFrequency(float updateFrequency) {
    this.updateFrequency = updateFrequency;
  }

  @Override
  public void run() {
    while(true) {
      try {
        // check if there is a client connected to our server
        if (client != null) {
          BufferedReader is = new BufferedReader(new InputStreamReader(client.getInputStream()));
          String s = is.readLine();
          // a hack to prevalidate the xml code (against timing issues)
          if(s.matches("<\\?xml.*</data>")) {
            if (parser.parse(s)) {
              // set the current motion values
              airSpeed.setValue(parser.getDouble(AIR_SPEED) * 1.852f);  // convert knots to km/h
              verticalSpeed.setValue(parser.getDouble(VERTICAL_SPEED) * 0.00508f); // convert ft/min to m/s
              pitchAngle.setValue(parser.getDouble(ANGLE_PITCH));
              rollAngle.setValue(parser.getDouble(ANGLE_ROLL));
              pitchAngularRate.setValue(parser.getDouble(ANGULAR_RATE_PITCH));
              rollAngularRate.setValue(parser.getDouble(ANGULAR_RATE_ROLL));
              yawAngularRate.setValue(parser.getDouble(ANGULAR_RATE_YAW));
            }
          }
        }
        // if no client is connected then check if there is a client waiting for a connection
        else if (server != null) {
          client = server.accept(); // waits until a connection is established
        }
      } catch (IOException e) {
          System.out.println("IOException in FlightGearMotionSensor::run(): " + e.getMessage());
          disconnectClient();
      }
      try { sleep((long )(1000 / updateFrequency)); } catch(InterruptedException e) {} 
    }
  }

  private void disconnectClient() {
    try {
      if (client != null) {
        client.close();
      }
    } catch (IOException e) {
      System.out.println("IOException in FlightGearMotionSensor::disconnectClient(): " + e.getMessage());
    }
    finally {
      client = null;
    }
  }

  /**
   * Disconnects the FlightGear client.
   */
  public void shutDown() {
    disconnectClient();
  }
}
