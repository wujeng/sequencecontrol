package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import tw.com.justiot.sequencecontrol.Command;
import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.Creater;
import tw.com.justiot.sequencecontrol.EArrays;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.ElementParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.dialog.CustomDialog;
import tw.com.justiot.sequencecontrol.panel.ConnectEvent;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.panel.PneumaticPanel;
import tw.com.justiot.sequencecontrol.part.BoardElement;
import tw.com.justiot.sequencecontrol.part.Port;
import tw.com.justiot.sequencecontrol.part.Position;

public abstract class PneumaticElement extends JPanel //implements MouseListener,MouseMotionListener,ActionListener
 {
  public static void clearCount() {
	Actuator.count=0;
	Connector.count=0;
	Delay.allcount=0;
	Valve.count=0;
	FlowValve.count=0;
	Gauge.count=0;
	Logic.count=0;
	PressureValve.count=0;
  }
  protected boolean mouseoff = true;
 
  public static final int Command_rotate=1;
  public static final int Command_changeName=2;

  protected PneumaticListener pneumaticlistener;
  private String name;
  protected String modelType;
  public String modelName;
  protected int curImage;
  protected double angle;

  protected Image realImage;
  protected Dimension realImageDim;
  protected Image[] images;
  protected Dimension imageDim;

  public Port[] ports;
  protected Port[] portOrg;

  public JPopupMenu popup;
  protected JMenuItem m,rotm;
  private boolean grouped;

  protected int width0,height0;
  protected CustomDialog customDialog;

  public void startTimer(java.util.Timer timer) {}

  private static Hashtable nametable=new Hashtable();

  protected tw.com.justiot.sequencecontrol.panel.PneumaticPanel pneumaticPanel;

  public PneumaticElement(String mname,boolean hasName, PneumaticListener owner)
   {super(true);
    this.pneumaticlistener=owner;
    this.pneumaticPanel=owner.getPneumaticPanel();
    if(PneumaticConfig.parameter.containsKey(mname))
     {ElementParameter ep=(ElementParameter) PneumaticConfig.parameter.get(mname);
      modelType=ep.modelType;
      modelName=ep.modelName;
      realImage=ep.realImage;
      realImageDim=ep.realImageDim;
      images=ep.images;
      imageDim=ep.imageDim;

      setSize(imageDim.width,imageDim.height);
      width0=imageDim.width;
      height0=imageDim.height;
      curImage=0;
      setBackground(Color.white);
/*
      addMouseListener(this);
      addMouseMotionListener(this);

      popup = new JPopupMenu();
      m = new JMenuItem(Config.getString("PneumaticElement.delete"));
      m.addActionListener(this);
      m.addMouseListener(new menuItemMouseAdapter());
      popup.add(m);
      rotm = new JMenuItem(Config.getString("PneumaticElement.rotate"));
      rotm.addActionListener(this);
      rotm.addMouseListener(new menuItemMouseAdapter());
      popup.add(rotm);

      popup.addSeparator();
      m = new JMenuItem(Config.getString("PneumaticElement.name"));
      m.addActionListener(this);
       m.addMouseListener(new menuItemMouseAdapter());
      popup.add(m);
      grouped=false;
      angle=0.0;
 */     
      if(hasName) setName(defaultName());

      if(modelType.equals("Actuator") || modelType.equals("Delay"))
       {startTimer(pneumaticPanel.timer);
       }
     }
   }

  public String defaultName()
   {String newname=null;
	String npref=modelName;
 //   if(modelName.length()>0) npref=modelName.substring(0,1);
	npref=modelType.substring(0,1);
 //   String nameno=(String) nametable.get(modelType+"*"+modelName);
	String nameno=(String) nametable.get(npref);
    if(nameno!=null && nameno.length()>0)
     {nameno=new Integer(Integer.parseInt(nameno)+1).toString();
  	  newname=npref+nameno;
 	  nametable.put(npref, nameno);
     }
    else
     {
 	  newname=npref+"1";
 	  nametable.put(npref, "1");
     }
    return newname;
   }

  public String getName() {return name;}
  public void setName(String nam) {name=nam;}

  public String getModelName() {return modelName;}
  public String getModelType() {return modelType;}

  public int getCurImage() {return curImage;}
  public void setCurImage(int no) {curImage=no;}

  public boolean getGrouped() {return grouped;}
  public void setGrouped(boolean b) {grouped=b;}

  public Image getRealImage() {return realImage;}

  public int deltax=3,deltay=3;
  protected int xm,ym,oldx,oldy,pressedx,pressedy,pressedPosx,pressedPosy;
  protected boolean drag=false;
//  protected Cursor savedCursor;

  private int translatex=0,translatey=0;
  AffineTransform translate;
  public void paintComponent(Graphics g)
   {
    super.paintComponent(g);

    Graphics2D g2=(Graphics2D) g;
    Color color0=g2.getColor();
    translate= AffineTransform.getTranslateInstance(translatex,translatey);
    translate.concatenate(AffineTransform.getRotateInstance(angle));
//   System.out.println(modelType+" curImage:"+curImage);
    g2.drawImage(images[curImage],translate,this);
//    if(mode==MODE_EDIT)
     {int x1=0,y1=0;
      Position pos=null;
      for(int i=0;i<ports.length;i++)
       {
        if(ports[i]!=null && !ports[i].getConnected())
         {
          pos=ports[i].getPosition();
          x1=pos.x-deltax;
          y1=pos.y-deltay;
          g2.setColor(Color.red);
 // System.out.println(modelName+" "+ pos.x+":"+pos.y);
          g2.drawOval(x1,y1,deltax*2,deltay*2);
          g2.setColor(color0);
         }
       }
      for(int i=0;i<ports.length;i++)
      {
       if(ports[i]!=null)
        {
         pos=ports[i].getPosition();
         x1=pos.x-deltax;
         y1=pos.y-deltay;
         /*
         if(ports[i].getIsPower()) {
           g2.setColor(Color.red);
// System.out.println(modelName+" "+ pos.x+":"+pos.y);
           g2.fillOval(x1,y1,deltax*2,deltay*2);
           g2.setColor(color0);
         }
         */
         if(ports[i].getIsStop()) {
             g2.setColor(Color.blue);
  // System.out.println(modelName+" "+ pos.x+":"+pos.y);
             g2.fillOval(x1,y1,deltax*2,deltay*2);
             g2.setColor(color0);
           }

         if(ports[i].getIsDrain()) {
             g2.setColor(Color.cyan);
  // System.out.println(modelName+" "+ pos.x+":"+pos.y);
             g2.fillOval(x1,y1,deltax*2,deltay*2);
             g2.setColor(color0);
           }



        }

      }
     }
    if(grouped)
     {int w=getSize().width-1,h=getSize().height-1;
      g2.setColor(Color.lightGray);
      g2.drawLine(0,0,w,0);
      g2.drawLine(w,0,w,h);
      g2.drawLine(w,h,0,h);
      g2.drawLine(0,h,0,0);
      g2.setColor(color0);
//System.err.println("group"+modelName);
     }

   }

  private Position rotatePos(Port po, int n)
   {if(po==null) return null;
    Position pts=new Position();
    Position pos=po.getPosition();
    int x=pos.x,y=pos.y;
//System.err.println(x+":"+y);
    switch(n)
     {case 0: pts.x=x;
              pts.y=y;
              break;
      case 1: pts.x=height0-1-y  -1;
              pts.y=x;
              break;
      case 2: pts.x=width0-1-x   -1;
              pts.y=height0-1-y  -1;
              break;
      case 3: pts.x=y;
              pts.y=width0-1-x   -1;
              break;
     }
    return pts;
   }

  private void rotateElement()
   {if(ports==null) return;
    int casei=(int)(2.0*angle/Math.PI);
    switch(casei)
     {case 1: translatex=height0-1;
              translatey=0;
              for(int i=0;i<ports.length;i++)
               {if(ports[i]!=null)
                 {ports[i].setDir(Port.rotateDir(portOrg[i].getDir(),1));
                  ports[i].setPosition(rotatePos(portOrg[i],1));
                 }
               }
              setSize(height0,width0);
        break;
      case 2: translatex=width0-1;
              translatey=height0-1;
              for(int i=0;i<ports.length;i++)
               {if(ports[i]!=null)
                 {ports[i].setDir(Port.rotateDir(portOrg[i].getDir(),2));
                  ports[i].setPosition(rotatePos(portOrg[i],2));
                 }
               }
              setSize(width0,height0);
        break;
      case 3: translatex=0;
              translatey=width0-1;
              for(int i=0;i<ports.length;i++)
               {if(ports[i]!=null)
                 {ports[i].setDir(Port.rotateDir(portOrg[i].getDir(),3));
                  ports[i].setPosition(rotatePos(portOrg[i],3));
                 }
               }
              setSize(height0,width0);
        break;
      case 0: translatex=0;
              translatey=0;
              for(int i=0;i<ports.length;i++)
               {if(ports[i]!=null)
                 {ports[i].setDir(portOrg[i].getDir());
                  ports[i].setPosition(rotatePos(portOrg[i],0));
                 }
               }
              setSize(width0,height0);
        break;
     }
   }

  public void setAngle(double d)
   {angle=d;
    rotateElement();
   }

  public double getAngle() {return angle;}



  public void rotate()
   {int casei=(int)(2.0*angle/Math.PI);
    switch(casei)
     {case 0: angle=Math.PI/2.0;break;
      case 1: angle=Math.PI;break;
      case 2: angle=(Math.PI*3.0)/2.0;break;
      case 3: angle=0.0;break;
     }
    rotateElement();
   }
  private void rotateBack()
   {int casei=(int)(2.0*angle/Math.PI);
    switch(casei)
     {case 2: angle=Math.PI/2.0;break;
      case 3: angle=Math.PI;break;
      case 0: angle=(Math.PI*3.0)/2.0;break;
      case 1: angle=0.0;break;
     }
    rotateElement();
   }

  public int portInElement(Port pt)
   {for(int i=0;i<ports.length;i++)
     if(ports[i]==pt) return i;
    return -1;
   }

  public void reset() {curImage=0;};
  public abstract void check();
  public abstract Port[] nextPorts(Port pt);
  public Port[] nextDrainPorts(Port pt) {return nextPorts(pt);}
  public Port[] nextPowerPorts(Port pt) {return nextPorts(pt);}
  public Port[] nextStopPorts(Port pt) {return nextPorts(pt);}
  public void mark() {} // mark stop/sink

  public abstract String extraWrite();
  public abstract void extraRead(String str);

  public BoardElement getBoardElement()
    {return new BoardElement(this);}

  public String write()
   {
    StringBuffer sb=new StringBuffer();
    sb.append(Integer.toString(SCCAD.Data_Element)+" ");
    sb.append(modelType+" ");
    sb.append(modelName+" ");
    if(name==null || name.length()==0) sb.append("null ");
    else sb.append(name+" ");
    sb.append(angle+" ");
    sb.append(getX()+" "+getY()+" ");
    sb.append(curImage+" ");
    return sb.toString()+extraWrite();
   }

  public static tw.com.justiot.sequencecontrol.pelement.PneumaticElement read(String str, PneumaticListener pneumaticlistener, ElectricListener electriclistener)
   {
	try {
    if(Config.getBoolean("debug")) System.out.println("PneumaticElement read:"+str);
    StringTokenizer token=new StringTokenizer(str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_Element) return null;
    String mType=token.nextToken();
    String mName=token.nextToken();
    String name=token.nextToken();
    if(name.equals("null")) name="";
    double angle=Double.parseDouble(token.nextToken());
    int x=Integer.parseInt(token.nextToken());
    int y=Integer.parseInt(token.nextToken());
    int cimage=Integer.parseInt(token.nextToken());
//System.out.println("Element instanceElement");
    Object obj=Creater.instanceElement(mType,mName, pneumaticlistener, electriclistener);
  //  System.out.println(obj);
    if(obj==null) return null;
    if(!(obj instanceof tw.com.justiot.sequencecontrol.pelement.PneumaticElement)) return null;
    tw.com.justiot.sequencecontrol.pelement.PneumaticElement element=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) obj;
/*
    if(obj instanceof EValve) {
 //   	System.out.println("EValve!");
    	if(pneumaticlistener.hasElectrics()) {
    		EArrays.ElectricFaceArray.add((EValve) element);
            pneumaticlistener.getEDevicePanel().add(((EValve) element).getESymbol());
            pneumaticlistener.getEDevicePanel().repaint();
            if(pneumaticlistener.hasSequence())
             {pneumaticlistener.sequenceRefreshSystemCombo();
             }
    	}
    }
*/    
    if(element!=null)
     {if(name!=null) element.setName(name);
      element.setAngle(angle);
      element.setLocation(x,y);
      element.setCurImage(cimage);
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
  //       System.err.println("element extra"+extra);
       }
     
     }
    
    
    
    return element;
	} catch(Exception e) {
		e.printStackTrace();
		return null;
	}
   }

  public void mouseClicked(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseClicked(e,left,e.getX(),e.getY(),pop);
   }
  public void MouseClicked(MouseEvent e, boolean left, int ex, int ey, boolean pop)
   {if(left) connectPort(ex,ey);
   }
  public void connectPort(int x,int y)
   {if(ports==null) return;
    int x1=0,x2=0,y1=0,y2=0;
    Position pos=null;
    for(int i=0;i<ports.length;i++)
     {if(ports[i]!=null && !ports[i].getConnected())
       {
        pos=ports[i].getPosition();
        x1=pos.x-deltax;
        x2=pos.x+deltax;
        y1=pos.y-deltay;
        y2=pos.y+deltay;
        if(x > x1 && x < x2 && y > y1 && y < y2)
         {if(pneumaticPanel!=null)
        	 pneumaticPanel.portClicked(new ConnectEvent(this,ports[i],getX()+x,getY()+y));
         }
       }
     }
   }

  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mousePressed(MouseEvent e) {
	  if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MousePressed(e,left,e.getX(),e.getY(),pop);
   }
   public void MousePressed(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {pressedx=ex;
        pressedy=ey;
        pressedPosx=getLocation().x;
        pressedPosy=getLocation().y;
//System.err.println("pressedpos:"+pressedPosx+":"+pressedPosy);
       }
      else
       maybeShowPopup(pop,ex,ey);
    }

  public void mouseReleased(MouseEvent e) {
	  if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseReleased(e,left,e.getX(),e.getY(),pop);
   }
   public void MouseReleased(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {//if(drag && elementListener!=null)
        // elementListener.move(new MoveEvent(this,pressedPosx+ex-pressedx,pressedPosy+ey-pressedy));
//        setLocation(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
//         moveTo(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
       setLocation(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
       pneumaticPanel.repaint();
       pneumaticlistener.setModified(true);
        drag=false;
       }
      else
       maybeShowPopup(pop,ex,ey);
   }
/*
  private void moveTo(int x,int y)
   {Component com=pneumaticPanel.inComponent(x,y,x+getSize().width,y+getSize().height);
    if(com!=null && com!=this)
     {
      if(((com.wujeng.data.element.Element)com).getModelType().equals("Actuator") &&
         (this instanceof Valve) && ((Valve) this).forceType==Valve.FORCE_MECHANIC)
//         modelName.equals("LimitSwitch"))
       {Actuator act=(Actuator) com;
        act.addValve((Valve)this);
        pneumatics.setStatus(pneumatics.getString("Status.install_LS"));
       }
      else
       pneumatics.setStatus(pneumatics.getString("Status.overlay"));
//      return;
     }
    setLocation(x,y);
    pneumaticPanel.repaint();
   }
*/
  public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop) {
        popup.show(this, ex, ey);
//        popup.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  public void mouseDragged(MouseEvent e) {
	 if(mouseoff) return;
     boolean left=(e.getModifiers() & MouseEvent.META_MASK) == 0;
     boolean pop=e.isPopupTrigger();
     MouseDragged(e,left,e.getX(),e.getY(),pop);
   }

   public void MouseDragged(MouseEvent e, boolean left, int ex, int ey, boolean pop) {
      if(left)
       {//xm=ex;
        //ym=ey;
        drag=true;

        pressedPosx=getLocation().x;
        pressedPosy=getLocation().y;
        setLocation(pressedPosx+ex-pressedx,pressedPosy+ey-pressedy);
        pneumaticPanel.repaint();
        pneumaticlistener.setModified(true);



       }
    }
  public void mouseMoved(MouseEvent e) {}

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

   private class rotateCommand extends Command
    {
	public rotateCommand(Object ele)
     {super("PneumaticElement",ele,Command_rotate);
     }
    public void undo()
     {rotateBack();
     }
    public void redo()
     {rotate();
     }
   }

  private class changeNameCommand extends Command
    {String oldname;
     String newname;
	public changeNameCommand(Object ele,String on,String nn)
     {super("PneumaticElement",ele,Command_changeName);
      oldname=on;
      newname=nn;
     }
    public void undo()
     {name=oldname;
     }
    public void redo()
     {name=newname;
     }
   }

  public void actionPerformed(ActionEvent e){

         ActionPerformed((JMenuItem) e.getSource(),null,null);
    }

  public void ActionPerformed(JMenuItem mi,String op,String input)
    {
      String option=mi.getText();
     if(option.equals(Config.getString("Element.delete")))
     {//if(elementListener!=null)
      // elementListener.delete(new ElementEvent(this));
    	 pneumaticPanel.deleteElement(this);
     }
    else if(option.equals(Config.getString("popup.rotate")))
     {rotate();
      pneumaticlistener.addCommand(new rotateCommand(this));
     }
    else if(option.equals(Config.getString("popup.name")))
     {String oldname=name;
	  customDialog = new CustomDialog(pneumaticlistener.getFrame(),Config.getString("popup.name"),Config.getString("popup.modifyname"),CustomDialog.VALUE_STRING);
      customDialog.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      customDialog.setLocation(
	  screenSize.width/2 - customDialog.getSize().width/2,
	  screenSize.height/2 - customDialog.getSize().height/2);
      customDialog.setTextField(name);
      customDialog.setVisible(true);
      name=customDialog.getValidatedText();
      pneumaticlistener.addCommand(new changeNameCommand(this,oldname,name));
     }
  }

  class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());
       }
   }

  public String toString() {
		if(name!=null && name.length()>0) return name;
		if(modelType!=null && modelType.length()>0) return modelType;
		if(modelName!=null && modelName.length()>0) return modelName;
		return super.toString();
	  }
 }
