package oui.mms.datatypes;

//import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class OuiCalendar extends GregorianCalendar {

    private double juldate = -1;

    private OuiCalendar() {
        super(TimeZone.getTimeZone("UTC"), Locale.getDefault());
    }

    static public OuiCalendar getInstance() {
        OuiCalendar oc = new OuiCalendar();
        oc.juldate = -1;
        oc.clear();
        return oc;
    }

    static public OuiCalendar getClone(OuiCalendar oc) {
        OuiCalendar oc1 = OuiCalendar.getInstance();
        long millis = oc.getTimeInMillis();
        oc1.setTimeInMillis(millis);

        // Need to do this to make sure that the internal state is updated.
        oc1.getTimeInMillis();
        oc1.getJulian();

        return oc1;
    }

    private void setYMD(int y, int m, int d, int h, int mi, int s, int ms) {
        clear();
        set(Calendar.YEAR, y);
        set(Calendar.MONTH, (m - 1));
        set(Calendar.DAY_OF_MONTH, d);
        set(Calendar.HOUR_OF_DAY, h);
        set(Calendar.MINUTE, mi);
        set(Calendar.SECOND, s);
        set(Calendar.MILLISECOND, ms);

        juldate = -1;
        getJulian();
    }

    public void setDT(String date_time) throws SetOuiCalendarException {
        int year, month, day, hour, minute, second;
        year = (Integer.parseInt(date_time.substring(0, 4)));

        if (date_time.charAt(5) == ' ') {
            month = (Integer.parseInt(date_time.substring(6, 7)));
        } else {
            month = (Integer.parseInt(date_time.substring(5, 7)));
        }

        if (date_time.charAt(8) == ' ') {
            day = (Integer.parseInt(date_time.substring(9, 10)));
        } else {
            day = (Integer.parseInt(date_time.substring(8, 10)));
        }

        hour = 0;
        minute = 0;
        second = 0;

        if (date_time.length() > 15) {
            if (date_time.charAt(11) == ' ') {
                hour = (Integer.parseInt(date_time.substring(12, 13)));
            } else {
                hour = (Integer.parseInt(date_time.substring(11, 13)));
            }

            if (date_time.charAt(14) == ' ') {
                minute = (Integer.parseInt(date_time.substring(15, 16)));
            } else {
                minute = (Integer.parseInt(date_time.substring(14, 16)));
            }
        }

        if (date_time.length() == 19) {
            if (date_time.charAt(17) == ' ') {
                second = (Integer.parseInt(date_time.substring(18, 19)));
            } else {
                second = (Integer.parseInt(date_time.substring(17, 19)));
            }
        }

        setYMD(year, month, day, hour, minute, second, 0);
    }

    public void setCsvDate(String date_time) throws SetOuiCalendarException {
        String[] date_parts = date_time.split("/");
        int year, month, day;

        year = Integer.parseInt(date_parts[2]);
        month = Integer.parseInt(date_parts[0]);
        day = Integer.parseInt(date_parts[1]);
        setYMD(year, month, day, 0, 0, 0, 0);
    }

    public void setDashDate(String date_time) throws SetOuiCalendarException {
        //  THis is for YYYY-MM-DD
        String[] date_parts = date_time.split("-");
        int year, month, day;

        year = Integer.parseInt(date_parts[0]);
        month = Integer.parseInt(date_parts[1]);
        day = Integer.parseInt(date_parts[2]);
        setYMD(year, month, day, 0, 0, 0, 0);
    }

    /*
 ** Output methods
     */
    private String formatedDateTime(String format) {
        float sec = (float) (get(Calendar.SECOND)) + (float) (get(Calendar.MILLISECOND) * 0.001);

        String s = String.format(format,
                get(Calendar.YEAR), get(Calendar.MONTH) + 1,
                get(Calendar.DAY_OF_MONTH), get(Calendar.HOUR_OF_DAY),
                get(Calendar.MINUTE), (int) sec);

        return (s);
    }

    private String formatedDate(String format) {
        String s = String.format(format, get(Calendar.YEAR), get(Calendar.MONTH) + 1,
                get(Calendar.DAY_OF_MONTH));
        return (s);
    }

    public String getMmsDateTime() {
        return (formatedDateTime("%1$04d %2$02d %3$02d %4$02d %5$02d %6$02d"));
    }

    public String getGISDateTime() {
        return (formatedDateTime("%1$04d-%2$02d-%3$02d:%4$02d:%5$02d:%6$02d"));
    }

    public String getRiverWareDateTime() {
        String s = String.format("%1$04d-%2$02d-%3$02d:%4$02d:%5$02d", get(Calendar.YEAR), get(Calendar.MONTH),
                get(Calendar.DAY_OF_MONTH), get(Calendar.HOUR_OF_DAY),
                get(Calendar.MINUTE));
        return (s);
    }

    public String getTabDateTime() {
        return (formatedDateTime("%1$04d\t%2$02d\t%3$02d\t%4$02d\t%5$02d\t%6$02d"));
    }

    public String getControlFileDateTime() {
        return (formatedDateTime("%1$04d,%2$02d,%3$02d,%4$02d,%5$02d,%6$02d"));
    }

    public String getRuntimeGraphDateTime() {
        // I'm not sure what the last zero is -- markstro
        // "2001,10,1,0,0,0,0"
        return (formatedDateTime("%1$04d,%2$02d,%3$02d,%4$02d,%5$02d,%6$02d,0"));
    }

    public String getMmsEspInitDate() {
        return (formatedDate("%1$04d%2$02d%3$02d"));
    }

    public String getSQLDate() {
        return (formatedDate("%1$04d-%2$02d-%3$02d"));
    }

    public String getMonthDay() {
        String s = String.format("%1$02d-%2$02d", get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH));
        return (s);
    }

    @Override
    public String toString() {
        return (getGISDateTime());
    }

    public int getYear() {
        return (get(Calendar.YEAR));
    }

    public int getWaterYear() {
        int month = getMonth();
        int year = getYear();

        if (month >= 10 && month <= 12) {
            return year + 1;
        }

        return year;
    }

    public int getMonth() {
        return (get(Calendar.MONTH) + 1);
    }

    public int getDay() {
        return (get(Calendar.DAY_OF_MONTH));
    }

    public java.sql.Date getJDBCDate() {
        return new java.sql.Date(getTimeInMillis());
    }

    public long getMillis() {
        return (getTimeInMillis());
    }


    /*
 * convert Julian date to Gregorian
 *
 * The Julian day starts at noon of the Gregorian day and extends
 * to noon the next Gregorian day. The Gregorian day is assumed
 * to begin at midnight.
 *
 * Julian date converter. Takes a julian date (the number of days since
** some distant epoch or other), and returns an int pointer to static space.
** Copied from Algorithm 199 in Collected algorithms of the CACM
** Author: Robert G. Tantzen, Translator: Nat Howard
     */
