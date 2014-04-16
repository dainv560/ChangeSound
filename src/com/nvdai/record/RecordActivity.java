package com.nvdai.record;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nvdai.changesound.MainActivity;
import com.nvdai.changesound.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class RecordActivity extends Activity {

	Integer[] freqset = { 11025, 16000, 22050, 44100 };
	private ArrayAdapter<Integer> adapter;

	Spinner spFrequency;
	ImageButton btStartRec, btPlayBack;
	Button btFormat;
	private static final String AUDIO_RECORDER_FOLDER = "ChangeSound/Recorder";
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4,
			AUDIO_RECORDER_FILE_EXT_3GP };
	private int currentFormat = 0;
	private String mFileName;

	Boolean recording = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.record_layout);
		btStartRec = (ImageButton) findViewById(R.id.btRecord);
		btFormat = (Button) findViewById(R.id.btFormat);
		btPlayBack = (ImageButton) findViewById(R.id.btPlayRecord);

		btStartRec.setOnClickListener(startRecOnClickListener);
		btFormat.setOnClickListener(formatOnClickListener);
		btPlayBack.setOnClickListener(playBackOnClickListener);

		spFrequency = (Spinner) findViewById(R.id.spFrequency);
		adapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item, freqset);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spFrequency.setAdapter(adapter);

	}

	private void recordingEnableButton(Boolean isRecording) {
		btPlayBack.setEnabled(!isRecording);
		btFormat.setEnabled(!isRecording);
		spFrequency.setEnabled(!isRecording);
	}

	private void playBackEnableButton(Boolean isPlaying) {
		btStartRec.setEnabled(!isPlaying);
		btFormat.setEnabled(!isPlaying);
		spFrequency.setEnabled(!isPlaying);
	}

	private void setFormatButtonCaption() {
		btFormat.setText(getString(R.string.audio_format) + " ("
				+ file_exts[currentFormat] + ")");
	}

	private void displayFormatDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String formats[] = { "MPEG 4", "3GPP" };

		builder.setTitle(getString(R.string.choose_format_title))
				.setSingleChoiceItems(formats, currentFormat,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								currentFormat = which;
								setFormatButtonCaption();

								dialog.dismiss();
							}
						}).show();
	}

	OnClickListener startRecOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (recording == false) {
				
				Thread recordThread = new Thread(new Runnable() {

					@Override
					public void run() {
						recording = true;
						startRecord();
					}

				});

				recordThread.start();
				recordingEnableButton(true);
				btStartRec.setImageResource(R.drawable.btn_stoprecord);
				btStartRec.setBackgroundResource(R.drawable.btn_stoprecord);
				Toast.makeText(RecordActivity.this, "Start Recording",
						Toast.LENGTH_SHORT).show();
			} else {
				recording = false;
				recordingEnableButton(recording);
				Toast.makeText(RecordActivity.this, "Stop Recording",
						Toast.LENGTH_SHORT).show();
				btStartRec.setImageResource(R.drawable.btn_record);
				btStartRec.setBackgroundResource(R.drawable.btn_record);
			}

		}
	};

	OnClickListener formatOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			displayFormatDialog();
		}
	};

	OnClickListener playBackOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			playRecord();
			btPlayBack.setImageResource(R.drawable.btn_pause);
			btPlayBack.setBackgroundResource(R.drawable.btn_pause);
		}

	};

	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);

		if (!file.exists()) {
			file.mkdirs();
		}

		mFileName = file.getAbsolutePath() + "/" + System.currentTimeMillis()
				+ file_exts[currentFormat];
		return (mFileName);
	}

	private void startRecord() {

		File file = new File(getFilename());

		int sampleFreq = (Integer) spFrequency.getSelectedItem();

		try {
			file.createNewFile();

			OutputStream outputStream = new FileOutputStream(file);
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
					outputStream);
			DataOutputStream dataOutputStream = new DataOutputStream(
					bufferedOutputStream);

			int minBufferSize = AudioRecord.getMinBufferSize(sampleFreq,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			short[] audioData = new short[minBufferSize];

			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, sampleFreq,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize);

			audioRecord.startRecording();

			while (recording) {
				int numberOfShort = audioRecord.read(audioData, 0,
						minBufferSize);
				for (int i = 0; i < numberOfShort; i++) {
					dataOutputStream.writeShort(audioData[i]);
				}
			}

			audioRecord.stop();
			dataOutputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void playRecord() {

		File file = new File(mFileName);

		int shortSizeInBytes = Short.SIZE / Byte.SIZE;

		int bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);
		short[] audioData = new short[bufferSizeInBytes];

		try {
			InputStream inputStream = new FileInputStream(file);
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					inputStream);
			DataInputStream dataInputStream = new DataInputStream(
					bufferedInputStream);

			int i = 0;
			while (dataInputStream.available() > 0) {
				audioData[i] = dataInputStream.readShort();
				i++;
			}

			dataInputStream.close();

			int sampleFreq = (Integer) spFrequency.getSelectedItem();

			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					sampleFreq, AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes,
					AudioTrack.MODE_STREAM);
			audioTrack.play();
			audioTrack.write(audioData, 0, bufferSizeInBytes);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(RecordActivity.this, MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}