package tw.com.justiot.sequencecontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tw.com.justiot.sequencecontrol.eelement.CDOutput;
import tw.com.justiot.sequencecontrol.eelement.CEDevice;
import tw.com.justiot.sequencecontrol.panel.CircuitIconPanel;
import tw.com.justiot.sequencecontrol.panel.EDevicePanel;
import tw.com.justiot.sequencecontrol.panel.EIconPanel;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.ElectricPanel;
import tw.com.justiot.sequencecontrol.panel.WirePanel;
import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;

public class Electrics extends JPanel  implements ChangeListener
  {
	private static final long serialVersionUID = 1L;
    public ElectricPanel electricPanel=null;
    public EDevicePanel edevicePanel=null;
    public Sequence sequence=null;

//    private int PREFERRED_WIDTH = 500;
//    private int PREFERRED_HEIGHT = 350;

    private JLabel statusMode,status;
    private JTextField statusPos;
    private Container contentPane = null;
    public PLCFrame plcFrame=null;
    JTabbedPane toolbarPane=null;
    public JScrollPane deviceScrollPane,electricScrollPane;
    public JSplitPane esplitPane;
    private ElectricListener electriclistener;
    public Electrics(ElectricListener electriclistener)
      {super();
       this.electriclistener=electriclistener;

       setLayout(new BorderLayout());
//       setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));

       electricPanel=new ElectricPanel(electriclistener);
       edevicePanel=new EDevicePanel(electriclistener);
       
       EIconPanel ip2=new EIconPanel("EDevice", electriclistener);
       WirePanel ip4=new WirePanel("Wire", electriclistener);
       CircuitIconPanel ip5=new CircuitIconPanel("EDemos", electriclistener);
       toolbarPane= new JTabbedPane();
       toolbarPane.addTab(Config.getString("electrics.EDevice"),ip2);
       toolbarPane.addTab(Config.getString("electrics.Wire"),ip4);
       toolbarPane.addTab(Config.getString("pneumatics.s19"),ip5);
       toolbarPane.addChangeListener(this);
       JPanel top = new JPanel();
       top.setLayout(new BorderLayout());
       if(electriclistener.getExampleURL()==null) add(toolbarPane, BorderLayout.NORTH);

       deviceScrollPane=new JScrollPane(edevicePanel);
       electricScrollPane = new JScrollPane(electricPanel);

        esplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,electricScrollPane, deviceScrollPane);
        esplitPane.setOneTouchExpandable(true);
        esplitPane.setDividerLocation(Integer.parseInt(electriclistener.getPropertyControl().properties.getProperty("esplitx")));
        Dimension minimumSize = new Dimension(0, 0);
        deviceScrollPane.setMinimumSize(minimumSize);
        add(esplitPane,BorderLayout.CENTER);

        JPanel statusPanel=new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusMode=new JLabel(Config.getString("Status.insert"));
        statusPos=new JTextField("row 0 : col 0");
        statusPos.setEditable(false);
        status=new JLabel();
        statusPanel.add(statusMode);
        statusPanel.add(new JLabel("   "));
        statusPanel.add(statusPos);
        statusPanel.add(new JLabel("   "));
        statusPanel.add(status);
        statusMode.setBackground(Color.white);
        statusPos.setBackground(Color.white);
        status.setBackground(Color.white);
        add(statusPanel, BorderLayout.SOUTH);
        
        edevicePanel.setFocusable(true);
    }

   public void stateChanged(ChangeEvent e)
     {JTabbedPane tab=(JTabbedPane) e.getSource();
       int ind=tab.getSelectedIndex();

     }
    public void changeTab(int ind)
     {toolbarPane.setSelectedIndex(ind);
     }

    public void startTimer(java.util.Timer timer)
     {edevicePanel.startTimer(timer);
      electricPanel.startTimer(timer);
      electriclistener.getESystemPanel().startTimer(timer);
      edevicePanel.setFocusable(true);
     }

    public void setStatus(String s) {status.setText(s);}
    public void setStatusMode(String s) {statusMode.setText(s);}
    public void setStatusPos(String s) {statusPos.setText(s);}
    
  public void forceClear()
   {electricPanel.Clear();
    electriclistener.getEArrays().removeAllEDevice();
    edevicePanel.removeAll();
    electriclistener.getESystemPanel().removeAll();
    electriclistener.getEArrays().ElectricFaceArray.clear();
    CEDevice.clearCount();
    CDOutput.clearCount();
    if(sequence!=null)
     {sequence.setVisible(false);
      sequence.sequencePanel.Clear();
     }
    PneumaticElement.clearCount();
  }

}
