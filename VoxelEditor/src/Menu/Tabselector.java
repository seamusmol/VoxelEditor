package Menu;

import java.util.ArrayList;
import java.util.List;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Console.Parser;
import Main.AssetLoaderManager;

public class Tabselector extends Button{

	List<String> tabNames = new ArrayList<String>();
	
	int x = 0;
	
	private int sx = 0;
	private int curx = 0;
	private String othercommandLine = "";
	
	public Tabselector(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		sx = Integer.parseInt((String)Arguments[9]);
		commandLine+=(String)Arguments[10];
		othercommandLine+=(String)Arguments[11];
		attachButton();
	}

	public void setTabNames(List<String> NewNames)
	{
		if(tabNames.size() < NewNames.size())
		{
			x = NewNames.size()-1;
		}
		tabNames = NewNames;
		curx = NewNames.size();
		attachButton();
	}
	
	@Override
	public boolean onClick(Vector2f MousePos, boolean IsPrimary)
	{
		if(hasFocus(MousePos) == 1)
		{
			//set x,y,v
			int tempx = (int) (Math.round(MousePos.x - position.x)/size.x * sx);
			if(tempx < curx)
			{
				if(IsPrimary)
				{
					x = tempx;
					Parser.parseString(commandLine + " " + x);
				}
				else
				{
					x = tempx;
					Parser.parseString(othercommandLine + " " + x);
				}
				attachButton();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void attachButton()
	{
		if(buttonNode.getChildren().size() > 0)
		{
			buttonNode.detachChildNamed(buttonName);
			buttonNode.detachAllChildren();
		}
		
		buttonNode.attachChild(createBackground());
		for(int i = 0; i < curx; i++)
		{
			if(i == x)
			{
				buttonNode.attachChild(createActiveTab());
			}
			else
			{
				buttonNode.attachChild(createTabGeometry(i));
			}
			buttonNode.attachChild(createTabText(i));
		}
	}
	
	private Geometry createBackground()
	{
		Vector3f[] coords = new Vector3f[4];
		int[] indices = new int[6];
		
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), 0);
		coords[1] = new Vector3f(position.getX(), position.getY(), 0);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), 0);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), 0);
		
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		indices[3] = 2;
		indices[4] = 3;
		indices[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(buttonName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indices));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("default").clone();
		someMaterial.setColor("Color", ColorRGBA.DarkGray);
		someGeometry.setMaterial(someMaterial);
		
		return someGeometry;
	}
	
	private Geometry createTabGeometry(int X)
	{
		float sxx = size.x/sx;
		
		float px = sxx * X;
		
		Vector3f[] coords = new Vector3f[4];
		int[] indices = new int[6];
		
		float padding = size.x * 0.0025f;
		
		coords[0] = new Vector3f(position.x + padding,position.y - padding, 0.1f).add(px , size.y, 0.0f);
		coords[1] = new Vector3f(position.x + padding,position.y + padding, 0.1f).add(px , 0, 0.0f);
		coords[2] = new Vector3f(position.x - padding,position.y + padding, 0.1f).add(px + sxx, 0, 0.0f);
		coords[3] = new Vector3f(position.x - padding,position.y - padding, 0.1f).add(px + sxx, size.y, 0.0f);
		
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		indices[3] = 2;
		indices[4] = 3;
		indices[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(buttonName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indices));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("default").clone();
		someMaterial.setColor("Color", ColorRGBA.Gray);
		someGeometry.setMaterial(someMaterial);
		
		return someGeometry;
	}
	
	private BitmapText createTabText(int index)
	{
		String line = tabNames.get(index);
		
		int stringlength = (int)size.getY()/sx;
		float sxx = size.x/sx;
		
		float px = sxx * index;
		
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText textfield = new BitmapText(font, false);
		textfield.setSize(size.getY()*0.5f);
		textfield.setText(line);
		textfield.setColor(ColorRGBA.White);
//		textfield.setLocalTranslation(position.getX() + size.getX()*0.0625f, position.getY() + size.getY()*0.875f, 0.1f);
		textfield.setLocalTranslation(new Vector3f(position.x, position.y, 0.1f).add(px , size.y*0.75f, 0.2f));
		
		textfield.attachChild(textfield);
		
		return textfield;
	}
	
	private Geometry createActiveTab()
	{
		float sxx = size.x/sx;
		float syy = size.y;
		
		float px = sxx * x;
		
		Vector3f[] coords = new Vector3f[4];
		int[] indices = new int[6];
		
		float padding = size.x * 0.0025f;
		
		coords[0] = new Vector3f(position.x + padding,position.y - padding, 0.1f).add(px , syy, 0.0f);
		coords[1] = new Vector3f(position.x + padding,position.y + padding, 0.1f).add(px , 0, 0.0f);
		coords[2] = new Vector3f(position.x - padding,position.y + padding, 0.1f).add(px + sxx, 0, 0.0f);
		coords[3] = new Vector3f(position.x - padding,position.y - padding, 0.1f).add(px + sxx, syy, 0.0f);
		
		indices[0] = 0;
		indices[1] = 1;
		indices[2] = 2;
		indices[3] = 2;
		indices[4] = 3;
		indices[5] = 0;
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(buttonName, someMesh);
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(coords));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indices));
		someMesh.updateBound();
		someMaterial = AssetLoaderManager.getMaterial("default").clone();
		someMaterial.setColor("Color", ColorRGBA.LightGray);
		someGeometry.setMaterial(someMaterial);
		
		return someGeometry;
	}
	
}
