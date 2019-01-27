package tw.com.justiot.sequencecontrol.part;

import java.awt.Point;

import tw.com.justiot.sequencecontrol.pelement.PneumaticElement;

public class BoardElement // cut and paste board
 {public String modelType;
  public String modelName;
  public String name;
  public double angle;
  public Point location;
  public int curImage;

  public BoardElement(PneumaticElement e)
   {modelType=e.getModelType();
    modelName=e.getModelName();
    name=e.getName();
    angle=e.getAngle();
    location=new Point(e.getX(),e.getY());
    curImage=e.getCurImage();
   }
 
  public String toString()
   {return modelType+" "+modelName+" "+name+" "+angle+" "+location.x+" "+location.y+" "+curImage;
   }
 }