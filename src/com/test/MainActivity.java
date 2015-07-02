package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.api.BitmapFilter;
import com.test.api.Filter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.BoringLayout.Metrics;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int C_WIDTH = 480;
	private static final int C_HEIGHT = 800;
	private static final int img_id = R.drawable.img_1;
	private ImageView imageview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Display display = this.getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		
		imageview = (ImageView) this.findViewById(R.id.imageview);
		imageview.setImageResource(img_id);
		GridView gridview = (GridView) this.findViewById(R.id.gridview);
		LinearLayout.LayoutParams params = new LayoutParams(outMetrics.widthPixels, LayoutParams.WRAP_CONTENT);
		gridview.setNumColumns(getData().size());
		gridview.setColumnWidth(GridView.AUTO_FIT);
		gridview.setHorizontalSpacing(2);
		gridview.setLayoutParams(params);
		
		gridview.setAdapter(new SimpleAdapter(this, getData(), R.layout.simple_adapter, new String[]{"img"}, new int[]{R.id.img}));
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				filterImageView(position);
			}
		});
		
		
	}

	
	/**
	 * 对图片进行滤镜
	 */
	private void filterImageView(int type){
		Bitmap  bitmap  = getBitmap(img_id);
		int[] src = getPiexlArray(bitmap);
		int[] des = new int[src.length];
		int[] mask;
		Bitmap scaleBitmap = null;
		switch (type) {
		case 0://黑白
			BitmapFilter.filter_black(src, des, bitmap.getWidth(), bitmap.getHeight());
			break;
		case 1://锐化
			BitmapFilter.filter_sharpen(src, des, bitmap.getWidth(), bitmap.getHeight());
			break;
		case 2://阳光
			BitmapFactory.Options  opts = new BitmapFactory.Options();
			opts.inScaled = false;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap  sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sunshine_mask, opts);
			
			int lenght = Math.min(bitmap.getWidth(), bitmap.getHeight());
			
			mask = new int[lenght * lenght];
			scaleBitmap = Bitmap.createScaledBitmap(sourceBitmap, lenght, lenght, true);
			
			sourceBitmap.recycle();
			sourceBitmap = null;
			
			scaleBitmap.getPixels(mask, 0, lenght, 0, 0, lenght, lenght);
			
			scaleBitmap.recycle();
			scaleBitmap = null;
			
			BitmapFilter.filter_sunshine(src, des, mask, bitmap.getWidth(), bitmap.getHeight(), lenght, lenght);
			break;
		default:
			break;
		}
		
		src = null;
		
		Bitmap  result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		result.setPixels(des, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
		
		imageview.setImageBitmap(result);
		
		des = null;
		mask = null;
		
		bitmap.recycle();
		bitmap = null;
		
	}
	
	private int[] getPiexlArray(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixels = new int[width * height];
		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		return pixels;
	}


	/**
	 * 获得原图
	 * 1.在240 和 320 范围取图。
	 *  1> 如果原图的宽高分别小于等于240  和 320 范围内，去原图
	 *  2> 如果原图的宽高分别大于  240  和 320 范围内，按照它们的宽高比率进行压缩。
	 *  
	 * 2.对生成的新图在 240  和 320  范围等比缩放。
	 * 
	 * @return
	 */
	private Bitmap getBitmap(int img_id) {
		BitmapFactory.Options  opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		
		BitmapFactory.decodeResource(getResources(), img_id, opts);
		
		int width = opts.outWidth;
		int height = opts.outHeight;
		
		if(width <= C_WIDTH && height <= C_HEIGHT ){
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = 1;
			return BitmapFactory.decodeResource(getResources(), img_id, opts);
		}
		
		//图片过大，进行图片压缩
		float widthRatio = width / C_WIDTH;
		float heightRatio = height / C_HEIGHT;
		int ratio =(int) (widthRatio > heightRatio ? (widthRatio) : heightRatio);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = ratio;
		Bitmap  bitmap = BitmapFactory.decodeResource(getResources(), img_id, opts);
		return bitmap;
		
	}


	private List<Map<String, Integer>> getData() {

		List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();

		Map<String, Integer> map1 = new HashMap<String, Integer>();
		map1.put("img", R.drawable.f7);

		list.add(map1);

		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map2.put("img", R.drawable.f8);

		list.add(map2);

		Map<String, Integer> map3 = new HashMap<String, Integer>();
		map3.put("img", R.drawable.f9);

		list.add(map3);

		return list;

	}

}
