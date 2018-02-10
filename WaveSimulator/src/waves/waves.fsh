#version 410 core

in vec3 wld_nrm;
in float wld_y;

out vec4 color;

const vec3 blue = vec3(0.0, 0.467, 0.745);

void main(void) {

	color = vec4(blue * wld_nrm.y, 1.0);
	
}