package Main;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import Util.VoxelGridUtil;
import Util.VoxelUtil;
import worldGen.Cell;

public class VoxelModel {

	boolean[][][] voxels;
	int[][][] materials;
	
	int x,y,z;
//	float scale = 1.0f;
	float ox,oy,oz;
	
	float curScale = 1.0f;
	
	float maxScale = 1.0f;
	float minScale = 0.125f;
	
	int selectedMaterial = 129;
	
	String modelName = "untitled";
	String fileString = "";
	String textureName;
	Vector3f cubeOutLineOffset = new Vector3f();
	Geometry geom;
	Geometry transGeom;
	Geometry cubeModel;
	Geometry centerModel;
	Geometry gridModel;
	
	boolean hasOutLineTool = false;
	
//	boolean needsResize = false;
	boolean needsGeometryUpdate = false;
	
	public VoxelModel(int X, int Y, int Z)
	{
		voxels = new boolean[X][Y][Z];
		materials = new int[X][Y][Z];
		
		x = X;
		y = Y;
		z = Z;
		
		cubeOutLineOffset.set(x/2,y/2,z/2);
		
		processModel();
		needsGeometryUpdate = true;
	}
	
	public VoxelModel(float Scale, int X, int Y, int Z, boolean[][][] Voxels, int[][][] Materials, String FileString)
	{
		voxels = Voxels;
		materials = Materials;
		
		x = X;
		y = Y;
		z = Z;
		
		curScale = Scale;
		
		cubeOutLineOffset.set(x/2,y/2,z/2);
		
		fileString = FileString;
		
		processModel();
		needsGeometryUpdate = true;
	}
	
