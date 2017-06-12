/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.mscb.paramfile2csv;

import csvutils.CsvTableModelAdaptor;
import csvutils.CsvWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author markstro
 */
public class SntempParametersFromPrms {

    // Extract the HRUWEST, HRUEAST, WVDENSUM, WVDENWIN, EVDENSUM, and EVDENWIN
    // from the PRMS HRU parameters and write them out by stream segment so that
    // they can be used as input to SNTemp as P2S input files.
    double[] localArea;
    double[] totalContributingArea;

    SntempParametersFromPrms(File destDir) {
        try {
            File hruCsvFile = new File(destDir, "nhru.csv");
            CsvTableModelAdaptor hruCsv = new CsvTableModelAdaptor(hruCsvFile.toString());

            File streamSegmentCsvFile = new File(destDir, "nsegment.csv");
            CsvTableModelAdaptor streamSegmentCsv = new CsvTableModelAdaptor(streamSegmentCsvFile.toString());

            int numberOfSegments = streamSegmentCsv.getRowCount();

//            System.out.println("numberOfSegments = " + numberOfSegments);
            int[] HRUWEST = new int[numberOfSegments];
            int[] HRUEAST = new int[numberOfSegments];
            double[] WVDENSUM = new double[numberOfSegments];
            double[] WVDENWIN = new double[numberOfSegments];
            double[] EVDENSUM = new double[numberOfSegments];
            double[] EVDENWIN = new double[numberOfSegments];
            localArea = new double[numberOfSegments];
            totalContributingArea = new double[numberOfSegments];
            int[] WVEGTYPE = new int[numberOfSegments];
            int[] EVEGTYPE = new int[numberOfSegments];

            int hruSegementColNum = hruCsv.findColumn("hru_segment");
            int hruAreaColNum = hruCsv.findColumn("hru_area");

            for (int seg = 0; seg < numberOfSegments; seg++) {
                boolean westFound = false;
                boolean eastFound = false;

                for (int i = 0; i < hruCsv.getRowCount(); i++) {
                    int segment = seg + 1;
                    int foo = Integer.parseInt(hruCsv.getValueAt(i, hruSegementColNum).toString());
                    if (segment == foo) {
                        if (!westFound) {
                            HRUWEST[seg] = (i + 1);

                            double fool = Double.parseDouble(hruCsv.getValueAt(i, hruAreaColNum).toString());
                            localArea[seg] = fool;


                                    
                            westFound = true;

                        } else if (!eastFound) {
                            HRUEAST[seg] = (i + 1);

                            double fool = Double.parseDouble(hruCsv.getValueAt(i, hruAreaColNum).toString());
                            localArea[seg] = localArea[seg] + fool;

                            
                            eastFound = true;

                        } else {  // more than 2 HRUs are connected to this stream segment
                            double fool = Double.parseDouble(hruCsv.getValueAt(i, hruAreaColNum).toString());
                            localArea[seg] = localArea[seg] + fool;
                        }
                    }
                }

//                System.out.println ("segment = " + (seg+1) + " west HRU = "
//                        + HRUWEST[seg] + " east HRU = " + HRUEAST[seg]);
            }

            int covdenSumColNum = hruCsv.findColumn("covden_sum");
            int covdenWinColNum = hruCsv.findColumn("covden_win");
            int hruVegTypeColNum = hruCsv.findColumn("cov_type");
            for (int seg = 0; seg < numberOfSegments; seg++) {
                EVEGTYPE[seg] = 0;
                WVEGTYPE[seg] = 0;
                
                if (HRUWEST[seg] > 0) {
                    int hruIndex = HRUWEST[seg] - 1;
                    double foo = Double.parseDouble(hruCsv.getValueAt(hruIndex, covdenSumColNum).toString());
                    WVDENSUM[seg] = foo;

                    foo = Double.parseDouble(hruCsv.getValueAt(hruIndex, covdenWinColNum).toString());
                    WVDENWIN[seg] = foo;

                    int foo1 = Integer.parseInt(hruCsv.getValueAt(hruIndex, hruVegTypeColNum).toString());
                    WVEGTYPE[seg] = foo1;
                }

                if (HRUEAST[seg] > 0) {
                    int hruIndex = HRUEAST[seg] - 1;
                    double foo = Double.parseDouble(hruCsv.getValueAt(hruIndex, covdenSumColNum).toString());
                    EVDENSUM[seg] = foo;

                    foo = Double.parseDouble(hruCsv.getValueAt(hruIndex, covdenWinColNum).toString());
                    EVDENWIN[seg] = foo;

                    int foo1 = Integer.parseInt(hruCsv.getValueAt(hruIndex, hruVegTypeColNum).toString());
                    EVEGTYPE[seg] = foo1;
                }
            }

            // Figure out accumulated contributing area for each segment.
            ArrayList<Segment> segments = new ArrayList(numberOfSegments);
            ArrayList<Segment> outlets = new ArrayList();
            for (int seg = 0; seg < numberOfSegments; seg++) {
                segments.add(seg, new Segment(seg));
            }

            int toSegmentColNum = streamSegmentCsv.findColumn("tosegment");
            for (int i = 0; i < streamSegmentCsv.getRowCount(); i++) {
                int tosegment = Integer.parseInt(streamSegmentCsv.getValueAt(i, toSegmentColNum).toString());
//                System.out.println("Stream segment " + (i + 1) + " connects to segment " + tosegment);

                if (tosegment > 0) {
                    segments.get(tosegment - 1).addUpStreamSegment(segments.get(i));
//                    System.out.println ("Adding " + i + " as upstream segment to " + (tosegment-1));
                } else {
                    outlets.add(segments.get(i));
                }
            }

            outlets.stream().forEach((seg) -> {
                computeTotalContributingArea(seg);
            });

            String fileName = "SNTemp.csv";
            File outFile = new File(destDir, fileName);
            CsvWriter writer = new CsvWriter(outFile.toString(), ',', Charset.forName("ISO-8859-1"));

            // These are the parameter names for this dimension
            String[] headers = {"nsegment_ID", "HRUWEST", "HRUEAST",
                "WVDENSUM", "WVDENWIN", "WVEGTYPE",
                "EVDENSUM", "EVDENWIN", "EVEGTYPE",
                "localArea", "totalArea"
            };

            writer.writeRecord(headers);

            String[] vals = new String[headers.length];
            for (int r = 0; r < numberOfSegments; r++) {
                vals[0] = "" + (r+1); // segment number
                vals[1] = "" + HRUWEST[r];
                vals[2] = "" + HRUEAST[r];
                vals[3] = "" + WVDENSUM[r];
                vals[4] = "" + WVDENWIN[r];
                vals[5] = "" + WVEGTYPE[r];
                vals[6] = "" + EVDENSUM[r];
                vals[7] = "" + EVDENWIN[r];
                vals[8] = "" + EVEGTYPE[r];
                vals[9] = "" + localArea[r];
                vals[10] = "" + totalContributingArea[r];

                // write column by column
                writer.writeRecord(vals);

            }

            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SntempParametersFromPrms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SntempParametersFromPrms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void computeTotalContributingArea(Segment seg) {
        int segId = seg.getId();

        System.out.println ("started calculation of segment " + segId);
        
        int size = seg.getUpStreamSegments().size();

        for (int i = 0; i < seg.getUpStreamSegments().size(); i++) {
            Segment upseg = seg.getUpStreamSegments().get(i);
            int upsegId = upseg.getId();

            computeTotalContributingArea(upseg);
            totalContributingArea[segId] = totalContributingArea[segId] + totalContributingArea[upsegId];
//            System.out.println ("   adding upstream area " + totalContributingArea[upsegId] + " from " + upsegId + " to the area " + seg.getTotalArea() + " of segment " + seg.getId());
        }

//        System.out.println ("   adding local area " + localArea[seg.getId()] + "  to segment " + seg.getId() + " current upstream area " + seg.getTotalArea());
        totalContributingArea[segId] = totalContributingArea[segId] + localArea[segId];
//        System.out.println ("finished calculation of segment " + seg.toString() + " " + seg.getTotalArea());
    }

    private static class Segment {
        private final ArrayList<Segment> upStreamSegments;
        private final int id;

        public Segment(int id) {
            this.id = id;
            this.upStreamSegments = new ArrayList(0);
        }

        public void addUpStreamSegment(Segment upStreamSeg) {
            upStreamSegments.add(upStreamSeg);
        }

        public ArrayList<Segment> getUpStreamSegments() {
            return upStreamSegments;
        }
        
        public int getId() {
            return id;
        }
        
        @Override
        public String toString () {
            return "" + id;
        }
    }
}
