package tw.com.justiot.sequencecontrol.dialog;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;

public class CustomDialog extends JDialog {
    public static final int VALUE_STRING=0;
    public static final int VALUE_INT=1;
    public static final int VALUE_FLOAT=2;

    private int intValue;
    private float floatValue;
    private JOptionPane optionPane;
    private String typedText;
    private int valueType;
    private JTextField textField;

    public String getValidatedText() {
        return typedText;
    }

    public int getInt() {return intValue;}
    public float getFloat() {return floatValue;}

    public void setTextField(String str)
     {textField.setText(str);
      textField.selectAll();
     }

    public CustomDialog(Frame aFrame,String title,String msgString,int vtype) 
//    public CustomDialog()
     {
        super(aFrame, true);
//        super();
//        Image iconImage=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/webladdercad.gif"));
//        setIconImage(iconImage);
        valueType=vtype;
        setTitle(title);
        textField = new JTextField(10);
        Object[] array = {msgString, textField};
        final String btnString1 = Config.getString("Dialog.Enter");
        final String btnString2 = Config.getString("Dialog.Cancel");
        Object[] options = {btnString1, btnString2};
        optionPane = new JOptionPane(array, 
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);
        setContentPane(optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
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

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                optionPane.setValue(btnString1);
            }
        });

        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();

                if (isVisible() 
                 && (e.getSource() == optionPane)
                 && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                     prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                    Object value = optionPane.getValue();

                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
                        //ignore reset
                        return;
                    }

                    // Reset the JOptionPane's value.
                    // If you don't do this, then if the user
                    // presses the same button next time, no
                    // property change event will be fired.
                    optionPane.setValue(
                            JOptionPane.UNINITIALIZED_VALUE);

                    if (value.equals(btnString1)) {
                        typedText = textField.getText();
                        if(valueType==VALUE_INT)
                         {try
                           {intValue=Integer.parseInt(textField.getText());}
                          catch(Exception ie)
                           {textField.selectAll();
                            JOptionPane.showMessageDialog(
                                            CustomDialog.this,
                                            "Sorry, \"" + typedText + "\" "
                                            + "isn't a valid int.\n"
                                            + "Please enter integer value !","Enter again!",
                                            JOptionPane.ERROR_MESSAGE);
                            typedText = null;
                           }
                         }
                        else if(valueType==VALUE_FLOAT)
                         {try
                           {floatValue=Float.parseFloat(textField.getText());}
                          catch(Exception fe)
                           {textField.selectAll();
                            JOptionPane.showMessageDialog(
                                            CustomDialog.this,Config.getString("Dialog.invalidfloat"),
                                            Config.getString("Dialog.reenter"),
                                            JOptionPane.ERROR_MESSAGE);
                            typedText = null;
                           }
                         }
//                        else
                       setVisible(false);
                    } else { 
                        typedText = null;
                        setVisible(false);
                    }
                }
            }
        });
    }
}

