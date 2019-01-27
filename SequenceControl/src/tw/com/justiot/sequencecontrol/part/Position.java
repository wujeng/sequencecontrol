package tw.com.justiot.sequencecontrol.part;

public class Position
 {public int x;
  public int y;
  public Position()
   {x=0;y=0;}
  public Position(int x0, int y0)
   {x=x0;y=y0;}
  public Position(Position p)
   {this.x=p.x;
    this.y=p.y;
   }
 }