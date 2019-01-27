package tw.com.justiot.sequencecontrol;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import java.io.*;

public abstract class Command
 {protected Object source;
  protected int id;
  protected String className;
  public Command(String cn,Object s,int id)
   {className=cn;
	source=s;
    this.id=id;
   }
  public abstract void undo();
  public abstract void redo();
  public Object getSource() {return source;}
  public int getId() {return id;}
  public String getClassName() {return className;}
 }