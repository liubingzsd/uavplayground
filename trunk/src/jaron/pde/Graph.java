package jaron.pde;

import jaron.components.Signal;
import jaron.gui.Colors;
import jaron.gui.Panel;

import java.util.Date;
import java.util.TreeMap;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * The <code>Graph</code> class provides a graphical component for the Processing
 * Development Environment (PDE). It contains n graphs that are added and
 * referenced via a unique key.<br>
 * Reversing the graph's is done by swapping the high and the low signal values of
 * the corresponding graph's signal.<br>
 * The graphs usually get their current amplitudes via the <code>EventListener</code>
 * mechanism as demonstrated in the example of the {@link jaron.uavsim.UAVsim}.
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class Graph extends Panel {
  private PApplet applet;
  private TreeMap<String, GraphData> graphs =  new TreeMap<String, GraphData>();
  private long timer = 0;
  private int updateFrequency = 20;

  /**
   * Creates a new <code>Graph</code> object for the Processing Development Environment
   * (PDE) at a certain position and with a certain width and height.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   * @param height    the component's height including the labels
   * @param width     the component's width including the labels
   */
  public Graph(PApplet applet, int left, int top, int width, int height) {
    super(left, top, width, height);
    this.applet = applet;
  }

  /**
   * Creates a new <code>Graph</code> object for the Processing Development Environment
   * (PDE) at a certain position.
   * 
   * @param applet    a reference to the PDE applet that provides the drawing environment
   * @param left      the component's position from the left
   * @param top       the component's position from top
   */
  public Graph(PApplet applet, int left, int top) {
    this(applet, left, top, 150, 150);
  }
  
  /**
   * Adds a graph that will be drawn periodically.
   * 
   * @param label   a short description and unique identifier of the graph
   * @param color   the color of the graph
   * @see Colors
   */
  public void addGraph(String label, int color) {
    GraphData graph = new GraphData();
    graph.color = color;
    graph.label = label;
    graph.signal = new Signal();
    graph.data = new double[(int )content.getWidth()];
    graphs.put(label, graph);

    setLabelBottomHeight((graphs.size() * Fonts.LINE_HEIGHT) + Fonts.LINE_SPACING);
  }
  
  /**
   * Adds a graph that will be drawn periodically and returns its signal.
   * 
   * @param label   a short description and unique identifier of the graph
   * @param color   the color of the graph
   * @return        the graph's signal
   * @see Colors
   * @see Signal
   */
  public Signal createGraph(String label, int color) {
    GraphData graph = new GraphData();
    graph.color = color;
    graph.label = label;
    graph.signal = new Signal();
    graph.data = new double[(int )content.getWidth()];
    graphs.put(label, graph);

    setLabelBottomHeight((graphs.size() * Fonts.LINE_HEIGHT) + Fonts.LINE_SPACING);
    
    return graph.signal;
  }
  
  /**
   * Draws the whole graph component, containing all its graphs and the labels,to the
   * screen. This method should usually be called from the <code>draw</code>
   * method of the Processing Development Environment. This ensures that the graph is
   * updated periodically.
   */
  public void draw() {
    // update the data before its drawn
    updateData();

    // initialize the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);

    // draw the frame
    applet.rect(frame.getLeft(), frame.getTop(), frame.getWidth(), frame.getHeight());

    // draw the bottom label's frame
    applet.pushMatrix();
    applet.translate(labelBottom.getLeft(), labelBottom.getTop());
    applet.rect(0, 0, labelBottom.getWidth(), labelBottom.getHeight());
    applet.popMatrix();

    // prepare for the 'clipping' (drawing  into an offline graphics port)
    PGraphics pg = applet.createGraphics(content.getWidth(), content.getHeight(), PConstants.JAVA2D);
    pg.beginDraw();
    // draw the graphs
    pg.noFill();
    pg.pushMatrix();
    pg.translate(0, content.getHeight() / 2);
    // iterate through all the graphs
    for (GraphData graph : graphs.values()) {
      pg.beginShape();
      pg.stroke(graph.color);
      for (int i=0; i<graph.data.length; ++ i) {
        pg.curveVertex(i, (float )graph.data[i]);
      }
      pg.endShape();
    }
    pg.popMatrix();
    // do the 'clipping' (copy the offline graphics port into the applet's graphics port) 
    pg.endDraw();
    applet.image(pg, content.getLeft(), content.getTop());
    
    // draw all the graph's labels
    applet.pushMatrix();
    applet.translate(labelBottom.getLeft(), labelBottom.getTop());
    applet.textFont(Fonts.getFontBold(applet), Fonts.FONT_SIZE);
    applet.textAlign(PApplet.LEFT);
    for (GraphData graph : graphs.values()) {
      applet.fill(graph.color);
      applet.text(graph.label, Fonts.LINE_SPACING, Fonts.LINE_HEIGHT);
      applet.translate(0, Fonts.LINE_HEIGHT);
    }
    applet.popMatrix();

    // restore the graphics environment
    applet.fill(Colors.GRAY_BACKGROUND);
    applet.stroke(Colors.STROKE);
    applet.strokeWeight(1);
  }
  
  /**
   * Returns the <code>Signal</code> of a certain graph. The graph is identified
   * by its label (unique key).
   * 
   * @param label   the label identifying the graph
   * @return        the graphs signal
   */
  public Signal getSignal(String label) {
    return graphs.get(label).signal;
  }
  
  /**
   * Sets the frequency on which the graphs should be updated. By default
   * the frequency is set to 20Hz.
   * 
   * @param frequency the new update frequency
   */
  public void setUpdateFrequency(int frequency) {
    updateFrequency = frequency;
  }
  
  /*
   * Internally used to update the graphs data.
   */
  private void updateData() {
    Date currentTime = new Date();

    // ensure that the graph's data is updated based on a certain frequency
    if (currentTime.getTime() - timer >= (1000 / updateFrequency)) {
      // iterate through all the graphs
      for (GraphData graph : graphs.values()) {
        // shift the graph's data to the left and update the current value
        for (int i=graph.data.length-1; i>0; -- i) {
          graph.data[i] = graph.data[i-1];
        }
        graph.data[0] = (float )(-((content.getHeight()-2) / 2) * graph.signal.getValue() / (graph.signal.getBandwidth() / 2));
      }
      // reset the timer
      timer = currentTime.getTime();
    }
  }

  /*
   * The GraphData class is an internally used container.
   */
  private class GraphData {
    private int color;
    private String label;
    private Signal signal;
    private double[] data;

  }
}
