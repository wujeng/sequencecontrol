package tw.com.justiot.sequencecontrol;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import tw.com.justiot.sequencecontrol.panel.ESystemPanel;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.SystemIconPanel;

public class ESystems extends JPanel
  {
    private JTabbedPane toolbarPane=null;
    public ESystemPanel esystemPanel=null;
    public ESystems(ElectricListener electriclistener)
     { super();
       setLayout(new BorderLayout());
       esystemPanel=new ESystemPanel(electriclistener);
       JScrollPane scroller = new JScrollPane(esystemPanel);
//       scroller.setPreferredSize(new Dimension(200,200));

       add(scroller, BorderLayout.CENTER);
       SystemIconPanel ip=new SystemIconPanel(electriclistener);
       add(ip, BorderLayout.NORTH);
     }

}
