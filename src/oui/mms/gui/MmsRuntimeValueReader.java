/*
 * MmsRuntimeValueReader.java
 *
 * Created on August 26, 2005, 9:38 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package oui.mms.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RefineryUtilities;
import oui.mms.datatypes.OuiCalendar;

/**
 *
 * @author markstro
 */
public class MmsRuntimeValueReader extends Thread {
    
    int bufferSize = 365;
    
    private int numRuntimePlots;
    private Process process;
    private int[] numVarsEachPlot;
    private OuiCalendar start;
    private int traceLength;    
    private double x;
    private double[] y;
    int[] varPlotIndex;
    int[] varPlotTraceIndex;
    ArrayList<String> varNames;
    ArrayList<String> varIndex;
    Day now;
    MmsSingleRunGui gui; 
    private RuntimeGraph runTimeGraph;
    private Mms mms;

    public MmsRuntimeValueReader(Process process, MmsSingleRunGui srg, Mms mms) {
        this.process = process;
        this.gui = srg;
        this.mms = mms;
        
        try {
            bufferSize = Integer.parseInt (mms.getSingleControlValue("dispGraphsBuffSize"));
        } catch (Exception e) {
            bufferSize = 30;
            System.out.println("MmsRuntimeValuesReader: Make sure that dispGraphsBuffSize is set in the control file");
            e.printStackTrace();
        }
        
        ArrayList<String> s = mms.getControlValues("start_time");
        now = new Day(Integer.parseInt(s.get(2)), Integer.parseInt(s.get(1)), Integer.parseInt(s.get(0)));
        
        numRuntimePlots = Integer.parseInt (mms.getSingleControlValue("ndispGraphs"));
        numVarsEachPlot = new int[numRuntimePlots];
        for (int i = 0; i < numRuntimePlots; i++) numVarsEachPlot[i] = 0;
        
        varNames = mms.getControlValues(MmsRTGVariableSelector.nameControlString);
        varIndex = mms.getControlValues(MmsRTGVariableSelector.indexControlString);
        ArrayList<String> varPlot = mms.getControlValues(MmsRTGVariableSelector.plotControlString);
        
        varPlotIndex = new int[varPlot.size()];
        varPlotTraceIndex = new int[varPlot.size()];
            
        int old_plot_index = -1;
        int count = 0;
        for (int i = 0; i < varPlot.size(); i++) {
            varPlotIndex[i] = Integer.parseInt(varPlot.get(i)) - 1;
            if (old_plot_index != varPlotIndex[i]) {
                old_plot_index = varPlotIndex[i];
                count = 0;
            }
            varPlotTraceIndex[i] = count++;
        }
        
/*
 * Figure out the number of variables on each plot
 */
        for (int i = 0; i < varPlot.size(); i++) {
            numVarsEachPlot[varPlotIndex[i]]++;
        }
        y = new double[varNames.size()];
    }
    
    private boolean getDataForTimestep (BufferedReader in) throws IOException {
        if (isInterrupted()) return false;
        
        String line = in.readLine();
        while (line != null && !line.startsWith("plotRuntimeGraphValue")) line = in.readLine();
        if (line == null) return false;
        
        try {
            StringTokenizer token = new StringTokenizer(line);
            for(int i=0; i<3; i++)
                token.nextToken(); // ignore the first three words
            
            x = Double.parseDouble(token.nextToken());
            for(int i=0; i<y.length; i++) y[i] = Double.parseDouble(token.nextToken());
        } catch (NumberFormatException e) {
            System.out.println ("line = " + line);
            e.printStackTrace();
        }
        
        return true;
    }
    
    private RuntimeGraph plotSetup(BufferedReader in, int bufferSize) throws IOException {
        String[][] keyString = new String[numRuntimePlots][];
        for (int plot = 0; plot < numRuntimePlots; plot++) {
            keyString[plot] = new String[numVarsEachPlot[plot]];
        }
        
        for(int i = 0; i < varPlotIndex.length; i++) {
            keyString[varPlotIndex[i]][varPlotTraceIndex[i]] = varNames.get(i) +"[" + varIndex.get(i) + "]";
        }
        
        runTimeGraph = new RuntimeGraph("MMF Run Time Plots", keyString, bufferSize);
        runTimeGraph.pack();
        RefineryUtilities.centerFrameOnScreen(runTimeGraph);
//        demo.setVisible(true);
        return runTimeGraph;
    }
    
    private void plotUpdate(BufferedReader in, RuntimeGraph demo, int bufferSize) throws IOException {
        int count = 0;
        int tsCount = 0;
        boolean update_flag = false;
        
        OuiJFreeTimeSeries[] series = demo.getRuntimeGraphPanel().getSeries();
        
        while (getDataForTimestep(in)) {
            now = (Day)(now.next());
            if (count == bufferSize) {
                update_flag = true;
                count = 0;
            } else {
                update_flag = false;
                count++;
            }
            
            for (int i = 0; i < series.length; i++) {
                series[i].add(new TimeSeriesDataItem(now, y[i]), update_flag);
            }
            
            if (tsCount == bufferSize) runTimeGraph.setVisible(true);
            tsCount++;
        }
        
        for (int i = 0; i < series.length; i++) {
// TODO            series[i].ageHistoryCountItems();
            series[i].fireSeriesChanged();
        }

        gui.modelFinished();

    }
    
    public void run() {
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

            RuntimeGraph demo = plotSetup(in, bufferSize);
            plotUpdate(in, demo, bufferSize);

            process.getInputStream().close();
        } catch(IOException e) { e.printStackTrace(); }
        
        System.out.println ("MmsRuntimeValueReader: exiting thread");
    }
}
