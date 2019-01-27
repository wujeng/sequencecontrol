package tw.com.justiot.sequencecontrol.part;

import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;

public class Port
 {
  public static final float DEFAULT_SOURCE_PRESSURE=6.0f;
  public static final float DEFAULT_FLOWRATE=40.0f;
  public static final float DEFAULT_PRESSURE_DROP_RATIO=0.0f;

  public static final int DIR_DOWN=0;
  public static final int DIR_UP=1;
  public static final int DIR_RIGHT=2;
  public static final int DIR_LEFT=3;

  public static float unknownPressure=-1.0f;
  public static float sourcePressure=DEFAULT_SOURCE_PRESSURE;
  public static float unknownFlowrate=-1.0f;
  public static float sourceFlowrate=DEFAULT_FLOWRATE;
  public static float pressureDropRatio=DEFAULT_PRESSURE_DROP_RATIO;

  private Position position;
  private float pressure;
  private float flowrate;
  private boolean connected;
  private int dir;
  private PneumaticElement owner;
  private boolean isPower;
  private boolean isDrain;
  private boolean isStop;

  public Port(Port pt)
   {position=pt.getPosition();
    connected=pt.getConnected();
    dir=pt.getDir();
    owner=pt.getOwner();
    setIsPower(pt.getIsPower());
    setIsDrain(pt.getIsDrain());
    setIsStop(pt.getIsStop());
    setPressure(pt.getPressure());
    setFlowrate(pt.getFlowrate());
   }

  public Port(Position p, PneumaticElement own)
   {position=p;
    owner=own;
    connected=false;
    dir=DIR_DOWN;
    pressure=unknownPressure;
    flowrate=unknownFlowrate;
    isPower=false;
    isDrain=false;
    isStop=false;
   }
  public Port(Position p)
   {position=p;
    owner=null;
    connected=false;
    dir=DIR_DOWN;
    pressure=unknownPressure;
    flowrate=unknownFlowrate;
    isPower=false;
    isDrain=false;
    isStop=false;
   }
  public void setOwner(PneumaticElement own)
   {owner=own;}
  public PneumaticElement getOwner()
   {return owner;}

  public int getRealX()
   {return owner.getX()+position.x;}
  public int getRealY()
   {return owner.getY()+position.y;}

  public void source()
   {
	isPower=true;
	isDrain=false;
    pressure=sourcePressure;
   }

  public void sink()
   {
	isDrain=true;
	isPower=false;
    pressure=0.0f;
   }

  public void open()
   {
    setPressure(unknownPressure);
    setFlowrate(unknownFlowrate);
   }

  private static int rotateDir(int dir)
   {switch(dir)
     {case DIR_DOWN: return DIR_LEFT;
      case DIR_UP:   return DIR_RIGHT;
      case DIR_RIGHT:return DIR_DOWN;
      case DIR_LEFT: return DIR_UP;
     }
    return DIR_DOWN;
   }

  public static int rotateDir(int dir, int n)
   {int dr=dir;
    for(int i=0;i<n;i++)
     dr=rotateDir(dr);
    return dr;
   }

  public boolean getIsPower() {return isPower;}
  public void setIsPower(boolean b) {isPower=b;}
  public boolean getIsDrain() {return isDrain;}
  public void setIsDrain(boolean b) {isDrain=b;}
  public boolean getIsStop() {return isStop;}
  public void setIsStop(boolean b) {
	if(owner!=null && owner.modelName.equals("Source")) return;
	if(owner!=null && owner.modelName.equals("Sink")) return;

	isStop=b;
	if(isStop) {
	  setFlowrate(0.0f);
	}
  }

  public boolean getConnected() {return connected;}
  public void setConnected(boolean c)
   {connected=c;
    if(!connected) open();
   }

  public int getDir() {return dir;}
  public void setDir(int c) {dir=c;}

  public Position getPosition() {return position;}
  public void setPosition(Position p) {position=p;}

  public float getPressure() {return pressure;}
  public void setPressure(float pre)
   {if(owner!=null && owner.modelName.equals("Source")) return;
	if(owner!=null && owner.modelName.equals("Sink")) return;
	
    pressure=pre;
   }

  public float getFlowrate() {return flowrate;}
  public void setFlowrate(float flo)
   {
    flowrate= flo;
   }
 }
