package oui.mms.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.OuiCalendar;


public class MmsStateFileReader {
    private final String fileName;
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
        
        if (this.stateDate == null) {
            this.stateDate = OuiCalendar.getInstance();
        }
        
        try {
            in = new BufferedReader(new FileReader(fileName));
            
            line = in.readLine();
            line = in.readLine();
            line = in.readLine();
            
            StringTokenizer st = new StringTokenizer(line, " ");
            st.nextToken();
            st.nextToken();
            st.nextToken();
            
            double jul_date = Double.parseDouble(st.nextToken());
            
            this.stateDate.setJulian(jul_date);
            

            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MmsStateFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MmsStateFileReader.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in!= null) in.close();
            }  catch (IOException E) {}
        }
    }
}