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

public class DBDialog extends JDialog {
    private JOptionPane optionPane;
    private String text="";
    private JTextField textField;
    private JComboBox fileList; 
    private boolean hasText;
    private String[] allFiles;
    private ArrayList flist;
    
    public Object getObject() 
     {if(flist==null || flist.size()==0) return null;
      return flist.get(fileList.getSelectedIndex());
     }
    
    public String getText() {
        if(hasText)
         return textField.getText();
        else
         {if(allFiles==null || allFiles.length==0) return null;
           if(fileList.getItemCount()>0 && text!=null && text.length()==0)
            text=(String) fileList.getItemAt(0);
          //return (String)fileList.getSelectedItem();
          return text;
         }
    }

    public void setText(String str)
     {if(str==null || str.length()==0) return;
      if(hasText)
       {textField.setText(str);
        textField.selectAll();
       }
      else
        text=str;
     }

    public DBDialog(Frame aFrame,ArrayList flist,String title,boolean hText) 
     {
        super(aFrame, true);
//        Image iconImage=Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/webladdercad.gif"));
//        setIconImage(iconImage);
        this.flist=flist;
        setTitle(title);
        this.hasText=hText;
//        final String btnString1 = "Yes";
//        final String btnString2 = "No";
        final String btnString1 = Config.getString("Dialog.Yes");
        final String btnString2 = Config.getString("Dialog.No");
        if(hasText) textField = new JTextField(10);
        if(flist!=null && flist.size()>0)
         {allFiles=new String[flist.size()];
          for(int i=0;i<flist.size();i++)
           allFiles[i]=(String) flist.get(i).toString();
         }  
        if(flist!=null && flist.size()>0) fileList = new JComboBox(allFiles);
        else fileList=new JComboBox();
        if(fileList.getItemCount()>0) fileList.setSelectedIndex(0);
        fileList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                setText((String)cb.getSelectedItem());
                
//                optionPane.setValue(btnString1);
            }
        });
        Object[] array=null;
        if(hasText) array =new Object[]{"Select file or input filename?",fileList, textField};
        else array=new Object[]{Config.getString("Dialog.Select")+" ?",fileList};
        
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
        if(hasText)
         {textField.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 optionPane.setValue(btnString1);
             }
          });
         }
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();

                 if (isVisible() && (e.getSource() == optionPane))
                  {Object value = optionPane.getValue();
                    if(value instanceof Integer)
                     {if(((Integer)value).intValue()==JOptionPane.NO_OPTION)
                       {text=""; return;}
                     }
                 }

                if (isVisible() 
                 && (e.getSource() == optionPane)
                 && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                     prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                    Object value = optionPane.getValue();

                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
                        //ignore reset
//                        text=null;
                        return;
                    }
                    // Reset the JOptionPane's value.
                    // If you don't do this, then if the user
                    // presses the same button next time, no
                    // property change event will be fired.
                    optionPane.setValue(
                            JOptionPane.UNINITIALIZED_VALUE);

                    if (value.equals(btnString1)) {
                        text = getText();                        
                        setVisible(false);
                    } else { 
                        text = "";
                        setVisible(false);
                    }
                }
            }
        });
    }
}

