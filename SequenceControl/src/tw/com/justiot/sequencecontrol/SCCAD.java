package tw.com.justiot.sequencecontrol;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.apache.commons.httpclient.HttpClient;

import tw.com.justiot.sequencecontrol.config.CircuitParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.dialog.CircuitFilter;
import tw.com.justiot.sequencecontrol.eelement.CEDevice;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.eelement.ESystem;
import tw.com.justiot.sequencecontrol.eelement.ESystemBase;
import tw.com.justiot.sequencecontrol.eelement.ElectricFace;
import tw.com.justiot.sequencecontrol.eelement.PumpMonitor;
import tw.com.justiot.sequencecontrol.eelement.WaterTank;
import tw.com.justiot.sequencecontrol.panel.EDevicePanel;
import tw.com.justiot.sequencecontrol.panel.EIconPanel;
import tw.com.justiot.sequencecontrol.panel.ESystemPanel;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.ElectricPanel;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.panel.PneumaticPanel;
import tw.com.justiot.sequencecontrol.panel.SequencePanel;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.pelement.Actuator;
import tw.com.justiot.sequencecontrol.pelement.EValve;
import tw.com.justiot.sequencecontrol.pelement.Piston;
import tw.com.justiot.sequencecontrol.theme.AquaTheme;
import tw.com.justiot.sequencecontrol.theme.CharcoalTheme;
import tw.com.justiot.sequencecontrol.theme.ContrastTheme;
import tw.com.justiot.sequencecontrol.theme.EmeraldTheme;
import tw.com.justiot.sequencecontrol.theme.RubyTheme;

import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
public class SCCAD extends JPanel implements ChangeListener,WindowListener,PneumaticListener,ElectricListener
  { //private String testfile="D:\\test.pc";
	private String testfile=null;
    public String lang;
    public Args args;

    public static final int OP_NONE=0;
    public static final int OP_SIMULATION=1;
    public static final int OP_CONTROL=2;
    public static final int OP_MONITOR=3;
    public static final int OP_EDIT=4;
    public static final int OP_INPUT=5;

    public static final int SIMULATION_NONE=0;
	public static final int SIMULATION_RUN=1;
    public static final int SIMULATION_PAUSE=2;

	public boolean stopRun=false;
	public long runperiod=200;
    private ArrayList commands=new ArrayList();
    private int commandIndex=-1;

	public int opMode=OP_EDIT;
	public int simulationMode=SIMULATION_RUN;

	public ESystems esystems=null;
    public Electrics electrics=null;
    public JFrame frame = null;
    private JTextField statusField = null;
    public void setStatus(String s) {statusField.setText(s);}

    private static int actuatorLimit=4;
    private static int conveyerLimit=4;
    private static int switchvalveLimit=8;
    private static int eswitchLimit=8;
    private static int esystemLimit=8;

    private static final String mac      = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    public static final String metal    = "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final String motif    = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static final String windows  = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    public String currentLookAndFeel = metal;
    private String THEME="DefaultMetal";

    private PropertyControl propertyControl;

   public static final int Data_Element=0;
   public static final int Data_Line=1;
   public static final int Data_Limitswitch=2;
   public static final int Data_EDevice=3;
   public static final int Data_ELimitswitch=4;
   public static final int Data_matrix=5;
   public static final int Data_cells=6;
   public static final int Data_ElectricFace=7;
   public static final int Data_ESystemBase=8;
   public static final int Data_Water=9;
   public static final int Data_CDOutput=10;

    public java.net.URL exampleURL = null;
    private String exampleDescription=null;
 //   private static final int PREFERRED_WIDTH = 1024;
 //   private static final int PREFERRED_HEIGHT = 500;
    public Image iconImage;

    private JDialog aboutBox = null;
    private JWindow splashScreen = null;
    public JApplet applet = null;
    private JLabel splashLabel = null;
    Container contentPane = null;
    JTabbedPane toolbarPane=null;
    private DefaultMetalTheme[] allThemes={new DefaultMetalTheme(),new AquaTheme(),new CharcoalTheme(),new ContrastTheme(),new EmeraldTheme(),new RubyTheme()};
    public int currentTheme;

  public void MessageBox(String des,String type)
   {JOptionPane.showMessageDialog(frame,type+":"+des);
   }

  private void finalBackup()
   {
    if(!isApplet()) propertyControl.saveProperties();;
   }

     public java.util.Timer timer=null;
     public java.util.Timer getTimer() {return timer;}
     public boolean modified=false;
     public File file;
     public JSplitPane psplitPane;
    public static void main(String[] args)
     {
    	SCCAD laddercad = new SCCAD(null,null,args);
     }

  private static void initLookAndFeel(String currentLookAndFeel, String THEME)
   {
//	if(Config.getBoolean("debug")) System.out.println("LookAndFeel->"+currentLookAndFeel+":"+THEME);
    String lookAndFeel = null;
    if (currentLookAndFeel != null)
     {
      if (currentLookAndFeel.equals(metal))
       lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
      else if(currentLookAndFeel.equals(windows))
       lookAndFeel = UIManager.getSystemLookAndFeelClassName();
      else if (currentLookAndFeel.equals(motif))
       lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
      else if (currentLookAndFeel.equals("GTK"))
       lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
      else
       {System.err.println("Unexpected value of LOOKANDFEEL specified: "+ currentLookAndFeel);
        lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
 //       lookAndFeel = UIManager.getSystemLookAndFeelClassName();
       }
      try
       {UIManager.setLookAndFeel(lookAndFeel);
        if (currentLookAndFeel.equals("Metal"))
         {
          if(THEME.equals("DefaultMetal"))
           MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
          else if(THEME.equals("Ocean"))
           MetalLookAndFeel.setCurrentTheme(new OceanTheme());
//          else
//           MetalLookAndFeel.setCurrentTheme(new TestTheme());

          UIManager.setLookAndFeel(new MetalLookAndFeel());
         }
       }
      catch (ClassNotFoundException e)
       {System.err.println("Couldn't find class for specified look and feel:"+ lookAndFeel);
        System.err.println("Did you include the L&F library in the class path?");
        System.err.println("Using the default look and feel.");
       }
      catch (UnsupportedLookAndFeelException e)
       {System.err.println("Can't use the specified look and feel ("+ lookAndFeel+ ") on this platform.");
        System.err.println("Using the default look and feel.");
       }
      catch (Exception e)
       {System.err.println("Couldn't get specified look and feel ("+ lookAndFeel+ "), for some reason.");
        System.err.println("Using the default look and feel.");
        e.printStackTrace();
       }
     }
    }


   public void windowClosing(WindowEvent e)
    {
	 finalBackup();
     System.exit(0);
    }
   public void windowOpened(WindowEvent e) {}
   public void windowClosed(WindowEvent e) {}
   public void windowIconified(WindowEvent e) {}
   public void windowDeiconified(WindowEvent e) {}
   public void windowActivated(WindowEvent e) {}
   public void windowDeactivated(WindowEvent e) {}

    EArrays earrays;
    public SCCAD(JFrame frame,SCCADApplet applet,String[] margs)
     { super();
       this.applet = applet;
       this.frame=frame;
       this.earrays=new EArrays();
       args=new Args(margs);//  get exampleURL exampleDescription
       propertyControl=new PropertyControl(this);

       lang=propertyControl.getLang();
       new Config(lang);
       System.out.println("lang="+lang);

       if(Config.getBoolean("debug")) System.out.println("PropertyControl()");

       KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
       manager.addKeyEventDispatcher(new MyKeyDispatcher(this));
       
       iconImage=util.loadImage("webladdercad","","iconImage",File.separator+"resources"+File.separator+"images"+File.separator+"webladdercad.gif");
       setLayout(new BorderLayout());
  //     setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));

       if(!isApplet())
        {createSplashScreen();
         SwingUtilities.invokeLater(new Runnable() {public void run() {showSplashScreen();}});
        }
       new PneumaticConfig(lang);
       if(Config.getBoolean("debug")) System.out.println("PneumaticConfig");
       esystems=new ESystems(this);
       if(Config.getBoolean("debug")) System.out.println("Pneumatics");
       electrics=new Electrics(this);
       if(Config.getBoolean("debug")) System.out.println("Electrics");

    	 psplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,esystems,electrics);
         psplitPane.setOneTouchExpandable(true);
         psplitPane.setDividerLocation(Integer.parseInt(propertyControl.properties.getProperty("psplitx")));
         Dimension minimumSize = new Dimension(0, 0);
         esystems.setMinimumSize(minimumSize);
         electrics.setMinimumSize(minimumSize);
         add(psplitPane,BorderLayout.CENTER);
    	 electrics.setVisible(true);

	   statusField = new JTextField("");
	   statusField.setEditable(false);
	   add(statusField, BorderLayout.SOUTH);

       if(!isApplet() || exampleURL==null)
        {JPanel top=new JPanel();
          top.setLayout(new BorderLayout());
          top.add(createMenus(), BorderLayout.NORTH);
          top.add(createToolBar(), BorderLayout.CENTER);
          add(top, BorderLayout.NORTH);
        }

        SwingUtilities.invokeLater(new Runnable() {
        	public void run() {

        		if(!isApplet()) hideSplash();
        		showWebLadderCAD();
        	}
        });

       loadExample();
if(Config.getBoolean("debug")) System.out.println("loadExample()");
       switchSimulationMenuButton();
    }

    private void setTitle() {
      if(file==null) frame.setTitle(Config.getString("Frame.title"));
      else frame.setTitle(Config.getString("Frame.title")+"-"+file.getAbsolutePath());
    }
    public void showWebLadderCAD()
    {
	 if(!isApplet())
	  {
		frame = new JFrame();
			  frame.addWindowListener(this);
      iconImage=util.loadImage("webladdercad","","iconImage",File.separator+"resources"+File.separator+"images"+File.separator+"webladdercad.gif");
        if(iconImage!=null) frame.setIconImage(iconImage);
//	    frame.setTitle(Config.getString("Frame.title"));
	    frame.getContentPane().add(this, BorderLayout.CENTER);
//	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

	    frame.setLocation(Integer.parseInt(propertyControl.properties.getProperty("win.x")),
	    		Integer.parseInt(propertyControl.properties.getProperty("win.y")));
		frame.setPreferredSize(new Dimension(Integer.parseInt(propertyControl.properties.getProperty("win.w")),
				Integer.parseInt(propertyControl.properties.getProperty("win.h"))));
		setTitle();
		frame.pack();
		frame.setVisible(true);
//	    propertyControl.applyWinProperties();
        currentLookAndFeel=propertyControl.properties.getProperty("lookfeel");
        THEME=propertyControl.properties.getProperty("theme");
  	    initLookAndFeel(currentLookAndFeel,THEME);


  	    openTest();
	  }
    }

    public void createSplashScreen()
     {splashLabel = new JLabel(util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"Splash.jpg", "Splash.accessible_description"));
