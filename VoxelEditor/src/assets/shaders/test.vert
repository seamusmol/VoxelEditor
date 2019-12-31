
uniform mat4 g_WorldViewProjectionMatrix;
in vec3 inPosition;
in vec2 inTexCoord;
in vec3 inNormal;

out vec2 texCoord1;
out vec3 pp;
out vec3 normal;

void main()
{
	texCoord1 = inTexCoord;
	pp = inPosition;
	normal = inNormal;
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
}