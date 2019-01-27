package tw.com.justiot.sequencecontrol.eelement;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.Creater;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.Rect;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;

public abstract class ESystemBase extends JPanel implements MouseListener,MouseMotionListener,ActionListener,CDOutputListener,ElectricFace
 {
  public static final int TimerPeriod=300;
  public double ratio=1.0;
  public CDOutput cdo;
  public ESymbol esymbol;

  protected int curImage;
  protected boolean timeron;
  protected Image realImage;
  protected Dimension realImageDim;
  protected Image[] images;
  protected Dimension imageDim;
  protected Image animatedImage;
  public Image EFsol1=null;
  public Image EFsol1on=null;
  public Image EFsol2=null;
  public Image EFsol2on=null;
  public Rect actionRect1=null;
  public Rect actionRect2=null;
  public Rect inputRect1=null;
  public Rect inputRect2=null;
  protected AudioClip audioClip;
  protected static boolean oneplaying=false;
  protected boolean playing=false;
  protected boolean soundon=true;
  protected boolean movable;
  public abstract void check();

  public JPopupMenu popup;
  protected JMenuItem m;
  protected boolean popupon;
  protected CustomDialog customDialog;

  protected int Width,Height,imagewid,imagehgt;
  protected boolean drag=false;
  protected int xm,ym,pressedx,pressedy,pressedPosx,pressedPosy;

  public boolean isTimeron() {return timeron;}
  public void setTimeron(boolean ton) {timeron=ton;}

  public void startTimer(java.util.Timer timer)
   {timer.scheduleAtFixedRate(new TimeTask(),0,TimerPeriod);
   }

  private class TimeTask extends TimerTask
   {
	public void run()
	 {check();
     }
   }


  protected static Hashtable nametable=new Hashtable();
  public ESymbol getESymbol() {return esymbol;}
  public CDOutput getCDOutput() {return cdo;}

//  CDOutputListener
//  public void solFStatusChanged(CDOutput ces);
//  public void solBStatusChanged(CDOutput ces);
 public void nameChanged(CDOutput cdo)
  {//name=cdo.getName();
   //if(WebLadderCAD.electrics!=null) WebLadderCAD.electrics.repaint();
  }
//ESystemBase
  public Image getEFsol1() {return EFsol1;}
  public Image getEFsol1on() {return EFsol1on;}
  public Image getEFsol2()  {return EFsol2;}
  public Image getEFsol2on() {return EFsol2on;}
  public Rect getActionRect1() {return actionRect1;}
  public Rect getActionRect2() {return actionRect2;}
  public int getForceNumber() {return CDOutput.TYPE_ONOFF;}
  public boolean getMemory() {return false;}
  public void reset() {cdo.reset();}
  public String getActuatorName() {return cdo.name;}

 public Rect getLSRect() {return null;}
 public ArrayList getValveArray() {return null;}
 public ArrayList getValvePos() {return null;}
 public boolean withLS(){return false;}
 public EDevice getFirstLimitswitch(){return null;}
 public EDevice getLastLimitswitch(){return null;}
 private static tw.com.justiot.sequencecontrol.panel.ESystemPanel container;

  public static void updateOneplaying()
   {ESystemBase esb;
	Component[] comps=container.getComponents();
	ESystemBase.oneplaying=false;
	for(int i=0;i<comps.length;i++)
	 {esb=(ESystemBase) comps[i];
	  if(esb.playing)
	   {ESystemBase.oneplaying=true;
	    return;
	   }
	 }
   }

 protected ElectricListener electriclistener;
 public ESystemBase(boolean movable,boolean soundon, boolean popupon, ElectricListener electriclistener)
 {
	this(movable, electriclistener);
	this.soundon=soundon;
	this.popupon=popupon;
 }

public ESystemBase(boolean movable,boolean soundon, ElectricListener electriclistener)
 {
	this(movable, electriclistener);
	this.soundon=soundon;
 }

  public ESystemBase(boolean movable, ElectricListener electriclistener)
   {
	this(electriclistener);
	this.movable=movable;
   }

  public ESystemBase(ElectricListener electriclistener)
   {super();
   Border blackline, raisedetched, loweredetched,
   raisedbevel, loweredbevel, empty;
	  blackline = BorderFactory.createLineBorder(Color.black);
	  raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	  loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	  raisedbevel = BorderFactory.createRaisedBevelBorder();
	  loweredbevel = BorderFactory.createLoweredBevelBorder();
	  empty = BorderFactory.createEmptyBorder();
 //  setPreferredSize(new Dimension(120, 120));
	  setBorder(raisedetched);
   
    this.electriclistener=electriclistener;
    curImage=0;
 //   setBackground(Color.white);

    addMouseListener(this);
    addMouseMotionListener(this);
    movable=true;

    popup = new JPopupMenu();
    m = new JMenuItem(Config.getString("ESystemBase.name"));
    m.addActionListener(this);
    m.addMouseListener(new menuItemMouseAdapter());
    popup.add(m);
    m = new JMenuItem(Config.getString("ESystemBase.delete"));
    m.addActionListener(this);
    m.addMouseListener(new menuItemMouseAdapter());
    popup.add(m);
    popupon=true;
//    popup.addSeparator();
    popup.addSeparator();
	m = new JMenuItem(Config.getString("ESystemBase.setRatio"));
    m.addActionListener(this);
    m.addMouseListener(new menuItemMouseAdapter());
    popup.add(m);
    popup.addSeparator();
    if(container==null) container=electriclistener.getESystemPanel();
   }

  public String defaultName()
   {String newname=null;
	String npref=cdo.modelName;
    if(cdo.modelName.length()>0) npref=cdo.modelName.substring(0,1);
    String nameno=(String) nametable.get(cdo.modelType+"*"+cdo.modelName);
//if(WebLadderCAD.debug) System.out.println("defaultName:"+cdo.modelType+"*"+cdo.modelName+"nameno="+nameno);
    if(nameno!=null && nameno.length()>0)
     {nameno=new Integer(Integer.parseInt(nameno)+1).toString();
	  newname=npref+nameno;
	  nametable.put(cdo.modelType+"*"+cdo.modelName, nameno);
     }
    else
     {
	  newname=npref+"1";
	  nametable.put(cdo.modelType+"*"+cdo.modelName, "1");
     }
    return newname;
   }

  public void statusChanged(CEDevice ced) {}
  public void valueChanged(CEDevice ced) {}
/*
  public void paintComponent(Graphics g)
   {
    super.paintComponent(g);
   }
*/
  public abstract String extraWrite();
  public abstract void extraRead(String str);

  public String write()
   {
    StringBuffer sb=new StringBuffer();
    sb.append(Integer.toString(SCCAD.Data_ESystemBase)+" ");
    sb.append(cdo.modelType+" ");
    sb.append(cdo.modelName+" ");
    if(cdo.name==null || cdo.name.length()==0) sb.append("null ");
    else sb.append(cdo.name+" ");
    sb.append(getX()+" "+getY()+" ");
    sb.append(curImage+" ");
    if(cdo.FPLCAddress==null) sb.append("null ");
    else sb.append(cdo.FPLCAddress+" ");
    if(cdo.BPLCAddress==null) sb.append("null ");
    else sb.append(cdo.BPLCAddress+" ");
    if(cdo.NAPKey1==null) sb.append("null ");
    else sb.append(cdo.NAPKey1+" ");
    sb.append(cdo.NAPno1+" ");
    if(cdo.NAPKey2==null) sb.append("null ");
    else sb.append(cdo.NAPKey2+" ");
    sb.append(cdo.NAPno2+" ");
    return sb.toString()+extraWrite();
   }

 public static tw.com.justiot.sequencecontrol.eelement.ESystemBase read(String str, ElectricListener electriclistener)
  {try {
   if(Config.getBoolean("debug")) System.out.println("ESystemBase: read():"+str);
   StringTokenizer token=new StringTokenizer(str);
   int dtype=Integer.parseInt(token.nextToken());
   if(dtype!=SCCAD.Data_ESystemBase) return null;
   String mType=token.nextToken();
   String mName=token.nextToken();
   String name=token.nextToken();
   if(name.equals("null")) name="";
   int x=Integer.parseInt(token.nextToken());
   int y=Integer.parseInt(token.nextToken());
   int cimage=Integer.parseInt(token.nextToken());
//System.out.println("Element instanceElement");
   Object obj=Creater.instanceESystem(mType,mName,electriclistener);
   if(obj==null) return null;
   if(!(obj instanceof tw.com.justiot.sequencecontrol.eelement.ESystemBase)) return null;
   tw.com.justiot.sequencecontrol.eelement.ESystemBase element=(tw.com.justiot.sequencecontrol.eelement.ESystemBase) obj;
//System.out.println("Element created!");
   if(element!=null)
    {if(name!=null) element.cdo.name=name;
     element.setLocation(x,y);
     element.curImage=cimage;

     element.cdo.FPLCAddress=token.nextToken();
     if(element.cdo.FPLCAddress.toLowerCase().indexOf("null")>=0) element.cdo.FPLCAddress=null;
     element.cdo.BPLCAddress=token.nextToken();
     if(element.cdo.BPLCAddress.toLowerCase().indexOf("null")>=0) element.cdo.BPLCAddress=null;
     element.cdo.NAPKey1=token.nextToken();
     element.cdo.NAPno1=Integer.parseInt(token.nextToken());
     element.cdo.NAPKey2=token.nextToken();
     element.cdo.NAPno2=Integer.parseInt(token.nextToken());
     if(element.cdo.NAPKey1.toLowerCase().indexOf("null")>=0) element.cdo.NAPKey1=null;
     if(element.cdo.NAPKey2.toLowerCase().indexOf("null")>=0) element.cdo.NAPKey2=null;


//System.err.println("enter extra");
     if(token.hasMoreTokens())
      {
        String extra=token.nextToken("\n");
//System.err.println("element extra"+extra);
        if(extra!=null && extra.length()>1 && extra.substring(0,1).equals(" ")) extra=extra.substring(1,extra.length());
        if(extra!=null && extra.length()>0)
         {
//System.out.println(extra);
           element.extraRead(extra);
         }
      }
    }
   return element;
  } catch(Exception e) {
	  e.printStackTrace();
	  return null;
  }
  }
  public void mouseClicked(MouseEvent e)
   {
//    boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
//    boolean pop=e.isPopupTrigger();
//    MouseClicked(e,left,e.getX(),e.getY(),pop);
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

  public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop)
   {
    if(left)
     {pressedx=ex;
      pressedy=ey;
      pressedPosx=getLocation().x;
      pressedPosy=getLocation().y;
//System.err.println(ex+":"+ey+"   pressedpos:"+pressedPosx+":"+pressedPosy);
     }
    else
     maybeShowPopup(pop,ex,ey);
   }
  public void MouseDragged(MouseEvent e, boolean left, int ex, int ey, boolean pop)
  {
   if(left)
    {xm=ex;
     ym=ey;
     drag=true;
 //    repaint();
 //    System.out.println(ex+":"+ey);
     pressedPosx=getLocation().x;
     pressedPosy=getLocation().y;
     setLocation(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
  //   electriclistener.repaint();
//     pressedx=ex;
 //    pressedy=ey;
 //    pressedPosx=getLocation().x;
 //    pressedPosy=getLocation().y;
    }
  }
  public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop)
  {
   if(left)
    {
//     if(drag && esystemBaseListener!=null)
//   	 esystemBaseListener.move(new MoveEvent(this,pressedPosx+ex-pressedx,pressedPosy+ey-pressedy));
     if(movable)
      {
  // 	System.out.println(ex+":"+ey);
    	 pressedPosx=getLocation().x;
         pressedPosy=getLocation().y;
       setLocation(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
       container.repaint();
       electriclistener.setModified(true);
      }
     drag=false;
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
//      popup.show(e.getComponent(), e.getX(), e.getY());
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

  public void switchItem(int n)
   {JMenuItem mi=(JMenuItem) popup.getComponent(n);
    Component[] coms=popup.getComponents();
//System.err.println("switchitem"+coms.length);
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
    if(option.equals(Config.getString("ESystemBase.delete")))
     {if(this instanceof WaterTank)
       {
         WaterTank wt=(WaterTank) this;
         electriclistener.getEDevicePanel().remove(wt.pumpa.esymbol);
         electriclistener.getEDevicePanel().remove(wt.pumpb.esymbol);
         electriclistener.getEArrays().ElectricFaceArray.remove((ESystemBase) wt.pumpa);
         electriclistener.getEArrays().ElectricFaceArray.remove((ESystemBase) wt.pumpb);
         if(electriclistener.hasSequence()) electriclistener.sequenceRefreshSystemCombo();
         repaint();
         return;
        }
      electriclistener.deleteESystemBase(this);
      container.remove(this);
      container.repaint();
      electriclistener.setModified(true);
     }
    else if(option.equals(Config.getString("ESystemBase.name")))
     {if(this instanceof WaterTank)
       {
         WaterTank wt=(WaterTank) this;
         String oldname=wt.name;
   	  customDialog = new CustomDialog(new JFrame(),Config.getString("ESystemBase.name"),Config.getString("ESystemBase.modifyname"),CustomDialog.VALUE_STRING);
         customDialog.pack();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         customDialog.setLocation(
   	  screenSize.width/2 - customDialog.getSize().width/2,
   	  screenSize.height/2 - customDialog.getSize().height/2);
         customDialog.setTextField(wt.name);
         customDialog.setVisible(true);
         wt.name=customDialog.getValidatedText();
         repaint();
         return;
        }
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
     }
    else if(option.equals(Config.getString("ESystemBase.setRatio")))
     {customDialog = new CustomDialog(new JFrame(),Config.getString("ESystemBase.setRatio"),Config.getString("ESytemBase.setRatio"),CustomDialog.VALUE_FLOAT);
      customDialog.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      customDialog.setLocation(
	  screenSize.width/2 - customDialog.getSize().width/2,
	  screenSize.height/2 - customDialog.getSize().height/2);
      customDialog.setTextField(new Float(ratio).toString());
      customDialog.setVisible(true);
      setRatio(customDialog.getFloat());
     }
   }

  class menuItemMouseAdapter extends MouseAdapter
   {
    public void mouseEntered(MouseEvent e)
     {int ind=popup.getComponentIndex((Component)e.getSource());
     }
   }


 }
