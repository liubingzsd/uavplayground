package jaron.flightgear;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The <code>FlightGearXMLParser</code> is a parser for the data that is
 * sent from FlightGear (FG) via its generic output interface.<br>
 * Although the number of parameter may vary the principal structure of the
 * data provided by FG has to be of the following form:<br>
 * <pre>
 * &lt;?xml version="1.0" encoding="windows-1250"?&gt;
 * &lt;data&gt;
 *   &lt;controls-flight-elevator&gt;0.300000&lt;/controls-flight-elevator&gt;
 *   &lt;controls-flight-aileron&gt;-1.000000&lt;/controls-flight-aileron&gt;
 * &lt;/data&gt;
 *</pre>
 * After the parsing, the data may be accessed via <code>getDouble</code> and
 * <code>getString</code> and the keys defined in the XML structure (e.g.
 * <code>controls-flight-elevator</code>).
 * 
 * @author      jarontec gmail com
 * @version     1.2
 * @since       1.0
 */
public class FlightGearXMLParser extends FlightGearParser {
  private DocumentBuilder db = null;
  
  /**
   * Parses the data which is received via the FlightGear generic output
   * interface.<br>
   * After the parsing the data may be accessed via <code>getDouble</code> and
   * <code>getString</code> and the keys defined in the XML structure (e.g.
   * <code>controls-flight-elevator</code>).
   *  
   * @param data    the data received from FlightGear
   * @return        <code>true</code> if the parsing was successful
   */
  public Boolean parse(String data) {
    this.data = new HashMap<String, String>();

    // extract a single line of data (ignore the following ones if
    // there are any).
    // although this could result in loosing some of the data sent by FG
    // this is acceptable because it is sent periodically with a high
    // frequency. 
    String[] s = data.split("\n");
    if (s.length > 0) {
      StringBuffer xml = new StringBuffer(s[0]);
      try {
        // parse the xml data
        if (db == null) db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new InputSource(new java.io.StringReader(xml.toString())));
        NodeList nodes = doc.getDocumentElement().getChildNodes();
        for (int i=0; i<nodes.getLength(); ++i) {
          Node node = nodes.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            this.data.put(node.getNodeName(), node.getTextContent());
          }
        }
      } catch (IOException e) {
        System.out.println("IOException in FlightGearXMLParser::run(): " + e.getMessage());
      } catch (SAXException e) {
        System.out.println("SAXException in FlightGearXMLParser::run(): " + e.getMessage());
      } catch (ParserConfigurationException e) {
        System.out.println("ParserConfigurationException in FlightGearXMLParser::run(): " + e.getMessage());
      }
    }

    return this.data.size() > 0;
  }
}
