package tw.com.justiot.sequencecontrol;

import java.util.Hashtable;
import java.awt.Image;
public class ImageMap {
 private static Hashtable<String, Image> graphics=new Hashtable<String, Image>();
  /*
  public static boolean hasImage(String modelType,String modelName,String var)
	{
	 String key=modelType+"*"+modelName+"*"+var;
System.out.println(key);	 
	 Image img=(Image) graphics.get(key);
	 if(img==null) return false;
	 else return true;
	}
	*/
  public static void putImage(String modelType,String modelName,String var, Image img)
	{String key=modelType+"*"+modelName+"*"+var;
	 if(img==null) System.err.println("Image not found? "+key);
	 
	 if(modelType==null || modelType.length()==0 || 
			    modelName==null || modelName.length()==0 || 
			    var==null || var.length()==0 || img==null) return;
	 
//	 System.out.println("put:"+key);
	 graphics.put(key, img);
	}
  public static Image getImage(String modelType,String modelName,String var)
	{
	 String key=modelType+"*"+modelName+"*"+var;
//	 System.out.println(key);	
	 Image img=(Image) graphics.get(key);
	 return img;
	}
}
