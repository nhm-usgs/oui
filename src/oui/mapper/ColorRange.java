/*
 * ColorRange.java
 *
 * Created on May 31, 2012, 11:05 AM
 */

package oui.mapper;
    
import java.awt.Color;
import java.text.DecimalFormat;
    
/**
 *
 * @author  markstro
 */
public class ColorRange {
    private DecimalFormat form = new DecimalFormat("0.0000E0");
    private double minRangeValue, maxRangeValue, rangeValue;
    private int binCount = 10;
    private Color[] binColors;
    private String[] binStrings;
    private int numBinIncrements;
    private double[] binIncrements;
    private int numBinBounds;
    private double[] binBounds;

    public ColorRange(int numBins, double min, double max) {
        setBins (numBins, min,  max);
    }
//
//    public Color getBinColor(int i) {
//        return binColors[i];
//    }
//
    public Color[] getBinColors() {
        return binColors;
    }

    public String[] getBinStrings () {
        return binStrings;
    }
    
    final void setBins(int binCount, double minRangeValue, double maxRangeValue) {
        this.binCount = binCount;
        this.minRangeValue = minRangeValue;
        this.maxRangeValue = maxRangeValue;

        rangeValue = maxRangeValue - minRangeValue;

        if (binStrings == null || binStrings.length != binCount) {
            defaultBins();
        }

        for (int i = 0; i < binCount; i++) {
            if (i == 0) {
                binStrings[i] = "< " + form.format(minRangeValue);

            } else if (i == binCount - 1) {
                binStrings[i] = "> " + form.format(maxRangeValue);

            } else {
                binStrings[i] = form.format(binBounds[i - 1]) + " - " + form.format(binBounds[i]);
            }

            float rgb = (float)i / (float)(binCount - 1);
            binColors[i] = new Color(rgb, 1.0f - rgb, 1.0f - rgb);
        }
    }

    private void defaultBins () {
        numBinIncrements = binCount - 2;
        numBinBounds = binCount - 1;
        
        binColors = new Color[binCount];
        binStrings = new String[binCount];
        binIncrements = new double[numBinIncrements];
        binBounds = new double[numBinBounds];
        
        double inc = rangeValue / numBinIncrements;
        for (int i = 0; i < numBinIncrements; i++) {
            binIncrements[i] = inc;
        }
        
        binBounds[0] = minRangeValue;
        binBounds[numBinBounds - 1] = maxRangeValue;
        for (int i = 1; i < numBinBounds - 1; i++) {
            binBounds[i] = binBounds[i-1] + binIncrements[i];
        }
    }

    public Color getColorForValue (double val) {
        for (int i = 0; i < numBinBounds-1; i++) {
            if (val < binBounds[i+1]) {
                return binColors[i];
            }
        }
        return binColors[numBinBounds-1];
    }
}
