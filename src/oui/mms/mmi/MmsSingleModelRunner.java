package oui.mms.mmi;

import oui.mms.datatypes.OuiCalendar;

/**
 *
 * @author  markstro
 */
public interface MmsSingleModelRunner {
    public void runModel(OuiCalendar queryStart, OuiCalendar queryEnd);
}
