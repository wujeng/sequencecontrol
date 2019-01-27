package tw.com.justiot.sequencecontrol.eelement;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.panel.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/*
commandListener.add(new changeNameCommand(this, oldname, name));
commandListener.add(new changePLCAddressCommand(this, oldPLCAddress, PLCAddress));
commandListener.add(new changePresetCommand(this,oldpv,PresetValue));
*/
public class EDevice extends JPanel implements MouseListener,ActionListener,CEDeviceListener
 {public CEDevice ced;
  public static int ELimitSwitchCount=0;
  public static final int Command_changeName=1;
  public static final int Command_changePLCAddress=2;
  public static final int Command_changePreset=3;
  public static final int TimerPeriod=200;

  public static final int MODE_Simulation=0;
  public static final int MODE_Edit=1;
  public static final int MODE_Input=2;
  public int mode=MODE_Edit;

  private JMenuItem connectionMenuItem;

  public static Dimension cellDim;
  private static Dimension realImageDim;

  public Image realImage;
  public Image ino,inohc,inc,incho,isol1,isol1on,isol2,isol2on;


//  public boolean remoteTrigger=false;

  public int activateKey;
  private String keyString;

  public static int DeviceWidth0,DeviceHeight0;
  private int imagex1,imagex2,imagey1,imagey2;
  private int Width,Height;
  private int cellwid,cellhgt,buttonw,buttonh;

  public JPopupMenu popup;
  protected JMenuItem m;
  protected CustomDialog customDialog;
  protected ElectricListener electriclistener;
  public EDevice(String mname, ElectricListener electriclistener)
  {super(true);
   this.electriclistener=electriclistener;
   if(PneumaticConfig.parameter.containsKey(mname))
    {EDeviceParameter ep=(EDeviceParameter) PneumaticConfig.parameter.get(mname);
     CEDevice.setModelType(ep.modelType);
     ced=new CEDevice(ep.modelName,ep.abbr,ep.actionType, electriclistener);
     ced.addListener(this);

     realImage=ep.realImage;
     ino=ep.ino;
     inohc=ep.inohc;
     inc=ep.inc;
     incho=ep.incho;
     isol1=ep.isol1;
     isol1on=ep.isol1on;
     isol2=ep.isol2;
     isol2on=ep.isol2on;
     if(realImageDim==null) realImageDim=ep.realImageDim;
     if(cellDim==null) cellDim=ep.imageDim;
     rescale();
     setSize(Width,Height);

     addMouseListener(this);

     menuItemMouseAdapter allmenuItemMouseAdapter=new menuItemMouseAdapter();
     popup = new JPopupMenu();
     m = new JMenuItem(Config.getString("EDevice.name"));
     m.addActionListener(this);
     m.addMouseListener(allmenuItemMouseAdapter);
     popup.add(m);
     
     if(mname.equals("PushButton") || mname.equals("ToggleButton")) {
    	 m = new JMenuItem(Config.getString("EDevice.activatekey"));
         m.addActionListener(this);
         m.addMouseListener(allmenuItemMouseAdapter);
         popup.add(m);
     }
     
     
     
     m = new JMenuItem(Config.getString("EDevice.PLCAddress"));
     m.addActionListener(this);
     m.addMouseListener(allmenuItemMouseAdapter);
     popup.add(m);
/*
     m = new JMenuItem("zoom");
     m.addActionListener(this);
     m.addMouseListener(allmenuItemMouseAdapter);
*/
     popup.add(m);
     if(ced.actionType==ced.TYPE_TIMER || ced.actionType==ced.TYPE_COUNTER)
      {popup.addSeparator();
        m = new JMenuItem(Config.getString("EDevice.PresetValue"));
        m.addActionListener(this);
        m.addMouseListener(allmenuItemMouseAdapter);
        popup.add(m);
      }
     /*
     if(ced.actionType==ced.TYPE_MECHANIC)
      {popup.addSeparator();
        m = new JMenuItem(Config.getString("EDevice.Position"));
        m.addActionListener(this);
        m.addMouseListener(allmenuItemMouseAdapter);
        popup.add(m);
  //      popup.addSeparator();
 //       connectionMenuItem = new JMenuItem(Config.getString("EDevice.Connection"));
  //      connectionMenuItem.addActionListener(this);
  //      connectionMenuItem.addMouseListener(allmenuItemMouseAdapter);
 //       popup.add(connectionMenuItem);
      }
//     if(ced.actionType==ced.TYPE_MANUAL_AUTO || ced.actionType==ced.TYPE_MANUAL_TOGGLE
 //   		 || ced.actionType==ced.TYPE_SENSOR)
 //     {popup.addSeparator();
 //       connectionMenuItem = new JMenuItem(Config.getString("EDevice.Connection"));
 //       connectionMenuItem.addActionListener(this);
 //       connectionMenuItem.addMouseListener(allmenuItemMouseAdapter);
 //       popup.add(connectionMenuItem);
 //     }
  */
  
     if(!ced.modelName.equals("ELimitSwitch")) {
       popup.addSeparator();
       m = new JMenuItem(Config.getString("EDevice.delete"));
       m.addActionListener(this);
       m.addMouseListener(allmenuItemMouseAdapter);
       popup.add(m);
     }
     if(ep.modelName.equals("ELimitSwitch")) ELimitSwitchCount++;
    }
  }

  public String toString()
   {
	return ced.name;
   }

  public void statusChanged(CEDevice ced)
   {if(electriclistener!=null) electriclistener.getElectricPanel().repaint();
	 repaint();
   }
  public void valueChanged(CEDevice ced)
   {if(electriclistener!=null) electriclistener.getElectricPanel().repaint();
	 repaint();
   }

  public EDevice(CEDevice ced, ElectricListener electriclistener)
  {super(true);
   this.electriclistener=electriclistener;
   if(PneumaticConfig.parameter.containsKey(ced.modelName))
    {EDeviceParameter ep=(EDeviceParameter) PneumaticConfig.parameter.get(ced.modelName);

     this.ced=ced;
     CEDevice.setModelType(ep.modelType);
     ced.addListener(this);
     
     realImage=ep.realImage;
     ino=ep.ino;
     inohc=ep.inohc;
     inc=ep.inc;
     incho=ep.incho;
     isol1=ep.isol1;
     isol1on=ep.isol1on;
     isol2=ep.isol2;
     isol2on=ep.isol2on;
     if(realImageDim==null) realImageDim=ep.realImageDim;
     if(cellDim==null) cellDim=ep.imageDim;
     rescale();
     setSize(Width,Height);

     addMouseListener(this);

     menuItemMouseAdapter allmenuItemMouseAdapter=new menuItemMouseAdapter();
     popup = new JPopupMenu();
     m = new JMenuItem(Config.getString("EDevice.name"));
     m.addActionListener(this);
     m.addMouseListener(allmenuItemMouseAdapter);
     popup.add(m);
     
     if(ced.modelName.equals("PushButton") || ced.modelName.equals("ToggleButton")) {
    	 m = new JMenuItem(Config.getString("EDevice.activatekey"));
         m.addActionListener(this);
         m.addMouseListener(allmenuItemMouseAdapter);
         popup.add(m);
     }
     
     m = new JMenuItem(Config.getString("EDevice.PLCAddress"));
     m.addActionListener(this);
     m.addMouseListener(allmenuItemMouseAdapter);
     popup.add(m);
/*
     m = new JMenuItem("zoom");
     m.addActionListener(this);
     m.addMouseListener(allmenuItemMouseAdapter);
*/
     popup.add(m);
     if(ced.actionType==ced.TYPE_TIMER || ced.actionType==ced.TYPE_COUNTER)
      {popup.addSeparator();
        m = new JMenuItem(Config.getString("EDevice.PresetValue"));
        m.addActionListener(this);
        m.addMouseListener(allmenuItemMouseAdapter);
        popup.add(m);
      }
     /*
     if(ced.actionType==ced.TYPE_MECHANIC)
      {popup.addSeparator();
        m = new JMenuItem(Config.getString("EDevice.Position"));
        m.addActionListener(this);
        m.addMouseListener(allmenuItemMouseAdapter);
        popup.add(m);
    //    popup.addSeparator();
   //     connectionMenuItem = new JMenuItem(Config.getString("EDevice.Connection"));
    //    connectionMenuItem.addActionListener(this);
    //    connectionMenuItem.addMouseListener(allmenuItemMouseAdapter);
     //   popup.add(connectionMenuItem);
      }
 //    if(ced.actionType==ced.TYPE_MANUAL_AUTO || ced.actionType==ced.TYPE_MANUAL_TOGGLE
 //   		 || ced.actionType==ced.TYPE_SENSOR)
  //    {popup.addSeparator();
  //      connectionMenuItem = new JMenuItem(Config.getString("EDevice.Connection"));
  //      connectionMenuItem.addActionListener(this);
  //      connectionMenuItem.addMouseListener(allmenuItemMouseAdapter);
  //      popup.add(connectionMenuItem);
   //   }
    * 
    */
     if(!ced.modelName.equals("ELimitSwitch")) {
       popup.addSeparator();
       m = new JMenuItem(Config.getString("EDevice.delete"));
       m.addActionListener(this);
       m.addMouseListener(allmenuItemMouseAdapter);
       popup.add(m);
     }
/*
     BufferedImage buffer=new BufferedImage(DeviceWidth0,DeviceHeight0,BufferedImage.TYPE_INT_RGB);
     Graphics osg=buffer.createGraphics();
     if(realImage!=null) osg.drawImage(realImage,0,0,this);
     if(ino!=null) osg.drawImage(ino,0,0,this);
     if(inohc!=null) osg.drawImage(inohc,0,0,this);
     if(inc!=null) osg.drawImage(inc,0,0,this);
     if(incho!=null) osg.drawImage(incho,0,0,this);
     if(isol1!=null) osg.drawImage(isol1,0,0,this);
     if(isol1on!=null) osg.drawImage(isol1on,0,0,this);
     if(isol2!=null) osg.drawImage(isol2,0,0,this);
     if(isol2on!=null) osg.drawImage(isol2on,0,0,this);
*/
    }
  }

  public void rescale()
    {
     DeviceWidth0=cellDim.width*2+3+5;
     DeviceHeight0=cellDim.height*2+5+4+12*2;
     Width=(int)(((double)DeviceWidth0)*CEDevice.ratio);
     Height=(int)(((double)DeviceHeight0)*CEDevice.ratio);
     setPreferredSize(new Dimension(Width,Height));

     cellwid=(int)(((double)cellDim.width)*CEDevice.ratio);
     cellhgt=(int)(((double)cellDim.height)*CEDevice.ratio);
     imagex1=(int) (4*CEDevice.ratio);
     imagey1=(int) (16*CEDevice.ratio);
     imagex2=imagex1+cellwid+1;
     imagey2=imagey1+cellhgt+1;
     buttonw=(int)(24.0*CEDevice.ratio);
     buttonh=(int)(16.0*CEDevice.ratio);
    }

  private void drawFrame(Graphics g)
   {g.setColor(Color.white);
    g.drawLine(0,Height-2,0,0);
    g.drawLine(0,0,Width-2,0);
    g.drawLine(Width-2,0,Width-2,Height-2);
    g.drawLine(Width-2,Height-2,0,Height-2);
    g.setColor(Color.gray);
    g.drawLine(1,Height-3,1,1);
    g.drawLine(1,1,Width-3,1);
    g.drawLine(Width-1,1,Width-1,Height-1);
    g.drawLine(Width-1,Height-1,1,Height-1);
  }

  public void reset() {
	  ced.reset();
  }
  
  public void paintComponent(Graphics g)
   {super.paintComponent(g);
    drawFrame(g);
    if(ced.status)
     {g.drawImage(inohc,imagex1,imagey1,cellwid,cellhgt,this);
      g.drawImage(incho,imagex2,imagey1,cellwid,cellhgt,this);
     }
    else
     {g.drawImage(ino,imagex1,imagey1,cellwid,cellhgt,this);
      g.drawImage(inc,imagex2,imagey1,cellwid,cellhgt,this);
     }
    if(isol1!=null)
     {if(ced.sol1Status)
        g.drawImage(isol1on,imagex1,imagey2,cellwid,cellhgt,this);
       else
        g.drawImage(isol1,imagex1,imagey2,cellwid,cellhgt,this);
     }
    if(isol2!=null)
     {
      if(ced.sol2Status)
        g.drawImage(isol2on,imagex2,imagey2,cellwid,cellhgt,this);
       else
        g.drawImage(isol2,imagex2,imagey2,cellwid,cellhgt,this);
     }
    if(ced.name!=null)
     {g.setColor(Color.black);
      g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*CEDevice.ratio)));
      g.drawString(ced.name,imagex1,imagey1-4);
     }
    if(ced.PLCAddress!=null)
     {g.setColor(Color.blue);
       g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (9*CEDevice.ratio)));
       g.drawString(ced.PLCAddress,imagex2,imagey1-4);
     }
    if(this.keyString!=null)
    {g.setColor(Color.red);
      g.setFont(new Font("Times New Roman",Font.BOLD,(int) (12*CEDevice.ratio)));
      g.drawString(keyString,(imagex1+imagex2)/2,imagey2+cellhgt/2);
    }
    if(ced.actionType==ced.TYPE_MECHANIC)
     {if(ced.element==null && electriclistener!=null) electriclistener.getLimitswitchLocation(this);;
      if(ced.element!=null)
       {g.setColor(Color.blue);
        g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (12*CEDevice.ratio)));
        g.drawString("in "+ced.element.getName()+" "+ced.LSpos,imagex1,imagey2+cellhgt);
       }
