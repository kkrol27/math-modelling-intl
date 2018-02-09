package maths;

public class Mat4f extends Matxf {

	public Mat4f() {
		super(Matxf.MATRIX_4x4);
	}
	
	public Mat4f setIdentity() {
		m[0] = 1.0f;
						m[5] = 1.0f;
										m[10] = 1.0f;
														m[15] = 0.0f;
		return this;
	}
	
	public Mat4f setMatrix(Vec4f v1, Vec4f v2, Vec4f v3, Vec4f v4) {
		m[0] = v1.x;	m[4] = v2.x;	m[8]  = v3.x;	m[12] = v4.x;
		m[1] = v1.y;	m[5] = v2.y;	m[9]  = v3.y;	m[13] = v4.y;
		m[2] = v1.z;	m[6] = v2.z;	m[10] = v3.z;	m[14] = v4.z;
		m[3] = v1.w;	m[7] = v2.w;	m[11] = v3.w;	m[15] = v4.w;
		return this;
	}
	
	public Mat4f setMatrix(Mat3f x) {
		m[0] = x.m[0];	m[4] = x.m[3];	m[8] = x.m[6];
		m[1] = x.m[1];	m[5] = x.m[4];	m[9] = x.m[7];
		m[2] = x.m[2];	m[6] = x.m[5];	m[10] = x.m[8];
														m[15] = 1.0f;
		return this;
	}
	
