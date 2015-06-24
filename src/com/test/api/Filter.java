package com.test.api;
import android.graphics.Color;

public class Filter {

	private static class Myshort {
		public short value;
	}
	
	/**
	 * 混合模式为：绿色 
	 * 公式：A = 1-(1-A)*(1-B)
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	private static void Screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) (255 - (((255 - src_r.value) * (255 - r)) >> 8));
		src_g.value = (short) (255 - (((255 - src_g.value) * (255 - g)) >> 8));
		src_b.value = (short) (255 - (((255 - src_b.value) * (255 - b)) >> 8));
	}

	/**
	 * 混合模式：绿色 + alpha
	 * 公式：A = (1-((1-A)*(1-B) / 2^8)) * a / 1 + A * (1 - a) / 1
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 * @param alpha
	 */
	private static void Screen(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b, short alpha) {
		short rVal, gVal, bVal;

		rVal = (short) (255 - (((255 - src_r.value) * (255 - r)) >> 8));
		gVal = (short) (255 - (((255 - src_g.value) * (255 - g)) >> 8));
		bVal = (short) (255 - (((255 - src_b.value) * (255 - b)) >> 8));
		
		src_r.value = (short) (rVal * alpha / 255 + src_r.value * (255 - alpha) / 255);
		src_g.value = (short) (gVal * alpha / 255 + src_g.value * (255 - alpha) / 255);
		src_b.value = (short) (bVal * alpha / 255 + src_b.value * (255 - alpha) / 255);
	}


	/**
	 * 混合模式：柔光
	 * B <= 127
	 * A = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A)
	 * B > 127
	 * A = ((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A
	 * @param src_r 原
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	private static void SoftLight(
			Myshort src_r, 
			Myshort src_g, 
			Myshort src_b,
			short r, 
			short g, 
			short b) {
		
		if (r <= 127) {
			src_r.value = (short) ((2 * r - 255) * (255 * src_r.value - src_r.value^2) / 255^2 + src_r.value);
		} else {
			src_r.value =  (short) ((2 * r - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_r.value) - src_r.value) / 255 + src_r.value);
		}
		
		if (g <= 127) {
			src_g.value = (short) ((2 * g - 255) * (255 * src_g.value - src_g.value^2) / 255^2 + src_g.value);
		} else {
			src_g.value =  (short) ((2 * g - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_g.value) - src_g.value) / 255 + src_g.value);
		}
		
		if (b <= 127) {
			src_b.value = (short) ((2 * b - 255) * (255 * src_b.value - src_b.value^2) / 255^2 + src_b.value);
		} else {
			src_b.value =  (short) ((2 * b - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_b.value) - src_b.value) / 255 + src_b.value);
		}

	}

	/**
	 * 混合模式：柔光
	 * B <= 127
	 * A = (((2 * B) - 1) * (1 * A - A^2)/1^2 + A) * a / 255 + A * (1 - a) / 1
	 * 
	 * B > 127
	 * A = (((2 * B) - 1) * (sqrt(1) * sqrt(A) - A) / 1 + A) * a / 255 + A * (1 - a) / 1
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

		if (r <= 127) {
			rVal = (short) ((2 * r - 255) * (255 * src_r.value - src_r.value^2) / 255^2 + src_r.value);
		} else {
			rVal =  (short) ((2 * r - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_r.value) - src_r.value) / 255 + src_r.value);
		}
		
		if (g <= 127) {
			gVal = (short) ((2 * g - 255) * (255 * src_g.value - src_g.value^2) / 255^2 + src_g.value);
		} else {
			gVal =  (short) ((2 * g - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_g.value) - src_g.value) / 255 + src_g.value);
		}
		
		if (b <= 127) {
			bVal = (short) ((2 * b - 255) * (255 * src_b.value - src_b.value^2) / 255^2 + src_b.value);
		} else {
			bVal =  (short) ((2 * b - 255) * (Math.sqrt(255.0) * Math.sqrt((double)src_b.value) - src_b.value) / 255 + src_b.value);
		}
		
		src_r.value = (short) (rVal * alpha / 255 + src_r.value * (255 - alpha) / 255);
		src_g.value = (short) (gVal * alpha / 255 + src_g.value * (255 - alpha) / 255);
		src_b.value = (short) (bVal * alpha / 255 + src_b.value * (255 - alpha) / 255);
	}
	
	/**
	 * 混合模式：过滤
	 * @param src_r
	 * @param src_g
	 * @param src_b
	 * @param r
	 * @param g
	 * @param b
	 */
	private static void Difference(Myshort src_r, Myshort src_g, Myshort src_b,
			short r, short g, short b) {
		src_r.value = (short) ((r > src_r.value) ? (r - src_r.value)
				: (src_r.value - r));
		src_g.value = (short) ((g > src_g.value) ? (g - src_g.value)
				: (src_g.value - g));
		src_b.value = (short) ((b > src_b.value) ? (b - src_b.value)
				: (src_b.value - b));
	}

