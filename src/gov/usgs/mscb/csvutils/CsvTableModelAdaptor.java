/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.usgs.mscb.csvutils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author markstro
 */
public class CsvTableModelAdaptor extends AbstractTableModel {
    String[] headers;
    Object[][] data;
    int rowCount;

    // This constructor reads a CSV file and puts the data into a TableModel
    // data structure.
    public CsvTableModelAdaptor(String csvFile) throws FileNotFoundException, IOException {
        this(csvFile,',');
    }
    
    public CsvTableModelAdaptor(String csvFile, char delimiter) throws FileNotFoundException, IOException {

        // Read the header line
        CsvReader csvRead = new CsvReader(csvFile, delimiter);
        csvRead.readHeaders();
        headers = csvRead.getHeaders();

        // Figure out how many rows
        rowCount = 0;
        while (csvRead.readRecord()) {
            rowCount++;
        }
        csvRead.close();

        // Data values go into a 2D array of Objects. Data accessed like this:
        //  data[rowIndex][colIndex];
        data = new Object[rowCount][headers.length];

        csvRead = new CsvReader(csvFile, delimiter);
        csvRead.readHeaders();

        String[] recordVals;
        int r = 0;
        while (csvRead.readRecord()) {
            recordVals = csvRead.getValues();
            System.arraycopy(recordVals, 0, data[r], 0, headers.length);
//            System.out.println ("CsvTableModelAdaptor r = " + r + " date = " + recordVals[0]);
            r++;
        }
        csvRead.close();
    }

    // This constructor is used to make a new (bigger) TableModel
    // from an old one when new rows are added, but the rest of the
    // data is the same as in the original one.
    public CsvTableModelAdaptor(TableModel inputCsv, int rowCount) {
        this.rowCount = rowCount;

        headers = new String[inputCsv.getColumnCount()];
        for (int c = 0; c < inputCsv.getColumnCount(); c++) {
            headers[c] = inputCsv.getColumnName(c);
        }

        // allocate enough space for the new data
        // copy the old data
        data = new Object[rowCount][headers.length];
        for (int r = 0; r < inputCsv.getRowCount(); r++) {
            for (int c = 0; c < inputCsv.getColumnCount(); c++) {
                data[r][c] = inputCsv.getValueAt(r, c);
            }
        }
    }
    
    // This is used to make a new empty table
    public CsvTableModelAdaptor (String[] headers, int rowCount) {
            this.headers = headers;
            this.rowCount = rowCount;
            this.data = new Object[rowCount][headers.length];
    }
            
    public String[] getHeaders () {
        return headers;
    }
    
    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return headers[columnIndex];
    }

    @Override
    public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
        data[rowIndex][columnIndex] = aValue;
    }

    public void write (String outFileName) {
        try {
            CsvWriter writer = new CsvWriter(outFileName, ',', Charset.forName("ISO-8859-1"));
            writer.writeRecord(headers);

            String[] vals = new String[getColumnCount()];
            for (int r = 0; r < getRowCount(); r++) {
                for (int c = 0; c < getColumnCount(); c++) {
                    vals[c] = (String) getValueAt(r, c);
                }
                writer.writeRecord(vals);
            }
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(CsvTableModelAdaptor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getColumnIndexForName(String colName) {
        for (int c = 0; c < getColumnCount(); c++) {
            if (getColumnName(c).equals(colName)) {
                return c;
            }
        }
        return -1;
    }
}
