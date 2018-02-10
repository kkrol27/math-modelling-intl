#version 410 core

in vec3 wld_nrm;

out vec4 color;

uniform mat3 vect_matrix;

void main(void) {

	color = vec4(vect_matrix * wld_nrm, 1.0);
	
}