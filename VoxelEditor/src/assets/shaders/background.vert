
uniform mat4 g_WorldViewProjectionMatrix;
attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

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