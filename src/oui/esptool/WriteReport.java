package oui.esptool;

import gov.usgs.cawsc.gui.WindowFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.OuiCalendar;
import oui.util.ReportPanel;

/**
 */
public class WriteReport {
    EspTool parent;
    
    public WriteReport(EspTool top) {
        parent = top;
//        Format f1 = new Format("%11.1f");
//        Format f2 = new Format("%3i");
//        Format f3 = new Format("%8.1f");
//        Format f4 = new Format("%4.1f");
        int vol_rank, peak_rank;
        EnsembleData ed = top.getEnsembleData();
        ArrayList stats = ed.getStats();
        OuiCalendar mdt = new OuiCalendar();
        ReportPanel reportPanel = new ReportPanel();
        WindowFactory.displayInFrame(reportPanel, "ESP Report");
        Writer out = reportPanel.getWriter();
        
        try {
            out.write(ed.getName() + "\n");
            out.write("        Analysis Period: " + top.getAnalysisStart() + " to " + top.getAnalysisEnd() + "\n");
            out.write("  Initialization Period: " + ed.getInitializationStart() + " to " + ed.getInitializationEnd() + "\n");
            out.write("        Forecast Period: " + ed.getForecastStart() + " to " + ed.getForecastEnd() + "\n\n\n");
            /*
             **  Write the report for all traces.
             */
            out.write("                        Summary of All Traces for Analysis Period\n\n");
            out.write("Historic   Volume      Volume    Volume    Volume    Peak   Peak    Peak\n");
            out.write("  Year    (cfs-days)  (acre-ft)   Rank   Exceedance  (cfs)  Rank  Exceedance   Date of Peak\n");
            out.write("--------  ----------  ---------  ------  ----------  ----   ----  ----------   ------------\n");

            Iterator it = stats.iterator();
            while (it.hasNext()) {
                EnsembleListLabel ell = (EnsembleListLabel) (it.next());
                mdt.setJulian(ell.getTimeToPeak());

                String s = String.format("  %1$4d  %2$11.1f%3$11.1f    %4$3d       %5$4.1f  %6$8.1f  %7$3d      %8$4.1f       ",
                        ell.getTraceYear(),
                        ell.getTraceVolume(), ell.getTraceVolume() * 1.9835,
                        ell.getVolumeRank(), ell.getActVolumeProb(),
                        ell.getTracePeak(),
                        ell.getPeakRank(),
                        ell.getActPeakProb());
                out.write(s + mdt + "\n");
            }
/*
 **  Write the report for selected traces.
 */
            Object[] sel = top.getTraceListList().getSelectedValues();
            
            ArrayList<EnsembleListLabel> selStatsInVolumeOrder = new ArrayList(sel.length);
            ArrayList<EnsembleListLabel> selStatsInPeakOrder = new ArrayList(sel.length);
            ArrayList<EnsembleListLabel> selYearOrder = new ArrayList(sel.length);
/*
 *  Make copies
 */
            for (int i = 0; i < sel.length; i++) {
                EnsembleListLabel tsi = new EnsembleListLabel((EnsembleListLabel)(sel[i]));
                selStatsInVolumeOrder.add(i, tsi);
                selStatsInPeakOrder.add(i, tsi);
                selYearOrder.add(i, tsi);
            }
            
            EnsembleData.Sort(selStatsInVolumeOrder, selStatsInPeakOrder);
            
            ArrayList order;
            
            if (sel.length > 0) {
                if (ed.getSortOrder() == EnsembleData.VOLUME) {
                    order = selStatsInVolumeOrder;
                    out.write("\n\n\n               Summary of Selected Traces for Analysis Period (by Volume)\n\n");
                    
                } else if (ed.getSortOrder() == EnsembleData.PEAK) {
                    order = selStatsInPeakOrder;
                    out.write("\n\n\n              Summary of Selected Traces for Analysis Period (by Peak)\n\n");
                    
                } else {
                    order = selYearOrder;
                    out.write("\n\n\n              Summary of Selected Traces for Analysis Period (by Year)\n\n");
                }
                
                out.write("Historic   Volume      Volume    Volume    Volume    Peak   Peak    Peak\n");
                out.write("  Year    (cfs-days)  (acre-ft)   Rank   Exceedance  (cfs)  Rank  Exceedance   Date of Peak\n");
                out.write("--------  ----------  ---------  ------  ----------  ----   ----  ----------   ------------\n");
                
/*
 **  Sort peak (if report by volume) or volume (if report by peak) of
 **  the selected traces.
 */
                it = order.iterator();
                while (it.hasNext()) {
                    EnsembleListLabel ell = (EnsembleListLabel) (it.next());
                    mdt.setJulian(ell.getTimeToPeak());
                    String s = String.format("  %1$4d  %2$11.1f%3$11.1f    %4$3d       %5$4.1f  %6$8.1f  %7$3d      %8$4.1f       ",
                            ell.getTraceYear(),
                            ell.getTraceVolume(), ell.getTraceVolume() * 1.9835,
                            ell.getVolumeRank(), ell.getActVolumeProb(),
                            ell.getTracePeak(),
                            ell.getPeakRank(),
                            ell.getActPeakProb());
                    out.write(s + mdt + "\n");
                }
            }
            out.flush();
            out.close();
            
        } catch (IOException e) {}
    }

