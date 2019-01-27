package tw.com.justiot.sequencecontrol.config;

import tw.com.justiot.sequencecontrol.*;
import tw.com.justiot.sequencecontrol.config.*;
import tw.com.justiot.sequencecontrol.dialog.*;
import tw.com.justiot.sequencecontrol.panel.*;
import tw.com.justiot.sequencecontrol.part.*;
import tw.com.justiot.sequencecontrol.pelement.*;

import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;

public class ValveParameter extends ElementParameter
 {public static final int FORCE_FLUID=0;
  public static final int FORCE_MAN=1;
  public static final int FORCE_MECHANIC=2;
  public static final int FORCE_ELECTRIC=3;

  public int squareNumber,forceNumber,forceType;
  public boolean exclusiveForce;
  public Port pport,rport,sport,aport,bport,yport,zport;
  public String[] connections;
  public Position[] forcePos;
  public boolean oneWay;
  public ValveParameter(String mname,String tip,int riwidth,int riheight,String rifile,
                             int iwidth,int iheight,String ifile, int imgnumber,
int squarenum, int forcenum, int ftype, boolean exforce, Position[] pos5, String[] connect, Position[] fpos, boolean oway)
   {super("Valve",mname,tip,riwidth,riheight,rifile,iwidth,iheight,ifile,imgnumber);
    squareNumber=squarenum;
    forceNumber=forcenum;
    forceType=ftype;
    exclusiveForce=exforce;
    forcePos=fpos;
    connections=connect;
    oneWay=oway;
    for(int i=0;i<connections.length;i++)
     connections[i]=connections[i].toLowerCase();
    if(pos5[0]!=null) pport=new Port(pos5[0]); else pport=null;
    if(pos5[1]!=null) rport=new Port(pos5[1]); else rport=null;
    if(pos5[2]!=null) sport=new Port(pos5[2]); else sport=null;
    if(pos5[3]!=null) aport=new Port(pos5[3]); else aport=null;
    if(pos5[4]!=null) bport=new Port(pos5[4]); else bport=null;

    if(connections[0].indexOf("p")<0) {pport=null;java.lang.System.err.println("no P port?");}
    if(connections[0].indexOf("r")<0) rport=null;
    if(connections[0].indexOf("s")<0) sport=null;
    if(connections[0].indexOf("a")<0) {aport=null;java.lang.System.err.println("no A port?");}
    if(connections[0].indexOf("b")<0) bport=null;    

   }
 }
