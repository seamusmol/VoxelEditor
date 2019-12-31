package Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import Console.Parser;
import Menu.Displaybutton;
import Menu.Tabselector;
import Menu.Tileselector2d;
import Util.CollisionShapeUtil;
import Util.VoxelIOUtil;

public class VoxelEditorManager extends AbstractAppState{

	Main main;
	CameraManager cameraManager;
	UIManager uiManager;
	
	Node modelNode = new Node("ModelNode");
	Node infoNode = new Node("InfoNode");
	
	List<VoxelModel> currentModels = new ArrayList<VoxelModel>();
	int activeIndex = 0;
	int maxModelCount = 10;
	
	boolean needsUpdate = false;
	boolean needsResize = false;
	
	public VoxelEditorManager(Parser ConsoleParser)
	{
		ConsoleParser.addAllowedClass(this, "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("generatenewvoxelmodel", "Main.VoxelEditorManager");
		
		ConsoleParser.addAllowedMethod("changedimension", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("changescale", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("changematerial", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("saveasmodel", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("savemodel", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("importmodel", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("changemodel", "Main.VoxelEditorManager");
		ConsoleParser.addAllowedMethod("removemodel", "Main.VoxelEditorManager");
	}
	
	@Override
	public void initialize(AppStateManager StateManager, Application Application)
	{
		main = (Main) Application;
		//test grid
		main.getRootNode().attachChild(infoNode);
		main.getRootNode().attachChild(modelNode);
		
		cameraManager = new CameraManager(this, main);
		
		uiManager = new UIManager(main.parser);
		
		main.getStateManager().attach(cameraManager);
		main.getStateManager().attach(uiManager);
		
		cameraManager.initialize(main.getStateManager(), main);
		uiManager.initialize(main.getStateManager(), main);
		
		Parser.parseString("createmenu GUILayout");
		updateDisplays();
	}
	
	@Override
	public void update(float tpf) 
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		VoxelModel model = GetActiveModel();
		if(model.needsGeometryUpdate)
		{
			//TODO
			//update spatial
			model.processModel();
			if(modelNode.getChild("VoxelModel") != null)
			{
				modelNode.detachChildNamed("VoxelModel");
				modelNode.detachChildNamed("TransVoxelModel");
			}
			
			modelNode.attachChild(model.geom);
			modelNode.attachChild(model.transGeom);
			
			if(infoNode.getChild("Grid") != null)
			{
				infoNode.detachChildNamed("Grid");
			}
			infoNode.attachChild(model.gridModel);
		}
	}
	
	public void updateDisplays()
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		
		int valx = GetActiveModel().getDimension("x");
		int valy = GetActiveModel().getDimension("y");
		int valz = GetActiveModel().getDimension("z");
		float vals = GetActiveModel().getScale();
		
		int selectorx = GetActiveModel().getMaterial();
		
		updateDisplayButton("disx", valx+"");
		updateDisplayButton("disy", valy+"");
		updateDisplayButton("disz", valz+"");
		updateDisplayButton("diss", vals+"");
		updateTileSelector2DButton("tex", selectorx);
		updateTabSelector("tabs");
		
	}
	
	public void saveasmodel(Object[] Value)
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		File newfile = null;
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.grabFocus();
		fileChooser.setDialogTitle("Save Voxel Model");
		
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
//		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Voxel(.vxl)", "vxl"));
		
		int result = fileChooser.showSaveDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
		    newfile = fileChooser.getSelectedFile();
		}
		if(newfile != null)
		{
			VoxelIOUtil.exportVoxelModel(GetActiveModel(), newfile.getAbsolutePath().replaceAll(".vxl", ""));
			GetActiveModel().setName(newfile.getName().replaceAll(".vxl", ""));
			GetActiveModel().fileString = newfile.getAbsolutePath();
			updateTabSelector("tabs");
		}
	}
	
	public void savemodel(Object[] Value)
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		String fileString = GetActiveModel().fileString;
		if(fileString.equals(""))
		{
			saveasmodel(new Object[]{});
		}
		else
		{
			VoxelIOUtil.exportVoxelModel(GetActiveModel(), fileString.substring(0,fileString.indexOf(".vxl")));
			updateTabSelector("tabs");
		}
	}
	
	public void importmodel(Object[] Value)
	{
		File newfile = null;
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.grabFocus();
		fileChooser.setDialogTitle("Import Voxel Model");
		
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
//		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Voxel(.vxl)", "vxl"));
		
		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
		    newfile = fileChooser.getSelectedFile();
		}
		if(newfile != null)
		{
			VoxelModel newModel = VoxelIOUtil.importModel(newfile);
			
			loadImportedModel(newModel);
			updateDisplays();
		}
	}
	
	public void changedimension(Object[] Value)
	{
		String dimension = (String)Value[0];
		int value = Integer.parseInt((String)Value[1]);
		
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().setDimensions(dimension, value);
		if(Value[2] != null)
		{
			int val = GetActiveModel().getDimension(dimension);
			updateDisplayButton((String)Value[2], val+"");
		}
		updateDisplays();
	}
	
	public void changematerial(Object[] Value)
	{
		int value = Integer.parseInt((String)Value[0]);
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().setMaterial(value);
		updateDisplays();
	}
	
	public Vector3f getOffSet()
	{
		if(currentModels.size() == 0)
		{
			return new Vector3f();
		}
		return GetActiveModel().getCenterOffset();
	}
	
	public void changeCenter(float OX, float OY, float OZ)
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().addCenterOffset(OX,OY,OZ);
		updateDisplays();
	}
	
