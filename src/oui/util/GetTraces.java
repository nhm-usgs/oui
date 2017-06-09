package oui.util;

import gov.usgs.cawsc.gui.GuiUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import oui.mms.datatypes.SpaceTimeSeriesData;
import oui.mms.datatypes.TimeSeries;
import oui.mms.io.CsvFileReader;
import oui.mms.io.MmsDataFileReader;
import oui.mms.io.MmsStatvarReader;

/**
 */
public class GetTraces {

    private static File last_dir = null;

    public static TimeSeries[] getAnimationFileTraces(Component parent) {
        JFileChooser chooser = new JFileChooser();

        Window ancestor = GuiUtilities.windowFor(parent);

        chooser.setDialogTitle("Load Time Series Trace From Animation File");
        chooser.setCurrentDirectory(last_dir);
        int retval = chooser.showOpenDialog(ancestor);

        if (retval == JFileChooser.APPROVE_OPTION) {
            String file_name = chooser.getSelectedFile().getAbsolutePath();
            last_dir = chooser.getSelectedFile();

            // arg[0] is the name of the animation file
            // arg[1] is the name of the vairable
            // arg[2] is the zone index (seg or HRU ID)
            SpaceTimeSeriesData dtsd = new SpaceTimeSeriesData(file_name);

            if (dtsd == null) {
                return null;
            }

            Object[] message = new Object[1];
            JPanel jp = new JPanel(new BorderLayout());
            DefaultListModel lm = new DefaultListModel();
            JList list = new JList(lm);
            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Output Variable List",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    parent.getFont()),
                    new EmptyBorder(0, 10, 0, 0)));
            jp.add(scroll);
            lm.removeAllElements();
//            String line;
            ArrayList<String> variableNames = dtsd.getVariableNames();
            for (int i = 0; i < variableNames.size(); i++) {
                lm.addElement(variableNames.get(i));
            }

            message[0] = jp;

            // Get the variable name
            String[] options = {"OK", "Cancel"};
            int result = JOptionPane.showOptionDialog(ancestor, message,
                    "Select Traces", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options,
                    options[0]);

            if (result == 0) {
                Object[] sel = list.getSelectedValues();
                if (sel.length <= 0) {
                    return null;

                } else {
                    // Got a valid variable name, now get an index for the spatial feature
                    CustomDialog foo = new CustomDialog(ancestor, (String) sel[0], dtsd.getZoneCount());
                    foo.pack();
                    foo.setLocationRelativeTo(ancestor);
                    foo.setVisible(true);

                    String s = foo.getValidatedText();

                    int spaceIndex = Integer.parseInt(s) - 1;
                    String variableName = (String) sel[0];
                    TimeSeries timeSeries = dtsd.getTimeSeries(variableName, spaceIndex);
                    timeSeries.setName(sel[0] + "_" + (spaceIndex + 1) + " " + file_name);
                    //                tst.addTrace(timeSeries);

                    TimeSeries[] ret = new TimeSeries[1];
                    ret[0] = timeSeries;
                    return ret;
                }
            }
        }
        return null;
    }

    public static TimeSeries[] getTraces(Component parent) {
        JFileChooser chooser = new JFileChooser();
        OuiFileFilter filter = new OuiFileFilter();
        filter.addExtension("statvar");
        filter.addExtension("data");
        filter.addExtension("csv");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Load Time Series Trace From File");
        chooser.setCurrentDirectory(last_dir);
        int retval = chooser.showOpenDialog(GuiUtilities.windowFor(parent));

        if (retval == JFileChooser.APPROVE_OPTION) {
            String file_name = chooser.getSelectedFile().getAbsolutePath();
            last_dir = chooser.getSelectedFile();

            String[] var_names;
            MmsStatvarReader msr = null;
            MmsDataFileReader mdfr = null;
            CsvFileReader csvr = null;

            if (file_name.endsWith(".statvar")) {
                msr = new MmsStatvarReader(file_name);
                var_names = msr.getVariableList();

            } else if (file_name.endsWith(".data")) {
                mdfr = new MmsDataFileReader(file_name);
                var_names = mdfr.getVariableList();

            } else if (file_name.endsWith(".csv")) {
                csvr = new CsvFileReader(file_name);
                var_names = csvr.getVariableList();

            } else {
                return null;
            }

//         try {
            Object[] message = new Object[1];
            JPanel jp = new JPanel(new BorderLayout());
            DefaultListModel lm = new DefaultListModel();
            JList list = new JList(lm);
            JScrollPane scroll = new JScrollPane(list);
            scroll.setBorder(new CompoundBorder(
                    new TitledBorder(null, "Trace List",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    parent.getFont()),
                    new EmptyBorder(0, 10, 0, 0)));
            jp.add(scroll);
            lm.removeAllElements();
            String line;
            for (int i = 0; i < var_names.length; i++) {
                lm.addElement(var_names[i]);
            }


            message[0] = jp;

            String[] options = {"OK", "Cancel"};
            int result = JOptionPane.showOptionDialog(parent, message,
                    "Select Traces", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options,
                    options[0]);

            if (result == 0) {
                Object[] sel = list.getSelectedValues();
                if (sel.length <= 0) {
                    return null;

                } else {
                    TimeSeries[] ret = new TimeSeries[sel.length];

                    for (int i = 0; i < sel.length; i++) {
                        String dataName = (String) sel[i];

                        if (msr != null) {
                            ret[i] = msr.getTimeSeries(dataName);
                        } else if (mdfr != null) {
                            ret[i] = mdfr.getTimeSeries(dataName);
                        } else if (csvr != null) {
                            ret[i] = csvr.getTimeSeries(dataName);
                        } else {
                            System.out.println("GetTraces: unknown format");
                        }
                    }
                    return ret;
                }
            }
        }
        return null;
    }
}

class CustomDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener {

    private String typedText = null;
    private JTextField textField;
    private JOptionPane optionPane;
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }

    public CustomDialog(Window window, String varName, int maxIndex) {
        super(window, Dialog.ModalityType.DOCUMENT_MODAL);
//        dd = parent;

        setTitle("Index for " + varName);

        textField = new JTextField(10);

        //Create an array of the text and components to be displayed.
        String msgString1 = "Type in the index of the spatial feature.";
        String msgString2 = "Valid values are 1 to " + maxIndex;
        Object[] array = {msgString1, msgString2, textField};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent ce) {
                textField.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                typedText = textField.getText();
                String ucText = typedText.toUpperCase();
                int retVal = Integer.parseInt(ucText);

                clearAndHide();
            } else {
                typedText = null;
                clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }
}
