package Main;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;

import Console.Parser;
import Input.InputHandler;

public class Main extends SimpleApplication{

	Parser parser;
	AssetLoaderManager assetLoaderManager;
	InputHandler inputHandler;
	VoxelEditorManager voxelEditor;
	
	public static void main(String[] args)
    {
        Main app = new Main();
        
        AppSettings newSetting = new AppSettings(true);
		newSetting.setFrameRate(144);	
		newSetting.setResolution(1600, 900);
//		newSetting.setResizable(true);
		
		app.setSettings(newSetting);
		app.setShowSettings(true);
        app.start();
    }
	
	@Override
	public void simpleInitApp() 
	{
		parser = new Parser();
		parser.addAllowedClass(this, "Main.Main");
		parser.addAllowedMethod("exit", "Main.Main");
		
		assetLoaderManager = new AssetLoaderManager(stateManager, this);
		inputHandler = new InputHandler(this, parser);
		
		voxelEditor = new VoxelEditorManager(parser);
		stateManager.attach(voxelEditor);
		
		Parser.execute("config");
//		Parser.parseString("createmenu menu");
	}

	@Override
    public void simpleUpdate(float TPF)
	{	
		inputHandler.update(TPF);
	}
	
	@Override
    public void simpleRender(RenderManager rm) 
	{
		
	}

	public void exit(Object[] Value)
	{
		System.exit(0);
	}
	
}
