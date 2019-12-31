package Menu;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

import Input.InputHandler;
import Main.AssetLoaderManager;

public class Crosshair extends AbstractAppState
{
	private static boolean isLocked = false;
	private static boolean isVisible = true;
	private static Vector2f position;
	private static Vector2f size;
	private static Node parentNode;
	private static Camera cam;
	
	@Override
	public void initialize(AppStateManager StateManager, Application Application)
	{
		SimpleApplication app = (SimpleApplication) Application;
		parentNode = app.getGuiNode();
		cam = app.getCamera();
		size = new Vector2f(100,100);
		position = new Vector2f(Application.getCamera().getWidth()/2-size.x/2, Application.getCamera().getHeight()/2-size.y/2);
		
//		AmbientLight someLight = new AmbientLight();
//		someLight.setColor(ColorRGBA.White);
//		
//		parentNode.addLight(someLight);
		attachCursor();
	}
	
	@Override
	public void update(float tpf) 
	{
		if(!isLocked)
		{
			if(InputHandler.hasInput("mouse_up"))
			{
				mouse_up(InputHandler.getKeyValue("mouse_up"));
			}
			if(InputHandler.hasInput("mouse_down"))
			{
				mouse_down(InputHandler.getKeyValue("mouse_down"));
			}
			if(InputHandler.hasInput("mouse_left"))
			{
				mouse_left(InputHandler.getKeyValue("mouse_left"));
			}
			if(InputHandler.hasInput("mouse_right"))
			{
				mouse_right(InputHandler.getKeyValue("mouse_right"));
			}
		}
	}
	
	private static void movemouse(Vector2f value)
	{
		if(!isLocked)
		{
			position.addLocal(value);
			
			if(position.getX() > cam.getWidth() - size.getX())
			{
				position.setX(cam.getWidth() - size.getX());
			}
			if(position.getY() > cam.getHeight() - size.getY())
			{
				position.setY(cam.getHeight() - size.getY());
			}
			if(position.getX() < 0)
			{
				position.setX(0);
			}
			if(position.getY() < 0)
			{
				position.setY(0);
			}
			attachCursor();
		}
	}
	
	private static void mouse_up(float Value)
	{
		movemouse( new Vector2f(0, Value * cam.getHeight()));
	}
	private static void mouse_down(float Value)
	{
		movemouse(new Vector2f(0, -Value * cam.getHeight()));
	}
	private static void mouse_left(float Value)
	{
		movemouse(new Vector2f(-Value * cam.getWidth(), 0));
	}
	private static void mouse_right(float Value)
	{
		movemouse(new Vector2f(Value * cam.getWidth(), 0));
	}
	
	private static void attachCursor()
	{
//		Vector3f[] coords = new Vector3f[4];
//		Vector2f[] texcoords = new Vector2f[4];
//		int[] indexes = new int[6];
//		
//		coords[0] = new Vector3f(position.getX(), position.getY(),1);
//		coords[1] = new Vector3f(position.getX() + size.getX() , position.getY(),1);
//		coords[2] = new Vector3f(position.getX(), position.getY() + size.getY(),1);
//		coords[3] = new Vector3f(position.getX() + size.getX(),position.getY() + size.getY(),1);
//	
//		float padding = 0.001f;
//		texcoords[0] = new Vector2f(0,0);
//		texcoords[1] = new Vector2f(1,0);
//		texcoords[2] = new Vector2f(0,1);
//		texcoords[3] = new Vector2f(1,1);
//		
//		indexes[0] = 2;
//		indexes[1] = 0;
//		indexes[2] = 1;
//		indexes[3] = 1;
//		indexes[4] = 3;
//		indexes[5] = 2;
//		
//		Mesh someMesh = new Mesh();
//		Geometry someGeometry = new Geometry("crosshair", someMesh);
//		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
//		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
//		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
//		someMesh.updateBound();
//		
//		Material mat = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//		mat.setTexture("ColorMap", AssetLoaderManager.getTexture("crosshair"));
//		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//		someGeometry.setQueueBucket(Bucket.Transparent);  
//		someGeometry.setMaterial(mat);
		
		Picture pic = new Picture("crosshair");
		pic.setTexture(AssetLoaderManager.getAssetManager(), (Texture2D) AssetLoaderManager.getTexture("crosshair"),true);
		pic.setWidth(size.x);
		pic.setHeight(size.y);
		pic.setPosition(position.getX(), position.getY());
		
		if(parentNode.getChild("crosshair") != null)
		{
			parentNode.detachChildNamed("crosshair");
		}
//		someGeometry.setLocalTranslation(someGeometry.getWorldTranslation().getX(),someGeometry.getWorldTranslation().getY(), 1);
//		parentNode.attachChild(someGeometry);
		
		parentNode.attachChild(pic);
	}
	
	public static void changeToGamePlayMode()
	{
		isVisible = true;
		isLocked = true;
		position = new Vector2f(cam.getWidth()/2 - size.getX()/2, cam.getHeight()/2 - size.getY()/2);
		
		attachCursor();
	}
	
	public static void changeToMenuMode()
	{
		isVisible = true;
		isLocked = false;
		position = new Vector2f(cam.getWidth()/2 - size.getX()/2, cam.getHeight()/2 - size.getY()/2);
		attachCursor();
	}
	
	public static Vector2f getPosition()
	{
		return position;
	}
	
	public static void setPosition(Vector2f Position)
	{
		position = Position;
	}
	
	public static Vector2f getSize()
	{
		return size;
	}
	
	public static void setSize(Vector2f Size)
	{
		size = Size;
	}
	
	public static void setIsCrossHairLocked(boolean Value)
	{
		isLocked = Value;
	}
	
	public static void setIsVisible(Boolean IsVisible)
	{
		isVisible = IsVisible;
	}
	
	public static boolean isVisible()
	{
		return isVisible;
	}
	
	
}