//                splashLabel=new JLabel(loadImageIcon("/resources/images/Splash.jpg"));

	  if(!isApplet())
	   {splashScreen = new JWindow();
	    splashScreen.getContentPane().add(splashLabel);
	    splashScreen.pack();
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    splashScreen.setLocation(screenSize.width/2 - splashScreen.getSize().width/2,screenSize.height/2 - splashScreen.getSize().height/2);
	   }
     }
    public void showSplashScreen()
     {if(!isApplet()) splashScreen.show();
	  else
	   {add(splashLabel, BorderLayout.CENTER);
//	    validate();
	    repaint();
	   }
     }
    public void hideSplash()
     {if(!isApplet())
       {splashScreen.setVisible(false);
	    splashScreen = null;
	    splashLabel = null;
	   }
     }
//
//-------------------------------------
//
  public boolean saveAs()
   {final JFileChooser fc = new JFileChooser(file);
    fc.addChoosableFileFilter(new CircuitFilter());
//    JFrame frame=new JFrame();
//    int returnVal = fc.showSaveDialog(frame);
    int returnVal = fc.showSaveDialog(frame);
    if(returnVal != JFileChooser.APPROVE_OPTION) return false;
    file = fc.getSelectedFile();
    if(file.exists())
     {int n = JOptionPane.showConfirmDialog(frame,
        "The file with the selected filename exists, would you like to overwrite it?",
        "Exist?Overwrite?",
        JOptionPane.YES_NO_OPTION);
      if(n==1) return false;
     }
//System.err.println(file.toString());
    save();
//    fc.setVisible(false);
    return true;
   }

 private boolean checkFileSaved()
  {if(oneModified())
     {int n = JOptionPane.showConfirmDialog(frame,
        "The circuit has been modified, would you like to save it?",
        "Save",
        JOptionPane.YES_NO_CANCEL_OPTION);
      if(n==0)
       {if(file!=null) save();
        else return(saveAs());
       }
      else if(n==2) return false;
      return true;
    }
   return true;
 }

  public boolean openFile()
   {if(!checkFileSaved()) return false;
    final JFileChooser fc = new JFileChooser(file);
    fc.addChoosableFileFilter(new CircuitFilter());
    int returnVal = fc.showOpenDialog(frame);
    if(returnVal != JFileChooser.APPROVE_OPTION) return false;
    file = fc.getSelectedFile();
    if(!file.exists()) return false;
    if(!clear(false)) return false;
    open();
    repaint();
    modified=false;
    return true;
   }

  private int inElement(tw.com.justiot.sequencecontrol.pelement.PneumaticElement ele, Object[] objs)
   {
    for(int i=0;i<objs.length;i++)
     if(ele==(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) objs[i]) return i;
    return -1;
   }


  public void open(BufferedReader br, String filestr)
   {
	     String s=null;
	//     forceClear(false);
         CEDevice.ratio=1.0;
//   boolean firstReadMatrix=true;
   try
    {while((s=br.readLine())!=null)
      {if(s==null || s.length()==0) continue;
 // System.out.println(s);
       StringTokenizer token=new StringTokenizer(s);
       int dtype=Integer.parseInt(token.nextToken());
        switch(dtype)
         {case Data_Element:
           Piston piston=Piston.read(s, this);
           break;
          case Data_ElectricFace:
           esystems.esystemPanel.open(s);
           break;
          case Data_EDevice:
        	  EDevice ed=EDevice.read(s, this);
        	  if(!SCCAD.checkLimit(CEDevice.modelType,ed.ced.getModelName())) return;
        	  if(opMode==SCCAD.OP_SIMULATION && timer!=null) ed.ced.startTimer(timer);
        	  earrays.addEDevice(ed);
        	  getEDevicePanel().add(ed);
        	  break;
          case Data_matrix:
        	  electrics.electricPanel.readMatrix(s);
           break;
          case Data_cells:
           if(electrics==null) continue;
           if(electrics.sequence==null) {electrics.sequence=new Sequence(this); propertyControl.applyWinSeqProperties();}
            {//electrics.open(s);
        	   electrics.sequence.sequencePanel.readCell(s);
            }
           break;
         }
      }
     br.close();
     setTitle();
    }
   catch(Exception bre)
    {System.err.println(bre.getMessage());
     setStatus(filestr+" reading error!!");
    }

   ElectricFace ef;
   for(int i=0;i<earrays.ElectricFaceArray.size();i++)
    {
     ef=(ElectricFace) earrays.ElectricFaceArray.get(i);
     if(ef instanceof EValve)
      {//EValve ev=(EValve) ef;
//       electrics.edevicePanel.add(ev.getESymbol());
      }
     else if(ef instanceof WaterTank)
      {WaterTank wt=(WaterTank) ef;
      electrics.edevicePanel.add(wt.pumpa.getESymbol());
      electrics.edevicePanel.add(wt.pumpb.getESymbol());
      }
     else
      electrics.edevicePanel.add(((ESystemBase) earrays.ElectricFaceArray.get(i)).getESymbol());
//System.err.println("addEsymbol");
    }

//   if(electrics!=null) electrics.restoreModuleIOObject();
   repaint();


   }

  private boolean saveFile()
   {
	try
     {PrintWriter out =new PrintWriter(new BufferedWriter(new FileWriter(file)));
      for(int i=0;i<earrays.PistonArray.size();i++) {
 		out.println(((Piston) earrays.PistonArray.get(i)).save());
 	  }
//     electrics.save()
      String str=getESystemPanel().writeElectricFace();// write ESystem
      if(str!=null && str.length()>0) out.println(str);
      str=electrics.edevicePanel.writeEDevice();
      if(str!=null && str.length()>0) out.println(str);
      str=electrics.electricPanel.writeMatrix();
      if(str!=null && str.length()>0) out.println(str);
      if(electrics.sequence!=null && electrics.sequence.sequencePanel!=null)
    	{
    	  str=electrics.sequence.sequencePanel.write();
          if(str!=null && str.length()>0) out.println(str);
    	}
      out.close();
      setTitle();
     }
    catch(EOFException e)
     {MessageBox("End of stream","Error");
      return false;
     }
    catch(IOException ioe)
     {MessageBox("IOException","Error");
      return false;
     }

    return true;
   }

  public String saveToString(boolean isgroup) throws Exception
  {/*
     StringBuffer sb=new StringBuffer();
     if(isgroup) sb.append(pneumatics.pneumaticPanel.saveGroup());
     else sb.append(pneumatics.pneumaticPanel.save());
     sb.append("/n");
     sb.append(electrics.save());
     sb.append("/n");
     return sb.toString();
     */
	  return "";
  }

  public void save()
   {if(file==null)
     {saveAs();
      return;
     }
    if(!saveFile()) return;
    modified=false;
   }

  public void openTest()
  {if(testfile==null) return;
   try
    {file=new File(testfile);
	 BufferedReader ins =new BufferedReader(new FileReader(file));
     open(ins,"File \""+testfile.toString()+"\"");
    }
   catch(Exception ioe)
    {setStatus("File \""+testfile.toString()+"\" I/O error!!");}
  }

  public void open()
   {if(file==null)
      {setStatus("File open error! : null");
        return;
      }
    try
     {BufferedReader ins =new BufferedReader(new FileReader(file));
      open(ins,"File \""+file.toString()+"\"");
     }
    catch(Exception ioe)
     {setStatus("File \""+file.toString()+"\" I/O error!!");}
   }

  public boolean oneModified()
   {
    return modified;
   }


  public void forceClear(boolean nullfile)
   {
	ArrayList<Piston> al=earrays.PistonArray;
	for(int i=0;i<al.size();i++) {
		al.get(i).remove();
	}
    electrics.forceClear();
	modified=false;

    if(nullfile) file=null;
    repaint();
   }

  public boolean clear(boolean nullfile)
   {if(oneModified())
     {int n = JOptionPane.showConfirmDialog(frame,
        Config.getString("savemodified"),
        Config.getString("Save"),
        JOptionPane.YES_NO_CANCEL_OPTION);
      if(n==0)
       {if(!saveAs()) return false;}
      else if(n==2)
       return false;
     }
    forceClear(nullfile);
    return true;
   }

