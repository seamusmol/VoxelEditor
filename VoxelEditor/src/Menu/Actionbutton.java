package Menu;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import Console.Parser;

public class Actionbutton extends Button{

	/**
	 * Actionbutton generates command onClick.
	 * 
	 * @param Arguments
	 * @param ParentNode
	 * @param ScreenWidth
	 * @param ScreenHeight
	 */
	public Actionbutton(Object[] Arguments, Node ParentNode, float ScreenWidth, float ScreenHeight) 
	{
		super(Arguments, ParentNode, ScreenWidth, ScreenHeight);
		
		for(int i = 9; i < Arguments.length; i++)
		{
			commandLine += Arguments[i].toString() + " ";
		}
	}
	
	@Override
	public boolean onClick(Vector2f MousePos, boolean IsPrimary)
	{
		if(hasFocus(MousePos) == 1)
		{
			Parser.parseString(commandLine);
			return true;
		}
		return false;
	}
}
