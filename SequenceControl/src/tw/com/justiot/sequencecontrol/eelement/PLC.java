 package tw.com.justiot.sequencecontrol.eelement;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.eelement.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import javax.swing.JOptionPane;

public class PLC
 {
   public static final int TimerPeriod=200;

   public static final int C_ORG=1;
   public static final int C_ORG_NOT=2;
   public static final int C_LOAD=3;
   public static final int C_LOAD_NOT=4;
   public static final int C_AND=5;
   public static final int C_AND_NOT=6;
   public static final int C_OR=7;
   public static final int C_OR_NOT=8;
   public static final int C_OR_LD=9;
   public static final int C_AND_LD=10;
   public static final int C_OUT=11;
   public static final int C_TIM=12;
   public static final int C_CNT=13;
   public static final int C_RST=14;
   public static final int C_END=15;
   public static final int C_None=16;
   public static final int C_TEMP=17;
   public static final int C_TEMPLOAD=18;

  public boolean running;
  public boolean codeReady;

  public String modelName,modelType,toolTip;
  public Dimension realImageDim;
  public Image realImage;
  public String[] commands;
  public int lineLimit;

  public String[] prefix;
  public int[] base;
  public ArrayList[]  rangeArray;
  public String timerForm;
  public String counterForm;
  public boolean tcsep;

  public ArrayList codeArray;
  public ArrayList codeArrayProgram;
  public ArrayList Program;
  protected String name;

  public JComboBox createPLCComboBox()
    {JComboBox cb=new JComboBox();
     Object obj=null;
     PLCParameter ep=null;
     for( Object key : PneumaticConfig.parameter.keySet() ){
    	  obj = PneumaticConfig.parameter.get(key);
          if(obj instanceof PLCParameter)
           {ep=(PLCParameter) obj;
             cb.addItem(ep.modelName);
           }
    	}
     cb.setSelectedItem(modelName);
     return cb; 
 /*   
      Enumeration e = PneumaticConfig.parameter.elements();
      Object obj=null;
      PLCParameter ep=null;
      while (e.hasMoreElements())
       {obj = e.nextElement();
         if(obj instanceof PLCParameter)
          {ep=(PLCParameter) obj;
            cb.addItem(ep.modelName);
          }
       }
      cb.setSelectedItem(modelName);
      return cb;
      */
    }

  private ElectricListener electriclistener;
  public PLC(String mname, ElectricListener electriclistener)
   {this.electriclistener=electriclistener;
	if(PneumaticConfig.parameter.containsKey(mname))
     {
      PLCParameter ep=(PLCParameter) PneumaticConfig.parameter.get(mname);
      modelType=ep.modelType;
      modelName=ep.modelName;
      toolTip=ep.toolTip;
      realImage=ep.realImage;
      realImageDim=ep.realImageDim;
      commands=ep.commands;
      lineLimit=ep.lineLimit;
      prefix=ep.prefix;
      base=ep.base;
      rangeArray=ep.rangeArray;
      timerForm=ep.timerForm;
      counterForm=ep.counterForm;
      tcsep=true;
      if(((Range) rangeArray[3].get(0)).min==((Range) rangeArray[4].get(0)).min &&
        ((Range) rangeArray[3].get(0)).max==((Range) rangeArray[4].get(0)).max) tcsep=false;
      codeArray=new ArrayList();
      codeArrayProgram=new ArrayList();
      Program=new ArrayList();
      codeReady=false;
//      start();
    }
  }

  public void changeModel(String mname)
   { if(PneumaticConfig.parameter.containsKey(mname))
     {PLCParameter ep=(PLCParameter) PneumaticConfig.parameter.get(mname);
      modelType=ep.modelType;
      modelName=ep.modelName;
      toolTip=ep.toolTip;
      realImage=ep.realImage;
      realImageDim=ep.realImageDim;
      commands=ep.commands;
      lineLimit=ep.lineLimit;
      prefix=ep.prefix;
      base=ep.base;
      rangeArray=ep.rangeArray;
      timerForm=ep.timerForm;
      counterForm=ep.counterForm;
      tcsep=true;
//      codeReady=false;
      if(((Range) rangeArray[3].get(0)).min==((Range) rangeArray[4].get(0)).min &&
        ((Range) rangeArray[3].get(0)).max==((Range) rangeArray[4].get(0)).max) tcsep=false;
//      codeArray.clear();
      codeArrayProgram.clear();
      Program.clear();
    }
   }

  private boolean Acc,CommandEnd;
  private boolean[] Stack=new boolean[50];
  private boolean[] TempReg=new boolean[300];
  private int StackPointer;

  
  private void ReadInput()
   {
	String errstr="";
//    if(electriclistener.getOpMode()==SCCAD.OP_CONTROL &&
//    		electriclistener.getControlMode()==SCCAD.CONTROL_SERVER)
//     errstr=Modules.read();

//    if(electriclistener.getOpMode()==WebLadderCAD.OP_CONTROL &&
//    		electriclistener.getControlMode()==WebLadderCAD.CONTROL_SERVER)
//      plcListener.readModuleInput();
     EDevice ed;ElectricFace sys;Actuator act;ESystem esystem;
//     ModuleBase mb=null;
     for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
      {
        ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
//System.err.println(ed.getName());
        switch(ed.ced.actionType)
         {case CEDevice.TYPE_MANUAL_AUTO:
           case CEDevice.TYPE_MANUAL_TOGGLE:
           case CEDevice.TYPE_MECHANIC:
              if(electriclistener.getOpMode()!=SCCAD.OP_CONTROL)
               {ed.ced.statusInput=ed.ced.status;
                if(ed.ced.NAPKey!=null && ed.ced.NAPKey.length()>0)
                 {//mb=(ModuleBase) Modules.modules.get(ed.ced.NAPKey);
                 // mb.inStatus[ed.ced.NAPno]=ed.ced.statusInput;
                 }
               }
              else
               {
    //        	Modules.read();

//System.err.println(ed.NAPKey+":"+ed.NAPno);
                 if(ed.ced.NAPKey!=null && ed.ced.NAPKey.length()>0)
                  {//mb=(ModuleBase) Modules.modules.get(ed.ced.NAPKey);
//                    ed.statusInput=!m40.getInputStatus(ed.NAPno) || ed.status;
    //                if(ed.remoteTrigger) {ed.ced.statusInput=true; ed.remoteTrigger=false;}
     //               else ed.ced.statusInput=!mb.inStatus[ed.ced.NAPno];
//System.err.println(ed.getName()+":"+ed.statusInput);
//                    ed.status=ed.statusInput;
                    ed.ced.setStatus(ed.ced.statusInput);
                    if(ed.ced.status && ed.ced.actionType==CEDevice.TYPE_MECHANIC)
                     {sys=electriclistener.getEArrays().getElectricFace(ed);
                       if(sys instanceof EValve)
                        {act=((EValve) sys).getActuator();
                          act.setCurImage(act.getLSPosition(ed));
                          act.repaint();
                        }

                     }
                  }
               }
           break;
         }
      }
//     if(electriclistener.getOpMode()==SCCAD.OP_CONTROL &&
//    		 electriclistener.getControlMode()==SCCAD.CONTROL_SERVER)
//      electriclistener.getElectricPanel().setActuatorPosAccording2LS();
   }

  private void WriteOutput()
   {
	 ElectricFace ef;
  //   ModuleBase mb=null;
     for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
      {ef=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
   //    if(electriclistener.getOpMode()!=SCCAD.OP_CONTROL ||
   //      (electriclistener.getOpMode()==SCCAD.OP_CONTROL && electriclistener.getControlMode()==SCCAD.CONTROL_CLIENT))
        {
 //       	System.out.println(ef.getCDOutput().getOutputFStatus() + " "+ ef.getCDOutput().getSolFStatus());
    	   ef.getCDOutput().setSolFStatus(ef.getCDOutput().getOutputFStatus());
           ef.getCDOutput().setSolBStatus(ef.getCDOutput().getOutputBStatus());
  //        System.out.println(ef.getCDOutput().getOutputFStatus() + " "+ ef.getCDOutput().getSolFStatus());
   //        System.out.println();

           if(ef.getCDOutput().NAPKey1!=null && ef.getCDOutput().NAPKey1.length()>0)
           {//mb=(ModuleBase) Modules.modules.get(ef.getCDOutput().NAPKey1);
            //mb.outStatus[ef.getCDOutput().NAPno1]=ef.getCDOutput().getOutputFStatus();
           }
          if(ef.getCDOutput().NAPKey2!=null && ef.getCDOutput().NAPKey2.length()>0)
           {//mb=(ModuleBase) Modules.modules.get(ef.getCDOutput().NAPKey2);
            //mb.outStatus[ef.getCDOutput().NAPno2]=ef.getCDOutput().getOutputBStatus();
           }


        }
       /*
       else if(electriclistener.getControlMode()==SCCAD.CONTROL_SERVER)
        {

        	if(ef.getCDOutput().NAPKey1!=null && ef.getCDOutput().NAPKey1.length()>0)
            {mb=(ModuleBase) Modules.modules.get(ef.getCDOutput().NAPKey1);
             if(mb instanceof DIOModule)
              {DIOModule dm=(DIOModule) mb;
               try

                {dm.setOutputStatus(ef.getCDOutput().NAPno1,ef.getCDOutput().getOutputFStatus());
                }
               catch(Exception e)
                {
            	 electriclistener.MessageBox("error:","WriteOutput error!");
                }
               ef.getCDOutput().setSolFStatus(ef.getCDOutput().getOutputFStatus());
              }
            }
           if(ef.getCDOutput().NAPKey2!=null && ef.getCDOutput().NAPKey2.length()>0)
            {mb=(ModuleBase) Modules.modules.get(ef.getCDOutput().NAPKey2);
             if(mb instanceof DIOModule)
              {DIOModule dm=(DIOModule) mb;
               try

                {dm.setOutputStatus(ef.getCDOutput().NAPno2,ef.getCDOutput().getOutputFStatus());
                }
               catch(Exception e)
                {
          	     electriclistener.MessageBox("error:","WriteOutput error!");
                }
               ef.getCDOutput().setSolBStatus(ef.getCDOutput().getOutputBStatus());
              }
            }
         }
       */
      }
//     String errstr="";
//     if(electriclistener.getOpMode()==SCCAD.OP_CONTROL &&
//    		electriclistener.getControlMode()==SCCAD.CONTROL_SERVER)
//      errstr=Modules.drive();

 //    plcListener.broadcastModules();
  //   electriclistener.repaint();
   }


  private boolean ReadStatus(int n)
   {PLCCode com=(PLCCode) codeArray.get(n);
     if(com.state<0)
      return TempReg[-com.state];
     else
      {switch(com.state)
        {case LadderCell.G_NO:
           if(com.ced.actionType==CEDevice.TYPE_MANUAL_AUTO  ||
              com.ced.actionType==CEDevice.TYPE_MANUAL_TOGGLE ||
              com.ced.actionType==CEDevice.TYPE_MECHANIC)
                             return com.ced.statusInput;
           else
             return com.ced.status;
         case LadderCell.G_NC:
            MessageBox(Config.getString("ReadStatusError"),Config.getString("PLC.error"));
            break;
         case LadderCell.G_ESOL1: return com.ced.sol1Status;
         case LadderCell.G_ESOL2: return com.ced.sol2Status;
         case LadderCell.G_SSOL1: return com.cdo.getOutputFStatus();
         case LadderCell.G_SSOL2: return com.cdo.getOutputBStatus();
         default:
            MessageBox(Config.getString("ReadStatusError"),Config.getString("PLC.error"));
            break;
        }
     }
    return false;
   }

  private void WriteStatus(int n)
   {PLCCode com=(PLCCode) codeArray.get(n);
   
 //  if(com.cdo!=null) System.out.println(com.cdo.name+" "+Acc);
     if(com.state<0)
      TempReg[-com.state]=Acc;
     else
      {switch(com.state)
         {case LadderCell.G_NO:
           case LadderCell.G_NC:
              MessageBox(Config.getString("WriteStatusError"),Config.getString("PLC.error"));
              break;
//           case LadderCell.G_ESOL1: if(com.ed.sol1Status!=Acc) com.ed.sol1Status=Acc; break;
//           case LadderCell.G_ESOL2: if(com.ed.sol2Status!=Acc) com.ed.sol2Status=Acc; break;
           case LadderCell.G_ESOL1: if(com.ced.sol1Status!=Acc) com.ced.setSol1Status(Acc); break;
           case LadderCell.G_ESOL2: if(com.ced.sol2Status!=Acc) com.ced.setSol2Status(Acc); break;          
           case LadderCell.G_SSOL1: com.cdo.setOutputFStatus(Acc); break;
           case LadderCell.G_SSOL2: com.cdo.setOutputBStatus(Acc); break;
           default:
              MessageBox(Config.getString("WriteStatusError"),Config.getString("PLC.error"));
              break;
         }
       }
 //    electriclistener.repaint();
   }

  private void Push()
   {                   //���֥[�����Ȧs�J���|
    StackPointer++;
//System.err.println("push:"+StackPointer);
    Stack[StackPointer]=Acc;
   }
  private void Pop()
   {                   //�����|���Ȧs�J�֥[��
    Acc=Stack[StackPointer];
    StackPointer--;
//System.err.println("pop:"+StackPointer);
   }

 public void printCode()
  {
//	if(!WebLadderCAD.debug) return;
//if(WebLadderCAD.debug) System.out.println("PLC printCode()");
	PLCCode com;
	for(int n=0;n<codeArray.size();n++)
    {com=(PLCCode) codeArray.get(n);
     String cedname="null";
     String cdoname="null";
     if(com.ced!=null) cedname=com.ced.getName();
     if(com.cdo!=null) cdoname=com.cdo.getName();
//if(WebLadderCAD.debug)  System.out.println(com.code+" "+com.state+" "+cedname+" "+cdoname);
    }
  }


  private void Scan()
   {
     boolean temp,outflag;
     CommandEnd=false;          
     StackPointer=-1;
     outflag=true;

     ReadInput();
//System.err.println("readinput");
     PLCCode com;
//System.err.println("size:"+codeArray.size());
     for(int n=0;n<codeArray.size();n++)
      {com=(PLCCode) codeArray.get(n);
//System.err.println(n+":"+com.code);
        switch(com.code)
         {case C_LOAD:
              if(outflag==true) outflag=false;
              else Push();
              Acc=ReadStatus(n);
              break;
           case C_LOAD_NOT:
              if(outflag==true) outflag=false;
              else Push();
              Acc=!ReadStatus(n);
              break;
//      case C_ORG:      Acc=ReadStatus(n); break;
//      case C_ORG_NOT:  Acc=!ReadStatus(n); break;
//      case C_LOAD:     Push();Acc=ReadStatus(n);break;
//      case C_LOAD_NOT: Push();Acc=!ReadStatus(n);break;
           case C_AND:      Acc=Acc && ReadStatus(n);break;
           case C_AND_NOT:  Acc=Acc && !ReadStatus(n);break;
           case C_OR:       Acc=Acc || ReadStatus(n);break;
           case C_OR_NOT:   Acc=Acc || !ReadStatus(n);break;
           case C_OR_LD:    temp=Acc;Pop();Acc=temp || Acc;break;
           case C_AND_LD:   temp=Acc;Pop();Acc=temp && Acc;break;
           case C_OUT:
           case C_TIM:
           case C_CNT:
           case C_RST:      outflag=true; WriteStatus(n);break;
           case C_END:      CommandEnd=true;break;
         }
        if(CommandEnd) break;
       }
//System.err.println("writeoutput");
       WriteOutput();
       
       electriclistener.getElectricPanel().repaint();
    }

  public void startTimer(java.util.Timer timer)
   {timer.scheduleAtFixedRate(new ScanThread(), 0, TimerPeriod);
   }


  public void timeStep()
  {
   try
    {
     if(electriclistener.getSimulationMode()==SCCAD.SIMULATION_PAUSE) return;
     if(electriclistener.getSimulationMode()==SCCAD.SIMULATION_NONE) return;
//System.err.println("plc scan");
     if(codeReady) Scan();
    }
   catch (Exception e) {}
 }

  class ScanThread extends TimerTask
   {
    public void run()
     {
      try
       {
        if(electriclistener.getSimulationMode()==SCCAD.SIMULATION_PAUSE) return;
        if(electriclistener.getSimulationMode()==SCCAD.SIMULATION_NONE) return;
//System.err.println("plc scan");
        if(codeReady) Scan();
       }
      catch (Exception e) {}
    }
   }

  public String getModelName() {return modelName;}
  public String getModelType() {return modelType;}
  public Image getRealImage() {return realImage;}

  public void AddCode(int code,CEDevice ced,CDOutput cdo,int state)
   {if(codeArray.size()>=lineLimit)
      {electriclistener.setStatus(Config.getString("PLC.linelimit"));
        MessageBox(Config.getString("PLC.linelimit"),Config.getString("PLC.error"));
        return;
      }
     codeArray.add(new PLCCode(code,ced,cdo,state));
   }

 public void ClearCode()
  {codeArray.clear();}

  private int NextNo(int n,ArrayList rangeList)
   {
     boolean ok=false;
     n++;
     int i;
     Range range=null;
     while(!ok)
      {for(i=0;i<rangeList.size();i++)
         {range=(Range) rangeList.get(i);
           if(n>=range.min && n<=range.max) return n;
         }
        n++;
      }
     MessageBox(Config.getString("PLC.numberlimit"),Config.getString("PLC.error"));
     return n;
   }

  private int NextTempNo(int n,ArrayList rangeList)
   {
    Range range=null;
    boolean ok=false;
    n--;
    while(!ok)
     {for(int i=rangeList.size()-1;i>=0;i--)
        {range=(Range) rangeList.get(i);
          if(n>=range.min && n<=range.max) return n;
        }
       n--;
     }
   MessageBox(Config.getString("PLC.numberlimit"),Config.getString("PLC.error"));
   return n;
}

private int xAddressType(int type,int initnumber)
 {int number=initnumber;
   EDevice ed;
  for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
   {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
    if(ed.ced.actionType==type)
     {ed.ced.PLCAddress=prefix[0]+Integer.toString(number,base[0]);
       number=NextNo(number,rangeArray[0]);
     }
   }
  return number;
 }

private void XAddress()
 {Range range=(Range) rangeArray[0].get(0);
  int number=range.min;
  number=xAddressType(CEDevice.TYPE_MANUAL_AUTO,number);
  number=xAddressType(CEDevice.TYPE_MANUAL_TOGGLE,number);
  number=xAddressType(CEDevice.TYPE_MECHANIC,number);
//  number=xAddressType(EDevice.TYPE_ELECTRIC,number);
//  number=xAddressType(EDevice.TYPE_TIMER,number);
//  number=xAddressType(EDevice.TYPE_COUNTER,number);
}

  private int EDAddress(int type,int start)
   { int nbase=10;
     String pref=null;
     ArrayList range=null;
     int number=0;

     switch(type)
      {case CEDevice.TYPE_ELECTRIC:
           range=rangeArray[2];
           nbase=base[2];
           pref=prefix[2];
           break;
        case CEDevice.TYPE_TIMER:
           range=rangeArray[3];
           nbase=base[3];
           pref=prefix[3];
           break;
        case CEDevice.TYPE_COUNTER:
           range=rangeArray[4];
           nbase=base[4];
           pref=prefix[4];
           break;
       }
     Range range0=(Range) range.get(0);
     if(start<0) number=range0.min;
     else number=start;
     EDevice ed;
     for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
      {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
       if(ed.ced.actionType==type)
        {ed.ced.PLCAddress=pref+Integer.toString(number,nbase);
          number=NextNo(number,range);
        }
      }
     return number;
   }

  private void YAddress()
   {Range range=(Range) rangeArray[1].get(0);
     int number=range.min;
     ElectricFace ef=null;
     for(int i=0;i<electriclistener.getEArrays().ElectricFaceArray.size();i++)
      {ef=(ElectricFace) electriclistener.getEArrays().ElectricFaceArray.get(i);
        ef.getCDOutput().setFPLCAddress(prefix[1]+Integer.toString(number,base[1]));
        number=NextNo(number,rangeArray[1]);
        if(ef.getCDOutput().twoWay)
         {ef.getCDOutput().BPLCAddress=prefix[1]+Integer.toString(number,base[1]);
           number=NextNo(number,rangeArray[1]);
         }
      }
   }

  private void SetPLCAddress()
   {int n;
    XAddress();
    n=EDAddress(CEDevice.TYPE_ELECTRIC,-1);
    n=EDAddress(CEDevice.TYPE_TIMER,-1);
    if(!tcsep) EDAddress(CEDevice.TYPE_COUNTER,n);
    else EDAddress(CEDevice.TYPE_COUNTER,-1);
    YAddress();
}

  private void SetORG()
   {PLCCode com;
     for(int i=0;i<codeArray.size();i++)
      {com=(PLCCode) codeArray.get(i);
        codeArrayProgram.add(new PLCCode(com.code,com.ced,com.cdo,com.state));
      }
     boolean CommandEnd=false;
     boolean outflag=true;
     for(int n=0;n<codeArrayProgram.size();n++)
      {com=(PLCCode) codeArrayProgram.get(n);
       switch(com.code)
        {case C_LOAD:
             if(outflag==true)
              {outflag=false;
                com.code=C_ORG;
              }
             break;
          case C_LOAD_NOT:
             if(outflag==true)
              {outflag=false;
                com.code=C_ORG_NOT;
              }
             break;
        }
       if(CommandEnd) break;
      }
   }

  private void PackCounter()
   { EDevice ed;
     PLCCode com;
     int number=((Range) rangeArray[2].get(rangeArray[2].size()-1)).max;
     for(int i=0;i<electriclistener.getEArrays().EDeviceArray.size();i++)
      {ed=(EDevice) electriclistener.getEArrays().EDeviceArray.get(i);
        int numcnt=0,numrst=0;
        if(ed.ced.actionType==CEDevice.TYPE_COUNTER)
         {for(int j=0;j<codeArrayProgram.size();j++)
            {com=(PLCCode) codeArrayProgram.get(j);
              if(com.ced==ed.ced && com.code==C_CNT)
               {com.code=C_TEMP;
                 com.ced=null;
                 com.state=number;
                 numcnt=number;
                 number=NextTempNo(number,rangeArray[2]);
                 break;
               }
            }
          for(int j=0;j<codeArrayProgram.size();j++)
           {com=(PLCCode) codeArrayProgram.get(j);
             if(com.ced==ed.ced && com.code==C_RST)
              {com.code=C_TEMP;
                com.ced=null;
                com.state=number;
                numrst=number;
                number=NextTempNo(number,rangeArray[2]);
                break;
               }
           }
          codeArrayProgram.add(new PLCCode(C_TEMPLOAD,null,null,numcnt));
          codeArrayProgram.add(new PLCCode(C_TEMPLOAD,null,null,numrst));
          codeArrayProgram.add(new PLCCode(C_CNT,ed.ced,null,numcnt));
         }
      }
   }

  private String ReturnForm(String str)
   {for(int i=0;i<str.length();i++)
      if(str.substring(i,i+1).toLowerCase().equals("x")) return str.substring(0,i);
     return str;
   }

  public String getProgramString()
    {ArrayList al=getProgram();
      if(al==null) return "";
      StringBuffer sb=new StringBuffer();
      for(int i=0;i<al.size();i++)
       sb.append((String) al.get(i) +"\n");
     return sb.toString();
   }

  public ArrayList getProgram()
   {codeReady=false;
   electriclistener.getElectricPanel().CreatePLCCode();
     codeReady=true;
     SetPLCAddress();
     SetORG();
     if(!tcsep) PackCounter();
     Program.clear();

     PLCCode com;
     int ind=-1;
     String str=null;
     String inst,para="";
     for(int i=0;i<codeArrayProgram.size();i++)
      {com=(PLCCode) codeArrayProgram.get(i);
//System.err.println(com.code);
        if(com.code==C_TEMP)
         Program.add("OUT "+prefix[2]+Integer.toString(com.state,base[2]));
        else if(com.code==C_TEMPLOAD)
         Program.add(commands[C_LOAD-1]+" "+prefix[2]+Integer.toString(com.state,base[2]));
        else if(com.code==C_TIM)
         {str=com.ced.PLCAddress;
           str=str.substring(prefix[3].length()-1,str.length());
           Program.add(commands[C_TIM-1]+" "+str);
           Program.add(ReturnForm(timerForm)+Integer.toString(com.ced.PresetValue));
         }
        else if(com.code==C_CNT)
         {str=com.ced.PLCAddress;
           str=str.substring(prefix[4].length()-1,str.length());
           Program.add(commands[C_CNT-1]+" "+str);
           Program.add(ReturnForm(counterForm)+Integer.toString(com.ced.PresetValue));
         }
        else if(com.code==C_OR_LD || com.code==C_AND_LD)
         {Program.add(commands[com.code-1]);}
        else
         {inst=commands[com.code-1]+" ";
           switch(com.state)
            {case LadderCell.G_NO:
              case LadderCell.G_NC:
              case LadderCell.G_ESOL1:
              case LadderCell.G_ESOL2:
                 para=com.ced.PLCAddress;
                 break;
              case LadderCell.G_SSOL1:
                 para=com.cdo.FPLCAddress;
                 break;
              case LadderCell.G_SSOL2:
                 para=com.cdo.BPLCAddress;
                 break;
             }
            Program.add(inst+para);
           }
         }
        Program.add("END");
        return Program;
    }

  public void MessageBox(String des,String type)
   {JOptionPane.showMessageDialog(electriclistener.getFrame(),type+":"+des);
   }
 }
