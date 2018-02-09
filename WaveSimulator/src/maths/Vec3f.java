package maths;

public class Vec3f {

	public float x;
	public float y;
	public float z;
	
	public Vec3f() {
		this(0.0f, 0.0f, 0.0f);
	}
	
	public Vec3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float getLength() {
		return ((float) Math.sqrt(x*x + y*y + z*z));
	}
	
	public Vec3f getNormalized() {
		float l = getLength();
		return new Vec3f(x / l, y / l, z / l);
	}
	
	public void normalize() {
		float l = getLength();
		x /= l;
		y /= l;
		z /= l;
	}
	
	public Vec3f getScaledBy(float scale) {
		return new Vec3f(scale*x, scale*y, scale*z);
	}
	
	public void scaledBy(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
	}
	
	public void setLength(float length) {
		scaledBy(length * getLength());
	}
	
	public float dot(Vec3f vec) {
		return vec.x*x + vec.y*y + vec.z*z;
	}
	
	public Vec3f cross(Vec3f vec) {
		return new Vec3f(y*vec.z - z*vec.y, z*vec.x - x*vec.z, x*vec.y - z*vec.x);
	}
	
	public Vec3f getDifference(Vec3f vec) {
		return new Vec3f(x - vec.x, y - vec.y, z - vec.z);
	}
	
	public void difference(Vec3f vec) {
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}
	
	public Vec3f getSum(Vec3f vec) {
		return new Vec3f(x + vec.x, y + vec.y, z + vec.z);
	}
	
	public void sum(Vec3f vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}
	
	@Override
	public String toString() {
		return "< " + x + " , " + y + " , " + z + " >";
	}
}
