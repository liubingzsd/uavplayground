package jaron.gui;

import java.awt.Rectangle;

/**
 * The <code>Rect</code> class represents a rectangle in a coordinate system.<br>
 * It is defined by its upper left corner (top, left), a width and a height.
 * 
 * @author      jarontec gmail com
 * @version     1.0
 * @since       1.0
 */
public class Rect {
  private int left = 0;
  private int top = 0;
  private int width = 0;
  private int height = 0;
  
  /**
   * Creates an new <code>Rect</code> object.
   * 
   * @param left    the x coordinate
   * @param top     the y coordinate
   * @param width   the width
   * @param height  the height
   */
  public Rect(int left, int top, int width, int height) {
    this.left = left;
    this.top = top;
    this.width = width;
    this.height = height;
  }

  /**
   * Creates an new <code>Rect</code> object. Its coordinates, its width and
   * and height are set to 0.
   */
  public Rect() {
    this(0, 0, 0, 0);
  }

  /**
   * Checks whether or not this <code>Rect</code> contains the point at the
   * specified location (x,y).
   * 
   * @param x   the specified x coordinate
   * @param y   the specified y coordinate
   * @return    true if the point (x,y) is inside this rectangle; false otherwise
   */
  public Boolean contains(int x, int y) {
    Rectangle rect = new Rectangle(left, top, width, height);
    return rect.contains(x, y);
  }
  
  /**
   * Computes the intersection of this Rectangle with the specified
   * <code>Rect</code>.<br>
   * Returns a new <code>Rect</code> that represents the intersection of the two
   * rectangles. If the two rectangles do not intersect, the result will be an empty
   * rectangle.
   * 
   * @param rect    the specified <code>Rect</code>
   * @return        the largest <code>Rect</code> contained in both the specified
   *                <code>Rect</code> and in this <code>Rect</code>; or if the rectangles
   *                do not intersect, an empty rectangle.
   */
  public Rect createIntersection(Rect rect) {
    Rectangle r1 = new Rectangle(left, top, width, height);
    Rectangle r2 = (Rectangle )r1.createIntersection(new Rectangle(rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight()));

    return new Rect((int )r2.getX(), (int )r2.getY(), (int )r2.getWidth(), (int )r2.getHeight());
  }

  /**
   * Returns the height of the <code>Rect</code>.
   * 
   * @return  the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns the x coordinate of the <code>Rect</code>.
   * 
   * @return  the x coordinate
   */
  public int getLeft() {
    return left;
  }

  /**
   * Returns the y coordinate of the <code>Rect</code>.
   * 
   * @return  the y coordinate
   */
  public int getTop() {
    return top;
  }

  /**
   * Returns the width of the <code>Rect</code>.
   * 
   * @return  the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the height of the <code>Rect</code>.
   * 
   * @param height  the height
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Sets the x coordinate of the <code>Rect</code>.
   * 
   * @param left  the x coordinate
   */
  public void setLeft(int left) {
    this.left = left;
  }

  /**
   * Moves the Rect to the specified location.
   * 
   * @param left  the x coordinate of the new location
   * @param top   the y coordinate of the new location
   */
  public void setLocation(int left, int top) {
    this.left = left;
    this.top = top;
  }

  /**
   * Sets the width and height of the Rect.
   * 
   * @param width   the new width
   * @param height  the new height
   */
  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  /**
   * Sets the y coordinate of the <code>Rect</code>.
   * 
   * @param top  the y coordinate
   */
  public void setTop(int top) {
    this.top = top;
  }

  /**
   * Sets the width of the <code>Rect</code>.
   * 
   * @param width   the width
   */
  public void setWidth(int width) {
    this.width = width;
  }
}