	//TODO
	//add scale
	public void GenerateCubeOutLineModel()
	{
		Object[] ModelData = AssetLoaderManager.getModelData("CubeOutLine");
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("CubeOutLine", someMesh);
		
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer((Vector3f[]) ModelData[0]));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer((Vector2f[]) ModelData[1]));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer((int[]) ModelData[2]));
		someMesh.updateBound();
		someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("CubeOutLine"));
		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		
		someGeometry.setMaterial(someMaterial);
		
		cubeModel = someGeometry;
	}
	
	public void GenerateCenterOutLine()
	{
		Object[] ModelData = AssetLoaderManager.getModelData("CubeOutLine");
		
		Mesh someMesh = new Mesh();
		Material someMaterial = null;
		Geometry someGeometry = new Geometry("CenterOutLine", someMesh);
		
		someMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer((Vector3f[]) ModelData[0]));
		someMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer((Vector2f[]) ModelData[1]));
		someMesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer((int[]) ModelData[2]));
		someMesh.updateBound();
		someMaterial = new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		
		someMaterial.setTexture("ColorMap", AssetLoaderManager.getTexture("CubeOutLine"));
		someMaterial.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		
		someGeometry.setMaterial(someMaterial);
		
		centerModel = someGeometry;
	}
	
	public Vector3f GetCubePosition()
	{
		return cubeModel.getWorldTranslation();
	}
	
	public void fillCube()
	{
		setVoxel(cubeOutLineOffset, selectedMaterial, true);
	}
	
	public void emptyCube()
	{
		setVoxel(cubeOutLineOffset, 0, false);
	}
	
	public void setVoxel(Vector3f Position, int material, boolean Value)
	{
		int px = (int) (Position.x / getScale());
		int py = (int) (Position.y / getScale());
		int pz = (int) (Position.z / getScale());
		
		voxels[px][py][pz] = Value;
		materials[px][py][pz] = material;
		needsGeometryUpdate = true;
	}
	
	public void setName(String NewName)
	{
		modelName = NewName;
	}
	
	@SuppressWarnings("unchecked")
	public void processModel()
	{
		needsGeometryUpdate = false;
		Object[] CellLists = VoxelUtil.GenerateVoxelCellList(voxels, materials, true);
        List<Cell> ChunkCellList = (List<Cell>)CellLists[0];
        List<Cell> TransChunkCellList = (List<Cell>)CellLists[1];
        
        Object[] data = VoxelUtil.createCellData(ChunkCellList);
 		Object[] buffers = VoxelUtil.createBuffers(data);
 		
 		Object[] transData = VoxelUtil.createCellData(TransChunkCellList);
 		Object[] transBuffers = VoxelUtil.createBuffers(transData);

 		geom = VoxelUtil.GenerateRigidBodyGeometry("VoxelModel", getScale(),  (FloatBuffer)buffers[0], (FloatBuffer)buffers[1], (IntBuffer)buffers[2], (FloatBuffer)buffers[3], 1, "test");
 		transGeom = VoxelUtil.GenerateRigidBodyGeometry("TransVoxelModel", getScale(),  (FloatBuffer)transBuffers[0], (FloatBuffer)transBuffers[1], (IntBuffer)transBuffers[2], (FloatBuffer)transBuffers[3], 2, "test");
 		
 		geom.setLocalScale(curScale);
 		transGeom.setLocalScale(curScale);
 		
 		gridModel = VoxelGridUtil.GenerateGrid(x, z, getScale());
 		GenerateCubeOutLineModel();
 		GenerateCenterOutLine();
	}
	
	public boolean[][][] getVoxels()
	{
		return voxels;
	}
	
	public int[][][] getMaterials()
	{
		return materials;
	}
	
	public void addCenterOffset(float OX, float OY, float OZ)
	{
		float tx = x/2;
		float ty = y/2;
		float tz = z/2;
		
		ox = ox+OX < -tx ? -tx: ox+OX;
		oy = oy+OY < 0 ? 0: oy+OY;
		oz = oz+OZ < -tz ? -tz: oz+OZ;
		
		ox = ox+OX > tx ? tx: ox+OX;
		oy = oy+OY > ty ? ty: oy+OY;
		oz = oz+OZ > tz ? tz: oz+OZ;
		
		
	}
	
	public void setMaterial(int NewMaterial)
	{
		if(NewMaterial > 0 && NewMaterial < SettingsLibrary.tileCount*SettingsLibrary.tileCount)
		{
			selectedMaterial = NewMaterial;
		}
	}
	
	public int getMaterial()
	{
		return selectedMaterial;
	}
	
	public float getScale()
	{
		return curScale;
	}
	
	public void setScale(int Change)
	{
		
		if(curScale <= minScale && Change == -1)
		{
			return;
		}
		else if(curScale >= maxScale && Change == 1)
		{
			return;
		}
		
		curScale += Change == 1 ? curScale: -curScale/2;
		
//		setDimensions(x);
		
		cubeOutLineOffset.set(x/2 * getScale(), y/2 * getScale(), z/2 * getScale());
		
		needsGeometryUpdate = true;
	}
	
	public void setDimensions(String Dimension, int Change)
	{
		switch(Dimension)
		{
			case "x":
				x+= Change;
				x = x > 3 ? x : 3;
				break;
			case "y":
				y+= Change;
				y = y > 3 ? y : 3;
				break;
			case "z":
				z+= Change;
				z = z > 3 ? z : 3;
				break;
		}
		needsGeometryUpdate = true;
		boolean[][][] newVoxels = new boolean[x][y][z];
		int[][][] newMaterials = new int[x][y][z];
		
		if(x < voxels.length || y < voxels[0].length || z < voxels[0][0].length)
		{
			//increase fields
			for(int i = 0; i < newVoxels.length; i++)
			{
				for(int j = 0; j < newVoxels[0].length; j++)
				{
					for(int k = 0; k < newVoxels[0][0].length; k++)
					{
						newVoxels[i][j][k] = voxels[i][j][k];
						newMaterials[i][j][k] = materials[i][j][k];
					}
				}
			}
		}
		else
		{
			//decrease fields
			for(int i = 0; i < voxels.length; i++)
			{
				for(int j = 0; j < voxels[0].length; j++)
				{
					for(int k = 0; k < voxels[0][0].length; k++)
					{
						newVoxels[i][j][k] = voxels[i][j][k];
						newMaterials[i][j][k] = materials[i][j][k];
					}
				}
			}
		}
		
		voxels = newVoxels;
		materials = newMaterials;
	}

	public int getDimension(String Dimension)
	{
		int val = -1;
		switch(Dimension)
		{
			case "x":
				val = x;
				break;
			case "y":
				val = y;
				break;
			case "z":
				val = z;
				break;
		}
		return val;
	}
	
	public Vector3f getCenterOffset()
	{
		return new Vector3f(ox,oy,oz);
	}
}
