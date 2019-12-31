package Main;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import com.jme3.util.BufferUtils;

public class AssetLoaderManager extends AbstractAppState{

	static Map<String, Material> materialStorage = new HashMap<String, Material>();
	static Map<String, Texture> textureStorage = new HashMap<String, Texture>();
	static Map<String, TextureArray> textureArrayStorage = new HashMap<String, TextureArray>();
	
	static Map<String, Object[]> modelDataStorage = new HashMap<String, Object[]>();
	
	static Map<String, int[][]> storeImageArrays = new HashMap<String, int[][]>();
	
	static AssetManager assetManager;
	
	static String[] heightMaps;
	
	public AssetLoaderManager(AppStateManager StateManager, Application App)
	{
		if(textureStorage.size() == 0)
		{
			Main main = (Main)App;
			
			assetManager = App.getAssetManager();
			loadTextures(main);
			loadMaterials();
			loadModels();
			//temporary
		}
		
        //assetManager.addAssetEventListener(asl);
	}
	
	private void loadTextures(Main Main)
	{
		textureStorage = new HashMap<String,Texture>();
		File folder = new File("src/assets/gfx");
		File[] listOfFiles = folder.listFiles();
		assetManager.registerLocator(folder.getAbsolutePath(), FileLocator.class);
		
		for(int i = 0; i < listOfFiles.length; i++)
		{
			if(listOfFiles[i].getName().endsWith(".png"))
			{
				if(listOfFiles[i].getName().startsWith("map_"))
				{
				}
				else
				{
//					
					textureStorage.put(listOfFiles[i].getName().replace(".png", ""), assetManager.loadTexture( "assets/gfx/" + listOfFiles[i].getName()));
				}
			}
			
		}
	}
	
	private void loadModels()
	{
		File headFolder = new File("src/assets/models");
		File[] modelFolders = headFolder.listFiles();
		
		for(int i = 0; i < modelFolders.length; i++)
		{
			File[] modelSubFolder = modelFolders[i].listFiles();
			
			for(int j = 0; j < modelSubFolder.length; j++)
			{
				if(modelSubFolder[j].getName().endsWith(".obj"))
				{
					Spatial someModel = (Spatial) assetManager.loadModel("/assets/models/" + modelFolders[i].getName() + "/" + modelSubFolder[j].getName());
					Geometry someGeometry = (Geometry) someModel;
					
					Object[] modelData = new Object[3];

					modelData[0] = BufferUtils.getVector3Array(someGeometry.getMesh().getFloatBuffer(Type.Position));
					modelData[1] = BufferUtils.getVector2Array(someGeometry.getMesh().getFloatBuffer(Type.TexCoord));
					
					int[] indices =  new int[someGeometry.getMesh().getIndexBuffer().size()];
							
					for(int k = 0; k < someGeometry.getMesh().getIndexBuffer().size(); k++)
					{
						indices[k] = someGeometry.getMesh().getIndexBuffer().get(k);
					}
					modelData[2] = indices;
					modelDataStorage.put(modelSubFolder[j].getName().replace(".obj", ""), modelData);
					System.out.println(modelSubFolder[j].getName().replace(".obj", ""));
				}
			}
			
		}
	}
	
	private void loadMaterials()
	{
		materialStorage.put("default", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
		
		/*
		 materialStorage.put("default", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
         materialStorage.put("background", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/background.j3md"));
         materialStorage.put("clouds", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/clouds.j3md"));
         materialStorage.put("instancetest", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/instancetest.j3md"));
         materialStorage.put("test", new Material(AssetLoaderManager.getAssetManager(), "Common/MatDefs/Misc/test.j3md"));
		*/
		
         
		File headFolder = new File("src/assets/shaders");
		File[] modelFolders = headFolder.listFiles();
		
		assetManager.registerLocator(headFolder.getAbsolutePath(), FileLocator.class);
		
		for(int i = 0; i < modelFolders.length; i++)
		{
			if(modelFolders[i].getName().endsWith(".j3md"))
			{
				materialStorage.put(modelFolders[i].getName().replaceAll(".j3md", ""), new Material(AssetLoaderManager.getAssetManager(), modelFolders[i].getName()));
			}
		}
		
	}
	
	public static Object[] getModelData(String Key)
	{
		return modelDataStorage.get(Key);
	}
	
	public static Material getMaterial(String Key)
	{
		return materialStorage.get(Key);
		
	}
	
	public static Texture getTexture(String Key)
	{
		return textureStorage.get(Key);
	}
	
	public static String[] getHeightMaps()
	{
		return heightMaps;
	}
	
	public static TextureArray getTextureArray(String Key)
	{
		return textureArrayStorage.get(Key);
	}
	
	public static AssetManager getAssetManager()
	{
		return assetManager;
	}
	
}
