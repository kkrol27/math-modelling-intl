package maths;

public class Vec2f {

	public float x;
	public float y;
	
	public Vec2f() {
		this(0.0f, 0.0f);
	}
	
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getLength() {
		return ((float) Math.sqrt(x*x + y*y));
	}
	
	public Vec2f getNormalized() {
		float l = getLength();
		return new Vec2f(x / l, y / l);
	}
	
	public void normalize() {
		float l = getLength();
		x /= l;
		y /= l;
	}
	
	public Vec2f getScaledBy(float scale) {
		return new Vec2f(scale*x, scale*y);
	}
	
	public void scaledBy(float scale) {
		x *= scale;
		y *= scale;
	}
	
	public void setLength(float length) {
		scaledBy(length * getLength());
	}
	
	public float dot(Vec2f vec) {
		return vec.x*x + vec.y*y;
	}
	
	public Vec2f getDifference(Vec2f vec) {
		return new Vec2f(x - vec.x, y - vec.y);
	}
	
	public void difference(Vec2f vec) {
		x -= vec.x;
		y -= vec.y;
	}
	
	public Vec2f getSum(Vec2f vec) {
		return new Vec2f(x + vec.x, y+ vec.x);
	}
	
	public void sum(Vec2f vec) {
		x += vec.x;
		y += vec.y;
	}
}
