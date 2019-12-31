package Main;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;

import Input.InputHandler;
import Util.MathUtil;

public class CameraManager extends AbstractAppState
{
	Camera cam;
	
	Vector3f lookPosition = new Vector3f();
	Vector3f lookOffset = new Vector3f();
	Vector3f center = new Vector3f();
	
//	Vector3f cubeOutLineOffset = new Vector3f();
	
	long lastCubeToggle = System.currentTimeMillis();
	
	double rot = 0;
	double roty = 0;
	
	float height = 10;
	float dist = 20;
	
	float sensitivity = 5.0f;
	
	Main main;
	VoxelEditorManager voxelEditor;
	
	public CameraManager(VoxelEditorManager VoxelEditorManager, Main Main)
	{
		main = Main;
		voxelEditor = VoxelEditorManager;
		main.getFlyByCamera().setEnabled(false);
		
		cam = main.getCamera();
	    cam.setFrustumPerspective( 95, 1.7777f, 0.5f, 1000);
//	    cam.setViewPort(0.0f, 1.0f, 0.0f, 1.0f);
	    main.getViewPort().setBackgroundColor(ColorRGBA.Gray);
	    cam.update();
	    updateCameraPosition();
		
	}
	
	@Override
	public void update(float tpf)
	{
		Vector2f newCenter = voxelEditor.GetCenterPosition();
		center.set(newCenter.x, 0, newCenter.y);
		
		boolean needsUpdate = false;
		
		if(InputHandler.hasInput("rotl"))
		{
			rot+=90;
			rot%=360;
			needsUpdate = true;
		}
		if(InputHandler.hasInput("rotr"))
		{
			rot-=90;
			rot%=360;
			needsUpdate = true;
		}
		float scale = voxelEditor.GetScale();
		float maxX = (int)voxelEditor.GetDimensions().x * scale;
		float maxY = (int)voxelEditor.GetDimensions().y * scale;
		float maxZ = (int)voxelEditor.GetDimensions().z * scale;
		
		Vector3f cubePositionMovement = new Vector3f();
		
		if(InputHandler.hasInput("mask"))
		{
			if(voxelEditor.hasOutLineTool())
			{
				if(InputHandler.hasInput("mouse_scrollinc"))
				{
					cubePositionMovement = cubePositionMovement.add(0,-scale,0);
				}
				else if(InputHandler.hasInput("mouse_scrolldec"))
				{
					cubePositionMovement = cubePositionMovement.add(0,scale,0);
				}
				else if(InputHandler.hasInput("mouse_primary"))
				{
					if(!voxelEditor.hasGUICollision())
					{
						Vector3f newPosition = voxelEditor.GetPickCubeLocation2D(0);
						
						if(!newPosition.equals(new Vector3f(-1,-1,-1)))
						{
							voxelEditor.GetCubePosition().set(newPosition);
						}
					}
				}
			}
		}
		else if(InputHandler.hasInput("tabmask"))
		{
			if(voxelEditor.hasOutLineTool())
			{
				Vector3f viewDir = cam.getRotation().getRotationColumn(2);
				Vector2f dir2D = new Vector2f(-Math.round(viewDir.z), Math.round(viewDir.x)); 
				if(InputHandler.hasInput("mouse_scrollinc"))
				{
					cubePositionMovement = cubePositionMovement.add(-dir2D.x * scale, 0, -dir2D.y * scale);
				}
				else if(InputHandler.hasInput("mouse_scrolldec"))
				{
					cubePositionMovement = cubePositionMovement.add(dir2D.x * scale, 0, dir2D.y*scale);
				}
			}
		}
		else if(InputHandler.hasInput("controlmask"))
		{
			if(voxelEditor.hasOutLineTool())
			{
				Vector3f viewDir = cam.getRotation().getRotationColumn(2);
				Vector2f dir2D = new Vector2f(Math.round(viewDir.x), Math.round(viewDir.z)); 
				if(InputHandler.hasInput("mouse_scrollinc"))
				{
					cubePositionMovement = cubePositionMovement.add(-dir2D.x * scale, 0, -dir2D.y*scale);
				}
				else if(InputHandler.hasInput("mouse_scrolldec"))
				{
					cubePositionMovement = cubePositionMovement.add(dir2D.x * scale, 0, dir2D.y*scale);
				}
				else if(InputHandler.hasInput("mouse_primary"))
				{
					if(!voxelEditor.hasGUICollision())
					{
						Vector3f newPosition = voxelEditor.GetPickCubeLocation2D(1);
						
						if(!newPosition.equals(new Vector3f(-1,-1,-1)))
						{
							voxelEditor.GetCubePosition().set(newPosition);
						}
					}
				}
			}
		}
		else if(InputHandler.hasInput("mouse_mask"))
		{	
			if(InputHandler.hasInput("mouse_left"))
			{
				float val = -InputHandler.getKeyValue("mouse_left") * 100f;
				rot+= val;
				rot%=360;
				needsUpdate = true;
			}
			else if(InputHandler.hasInput("mouse_right"))
			{
				float val = InputHandler.getKeyValue("mouse_right") * 100f;
				rot+= val;
				rot%=360;
				needsUpdate = true;
			}
			
			if(InputHandler.hasInput("mouse_up"))
			{
				float val = InputHandler.getKeyValue("mouse_up") * 200f;
				roty+= val;
				roty = roty < 180 ? roty : 180;
				needsUpdate = true;
			}
			else if(InputHandler.hasInput("mouse_down"))
			{
				float val = -InputHandler.getKeyValue("mouse_down") * 200f;
				roty+= val;
				roty = roty > 0 ? roty : 0;
				needsUpdate = true;
			}
		}
		else
		{
			if(InputHandler.hasInput("mouse_scrollinc"))
			{
				Vector3f dir = cam.getLocation().subtract(lookPosition).normalize();
				
				float scrollVal = InputHandler.getKeyValue("mouse_scrollinc");
				dist+= scrollVal;
				dist = dist < 1 ? 1: dist;
				
				float heightdif = scrollVal * dir.y;
				height+= heightdif;
				height = height < 1 ? 1: height;
				needsUpdate = true;
			}
			else if(InputHandler.hasInput("mouse_scrolldec") )
			{
				Vector3f dir = cam.getLocation().subtract(lookPosition).normalize();
				float scrollVal = -InputHandler.getKeyValue("mouse_scrolldec");
				dist+= scrollVal;
				dist = dist < 1 ? 1: dist;
				
				float heightdif = scrollVal * dir.y;
				height+= heightdif;
				height = height < 1 ? 1: height;
				needsUpdate = true;
			}
			else if(InputHandler.hasInput("zoomin") )
			{
				Vector3f dir = cam.getLocation().subtract(lookPosition).normalize();
				float scrollVal = -0.5f * voxelEditor.GetScale();
				dist+= scrollVal;
				dist = dist < 1 ? 1: dist;
				
				float heightdif = scrollVal * dir.y;
				height+= heightdif;
				height = height < 1 ? 1: height;
				needsUpdate = true;
			}
			else if(InputHandler.hasInput("zoomout") )
			{
				Vector3f dir = cam.getLocation().subtract(lookPosition).normalize();
				float scrollVal = 0.5f * voxelEditor.GetScale();
				dist+= scrollVal;
				dist = dist < 1 ? 1: dist;
				
				float heightdif = scrollVal * dir.y;
				height+= heightdif;
				height = height < 1 ? 1: height;
				needsUpdate = true;
			}
		}
		boolean hasCenterUpdate = false;
		float sens = 0.1f;
		if(InputHandler.hasInput("forward"))
		{
			Vector3f viewDir = cam.getRotation().getRotationColumn(2);
			Vector2f dir2D = new Vector2f(Math.round(viewDir.x), Math.round(viewDir.z)); 
			Vector3f offset = new Vector3f(dir2D.x * scale, 0, dir2D.y*scale);
			voxelEditor.changeCenter(offset.x * sens,0,offset.z * sens);
			hasCenterUpdate = true;
			needsUpdate = true;
		}
		if(InputHandler.hasInput("backward"))
		{
			Vector3f viewDir = cam.getRotation().getRotationColumn(2);
			Vector2f dir2D = new Vector2f(Math.round(viewDir.x), Math.round(viewDir.z)); 
			Vector3f offset = new Vector3f(-dir2D.x * scale, 0, -dir2D.y*scale);
			voxelEditor.changeCenter(offset.x * sens,0,offset.z * sens);
			hasCenterUpdate = true;
			needsUpdate = true;
		}
		if(InputHandler.hasInput("left"))
		{
			Vector3f viewDir = cam.getRotation().getRotationColumn(2);
			Vector2f dir2D = new Vector2f(-Math.round(viewDir.z), Math.round(viewDir.x)); 
			Vector3f offset = new Vector3f(-dir2D.x * scale, 0, -dir2D.y * scale);
			voxelEditor.changeCenter(offset.x * sens,0,offset.z * sens);
			hasCenterUpdate = true;
			needsUpdate = true;
		}
		if(InputHandler.hasInput("right"))
		{
			Vector3f viewDir = cam.getRotation().getRotationColumn(2);
			Vector2f dir2D = new Vector2f(-Math.round(viewDir.z), Math.round(viewDir.x)); 
			Vector3f offset = new Vector3f(dir2D.x * scale, 0, dir2D.y * scale);
			voxelEditor.changeCenter(offset.x * sens, 0,offset.z * sens);
			hasCenterUpdate = true;
			needsUpdate = true;
		}
		
		if(InputHandler.hasInput("up"))
		{
			voxelEditor.changeCenter(0,sens,0);
			hasCenterUpdate = true;
			needsUpdate = true;
		}
		
		if(InputHandler.hasInput("down"))
		{
			voxelEditor.changeCenter(0,-sens,0);
			hasCenterUpdate = true;
			needsUpdate = true;
		}
		
		
		if(InputHandler.hasInput("space"))
		{
			if(voxelEditor.hasOutLineTool())
			{
				voxelEditor.fillCube();
			}
		}
		if(InputHandler.hasInput("empty"))
		{
			if(voxelEditor.hasOutLineTool())
			{
				voxelEditor.emptyCube();
			}
		}
		
		if(System.currentTimeMillis()-lastCubeToggle > 100)
		{
			if(InputHandler.hasInput("cubetoggle"))
			{
				voxelEditor.setHasOutLineTool(!voxelEditor.hasOutLineTool());
				lastCubeToggle = System.currentTimeMillis();
			}
		}
		
		if(needsUpdate)
		{
			updateCameraPosition();
		}
		
		Vector3f cubePosition = voxelEditor.GetCubePosition();
		
		Vector3f newPosition = new Vector3f(cubePosition.add(cubePositionMovement));
		newPosition.x = newPosition.x > 0 ? newPosition.x : 0;
		newPosition.y = newPosition.y > 0 ? newPosition.y : 0;
		newPosition.z = newPosition.z > 0 ? newPosition.z : 0;
		
		newPosition.x = newPosition.x < maxX ? newPosition.x : maxX;
		newPosition.y = newPosition.y < maxY ? newPosition.y : maxY;
		newPosition.z = newPosition.z < maxZ ? newPosition.z : maxZ;
		cubePosition.set(newPosition);
		
		DrawCubeOutLine();
		
		DrawCenterOutLine();
		
	}
	
