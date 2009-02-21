package jaron.gps;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * The <code>Latitude</code> class represents a latitude coordinate
 * of a geographical location.<br>
 * <br>
 * Modified source code from: http://code.google.com/p/gpsparser/
 * 
 * @author      jarontec gmail com
 * @version     1.1
 * @since       1.1
 */
public class Latitude {
  private double degrees;
  private double minutes;
  private double seconds;
  private Direction direction;
  private double decimal;
  private static NumberFormat format = DecimalFormat.getInstance(Locale.US);
  static {
    format.setMaximumFractionDigits(6);
  }

  public Latitude(String value, Direction direction) {
    if (Direction.WEST.equals(direction) || Direction.EAST.equals(direction)) {
      throw  new IllegalArgumentException("Latitude can only be NORTH or SOUTH");
    }
    this.direction=direction;
    degrees = Double.parseDouble(value.substring(0, 2));
    minutes = Double.parseDouble(value.substring(2, 4));
    seconds = 60 * Double.parseDouble(value.substring(4, value.length()));

    int sign = Direction.NORTH.equals(direction) ? 1 : -1;
    decimal = (degrees + (minutes / 60) + (seconds / 3600))* sign;
    decimal = Double.parseDouble(format.format(decimal));
  }

  public Latitude(double decimalValue){
    decimal = decimalValue;
    if (decimalValue < 0) {
      degrees = Math.ceil(decimal);
      direction = Direction.SOUTH;
    }
    else{
      degrees = Math.floor(decimal);
      direction = Direction.NORTH;
    }
    double remMin = (decimalValue - degrees) * 60;
    if (remMin < -0) {
      remMin *= -1;
    }
    minutes=Math.floor(remMin);
    seconds=Math.round((remMin - minutes) * 60);
  }

  public  double getDecimal() {
    return decimal;
  }

  public void setDegrees(double degrees) {
    this.degrees = degrees;
  }
  
  public double getMinutes() {
    return minutes;
  }

  public void setMinutes(double minutes) {
    this.minutes = minutes;
  }

  public double getSeconds() {
    return seconds;
  }

  public void setSeconds(double seconds) {
    this.seconds = seconds;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public double getDegrees() {
    return degrees;
  }
}