#version 410 core

layout(location = 0) in vec3 mdl_pos;
layout(location = 1) in vec3 mdl_nrm;

out vec3 wld_nrm;
out float wld_y;

uniform mat4 view_matrix;
uniform mat4 proj_matrix;

void main(void) {

	wld_nrm = mdl_nrm;
	
	wld_y = mdl_pos.y;
	
	gl_Position = proj_matrix * view_matrix * vec4(mdl_pos, 1.0);

}