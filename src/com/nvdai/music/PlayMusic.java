package com.nvdai.music;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.nvdai.changesound.R;

public class PlayMusic extends FragmentActivity implements
		OnSeekBarChangeListener, OnCompletionListener, AnimationListener,
		OnGestureListener {

	private Context context;
	private ImageButton btn_play;
	private ImageButton btn_stop;
	private ImageButton btn_next;
	private ImageButton btn_previous;
	private ImageButton btn_shuffle;
	private ImageButton btn_repeat;
	private ImageButton btn_library;
	private ImageButton btn_back;
	private ImageButton btn_forward;
	private ImageButton btn_backward;
	private ImageSwitcher imageSwitcherMusic;
	private Handler mHandler = new Handler();
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private SeekBar songProgressBar;
	private Animation inFade;
	private Animation outFade;
	private MediaPlayer mp;
	private SongsManager songManager;
	private Utilities utils;
	private Equalizer equalizer;
	private ArrayList<SongDetails> songsList = new ArrayList<SongDetails>();
	private int currentSongIndex = 0;
	private boolean isShuffling = false;
	private int Repeating = 0;
	private int seekForwardTime = 5000;
	private int seekBackwardTime = 5000;
	private GestureDetector gestureDetector;
	private int SWIPE_MIN_VELOCITY = 100;
	private int SWIPE_MIN_DISTANCE = 100;
	private Animation inLeft, outLeft, inRight, outRight;
	private String IfArtist, IfAlbum, IfLength;
	private int LEFT_DIRECTION = 1;
	private int RIGHT_DIRECTION = 2;
	private int NO_DIRECTION = 0;
	private String typeSort = "name";
	private AudioManager audio;
	private int MAX_VOLUME;
	private int MIN_VOLUME = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.play_music_layout);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		context = this;
		btn_play = (ImageButton) findViewById(R.id.btPlay);
		btn_stop = (ImageButton) findViewById(R.id.btStop);
		btn_next = (ImageButton) findViewById(R.id.btNext);
		btn_previous = (ImageButton) findViewById(R.id.btPrevious);
		btn_repeat = (ImageButton) findViewById(R.id.btRepeat);
		btn_shuffle = (ImageButton) findViewById(R.id.btShuffle);
		btn_library = (ImageButton) findViewById(R.id.btn_playlist);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_forward = (ImageButton) findViewById(R.id.btForward);
		btn_backward = (ImageButton) findViewById(R.id.btBackward);
		imageSwitcherMusic = (ImageSwitcher) findViewById(R.id.imageSwitcherMusic);
		songTitleLabel = (TextView) findViewById(R.id.songName);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		MAX_VOLUME = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		imageSwitcherMusic.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
				imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				return imageView;
			}
		});

		// set animation variable
		inFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
		outFade = AnimationUtils.loadAnimation(context, R.anim.fade_out);
		inLeft = AnimationUtils.loadAnimation(this, R.anim.left_in);
		outLeft = AnimationUtils.loadAnimation(this, R.anim.left_out);
		inRight = AnimationUtils.loadAnimation(this, R.anim.right_in);
		outRight = AnimationUtils.loadAnimation(this, R.anim.right_out);

		// set new gestureDetector
		gestureDetector = new GestureDetector(this, this);

		// create new MediaPlayer, SongManager and Utilities
		mp = new MediaPlayer();
		songManager = new SongsManager();
		utils = new Utilities();

		songProgressBar.setOnSeekBarChangeListener(this);
		mp.setOnCompletionListener(this);

		songsList = songManager.getPlaylist(PlayMusic.this, typeSort);

		loadSongInformation(0, NO_DIRECTION);

		// click button play
		btn_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						btn_play.setImageResource(R.drawable.btn_play);
						btn_play.setBackgroundResource(R.drawable.btn_play);
					}
				} else {
					if (mp != null) {
						mp.start();
						btn_play.setImageResource(R.drawable.btn_pause);
						btn_play.setBackgroundResource(R.drawable.btn_pause);
					}
				}

			}
		});

		// click button stop
		btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mp.stop();
				loadSongInformation(currentSongIndex, NO_DIRECTION);
				songProgressBar.setProgress(0);
				songCurrentDurationLabel.setText("0:00");
				btn_play.setImageResource(R.drawable.btn_play);
				btn_play.setBackgroundResource(R.drawable.btn_play);
			}
		});

		// click button next
		btn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nextSong();
			}
		});

		// click button previous
		btn_previous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				previousSong();
			}
		});

		btn_forward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int currentPosition = mp.getCurrentPosition();
				if (currentPosition + seekForwardTime <= mp.getDuration()) {
					mp.seekTo(currentPosition + seekForwardTime);
				} else {
					mp.seekTo(mp.getDuration());
				}
			}
		});

		btn_backward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int currentPosition = mp.getCurrentPosition();
				if (currentPosition - seekBackwardTime >= 0) {
					mp.seekTo(currentPosition - seekBackwardTime);
				} else {
					mp.seekTo(0);
				}
			}
		});

		// click button playlist
		btn_library.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						SongsLibrary.class);
				startActivityForResult(intent, 100);
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
			}
		});

		// click button shuffle
		btn_shuffle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShuffling) {
					isShuffling = false;
					btn_repeat.setEnabled(true);
					btn_shuffle.setImageResource(R.drawable.icon_shuffle);
					btn_shuffle.setBackgroundResource(R.drawable.icon_shuffle);
				} else {
					isShuffling = true;
					Repeating = 0;
					btn_repeat.setEnabled(false);
					btn_shuffle.setImageResource(R.drawable.icon_shuffle_hover);
					btn_shuffle
							.setBackgroundResource(R.drawable.icon_shuffle_hover);
					btn_repeat.setImageResource(R.drawable.icon_repeat);
					btn_repeat.setBackgroundResource(R.drawable.icon_repeat);
				}
			}
		});

		// click button repeat
		btn_repeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Repeating == 2) {
					Repeating = 0;
					btn_repeat.setImageResource(R.drawable.icon_repeat);
					btn_repeat.setBackgroundResource(R.drawable.icon_repeat);
				} else {
					if (Repeating == 1) {
						Repeating = 2;
						btn_repeat.setImageResource(R.drawable.icon_repeat_one);
						btn_repeat
								.setBackgroundResource(R.drawable.icon_repeat_one);
					} else {
						Repeating = 1;
						btn_repeat.setImageResource(R.drawable.icon_repeat_all);
						btn_repeat
								.setBackgroundResource(R.drawable.icon_repeat_all);
					}
				}
			}
		});

		// click button back
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PlayMusic.this, MusicActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.right_out);
			}
		});
		
		songProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				//Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_SHORT).show();
				if (mp.isPlaying()){
					int nextTime  = utils.progressToTimer(progress, mp.getDuration());
					mp.seekTo(nextTime);
				}
				else {
					int currentProgress = (int) (utils.getProgressPercentage(mp.getCurrentPosition(),
							mp.getDuration()));
					songProgressBar.setProgress(currentProgress);
				}
			}
		});
	}

	private void changeFragment(int direction) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		Fragment_song_details fragment = new Fragment_song_details();
		Bundle bundle = new Bundle();
		IfArtist = songsList.get(currentSongIndex).getSongArtist();
		IfAlbum = songsList.get(currentSongIndex).getSongAlbum();
		IfLength = utils.milliSecondsToTimer(mp.getDuration());
		bundle.putString("artist", IfArtist);
		bundle.putString("album", IfAlbum);
		bundle.putString("length", IfLength);
		fragment.setArguments(bundle);

		if (direction == NO_DIRECTION) {
			imageSwitcherMusic.setInAnimation(inFade);
			imageSwitcherMusic.setOutAnimation(outFade);
			fragmentTransaction.setCustomAnimations(R.anim.fade_in,
					R.anim.fade_out);
		}
		if (direction == RIGHT_DIRECTION) {
			imageSwitcherMusic.setInAnimation(inRight);
			imageSwitcherMusic.setOutAnimation(outLeft);
			fragmentTransaction.setCustomAnimations(R.anim.right_in,
					R.anim.left_out);
		}
		if (direction == LEFT_DIRECTION) {
			imageSwitcherMusic.setInAnimation(inLeft);
			imageSwitcherMusic.setOutAnimation(outRight);
			fragmentTransaction.setCustomAnimations(R.anim.left_in,
					R.anim.right_out);
		}

		if (songsList.get(currentSongIndex).getImage() != null) {
			imageSwitcherMusic.setImageDrawable(new BitmapDrawable(songsList
					.get(currentSongIndex).getImage()));
		} else {
			imageSwitcherMusic.setImageResource(R.drawable.icon_music);
		}

		fragmentTransaction.replace(R.id.flMusicInformation, fragment, "F1")
				.commit();
	}

	public void loadSongInformation(int songIndex, int direct) {
		if (!mp.isPlaying()) {
			try {
				// set mediaplayer
				mp.reset();
				if (songsList.size() == 0) {
					Toast.makeText(PlayMusic.this, "No songs to play",
							Toast.LENGTH_SHORT).show();
					btn_play.setEnabled(false);
					btn_stop.setEnabled(false);
					btn_forward.setEnabled(false);
					btn_backward.setEnabled(false);
					btn_next.setEnabled(false);
					btn_previous.setEnabled(false);
					return;
				}
				mp.setDataSource(songsList.get(songIndex).getSongPath());
				mp.prepare();

				// change fragment of music information
				changeFragment(direct);

				// set other information
				String songTitle = songsList.get(songIndex).getSongName();
				songTitleLabel.setText(songTitle);
				songTitleLabel.setAnimation(inFade);
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);
				updateProgressBar();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void nextSong() {
		if (!isShuffling) {
			if (currentSongIndex < (songsList.size() - 1)) {
				currentSongIndex++;
			} else {
				currentSongIndex = 0;
			}
		} else {
			Random rand = new Random();
			currentSongIndex = rand.nextInt(songsList.size());
		}

		if (mp.isPlaying()) {
			playSong(currentSongIndex);
		} else {
			loadSongInformation(currentSongIndex, RIGHT_DIRECTION);
		}

		changeFragment(RIGHT_DIRECTION);
	}

	public void previousSong() {
		if (!isShuffling) {
			if (currentSongIndex > 0) {
				currentSongIndex--;
			} else {
				currentSongIndex = (songsList.size() - 1);
			}
		} else {
			Random rand = new Random();
			currentSongIndex = rand.nextInt(songsList.size());
		}

		if (mp.isPlaying()) {
			playSong(currentSongIndex);
		} else {
			loadSongInformation(currentSongIndex, LEFT_DIRECTION);
		}

		changeFragment(LEFT_DIRECTION);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			currentSongIndex = data.getExtras().getInt("songIndex");
			String newTypeSort = data.getExtras().getString("typeSort");
			if (newTypeSort != typeSort) {
				typeSort = newTypeSort;
				songsList.clear();
				songsList = songManager.getPlaylist(context, typeSort);
			}
			playSong(currentSongIndex);
			changeFragment(NO_DIRECTION);
		}
	}

	public void setupEqualizer() {
		equalizer = new Equalizer(0, mp.getAudioSessionId());
		equalizer.setEnabled(true);
	}

	// method to play music
	public void playSong(int songIndex) {

		try {
			mp.reset();
			mp.setDataSource(songsList.get(songIndex).getSongPath());
			mp.prepare();
			mp.start();

			String songTitle = songsList.get(songIndex).getSongName();
			songTitleLabel.setText(songTitle);
			songTitleLabel.setAnimation(inFade);

			btn_play.setImageResource(R.drawable.btn_pause);
			btn_play.setBackgroundResource(R.drawable.btn_pause);

			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	public Runnable mUpdateTimeTask = new Runnable() {

		@Override
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

			songTotalDurationLabel.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			songCurrentDurationLabel.setText(""
					+ utils.milliSecondsToTimer(currentDuration));

			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			songProgressBar.setProgress(progress);

			mHandler.postDelayed(this, 100);

		}
	};


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentDuration = utils.progressToTimer(
				songProgressBar.getProgress(), totalDuration);

		mp.seekTo(currentDuration);

		updateProgressBar();

	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		if (Repeating == 2) {
			playSong(currentSongIndex);
		} else if (isShuffling) {
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else {
			if (currentSongIndex < (songsList.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex++;
			} else {
				if (Repeating == 1) {
					playSong(0);
					currentSongIndex = 0;
				} else {
					mp.stop();
					loadSongInformation(0, NO_DIRECTION);
					songProgressBar.setProgress(0);
					songCurrentDurationLabel.setText("0:00");
					btn_play.setImageResource(R.drawable.btn_play);
					btn_play.setBackgroundResource(R.drawable.btn_play);
				}
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAnimationEnd(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {

	}

	@Override
	public void onAnimationStart(Animation arg0) {

	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float ev1X = e1.getX();
		float ev2X = e2.getX();
		float ev1Y = e1.getY();
		float ev2Y = e2.getY();

		final float xdistance = Math.abs(ev1X - ev2X);
		final float ydistance = Math.abs(ev1Y - ev2Y);
		final float xvelocity = Math.abs(velocityX);

		if (xvelocity > SWIPE_MIN_VELOCITY && xdistance > SWIPE_MIN_DISTANCE
				&& ydistance < SWIPE_MIN_DISTANCE) {
			if (ev1X < ev2X) {
				previousSong();
			} else {
				nextSong();
			}
		}

		if ((xdistance < SWIPE_MIN_DISTANCE)
				&& (ydistance > SWIPE_MIN_DISTANCE)) {

			int currentVolume = audio
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			int changing = (int) Math.abs(ev1Y - ev2Y) / SWIPE_MIN_DISTANCE;
			if (ev1Y > ev2Y) {
				int afterVolume = Math
						.min(currentVolume + changing, MAX_VOLUME);
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, afterVolume,
						AudioManager.FLAG_SHOW_UI);
			} else {
				int afterVolume = Math
						.max(currentVolume - changing, MIN_VOLUME);
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, afterVolume,
						AudioManager.FLAG_SHOW_UI);
			}
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(PlayMusic.this, MusicActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