/*
      ElectricFace sys=electriclistener.getEArrays().getElectricFace(this);
       if(sys!=null)
        {if(sys.getActuatorName()!=null && sys.getActuatorName().length()>0)
           {g.setColor(Color.blue);
             g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (12*CEDevice.ratio)));
             g.drawString("in "+sys.getActuatorName(),imagex1,imagey2+cellhgt);
           }
        }
*/
     }
    if(ced.actionType==ced.TYPE_SENSOR)
    {
//    	 System.out.println("EDevice paint SENSOR");
     if(ced.esb!=null)
      {g.setColor(Color.blue);
       g.setFont(new Font("Times New Roman", Font.BOLD, (int) (11*CEDevice.ratio)));
//       g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (12*CEDevice.ratio)));
       if(ced.esb instanceof PumpMonitor)
        {PumpMonitor pm=(PumpMonitor) ced.esb;
    	 if(ced==pm.ced1) g.drawString(ced.esb.cdo.name+" "+Config.getString("PumpMonitor.glight"),imagex1,imagey2+cellhgt);
    	 else if(ced==pm.ced2) g.drawString(ced.esb.cdo.name+" "+Config.getString("PumpMonitor.rlight"),imagex1,imagey2+cellhgt);
//    	 if(ced.LSpos==0) g.drawString(ced.esb.cdo.name+" �B���O��",imagex1,imagey2+cellhgt);
//      	 else if(ced.LSpos==1) g.drawString(ced.esb.cdo.name+" ���`�O��",imagex1,imagey2+cellhgt);
        }
       else if(ced.esb instanceof WaterTank)
       {WaterTank wt=(WaterTank) ced.esb;
// System.out.println("EDevice paint WaterTank");
   	    if(ced==wt.cedvh) g.drawString(((WaterTank)(ced.esb)).name+" "+Config.getString("WaterTank.vh"),imagex1,imagey2+cellhgt);
   	    else if(ced==wt.cedh) g.drawString(((WaterTank)(ced.esb)).name+" "+Config.getString("WaterTank.h"),imagex1,imagey2+cellhgt);
   	    else if(ced==wt.cedl) g.drawString(((WaterTank)(ced.esb)).name+" "+Config.getString("WaterTank.l"),imagex1,imagey2+cellhgt);
     	else if(ced==wt.cedvl) g.drawString(((WaterTank)(ced.esb)).name+" "+Config.getString("WaterTank.vl"),imagex1,imagey2+cellhgt);
       }
       else
        g.drawString("in "+ced.esb.getName()+" "+ced.LSpos,imagex1,imagey2+cellhgt);
      }
    }
     if(ced.actionType==ced.TYPE_COUNTER || ced.actionType==ced.TYPE_TIMER)
     {g.setColor(Color.red);
       g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*CEDevice.ratio)));
       g.drawString(Integer.toString(ced.CurrentValue),imagex1,Height-2);
       g.setColor(Color.blue);
       g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*CEDevice.ratio)));
       g.drawString(Integer.toString(ced.PresetValue),imagex2,Height-2);
     }
    if(ced.NAPKey!=null && ced.NAPKey.length()>0)
     {g.setColor(Color.red);
       g.setFont(new Font("Times New Roman",Font.PLAIN,(int) (10*CEDevice.ratio)));
       if(!ced.NAPKey.toLowerCase().equals("null"))
        {g.drawString(cutNAPKey(4)+" "+Integer.toString(ced.NAPno),imagex1,Height-2);
//System.err.println("no null");
        }
     }
   }

 private String cutNAPKey(int n)
  {if(ced.NAPKey==null || ced.NAPKey.length()==0) return "";
    int ind=ced.NAPKey.lastIndexOf('.');
    String str=ced.NAPKey.substring(ind+1,ced.NAPKey.length());
    if(str.length()>n) str=str.substring(0,n);
    return str;
  }

  public Image getRealImage() {return realImage;}

  public String write()
   {return ced.write();
   }

  public static EDevice read(String str, ElectricListener electriclistener)
   {
	  CEDevice ced=CEDevice.read(str, electriclistener);
	  EDevice ed=new EDevice(ced, electriclistener);
      return ed;
/*
	  StringTokenizer token=new StringTokenizer(str);
	    int dtype=Integer.parseInt(token.nextToken());
	    if(dtype!=WebLadderCAD.Data_EDevice) return null;
//	    String dtype=token.nextToken();
//	    if(!dtype.equals("EDevice")) return null;
	    String mType=token.nextToken();
	    String mName=token.nextToken();
	    String name=token.nextToken();
	    if(name.equals("null")) name=null;
	    String plc=token.nextToken();
	    if(plc.equals("null")) plc=null;
	    int pvalue=Integer.parseInt(token.nextToken());
	    double rat=Double.parseDouble(token.nextToken());
	    EDevice ed=Creater.instanceEDevice(mType,mName);
	    if(ed!=null)
	     {if(ed.ced.name!=null) ed.ced.name=name;
	       if(plc!=null) ed.ced.PLCAddress=plc;
	       ed.ced.PresetValue=pvalue;
	       CEDevice.ratio=rat;

	       if(token.hasMoreTokens())
	        {ed.ced.NAPKey=token.nextToken();
	          ed.ced.NAPno=Integer.parseInt(token.nextToken());
	        }
	     }
	    return ed;
*/
   }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }
   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {int wid=imagex2-imagex1;
         int hgt=imagey2-imagey1;
        boolean inArea1=(ex > imagex1 && ex < imagex2 && ey > imagey1 && ey < imagey2);
        boolean inArea2=(ex > imagex2 && ex < imagex2+wid && ey > imagey1 && ey < imagey2);
        boolean inArea3=(ex > imagex1 && ex < imagex2 && ey > imagey2 && ey < imagey2+hgt);
        boolean inArea4=(ex > imagex2 && ex < imagex2+wid && ey > imagey2 && ey < imagey2+hgt);
        if(inArea1)
         {
//System.out.print("mode="+mode);
          switch(mode)
            {case MODE_Simulation:
//System.out.println("Simulation");
            	 ced.setStatus(true);
            	 repaint();
            	 break;
              case MODE_Edit:
            	  if(electriclistener!=null) electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NO,this.ced,null,true);
                 break;
              case MODE_Input:
                 break;
            }
         }
         if(inArea2)
         {switch(mode)
            {case MODE_Simulation:
            	 ced.setStatus(true);
            	 repaint();
                 break;
              case MODE_Edit:
            	  if(electriclistener!=null) electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_NC,this.ced,null,true);
                 break;
              case MODE_Input:
                 break;
            }
         }
         if(inArea3)
         {switch(mode)
            {case MODE_Simulation:
            	 ced.setSol1Status(true);
            	 repaint();
                 break;
              case MODE_Edit:
                 if(ced.actionType==ced.TYPE_ELECTRIC || ced.actionType==ced.TYPE_TIMER || ced.actionType==ced.TYPE_COUNTER)
                	 if(electriclistener!=null) electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL1,this.ced,null,true);
                 break;
              case MODE_Input:
                 if(ced.actionType==ced.TYPE_TIMER)
                 if(electriclistener!=null && electriclistener.hasSequence() && electriclistener.getSequencePanel()!=null)
                	 electriclistener.getSequencePanel().AddCell(SequenceCell.IT_Delay,null,this,SequenceCell.ID_None,true);
                 break;
            }
         }
         if(inArea4)
         {if(ced.actionType!=ced.TYPE_COUNTER) return;
           switch(mode)
            {case MODE_Simulation:
            	 ced.setSol2Status(true);
            	 repaint();
                 break;
              case MODE_Edit:
            	  if(electriclistener!=null) electriclistener.getElectricPanel().AddCell(LadderCell.T_EDevice,LadderCell.G_ESOL2,this.ced,null,true);
                 break;
              case MODE_Input:
                 break;
            }
         }
       }
      else
       maybeShowPopup(pop,ex,ey);
   }
  public void mouseReleased(MouseEvent e) {
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {int wid=imagex2-imagex1;
         int hgt=imagey2-imagey1;
        boolean inArea1=(ex > imagex1 && ex < imagex2 && ey > imagey1 && ey < imagey2);
        boolean inArea2=(ex > imagex2 && ex < imagex2+wid && ey > imagey1 && ey < imagey2);
        boolean inArea3=(ex > imagex1 && ex < imagex2 && ey > imagey2 && ey < imagey2+hgt);
        boolean inArea4=(ex > imagex2 && ex < imagex2+wid && ey > imagey2 && ey < imagey2+hgt);
        if(inArea1)
         {switch(mode)
            {case MODE_Simulation:
            	 ced.setStatus(false);
            	 repaint();
                 break;
              case MODE_Edit:
                 break;
              case MODE_Input:
                 break;
            }
         }
         if(inArea2)
         {switch(mode)
            {case MODE_Simulation:
            	 ced.setStatus(false);
            	 repaint();
                 break;
              case MODE_Edit:
                 break;
              case MODE_Input:
                 break;
            }
         }
         if(inArea3)
         {switch(mode)
            {case MODE_Simulation:
            	 ced.setSol1Status(false);
            	 repaint();
                 break;
              case MODE_Edit:
                 break;
              case MODE_Input:
                 break;
            }
         }
         if(inArea4)
         {if(ced.actionType!=ced.TYPE_COUNTER) return;
           switch(mode)
            {case MODE_Simulation:
            	 ced.setSol2Status(false);
            	 repaint();
                 break;
              case MODE_Edit:
                 break;
              case MODE_Input:
                 break;
            }
         }
       }
      else
       maybeShowPopup(pop,ex,ey);
   }


  public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop)
       {if(connectionMenuItem!=null)
          {//if(Modules.hasModules())
	       // connectionMenuItem.setEnabled(true);
           //else
            connectionMenuItem.setEnabled(false);
          }
        popup.show(this, ex, ey);
      }
    }

    public void switchItem(int n)
    {JMenuItem mi=(JMenuItem) popup.getComponent(n);
      Component[] coms=popup.getComponents();
       for(int i=0;i<coms.length;i++)
         {if(coms[i]==mi) mi.setArmed(true);
          else
            {if(coms[i] instanceof JMenuItem)
                ((JMenuItem) coms[i]).setArmed(false);
            }
         }
    }

  public void actionPerformed(ActionEvent e)
   {ActionPerformed((JMenuItem) e.getSource(),null,null);
   }

  public void ActionPerformed(JMenuItem mi,String op,String input)
    {
      String option=mi.getText();
    if(option.equals(Config.getString("EDevice.name")))
     {String oldname=ced.name;
      customDialog = new CustomDialog(new JFrame(),Config.getString("EDevice.name"),Config.getString("EDevice.modifyname"),CustomDialog.VALUE_STRING);
      customDialog.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
      customDialog.setTextField(ced.name);
      customDialog.setVisible(true);
      ced.name=customDialog.getValidatedText();
      if(electriclistener!=null) electriclistener.addCommand(new changeNameCommand(this, oldname, ced.name));
      repaint();
     }
    if(option.equals(Config.getString("EDevice.activatekey")))
    {KeyDialog keyDialog = new KeyDialog(electriclistener.getFrame());
    keyDialog.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    keyDialog.setLocation(
	screenSize.width/2 - keyDialog.getSize().width/2,
	screenSize.height/2 - keyDialog.getSize().height/2);
    keyDialog.setVisible(true);
    activateKey=keyDialog.getKey();
    keyString=keyDialog.getKeyString();
 //   System.out.println(activateKey+":"+keyString);
     repaint();
    }
    else if(option.equals(Config.getString("EDevice.PLCAddress")))
     {String oldPLCAddress=ced.PLCAddress;
	  customDialog = new CustomDialog(new JFrame(),Config.getString("EDevice.PLCAddress"),Config.getString("EDevice.modifyPLCAddress"),CustomDialog.VALUE_STRING);
      customDialog.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
      customDialog.setTextField(ced.PLCAddress);
      customDialog.setVisible(true);
      ced.PLCAddress=customDialog.getValidatedText();
      if(electriclistener!=null) electriclistener.addCommand(new changePLCAddressCommand(this, oldPLCAddress, ced.PLCAddress));
      repaint();
     }
 //   else if(option.equals(Config.getString("EDevice.Position")))
 //    {if(electriclistener!=null) electriclistener.getElectricPanel().putEDevice(this);
  //   }
    else if(option.equals(Config.getString("EDevice.PresetValue")))
     {int oldpv=ced.PresetValue;
	  customDialog = new CustomDialog(new JFrame(),Config.getString("EDevice.PresetValue"),Config.getString("EDevice.modifyPresetValue"),CustomDialog.VALUE_INT);
      customDialog.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      customDialog.setLocation(screenSize.width/2 - customDialog.getSize().width/2,screenSize.height/2 - customDialog.getSize().height/2);
      customDialog.setTextField(Integer.toString(ced.PresetValue));
      customDialog.setVisible(true);
      ced.PresetValue=customDialog.getInt();
      ced.CurrentValue=ced.PresetValue;
      if(electriclistener!=null) electriclistener.addCommand(new changePresetCommand(this,oldpv,ced.PresetValue));

     }
//    else if(option.equals(Config.getString("EDevice.Connection")))
//     {if(electriclistener!=null) electriclistener.getElectricPanel().connectEDevice(this);
//     }
    else if(option.equals(Config.getString("EDevice.delete")))
     {electriclistener.getEArrays().deleteEDevice(this);
     }
  }

  private class changeNameCommand extends Command
    {String oldname;
     String newname;
	public changeNameCommand(Object ele,String oldname,String newname)
     {super("EDevice",ele,Command_changeName);
      this.oldname=oldname;
      this.newname=newname;
     }
    public void undo()
     {ced.name=oldname;
     }
    public void redo()
     {ced.name=newname;
     }
   }
  private class changePLCAddressCommand extends Command
    {String oldadd;
     String newadd;
	public changePLCAddressCommand(Object ele,String oldadd,String newadd)
     {super("EDevice",ele,Command_changePLCAddress);
      this.oldadd=oldadd;
      this.newadd=newadd;
     }
    public void undo()
     {ced.PLCAddress=oldadd;
     }
    public void redo()
     {ced.PLCAddress=newadd;
     }
   }
  private class changePresetCommand extends Command
    {int oldvalue;
     int newvalue;
	public changePresetCommand(Object ele,int oldvalue,int newvalue)
     {super("EDevice",ele,Command_changePreset);
      this.oldvalue=oldvalue;
      this.newvalue=newvalue;
     }
    public void undo()
     {ced.PresetValue=oldvalue;
      ced.CurrentValue=ced.PresetValue;
     }
    public void redo()
     {ced.PresetValue=newvalue;
      ced.CurrentValue=ced.PresetValue;
     }
   }

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

 }