//-------------------------------

  private static boolean checkActuatorCount()
   {if(actuatorLimit<0) return true;
    if(Actuator.count>=actuatorLimit)
     {JOptionPane.showMessageDialog(new JFrame(),Config.getString("iconPanel.actuatorlimit")+":"+Config.getString("iconPanel.warn"));
      return false;
     }
    return true;
   }

  private static boolean checkConveyerCount()
   {if(conveyerLimit<0) return true;
    if(ESystem.ConveyerCount>=conveyerLimit)
     {JOptionPane.showMessageDialog(new JFrame(),Config.getString("iconPanel.conveyerlimit")+":"+Config.getString("iconPanel.warn"));
      return false;
     }
    return true;
   }

  private static boolean checkESystemCount()
   {if(esystemLimit<0) return true;
    if(ESystem.count>=esystemLimit)
     {JOptionPane.showMessageDialog(new JFrame(),Config.getString("iconPanel.esystemlimit")+":"+Config.getString("iconPanel.warn"));
      return false;
     }
    return true;
   }

  private static boolean checkESwitchCount()
   {if(eswitchLimit<0) return true;
    if(EDevice.ELimitSwitchCount>=eswitchLimit)
     {JOptionPane.showMessageDialog(new JFrame(),Config.getString("iconPanel.eswitchlimit")+":"+Config.getString("iconPanel.warn"));
      return false;
     }
    return true;
   }

  public static boolean checkLimit(String modelType, String command)
   {//if(!checkApplet()) return false;
//    if(isApplet() && exampleURL!=null && exampleURL.toString().length()>0) return true;
    if(modelType!=null && modelType.equals("Actuator") && !checkActuatorCount()) return false;
    if(modelType!=null && command!=null && modelType.equals("ESystem") && command.equals("Conveyer") && !checkConveyerCount()) return false;
    if(modelType!=null && modelType.equals("ESystem") && !checkESystemCount()) return false;
    if(modelType!=null && command!=null && modelType.equals("EDevice") && command.equals("ELimitSwitch") && !checkESwitchCount()) return false;

    return true;
   }

//-----------------------------------------------------------

    private void loadExample()
     {if(exampleURL!=null)
       {

    	 try
         {BufferedReader br=new BufferedReader(new InputStreamReader(exampleURL.openStream()));
          open(br, exampleURL.toString());
          opMode=OP_SIMULATION;
          simulationMode=SIMULATION_RUN;
          startTimer();
         }
        catch(Exception e)
         {statusField.setText(Config.getString("pneumatics.s8")+exampleURL.toString());}
        statusField.setText(exampleDescription);

       }
//debug("loadExample");
     }

   public void startTimer()
    {if(timer!=null) return;
//     if(timer!=null) timer.cancel();
     timer=new java.util.Timer();
//     pneumatics.startTimer(timer);
//System.out.println("pneumatics.starttimer");
     if(electrics!=null)
      {earrays.setEDeviceMode(EDevice.MODE_Simulation);
	   electrics.startTimer(timer);
      }
    }
   public void reset() {
	   earrays.reset();
   }
   public void stopTimer()
    {if(timer==null) return;
     timer.cancel();
     timer=null;
     earrays.reset();
     if(electrics!=null) earrays.setEDeviceMode(EDevice.MODE_Edit);
    }

//   private ToggleButtonToolBar toolbar = null;
   public JButton newButton,openButton,saveButton,runButton,pauseButton,stopButton,sequenceButton,plcButton,accButton,controllerButton;
   public JButton undoButton,redoButton;
   private JToolBar createToolBar()
    {JToolBar toolBar = new JToolBar();
      Insets inset=new Insets(1,1,1,1);
      allAction aAction=new allAction(null,0);
      Image image=util.loadImage(File.separator+"resources"+File.separator+"images"+File.separator+"new.gif");

      if(image!=null) newButton=new JButton((Icon) new ImageIcon(image));
      else newButton=new JButton();
//         button=new JButton((Icon) new ImageIcon(iconImages[i]));
      newButton.setMargin(inset);
      newButton.setText(null);
      newButton.setToolTipText(Config.getString("tooltip.startdesign"));
      newButton.setActionCommand("F_new");
      newButton.addActionListener(aAction);
      toolBar.add(newButton);
      openButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"open.gif"));
//         button=new JButton((Icon) new ImageIcon(iconImages[i]));
      openButton.setMargin(inset);
      openButton.setText(null);
      openButton.setToolTipText(Config.getString("tooltip.openold"));
      openButton.setActionCommand("F_open");
      openButton.addActionListener(aAction);
      toolBar.add(openButton);
      saveButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"save.gif"));
//         button=new JButton((Icon) new ImageIcon(iconImages[i]));
      saveButton.setMargin(inset);
      saveButton.setText(null);
      saveButton.setToolTipText(Config.getString("tooltip.savedesign"));
      saveButton.setActionCommand("F_save");
      saveButton.addActionListener(aAction);
      toolBar.add(saveButton);

      toolBar.addSeparator();

      if(isApplet())
       {openButton.setEnabled(false);
        saveButton.setEnabled(false);
       }

       undoButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"undo.gif"));
 //         button=new JButton((Icon) new ImageIcon(iconImages[i]));
      undoButton.setMargin(inset);
      undoButton.setText(null);
      undoButton.setToolTipText(Config.getString("tooltip.Undo"));
      undoButton.setActionCommand("undo");
      undoButton.addActionListener(aAction);
      toolBar.add(undoButton);
      redoButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"redo.gif"));
//         button=new JButton((Icon) new ImageIcon(iconImages[i]));
      redoButton.setMargin(inset);
      redoButton.setText(null);
      redoButton.setToolTipText(Config.getString("tooltip.Redo"));
      redoButton.setActionCommand("redo");
      redoButton.addActionListener(aAction);
      toolBar.add(redoButton);

undoButton.setEnabled(false);
redoButton.setEnabled(false);
      toolBar.addSeparator();

      runButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"run.gif"));
