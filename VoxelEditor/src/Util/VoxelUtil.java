package Util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.util.BufferUtils;

import Main.AssetLoaderManager;
import Main.SettingsLibrary;
import VoxelModels.VoxelList;
import worldGen.Cell;

public class VoxelUtil
{
    public static Object[] GenerateVoxelCellList(boolean[][][] voxels, int[][][] materials, boolean splitFields)
    {
    	if(splitFields)
    	{
    		Object[] SortedFields = SplitFields(voxels,materials);
    		List<Cell> CellList = GenerateVoxelValues((boolean[][][]) SortedFields[0], (int[][][]) SortedFields[2]);
    		List<Cell> TransLucentCellList = GenerateVoxelValues((boolean[][][]) SortedFields[1], (int[][][]) SortedFields[3]);
    		Object[] Lists = {CellList,TransLucentCellList};
    		return Lists;
    	}
    	else
    	{
    		List<Cell> CellList = GenerateVoxelValues(voxels, materials);
    		Object[] List = {CellList};
    		return List;
    	}
    }
    
    public static Object[] SplitFields(boolean[][][] voxels, int[][][] materials)
    {
        boolean[][][] SolidVoxels = new boolean[voxels.length][ voxels[0].length][ voxels[0][0].length];
        boolean[][][] TranslucentVoxels = new boolean[voxels.length][ voxels[0].length][ voxels[0][0].length];

        int[][][] SolidMaterials = new int[materials.length][ materials[0].length][ materials[0][0].length];
        int[][][] TranslucentMaterials = new int[materials.length][ materials[0].length][ materials[0][0].length];

        for (int i = 0; i < voxels.length; i++)
        {
            for (int j = 0; j < voxels[0].length; j++)
            {
                for (int k = 0; k < voxels[0][0].length; k++)
                {
                	if(voxels[i][j][k])
                	{
                		if (materials[i][ j][ k] < 896)
	                    {
	                        SolidVoxels[i][ j][ k] = true;
	                        SolidMaterials[i][ j][ k] = materials[i][ j][ k];
	                    }
	                    else if(materials[i][ j][ k] >= 896)
	                    {
	                        TranslucentVoxels[i][ j][ k] = true;
	                        TranslucentMaterials[i][ j][ k] = materials[i][ j][ k];
	                    }
                	}
                }
            }
        }
        Object[] Data = new Object[4];
        Data[0] = SolidVoxels;
        Data[1] = TranslucentVoxels;
        Data[2] = SolidMaterials;
        Data[3] = TranslucentMaterials;
        return Data;
    }
    
    public static List<Cell> GenerateVoxelValues(boolean[][][] voxels, int[][][] materials)
    {
        List<Cell> cellList = new ArrayList<Cell>();

        for (int i = 0; i < voxels.length - 1; i++)
        {
            for (int j = 0; j < voxels[0].length - 1; j++)
            {
                for (int k = 0; k < voxels[0][0].length - 1; k++)
                {
                    int binaryValue = 0;
                    binaryValue += voxels[i][ j + 1][ k + 1] ? 128 : 0;
                    binaryValue += voxels[i + 1][ j + 1][ k + 1] ? 64 : 0;
                    binaryValue += voxels[i + 1][ j + 1][ k] ? 32 : 0;
                    binaryValue += voxels[i][ j + 1][ k] ? 16 : 0;
                    binaryValue += voxels[i][ j][ k + 1] ? 8 : 0;
                    binaryValue += voxels[i + 1][ j][ k + 1] ? 4 : 0;
                    binaryValue += voxels[i + 1][ j][ k] ? 2 : 0;
                    binaryValue += voxels[i][ j][ k] ? 1 : 0;

                    if (binaryValue != 255 && binaryValue != 0)
                    {
                    	int vals[] = {
                            materials[i][j + 1][k + 1],
                            materials[i + 1][j + 1][k + 1],
                            materials[i + 1][j + 1][k],
                            materials[i][j + 1][k],
                            materials[i][j][k + 1],
                            materials[i + 1][j][k + 1],
                            materials[i + 1][j][k],
                            materials[i][j][k]
                            };
                    	
                    	//define rotation
                        cellList.add(new Cell(binaryValue, i, j, k, vals,false));
                    }
                }
            }
        }
        //process rotations
//        
        for(int i = 0; i < cellList.size(); i++)
        {
        	if(cellList.get(i).value == 15)
        	{
        		int m = calculateMost(cellList.get(i));
        		int px = cellList.get(i).px;
        		int pz = cellList.get(i).pz;
        		
        		for(int j = 0; j < cellList.size(); j++)
        		{
        			if(px == cellList.get(j).px-1 && pz == cellList.get(j).pz)
        			{
        				//rot = true
        				if(m == calculateMost(cellList.get(j)))
        				{
        					//cellList.get(j).rot = true;
	        				cellList.get(i).rot = true;
	        				break;
        				}
        			}
        			if(px == cellList.get(j).px+1 && pz == cellList.get(j).pz)
        			{
        				//rot = true
        				if(m == calculateMost(cellList.get(j)))
        				{
        					//cellList.get(j).rot = true;
	        				cellList.get(i).rot = true;
	        				break;
        				}
        			}
        			if(px == cellList.get(j).pz-1 && px == cellList.get(j).px)
        			{
        				//rot = false
        				if(m == calculateMost(cellList.get(j)))
        				{
        					//cellList.get(j).rot = false;
	        				cellList.get(i).rot = false;
	        				break;
        				}
        			}
        			if(px == cellList.get(j).pz+1 && px == cellList.get(j).px)
        			{
        				//rot = false
        				if(m == calculateMost(cellList.get(j)))
        				{
        					//cellList.get(j).rot = false;
	        				cellList.get(i).rot = false;
	        				break;
        				}
        			}
        		}
        	}
        }
        return cellList;
    }
    
