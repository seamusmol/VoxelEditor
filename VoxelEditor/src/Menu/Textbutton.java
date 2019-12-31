package Menu;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class Textbutton extends Button{

	String text = "";
	Vector2f textSize = new Vector2f();
	
	public Textbutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		text = Arguments[9].toString();
		textSize = new Vector2f(size.getX()*0.75f, size.getY()*0.75f);
		updateText(text);
	}

	public void updateText(String Value)
	{
		text = Value;
		attachButton();
	}
	
	@Override
	public void attachButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), 1);
		coords[1] = new Vector3f(position.getX(), position.getY(), 1);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), 1);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), 1);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX() + padding, 1 - texPos.getY() - padding);
		texcoords[1] = new Vector2f(texPos.getX() + padding, 1 - texPos.getY() - texSize.getY() + padding);
		texcoords[2] = new Vector2f(texPos.getX() + texSize.getX() - padding, 1 - texPos.getY()  - texSize.getY() + padding);
		texcoords[3] = new Vector2f(texPos.getX() + texSize.getX() - padding, 1 - texPos.getY()  - padding);
		
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
		someMaterial = AssetLoaderManager.getMaterial("button");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		someGeometry.setMaterial(someMaterial);
		
		if(buttonNode.getChild(buttonName) != null)
		{
			buttonNode.detachChildNamed(buttonName);
		}
		if(buttonNode.getChild(buttonName+"text") != null)
		{
			buttonNode.detachChildNamed(buttonName+"text");
		}
		if(text == null)
		{
			text = "";
			textSize = new Vector2f();
		}
		
		Vector2f adjustedTextSize = new Vector2f(textSize.getX()/text.length(), textSize.getY());
		
		Geometry geom = TextCreator.createText(position, adjustedTextSize, text, "font", buttonName+"text");
		buttonNode.attachChild(someGeometry);
		buttonNode.attachChild(geom);
	}
	
}
