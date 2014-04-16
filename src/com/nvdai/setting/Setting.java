package com.nvdai.setting;

import java.io.File;

import com.nvdai.changesound.MainActivity;
import com.nvdai.changesound.R;
import com.nvdai.database.DatabaseHandler;
import com.nvdai.database.SongData;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Setting extends Activity {

	private Button btUpdateData;
	private Button btResetData;
	final String MEDIA_PATH = new String("/sdcard/");
	final String RESET_TYPE = "reset";
	final String UPDATE_TYPE = "update";
	private File home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		btUpdateData = (Button) findViewById(R.id.btUpdateLibrary);
		btResetData = (Button) findViewById(R.id.btResetLibrary);
		home = new File(MEDIA_PATH);

		btUpdateData.setOnClickListener(new OnClickListener() {
			Thread loaddingThread;

			@Override
			public void onClick(View v) {
				setStatusButton(false);

				loaddingThread = new Thread() {
					public void run() {
						try {
							scanDirectory(home, UPDATE_TYPE);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							runOnUiThread(new Runnable() {
								public void run() {
									setStatusButton(true);
								}
							});
						}
					}
				};
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						loaddingThread.start();
					}
				});
			}
		});

		btResetData.setOnClickListener(new OnClickListener() {
			Thread loaddingThread;

			@Override
			public void onClick(View v) {
				setStatusButton(false);

				loaddingThread = new Thread() {
					public void run() {
						try {
							DatabaseHandler db = new DatabaseHandler(
									Setting.this);
							db.deleteAll();
							db = new DatabaseHandler(Setting.this);
							scanDirectory(home, RESET_TYPE);
							db.close();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							runOnUiThread(new Runnable() {
								public void run() {
									setStatusButton(true);
								}
							});
						}
					}
				};
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						loaddingThread.start();
					}
				});
			}
		});

	}

	public void setStatusButton(Boolean status) {
		btUpdateData.setEnabled(status);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	private void scanDirectory(File directory, String type) {
		if (directory != null) {
			File[] listFiles = directory.listFiles();
			if (listFiles != null && listFiles.length > 0) {
				for (File file : listFiles) {
					if (file.isDirectory()) {
						scanDirectory(file, type);
					} else {
						if (type == RESET_TYPE) {
							addSongToList(file);
						} else {
							DatabaseHandler db = new DatabaseHandler(
									Setting.this);
							if (file.getName().endsWith(".mp3")){
								if (db.checkSongPath(file.getPath())) {
									Log.e("name update", file.getName());
									addSongToList(file);
								}
							}
						}
					}

				}
			}
		}
	}

	private void addSongToList(File file) {
		if (file.getName().endsWith(".mp3")) {
			DatabaseHandler db = new DatabaseHandler(Setting.this);
			String songName = file.getName().substring(0,
					(file.getName().length() - 4));
			String songPath = file.getPath();
			MediaMetadataRetriever media = new MediaMetadataRetriever();
			media.setDataSource(songPath);
			byte[] data = media.getEmbeddedPicture();
			String songArtist = media
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			String songAlbum = media
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			media.release();
			db.addSongData(new SongData(songName, songPath, songArtist,
					songAlbum, data, 0));
			db.close();
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(Setting.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