//         button=new JButton((Icon) new ImageIcon(iconImages[i]));
      runButton.setMargin(inset);
      runButton.setText(null);
      runButton.setToolTipText(Config.getString("tooltip.runsimulation"));
      runButton.setActionCommand("simulationRun");
      runButton.addActionListener(aAction);
      toolBar.add(runButton);

      pauseButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"pause.gif"));
      pauseButton.setMargin(inset);
      pauseButton.setText(null);
      pauseButton.setToolTipText(Config.getString("tooltip.pausesimulation"));
      pauseButton.setActionCommand("simulationPause");
      pauseButton.addActionListener(aAction);
      toolBar.add(pauseButton);

      stopButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"stop.gif"));
      stopButton.setMargin(inset);
      stopButton.setText(null);
      stopButton.setToolTipText(Config.getString("tooltip.stopsimulation"));
      stopButton.setActionCommand("stop");
      stopButton.addActionListener(aAction);
      toolBar.add(stopButton);
      if(electrics!=null)
       {toolBar.addSeparator();
         sequenceButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"sequence.gif"));
         sequenceButton.setMargin(inset);
        sequenceButton.setText(null);
        sequenceButton.setToolTipText(Config.getString("tooltip.opensequence"));
        sequenceButton.setActionCommand("sequence");
        sequenceButton.addActionListener(aAction);
        toolBar.add(sequenceButton);

        toolBar.addSeparator();
        plcButton=new JButton((Icon) util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"plc.gif"));
        plcButton.setMargin(inset);
        plcButton.setText(null);
        plcButton.setToolTipText(Config.getString("tooltip.openplc"));
        plcButton.setActionCommand("program");
        plcButton.addActionListener(aAction);
        toolBar.add(plcButton);
/*
        toolBar.addSeparator();
        String acc=Modules.LoginName;
        if(acc==null || acc.length()==0) acc="�b��";
        accButton=new JButton(acc);
        accButton.setMargin(inset);
        accButton.setToolTipText("Login to Database.");
        accButton.setActionCommand("login");
        accButton.addActionListener(aAction);
        toolBar.add(accButton);
        String cname=Modules.controllername;
        if(cname==null || cname.length()==0) cname="���";
        controllerButton=new JButton(cname);
        controllerButton.setMargin(inset);
        controllerButton.setToolTipText("set controller.");
        controllerButton.setActionCommand("controller");
        controllerButton.addActionListener(aAction);
        toolBar.add(controllerButton);
        */
       }
      return toolBar;
     }

    public void stateChanged(ChangeEvent e)
     {if(!(e.getSource() instanceof JTabbedPane)) return;
       JTabbedPane tab=(JTabbedPane) e.getSource();
       int ind=tab.getSelectedIndex();

     }
    public void changeTab(int ind)
     {toolbarPane.setSelectedIndex(ind);
     }


//   HelpSet hs;
//   HelpBroker hb;
    private JMenuBar menuBar = null;
    private JMenu themesMenu = null;
