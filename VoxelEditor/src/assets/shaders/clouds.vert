
uniform mat4 g_WorldViewProjectionMatrix;
uniform sampler2D m_CloudMap;
uniform int m_X;
uniform int m_Z;
uniform int m_BufferSize;
uniform int m_CloudMapSize;
in vec3 inPosition;

out vec3 pp;
out float height;

void main()
{
	float x = inPosition.x + m_X;
	float y = 200;
	float z = inPosition.z + m_Z;
	
	float px = float(x/m_CloudMapSize);
	float py = float(z/m_CloudMapSize);
	
	float h = float(texture(m_CloudMap, vec2(px,py)).x * 40.0);
	float d = float(texture(m_CloudMap, vec2(px,py)).y * 20.0);
	
	height = 0;
	
	if(h > 0 && d > 0)
	{
		if(gl_VertexID <= m_BufferSize)
		{
			y -= d;
			height = d;
		}
		else
		{
			y += h;
			height = h;
		}
	}
	
	vec3 curpos = vec3(x,y,z);
	pp = inPosition;
    gl_Position = g_WorldViewProjectionMatrix * vec4(curpos, 1.0);
}