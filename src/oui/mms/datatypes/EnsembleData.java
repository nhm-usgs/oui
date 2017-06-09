package oui.mms.datatypes;

import oui.esptool.EnsembleListLabel;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import oui.mms.io.MmsOuiEspReader;
import oui.mms.OuiProjectXml;


public class EnsembleData {
    static public int VOLUME = 1;
    static public int PEAK = 2;
    static public int YEAR = 3;
    private TimeSeries initialization; //  contains data for plotting
    private ArrayList<TimeSeries> forecasts;           //  array of TimeSeries with data for plotting
    private ArrayList<TimeSeries> historic;            //  array of TimeSeries with no data; used to keep track of historic years
    private ArrayList<TimeSeries> input;               //  array of TimeSeries with no data; used to keep track of input files
    private ArrayList<TimeSeries> output;              //  array of TimeSeries with init + forecast data for analysis
    private ArrayList<TimeSeries> cbhPrecip;           // Climate by HRU Precip data
    private ArrayList<TimeSeries> cbhTmax;           // Climate by HRU Tmax data
    private ArrayList<TimeSeries> cbhTmin;           // Climate by HRU Tmin data
    private ArrayList<EnsembleListLabel> stats;               //  array of EnsembleListLabel
    private ArrayList<EnsembleListLabel> statsInVolumeOrder;  
    private ArrayList<EnsembleListLabel> statsInPeakOrder;
    private String name;
    private int sortOrder = VOLUME;

    public EnsembleData (String name, TimeSeries initialization, ArrayList<TimeSeries> forecasts, ArrayList<TimeSeries> historic) {
        this.name = name;
        this.initialization = initialization;
        this.historic = historic;
        setForecasts(forecasts);
    }
    
    private void PROCESS_trace(OuiCalendar analysisStart, OuiCalendar analysisEnd) {
        int start_index;
        int analysis_dateCount = (int)(analysisEnd.getJulian() - analysisStart.getJulian()) + 1;
        for (int j = 0; j < forecasts.size(); j++) {
            EnsembleListLabel ts = (EnsembleListLabel)(stats.get(j));
            TimeSeries trace = (TimeSeries)(output.get(j));
            double[] trace_data = trace.getVals();
            double[] trace_dates = trace.getDates();
            
            ts.setTraceVolume(0.0);
            ts.setTracePeak(0.0);
            
            int offset = (int)(analysisStart.getJulian()) - (int)(trace.getStart().getJulian());
            int length = (int)(analysisEnd.getJulian()) - (int)(analysisStart.getJulian()) + 1;
                
            for (int k = 0; k < length; k++) {
                ts.setTraceVolume(trace_data[k + offset] + ts.getTraceVolume());
                
                if (ts.getTracePeak() < trace_data[k + offset]) {
                    ts.setTracePeak(trace_data[k + offset]);
                    ts.setTimeToPeak(trace_dates[k + offset]);
                }
            }
        }
        
        statsInVolumeOrder = new ArrayList<EnsembleListLabel> (stats.size());
        statsInPeakOrder = new ArrayList<EnsembleListLabel> (stats.size()); 
/*
 *  Make copies
 */
        for (int i = 0; i < stats.size(); i++) {
            statsInVolumeOrder.add(i, stats.get(i));
            statsInPeakOrder.add(i, stats.get(i));
        }
        
        Sort(statsInVolumeOrder, statsInPeakOrder);
    }
    
