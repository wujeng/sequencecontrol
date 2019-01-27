package tw.com.justiot.sequencecontrol.eelement;

import java.awt.*;
import java.util.*;

import tw.com.justiot.sequencecontrol.config.*;

public interface ElectricFace 
  {//public Image getEFsol1() ;
   //public Image getEFsol1on() ;
   //public Image getEFsol2() ;
   //public Image getEFsol2on() ;
   //public Rect getActionRect1() ;
   //public Rect getActionRect2() ;
   //public Rect getLSRect() ;

   public ArrayList getValveArray();
   public ArrayList getValvePos();
   public String getActuatorName();
   public boolean withLS();
 //  public int getForceNumber();
   public boolean getMemory();
   public EDevice getFirstLimitswitch();
//   public EDevice getLastLimitswitch();
   public String write();
   public ESymbol getESymbol();
   public CDOutput getCDOutput();
   public void reset();
  }

//public Point getSP1(); 
//public Point getSP2(); 
//public void delete();