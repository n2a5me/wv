package com.nmn.watchvideo;

import java.util.Locale;

import com.nmn.watchvideo.media.PlayerEngine;
import com.nmn.watchvideo.media.PlayerEngineListener;
import com.nmn.watchvideo.media.PlayerService;
import com.nmn.watchvideo.model.Playlist;
import com.nmn.watchvideo.model.Playlist.PlaylistPlaybackMode;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;


//@ReportsCrashes(formKey = "dExlWVJNZGd1RW51MUt5WmtmUElrR1E6MQ")
public class UILApplication extends Application {

	public static final String TAG = "UILApplication";
	private int listDetailId = -1;
	private Configuration config;
	private SharedPreferences sharedPref;
	public int getListDetailId() {
		return listDetailId;
	}

	public void setListDetailId(int listDetailId) {
		this.listDetailId = listDetailId;
	}

//	private Thread.UncaughtExceptionHandler androidDefaultUEH;
//	private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
//		private String LINE_SEPARATOR="\n";
//		public void uncaughtException(Thread thread, Throwable exception) {
//			StringWriter stackTrace = new StringWriter();
//			exception.printStackTrace(new PrintWriter(stackTrace));
//			StringBuilder errorReport = new StringBuilder();
//			errorReport.append("************ CAUSE OF ERROR ************\n\n");
//			errorReport.append(stackTrace.toString());
//			errorReport.append("\n************ DEVICE INFORMATION ***********\n");
//			errorReport.append("Brand: ");
//			errorReport.append(Build.BRAND);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("Device: ");
//			errorReport.append(Build.DEVICE);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("Model: ");
//			errorReport.append(Build.MODEL);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("Id: ");
//			errorReport.append(Build.ID);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("Product: ");
//			errorReport.append(Build.PRODUCT);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("\n************ FIRMWARE ************\n");
//			errorReport.append("SDK: ");
//			errorReport.append(Build.VERSION.SDK);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("Release: ");
//			errorReport.append(Build.VERSION.RELEASE);
//			errorReport.append(LINE_SEPARATOR);
//			errorReport.append("Incremental: ");
//			errorReport.append(Build.VERSION.INCREMENTAL);
//			errorReport.append(LINE_SEPARATOR);
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
//			Date now = new Date();
//			String date = simpleDateFormat.format(now);
//			CommonUtils.writetoFile(date+"\n"+errorReport, "crashes.log");
//			Intent a=new Intent();
//			a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			Intent intent = new Intent(getApplicationContext(), DisplayCrashDialog.class);
//			intent.putExtra("error", errorReport.toString());
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(10);
//			androidDefaultUEH.uncaughtException(thread, exception);
//		}
//	};
//	
	@Override
	public void onCreate() {
		super.onCreate();
//		androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
//		Thread.setDefaultUncaughtExceptionHandler(handler);
		instance = this;
		// The following line triggers the initialization of ACRA
//        ACRA.init(this);
	}

	public enum PlayerClass {
		TRACK;
	}

	/**
	 * Tag used for DDMS logging
	 */

	/**
	 * Singleton pattern
	 */
	private static UILApplication instance;

	/**
	 * Image cache, one for all activities and orientations
	 */

	/**
	 * Web request cache, one for all activities and orientations
	 */

	/**
	 * Service player engine
	 */
	public PlayerEngine mServicePlayerEngine;

	/**
	 * Media player playing
	 */
	private MediaPlayer mCurrentMedia;

	/**
	 * Equalizer instance for runtime manipulation
	 */

	/**
	 * Equalizer settings
	 */

	/**
	 * Intent player engine
	 */
	private PlayerEngine mPlayerEngine;

	/**
	 * Player engine listener
	 */
	private PlayerEngineListener mPlayerEngineListener;

	/**
	 * Stored in Application instance in case we destroy Player service
	 */

	private Playlist mPlaylist;

	/**
	 * Provides interface for download related actions.
	 */

