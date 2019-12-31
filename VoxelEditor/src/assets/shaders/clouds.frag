

in vec3 pp;
in float height;
out vec4 inColor;

void main()
{
	float color = height/5.0;
	
	if(height == 0.0)
	{
		discard;
	}
	
	if(color < 0.1)
	{
		color = 0.1;
	}
	
	vec4 keyColor = vec4(1.0, 1.0, 1.0, 1.0);
	inColor = keyColor;
}



