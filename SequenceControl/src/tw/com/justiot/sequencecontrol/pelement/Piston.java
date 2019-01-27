package tw.com.justiot.sequencecontrol.pelement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.Creater;
import tw.com.justiot.sequencecontrol.EArrays;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.eelement.CDOutput;
import tw.com.justiot.sequencecontrol.eelement.CDOutputListener;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.eelement.ESymbol;
import tw.com.justiot.sequencecontrol.eelement.ESystemBase;
import tw.com.justiot.sequencecontrol.eelement.ElectricFace;
import tw.com.justiot.sequencecontrol.eelement.WaterTank;
import tw.com.justiot.sequencecontrol.panel.EDevicePanel;
import tw.com.justiot.sequencecontrol.panel.ESystemPanel;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.ElectricPanel;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.panel.PneumaticPanel;
import tw.com.justiot.sequencecontrol.panel.SequencePanel;


public class Piston extends JPanel implements PneumaticListener, MouseListener,MouseMotionListener,ActionListener {
	public static final int Type_D42=0;
	public static final int Type_43=1;
	public static final int Type_S42=2;
	private int Type = 0;
	public static final int Data_Element=0;
	public static final int Data_Line=1;
	public static final int Data_Limitswitch=2;
	public static int count=0;
	public static void main(String[] args) {
		new PneumaticConfig("tw"); 
		  Piston piston=new Piston(Piston.Type_D42, null);
			JFrame frame = new JFrame();
		    frame.getContentPane().add(piston, BorderLayout.CENTER);
	      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	      frame.pack();
	      frame.setVisible(true);  
	}

    private PneumaticPanel pneumaticPanel;
    public Actuator actuator;
    private ElectricListener electriclistener;
    private boolean movable=true;
    public JPopupMenu popup;
    private JMenuItem m;
    private boolean popupon;
    private int Width,Height,imagewid,imagehgt;
    private boolean drag=false;
    private int xm,ym,pressedx,pressedy,pressedPosx,pressedPosy;
    private double ratio=1.0;
    private String name;
    private EDevice ls1;  // far end limitswitch
    private EDevice ls2;  // near end limitswitch
    
	public Piston(int type, ElectricListener electriclistener) {
	  super();
	  count++;
	  this.electriclistener=electriclistener;
	  this.Type = type;
	  pneumaticPanel=new PneumaticPanel(this, electriclistener);
	  Border blackline, raisedetched, loweredetched,
      raisedbevel, loweredbevel, empty;
	  blackline = BorderFactory.createLineBorder(Color.black);
	  raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	  loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	  raisedbevel = BorderFactory.createRaisedBevelBorder();
	  loweredbevel = BorderFactory.createLoweredBevelBorder();
	  empty = BorderFactory.createEmptyBorder();
//	System.out.println("Type:"+Type);  
	  String dir=Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+"piston"+File.separator;
	  String fstr="piston1.pc";
	  if(Type==Type_43) fstr="piston2.pc";
	  else if(Type==Type_S42) fstr="piston3.pc";
//	  try {
		open(new File(dir+fstr));
//	  } catch(Exception e) {
//		  e.printStackTrace();
//		  System.out.println(dir+fstr+":"+e.getMessage());
//	  }
	  setLayout(new BorderLayout());
	  add(pneumaticPanel, BorderLayout.CENTER);
	  setPreferredSize(new Dimension(120, 120));
	  setBorder(raisedetched);
//System.out.println("read done.");  
	  actuator=getActuator();
	  
	  ls1=Creater.instanceEDevice("EDevice", "ELimitSwitch", electriclistener);
	  ls2=Creater.instanceEDevice("EDevice", "ELimitSwitch", electriclistener);
	//  System.out.println(ls1);
	  actuator.addValve(ls1);
	  actuator.addValve(ls2);
	  electriclistener.getEArrays().addEDevice(ls1);
	  electriclistener.getEArrays().addEDevice(ls2);
	  setESymbol();
	  addMouseListener(this);
	    addMouseMotionListener(this);
actuator.setName("A"+count);

	    popup = new JPopupMenu();
	    m = new JMenuItem(Config.getString("Piston.name"));
	    m.addActionListener(this);
	    m.addMouseListener(new menuItemMouseAdapter());
	    popup.add(m);
	    m = new JMenuItem(Config.getString("Piston.delete"));
	    m.addActionListener(this);
	    m.addMouseListener(new menuItemMouseAdapter());
	    popup.add(m);
	    popupon=true;
	    /*
	    popup.addSeparator();
		m = new JMenuItem(Config.getString("Piston.setRatio"));
	    m.addActionListener(this);
	    m.addMouseListener(new menuItemMouseAdapter());
	    popup.add(m);
	    popup.addSeparator();
	    */
	}
	
