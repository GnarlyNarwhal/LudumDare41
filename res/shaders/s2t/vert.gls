#version 330 core

uniform mat4 mvp;

layout (location = 0) in vec3 vertices;
layout (location = 1) in vec2 itexCoords;

out vec2 texCoords;

void main() {
	texCoords = itexCoords;
	gl_Position = mvp * vec4(vertices, 1);
}