	public void changescale(Object[] Value)
	{
		int value = Integer.parseInt((String)Value[0]);
		if(currentModels.size() == 0)
		{
			return;
		}
		
		GetActiveModel().setScale(value);
		if(Value[1] != null)
		{
			updateDisplayButton((String)Value[1], GetActiveModel().getScale()+"");
		}
		updateDisplays();
	}
	
	public void updateDisplayButton(String ButtonName, String Text)
	{
		if(uiManager.getButtonByName(ButtonName) != null)
		{
			if(uiManager.getButtonByName(ButtonName) instanceof Displaybutton)
			{
				Displaybutton display = (Displaybutton) uiManager.getButtonByName(ButtonName);
				display.updateText(Text + "");
			}
		}
	}
	
	public void updateTileSelector2DButton(String ButtonName, int Value)
	{
		if(uiManager.getButtonByName(ButtonName) != null)
		{
			if(uiManager.getButtonByName(ButtonName) instanceof Tileselector2d)
			{
				Tileselector2d selector = (Tileselector2d) uiManager.getButtonByName(ButtonName);
				selector.setValue(Value);
			}
		}
	}
	
	public void updateTabSelector(String ButtonName)
	{
		if(uiManager.getButtonByName(ButtonName) != null)
		{
			if(uiManager.getButtonByName(ButtonName) instanceof Tabselector)
			{
				Tabselector selector = (Tabselector) uiManager.getButtonByName(ButtonName);
				List<String> tabNames = new ArrayList<String>();
				for(int i = 0; i < currentModels.size(); i++)
				{
					tabNames.add(currentModels.get(i).modelName);
				}
				selector.setTabNames(tabNames);
			}
		}
	}
	
	public void generatenewvoxelmodel(Object[] Value)
	{
		if(currentModels.size() >= maxModelCount)
		{
			return;
		}
		VoxelModel newModel = new VoxelModel(10,10,10);
		currentModels.add(newModel);
		
		changemodel(new Object[]{currentModels.size()-1 + ""});
		updateTabSelector("tabs");
	}
	
