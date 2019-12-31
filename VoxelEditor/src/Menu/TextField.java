package Menu;

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

import Main.AssetLoaderManager;

public class TextField {

	Node parentNode;
	Node textFieldNode;
	
	String textureName = "Menu";
	String buttonName;
	
	String[] lines = new String[10];
	
	Vector2f position = new Vector2f();
	Vector2f size = new Vector2f();
	
	Vector2f texPos = new Vector2f();
	Vector2f texSize = new Vector2f();
	
	/*
	 * String,float float, float float,float float, float float, Node
	 */
	public TextField(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		buttonName = (String) Arguments[0];
		
		position = new Vector2f(Float.parseFloat(Arguments[1].toString())*ScreenWidth, Float.parseFloat(Arguments[2].toString())*ScreenHeight);
		size =  new Vector2f(Float.parseFloat(Arguments[3].toString())*ScreenWidth, Float.parseFloat(Arguments[4].toString())*ScreenHeight);
		
		texPos = new Vector2f(Float.parseFloat(Arguments[5].toString()), Float.parseFloat(Arguments[6].toString()));
		texSize = new Vector2f(Float.parseFloat(Arguments[7].toString()), Float.parseFloat(Arguments[8].toString()));
		
		parentNode = ParentNode;
		
		for(int i = 0; i < lines.length; i++)
		{
			lines[i] = "";
		}
	}
	
	public void updateText(String NewLine)
	{
		if(!NewLine.equals(""))
		{
			String[] newLines = new String[10];
			for(int i = 1; i < lines.length; i++)
			{
				newLines[i-1] = lines[i];
			}
			newLines[9] = NewLine;
			lines = newLines;
			attachGeometry();
		}
	}
	
	public void update()
	{
		attachGeometry();
	}
	
	public void attachGeometry()
	{
		if(textFieldNode != null)
		{
			textFieldNode.detachAllChildren();
		}
		textFieldNode = new Node("TextFieldNode");
		
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText[] textfield = new BitmapText[lines.length];
		
		for(int i = 0; i < lines.length; i++)
		{
			textfield[i] = new BitmapText(font, false);
			textfield[i].setSize(size.getY()/lines.length);
			textfield[i].setText(lines[i]);
			textfield[i].setColor(ColorRGBA.Black);
			textfield[i].setLocalTranslation(position.getX(), position.getY() + size.getY() - ((size.getY()/10)*(i)), 0.1f);
			textFieldNode.attachChild(textfield[i]);
		}
		
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(position.getX(), position.getY() + size.getY(), 0);
		coords[1] = new Vector3f(position.getX(), position.getY(), 0);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), 0);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), 0);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX() + padding, 1 - texPos.getY() - padding);
		texcoords[1] = new Vector2f(texPos.getX() + padding, 1 - texPos.getY() - texSize.getY() + padding);
		texcoords[2] = new Vector2f(texPos.getX() + texSize.getX() - padding, 1 - texPos.getY() - texSize.getY()  + padding);
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
		someMaterial = AssetLoaderManager.getMaterial("default");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		someGeometry.setMaterial(someMaterial);
		
		textFieldNode.attachChild(someGeometry);
		parentNode.attachChild(textFieldNode);
	}
}
