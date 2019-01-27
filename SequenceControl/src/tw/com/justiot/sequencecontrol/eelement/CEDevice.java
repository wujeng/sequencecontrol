package tw.com.justiot.sequencecontrol.eelement;

import java.awt.Point;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TimerTask;

import tw.com.justiot.sequencecontrol.SCCAD;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;

public class CEDevice
 {public static String modelType="EDevice";
  public static double ratio=1.0;
  private static int nMANUAL_AUTO;
  private static int nMECHANIC;
  public static int nELECTRIC;
  private static int nTIMER;
  private static int nCOUNTER;
  private static int nMANUAL_TOGGLE;
  private static int nSENSOR;


  public static final int TimerPeriod=100;

  public static final int TYPE_MANUAL_AUTO=0;
  public static final int TYPE_MECHANIC=1;
  public static final int TYPE_ELECTRIC=2;
  public static final int TYPE_TIMER=3;
  public static final int TYPE_COUNTER=4;
  public static final int TYPE_MANUAL_TOGGLE=5;
  public static final int TYPE_SENSOR=6;
  public int actionType=TYPE_MANUAL_AUTO;

  public String modelName;
  private String abbr;
  public String name;
  public boolean status,sol1Status,sol2Status;
  public int CurrentValue,PresetValue;
  private boolean timeron;
  public boolean statusInput;
  public String PLCAddress;
  public String NAPKey=null;
  public int NAPno=0;

  public PneumaticElement element;
  public ESystemBase esb;
  public int LSpos;
//  public boolean remoteTrigger=false;
  public ArrayList AList=new ArrayList();
  public ArrayList BList=new ArrayList();;
  public Point SP1=new Point(), SP2=new Point();


  private ArrayList listeners=new ArrayList();

  public String getName() {return name;}
  public void setName(String nam) {name=nam;}
  public void setPLCAddress(String plcaddr) {PLCAddress=plcaddr;}
  public void setNAPKey(String napkey) {NAPKey=napkey;}
  public void setNAPno(int no) {NAPno=no;}
  public void setPresetValue(int no) {PresetValue=no;}
  public String getModelName() {return modelName;}


  public static void setModelType(String mt)
   {if(modelType==null || modelType.length()==0) modelType=mt;
   }
  public static void clearCount()
   {nMANUAL_AUTO=0;
    nMECHANIC=0;
    nELECTRIC=0;
    nTIMER=0;
    nCOUNTER=0;
    nMANUAL_TOGGLE=0;
    nSENSOR=0;
   }
  public void addListener(CEDeviceListener listener)
   {
    listeners.add(listener);
   }
  public void removeListener(CEDeviceListener listener)
   {
	listeners.remove(listener);
   }

  private void statusChanged()
   {
	if(listeners!=null && listeners.size()>0)
	 {CEDeviceListener listener=null;
	  for(int i=0;i<listeners.size();i++)
	   {listener=(CEDeviceListener) listeners.get(i);
		listener.statusChanged(this);
	   }
	 }
   }
  private void valueChanged()
   {
	if(listeners!=null && listeners.size()>0)
	 {CEDeviceListener listener=null;
	  for(int i=0;i<listeners.size();i++)
	   {listener=(CEDeviceListener) listeners.get(i);
		listener.valueChanged(this);
	   }
	 }
   }
  public boolean isTimeron() {return timeron;}
  public void setTimeron(boolean ton) {timeron=ton;}

  public void startTimer(java.util.Timer timer)
   {if(actionType==TYPE_TIMER) timer.scheduleAtFixedRate(new TimeTask(),0,TimerPeriod);
   }

  public void timeStep()
	 {if(timeron)
	   {if(actionType!=TYPE_TIMER) return;
     if(CurrentValue>0)
      {CurrentValue--;
       valueChanged();
      }
     else if(CurrentValue==0)
      {status=true;
       setTimeron(false);
       statusChanged();
      }
    }
  }


  private class TimeTask extends TimerTask
   {
	public void run()
	 {if(timeron)
	   {if(actionType!=TYPE_TIMER) return;
	   if(electriclistener.getOpMode()==SCCAD.OP_SIMULATION && electriclistener.getSimulationMode()==SCCAD.SIMULATION_PAUSE) return;
        if(CurrentValue>0)
         {CurrentValue--;
          valueChanged();
         }
        else if(CurrentValue==0)
         {status=true;
          setTimeron(false);
          statusChanged();
         }
       }
     }
   }


  private ElectricListener electriclistener;
  public CEDevice(String modelName, String abbr, int atype, ElectricListener electriclistener)
   {this.electriclistener=electriclistener;
	this.modelName=modelName;
    this.abbr=abbr;
    this.actionType=atype;

    timeron=false;
    if(actionType==TYPE_TIMER) PresetValue=30;
    else PresetValue=3;
    CurrentValue=PresetValue;
      switch(actionType)
        {case TYPE_MANUAL_AUTO:
            nMANUAL_AUTO++;
            name=abbr+Integer.toString(nMANUAL_AUTO);
            break;
         case TYPE_MANUAL_TOGGLE:
            nMANUAL_TOGGLE++;
            name=abbr+Integer.toString(nMANUAL_TOGGLE);
            break;
         case TYPE_MECHANIC:
            nMECHANIC++;
            name=abbr+Integer.toString(nMECHANIC);
            break;
         case TYPE_ELECTRIC:
            nELECTRIC++;
            name=abbr+Integer.toString(nELECTRIC);
            break;
         case TYPE_TIMER:
            nTIMER++;
            name=abbr+Integer.toString(nTIMER);
            break;
         case TYPE_COUNTER:
            nCOUNTER++;
            name=abbr+Integer.toString(nCOUNTER);
            break;
         case TYPE_SENSOR:
             nSENSOR++;
             name=abbr+Integer.toString(nSENSOR);
             break;
       }
     reset();
   }

/*
  public void reset() {
	  switch(actionType)
      {case TYPE_MANUAL_AUTO:
          nMANUAL_AUTO++;
          name=abbr+Integer.toString(nMANUAL_AUTO);
          break;
       case TYPE_MANUAL_TOGGLE:
          nMANUAL_TOGGLE++;
          name=abbr+Integer.toString(nMANUAL_TOGGLE);
          break;
       case TYPE_MECHANIC:
          nMECHANIC++;
          name=abbr+Integer.toString(nMECHANIC);
          break;
       case TYPE_ELECTRIC:
          nELECTRIC++;
          name=abbr+Integer.toString(nELECTRIC);
          break;
       case TYPE_TIMER:
          nTIMER++;
          name=abbr+Integer.toString(nTIMER);
          break;
       case TYPE_COUNTER:
          nCOUNTER++;
          name=abbr+Integer.toString(nCOUNTER);
          break;
       case TYPE_SENSOR:
           nSENSOR++;
           name=abbr+Integer.toString(nSENSOR);
           break;
     }
  }
  */
  public String write()
   {String wname=name;
    if(wname==null || wname.length()==0) wname="null";
    String wnapkey=NAPKey;
    if(wnapkey==null || wnapkey.length()==0) wnapkey="null";
    String wplc=PLCAddress;
    if(wplc==null || wplc.length()==0) wplc="null";
	return SCCAD.Data_EDevice+" "+modelType+" "+modelName+" "+abbr+" "+wname+" "+actionType+" "+PresetValue+" "+NAPKey+" "+NAPno+" "+wplc;
   }

  public static CEDevice read(String str, ElectricListener electriclistener)
   {
	try {  
	StringTokenizer token=new StringTokenizer(str);
  // System.out.println("CEDevice: "+str);
    int dtype=Integer.parseInt(token.nextToken());
    if(dtype!=SCCAD.Data_EDevice) return null;
    String mType=token.nextToken();
    String mName=token.nextToken();
    String abbr=token.nextToken();
    String name=token.nextToken();
    if(name.equals("null")) name=null;
    int atype=Integer.parseInt(token.nextToken());
    int pvalue=Integer.parseInt(token.nextToken());
    String napkey=token.nextToken();
    if(napkey.equals("null")) napkey=null;
    int napno=Integer.parseInt(token.nextToken());
    String plc=token.nextToken();
    if(plc.equals("null")) plc=null;
    CEDevice ced=new CEDevice(mName,abbr,atype, electriclistener);
    
    if(ced!=null)
     {ced.name=name;
      ced.setPresetValue(pvalue);
      ced.NAPKey=napkey;
      ced.NAPno=napno;
      ced.PLCAddress=plc;
     }
 //   System.out.println(ced);
    return ced;
	} catch(Exception e) {
		e.printStackTrace();
		return null;
	}
   }

  public void setStatus(boolean st)
   {boolean changed=false;
    if(status!=st) changed=true;
	switch(actionType)
      {case TYPE_MANUAL_AUTO:
           status=st;
           break;
        case TYPE_MANUAL_TOGGLE:
           if(st)
        	{status=!status;
        	 changed=true;
        	}
           else changed=false;
           break;
        case TYPE_MECHANIC:
        case TYPE_SENSOR:
           status=st;
           break;
        case TYPE_ELECTRIC:
//           status=st;
           break;
        case TYPE_TIMER:
//           status=st;
           break;
        case TYPE_COUNTER:
//           status=st;
           break;
      }
     if(changed) statusChanged();
    }

  public void setSol1Status(boolean st)
   {boolean changed=false;
	switch(actionType)
      {case TYPE_MANUAL_AUTO:
           break;
        case TYPE_MANUAL_TOGGLE:
           break;
        case TYPE_MECHANIC:
        case TYPE_SENSOR:
           break;
        case TYPE_ELECTRIC:
           if(status!=st) changed=true;
           sol1Status=st;
           status=sol1Status;
           break;
        case TYPE_TIMER:
           sol1Status=st;
           if(st) setTimeron(true);
           else
             {setTimeron(false);
              status=false;
              CurrentValue=PresetValue;
              valueChanged();
              changed=true;
             }
           break;
        case TYPE_COUNTER:
        	
           if(st && !sol1Status)
            {if(CurrentValue>0)
              {CurrentValue--;
               valueChanged();
              }
             if(CurrentValue==0)
              {status=true;
               changed=true;
              }
            }
           sol1Status=st;
           break;
      }
	 if(changed) statusChanged();
    }

  public void setSol2Status(boolean st)
   {switch(actionType)
      {case TYPE_MANUAL_AUTO:
           break;
        case TYPE_MANUAL_TOGGLE:
           break;
        case TYPE_MECHANIC:
        case TYPE_SENSOR:
           break;
        case TYPE_ELECTRIC:
           break;
        case TYPE_TIMER:
           break;
        case TYPE_COUNTER:
        	this.sol2Status=st;
           if(st)
            {CurrentValue=PresetValue;
             status=false;
             
             valueChanged();
             statusChanged();
            }
           break;
      }
    }

  public void reset()
   {
    setSol2Status(true);
    setSol2Status(false);
    setSol1Status(false);
    setStatus(false);
   }
/*
  public void inputModuleData()
   {DIOModule dio=(DIOModule) Modules.modules.get(NAPKey);
    statusInput=dio.inStatus[NAPno];
   }
   */
 }
