package main;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/** Simple GL loading and disposal functionality */
public class GLL {
	
	/** Store used vertex array ids */
	private static List<Integer> vaos;
	/** Store used vertex buffer ids */
	private static List<Integer> vbos;
	
	/** Initialize functionality */
	public static void init() {
		GL.createCapabilities(true);
		vaos = new LinkedList<Integer>();
		vbos = new LinkedList<Integer>();
	}
	
	/** Releases all vao and vbo memory */
	public static void terminate() {
		for(int vao:vaos)
			GL30.glDeleteVertexArrays(vao);
		for(int vbo:vbos)
			GL15.glDeleteBuffers(vbo);
	}
	
	/** Store vao attributes in a vbo */
	private static void store_vbo(int attrib, int dim, float[] data) {
		final int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		vbos.add(vbo);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attrib, dim, GL11.GL_FLOAT, false, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	/** Load data into a vao */
	public static int load_vao(float[] pos, float[] norm, int[] ind) {
		final int vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		vaos.add(vao);
		
		store_vbo(0, 3, pos);
		store_vbo(1, 3, norm);
		final int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ind, GL15.GL_STATIC_DRAW);
		
		GL30.glBindVertexArray(0);
		return vao;
	}
}
