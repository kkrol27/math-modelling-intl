package maths;

public class Vec4f {

	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vec4f() {
		this(0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public Vec4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public float getLength() {
		return ((float) Math.sqrt(x*x + y*y + z*z + w*w));
	}
	
	public Vec4f getNormalized() {
		float l = getLength();
		return new Vec4f(x / l, y / l, z / l, w / l);
	}
	
	public void normalize() {
		float l = getLength();
		x /= l;
		y /= l;
		z /= l;
		w /= l;
	}
	
	public Vec4f getScaledBy(float scale) {
		return new Vec4f(scale*x, scale*y, scale*z, scale*w);
	}
	
	public void scaledBy(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		w *= scale;
	}
	
	public void setLength(float length) {
		scaledBy(length * getLength());
	}
	
	public float dot(Vec4f vec) {
		return vec.x*x + vec.y*y + vec.z*z + w*vec.w;
	}
	
	public Vec4f getDifference(Vec4f vec) {
		return new Vec4f(x - vec.x, y - vec.y, z - vec.z, w - vec.w);
	}
	
	public void difference(Vec4f vec) {
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
		w -= vec.w;
	}
	
	public Vec4f getSum(Vec4f vec) {
		return new Vec4f(x + vec.x, y + vec.y, z + vec.z, w + vec.z);
	}
	
	public void sum(Vec4f vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
		w += vec.w;
	}
}
