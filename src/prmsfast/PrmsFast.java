/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prmsfast;

import csvutils.CsvTableModelAdaptor;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import oui.mms.datatypes.Control;
import oui.mms.datatypes.ControlSet;
import oui.mms.datatypes.Parameter;
import oui.mms.datatypes.ParameterSet;
import oui.mms.io.MmsParamsReader;
import oui.mms.io.MmsParamsWriter;

/**
 *
 * @author markstro
 */
public class PrmsFast {

    public static void main(String args[]) {
        
//        test();
        
        File paramFile = new File(args[0]);
        File controlFile = new File(args[1]);
        File trialFile = new File(args[2]);
        int numOfCpus = Integer.parseInt(args[3]);
        PrmsFast pf = new PrmsFast(paramFile, controlFile, trialFile, numOfCpus);
    }
    
    public static void test () {
        File paramFile = new File("D:/backedUp/development/oui4/testData/input/daymet.params");
        File controlFile = new File("D:/backedUp/development/oui4/testData/control/daymet.control");
        File trialFile = new File("D:/backedUp/development/oui4/testData/trials.csv");
        int numOfCpus = 1;
        PrmsFast pf = new PrmsFast(paramFile, controlFile, trialFile, numOfCpus);        
    }

    private PrmsFast(File paramFile, File controlFile, File trialFile, int numOfCpus) {
        try {

            // create a sens directory where the parameter file is
            File trialParamDir = new File(paramFile.getParentFile(), "sens");
            deleteFolder(trialParamDir);
            trialParamDir.mkdirs();

            // figure out the base name and the file extension of the parameter
            // file. Add the trial number to this info to get the name for the
            // parameter file that corresponds to each trial.
            String f1 = paramFile.getName();
            String[] parts = f1.split("\\.");
            String rootParamFileName = parts[0];

            // Read the default parameter file
            String extentionParamFileName = null;
            try {
                extentionParamFileName = parts[1];
            } catch (NullPointerException e) {
                System.out.println ("Parameter file " + paramFile.getName() + " does not have a file extension");
            }
            MmsParamsReader mpr = new MmsParamsReader(paramFile.toString());
            ParameterSet ps = mpr.read();

            // Set print_freq and print_type to 0 to get minimal output to .out file
            Parameter p = ps.getParameter("print_freq");
            p.setAllVals("0");
            p = ps.getParameter("print_type");
            p.setAllVals("0");

            // create a sens directory where the control file is
            File trialControlDir = new File(controlFile.getParentFile(), "sens");
            deleteFolder(trialControlDir);
            trialControlDir.mkdirs();

            // Read the default control file
            f1 = controlFile.getName();
            String[] cont_parts = f1.split("\\.");
            String rootControlFileName = cont_parts[0];
            String extentionControlFileName = cont_parts[1];
            ControlSet cs = ControlSet.readMmsControl(controlFile.getCanonicalPath());

            // Turn off the animation and statvar file in this control set
//            setControlValues(cs, "aniOutON_OFF", 0, "0");
//            setControlValues(cs, "statsON_OFF", 0, "0");
            setControlValues(cs, "print_debug", 0, "-1");
            setControlValues(cs, "parameter_check_flag", 0, "0");


            // get the executable name
            Control mc = cs.getControlVariableByName("executable_model");
            File executableFile = new File(mc.getSingleVal());

            // Get the name of the CSV file
            mc = cs.getControlVariableByName("csv_output_file");
            File csvFile = new File(mc.getSingleVal());
            
            // Get the name of the statvar file
            mc = cs.getControlVariableByName("stat_var_file");
            File statVarFile = new File(mc.getSingleVal());
            
            // Get the name of the animation file
            mc = cs.getControlVariableByName("ani_output_file");
            File aniFile = new File(mc.getSingleVal());
            
            // Get the name of the nhruOutBaseFileName file
//            mc = cs.getControlVariableByName("nhruOutBaseFileName");
//            String nhruOutBaseFileName = mc.getSingleVal();

            // create a sens directory where the csv output file is
            File trialCsvDir = new File(csvFile.getParentFile(), "sens");
            deleteFolder(trialCsvDir);
            trialCsvDir.mkdirs();

            // figure out the base name and the file extension of the csv
            // file. Add the trial number to this info to get the name for the
            // csv file that corresponds to each trial.
            f1 = csvFile.getName();
            String[] csv_parts = f1.split("\\.");
            String rootCsvFileName = csv_parts[0];
            String extentionCsvFileName = csv_parts[1];
            
            // figure out the base name and the file extension of the statvar
            // file. Add the trial number to this info to get the name for the
            // statvar file that corresponds to each trial.
            f1 = statVarFile.getName();
            String[] statvar_parts = f1.split("\\.");
            String rootStatVarFileName = statvar_parts[0];
            String extentionStatVarFileName;
            if (statvar_parts.length == 1) {
                extentionStatVarFileName = "statvar";
            } else {
                extentionStatVarFileName = statvar_parts[1];
            }
            
            // figure out the base name and the file extension of the animation
            // file. Add the trial number to this info to get the name for the
            // animation file that corresponds to each trial.
            f1 = aniFile.getName();
            String[] ani_parts = f1.split("\\.");
            String rootAniFileName = ani_parts[0];
            String extentionAniFileName = ani_parts[1];

            // read the trials
            CsvTableModelAdaptor trialTable = new CsvTableModelAdaptor(trialFile.getCanonicalPath());
            // loop over the trials
            int rowCount = trialTable.getRowCount();
            int colCount = trialTable.getColumnCount();

            // Get the Parameter objects from the Parameter Set
            Parameter[] sensParams = new Parameter[colCount - 1];
            for (int j = 1; j < colCount; j++) {
                sensParams[j - 1] = ps.getParameter(trialTable.getColumnName(j));
                if (sensParams[j - 1] == null) {
                    System.out.println ("Parameter " + trialTable.getColumnName(j) + " in " + trialFile.getCanonicalPath() + " not found in model.  Fix this.");
                    System.exit(1);
                }
            }


            // Write the "default" paramter file for trial 0
            String name = rootParamFileName + "_0." + extentionParamFileName;
            File trialParameterFile = new File(trialParamDir, name);
            MmsParamsWriter.write(trialParameterFile.getCanonicalPath(), ps);

            // Write the "default" control file for trial 0
            setControlValues(cs, "param_file", 0, trialParameterFile.getCanonicalPath());

            // Turn on the CSV file
            // Leave the csv file on if it is on, off if it is off
//            setControlValues(cs, "csvON_OFF", 0, "1");
            name = rootCsvFileName + "_0." + extentionCsvFileName;
            File trialCsvFile = new File(trialCsvDir, name);
            setControlValues(cs, "csv_output_file", 0, trialCsvFile.getCanonicalPath());
            
            // Turn on the Statvar file
//            setControlValues(cs, "statsON_OFF", 0, "1");
            name = rootStatVarFileName + "_0." + extentionStatVarFileName;
            
            // Statvar files should be written where the csv files are being written.
            File trialStatVarFile = new File(trialCsvDir, name);
            setControlValues(cs, "stat_var_file", 0, trialStatVarFile.getCanonicalPath());

// Turn on the Ani file
//            setControlValues(cs, "aniOutON_OFF", 0, "1");
            name = rootAniFileName + "_0." + extentionAniFileName;
            
            // Ani files should be written where the csv files are being written.
            File trialAniFile = new File(trialCsvDir, name);
            setControlValues(cs, "ani_output_file", 0, trialAniFile.getCanonicalPath());
            
            // nhruOutBaseName run 0
//            name = nhruOutBaseFileName + File.separatorChar + "0_";
//            File trialNhruOutBase = new File(name);
//            setControlValues(cs, "nhruOutBaseFileName", 0, trialNhruOutBase.getCanonicalPath());
            
            // Write the control file for this trial
            name = rootControlFileName + "_0." + extentionControlFileName;
            File trialControlFile = new File(trialControlDir, name);
            ControlSet.writeMmsControl(cs, trialControlFile.getCanonicalPath());

            // Write the paramter and control files for each trial
            for (int i = 0; i < rowCount; i++) {
                // loop over the sensitivity parameters for this trial
                for (int j = 1; j < colCount; j++) {
                    String parName = trialTable.getColumnName(j);
                    
                    String foo = trialTable.getValueAt(i, j).toString();

                    
                    // These are parameters that depend on each other
                    // hru_percent_imperv + dprst_frac_hru < 0.999
                    if (parName.equals("hru_percent_imperv")) {
                        // Read the dprst_frac_hru parameter from the parameter file.
                        double max = ps.getParameter("dprst_frac_hru").getMax();
                        
                        // The range of hru_percent_imperv is calculated
                        double range = 0.99 - max;
                        
                        // The new parameter value is the range multiplied by the porpotion
                        // set by FAST.  Reset the value of foo for below.
                        foo = String.valueOf(range * Double.parseDouble(foo));
                        
                    } else if (parName.equals("dprst_frac_hru")) {
                        // Read the hru_percent_imperv parameter from the parameter file.
                        double max = ps.getParameter("hru_percent_imperv").getMax();
                        
                        // The range of dprst_frac_hru is calculated
                        double range = 0.99 - max;
                        
                        // The new parameter value is the range multiplied by the porpotion
                        // set by FAST.  Reset the value of foo for below.
                        foo = String.valueOf(range * Double.parseDouble(foo));

                    } else if (parName.equals("soil_moist_max")) {

                    // These are parameters that depend on each other
                    // soil_moist_max > soil_rechr_max
                    // Read the soil_rechr_max parameter from the parameter file.
                        double max = ps.getParameter("soil_rechr_max").getMax();
                      
                    }
                    
                    if (parName.equals("soil_rechr_max")) {
                        System.out.println ("prmsfast.PrmsFast changing soil_moist_max/soil_rechr_max");
                        System.out.println ("still need to fix the code");                        
                    }
                    
                    foo = trialTable.getValueAt(i, j).toString();
                    sensParams[j - 1].setAllVals(foo);
                }

                // Write the parameter file for this trial
                name = rootParamFileName + "_" + (i + 1) + "." + extentionParamFileName;
                trialParameterFile = new File(trialParamDir, name);
                MmsParamsWriter.write(trialParameterFile.getCanonicalPath(), ps);

                // Set the control values for this trial
                setControlValues(cs, "param_file", 0, trialParameterFile.getCanonicalPath());

                // Turn on the CSV file
//                setControlValues(cs, "csvON_OFF", 0, "1");
                name = rootCsvFileName + "_" + (i + 1) + "." + extentionCsvFileName;
                trialCsvFile = new File(trialCsvDir, name);
                setControlValues(cs, "csv_output_file", 0, trialCsvFile.getCanonicalPath());
                
                // Turn on the statvar file
//                setControlValues(cs, "statsON_OFF", 0, "1");
                name = rootStatVarFileName + "_" + (i + 1) + "." + extentionStatVarFileName;
                trialStatVarFile = new File(trialCsvDir, name);
                setControlValues(cs, "stat_var_file", 0, trialStatVarFile.getCanonicalPath());

                // Turn on the Ani file
//                setControlValues(cs, "aniOutON_OFF", 0, "1");
                name = rootAniFileName + "_" + (i + 1) + "." + extentionAniFileName;
                trialAniFile = new File(trialCsvDir, name);
                setControlValues(cs, "ani_output_file", 0, trialAniFile.getCanonicalPath());
                
                // NhruOutBaseName
//                name = nhruOutBaseFileName + File.separatorChar + (i + 1) + "_";
//                trialNhruOutBase = new File(name);
//                setControlValues(cs, "nhruOutBaseFileName", 0, trialNhruOutBase.getCanonicalPath());
                
                // Write the control file for this trial
                name = rootControlFileName + "_" + (i + 1) + "." + extentionControlFileName;
                trialControlFile = new File(trialControlDir, name);
                ControlSet.writeMmsControl(cs, trialControlFile.getCanonicalPath());
            }

            // write a batch file and shell script for each CPU
            int runsPerCpu = rowCount / numOfCpus;
            int remainder = rowCount % numOfCpus;

            int trial = 1;
            int trialsForThisCpu = 0;
            for (int i = 0; i < numOfCpus; i++) {
                PrintWriter batchOut = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(new File("./", "sensRun_" + (i + 1) + ".bat"))));

                PrintWriter shOut = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(new File("./", "sensRun_" + (i + 1) + ".sh"))));
                
                PrintWriter slurmOut = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(new File("./", "sensRun_" + (i + 1) + ".slurm"))));

                slurmOut.println("#!/bin/bash");
                slurmOut.println("#SBATCH -p Prod");
                slurmOut.println("#SBATCH -A Water");
                slurmOut.println("#SBATCH -J PRMS");
                slurmOut.println("#SBATCH --output=%J-prms.out");
                slurmOut.println("#SBATCH --time=0-01:00:00");
                slurmOut.println("#SBATCH --mail-user=markstro@usgs.gov");
                slurmOut.println("#SBATCH --mail-type=ALL\n");

                // print out the default parameter (unmodified) run as trial 0 in script 1
                if (trial == 1) {
                    name = rootControlFileName + "_0." + extentionControlFileName;
                    trialControlFile = new File(trialControlDir, name);

                    batchOut.println(executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NUL");
                    shOut.println(executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NULL");
                    slurmOut.println("srun " + executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NULL");
                }


                while (trialsForThisCpu < runsPerCpu) {
                    name = rootControlFileName + "_" + (trial) + "." + extentionControlFileName;
                    trialControlFile = new File(trialControlDir, name);

                    batchOut.println(executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NUL");
                    shOut.println(executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NULL");
                    slurmOut.println("srun " + executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NULL");

                    trialsForThisCpu++;
                    trial++;
                }

                if (remainder > 0) {
                    name = rootControlFileName + "_" + (trial) + "." + extentionControlFileName;
                    trialControlFile = new File(trialControlDir, name);

                    batchOut.println(executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NUL");
                    shOut.println(executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NULL");
                    slurmOut.println("srun " + executableFile.getCanonicalPath() + " -C\"" + trialControlFile.getCanonicalPath() + "\" > NULL");

                    trial++;
                    remainder--;
                }

                batchOut.close();
                shOut.close();
                slurmOut.close();

                trialsForThisCpu = 0;
            }

        } catch (IOException ex) {
            Logger.getLogger(PrmsFast.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void setControlValues(ControlSet cs, String controlName, int index,
            String value) {
        Control mc = cs.getControlVariableByName(controlName);

        if (mc == null) {
            // 1 = long  2 = float  3 = double  4 = string
            ArrayList<String> foo = new ArrayList(1);
            foo.add(0, value);
            mc = new Control(controlName, 4, foo);
            cs.put(mc);

        } else {
            mc.setValueAt(index, value);
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
}
