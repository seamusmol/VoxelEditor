package Console;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 * Notes
 * allowed classes can contain duplicate references of the same object
 */
public class Parser{

	private static String scriptExtensionName = ".cfg";
	private static Map<String,String> allowedMethods;
	private static Map<String,Object> allowedClasses;
	
	//method name, class name
	public Parser()
	{
		allowedMethods = new HashMap<String,String>();
		allowedClasses = new HashMap<String, Object>();
	}
	public void addAllowedClass(Object Object, String Name)
	{
		allowedClasses.put(Name, Object);
	}
	
	public void addAllowedMethod(String MethodName, String ClassName)
	{
		allowedMethods.put(MethodName, ClassName);
	}
	
	public static void parseString(String Command)
	{
		String commandLine = Command.toLowerCase();
		String[] commandBits = commandLine.split(" ");
		String commandMethod = commandBits[0];
		
		Object[] arguments = new Object[commandBits.length-1];
		
		for(int i = 1; i < commandBits.length; i++)
		{
			arguments[i-1] = commandBits[i];
		}
		
		if(commandMethod != null )
		{
			executeCommand(commandMethod, arguments);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void executeCommand(String MethodName, Object[] Arguments)
	{
		for(Map.Entry<String,String> entry: allowedMethods.entrySet())
		{
			if(MethodName.equals(entry.getKey()))
			{
				Class someClass = null;
				try 
				{
					someClass = Class.forName(entry.getValue());
					for(int i = 0; i < someClass.getMethods().length; i++)
					{
						if(MethodName.equals(someClass.getMethods()[i].getName()))
						{
							someClass.getMethods()[i].invoke( allowedClasses.get(entry.getValue()), (Object)Arguments);
							return;
						}
					}
				} 
				catch(ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) 
				{
//					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public static Object getMethodValue(Method Method)
	{
		try {
			return (Object)Method.invoke(null, null);
		} 
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static void execute(String Value)
	{
		String fileName = Value + scriptExtensionName;
		try 
		{
//			System.out.println(System.getProperty("os.name"));
//			if(System.getProperty("os.name").equals("Linux"))
//			{
//				Runtime runtime = Runtime.getRuntime();
//				runtime.exec(new String[] { "/bin/chmod", "777",new File("src/Assets/config/" + fileName).getPath()});
//			}
			BufferedReader script = new BufferedReader( new FileReader("src/assets/config/" + fileName));
			for(String line = script.readLine(); line != null ; line = script.readLine())
			{
				Parser.parseString(line);
			}
			script.close();
		} 
		catch(FileNotFoundException e) 
		{
//			System.out.println(e.getMessage());
			return;
		}
		catch(IOException e) 
		{
//			System.out.println(e.getMessage());
			return;
		}
	}
	
//	public static void test(Object[] Value)
//	{
//		System.out.println(Value[0].toString());
//	}
	
}
