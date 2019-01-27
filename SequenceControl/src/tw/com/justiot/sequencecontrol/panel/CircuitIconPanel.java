package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
//import java.lang.reflect.*;

public class CircuitIconPanel extends JPanel
 {private String modelType;
  private ElectricListener electriclistener;
  ButtonGroup toolbarGroup = new ButtonGroup();
  public CircuitIconPanel(String type, ElectricListener electriclistener)
   {super();
    this.electriclistener=electriclistener;
    modelType=type;
    setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    addButtons(toolBar);
    add(toolBar,BorderLayout.CENTER);
   }
  protected void addButtons(JToolBar toolBar)
   {Insets inset=new Insets(1,1,1,1);
    JToggleButton button = null;
    Image img=null;
  for( Object key : PneumaticConfig.circuit.keySet() ){
   	CircuitParameter cp=(CircuitParameter) PneumaticConfig.circuit.get(key);
    if(cp.modelType.equals(modelType))
     {
      img=cp.image;
      if(img!=null)
       button=new JToggleButton(cp.modelName,(Icon) new ImageIcon(img));
      else
       button=new JToggleButton(cp.modelName);
      button.setMargin(inset);
      button.setText(null);
      button.setToolTipText(cp.toolTip.replace('_',' '));
      button.setActionCommand(cp.modelName);
      button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
	          JToggleButton b=(JToggleButton) e.getSource();
            String modelName=b.getActionCommand();
            try
            {
             electriclistener.loadCircuit(modelName);
            }
             catch(Exception ex)
             {
          	   electriclistener.MessageBox("Error:",ex.getMessage());
             }

//	          WebLadderCAD.self.panelAction("CircuitIcon",modelName,"",-1);

          }
      });
      toolBar.add(button);
      toolbarGroup.add(button);
     }
   }
    
    
  /*  
    Enumeration e = PneumaticConfig.circuit.elements();
    while(e.hasMoreElements())
     {CircuitParameter cp=(CircuitParameter) e.nextElement();
      if(cp.modelType.equals(modelType))
       {
        img=cp.image;
        if(img!=null)
         button=new JToggleButton(cp.modelName,(Icon) new ImageIcon(img));
        else
         button=new JToggleButton(cp.modelName);
        button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(cp.toolTip.replace('_',' '));
        button.setActionCommand(cp.modelName);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	          JToggleButton b=(JToggleButton) e.getSource();
              String modelName=b.getActionCommand();
              try
              {
               electriclistener.loadCircuit(modelName);
              }
               catch(Exception ex)
               {
            	   electriclistener.MessageBox("Error:",ex.getMessage());
               }

//	          WebLadderCAD.self.panelAction("CircuitIcon",modelName,"",-1);

            }
        });
        toolBar.add(button);
        toolbarGroup.add(button);
       }
     }
    */
   }
 }