//    private void setJul2Greg(double julian_day) {
//        setJulian(julian_day);
//    }

    public void setJulian(double julian_day) {
        int month, day, year, hour, minute;
        double second;

        long j = (long) (julian_day);
        double tmp, frac = julian_day - j;

        if (frac >= 0.5) {
            frac = frac - 0.5;
            j++;
        } else {
            frac = frac + 0.5;
        }

        j -= 1721119L;
        year = (int) ((4L * j - 1L) / 146097L);
        j = 4L * j - 1L - 146097L * year;
        day = (int) (j / 4L);
        j = (4L * day + 3L) / 1461L;
        day = (int) (4L * day + 3L - 1461L * j);
        day = (int) ((day + 4L) / 4L);
        month = (int) ((5L * day - 3L) / 153L);
        day = (int) (5L * day - 3 - 153L * month);
        day = (int) ((day + 5L) / 5L);
        year = (int) (100L * year + j);
        if (month < 10) {
            month += 3;
        } else {
            month -= 9;
            year += 1;
        }
        tmp = 3600.0 * (frac * 24.0);
        hour = (int) (tmp / 3600.0);
        tmp = tmp - hour * 3600.0;
        minute = (int) (tmp / 60.0);
        second = tmp - minute * 60.0;

        juldate = julian_day;
        setYMD(year, month, day, hour, minute, (int) second,
                (int) ((second - (int) second) * 1000.0));
    }

    public double getInternalJd() {
        return juldate;
    }
    
    /**
     *
     * Takes a date, and returns a Julian day. A Julian day is the number of
     * days since some base date (in the very distant past). Handy for getting
     * date of x number of days after a given Julian date (use jdate to get that
     * from the Gregorian date). Author: Robert G. Tantzen, translator: Nat
     * Howard Translated from the algol original in Collected Algorithms of CACM
     * (This and jdate are algorithm 199).
     *
     * @return
     */
    public double getJulian() {
//      if (juldate > 0.0) return (juldate);
        long m = (long) (get(Calendar.MONTH) + 1);
        long d = (long) (get(Calendar.DAY_OF_MONTH));
        long y = (long) (get(Calendar.YEAR));
        long c, ya, j;

        double seconds = (double) (get(Calendar.HOUR_OF_DAY) * 3600.0)
                + (double) (get(Calendar.MINUTE) * 60)
                + (double) (get(Calendar.SECOND))
                + (double) (get(Calendar.MILLISECOND) * 0.001);

        if (m > 2) {
            m -= 3;
        } else {
            m += 9;
            --y;
        }

        c = y / 100L;
        ya = y - (100L * c);
        j = (146097L * c) / 4L + (1461L * ya) / 4L + (153L * m + 2L)
                / 5L + d + 1721119L;
        if (seconds < 12 * 3600.0) {
            j--;
            seconds += 12.0 * 3600.0;
        } else {
            seconds = seconds - 12.0 * 3600.0;
        }

        juldate = j + (seconds / 3600.0) / 24.0;

        return (juldate);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            return false;
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
    
    private static OuiCalendar foo = null;
    public static String jul2dat (double jd) {
        if (foo == null) {
            foo = OuiCalendar.getInstance();
        }
        foo.setJulian(jd);
        return foo.getGISDateTime();
    }

    public void setDate(java.util.Date date) {
//        long time1 = date.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        setYMD(year, month+1, day, 0, 0, 0, 0);
    }
}
