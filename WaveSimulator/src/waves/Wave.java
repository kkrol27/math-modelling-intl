package waves;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.GLL;

/** Class responsible for constructing a wave mesh from ocean spectra */
public class Wave {

	/** Wave vao id */
	private int vao;
	/** Wave vertex count */
	private int count;
	
	/**
	 * Generates a wave and a corresponding mesh.
	 * 
	 * @param wm	
	 * @param hm
	 * @param vc
	 * @param d
	 * @param xo
	 * @param zo
	 */
	public Wave(int vc, double d, double xo, double zo) {
		// Spectrum
		Ocean ocean = new Ocean();
		// Calm ocean
		ocean.add(new Spectrum(0.4d * Math.sqrt(9.81d / 1.5d), 2.0d));
		ocean.add(new Spectrum(Math.PI / 2.0f, 0.25d));
		// Rough ocean
		// ocean.add(new Spectrum(0.4d * Math.sqrt(9.81d / 3.0d), 3.0d));
		// ocean.add(new Spectrum(Math.PI / 2.0f, 2.0d));
		
		// Wave components
		double[][] wave_k = new double[105][2];
		double[] wave_a = new double[105];
		double[] wave_d = new double[105];
		
		// Calculate wave components
		int n = 0;
		Random rand = new Random();
		for(int w = 0; w < 15; w++) {
			for(int t = -3; t < 4; t++) {
				double ti = ((double) t) * 0.4d;
				double wi = ((double) w) * 0.2d + 0.5d;
				double ks = 2.0d * Math.PI * wi / 9.81d;
				wave_d[n] = 2.0d * Math.PI * (rand.nextDouble() - 0.5d);
				wave_a[n] = 0.08d * ocean.eval(wi, ti);
				wave_k[n][0] = ks * Math.cos(ti);
				wave_k[n][1] = -ks * Math.sin(ti);
				n++;
			}
		}
		print_wave("calm.txt", wave_a, wave_k, wave_d);
		
		// Generate mesh
		float[] mesh_p = new float[3 * vc * vc];
		float[] mesh_n = new float[3 * vc * vc];
		for(int i = 0; i < vc; i++) {
			for(int k = 0; k < vc; k++) {
				double x = xo +(((double) i) / ((double)(vc - 1))) * d;
				double z = zo +(((double) k) / ((double)(vc - 1))) * d;
				n = 3 * (i + vc * k);
				// Mesh coordinates
				mesh_p[n + 0] = (float) x;
				mesh_p[n + 1] = (float) displacement(wave_a, wave_k, wave_d, xo, zo, x, z);
				mesh_p[n + 2] = (float) z;
				// Mesh normal vector
				double dx1 = 0.001d, dy1 = displacement(wave_a, wave_k, wave_d, xo, zo, x + 0.001d, z) - mesh_p[n + 1], dz1 = 0.0d;
				double dx2 = 0.0d, dy2 = displacement(wave_a, wave_k, wave_d, xo, zo, x, z + 0.001d) - mesh_p[n + 1], dz2 = 0.001d;
				double xn = dy2 * dz1 - dy1 * dz2, yn = dx1 * dz2 - dx2 * dz1, zn = dx2 * dy1 - dx1 * dy2;
				double nm = Math.sqrt(xn*xn + yn*yn + zn*zn);
				mesh_n[n + 0] = (float)(xn / nm);
				mesh_n[n + 1] = (float)(yn / nm);
				mesh_n[n + 2] = (float)(zn / nm);
			}
		}
		
		// Generate index array
		n = 0;
		int[] mesh_i = new int[6 * (vc - 1) * (vc - 1)];
		for(int i = 0; i < vc - 1; i++) {
			for(int k = 0; k < vc - 1; k++) {
				int a = i + vc * k;
				mesh_i[n++] = a;
				mesh_i[n++] = a + vc + 1;
				mesh_i[n++] = a + 1;
				mesh_i[n++] = a;
				mesh_i[n++] = a + vc;
				mesh_i[n++] = a + vc + 1;
			}
		}
		
		// Load data to vao and set vao id
		this.vao = GLL.load_vao(mesh_p, mesh_n, mesh_i);
		this.count = mesh_i.length;
	}
	
	/** Returns the wave vao */
	public int vao() {
		return vao;
	}
	
	/** Returns the wave vertex count */
	public int count() {
		return count;
	}
	
	/** Returns the water displacement */
	private static double displacement(double[] a, double[][] k, double[] d, double xo, double zo, double x, double z) {
		double v = 0.0d;
		for(int i = 0; i < a.length; i++)
			v += a[i] * Math.cos(k[i][0] * (x - xo) + k[i][1] * (z - zo) - d[i]);
		return v;
	}
	
	/** Prints out the current wave function information */
	private static void print_wave(String name, double[] a, double[][] k, double[] d) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(name));
			writer.write('[');
			for(double n:a)
				writer.write(n + ",");
			writer.write("\n[");
			for(double[] n:k)
				writer.write(n[0] + "," + n[1] + ";");
			writer.write("\n[");
			for(double n:d)
				writer.write(n + ",");
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** Contains a list of spectra */
	private static class Ocean {
		 
		/** This ocean's spectra */
		private List<Spectrum> spectra;
		
		/** Generates a new ocean */
		private Ocean() {
			this.spectra = new ArrayList<Spectrum>();
		}
		
		/** Adds a new spectrum to this ocean */
		private void add(Spectrum s) {
			spectra.add(s);
		}
		
		/** Evaluates the spectrum */
		private double eval(double w, double a) {
			double d = 0.0d;
			for(Spectrum s:spectra)
				d += s.eval(w);
			d = d * Math.pow(Math.cos(a / 2.0d), 9);
			return d;
		}
	}
	
	/** Represents an ocean spectrum */
	private static class Spectrum {
		
		/** Spectrum function constants */
		private double c, e;
		
		/** Creates a new spectrum */
		private Spectrum(double w, double h) {
			this.c = 1.25d * Math.pow(w, 4.0d) * h * h / 4.0d;
			this.e = -1.25d * Math.pow(w, 4.0d);
		}
		
		/** Evaluates this spectra */
		public double eval(double w) {
			double d = Math.sqrt(2.0d * c * Math.exp(e / Math.pow(w, 4.0d)) / Math.pow(w, 5.0d));
			return (Double.isNaN(d) ? 0.0d : d);
		}
	}
}
