package waves;

import main.Shader;
import maths.Mat3f;
import maths.Mat4f;

public class WaveShader extends Shader {
	
	private int 	loc_view_matrix,
				loc_proj_matrix,
				loc_vect_matrix;

	public WaveShader() {
		super("src/waves/waves.vsh", "src/waves/waves.fsh");
	}

	@Override
	protected void find_uniforms() {
		loc_view_matrix = super.find_uniform("view_matrix");
		loc_proj_matrix = super.find_uniform("proj_matrix");
		loc_vect_matrix = super.find_uniform("vect_matrix");
	}
	
	public void load_view_mat(Mat4f view) {
		super.load_mat(loc_view_matrix, view);
	}
	
	public void load_proj_mat(Mat4f proj) {
		super.load_mat(loc_proj_matrix, proj);
	}
	
	public void load_vect_mat(Mat3f vect) {
		super.load_mat(loc_vect_matrix, vect);
	}
}
