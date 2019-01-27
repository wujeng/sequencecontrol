package tw.com.justiot.sequencecontrol.part;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Line
 {private static final int extrude=10;
  final static BasicStroke wideStroke = new BasicStroke(3.0f);
  final static BasicStroke basicStroke = new BasicStroke(1.0f);

  public static float pressureDrop=0.000f;
  public Port port1,port2;
  private ArrayList<Position> points=new ArrayList<Position>();
  private int length;
  public static int defaultlineinc=10;
  public int inc=defaultlineinc;
  private int curLength;
  private boolean first;
  public static boolean flowing;

  public Line()
   {
    port1=null;
    port2=null;
    curLength=0;
   }

  public void reset() {
	  curLength=0;
  }
  public void addPort(Port port)
   {if(port1==null)
     port1=port;
    else
     port2=port;
    port.setConnected(true);
   }

  public void deletePort(Port port)
   {if(port==null) return;
    if(port1==port) port1=null;
    else if(port2==port) port2=null;
    port.setConnected(false);
   } 
   
  public boolean hasPort(Port p)
   {if(port1!=null && port1==p) return true;
    if(port2!=null && port2==p) return true;
    return false;
   }

  public Port anotherPort(Port p)
   {if(port1!=null && port1==p) return port2;
    if(port2!=null && port2==p) return port1;
    return null;
   }

  private int closeto(int x1, int x2)
   {int abs=Math.abs(x1-x2);
    if(abs < extrude)
     {if(x2>x1) return abs/2;
      else return -(abs/2);
     }
    else
     {if(x2>x1) return extrude;
      else return -extrude;
     }
   }

  private int getXedge(Port port1,Port port2, int x)
   {int x1=port1.getOwner().getX();
    int x2=x1+port1.getOwner().getSize().width;
    int x3=port2.getOwner().getX();
    int x4=x3+port2.getOwner().getSize().width;
    int xmin=Math.min(x1,x3),xmax=Math.max(x2,x4);
    if((x3> x1 && x3 < x2) || (x4 > x1 && x4 < x2))
     {if(x4-x > x-x3) return xmin-extrude;
      else return xmax+extrude;
     }
    else return 0;
   }
    
  private int getYedge(Port port1,Port port2, int y)
   {int y1=port1.getOwner().getY();
    int y2=y1+port1.getOwner().getSize().height;
    int y3=port2.getOwner().getY();
    int y4=y3+port2.getOwner().getSize().height;
    int ymin=Math.min(y1,y3),ymax=Math.max(y2,y4);
    if((y3> y1 && y3 < y2) || (y4 > y1 && y4 < y2))
     {if(y4-y > y-y3) return ymin-extrude;
      else return ymax+extrude;
     }
    else return 0;
   }

  private int getXedge(Port port, int x)
   {int x1=port.getOwner().getX();
    int x2=x1+port.getOwner().getSize().width;
    if(x > x1 && x < x2)
     {if(x2-x > x-x1) return x1-extrude;
      else return x2+extrude;
     }
    else return 0;
   }
    
  private int getYedge(Port port, int y)
   {int y1=port.getOwner().getY();
    int y2=y1+port.getOwner().getSize().height;
    if(y > y1 && y < y2)
     {if(y2-y > y-y1) return y1-extrude;
      else return y2+extrude;
     }
    else return 0;
   }

  private void createPoints()
   {if(port1==null || port2==null) return;
    Port pt1=port1,pt2=port2;
    if(port1.getDir() < port2.getDir())
     {pt1=port2;pt2=port1;
      port1=pt1;
      port2=pt2;
     }
    points.clear();
    int rx1=pt1.getRealX(),ry1=pt1.getRealY();
    int rx2=pt2.getRealX(),ry2=pt2.getRealY();
    int xt=0,yt=0,ptemp=0;
    points.add(new Position(rx1,ry1));
    switch(pt1.getDir())
     {case Port.DIR_LEFT:
        switch(pt2.getDir())
         {case Port.DIR_LEFT:
            if(rx1 > rx2)
             yt=getYedge(pt2,ry1);
            else
             yt=getYedge(pt1,ry2);
            if(yt>0)
             {points.add(new Position(rx1-extrude,ry1));
              points.add(new Position(rx1-extrude,yt));
              points.add(new Position(rx2-extrude,yt));
              points.add(new Position(rx2-extrude,ry2));
             }
            else
             {if(ry1 > ry2)
               {if(rx1 > rx2)
                 {points.add(new Position(rx2-extrude,ry1));
                  points.add(new Position(rx2-extrude,ry2));
                 }
                else
                 {points.add(new Position(rx1-extrude,ry1));
                  points.add(new Position(rx1-extrude,ry2));
                 }
               }
              else
               {if(rx1 < rx2)
                 {points.add(new Position(rx1-extrude,ry1));
                  points.add(new Position(rx1-extrude,ry2));
                 }
                else
                 {points.add(new Position(rx2-extrude,ry1));
                  points.add(new Position(rx2-extrude,ry2));
                 }
               }
             }
            break;
          case Port.DIR_RIGHT:
            if(rx2 > rx1)
             {yt=getYedge(pt2,pt1,ry1); 
              if(yt==0)
               {if(ry1 > ry2)
                 yt=pt2.getOwner().getY()+pt2.getOwner().getSize().height+extrude;
                else
                 yt=pt1.getOwner().getY()+pt1.getOwner().getSize().height+extrude;
               }
              points.add(new Position(rx1-extrude,ry1));
              points.add(new Position(rx1-extrude,yt));
              points.add(new Position(rx2+extrude,yt));
              points.add(new Position(rx2+extrude,ry2));
             }
            else
             {if(ry1 > ry2)
               {points.add(new Position(rx1-extrude,ry1));
                points.add(new Position(rx1-extrude,ry2));
               }
              else
               {points.add(new Position(rx2+extrude,ry1));
                points.add(new Position(rx2+extrude,ry2));
               }
             }
            break;
          case Port.DIR_UP:
            if(ry1 > ry2)
             {ptemp=pt2.getOwner().getX();
              xt=ptemp+pt2.getOwner().getSize().width;              
              if(rx1 < xt+extrude)
               {points.add(new Position(ptemp-extrude,ry1));
                points.add(new Position(ptemp-extrude,ry2-extrude));
                points.add(new Position(rx2,ry2-extrude));
               }
              else
               {points.add(new Position(xt+extrude,ry1));
                points.add(new Position(xt+extrude,ry2-extrude));
                points.add(new Position(rx2,ry2-extrude));
               }
             }
            else
             {points.add(new Position(rx1-extrude,ry1));
              points.add(new Position(rx1-extrude,ry2-extrude));
              points.add(new Position(rx2,ry2-extrude));
             }
            break;
          case Port.DIR_DOWN:
            points.add(new Position(rx1-extrude,ry1));
            points.add(new Position(rx1-extrude,ry2+extrude));
            points.add(new Position(rx2,ry2+extrude));
            break;
         }
        break;

      case Port.DIR_RIGHT:
        switch(pt2.getDir())
         {case Port.DIR_RIGHT:
            if(rx1 < rx2)
             yt=getYedge(pt2,ry1);
            else
             yt=getYedge(pt1,ry2);
            if(yt>0)
             {points.add(new Position(rx1+extrude,ry1));
              points.add(new Position(rx1+extrude,yt));
              points.add(new Position(rx2+extrude,yt));
              points.add(new Position(rx2+extrude,ry2));
             }
            else
             {if(ry1 > ry2)
               {if(rx1 > rx2)
                 {points.add(new Position(rx1+extrude,ry1));
                  points.add(new Position(rx1+extrude,ry2));
                 }
                else
                 {points.add(new Position(rx2+extrude,ry1));
                  points.add(new Position(rx2+extrude,ry2));
                 }
               }
              else
               {if(rx1 > rx2)
                 {points.add(new Position(rx1+extrude,ry1));
                  points.add(new Position(rx1+extrude,ry2));
                 }
                else
                 {points.add(new Position(rx2+extrude,ry1));
                  points.add(new Position(rx2+extrude,ry2));
                 }
               }
             }
            break;
          case Port.DIR_UP:
            if(ry1 > ry2)
             {xt=getXedge(pt2,rx1);
              if(xt>0)
               {ptemp=pt1.getOwner().getY()-extrude;
                points.add(new Position(rx1+extrude,ry1));
                points.add(new Position(rx1+extrude,ptemp));
                points.add(new Position(xt,ptemp));
                points.add(new Position(xt,ry2-extrude));
                points.add(new Position(rx2,ry2-extrude));
               }
              else
               {if(rx1 > rx2)
                 xt=pt2.getOwner().getX()+pt2.getOwner().getSize().width+extrude;
                else
                 xt=pt2.getOwner().getX()-extrude;
                points.add(new Position(xt,ry1));
                points.add(new Position(xt,ry2-extrude));
                points.add(new Position(rx2,ry2-extrude));
               }
             }
            else
             {points.add(new Position(rx1+extrude,ry1));
              points.add(new Position(rx1+extrude,ry2-extrude));
              points.add(new Position(rx2,ry2-extrude));
             }
            break;
          case Port.DIR_DOWN:
            if(ry1 > ry2)
             {if(rx2 < rx1+extrude)
               {ptemp=pt1.getOwner().getY()-extrude;
                points.add(new Position(rx1+extrude,ry1));
                points.add(new Position(rx1+extrude,ptemp));
                points.add(new Position(rx2,ptemp));
               }
              else
               {points.add(new Position(rx2,ry1));}
             }
            else
             {points.add(new Position(rx1+extrude,ry1));
              points.add(new Position(rx1+extrude,ry2+extrude));
              points.add(new Position(rx2,ry2+extrude));
             }
            break;
         }
        break;

      case Port.DIR_UP:
        switch(pt2.getDir())
         {case Port.DIR_UP:
            if(ry1 > ry2)
             xt=getXedge(pt2,rx1);
            else
             xt=getXedge(pt1,rx2);
            if(xt>0)
             {points.add(new Position(rx1,ry1-extrude));
              points.add(new Position(xt,ry1-extrude));
              points.add(new Position(xt,ry2-extrude));
              points.add(new Position(rx2,ry2-extrude));
             }
            else
             {if(ry1 > ry2)
               {points.add(new Position(rx1,ry2-extrude));
                points.add(new Position(rx2,ry2-extrude));
               }
              else
               {points.add(new Position(rx1,ry1-extrude));
                points.add(new Position(rx2,ry1-extrude));
               }
             }
            break;
          case Port.DIR_DOWN:
            if(ry1<ry2)
             {if(rx2<rx1)
               ptemp=Math.max(pt1.getOwner().getX()+pt1.getOwner().getSize().width,
                        pt2.getOwner().getX()+pt2.getOwner().getSize().width)+extrude;
              else
               ptemp=Math.min(pt1.getOwner().getX(),
                              pt2.getOwner().getX())-extrude;
              points.add(new Position(rx1,ry1-extrude));
              points.add(new Position(ptemp,ry1-extrude));
              points.add(new Position(ptemp,ry2+extrude));
              points.add(new Position(rx2,ry2+extrude));
             }
            else
             {points.add(new Position(rx1,ry2+extrude));
              points.add(new Position(rx2,ry2+extrude));
             }
            break;
         }
        break;

      case Port.DIR_DOWN:
        switch(pt2.getDir())
         {case Port.DIR_DOWN:
            if(ry1 > ry2)
             xt=getXedge(pt1,rx2);
            else
             xt=getXedge(pt2,rx1);
            if(xt>0)
             {points.add(new Position(rx1,ry1+extrude));
              points.add(new Position(xt,ry1+extrude));
              points.add(new Position(xt,ry2+extrude));
              points.add(new Position(rx2,ry2+extrude));
             }
            else
             {if(ry1 > ry2)
               {points.add(new Position(rx1,ry1+extrude));
                points.add(new Position(rx2,ry1+extrude));
               }
              else
               {points.add(new Position(rx1,ry2+extrude));
                points.add(new Position(rx2,ry2+extrude));
               }
             }
            break;
         }
        break;
     }
    points.add(new Position(rx2,ry2));

    int leng=0;
    int seg=0;
    Position old=(Position) points.get(0);
    Position newpos=null;
    for(int i=0;i<points.size()-1;i++)
     {newpos=(Position) points.get(i+1);
      if(old.x==newpos.x) seg=Math.abs(old.y-newpos.y);
      else seg=Math.abs(old.x-newpos.x);
      leng=leng+seg;
      old=newpos;
     }
    length=leng;
   }

  public void delete()
   {
    if(port1!=null) port1.setConnected(false);      
    if(port2!=null) port2.setConnected(false);      
    points.clear();
   }
  public void undelete()
   {
    if(port1!=null) port1.setConnected(true);      
    if(port2!=null) port2.setConnected(true); 
   } 
  

  private static final int delta=3;
  public boolean inLine(int x, int y)
   {
    Position pos1=(Position) points.get(0);
    Position pos2=null;
    int min=0,max=0;
    for(int i=1;i<points.size();i++)
     {pos2=(Position) points.get(i);
      if(pos1.x==pos2.x)
       {if(pos1.y>pos2.y) 
         {max=pos1.y;min=pos2.y;}
        else
         {max=pos2.y;min=pos1.y;}
        if(x>pos1.x-delta && x < pos1.x+delta && y>min && y<max) return true;
       }
      else
       {if(pos1.x>pos2.x)
         {max=pos1.x;min=pos2.x;}
        else
         {max=pos2.x;min=pos1.x;}
        if(y>pos1.y-delta && y<pos1.y+delta && x>min && x<max) return true;
       }
      pos1=pos2;
     }
    return false;
   }

  public void draw(Graphics g, boolean wide)
   {createPoints();
    if(points.size()==0) return;
    int len=0,seg=0,overi=0,midlen=0;
    if(first) midlen=curLength;
    else midlen=length-curLength;
    Position pos1=(Position) points.get(0);
    Position pos2=null;    
    for(int i=1;i<points.size();i++)
     {pos2=(Position) points.get(i);
      if(pos1.x==pos2.x) seg=Math.abs(pos1.y-pos2.y);
      else seg=Math.abs(pos1.x-pos2.x);
      len=len+seg;
      if(len > midlen)
       {overi=i;break;}
      pos1=pos2;
     }
    if(midlen<0) overi=points.size();

    Graphics2D g2=(Graphics2D) g;
    Color color0=g2.getColor();
    if(wide) g2.setStroke(wideStroke);
    if(first) 
     g2.setColor(Color.pink);
    else 
     g2.setColor(Color.gray);
    pos1=(Position) points.get(0);
    pos2=null;
    for(int i=1;i<points.size();i++)
     {pos2=(Position) points.get(i);
      if(i<overi)
       {g2.drawLine(pos1.x,pos1.y,pos2.x,pos2.y);
       }
      else if(i==overi)
       {int temp=0;
        if(pos1.x==pos2.x)
         {if(pos1.y>pos2.y)
           temp=pos2.y+(len-midlen);
          else
           temp=pos2.y-(len-midlen);
          g2.drawLine(pos1.x,pos1.y,pos1.x,temp);
          if(first) g2.setColor(Color.gray);
          else g2.setColor(Color.pink);
          g2.drawLine(pos1.x,temp,pos2.x,pos2.y);
         }
        else
         {if(pos1.x>pos2.x)
           temp=pos2.x+(len-midlen);
          else
           temp=pos2.x-(len-midlen);
          g2.drawLine(pos1.x,pos1.y,temp,pos1.y);
          if(first) g2.setColor(Color.gray);
          else g2.setColor(Color.pink);
          g2.drawLine(temp,pos1.y,pos2.x,pos2.y);
         }
       }
      else
       {g2.drawLine(pos1.x,pos1.y,pos2.x,pos2.y);
       }
      pos1=pos2;
     }
    if(wide) g2.setStroke(basicStroke);
    g2.setColor(color0);
   }

  public Port getPort1() {return port1;}
  public Port getPort2() {return port2;}
  public int getLength() {return length;}

  private static float minpositive(float f1, float f2) {
		if(f1>0.0f && f2>0.0f) return Math.min(f1, f2);
		else {
		  if(f1<=0.0f) return f2;
		  else if(f2<=0.0f) return f1;
		}
		return -1.0f;
	  }
  private static float minPressure(float f1, float f2) {
		if(f1>0.0f && f2>0.0f) return Math.min(f1, f2);
		else {
		  if(f1==0.0f && f2>=0.0f) return f2;
		  if(f2==0.0f && f1>=0.0f) return f1;
		  float max=Math.max(f1,f2);
		  if(max>=0.0f) return max;
		}
		return -1.0f;
	  }
  
  private static boolean checkStop(Port p1, Port p2) {
	  if( p1.getIsStop() || p2.getIsStop()) {	
		  p1.setFlowrate(0.0f);
		  p2.setFlowrate(0.0f);
		  float f1=p1.getPressure();
		    float f2=p2.getPressure();
		    float minp=minPressure(f1,f2);   
		    if(minp<0.0f) return true;   
		    p1.setPressure(minp);
			p2.setPressure(minp);
		  return true;
	  }
	 return false;	  
  }
  private static boolean checkDrain(Port p1, Port p2, boolean setflowrate) {
	  if(p1.getIsDrain() || p2.getIsDrain()) {
		  p1.setPressure(0.0f);
		  p2.setPressure(0.0f);
		if(setflowrate) {
		  float min=minpositive(p1.getFlowrate(), p2.getFlowrate());
		  if(min>0.0f) {
			p1.setFlowrate(min);
		    p2.setFlowrate(min);		  
		  }
		}
		  return true;
	  }
	 return false;	  
  }
  private static boolean isModelType(Port p, String type) {
	if(p.getOwner().getModelType().equals(type)) return true;
	return false;
  }
  private static boolean checkPower(Port p1, Port p2, boolean setflowrate) {
	    float f1=p1.getPressure();
	    float f2=p2.getPressure();
	    float minp=minPressure(f1,f2);   
	    if(minp<0.0f) return true;   
	    p1.setPressure(minp);
		p2.setPressure(minp);
		      if(setflowrate) {
			      float min=minpositive(p1.getFlowrate(), p2.getFlowrate());
			      if(min>0.0f) {
			        p2.setFlowrate(min);
			        p1.setFlowrate(min);
			      }
		      }
	    return true;
  }
  public static void connect(Port p1, Port p2, boolean setflowrate) {
	  if(checkDrain(p1,p2,setflowrate)) return;
	  if(checkStop(p1,p2)) return;
	  checkPower(p1,p2,setflowrate);
	  return;
  }
  public static void connect(Port p1, Port p2) {
	  connect(p1, p2, true);
  }
  
  public void mark() {
	  inc=defaultlineinc;
	  if(port1.getFlowrate()==0.0f && port2.getFlowrate()==0.0f && port1.getPressure()<=0.0f && port2.getPressure()<=0.0f) curLength=0; 
  }
  public void check()
   {if(port1.getIsDrain() || port2.getIsDrain()) {
	   connect(port1, port2);
	   curLength=0;
    } else if(port1.getIsStop() && port2.getIsStop()) {
	   if(curLength<length) {
		 port1.setPressure(0.0f);
		 port2.setPressure(0.0f);
		 curLength=0;
	   } else {
		 connect(port1, port2);
		 if(port1.getPressure()>0.0f) curLength=length;
		 else curLength=0;
	   }
    } else {
    	if(port1.getPressure()<=0.0f && port2.getPressure()<=0.0f) return;
       Port ptstart=port1,ptend=port2;first=true;
       if(port1.getPressure() < port2.getPressure())
        {ptstart=port2;ptend=port1;first=false;} 
       if(curLength< length) {
    	   curLength=curLength+inc;
    	   flowing=true;
       } else {
    	   connect(port1, port2);
       }
       if(port1.getFlowrate()==0.0f && port2.getFlowrate()==0.0f && port1.getPressure()<=0.0f && port2.getPressure()<=0.0f) curLength=0;
       inc=0;
    }
	
    return;
   }
 }