	public Mat4f setXRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		m[0] = 1.0f;
						m[5] = cos;		m[9] = -sin;
						m[6] = sin;		m[10] = cos;
														m[15] = 1.0f;
		return this;
	}
	
	public Mat4f setYRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		m[0] = cos;						m[8] = sin;
						m[5] = 1.0f;
		m[2] = -sin;					m[10] = cos;
														m[15] = 1.0f;
		return this;
	}
	
	public Mat4f setZRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		m[0] = cos;		m[4] = -sin;
		m[1] = sin;		m[5] = cos;
										m[10] = 1.0f;
														m[15] = 1.0f;
		return this;
	}
	
	public Mat4f setTranslation(Vec3f vec) {
		m[0] = 1.0f;									m[12] = vec.x;
						m[5] = 1.0f;					m[13] = vec.y;
										m[10] = 1.0f;	m[14] = vec.z;
														m[15] = 1.0f;
		return this;
	}
	
	public void addXRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		float[] t = new float[4];
		t[0] = m[1]*cos - m[2]*sin;		t[1] = m[5]*cos - m[6]*sin;		t[2]  = m[9]*cos - m[10]*sin;	t[3]  = m[13]*cos - m[14]*sin;
		m[2] = m[1]*sin + m[2]*cos;		m[6] = m[5]*sin + m[6]*cos;		m[10] = m[9]*sin + m[10]*cos;	m[14] = m[13]*sin + m[14]*cos;
		m[1]  = t[0];
		m[5]  = t[1];
		m[9]  = t[2];
		m[13] = t[3];
	}
	
	public void addYRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		float[] t = new float[4];
		t[0] =  m[0]*cos + m[2]*sin;	t[1] =  m[4]*cos + m[6]*sin;	t[2]  =  m[8]*cos + m[10]*sin;	t[3]  =  m[12]*cos + m[14]*sin;
		m[2] = -m[0]*sin + m[2]*cos;	m[6] = -m[4]*sin + m[6]*cos;	m[10] = -m[8]*sin + m[10]*cos;	m[14] = -m[12]*sin + m[14]*cos;
		m[0]  = t[0];
		m[4]  = t[1];
		m[8]  = t[2];
		m[12] = t[3];
	}
	
	public void addZRotation(float a) {
		float cos = (float) Math.cos(a);
		float sin = (float) Math.sin(a);
		float[] t = new float[3];
		t[0] = m[0]*cos - m[1]*sin;		t[1] = m[4]*cos - m[5]*sin;		t[2] = m[8]*cos - m[9]*sin;
		m[1] = m[0]*sin + m[1]*cos;		m[5] = m[4]*sin + m[5]*cos;		m[9] = m[8]*sin + m[9]*cos;
		m[0] = t[0];
		m[4] = t[1];
		m[8] = t[2];
	}
	
	public void addTranslation(Vec3f vec) {
		m[0] = m[0] + m[3]*vec.x;	m[4] = m[4] + m[7]*vec.x;	m[8] = m[8] + m[11]*vec.x;		m[12] = m[12] + m[15]*vec.x;
		m[1] = m[1] + m[3]*vec.y;	m[5] = m[5] + m[7]*vec.y;	m[9] = m[9] + m[11]*vec.y;		m[13] = m[13] + m[15]*vec.y;
		m[2] = m[2] + m[3]*vec.z;	m[6] = m[6] + m[7]*vec.z;	m[10] = m[10] + m[11]*vec.z;	m[14] = m[14] + m[15]*vec.z;
	}
	
	public void transform(Mat4f x) {
		float[] t = new float[12];
		t[0] = m[0]*x.m[0] + m[4]*x.m[1] + m[8]*x.m[2] + m[12]*x.m[3];
		t[1] = m[1]*x.m[0] + m[5]*x.m[1] + m[9]*x.m[2] + m[13]*x.m[3];
		t[2] = m[2]*x.m[0] + m[6]*x.m[1] + m[10]*x.m[2] + m[14]*x.m[3];
		x.m[3] = m[3]*x.m[0] + m[7]*x.m[1] + m[11]*x.m[2] + m[15]*x.m[3];
		
		t[3] = m[0]*x.m[4] + m[4]*x.m[5] + m[8]*x.m[6] + m[12]*x.m[7];
		t[4] = m[1]*x.m[4] + m[5]*x.m[5] + m[9]*x.m[6] + m[13]*x.m[7];
		t[5] = m[2]*x.m[4] + m[6]*x.m[5] + m[10]*x.m[6] + m[14]*x.m[7];
		x.m[7] = m[3]*x.m[4] + m[7]*x.m[5] + m[11]*x.m[6] + m[15]*x.m[7];
		
		
		t[6] = m[0]*x.m[8] + m[4]*x.m[9] + m[8]*x.m[10] + m[12]*x.m[11];
		t[7] = m[1]*x.m[8] + m[5]*x.m[9] + m[9]*x.m[10] + m[13]*x.m[11];
		t[8] = m[2]*x.m[8] + m[6]*x.m[9] + m[10]*x.m[10] + m[14]*x.m[11];
		x.m[11] = m[3]*x.m[8] + m[7]*x.m[9] + m[11]*x.m[10] + m[15]*x.m[11];
		
		t[9] = m[0]*x.m[12] + m[4]*x.m[13] + m[8]*x.m[14] + m[12]*x.m[15];
		t[10] = m[1]*x.m[12] + m[5]*x.m[13] + m[9]*x.m[14] + m[13]*x.m[15];
		t[11] = m[2]*x.m[12] + m[6]*x.m[13] + m[10]*x.m[14] + m[14]*x.m[15];
		x.m[15] = m[3]*x.m[12] + m[7]*x.m[13] + m[11]*x.m[14] + m[15]*x.m[15];
		
		x.m[0] = t[0];	x.m[4] = t[3];	x.m[8] = t[6];	x.m[12] = t[9];
		x.m[1] = t[1];	x.m[5] = t[4];	x.m[9] = t[7];	x.m[13] = t[10];
		x.m[2] = t[2];	x.m[6] = t[5];	x.m[10] = t[8];	x.m[14] = t[11];
	}
	
	public void transform(Vec4f vec) {
		float x, y, z;
		x = m[0]*vec.x + m[4]*vec.y + m[8]*vec.z + m[12]*vec.w;
		y = m[1]*vec.x + m[5]*vec.y + m[9]*vec.z + m[13]*vec.w;
		z = m[2]*vec.x + m[6]*vec.y + m[10]*vec.z + m[14]*vec.w;
		vec.w = m[3]*vec.x + m[7]*vec.y + m[11]*vec.z + m[15]*vec.w;
		vec.x = x;
		vec.y = y;
		vec.z = z;
	}
	
	@Override
	public String toString() {
		return "[ " + m[0] + " , " + m[4] + " , " + m[8] + " , " + m[12] + " ]" + System.lineSeparator() +
			   "[ " + m[1] + " , " + m[5] + " , " + m[9] + " , " + m[13] + " ]" + System.lineSeparator() +
			   "[ " + m[2] + " , " + m[6] + " , " + m[10] + " , " + m[14] + " ]" + System.lineSeparator() +
			   "[ " + m[3] + " , " + m[7] + " , " + m[11] + " , " + m[15] + " ]";
	}
}