	public void DrawCenterOutLine()
	{
		Geometry geom = voxelEditor.GetCenterModel();
		if(geom == null)
		{
			return;
		}
		
		if(!voxelEditor.infoNode.hasChild(geom))
    	{
    		voxelEditor.infoNode.attachChild(geom);
    	}
    	else
    	{
    		voxelEditor.infoNode.detachChildNamed("CenterOutLine");
    		voxelEditor.infoNode.attachChild(geom);
    	}
		Vector3f offset = voxelEditor.getOffSet();
		geom.setLocalTranslation(center.add(offset).add(lookOffset).mult(voxelEditor.GetScale()));
		geom.setLocalScale(voxelEditor.GetScale());
	}
	
	public void DrawCubeOutLine()
    {
		
        if(voxelEditor.hasOutLineTool())
        {
        	if(voxelEditor.infoNode.getChild("CubeOutLine") == null)
        	{
        		voxelEditor.infoNode.attachChild(voxelEditor.GetCubeModel());
        	}
        	else if(voxelEditor.infoNode.getChild("CubeOutLine") != null && !voxelEditor.infoNode.hasChild(voxelEditor.GetCubeModel()))
        	{
        		voxelEditor.infoNode.detachChildNamed("CubeOutLine");
        		voxelEditor.infoNode.attachChild(voxelEditor.GetCubeModel());
        	}
        	voxelEditor.GetCubeModel().setLocalTranslation(voxelEditor.GetCubePosition());
        	voxelEditor.GetCubeModel().setLocalScale(voxelEditor.GetScale());
        }
        else
        {
        	if(voxelEditor.infoNode.getChild("CubeOutLine") != null)
        	{
        		voxelEditor.infoNode.detachChildNamed("CubeOutLine");
        	}
        }
    }
	
	//TODO
	//change positions to voxelEditor positions
	public void updateCameraPosition()
	{
		Vector2f newPosition = MathUtil.RotatePoint(new Vector2f(dist,0), rot);
		Vector2f newPositionY = MathUtil.RotatePoint(new Vector2f(dist,0), roty);
	
		Vector3f newCameraPosition = new Vector3f(newPosition.x, newPositionY.x, newPosition.y);
		Vector3f offset = voxelEditor.getOffSet();
		cam.setLocation(newCameraPosition.add(lookPosition.add(center).add(offset).mult(voxelEditor.GetScale())));
		cam.lookAt(lookPosition.add(center).add(offset).add(lookOffset).mult(voxelEditor.GetScale()), new Vector3f(0,1,0));
		cam.update();
	}
	
}
