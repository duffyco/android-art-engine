precision mediump float;

varying vec2 v_texCoord;
uniform sampler2D s_baseMap;
uniform float f_alpha;

void main()                             
{                                       
	vec4 baseColor;                       

  	baseColor = texture2D( s_baseMap, v_texCoord );

  	gl_FragColor = baseColor * f_alpha;
}