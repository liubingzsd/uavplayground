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
 * connect to FG (with port 5556 by default).<br>
 * Be aware that running FG and a Java application that uses the
 * <code>FlightGearSender</code> needs a computer with sufficient performance.
 * The consequence of a lack of performance is, that some of the data sent to FG
 * could be lost because of timing issues. In this case you could run FG and the
 * Java application on two different machines via a network connection.<br>
 * <br>
 * The following is a description of how you would setup FG 1.9 and a Java
 * application. It assumes that you've installed FlightGear in a directory that is referred
 * here as <code>%PROGRAMFILES%\FlightGear</code>.<br>
 * <br>
 * <strong>Create an FG configuration file</strong><br>
 * <br>
 * <code>%PROGRAMFILES%\FlightGear\data\Protocol\FlightGearSender-Protocol.xml</code><br>
 * <br>
 * <strong>Copy this XML code into the FG configuration file</strong>
 * <pre>
&lt;?xml version="1.0"?>
&lt;PropertyList&gt;
 &lt;generic&gt;
  &lt;input&gt;
   &lt;line_separator&gt;\n&lt;/line_separator&gt;
   &lt;var_separator&gt;tab&lt;/var_separator&gt;
   &lt;binary_mode&gt;false&lt;/binary_mode&gt;
   &lt;chunk&gt;
    &lt;name&gt;elevator&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/flight/elevator&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;aileron&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/flight/aileron&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;rudder&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/flight/rudder&lt;/node&gt;
   &lt;/chunk&gt;
   &lt;chunk&gt;
    &lt;name&gt;throttle&lt;/name&gt;
    &lt;type&gt;float&lt;/type&gt;
    &lt;node&gt;/controls/engines/engine/throttle&lt;/node&gt;
   &lt;/chunk&gt;
  &lt;/input&gt;
 &lt;/generic&gt;
&lt;/PropertyList&gt;
 *</pre>
 * <strong>Create a Java application and run it</strong>
 * <pre>
import jaron.flightgear.*;
import jaron.pde.*;

FlightGearSender sender;
Joystick stickRight;
Joystick stickLeft;

public void setup() {
  // Setup and start the FlightGear data sender
  sender = new FlightGearSender("127.0.0.1", 5556);
  
  // Setup the left stick (throttle and rudder)
  stickLeft = new Joystick(this, "Rudder", "Throttle", 10, 10);
  // Set bandwidth for throttle (FlightGear uses 0-1)
  stickLeft.setBandwidthY(0, 1);
  // The throttle has no spring
  stickLeft.setSpringY(false);

  // Setup the right stick (elevator and aileron)
  stickRight = new Joystick(this, "Aileron", "Elevator", 170, 10);

  // Connect the FlightGear sender to the sticks
  stickRight.addListenerX(sender.getAileron());
  stickRight.addListenerY(sender.getElevator());
  stickLeft.addListenerX(sender.getRudder());
  stickLeft.addListenerY(sender.getThrottle());
}

public void draw() {
  stickRight.draw();
  stickLeft.draw();
}

public void mouseDragged() {
  stickRight.mouseDragged(mouseX, mouseY);
  stickLeft.mouseDragged(mouseX, mouseY);
 }

public void mousePressed() {
  stickRight.mousePressed(mouseX, mouseY);
  stickLeft.mousePressed(mouseX, mouseY);
}

public void mouseReleased() {
  stickRight.mouseReleased(mouseX, mouseY);
  stickLeft.mouseReleased(mouseX, mouseY);
}

public void mouseMoved() {
  stickRight.mouseMoved(mouseX, mouseY);
  stickLeft.mouseMoved(mouseX, mouseY);
}

public void destroy() {
  sender.setDebug(true);
  sender.shutDown();
}</pre>
 * <strong>Start up FlightGear</strong> (replace the IP address 127.0.0.1 with the address of the machine FG runs on)<br>
 * <pre>
"%PROGRAMFILES%\FlightGear\bin\Win32\fgfs" ^
 "--fg-root=%PROGRAMFILES%\FlightGear\data" ^
 "--generic=socket,in,10,127.0.0.1,5556,tcp,FlightGearSender-Protocol" ^
 "--timeofday=noon"</pre>
 *  
 * @author      jarontec gmail com
 * @version     1.0
 * @since       1.0
 */
public class FlightGearSender extends Thread {
  /**
   * The default port that is used for the outgoing connection. At the moment
   * this is port 5556 but this cold change in the future.
   */
  public static final int kDefaultPort = 5556;

  /**
   * The default IP addres that is used for the outgoing connection. This is
   * the local address 127.0.0.1.
   */
  public static final String kDefaultIP = "127.0.0.1";

  private Socket server = null;
  private String targetAddress;
  private int targetPort;
  private int sendDelay = 300;
  private int connectionDelay = 2000;
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
              if (debug) System.out.println("FlightGearSender::Thread(): IOException: " + e.getMessage()); // suppress IOException message because it's thrown until the connection is established
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
   * port <code>kDefaultPort</code>.
   * 
   * @param ip      the IP address of the FlightGear application
   * @see FlightGearSender#kDefaultPort
   */
  public FlightGearSender(String ip) {
    this(ip, kDefaultPort);
  }

  /**
   * Creates a new <code>FlightGearSender</code> and starts a <code>Thread</code>
   * that tries to connect to FlightGear. It tries to connect to the default
   * IP adres <code>kDefaultIP</code> and the default port <code>kDefaultPort</code>.
   * 
   * @see FlightGearSender#kDefaultPort
   * @see FlightGearSender#kDefaultIP
   */
  public FlightGearSender() {
    this(kDefaultIP, kDefaultPort);
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
  
  /* (non-Javadoc)
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    while(true) {
      // send data continuesly as soon as the server is connected
      if (server != null && senderAlive) {
        try {
          OutputStream out = server.getOutputStream(); 
          PrintWriter printer = new PrintWriter(out, true);
          String data = String.format(new Locale( "en", "US" ), "%1.3f\t%1.3f\t%1.3f\t%1.3f\t", elevator.getValue(), aileron.getValue(), rudder.getValue(), throttle.getValue());
          printer.println(data);
        } catch (IOException e) {
          System.out.println("IOException in FlightGearSender::send(): " + e.getMessage());
        }
      }
      try { sleep(sendDelay); } catch(InterruptedException e) {} 
    }
  }

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
