package com.test.api;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;

public class Filter {
	final double MagickEpsilon = 1.0e-12;
	static final int MaxRGB = 255;

	private static int CLAMP(int x, int a, int b) {
		return (x < a) ? a : ((x > b) ? b : x);
	}

	private static short Max(short a, short b, short c) {
		return (short) Math.max(a, Math.max(b, c));
	}

	private static int Max(int a, int b, int c) {
		return (short) Math.max(a, Math.max(b, c));
	}

	private static short Min(short a, short b, short c) {
		return (short) Math.min(a, Math.min(b, c));
	}

	private static int Min(int a, int b, int c) {
		return (short) Math.min(a, Math.min(b, c));
	}

	public class MyColor {
		public Color color = new Color();
	}

	public static int Between(int src, int min, int max) {
		if (src < min)
			return min;
		if (src > max)
			return max;
		return src;
	}

	/*
	 * 对比度 degree,[-100,100]
	 */
	public static void Contrast(int[] pixels, int[] des, int width, int height,
			int degree) {
		degree = Between(degree, -100, 100);
		double contrast = (100.0 + degree) / 100.0;
		contrast *= contrast;

		int size = width * height;
		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort(), alpha = new Myshort();
		double rr, gg, bb;
		for (int i = 0; i < size; i++) {
			alpha.value = (short) Color.alpha(pixels[i]);
			r.value = (short) Color.red(pixels[i]);
			g.value = (short) Color.green(pixels[i]);
			b.value = (short) Color.blue(pixels[i]);

			rr = (((r.value / 255.0 - 0.5) * contrast + 0.5) * 255);
			gg = (((g.value / 255.0 - 0.5) * contrast + 0.5) * 255);
			bb = (((b.value / 255.0 - 0.5) * contrast + 0.5) * 255);
			if (rr < 0.0)
				rr = 0;
			if (rr > 255.0)
				rr = 255;
			if (gg < 0.0)
				gg = 0;
			if (gg > 255.0)
				gg = 255;
			if (bb < 0.0)
				bb = 0;
			if (bb > 255.0)
				bb = 255;
			r.value = (short) rr;
			g.value = (short) gg;
			b.value = (short) bb;
			des[i] = Color.argb(alpha.value, r.value, g.value, b.value);
		}

	}

	// 边缘检测
	private static void EdgeDetect_Roberts(int[] src, int[] des, int width,
			int height) {
		int A, B; // A(x-1, y-1) B(x, y-1)
		int C, D; // C(x-1, y) D(x, y)

		// 不处理最上边和最左边
		for (int y = 1; y < height; y++) {
			for (int x = 1; x < width; x++) {

				A = (Color.red(src[(x - 1) + (y - 1) * width])
						+ Color.green(src[(x - 1) + (y - 1) * width]) + Color
						.blue(src[(x - 1) + (y - 1) * width])) / 3;
				B = (Color.red(src[x + (y - 1) * width])
						+ Color.green(src[x + (y - 1) * width]) + Color
						.blue(src[x + (y - 1) * width])) / 3;
				C = (Color.red(src[(x - 1) + y * width])
						+ Color.green(src[(x - 1) + y * width]) + Color
						.blue(src[(x - 1) + y * width])) / 3;
				D = (Color.red(src[x + y * width])
						+ Color.green(src[x + y * width]) + Color.blue(src[x
						+ y * width])) / 3;

				short rgb = (short) (Math.sqrt((A - D) * (A - D) + (B - C)
						* (B - C)));
				des[x + y * width] = Color.argb(
						Color.alpha(src[(x - 1) + (y - 1) * width]), rgb, rgb,
						rgb);

			} // x
		} // y
	} // end of Roberts

	private static class MyDouble {
		public double value;
	}

	private static class Myshort {
		public short value;
	}

	

	// / <summary>
	// / below is color blending mode
	// / blending mode:Transparence,c= (a*(255-alpha)+b*alpha)/255
	// / </summary>
	// / <param name="src_r"></param>
	// / <param name="src_g"></param>
	// / <param name="src_b"></param>
	// / <param name="r"></param>
	// / <param name="g"></param>
	// / <param name="b"></param>
	// / <param name="alpha"></param>
	private static void Transparence(Myshort src_r, Myshort src_g,
			Myshort src_b, short r, short g, short b, short alpha) {
		src_r.value = (short) ((src_r.value * (255 - alpha) + (r * alpha)) >> 8);
		src_g.value = (short) ((src_g.value * (255 - alpha) + (g * alpha)) >> 8);
		src_b.value = (short) ((src_b.value * (255 - alpha) + (b * alpha)) >> 8);
	}

