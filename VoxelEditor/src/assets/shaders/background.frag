
uniform sampler2D m_ColorMap;
uniform vec2 m_Position;
uniform float m_Distance;
uniform float m_IDX;
uniform float m_IDZ;
uniform float m_TexSize;
uniform float m_TileSize;

in vec2 texCoord1;
in vec3 pp;
in vec3 normal;
out vec4 inColor;

void get_texel(inout float x, inout float y)
{
	float num = 32;
		
	float x2 = mod((x * num),1)+0.5;
	float y2 = mod((y * num),1)+0.5;
	
	x = x2/num;
	y = y2/num;
}

void main()
{
	float pidx = floor(pp.x / 32);
	float pidz = floor(pp.z / 32);
	if( abs(m_IDX - pidx) <= m_Distance && abs(m_IDZ - pidz) <= m_Distance)
	{
		discard;
	}

	float tx = fract(pp.x) * m_TexSize;
	float ty = fract(pp.z) * m_TexSize;
	get_texel(tx,ty);
	
	vec4 keyColor = texture(m_ColorMap, vec2(texCoord1.x + tx, texCoord1.y + ty));
	inColor = keyColor;
}



