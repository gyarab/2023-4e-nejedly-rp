#type vertex
#version 420
layout (location = 0) in vec3 vPos;
layout (location = 1) in vec3 vColor;

out vec3 fColor;


uniform mat4 uView;
uniform mat4 uProjection;

void main()
{
    fColor = vColor;

    gl_Position = uProjection * uView * vec4(vPos, 1.0);
}

#type fragment
#version 330 core
in vec3 fColor;

out vec4 color;

void main()
{
    color = vec4(fColor, 1.0);
}


