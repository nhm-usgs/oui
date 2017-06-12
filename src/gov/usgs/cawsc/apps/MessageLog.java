package gov.usgs.cawsc.apps;

/**
 * A singleton log that stores messages as they are entered
 * <p>
 * United States Department of Interior U.S. Geological Survey Water Resources Division National
 * Water Information System
 * 
 * @author John M. Donovan
 */
public class MessageLog
{

    /** Debug level 3 of reporting for the application logging */
    public static final int DEBUG3 = 300;

    /** Debug level 2 of reporting for the application logging */
    public static final int DEBUG2 = 301;

    /** Debug level 1 of reporting for the application logging */
    public static final int DEBUG1 = 302;

    /** The information level of reporting for the application logging */
    public static final int INFORMATION = 303;

    /** The warning level of reporting for the application logging */
    public static final int WARNING = 304;

    /** The failure level of reporting for the application logging */
    public static final int FAILURE = 305;

    /** The fatal error level of reporting for the application logging */
    public static final int FATAL = 306;

}