	public void remove() {
		pneumaticPanel.stopTimer();
		electriclistener.getEArrays().PistonArray.remove(this);
		electriclistener.getESystemPanel().remove(this);
		electriclistener.setModified(true);
		electriclistener.getEArrays().deleteEDevice(ls1);
		electriclistener.getEArrays().deleteEDevice(ls2);
		
		EValve evalve=getEValve();
		electriclistener.getEArrays().ElectricFaceArray.remove(evalve);
        electriclistener.getEDevicePanel().remove(evalve.getESymbol());
        electriclistener.getEDevicePanel().repaint();
        if(electriclistener.hasSequence())
         {electriclistener.sequenceRefreshSystemCombo();
//System.err.println("iconpanel systemcombo");
         }
		
		electriclistener.getESystemPanel().repaint();
//		electriclistener.repaint();
	}
	
	public String save() {
		
		StringBuffer sb=new StringBuffer();
	     sb.append(Integer.toString(SCCAD.Data_Element)+" ");
	     sb.append(Integer.toString(Type)+" ");
	     sb.append(getX()+" "+getY()+" ");
	     System.out.println(sb.toString());
		return sb.toString();
	}
    public static Piston read(String str, ElectricListener electriclistener) {
    	StringTokenizer token=new StringTokenizer(str);
        int dtype=Integer.parseInt(token.nextToken());
        if(dtype!=SCCAD.Data_Element) return null;
        
        int type=Integer.parseInt(token.nextToken());
        int x=Integer.parseInt(token.nextToken());
        int y=Integer.parseInt(token.nextToken());
        Piston piston=new Piston(type, electriclistener);
        electriclistener.getESystemPanel().add(piston);
    	
    	Insets insets = electriclistener.getESystemPanel().getInsets();
        Dimension size = piston.getPreferredSize();
        piston.setBounds(5 + insets.left, 5 + insets.top,
                     size.width, size.height);
        electriclistener.getEArrays().PistonArray.add(piston);
        
        
        piston.setLocation(x,y);
    	electriclistener.getESystemPanel().repaint();
        
		return piston;
	}
	public void clear() {
		pneumaticPanel.stopTimer();
		pneumaticPanel.forceClear();
	}
	public void reset() {
		EValve evalve=getEValve();
		evalve.reset();
	}
	public void setESymbol() {
		EValve evalve=getEValve();
		electriclistener.getEArrays().ElectricFaceArray.add(evalve);
        electriclistener.getEDevicePanel().add(evalve.getESymbol());
        electriclistener.getEDevicePanel().repaint();
        if(electriclistener.hasSequence())
         {electriclistener.sequenceRefreshSystemCombo();
//System.err.println("iconpanel systemcombo");
         }
	}
	public int getLSPosition(EDevice ed) {
		if(ed==ls1) return 11;
		if(ed==ls2) return 0;
		return -1;
	}
	public EValve getEValve() {
		Component[] cps=pneumaticPanel.getComponents();
		for(int i=0;i<cps.length;i++) {
		  if(cps[i] instanceof EValve) return (EValve) cps[i];
		}
		return null;
	}
	public Actuator getActuator() {
		Component[] cps=pneumaticPanel.getComponents();
		for(int i=0;i<cps.length;i++) {
		  if(cps[i] instanceof Actuator) return (Actuator) cps[i];
		}
		return null;
	}
	public void open(File file)
	   {
		  pneumaticPanel.deGroup();
		  pneumaticPanel.tempElementArray.clear();
//		System.err.println("degroup()");
		     String s=null;
	  //       CEDevice.ratio=1.0; 
	   boolean firstReadMatrix=true;      
	   try
	    {BufferedReader ins =new BufferedReader(new FileReader(file));
		   
		 while((s=ins.readLine())!=null)
	      {if(s==null || s.length()==0) continue;
//	 System.out.println(s);     
	       StringTokenizer token=new StringTokenizer(s);
	       int dtype=Integer.parseInt(token.nextToken());
	        switch(dtype)
	         {case Data_Element: 
	          case Data_Line: 
	          case Data_Limitswitch: 
	           pneumaticPanel.open(s);
	           break;
	         }
	      }
	     ins.close();
	     pneumaticPanel.deGroup();
	    }
	   catch(Exception bre)
	    {bre.printStackTrace();
		 System.err.println(bre.getMessage());
	    }
	   }
	
