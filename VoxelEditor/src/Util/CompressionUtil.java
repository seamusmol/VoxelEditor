package Util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

import worldGen.Cell;

public class CompressionUtil 
{
	private static class CellSort implements Comparator<Cell>
	{
		@Override
		public int compare(Cell c1, Cell c2) 
		{
			int result = Float.compare(c1.value, c2.value);
			if(result == 0)
			{
				result = Float.compare(c1.m[0], c2.m[0]);
			}
			return result;
		}
	}
    
	public static byte[] GenerateCompressedChunkData(int ChunkIDX, int ChunkIDZ, boolean[][][] Voxels, int[][][]Materials)
    {
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				for(int k = 0; k < Voxels[0][0].length; k++)
				{
					flatVoxelArray.add(Voxels[i][j][k] ? 1:0);
					flatMaterialArray.add(Materials[i][j][k]);
				}
			}
		}
		
		List<Integer> compressedVoxelsValues = compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = compress(flatVoxelArray).get(1);
		
		//8 values
		List<Integer> compressedMaterialValues = compress(flatMaterialArray).get(0);
		List<Integer> compressedMaterialQuantities = compress(flatMaterialArray).get(1);
		
		List<Byte> data = new ArrayList<Byte>();
		
		AddValueBytes(ChunkIDX, data);
		AddValueBytes(ChunkIDZ, data);
		AddValueBytes(compressedVoxelsValues.size(), data);
		AddValueBytes(compressedMaterialValues.size(), data);
		AddValueBytes(Voxels.length-1, data);
		AddValueBytes(Voxels[0].length-1, data);
		AddValueBytes(Voxels[0][0].length-1, data);
		
		data.addAll(CompressionUtil.IntListToByteList(compressedVoxelsValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedVoxelsQuantities));
		
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
		byte[] compressedChunkData =  new byte[data.size()];
		for(int i = 0; i < compressedChunkData.length; i++)
		{
			compressedChunkData[i] = data.get(i);
		}
    	return compressedChunkData;
	}
	
	public static Object[] UnpackVoxelData(byte[] Data)
	{
		List<Integer> flatVoxelArrayValues = new ArrayList<Integer>();
		List<Integer> flatVoxelArrayQuantities = new ArrayList<Integer>();
		
		List<Integer> flatMaterialArrayValues = new ArrayList<Integer>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		int IDX = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int IDZ = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int cellShapeSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int cellMaterialSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int chunkWidth = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int chunkHeight = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int chunkLength = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatVoxelArrayValues.add(vertexValue);
		}
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValueQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatVoxelArrayQuantities.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayValues.add(cellMaterialValue);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		
		for(int i = 0; i < flatVoxelArrayValues.size(); i++)
		{
			for(int j = 0; j < flatVoxelArrayQuantities.get(i); j++)
			{
				flatVoxelArray.add(flatVoxelArrayValues.get(i));
			}
		}
		
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
		boolean[][][] Voxels = new boolean[chunkWidth+1][chunkHeight+1][chunkLength+1];
		int[][][] Materials = new int[chunkWidth+1][chunkHeight+1][chunkLength+1];
		
		int voxelCount = 0;
		
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				for(int k = 0; k < Voxels[0][0].length; k++)
				{
					Voxels[i][j][k] = flatVoxelArray.get(voxelCount) == 1;
					Materials[i][j][k] = flatMaterialArray.get(voxelCount);
					voxelCount++;
				}
			}
		}
		Object[] Values = new Object[7];
		Values[0] = IDX;
		Values[1] = IDZ;
		Values[2] = chunkWidth;
		Values[3] = chunkHeight;
		Values[4] = chunkLength;
		Values[5] = Voxels;
		Values[6] = Materials;
		
		return Values;
	}
	
	public static byte[] GenerateCompressedRigidBodyData(Float VoxelScale, Vector3f Position, Quaternion Rotation, boolean[][][] Voxels, int[][][]Materials)
    {
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				for(int k = 0; k < Voxels[0][0].length; k++)
				{
					flatVoxelArray.add(Voxels[i][j][k] ? 1:0);
					flatMaterialArray.add(Materials[i][j][k]);
				}
			}
		}
		
		List<Integer> compressedVoxelsValues = compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = compress(flatVoxelArray).get(1);
		
		//8 values
		List<Integer> compressedMaterialValues = compress(flatMaterialArray).get(0);
		List<Integer> compressedMaterialQuantities = compress(flatMaterialArray).get(1);
		
		List<Byte> data = new ArrayList<Byte>();
		
		AddValueBytes(VoxelScale, data);
		AddValueBytes(Position.x, data);
		AddValueBytes(Position.y, data);
		AddValueBytes(Position.z, data);
		AddValueBytes(Rotation.getX(), data);
		AddValueBytes(Rotation.getY(), data);
		AddValueBytes(Rotation.getZ(), data);
		AddValueBytes(Rotation.getW(), data);
		AddValueBytes(Voxels.length-1, data);
		AddValueBytes(Voxels[0].length-1, data);
		AddValueBytes(Voxels[0][0].length-1, data);
		AddValueBytes(compressedVoxelsValues.size(), data);
		AddValueBytes(compressedMaterialValues.size(), data);
		
		data.addAll(CompressionUtil.IntListToByteList(compressedVoxelsValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedVoxelsQuantities));
		
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialValues));
		data.addAll(CompressionUtil.IntListToByteList(compressedMaterialQuantities));
		
		byte[] compressedChunkData =  new byte[data.size()];
		for(int i = 0; i < compressedChunkData.length; i++)
		{
			compressedChunkData[i] = data.get(i);
		}
    	return compressedChunkData;
	}
	
	public static Object[] UnpackRigidBodyData(byte[] Data)
	{
		List<Integer> flatVoxelArrayValues = new ArrayList<Integer>();
		List<Integer> flatVoxelArrayQuantities = new ArrayList<Integer>();
		
		List<Integer> flatMaterialArrayValues = new ArrayList<Integer>();
		List<Integer> flatMaterialArrayQuantities = new ArrayList<Integer>();
		
		int ByteCount = 0;
		
		//scale
		int iscale = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		float scale = Float.intBitsToFloat(iscale);
		
		//position
		int pix = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int piy = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int piz = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		float px = Float.intBitsToFloat(pix);
		float py = Float.intBitsToFloat(piy);
		float pz = Float.intBitsToFloat(piz);
		
		//rotation
		int rix = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int riy = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int riz = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int riw = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		float rx = Float.intBitsToFloat(rix);
		float ry = Float.intBitsToFloat(riy);
		float rz = Float.intBitsToFloat(riz);
		float rw = Float.intBitsToFloat(riw);
		
		int rigidBodyWidth = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int rigidBodyHeight = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int rigidBodyLength = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		int cellShapeSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		int cellMaterialSize =  Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
		
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatVoxelArrayValues.add(vertexValue);
		}
		for(int i = 0; i < cellShapeSize; i++)
		{
			int vertexValueQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatVoxelArrayQuantities.add(vertexValueQuantity);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialValue = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayValues.add(cellMaterialValue);
		}
		
		for(int i = 0; i < cellMaterialSize; i++)
		{
			int cellMaterialQuantity = Data[ByteCount++] & 0xFF | (Data[ByteCount++] & 0xFF) << 8 | (Data[ByteCount++] & 0xFF) << 16 | (Data[ByteCount++] & 0xFF) << 24;
			flatMaterialArrayQuantities.add(cellMaterialQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < flatVoxelArrayValues.size(); i++)
		{
			for(int j = 0; j < flatVoxelArrayQuantities.get(i); j++)
			{
				flatVoxelArray.add(flatVoxelArrayValues.get(i));
			}
		}
		List<Integer> flatMaterialArray = new ArrayList<Integer>();
		for(int i = 0; i < flatMaterialArrayValues.size(); i++)
		{
			for(int j = 0; j < flatMaterialArrayQuantities.get(i);j++)
			{
				flatMaterialArray.add(flatMaterialArrayValues.get(i));
			}
		}
		
		boolean[][][] Voxels = new boolean[rigidBodyWidth+1][rigidBodyHeight+1][rigidBodyLength+1];
		int[][][] Materials = new int[rigidBodyWidth+1][rigidBodyHeight+1][rigidBodyLength+1];
		
		int voxelCount = 0;
		
		//TODO
		//fix out of bounds on line 448
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				for(int k = 0; k < Voxels[0][0].length; k++)
				{
					Voxels[i][j][k] = flatVoxelArray.get(voxelCount) == 1;
					Materials[i][j][k] = flatMaterialArray.get(voxelCount);
					voxelCount++;
				}
			}
		}
		Object[] Values = new Object[8];
		Values[0] = scale;
		Values[1] = new Vector3f(px,py,pz);
		Values[2] = new Quaternion(rx,ry,rz,rw);
		Values[3] = rigidBodyWidth;
		Values[4] = rigidBodyHeight;
		Values[5] = rigidBodyLength;
		Values[6] = Voxels;
		Values[7] = Materials;
		
		return Values;
	}
	
	public static Object[] GenerateByteListBoolean2D(boolean[][] Map)
	{
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < Map.length; i++)
		{
			for(int j = 0; j < Map[0].length; j++)
			{
				flatVoxelArray.add(Map[i][j] ? 1:0);
			}
		}
		List<Integer> compressedVoxelsValues = compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = compress(flatVoxelArray).get(1);
		
		Object[] Data = new Object[3];
		Data[0] = compressedVoxelsValues.size();
		Data[1] = CompressionUtil.IntListToByteList(compressedVoxelsValues);
		Data[2] = CompressionUtil.IntListToByteList(compressedVoxelsQuantities);
		return Data;
	}

	public static Object[] GenerateByteListInt2D(int[][] Map)
	{
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < Map.length; i++)
		{
			for(int j = 0; j < Map[0].length; j++)
			{
				flatVoxelArray.add(Map[i][j]);
			}
		}
		List<Integer> compressedVoxelsValues = compress(flatVoxelArray).get(0);
		List<Integer> compressedVoxelsQuantities = compress(flatVoxelArray).get(1);
		
		Object[] Data = new Object[3];
		Data[0] = compressedVoxelsValues.size();
		Data[1] = CompressionUtil.IntListToByteList(compressedVoxelsValues);
		Data[2] = CompressionUtil.IntListToByteList(compressedVoxelsQuantities);
		return Data;
	}

	public static List<Byte> GenerateByteListVector3f(List<Vector3f> List)
	{
		List<Byte> data = new ArrayList<Byte>();
		for(int i = 0; i < List.size(); i++)
		{
			AddValueBytes(List.get(i).x,data);
			AddValueBytes(List.get(i).y,data);
			AddValueBytes(List.get(i).z,data);
		}
		return data;
	}
	
	public static List<Byte> GenerateByteListVector2f(List<Vector2f> List)
	{
		List<Byte> data = new ArrayList<Byte>();
		for(int i = 0; i < List.size(); i++)
		{
			AddValueBytes(List.get(i).x,data);
			AddValueBytes(List.get(i).y,data);
		}
		return data;
	}
	
	public static List<Byte> GenerateByteListInt(List<Integer> List)
	{
		List<Byte> data = new ArrayList<Byte>();
		for(int i = 0; i < List.size(); i++)
		{
			AddValueBytes(List.get(i),data);
		}
		return data;
	}
	
	public static void AddValueBytes(float Value, List<Byte> Data)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(4).putFloat(Value).array();
		Data.add(compressSizeValL1[3]);
		Data.add(compressSizeValL1[2]);
		Data.add(compressSizeValL1[1]);
		Data.add(compressSizeValL1[0]);
	}
	
	public static void AddValueBytes(int Value, List<Byte> Data)
	{
		byte[] compressSizeValL1 = ByteBuffer.allocate(4).putInt(Value).array();
		Data.add(compressSizeValL1[3]);
		Data.add(compressSizeValL1[2]);
		Data.add(compressSizeValL1[1]);
		Data.add(compressSizeValL1[0]);
	}
	
	public static Object[] CreateFlatArrayList2D(int[][] Array)
	{
		List<Integer> flatArray = new ArrayList<Integer>();
		for(int i = 0; i < Array.length; i++)
		{
			for(int j = 0; j < Array.length; j++)
			{
				flatArray.add(Array[i][j]);
			}
		}
		return CreateCompressListInt(flatArray);
	}
	
	public static Object[] CreateFlatArrayList2D(boolean[][] Array)
	{
		List<Integer> flatArray = new ArrayList<Integer>();
		for(int i = 0; i < Array.length; i++)
		{
			for(int j = 0; j < Array.length; j++)
			{
				flatArray.add(Array[i][j] ? 1:0);
			}
		}
		return CreateCompressListInt(flatArray);
	}
	
	
    
	public static Object[] CreateCompressListInt(List<Integer> List)
	{
		List<Integer> compressedValues = compress(List).get(0);
		List<Integer> compressedQuantities = compress(List).get(1);
		
		Object[] CompressedBytes = new Object[2];
		CompressedBytes[0] = IntListToByteList(compressedValues);
		CompressedBytes[1] = IntListToByteList(compressedQuantities);
		
		return CompressedBytes;
	}
	
    public static List<List<Integer>> compress(List<Integer> List)
	{
		List<Integer> quantities = new ArrayList<Integer>();
		List<Integer> values = new ArrayList<Integer>();
		
		if(List.size() == 0)
		{
			quantities.add(0);
			values.add(0);
			List<List<Integer>> compressedData = new ArrayList<List<Integer>>();
			compressedData.add(values);
			compressedData.add(quantities);
			
			return compressedData;
		}
		
		int currentValue = List.get(0);
		int currentQuantity = 0;
		
		for(int i = 0; i < List.size(); i++)
		{
			if(i == List.size()-1)
			{
				if(currentValue == List.get(i))
				{
					currentQuantity++;
				}
				else
				{
					quantities.add(currentQuantity);
					values.add(currentValue);
					
					currentValue = List.get(i);
					currentQuantity = 1;
				}
				
				quantities.add(currentQuantity);
				values.add(currentValue);
			}
			else if(currentValue == List.get(i))
			{
				currentQuantity++;
			}
			else
			{
				quantities.add(currentQuantity);

				values.add(currentValue);
				
				currentValue = List.get(i);
				currentQuantity = 1;
			}
		}
		
		List<List<Integer>> compressedData = new ArrayList<List<Integer>>();
		compressedData.add(values);
		compressedData.add(quantities);
		
		return compressedData;
	}
    
    public static boolean[][] unpackBooleanArray2D(int byteCount, byte[] data, int Size, int mapSize)
	{
    	List<Integer> values = new ArrayList<Integer>(); 
		for(int i = 0; i < Size; i++)
		{
			int vertexValue = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			values.add(vertexValue);
		}
		List<Integer> quantities = new ArrayList<Integer>();
		for(int i = 0; i < Size; i++)
		{
			int vertexValueQuantity = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			quantities.add(vertexValueQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < values.size(); i++)
		{
			for(int j = 0; j < quantities.get(i); j++)
			{
				flatVoxelArray.add(values.get(i));
			}
		}
		
		boolean[][] Voxels = new boolean[mapSize][mapSize];
		int voxelCount = 0;
		
		for(int i = 0; i < Voxels.length; i++)
		{
			for(int j = 0; j < Voxels[0].length; j++)
			{
				Voxels[i][j] = flatVoxelArray.get(voxelCount) == 1;
				voxelCount++;
			}
		}
		return Voxels;
	}
    
    public static int[][] unpackIntArray2D(int byteCount, byte[] data, int Size, int mapSize)
	{
    	List<Integer> flatArrayValues = new ArrayList<Integer>();
    	List<Integer> flatArrayQuantities = new ArrayList<Integer>();
    	
    	for(int i = 0; i < Size; i++)
		{
			int vertexValue = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			flatArrayValues.add(vertexValue);
		}
		for(int i = 0; i < Size; i++)
		{
			int vertexValueQuantity = data[byteCount++] & 0xFF | (data[byteCount++] & 0xFF) << 8 | (data[byteCount++] & 0xFF) << 16 | (data[byteCount++] & 0xFF) << 24;
			flatArrayQuantities.add(vertexValueQuantity);
		}
		
		List<Integer> flatVoxelArray = new ArrayList<Integer>();
		for(int i = 0; i < flatArrayValues.size(); i++)
		{
			for(int j = 0; j < flatArrayQuantities.get(i); j++)
			{
				flatVoxelArray.add(flatArrayValues.get(i));
			}
		}
		
		int index = 0;
		int[][] array = new int[mapSize][mapSize];
		for(int i = 0; i < array.length; i++)
		{
			for(int j = 0; j < array[0].length; j++)
			{
				array[i][j] = flatVoxelArray.get(index);
				index++;
			}
		}
		return array;
	}
    
    
    public static float BytesToFloat(byte[] Data, int Index)
    {
    	int ix = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
    	float fx = Float.intBitsToFloat(ix);
    	return fx;
    }
    
    public static String BytesToString(byte[] Data, int Index, int DataLength)
    {
    	byte[] stringData = new byte[DataLength];
    	for(int i = 0; i < stringData.length; i++)
    	{
    		stringData[i] = Data[Index++];
    	}
    	String someString = new String(stringData);
    	
    	return someString;
    }
    
    public static List<Integer> GenerateIntList(byte[] Data, int Index, int DataLength)
    {
    	List<Integer> list = new ArrayList<Integer>();
    	for(int i = 0; i < DataLength; i++)
    	{
    		int ix = Data[Index++] & 0xFF | (Data[Index++] & 0xFF) << 8 | (Data[Index++] & 0xFF) << 16 | (Data[Index++] & 0xFF) << 24;
    		list.add(ix);
    	}
    	return list;
    }
    
    public static List<Vector2f> GenerateVector2fList(byte[] Data, int Index, int DataLength)
	{
		List<Vector2f> list = new ArrayList<Vector2f>();
		
		for(int i = 0; i < DataLength; i++)
		{
			list.add(new Vector2f(BytesToFloat(Data,Index),BytesToFloat(Data,Index)));
		}
		return list;
	}
    
    public static List<Vector3f> GenerateVector3fList(byte[] Data, int Index, int DataLength)
	{
		List<Vector3f> list = new ArrayList<Vector3f>();
		
		for(int i = 0; i < DataLength; i++)
		{
			list.add(new Vector3f(BytesToFloat(Data,Index),BytesToFloat(Data,Index),BytesToFloat(Data,Index)));
		}
		return list;
	}
    
	public static List<Byte> IntListToByteList(List<Integer> IntList)
	{
		List<Byte> ByteList = new ArrayList<Byte>();
		for(int i = 0; i < IntList.size(); i++)
		{
			byte[] byteList = ByteBuffer.allocate(4).putInt(IntList.get(i)).array();
			ByteList.add(byteList[3]);
			ByteList.add(byteList[2]);
			ByteList.add(byteList[1]);
			ByteList.add(byteList[0]);
		}
		return ByteList;
	}

}
