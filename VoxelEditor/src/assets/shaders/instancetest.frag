
uniform sampler2D m_ColorMap;
uniform float m_TexSize;
uniform float m_TileSize;

in vec2 texCoord1;
in vec3 pp;
in vec3 normal;
out vec4 inColor;

void normuv(vec3 vec, vec3 pos, bool rot, out vec2 uv)
{
	bool PosX = pos.x > 0;
	bool PosY = pos.y > 0;
	bool PosZ = pos.z > 0;

	float absX = abs(vec.x);
	float absY = abs(vec.y);
	float absZ = abs(vec.z);

	if (PosX && absX >= absY && absX >= absZ)
	{
		if(rot)
		{
			uv = vec2(pos.z, pos.y);
		}
		else
		{
			uv = vec2(pos.y, pos.z);
		}
	}
	else if (!PosX && absX >= absY && absX >= absZ)
	{
		if(rot)
		{
			uv = vec2(pos.z, pos.y);
		}
		else
		{
			uv = vec2(pos.y, pos.z);
		}
	}
	else if (PosY && absY >= absX && absY >= absZ)
	{
		if(rot)
		{
			uv = vec2(pos.x, pos.z);
		}
		else
		{
			uv = vec2(pos.z, pos.x);
		}
	}
	else if (!PosY && absY >= absX && absY >= absZ)
	{
		if(rot)
		{
			uv = vec2(pos.x, pos.z);
		}
		else
		{
			uv = vec2(pos.z, pos.x);
		}
	}
	else if (PosZ && absZ >= absX && absZ >= absY)
	{
		if(rot)
		{
			uv = vec2(pos.x, pos.y);
		}
		else
		{
			uv = vec2(pos.y, pos.x);
		}
	}
	else if (!PosZ && absZ >= absX && absZ >= absY)
	{
		if(rot)
		{
		uv = vec2(pos.x, pos.y);
		}
		else
		{
		}
	}
	else
	{
		if(rot)
		{
			uv = vec2(pos.x, pos.z);
		}
		else
		{
			uv = vec2(pos.z, pos.x);
		}
	}
}

void get_texel(inout float x, inout float y)
{
	float num = 4096.0;
		
	float x2 = mod((x * num), (num/m_TileSize-2))+1;
	float y2 = mod((y * num), (num/m_TileSize-2))+1;
	
	x = x2/num;
	y = y2/num;
}

void main()
{
	vec3 pos = vec3(fract(pp.x),fract(pp.y),fract(pp.z));
	vec2 tp = vec2(0,0);
	normuv(normal,pos,true,tp);
	
	float tx = tp.x * m_TexSize;
	float ty = tp.y * m_TexSize;
	get_texel(tx,ty);
	
	vec4 keyColor = texture(m_ColorMap, vec2(texCoord1.x + tx, texCoord1.y + ty));
	
	if(keyColor.a < 0.0)
	{
		discard;
	}
	
	inColor = keyColor;
}



