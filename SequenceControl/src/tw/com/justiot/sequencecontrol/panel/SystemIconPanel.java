package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.ElementParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.pelement.Piston;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;

import javax.swing.*;

public class SystemIconPanel extends JPanel
 {
  ButtonGroup toolbarGroup = new ButtonGroup();
  private ElectricListener electriclistener;
  public SystemIconPanel(ElectricListener electriclistener)
   {super();
   this.electriclistener=electriclistener;
    setLayout(new BorderLayout());
    JToolBar toolBar = new JToolBar();
    addButtons(toolBar);
    add(toolBar,BorderLayout.CENTER);
   }

  protected void addButtons(JToolBar toolBar)
   {Insets inset=new Insets(1,1,1,1);
    JButton button = null;

    // EVD42  EVD43  EVS42

    Image realImage=util.loadImage("PistonSystem1","EVD42","realImage","/resources/images/SymSys/PistonSystem1.GIF");
    button=new JButton("EVD42",(Icon) new ImageIcon(realImage));
    button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(Config.getString("tooltip.EVD42"));
        button.setActionCommand("EVD42");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JButton b=(JButton) e.getSource();
              try
               {
            	Piston piston=new Piston(Piston.Type_D42, electriclistener);
            	electriclistener.getESystemPanel().add(piston);

            	Insets insets = electriclistener.getESystemPanel().getInsets();
                Dimension size = piston.getPreferredSize();
                piston.setBounds(5 + insets.left, 5 + insets.top,
                             size.width, size.height);
                electriclistener.getEArrays().PistonArray.add(piston);
            	electriclistener.getESystemPanel().repaint();
               }
              catch(Exception ex)
               {
            	electriclistener.MessageBox("Error:",ex.getMessage());
               }
            }
        });
        toolBar.add(button);
        toolbarGroup.add(button);


        realImage=util.loadImage("PistonSystem2","EVD43","realImage","/resources/images/SymSys/PistonSystem2.GIF");
        button=new JButton("EVD43",(Icon) new ImageIcon(realImage));
        button.setMargin(inset);
            button.setText(null);
            button.setToolTipText(Config.getString("tooltip.EVD43"));
            button.setActionCommand("EVD43");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  JButton b=(JButton) e.getSource();
                  try
                   {
                	Piston piston=new Piston(Piston.Type_43, electriclistener);
                	electriclistener.getESystemPanel().add(piston);

                	Insets insets = electriclistener.getESystemPanel().getInsets();
                    Dimension size = piston.getPreferredSize();
                    piston.setBounds(5 + insets.left, 5 + insets.top,
                                 size.width, size.height);
                    electriclistener.getEArrays().PistonArray.add(piston);
                	electriclistener.getESystemPanel().repaint();
                   }
                  catch(Exception ex)
                   {
                	electriclistener.MessageBox("Error:",ex.getMessage());
                   }
                }
            });
            toolBar.add(button);
            toolbarGroup.add(button);

            realImage=util.loadImage("PistonSystem3","EVS42","realImage","/resources/images/SymSys/PistonSystem3.GIF");
            button=new JButton("EVS42",(Icon) new ImageIcon(realImage));
            button.setMargin(inset);
                button.setText(null);
                button.setToolTipText(Config.getString("tooltip.EVS42"));
                button.setActionCommand("EVS42");
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                      JButton b=(JButton) e.getSource();
                      try
                       {
                    	Piston piston=new Piston(Piston.Type_S42, electriclistener);
                    	electriclistener.getESystemPanel().add(piston);

                    	Insets insets = electriclistener.getESystemPanel().getInsets();
                        Dimension size = piston.getPreferredSize();
                        piston.setBounds(5 + insets.left, 5 + insets.top,
                                     size.width, size.height);
                        electriclistener.getEArrays().PistonArray.add(piston);
                    	electriclistener.getESystemPanel().repaint();
                       }
                      catch(Exception ex)
                       {
                    	electriclistener.MessageBox("Error:",ex.getMessage());
                       }
                    }
                });
                toolBar.add(button);
                toolbarGroup.add(button);
                toolBar.addSeparator();

    Image img=null;

    Object obj=null;
    for( Object key : PneumaticConfig.parameter.keySet() ){
       	obj=PneumaticConfig.parameter.get(key);

      if(!(obj instanceof ElementParameter)) continue;
      ElementParameter ep=(ElementParameter) obj;
      if(ep.modelType.equals("ESystem"))
       {
        img=ep.realImage;
        if(img!=null)
         button=new JButton(ep.modelName,(Icon) new ImageIcon(img));
        else
         button=new JButton(ep.modelName);
        button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(ep.toolTip.replace('_',' '));
        button.setActionCommand(ep.modelName);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JButton b=(JButton) e.getSource();
              try
              {
               electriclistener.createESystem("ESystem",b.getActionCommand());
              }
               catch(Exception ex)
               {
            	   electriclistener.MessageBox("Error:",ex.getMessage());
               }
            }
        });
        toolBar.add(button);
        toolbarGroup.add(button);
       }
     }

    toolBar.addSeparator();
    realImage=util.loadImage("Water","PumpMonitor","realImage","/resources/images/SymSP/pumpc.gif");
    button=new JButton("PumpMonitor",(Icon) new ImageIcon(realImage));
    button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(Config.getString("tooltip.PumpMonitor"));
        button.setActionCommand("PumpMonitor");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              JButton b=(JButton) e.getSource();
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

    Image realImage2=util.loadImage("Water","WaterTank","realImage","/resources/images/SymSP/watertankc.gif");
    button=new JButton("WaterTank",(Icon) new ImageIcon(realImage2));
        button.setMargin(inset);
            button.setText(null);
            button.setToolTipText(Config.getString("tooltip.WaterTank"));
            button.setActionCommand("WaterTank");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  JButton b=(JButton) e.getSource();
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
