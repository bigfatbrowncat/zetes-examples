#version 130
uniform sampler2D uni_texture;

uniform float uni_textureWidth;
uniform float uni_textureHeight;

uniform vec4 uni_foregroundColor;
uniform vec4 uni_backgroundColor;

in vec2 textureCoords;
in vec4 lightPosition;


out vec4 color;

void main()
{
	float brightness = 2.0;
	float ambientBrightness = 0.5;

	float k = 1;
    float a00 = texture(uni_texture, textureCoords.st).a;
	float arg = a00;
	float rf = 1.0;
	int r = int(rf);
	for (int i = -r; i <= r; i++)
	for (int j = -r; j <= r; j++)
	{
		vec2 q = vec2(textureCoords.s + float(i) / uni_textureWidth, textureCoords.t + float(j) / uni_textureHeight);
	    float axy = texture(uni_texture, q).a;
	    if (axy >= a00)
	    {
	    	arg += axy;
	    	k += 1;
	    }
	}
	arg /= k;
    
    color = vec4(
    	uni_foregroundColor.r * arg + uni_backgroundColor.r * (1 - arg), 
    	uni_foregroundColor.g * arg + uni_backgroundColor.g * (1 - arg), 
		uni_foregroundColor.b * arg + uni_backgroundColor.b * (1 - arg),
		uni_foregroundColor.a * arg + uni_backgroundColor.a * (1 - arg));
}
