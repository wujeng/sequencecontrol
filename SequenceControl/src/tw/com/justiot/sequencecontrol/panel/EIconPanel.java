package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EIconPanel extends JPanel
 {public static final int Command_createEDevice=1;

  private static final int PREFERRED_WIDTH = 200;
   private static final int PREFERRED_HEIGHT = 15;

  String modelType;
  ButtonGroup toolbarGroup = new ButtonGroup();
  private ElectricListener electriclistener;
  public EIconPanel(String type, ElectricListener electriclistener)
   {super();
    this.electriclistener=electriclistener;
    modelType=type;
    setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    addButtons(toolBar);
    add(toolBar,BorderLayout.CENTER);
//    setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
   }
  protected void addButtons(JToolBar toolBar)
   {Insets inset=new Insets(1,1,1,1);
    JToggleButton button = null;
    Image img=null;
    
    Object obj=null;
    for( Object key : PneumaticConfig.parameter.keySet() ){
       	obj=PneumaticConfig.parameter.get(key);
        
      if(!(obj instanceof EDeviceParameter)) continue;
      EDeviceParameter ep=(EDeviceParameter) obj;
      if(ep.modelType.equals(modelType))
       {
        img=ep.realImage;
        if(img!=null)
         button=new JToggleButton(ep.modelName,(Icon) new ImageIcon(img));
        else
         button=new JToggleButton(ep.modelName);
        button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(ep.toolTip.replace('_',' '));
        button.setActionCommand(ep.modelName);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        JToggleButton b=(JToggleButton) e.getSource();
        try
        {electriclistener.createEDevice(modelType,b.getActionCommand());

        }
         catch(Exception ex)
         {
      	   electriclistener.MessageBox("Error:",ex.getMessage());
         }
//        if(panelListener!=null) panelListener.panelAction("EIcon",modelType, b.getActionCommand(),-1);
/*
        if(!pneumatics.register.checkLimit(modelType, b.getActionCommand())) return;
         EDevice ed=Creater.instanceEDevice(modelType,electricPanel,b.getActionCommand());
              if(ed!=null)
               {if(pneumatics.edevicePanel!=null)
                  {pneumatics.edevicePanel.addEDevice(ed);
                    if(ed.actionType==EDevice.TYPE_TIMER && pneumatics.sequence !=null)
                      pneumatics.sequence.refreshSystemCombo();
                    if(pneumatics.allClient.connected)
                     {pneumatics.writeEvent(new PneumaticsEvent(PneumaticsEvent.S_eIconPanel,b.getActionCommand(),null,modelType));
                       pneumatics.allClient.writeByteArray();
                     }
                    pneumatics.edevicePanel.repaint();
                  }
               }
*/
            }
        });
        toolBar.add(button);
        toolbarGroup.add(button);
       }
     }
   }

 }
