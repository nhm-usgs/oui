/*
 * TimeSeries.java
 *
 * Created on November 19, 2004, 8:23 AM
 */

package oui.mms.datatypes;

import org.w3c.dom.Node;
import oui.mms.OuiProjectXml;

/**
 *
 * @author  markstro
 */
public class TimeSeries {
    private double[] dates;
    private double[] vals;
    private OuiCalendar start;
    private OuiCalendar end;
    private String description;
    private String name;
    private String source;
    private String units;
    
    /** Creates a new instance of TimeSeries */
    public TimeSeries() {
    }
        
    public TimeSeries(String name, double[] dates, double[] vals,
            OuiCalendar start, OuiCalendar end, String description, String source, String units) {
        this.name = name;
        this.dates = dates;
        this.vals = vals;
        this.start = start;
        this.end = end;
        this.description = description;
        this.source = source;
        this.units = units;
    }
 
    public TimeSeries(String name, double[] dates, double[] vals) {
        this.name = name;
        this.dates = dates;
        this.vals = vals;
        setDates(dates);
        this.description = name;
        this.source = "unknown";
        this.units = "unknown";
    }
        
    public double[] getDates() {
        return dates;
    }
    
    public final void setDates(double[] dates) {
        this.dates = dates;
        this.start = OuiCalendar.getInstance();
        start.setJulian(dates[0]);
        this.end = OuiCalendar.getInstance();
        end.setJulian(dates[dates.length - 1]);
    }
    
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public OuiCalendar getEnd() {
        return end;
    }
    public void setEnd(OuiCalendar end) {
        end.getMillis();
        this.end = end;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public OuiCalendar getStart() {
        return start;
    }
    public void setStart(OuiCalendar start) {
        start.getMillis();
        this.start = start;
    }
    public String getUnits() {
        return units;
    }
    public void setUnits(String units) {
        this.units = units;
    }
    public double[] getVals() {
        return vals;
    }
    public void setVals(double[] vals) {
        this.vals = vals;
    }
    @Override
    public String toString() {
        return name;
    }
    
    public String getXmlBlock() {
        return "<TimeSeries name=\"" + name + "\" description=\"" + description + "\" source=\"" + 
                        source + "\" units=\"" + units + "\" start =\"" + start.getJulian() + "\" end =\"" +
                        end.getJulian() + "\"/>";
    }

    public void dump () {
        System.out.println ("TimeSeries name = " + name);
        System.out.println ("    description = " + description);
        System.out.println ("         source = " + source);
        System.out.println ("          units = " + units);
        System.out.println ("          start = " + start);
        System.out.println ("            end = " + end);
    }
    
    public static TimeSeries getTimeSeriesFromXmlNode(Node node) {
        TimeSeries ts = new TimeSeries();
        
        ts.setName(OuiProjectXml.getElementContent(node, "@name", null));
        ts.setDescription(OuiProjectXml.getElementContent(node, "@description", null));
        ts.setSource(OuiProjectXml.getElementContent(node, "@source", null));
        ts.setUnits(OuiProjectXml.getElementContent(node, "@units", null));

        OuiCalendar start = OuiCalendar.getInstance();
        start.setJulian(Double.parseDouble(OuiProjectXml.getElementContent(node, "@start", null)));
        ts.setStart(start);

        OuiCalendar end = OuiCalendar.getInstance();
        end.setJulian(Double.parseDouble(OuiProjectXml.getElementContent(node, "@end", null)));
        ts.setEnd(end);

        return ts;
    }
    
    public void trim(OuiCalendar start, OuiCalendar end) {
        double start_jd = start.getJulian();
        double end_jd = end.getJulian();
        
        int count = 0;
        for (int i = 0; i < dates.length; i++) {
            if ((dates[i] >= start_jd) && (dates[i] <= end_jd)) {
                count++;
            }
        }
        
        double[] new_dates = new double[count];
        double[] new_data = new double[count];
        
        count = 0;
        for (int i = 0; i < dates.length; i++) {
            if ((dates[i] >= start_jd) && (dates[i] <= end_jd)) {
                new_dates[count] = dates[i];
                new_data[count] = vals[i];
                count++;
            }
        }
        
        dates = new_dates;
        vals = new_data;
        this.start = OuiCalendar.getInstance();
        start.setJulian(dates[0]);
        this.end = OuiCalendar.getInstance();
        end.setJulian(dates[count -1]);
    }
}