    public static void Sort(ArrayList<EnsembleListLabel> statsInVolumeOrder, ArrayList<EnsembleListLabel> statsInPeakOrder) {

/*
 *  Sort by volumes
 */
        for (int i = 0; i < statsInVolumeOrder.size() - 1; i++) {
            EnsembleListLabel tsi = (EnsembleListLabel)(statsInVolumeOrder.get(i));
            for (int j = i+1; j < statsInVolumeOrder.size(); j++) {
                EnsembleListLabel tsj = (EnsembleListLabel)(statsInVolumeOrder.get(j));
                if (tsj.getTraceVolume() > tsi.getTraceVolume()) {
                    statsInVolumeOrder.set(j, tsi);
                    statsInVolumeOrder.set(i, tsj);
                    tsi = tsj;
                }
            }
        }

/*
 *  Generate volume probabilities
 *  Formula is   P = (100.0) m / (n + 1.0)  (Linsley, Kohler, and Paulhus page 249)
 */        
        double n = (double)(statsInVolumeOrder.size() + 1);
        for (int i = 0; i < statsInVolumeOrder.size(); i++) {
            double m = (double)(i + 1);
            EnsembleListLabel tsi = (EnsembleListLabel)(statsInVolumeOrder.get(i));
            double prob = 100 * m / n;
            tsi.setVolumeRank(i+1);
            tsi.setActVolumeProb(prob);
            tsi.setRoundVolumeProb(fivePercentRound(prob));
        }
        
/*        
        System.out.println("\nVolume");
        for (int i = 0; i < stats.size(); i++) {
            EnsembleListLabel tsi = (EnsembleListLabel)(statsInVolumeOrder.get(i));
            System.out.println ("EnsembleData.Sort statsInVolumeOrder = " + tsi + " volume = " + tsi.getTraceVolume() + " peak = " + tsi.getTracePeak() + " time to peak = " + tsi.getTimeToPeak());
        }
*/        
/*
 *  Sort by peak
 */
        for (int i = 0; i < statsInPeakOrder.size() - 1; i++) {
            EnsembleListLabel tsi = (EnsembleListLabel)(statsInPeakOrder.get(i));
            for (int j = i+1; j < statsInPeakOrder.size(); j++) {
                EnsembleListLabel tsj = (EnsembleListLabel)(statsInPeakOrder.get(j));
                if (tsj.getTracePeak() > tsi.getTracePeak()) {
                    statsInPeakOrder.set(j, tsi);
                    statsInPeakOrder.set(i, tsj);
                    tsi = tsj;
                }
            }
        }
/*        
        System.out.println("\nPeak");
        for (int i = 0; i < stats.size(); i++) {
            EnsembleListLabel tsi = (EnsembleListLabel)(statsInPeakOrder.get(i));
            System.out.println ("EnsembleData.Sort statsInPeakOrder = " + tsi + " volume = " + tsi.getTraceVolume() + " peak = " + tsi.getTracePeak() + " time to peak = " + tsi.getTimeToPeak());
        }
 */
/*
 *  Generate peak probabilities
 *  Formula is   P = (100.0) m / (n + 1.0)  (Linsley, Kohler, and Paulhus page 249)
 */        
        n = (double)(statsInPeakOrder.size() + 1);
        for (int i = 0; i < statsInPeakOrder.size(); i++) {
            double m = (double)(i + 1);
            EnsembleListLabel tsi = (EnsembleListLabel)(statsInPeakOrder.get(i));
            double prob = 100 * m / n;
            tsi.setPeakRank(i+1);
            tsi.setActPeakProb(prob);
            tsi.setRoundPeakProb(fivePercentRound(prob));
        }        

    }
    
    public static double fivePercentRound(double p) {
        
        if (p > 92.5) {
            return (95.0);
        } else if (p > 87.5) {
            return (90.0);
        } else if (p > 82.5) {
            return (85.0);
        } else if (p > 77.5) {
            return (80.0);
        } else if (p > 72.5) {
            return (75.0);
        } else if (p > 67.5) {
            return (70.0);
        } else if (p > 62.5) {
            return (65.0);
        } else if (p > 57.5) {
            return (60.0);
        } else if (p > 52.5) {
            return (55.0);
        } else if (p > 47.5) {
            return (50.0);
        } else if (p > 42.5) {
            return (45.0);
        } else if (p > 37.5) {
            return (40.0);
        } else if (p > 32.5) {
            return (35.0);
        } else if (p > 27.5) {
            return (30.0);
        } else if (p > 22.5) {
            return (25.0);
        } else if (p > 17.5) {
            return (20.0);
        } else if (p > 12.5) {
            return (15.0);
        } else if (p > 7.5) {
            return (10.0);
        } else {
            return (5.0);
        }
    }
    
