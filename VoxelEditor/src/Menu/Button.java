package Menu;


import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;


public class Button{
	
	Vector2f position = new Vector2f();
	Vector2f size = new Vector2f();

	Vector2f texPos = new Vector2f();
	Vector2f texSize = new Vector2f();
	
	Node parentNode;
	Node buttonNode;
	
	String buttonName;
	String commandLine = "";
	
	boolean hasFocus = false;
	
	/*
	 * newButton buttonType buttonName posx posy width height texX texY texwidth texheight
	 */
	public Button(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		buttonName = (String) Arguments[0];
		
		position = new Vector2f(Float.parseFloat(Arguments[1].toString())*ScreenWidth, Float.parseFloat(Arguments[2].toString())*ScreenHeight);
		size =  new Vector2f(Float.parseFloat(Arguments[3].toString())*ScreenWidth, Float.parseFloat(Arguments[4].toString())*ScreenHeight);
		
		texPos = new Vector2f(Float.parseFloat(Arguments[5].toString()), Float.parseFloat(Arguments[6].toString()));
		texSize = new Vector2f(Float.parseFloat(Arguments[7].toString()), Float.parseFloat(Arguments[8].toString()));
		
		parentNode = ParentNode;
		buttonNode = new Node(buttonName + "Node");
		parentNode.attachChild(buttonNode);
		attachButton();
	}
	
	public void destroy()
	{
		parentNode.detachChildNamed(buttonName);
		position = null;
		size = null;
		texPos = null;
		texSize = null;
		parentNode = null;
		buttonName = null;
		commandLine = null;
	}
	
	public void onFocus(Vector2f MousePos)
	{
		if(hasFocus(MousePos) == 1)
		{
			
		}
	}
	
	public boolean onClick(Vector2f MousePos, boolean IsPrimary)
	{
		if(hasFocus(MousePos) == 1)
		{
			
		}
		return false;
	}
	
	public void createButtonGeometry()
	{
		
	}
	
	public int hasFocus(Vector2f MousePos)
	{
		Vector3f dir = new Vector3f(0,0,-1);
		Vector3f mousePos = new Vector3f(MousePos.getX(),MousePos.getY(), 1);
		Ray ray = new Ray(mousePos,dir);
		CollisionResults results = new CollisionResults();
		
		buttonNode.getChild(buttonName).collideWith(ray, results);
		if(results.size() > 0)
		{	
			return 1;
		}
		return 0;
	}
	
	public void attachButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), 1.0f);
		coords[1] = new Vector3f(position.getX(), position.getY(), 1.0f);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), 1.0f);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), 1.0f);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX() + padding, 1 - texPos.getY() - padding);
		texcoords[1] = new Vector2f(texPos.getX() + padding, 1 - texPos.getY() - texSize.getY() + padding);
		texcoords[2] = new Vector2f(texPos.getX() + texSize.getX() - padding, 1 - texPos.getY() - texSize.getY() + padding);
		texcoords[3] = new Vector2f(texPos.getX() + texSize.getX() - padding, 1 - texPos.getY() - padding);
		
		indexes[0] = 0;
		indexes[1] = 1;
		indexes[2] = 2;
		indexes[3] = 2;
		indexes[4] = 3;
		indexes[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		
		Geometry someGeometry = new Geometry(buttonName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texcoords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("default").clone();
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		someGeometry.setMaterial(someMaterial);
		
		if(buttonNode.getChild(buttonName) != null)
		{
			buttonNode.detachChildNamed(buttonName);
		}
		buttonNode.attachChild(someGeometry);
	}
	
	/*
	public void updateGeometry()
	{
		Spatial someSpatial = (Geometry)parentNode.getChild(buttonName);
		Geometry someGeometry = (Geometry) someSpatial;
		someGeometry.getMaterial().setBoolean("onFocus", hasFocus);
		someGeometry.updateGeometricState();
	}
	*/
	
	public void setFocus(boolean Focus)
	{
		hasFocus = Focus;
	}
	
	public boolean getFocus()
	{
		return hasFocus;
	}
	
	public String getName()
	{
		return buttonName;
	}
	
}