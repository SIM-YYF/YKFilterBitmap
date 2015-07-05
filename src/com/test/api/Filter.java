package com.test.api;
import android.graphics.Color;

public class Filter {

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
	// 阳光
	/**
	 * 
	 * @param src：原图
	 * @param des：
	 * @param mask：图层或者蒙版
	 * @param width
	 * @param height
	 * @param mask_width
	 * @param mask_height
	 */
	public static void filter_sunshine(int[] src, int[] des, int[] mask,
			int width, int height, int mask_width, int mask_height) {
		//degree取值范围在[-100 , 100]

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
			
			//检测每个像素的rgb的临界值
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

				//在柔光模式下进行叠加或者混合(rgb:255, 255, 255)
				MixedMode.soft_light(r, g, b, (short) 255, (short) 255, (short) 255);
				//在绿光 + Alpha 模式下进行叠加或者混合(rgba: 143, 27, 147,94)
				MixedMode.sreen_alpha(r, g, b, (short) 143, (short) 27, (short) 147,
						(short) 94);
				//在柔光 + Alpha 模式进行叠加或者混合(rgba:199,169,31,102) 
				MixedMode.soft_light_alpha_2(r, g, b, (short) 199, (short) 169, (short) 31,(short) 102);
				//在底片区域中，添加图层或者蒙版
				if (x < mask_width && y < mask_height) {

					int temp = y * mask_width + x;
					mask_r = (short) Color.red(mask[temp]);
					mask_g = (short) Color.green(mask[temp]);
					mask_b = (short) Color.blue(mask[temp]);

					//在绿光模式下进行叠加或者混合
					MixedMode.screen(r, g, b, mask_r, mask_g, mask_b);
					MixedMode.screen(r, g, b, mask_r, mask_g, mask_b);
				}

				des[pos] = Color.argb((short) Color.alpha(des[pos]), r.value, g.value, b.value);
			}
		}
	}
}