	/**
	 * Handler for player related gestures.
	 */

	public static UILApplication getInstance() {
		return instance;
	}

	/**
	 * Access to global image cache across Activity instances
	 * 
	 * @return
	 */

	/**
	 * This setter should be only used for setting real player engine interface,
	 * e.g. used to pass Player Service's engine
	 * 
	 * @param playerEngine
	 */
	public void setConcretePlayerEngine(PlayerEngine playerEngine) {
		// add some logic since there are two concrete implementations now.
		if (this.mServicePlayerEngine != null
				&& this.mServicePlayerEngine != playerEngine) {
			this.mServicePlayerEngine.stop();
		}
		this.mServicePlayerEngine = playerEngine;
	}

	public PlayerEngine getConcretePlayerEngine() {
		return mServicePlayerEngine;
	}

	public void setMyCurrentMedia(MediaPlayer player) {
		this.mCurrentMedia = player;
	}

	/**
	 * Equalizer preset to use when the next time an Equalizer instance is
	 * created. -1 is reserved for custom preset -2 is reserved for no preset
	 */
	private short mEqualizerPreset = -2;

	public void setEqualizerPreset(short preset) {
		mEqualizerPreset = preset;
	}

	public short getEqualizerPreset() {
		return mEqualizerPreset;
	}

	/**
	 * This getter allows performing logical operations on the player engine's
	 * interface from UI space
	 * 
	 * @return
	 */

	private PlayerEngine mIntentPlayerEngine;

	public PlayerEngine getPlayerEngineInterface() {
		// request service bind
		Log.e("UiApplication","getPlayerEngineInterface");
		if (mIntentPlayerEngine == null) {
			Log.e("UiApplication","getPlayerEngineInterface == null");
			mIntentPlayerEngine = new IntentPlayerEngine();
		}
		return mIntentPlayerEngine;
	}

	// public GesturesHandler getPlayerGestureHandler(){
	// if(mPlayerGestureHandler == null){
	// mPlayerGestureHandler = new GesturesHandler(this, new
	// PlayerGestureCommandRegiser(getPlayerEngineInterface()));
	// }
	// return mPlayerGestureHandler;
	// }

	/**
	 * This function allows to add listener to the concrete player engine
	 * 
	 * @param l
	 */
	public void setPlayerEngineListener(PlayerEngineListener l) {
		Log.e("UiApplication","getPlayerEngineInterface from setPlayerEngineListener");
		getPlayerEngineInterface().setListener(l);
	}

	/**
	 * This function is used by PlayerService on ACTION_BIND_LISTENER in order
	 * to get to Application's exposed listener.
	 * 
	 * @return
	 */
	public PlayerEngineListener fetchPlayerEngineListener() {
		return mPlayerEngineListener;
	}

	/**
	 * Returns current playlist, used in PlayerSerive in onStart method
	 * 
	 * @return //
	 */
	public Playlist fetchPlaylist() {
		return mPlaylist;
	}

	/**
	 * Retrieves application's version number from the manifest
	 * 
	 * @return
	 */
	public String getVersion() {
		String version = "0.0.0";

		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}

	//
	// public String getDownloadFormat() {
	// return PreferenceManager.getDefaultSharedPreferences(this).getString(
	// "download_format", JamendoGet2Api.ENCODING_MP3);
	// }

	// public String getStreamEncoding() {
	// // http://groups.google.com/group/android-developers/msg/c546760177b22197
	// // According to JBQ: ogg files are supported but not streamable
	// return JamendoGet2Api.ENCODING_MP3;
	// }
	//
	// public DownloadManager getDownloadManager() {
	// return mDownloadManager;
	// }

	/**
	 * Since 0.9.8.7 we embrace "bindless" PlayerService thus this adapter. No
	 * big need of code refactoring, we just wrap sending intents around defined
	 * interface
	 * 
	 * @author Lukasz Wisniewski
	 */
	private class IntentPlayerEngine implements PlayerEngine {

