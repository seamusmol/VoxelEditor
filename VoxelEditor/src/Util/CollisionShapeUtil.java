package Util;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class CollisionShapeUtil {

	//Position = cube position
	/**
	 * @param P1 0,0
	 * @param P2 1,0
	 * @param P3 0,1
	 * @param P4 1,1
	 * @return
	 */
	public static Geometry generateCollisionQuad(Vector3f P1, Vector3f P2, Vector3f P3, Vector3f P4)
	{
		Vector3f [] vertices = new Vector3f[4];
		vertices[0] = new Vector3f(P1);
		vertices[1] = new Vector3f(P2);
		vertices[2] = new Vector3f(P3);
		vertices[3] = new Vector3f(P4);
		
		int [] indexes = { 1,0,2,2,3,1 };
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("CollisionQuad", someMesh);
		
		//server
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
		someMesh.updateBound();
		someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		//someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName));
		someMaterial.setColor("Color", ColorRGBA.Blue);
//		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		someGeometry.setMaterial(someMaterial);
				
		return someGeometry;
	}
	
}
