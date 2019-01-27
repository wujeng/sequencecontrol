package tw.com.justiot.sequencecontrol.eelement;

import java.awt.Point;
import java.util.*;

import tw.com.justiot.sequencecontrol.SCCAD;

public class CDOutput
 {
   public static final int TYPE_ONOFF=0;
   public static final int TYPE_LOOP=1;
   public static final int TYPE_TWOWAY=2;
   public static final int TYPE_ONEWAY=3;
   public int systemType=TYPE_ONOFF;
   
   public String name;
   public String modelType;
   public String modelName;
    
    public boolean twoWay=false;
    public boolean ActuatorTwoWay=false;
    public String NAPKey1,NAPKey2;
    public int NAPno1,NAPno2;
    public String FPLCAddress=null,BPLCAddress=null;
    private boolean SolFStatus=false,SolBStatus=false;
    
    public Point SP1=new Point(),SP2=new Point(); 
    public static int count=0;
    public static void clearCount() {count=0;}
    
    public CDOutput(String modelType, String modelName, int stype, boolean tway)
     {this.modelType=modelType;
      this.modelName=modelName;
  	  this.systemType=stype;  
  	  this.twoWay=tway;
  	  count++;
      name=modelName.substring(0,1)+Integer.toString(count);
      NAPKey1=null;
      NAPKey2=null;
     }
    
   public void setFPLCAddress(String addr) {FPLCAddress=addr;}
   public void setBPLCAddress(String addr) {BPLCAddress=addr;}
   
   private ArrayList listeners=new ArrayList();
   public void addListener(CDOutputListener listener)
    {listeners.add(listener);}
   public void removeListener(CDOutputListener listener)
    {listeners.remove(listener);}
  
   public void setName(String na)
    {name=na;
     nameChanged();
    }
   public String getName()
    {
	  return name;  
    }
  private void solFStatusChanged()
   {
	if(listeners!=null && listeners.size()>0)
	 {CDOutputListener listener=null;
	  for(int i=0;i<listeners.size();i++)
	   {listener=(CDOutputListener) listeners.get(i);
		listener.solFStatusChanged(this);  
	   }
	 }
   }
  private void solBStatusChanged()
   {
	if(listeners!=null && listeners.size()>0)
	 {CDOutputListener listener=null;
	  for(int i=0;i<listeners.size();i++)
	   {listener=(CDOutputListener) listeners.get(i);
		listener.solBStatusChanged(this);  
	   }
	 }
    }
  private void nameChanged()
  {
	if(listeners!=null && listeners.size()>0)
	 {CDOutputListener listener=null;
	  for(int i=0;i<listeners.size();i++)
	   {listener=(CDOutputListener) listeners.get(i);
		listener.nameChanged(this);  
	   }
	 }
  }
   public boolean getSolFStatus() {return SolFStatus;}
   public boolean getSolBStatus() {return SolBStatus;}
   public void setSolFStatus(boolean b) 
    {boolean changed=false;
     if(SolFStatus!=b) changed=true;
	 SolFStatus=b;     
     if(changed) solFStatusChanged();
//      if(elementListener.getElectrics()!=null) elementListener.getElectrics().repaint();
    }
   public void setSolBStatus(boolean b) 
     {//if(EFsol2==null) return;
	   boolean changed=false;
	   if(SolBStatus!=b) changed=true;
       SolBStatus=b;
       if(changed) solBStatusChanged();
//       if(elementListener.getElectrics()!=null) elementListener.getElectrics().repaint();
     }
   public void reset()
    {setSolBStatus(true);
     setSolBStatus(false);
     setSolFStatus(false);
    } 
   
   public String write()
    {
	 if(modelType.equals("EValve")) return "";
     StringBuffer sb=new StringBuffer();
     sb.append(SCCAD.Data_CDOutput+" ");
     sb.append(modelType+" ");
     sb.append(modelName+" ");
     sb.append(systemType+" ");
     sb.append(twoWay+" ");
     sb.append(name+" ");
     
     String napk1=NAPKey1;
     if(napk1==null || napk1.length()==0) napk1="null";
     String napk2=NAPKey2;
     if(napk2==null || napk2.length()==0) napk2="null";
     String fplc=FPLCAddress;
     if(fplc==null || fplc.length()==0) fplc="null";
     String bplc=BPLCAddress;
     if(bplc==null || bplc.length()==0) bplc="null";
     
     sb.append(napk1+" ");
     sb.append(NAPno1+" ");
     sb.append(napk2+" ");
     sb.append(NAPno2+" ");
     sb.append(fplc+" ");
     sb.append(bplc+" ");
     return sb.toString();
    }
   
   public static CDOutput read(String str)
    {try {
     StringTokenizer token=new StringTokenizer(str);
     int dtype=Integer.parseInt(token.nextToken());
     if(dtype!=SCCAD.Data_CDOutput) throw new Exception("Wrong data type!");
     String mtype=token.nextToken();
     String mName=token.nextToken();
     int stype=Integer.parseInt(token.nextToken());
     boolean tway=false;
     if(token.nextToken().equals("true")) tway=true;
     CDOutput cdo=new CDOutput(mtype,mName,stype,tway);
     if(cdo!=null)
      {	 
       String name=token.nextToken();
       if(name.equals("null")) name=null;
       cdo.name=name;
       String napkey1=token.nextToken();
       if(napkey1.equals("null")) napkey1=null;
       cdo.NAPKey1=napkey1;
       cdo.NAPno1=Integer.parseInt(token.nextToken());
       String napkey2=token.nextToken();
       if(napkey2.equals("null")) napkey2=null;
       cdo.NAPKey2=napkey2;
       cdo.NAPno2=Integer.parseInt(token.nextToken());
       String fplc=token.nextToken();
       if(fplc.equals("null")) fplc=null;
       cdo.FPLCAddress=fplc;
       String bplc=token.nextToken();
       if(bplc.equals("null")) bplc=null;
       cdo.BPLCAddress=bplc;
      }     
     return cdo;
    } catch(Exception e) {
    	e.printStackTrace();
    	return null;
    }
    }
   // for PLC
   private boolean OutputFStatus,OutputBStatus;
   public boolean getOutputFStatus() {return OutputFStatus;}
   public boolean getOutputBStatus() {return OutputBStatus;}
   public void setOutputFStatus(boolean b) {OutputFStatus=b;}
   public void setOutputBStatus(boolean b) {OutputBStatus=b;}
   
   /*
   public void driveModule()
    {try {
	 if(NAPKey1!=null && NAPKey1.length()>0)
	  {DIOModule dio=(DIOModule) Modules.modules.get(NAPKey1);
	   dio.setOutputStatus(NAPno1, SolFStatus);
	  }
     if(NAPKey2!=null && NAPKey2.length()>0)
	  {DIOModule dio=(DIOModule) Modules.modules.get(NAPKey2);
	   dio.setOutputStatus(NAPno2, SolBStatus);
	  }  
    } catch(Exception e) {
    	e.printStackTrace();
    }
    }
    */
 } 
