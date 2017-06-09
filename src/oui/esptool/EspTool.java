/*
 * EspTool.java
 *
 * Created on November 5, 2004, 8:10 AM
 */
package oui.esptool;

import gov.usgs.cawsc.gui.GuiUtilities;
import gov.usgs.cawsc.gui.MenuBarProvider;
import gov.usgs.cawsc.gui.PersistentSplitterTracker;
import gov.usgs.cawsc.gui.WindowFactory;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import javax.swing.*;
import org.w3c.dom.Node;
import oui.mms.datatypes.EnsembleData;
import oui.mms.datatypes.OuiCalendar;
import oui.mms.datatypes.TimeSeries;
import oui.mms.dmi.EspOutputDmi;
import oui.mms.io.EnsembleDataFromEsp;
import oui.util.GetTraces;
import oui.util.OuiClassLoader;
import oui.util.OuiHelp;
import oui.util.TimeSeriesPlotter;

/**
 *
 * @author markstro
 */
public class EspTool extends JPanel implements MenuBarProvider {

    private TimeSeriesPlotter plotter;
    private DefaultListModel listModel = new DefaultListModel();
    private String espStationName = null;

    protected PersistentSplitterTracker persistentSplitterTracker = null;

    /**
     * Creates new form EspTool
     */
    public EspTool(String xmlFile, String variableName) {  //  Constructor for xml file version
        this(EnsembleData.load(xmlFile));
    }

    public EspTool(String initStatvarFile, String espSeriesFile, String espInitVariable, String title) {  //  Constructor for MMS version
        this(EnsembleDataFromEsp.load(initStatvarFile, espSeriesFile, espInitVariable, title, 1));
    }

    public EspTool(String initStatvarFile, String espSeriesFile, String espInitVariable, String title, int seriesNum) {  //  Constructor for MMS version
        this(EnsembleDataFromEsp.load(initStatvarFile, espSeriesFile, espInitVariable, title, seriesNum));
    }

    public EspTool(EnsembleData ed) {
        this(ed, null);
    }

    public EspTool(EnsembleData ed, String espStationName) {
        this.espStationName = espStationName;
        this.ensembleData = ed;

        initComponents();
        traceListList.setModel(listModel);


        /*
         * Add the plotter
         */
        plotter = new TimeSeriesPlotter(ed.getName(), "Date", "Stream Flow");
        rightPanel.add(plotter.getPanel());
        /*
         * Plot the data for the init period
         */
        plotData(ed.getInitialization());

        /*
         *  Forecast period id the default analysis period.
         */
        ArrayList forecasts = ed.getForecasts();
        TimeSeries forecastTS = (TimeSeries) (forecasts.get(0));

        analysisStart = (OuiCalendar) forecastTS.getStart().clone();
        analysisEnd = (OuiCalendar) forecastTS.getEnd().clone();

        dmiStart = (OuiCalendar) analysisStart.clone();
        dmiEnd = (OuiCalendar) analysisEnd.clone();

        setDefaultDates();
        ed.setAnalysisPeriod(analysisStart, analysisEnd);

        loadList();
        setSize(getPreferredSize());
        
        persistentSplitterTracker = new PersistentSplitterTracker(this, jSplitPane1);
    }

    public JMenuBar getMenuBar() {
        return jMenuBar1;
    }

    private void loadList() {
        ArrayList order;

        listModel.clear();

        if (ensembleData.getSortOrder() == EnsembleData.VOLUME) {
            order = ensembleData.getStatsInVolumeOrder();
        } else if (ensembleData.getSortOrder() == EnsembleData.PEAK) {
            order = ensembleData.getStatsInPeakOrder();
        } else {
            order = ensembleData.getStats();
        }

        for (int i = 0; i < order.size(); i++) {
            EnsembleListLabel tsi = (EnsembleListLabel) (order.get(i));
            listModel.addElement(tsi);
        }
    }

    public final void plotData(TimeSeries tsc) {
        plotter.addTrace(tsc);
    }

    public final void plotData(TimeSeries[] tscs) {
        for (int i = 0; i < tscs.length; i++) {
            plotData(tscs[i]);
        }
    }