	// ==========================================================================================
	
	// 黑白效果 
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
	 * 1. 复制底片，在复制的底片上叠加qx_01.png(RGBA:50,0,255,100%)，混合模式设置为排除
	 * 2. 把第一步的结果叠加在底片上，混合模式设置为柔光 
	 * 3. 叠加qx_02.png(RGBA:251,255,217,100%)，混合模式设置为柔光
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
	
	// 阳光
	/**
	 * 
	 * @param src：原图
	 * @param des：
	 * @param mask：混合面板图
	 * @param width
	 * @param height
	 * @param mask_width
	 * @param mask_height
	 */
	public static void filter_sunshine(int[] src, int[] des, int[] mask,
			int width, int height, int mask_width, int mask_height) {
		//degree取值范围在[-100 , 100]
		
		double contrast = (100.0 + 52) / 100.0;
		contrast *= contrast;

		int size = width * height;
		Myshort dr = new Myshort(), dg = new Myshort(), db = new Myshort(), alpha = new Myshort();
		double rr, gg, bb;
		for (int i = 0; i < size; i++) {
			alpha.value = (short) Color.alpha(src[i]);
			dr.value = (short) Color.red(src[i]);
			dg.value = (short) Color.green(src[i]);
			db.value = (short) Color.blue(src[i]);

			//B = (((A / 1 - 0.5) *  ((100.0 + degree[-100, 100] = 52) / 100.0)^2 + 0.5) * 1)
			
			rr = (((dr.value / 255.0 - 0.5) * ((100.0 + 52) / 100.0) * ((100.0 + 52) / 100.0) + 0.5) * 255);
			gg = (((dg.value / 255.0 - 0.5) * ((100.0 + 52) / 100.0) * ((100.0 + 52) / 100.0) + 0.5) * 255);
			bb = (((db.value / 255.0 - 0.5) * ((100.0 + 52) / 100.0) * ((100.0 + 52) / 100.0) + 0.5) * 255);
			
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
			dr.value = (short) rr;
			dg.value = (short) gg;
			db.value = (short) bb;
			des[i] = Color.argb(alpha.value, dr.value, dg.value, db.value);
		}
		
		/////////////////////////////////////////////////////////////
		Myshort r = new Myshort(), g = new Myshort(), b = new Myshort();
		short mask_r, mask_g, mask_b;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pos = y * width + x;

				r.value = (short) Color.red(des[pos]);
				g.value = (short) Color.green(des[pos]);
				b.value = (short) Color.blue(des[pos]);

				//在柔光模式下进行混合
				SoftLight(r, g, b, (short) 255, (short) 255, (short) 255);
				//在绿光 + Alpha 模式下进行混合
				Screen(r, g, b, (short) 143, (short) 27, (short) 147,
						(short) 94);
				//在柔光 + Alpha 模式进行混合
				SoftLight(r, g, b, (short) 199, (short) 169, (short) 31,
						(short) 102);
				//在图像区域中，添加图层或者面板
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
}
