/*
 * DogbyteData.java
 *
 * Created on December 15, 2006, 9:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oui.dogbyte;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author markstro
 */
public class DogbyteData {
    private  Hashtable<String,DimensionInfo> dim_info = new Hashtable<String,DimensionInfo> ();
    private  Hashtable<String,String> paths = new Hashtable<String,String> ();
    private String dim_name =null;
    
    
    /** Creates a new instance of DogbyteData */
    public DogbyteData(String file_name) {
        readXml (file_name);
    }
    
    public String getParameterFilePathForDim (String dim_name) {
        return dim_info.get(dim_name).paramInfo.valueFileName;
    }
    
    public String getThemePathForDim (String dim_name) {
        return dim_info.get(dim_name).themeInfo.valueFileName;
    }
    
    public String getGisOutPathForDim (String dim_name) {
        return dim_info.get(dim_name).gisOutInfo.valueFileName;
    }
    
    public String getiParamDefaultPathForDim (String dim_name) {
        return dim_info.get(dim_name).paramInfo.defaultFileName;
    }
    
    public Enumeration getDims () {
        return dim_info.keys();
    }
    
    private void readXml (String file_name) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        try {
            File file = new File(file_name);
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(file, new PathParser());
            saxParser.parse(file, new DimensionParser());
            
        } catch (Throwable err) {
            err.printStackTrace();
        }
    }
    
    private class PathParser extends DefaultHandler {
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("path")) {
                if (attributes != null) {
                    String path_name = null;
                    String path = null;

                    for (int i = 0; i < attributes.getLength(); i++) {
                        if (attributes.getQName(i).equals("name")) {
                            path_name = attributes.getValue(i);
                        } else if (attributes.getQName(i).equals("path")) {
                            path = attributes.getValue(i);
                            paths.put (path_name, path);
                        }
                    }
                }
            }
        }
    }
    
    private class DimensionParser extends DefaultHandler {
        private Stack stack;
        private Locator locator;
        
        public DimensionParser() {
            stack = new Stack ();
        }
        
        public void setDocumentLocator (Locator rhs) {locator = rhs;}
        
        private final int PARAMETER_FILE = 1;
        private final int GISOUT_FILE = 2;
        private final int THEME = 3;
        
        public void startElement (String uri, String localName, String qName, Attributes attribs) {
//            System.out.println ("startElement localName = " + localName + " qName = " + qName);
            
            // if next element is complex, push a new instance on the stack
            // if element has attributes, set them in the new instance
            if (qName.equals( "dimension")) {
                DimensionInfo di = new DimensionInfo ();
                stack.push(di);
                dim_info.put(resolveAttrib (uri, "name", attribs, "unknown"), di);
                
            } else if (qName.equals( "parameter_file")) {
                DimensionInfo di = (DimensionInfo)stack.peek();
                FileInfo fi = new FileInfo();
                stack.push(fi);
                di.paramInfo = fi;
                
                fi.fileType = PARAMETER_FILE;
                fi.valueFileName = getFullPath(uri, attribs);
                
            } else if (qName.equals( "theme" )) {
                DimensionInfo di = (DimensionInfo)stack.peek();
                FileInfo fi = new FileInfo();
                stack.push(fi);
                di.themeInfo = fi;
                
                fi.fileType = THEME;
                fi.valueFileName = getFullPath(uri, attribs);
                
            } else if (qName.equals( "gis_output_file")) {
                DimensionInfo di = (DimensionInfo)stack.peek();
                FileInfo fi = new FileInfo();
                stack.push(fi);
                di.gisOutInfo = fi;
                
                fi.fileType = GISOUT_FILE;
                fi.valueFileName = getFullPath(uri, attribs);
                
            } else if (qName.equals( "defaults")) {
                FileInfo fi = (FileInfo)stack.peek();
                fi.defaultFileName = getFullPath(uri, attribs);
                
            } else if (qName.equals( "ranges")) {
                FileInfo fi = (FileInfo)stack.peek();
                fi.rangeFileName = getFullPath(uri, attribs);
                
            } else if (qName.equals( "descriptions")) {
                FileInfo fi = (FileInfo)stack.peek();
                fi.descFileName = getFullPath(uri, attribs);                

            } else{
                // if none of the above, it is an unexpected element do nothing
            }
        }
        
        private String getFullPath (String uri, Attributes attribs) {
            return paths.get(resolveAttrib(uri, "path", attribs, "unknown")) + resolveAttrib(uri, "file", attribs, "unknown"); 
        }
        
        public void endElement( String uri, String localName, String qName ) {
//            System.out.println(" endElement localName = " + localName + " qName = " + qName);
            if (qName.equals( "dimension")) {
                stack.pop();
            } else if (qName.equals( "parameter_file")) {
                stack.pop();
            } else if (qName.equals( "gis_output_file")) {
                stack.pop();
            } else if (qName.equals( "theme")) {
                stack.pop();
            }
        }
        
        private String resolveAttrib( String uri, String localName, Attributes attribs, String defaultValue ) {
            String tmp = attribs.getValue( uri, localName );
            return (tmp!=null)?(tmp):(defaultValue);
        }
    }
        
    private class DimensionInfo {
        private FileInfo themeInfo = null;
        private FileInfo paramInfo = null;
        private FileInfo gisOutInfo = null;
        
        /** Creates a new instance of DimensionInfo */
        public DimensionInfo() {}
    }
                           
    private class FileInfo {
        private int fileType = -1;
        private String defaultFileName = null;
        private String rangeFileName = null;
        private String descFileName = null;
        private String valueFileName = null;
        
        /** Creates a new instance of ParamFileInfo */
        public FileInfo() {}
    }
}