		@Override
		public Playlist getPlaylist() {
			return mPlaylist;
		}

		@Override
		public boolean isPlaying() {
			if (mServicePlayerEngine == null) {
				// service does not exist thus no playback possible
				return false;
			} else {
				return mServicePlayerEngine.isPlaying();
			}
		}

		@Override
		public void next() {
			if (mServicePlayerEngine != null) {
				playlistCheck();
				mServicePlayerEngine.next();
			} else {
				startAction(PlayerService.ACTION_NEXT);
			}
		}

		@Override
		public void openPlaylist(Playlist playlist) {
			mPlaylist = playlist;
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.openPlaylist(playlist);
			}
		}

		@Override
		public void pause() {
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.pause();
			}
		}

		@Override
		public void play() {
			if (mServicePlayerEngine != null) {
				playlistCheck();
				mServicePlayerEngine.play();
			} else {
				startAction(PlayerService.ACTION_PLAY);
			}
		}

		@Override
		public void prev() {
			if (mServicePlayerEngine != null) {
				playlistCheck();
				mServicePlayerEngine.prev();
			} else {
				startAction(PlayerService.ACTION_PREV);
			}
		}

		@Override
		public void setListener(PlayerEngineListener playerEngineListener) {
			mPlayerEngineListener = playerEngineListener;
			// we do not want to set this listener if Service
			// is not up and a new listener is null
			if (mServicePlayerEngine != null || mPlayerEngineListener != null) {
				startAction(PlayerService.ACTION_BIND_LISTENER);
			}
		}

		@Override
		public void skipTo(int index) {
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.skipTo(index);
			}
		}

		@Override
		public void stop() {
			// startAction(PlayerService.ACTION_STOP);
			stopService(new Intent(UILApplication.this, PlayerService.class));
		}

		private void startAction(String action) {
			Intent intent = new Intent(UILApplication.this, PlayerService.class);
			intent.setAction(action);
			startService(intent);
		}

		/**
		 * This is required if Player Service was binded but playlist was not
		 * passed from Application to Service and one of buttons: play, next,
		 * prev is pressed
		 */
		private void playlistCheck() {
			if (mServicePlayerEngine != null) {
				if (mServicePlayerEngine.getPlaylist() == null
						&& mPlaylist != null) {
					mServicePlayerEngine.openPlaylist(mPlaylist);
				}
			}
		}

		@Override
		public void setPlaybackMode(PlaylistPlaybackMode aMode) {
			mPlaylist.setPlaylistPlaybackMode(aMode);
		}

		@Override
		public PlaylistPlaybackMode getPlaybackMode() {
			return mPlaylist.getPlaylistPlaybackMode();
		}

		@Override
		public void forward(int time) {
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.forward(time);
			}

		}

		@Override
		public void rewind(int time) {
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.rewind(time);
			}

		}

		@Override
		public void prevList() {
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.prevList();
			}

		}

		@Override
		public void play(int index) {
			// TODO Auto-generated method stub
			if (mServicePlayerEngine != null) {
				playlistCheck();
				mServicePlayerEngine.play(index);
			}
			// else {
			// startAction(PlayerService.ACTION_PLAY_SELECTION_SONG);
			// }
		}

		@Override
		public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
			// TODO Auto-generated method stub
			Log.e("", "setsurface holder in application "+mServicePlayerEngine);
			if (mServicePlayerEngine != null) {
				mServicePlayerEngine.setSurfaceHolder(surfaceHolder);
			}
		}

		@Override
		public int getVideoWidth() {
			// TODO Auto-generated method stub
			if (mServicePlayerEngine != null) {
				return mServicePlayerEngine.getVideoWidth();
			}
			return 0;
		}

		@Override
		public int getVideoHeight() {
			// TODO Auto-generated method stub
			if (mServicePlayerEngine != null) {
				return mServicePlayerEngine.getVideoHeight();
			}
			return 0;
		}

	}

	public int task_webview_count = 0;

}
