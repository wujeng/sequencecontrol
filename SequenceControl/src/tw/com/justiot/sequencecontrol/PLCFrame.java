package tw.com.justiot.sequencecontrol;

import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.pelement.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class PLCFrame extends JFrame {

    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 1L;
	// The preferred size of the demo
    private int PREFERRED_WIDTH = 200;
    private int PREFERRED_HEIGHT = 300;

    // Box spacers
 //   private Dimension HGAP = new Dimension(1,5);
 //   private Dimension VGAP = new Dimension(5,1);


     private PLC plc;
     public JComboBox cb=null;
     private JTextArea textArea=null;
     JScrollPane scrollPane=null;
    /**
     * PLCFrame Constructor
     */

    public PLCFrame(PLC pl, ElectricListener electriclistener)
      {super();
        this.plc=pl;
        setIconImage(electriclistener.getIconImage());
        WindowListener l = new WindowAdapter() {
	   public void windowClosing(WindowEvent e) {
//		System.exit(0);
                          hide();
	    }
	};
          addWindowListener(l);
          setTitle(Config.getString("plcframe.title"));
          cb=plc.createPLCComboBox();
          cb.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
//System.err.println("itemchanged");
                     String aName = (String) cb.getSelectedItem();
                     plc.changeModel(aName);
                     textArea.setText(plc.getProgramString());
                 }
               });
          textArea=new JTextArea();
          textArea.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(true);

          scrollPane = new JScrollPane(textArea);
          textArea.setText(plc.getProgramString());
          Container contentPane=getContentPane();
          contentPane.add(cb, BorderLayout.NORTH);
          contentPane.add(scrollPane, BorderLayout.CENTER);
          pack();
          scrollPane.setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
/*
          if(webLadderCAD.propertyControl.winPLCw==0 || webLadderCAD.propertyControl.winPLCh==0)
           {Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
             setLocation(screenSize.width/2 - getSize().width/2,screenSize.height/2 - getSize().height/2);
           }
          else
           {setLocation(webLadderCAD.propertyControl.winPLCx,webLadderCAD.propertyControl.winPLCy);
             setSize(webLadderCAD.propertyControl.winPLCw,webLadderCAD.propertyControl.winPLCh);
           }
*/
//System.err.println("before show");
          show();
    }
}
