package Main;

import java.lang.reflect.Field;

public class GUIConfiguration {

	//newbutton <buttontype> name x y w h tx ty tw th
	public static String[] guilayout = {
			
			"newButton actionbutton new 0.0 0.95 0.05 0.05 0.0 0.0625 0.0625 0.0625 generatenewvoxelmodel" ,
			"newButton actionbutton load 0.05 0.95 0.05 0.05 0.0 0.125 0.0625 0.0625 importmodel" ,
			"newButton actionbutton saveas 0.1 0.95 0.05 0.05 0.0 0.25 0.0625 0.0625 saveasmodel" ,
			"newButton actionbutton save 0.15 0.95 0.05 0.05 0.0 0.1875 0.0625 0.0625 savemodel" ,
			"newButton tabselector tabs 0.2 0.95 0.5 0.05 0.0 0.0 0.0625 0.0625 10 changemodel removemodel",
			
			"newButton tileselector2d tex 0.8 0.0 0.2 0.3 0.0625 0.0 0.0625 0.0625 32 32 changematerial",
			
			"newButton displaybutton diss 0.60 0.1 0.05 0.05 0.0 0.0 0.0625 0.0625" ,
			"newButton displaybutton disx 0.65 0.1 0.05 0.05 0.0 0.0 0.0625 0.0625" ,
			"newButton displaybutton disy 0.7 0.1 0.05 0.05 0.0 0.0 0.0625 0.0625" ,
			"newButton displaybutton disz 0.75 0.1 0.05 0.05 0.0 0.0 0.0625 0.0625" ,
			
			"newButton actionbutton ups 0.6 0.05 0.05 0.05 0.0 0.375 0.0625 0.0625 changescale 1 diss" ,
			"newButton actionbutton upx 0.65 0.05 0.05 0.05 0.0 0.375 0.0625 0.0625 changedimension x 1 disx" ,
			"newButton actionbutton upy 0.7 0.05 0.05 0.05 0.0 0.375 0.0625 0.0625 changedimension y 1 disy" ,
			"newButton actionbutton upz 0.75 0.05 0.05 0.05 0.0 0.375 0.0625 0.0625 changedimension z 1 disz" ,
			
			"newButton actionbutton downs 0.6 0.0 0.05 0.05 0.0 0.4375 0.0625 0.0625 changescale -1 diss" ,
			"newButton actionbutton downx 0.65 0.0 0.05 0.05 0.0 0.4375 0.0625 0.0625 changedimension x -1 disx" ,
			"newButton actionbutton downy 0.7 0.0 0.05 0.05 0.0 0.4375 0.0625 0.0625 changedimension y -1 disy" ,
			"newButton actionbutton downz 0.75 0.0 0.05 0.05 0.0 0.4375 0.0625 0.0625 changedimension z -1 disz",
			
	};
	
	public static String[] GetGUILayout(String LayoutName)
	{
		@SuppressWarnings("rawtypes")
		Class someClass;
		try 
		{
			someClass = Class.forName("Main.GUIConfiguration");
			Field[] fields = someClass.getFields();
    	
        	for(int i = 0; i < fields.length; i++)
            {
    			if(fields[i].getName().equals(LayoutName))
        		{
        			if(fields[i].getType().isAssignableFrom(String[].class))
	        		{
	        			return (String[]) fields[i].get(new String[0]);
	        		}
        		}
        	}
		}
		catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new String[0];
	}
	
}
