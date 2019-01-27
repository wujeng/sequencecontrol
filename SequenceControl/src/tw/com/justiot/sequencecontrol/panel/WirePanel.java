package tw.com.justiot.sequencecontrol.panel;

import tw.com.justiot.sequencecontrol.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WirePanel extends JPanel
 {
  private String modelType;
  private ButtonGroup toolbarGroup = new ButtonGroup();
  private static String[] name={"Power","Hline","Vline","Tshpae","RTshape","RLshape","Lshape","LTshape","Ground"};
  private ElectricListener electriclistener;
  public WirePanel(String type, ElectricListener electriclistener)
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
    for(int i=0;i<ElectricPanel.iwireImage.length;i++)
     {img=ElectricPanel.iwireImage[i];
       if(img!=null)
         button=new JToggleButton(name[i],(Icon) new ImageIcon(img));
        else
         button=new JToggleButton(name[i]);
        button.setMargin(inset);
        button.setText(null);
        button.setToolTipText(name[i]);
        button.setActionCommand(Integer.toString(i));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
	          JToggleButton b=(JToggleButton) e.getSource();
              int ind=Integer.parseInt(b.getActionCommand());
              electriclistener.getElectricPanel().AddCell(LadderCell.T_Wire,ind,null,null,true);
//              if(panelListener!=null) panelListener.panelAction("Wire","","",ind);
/*
//                if(!pneumatics.legal) return;
        if(pneumatics.isApplet() && !pneumatics.register.isLegal)
         {JOptionPane.showMessageDialog(new Frame(),
              pneumatics.getString("pneumatics.s1")+" certain Host "+pneumatics.getString("pneumatics.s2"));
           return;
         }

int ver=pneumatics.register.version;
              if(pneumatics.isApplet() && !(ver==Register.Ver_Electrics_Server || ver==Register.Ver_Electrics_Control ||
                  ver==Register.Ver_Electrics_Collaborative || ver==Register.Ver_Electrics_Demo  || ver==Register.Ver_Pneumatics_Demo)) return;

                JToggleButton b=(JToggleButton) e.getSource();
                int ind=Integer.parseInt(b.getActionCommand());
                electricPanel.AddCell(LadderCell.T_Wire,ind,null,null,true);
                if(pneumatics.allClient.connected)
                 {pneumatics.writeEvent(new PneumaticsEvent(PneumaticsEvent.S_wirePanel,Integer.toString(ind),null,null));
                  pneumatics.allClient.writeByteArray();
                 }
*/
              }
            });
        toolBar.add(button);
        toolbarGroup.add(button);
       }
     }
   }
