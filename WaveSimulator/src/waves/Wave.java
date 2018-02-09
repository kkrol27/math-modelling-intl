package waves;

import main.GLL;
import maths.Vec3f;

/** Class responsible for constructing a wave mesh from ocean spectra */
public class Wave {

	/** Wave vao id */
	private int vao;
	/** Wave vertex count */
	private int count;
	
	/**
	 * Creates a new wave mesh based on deep ocean spectra.
	 * 
	 * @param fo
	 * 		initial integration frequency
	 * @param ff
	 * 		final integration frequency
	 * @param fc
	 * 		frequency component count
	 * @param hm
	 * 		spectrum amplitude
	 * @param wm
	 * 		modal angular frequency
	 * @param vc
	 * 		wave mesh vertex count along a single dimension
	 * @param d
	 * 		wave mesh dimension
	 * @param xo
	 * 		initial x displacement
	 * @param zo
	 * 		initial z displacement
	 */
	public Wave(double fo, double ff, int fc, double hm, double wm, int vc, double d, double xo, double zo) {
		// Wave components
		double[] wave_k = new double[fc];
		double[] wave_a = new double[fc];
		
		// Calculate wave components
		double df = (ff - fo) / ((double) fc);
		for(int i = 0; i < fc; i++) {
			double fi = (((double) i) + 0.5d) * df;
			wave_a[i] = df * spectrum(fi, hm);//spectrum(wm, hm, fi);
			wave_k[i] = 1.5613099917d / fi;
		}
		
		// Generate mesh
		float[] mesh_p = new float[3 * vc * vc];
		float[] mesh_n = new float[3 * vc * vc];
		for(int i = 0; i < vc; i++) {
			for(int k = 0; k < vc; k++) {
				double x = xo +(((double) i) / ((double)(vc - 1))) * d;
				double z = zo +(((double) k) / ((double)(vc - 1))) * d;
				int n = 3 * (i + vc * k);
				// Mesh coordinates
				mesh_p[n + 0] = (float) x;
				mesh_p[n + 1] = vert_disp(wave_a, wave_k, x);
				mesh_p[n + 2] = (float) z;
				// Mesh normal vector
				double dx1 = 0.001d, dy1 = vert_disp(wave_a, wave_k, x + 0.001d) - mesh_p[n + 1], dz1 = 0.0d;
				double dx2 = 0.0d, dy2 = 0.0d, dz2 = 0.001d;
				double xn = dy2 * dz1 - dy1 * dz2, yn = dx1 * dz2 - dx2 * dz1, zn = dx2 * dy1 - dx1 * dy2;
				double nm = Math.sqrt(xn*xn + yn*yn + zn*zn);
				mesh_n[n + 0] = (float)(xn / nm);
				mesh_n[n + 1] = (float)(yn / nm);
				mesh_n[n + 2] = (float)(zn / nm);
			}
		}
		
		// Generate index array
		int n = 0;
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
	
	/** Calculates wave displacement for the specified coordinate */
	private static float vert_disp(double[] a, double[] k, double x) {
		double d = 0.0d;
		for(int i = 0; i < a.length; i++)
			d += a[i] * Math.sin(k[i] * x);
		return (float) d;
	}
	
	/*
	private static double spectrum(double wm, double hm, double w) {
		double s = (5.0d / 16.0d) * (Math.pow(wm, 4.0d) / Math.pow(w, 5.0d)) * Math.pow(hm, 0.66666667d)
				   * Math.exp(-5.0d * Math.pow(wm, 4.0d) / (4.0d * Math.pow(w, 4.0d)));
		System.out.println(w + "\t" + s);
		return s;
	}
	*/
	/** Returns frequency spectrum function values */
	private static double spectrum(double w, double h) {
		double coeff = 8.1d * 9.81d * 9.81d / (1000.0d * Math.pow(w, 5));
		double s = coeff * Math.exp(-3.079552d * Math.pow(h, -0.666666667) * Math.pow(w, -4.0d));
		return Math.sqrt(s);
	}
	
	/** Complex number */
	private static class CNum {
		
		/** Elements */
		private double r, c;
		
		/** Creates a new complex number */
		public CNum(double r, double c) {
			this.r = r;
			this.c = c;
		}
		
		/** Adds two complex numbers */
		public CNum add(CNum n) {
			return new CNum(r + n.r, c + n.c);
		}
		
		
	}
}
