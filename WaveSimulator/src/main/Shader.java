package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import maths.Mat3f;
import maths.Mat4f;
import maths.Vec3f;
import maths.Vec4f;

/** Contains general glsl shader functionality */
public abstract class Shader {
	
	/** Program identifiers */
	private int prog_sh, vert_sh, frag_sh;

	/** Constructs a standard program shader */
	protected Shader(String vsh, String fsh) {
		vert_sh = loadShader(vsh, GL20.GL_VERTEX_SHADER);
		frag_sh = loadShader(fsh, GL20.GL_FRAGMENT_SHADER);
		prog_sh = GL20.glCreateProgram();
		GL20.glAttachShader(prog_sh, vert_sh);
		GL20.glAttachShader(prog_sh, frag_sh);
		GL20.glLinkProgram(prog_sh);
		GL20.glValidateProgram(prog_sh);
		find_uniforms();
	}
	
	/** Locate uniform variables */
	protected abstract void find_uniforms();
	
	/** Start using this shader */
	public void start() {
		GL20.glUseProgram(prog_sh);
	}
	
	/** Stop using this shader */
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	/** Dispose of the shader */
	public void terminate() {
		GL20.glUseProgram(0);
		GL20.glDetachShader(prog_sh, vert_sh);
		GL20.glDetachShader(prog_sh, frag_sh);
		GL20.glDeleteShader(vert_sh);
		GL20.glDeleteShader(frag_sh);
		GL20.glDeleteProgram(prog_sh);
	}
	
	/** Locates a uniform with the given name */
	protected int find_uniform(String name) {
		return GL20.glGetUniformLocation(prog_sh, name);
	}
	
	/** Loads float value to a uniform variable */
	protected void load_float(int loc, float f) {
		GL20.glUniform1f(loc, f);
	}
	
	/** Load Vec3f value to a uniform variable */
	protected void load_vec(int loc, Vec3f v) {
		GL20.glUniform3f(loc, v.x, v.y, v.z);
	}
	
	/** Load Vec4f value to a uniform variable */
	protected void load_vec(int loc, Vec4f v) {
		GL20.glUniform4f(loc, v.x, v.y, v.z, v.w);
	}
	
	/** Load Mat3f value to a uniform variable */
	protected void load_mat(int loc, Mat3f m) {
		GL20.glUniformMatrix3fv(loc, false, m.getMatrix());
	}
	
	/** Load Mat4f value to a uniform variable */
	protected void load_mat(int loc, Mat4f m) {
		GL20.glUniformMatrix4fv(loc, false, m.getMatrix());
	}
	
	/** Load in shader source code */
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine())!=null) {
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
	}
}