//    private ButtonGroup toolbarGroup = new ButtonGroup();
    private ButtonGroup lafMenuGroup = new ButtonGroup();
    private ButtonGroup themesMenuGroup = new ButtonGroup();
    private ButtonGroup controlMenuGroup = new ButtonGroup();
    private JMenuItem newMenuItem,openMenuItem,saveMenuItem,saveasMenuItem,importMenuItem,exportMenuItem;
    private JMenuItem moveMenuItem,cutMenuItem,copyMenuItem,pasteMenuItem,deleteMenuItem;

    private JMenuItem epasteMenuItem,eoverwriteMenuItem;
    private JMenuItem esimuRunMenuItem,esimuPauseMenuItem,estopMenuItem;
    private JMenu selectedMenu=null;
    private JMenuItem undoMenuItem,redoMenuItem;
    /**
     * Create menus
     */
    private menuItemMouseAdapter allmenuItemMouseAdapter;
    public JMenuBar createMenus()
     {JMenuItem mi;
      allAction aAction=new allAction(null,0);
      allAction bAction=aAction;
      allmenuItemMouseAdapter=new menuItemMouseAdapter();
      if(isApplet()) bAction=null;
      allMenuListener allMenu=new allMenuListener();
	// ***** create the menubar ****
	  menuBar = new JMenuBar();
	  menuBar.getAccessibleContext().setAccessibleName(Config.getString("MenuBar.accessible_description"));
	// ***** create File menu
	  JMenu fileMenu = (JMenu) menuBar.add(new JMenu(Config.getString("FileMenu.file_label")));
      fileMenu.setMnemonic(getMnemonic("FileMenu.file_mnemonic"));
	  fileMenu.getAccessibleContext().setAccessibleDescription(Config.getString("FileMenu.accessible_description"));
      fileMenu.setActionCommand("file");
      fileMenu.addMenuListener(allMenu);
      newMenuItem=createMenuItem(fileMenu,null, "F_new","FileMenu.new_label", "FileMenu.new_mnemonic","FileMenu.new_accessible_description", bAction);
      openMenuItem=createMenuItem(fileMenu,null, "F_open","FileMenu.open_label", "FileMenu.open_mnemonic","FileMenu.open_accessible_description", bAction);
      saveMenuItem=createMenuItem(fileMenu,null, "F_save","FileMenu.save_label", "FileMenu.save_mnemonic","FileMenu.save_accessible_description", bAction);
      saveasMenuItem=createMenuItem(fileMenu,null,"F_saveas", "FileMenu.save_as_label", "FileMenu.save_as_mnemonic","FileMenu.save_as_accessible_description", bAction);

      if(!isApplet())
       {fileMenu.addSeparator();
	    createMenuItem(fileMenu,null, "F_exit","FileMenu.exit_label", "FileMenu.exit_mnemonic","FileMenu.exit_accessible_description", aAction);
	   }
      if(isApplet())
       {
        openMenuItem.setEnabled(false);
        saveMenuItem.setEnabled(false);
        saveasMenuItem.setEnabled(false);
        if(electrics==null)
         {importMenuItem.setEnabled(false);
          exportMenuItem.setEnabled(false);
         }
       }
	// ***** create Edit menu
	  JMenu editMenu = (JMenu) menuBar.add(new JMenu(Config.getString("EditMenu.edit_label")));
      editMenu.setMnemonic(getMnemonic("EditMenu.edit_mnemonic"));
	  editMenu.getAccessibleContext().setAccessibleDescription(Config.getString("EditMenu.accessible_description"));
//        editMenu.addMenuListener(new EditMenuListener());
      editMenu.setActionCommand("edit");
      editMenu.addMenuListener(allMenu);
      undoMenuItem=createMenuItem(editMenu,null,"undo", "EditMenu.undo_label", "EditMenu.undo_mnemonic","EditMenu.undo_accessible_description", aAction);
      redoMenuItem=createMenuItem(editMenu,null,"redo", "EditMenu.redo_label", "EditMenu.redo_mnemonic","EditMenu.redo_accessible_description", aAction);
undoMenuItem.setEnabled(false);
redoMenuItem.setEnabled(false);
      editMenu.addSeparator();

       editMenu.addSeparator();
        createMenuItem(editMenu,null,"electrics", "EditMenu.electrics_label", "EditMenu.electrics_mnemonic","EditMenu.electrics_accessible_description", null);
        createMenuItem(editMenu,null,"del", "editMenu.del_label", "editMenu.del_mnemonic","editMenu.del_accessible_description", aAction);
        createMenuItem(editMenu,null,"cut", "editMenu.cut_label", "editMenu.cut_mnemonic","editMenu.cut_accessible_description", aAction);
        epasteMenuItem=createMenuItem(editMenu,null,"paste", "editMenu.paste_label", "editMenu.paste_mnemonic","editMenu.paste_accessible_description", aAction);
        createMenuItem(editMenu,null,"copy", "editMenu.copy_label", "editMenu.copy_mnemonic","editMenu.copy_accessible_description", aAction);
//        editMenu.addSeparator();
        eoverwriteMenuItem=createMenuItem(editMenu,null,"overwrite", "editMenu.overwrite_label", "editMenu.overwrite_mnemonic","editMenu.overwrite_accessible_description", aAction);


      editMenu.addSeparator();
      createMenuItem(editMenu,null,"saveProperty","EditMenu.save_label","EditMenu.save_mnemonic","EditMenu.save_accessible_description", aAction);
              // ***** create Simulation menu
	  JMenu simuMenu = (JMenu) menuBar.add(new JMenu(Config.getString("simuMenu.simu_label")));
      simuMenu.setMnemonic(getMnemonic("simuMenu.simu_mnemonic"));
	  simuMenu.getAccessibleContext().setAccessibleDescription(Config.getString("simuMenu.accessible_description"));
 //               simuMenu.addMenuListener(new SimuMenuListener());
      simuMenu.setActionCommand("simu");
      simuMenu.addMenuListener(allMenu);
      if(electrics!=null)
       {
        createMenuItem(simuMenu,null,"analysis", "simuMenu.analysis_label", "simuMenu.analysis_mnemonic","simuMenu.analysis_accessible_description", aAction);
        simuMenu.addSeparator();
       }
      esimuRunMenuItem=createMenuItem(simuMenu,null,"simulationRun", "simuMenu.run_label", "simuMenu.run_mnemonic","simuMenu.run_accessible_description", aAction);
      esimuPauseMenuItem=createMenuItem(simuMenu,null,"simulationPause", "simuMenu.pause_label", "simuMenu.pause_mnemonic","simuMenu.pause_accessible_description", aAction);
      simuMenu.addSeparator();
      estopMenuItem=createMenuItem(simuMenu,null,"stop", "simuMenu.stop_label", "simuMenu.stop_mnemonic","simuMenu.stop_accessible_description", aAction);
      if(electrics!=null)
       {
                             // ***** create Sequence menu
	    JMenu seqMenu = (JMenu) menuBar.add(new JMenu(Config.getString("seqMenu.label")));
        seqMenu.setMnemonic(getMnemonic("seqMenu.mnemonic"));
	    seqMenu.getAccessibleContext().setAccessibleDescription(Config.getString("seqMenu.accessible_description"));
//                seqMenu.addMenuListener(new seqMenuListener());
        seqMenu.setActionCommand("seq");
        seqMenu.addMenuListener(allMenu);
        createMenuItem(seqMenu,null,"sequence", "seqMenu.sequence_label", "seqMenu.sequence_mnemonic","seqMenu.sequence_accessible_description", aAction);

                            // ***** create PLC menu
	    JMenu plcMenu = (JMenu) menuBar.add(new JMenu(Config.getString("plcMenu.label")));
        plcMenu.setMnemonic(getMnemonic("plcMenu.mnemonic"));
	    plcMenu.getAccessibleContext().setAccessibleDescription(Config.getString("plcMenu.accessible_description"));
//                seqMenu.addMenuListener(new seqMenuListener());
        plcMenu.setActionCommand("plc");
        plcMenu.addMenuListener(allMenu);
        createMenuItem(plcMenu,null,"program", "plcMenu.program_label", "plcMenu.program_mnemonic","plcMenu.program_accessible_description", aAction);
       }

      menuBar.add(Box.createHorizontalGlue());
        	// ***** create laf switcher menu

	  JMenu lafMenu = (JMenu) menuBar.add(new JMenu(Config.getString("LafMenu.laf_label")));
      lafMenu.setMnemonic(getMnemonic("LafMenu.laf_mnemonic"));
	  lafMenu.getAccessibleContext().setAccessibleDescription(Config.getString("LafMenu.laf_accessible_description"));
      lafMenu.setActionCommand("laf");
      lafMenu.addMenuListener(allMenu);
      mi = createMenuItem(lafMenu,lafMenuGroup, "L_java","LafMenu.java_label", "LafMenu.java_mnemonic","LafMenu.java_accessible_description",new allAction(metal,0));
	  mi.setSelected(true); // this is the default l&f
      createMenuItem(lafMenu,lafMenuGroup,"L_mac", "LafMenu.mac_label", "LafMenu.mac_mnemonic","LafMenu.mac_accessible_description",new allAction(mac,0) );
	  createMenuItem(lafMenu,lafMenuGroup,"L_motif", "LafMenu.motif_label", "LafMenu.motif_mnemonic","LafMenu.motif_accessible_description",new allAction(motif,0) );
	  mi=createMenuItem(lafMenu,lafMenuGroup,"L_windows", "LafMenu.windows_label", "LafMenu.windows_mnemonic","LafMenu.windows_accessible_description",new allAction(windows,0) );
//	mi.setSelected(true); // this is the default l&f
	// ***** create themes menu
	  themesMenu = (JMenu) menuBar.add(new JMenu(Config.getString("ThemesMenu.themes_label")));
      themesMenu.setMnemonic(getMnemonic("ThemesMenu.themes_mnemonic"));
	  themesMenu.getAccessibleContext().setAccessibleDescription(Config.getString("ThemesMenu.themes_accessible_description"));
      themesMenu.setActionCommand("theme");
      themesMenu.addMenuListener(allMenu);
	  mi = createMenuItem(themesMenu,themesMenuGroup, "T_default","ThemesMenu.default_label", "ThemesMenu.default_mnemonic","ThemesMenu.default_accessible_description", new allAction(null,0));
	  mi.setSelected(true); // This is the default theme
	  createMenuItem(themesMenu,themesMenuGroup,"T_aqua", "ThemesMenu.aqua_label", "ThemesMenu.aqua_mnemonic","ThemesMenu.aqua_accessible_description",new allAction(null,1));
	  createMenuItem(themesMenu,themesMenuGroup,"T_charcoal", "ThemesMenu.charcoal_label", "ThemesMenu.charcoal_mnemonic","ThemesMenu.charcoal_accessible_description",new allAction(null,2));
	  createMenuItem(themesMenu,themesMenuGroup,"T_contrast", "ThemesMenu.contrast_label", "ThemesMenu.contrast_mnemonic","ThemesMenu.contrast_accessible_description", new allAction(null,3));
	  createMenuItem(themesMenu,themesMenuGroup,"T_emerald", "ThemesMenu.emerald_label", "ThemesMenu.emerald_mnemonic","ThemesMenu.emerald_accessible_description",new allAction(null,4));
	  createMenuItem(themesMenu,themesMenuGroup,"T_ruby", "ThemesMenu.ruby_label", "ThemesMenu.ruby_mnemonic","ThemesMenu.ruby_accessible_description",new allAction(null,5));
               // ***** create Help menu
	  JMenu helpMenu = (JMenu) menuBar.add(new JMenu(Config.getString("helpMenu.edit_label")));
      helpMenu.setMnemonic(getMnemonic("helpMenu.edit_mnemonic"));
	  helpMenu.getAccessibleContext().setAccessibleDescription(Config.getString("helpMenu.accessible_description"));
      createMenuItem(helpMenu,null,"online","helpMenu.online_label","helpMenu.online_mnemonic","helpMenu.online_accessible_description", aAction);
      createMenuItem(helpMenu,null, "F_about","helpMenu.about_label", "helpMenu.about_mnemonic","helpMenu.about_accessible_description", aAction);

	  return menuBar;
    }

   public JMenuItem createMenuItem(JMenu menu,ButtonGroup bg,String acommand, String label, String mnemonic,String accessibleDescription, Action action)
    {JMenuItem mi=null;
	 if(bg==null) mi = (JMenuItem) menu.add(new JMenuItem(Config.getString(label)));
	 else mi = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(Config.getString(label)));
	 if(bg!=null) bg.add(mi);
     mi.setActionCommand(acommand);
     mi.setMnemonic(getMnemonic(Config.getString(mnemonic)));
	 mi.getAccessibleContext().setAccessibleDescription(Config.getString(accessibleDescription));
	 mi.addActionListener(action);
     mi.addMouseListener(allmenuItemMouseAdapter);
	 if(action==null) mi.setEnabled(false);
	 return mi;
    }


    public boolean isApplet() {return (applet != null);}
    public JApplet getApplet() {return applet;}
    public JFrame getFrame() {return frame;}
    public Container getContentPane()
     {if(contentPane==null)
       {if(getFrame() != null) contentPane = getFrame().getContentPane();
	    else if(getApplet()!=null) {contentPane = getApplet().getContentPane();}
	   }
	  return contentPane;
     }

    public char getMnemonic(String key) {return key.charAt(0);}

    public void setLookAndFeel(String laf)
     {
      if(Config.getBoolean("debug")) System.out.println("setLookAndFeel:"+laf);
      if(currentLookAndFeel != laf)
       {
	    currentLookAndFeel = laf;
	    themesMenu.setEnabled(laf == metal);
	    updateLookAndFeel();
	   }
     }
    public void updateLookAndFeel()
     {try
       {UIManager.setLookAndFeel(currentLookAndFeel);
	    SwingUtilities.updateComponentTreeUI(this);
	    SwingUtilities.updateComponentTreeUI(esystems);
	    if(electrics!=null)
	     {SwingUtilities.updateComponentTreeUI(electrics);
	      SwingUtilities.updateComponentTreeUI(electrics.electricPanel);
	      SwingUtilities.updateComponentTreeUI(electrics.edevicePanel);
	      if(electrics.sequence!=null) SwingUtilities.updateComponentTreeUI(electrics.sequence);
//PLCFrame
//ServerFrame
         }
	    UIManager.setLookAndFeel(currentLookAndFeel);
	    SwingUtilities.updateComponentTreeUI(frame);
	    frame.pack();


	   }
	  catch (Exception ex)
	   {setStatus(Config.getString("pneumatics.s22")+currentLookAndFeel);
	   }
     }

   class allAction extends AbstractAction
    {String text;
     int themeno;
     public allAction(String tex,int themeno)
      {this.text=tex;
       this.themeno=themeno;
      }
     public void actionPerformed(ActionEvent e)
      {
          String com=null;
          if(e.getSource() instanceof JMenuItem) com=((JMenuItem) e.getSource()).getActionCommand();
          else if(e.getSource() instanceof JButton) com=((JButton) e.getSource()).getActionCommand();
          else return;
          ActionPerformed(com,text,themeno);
      }
   }

  public void simulationRun() {
	  opMode=OP_SIMULATION;
	    simulationMode=SIMULATION_RUN;
	    startTimer();
	    switchSimulationMenuButton();
  }
  public void simulationStop() {
	  stopTimer();
	    opMode=OP_EDIT;
	    simulationMode=SIMULATION_NONE;
	    switchSimulationMenuButton();
  }
  public void simulationPause() {
	  opMode=OP_SIMULATION;
	    simulationMode=SIMULATION_PAUSE;
	    switchSimulationMenuButton();
  }
  private void ActionPerformed(String com,String text,int themeno)
   {
//System.out.println("com:"+com);
	if(com.equals("F_about"))
     {if(aboutBox==null)
       {JPanel panel = new AboutPanel(this);
		panel.setLayout(new BorderLayout());
		aboutBox = new JDialog(getFrame(), Config.getString("AboutBox.title"), false);
		aboutBox.getContentPane().add(panel, BorderLayout.CENTER);
		JPanel buttonpanel = new JPanel();
		buttonpanel.setOpaque(false);
		JButton button = (JButton) buttonpanel.add(new JButton(Config.getString("AboutBox.ok_button_text")));
		panel.add(buttonpanel, BorderLayout.SOUTH);
		button.addActionListener(new OkAction(aboutBox));
	   }
	  aboutBox.pack();
	  aboutBox.setLocation(getLocationOnScreen().x + 10, getLocationOnScreen().y +10);
	  aboutBox.show();
     }
    else if(com.equals("F_new"))
     {stopTimer();
      clear(true);
     }
    else if(com.equals("F_open"))
     {stopTimer();
      openFile();
     }
    else if(com.equals("F_save"))
     {save();}
    else if(com.equals("F_saveas"))
     {saveAs();}
    else if(com.equals("saveProperty") && !isApplet()) propertyControl.saveProperties();
    else if(com.equals("F_exit"))
     {
      int value=JOptionPane.showConfirmDialog(frame,Config.getString("pneumatics.s32"),
                Config.getString("pneumatics.s33"),
                JOptionPane.YES_NO_OPTION);
      if (value == JOptionPane.YES_OPTION)
       {
	    finalBackup();
//        if(electrics!=null && electrics.serverFrame!=null) electrics.serverFrame.updateFlag=false;
        System.exit(0);
       }
     }
    else if(com.equals("L_java") || com.equals("L_mac") || com.equals("L_motif") || com.equals("L_windows"))
     {setLookAndFeel(text);}
    else if(com.equals("T_default") || com.equals("T_aqua") || com.equals("T_charcoal") || com.equals("T_contrast") ||
                   com.equals("T_emerald") || com.equals("T_ruby"))
     {currentTheme=themeno;
      MetalLookAndFeel.setCurrentTheme(allThemes[themeno]);
	  updateLookAndFeel();
     }
    else if(com.equals("online"))
     {URL url=null;
      try
       {if(isApplet())
         {url=new URL(Config.getString("helpFrameURL"));
          getApplet().getAppletContext().showDocument(url,"_blank");
         }
        else
         {//System.out.println("WebBrowser");
          WebBrowser wb=new WebBrowser(iconImage);
          wb.setVisible(true);
         }
       }
      catch(Exception ue)
       {System.err.println(ue.getMessage());
        System.err.println("error helpURL!");
        setStatus(Config.getString("pneumatics.s31"));
        return;
       }
//System.err.println(url.toString());
//            getApplet().getAppletContext().showDocument(url,"_blank");
     }

    else if(com.equals("cut"))
       {if(electrics.electricPanel!=null)
         {electrics.electricPanel.copyToBoard();
          electrics.electricPanel.deleteBlock();
          electrics.electricPanel.degroup();
          electrics.electricPanel.repaint();
         }
       }
      else if(com.equals("copy"))
       {if(electrics.electricPanel!=null)
         {electrics.electricPanel.copyToBoard();
          electrics.electricPanel.degroup();
          electrics.electricPanel.repaint();
         }
       }
      else if(com.equals("paste"))
       {if(electrics.electricPanel!=null)
         {electrics.electricPanel.pasteBoardTo();
          electrics.electricPanel.degroup();
          electrics.electricPanel.repaint();
         }
       }
      else if(com.equals("del"))
       {if(electrics.electricPanel!=null)
         {electrics.electricPanel.deleteBlock();
          electrics.electricPanel.degroup();
          electrics.electricPanel.repaint();
         }
       }
      else if(com.equals("overwrite"))
       {if(electrics.electricPanel!=null)
         {electrics.electricPanel.overWrite=!electrics.electricPanel.overWrite;
          String str=Config.getString("Status.overwrite");
          if(!electrics.electricPanel.overWrite) str=Config.getString("Status.insert");
          electrics.setStatusMode(str);
          electrics.electricPanel.degroup();
         }
       }
      else if(com.equals("analysis"))
       {if(electrics!=null && electrics.electricPanel!=null)
         {electrics.electricPanel.LadderCheck();
         }
       }
      else if(com.equals("simulationRun"))
       {simulationRun();
       }
      else if(com.equals("simulationPause"))
       {simulationPause();
       }
      else if(com.equals("stop"))
       {simulationStop();
       }
    else if(com.equals("sequence"))
     {if(electrics==null)  return;
      if(electrics.sequence!=null) {
    	  electrics.sequence.getFrame().show();
    	  electrics.sequence.refreshSystemCombo();
    	  electrics.sequence.refreshLSCombo();
      }
      else {electrics.sequence=new Sequence(this); propertyControl.applyWinSeqProperties();}
      earrays.setEDeviceMode(EDevice.MODE_Edit);
     }
    else if(com.equals("program"))
     {if(electrics==null)  return;
      if(electrics.plcFrame!=null)
       {String aName = (String) electrics.plcFrame.cb.getSelectedItem();
        electrics.electricPanel.FPLC.changeModel(aName);
        electrics.plcFrame.show();
       }
      else {electrics.plcFrame=new PLCFrame(electrics.electricPanel.FPLC, this); propertyControl.applyWinPLCProperties();}
     }

    else if(com.equals("undo"))
     {((Command) commands.get(commandIndex)).undo();
      commandIndex--;
      switchDoMenuButton();
     }
    else if(com.equals("redo"))
     {commandIndex++;
      ((Command) commands.get(commandIndex)).redo();
      switchDoMenuButton();
     }

  }

    class OkAction extends AbstractAction
     {JDialog aboutBox;
      protected OkAction(JDialog aboutBox)
       {super("OkAction");
	    this.aboutBox = aboutBox;
       }
      public void actionPerformed(ActionEvent e) {aboutBox.setVisible(false);}
     }

    class AboutPanel extends JPanel
     {ImageIcon aboutimage = null;
	  SCCAD pneumatics = null;
	  public AboutPanel(SCCAD pneu)
	   {this.pneumatics = pneu;
	    aboutimage = util.loadImageIcon(File.separator+"resources"+File.separator+"images"+File.separator+"About.jpg");
	    setOpaque(false);
	   }
	  public void paint(Graphics g)
	   {aboutimage.paintIcon(this, g, 0, 0);
	    super.paint(g);
	   }
	  public Dimension getPreferredSize()
	   {return new Dimension(aboutimage.getIconWidth(),aboutimage.getIconHeight());
	   }
     }

    class allMenuListener implements MenuListener
     {
      public void menuSelected(MenuEvent e)
       {if(!(e.getSource() instanceof JMenu)) return;
//System.err.println("entermenuselected");
         JMenu menu=(JMenu) e.getSource();
         selectedMenu=menu;
         int nmenu=-1;
         for(int i=0;i<menuBar.getMenuCount();i++)
          {if(menu==menuBar.getMenu(i)) {nmenu=i;break;}}
//System.err.println(nmenu);

        String com=menu.getActionCommand();
        if(com.equals("edit"))
         {

          if(electrics!=null)
           {if(electrics.electricPanel.cellGroup!=null && electrics.electricPanel.cellGroup.size()>0)
              epasteMenuItem.setEnabled(true);
             else
              epasteMenuItem.setEnabled(false);
             if(!electrics.electricPanel.overWrite) eoverwriteMenuItem.setText(Config.getString("Status.overwrite"));
             else eoverwriteMenuItem.setText(Config.getString("Status.insert"));
           }
         }

      else if(electrics!=null && com.equals("simu"))
       {switchSimulationMenuButton();
       }
      }
      public void menuCanceled(MenuEvent e) {}
      public void menuDeselected(MenuEvent e) {}
     }

 private void switchDoMenuButton()
  {if(commandIndex>=0 && commandIndex<commands.size())
    {undoMenuItem.setEnabled(true);
     undoButton.setEnabled(true);
    }
   else
    {undoMenuItem.setEnabled(false);
     undoButton.setEnabled(false);
    }
   if(commandIndex+1>=0 && commandIndex+1<commands.size())
    {redoMenuItem.setEnabled(true);
     redoButton.setEnabled(true);
    }
   else
    {redoMenuItem.setEnabled(false);
     redoButton.setEnabled(false);
    }
  }

 private void switchSimulationMenuButton()
  {if((opMode!=OP_SIMULATION && opMode!=OP_CONTROL) || simulationMode==SIMULATION_NONE)
    {estopMenuItem.setEnabled(false);
     stopButton.setEnabled(false);
	 esimuRunMenuItem.setEnabled(true);
     runButton.setEnabled(true);
	 esimuPauseMenuItem.setEnabled(false);
     pauseButton.setEnabled(false);
     return;
    }
   if(simulationMode==SIMULATION_RUN)
    {estopMenuItem.setEnabled(true);
     stopButton.setEnabled(true);
	 esimuRunMenuItem.setEnabled(false);
     runButton.setEnabled(false);
	 esimuPauseMenuItem.setEnabled(true);
     pauseButton.setEnabled(true);
    }
   else if(simulationMode==SIMULATION_PAUSE)
    {estopMenuItem.setEnabled(true);
     stopButton.setEnabled(true);
	 esimuRunMenuItem.setEnabled(true);
     runButton.setEnabled(true);
	 esimuPauseMenuItem.setEnabled(false);
     pauseButton.setEnabled(false);
    }
  }

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {JMenuItem mitem=(JMenuItem) e.getSource();
         if(selectedMenu==null) return;
         int n=-1;
         for(int i=0;i<selectedMenu.getItemCount();i++)
          if(selectedMenu.getItem(i)==mitem) {n=i;break;}
        if(n<0) return;
       }
   }


  public void createESystem(String modelType,String command)
   {//if(!checkLimit(modelType, command)) return;
	  try {
    Object obj=Creater.instanceESystem(modelType,command, this);
    if(obj!=null)
     {tw.com.justiot.sequencecontrol.eelement.ESystem esystem=(tw.com.justiot.sequencecontrol.eelement.ESystem) obj;
//      if(obj instanceof ESystem)
//       {esystem=(ESystem) obj;
//        element=(tw.com.justiot.sequencecontrol.eelement.ESystem) esystem;
 //      }
//System.out.println("ESystem");
	  boolean oldmodified=modified;
//	  element.setESystemBaseListener(electrics.esystemPanel);
//      element.setCommandListener(this);
      if(opMode==OP_SIMULATION && timer!=null) esystem.startTimer(timer);
	  esystems.esystemPanel.add(esystem);

	  Insets insets = esystems.esystemPanel.getInsets();
      Dimension size = esystem.getPreferredSize();
      esystem.setBounds(5 + insets.left, 5 + insets.top,
                   size.width, size.height);


	  esystems.esystemPanel.repaint();
      modified=true;
//System.out.println("ESystemBase");
      if(obj instanceof ESystemBase)
       {if(electrics!=null)
         {earrays.ElectricFaceArray.add((ESystemBase) esystem);
          electrics.edevicePanel.add(((ESystemBase) esystem).getESymbol());
          electrics.edevicePanel.repaint();
          if(electrics.sequence!=null)
           {electrics.sequence.refreshSystemCombo();
//System.err.println("iconpanel systemcombo");
           }
         }
       }
 //     Component[] cs=esystems.esystemPanel.getComponents();
 //     System.out.println(cs.length);
//      add(new createESystemCommand(element,oldmodified));
     }
	  } catch(Exception e) {
		  e.printStackTrace();
	  }
   }

  public void createWaterElement(String modelType,String command)
   {try {
	if(!checkLimit(modelType, command)) return;
    if(!modelType.equals("Water")) return;
    if(command.equals("PumpMonitor"))
     {PumpMonitor pm=new PumpMonitor(this);
      boolean oldmodified=modified;
      if(opMode==OP_SIMULATION && timer!=null) pm.startTimer(timer);
      esystems.esystemPanel.add(pm);
      esystems.esystemPanel.repaint();
      earrays.ElectricFaceArray.add((ElectricFace) pm);
      electrics.edevicePanel.add(pm.getESymbol());
      electrics.edevicePanel.repaint();
      if(electrics.sequence!=null)
       {electrics.sequence.refreshSystemCombo();
//System.err.println("iconpanel systemcombo");
       }
      modified=true;
//      addCommand(new createElementCommand(element,oldmodified));
     }
    else if(command.equals("WaterTank"))
     {WaterTank wt=new WaterTank(this);
      boolean oldmodified=modified;
      if(opMode==OP_SIMULATION && timer!=null)
       {wt.pumpa.startTimer(timer);
        wt.startTimer(timer);
       }
     esystems.esystemPanel.add(wt);
     esystems.esystemPanel.repaint();
     earrays.ElectricFaceArray.add((ElectricFace) wt);
     electrics.edevicePanel.add(wt.pumpa.getESymbol());
     electrics.edevicePanel.add(wt.pumpb.getESymbol());
     electrics.edevicePanel.repaint();
     modified=true;
      if(electrics!=null)
        {
         if(electrics.sequence!=null)
          {electrics.sequence.refreshSystemCombo();
//System.err.println("iconpanel systemcombo");
          }
        }

//     addCommand(new createElementCommand(element,oldmodified));
    }
   } catch(Exception e) {
	   e.printStackTrace();
   }
   }
