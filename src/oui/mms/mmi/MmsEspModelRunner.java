package oui.mms.mmi;

import oui.mms.datatypes.OuiCalendar;

/**
 *
 * @author  markstro
 */
public interface MmsEspModelRunner {
    public void runModel(OuiCalendar queryStart, OuiCalendar queryEnd);
}
