package tw.com.justiot.sequencecontrol.dialog;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import javax.swing.*;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class KeyDialog extends JDialog {
    private JOptionPane optionPane;
    private int key;
    private String keyString;
    private JComboBox keyList; 

    public int getKey() {return key;}
    public String getKeyString() {return keyString;}

    public KeyDialog(JFrame frame) 
     {super(frame, true);
//      Image iconImage=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/webladdercad.gif"));
//      setIconImage(iconImage);
//      setTitle("Set ActivateKey");
      setTitle(Config.getString("KeyDialog.SetActivateKey"));
//      final String btnString1 = "Enter";
//      final String btnString2 = "Cancel";
      final String btnString1 = Config.getString("Dialog.Enter");
      final String btnString2 = Config.getString("Dialog.Cancel");
      String[] keys=new String[]{" ","F1","F2","F3","F4","F5","F6","F7","F8","F9","F10","F11","F12"};
      final int[] keysindex=new int[]{-1,KeyEvent.VK_F1,KeyEvent.VK_F2,KeyEvent.VK_F3,KeyEvent.VK_F4,
                                KeyEvent.VK_F5,KeyEvent.VK_F6,KeyEvent.VK_F7,KeyEvent.VK_F8,
                   KeyEvent.VK_F9,KeyEvent.VK_F10,KeyEvent.VK_F11,KeyEvent.VK_F12};
      keyList = new JComboBox(keys);
      keyList.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              JComboBox cb = (JComboBox)e.getSource();
              key=keysindex[cb.getSelectedIndex()];
              keyString=(String) cb.getSelectedItem();
              optionPane.setValue(btnString1);
          }
      });
      Object[] array =new Object[]{Config.getString("KeyDialog.SelectActivateKey"),keyList};
      
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
                        setVisible(false);
                    } else { 
                        key=-1;
                        setVisible(false);
                    }
                }
            }
        });
    }
}

