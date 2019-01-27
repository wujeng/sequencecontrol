package tw.com.justiot.sequencecontrol.dialog;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import javax.swing.*;
import java.util.*;
import java.beans.*; //Property change stuff
import java.awt.*;
import java.awt.event.*;

public class ConditionDialog extends JDialog {
    private Hashtable startHash=new Hashtable();
    private Hashtable stopHash=new Hashtable();
    private Hashtable choiceHash=new Hashtable();
    private Hashtable counterHash=new Hashtable();
    private JCheckBox checkBox;
    private JComboBox startList;
    private JComboBox stopList;
    private JComboBox choiceList;
    private JComboBox counterList;
    private ButtonGroup group;
    private SequencePanel sequencePanel=null;
    ArrayList EDeviceArray;
    public ConditionDialog(Frame aFrame,SequencePanel seqp,ArrayList eds) 
     {super(aFrame,Config.getString("ConditionDialog.title"));
       sequencePanel=seqp;
       EDeviceArray=eds;
//       setIconImage(sequence.electrics.pneumatics.iconImage);
       EDevice ed=null;
       for(int i=0;i<EDeviceArray.size();i++)
        {ed=(EDevice) EDeviceArray.get(i);
          if(ed.ced.actionType==CEDevice.TYPE_MANUAL_AUTO && ed.ced.name!=null && ed.ced.name.length()>0)
            {startHash.put(ed.ced.name,ed);
              stopHash.put(ed.ced.name,ed);
            }
          if(ed.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE && ed.ced.name!=null && ed.ced.name.length()>0)
            choiceHash.put(ed.ced.name,ed);
          if(ed.ced.actionType==CEDevice.TYPE_COUNTER && ed.ced.name!=null && ed.ced.name.length()>0)
            counterHash.put(ed.ced.name,ed);
        }
       Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel p1=new JPanel();
         p1.setLayout(new GridLayout(0,1));
         JPanel p11=new JPanel();
          p11.setLayout(new GridLayout(1,2));
          p11.add(new JLabel(Config.getString("ConditionDialog.StartButton")));
           String[] chstr=new String[startHash.size()];
           Enumeration e = startHash.keys();
           int k=0;
           while (e.hasMoreElements()) 
            {chstr[k] = (String) e.nextElement();
              k++;
            }
           startList = new JComboBox(chstr);
/*
            startList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
      //               JComboBox cb = (JComboBox)e.getSource();
      //               String petName = (String)cb.getSelectedItem();
      //                 picture.setIcon(new ImageIcon("images/" + petName + ".gif"));
                 }
               });
*/
          p11.add(startList);
        p1.add(p11);
          JPanel p12= new JPanel();
           checkBox = new JCheckBox(Config.getString("ConditionDialog.StopButton"));
           checkBox.setSelected(false);
           p12.setLayout(new GridLayout(1,2));
           p12.add(checkBox);
           chstr=new String[stopHash.size()];
           e = stopHash.keys();
           k=0;
           while (e.hasMoreElements()) 
            {chstr[k] = (String) e.nextElement();
              k++;
            }
           stopList = new JComboBox(chstr);
/*
            stopList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
      //               JComboBox cb = (JComboBox)e.getSource();
      //               String petName = (String)cb.getSelectedItem();
      //                 picture.setIcon(new ImageIcon("images/" + petName + ".gif"));
                 }
               });
*/
           p12.add(stopList);
         p1.add(p12);
       contentPane.add(p1,BorderLayout.NORTH);
          final int numButtons = 4;
          JRadioButton[] radioButtons = new JRadioButton[numButtons];
          group = new ButtonGroup();
          final String sCommand = "simple";
          final String cCommand = "continuous";
          final String chCommand = "choice";
          final String counterCommand = "counter";
          
          radioButtons[0] = new JRadioButton(Config.getString("ConditionDialog.Simplemotion"));
          radioButtons[0].setActionCommand(sCommand);
          radioButtons[1] = new JRadioButton(Config.getString("ConditionDialog.Continuousmotion"));
          radioButtons[1].setActionCommand(cCommand);
          radioButtons[2] = new JRadioButton(Config.getString("ConditionDialog.SCoption"));
          radioButtons[2].setActionCommand(chCommand);
          radioButtons[3] = new JRadioButton(Config.getString("ConditionDialog.Repeated"));
          radioButtons[3].setActionCommand(counterCommand);
          for (int i = 0; i < numButtons; i++) {
              group.add(radioButtons[i]);
          }
          radioButtons[0].setSelected(true);
        JPanel p2=new JPanel();
          p2.setLayout(new GridLayout(0, 1));
          p2.add(radioButtons[0]);
          p2.add(radioButtons[1]);
          JPanel p23=new JPanel();
            p23.setLayout(new GridLayout(1,2));
            p23.add(radioButtons[2]);
            chstr=new String[choiceHash.size()];
            e = choiceHash.keys();
            k=0;
            while (e.hasMoreElements()) 
             {chstr[k] = (String) e.nextElement();
               k++;
             }
            choiceList = new JComboBox(chstr);
/*
            choiceList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
      //               JComboBox cb = (JComboBox)e.getSource();
      //               String petName = (String)cb.getSelectedItem();
      //                 picture.setIcon(new ImageIcon("images/" + petName + ".gif"));
                 }
               });
*/
           p23.add(choiceList);
          p2.add(p23);

        JPanel p24=new JPanel();
            p24.setLayout(new GridLayout(1,2));
            p24.add(radioButtons[3]);
            chstr=new String[counterHash.size()];
            e = counterHash.keys();
            k=0;
            while (e.hasMoreElements()) 
             {chstr[k] = (String) e.nextElement();
               k++;
             }
            counterList = new JComboBox(chstr);
/*
            counterList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
        //             JComboBox cb = (JComboBox)e.getSource();
          //           String petName = (String)cb.getSelectedItem();
            //           picture.setIcon(new ImageIcon("images/" + petName + ".gif"));
                 }
               });
*/
           p24.add(counterList);
          p2.add(p24);
       contentPane.add(p2,BorderLayout.CENTER);
         JPanel p3=new JPanel();
          p3.setLayout(new FlowLayout());
          JButton b1=new JButton(Config.getString("Dialog.Enter"));
          b1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   String str=(String) startList.getSelectedItem();
                   if(str==null) 
                    {MessageBox(Config.getString("ConditionDialog.selectstart"),Config.getString("ConditionDialog.error"));
                      return;
                    }
                   else 
                     sequencePanel.CondStart=(EDevice) startHash.get(str);

                   if(checkBox.isSelected()) 
                    {str=(String) stopList.getSelectedItem();
                      if(str==null) 
                       {MessageBox(Config.getString("ConditionDialog.selectstop"),Config.getString("ConditionDialog.error"));
                         return;
                       }
                      else 
                        sequencePanel.CondStop=(EDevice) stopHash.get(str);
                    }
                   else sequencePanel.CondStop=null;
                   String com=group.getSelection().getActionCommand();
                   if(com.equals(sCommand)) sequencePanel.OperationMode=0;
                   else if(com.equals(cCommand)) sequencePanel.OperationMode=1;
                   else if(com.equals(chCommand)) sequencePanel.OperationMode=2;
                   else if(com.equals(counterCommand)) sequencePanel.OperationMode=3;
                   else  sequencePanel.OperationMode=0;

                   if(sequencePanel.OperationMode==2) 
                    {str=(String) choiceList.getSelectedItem();
                      if(str==null) 
                       {MessageBox(Config.getString("ConditionDialog.selecttoggle"),Config.getString("ConditionDialog.error"));
                         return;
                       }
                      else 
                        sequencePanel.CondChoice=(EDevice) choiceHash.get(str);
                    }
                   if(sequencePanel.OperationMode==3) 
                    {str=(String) counterList.getSelectedItem();
                      if(str==null) 
                       {MessageBox(Config.getString("ConditionDialog.selectcounter"),Config.getString("ConditionDialog.error"));
                         return;
                       }
                      else 
                        sequencePanel.CondCounter=(EDevice) counterHash.get(str);
                    }
                   hide();
                   dispose();
                 }
               });
          JButton b2=new JButton(Config.getString("Dialog.Cancel"));
          b2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   hide();
                   dispose();
                 }
               });
            p3.add(b1);
            p3.add(b2);
        contentPane.add(p3,BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                hide();
                dispose();
            }
        });

        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width/2 - getSize().width/2,
		screenSize.height/2 - getSize().height/2);
        setVisible(true);
        show();
    }

   private void MessageBox(String des,String type)
 {JOptionPane.showMessageDialog(this,type+":"+des);
 }
}

