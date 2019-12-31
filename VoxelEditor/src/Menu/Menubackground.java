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

public class Menubackground extends Button{

	Vector2f position = new Vector2f();
	Vector2f size = new Vector2f();

	Vector2f texPos = new Vector2f();
	Vector2f texSize = new Vector2f();
	
	Node parentNode;
	
	String textureName = "";
	String buttonName;
	
	public Menubackground(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight)
	{
		super(Arguments, ParentNode, ScreenHeight, ScreenHeight);
		
		textureName = Arguments[9].toString();
		
		attachButton();
		
		parentNode = ParentNode;
	}
	
	public void attachButton()
	{
		Vector3f[] coords = new Vector3f[4];
		Vector2f[] texcoords = new Vector2f[4];
		int[] indexes = new int[6];
		
		coords[0] = new Vector3f(0, 0, 1);
		coords[1] = new Vector3f(0, 0, 1);
		coords[2] = new Vector3f(position.getX() + size.getX(), position.getY(), 1);
		coords[3] = new Vector3f(position.getX() + size.getX(), position.getY() + size.getY(), 1);
		
		float padding = 0.001f;
		texcoords[0] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - padding);
		texcoords[1] = new Vector2f(texPos.getX()* 0.0625f + padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[2] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - texSize.getY() * 0.0625f + padding);
		texcoords[3] = new Vector2f(texPos.getX()* 0.0625f + texSize.getX() * 0.0625f - padding, 1 - texPos.getY() * 0.0625f - padding);
		
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
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(textureName));
		someGeometry.setMaterial(someMaterial);
		
		parentNode.attachChild(someGeometry);
	}
	
}
