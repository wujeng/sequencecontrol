package tw.com.justiot.sequencecontrol;
import tw.com.justiot.sequencecontrol.panel.ElectricListener;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;

public class Creater
 {
/*
  public static Object instanceElement(String modelType,String modelName) throws Exception
   {Object element=null;
     Class eclass=Class.forName("com.wujeng.data.element."+modelType);
     Constructor cons=eclass.getConstructor(new Class[]{String.class});
     element=(com.wujeng.data.element.Element) cons.newInstance(new Object[]{modelName});
     return element;
   }
*/
  public static Object instanceElement(String modelType,String modelName,PneumaticListener pneumaticlistener, ElectricListener electriclistener)
  {
   if(Config.getBoolean("debug")) System.out.println("Creater instanceElement "+modelType+":"+modelName);
   tw.com.justiot.sequencecontrol.pelement.PneumaticElement ele=null;
   try {
   
   if(modelType.equals("Actuator"))
    {
	 tw.com.justiot.sequencecontrol.pelement.Actuator act=new tw.com.justiot.sequencecontrol.pelement.Actuator(modelName, pneumaticlistener);
	 ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) act;
    }
   else if(modelType.equals("Valve"))
    {
     tw.com.justiot.sequencecontrol.pelement.Valve va=new tw.com.justiot.sequencecontrol.pelement.Valve(modelName, pneumaticlistener, electriclistener);
     ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
    }
   else if(modelType.equals("Connector"))
    {
	 tw.com.justiot.sequencecontrol.pelement.Connector va=new tw.com.justiot.sequencecontrol.pelement.Connector(modelName, pneumaticlistener);
	 ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
    }
   else if(modelType.equals("Logic"))
    {
	 tw.com.justiot.sequencecontrol.pelement.Logic va=new tw.com.justiot.sequencecontrol.pelement.Logic(modelName, pneumaticlistener);
	 ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
    }
   else if(modelType.equals("Delay"))
    {
     tw.com.justiot.sequencecontrol.pelement.Delay va=new tw.com.justiot.sequencecontrol.pelement.Delay(modelName, pneumaticlistener);
     ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
	}
   else if(modelType.equals("FlowValve"))
    {
     tw.com.justiot.sequencecontrol.pelement.FlowValve va=new tw.com.justiot.sequencecontrol.pelement.FlowValve(modelName, pneumaticlistener);
     ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
	}
   else if(modelType.equals("PressureValve"))
    {
     tw.com.justiot.sequencecontrol.pelement.PressureValve va=new tw.com.justiot.sequencecontrol.pelement.PressureValve(modelName, pneumaticlistener);
     ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
	}
   else if(modelType.equals("Gauge"))
    {
     tw.com.justiot.sequencecontrol.pelement.Gauge va=new tw.com.justiot.sequencecontrol.pelement.Gauge(modelName, pneumaticlistener);
     ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
	}
   else if(modelType.equals("EValve"))
    {
     tw.com.justiot.sequencecontrol.pelement.EValve va=new tw.com.justiot.sequencecontrol.pelement.EValve(modelName, pneumaticlistener, electriclistener);
     ele=(tw.com.justiot.sequencecontrol.pelement.PneumaticElement) va;
	}
   } catch(Exception e) {
	   e.printStackTrace();
	   
   }
   return ele;
  
  }

 public static Object instanceESystem(String modelType,String modelName, ElectricListener electriclistener)
  {tw.com.justiot.sequencecontrol.eelement.ESystemBase esb=null;
//if(WebLadderCAD.debug)  System.out.println(modelType+":"+modelName);
   try {
   if(modelType.equals("ESystem"))
    {
     tw.com.justiot.sequencecontrol.eelement.ESystem va=new tw.com.justiot.sequencecontrol.eelement.ESystem(modelName, electriclistener);
     return va;
	}
   } catch(Exception e) {
	   e.printStackTrace();
   }
   return esb;
  }

  public static tw.com.justiot.sequencecontrol.eelement.EDevice instanceEDevice(String modelType,String modelName, ElectricListener electriclistener)
   {tw.com.justiot.sequencecontrol.eelement.EDevice ed=null;
     try
      {ed=new tw.com.justiot.sequencecontrol.eelement.EDevice(modelName, electriclistener);
      }
     catch(Exception ce)
      {ce.printStackTrace();
      }
     return ed;
   }
}
