/*
 * OuiJFreeTimeSeries.java
 *
 * Created on October 17, 2005, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package oui.mms.gui;

import java.util.Collections;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

/**
 *
 * @author markstro
 */
public class OuiJFreeTimeSeries extends TimeSeries {
    
    public OuiJFreeTimeSeries(String name) {
        super(name);
    }

    public OuiJFreeTimeSeries(String name, Class timePeriodClass) {
        super(name, timePeriodClass);
    }

    public OuiJFreeTimeSeries(String name, String domain, String range, Class timePeriodClass) {
        super (name, domain, range, timePeriodClass);
    }   
    
    public void add(TimeSeriesDataItem item, boolean update_flag) {
        
        if (item == null) {
            throw new IllegalArgumentException("Null 'item' argument.");
        }
        if (!item.getPeriod().getClass().equals(this.timePeriodClass)) {
            StringBuffer b = new StringBuffer();
            b.append("You are trying to add data where the time period class ");
            b.append("is ");
            b.append(item.getPeriod().getClass().getName());
            b.append(", but the TimeSeries is expecting an instance of ");
            b.append(this.timePeriodClass.getName());
            b.append(".");
            throw new SeriesException(b.toString());
        }
        
        // make the change (if it's not a duplicate time period)...
        boolean added = false;
        int count = getItemCount();
        if (count == 0) {
            this.data.add(item);
            added = true;
        } else {
            RegularTimePeriod last = getTimePeriod(getItemCount() - 1);
            if (item.getPeriod().compareTo(last) > 0) {
                this.data.add(item);
                added = true;
            } else {
                int index = Collections.binarySearch(this.data, item);
                if (index < 0) {
                    this.data.add(-index - 1, item);
                    added = true;
                } else {
                    StringBuffer b = new StringBuffer();
                    b.append("You are attempting to add an observation for ");
                    b.append("the time period ");
                    b.append(item.getPeriod().toString());
                    b.append(" but the series already contains an observation");
                    b.append(" for that time period. Duplicates are not ");
                    b.append("permitted.  Try using the addOrUpdate() method.");
                    throw new SeriesException(b.toString());
                }
            }
        }
        
        if (added && update_flag) {
            // check if this addition will exceed the maximum item count...
            if (getItemCount() > getMaximumItemCount()) {
                this.data.remove(0);
            }
            
            // check if there are any values earlier than specified by the
            // history count...
// TODO           ageHistoryCountItems();
            fireSeriesChanged();
        }
        
    }
}
