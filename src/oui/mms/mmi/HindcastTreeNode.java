/*
 * HindcastTreeNode.java
 *
 */
package oui.mms.mmi;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.w3c.dom.NodeList;
import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.SetOuiCalendarException;
import oui.esptool.WriteReport;
import oui.mms.MmsProjectXml;
import org.w3c.dom.Node;
import oui.util.OuiClassLoader;

/**
 *
 * @author  markstro
 */
public class HindcastTreeNode extends MmsModelTreeNode {
//    private MmsProjectXml pxml;
    private String mmsWorkspace;
    private String parent_class_name = null;
    private Object[] treeNodeArgs = null;
    private ArrayList<String> foreStartStrings = null;
    private ArrayList<String> foreEndStrings = null;
    private ArrayList<String> analyStartStrings = null;
    private ArrayList<String> analyEndStrings = null;
    private ArrayList<String> sortTypeStrings = null;
    private String parentNodeName;
    
    /** Creates a new instance of SingleRunMmi */
    public HindcastTreeNode(Node xml_node) {
        super(xml_node);
       MmsProjectXml pxml = MmsProjectXml.getMmsProjectXml();
        mmsWorkspace = pxml.getPath(MmsProjectXml.getElementContent(xml_node, "@path", null));
        Node parent_node = xml_node.getParentNode();
        parentNodeName = parent_node.getNodeName();
//        parent_class_name = pxml.getClassName(parent_node);
////        Object[] args = {parent_node};
////        for (int i = 0; i < args.length; i++) {
////            treeNodeArgs[i] = args[i];
////        }
        
        NodeList hindcastNodes = MmsProjectXml.selectNodes(xml_node, "hindcast");
        foreStartStrings = new ArrayList<String> (hindcastNodes.getLength());
        foreEndStrings = new ArrayList<String> (hindcastNodes.getLength());
        analyStartStrings = new ArrayList<String> (hindcastNodes.getLength());
        analyEndStrings = new ArrayList<String> (hindcastNodes.getLength());
        sortTypeStrings = new ArrayList<String> (hindcastNodes.getLength());
        
        for (int i = 0; i < hindcastNodes.getLength(); i++) {
            foreStartStrings.add(i, MmsProjectXml.getElementContent(hindcastNodes.item(i), "@forecastStart", null));
            foreEndStrings.add(i, MmsProjectXml.getElementContent(hindcastNodes.item(i), "@forecastEnd", null));
            analyStartStrings.add(i, MmsProjectXml.getElementContent(hindcastNodes.item(i), "@analysisStart", null));
            analyEndStrings.add(i, MmsProjectXml.getElementContent(hindcastNodes.item(i), "@analysisEnd", null));
            sortTypeStrings.add(i, MmsProjectXml.getElementContent(hindcastNodes.item(i), "@sortType", null));
        }
        
    }
    
    @Override
    public void run() {
//        Node parent_node = this._xml_node.getParentNode();
//        
//        String parent_class_name = pxml.getClassName(parent_node);
        
        try {
            Class cl = OuiClassLoader.factory().loadClass(parent_class_name, true);
            Class[] signature = {Class.forName("org.w3c.dom.Node")};
            Constructor constructor = cl.getConstructor(signature);
            // System.out.println ("processNodes: class = " + class_name + " signature = " + signature);
            
            Node parent_node = MmsProjectXml.getMmsProjectXml().getMmiNode(parentNodeName);
            parent_class_name = MmsProjectXml.getMmsProjectXml().getClassName(parent_node);
            Object[] args = {parent_node};
            
            Object espTreeNode = constructor.newInstance(args);
            
            ((MmsModelTreeNode)espTreeNode).setShowRunnerGui(false);
            
//            NodeList hindcastNodes = pxml.selectNodes(this._xml_node, "hindcast");


            //  This will setYMD the esp directory
            ((MmsModelTreeNode)espTreeNode).run();
            String baseEspIODir = MmsProjectXml.getMmsProjectXml().getEspIODir(null, null);
            
            for (int i = 0; i < foreStartStrings.size(); i++) {
                try {
                    OuiCalendar forecastStart = new OuiCalendar();
                    forecastStart.setDT(foreStartStrings.get(i));

                    OuiCalendar forecastEnd = new OuiCalendar();
                    forecastEnd.setDT(foreEndStrings.get(i));

                    OuiCalendar analysisStart = new OuiCalendar();
                    analysisStart.setDT(analyStartStrings.get(i));

                    OuiCalendar analysisEnd = new OuiCalendar();
                    analysisEnd.setDT(analyEndStrings.get(i));

                    String sortType = sortTypeStrings.get(i);
                    
                    int sortFlag = EnsembleData.VOLUME;
                    if (sortType.equals("VOLUME")) {
                        sortFlag = EnsembleData.VOLUME;
                    } else if (sortType.equals("PEAK")) {
                        sortFlag = EnsembleData.PEAK;
                    } else if (sortType.equals("YEAR")) {
                        sortFlag = EnsembleData.YEAR;
                    }
                    
                    String relativeFilePath = baseEspIODir + File.separatorChar + forecastStart.getMmsEspInitDate();

                    File outDir = new File(mmsWorkspace + "/output/" + relativeFilePath);
                    if (!outDir.exists()) {
                        if (!outDir.mkdir()) {
                            System.out.println("HindcastTreeNode: " + outDir.getAbsolutePath() + " not created, so ESP not run.");
                            return;
                        }
                    }

                    File inDir = new File(mmsWorkspace + "/input/data/" + relativeFilePath);
                    if (!inDir.exists()) {
                        if (!inDir.mkdir()) {
                            System.out.println("HindcastTreeNode: " + inDir.getAbsolutePath() + " not created, so ESP not run.");
                            return;
                        }
                    }
                    
                    // Set the ESP IO dir in the parent esp node to the directory just made.  The ESP traces for this forecast will go in there.
                    MmsProjectXml.getMmsProjectXml().setEspIODir(relativeFilePath);
                    
                    ((MmsModelTreeNode)espTreeNode).run();
                    ((MmsEspModelRunner)espTreeNode).runModel(forecastStart, forecastEnd);
                    
                    //  Generated reports for the .xml files in the current output directory
                    File[] xmlFiles = outDir.listFiles (new FileFilter () {
                        public boolean accept (File f) {
                            return f.getAbsolutePath().endsWith(".xml");
                        }
                    });
                    
                    for (int ii = 0; ii < xmlFiles.length; ii++) {
//                        System.out.println ("File = " + xmlFiles[ii].getAbsolutePath());
                        WriteReport.write (xmlFiles[ii], analysisStart, analysisEnd, sortFlag);
                    }
                    
                } catch (SetOuiCalendarException ex) {
                    ex.printStackTrace();
                }
            }
            
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("HindcastTreeNode: class " + parent_class_name + " invocation exception.");
            
        } catch (NoSuchMethodException e) {
            System.out.println("HindcastTreeNode: class " + parent_class_name + " doesn't have the proper constructor.");
            
        } catch (ClassNotFoundException e) {
            System.out.println("HindcastTreeNode: class " + parent_class_name + " not found.");
            
        } catch (InstantiationException e) {
            System.out.println("HindcastTreeNode: class " + parent_class_name + " instantiation exception.");
            
        } catch (IllegalAccessException e) {
            System.out.println("HindcastTreeNode: class " + parent_class_name + " illegal access exception.");
        }
    }
}