    @SuppressWarnings("unchecked")
	public static Object[] createBuffers(Object[] CellData)
    {
		List<Vector3f> vectorList = new ArrayList<Vector3f>();
		List<Vector2f> texCoordList = new ArrayList<Vector2f>();
		
		vectorList.addAll((ArrayList<Vector3f>) CellData[0]);
		texCoordList.addAll((ArrayList<Vector2f>) CellData[1]);
		
		Vector3f[] positionArray = new Vector3f[vectorList.size()];
		Vector2f[] texcoordArray = new Vector2f[texCoordList.size()];
		
		for(int i = 0; i < vectorList.size(); i++)
		{
			positionArray[i] = vectorList.get(i);
		}
		for(int i = 0; i < texCoordList.size(); i++)
		{
			texcoordArray[i] = texCoordList.get(i);
		}
		
		int[] indexBufferArray = new int[vectorList.size()];
		for(int i = 0; i < vectorList.size(); i++)
		{
			indexBufferArray[i] = i;
		}
		
		Vector3f[] normals = generateNormalArray(vectorList);
		Object[] buffers = new Object[4];
		
		buffers[0] = BufferUtils.createFloatBuffer(positionArray);
		buffers[1] = BufferUtils.createFloatBuffer(texcoordArray);
		buffers[2] = BufferUtils.createIntBuffer(indexBufferArray);
		buffers[3] = BufferUtils.createFloatBuffer(normals);
    	
    	return buffers;
    }
    
    public static Vector3f[] generateNormalArray(List<Vector3f> Vertices)
    {
    	List<Vector3f> Normals = new ArrayList<Vector3f>();
    	for(int i = 0; i < Vertices.size(); i+=3)
    	{
    		Vector3f p1 = Vertices.get(i);
    		Vector3f p2 = Vertices.get(i+1);
    		Vector3f p3 = Vertices.get(i+2);
    		
    		Vector3f pv1 = p2.subtract(p1).cross(p3.subtract(p1)).normalize();
    		Vector3f pv2 = p3.subtract(p2).cross(p1.subtract(p2)).normalize();
    		Vector3f pv3 = p1.subtract(p3).cross(p2.subtract(p3)).normalize();
    		
    		Normals.add(pv1);
    		Normals.add(pv2);
    		Normals.add(pv3);
    		
    	}
    	Vector3f[] normalArray = new Vector3f[Normals.size()];
    	for(int i = 0; i < normalArray.length; i++)
    	{
    		normalArray[i] = Normals.get(i);
    	}
    	return normalArray;
    }