//-------------------------------
//  EIconPanelListener
  private class createEDeviceCommand extends Command
    {boolean oldmodified;
	public createEDeviceCommand(Object ele,boolean old)
     {super("EIconPanel",ele,EIconPanel.Command_createEDevice);
      oldmodified=old;
     }
    public void undo()
     {EDevice ed=(EDevice) source;
	  earrays.deleteEDevice(ed);
      if(ed.ced.actionType==CEDevice.TYPE_TIMER && electrics.sequence !=null)
         electrics.sequence.refreshSystemCombo();
      electrics.edevicePanel.repaint();
      modified=oldmodified;
     }
    public void redo()
     {EDevice ed=(EDevice) source;
      earrays.addEDevice(ed);
      if(ed.ced.actionType==CEDevice.TYPE_TIMER && electrics.sequence !=null)
         electrics.sequence.refreshSystemCombo();
      electrics.edevicePanel.repaint();
      modified=true;
     }
   }

  public void createEDevice(String modelType,String modelName)
   {if(!checkLimit(modelType, modelName)) return;
    if(electrics==null) return;
//    EDevice ed=Creater.instanceEDevice(modelType,modelName);
    EDevice ed=new EDevice(modelName, this);
    ed.ced.modelType=modelType;
    if(ed!=null)
     {boolean oldmodified=modified;
      if(opMode==OP_SIMULATION && timer!=null) ed.ced.startTimer(timer);
	  if(electrics.edevicePanel!=null)
       {earrays.addEDevice(ed);
        if(ed.ced.actionType==CEDevice.TYPE_TIMER && electrics.sequence !=null)
         electrics.sequence.refreshSystemCombo();
        electrics.edevicePanel.repaint();
        modified=true;
        addCommand(new createEDeviceCommand(ed,oldmodified));
       }
     }
   }
