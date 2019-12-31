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

public class Displaybutton extends Button {

	
	String line = "";
	
	public Displaybutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
	}
	
	public void updateText(String NewLine)
	{
		if(!NewLine.equals(""))
		{
			line = NewLine;
		}
		else
		{
			line = "X";
		}
		attachButton();
	}
	
	@Override
	public void attachButton()
	{
		BitmapFont font = AssetLoaderManager.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
		
		BitmapText textfield = new BitmapText(font, false);
		textfield.setSize(size.getY()*0.5f);
		textfield.setText(line);
		textfield.setColor(ColorRGBA.Red);
		textfield.setLocalTranslation(position.getX() + size.getX()*0.0625f, position.getY() + size.getY()*0.875f, 0.1f);
		textfield.attachChild(textfield);
		
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
		
		if(buttonNode.getChildren().size() > 0)
		{
//			buttonNode.detachChildNamed(buttonName);
			buttonNode.detachAllChildren();
		}
		buttonNode.attachChild(textfield);
		buttonNode.attachChild(someGeometry);
	}
	
}
