package oui.mms.dmi;

import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.OuiCalendar;

public interface EspOutputDmi {
    public void runDmi(OuiCalendar dmiStart, OuiCalendar dmiEnd, String[] years, String[] probs, String espStationName);
}