//-------------------------------
//  CircuitIconPanelListener
  public void loadCircuit(String modelName)
   {

    String fstr=((CircuitParameter) PneumaticConfig.circuit.get(modelName)).fileString;
    fstr=Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+fstr;
 //   System.out.println(fstr);
    BufferedReader br;
    URL url;
    try
     {url = new File(fstr).toURI().toURL();;
      br =new BufferedReader(new InputStreamReader(url.openStream()));
//if(debug) System.out.println("loadCircuit:");
      open(br,fstr);
     }
    catch(Exception ce)
     {System.err.println(ce.getMessage());
     if(Config.getBoolean("debug")) System.out.println(fstr);
     }
   }
//-------------------------------
//  WirePanelListener
/*
  public void AddCell(int type,int state,CEDevice ced,CDOutput cdo,boolean ShiftEnabled)
   {if(isApplet() && !Register.isLegal)
     {JOptionPane.showMessageDialog(frame,Config.getString("pneumatics.s1")+" certain Host "+Config.getString("pneumatics.s2"));
      return;
     }

//int ver=pneumatics.register.version;
//              if(pneumatics.isApplet() && !(ver==Register.Ver_Electrics_Server || ver==Register.Ver_Electrics_Control ||
//                  ver==Register.Ver_Electrics_Collaborative || ver==Register.Ver_Electrics_Demo  || ver==Register.Ver_Pneumatics_Demo)) return;
    electrics.electricPanel.AddCell(type,state,ced,cdo,ShiftEnabled);
    modified=true;
//    electrics.electricPanel.repaint();
   }
*/
//-------------------------------
//  CommandListener
  public void addCommand(Command com)
   {commandIndex++;
	if(commandIndex>commands.size()-1) commands.add(com);
	else commands.set(commandIndex,com);
	switchDoMenuButton();

	Object s=com.getSource();
	int id=com.getId();
	String cn=com.getClassName();
//System.out.println(Config.getString("commands."+cn+"."+id));
//    pneumatics.repaint();
//    if(electrics!=null) electrics.repaint();
//    if(electrics!=null && electrics.sequence!=null) electrics.sequence.repaint();
//    repaint();
   }
