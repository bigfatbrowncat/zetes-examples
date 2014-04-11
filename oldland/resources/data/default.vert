#version 130

in vec3 in_mesh_vertexPosition;
in vec2 in_mesh_textureCoords;
in vec3 in_mesh_normal;

uniform mat4 uni_matrix;
uniform mat4 uni_globalMatrix;
uniform mat3 uni_normalMatrix;

out vec2 textureCoords;

void main()
{
    gl_Position = uni_globalMatrix * uni_matrix * vec4(in_mesh_vertexPosition, 1.0);

    textureCoords = in_mesh_textureCoords;
}
