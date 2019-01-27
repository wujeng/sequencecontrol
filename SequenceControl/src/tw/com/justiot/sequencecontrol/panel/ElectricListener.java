package tw.com.justiot.sequencecontrol.panel;

import java.awt.Image;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.EArrays;
import tw.com.justiot.sequencecontrol.PropertyControl;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.eelement.ESystemBase;
import tw.com.justiot.sequencecontrol.pelement.EValve;

public interface ElectricListener {

	  public boolean getStopRun();
	  public int getRunPeriod();

	  public Image getIconImage();
	  public URL getExampleURL();
	  public char getMnemonic(String str);
	  
	  public void setModified(boolean b);
	  public boolean getModified();
	  public void addCommand(Command com);
	  public JFrame getFrame();
	  public void MessageBox(String des,String type);
	  public void setStatus(String str);
//	  public void repaint();
	  
	  public void removeEValve(EValve element);
	  public void refreshSystemCombo();
	  
	  public java.util.Timer getTimer();
	  public ElectricPanel getElectricPanel();
	  public SequencePanel getSequencePanel();
	  public EDevicePanel getEDevicePanel();
	  public ESystemPanel getESystemPanel();
	  public int getOpMode();
	  public int getSimulationMode();
//	  public boolean getStepFlag();
//	  public void setStepFlag(boolean b);
	  public boolean hasElectrics();
	  public boolean hasSequence();
	  
	  public void loadCircuit(String str);
	  public void createEDevice(String modelType,String modelName);

	  public void createESystem(String modelType,String command);
	  public void createWaterElement(String model, String command);
	  
	  public void setElectricsStatusMode(String str);
	  public void setElectricsStatusPos(String str);
	  public void setSequenceStatusPos(String str);
	  public void setSequenceStatus(String str);
	  
	  public void sequenceRefreshSystemCombo();
	  public void deleteESystemBase(ESystemBase esystem);
	  
	  public JScrollPane getElectricsDeviceScrollPane();
	  public JScrollPane getElectricsElectricScrollPane(); 
	  
	  public String saveToString(boolean b) throws Exception;
	  
	  public void getLimitswitchLocation(EDevice ed);
	  public void setActuatorPosAccording2LS();
	  public int getLSPosition(EDevice ed);
	  
	  public EArrays getEArrays();
	  public void simulationStop();
	  
	  public PropertyControl getPropertyControl();
}
