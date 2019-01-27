package tw.com.justiot.sequencecontrol.pelement;

import tw.com.justiot.sequencecontrol.config.LogicParameter;
import tw.com.justiot.sequencecontrol.config.PneumaticConfig;
import tw.com.justiot.sequencecontrol.panel.PneumaticListener;
import tw.com.justiot.sequencecontrol.part.Port;

public class Logic extends PneumaticElement
 {private Port aport,xport,yport;
  public static int count=0;
  public Logic(String mname,PneumaticListener ownerlistener)
   {super(mname,false, ownerlistener);
    if(PneumaticConfig.parameter.containsKey(mname))
     {LogicParameter ep=(LogicParameter) PneumaticConfig.parameter.get(mname);
      if(ep.aport!=null)
       {this.aport=new Port(ep.aport);
        this.aport.setDir(Port.DIR_UP);
       }
      else this.aport=null;
      if(ep.xport!=null)
       {this.xport=new Port(ep.xport);
        this.xport.setDir(Port.DIR_LEFT);
       }
      else this.xport=null;
      if(ep.yport!=null)
       {this.yport=new Port(ep.yport);
        this.yport.setDir(Port.DIR_RIGHT);
       }
      else this.yport=null;
      ports=new Port[]{this.aport,this.xport,this.yport};
      portOrg=new Port[ports.length];
      for(int i=0;i<ports.length;i++)
       {if(ports[i]!=null)
         {ports[i].setOwner(this);
          ports[i].open();
          portOrg[i]=new Port(ports[i]);
         }
        else
         portOrg[i]=null;
       }
     }
   }

   public Port[] nextPorts(Port pt)
    {Port[] pts=new Port[1];
     if(modelName.equals("OR"))
      {switch(curImage)
        {case 0:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts[0]=aport;
                if(pt==yport) pts[0]=aport;
                break;
         case 1:if(pt==aport) pts[0]=xport;
                if(pt==xport) pts[0]=aport;
                if(pt==yport) pts=null;
                break;
         case 2:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts=null;
                if(pt==yport) pts[0]=aport;
                break;
         case 3:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts[0]=aport;
                if(pt==yport) pts[0]=aport;
                break;
        }
      }
     if(modelName.equals("AND"))
      {switch(curImage)
        {case 0:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts[0]=aport;
                if(pt==yport) pts[0]=aport;
                break;
         case 1:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts=null;
                if(pt==yport) pts[0]=aport;
                break;
         case 2:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts[0]=aport;
                if(pt==yport) pts=null;
                break;
         case 3:if(pt==aport) pts[0]=yport;
                if(pt==xport) pts[0]=aport;
                if(pt==yport) pts[0]=aport;
                break;
        }
      }
     return pts;
    }

   public void check()
    {if(xport.getPressure()> Port.sourcePressure/2.0f)
      {if(yport.getPressure()> Port.sourcePressure/2.0f)
        curImage=3;
       else
        curImage=1;
      }
     else
      {if(yport.getPressure()> Port.sourcePressure/2.0f)
        curImage=2;
       else
        curImage=0;
      }
     if(modelName.equals("OR"))
      {switch(curImage)
        {case 0:aport.open();
                break;
         case 1:aport.setPressure(xport.getPressure());
                aport.setFlowrate(xport.getFlowrate());
                break;
         case 2:aport.setPressure(yport.getPressure());
                aport.setFlowrate(yport.getFlowrate());
                break;
         case 3:if(xport.getPressure() > yport.getPressure())
                 {aport.setPressure(xport.getPressure());
                  aport.setFlowrate(xport.getFlowrate());
                 }
                else
                 {aport.setPressure(yport.getPressure());
                  aport.setFlowrate(yport.getFlowrate());
                 }
                break;
        }
      }
     if(modelName.equals("AND"))
      {switch(curImage)
        {case 0:
         case 1:
         case 2:aport.open();
                break;
         case 3:if(xport.getPressure() > yport.getPressure())
                 {aport.setPressure(xport.getPressure());
                  aport.setFlowrate(xport.getFlowrate());
                 }
                else
                 {aport.setPressure(yport.getPressure());
                  aport.setFlowrate(yport.getFlowrate());
                 }
                break;
        }
      }
    }

  public String extraWrite()
   {return "";}

  public void extraRead(String str)
   {}

 }
