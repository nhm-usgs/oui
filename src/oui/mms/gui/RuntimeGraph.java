
package oui.mms.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.Range;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
//import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.util.UnitType;

public class RuntimeGraph extends JFrame {

    static class RuntimeGraphPanel extends JPanel {

        /** The number of subplots. */
        private int subplotCount;

        /** The datasets. */
        private TimeSeriesCollection[] datasets;

        private OuiJFreeTimeSeries[] series;

        public OuiJFreeTimeSeries[] getSeries () {return (series);}

        public CombinedDomainXYPlot getPlot () {return (plot);}
        private CombinedDomainXYPlot plot;

        /**
         * Creates a new self-contained demo panel.
         */
        public RuntimeGraphPanel(String[][] varNames) {
            super(new BorderLayout());

            subplotCount = varNames.length;

            plot = new CombinedDomainXYPlot(new DateAxis("Time"));
            plot.setGap(plot.getGap() * 2.0);
            this.datasets = new TimeSeriesCollection[subplotCount];

            int seriesCount = 0;
            for (int i = 0; i < subplotCount; i++) seriesCount =  seriesCount + varNames[i].length;
            series = new OuiJFreeTimeSeries[seriesCount];

            int k = 0;
            for (int i = 0; i < subplotCount; i++) {
                this.datasets[i] = new TimeSeriesCollection(null, TimeZone.getDefault());

                for (int j = 0; j < varNames[i].length; j++) {
                    series[k] = new OuiJFreeTimeSeries(varNames[i][j], Day.class);
                    this.datasets[i].addSeries(series[k++]);
                }

                NumberAxis rangeAxis = new NumberAxis(null);
                rangeAxis.setAutoRangeIncludesZero(false);
                XYPlot subplot = new XYPlot(
                        this.datasets[i], null, rangeAxis,
                        new StandardXYItemRenderer()
                        );
                subplot.setBackgroundPaint(Color.lightGray);
                subplot.setDomainGridlinePaint(Color.white);
                subplot.setRangeGridlinePaint(Color.white);
                plot.add(subplot);
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            JFreeChart chart = new JFreeChart("Run Time: " + df.format(new Date(System.currentTimeMillis())), plot);
            LegendTitle legend = (LegendTitle) chart.getSubtitle(0);
            legend.setPosition(RectangleEdge.RIGHT);
            legend.setMargin(
                    new RectangleInsets(UnitType.ABSOLUTE, 0, 4, 0, 4)
                    );
            chart.setBorderPaint(Color.black);
            chart.setBorderVisible(true);
            chart.setBackgroundPaint(Color.white);

            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);
            plot.setAxisOffset(new RectangleInsets(4, 400, 4, 4));
//            ValueAxis axis = plot.getDomainAxis();
//            axis.setAutoRange(true);
//            axis.setFixedAutoRange(60000.0);  // 60 seconds
//            axis.setFixedAutoRange(365.0);  // 60 seconds

            ChartPanel chartPanel = new ChartPanel(chart);
            add(chartPanel);

            chartPanel.setPreferredSize(new java.awt.Dimension(500, 470));
            chartPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
    }

    /**
     * Constructs a new demonstration application.
     *
     * @param title  the frame title.
     */
    RuntimeGraphPanel dp;
    private int bufferSize = 1;

    public RuntimeGraph(String title, String[][] varNames, int bufferSize) {
        this(title, varNames);
        this.bufferSize = bufferSize;
    }

    public RuntimeGraph(String title, String[][] varNames) {
        super(title);
        dp = new RuntimeGraphPanel(varNames);
        setContentPane(dp);
    }

    public RuntimeGraphPanel getRuntimeGraphPanel() {return dp;}

    public void testPlotUpdate(int bufferSize) {
        RuntimeGraphPanel dp = getRuntimeGraphPanel();
        Day now = new Day();
        System.out.println("Now = " + now.toString());

        int plotCount = dp.datasets.length;
        int[] seriesCounts = new int[plotCount];
        for (int i = 0; i < plotCount; i++) seriesCounts[i] = dp.datasets[i].getSeriesCount();

        int count = 0;
        boolean update_flag = false;
        for (int iday = 0; iday < 365; iday++) {
            now = (Day)(now.next());
            if (count == bufferSize) {
                update_flag = true;
                count = 0;
            } else {
                update_flag = false;
                count++;
            }
            for (int i = 0; i < dp.series.length; i++) {
                if (i > 1) {
                dp.series[i].add(new TimeSeriesDataItem(now, (0.90 + 0.2 * Math.random()) * 100000000.0), update_flag);
                }else {
                dp.series[i].add(new TimeSeriesDataItem(now, (0.90 + 0.2 * Math.random())), update_flag);
                }
            }
            if (bufferSize == iday) setVisible(true);
        }
        System.out.println("done");
    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

        String [][] varNames = {{"basin_cfs", "runoff"},
        {"ssr_flow", "sro", "gwflow"},
        {"basin_pw_equiv"},
        {"basin_pet", "basin_aet"}
        };

        int bufferSize = 14;

        RuntimeGraph demo = new RuntimeGraph("MMF Run Time Plots", varNames, bufferSize);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
//        demo.setVisible(true);

        demo.testPlotUpdate(bufferSize);

    }
}
