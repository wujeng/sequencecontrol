package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class PIconPanel extends JPanel
 {public static final int Command_createElement=1;

  private static final int PREFERRED_WIDTH = 200;
   private static final int PREFERRED_HEIGHT = 15;

  String modelType;
  ButtonGroup toolbarGroup = new ButtonGroup();
  private PneumaticListener pneumaticlistener;
  public PIconPanel(String type, PneumaticListener pneumaticlistener)
   {super();
   this.pneumaticlistener=pneumaticlistener;
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
        
      if(!(obj instanceof ElementParameter)) continue;
      ElementParameter ep=(ElementParameter) obj;
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
              {
  //             pneumaticlistener.createElement(modelType,b.getActionCommand());

              }
               catch(Exception ex)
               {
            	pneumaticlistener.MessageBox("Error:",ex.getMessage());
               }

            }
        });
        toolBar.add(button);
        toolbarGroup.add(button);
       }
     }
   }
 }
