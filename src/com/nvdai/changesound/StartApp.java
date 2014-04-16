package com.nvdai.changesound;

import java.io.File;

import com.nvdai.additionclass.FileExtensionFilter;
import com.nvdai.changesound.R;
import com.nvdai.database.DatabaseHandler;
import com.nvdai.database.SongData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.Window;

public class StartApp extends Activity {
	@SuppressLint("SdCardPath")
	final String MEDIA_PATH = new String("/sdcard/ChangeSound/Music/");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		ActivityManager am = (ActivityManager) this
//				.getSystemService(ACTIVITY_SERVICE);
//		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//		Iterator<ActivityManager.RunningTaskInfo> itr = taskInfo.iterator();
//		while (itr.hasNext()) {
//			ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) itr
//					.next();
//			int id = runningTaskInfo.id;
//			CharSequence desc = runningTaskInfo.description;
//			int numOfActivities = runningTaskInfo.numRunning;
//			String topActivity = runningTaskInfo.topActivity
//					.getShortClassName();
//			Log.e("all task:", "" + id + "=" + desc + "=" + numOfActivities
//					+ "=" + topActivity);
//		}
//		Log.e("topActivity", "Start CURRENT Activity ::"
//				+ taskInfo.get(0).topActivity.getClassName());

		setContentView(R.layout.starting_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Thread loaddingThread = new Thread() {
			public void run() {
				try {
					
					DatabaseHandler db = new DatabaseHandler(StartApp.this);
					if (db.getSongDatasCount() == 0){
						File home = new File(MEDIA_PATH);
						if (home.listFiles(new FileExtensionFilter()).length > 0) {
							for (File file : home
									.listFiles(new FileExtensionFilter())) {
								String songName = file.getName().substring(0,
										(file.getName().length() - 4));
								String songPath = file.getPath();
								MediaMetadataRetriever media = new MediaMetadataRetriever();
								media.setDataSource(songPath);
								byte[] data;
								if (media.getEmbeddedPicture() != null) {
									data = media.getEmbeddedPicture();
								} else {
									data = null;
								}
								String songArtist = media
										.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
								String songAlbum = media
										.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
								media.release();
								db.addSongData(new SongData(songName, songPath,
										songArtist, songAlbum, data,0));
							}
						}
					}else{
						sleep(2000);
					}
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					startActivity(new Intent(getApplicationContext(),
							MainActivity.class));
					overridePendingTransition(R.anim.right_in, R.anim.left_out);
					finish();
				}
			}
		};
		loaddingThread.start();
	}

};
