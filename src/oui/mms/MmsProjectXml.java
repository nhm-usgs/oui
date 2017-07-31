/*
 * LoadedPanel.java
 *
 * Created on November 13, 2002, 2:11 PM
 */

package oui.mms;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MmsProjectXml extends OuiProjectXml {
    private static final Logger logger = Logger.getLogger(MmsProjectXml.class.getName());

    public static MmsProjectXml getMmsProjectXml() {
        return (MmsProjectXml)opxml;
    }

    public MmsProjectXml(String projectFile) {
        super (projectFile);
    }

    /*
     *  For sure that I need these
     *---------------------------------------------------------------------------------------------------------------------
     **/
    private Node getFileNode(String typeTag, String fileTag) {
        Node node = selectSingleNode(projectXmlNode, "MetaData/files/file[@type='" + typeTag + "'][@name='" + fileTag + "']");
        if (node == null) {
            logger.log(Level.WARNING, "File definition not found in project.xml file for MetaData/files/file[@type=''{0}''][@name=''{1}'']", new Object[]{typeTag, fileTag});
        }
        return node;
    }
    
    public String getFilePath(String typeTag, String fileTag) {
        String path = getPath(getElementContent(getFileNode(typeTag, fileTag), "@path", null));
        if (path == null) {
            logger.log(Level.WARNING, "File path not found in project.xml file for typeTag = {0} fileTag = {1}", new Object[]{typeTag, fileTag});
        }
        return path;
    }
    
    public String getFileFile(String typeTag, String fileTag) {
        String elementContent = getElementContent(getFileNode(typeTag, fileTag), "@file", null);
        if (elementContent == null) {
            logger.log(Level.WARNING, "File name not found in project.xml file for typeTag = {0} fileTag = {1}", new Object[]{typeTag, fileTag});
        }
        return elementContent;
    }
    
    public String getFileFullPath (String typeTag, String fileTag) {
        String foo = getFilePath(typeTag, fileTag) + "/" + getFileFile (typeTag, fileTag);
        if (foo == null) {
            logger.log(Level.WARNING, "Full file path and name not found in project.xml file for typeTag = {0} fileTag = {1}", new Object[]{typeTag, fileTag});
        }
        return foo;
    }
    
    public Node getMmiNode(String mmiTag) {
        Node selectSingleNode = selectSingleNode(projectXmlNode, "MetaData/mmis/mmi[@mmi='" + mmiTag + "']");
        if (selectSingleNode == null) {
            logger.log(Level.WARNING, "MMI node not found in project.xml file for MetaData/mmis/mmi[@mmi=''{0}'']", mmiTag);
        }
        return selectSingleNode;
    }
    
    public String getMmiPath(String mmiTag) {
        String path = getPath(getElementContent(getMmiNode(mmiTag), "@path", null));
        if (path == null) {
            logger.log(Level.WARNING, "MMI path (tag @path) not found in project.xml file for mmiTag = ''{0}''", mmiTag);
        }
        return path;
    }
    
    public String getMmiFile(String mmiTag, String fileTypeTag) {
        String elementContent = getElementContent(getMmiNode(mmiTag), fileTypeTag, null);
        if (elementContent == null) {
            logger.log(Level.WARNING, "MMI file not found in project.xml file for mmiTag = {0} fileTypeTag = {1}", new Object[]{mmiTag, fileTypeTag});
        }
        return elementContent;
    }

    public Node getDmiNode(String dmiXmlTag) {
        Node node = selectSingleNode(projectXmlNode, "MetaData/dmis/dmi[@dmi='" + dmiXmlTag + "']");
        if (node == null) {
            logger.log(Level.WARNING, "DMI node not found in project.xml file for MetaData/dmis/dmi[@dmi=''{0}'']", dmiXmlTag);
        }
        return node;
    }
    
    public Node getDmiDatafileNode(String dmiDataFileTag) {
        Node node = getFileNode("datafile", dmiDataFileTag);
        if (node == null) {
            logger.log(Level.WARNING, "No xml defining the {0} DMI", dmiDataFileTag);
        }
        return node;
    }
    
    private Node getDataFileColumnNode(String dmiXmlTag, String col_index) {
        Node node = selectSingleNode(getFileNode("datafile", dmiXmlTag), "columns/column[@column='" + col_index + "']");

        if (node == null) {
            logger.log(Level.WARNING, "Data File Column node not found in project.xml file for columns/column[@column=''{0}'']", col_index);
        }
        return node;
    }

    public String getDmiDataFile(String dmiXmlTag) {
        Node dmiNode = getDmiNode(dmiXmlTag);
        if (dmiNode == null) {
            logger.log(Level.WARNING, "DMI node not found in project.xml file for {0}", dmiXmlTag);
        }

        String s = getElementContent(dmiNode, "@datafile", null);
        if (s == null) {
            logger.log(Level.WARNING, "@datafile tag not found in project.xml file for DMI node {0}", dmiXmlTag);
        }
        return s;
    }
      
    public String getDataFileHeader(String dmiXmlTag) {
        Node dataFileNode = getFileNode("datafile", dmiXmlTag);
        Node node = selectSingleNode(dataFileNode, "header");

        if (node == null) {
            logger.log(Level.WARNING, "No xml defining the header node for Data File {0}", dmiXmlTag);
            return null;
        }

        String s = getNodeContent(node);
        if (s == null) {
            logger.log(Level.WARNING, "No header content for Data File node {0}", dmiXmlTag);
            return null;
        }

        return s;
    }
    
    public int getDataFileColumnCount(String dmiXmlTag) {
        NodeList nodes = selectNodes(getDmiDatafileNode(dmiXmlTag), "columns/column");

        if (nodes == null) {
            logger.log(Level.WARNING, "No xml defining the columns/column nodes for the DMI Data File {0}", dmiXmlTag);
            return 0;
        }
        
        return nodes.getLength ();
    }
        
    public String getDataFileVariableName (String dmiXmlTag, String col_index) {
        String s = getElementContent (getDataFileColumnNode (dmiXmlTag, col_index), "@variable", null);

        if (s == null) {
            logger.log(Level.WARNING, "No xml defining the Data File Variable name (@variable tag) nodes for the DMI Data File {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }

        return s;
    }
    
    public int getDataFileVariableIndex (String dmiXmlTag, String col_index) {
        String index = getElementContent (getDataFileColumnNode (dmiXmlTag, col_index), "@variableIndex", null); 
        
        if (index == null) {
            logger.log(Level.WARNING, "No xml defining the Data File Variable index (@variableIndex tag) nodes for the DMI Data File {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }
        
        int i = Integer.parseInt (index);
        return i;
    } 
    
    public String getDataFileVariableStation(String dmiXmlTag, String col_index) {
        Node col_node = getDataFileColumnNode(dmiXmlTag, col_index);

        if (col_node == null) {
            logger.log(Level.WARNING, "No xml defining the Data File variable station node nodes for the DMI Data File {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }

        String s = getElementContent(col_node, "@name", "No Name");

        if (s == null) {
            logger.log(Level.WARNING, "No @name tag for the DMI Data File variable station in node {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }

        return s;
    }
            
    public String getDataFileVariableStationId(String dmiXmlTag, String col_index) {
        Node col_node = getDataFileColumnNode(dmiXmlTag, col_index);
        if (col_node == null) {
            logger.log(Level.WARNING, "No DMI Data File column node for {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }

        String station_type = getElementContent(col_node, "@type", null);
        if (station_type == null) {
            logger.log(Level.WARNING, "No @type tag for DMI Data File column node for {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }
        
        String station_lookup_name = getElementContent(col_node, "@name", null);
        if (station_lookup_name == null) {
            logger.log(Level.WARNING, "No @name tag for DMI Data File column node for {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }
        
        Node station_node = null;
        if (station_type.equals("stations")) {
            station_node = selectSingleNode(projectXmlNode, "MetaData/data/stations/station[@station='" + station_lookup_name + "']");
            if (station_node == null) {
                logger.log(Level.WARNING, "No climate station node for MetaData/data/stations/station[@station=''{0}'']", station_lookup_name);
            }

        } else {    // station_type = "streamgages"
            station_node = selectSingleNode(projectXmlNode, "MetaData/data/streamgages/streamgage[@streamgage='" + station_lookup_name + "']");
            if (station_node == null) {
                logger.log(Level.WARNING, "No streamgage node for MetaData/data/streamgages/streamgage[@streamgage=''{0}'']", station_lookup_name);
            }
        }

        String s = getElementContent(station_node, "@id", null);
        if (s == null) {
            logger.log(Level.WARNING, "No @id tag for station node =''{0}'']", station_node.getNodeName());
        }

        return s;
    }
    
    public String getDataFileVariableStationType(String dmiXmlTag, String col_index) {
        Node col_node = getDataFileColumnNode(dmiXmlTag, col_index);
         if (col_node == null) {
            logger.log(Level.WARNING, "No xml defining the Data File variable station type for the DMI Data File {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }

        String station_type = getElementContent(col_node, "@type", null);
        if (station_type == null) {
            logger.log(Level.WARNING, "No @type tag for DMI Data File station type for {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }
        
        String station_lookup_name = getElementContent(col_node, "@name", null);
        if (station_lookup_name == null) {
            logger.log(Level.WARNING, "No @name tag for DMI Data File variable station type for {0} column index {1}", new Object[]{dmiXmlTag, col_index});
        }
        
        Node station_node = null;
        if (station_type != null && station_type.equals("stations")) {
            station_node = selectSingleNode(projectXmlNode, "MetaData/data/stations/station[@station='" + station_lookup_name + "']");
            if (station_node == null) {
                logger.log(Level.WARNING, "No climate station node for MetaData/data/stations/station[@station=''{0}'']", station_lookup_name);
            }

        } else {    // station_type = "streamgages"
            station_node = selectSingleNode(projectXmlNode, "MetaData/data/streamgages/streamgage[@streamgage='" + station_lookup_name + "']");
            if (station_node == null) {
                logger.log(Level.WARNING, "No streamgage node for MetaData/data/streamgages/streamgage[@streamgage=''{0}'']", station_lookup_name);
            }
        }
        String s = getElementContent(station_node, "@type", null);
        
        if (s == null) {
            logger.log(Level.WARNING, "No @type tag for station node =''{0}'']", station_node.getNodeName());
        }
        return s;
    }

    public String getDataFileVariableFormat (String dmiXmlTag, String col_index) {
        String variable_lookup_name = getDataFileVariableName (dmiXmlTag, col_index);
        if (variable_lookup_name == null) {
            logger.log(Level.WARNING, "No variable lookup name for  Data File DMI = {0} column index = {1}", new Object[]{dmiXmlTag, col_index});
        }

        Node variable_node = selectSingleNode(projectXmlNode, "MetaData/variables/variable[@variable='" + variable_lookup_name + "']");
        if (variable_node == null) {
            logger.log(Level.WARNING, "No node for variable MetaData/variables/variable[@variable=''{0}'']", variable_lookup_name);
        }

//        String s = getElementContent (variable_node, "@format", "f7.1");
        String s = getElementContent(variable_node, "@format", null);
        if (s == null) {
            logger.log(Level.WARNING, "No @format tag for Data File DMI = {0} column index = {1}. Try f7.1 as a default", new Object[]{dmiXmlTag, col_index});
        }
        return s;
    }

    public double getDataFileVariableMissingValue (String dmiXmlTag, String col_index) {
        String variable_lookup_name = getDataFileVariableName (dmiXmlTag, col_index);
        if (variable_lookup_name == null) {
            logger.log(Level.WARNING, "No variable lookup name for  Data File DMI = {0} column index = {1}", new Object[]{dmiXmlTag, col_index});
        }

        Node variable_node = selectSingleNode(projectXmlNode, "MetaData/variables/variable[@variable='" + variable_lookup_name + "']");
        if (variable_node == null) {
            logger.log(Level.WARNING, "No node for variable MetaData/variables/variable[@variable=''{0}'']", variable_lookup_name);
        }

        String s = getElementContent(variable_node, "@missing", null);
        if (s == null) {
            logger.log(Level.WARNING, "No @format tag for Data File DMI = {0} column index = {1}. Try -99.9 as a default", new Object[]{dmiXmlTag, col_index});
            s = "-99.9";
        }

        double d;
        try {
             d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Bad syntax on @format tag for Data File DMI = {0} column index = {1}. Setting the value to -99.9 as a default", new Object[]{dmiXmlTag, col_index});
            d = -99.9;
        }

        return d;
    }

    public String[] getDmiVariableNames (Node node) {
        NodeList nodes = selectNodes (node, "variable");
        if (nodes == null) {
            logger.log(Level.WARNING, "No DMI variable name nodes for node = {0}", node.getNodeName());
        }

        String[] ret = new String[nodes.getLength ()];

        for (int i = 0; i < nodes.getLength(); i++) {
            ret[i] = getElementContent(nodes.item(i), "@variable", null);

            if (ret[i] == null) {
                logger.log(Level.WARNING, "No @variable tag for node = {0}", nodes.item(i).getNodeName());
            }
        }
        return ret;
    }
    
    public String getDmiDataFileStart(String dmiXmlTag) {
        Node dmiNode = getDmiNode(dmiXmlTag);
        if (dmiNode == null) {
            logger.log(Level.WARNING, "No DMI node for tag {0}", dmiXmlTag);
        }

        String s = getElementContent(dmiNode, "@start", null);
        if (s == null) {
            logger.log(Level.WARNING, "No @start attribute for DMI node for tag {0}", dmiXmlTag);
        }

        return s;
    }

    public String getDmiDataFileEnd (String dmiXmlTag) {
        Node dmiNode = getDmiNode(dmiXmlTag);
        if (dmiNode == null) {
            logger.log(Level.WARNING, "No DMI node for tag {0}", dmiXmlTag);
        }

        String s = getElementContent(dmiNode, "@end", null);
        if (s == null) {
            logger.log(Level.WARNING, "No @end attribute for DMI node for tag {0}", dmiXmlTag);
        }
        return s;
    }

    private String espIODir = null;
    private Node espMmiNode = null;
    public String getEspIODir(Node espMmiNode, String string) {
        if (espIODir == null || (espMmiNode != null && espMmiNode != this.espMmiNode)) {
            espIODir = getElementContent(espMmiNode, "@espIODir", null);

            if (espIODir == null) {
                logger.log(Level.WARNING, "No @espIODir attribute for DMI node for tag {0}", espMmiNode.getNodeName());
            }
            this.espMmiNode = espMmiNode;
        }
        
        return espIODir;
    }
    
    public void setEspIODir (String espIODir) {
        this.espIODir = espIODir;
    }
}