    // TODO this is a hack.  The stuff above should be merged with this.
    public static void write(File xmlFile, OuiCalendar analysisStart, OuiCalendar analysisEnd, int sortInt) {
//        Format f1 = new Format("%11.1f");
//        Format f2 = new Format("%3i");
//        Format f3 = new Format("%8.1f");
//        Format f4 = new Format("%4.1f");
        OuiCalendar mdt = new OuiCalendar();
        
        File reportFile = new File(xmlFile.getParentFile(), xmlFile.getName().replace(".xml", ".rpt"));
        EnsembleData ed = EnsembleData.load(xmlFile.getAbsolutePath());
        ed.setSortOrder (sortInt);
        ed.setAnalysisPeriod(analysisStart, analysisEnd);
        ArrayList stats = ed.getStats();

        try {
            Writer out = new FileWriter(reportFile);
                out.write(ed.getName() + "\n");
                out.write("       Analysis Period: " + analysisStart + " to " + analysisEnd + "\n");
                out.write("  Initialization Period: " + ed.getInitializationStart() + " to " + ed.getInitializationEnd() + "\n");
                out.write("        Forecast Period: " + ed.getForecastStart() + " to " + ed.getForecastEnd() + "\n\n\n");
                /*
                **  Write the report for all traces.
                */
                out.write("                        Summary of All Traces for Analysis Period\n\n");
                out.write("Historic   Volume      Volume    Volume    Volume    Peak   Peak    Peak\n");
                out.write("  Year    (cfs-days)  (acre-ft)   Rank   Exceedance  (cfs)  Rank  Exceedance   Date of Peak\n");
                out.write("--------  ----------  ---------  ------  ----------  ----   ----  ----------   ------------\n");
                
                Iterator it = stats.iterator();
                while (it.hasNext()) {
                    EnsembleListLabel ell = (EnsembleListLabel)(it.next());
                    
                    mdt.setJulian(ell.getTimeToPeak());
                    String s = String.format("  %1$4d  %2$11.1f%3$11.1f    %4$3d       %5$4.1f  %6$8.1f  %7$3d      %8$4.1f       ",
                            ell.getTraceYear(),
                            ell.getTraceVolume(), ell.getTraceVolume() * 1.9835,
                            ell.getVolumeRank(), ell.getActVolumeProb(),
                            ell.getTracePeak(),
                            ell.getPeakRank(),
                            ell.getActPeakProb());
                    out.write(s + mdt + "\n");
                }
            
        } catch (IOException e) {

        }
    }
}


