package tw.com.justiot.sequencecontrol.pelement;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;

import javax.swing.JMenuItem;

import tw.com.justiot.sequencecontrol.Config;
import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.config.ValveParameter;
import tw.com.justiot.sequencecontrol.dialog.KeyDialog;
import tw.com.justiot.sequencecontrol.eelement.ESystem;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;
import tw.com.justiot.sequencecontrol.part.Position;

public class Valve extends PneumaticElement
 {
  public static final int FORCE_FLUID=0;
  public static final int FORCE_MAN=1;
  public static final int FORCE_MECHANIC=2;
  public static final int FORCE_ELECTRIC=3;

  protected String valveType;
  protected int squareNumber;
  protected String[] connections;
  protected String curConnection;
  protected boolean memory;

  protected int forceNumber;
  protected Position[] forcePos;
  protected boolean[] forceOn;
  protected boolean exclusiveForce;
//  protected boolean toggle;
  public int forceType;

  public Port pport,rport,sport,aport,bport;
  protected Port yport,zport;
  public boolean oneWay;
  public boolean oneWayLeft;

  private int activateKey;
  private String keyString;
  private boolean firstPopup=true;

  private Valve seft;

  public static int count=0;
  protected ElectricListener electriclistener;
  public Valve(String mname,PneumaticListener pneumaticlistener, ElectricListener electriclistener)
   {super(mname,false,pneumaticlistener);
    this.electriclistener=electriclistener;
    if(PneumaticConfig.parameter.containsKey(mname))
     {ValveParameter ep=(ValveParameter) PneumaticConfig.parameter.get(mname);
      squareNumber=ep.squareNumber;
      forceNumber=ep.forceNumber;
      forceType=ep.forceType;
      exclusiveForce=ep.exclusiveForce;
      forcePos=ep.forcePos;
      connections=ep.connections;
      oneWay=ep.oneWay;
      oneWayLeft=false;
      if(ep.pport!=null)
       {this.pport=new Port(ep.pport);
        this.pport.setDir(Port.DIR_DOWN);
       }
      else this.pport=null;
      if(ep.rport!=null)
       {this.rport=new Port(ep.rport);
        this.rport.setDir(Port.DIR_DOWN);
       }
      else this.rport=null;
      if(ep.sport!=null)
       {this.sport=new Port(ep.sport);
        this.sport.setDir(Port.DIR_DOWN);
       }
      else this.sport=null;
      if(ep.aport!=null)
       {this.aport=new Port(ep.aport);
        this.aport.setDir(Port.DIR_UP);
       }
      else this.aport=null;
      if(ep.bport!=null)
       {this.bport=new Port(ep.bport);
        this.bport.setDir(Port.DIR_UP);
       }
      else this.bport=null;
      if(ep.yport!=null)
       {this.yport=new Port(ep.yport);
        this.yport.setDir(Port.DIR_RIGHT);
//        this.yport.setQCapacity(0.0f);
       }
      else this.yport=null;
      if(ep.zport!=null)
       {this.zport=new Port(ep.zport);
        this.zport.setDir(Port.DIR_LEFT);
//        this.zport.setQCapacity(0.0f);
       }
      else this.zport=null;
      if(squareNumber==(forceNumber+1)) memory=false;
      else memory=true;
      forceOn=new boolean[forceNumber];
      for(int i=0;i<forceNumber;i++)
       {forceOn[i]=false;
//        Tools.trace("forceon"+forceOn[i]+i);
       }
      if(forceType!=FORCE_FLUID)
       {yport=null;zport=null;}
      else
       {
        if(forcePos==null)
         java.lang.System.err.println("forcePos[] error!!");
        else
         {if(forcePos[0]!=null)
           {yport=new Port(forcePos[0],this);
            this.yport.setDir(Port.DIR_RIGHT);
//            yport.setFlowrate(0.0f);
//            yport.setQCapacity(0.0f);
           }
          else yport=null;

          if(forcePos.length>1 && forcePos[1]!=null)
           {zport=new Port(forcePos[1],this);
            this.zport.setDir(Port.DIR_LEFT);
//            zport.setFlowrate(0.0f);
//            zport.setQCapacity(0.0f);
           }
          else zport=null;

          seft=this;
         }
       }
      ports=new Port[]{pport,rport,sport,aport,bport,yport,zport};
      portOrg=new Port[ports.length];
      for(int i=0;i<ports.length;i++)
       {if(ports[i]!=null)
         {ports[i].setOwner(this);
          portOrg[i]=new Port(ports[i]);
         }
        else
         portOrg[i]=null;
       }
      curConnection=connections[0];
//      toggle=false;
      activateKey=-1;
     }
   }

  private Port getPort(String ch)
   {
//Tools.trace("getPort"+ch);
    if(ch.equals("p")) return pport;
    else if(ch.equals("r")) return rport;
    else if(ch.equals("s")) return sport;
    else if(ch.equals("a")) return aport;
    else if(ch.equals("b")) return bport;
    else
     {java.lang.System.err.println("error in getPort:"+ch);
      return null;
     }
   }

  public boolean getForce(int no)
    {return forceOn[no];}

  public void reset()
   {setForce(forceNumber-1,true);
    setForce(forceNumber-1,false);
    for(int i=0;i<forceNumber-1;i++)
     setForce(i,false);
   }

  public void setForce(int no,boolean onoff)
   {if((no+1)>forceNumber) return;
//    if(!forceOn[no] && onoff && forceType==FORCE_MECHANIC) pneumaticlistener.setStepFlag(true);
    if(forceOn[no]==onoff) return;
    forceOn[no]=onoff;
 //   System.out.println(memory);
    if(forceOn[no])
     {
      if(memory)
       {curImage=forceNumber+no;
        curConnection=connections[no];
       }
      else
       {curImage=1+no;
        curConnection=connections[no+1];
       }
     }
    else
     {if(!memory)
       {curConnection=connections[0];
        curImage=0;
       }
      else
       {curImage=no;

       }
     }
    repaint();
 //   System.out.println("setForce curImage:"+curImage);
  }

  public Port[] nextPorts(Port pt)
   {if(yport!=null && yport==pt)
   {//yport.setQCapacity(0.0f);
   return null;
  }
 if(zport!=null && zport==pt)
  {//zport.setQCapacity(0.0f);
   return null;
  }
 String ch=null;
 if(pport!=null && pport==pt) ch="p";
 else if(rport!=null && rport==pt) ch="r";
 else if(sport!=null && sport==pt) ch="s";
 else if(aport!=null && aport==pt) ch="a";
 else if(bport!=null && bport==pt) ch="b";
 else return null;
 int ind=curConnection.indexOf(ch);
 if(ind % 2==0) ch=curConnection.substring(ind+1,ind+2);
 else ch=curConnection.substring(ind-1,ind);
 if(ch.equals("0")) return null;
 Port[] ps= {getPort(ch)};
 return ps;
   }

  public void mark() {
	  String ch=null;
	    Port po=null;
	    for(int i=0;i<curConnection.length()/2;i++)
	     {ch=curConnection.substring(2*i,2*i+2);
	      po=getPort(ch.substring(0,1));
	      if(ch.substring(1,2).equals("0"))
	       {po.setFlowrate(0.0f);
	        po.setIsStop(true);
	       }
	     }
	    if(rport!=null) {
	        if(!rport.getConnected()) {rport.setIsDrain(true); rport.setPressure(0.0f);}
	      }
	      if(sport!=null) {
	        if(!sport.getConnected()) {sport.setIsDrain(true); sport.setPressure(0.0f);}
	      }
  }
  public void check()
   {
//System.out.println("Valve check");
    if(forceType==FORCE_FLUID)
     {if(yport!=null)
       {if(yport.getPressure()>Port.sourcePressure/2.0)
         {if(zport==null || (zport!=null && !forceOn[1])) setForce(0,true);}
        else
         setForce(0,false);
       }
      if(zport!=null)
       {if(zport.getPressure()>Port.sourcePressure/2.0)
         {if(yport==null || (yport!=null && !forceOn[0])) setForce(1,true);}
        else
         setForce(1,false);
       }
     }

    String ch=null;
    Port po=null;
    for(int i=0;i<curConnection.length()/2;i++)
     {ch=curConnection.substring(2*i,2*i+2);
      po=getPort(ch.substring(0,1));
      po.setIsDrain(false);
 //     po.setIsPower(false);
 //     po.setIsStop(false);
  // System.out.println(ch);
      if(ch.substring(1,2).equals("0"))
       {po.setFlowrate(0.0f);
 //       po.setQCapacity(0.0f);
        po.setIsStop(true);
       }
      else
       {//po.connect(getPort(ch.substring(1,2)),0.0f);
    	Port oport=getPort(ch.substring(1,2));
 //   	oport.setIsStop(false);
 //   	oport.setIsDrain(false);
 //   	oport.setIsPower(false);
 //   	po.setIsStop(false);
 //   	po.setIsDrain(false);
 //   	po.setIsPower(false);
  //  	System.out.println("valve");
        Line.connect(po, oport);
 //       System.out.println(po.getPressure()+":"+oport.getPressure());
       }
     }
   }

 private int getForcePosX(int i)
  {if(angle==0.0) return forcePos[i].x;
    if(angle==Math.PI/2.0) return getSize().width-forcePos[i].y;
    if(angle==Math.PI) return getSize().width-forcePos[i].x;
    if(angle==(Math.PI*3.0)/2.0) return forcePos[i].y;
    return 0;
  }

 private int getForcePosY(int i)
  {if(angle==0.0) return forcePos[i].y;
    if(angle==Math.PI/2.0) return forcePos[i].x;
    if(angle==Math.PI) return getSize().height-forcePos[i].y;
    if(angle==(Math.PI*3.0)/2.0) return getSize().height-forcePos[i].x;
    return 0;
  }

  private final int delta=3;
  public void MouseClicked(MouseEvent e,boolean left,int ex,int ey,boolean pop) {
    super.MouseClicked(e,left,ex,ey,pop);
    if(left)
     {int x=ex,y=ey;
       int fposx=0,fposy=0;
      for(int i=0;i<forcePos.length;i++)
       {if(forcePos[i]!=null && (forceType==FORCE_MAN || forceType==FORCE_ELECTRIC) && memory)
         {fposx=getForcePosX(i);fposy=getForcePosY(i);
           if(x>fposx-delta && x<fposx+delta && y>fposy-delta && y<fposy+delta)
           {setForce(i,true);
//System.err.println("on:"+i);
            break;
           }
//System.err.println(x+":"+y);
//System.err.println(forcePos[i].x+":"+forcePos[i].y);
         }
       }
     }
  }
  public void MousePressed(MouseEvent e,boolean left,int ex,int ey,boolean pop) {
      super.MousePressed(e,left,ex,ey,pop);
      if(left)
       {int x=ex,y=ey;
         int fposx=0,fposy=0;
        for(int i=0;i<forcePos.length;i++)
         {//if(forcePos[i]!=null && (forceType==FORCE_MAN || forceType==FORCE_ELECTRIC) && !memory)
          if(forcePos[i]!=null && (forceType==FORCE_MAN || forceType==FORCE_ELECTRIC))
           {fposx=getForcePosX(i);fposy=getForcePosY(i);
             if(x>fposx-delta && x<fposx+delta && y>fposy-delta && y<fposy+delta)
             {setForce(i,true);
              break;
             }
           }
         }
     }
  }
  public void MouseReleased(MouseEvent e,boolean left,int ex,int ey,boolean pop) {
      super.MouseReleased(e,left,ex,ey,pop);
      if(left)
       {int x=ex,y=ey;
         int fposx=0,fposy=0;
        for(int i=0;i<forcePos.length;i++)
         {//if(forcePos[i]!=null && forceType==FORCE_MAN || forceType==FORCE_ELECTRIC) && !memory)
          if(forcePos[i]!=null && (forceType==FORCE_MAN || forceType==FORCE_ELECTRIC))
           {fposx=getForcePosX(i);fposy=getForcePosY(i);
             if(x>fposx-delta && x<fposx+delta && y>fposy-delta && y<fposy+delta)
             {setForce(i,false);
              break;
             }
           }
         }
       }
   }
  
  /*
   public void maybeShowPopup(boolean pop,int ex,int ey) {
      if(pop) {
        if(firstPopup && forceType==FORCE_MAN)
         {
          popup.addSeparator();
          m = new JMenuItem(Config.getString("Valve.ActivateKey"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);

          }
        if(firstPopup && memory)
         {popup.addSeparator();
          m = new JMenuItem(Config.getString("Valve.changestate"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
         }
        if(firstPopup && oneWay && forceType==FORCE_MECHANIC)
         {popup.addSeparator();
          m = new JMenuItem(Config.getString("Valve.changedirection"));
          m.addActionListener(this);
          m.addMouseListener(new menuItemMouseAdapter());
          popup.add(m);
         }
    //    if(firstPopup && forceType==FORCE_MECHANIC)
    //    {popup.addSeparator();
    //     m = new JMenuItem(Config.getString("Valve.setLS"));
    //     m.addActionListener(this);
    //     m.addMouseListener(new menuItemMouseAdapter());
    //     popup.add(m);
    //    }
        firstPopup=false;
        popup.show(this, ex, ey);
      }
    }
*/
    public void ActionPerformed(JMenuItem mi,String op,String input)
      {String option=mi.getText();
 //System.err.println(option);
      if(option.equals(Config.getString("Element.delete")) && forceType==FORCE_MECHANIC)
       {
 //System.err.println("delete mechanic");
        Component[] comps=pneumaticPanel.getComponents();
        Actuator act=null;
        ESystem sys=null;
        for(int i=0;i<comps.length;i++)
         {if(comps[i] instanceof Actuator)
            {act=(Actuator) comps[i];
              if(act.hasValve(this)) act.deleteValve(this);
            }
       //    if(comps[i] instanceof ESystem)
        //    {sys=(ESystem) comps[i];
        //      if(sys.hasValve(this)) sys.deleteValve(this);
        //    }
         }
       }
 //System.err.println("option:"+option);
       super.ActionPerformed(mi,op,input);
 //System.err.println("option:"+option);
      if(option.equals(Config.getString("Valve.ActivateKey")))
      {KeyDialog keyDialog = new KeyDialog(pneumaticlistener.getFrame());
          keyDialog.pack();
          Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
          keyDialog.setLocation(
 		screenSize.width/2 - keyDialog.getSize().width/2,
 		screenSize.height/2 - keyDialog.getSize().height/2);
          keyDialog.setVisible(true);
          activateKey=keyDialog.getKey();
          keyString=keyDialog.getKeyString();
      }
     else if(option.equals(Config.getString("Valve.changestate")))
      {setForce((curImage+1)%forceNumber,true);
      }
     else if(option.equals(Config.getString("Valve.changedirection")))
      {oneWayLeft=!oneWayLeft;
      }
  //   else if(option.equals(Config.getString("Valve.setLS")))
  //    {
   //   }
    }

   class menuItemMouseAdapter extends MouseAdapter
   {
     public void mouseEntered(MouseEvent e)
       {int ind=popup.getComponentIndex((Component)e.getSource());

       }
   }

   public int getForceType() {return forceType;}
   public int getActivateKey() {return activateKey;}
   public String getKeyString() {return keyString;}
   public void setCurImage(int no)
    {curImage=no;
     if(memory)
      {int fn=curImage%forceNumber;
       setForce(fn,true);
       setForce(fn,false);
      }
    }

   public String extraWrite()
   {String str="false";
     if(oneWayLeft) str="true";
     return str+" "+Integer.toString(activateKey)+" "+keyString;
   }

  public void extraRead(String str)
   {StringTokenizer token=new StringTokenizer(str);
//System.out.println("valve extraread:"+str);
     if(token.hasMoreTokens())
      {
        oneWayLeft=false;
        if(token.nextToken().toLowerCase().equals("true")) oneWayLeft=true;
        activateKey=Integer.parseInt(token.nextToken());
        keyString=token.nextToken();
      }
   }
 }
