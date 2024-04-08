#type vertex
#version 420
layout (location=0) in vec3 vPos;
layout (location=1) in vec4 vColor;
layout (location=2) in vec2 vTexCoords;
layout (location=3) in float vTexId;

uniform mat4 uProjection;
uniform mat4 uView;


out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main()
{
    fColor = vColor;
    fTexCoords = vTexCoords;
    fTexId = vTexId;

    gl_Position = uProjection * uView * vec4(vPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];

out vec4 color;

void main()
{
    if(fTexId > 0) {
        int id = int(fTexId);
        color = fColor * texture(uTextures[id], fTexCoords);
    } else {
        color = fColor;
    }
}