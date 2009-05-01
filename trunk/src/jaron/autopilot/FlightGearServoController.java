package jaron.autopilot;

import jaron.components.Signal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * The <code>FlightGearServoController</code> class provides access to the
 * controls (e.g. aileron, elevator, rudder, throttle) of an airplane in the
 * FlightGear flight simulator.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.2
 */
public class FlightGearServoController extends Thread {
  /**
   * The default update frequency of the <code>FlightGearServoController</code>
   * is set to {@value DEFAULT_UPDATE_FREQUENCY} Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 10;
  /**
   * The default port that is used for the incoming connection. At the moment
   * this is port {@value DEFAULT_PORT} but this could change in the future.
   */
  public static final int DEFAULT_PORT = 5556;
  /**
   * The default IP address that is used for the outgoing connection. This is
   * the local address {@value DEFAULT_IP}.
   */
  public static final String DEFAULT_IP = "127.0.0.1";
  private static final int CONNECTION_DELAY = 2000;
  private static final int SERVO_DIRECTION_NORMAL = 1;
  private static final int SERVO_DIRECTION_REVERSE = -1;

  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;
  private Socket server = null;
  private String targetAddress;
  private int targetPort;
  private Boolean senderAlive = false;
  private Signal aileron = new Signal();
  private Signal elevator = new Signal();
  private Signal rudder = new Signal();
  private Signal throttle = new Signal();

  /**
   * Creates a new <code>FlightGearServoController</code> and starts a
   * <code>Thread</code> that tries to connect to FlightGear.
   * 
   * @param ip      the IP address of the FlightGear application
   * @param port    the port of the FlightGear application
   */
 public FlightGearServoController(String ip, int port) {
    this.targetAddress = ip;
    this.targetPort = port;
    
    // start polling the server
    new Thread() {
      @Override public void run() { 
        while (true) {
          if (server == null) {
            try {
              server = new Socket(targetAddress, targetPort);
              if (server != null) {
                senderAlive = true;
              }
            } catch (UnknownHostException e) {
              System.out.println("UnknownHostException in FlightGearServoController::Thread(): " + e.getMessage());
            } catch (IOException e) {
            }
          }
          try { sleep(CONNECTION_DELAY); } catch(InterruptedException e) {} 
        }
      } 
    }.start();

    // start sending data
    setDaemon(true);
    start();
  }
  
 /**
  * Creates a new <code>FlightGearServoController</code> and starts a
  * <code>Thread</code> that tries to connect to FlightGear. It tries to connect
  * to the default port 5556.
  * 
  * @param ip      the IP address of the FlightGear application
  */
  public FlightGearServoController(String ip) {
    this(ip, DEFAULT_PORT);
  }

  /**
   * Creates a new <code>FlightGearServoController</code> and starts a
   * <code>Thread</code> that tries to connect to FlightGear. It tries to
   * connect to the default IP address 127.0.0.1 and the default port
   * 5556.
   */
  public FlightGearServoController() {
    this(DEFAULT_IP, DEFAULT_PORT);
  }

  /**
   * Returns the aileron signal.
   * 
   * @return aileron signal
   */
  public Signal getAileron() {
    return aileron;
  }

  /**
   * Returns the elevator signal.
   * 
   * @return elevator signal
   */
  public Signal getElevator() {
    return elevator;
  }

  /**
   * Returns the rudder signal.
   * 
   * @return rudder signal
   */
  public Signal getRudder() {
    return rudder;
  }

  /**
   * Returns the aileron signal.
   * 
   * @return aileron signal
   */
  public Signal getThrottle() {
    return throttle;
  }

  /**
   * Sets the update frequency for the <code>FlightGearServoController</code>.
   * The default update frequency is set to 10 Hz.
   * 
   * @param updateFrequency the new frequency in Hz
   */
  public void setUpdateFrequency(float updateFrequency) {
    this.updateFrequency = updateFrequency;
  }

  @Override
  public void run() {
    while(true) {
      // send data continuously as soon as the server is connected
      if (server != null && senderAlive) {
        try {
          OutputStream out = server.getOutputStream(); 
          PrintWriter printer = new PrintWriter(out, true);
          String data = String.format(Locale.US, "%1.3f\t%1.3f\t%1.3f\t%1.3f\t",
              elevator.getValue() * SERVO_DIRECTION_NORMAL,
              aileron.getValue() * SERVO_DIRECTION_NORMAL,
              rudder.getValue() * SERVO_DIRECTION_NORMAL,
              throttle.getValue() * SERVO_DIRECTION_NORMAL);
          printer.println(data);
        } catch (IOException e) {
          System.out.println("IOException in FlightGearServoController::send(): " + e.getMessage());
        }
      }
      try { sleep((long )(1000 / updateFrequency)); } catch(InterruptedException e) {} 
    }
  }
  
  /**
   * Disconnects from FlightGear.
   */
  public void shutDown() {
    senderAlive = false;

    try {
      if (server != null) {
        server.close();
      }
    } catch (IOException e) {
      System.out.println("IOException in FlightGearServoController::shutDown(): " + e.getMessage());
    }
    finally {
      server = null;
    }
  }
}
