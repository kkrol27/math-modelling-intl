#version 410 core

in vec3 wld_nrm;

out vec4 color;

uniform mat3 vect_matrix;

void main(void) {

	vec3 n = ((vect_matrix * wld_nrm) / 2.0) + 0.5;

	color = vec4(n.xyz, 1.0);
	
}