package maths;

public class Mat3f extends Matxf {

	public Mat3f() {
		super(Matxf.MATRIX_3x3);
	}
	
	public Mat3f setIdentity() {
		m[0] = 1.0f;
						m[4] = 1.0f;
										m[8] = 1.0f;
		return this;
	}
	
	public Mat3f setMatrix(Vec3f v1, Vec3f v2, Vec3f v3) {
		m[0] = v1.x;	m[3] = v2.x;	m[6] = v3.x;
		m[1] = v1.y;	m[4] = v2.y;	m[7] = v3.y;
		m[2] = v1.z;	m[5] = v2.z;	m[8] = v3.z;
		return this;
	}
	
	public Mat3f setXRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		m[0] = 1.0f;
						m[4] = cos;		m[7] = -sin;
						m[5] = sin;		m[8] = cos;
		return this;
	}
	
	public Mat3f setYRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		m[0] = cos;						m[6] = sin;
						m[4] = 1.0f;
		m[2] = -sin;					m[8] = cos;
		return this;
	}
	
	public Mat3f setZRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		m[0] = cos;		m[3] = -sin;
		m[1] = sin;		m[4] = cos;
										m[8] = 1.0f;
		return this;
	}
	
	public void addXRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		float[] t = new float[3];
		t[0] = m[1]*cos - m[2]*sin;		t[1] = m[4]*cos - m[5]*sin;		t[2] = m[7]*cos - m[8]*sin;
		m[2] = m[1]*sin + m[2]*cos;		m[5] = m[4]*sin + m[5]*cos;		m[8] = m[7]*sin + m[8]*cos;
		m[1] = t[0];
		m[4] = t[1];
		m[7] = t[2];
	}
	
	public void addYRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		float[] t = new float[3];
		t[0] =  m[0]*cos + m[2]*sin;	t[1] =  m[3]*cos + m[5]*sin;	t[2] =  m[6]*cos + m[8]*sin;
		m[2] = -m[0]*sin + m[2]*cos;	m[5] = -m[3]*sin + m[5]*cos;	m[8] = -m[6]*sin + m[8]*cos;
		m[0] = t[0];
		m[3] = t[1];
		m[6] = t[2];
	}
	
	public void addZRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		float[] t = new float[3];
		t[0] = m[0]*cos - m[1]*sin;		t[1] = m[3]*cos - m[4]*sin;		t[2] = m[6]*cos - m[7]*sin;
		m[1] = m[0]*sin + m[1]*cos;		m[4] = m[3]*sin + m[4]*cos;		m[7] = m[6]*sin + m[7]*cos;
		m[0] = t[0];
		m[3] = t[1];
		m[6] = t[2];
	}
	
	public void transform(Mat3f x) {
		float[] t = new float[6];
		t[0]   = m[0]*x.m[0] + m[3]*x.m[1] + m[6]*x.m[2];		t[2]   = m[0]*x.m[3] + m[3]*x.m[4] + m[6]*x.m[5];		t[4]   = m[0]*x.m[6] + m[3]*x.m[7] + m[6]*x.m[8];
		t[1]   = m[1]*x.m[0] + m[4]*x.m[1] + m[7]*x.m[2];		t[3]   = m[1]*x.m[3] + m[4]*x.m[4] + m[7]*x.m[5];		t[5]   = m[1]*x.m[6] + m[4]*x.m[7] + m[7]*x.m[8];
		x.m[2] = m[2]*x.m[0] + m[5]*x.m[1] + m[8]*x.m[2];		x.m[5] = m[2]*x.m[3] + m[5]*x.m[4] + m[8]*x.m[5];		x.m[8] = m[2]*x.m[6] + m[5]*x.m[7] + m[8]*x.m[8];
		x.m[0] = t[0];	x.m[3] = t[2];	x.m[6] = t[4];
		x.m[1] = t[1];	x.m[4] = t[3];	x.m[7] = t[5];
	}
	
	public void transform(Vec3f vec) {
		float x, y;
		x = m[0]*vec.x + m[3]*vec.y + m[6]*vec.z;
		y = m[1]*vec.x + m[4]*vec.y + m[7]*vec.z;
		vec.z = m[2]*vec.x + m[5]*vec.y + m[8]*vec.z;
		vec.x = x;
		vec.y = y;
	}
	
	@Override
	public String toString() {
		return "[ " + m[0] + " , " + m[3] + " , " + m[6] + " ]" + System.lineSeparator() +
			   "[ " + m[1] + " , " + m[4] + " , " + m[7] + " ]" + System.lineSeparator() +
			   "[ " + m[2] + " , " + m[5] + " , " + m[8] + " ]";
	}
}
