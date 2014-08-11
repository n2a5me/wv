package com.nmn.watchvideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nmn.watchvideo.media.PlayerEngine;
import com.nmn.watchvideo.media.PlayerEngineListener;
import com.nmn.watchvideo.media.PlayerService;
import com.nmn.watchvideo.model.MusicObject;
import com.nmn.watchvideo.model.Playlist;
import com.nmn.watchvideo.model.Playlist.PlaylistPlaybackMode;
import com.nmn.watchvideo.util.Constants;
import com.nmn.watchvideo.util.Utils;
import com.nmn.watchvideo.view.VerticalSeekBar;

import de.greenrobot.event.EventBus;

public class VideoActivity extends Activity implements Callback,
		AnimationListener {

	private boolean is_show_confirm_buy_film = false;

	private boolean is_buy = false;

	private int duration_tobuy = 5 * 60;

	private int duration;

	private boolean ischangeLayout = false;

	private Playlist mPlaylist;

	private int animation_duration_long = 5000;

	private int animation_duration_short = 500;

	private TextView textViewPlayed;

	private TextView textViewLength;

	private TextView textTitle;

	private SeekBar timeSeekbar;
	private VerticalSeekBar volumeSeekbar;

	private SurfaceView surfaceViewFrame;

	private ImageButton imgPauseAndPlay;
	private ImageButton imgBacktoEnd;
	private ImageButton imgBackVideo;
	private ImageButton imgNextVideo;
	private ImageButton imgNextToEnd;
	private ImageButton imgBackActivity;
	private ImageButton imgVolume;
	private ImageButton imgShare;

	private LinearLayout header_control_group;
	private LinearLayout media_control_group;
	private LinearLayout timebar_control_group;
	private LinearLayout volume_control_group;

	private SurfaceHolder holder;

	private Animation hideMediaController;

	private RelativeLayout linearLayoutMediaController;

	private static final String TAG = "androidEx2 = VideoSample";

	private ProgressBar progressBar;

	private String url;
	private String title;
	private String episode;
	private String localFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/music.mp4";
	private boolean is_surface_created = false;

	private PlayerEngineListener playerEngineListener = new PlayerEngineListener() {

		@Override
		public void onTrackStreamError() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackStreamError");
		}

		@Override
		public void onTrackStop() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackStop");
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.play_video_button_bg);
		}

		@Override
		public boolean onTrackStart(int duration,MusicObject musicObject) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackStarta");
			VideoActivity.this.duration = duration;
			textTitle.setText(musicObject.getTitle() + " - "
					+ musicObject.getArtist());
			progressBar.setVisibility(View.GONE);
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.pause_video_button_bg);
			// prepare();
			textViewLength.setText(Utils.secondsToString(duration / 1000));
			linearLayoutMediaController.setVisibility(View.VISIBLE);
			animationHandler.postDelayed(animationCallBack,
					animation_duration_long);
			timeSeekbar.setMax(duration / 1000);
			return true;
		}

		@Override
		public void onTrackProgress(int seconds, int duration) {
			// TODO Auto-generated method stub

			if (!is_buy && seconds >= duration_tobuy
					&& !is_show_confirm_buy_film) {
				getPlayerEngine().pause();
				Log.e("MyPhoneState","getPlayerEngineInterface VideoPlayer 150");
				is_show_confirm_buy_film = true;
			} else {

			}
			curenprogress = seconds;
			textViewPlayed.setText(Utils.secondsToString(seconds) + "/");
			timeSeekbar.setMax(duration / 1000);
			timeSeekbar.setProgress(seconds);
		}

		@Override
		public void onTrackPause() {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackPause");
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.play_video_button_bg);
		}

		@Override
		public void onTrackChanged(MusicObject playlistEntry) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onTrackChanged");
				textTitle.setText(playlistEntry.getTitle() + " - "
						+ playlistEntry.getArtist());
			imgPauseAndPlay
					.setBackgroundResource(R.drawable.play_video_button_bg);

		}

		@Override
		public void onTrackBuffering(int percent) {
			// TODO Auto-generated method stub
			timeSeekbar.setSecondaryProgress(percent);
		}

		@Override
		public void onTrackStreamSlow() {
			// TODO Auto-generated method stub
			// getPlayerEngine().pause();
			// showDialogSlowStreamVideo();
		}
	};

	protected int curenprogress;

	private AudioManager audioManager;

	private int maxVolume;

	private int curVolume;

	private String accessToken;
	private int versionId;
	private String slug;
	private int price1;
	private int price2;

	private BroadcastReceiver mReceiver;

	private PlayerEngine getPlayerEngine() {
		Log.e("VideoActivity","getPlayerEngineInterface from VideoActivity 210");
		return UILApplication.getInstance().getPlayerEngineInterface();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Thread.setDefaultUncaughtExceptionHandler(new com.appvn.mobi.util.ExceptionHandler(this));
//		if (!LibsChecker.checkVitamioLibs(this))
//			return;
		mReceiver = new MyReciver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerService.ACTION_BIND_LISTENER);
		this.registerReceiver(mReceiver, filter);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_video);
		initView();
		getInfoMovie();

		UILApplication.getInstance().setPlayerEngineListener(
				playerEngineListener);

	}

	public class MyReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1 != null
					&& arg1.getAction() == PlayerService.ACTION_BIND_LISTENER) {
				if (holder != null) {
					getPlayerEngine().setSurfaceHolder(holder);
						if (!getPlayerEngine().isPlaying()) {
							Log.e(TAG, "surfaceCreated and start play");
							Log.e("MyPhoneState","getPlayerEngineInterface VideoPlayer 246");
							getPlayerEngine().play();
						}
				}
			}

		}

	}

	private String[] can_play_list_host = new String[] {
			"http://download002.fshare.vn", "http://download003.fshare.vn",
			"http://download123.fshare.vn" };

	private void getInfoMovie() {
		// TODO Auto-generated method stub
//		url=Environment.getExternalStorageDirectory().getAbsolutePath()+"/music.mp4";
//		url = "http://channelz2.s2.mp3.zdn.vn/zv/a85b9a44e1012aab4713ea88778ad089/5359ebd0/2014/02/09/a/5/a56d250a69f0f8f49809b23143790efd.mp4?start=0";
//		url="http://download057.fshare.vn/vip/tt1AgejG9P5EdAdWJvxvNxAt/BrideOfTheCentury01.mp4";
		url="http://channelz2.s1.mp3.zdn.vn/zv/18027f33f4eac7279e627a7efb700ee2/53e84dd0/2014/08/11/9/5/95bdc4e478ec4af46f8254bc3aefea1b.mp4";
		title = "title";
		episode = "eps";
	}

	private void initLocalPlaylist() {
		mPlaylist = new Playlist();
		MusicObject musicObject = new MusicObject(title, episode);
		musicObject.setFilePath(localFilePath);
		mPlaylist.addTrack(musicObject);
		mPlaylist.calculateOrder(true);
		mPlaylist.select(0);
		if (getPlayerEngine().getPlaylist() != mPlaylist && mPlaylist != null) {
			Log.e(TAG, "open playlist ");
			Log.e("MyPhoneState","getPlayerEngineInterface VideoPlayer 278");
			getPlayerEngine().openPlaylist(mPlaylist);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = new Intent(this, PlayerService.class);
		intent.setAction(PlayerService.ACTION_BIND_LISTENER);
		startService(intent);
		if (getPlayerEngine().isPlaying()) {
			progressBar.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mReceiver!=null)
		unregisterReceiver(mReceiver);
	}

	private void initPlaylist() {
		// TODO Auto-generated method stub
			mPlaylist = new Playlist();
			MusicObject musicObject = new MusicObject(title, episode);
			musicObject.setFilePath(url);
			mPlaylist.addTrack(musicObject);
			mPlaylist.calculateOrder(true);
			mPlaylist.select(0);
			if (getPlayerEngine().getPlaylist() != mPlaylist
					&& mPlaylist != null) {
				Log.e(TAG, "open playlist ");
				getPlayerEngine().openPlaylist(mPlaylist);
			}

	}

	@SuppressWarnings("deprecation")
	private void initView() {
		// TODO Auto-generated method stub
		textTitle = (TextView) findViewById(R.id.txTitle);
		linearLayoutMediaController = (RelativeLayout) findViewById(R.id.linearLayoutMediaController);
		header_control_group = (LinearLayout) findViewById(R.id.header_control_group);
		media_control_group = (LinearLayout) findViewById(R.id.media_controll_group);
		timebar_control_group = (LinearLayout) findViewById(R.id.timebar_control_group);
		volume_control_group = (LinearLayout) findViewById(R.id.volume_controll_group);
		hideMediaController = AnimationUtils.loadAnimation(this,
				R.anim.disapearing);
		hideMediaController.setAnimationListener(this);
		imgBackActivity = (ImageButton) findViewById(R.id.imgBack);
		imgVolume = (ImageButton) findViewById(R.id.imgVol);
		imgShare = (ImageButton) findViewById(R.id.imgShare);
		imgBacktoEnd = (ImageButton) findViewById(R.id.imgBacktoEnd);
		imgBackVideo = (ImageButton) findViewById(R.id.imgBackVideo);
		imgPauseAndPlay = (ImageButton) findViewById(R.id.imgPauseAndPlay);
		imgNextVideo = (ImageButton) findViewById(R.id.imgNextVideo);
		imgNextToEnd = (ImageButton) findViewById(R.id.imgNextToEnd);
		textViewPlayed = (TextView) findViewById(R.id.textViewPlayed);
		textViewLength = (TextView) findViewById(R.id.textViewLength);
		surfaceViewFrame = (SurfaceView) findViewById(R.id.surfaceViewFrame);
		surfaceViewFrame.setClickable(false);
		timeSeekbar = (SeekBar) findViewById(R.id.seekBarProgress);
		timeSeekbar.setProgress(0);
		volumeSeekbar = (VerticalSeekBar) findViewById(R.id.volumeSeekbar);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		volumeSeekbar.setMax(maxVolume);
		volumeSeekbar.setProgress(curVolume);
		holder = surfaceViewFrame.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		progressBar = (ProgressBar) findViewById(R.id.progressbar);

		setUserEventListenner();

	}

	private void setUserEventListenner() {
		// TODO Auto-generated method stub
		surfaceViewFrame.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (linearLayoutMediaController.getVisibility() == View.GONE) {
					linearLayoutMediaController.setVisibility(View.VISIBLE);
					showControl();
					animationHandler.postDelayed(animationCallBack,
							animation_duration_long);
				} else if (linearLayoutMediaController.getVisibility() == View.VISIBLE) {
					linearLayoutMediaController.setVisibility(View.VISIBLE);
					hideControl();
					animationHandler.removeCallbacks(animationCallBack);
				}
			}
		});

		volumeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				animationHandler.postDelayed(animationCallBack,
						animation_duration_short);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				animationHandler.removeCallbacks(animationCallBack);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);

			}
		});

		timeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				int progress = timeSeekbar.getProgress();
				if (is_buy) {
					if (progress > curenprogress) {
						getPlayerEngine().forward(
								(progress - curenprogress) * 1000);
					} else {
						if (getPlayerEngine().isPlaying()) {
							getPlayerEngine().rewind(
									(curenprogress - progress) * 1000);
						} else {
							getPlayerEngine().play(
									getPlayerEngine().getPlaylist()
											.getSelectedIndex());
							getPlayerEngine().forward((progress) * 1000);
						}
					}
				} else {
					if (progress >= duration_tobuy) {
						Log.e("", "process not buy" + progress + " " + " "
								+ duration_tobuy);
						if (curenprogress < duration_tobuy) {

						} else {
						}
					} else {
						if (progress > curenprogress) {
							getPlayerEngine().forward(
									(progress - curenprogress) * 1000);
						} else {
							if (getPlayerEngine().isPlaying()) {
								getPlayerEngine().rewind(
										(curenprogress - progress) * 1000);
							} else {
								getPlayerEngine().play(
										getPlayerEngine().getPlaylist()
												.getSelectedIndex());
								getPlayerEngine().forward((progress) * 1000);
							}
						}
					}

				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub

			}
		});

		imgBacktoEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						if (getPlayerEngine().getPlaylist().getSelectedIndex() > 0) {
							mPlaylist.select(0);
							getPlayerEngine().play();
						}
					} else {
						mPlaylist.select(0);
						getPlayerEngine().play();
					}
				}
			}
		});

		imgBackVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						if (getPlayerEngine().getPlaylist().getSelectedIndex() > 0) {

							getPlayerEngine().prev();
						}
					} else {
						getPlayerEngine().prev();
					}
				}
			}
		});

		imgNextToEnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						mPlaylist
								.select(mPlaylist.getMusicObjects().size() - 1);
						getPlayerEngine().play();
					} else {
						mPlaylist
								.select(mPlaylist.getMusicObjects().size() - 1);
						getPlayerEngine().play();
					}
				}
			}
		});

		imgNextVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPlaylist != null && mPlaylist.getMusicObjects().size() > 0) {
					if (getPlayerEngine().getPlaybackMode() == PlaylistPlaybackMode.NORMAL) {
						if (getPlayerEngine().getPlaylist().getSelectedIndex() < (getPlayerEngine()
								.getPlaylist().size() - 1)) {
							getPlayerEngine().next();
						}
					} else {
						getPlayerEngine().next();
					}
				}
			}
		});

		imgPauseAndPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getPlayerEngine().isPlaying()) {
					getPlayerEngine().pause();
					animationHandler.removeCallbacks(animationCallBack);
				} else {
					getPlayerEngine().play();
					animationHandler.postDelayed(animationCallBack,
							animation_duration_short);
				}
			}
		});

		imgBackActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		imgVolume.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				volume_control_group.setVisibility(View.VISIBLE);
				animationHandler.removeCallbacks(animationCallBack);
				animationHandler.postDelayed(animationCallBack,
						animation_duration_long);
			}
		});

		imgShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

	}

	private Runnable animationCallBack = new Runnable() {
		public void run() {
			hideControl();
		}
	};

	private Handler animationHandler = new Handler();

	protected ProgressDialog pDialog;

	// private boolean isShow = false;

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.i(TAG, "========== onProgressChanged : " + progress
				+ " from user: " + fromUser);
		if (!fromUser) {
			textViewPlayed.setText(Utils.secondsToString(progress));
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	// public void onStopTrackingTouch(SeekBar seekBar) {
	// if (player.isPlaying()) {
	// progressBarWait.setVisibility(View.VISIBLE);
	// player.seekTo(seekBar.getProgress() * 1000);
	// Log.i(TAG, "========== SeekTo : " + seekBar.getProgress());
	// }
	// }

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method
		Log.e("", "surface changed");
		Log.e("MyPhoneState","getPlayerEngineInterface VideoPlayer 629");
		if (!getPlayerEngine().isPlaying()) {
			Log.e(TAG, "surfaceChange and start play");
			getPlayerEngine().play();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		is_surface_created = true;

//		if (getIntent().getExtras() != null) {
//			if (url == null) {
//				localFilePath = getIntent()
//						.getStringExtra(Constants.MOVIE_PATH);
//				initLocalPlaylist();
//				is_buy = true;
//			} else {
				initPlaylist();
//			}
//		}
				Log.e("MyPhoneState","getPlayerEngineInterface VideoPlayer 649");
		getPlayerEngine().setSurfaceHolder(holder);
		if (!getPlayerEngine().isPlaying()) {
			Log.e(TAG, "surfaceCreated and start play");
			getPlayerEngine().play();
		} else {
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(TAG, "surfaceDestroyed");

	}

	public void prepare() {
		// Get the dimensions of the video
		Log.e("MyPhoneState","getPlayerEngineInterface VideoPlayer 667");
		int videoWidth = getPlayerEngine().getVideoWidth();
		int videoHeight = getPlayerEngine().getVideoHeight();
		float videoProportion = (float) videoWidth / (float) videoHeight;
		Log.e(TAG, "VIDEO SIZES: W: " + videoWidth + " H: " + videoHeight
				+ " PROP: " + videoProportion);

		// Get the width of the screen
		int screenWidth = Utils.getWidthScreen(this);
		int screenHeight = Utils.getHeightScreen(this);
		float screenProportion = (float) screenWidth / (float) screenHeight;
		Log.e(TAG, "VIDEO SIZES: W: " + screenWidth + " H: " + screenHeight
				+ " PROP: " + screenProportion);

		// Get the SurfaceView layout parameters
		android.view.ViewGroup.LayoutParams lp = surfaceViewFrame
				.getLayoutParams();

		if (videoProportion > screenProportion) {
			lp.width = screenWidth;
			lp.height = (int) ((float) screenWidth / videoProportion);
		} else {
			lp.width = (int) (videoProportion * (float) screenHeight);
			lp.height = screenHeight;
		}
		// Commit the layout parameters
		surfaceViewFrame.setLayoutParams(lp);
		surfaceViewFrame.setClickable(true);
	}

	public void onSeekComplete(MediaPlayer mp) {
	}

	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		linearLayoutMediaController.setVisibility(View.GONE);

	}

	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	public void onAnimationStart(Animation animation) {
		linearLayoutMediaController.setVisibility(View.GONE);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		ischangeLayout = !ischangeLayout;
		return ischangeLayout;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	protected void hideTimeBarControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_top);
		timebar_control_group.setVisibility(View.INVISIBLE);
		timebar_control_group.startAnimation(animation);
	}

	protected void showTimeBarControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_top);

		timebar_control_group.startAnimation(animation);
		timebar_control_group.setVisibility(View.VISIBLE);
	}

	protected void hideHeaderControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_in_top);
		header_control_group.setVisibility(View.INVISIBLE);
		header_control_group.startAnimation(animation);
	}

	protected void showHeaderControl() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.slide_out_top);

		header_control_group.startAnimation(animation);
		header_control_group.setVisibility(View.VISIBLE);
	}

	protected void showControl() {
		showHeaderControl();
		showTimeBarControl();
		media_control_group.setVisibility(View.VISIBLE);
	}

	protected void hideControl() {
		hideHeaderControl();
		hideTimeBarControl();
		media_control_group.setVisibility(View.GONE);
		volume_control_group.setVisibility(View.GONE);
		linearLayoutMediaController.setVisibility(View.GONE);
	}

//	public void onEventMainThread(BuySuccessEvent event) {
//		if (pDialog != null) {
//			pDialog.dismiss();
//		}
//		Log.e(TAG, "onEventMainThread BuySuccessEvent");
//		if (is_surface_created) {
//			getPlayerEngine().play();
//		}
//
//		is_buy = true;
//	}
	
//	public void onEventMainThread(BuyFailEvent event) {
//		if (pDialog != null) {
//			pDialog.dismiss();
//		}
//		showDialogBuyFilmFail();
//	}

	protected void showDialogSlowStreamVideo() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("message")
				.setMessage("Slow stream")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
						finish();
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								arg0.dismiss();
								VideoActivity.this.finish();
							}
						});
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Intent intent = new Intent(this, PlayerService.class);
		intent.setAction(PlayerService.ACTION_STOP);
		startService(intent);
	}
}
