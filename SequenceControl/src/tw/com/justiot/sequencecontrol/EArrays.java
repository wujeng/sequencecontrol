package tw.com.justiot.sequencecontrol;

import java.io.PrintWriter;
import java.util.ArrayList;

import tw.com.justiot.sequencecontrol.eelement.CDOutput;
import tw.com.justiot.sequencecontrol.eelement.CEDevice;
import tw.com.justiot.sequencecontrol.eelement.Compound;
import tw.com.justiot.sequencecontrol.eelement.EDevice;
import tw.com.justiot.sequencecontrol.eelement.ESystemBase;
import tw.com.justiot.sequencecontrol.eelement.ElectricFace;
import tw.com.justiot.sequencecontrol.pelement.EValve;
import tw.com.justiot.sequencecontrol.pelement.Piston;
import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;

public class EArrays {
  public ArrayList<Piston> PistonArray=new ArrayList<Piston>();
  public ArrayList<ElectricFace> ElectricFaceArray=new ArrayList<ElectricFace>();  // ElectricFace replace ESystem to account for compound.
  public ArrayList<EDevice> EDeviceArray=new ArrayList<EDevice>();

  public ArrayList<EDevice> getEDeviceArray(int aType)
  {ArrayList<EDevice> al=new ArrayList<EDevice>();
	EDevice ed=null;
   for(int i=0;i<EDeviceArray.size();i++)
    {	   
     ed=(EDevice) EDeviceArray.get(i);   
     if(ed.ced.actionType==aType) al.add(ed);
    }
	return al;  
  }
  public EDevice findEDeviceByName(String name)
  {if(name==null || name.length()==0 || name.toLowerCase().equals("null")) return null;
	EDevice ed=null;
	for(int i=0;i<EDeviceArray.size();i++)
	    {	   
		
	     ed=(EDevice) EDeviceArray.get(i);   
	     System.out.println("EDevice "+name+" "+ed.getName());
	     if(ed.ced.getName().equals(name)) return ed;
	    }
		return null;    
 }
  public CEDevice findCEDeviceByName(String name)
   {if(name==null || name.length()==0 || name.toLowerCase().equals("null")) return null;
	EDevice ed=null;
	for(int i=0;i<EDeviceArray.size();i++)
	    {	   
	     ed=(EDevice) EDeviceArray.get(i);   
	     if(ed.ced.name.equals(name)) return ed.ced;
	    }
		return null;    
  }
  public ElectricFace findElectricFaceByName(String name) 
  { if(name==null || name.length()==0 || name.toLowerCase().equals("null")) return null;
   ElectricFace ef=null;
   for(int i=0;i<ElectricFaceArray.size();i++)
    {System.out.println("ElectricFace "+name+" "+ElectricFaceArray.get(i).getActuatorName());
	 if(ElectricFaceArray.get(i).getActuatorName().equals(name)) return ElectricFaceArray.get(i);
   }
	return null; 
	  
  }
  public CDOutput findCDOutputByName(String name) 
  { if(name==null || name.length()==0 || name.toLowerCase().equals("null")) return null;
//   System.out.println("to find:"+name);
   ArrayList<CDOutput> al=null;
   CDOutput cdo=null;
   for(int i=0;i<ElectricFaceArray.size();i++)
    {if(ElectricFaceArray.get(i) instanceof Compound)
      {al=((Compound) ElectricFaceArray.get(i)).getCDOutputs();
  	   if(al!=null)
  	    {for(int j=0;j<al.size();j++)
  	     {cdo=(CDOutput) al.get(j);
  //	  System.out.println("compound cdo:"+cdo.name);
  		  if(cdo.name.equals(name)) return cdo;
  	     }
  	    }
      }
     else
      {cdo=((ElectricFace) ElectricFaceArray.get(i)).getCDOutput();
   //   System.out.println("cdo:"+cdo.name);
       if(cdo.name.equals(name)) return cdo;
      }
   }
	return null; 
	  
  }
  
  private ArrayList<EArraysListener> listeners=new ArrayList<EArraysListener>();
  public void addListener(EArraysListener listener)
    {listeners.add(listener);}
  public void removeListener(EArraysListener listener)
    {listeners.remove(listener);}
  private void EArraysChanged(String type,String op,Object obj)
   {
  	if(listeners!=null && listeners.size()>0)
  	 {EArraysListener listener=null;
  	  for(int i=0;i<listeners.size();i++)
  	   {listener=(EArraysListener) listeners.get(i);
  		listener.EArraysChanged(type,op,obj);  
  	   }
  	 }
   }
 
  public ElectricFace getElectricFace(EDevice ed)
   {if(ed.ced.actionType!=CEDevice.TYPE_MECHANIC) return null;
   ElectricFace sys=null;
    ArrayList al=null;
    for(int i=0;i<ElectricFaceArray.size();i++)
     {sys=(ElectricFace) ElectricFaceArray.get(i);
      if(sys.withLS())
       {al=sys.getValveArray();
        if(al!=null)
         {for(int j=0;j<al.size();j++)
            if(al.get(j) instanceof EDevice && ((EDevice) al.get(j))==ed) return sys;
         }
       }
     }
    return null;
   }

  public void reset()
   {EDevice ed=null;
    for(int i=0;i<EDeviceArray.size();i++)
     {ed=(EDevice) EDeviceArray.get(i);
      ed.ced.reset();
     }
    ElectricFace sys=null;
    for(int i=0;i<ElectricFaceArray.size();i++)
     {sys=(ElectricFace) ElectricFaceArray.get(i);
      CDOutput cdo=sys.getCDOutput();
      if(cdo!=null) cdo.reset();
     }
   }
  
  public void setEDeviceMode(int m)
   {EDevice ed=null;
	 for(int i=0;i<EDeviceArray.size();i++)
    {ed=(EDevice) EDeviceArray.get(i);
     ed.mode=m;
    }   
   }
  
  public void addEDevice(EDevice ed)
   {
    EDeviceArray.add(ed);
    EArraysChanged("EDevice","addEDevice",ed);
   }
  public void deleteEDevice(EDevice ed)
   {EDeviceArray.remove(ed);
	EArraysChanged("EDevice","deleteEDevice",ed);
   }
  public void removeAllEDevice()
   {EDeviceArray.clear();
    CEDevice.clearCount();
    EArraysChanged("EDevice","removeAllEDevice",null);
   }
  
  /*
  public EDevice getEDevice4Name(String name)
   {EDevice ed=null;
    for(int i=0;i<EDeviceArray.size();i++)
     {ed=(EDevice) EDeviceArray.get(i);
      if(ed.ced.name.equals(name)) return ed;
     }	
    return ed;
   }
  public ESystemBase getESystemBase4Name(String name)
   {ESystemBase ef=null;
    for(int i=0;i<ElectricFaceArray.size();i++)
     {ef=(ESystemBase) ElectricFaceArray.get(i);
      if(ef.getCDOutput().name.equals(name)) return ef;
    }	
    return ef;
   } 
   */
}
