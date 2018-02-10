#version 410 core

in vec3 wld_nrm;
in float wld_y;

out vec4 color;

void main(void) {

	if(wld_y < -5.0)
		color = vec4(1.0, 0.0, 0.0, 1.0);
	else if(wld_y < 0.0)
		color = vec4(1.0 - ((5.0 + wld_y) / 5.0), ((5.0 + wld_y) / 5.0), 0.0, 1.0);
	else if(wld_y < 10.0)
		color = vec4(0.0, 1.0, 0.0, 1.0);
	else
		color = vec4(0.0, 0.0, 1.0, 1.0);
	
}