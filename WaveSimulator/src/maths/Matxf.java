package maths;

public abstract class Matxf {
	
	protected static final int MATRIX_3x3 = 9;
	protected static final int MATRIX_4x4 = 16;

	protected float[] m;
	
	protected Matxf(int size) {
		this.m = new float[size];
	}
	
	public float[] getMatrix() {
		return m;
	}
	
	public void scale(float scalar) {
		for(int i = 0; i < m.length; i++) {
			m[i] *= scalar;
		}
	}
}