	// / <summary>
	// / blending mode: Mutiply
	// / </summary>
	// / <param name="src_r"></param>
	// / <param name="src_g"></param>
	// / <param name="src_b"></param>
	// / <param name="r"></param>
	// / <param name="g"></param>
	// / <param name="b"></param>
	private static void Mutiply(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) ((src_r.value * r) >> 8);
		src_g.value = (short) ((src_g.value * g) >> 8);
		src_b.value = (short) ((src_b.value * b) >> 8);
	}

	private static void Mutiply(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) ((src_r.value * r) >> 8);
		gVal = (short) ((src_g.value * g) >> 8);
		bVal = (short) ((src_b.value * b) >> 8);
		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	// / <summary>
	// / blending mode: Darken
	// / </summary>
	// / <param name="src_r"></param>
	// / <param name="src_g"></param>
	// / <param name="src_b"></param>
	// / <param name="r"></param>
	// / <param name="g"></param>
	// / <param name="b"></param>
	void Darken(Myshort src_r, Myshort src_g, Myshort src_b, short r, short g,
			short b) {
		src_r.value = (src_r.value <= r) ? src_r.value : r;
		src_g.value = (src_g.value <= g) ? src_g.value : g;
		src_b.value = (src_b.value <= b) ? src_b.value : b;
	}



	
	// blending mode: screen , c = 1-(1-A)*(1-B)
	private static void Screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) (255 - (((255 - src_r.value) * (255 - r)) >> 8));
		src_g.value = (short) (255 - (((255 - src_g.value) * (255 - g)) >> 8));
		src_b.value = (short) (255 - (((255 - src_b.value) * (255 - b)) >> 8));
	}

	private static void Screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) (255 - (((255 - src_r.value) * (255 - r)) >> 8));
		gVal = (short) (255 - (((255 - src_g.value) * (255 - g)) >> 8));
		bVal = (short) (255 - (((255 - src_b.value) * (255 - b)) >> 8));

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	private static short calc_with_alpha(short val, short s, short alpha) {
		return (short) (val * alpha / 255 + s * (255 - alpha) / 255);
	}

	private static short soft_light(short b, short s) {
		if (s <= 127) {
			return (short) ((2 * s - 255) * (255 * b - b^2) / 255^2 + b);
		} else {
			return (short) ((2 * s - 255) * (Math.sqrt(255.0) * Math.sqrt((double)b) - b) / 255 + b);
		}
	}

	// / <summary>
	// / blending mode:Soft Light
	// / </summary>
	// / <param name="src_r"></param>
	// / <param name="src_g"></param>
	// / <param name="src_b"></param>
	// / <param name="r"></param>
	// / <param name="g"></param>
	// / <param name="b"></param>
	// /
	private static void SoftLight(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = soft_light(src_r.value, r);
		src_g.value = soft_light(src_g.value, g);
		src_b.value = soft_light(src_b.value, b);

	}

	/**
	 * 添加柔光
	 * 
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	private static void SoftLight(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = soft_light(src_r.value, r);
		gVal = soft_light(src_g.value, g);
		bVal = soft_light(src_b.value, b);

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	private static short hard_light(short b, short s) {
		if (s <= 127) {
			return (short) (2 * b * s / 255);
		} else {
			return (short) (255 - 2 * (255 - s) * (255 - b) / 255);
		}
	}

	// / <summary>
	// / blending mode:Hard Light
	// / </summary>
	// / <param name="src_r"></param>
	// / <param name="src_g"></param>
	// / <param name="src_b"></param>
	// / <param name="r"></param>
	// / <param name="g"></param>
	// / <param name="b"></param>
	// /
	void HardLight(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b) {
		src_r.value = hard_light(src_r.value, r);
		src_g.value = hard_light(src_g.value, g);
		src_b.value = hard_light(src_b.value, b);

	}

	

	
	

	

	
	// blending mode,Difference
	private static void Difference(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) ((r > src_r.value) ? (r - src_r.value)
				: (src_r.value - r));
		src_g.value = (short) ((g > src_g.value) ? (g - src_g.value)
				: (src_g.value - g));
		src_b.value = (short) ((b > src_b.value) ? (b - src_b.value)
				: (src_b.value - b));
	}

	void Difference(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) ((r > src_r.value) ? (r - src_r.value)
				: (src_r.value - r));
		gVal = (short) ((g > src_g.value) ? (g - src_g.value)
				: (src_g.value - g));
		bVal = (short) ((b > src_b.value) ? (b - src_b.value)
				: (src_b.value - b));

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	// blending mode,Exclusion
	void Exclusion(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b) {
		src_r.value = (short) (r + src_r.value - (2 * r * src_r.value) / MaxRGB);
		src_g.value = (short) (g + src_g.value - (2 * g * src_g.value) / MaxRGB);
		src_b.value = (short) (b + src_b.value - (2 * b * src_b.value) / MaxRGB);
	}

	void Exclusion(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) (r + src_r.value - (2 * r * src_r.value) / MaxRGB);
		gVal = (short) (g + src_g.value - (2 * g * src_g.value) / MaxRGB);
		bVal = (short) (b + src_b.value - (2 * b * src_b.value) / MaxRGB);

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	
	// blending mode,Linear Dodge
	void LinearDodge(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b) {
		src_r.value = (short) ((r + src_r.value > MaxRGB) ? MaxRGB : r
				+ src_r.value);
		src_g.value = (short) ((g + src_g.value > MaxRGB) ? MaxRGB : g
				+ src_g.value);
		src_b.value = (short) ((b + src_b.value > MaxRGB) ? MaxRGB : b
				+ src_b.value);
	}

	void LinearDodge(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) ((r + src_r.value > MaxRGB) ? MaxRGB : r + src_r.value);
		gVal = (short) ((g + src_g.value > MaxRGB) ? MaxRGB : g + src_g.value);
		bVal = (short) ((b + src_b.value > MaxRGB) ? MaxRGB : b + src_b.value);

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	// blending mode,Linear Burn
	private static void LinearBurn(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) ((r + src_r.value > MaxRGB) ? r + src_r.value
				- MaxRGB : 0);
		src_g.value = (short) ((g + src_g.value > MaxRGB) ? g + src_g.value
				- MaxRGB : 0);
		src_b.value = (short) ((b + src_b.value > MaxRGB) ? b + src_b.value
				- MaxRGB : 0);
	}

	void LinearBurn(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) ((r + src_r.value > MaxRGB) ? r + src_r.value - MaxRGB
				: 0);
		gVal = (short) ((g + src_g.value > MaxRGB) ? g + src_g.value - MaxRGB
				: 0);
		bVal = (short) ((b + src_b.value > MaxRGB) ? b + src_b.value - MaxRGB
				: 0);

		src_r.value = calc_with_alpha(rVal, src_r.value, alpha);
		src_g.value = calc_with_alpha(gVal, src_g.value, alpha);
		src_b.value = calc_with_alpha(bVal, src_b.value, alpha);
	}

	

	void Luminosity(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b, short alpha) {
		int delta, scale;
		int rr, rg, rb, y;

		/* 0.3, 0.59, 0.11 in fixed point */
		delta = ((r - src_r.value) * 77 + (g - src_g.value) * 151
				+ (b - src_b.value) * 28 + 0x80) >> 8;
		rr = src_r.value + delta;
		rg = src_g.value + delta;
		rb = src_b.value + delta;

		if (((rr | rg | rb) & 0x100) != 0) {
			y = (r * 77 + g * 151 + b * 28 + 0x80) >> 8;
			if (delta > 0) {
				int max;
				max = Math.max(rr, Math.max(rg, rb));
				scale = ((255 - y) << 16) / (max - y);
			} else {
				int min;
				min = Math.min(rr, Math.min(rg, rb));
				scale = (y << 16) / (y - min);
			}
			rr = y + (((rr - y) * scale + 0x8000) >> 16);
			rg = y + (((rg - y) * scale + 0x8000) >> 16);
			rb = y + (((rb - y) * scale + 0x8000) >> 16);
		}

		src_r.value = calc_with_alpha((short) rr, src_r.value, alpha);
		src_g.value = calc_with_alpha((short) rg, src_g.value, alpha);
		src_b.value = calc_with_alpha((short) rb, src_b.value, alpha);

	}


	void Saturation(Myshort src_r, Myshort src_g, Myshort src_b, short r,
			short g, short b, short alpha) {
		int minb, maxb;
		int mins, maxs;
		int y;
		int scale;
		int rr, rg, rb;

		minb = Min(src_r.value, src_g.value, src_b.value);
		maxb = Max(src_r.value, src_g.value, src_b.value);
		if (minb == maxb) {
			// backdrop has zero saturation, avoid divide by 0
			return;
		}

		mins = Min(r, g, b);
		maxs = Max(r, g, b);

		scale = ((maxs - mins) << 16) / (maxb - minb);
		y = (src_r.value * 77 + src_g.value * 151 + src_b.value * 28 + 0x80) >> 8;
		rr = y + ((((src_r.value - y) * scale) + 0x8000) >> 16);
		rg = y + ((((src_g.value - y) * scale) + 0x8000) >> 16);
		rb = y + ((((src_b.value - y) * scale) + 0x8000) >> 16);

		if (((rr | rg | rb) & 0x100) != 0) {
			int scalemin, scalemax;
			int min, max;

			min = Min(rr, rg, rb);
			max = Max(rr, rg, rb);

			if (min < 0)
				scalemin = (y << 16) / (y - min);
			else
				scalemin = 0x10000;

			if (max > 255)
				scalemax = ((255 - y) << 16) / (max - y);
			else
				scalemax = 0x10000;

			scale = Math.min(scalemin, scalemax);
			rr = y + (((rr - y) * scale + 0x8000) >> 16);
			rg = y + (((rg - y) * scale + 0x8000) >> 16);
			rb = y + (((rb - y) * scale + 0x8000) >> 16);
		}

		src_r.value = calc_with_alpha((short) rr, src_r.value, alpha);
		src_g.value = calc_with_alpha((short) rg, src_g.value, alpha);
		src_b.value = calc_with_alpha((short) rb, src_b.value, alpha);
	}






	private static final int RED = 1;
	private static final int Yellow = 2;
	private static final int Green = 3;
	private static final int Blue = 4;

	public static Bitmap deco(Bitmap bitmap_, int type) {
		Bitmap bitmap = Bitmap.createBitmap(bitmap_);
		float[] hsv = new float[3];
		for (int h = 0; h < bitmap.getHeight(); h++) {
			for (int w = 0; w < bitmap.getWidth(); w++) {
				int pix = bitmap.getPixel(w, h);
				int a = Color.alpha(pix);
				Color.colorToHSV(pix, hsv);
				switch (type) {
				case RED:
					doRed(hsv);
					break;
				case Yellow:
					doYellow(hsv);
					break;
				case Green:
					doGreen(hsv);
					break;
				case Blue:
					doBlue(hsv);
					break;
				}
				int color = Color.HSVToColor(a, hsv);
				bitmap.setPixel(w, h, color);
			}
		}

		return bitmap;
	}

	// h:色相。 s:饱和度。v:亮度
	private static final float R1 = 10f, R2 = 30f, R3 = 320f, R4 = 350f,
			R21 = R2 - R1, R43 = R4 - R3;
	private static final float MIN_V = 0.1f;

	private static void doRed(float[] hsv) {
		if (hsv[2] < MIN_V || (hsv[0] < R3 && hsv[0] > R2)) {
			hsv[1] = 0;
		} else {
			if (hsv[0] >= R4 || hsv[0] <= R1) {
			} else if (hsv[0] < R2) {
				hsv[1] *= ((R2 - hsv[0]) / R21);
			} else if (hsv[0] > R3) {
				hsv[1] *= ((hsv[0] - R3) / R43);
			}
		}
	}

	private static final float G1 = 70f, G2 = 80f, G3 = 140f, G4 = 160f,
			G21 = G2 - G1, G43 = G4 - G3;

	private static void doGreen(float[] hsv) {
		if (hsv[2] < MIN_V || hsv[0] < G1 || hsv[0] > G4) {
			hsv[1] = 0;
		} else {// 70,160
			if (hsv[0] > G2 && hsv[0] < G3) {
			} else if (hsv[0] < G2 && hsv[0] > G1) {
				hsv[1] *= ((hsv[0] - G1) / G21);
			} else if (hsv[0] > G3 && hsv[0] < G4) {
				hsv[1] *= ((G4 - hsv[0]) / G43);
			}
		}
	}

	private static final float Y1 = 20f, Y2 = 50f, Y3 = 65f, Y4 = 85f, Y21 = Y2
			- Y1, Y43 = Y4 - Y3;

	private static void doYellow(float[] hsv) {
		if (hsv[2] < MIN_V || hsv[0] < Y1 || hsv[0] > Y4) {
			hsv[1] = 0;
		} else {
			if (hsv[0] > Y2 && hsv[0] < Y3) {
			} else if (hsv[0] < Y2 && hsv[0] > Y1) {
				hsv[1] *= ((hsv[0] - Y1) / Y21);
			} else if (hsv[0] > Y3 && hsv[0] < Y4) {
				hsv[1] *= ((Y4 - hsv[0]) / Y43);
			}
		}
	}

	private static final float B1 = 190f, B2 = 220f, B3 = 260f, B4 = 270f,
			B21 = B2 - B1, B43 = B4 - B3;

	private static void doBlue(float[] hsv) {
		if (hsv[2] < MIN_V || hsv[0] < B1 || hsv[0] > B4) {
			hsv[1] = 0;
		} else {// 190,270
			if (hsv[0] > B2 && hsv[0] < B3) {
				// hsv[1] *= 0.7;
			} else if (hsv[0] < B2 && hsv[0] > B1) {
				hsv[1] *= ((hsv[0] - B1) / B21);
			} else if (hsv[0] > B3 && hsv[0] < B4) {
				hsv[1] *= ((B4 - hsv[0]) / B43);
			}
		}
	}

	
	// 透明
	public void filter_transparence(int[] src, int[] des, int width, int height) {
		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort();
		int nIndexSrc = 0;
		int nIndexDes = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				r.value = (short) Color.red(src[nIndexSrc]);
				g.value = (short) Color.green(src[nIndexSrc]);
				b.value = (short) Color.blue(src[nIndexSrc]);

				Transparence(r, g, b, r.value, g.value, b.value,
						(short) Color.alpha(src[nIndexSrc]));

				des[nIndexDes] = Color.argb(255, r.value, g.value, b.value);

				nIndexSrc++;
				nIndexDes++;
			}
		}
	}

	int filter_getpixel_index(int i, int j, int w, int h) {
		if (i < 0)
			i = 0;
		if (j < 0)
			j = 0;
		if (i > w - 1)
			i = w - 1;
		if (j > h - 1)
			j = h - 1;

		return j * w + i;
	}



	// ==========================================================================================
	
	// 黑白效果 pass
	public static void filter_black(int[] src, int[] des, int width, int height) {
		short r, g, b;
		short gray;
		int size = width * height;

		int nIndexSrc = 0;
		int nIndexDes = 0;
		for (int i = 0; i < size; i++) {
			r = (short) Color.red(src[nIndexSrc]);
			g = (short) Color.green(src[nIndexSrc]);
			b = (short) Color.blue(src[nIndexSrc]);

			gray = (short) ((r + g + b) / 3);
			r = gray;
			g = gray;
			b = gray;

			des[nIndexDes] = Color.argb(Color.alpha(src[nIndexSrc]), r, g, b);
			nIndexSrc++;
			nIndexDes++;
		}
	}


	
	
	

	
	

	
	
	// 清新 pass
	/*
	 * 1. 复制底片，在复制的底片上叠加qx_01.png(RGBA:50,0,255,100%)，混合模式设置为排除 2.
	 * 把第一步的结果叠加在底片上，混合模式设置为柔光 3. 叠加qx_02.png(RGBA:251,255,217,100%)，混合模式设置为柔光
	 */
	public static void filter_fresh(int[] src, int[] des, int width, int height) {

		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort(), r_tmp = new Myshort(), g_tmp = new Myshort(), b_tmp = new Myshort();
		int size = width * height;

		int nIndexSrc = 0;
		int nIndexDes = 0;
		for (int i = 0; i < size; i++) {

			r.value = r_tmp.value = (short) Color.red(src[nIndexSrc]);
			g.value = g_tmp.value = (short) Color.green(src[nIndexSrc]);
			b.value = b_tmp.value = (short) Color.blue(src[nIndexSrc]);

			// L1 复制底片，在复制的底片上叠加qx_01.png(RGBA:50,0,255,100%)，混合模式设置为排除
			Difference(r_tmp, g_tmp, b_tmp, (short) 50, (short) 0, (short) 255);
			// L2 把第一步的结果叠加在底片上，混合模式设置为柔光
			SoftLight(r, g, b, (short) r_tmp.value, (short) g_tmp.value,
					(short) b_tmp.value);
			// L3 叠加qx_02.png(RGBA:251,255,217,100%)，混合模式设置为柔光
			SoftLight(r, g, b, (short) 251, (short) 255, (short) 217);

			des[nIndexDes] = Color.argb((short) Color.alpha(src[nIndexSrc]),
					r.value, g.value, b.value);
			nIndexSrc++;
			nIndexDes++;
		}
	}

	// 素描
	/*
	 * 1. 对底片查找边缘（程序算法） 2. 叠加sketch.png，混合模式设置为正片叠底
	 */
	public static void filter_sketch(int[] src, int[] des, int[] M1mask,
			int width, int height) {
		// Color src_color, maskM1_color; ;
		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort();
		int size = width * height;
		short M1mask_a, M1mask_r, M1mask_g, M1mask_b;

		for (int i = 0; i < size; i++) {

			r.value = (short) Color.red(src[i]);
			g.value = (short) Color.green(src[i]);
			b.value = (short) Color.blue(src[i]);

			M1mask_a = (short) Color.alpha(M1mask[i]);
			if (0 != M1mask_a) {
				M1mask_r = (short) (Color.red(M1mask[i]) * 255 / M1mask_a);
				M1mask_g = (short) (Color.green(M1mask[i]) * 255 / M1mask_a);
				M1mask_b = (short) (Color.blue(M1mask[i]) * 255 / M1mask_a);
			} else {
				M1mask_r = (short) Color.red(M1mask[i]);
				M1mask_g = (short) Color.green(M1mask[i]);
				M1mask_b = (short) Color.blue(M1mask[i]);
			}

			Mutiply(r, g, b, M1mask_r, M1mask_g, M1mask_b, M1mask_a);

			des[i] = Color.argb((short) Color.alpha(src[i]), r.value, g.value,
					b.value);
		}
	}


	
	// 素描 pass
	public static void filter_sketch_new(int[] src, int[] des, int[] mask,
			int width, int height) {
		EdgeDetect_Roberts(src, des, width, height);

		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort();
		int size = width * height;
		for (int i = 0; i < size; i++) {

			r.value = (short) (255 - (short) Color.red(des[i]));
			g.value = (short) (255 - (short) Color.green(des[i]));
			b.value = (short) (255 - (short) Color.blue(des[i]));

			LinearBurn(r, g, b, r.value, g.value, b.value); // 新添加 3.12
			Mutiply(r, g, b, (short) Color.red(mask[i]),
					(short) Color.green(mask[i]), (short) Color.blue(mask[i]));

			des[i] = Color.argb((short) Color.alpha(des[i]), r.value, g.value,
					b.value);
		}
	}

	// 阳光Sunshine
	public static void filter_sunshine(int[] src, int[] des, int[] mask,
			int width, int height, int mask_width, int mask_height) {
		Contrast(src, des, width, height, 52);
		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort();
		short mask_r, mask_g, mask_b;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pos = y * width + x;

				r.value = (short) Color.red(des[pos]);
				g.value = (short) Color.green(des[pos]);
				b.value = (short) Color.blue(des[pos]);

				SoftLight(r, g, b, (short) 255, (short) 255, (short) 255);
				Screen(r, g, b, (short) 143, (short) 27, (short) 147,
						(short) 94);
				SoftLight(r, g, b, (short) 199, (short) 169, (short) 31,
						(short) 102);
				if (x < mask_width && y < mask_height) {

					int temp = y * mask_width + x;
					mask_r = (short) Color.red(mask[temp]);
					mask_g = (short) Color.green(mask[temp]);
					mask_b = (short) Color.blue(mask[temp]);

					Screen(r, g, b, mask_r, mask_g, mask_b);
					Screen(r, g, b, mask_r, mask_g, mask_b);
				}

				des[pos] = Color.argb((short) Color.alpha(des[pos]), r.value,
						g.value, b.value);
			}
		}
	}

	

	/**
	 * 获取图片的角度
	 * 
	 * @param baseUri
	 * @return s
	 */
	public static int getDegree(Uri baseUri, android.content.Context content) {
		Cursor cursor = null;
		String[] columns = { MediaStore.Images.Media.ORIENTATION };
		cursor = MediaStore.Images.Media.query(content.getContentResolver(),
				baseUri, columns);
		int degree = 0;
		if (cursor != null) {
			cursor.moveToFirst();
			degree = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Images.Media.ORIENTATION));
		}

		if (cursor != null) {
			cursor.close();
		}

		return degree;
	}
}
