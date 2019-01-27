package tw.com.justiot.sequencecontrol.panel;

import java.awt.Image;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.eelement.ESystemBase;
import tw.com.justiot.sequencecontrol.pelement.EValve;

public interface PneumaticListener {	  
	  public PneumaticPanel getPneumaticPanel();
	  public void setModified(boolean b);
	  public boolean getModified();
	  public void addCommand(Command com);
	  public JFrame getFrame();
	  public void MessageBox(String des,String type);
	  public void setStatus(String str);
	  public void repaint();
	  
	  public void loadCircuit(String str);
	  
	  public boolean isPause();
}
