package tw.com.justiot.sequencecontrol;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ImageIcon;

public class util {
	
	public static AudioClip loadSound(String file)
    {
	 if(!Config.getBoolean("loadsound")) return null;
	 AudioClip clip=null;
     if(Config.getBoolean("debug")) System.out.println(Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+file);
     try
      {
//	  clip=Applet.newAudioClip(getClass().getResource("/resources/"+file));
    	URL url=new File(Paths.get("").toAbsolutePath().toString()+File.separator+"resources"+File.separator+file).toURI().toURL();
	    clip=Applet.newAudioClip(url);
	   }
     catch (Exception e)
      {java.lang.System.err.println("Please verify your imageURL.:"+file);
       e.printStackTrace();
      }
     return clip;
    }
    
   public static Image[] loadImages(String mtype,String mname,String file, int number) 
    {Image[] images=null;
     String tail=null;
     String head=null;
     int ind=file.indexOf(".");
     if(ind<0) 
      {head=file;
       tail=".gif";
      }
     else
      {head=file.substring(0,ind);
       tail=file.substring(ind,file.length());
      }
     images=new Image[number];
     for(int i=0;i<number;i++)
      {String path=File.separator+"resources"+File.separator+head+Integer.toString(i+1)+tail;
       images[i]=loadImage(mtype,mname,"images"+i,path);
      }
     return images;
    }
	
   public static ImageIcon loadImageIcon(String pa) 
	{return loadImageIcon(pa,"");
	}  
	  
   public static ImageIcon loadImageIcon(String path,String description) 
	{String s = Paths.get("").toAbsolutePath().toString()+path;
	   java.net.URL imgURL=null;
	   try {
		  imgURL= new File(s).toURI().toURL();
	   } catch(Exception e) {
		   e.printStackTrace();
		   return null;
	   }
	   
	 if(imgURL!=null) return new ImageIcon(imgURL,description);
	 else 
	  {
	   System.err.println("loadImageIcon: Couldn't find file: " + path);
	   return null;
	  }
	}
  
  public static Image loadImage(String mtype,String mname,String var,String path) 
  {
   Image img=(Image) ImageMap.getImage(mtype, mname, var);
   if(img!=null) return img;
	//  java.net.URL imgURL = PneumaticsCAD.class.getResource(path);
   java.net.URL imgURL=null;
   try {
	  imgURL= new File(Paths.get("").toAbsolutePath().toString()+path).toURI().toURL();
   } catch(Exception e) {
	   System.err.println("loadImage: Couldn't find file: " + path);
	   e.printStackTrace();
	   return null;
   }
   if(imgURL!=null) 
    {img=(new ImageIcon(imgURL, "")).getImage();
     if(img!=null) ImageMap.putImage(mtype, mname, var,img);
     else {System.err.println("Image not found: "+mtype+" "+mname+" "+var);}
	 return img;
    }
   else 
    {
     System.err.println("loadImage: Couldn't find file: " + path);
     return null;
    }
  }
  
  
public static Image loadImage(String path) 
 {
	String s = Paths.get("").toAbsolutePath().toString()+path;
	   java.net.URL imgURL=null;
	   try {
		  imgURL= new File(s).toURI().toURL();
	   } catch(Exception e) {
		   System.err.println("loadImage3: Couldn't find file: " + path);
		   e.printStackTrace();
		   return null;
	   }
	
//	java.net.URL imgURL = util.class.getResource(path);
  if(imgURL!=null) return (new ImageIcon(imgURL)).getImage();
  else 
   {
    System.err.println("loadImage3: Couldn't find file: " + path);
    return null;
   }
 }
}
