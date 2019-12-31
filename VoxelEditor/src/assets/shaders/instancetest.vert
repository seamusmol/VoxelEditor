
uniform mat4 g_WorldViewProjectionMatrix;

in vec3 inPosition;
in vec2 inTexCoord;
in vec2 inTexCoord2;
in vec2 inTexCoord3;
in vec3 inNormal;

out vec2 texCoord1;
out vec3 pp;
out vec3 normal;

void main()
{
	float x = inPosition.x + inTexCoord2.x;
	float y = inPosition.y + inTexCoord2.y;
	float z = inPosition.z + inTexCoord3.x;
	
	vec3 pos = vec3(x,y,z);
	
	texCoord1 = inTexCoord;
	pp = inPosition;
	normal = inNormal;
    gl_Position = g_WorldViewProjectionMatrix * vec4(pos, 1.0);
}