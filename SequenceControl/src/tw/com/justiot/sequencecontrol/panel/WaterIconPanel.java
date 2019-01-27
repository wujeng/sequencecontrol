package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WaterIconPanel extends JPanel
 {public static final int Command_createElement=1;

  private static final int PREFERRED_WIDTH = 200;
  private static final int PREFERRED_HEIGHT = 15;

  String modelType;
  ButtonGroup toolbarGroup = new ButtonGroup();
  private ElectricListener electriclistener;
  public WaterIconPanel(String type, ElectricListener electriclistener)
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
    String modelType="Water";
    String modelName="PumpMonitor";

    Image realImage=util.loadImage(modelType,modelName,"realImage","/resources/images/SymSP/pumpc.gif");
    button=new JToggleButton(modelName,(Icon) new ImageIcon(realImage));
    button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(Config.getString("tooltip.PumpMonitor"));
        button.setActionCommand(modelName);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JToggleButton b=(JToggleButton) e.getSource();
              try
               {
                electriclistener.createWaterElement("Water", b.getActionCommand());
               }
              catch(Exception ex)
               {
            	electriclistener.MessageBox("Error:",ex.getMessage());
               }
//              if(panelListener!=null) panelListener.panelAction("Water","Water", b.getActionCommand(),-1);
            }
        });
        toolBar.add(button);
        toolbarGroup.add(button);

    modelName="WaterTank";
    Image realImage2=util.loadImage(modelType,modelName,"realImage","/resources/images/SymSP/watertankc.gif");
    button=new JToggleButton(modelName,(Icon) new ImageIcon(realImage2));
        button.setMargin(inset);
            button.setText(null);
            button.setToolTipText(Config.getString("tooltip.WaterTank"));
            button.setActionCommand(modelName);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  JToggleButton b=(JToggleButton) e.getSource();
                  try
                   {
                    electriclistener.createWaterElement("Water", b.getActionCommand());
                   }
                  catch(Exception ex)
                   {
                	electriclistener.MessageBox("Error:",ex.getMessage());
                   }
//                  if(panelListener!=null) panelListener.panelAction("Water","Water", b.getActionCommand(),-1);
                }
            });
            toolBar.add(button);
            toolbarGroup.add(button);

   }
 }
