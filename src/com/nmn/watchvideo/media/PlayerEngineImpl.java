/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmn.watchvideo.media;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;

import com.nmn.watchvideo.model.MusicObject;
import com.nmn.watchvideo.model.Playlist;
import com.nmn.watchvideo.model.Playlist.PlaylistPlaybackMode;


public class PlayerEngineImpl implements PlayerEngine {

	/**
	 * Time frame - used for counting number of fails within that time
	 */
	private static final long FAIL_TIME_FRAME = 1000;

	/**
	 * Acceptable number of fails within FAIL_TIME_FRAME
	 */
	private static final int ACCEPTABLE_FAIL_NUMBER = 2;

	protected static final int MSG_MP_RELEASE = 5;

	/**
	 * Beginning of last FAIL_TIME_FRAME
	 */
	private long mLastFailTime;

	/**
	 * Number of times failed within FAIL_TIME_FRAME
	 */
	private long mTimesFailed;

	/**
	 * Simple MediaPlayer extensions, adds reference to the current track
	 * 
	 * @author Lukasz Wisniewski
	 */
	private class InternalMediaPlayer extends MediaPlayer {

		/**
		 * Keeps record of currently played track, useful when dealing with
		 * multiple instances of MediaPlayer
		 */
		public MusicObject playlistEntry;

		/**
		 * Still buffering
		 */
		public boolean preparing = false;

		/**
		 * Determines if we should play after preparation, e.g. we should not
		 * start playing if we are pre-buffering the next track and the old one
		 * is still playing
		 */
		public boolean playAfterPrepare = false;

	}

	/**
	 * InternalMediaPlayer instance (maybe add another one for cross-fading)
	 */
	private InternalMediaPlayer mCurrentMediaPlayer;

	/**
	 * Listener to the engine events
	 */
	private PlayerEngineListener mPlayerEngineListener;

	/**
	 * Playlist
	 */
	private Playlist mPlaylist = null;

	/**
	 * Playlist of song played before
	 */
	private Playlist prevPlaylist = null;

	private Context context;

	/**
	 * Handler to the context thread
	 */
	private Handler mHandler;

	/**
	 * Runnable periodically querying Media Player about the current position of
	 * the track and notifying the listener
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {

			if (mPlayerEngineListener != null) {

				if (mCurrentMediaPlayer != null
						&& !mCurrentMediaPlayer.preparing
						&& mCurrentMediaPlayer != null) {
					mPlayerEngineListener.onTrackProgress(
							mCurrentMediaPlayer.getCurrentPosition() / 1000,
							mCurrentMediaPlayer.getDuration());

				}
				mHandler.postDelayed(this, 1000);
			}
		}
	};

	protected Handler mMPHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_MP_RELEASE) {
				if (mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT1
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.REPEAT1) {
					play(mPlaylist.getSelectedIndex());
				} else if (!mPlaylist.isLastTrackOnList()
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.REPEAT
						|| mPlaylist.getPlaylistPlaybackMode() == PlaylistPlaybackMode.SHUFFLE_AND_REPEAT) {
					next();
				} else

				{
					stop();
				}
			}
		}
	};

	/**
	 * Default constructor
	 */
	public PlayerEngineImpl(Context context) {
		mLastFailTime = 0;
		mTimesFailed = 0;
		mHandler = new Handler();
		this.context = context;
	}

	@Override
	public void next() {
		if (mPlaylist != null) {
			mPlaylist.selectNext();
			play();
		}
	}

	@Override
	public void openPlaylist(Playlist playlist) {
		if (!playlist.isEmpty()) {
			prevPlaylist = mPlaylist;
			mPlaylist = playlist;
		} else
			mPlaylist = null;
	}

	@Override
	public void pause() {
		if (mCurrentMediaPlayer != null) {

			if (mCurrentMediaPlayer.preparing) {
				mCurrentMediaPlayer.playAfterPrepare = false;
				return;
			}

			if (mCurrentMediaPlayer.isPlaying()) {
				mCurrentMediaPlayer.pause();
				if (mPlayerEngineListener != null)
					mPlayerEngineListener.onTrackPause();
				return;
			}
		}
	}

	@Override
	public void play(int index) {

		if (mPlaylist != null) {
			mPlaylist.select(index);

			if (mCurrentMediaPlayer == null) {
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (!mCurrentMediaPlayer.preparing
					&& mPlayerEngineListener.onTrackStart(mCurrentMediaPlayer
							.getDuration(), getPlaylist().getSelectedTrack()) == false) {
				return;
			}

			if (mCurrentMediaPlayer != null
					&& mCurrentMediaPlayer.playlistEntry != mPlaylist
							.getSelectedTrack()) {
				cleanUp();
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			} else {
				cleanUp();
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mCurrentMediaPlayer == null)
				return;

			if (!mCurrentMediaPlayer.preparing) {

				if (!mCurrentMediaPlayer.isPlaying()) {
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 1000);

					mCurrentMediaPlayer.start();
				}
			} else {

				mCurrentMediaPlayer.playAfterPrepare = true;
			}
		}

	}

