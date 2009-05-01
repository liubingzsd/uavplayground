package jaron.flightgear;

import jaron.components.Signal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;


/**
 * The <code>FlightGearSender</code> class provides a TCP/IP connection to the
 * FlightGear flight simulator (FG) via the FG generic input interface.<br>
 * <code>FlightGearSender</code> runs as a thread an by instantiating it tries to
 * connect to FG (with port {@value DEFAULT_PORT} and ip address {@value DEFAULT_IP}
 * by default).<br>
 * Be aware that running FG and a Java application that uses the
 * <code>FlightGearSender</code> needs a computer with sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent to FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 *  
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class FlightGearSender extends Thread {
  /**
   * The default update frequency of the <code>FlightGearSender</code>
   * is {@value DEFAULT_UPDATE_FREQUENCY} Hz.
   */
  public static final float DEFAULT_UPDATE_FREQUENCY = 3;
  
  /**
   * The default port that is used for the outgoing connection. At the moment
   * this is port {@value DEFAULT_PORT} but this cold change in the future.
   */
  public static final int DEFAULT_PORT = 5556;

  /**
   * The default IP address that is used for the outgoing connection. This is
   * the local address {@value DEFAULT_IP}.
   */
  public static final String DEFAULT_IP = "127.0.0.1";

  private float updateFrequency = DEFAULT_UPDATE_FREQUENCY;
  private int connectionDelay = 2000;
  private Socket server = null;
  private String targetAddress;
  private int targetPort;
  private Boolean senderAlive = false;
  private Signal elevator;
  private Signal aileron;
  private Signal rudder;
  private Signal throttle;
  private Boolean debug = false;
  
  /**
   * Creates a new <code>FlightGearSender</code> and starts a <code>Thread</code>
   * that tries to connect to FlightGear.
   * 
   * @param ip      the IP address of the FlightGear application
   * @param port    the port of the FlightGear application
   */
  public FlightGearSender(String ip, int port) {
    this.targetAddress = ip;
    this.targetPort = port;

    elevator = new Signal();
    aileron = new Signal();
    rudder = new Signal();
    throttle = new Signal();

    // start polling the server
    new Thread() {
      @Override public void run() { 
        while (true) {
          if (server == null) {
            if (debug) System.out.println("FlightGearSender::Thread()::run(): Waiting for the server to connect");
            try {
              server = new Socket(targetAddress, targetPort);
              if (server != null) {
                senderAlive = true;
                if (debug) System.out.println("FlightGearSender::Thread()::run(): Server connected");
              }
            } catch (UnknownHostException e) {
              System.out.println("UnknownHostException in FlightGearSender::Thread(): " + e.getMessage());
            } catch (IOException e) {
              if (debug) System.out.println("IOException in FlightGearSender::Thread(): " + e.getMessage()); // suppress IOException message because it's thrown until the connection is established
            }
          }
          try { sleep(connectionDelay); } catch(InterruptedException e) {} 
        }
      } 
    }.start();

    // start sending data
    setDaemon(true);
    start();
  }

  /**
   * Creates a new <code>FlightGearSender</code> and starts a <code>Thread</code>
   * that tries to connect to FlightGear. It tries to connect to the default
   * port <code>DEFAULT_PORT</code>.
   * 
   * @param ip      the IP address of the FlightGear application
   * @see FlightGearSender#DEFAULT_PORT
   */
  public FlightGearSender(String ip) {
    this(ip, DEFAULT_PORT);
  }

  /**
   * Creates a new <code>FlightGearSender</code> and starts a <code>Thread</code>
   * that tries to connect to FlightGear. It tries to connect to the default
   * IP adres <code>DEFAULT_IP</code> and the default port <code>DEFAULT_PORT</code>.
   * 
   * @see FlightGearSender#DEFAULT_PORT
   * @see FlightGearSender#DEFAULT_IP
   */
  public FlightGearSender() {
    this(DEFAULT_IP, DEFAULT_PORT);
  }

  /**
   * Returns a <code>Signal</code> object that provides access to the aileron
   * data. Usually the signal is linked to another signal via the <code>EventListener</code>
   * mechanism. Like that the aileron data is for example sent from a joystick
   * to FlightGear.<br>
   * The signal value sent to FG ranges form -1 to + 1.
   * 
   * @return    the aileron signal
   */
  public Signal getAileron() {
    return aileron;
  }
  
  /**
   * Returns a <code>Signal</code> object that provides access to the elevator
   * data. Usually the signal is linked to another signal via the <code>EventListener</code>
   * mechanism. Like that the elevator data is for example sent from a joystick
   * to FlightGear.<br>
   * The signal value sent to FG ranges form -1 to + 1.
   * 
   * @return    the elevator signal
   */
  public Signal getElevator() {
    return elevator;
  }
  
  /**
   * Returns a <code>Signal</code> object that provides access to the rudder
   * data. Usually the signal is linked to another signal via the <code>EventListener</code>
   * mechanism. Like that the rudder data is for example sent from a joystick
   * to FlightGear.<br>
   * The signal value sent to FG ranges form -1 to + 1.
   * 
   * @return    the rudder signal
   */
  public Signal getRudder() {
    return rudder;
  }
  
  /**
   * Returns a <code>Signal</code> object that provides access to the throttle
   * data. Usually the signal is linked to another signal via the <code>EventListener</code>
   * mechanism. Like that the throttle data is for example sent from a joystick
   * to FlightGear.<br>
   * The signal value sent to FG ranges form 0 to + 1.
   * 
   * @return    the throttle signal
   */
  public Signal getThrottle() {
    return throttle;
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
   * Sets the update frequency for the <code>FlightGearSender</code>.
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
      // send data continuously as soon as the server is connected
      if (server != null && senderAlive) {
        try {
          OutputStream out = server.getOutputStream(); 
          PrintWriter printer = new PrintWriter(out, true);
          String data = String.format(Locale.US, "%1.3f\t%1.3f\t%1.3f\t%1.3f\t", elevator.getValue(), aileron.getValue(), rudder.getValue(), throttle.getValue());
          printer.println(data);
        } catch (IOException e) {
          System.out.println("IOException in FlightGearSender::send(): " + e.getMessage());
        }
      }
      try { sleep((long )(1000 / updateFrequency)); } catch(InterruptedException e) {} 
    }
  }

  /**
   * Shuts this sender down.
   */
  public void shutDown() {
    senderAlive = false;

    try {
      if (server != null) {
        server.close();
        if (debug) System.out.println("FlightGearSender::shutDown(): Sender is shut down.");
      }
    } catch (IOException e) {
      System.out.println("IOException in FlightGearSender::shutDown(): " + e.getMessage());
    }
    finally {
      server = null;
    }
  }
}
