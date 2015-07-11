#version 330 core

in vec3 tex;

uniform sampler2DArray textures;

out vec4 color;

void main() {
	color = texture(textures, tex);
}