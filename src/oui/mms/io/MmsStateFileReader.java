package oui.mms.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import oui.mms.datatypes.OuiCalendar;


public class MmsStateFileReader {
    private String fileName;
    OuiCalendar stateDate = null;
    
    public MmsStateFileReader(String fn) {
        fileName = fn;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public OuiCalendar getStateDate() {
        if (this.stateDate == null) readDate();
        return this.stateDate;
    }
    
    private void readDate() {
        BufferedReader in = null;
        String line;
        
        if (this.stateDate == null) this.stateDate = new OuiCalendar();
        
        try {
            in = new BufferedReader(new FileReader(fileName));
            
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            
            StringTokenizer st = new StringTokenizer(line, " ");
            st.nextToken();
            st.nextToken();
            st.nextToken();
            
            double jul_date = Double.valueOf(st.nextToken()).doubleValue();
            
            this.stateDate.setJulian(jul_date);
            
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }
    }
}