    public static Geometry GenerateRigidBodyGeometry(String Name, float VoxelScale, FloatBuffer VertexBuffer, FloatBuffer TexCoordBuffer, IntBuffer IndexBuffer, FloatBuffer NormalBuffer, int RenderType, String TextureName)
    {
    	Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry(Name, someMesh);
		
		switch(RenderType)
		{
			case 0:
				//server
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				//someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.updateBound();
				someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md").clone();
				someGeometry.setMaterial(someMaterial);
				
				break;
			case 1:
				//test material
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.setBuffer(Type.Normal,    3, NormalBuffer);
				someMesh.updateBound();
				someMaterial = AssetLoaderManager.getMaterial("test").clone();
				someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
				someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
				someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName).clone());
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
				someGeometry.setMaterial(someMaterial);
				someGeometry.setQueueBucket(Bucket.Opaque);
				
				break;
			case 2:
				
				//test material
				someMesh.setBuffer(Type.Position, 3, VertexBuffer);
				someMesh.setBuffer(Type.TexCoord, 2, TexCoordBuffer);
				someMesh.setBuffer(Type.Index,    3, IndexBuffer);
				someMesh.setBuffer(Type.Normal,    3, NormalBuffer);
				someMesh.updateBound();
				someMaterial = AssetLoaderManager.getMaterial("test").clone();
				someMaterial.setFloat("TexSize", (float)(1.0f/SettingsLibrary.tileCount));
				someMaterial.setFloat("TileSize", SettingsLibrary.tileCount);
				someMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
				someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture(TextureName).clone());
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMinFilter(MinFilter.BilinearNoMipMaps);
				someMaterial.getTextureParam("ColorMap").getTextureValue().setMagFilter(MagFilter.Bilinear);
				someGeometry.setMaterial(someMaterial);
				someGeometry.setQueueBucket(Bucket.Transparent);
				
				break;
		}
		return someGeometry;
    }
    
  //TODO 
    //set all TexCoords to floored tex value
    public static Object[] createCellData(List<Cell> cellList)
    {
        List<Vector3f> PointList = new ArrayList<Vector3f>();
        List<Vector2f> texCoordList = new ArrayList<Vector2f>();
        int[][] indices = VoxelList.getList();
        
        float texsize = 1.0f/SettingsLibrary.tileCount;
//        float buffer = texsize * 0.015625f;

        for (int i = 0; i < cellList.size(); i++)
        {
            int value = cellList.get(i).value;
            float px = cellList.get(i).px;
            float py = cellList.get(i).py;
            float pz = cellList.get(i).pz;
            int material = MathUtil.calculateMost(cellList.get(i).m);

            Vector3f[] voxelVertices = new Vector3f[12];
            voxelVertices[0] = new Vector3f(0.5f, 0, 0);
            voxelVertices[1] = new Vector3f(1.0f, 0, 0.5f);
            voxelVertices[2] = new Vector3f(0.5f, 0, 1.0f);
            voxelVertices[3] = new Vector3f(0, 0, 0.5f);

            voxelVertices[4] = new Vector3f(0.5f, 1.0f, 0);
            voxelVertices[5] = new Vector3f(1.0f, 1.0f, 0.5f);
            voxelVertices[6] = new Vector3f(0.5f, 1.0f, 1.0f);
            voxelVertices[7] = new Vector3f(0, 1.0f, 1.0f / 2);

            voxelVertices[8] = new Vector3f(0, 0.5f, 0);
            voxelVertices[9] = new Vector3f(1.0f, 0.5f, 0);
            voxelVertices[10] = new Vector3f(1.0f, 0.5f, 1.0f);
            voxelVertices[11] = new Vector3f(0, 0.5f, 1.0f);
           
	        float matX = texsize * ((material) / SettingsLibrary.tileCount);
	        float matY = 1.0f- (texsize * ((material) % SettingsLibrary.tileCount) + texsize);
	        
            for (int indexCount = 0; indexCount < indices[value].length; indexCount+=3)
            {
            	 Vector3f p1 = new Vector3f(voxelVertices[indices[value][indexCount]].x, voxelVertices[indices[value][indexCount]].y, voxelVertices[indices[value][indexCount]].z);
                 Vector3f p2 = new Vector3f(voxelVertices[indices[value][indexCount + 1]].x, voxelVertices[indices[value][indexCount + 1]].y, voxelVertices[indices[value][indexCount + 1]].z);
                 Vector3f p3 = new Vector3f(voxelVertices[indices[value][indexCount + 2]].x, voxelVertices[indices[value][indexCount + 2]].y, voxelVertices[indices[value][indexCount + 2]].z);
                 
                 p1.x += px;
                 p2.x += px;
                 p3.x += px;
                 p1.y += py;
                 p2.y += py;
                 p3.y += py;
                 p1.z += pz;
                 p2.z += pz;
                 p3.z += pz;
                 
                 PointList.add(p1);
                 PointList.add(p2);
                 PointList.add(p3);
                 
                 texCoordList.add(new Vector2f(matX,matY));
                 texCoordList.add(new Vector2f(matX,matY));
                 texCoordList.add(new Vector2f(matX,matY));
            }
        }
        Object[] data = new Object[2];
        data[0] = PointList;
        data[1] = texCoordList;
        return data;
    }
    
    public static int calculateMost(Cell Cell)
    {
    	int most = 999;
        int count = 0;
        for (int countX = 0; countX < Cell.m.length; countX++)
        {
            if (Cell.m[countX] == most)
            {
                continue;
            }
            int curCount = 0;
            for (int countY = 0; countY < Cell.m.length; countY++)
            {
                if (Cell.m[countX] == Cell.m[countY])
                {
                    curCount++;
                }
            }
            if (curCount >= count && Cell.m[countX] < most && Cell.m[countX] != 0)
            {
                most = Cell.m[countX];
                count = curCount;
            }
        }
        
        return most;
    }

    public static Vector2f NormUV(Vector3f Vec, Vector3f P, boolean Rot)
    {
        boolean PosX = Vec.x > 0;
        boolean PosY = Vec.y > 0;
        boolean PosZ = Vec.z > 0;
 	
        double absX = Math.abs(Vec.x);
        double absY = Math.abs(Vec.y);
        double absZ = Math.abs(Vec.z);

        //90 degree around y axis(xz plane)
        
        if (PosX && absX >= absY && absX >= absZ)
        {
        	if(Rot)
        	{
        		return new Vector2f(P.y, P.z);
        	}
        	else
        	{
        		return new Vector2f(P.z, P.y);
        	}
        }
        if (!PosX && absX >= absY && absX >= absZ)
        {
        	if(Rot)
        	{
        		return new Vector2f(P.y, P.z);
        	}
        	else
        	{
        		return new Vector2f(P.z, P.y);
        	}
        }

        if (PosY && absY >= absX && absY >= absZ)
        {
        	if(Rot)
        	{
        		return new Vector2f(P.z, P.x);
        	}
        	else
        	{
        		return new Vector2f(P.x, P.z);
        	}
        }
        if (!PosY && absY >= absX && absY >= absZ)
        {
        	if(Rot)
        	{
        		return new Vector2f(P.z, P.x);
        	}
        	else
        	{
        		return new Vector2f(P.x, P.z);
        	}
        }

        if (PosZ && absZ >= absX && absZ >= absY)
        {
        	if(Rot)
        	{
        		return new Vector2f(P.y, P.x);
        	}
        	else
        	{
        		return new Vector2f(P.x, P.y);
        	}
        }
        if (!PosZ && absZ >= absX && absZ >= absY)
        {
        	if(Rot)
        	{
        		return new Vector2f(P.y, P.x);
        	}
        	else
        	{
        		return new Vector2f(P.x, P.y);
        	}
        }
        
        return new Vector2f();
    }

    public static boolean[] getVoxelValue(boolean[][][] Voxels, int x, int y,int z)
    {
        boolean[] result = new boolean[8];
        if (x >= Voxels.length - 1 || y >= Voxels[0].length - 1 || z >= Voxels[0][0].length- 1)
        {
            return result;
        }
        else if (y < 0)
        {
        	boolean[] vals = { true, true, true, true, true, true, true,true };
            return vals;
        }
        else
        {
            //Debug.WriteLine(x + "-" + y + "-" + z);
            result[7] = Voxels[x][ y + 1][ z + 1];
            result[6] = Voxels[x + 1][ y + 1][ z + 1];
            result[5] = Voxels[x + 1][ y + 1][ z];
            result[4] = Voxels[x][ y + 1][ z];
            result[3] = Voxels[x][ y][ z + 1];
            result[2] = Voxels[x + 1][ y][ z + 1];
            result[1] = Voxels[x + 1][ y][ z];
            result[0] = Voxels[x][ y][ z];
        }
        return result;
    }

}
