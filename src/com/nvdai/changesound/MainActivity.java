package com.nvdai.changesound;

import com.nvdai.changesound.R;
import com.nvdai.music.MusicActivity;
import com.nvdai.phone.PhoneActivity;
import com.nvdai.record.RecordActivity;
import com.nvdai.setting.Setting;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {
	Context context;
	private ImageButton bt_phone_act;
	private ImageButton bt_music_act;
	private ImageButton bt_record_act;
	private ImageButton bt_setting_act;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		bt_phone_act = (ImageButton) findViewById(R.id.bt_phone_act);
		bt_music_act = (ImageButton) findViewById(R.id.bt_music_act);
		bt_record_act = (ImageButton) findViewById(R.id.bt_record_act);
		bt_setting_act = (ImageButton) findViewById(R.id.bt_setting_act);

		bt_phone_act.setOnClickListener(this);
		bt_music_act.setOnClickListener(this);
		bt_record_act.setOnClickListener(this);
		bt_setting_act.setOnClickListener(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public boolean isScreenLarge() {
		final int screenSize = getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK;
		return screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
				|| screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bt_phone_act:
			intent.setClass(MainActivity.this, PhoneActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			break;

		case R.id.bt_music_act:
			intent.setClass(MainActivity.this, MusicActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			break;
		case R.id.bt_record_act:
			intent.setClass(MainActivity.this, RecordActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			break;
		case R.id.bt_setting_act:
			intent.setClass(MainActivity.this, Setting.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			break;
		default:
			break;
		}
	}

}