    private void setDefaultDates() {
        OuiCalendar initStart = ensembleData.getInitializationStart();
        OuiCalendar initEnd = ensembleData.getInitializationEnd();
        OuiCalendar forecastStart = ensembleData.getForecastStart();
        OuiCalendar forecastEnd = ensembleData.getForecastEnd();

        initStartLabel.setText(initStart.getJDBCDate().toString());
        initEndLabel.setText(initEnd.getJDBCDate().toString());
        ensembleStartLabel.setText(forecastStart.getJDBCDate().toString());
        ensembleEndLabel.setText(forecastEnd.getJDBCDate().toString());

        analysisStartSpinner.setModel(new SpinnerDateModel(analysisStart.getTime(), initStart.getTime(), forecastEnd.getTime(), Calendar.DAY_OF_MONTH));
        analysisStartSpinner.setEditor(new JSpinner.DateEditor(analysisStartSpinner, "yyyy-MM-dd"));

        analysisEndSpinner.setModel(new SpinnerDateModel(analysisEnd.getTime(), initStart.getTime(), forecastEnd.getTime(), Calendar.DAY_OF_MONTH));
        analysisEndSpinner.setEditor(new JSpinner.DateEditor(analysisEndSpinner, "yyyy-MM-dd"));

        dmiStartSpinner.setModel(new SpinnerDateModel(forecastStart.getTime(), initStart.getTime(), forecastEnd.getTime(), Calendar.DAY_OF_MONTH));
        dmiStartSpinner.setEditor(new JSpinner.DateEditor(dmiStartSpinner, "yyyy-MM-dd"));

        dmiEndSpinner.setModel(new SpinnerDateModel(forecastEnd.getTime(), initStart.getTime(), forecastEnd.getTime(), Calendar.DAY_OF_MONTH));
        dmiEndSpinner.setEditor(new JSpinner.DateEditor(dmiEndSpinner, "yyyy-MM-dd"));
    }

