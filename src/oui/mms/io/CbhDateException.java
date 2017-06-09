/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package oui.mms.io;

/**
 *
 * @author markstro
 */
public class CbhDateException extends Exception  {
    private final String startDate;
    private final String endDate;
    private final int numOfTimeSteps;
    
    public CbhDateException(String startDate, String endDate, int numOfTimeSteps) {
      this.startDate = startDate;
      this.endDate = endDate;
      this.numOfTimeSteps = numOfTimeSteps;
    }
    
    public String getStartDate() {
        return startDate;
    }
       
    public String getEndDate() {
        return endDate;
    }
       
    public int getNumOfTimeSteps() {
        return numOfTimeSteps;
    }
}
