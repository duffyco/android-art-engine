attribute vec4 a_position;
attribute vec2 a_texCoord;
uniform float uAngle;
varying vec2 v_texCoord;

mat4 rotationMatrix(vec3 axis, float angle)
{
        axis = normalize(axis);
        float s = sin(angle);
        float c = cos(angle);
        float oc = 1.0 - c;


        return mat4(    cos(angle), -sin(angle),  0.0,  0.0,
                        sin(angle),  cos(angle),  0.0,  0.0,
                        0.0,  0.0,                1.0,  0.0,
                        0.0,  0.0,                0.0,  1.0);
}


 mat4 glOrthoMatrix()
                {
                    return mat4( 1085.0/1920.0 , 0.0, 0.0, 0.0,
                                 0.0, 1.0 ,  0.0, 0.0,
                                 0.0, 0.0, 1.0, 0.0,
                                 0.0, 0.0, 0.0, 1.0 );

                }

mat4 objectScale()
                {
                    return mat4( 1920.0/1085.0 , 0.0, 0.0, 0.0,
                                 0.0, 1.0 ,  0.0, 0.0,
                                 0.0, 0.0, 1.0, 0.0,
                                 0.0, 0.0, 0.0, 1.0 );
                }


void main()
{
   mat4 RotationMatrix = rotationMatrix ( vec3( 0.0, 0.0, 1.0 ), uAngle );
   mat4 OrthoMatrix = glOrthoMatrix();
   mat4 ObjectMatrix = objectScale();

   gl_Position = OrthoMatrix * RotationMatrix * ObjectMatrix * a_position;
   v_texCoord = a_texCoord;
}
