package com.test;



import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity  extends ActionBarActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getSupportActionBar();
		actionBar.show();
	}
	
	
	@Override
	  public boolean onCreateOptionsMenu(Menu menu)
	  {
	    getMenuInflater().inflate(R.menu.main, menu);
	  //返回true才会显示overflow按钮
	    return true;
	  }
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item)
	  {
	    switch(item.getItemId())
	    {
	      case R.id.action_copy:
	        Toast.makeText(this, item.getTitle(), 1000).show();
	        return true;
	      case R.id.action_cut:
	        Toast.makeText(this, item.getTitle(), 1000).show();
	        return true;
	      case R.id.action_edit:
	        Toast.makeText(this, item.getTitle(), 1000).show();
	        return true;
	      case R.id.action_email:
	        Toast.makeText(this, item.getTitle(), 1000).show();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	  }
	
}
