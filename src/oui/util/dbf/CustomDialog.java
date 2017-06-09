package oui.util.dbf;

import gov.usgs.cawsc.gui.GuiUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JTable;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

class CustomDialog extends JDialog {
   private Object ret_value = null;
   private JOptionPane optionPane;

   public CustomDialog(String message, JTable t) {
      super(GuiUtilities.windowFor(t));

      final JTable table = t;
      final JTextField textField = new JTextField(10);
      Object[] array = {message, textField};

      final String btnString1 = "Enter";
      final String btnString2 = "Cancel";
      Object[] options = {btnString1, btnString2};

      setModal(true);

      optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION, null, options,
                                    options[0]);
      setContentPane(optionPane);
      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent we) {
            optionPane.setValue (new Integer (JOptionPane.CLOSED_OPTION));
         }
      });

      textField.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            optionPane.setValue(btnString1);
         }
      });

      optionPane.addPropertyChangeListener(new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent e) {

            String prop = e.getPropertyName();

            if (isVisible() && (e.getSource() == optionPane)
                     && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                     prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {

               Object value = optionPane.getValue();

               if (value == JOptionPane.UNINITIALIZED_VALUE) {
                  return;
               }

               optionPane.setValue (JOptionPane.UNINITIALIZED_VALUE);

               if (value.equals (btnString1)) {
                  String typedText = textField.getText();
                  int[] sel = table.getSelectedColumns();
                  for (int i = 0; i < sel.length; i++) {
                     Class cl = table.getColumnClass(sel[i]);
                     if (cl == Integer.class) {
                        try {
//                           int val = Integer.valueOf(typedText).intValue();
                           ret_value = new Integer (typedText);
//
//                                 if (val > max) {
//                                    val = max;
//                                 } else if (val < min) {
//                                    val = min;
//                                 }
                           } catch (NumberFormatException ex) {
                              ret_value = null;
                              JOptionPane.showMessageDialog(GuiUtilities.windowFor(table), "Please check your data types -- Expecting an integer");
                              break;
                           }

                     } else if (cl == Float.class) {
                        try {
//                           float val = Float.valueOf(typedText).floatValue();
                           ret_value = new Float (typedText);
//
//                                 if (val > max) {
//                                    val = max;
//                                 } else if (val < min) {
//                                    val = min;
//                                 }
                        } catch (NumberFormatException ex) {
                           ret_value = null;
                           JOptionPane.showMessageDialog(GuiUtilities.windowFor(table), "Please check your data types -- Expecting a float");
                           break;
                        }
                     } else {
                        System.out.println("Data type " + cl.toString() + " is unknown.");
                        ret_value = null;
                     }
                     setVisible(false);
                  }
               } else {
                  ret_value = null;
                  setVisible(false);
               }
            }
         }
      });
   }

   public Object getValidatedInput() {
      return ret_value;
   }

}
