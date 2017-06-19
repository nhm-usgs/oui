/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.mscb.paramfile2csv;

import gov.usgs.mscb.csvutils.CsvTableModelAdaptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsParamsReader;

/**
 *
 * @author markstro
 */
public class CsvFileFixer {

    private final String streamSegmentIdAttributeName = "GRID_CODE";

    // 3 Arguments are rnkfr_streams.csv hacked_up.csv ../../prmsParams/rnk_params_usfs
    // Workspace is E:/roanokeForestServiceTemp/roanokeFineShapefiles/rnkfr
    private CsvFileFixer(File origCsvFile, File fixedCsvFile, File prmsParameterFile) {
        try {
            CsvTableModelAdaptor origCsv = new CsvTableModelAdaptor(origCsvFile.getAbsolutePath());

            // Read the PRMS parameter file
            MmsParamsReader mpr = new MmsParamsReader(prmsParameterFile.toString());
            ParameterSet ps = mpr.read();
            Parameter toSegmentPrms = ps.getParameter("tosegment");


            // Look for rows with flow direction going the wrong direction.
            // Attribute T_ALT is greater than F_ALT means that it is flowing
            // wrong direction.
            int f_altColumnIndex = getIndexForColumnName(origCsv, "F_ALT");
            int t_altColumnIndex = getIndexForColumnName(origCsv, "T_ALT");
            int f_latColumnIndex = getIndexForColumnName(origCsv, "F_LAT");
            int t_latColumnIndex = getIndexForColumnName(origCsv, "T_LAT");
            int f_xColumnIndex = getIndexForColumnName(origCsv, "F_X");
            int t_xColumnIndex = getIndexForColumnName(origCsv, "T_X");
            int f_yColumnIndex = getIndexForColumnName(origCsv, "F_Y");
            int t_yColumnIndex = getIndexForColumnName(origCsv, "T_Y");
            int from_nodeColumnIndex = getIndexForColumnName(origCsv, "FROM_NODE");
            int to_nodeColumnIndex = getIndexForColumnName(origCsv, "TO_NODE");
            for (int r = 0; r < origCsv.getRowCount(); r++) {
                double f_alt = Double.parseDouble(origCsv.getValueAt(r,
                        f_altColumnIndex).toString());
                double t_alt = Double.parseDouble(origCsv.getValueAt(r,
                        t_altColumnIndex).toString());

                if (t_alt > f_alt) { // downstream side is higher, so the ends need to be switched.
//                    System.out.println ("switching direction of row " + r);
                    switchColumnValues(r, f_altColumnIndex, t_altColumnIndex, origCsv);
                    switchColumnValues(r, f_latColumnIndex, t_latColumnIndex, origCsv);
                    switchColumnValues(r, f_xColumnIndex, t_xColumnIndex, origCsv);
                    switchColumnValues(r, f_yColumnIndex, t_yColumnIndex, origCsv);
                    switchColumnValues(r, from_nodeColumnIndex, to_nodeColumnIndex, origCsv);
                }
            }

            // Values of attribute GRID_CODE should start at 1 and go to the
            // number of stream segments. Figure out how many stream segments.
            int streamSegmentIdColumnIndex = getIndexForColumnName(origCsv,
                    streamSegmentIdAttributeName);

            int max_GRID_CODE = -1;
            for (int r = 0; r < origCsv.getRowCount(); r++) {
                int grid_code = Integer.parseInt(origCsv.getValueAt(r,
                        streamSegmentIdColumnIndex).toString());
                max_GRID_CODE = Math.max(max_GRID_CODE, grid_code);
            }

            CsvTableModelAdaptor fixedCsv = new CsvTableModelAdaptor(origCsv.getHeaders(), max_GRID_CODE);

            if (max_GRID_CODE != origCsv.getRowCount()) { // there are more lines than there are segments

                // Look for rows with the same value of segment ID
                for (int i = 0; i < max_GRID_CODE; i++) {
                    boolean found = false;
                    boolean duplicate = false;
                    int origRow = -1;
                    for (int r = 0; r < origCsv.getRowCount(); r++) {
                        if (Integer.parseInt(origCsv.getValueAt(r,
                                streamSegmentIdColumnIndex).toString()) == (i + 1)) {
                            if (found == true) {
                                duplicate = true;
                            } else {
                                found = true;
                            }
                            origRow = r;
                        }
                    }

                    if (!duplicate) {
                        if (origRow == -1) { // This GRID_CODE is not included in the orig file! Does this mean that the IDs are not packed? Sure hope there are no HRUs connected to this segment.
                            for (int c = 0; c < origCsv.getColumnCount(); c++) {
                                if (i == 0) {
                                    origRow = i + 1;
                                } else {
                                    origRow = i - 1;
                                }
                                Object valueAt = origCsv.getValueAt(origRow, c);
                                fixedCsv.setValueAt(valueAt, i, c);
                            }
                            fixedCsv.setValueAt(("" + (i + 1)), i, streamSegmentIdColumnIndex);
                            System.out.println("GRID_CODE " + (i + 1) + " added");

                        } else {

                            for (int c = 0; c < origCsv.getColumnCount(); c++) {
                                Object valueAt = origCsv.getValueAt(origRow, c);
                                fixedCsv.setValueAt(valueAt, i, c);
                            }
                        }
                    } else {
                        origRow = figureOutWhichRowForGridCode(i, origCsv,
                                streamSegmentIdColumnIndex);
                        for (int c = 0; c < origCsv.getColumnCount(); c++) {
                            Object valueAt = origCsv.getValueAt(origRow, c);
                            fixedCsv.setValueAt(valueAt, i, c);
                        }
                        System.out.println("Duplicates for GRID_CODE " + (i + 1) + " removed");
                    }
                }

                // The TO_NODEs and FROM_NODEs can be totally hosed from the GIS.
                // RESET the FROM_NODE to the GRID_CODE
                int[] toSegement = (int[]) toSegmentPrms.getVals();

                for (int r = 0; r < fixedCsv.getRowCount(); r++) {
                    int gridCode = Integer.parseInt(fixedCsv.getValueAt(r, streamSegmentIdColumnIndex).toString());
                    fixedCsv.setValueAt("" + gridCode, r, from_nodeColumnIndex);
                    fixedCsv.setValueAt("" + toSegement[gridCode-1], r, to_nodeColumnIndex);
                }
            }




            // After everything is fixed, write out the "fixed" csv file
            fixedCsv.write(fixedCsvFile.getAbsolutePath());

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFileFixer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CsvFileFixer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        File origCsvFile = new File(args[0]);
        File fixedCsvFile = new File(args[1]);
        File prmsParameterFile = new File(args[2]);
        CsvFileFixer paramFile2Csv = new CsvFileFixer(origCsvFile, fixedCsvFile,
                prmsParameterFile);
    }

    private int getIndexForColumnName(CsvTableModelAdaptor csv, String columnName) {
        for (int c = 0; c < csv.getColumnCount(); c++) {
            if (columnName.equals(csv.getColumnName(c))) {
                return c;
            }
        }
        return -1;
    }

    private int figureOutWhichRowForGridCode(int i, CsvTableModelAdaptor csv, int c) {
        int bestRow = -1;
        double length = -1.0;
        int lengthColumnIndex = getIndexForColumnName(csv, "LENGTH");
        for (int r = 0; r < csv.getRowCount(); r++) {
            if (Integer.parseInt(csv.getValueAt(r, c).toString()) == (i + 1)) {
                double foo = Double.parseDouble(csv.getValueAt(r, lengthColumnIndex).toString());
                if (foo > length) {
                    length = foo;
                    bestRow = r;
                }
            }
        }

//        System.out.println ("figureOutWhichRowForGridCode GRID_CODE = " + (i+1) + " bestRow = " + bestRow);
        return bestRow;
    }

    private void switchColumnValues(int r, int f_altColumnIndex,
            int t_altColumnIndex, CsvTableModelAdaptor csv) {
        Object from = csv.getValueAt(r, f_altColumnIndex);
        csv.setValueAt(csv.getValueAt(r, t_altColumnIndex), r, f_altColumnIndex);
        csv.setValueAt(from, r, t_altColumnIndex);
    }
}