	public void loadImportedModel(VoxelModel Model)
	{
		if(currentModels.size() >= maxModelCount)
		{
			return;
		}
		currentModels.add(Model);
		
		changemodel(new Object[]{currentModels.size()-1 + ""});
		updateTabSelector("tabs");
		
	}
	
	
	public void updatemodel()
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().processModel();
	}
	
	public void changemodel(Object[] Value)
	{
		int newIndex = (int)Integer.parseInt((String)Value[0]);
		activeIndex = newIndex;
			
		if(activeIndex > currentModels.size()-1)
		{
			activeIndex = currentModels.size()-1;
		}
		
		if(modelNode.getChild("VoxelModel") != null)
		{
			modelNode.detachChildNamed("VoxelModel");
		}
		if(currentModels.size() != 0)
		{
			modelNode.attachChild(GetActiveModel().geom);
			GetActiveModel().needsGeometryUpdate = true;
		}
		
		
		updateDisplays();
	}
	
	public void removemodel(Object[] Value)
	{
		int index = (int)Integer.parseInt((String)Value[0]);
		
		if(currentModels.size() > 1)
		{
			currentModels.remove(index);
		}
		changemodel(new Object[]{activeIndex + ""});
		updateDisplays();
	}
	
	public void fillCube()
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().fillCube();
	}
	public void emptyCube()
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().emptyCube();
	}
	
	public void setModelMaterial(int Material)
	{
		if(currentModels.size() == 0)
		{
			return;
		}
		GetActiveModel().selectedMaterial = Material;
	}
	
	public Vector3f GetPickCubeLocation2D(int SelectionPlaneType)
	{
		Vector3f a = new Vector3f();
		Vector3f b = new Vector3f();
		Vector3f c = new Vector3f();
		Vector3f d = new Vector3f();
		
		float scale = GetScale();
		Vector3f size = GetDimensions().mult(GetScale());
		Vector3f curPos = new Vector3f(GetCubePosition());
		switch(SelectionPlaneType)
		{
			case 0:
				Vector3f Direction = main.getCamera().getRotation().getRotationColumn(2);
				Vector2f dir2D = new Vector2f(-Math.round(Direction.z), Math.round(Direction.x)); 
				
				a = curPos.subtract( new Vector3f(-dir2D.x,0,-dir2D.y).mult(size));
				a.x = a.x > 0 ? a.x : 0;
				a.z = a.z > 0 ? a.z : 0;
				a.y = 0;
				a.x = a.x < size.x ? a.x : size.x;
				a.z = a.z < size.z ? a.z : size.z;
				
				b = new Vector3f(a).add(new Vector3f(-dir2D.x,0,-dir2D.y).mult(size));
				
				b.x = b.x > 0 ? b.x : 0;
				b.z = b.z > 0 ? b.z : 0;
				b.y = 0;
				b.x = b.x < size.x ? b.x : size.x;
				b.z = b.z < size.z ? b.z : size.z;
				
				c = a.add(0, size.y,0);
				d = b.add(0, size.y,0);
				
				break;
			case 1:
				a = new Vector3f(0,curPos.y,0).add(-scale/2,0,-scale/2);
				b = new Vector3f(size.x,curPos.y,0).add(scale/2,0,-scale/2);
				c = new Vector3f(0,curPos.y,size.z).add(-scale/2,0,scale/2);
				d = new Vector3f(size.x,curPos.y,size.z).add(scale/2,0,scale/2);
				break;
		}
		
		Geometry quadMesh = CollisionShapeUtil.generateCollisionQuad(a, b, c, d);
		
		Vector2f click2d = main.getInputManager().getCursorPosition().clone();
		Vector3f click3d = main.getCamera().getWorldCoordinates( click2d, 0f).clone();
		Vector3f dir = main.getCamera().getWorldCoordinates( click2d, 1f).subtractLocal(click3d).normalizeLocal();
		
		Ray ray = new Ray(click3d,dir);
		CollisionResults results = new CollisionResults();
		
//		if(main.getRootNode().getChild("CollisionQuad") != null)
//		{
//			main.getRootNode().detachChildNamed("CollisionQuad");
//		}
//		main.getRootNode().attachChild(quadMesh);
		
		quadMesh.collideWith(ray, results);
		
		if(results.size() > 0)
		{
			Vector3f roundedCollisionPoint = new Vector3f(results.getClosestCollision().getContactPoint());
			roundedCollisionPoint.x = Math.round(roundedCollisionPoint.x / GetScale()) * GetScale();
			roundedCollisionPoint.y = Math.round(roundedCollisionPoint.y / GetScale()) * GetScale();
			roundedCollisionPoint.z = Math.round(roundedCollisionPoint.z / GetScale()) * GetScale();
			
			return roundedCollisionPoint;
		}
		else
		{
			return new Vector3f(-1,-1,-1);
		}
	}
	
	public Geometry GetCubeModel()
	{
		if(currentModels.size() == 0)
		{
			return null;
		}
		return GetActiveModel().cubeModel;
	}
	
	public Geometry GetCenterModel()
	{
		if(currentModels.size() == 0)
		{
			return null;
		}
		return GetActiveModel().centerModel;
	}
	
	public Vector3f GetDimensions()
	{
		if(currentModels.size() == 0)
		{
			return new Vector3f(9,9,9);
		}
		return new Vector3f(GetActiveModel().x-1,GetActiveModel().y-1,GetActiveModel().z-1);
	}
	
	public float GetScale()
	{
		if(currentModels.size() == 0)
		{
			return 1.0f;
		}
		return GetActiveModel().getScale();
	}
	
	public Vector3f GetCubePosition()
	{
		if(currentModels.size() == 0)
		{
			return new Vector3f();
		}
		return GetActiveModel().cubeOutLineOffset;
	}
	
	
	public Vector2f GetCenterPosition()
	{
		if(currentModels.size() == 0)
		{
			return new Vector2f(5,5);
		}
		return new Vector2f(GetActiveModel().x/2, GetActiveModel().z/2);
	}
	
	private VoxelModel GetActiveModel()
	{
		return currentModels.get(activeIndex);
	}

	public boolean hasGUICollision()
	{
		return uiManager.hasGUICollision();
	}
	
	public void setHasOutLineTool(boolean Value)
	{
		if(currentModels.size() == 0)
		{
			return;
			
		}
		GetActiveModel().hasOutLineTool = Value;
	}
	
	public boolean hasOutLineTool() 
	{
		if(currentModels.size() == 0)
		{
			return false;
		}
		return GetActiveModel().hasOutLineTool;
	}
}
