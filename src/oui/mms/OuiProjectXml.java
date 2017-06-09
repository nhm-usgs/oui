/*
 * LoadedPanel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms;

import com.sun.org.apache.xpath.internal.XPathAPI;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;	//	DOM will parse XML file and can utilze the tree from there...
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import oui.gui.Oui;
import oui.util.OuiClassLoader;

public class OuiProjectXml {
    private static final Logger logger = Logger.getLogger(OuiProjectXml.class.getName());
    protected Document doc = null;	//	Total contents of the XML file...is parsed!!!  COOL!
    
    protected Node treeXmlNode = null;
    protected Node pathsXmlNode = null;
    protected Node projectXmlNode = null;
    protected Node themeMetaDataXmlNode = null;
    protected String projectName = null;
    protected String treeName = null;
//    protected String treeLogFile = null;
    protected String projectFile = null;
    protected static OuiProjectXml opxml = null;

    final private static DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
    private static DocumentBuilder builder;

    
    static {
        logger.setLevel(Oui.ouiLogLevel);
        
        try {
            factory.setNamespaceAware(true);
            //	factory.setIgnoringElementContentWhitespace(true);
            builder = factory.newDocumentBuilder();
        } catch (Exception E) {
            logger.log(Level.WARNING, E.getMessage());
        }
    }

    public static OuiProjectXml OuiProjectXmlFactory(String projectFile) {
        
        Document d = loadDocument(projectFile, true);	//	Project file is passed to this constructor,
        Node pn = selectSingleNode(d, "/project");	//	"/project" is XPath language
        
        String xml_adapt_class = getElementContent(pn, "@xmlAdapterClass", "oui.gui.OuiProjectXml");
        d = null;
        pn = null;
        
        try {
            Class cl = OuiClassLoader.factory().loadClass(xml_adapt_class, true);
            Class[] signature = {Class.forName("java.lang.String")};
            Constructor constructor = cl.getConstructor(signature);
            Object[] args = {projectFile};
            opxml = (OuiProjectXml) constructor.newInstance(args);

        } catch (Exception e) {
            logger.log(Level.WARNING, "{0} invocation exception.", xml_adapt_class);
        }
        
        return opxml;
    }
    
    public static OuiProjectXml getOuiProjectXml() { return opxml; }
    
    /** Create a OuiProjectXml object.
     * @param oui The OUI object for this panel.
     */
    public OuiProjectXml(String projectFile) {
        this.projectFile = projectFile;
        
        doc = loadDocument(projectFile, true);
        if (doc == null) {
            logger.log(Level.WARNING, "document {0} not loaded", projectFile);
        }
        
        projectXmlNode = selectSingleNode(doc, "/project");
        if (projectXmlNode == null) {
            logger.log(Level.WARNING, "Node /project not found in {0}", projectFile);
        }

        projectName = getElementContent(projectXmlNode, "@name", "Project");
        if (projectName == null) {
            logger.log(Level.WARNING, "Attribute @name not set in node /project in {0}", projectFile);
        }

        treeXmlNode = selectSingleNode(projectXmlNode, "tree");
        if (treeXmlNode == null) {
            logger.log(Level.WARNING, "Node /project/tree not set in {0}", projectFile);
        }

        treeName = getElementContent(treeXmlNode, "@name", null);
        if (treeName == null) {
            logger.log(Level.WARNING, "Attribute @name not set in node /project/tree in {0}", projectFile);
        }

        themeMetaDataXmlNode = selectSingleNode(projectXmlNode, "MetaData/gis/themes");
        
        // DANGER hack
        if (themeMetaDataXmlNode == null) {
            themeMetaDataXmlNode = selectSingleNode(projectXmlNode, "MetaData/themes");
        }

        if (themeMetaDataXmlNode == null) {
            logger.log(Level.WARNING, "Node not set for MetaData/gis/themes in {0}", projectFile);
        }
        
        pathsXmlNode = selectSingleNode(projectXmlNode, "paths");
        if (pathsXmlNode == null) {
            logger.log(Level.WARNING, "Node not set for paths in {0}", projectFile);
        }

        // using the java logging system so I don't need this any more.
//        paths = figureOutPaths();
//        String path = getPath ("log");
//        treeLogFile = path + "/" + getElementContent(treeXmlNode, "@logFile", "./Oui.log");
    }
    
    /**
     * Getter for property projectXmlNode.
     * @return Value of property projectXmlNode.
     */
    public Node getProjectXmlNode() {
        return this.projectXmlNode;
    }
    
    /**
     * Getter for property treeXmlNode.
     * @return Value of property treeXmlNode.
     */
    public Node getTreeXmlNode() {
        return this.treeXmlNode;
    }
    
    public static Document loadDocument(String url, boolean validate) {
        Document doc = null;
        
        try {
            doc = builder.parse(url);
        } catch (Exception E) {
            logger.log(Level.WARNING, E.getMessage());
        }
        return doc;
    }

    public static String getElementContent(Node node, String xql, String def) {
        if (node == null) {
            logger.log(Level.WARNING, "Query attempted on null node");
            return def;
        }

        Node n = selectSingleNode(node, xql);

        if (n == null) {
                logger.log(Level.WARNING, "Selection from node = ''{0}'' failed with query = ''{1}''. Setting to default value = ''{2}''", new Object[]{node.getUserData("name"), xql, def});
//            StackTraceElement[] ste = new Throwable().getStackTrace();
//            for (int i = 0; i < ste.length; i++) {
//                System.out.println("Class Name: " + ste[i].getClassName() + ", Method Name: " + ste[i].getMethodName());
//            }
            return def;
        }

        String s = getNodeContent(n);
        if (s == null) {
            logger.log(Level.WARNING, "Node {0} has no content", n.getNodeName());
        }
        return s;
    }
    
    public static Node selectSingleNode(Node n, String xqlString) {
        try {
            NodeList node_list = selectNodes(n, xqlString);

            if (node_list == null) {
                logger.log(Level.WARNING, "Selection of node from node = {0} failed with query = {1}", new Object[]{n.getNodeName(), xqlString});
                return null;

            } else if (node_list.getLength() > 1) {
                logger.log(Level.WARNING, "More than one node matches selection of node from node = {0} failed with query = {1}", new Object[]{n.getNodeName(), xqlString});
            }

            return node_list.item(0);

        } catch (Exception E) {
            logger.log(Level.WARNING, E.getMessage());
            return null;
        }
    }
    
    /**
     * returns the content of a node as a String
     */
    public static String getNodeContent(Node n) {
        String v;
        if (n instanceof Element){
            Node child = n.getFirstChild();
            v = (child!=null) ? child.getNodeValue() : "";
        } else
            v = n.getNodeValue();
        return v;
    }
    
    public static Element getFirstElement(Node n) {
        Node tmp = n.getFirstChild();
        while (tmp != null) {
            if (tmp instanceof Element)
                return (Element) tmp;
            tmp = tmp.getNextSibling();
        }
        return null;
    }
    
    public static NodeList selectNodes(Node n, String xqlString) {
        try {
            return XPathAPI.selectNodeList(n, xqlString);
        } catch (Exception E) {
            System.err.println(E.getMessage());
            return null;
        }
    }

    public String getMenuName(Node menu) {
        return getElementContent(menu, "@name", "No Name");
    }
    
    public NodeList getSubMenu(Node menu) {
        return selectNodes(menu, "MenuItem");
    }

    public static String getPythonName(Node node) {
        return getElementContent(node, "@py_file", "none");
    }
    
    public String getClassName(Node node) {
        return getElementContent(node, "@class", "oui.treetypes.OuiTreeNode");
    }
    
    public String getThemeMetaData(String theme_name, String arg, String def) {
        String xql = "theme[@theme='" + theme_name + "']";
        Node node = selectSingleNode(themeMetaDataXmlNode, xql);
        
        if (node == null) {
            System.out.println("OuiProjectXml.getThemeMetaData:  No meta data found for " + theme_name);
            return def;
        }
        
        return getElementContent(node, arg, def);
    }
    
    public String getModelIdForThemeId(String theme_name, String station_name) {
        String xql = "theme[@theme='" + theme_name + "']/mappings/mapping[@themeId='" + station_name + "']";
        Node node = selectSingleNode(themeMetaDataXmlNode, xql);
        
        if (node == null) {
            System.out.println("OuiProjectXml.getModelIdForThemeId:  No node found for " + theme_name + " " + station_name);
            return null;
        }
        
        return getElementContent(node, "@modelId", "bad value!");
    }
    
    public String getModelNameForThemeId(String theme_name, String station_name) {
        String xql = "theme[@theme='" + theme_name + "']/mappings/mapping[@themeId='" + station_name + "']";
        Node node = selectSingleNode(themeMetaDataXmlNode, xql);
        
        if (node == null) {
            System.out.println("OuiProjectXml.getModelNameForThemeId:  No node found for " + theme_name + " " + station_name);
            return null;
        }
        
        return getElementContent(node, "@modelName", "bad value!");
    }

    public void changePathAttribute (String attribute, String key, String new_val) {
        Element em = (Element)(selectSingleNode(pathsXmlNode, "path[@name='" + key + "']"));
        
        Attr attrib = doc.createAttribute(attribute);
        attrib.setValue(new_val);
        em.setAttributeNode(attrib);
    }

    public void deletePathAttribute(String key) {
        pathsXmlNode.removeChild(selectSingleNode(pathsXmlNode, "path[@name='" + key + "']"));
    }
    
    public String[] getPathKeys () {
        NodeList nodes = selectNodes (pathsXmlNode, "path");
        String[] keys = new String[nodes.getLength()];
//                System.out.println("paths count = " + keys.length);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            keys[i] = getElementContent(node, "@name", "bad name!");
        }
        return keys;
    }
    
    final public String getPath(String name) {
//                System.out.println("get path name = " + name);
//                System.out.println("get path count = " + pathsXmlNode.getChildNodes());
        String xql = "path[@name='" + name + "']";
        Node node = selectSingleNode(pathsXmlNode, xql);
        
        if (node == null) {
            System.out.println("OuiProjectXml.getPath:  No node found for " + name);
            return null;
        }
        
        return getElementContent(node, "@path", "bad path!");
    }
    
    public String getPath (Node node) {
        return getPath (getElementContent (node, "@path", null));
    }
    
    public void addPath (String tag, String path) {
        try {
            Element new_node = doc.createElement("path");
            Attr attrib = doc.createAttribute("name");
            attrib.setValue(tag);
            new_node.setAttributeNode(attrib);
            
            attrib = doc.createAttribute("path");
            attrib.setValue(path);
            new_node.setAttributeNode(attrib);

            pathsXmlNode.appendChild(new_node);
            
        } catch (DOMException e) {
            e.printStackTrace();
        }
    }
}