	public PneumaticPanel getPneumaticPanel() {
		return pneumaticPanel;
	}
	@Override
	public void setModified(boolean b) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean getModified() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void addCommand(Command com) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JFrame getFrame() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void MessageBox(String des, String type) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setStatus(String str) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void loadCircuit(String str) {
		// TODO Auto-generated method stub
		
	}
	public void mouseClicked(MouseEvent e)
	   {
//	    boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
//	    boolean pop=e.isPopupTrigger();
//	    MouseClicked(e,left,e.getX(),e.getY(),pop);
	   }

	//  public void MouseClicked(MouseEvent e, boolean left, int ex, int ey, boolean pop)
	//   {if(left) connectPort(ex,ey);
	//   }

	  public void mouseEntered(MouseEvent e) {}
	  public void mouseExited(MouseEvent e) {}
	  public void mousePressed(MouseEvent e)
	   {
	    boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
	    boolean pop=e.isPopupTrigger();
	    MousePressed(e,left,e.getX(),e.getY(),pop);
	   }
	  public void MouseDragged(MouseEvent e, boolean left, int ex, int ey, boolean pop)
	   {
		  if(left)
		    {xm=ex;
		     ym=ey;
		     drag=true;
		     pressedPosx=getLocation().x;
		     pressedPosy=getLocation().y;
		     setLocation(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
	//	     electriclistener.repaint();
		    }
	   
	   }
	  public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop)
	   {
		    if(left)
		     {pressedx=ex;
		      pressedy=ey;
		      pressedPosx=getLocation().x;
		      pressedPosy=getLocation().y;
		     }
		    else
		     maybeShowPopup(pop,ex,ey);
	   }
	  public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop)
	   {
		  if(left)
		     {pressedx=ex;
		      pressedy=ey;
		      pressedPosx=getLocation().x;
		      pressedPosy=getLocation().y;
		     }
		    else
		     maybeShowPopup(pop,ex,ey);
	   }
	  public void mouseReleased(MouseEvent e)
	   {
	    boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
	    boolean pop=e.isPopupTrigger();
	    MouseReleased(e,left,e.getX(),e.getY(),pop);
	   }

	  public void maybeShowPopup(boolean pop,int ex,int ey)
	   {
	    if(pop && popupon)
	     {
	      popup.show(this, ex, ey);
//	      popup.show(e.getComponent(), e.getX(), e.getY());
	     }
	   }

	  public void mouseDragged(MouseEvent e)
	   {
	    boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
	    boolean pop=e.isPopupTrigger();
	    MouseDragged(e,left,e.getX(),e.getY(),pop);
	   }

	  public void mouseMoved(MouseEvent e) {}

	  public void setRatio(double ratio)
	   {
		this.ratio=ratio;
		repaint();
	   }

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem mi=(JMenuItem) e.getSource();
	    String option=mi.getText();
	    if(option.equals(Config.getString("ESystemBase.delete")))
	     {
	       remove();
	     }
	    else if(option.equals(Config.getString("ESystemBase.name")))
	     {
	    	/*
	      String oldname=cdo.name;
		  customDialog = new CustomDialog(new JFrame(),Config.getString("ESystemBase.name"),Config.getString("ESystemBase.modifyname"),CustomDialog.VALUE_STRING);
	      customDialog.pack();
	      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	      customDialog.setLocation(
		  screenSize.width/2 - customDialog.getSize().width/2,
		  screenSize.height/2 - customDialog.getSize().height/2);
	      customDialog.setTextField(cdo.name);
	      customDialog.setVisible(true);
	      cdo.name=customDialog.getValidatedText();
	      repaint();
	      */
	     }
	    else if(option.equals(Config.getString("ESystemBase.setRatio")))
	     {/*
	    	customDialog = new CustomDialog(new JFrame(),Config.getString("ESystemBase.setRatio"),Config.getString("ESytemBase.setRatio"),CustomDialog.VALUE_FLOAT);
	      customDialog.pack();
	      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	      customDialog.setLocation(
		  screenSize.width/2 - customDialog.getSize().width/2,
		  screenSize.height/2 - customDialog.getSize().height/2);
	      customDialog.setTextField(new Float(ratio).toString());
	      customDialog.setVisible(true);
	      setRatio(customDialog.getFloat());
	      */
	     }
	}
	
	class menuItemMouseAdapter extends MouseAdapter
	   {
	    public void mouseEntered(MouseEvent e)
	     {int ind=popup.getComponentIndex((Component)e.getSource());
	     }
	   }

	@Override
	public boolean isPause() {
	  if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION && electriclistener.getSimulationMode()==SCCAD.SIMULATION_PAUSE) 
		   return true;
	  else return false;
	}


}