	@Override
	public void play() {
		if (mPlaylist != null) {
			if (mCurrentMediaPlayer == null) {
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mCurrentMediaPlayer!=null&&!mCurrentMediaPlayer.preparing
					&& mPlayerEngineListener.onTrackStart(mCurrentMediaPlayer
							.getDuration(), getPlaylist().getSelectedTrack()) == false) {
				return;
			}

			if (mCurrentMediaPlayer != null
					&& mCurrentMediaPlayer.playlistEntry != mPlaylist
							.getSelectedTrack()) {
				Log.e("", "mCurrentMediaPlayer != null"
						+ "mCurrentMediaPlayer.playlistEntry != mPlaylist ="
						+ true);
				cleanUp();
				mCurrentMediaPlayer = build(mPlaylist.getSelectedTrack());
			}

			if (mCurrentMediaPlayer == null)
				return;
			if (!mCurrentMediaPlayer.preparing) {

				if (!mCurrentMediaPlayer.isPlaying()) {
					Log.e("preparing", "mCurrentMediaPlayer.isPlaying() "
							+ mCurrentMediaPlayer.isPlaying() + " preparung "
							+ mCurrentMediaPlayer.preparing);
					Log.e("preparing", "here surfaceholder param  "+surfaceHolder.getSurfaceFrame()
							);
					mHandler.removeCallbacks(mUpdateTimeTask);
					mHandler.postDelayed(mUpdateTimeTask, 1000);
					mCurrentMediaPlayer.start();
				}
			} else {

				mCurrentMediaPlayer.playAfterPrepare = true;
			}
		}

	}

	@Override
	public void prev() {
		if (mPlaylist != null) {
			mPlaylist.selectPrev();
			play();
		}
	}

	@Override
	public void skipTo(int index) {
		mPlaylist.select(index);
		play();
	}

	@Override
	public void stop() {
		cleanUp();

		if (mPlayerEngineListener != null) {
			mPlayerEngineListener.onTrackStop();
		}
	}

	/**
	 * Stops & destroys media player
	 */
	private void cleanUp() {
		// nice clean-up job
		if (mCurrentMediaPlayer != null) {
			try {
				mCurrentMediaPlayer.stop();
				Log.e("", "media stop");
			} catch (IllegalStateException e) {
				// this may happen sometimes
			} finally {
				mCurrentMediaPlayer.release();
				mCurrentMediaPlayer = null;
				Log.e("", "media release ");
			}

		}
	}

	private InternalMediaPlayer build(MusicObject musicObject) {
		if (mCurrentMediaPlayer == null) {
			mCurrentMediaPlayer = new InternalMediaPlayer();
		} else {
			mCurrentMediaPlayer.release();
		}
		try {
			try {
				Log.e("file music ", musicObject.getFilePath());
				// FileInputStream fileInputStream = new FileInputStream(
				// musicObject.getFilePath());
				if(surfaceHolder==null){
					return null;
				}
				Log.e("", "display surfaceholder "+ surfaceHolder.getSurfaceFrame());
				mCurrentMediaPlayer.setDisplay(surfaceHolder);
				mCurrentMediaPlayer.setWakeMode(context,
						PowerManager.PARTIAL_WAKE_LOCK);
				mCurrentMediaPlayer
						.setAudioStreamType(AudioManager.STREAM_MUSIC);
//				Uri uri = Uri.parse(musicObject.getFilePath());
//				Map<String, String> headers = new HashMap<String, String>();
//				headers.put("Content-Range", "");
//
//				// Use java reflection call the hide API:
//				Method method = mCurrentMediaPlayer.getClass().getMethod("setDataSource", new Class[] { Context.class, Uri.class, Map.class });
//				method.invoke(mCurrentMediaPlayer, new Object[] {this, uri, headers});
				String path=musicObject.getFilePath();
				mCurrentMediaPlayer.setDataSource(musicObject.getFilePath());
				// fileInputStream.close();

			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCurrentMediaPlayer.playlistEntry = musicObject;

			mCurrentMediaPlayer
					.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							mMPHandler.sendEmptyMessageDelayed(MSG_MP_RELEASE,
									500);
						}

					});

			mCurrentMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mCurrentMediaPlayer.preparing = false;
					// we may start playing
					if (mPlaylist.getSelectedTrack() == mCurrentMediaPlayer.playlistEntry
							&& mCurrentMediaPlayer.playAfterPrepare) {
						mCurrentMediaPlayer.playAfterPrepare = false;
						// mCurrentMediaPlayer.setDisplay(surfaceHolder);

						play();
						Log.e("onprepared", "true");
					}

				}

			});
			
			mCurrentMediaPlayer.setOnInfoListener(new OnInfoListener() {
				boolean is_slow_network;
				int count_slow_network;
				@Override
				public boolean onInfo(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
//					Log.e("", " what ="+ what);
//					if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
//						count_slow_network++;
//						Log.e("", " count_slow_network ="+ count_slow_network);
//						if(count_slow_network >=15){
//							Log.e("", " slow network  with count_slow_network="+ count_slow_network);
//							count_slow_network=0;
//							if (mPlayerEngineListener != null) {
//								mPlayerEngineListener.onTrackStreamSlow();
//							}
//						}
//					}
					return false;
				}
			});

			mCurrentMediaPlayer
					.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
						@Override
						public void onBufferingUpdate(MediaPlayer mp,
								int percent) {
							if (mPlayerEngineListener != null) {
								mPlayerEngineListener.onTrackBuffering(percent);
							}
						}

					});

			mCurrentMediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {

						if (mPlayerEngineListener != null) {
							mPlayerEngineListener.onTrackStreamError();
						}
						stop();
						return true;
					}

					if (what == -1) {
						long failTime = System.currentTimeMillis();
						if (failTime - mLastFailTime > FAIL_TIME_FRAME) {

							mTimesFailed = 1;
							mLastFailTime = failTime;
						} else {

							mTimesFailed++;
							if (mTimesFailed > ACCEPTABLE_FAIL_NUMBER) {
								if (mPlayerEngineListener != null) {
									mPlayerEngineListener.onTrackStreamError();
								}
								stop();
								return true;
							}
						}
					}
					return false;
				}
			});

			mCurrentMediaPlayer.preparing = true;
			// try {
			// mCurrentMediaPlayer.prepare();
			// } catch (IOException e) {
			// Log.e("ErrorTag", e.getMessage(), e);
			// e.printStackTrace();
			// }
			mCurrentMediaPlayer.prepareAsync();

			if (mPlayerEngineListener != null) {
				mPlayerEngineListener.onTrackChanged(mPlaylist
						.getSelectedTrack());
			}

			return mCurrentMediaPlayer;
		} catch (IllegalArgumentException e) {
			Log.e("ErrorTag", e.getMessage(), e);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e("ErrorTag", e.getMessage(), e);
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Playlist getPlaylist() {
		return mPlaylist;
	}

	@Override
	public boolean isPlaying() {

		if (mCurrentMediaPlayer == null)
			return false;

		if (mCurrentMediaPlayer.preparing)
			return false;

		return mCurrentMediaPlayer.isPlaying();
	}

	@Override
	public void setListener(PlayerEngineListener playerEngineListener) {
		mPlayerEngineListener = playerEngineListener;
	}

	@Override
	public void setPlaybackMode(PlaylistPlaybackMode aMode) {
		mPlaylist.setPlaylistPlaybackMode(aMode);
	}

	@Override
	public PlaylistPlaybackMode getPlaybackMode() {
		return mPlaylist.getPlaylistPlaybackMode();
	}

	public void forward(int time) {
		if (mCurrentMediaPlayer != null)
			mCurrentMediaPlayer.seekTo(mCurrentMediaPlayer.getCurrentPosition()
					+ time);

	}

	@Override
	public void rewind(int time) {
		mCurrentMediaPlayer.seekTo(mCurrentMediaPlayer.getCurrentPosition()
				- time);
	}

	@Override
	public void prevList() {
		if (prevPlaylist != null) {
			openPlaylist(prevPlaylist);
			play();
		}
	}

	SurfaceHolder surfaceHolder;

	public SurfaceHolder getSurfaceHolder() {
		return surfaceHolder;
	}

	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		Log.e("", "set surfaceholder "+ surfaceHolder.getSurfaceFrame());
		this.surfaceHolder = surfaceHolder;
	}

	@Override
	public int getVideoWidth() {
		// TODO Auto-generated method stub
		if (mCurrentMediaPlayer != null) {
			return mCurrentMediaPlayer.getVideoWidth();
		}
		return 0;
	}

	@Override
	public int getVideoHeight() {
		// TODO Auto-generated method stub
		if (mCurrentMediaPlayer != null) {
			return mCurrentMediaPlayer.getVideoHeight();
		}
		return 0;
	}

}