//-------------------------------
//  CircuitListener
  public void checkThreadStopped(String str)
   {stopTimer();
	MessageBox("Check Thread stopped??","Error");
   }
  public void refreshSystemCombo()
   {if(electrics==null || electrics.sequence==null) return;
	electrics.sequence.refreshSystemCombo();
   }

  public int getSimulationMode() {return simulationMode;}
  public Electrics getElectrics() {return electrics;}
  public void removeEValve(EValve ev)
   {if(electrics==null) return;
	electrics.edevicePanel.remove(ev.esymbol);
    earrays.ElectricFaceArray.remove(ev);
    repaint();
   }

//-------------------------------------------
//  interface LadderListener
  public void setStatusPos(String str)
    {if(electrics==null) return;
     electrics.setStatusPos(str);
    }
   public void setStatusMode(String str)
    {if(electrics==null) return;
     electrics.setStatusMode(str);
    }
   public int getOpMode()
    {return opMode;
    }
//   public void setStatus(String str);

  public void setModified(boolean m)
   {modified=m;
//    if(m==true) repaint();
   }
  public boolean getModified() {return modified;}
   public void deleteESystemBase(ESystemBase esystem)
    {if(electrics==null) return;

	 electrics.edevicePanel.remove(esystem.esymbol);
//     electrics.edevicePanel.repaint();
     earrays.ElectricFaceArray.remove((ESystemBase) esystem);
     if(electrics.sequence!=null) electrics.sequence.refreshSystemCombo();
//     electrics.electricPanel.repaint();
     repaint();
    }
//---------------------------------------
//    interface SequencePanelListener
   /*
  public int getLSPosition(Object obj)
   {return pneumatics.pneumaticPanel.getLSPosition(obj);}
  public void clearElectricPanel()
   {if(electrics==null) return;
    electrics.electricPanel.Clear();
   }
  public boolean getElectricPanelOverwrite()
   {if(electrics==null) return false;
    return electrics.electricPanel.overWrite;
   }
  public void setElectricPanelOverwrite(boolean b)
   {if(electrics==null) return;
    electrics.electricPanel.overWrite=b;
   }
  public void setElectricPanelCursor(int x,int y)
   {if(electrics==null) return;
    electrics.electricPanel.SetCursor(x,y);
   }
  public void deleteElectricPanelBlock()
   {if(electrics==null) return;
    electrics.electricPanel.deleteBlock();
   }
  public Point getElectricPanelDragPoint()
   {if(electrics==null) return null;
    return electrics.electricPanel.dragPoint1;
   }
  public int getMatrixType(int i,int j)
   {if(electrics==null) return -1;
    return electrics.electricPanel.matrix[i][j].type;
   }
*/
/*
  public void setEDeviceMode(int m)
   {if(electrics==null) return;
    electrics.edevicePanel.setEDeviceMode(m);
   }
  public int getEDeviceNo(EDevice ed)
   {if(electrics==null) return -1;
    return electrics.edevicePanel.getEDeviceNo(ed);
   }
  public void deleteEDevice(EDevice ed)
   {if(electrics==null) return;
    electrics.edevicePanel.deleteEDevice(ed);
   }
  public void setStatusMode(String str)
   {if(electrics==null) return;
    electrics.setStatusMode(str);
   }
  public void setStatusPos(String str)
   {if(electrics==null) return;
    electrics.setStatusPos(str);
   }
*/
  public void setSequenceStatusPos(String str)
   {if(electrics==null || electrics.sequence==null) return;
    electrics.sequence.setStatusPos(str);
   }
  public void setSequenceStatus(String str)
   {if(electrics==null || electrics.sequence==null) return;
    electrics.sequence.setStatus(str);
   }
@Override
public boolean getStopRun() {
	return stopRun;
}
@Override
public int getRunPeriod() {
	return (int)runperiod;
}

@Override
public Image getIconImage() {
	return iconImage;
}
@Override
public URL getExampleURL() {
	return exampleURL;
}

/*
@Override
public void repaint() {
	repaint();
}
*/
@Override
public ElectricPanel getElectricPanel() {
	return electrics.electricPanel;
}
@Override
public SequencePanel getSequencePanel() {
	return electrics.sequence.sequencePanel;
}
@Override
public EDevicePanel getEDevicePanel() {
	return electrics.edevicePanel;
}
@Override
public ESystemPanel getESystemPanel() {
	return esystems.esystemPanel;
}
public boolean hasElectrics()
{if(electrics!=null && (earrays.EDeviceArray.size()>0 ||
       earrays.ElectricFaceArray.size()>0 ||
       !electrics.electricPanel.isEmpty())) return true;
 return false;
}

public boolean hasSequence()
{if(electrics!=null && electrics.sequence !=null &&
    !electrics.sequence.sequencePanel.isEmpty()) return true;
 return false;
}

@Override
public void setElectricsStatusMode(String str) {
	electrics.setStatusMode(str);
}
@Override
public void setElectricsStatusPos(String str) {
	electrics.setStatusPos(str);
}
@Override
public void sequenceRefreshSystemCombo() {
	electrics.sequence.refreshSystemCombo();
}
@Override
public JScrollPane getElectricsDeviceScrollPane() {
	return electrics.deviceScrollPane;
}
@Override
public JScrollPane getElectricsElectricScrollPane() {
	return electrics.electricScrollPane;
}

@Override
public PneumaticPanel getPneumaticPanel() {
	return null;
}

@Override
public void getLimitswitchLocation(EDevice ed) {
	for(int i=0;i<earrays.PistonArray.size();i++) {
		((Piston) earrays.PistonArray.get(i)).getPneumaticPanel().getLimitswitchLocation(ed);
	}
	//System.out.println("getLimitSwitchLocation(EDevice ed)");
}

@Override
public void setActuatorPosAccording2LS() {
	for(int i=0;i<earrays.PistonArray.size();i++) {
		((Piston) earrays.PistonArray.get(i)).getPneumaticPanel().setActuatorPosAccording2LS();;
	}
	//System.out.println("setActuatorPosAccording2LS()");
}

@Override
public int getLSPosition(EDevice ed) {
	int pos=-1;
	for(int i=0;i<earrays.PistonArray.size();i++) {
		pos=((Piston) earrays.PistonArray.get(i)).getLSPosition(ed);
		if(pos>=0) return pos;
	}
	//System.out.println("getLSPosition(EDevice ed)");
	return -1;
}

@Override
public EArrays getEArrays() {
	return earrays;
}

@Override
public PropertyControl getPropertyControl() {
	return propertyControl;
}

@Override
public boolean isPause() {
	return opMode==SCCAD.OP_SIMULATION && simulationMode==SCCAD.SIMULATION_PAUSE;
}

}
