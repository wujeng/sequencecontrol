package tw.com.justiot.sequencecontrol.pelement;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EValve extends Valve implements CDOutputListener,ElectricFace
 {public CDOutput cdo;
  public ESymbol esymbol;
  
  private String name; 
  public void setName(String na)
   {if(cdo!=null) cdo.setName(na);
    super.setName(na);
   }
  public String getName()
   {if(cdo!=null) return cdo.getName(); 
    else return super.getName(); 
   }
  
    public Image EFsol1=null;
    public Image EFsol1on=null;
    public Image EFsol2=null;
    public Image EFsol2on=null;
    public Rect actionRect1=null;
    public Rect actionRect2=null;
    public Rect inputRect1=null;
    public Rect inputRect2=null;
    public Rect LSRect=null;
    public Point SP1=new Point(),SP2=new Point();     // positions of electric_symbol in electric panel
    private ArrayList valveArray;
    private ArrayList valvePos;
//  
//ElectricFace   
  public Image getEFsol1() {return EFsol1;}
  public Image getEFsol1on() {return EFsol1on;}
  public Image getEFsol2()  {return EFsol2;}
  public Image getEFsol2on() {return EFsol2on;}
  public Rect getActionRect1() {return actionRect1;}
  public Rect getActionRect2() {return actionRect2;}
  public Rect getLSRect() {return LSRect;}
  public Point getSP1() {return SP1;}
  public Point getSP2() {return SP2;}
  
  public Actuator getActuator()
  {
    Port inport=null,tempport=null;
    PneumaticElement obj1=null,obj2=null;
    Port[] pts=null;
    inport=aport;
    while(true)
      {if(inport==null) {obj1=null;break;}
        tempport=pneumaticPanel.anotherPort(inport);
        if(tempport==null) {obj1=null;break;}
        obj1=tempport.getOwner();
        if(obj1==null) {obj1=null;break;}
        if(obj1 instanceof Actuator) break;
        pts=obj1.nextPorts(tempport);
        if(pts==null) {obj1=null;break;}
        if(pts.length==0) {obj1=null;break;}
        if(pts.length>1) {obj1=null;break;}
        inport=pts[0];
      }
    inport=bport;
    while(true)
      {if(inport==null) {obj2=null;break;}
        tempport=pneumaticPanel.anotherPort(inport);
        if(tempport==null) {obj2=null;break;}
        obj2=tempport.getOwner();
        if(obj2==null) {obj2=null;break;}
        if(obj2 instanceof Actuator) break;
        pts=obj2.nextPorts(tempport);
        if(pts==null) {obj2=null;break;}
        if(pts.length==0) {obj2=null;break;}
        if(pts.length>1) {obj2=null;break;}
        inport=pts[0];
      }
    if(obj1!=null && obj2!=null && obj1==obj2)
     {
//System.err.println(((Actuator) obj1).getName());
       return (Actuator) obj1;
//        return "";
     }
    else
     return null;
  }

  public String getActuatorName()
  {Actuator act=getActuator();
   if(act!=null) return act.getName();
   else return null;
  }

// public String getName() {return Name;}
 
 public ArrayList getValveArray()
   {Port p1=pneumaticPanel.anotherPort(aport);
    Port p2=pneumaticPanel.anotherPort(bport);
    if(p1!=null && p2!=null && p1.getOwner()==p2.getOwner())
     {if(p1.getOwner() instanceof Actuator)
        {valveArray=((Actuator) (p1.getOwner())).getValveArray();
          valvePos=((Actuator) (p1.getOwner())).getValvePos();
          return valveArray;
        }
     }
    return null;
  }
 public ArrayList getValvePos()
   {Port p1=pneumaticPanel.anotherPort(aport);
    Port p2=pneumaticPanel.anotherPort(bport);
    if(p1!=null && p2!=null && p1.getOwner()==p2.getOwner())
     {if(p1.getOwner() instanceof Actuator)
        {valveArray=((Actuator) (p1.getOwner())).getValveArray();
          valvePos=((Actuator) (p1.getOwner())).getValvePos();
          return valvePos;
        }
     }
    return null;
  }
 public EDevice getFirstLimitswitch()
   {ArrayList al=getValveArray();
     if(al==null) return null;
     EDevice ed=null,edret=null;
     int pos=100;
     for(int i=0;i<al.size();i++)
      {if(!(al.get(i) instanceof EDevice)) continue;
        ed=(EDevice) al.get(i);
        if(((Integer) valvePos.get(i)).intValue() < pos)
         {pos=((Integer) valvePos.get(i)).intValue();
           edret=ed;
         }
      }
     return edret;
   }
        
 public EDevice getLastLimitswitch()
   {ArrayList al=getValveArray();
     if(al==null) return null;
     EDevice ed=null,edret=null;
     int pos=-1;
     for(int i=0;i<al.size();i++)
      {if(!(al.get(i) instanceof EDevice)) continue;
        ed=(EDevice) al.get(i);
        if(((Integer) valvePos.get(i)).intValue() > pos)
         {pos=((Integer) valvePos.get(i)).intValue();
           edret=ed;
         }
      }
     return edret;
   }
 public int getForceNumber() {return forceNumber;}
 public boolean getMemory() {return memory;}
  public boolean withLS() {return true;}
  public ESymbol getESymbol() {return esymbol;}
  public CDOutput getCDOutput() {return cdo;}
  public void delete()
  {//elementListener.delete(new ElementEvent(this));
   pneumaticPanel.deleteElement(this);
  }
  public void reset() {cdo.reset();}
//  public String write();
//  
//  
//      
   
   public void solFStatusChanged(CDOutput cdo)
    {if(forceNumber==2) setForce(1,cdo.getSolFStatus());
     if(forceNumber==1) 
      {if(cdo.getSolFStatus()) setForce(0,true);
       else setForce(0,false);
      }  
	 }
   
   public void solBStatusChanged(CDOutput cdo)
    {if(forceNumber==1) return;
     setForce(0,cdo.getSolBStatus());
	}
   public void nameChanged(CDOutput cdo)
   {name=cdo.getName();
    if(pneumaticlistener!=null) pneumaticlistener.repaint();
   }
//   public boolean withLS() {return true;}
   
  public EValve(String mname, PneumaticListener pneumaticlistener, ElectricListener electriclistener)
   {super(mname, pneumaticlistener, electriclistener);
    if(Config.getBoolean("debug")) System.out.println("Creater EValve "+mname);
//  System.out.println(electriclistener);  
    if(PneumaticConfig.parameter.containsKey(mname))
     {EValveParameter ep=(EValveParameter) PneumaticConfig.parameter.get(mname);
   
       EFsol1=ep.EFsol1;
       EFsol1on=ep.EFsol1on;
       EFsol2=ep.EFsol2;
       EFsol2on=ep.EFsol2on;
       actionRect1=ep.actionRect1;
       actionRect2=ep.actionRect2;
       inputRect1=ep.inputRect1;
       inputRect2=ep.inputRect2;
       LSRect=null;
//       SP1=new Point();
//       if(twoWay) SP2=new Point();
       count++;
       name="E"+Integer.toString(count);
       forceType=Valve.FORCE_ELECTRIC;
/*       
       int systemType=0; 
       if(images.length==2) systemType=CDOutput.TYPE_ONOFF;
       else if(EFsol2==null)
        {if(!withLS()) systemType=CDOutput.TYPE_LOOP;
          else systemType=CDOutput.TYPE_ONEWAY;
        }
       else systemType=CDOutput.TYPE_TWOWAY;
*/       
       int systemType=CDOutput.TYPE_ONOFF; 
       boolean tway=false;
       if(EFsol2!=null) tway=true;
      
       cdo=new CDOutput(ep.modelType,ep.modelName,systemType,tway);
       
       cdo.ActuatorTwoWay=true;
       cdo.addListener(this);     
       esymbol=new ESymbol(cdo, electriclistener);
       
       setName(defaultName());
     }
   }

  public EValve(CDOutput cdo, PneumaticListener pneumaticlistener, ElectricListener electriclistener)
  {super(cdo.modelName, pneumaticlistener, electriclistener);
   if(PneumaticConfig.parameter.containsKey(cdo.modelName))
    {EValveParameter ep=(EValveParameter) PneumaticConfig.parameter.get(cdo.modelName);
//System.out.println("EValveParameter");     
      EFsol1=ep.EFsol1;
      EFsol1on=ep.EFsol1on;
      EFsol2=ep.EFsol2;
      EFsol2on=ep.EFsol2on;
      actionRect1=ep.actionRect1;
      actionRect2=ep.actionRect2;
      inputRect1=ep.inputRect1;
      inputRect2=ep.inputRect2;
      LSRect=null;
//      SP1=new Point();
//      if(twoWay) SP2=new Point();
      count++;
      name="E"+Integer.toString(count);
      forceType=Valve.FORCE_ELECTRIC;
/*       
      int systemType=0; 
      if(images.length==2) systemType=CDOutput.TYPE_ONOFF;
      else if(EFsol2==null)
       {if(!withLS()) systemType=CDOutput.TYPE_LOOP;
         else systemType=CDOutput.TYPE_ONEWAY;
       }
      else systemType=CDOutput.TYPE_TWOWAY;
*/       
      int systemType=CDOutput.TYPE_ONOFF; 
      boolean tway=false;
      if(EFsol2!=null) tway=true;
      
      this.cdo=cdo;
      this.cdo.ActuatorTwoWay=true;
      this.cdo.addListener(this);     
      esymbol=new ESymbol(this.cdo, electriclistener);
      
      setName(defaultName());
    }
  }
  
  public void MousePressed(MouseEvent e,boolean left,int ex,int ey,boolean pop) {
     super.MousePressed(e,left,ex,ey,pop);
      if(left)
       {int x=ex,y=ey;
        if(actionRect1!=null && ex>actionRect1.left && ex<actionRect1.right && ey>actionRect1.top && ey<actionRect1.bottom)
           cdo.setSolFStatus(true);
        if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
           cdo.setSolBStatus(true);
     }
  }
  public void MouseReleased(MouseEvent e,boolean left,int ex,int ey,boolean pop) {
      super.MouseReleased(e,left,ex,ey,pop);
      if(left)
       {int x=ex,y=ey;
         if(actionRect1!=null && ex>actionRect1.left && ex<actionRect1.right && ey>actionRect1.top && ey<actionRect1.bottom)
          {cdo.setSolFStatus(false);
          }
        if(actionRect2!=null && ex>actionRect2.left && ex<actionRect2.right && ey>actionRect2.top && ey<actionRect2.bottom)
          {cdo.setSolBStatus(false);
          }
     }
   }

  public void ActionPerformed(JMenuItem mi,String op,String input)
     {String option=mi.getText();  
//System.err.println(option);
//     if(option.equals("delete") && forceType==FORCE_ELECTRIC)
     if(option.equals(Config.getString("Element.delete")))
      {//elementListener.delete(new ElementEvent(this));
    	 pneumaticPanel.deleteElement(this); 
    	 
      }
//System.err.println("option:"+option);
      super.ActionPerformed(mi,op,input);
   } 

  public String extraWrite()
   {String str="";
     if(cdo.FPLCAddress==null) str=str+"null ";
     else str=str+cdo.FPLCAddress+" ";
     if(cdo.BPLCAddress==null) str=str+"null ";
     else str=str+cdo.BPLCAddress+" ";
     return str+cdo.NAPKey1+" "+cdo.NAPno1+" "+cdo.NAPKey2+" "+cdo.NAPno2;
   }

  public void extraRead(String str)
   {// electriclistener.getEArrays().tempElectricFaceArray.add(this);
     StringTokenizer token=new StringTokenizer(str);
     cdo.FPLCAddress=token.nextToken();
     if(cdo.FPLCAddress.toLowerCase().indexOf("null")>=0) cdo.FPLCAddress=null;
     cdo.BPLCAddress=token.nextToken();
     if(cdo.BPLCAddress.toLowerCase().indexOf("null")>=0) cdo.BPLCAddress=null;
     cdo.NAPKey1=token.nextToken();
     cdo.NAPno1=Integer.parseInt(token.nextToken());
     cdo.NAPKey2=token.nextToken();
     cdo.NAPno2=Integer.parseInt(token.nextToken());
     if(cdo.NAPKey1.toLowerCase().indexOf("null")>=0) cdo.NAPKey1=null;
     if(cdo.NAPKey2.toLowerCase().indexOf("null")>=0) cdo.NAPKey2=null;
   }
/*
  public void setForce(int no,boolean onoff)
   {if((no+1)>forceNumber) return;
    if(toggle)
     {if(onoff)
       {if(exclusiveForce)
         {
          for(int j=0;j<forceNumber;j++)
           {
            if(j==no) forceOn[j]=!forceOn[j];
            else forceOn[j]=false;
           }
         }
        else
         forceOn[no]=!forceOn[no];
       }
     }
    else
     {
      if(forceOn[no]==onoff) return;
      forceOn[no]=onoff;
     }
//Tools.trace("setForce"+forceOn[no]);
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
//      System.err.println(curConnection);
//      check();
     }
    else
     {if(!memory) 
       {curConnection=connections[0];
        curImage=0;
//        check();
       }
//      else
//       curImage=no;
     }
//   System.err.println(curConnection);
    pneumaticPanel.repaint();
  }
*/

 } 
