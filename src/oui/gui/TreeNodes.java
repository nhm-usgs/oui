/*
 * TreeNodes.java
 *
*/

package oui.gui;

import com.sun.org.apache.xpath.internal.XPathAPI;
import java.util.logging.Level;
import javax.xml.transform.TransformerException;
import oui.mms.OuiProjectXml;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import org.omscentral.gis.model.Theme;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import oui.treetypes.OuiThemeTreeNode;
import oui.treetypes.OuiTreeNode;
import oui.util.OuiClassLoader;

// This class extracts the root tree node from the xml file.
// It will also find a tree node corresponding to a GIS theme name.
public class TreeNodes {
    private static final Logger logger = Logger.getLogger(TreeNodes.class.getName());
    static protected OuiTreeNode root = null;
    
    public static OuiTreeNode getRootNode() {
        OuiProjectXml pxml = OuiProjectXml.getOuiProjectXml();
        if (pxml == null) return null;
        
        root =  new OuiTreeNode(pxml.getTreeXmlNode());
        processNodes(root, OuiProjectXml.selectNodes(pxml.getTreeXmlNode(), "node"));
        
        return (root);
    }

   /** Find the tree node that contains a certain theme.
     * @param theme The theme to look for.
     * @return The tree node.
     */
    static public OuiTreeNode getNodeForTheme(Theme theme) {
        if (root == null) {
            return null;
        }
        return (_lookForChildren(theme, root));
    }
    
    private static void processNodes(OuiTreeNode parent, NodeList node_list) {
        for (int i = 0; i < node_list.getLength(); i++) {
            Node node = node_list.item(i);
            OuiTreeNode tnd = null;
            String class_name;
            OuiProjectXml pxml = OuiProjectXml.getOuiProjectXml();
//            System.out.println ("processNodes: = " + node.toString ());
            
            //  Check to see if there is a python script associated with this node.  If there
            //  is, start the jython interpreter and load the script.
            String py_name = OuiProjectXml.getPythonName (node);

            if (py_name.compareTo("none") != 0) {
                class_name = "oui.mms.mmi.PythonTreeNode";
                
            } else {
                
                //  There is no python script.  Get the name of the class to load and load it.
                class_name = pxml.getClassName (node);
            }

            try {
                Node n = XPathAPI.selectSingleNode(node, "@name");
                if (n == null) {
                    node.setUserData("name", "No name set for this node", null);
                } else {
                    node.setUserData("name", OuiProjectXml.getNodeContent(n), null);
                }
            } catch (TransformerException ex) {
            }


            
            try {
                Class cl = OuiClassLoader.factory().loadClass(class_name, true);
                Class[] signature = {Class.forName("org.w3c.dom.Node")};
                Constructor constructor = cl.getConstructor(signature);
                logger.log(Level.FINEST, "class = {0} signature = {1}", new Object[]{class_name, signature});
                               
                Object[] args = {node};
                tnd = (OuiTreeNode)constructor.newInstance(args);
                
            } catch (InvocationTargetException e) {
             
            } catch (NoSuchMethodException e) {
                logger.log(Level.SEVERE, "class {0} in project.xml file, doesn''t have the proper constructor.", class_name);
            
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "class {0} in project.xml file, not found.", class_name);
                
            } catch (InstantiationException e) {
                logger.log(Level.SEVERE, "class {0} in project.xml file, instantiation exception.", class_name);
                
            } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, "class {0} in project.xml file, illegal access exception.", class_name);
                
            } finally {
                if (tnd == null) tnd = new OuiTreeNode(node);
                if (parent != null) parent.addChild(tnd);

            }
            processNodes (tnd, OuiProjectXml.selectNodes (node, "node"));
        }
    }

    static private OuiTreeNode _lookForChildren(Theme theme, OuiTreeNode parent) {
        OuiTreeNode child, tnd;
        
        if ((parent.has_map()) && (theme == ((OuiThemeTreeNode)parent).getTheme())) {
            return parent;
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            child = parent.getChildAt(i);
            if ((child.has_map()) && (theme == ((OuiThemeTreeNode)child).getTheme())) {
                return child;
            }
        }
        
        for (int i = 0; i < parent.getChildCount(); i++) {
            child = parent.getChildAt(i);
            tnd = _lookForChildren(theme, child);
            if (tnd != null) {
                return tnd;
            }
        }
        
        return null;
    }
}
