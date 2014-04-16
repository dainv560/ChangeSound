package com.nvdai.music;

import com.nvdai.changesound.MainActivity;
import com.nvdai.changesound.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MusicActivity extends Activity implements OnItemClickListener {

	private ListView lvMenuMusic;
	private String[] menuMusic = { "Play Music", "Library", "Back" };
	private ArrayAdapter<String> musicAdapter;
	private int[] REQUEST_CODE = { 11, 12, 13 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.music_layout);
		lvMenuMusic = (ListView) findViewById(R.id.lvMenuMusic);
		musicAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, menuMusic);
		lvMenuMusic.setAdapter(musicAdapter);
		lvMenuMusic.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String item = ((TextView) view).getText().toString();
		Intent intent = new Intent();

		if (item.equals(menuMusic[0])) {
			intent.setClass(MusicActivity.this, PlayMusic.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		} else if (item.equals(menuMusic[1])) {
			intent.setClass(MusicActivity.this, SongsLibrary.class);
			startActivityForResult(intent, REQUEST_CODE[0]);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		} else if (item.equals(menuMusic[2])) {
			intent.setClass(MusicActivity.this, MainActivity.class);
			startActivityForResult(intent, REQUEST_CODE[1]);
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(MusicActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