    public OuiCalendar getInitializationStart () {
        return this.initialization.getStart();
    }
    
    public OuiCalendar getInitializationEnd () {
        return this.initialization.getEnd();
    }
    
    public OuiCalendar getForecastStart () {
        TimeSeries forecast = (TimeSeries)(forecasts.get(0));
        return forecast.getStart();
    }
    
    public OuiCalendar getForecastEnd () {
        TimeSeries forecast = (TimeSeries)(forecasts.get(0));
        return forecast.getEnd();
    }
    
    /**
     * Getter for property initialization.
     * @return Value of property initialization.
     */
    public TimeSeries getInitialization() {
        return this.initialization;
    }
    
    /**
     * Setter for property initialization.
     * @param initialization New value of property initialization.
     */
    public void setInitialization(TimeSeries initialization) {
        this.initialization = initialization;
    }
    
    /**
     * Getter for property historic.
     * @return Value of property historic.
     */
    public ArrayList<TimeSeries> getHistoric() {
        return this.historic;
    }
    
    /**
     * Setter for property historic.
     * @param historic New value of property historic.
     */
    public void setHistoric(ArrayList<TimeSeries> historic) {
        this.historic = historic;
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Getter for property forecasts.
     * @return Value of property forecasts.
     */
    public ArrayList<TimeSeries> getForecasts() {
        return this.forecasts;
    }    
    
    /**
     * Setter for property forecasts.
     * @param forecasts New value of property forecasts.
     */
    public void setForecasts(ArrayList<TimeSeries> forecasts) {
        this.forecasts = forecasts;
        if (forecasts != null) {
            int size = forecasts.size();
            this.stats = new ArrayList<EnsembleListLabel>(size);
            for (int i = 0; i < size; i++) {
                TimeSeries ts = (TimeSeries)(forecasts.get(i));
                this.stats.add(new EnsembleListLabel(ts, this));
            }
        }
    }
    
    /**
     * Getter for property input.
     * @return Value of property input.
     */
    public ArrayList<TimeSeries> getInput() {
        return this.input;
    }
    
    /**
     * Setter for property input.
     * @param input New value of property input.
     */
    public void setInput(ArrayList<TimeSeries> input) {
        this.input = input;
    }
    
    /**
     * Getter for property output.
     * @return Value of property output.
     */
    public ArrayList<TimeSeries> getOutput() {
        return this.output;
    }
    
    /**
     * Setter for property output.
     * @param output New value of property output.
     */
    public void setOutput(ArrayList<TimeSeries> output) {
        this.output = output;
    }
    
    
    public void setAnalysisPeriod (OuiCalendar start_date, OuiCalendar end_date) {
        PROCESS_trace(start_date, end_date);
    }
    
    /**
     * Getter for property stats.
     * @return Value of property stats.
     */
    public ArrayList getStats() {
        return this.stats;
    }
    
    /**
     * Getter for property statsInVolumeOrder.
     * @return Value of property statsInVolumeOrder.
     */
    public ArrayList<EnsembleListLabel> getStatsInVolumeOrder() {
        return this.statsInVolumeOrder;
    }
    
    /**
     * Getter for property statsInPeakOrder.
     * @return Value of property statsInPeakOrder.
     */
    public ArrayList<EnsembleListLabel> getStatsInPeakOrder() {
        return this.statsInPeakOrder;
    }
    
    //  Pass this one  the esp variable/index  
    public static EnsembleData load(String file_name, String espVarAndIndex) {
               EnsembleData ed = null;
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file_name);
            
            Node node = OuiProjectXml.selectSingleNode(doc, "/ESP");
            ed = new EnsembleData(OuiProjectXml.getElementContent(node, "@name", null), null, null, null);
            
            
/*
 *  Read ESP meta data from the xml file
 */            
            ed.setInitialization(TimeSeries.getTimeSeriesFromXmlNode(OuiProjectXml.selectSingleNode(doc, "/ESP/initialization/TimeSeries")));
//            initialization.dump();
            
            ed.setHistoric(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/historic")));
            ed.setInput(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/input")));
            ed.setOutput(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/output")));
            
            //  Hack on the variable name and index
            ArrayList<TimeSeries> output = ed.getOutput();
            Iterator<TimeSeries> it = output.iterator();
            while (it.hasNext()) {
                it.next().setDescription(espVarAndIndex);
            }
            
            ed.setForecasts(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/forecasts")));
/*
 *  Read the trace data
 */
            MmsOuiEspReader.readEsp(ed);
            
        } catch (Exception E) {
            System.err.println(E.getMessage());
        }
        
        return ed;
    }
    
    //  This one gets the esp variable/index from the xml file
    public static EnsembleData load (String file_name) {
        EnsembleData ed = null;
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file_name);
            
            Node node = OuiProjectXml.selectSingleNode(doc, "/ESP");
            ed = new EnsembleData(OuiProjectXml.getElementContent(node, "@name", null), null, null, null);
            
            
/*
 *  Read ESP meta data from the xml file
 */            
            ed.setInitialization(TimeSeries.getTimeSeriesFromXmlNode(OuiProjectXml.selectSingleNode(doc, "/ESP/initialization/TimeSeries")));
//            initialization.dump();
            
            ed.setHistoric(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/historic")));
            ed.setInput(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/input")));
            ed.setOutput(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/output")));
            ed.setForecasts(getArrayListFromXmlNode (OuiProjectXml.selectSingleNode(doc, "/ESP/forecasts")));
/*
 *  Read the trace data
 */
            MmsOuiEspReader.readEsp(ed);
            
        } catch (Exception E) {
            System.err.println(E.getMessage());
        }
        
        return ed;
    }
        
    public static ArrayList<TimeSeries> getArrayListFromXmlNode (Node node) {
        NodeList nl = OuiProjectXml.selectNodes(node, "TimeSeries");
        
        ArrayList<TimeSeries> al = new ArrayList<TimeSeries>(nl.getLength());
        for (int i = 0; i < nl.getLength(); i++) {
            TimeSeries ts = TimeSeries.getTimeSeriesFromXmlNode(nl.item(i));
//            ts.dump();
            al.add(i, ts);
        }
        
        return al;
    }
    
    public void save (String file_name) {
        PrintWriter out = null;
        
        System.out.println ("EnsembleData.save: writing file " + file_name);

        try {
            out = new PrintWriter(new FileWriter(file_name));
            
            out.println ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println ("<ESP name=\"" + this.name + "\">");
            
            out.println ("   <initialization>");
            out.println ("      " + initialization.getXmlBlock());
            out.println ("   </initialization>");
            
            writeArrayListBlock(out, "forecasts", forecasts);
            writeArrayListBlock(out, "historic", historic);
            writeArrayListBlock(out, "input", input);
            writeArrayListBlock(out, "output", output);
            
            out.println ("</ESP>");
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            if (out!= null) out.close();
        }
    }
    
    private void writeArrayListBlock(PrintWriter out, String name, ArrayList list) {
        out.println("   <" + name + ">");
        Iterator it = list.iterator();
        while (it.hasNext()) {
            TimeSeries ts = (TimeSeries)(it.next());
            out.println("      " + ts.getXmlBlock());
        }
        out.println("   </" + name + ">");
    }
    
    /**
     * Getter for property sortOrder.
     * @return Value of property sortOrder.
     */
    public int getSortOrder() {
        return this.sortOrder;
    }
    
    /**
     * Setter for property sortOrder.
     * @param sortOrder New value of property sortOrder.
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setCbhPrecip(ArrayList<TimeSeries> input) {
        this.cbhPrecip = input;
    }

    public ArrayList<TimeSeries> getCbhPrecip() {
        return this.cbhPrecip;
    }

    public void setCbhTmax(ArrayList<TimeSeries> cbhTmax) {
        this.cbhTmax = cbhTmax;
    }

    public void setCbhTmin(ArrayList<TimeSeries> cbhTmin) {
        this.cbhTmin = cbhTmin;
    }

    public ArrayList<TimeSeries> getCbhTmax() {
        return this.cbhTmax;
    }

    public ArrayList<TimeSeries> getCbhTmin() {
        return this.cbhTmin;
    }
}
