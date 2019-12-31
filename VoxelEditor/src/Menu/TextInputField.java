package Menu;

import java.util.List;

import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Console.Parser;
import Input.InputHandler;
import Main.AssetLoaderManager;

public class TextInputField{
	
	String currentInput = "";
	
	TextField affectedField;
	
	String textureName = "Menu";
	String buttonName;
	
	Vector2f position = new Vector2f();
	Vector2f size = new Vector2f();
	
	Vector2f texPos = new Vector2f();
	Vector2f texSize = new Vector2f();
	
	boolean hasInputFocus = false;
	
	int characterlimit;
	
	Node parentNode;
	Node textFieldInputNode;
	
	public TextInputField(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight, TextField AffectTextField)
	{
		affectedField = AffectTextField;
		buttonName = (String) Arguments[0];
		
		position = new Vector2f(Float.parseFloat(Arguments[1].toString())*ScreenWidth, Float.parseFloat(Arguments[2].toString())*ScreenHeight);
		size =  new Vector2f(Float.parseFloat(Arguments[3].toString())*ScreenWidth, Float.parseFloat(Arguments[4].toString())*ScreenHeight);
		
		texPos = new Vector2f(Float.parseFloat(Arguments[5].toString()), Float.parseFloat(Arguments[6].toString()));
		texSize = new Vector2f(Float.parseFloat(Arguments[7].toString()), Float.parseFloat(Arguments[8].toString()));
		
		parentNode = ParentNode;
		textFieldInputNode = new Node(buttonName);
		
		characterlimit = (int) (size.getX() / (size.getY()/2)) * 2;
		
	}
	
	public boolean hasFocus(Vector2f MousePos)
	{
		Vector3f dir = new Vector3f(0,0,1);
		Vector3f mousePos = new Vector3f(MousePos.getX(),MousePos.getY(), 1);
		Ray ray = new Ray(mousePos,dir);
		CollisionResults results = new CollisionResults();
		
		parentNode.getChild(buttonName).collideWith(ray, results);
		if(results.size() > 0)
		{	
			return true;
		}
		return false;
	}

	private void addCharacter(String Input)
	{
		if(currentInput.length() < characterlimit)
		{
			currentInput += Input;
		}
	}
	
	private void spaceBar()
	{
		if(currentInput.length() < characterlimit)
		{
			currentInput += " ";
		}
	}
	
	private void backSpace()
	{
		if(currentInput.length() > 0)
		{
			currentInput = currentInput.substring(0, currentInput.length()-1);
		}
	}
	
	private void submitInput()
	{
		affectedField.updateText(currentInput);
		Parser.parseString(currentInput);
		currentInput = "";
	}
	
	public void update()
	{
		if(hasFocus(position))
		{
			List<String> pressedKeysNames = (List<String>) InputHandler.getTextInput();
			
			if(pressedKeysNames.size() > 0)
			{
				for(int i = 0; i < pressedKeysNames.size(); i++)
				{
					if(pressedKeysNames.get(i).equals("space"))
					{
						spaceBar();
						attachGeometry();
					}
					else if(pressedKeysNames.get(i).equals("back"))
					{
						backSpace();
						attachGeometry();
					}
					else if(pressedKeysNames.get(i).equals("return"))
					{
						submitInput();
						attachGeometry();
					}
					else if(pressedKeysNames.get(i).length() == 1)
					{
						addCharacter(pressedKeysNames.get(i));
						attachGeometry();
					}
					else
					{
						
					}
				}
			}
		}
	}
	
	public void attachGeometry()
	{
		if(textFieldInputNode != null)
		{
			textFieldInputNode.detachAllChildren();
		}
		textFieldInputNode = new Node("TextFieldNode");
		
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		BitmapText textfield = new BitmapText(font, false);
		
		int fontSize = (int) (size.getY()/2);
		textfield = new BitmapText(font, false);
		textfield.setSize(fontSize - fontSize/10);
		textfield.setText(currentInput);
		textfield.setColor(ColorRGBA.Black);
		textfield.setLocalTranslation(position.getX() + fontSize, position.getY() + size.getY()/2 + fontSize/2, 0.1f);
		textFieldInputNode.attachChild(textfield);
		
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
		someMaterial = AssetLoaderManager.getMaterial("default");
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("menu"));
		someGeometry.setMaterial(someMaterial);
		
		textFieldInputNode.attachChild(someGeometry);
		parentNode.attachChild(textFieldInputNode);
		
	}
	
}
