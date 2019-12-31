package Main;

import java.awt.MouseInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

import Console.Parser;
import Input.InputHandler;
import Menu.Button;
import Menu.Tileselector2d;


/*
 * TODO
 * 
 * Organize argument order for Button
 */
public class UIManager extends AbstractAppState{

	private Camera cam;
	private Node menuNode;
	private Main main;
	
	private List<Button> activeButtons = new ArrayList<Button>();
	
	private long tickTime = 100;
	private long lastTick = 0;
	
	public UIManager(Parser ConsoleParser)
	{
		ConsoleParser.addAllowedClass(this, "Main.UIManager");
		ConsoleParser.addAllowedMethod("createmenu", "Main.UIManager");
		ConsoleParser.addAllowedMethod("newbutton", "Main.UIManager");
		ConsoleParser.addAllowedMethod("clearmenu", "Main.UIManager");
	}
	
	@Override
	public void initialize(AppStateManager StateManager, Application Application)
	{
		SimpleApplication app = (SimpleApplication) Application;
		
		super.initialize(StateManager, app);
		menuNode = new Node("MenuManagerNode");
		menuNode.setCullHint(CullHint.Never);
	    
		for(int i = 0; i < app.getGuiNode().getWorldLightList().size(); i++)
		{
			app.getGuiNode().removeLight(app.getGuiNode().getWorldLightList().get(i));
		}
		
	    app.getGuiNode().attachChild(menuNode);
	    cam = app.getCamera();
	    
	    app.getGuiNode().updateLogicalState(1);
	    app.getGuiNode().updateGeometricState();
	    	 
	    main = (Main) app;
	    this.setEnabled(true);
	}
	
	public Button getButtonByName(String ButtonName)
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			if(activeButtons.get(i).getName().equals(ButtonName))
			{
				return activeButtons.get(i);
			}
		}
		return null;
	}
	
	public void clearmenu()
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			activeButtons.get(i).destroy();
		}
		menuNode.detachAllChildren();
		activeButtons = new ArrayList<Button>();
	}
	
	public void createmenu(Object[] Value)
	{
		String menuName = (String)Value[0];
		if(menuName.length() > 0)
		{
			String[] buttonCommands = GUIConfiguration.GetGUILayout(menuName);
			if(buttonCommands.length > 0)
			{
				for(int i = 0; i < activeButtons.size(); i++)
				{
					activeButtons.get(i).destroy();
				}
				menuNode.detachAllChildren();
				activeButtons.clear();
				
				for(int i = 0; i < buttonCommands.length; i++)
				{
					Parser.parseString(buttonCommands[i]);
				}
			}
		}
	}
	
	public void newbutton(Object[] Value)
	{
		Object[] arguments = new Object[Value.length-1];
		for(int i = 0; i < arguments.length; i++)
		{
			arguments[i] = Value[i+1];
		}
		activeButtons.add( createButton(Value[0].toString(), arguments));
	}
	
	public Object[] getTileSelector2DValue(String PickerName)
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			if(activeButtons.get(i).getName().equals(PickerName))
			{
				if(activeButtons.get(i) instanceof Tileselector2d)
				{
					return ((Tileselector2d) activeButtons.get(i)).getValue();
				}
			}
		}
		return new Object[]{};
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Button createButton(String ButtonTypeName, Object[] Arguments)
	{
		Class someClass = null;
		try 
		{
			someClass = Class.forName("Menu." + ButtonTypeName.substring(0, 1).toUpperCase() + ButtonTypeName.substring(1));
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Constructor con = null;
		try 
		{
			//Object[].class, Node.class,float.class, float.class
			con = someClass.getConstructor(Object[].class, Node.class, float.class, float.class);
		} 
		catch (NoSuchMethodException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SecurityException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try 
		{
			//Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight
			return (Button)con.newInstance(Arguments, menuNode, cam.getWidth(), cam.getHeight());
		} 
		catch (InstantiationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void mouse_primary(int MSX, int MSY, boolean IsPrimary)
	{
		for(int i = 0; i < activeButtons.size(); i++)
		{
			if((System.nanoTime() - lastTick)/1000000 > tickTime)
			{
				if(activeButtons.get(i).onClick( new Vector2f(MSX,MSY), IsPrimary))
				{
					lastTick = System.nanoTime();
				}
			}
		}
	}
	
	public boolean hasGUICollision()
	{
		int msx = (int) MouseInfo.getPointerInfo().getLocation().getX();
		int msy = (int) MouseInfo.getPointerInfo().getLocation().getY();
		int minx = Display.getX();
		int miny = Display.getY();
		
//		int maxx = Display.getWidth() + minx;
		int maxy = Display.getHeight() + miny;
		
		Vector3f dir = new Vector3f(0,0,-1);
		Vector3f mousePos = new Vector3f(msx - minx, maxy - msy, 1);
		Ray ray = new Ray(mousePos,dir);
		CollisionResults results = new CollisionResults();
		main.getGuiNode().collideWith(ray, results);
		
		return results.size() > 0;
	}
	
	@Override
	public void update(float tpf) 
	{
		if(MouseInfo.getPointerInfo() == null)
		{
			return;
		}
		if(MouseInfo.getPointerInfo().getLocation() == null)
		{
			return;
		}
		
		int msx = (int) MouseInfo.getPointerInfo().getLocation().getX() - 4;
		int msy = (int) MouseInfo.getPointerInfo().getLocation().getY() - 24;
		int minx = Display.getX();
		int miny = Display.getY();
		
		int maxx = Display.getWidth() + minx;
		int maxy = Display.getHeight() + miny;
		
		if(msx < minx || msx > maxx || msy < miny || msy > maxy)
		{
			return;
		}
		
		if(InputHandler.hasInput("mouse_primary"))
		{
			mouse_primary(msx - minx, maxy - msy, true);
		}
		else if(InputHandler.hasInput("mouse_secondary"))
		{
			mouse_primary(msx - minx, maxy - msy, false);
		}	
	}
	
	public boolean hasMenu()
	{
		return activeButtons.size() > 0 && menuNode.getQuantity() > 0;
	}
	
}