    public void selectListItems(int enso_code) {

        int count = 0;
        for (int j = 0; j < listModel.getSize(); j++) {
            EnsembleListLabel label = (EnsembleListLabel) (listModel.getElementAt(j));
            if (ElNino.lookUp(enso_code, label)) {
                count++;
            }
        }

        int[] sel = new int[count];
        count = 0;
        for (int j = 0; j < listModel.getSize(); j++) {
            EnsembleListLabel label = (EnsembleListLabel) (listModel.getElementAt(j));
            if (ElNino.lookUp(enso_code, label)) {
                sel[count++] = j;
            }
        }
        traceListList.setSelectedIndices(sel);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do
     * NOT modify this code. The content of this method is always regenerated by the Form
     * Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        getTraceMenuItem = new javax.swing.JMenuItem();
        quitMenuItem = new javax.swing.JMenuItem();
        selectYearsMenu = new javax.swing.JMenu();
        laNinaMenuItem = new javax.swing.JMenuItem();
        elNinoMenuItem = new javax.swing.JMenuItem();
        ensoNeutralMenuItem = new javax.swing.JMenuItem();
        pdoNegMenuItem = new javax.swing.JMenuItem();
        pdoPosMenuItem = new javax.swing.JMenuItem();
        pdoNeuMenuItem = new javax.swing.JMenuItem();
        reportMenu = new javax.swing.JMenu();
        reportMenuItem = new javax.swing.JMenuItem();
        dmiMenu = new javax.swing.JMenu();
        dmiMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        tracePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        traceListList = new javax.swing.JList();
        datePanel = new javax.swing.JPanel();
        rankPanel = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        analysisDatePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ensembleStartLabel = new javax.swing.JLabel();
        ensembleEndLabel = new javax.swing.JLabel();
        analysisStartSpinner = new javax.swing.JSpinner();
        analysisEndSpinner = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        initStartLabel = new javax.swing.JLabel();
        initEndLabel = new javax.swing.JLabel();
        dmiDatePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dmiStartSpinner = new javax.swing.JSpinner();
        dmiEndSpinner = new javax.swing.JSpinner();
        rightPanel = new javax.swing.JPanel();

        fileMenu.setText("File");

        getTraceMenuItem.setText("Get Trace From File");
        getTraceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getTraceMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(getTraceMenuItem);

        quitMenuItem.setText("Quit");
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(quitMenuItem);

        jMenuBar1.add(fileMenu);

        selectYearsMenu.setText("Select Years");

        laNinaMenuItem.setText("La Nina (Water Year NINO3.4 SSTs < -0.5 C)");
        laNinaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                laNinaMenuItemActionPerformed(evt);
            }
        });
        selectYearsMenu.add(laNinaMenuItem);

        elNinoMenuItem.setText("El Nino (Water Year NINO3.4 SSTs > 0.5 C)");
        elNinoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elNinoMenuItemActionPerformed(evt);
            }
        });
        selectYearsMenu.add(elNinoMenuItem);

        ensoNeutralMenuItem.setText("ENSO Neutral");
        ensoNeutralMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ensoNeutralMenuItemActionPerformed(evt);
            }
        });
        selectYearsMenu.add(ensoNeutralMenuItem);

        pdoNegMenuItem.setText("PDO < -0.5");
        pdoNegMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdoNegMenuItemActionPerformed(evt);
            }
        });
        selectYearsMenu.add(pdoNegMenuItem);

        pdoPosMenuItem.setText("PDO > 0.5");
        pdoPosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdoPosMenuItemActionPerformed(evt);
            }
        });
        selectYearsMenu.add(pdoPosMenuItem);

        pdoNeuMenuItem.setText("PDO Neutral");
        pdoNeuMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdoNeuMenuItemActionPerformed(evt);
            }
        });
        selectYearsMenu.add(pdoNeuMenuItem);

        jMenuBar1.add(selectYearsMenu);

        reportMenu.setText("Reports");

        reportMenuItem.setText("Write Report");
        reportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportMenuItemActionPerformed(evt);
            }
        });
        reportMenu.add(reportMenuItem);

        jMenuBar1.add(reportMenu);

        dmiMenu.setText("DMIs");

        dmiMenuItem.setText("Run ESP DMI for Selected Traces");
        dmiMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dmiMenuItemActionPerformed(evt);
            }
        });
        dmiMenu.add(dmiMenuItem);

        jMenuBar1.add(dmiMenu);

        helpMenu.setText("Help");

        helpMenuItem.setText("OUI Users Manual");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuSelected(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        jMenuBar1.add(helpMenu);

        leftPanel.setLayout(new java.awt.BorderLayout());

        tracePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Ensemble Traces"));
        tracePanel.setLayout(new java.awt.BorderLayout());

        traceListList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                traceListListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(traceListList);

        tracePanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        leftPanel.add(tracePanel, java.awt.BorderLayout.CENTER);

        datePanel.setLayout(new java.awt.BorderLayout());

        rankPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Rank By"));

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Volume");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });
        rankPanel.add(jRadioButton1);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Peak");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });
        rankPanel.add(jRadioButton2);

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Year");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });
        rankPanel.add(jRadioButton3);

        datePanel.add(rankPanel, java.awt.BorderLayout.NORTH);

        analysisDatePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Analysis Dates"));
        analysisDatePanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Forecast Start:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        analysisDatePanel.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Forecast End:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        analysisDatePanel.add(jLabel2, gridBagConstraints);

        jLabel3.setText("Analysis Start:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        analysisDatePanel.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Analysis End:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        analysisDatePanel.add(jLabel4, gridBagConstraints);

        ensembleStartLabel.setText("2004-01-01");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        analysisDatePanel.add(ensembleStartLabel, gridBagConstraints);

        ensembleEndLabel.setText("jLabel5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        analysisDatePanel.add(ensembleEndLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        analysisDatePanel.add(analysisStartSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        analysisDatePanel.add(analysisEndSpinner, gridBagConstraints);

        jLabel7.setText("Initilization Start:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        analysisDatePanel.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Intialization End:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        analysisDatePanel.add(jLabel8, gridBagConstraints);

        initStartLabel.setText("jLabel9");
        analysisDatePanel.add(initStartLabel, new java.awt.GridBagConstraints());

        initEndLabel.setText("jLabel9");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        analysisDatePanel.add(initEndLabel, gridBagConstraints);

        datePanel.add(analysisDatePanel, java.awt.BorderLayout.CENTER);

        dmiDatePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("DMI Dates"));
        dmiDatePanel.setLayout(new java.awt.GridBagLayout());

        jLabel5.setText("DMI Start:");
        dmiDatePanel.add(jLabel5, new java.awt.GridBagConstraints());

        jLabel6.setText("DMI End:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        dmiDatePanel.add(jLabel6, gridBagConstraints);
        dmiDatePanel.add(dmiStartSpinner, new java.awt.GridBagConstraints());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        dmiDatePanel.add(dmiEndSpinner, gridBagConstraints);

        datePanel.add(dmiDatePanel, java.awt.BorderLayout.SOUTH);

        leftPanel.add(datePanel, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(leftPanel);

        rightPanel.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(rightPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jMenuBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jMenuBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void helpMenuSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuSelected
        OuiHelp.OuiHelpFactory().showTopic("");
    }//GEN-LAST:event_helpMenuSelected

    private void traceListListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_traceListListValueChanged
        if (evt.getValueIsAdjusting() == false) {

            /*
             *  Clear the ensemble traces in the plot
             */
            Iterator it = ensembleData.getStats().iterator();
            while (it.hasNext()) {
                EnsembleListLabel ell = (EnsembleListLabel) (it.next());
                plotter.clearTrace(ell.getTraceName());
            }

            if (traceListList.getSelectedIndex() != -1) {
                Object[] sel = traceListList.getSelectedValues();
                for (int i = 0; i < sel.length; i++) {
                    EnsembleListLabel ell = (EnsembleListLabel) (sel[i]);
                    plotData(ell.getForecast());
                }
            }
        }
    }//GEN-LAST:event_traceListListValueChanged

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        ensembleData.setSortOrder(EnsembleData.YEAR);
        loadList();
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        ensembleData.setSortOrder(EnsembleData.PEAK);
        loadList();
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        ensembleData.setSortOrder(EnsembleData.VOLUME);
        loadList();
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void dmiMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dmiMenuItemActionPerformed

        if (espDmiClassName == null) {
            System.out.println("EspTool:  no esp dmi specified -- not run.");
            JOptionPane.showMessageDialog(GuiUtilities.windowFor(this), "No esp dmi specified -- not run.", "EspTool DMI", JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        try {
            System.out.println("EspTool:  running esp dmi " + espDmiClassName);
            Object[] sel = traceListList.getSelectedValues();
            String[] years = new String[sel.length];
            String[] probs = new String[sel.length];

            for (int i = 0; i < sel.length; i++) {
                EnsembleListLabel ell = (EnsembleListLabel) (sel[i]);
                years[i] = "" + ell.getTraceYear();

                probs[i] = "-1";
                if (ensembleData.getSortOrder() == EnsembleData.VOLUME) {
                    probs[i] = "" + (int) (ell.getRoundVolumeProb());
                } else if (ensembleData.getSortOrder() == EnsembleData.PEAK) {
                    probs[i] = "" + (int) (ell.getRoundPeakProb());
                } else {
                    System.out.println("EspTool:  either peak or volume must be selected to run dmi");
                    return;
                }

                System.out.println("year = " + years[i] + " dmi prob = " + probs[i]);
            }

            Class cl = OuiClassLoader.factory().loadClass(espDmiClassName, true);
            Class[] signature = {Class.forName("org.w3c.dom.Node")};
            Constructor constructor = cl.getConstructor(signature);
            Object[] args = {espDmiNode};

            EspOutputDmi output_dmi = (EspOutputDmi) (constructor.newInstance(args));
            output_dmi.runDmi(dmiStart, dmiEnd, years, probs, espStationName);

        } catch (java.lang.reflect.InvocationTargetException e) {
            System.out.println("processNodes: class " + espDmiClassName + " invocation exception.");

        } catch (NoSuchMethodException e) {
            System.out.println("processNodes: class " + espDmiClassName + " doesn't have the proper constructor.");

        } catch (ClassNotFoundException e) {
            System.out.println("processNodes: class " + espDmiClassName + " not found.");

        } catch (InstantiationException e) {
            System.out.println("processNodes: class " + espDmiClassName + " instantiation exception.");

        } catch (IllegalAccessException e) {
            System.out.println("processNodes: class " + espDmiClassName + " illegal access exception.");
        }
    }//GEN-LAST:event_dmiMenuItemActionPerformed

    private void reportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportMenuItemActionPerformed
        try {
            WriteReport wr = new WriteReport(this);
        } catch (Exception excep) {
            System.out.println(excep);
        }
    }//GEN-LAST:event_reportMenuItemActionPerformed

    private void pdoNeuMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdoNeuMenuItemActionPerformed
        selectListItems(ElNino.NEU_PDO);
    }//GEN-LAST:event_pdoNeuMenuItemActionPerformed

    private void pdoPosMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdoPosMenuItemActionPerformed
        selectListItems(ElNino.POS_PDO);
    }//GEN-LAST:event_pdoPosMenuItemActionPerformed

    private void pdoNegMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdoNegMenuItemActionPerformed
        selectListItems(ElNino.NEG_PDO);
    }//GEN-LAST:event_pdoNegMenuItemActionPerformed

    private void ensoNeutralMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ensoNeutralMenuItemActionPerformed
        selectListItems(ElNino.NEUTRAL);
    }//GEN-LAST:event_ensoNeutralMenuItemActionPerformed

    private void laNinaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_laNinaMenuItemActionPerformed
        selectListItems(ElNino.LA_NINA);
    }//GEN-LAST:event_laNinaMenuItemActionPerformed

    private void elNinoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elNinoMenuItemActionPerformed
        selectListItems(ElNino.EL_NINO);
    }//GEN-LAST:event_elNinoMenuItemActionPerformed

    private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
        exit();
    }//GEN-LAST:event_quitMenuItemActionPerformed

    private void getTraceMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getTraceMenuItemActionPerformed
        plotData(GetTraces.getTraces(this));
    }//GEN-LAST:event_getTraceMenuItemActionPerformed

    /**
     * Exit the Application
     */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        exit();
    }//GEN-LAST:event_exitForm

    private void exit() {
        WindowFactory.instance().closeWindow(this);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            if (args[0].compareTo("help") == 0) {
                usage();

            } else if (args.length == 2) {
//            EspTool et = new EspTool("/home/projects/oui_projects/rio_grande/riogr_mms_work/output/esp/LittleNavajo.xml", "basin_cfs.strmflow 1");
                EspTool et = new EspTool(args[0], args[1]);

                String title = "OUI ESP Tool";
                WindowFactory.displayInFrame(et, title);

            } else if (args.length == 4) {
//            EspTool et1 = new EspTool("/home/mms_workspaces/heihe_work/output/ESP.init.statvar", "/home/mms_workspaces/heihe_work/output/esp.series", "basin_cms.strmflow 1", "Heihe");
                EspTool et1 = new EspTool(args[0], args[1], args[2], args[3]);
                String title = args[3];
                WindowFactory.displayInFrame(et1, title);
                WindowFactory.instance().displayInWindow(et1);

            } else {
                usage();
            }

        } catch (Exception ex) {
//            ex.printStackTrace();
            System.out.println();
            usage();
            System.out.println();

        }
    }

    public static void usage() {
        System.out.println("EspTool usage:");
        System.out.println("  EspTool can be called in two ways depending on the type of esp output you have.\n");
        System.out.println("  java -cp <classpath> oui.esptool.EspTool <esp.xml file> <esp variable and index>");
        System.out.println("  example: java -cp oui.jar;sgt_v2.jar oui.esptool.EspTool /home/mms_work/output/esp/LittleNavajo.xml \"basin_cfs.strmflow 1\"\n");
        System.out.println("  or\n");
        System.out.println("  java -cp <classpath> oui.esptool.EspTool <esp init statvar file> <esp series file> <esp variable and index> <title>");
        System.out.println("  example: java -cp oui.jar;sgt_v2.jar oui.esptool.EspTool /home/mms_work/output/ESP.init.statvar /home/mms_work/output/esp.series \"basin_cms.strmflow 1\" Heihe");
    }

    /**
     * Getter for property analysisStart.
     *
     * @return Value of property analysisStart.
     */
    public OuiCalendar getAnalysisStart() {
        return this.analysisStart;
    }

    /**
     * Setter for property analysisStart.
     *
     * @param analysisStart New value of property analysisStart.
     */
    public void setAnalysisStart(OuiCalendar analysisStart) {
        this.analysisStart = analysisStart;
        analysisStartSpinner.setValue(analysisStart.getJDBCDate().toString());
    }

    /**
     * Getter for property analysisEnd.
     *
     * @return Value of property analysisEnd.
     */
    public OuiCalendar getAnalysisEnd() {
        return this.analysisEnd;
    }

    /**
     * Setter for property analysisEnd.
     *
     * @param analysisEnd New value of property analysisEnd.
     */
    public void setAnalysisEnd(OuiCalendar analysisEnd) {
        this.analysisEnd = analysisEnd;
        analysisEndSpinner.setValue(analysisEnd.getJDBCDate().toString());
    }

    /**
     * Getter for property dmiStart.
     *
     * @return Value of property dmiStart.
     */
    public OuiCalendar getDmiStart() {
        return this.dmiStart;
    }

    /**
     * Setter for property dmiStart.
     *
     * @param dmiStart New value of property dmiStart.
     */
    public void setDmiStart(OuiCalendar dmiStart) {
        this.dmiStart = dmiStart;
        dmiStartSpinner.setValue(dmiStart.getJDBCDate().toString());
    }

    /**
     * Getter for property dmiEnd.
     *
     * @return Value of property dmiEnd.
     */
    public OuiCalendar getDmiEnd() {
        return this.dmiEnd;
    }

    /**
     * Setter for property dmiEnd.
     *
     * @param dmiEnd New value of property dmiEnd.
     */
    public void setDmiEnd(OuiCalendar dmiEnd) {
        this.dmiEnd = dmiEnd;
        dmiEndSpinner.setValue(dmiEnd.getJDBCDate().toString());
    }

    /**
     * Getter for property ensembleData.
     *
     * @return Value of property ensembleData.
     */
    public EnsembleData getEnsembleData() {
        return this.ensembleData;
    }

    /**
     * Setter for property ensembleData.
     *
     * @param ensembleData New value of property ensembleData.
     */
    public void setEnsembleData(EnsembleData ensembleData) {
        this.ensembleData = ensembleData;
        setDefaultDates();
    }

    /**
     * Getter for property traceListList.
     *
     * @return Value of property traceListList.
     */
    public JList getTraceListList() {
        return this.traceListList;
    }

    /**
     * Setter for property traceListList.
     *
     * @param traceListList New value of property traceListList.
     */
    public void setTraceListList(JList traceListList) {
        this.traceListList = traceListList;
    }

    /**
     * Getter for property espDmiClassName.
     *
     * @return Value of property espDmiClassName.
     */
    public String getEspDmiClassName() {
        return this.espDmiClassName;
    }

    /**
     * Setter for property espDmiClassName.
     *
     * @param espDmiClassName New value of property espDmiClassName.
     */
    public void setEspDmiClassName(String espDmiClassName) {
        this.espDmiClassName = espDmiClassName;
    }

    /**
     * Getter for property espDmiNode.
     *
     * @return Value of property espDmiNode.
     */
    public Node getEspDmiNode() {
        return this.espDmiNode;
    }

    /**
     * Setter for property espDmiNode.
     *
     * @param espDmiNode New value of property espDmiNode.
     */
    public void setEspDmiNode(Node espDmiNode) {
        this.espDmiNode = espDmiNode;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel analysisDatePanel;
    private javax.swing.JSpinner analysisEndSpinner;
    private javax.swing.JSpinner analysisStartSpinner;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel datePanel;
    private javax.swing.JPanel dmiDatePanel;
    private javax.swing.JSpinner dmiEndSpinner;
    private javax.swing.JMenu dmiMenu;
    private javax.swing.JMenuItem dmiMenuItem;
    private javax.swing.JSpinner dmiStartSpinner;
    private javax.swing.JMenuItem elNinoMenuItem;
    private javax.swing.JLabel ensembleEndLabel;
    private javax.swing.JLabel ensembleStartLabel;
    private javax.swing.JMenuItem ensoNeutralMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem getTraceMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JLabel initEndLabel;
    private javax.swing.JLabel initStartLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem laNinaMenuItem;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JMenuItem pdoNegMenuItem;
    private javax.swing.JMenuItem pdoNeuMenuItem;
    private javax.swing.JMenuItem pdoPosMenuItem;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JPanel rankPanel;
    private javax.swing.JMenu reportMenu;
    private javax.swing.JMenuItem reportMenuItem;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JMenu selectYearsMenu;
    private javax.swing.JList traceListList;
    private javax.swing.JPanel tracePanel;
    // End of variables declaration//GEN-END:variables
    /**
     * Holds value of property analysisStart.
     */
    private OuiCalendar analysisStart = new OuiCalendar();

    /**
     * Holds value of property analysisEnd.
     */
    private OuiCalendar analysisEnd = new OuiCalendar();

    /**
     * Holds value of property dmiStart.
     */
    private OuiCalendar dmiStart = new OuiCalendar();

    /**
     * Holds value of property dmiEnd.
     */
    private OuiCalendar dmiEnd = new OuiCalendar();

    /**
     * Holds value of property ensembleData.
     */
    private EnsembleData ensembleData;

    /**
     * Holds value of property espDmiClassName.
     */
    private String espDmiClassName;

    /**
     * Holds value of property espDmiNode.
     */
    private Node espDmiNode;

}
