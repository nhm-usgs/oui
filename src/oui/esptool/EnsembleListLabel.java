/*
 * EnsembleListLabel.java
 *
 * Created on November 17, 2004, 10:10 AM
 */

package oui.esptool;

import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.TimeSeries;

/**
 *
 * @author  markstro
 */
public class EnsembleListLabel {
    
    private double traceVolume;
    private double tracePeak;
    private double timeToPeak;
    private TimeSeries forecast;
    private double actVolumeProb;
    private double roundVolumeProb;
    private double actPeakProb;
    private double roundPeakProb;
    private int volumeRank;
    private int peakRank;
//    private Format probFormat = new Format("%5.1f");
    private EnsembleData ensembleData;
    
    /** Creates a new instance of EspListLabel */
//    public EspListLabel() {}
    
    public EnsembleListLabel(TimeSeries forecast, EnsembleData ensembleData) {
        this.forecast = forecast;
        this.ensembleData = ensembleData;
    }
    
    public EnsembleListLabel(EnsembleListLabel ell) {
        this.traceVolume = ell.traceVolume;
        this.tracePeak = ell.tracePeak;
        this.timeToPeak = ell.timeToPeak;
        this.forecast = ell.forecast;
        this.actVolumeProb = ell.actVolumeProb;
        this.roundVolumeProb = ell.roundVolumeProb;
        this.actPeakProb = ell.actPeakProb;
        this.roundPeakProb = ell.roundPeakProb;
        this.volumeRank = ell.volumeRank;
        this.peakRank = ell.peakRank;
        this.ensembleData = ell.ensembleData;
    }
    
    @Override
    public String toString () {
        if (ensembleData == null) {
            return forecast.getName();
        } else if (ensembleData.getSortOrder() == EnsembleData.YEAR) {
            return forecast.getName();
        } else if (ensembleData.getSortOrder() == EnsembleData.VOLUME) {
            return getEspToolVolumeLabel();
        } else if (ensembleData.getSortOrder() == EnsembleData.PEAK) {
            return getEspToolPeakLabel();
        }
        return forecast.getName();
    }
    
    public String getTraceName () {
        return forecast.getName();
    }
    public int getTraceYear () {
        return Integer.parseInt(forecast.getName());
    }

    public String getEspToolVolumeLabel() {
        String s = String.format("   [actP= %1$5.1f dmiP=%2$5.1f]", actVolumeProb, roundVolumeProb);
        return forecast.getName() + s;
    }

    public String getEspToolPeakLabel() {
        String s = String.format("   [actP= %1$5.1f dmiP=%2$5.1f]", actPeakProb, roundPeakProb);
        return forecast.getName() + s;
    }

    /**
     * Getter for property traceVolumes.
     * @return Value of property traceVolumes.
     */
    public double getTraceVolume() {
        return this.traceVolume;
    }
    
    /**
     * Setter for property traceVolumes.
     * @param traceVolumes New value of property traceVolumes.
     */
    public void setTraceVolume(double traceVolume) {
        this.traceVolume = traceVolume;
    }
    
    /**
     * Getter for property tracePeaks.
     * @return Value of property tracePeaks.
     */
    public double getTracePeak() {
        return this.tracePeak;
    }
    
    /**
     * Setter for property tracePeaks.
     * @param tracePeaks New value of property tracePeaks.
     */
    public void setTracePeak(double tracePeak) {
        this.tracePeak = tracePeak;
    }
    
    /**
     * Getter for property timeToPeak.
     * @return Value of property timeToPeak.
     */
    public double getTimeToPeak() {
        return this.timeToPeak;
    }
    
    /**
     * Setter for property timeToPeak.
     * @param timeToPeak New value of property timeToPeak.
     */
    public void setTimeToPeak(double timeToPeak) {
        this.timeToPeak = timeToPeak;
    }
    
    /**
     * Getter for property forecast.
     * @return Value of property forecast.
     */
    public TimeSeries getForecast() {
        return this.forecast;
    }
    
    /**
     * Setter for property forecast.
     * @param forecast New value of property forecast.
     */
    public void setForecast(TimeSeries forecast) {
        this.forecast = forecast;
    }
    
    /**
     * Getter for property actVolumeProb.
     * @return Value of property actVolumeProb.
     */
    public double getActVolumeProb() {
        return this.actVolumeProb;
    }
    
    /**
     * Setter for property actVolumeProb.
     * @param actVolumeProb New value of property actVolumeProb.
     */
    public void setActVolumeProb(double actVolumeProb) {
        this.actVolumeProb = actVolumeProb;
    }
    
    /**
     * Getter for property roundVolumeProb.
     * @return Value of property roundVolumeProb.
     */
    public double getRoundVolumeProb() {
        return this.roundVolumeProb;
    }
    
    /**
     * Setter for property roundVolumeProb.
     * @param roundVolumeProb New value of property roundVolumeProb.
     */
    public void setRoundVolumeProb(double roundVolumeProb) {
        this.roundVolumeProb = roundVolumeProb;
    }
    
    /**
     * Getter for property actPeakProb.
     * @return Value of property actPeakProb.
     */
    public double getActPeakProb() {
        return this.actPeakProb;
    }
    
    /**
     * Setter for property actPeakProb.
     * @param actPeakProb New value of property actPeakProb.
     */
    public void setActPeakProb(double actPeakProb) {
        this.actPeakProb = actPeakProb;
    }
    
    /**
     * Getter for property roundPeakProb.
     * @return Value of property roundPeakProb.
     */
    public double getRoundPeakProb() {
        return this.roundPeakProb;
    }
    
    /**
     * Setter for property roundPeakProb.
     * @param roundPeakProb New value of property roundPeakProb.
     */
    public void setRoundPeakProb(double roundPeakProb) {
        this.roundPeakProb = roundPeakProb;
    }
    
    /**
     * Getter for property volumeRank.
     * @return Value of property volumeRank.
     */
    public int getVolumeRank() {
        return this.volumeRank;
    }
    
    /**
     * Setter for property volumeRank.
     * @param volumeRank New value of property volumeRank.
     */
    public void setVolumeRank(int volumeRank) {
        this.volumeRank = volumeRank;
    }
    
    /**
     * Getter for property peakRank.
     * @return Value of property peakRank.
     */
    public int getPeakRank() {
        return this.peakRank;
    }
    
    /**
     * Setter for property peakRank.
     * @param peakRank New value of property peakRank.
     */
    public void setPeakRank(int peakRank) {
        this.peakRank = peakRank;
    }
    
}
