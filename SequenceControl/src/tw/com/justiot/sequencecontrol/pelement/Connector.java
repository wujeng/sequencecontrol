package tw.com.justiot.sequencecontrol.pelement;

import java.util.ArrayList;

import tw.com.justiot.sequencecontrol.config.ConnectorParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Line;
import tw.com.justiot.sequencecontrol.part.Port;

public class Connector extends PneumaticElement
 {public static int count=0;
  public Connector(String mname,PneumaticListener ownerlistener)
   {super(mname,false, ownerlistener);
    if(PneumaticConfig.parameter.containsKey(mname))
     {ConnectorParameter ep=(ConnectorParameter) PneumaticConfig.parameter.get(mname);
      this.ports=new Port[ep.ports.length];
      portOrg=new Port[this.ports.length];
      for(int i=0;i<ep.ports.length;i++)
       {
        if(ep.ports[i]!=null)
         {this.ports[i]=new Port(ep.ports[i]);
          this.ports[i].setOwner(this);
          portOrg[i]=new Port(ports[i]);
         }
        else
         {this.ports[i]=null;
          portOrg[i]=null;
         }
       }
      if(mname.equals("Source"))
       {ports[0].source();
        portOrg[0].source();
       }
      if(mname.equals("Sink"))
       {ports[0].sink();
        portOrg[0].sink();
       }
       if(mname.equals("Stop"))
        {ports[0].setFlowrate(0.0f);
   //      ports[0].setQCapacity(0.0f);
         portOrg[0].setFlowrate(0.0f);
   //      portOrg[0].setQCapacity(0.0f);
         ports[0].setIsStop(true);
         portOrg[0].setIsStop(true);
        }

     }
   }

   public Port[] nextPorts(Port pt)
    {if(!modelName.equals("3Way") && !modelName.equals("1Way")) return null;
    if(modelName.equals("3Way"))
    {
     if(ports[0]!=null && ports[0]==pt)
      {
   	Port[] ps={ports[1],ports[2]};
       return ps;
      }
     if(ports[1]!=null && ports[1]==pt)
      {Port[] ps={ports[0],ports[2]};
       return ps;
      }
     if(ports[2]!=null && ports[2]==pt)
      {Port[] ps={ports[1],ports[0]};
      return ps;
      }
     return null;
    }
   if(modelName.equals("1Way"))
    {if(pt==ports[0])
      {
       Port[] ps= {ports[1]};
       return ps;
      }
     else
      return null;
    }
   return null;
    }

   public Port[] nextStopPorts(Port pt) {
 	  if(!modelName.equals("3Way") && !modelName.equals("1Way")) return null;
 	  if(modelName.equals("3Way"))
 	   {int stopcount=0;
 		  for(int i=0;i<3;i++) if(ports[i].getIsStop()) stopcount++;
 		  //	System.out.println("stopcount="+stopcount);
 		  	  if(stopcount>=2) for(int i=0;i<3;i++) ports[i].setIsStop(true);
 		ArrayList<Port> al=new ArrayList<Port>();
 	    Port p=null;
 		for(int i=0;i<3;i++) {
 		  p=ports[i];
 		  if(p==pt) continue;
 		  if(p.getIsStop()) al.add(p);
 		}
 		Port[] ps=new Port[al.size()];
 		for(int i=0;i<ps.length;i++) ps[i]=al.get(i);
 	    return ps;
 	   }
 	  if(modelName.equals("1Way"))
 	   {if(pt==ports[0])
 	     {
 	      Port[] ps= {ports[1]};
 	      return ps;
 	     }
 	    else
 	     return null;
 	   }
 	  return null;
   }

   public void mark()
     {
      if(modelName.equals("Source"))
       {
        ports[0].setPressure(Port.sourcePressure);
        ports[0].setIsPower(true);
        ports[0].setIsStop(false);
        ports[0].setIsDrain(false);
    //    ports[0].setQCapacity(Port.sourceFlowrate);
        return;
       }
      if(modelName.equals("Sink"))
       {ports[0].setPressure(0.0f);
        ports[0].setIsDrain(true);
        ports[0].setIsPower(false);
        ports[0].setIsStop(false);
    //    ports[0].setQCapacity(Port.sourceFlowrate);
        return;
       }
      if(modelName.equals("Stop"))
       {ports[0].setFlowrate(0.0f);
    //    ports[0].setQCapacity(0.0f);
        ports[0].setIsStop(true);
        return;
       }
    //  if(modelName.equals("3Way"))
    //  {int stopcount=0;//
   //	for(int i=0;i<3;i++) if(ports[i].getIsStop()) stopcount++;
   //	System.out.println("stopcount="+stopcount);
   //	if(stopcount>=3) for(int i=0;i<2;i++) ports[i].setIsStop(true);
    //  }
     }

     public void check()
      {
       if(modelName.equals("Sink"))
        {ports[0].setPressure(0.0f);
     //    ports[0].setPCapacity(0.0f);
         return;
        }
       if(modelName.equals("3Way"))
        {int stopcount=0;
    	  for(int i=0;i<3;i++) if(ports[i].getIsStop()) stopcount++;
    //	System.out.println("stopcount="+stopcount);
    	  if(stopcount>=2) for(int i=0;i<3;i++) ports[i].setIsStop(true);
         if(!ports[0].getConnected() || !ports[1].getConnected() || !ports[2].getConnected()) {
       	 ports[0].setIsDrain(true);
       	 ports[1].setIsDrain(true);
       	 ports[2].setIsDrain(true);
         } else {

       	for(int i=0;i<4;i++) {
       	 Line.connect(ports[0], ports[1]);
       	 Line.connect(ports[1], ports[2]);
       	 Line.connect(ports[2], ports[0]);
       	}
         }
        }
       if(modelName.equals("1Way"))
        {if(ports[0].getPressure()>ports[1].getPressure())
          {ports[1].setPressure(ports[0].getPressure());
           ports[1].setFlowrate(ports[0].getFlowrate());
          }
         else
          {ports[0].setFlowrate(0.0f);
           ports[1].setFlowrate(0.0f);
      //     ports[1].setQCapacity(0.0f);
       ///    ports[0].setQCapacity(0.0f);
          }
        }
       if(modelName.equals("Stop"))
        {ports[0].setFlowrate(0.0f);
     //    ports[0].setQCapacity(0.0f);
        }
      }

     public String extraWrite()
      {return "";}

     public void extraRead(String str)
      {}
 }
