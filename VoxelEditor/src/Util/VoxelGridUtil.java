package Util;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;

public class VoxelGridUtil {

	
	public static Geometry GenerateGrid(int X, int Z, float Scale)
	{
		List<Vector3f> positions = new ArrayList<Vector3f>();
		List<Integer> colorValues = new ArrayList<Integer>();
		
		int[][] in = new int[X][Z];
		int indexCount = 0;
		
		for(int i = 0; i < X; i++)
		{
			for(int j = 0; j < Z; j++)
			{
				positions.add(new Vector3f(i,0,j));
				colorValues.add(128);
				colorValues.add(128);
				colorValues.add(128);
				colorValues.add(128);
				in[i][j] = indexCount;
				indexCount++;
			}
		}
		
		int[] axisXIndices = new int[X];
		for(int i = 0; i < axisXIndices.length; i++)
		{
			positions.add(new Vector3f(i,0,0));
			
			colorValues.add(256);
			colorValues.add(0);
			colorValues.add(0);
			colorValues.add(0);
			
			axisXIndices[i] = indexCount;
			indexCount++;
		}
		
		int[] axisZIndices = new int[Z];
		for(int i = 0; i < axisZIndices.length; i++)
		{
			positions.add(new Vector3f(0,0,i));
			
			colorValues.add(0);
			colorValues.add(256);
			colorValues.add(0);
			colorValues.add(0);
			
			axisZIndices[i] = indexCount;
			indexCount++;
		}
		
		List<Integer> indices = new ArrayList<Integer>();
		
		for(int i = 0; i < X-1; i++)
		{
			for(int j = 0; j < Z-1; j++)
			{
				indices.add(in[i][j]);
				indices.add(in[i+1][j]);
				
				indices.add(in[i][j]);
				indices.add(in[i][j+1]);
			}
		}
		
		for(int i = 0; i < axisXIndices.length-1; i++)
		{
			indices.add(in[i][Z-1]);
			indices.add(in[i+1][Z-1]);
			
			indices.add(axisXIndices[i]);
			indices.add(axisXIndices[i+1]);
		}
		
		for(int i = 0; i < axisZIndices.length-1; i++)
		{
			indices.add(in[X-1][i]);
			indices.add(in[X-1][i+1]);
			
			indices.add(axisZIndices[i]);
			indices.add(axisZIndices[i+1]);
		}
		
		int[] colors = new int[colorValues.size()];
		for(int i = 0; i < colors.length; i++)
		{
			colors[i] = colorValues.get(i);
		}
		
		Vector3f[] positionArray = new Vector3f[positions.size()];
		for(int i = 0; i < positions.size(); i++)
		{
			positionArray[i] = positions.get(i);
		}
		
		int[] indexBufferArray = new int[indices.size()];
		for(int i = 0; i < indices.size(); i++)
		{
			indexBufferArray[i] = indices.get(i);
		}
		return GenerateGeometry(Scale, X, Z, positionArray, indexBufferArray, colors);
	}
	
	private static Geometry GenerateGeometry(float Scale, int X,int Z, Vector3f[] Position, int[] Indices, int[] Colors)
	{
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("Grid", someMesh);
		
		//client solid
		someMesh.setMode(Mesh.Mode.Lines);
		
		someMesh.setBuffer(Type.Position, 3,  BufferUtils.createFloatBuffer(Position));
		someMesh.setBuffer(Type.Color, 4, Colors);
		
//		someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(Indices));
		someMesh.updateBound();
		
		someMaterial = AssetLoaderManager.getMaterial("default").clone();
		someMaterial.setColor("Color", ColorRGBA.White);
		someMaterial.setBoolean("VertexColor", true);
		
		someGeometry.setMaterial(someMaterial);
		someGeometry.setLocalScale(Scale);
		
		return someGeometry;
	}
	
}
