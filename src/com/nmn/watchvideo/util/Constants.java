package com.nmn.watchvideo.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

public class Constants {
	
	public static final int APP_UPDATE_NOTIFICATION_ID = 150;
	
	public static final String EVENT = "UI-action";
	public static final String INSTALL_EVENT = "Install";
	public static final String EVENT_ACTION = "button-click";
	
	
	
	public static final String PREF_USER_LOGIN_TOKEN = "pref_user_token";
	public static final String USER_TOKEN_KEY = "user_token_key";
	public static final String PREF_ACCESS_TOKEN_TEMP = "pref_access_token_temp";
	public static final String ACCESS_TOKEN_TEMP = "access_token_temp";
	public static final String REFRESH_TOKEN_TEMP = "refresh_token_temp";
	public static final String EXPIRE_TEMP = "expire_temp";
	public static final String PREF_WISHLIST = "pref_dll";
	public static final String DLL = "dll";
	
	public static final String PREF_DOWNLOADING = "pref_downloading";
	public static final String PREF_DOWNLOAD_QUEUE = "pref_download_queue";
	public static final String KEY_DOWNLOADING = "key_downloading";
	public static final String KEY_DOWNLOAD_QUEUE = "key_download_queue";
	public static final String PREF_APP_ID = "pref_app_id";
	public static final String KEY_APP_ID = "_app_id";
	public static final String SETTING_PREF = "__settings_pref";
	public static final String KEY_AUTO_UPDATE = "__settings_auto_update";
	public static final String KEY_AUTO_INSTALL = "__settings_auto_install";
	public static final String KEY_ALREADY_UPDATING = "__already_update";
	public static final String KEY_ALLOW_PUSH = "__allow_push";
	public static final String PREF_DEALER_KEY = "_pref_dealer";
	public static final String DEALER_KEY = "__dealer_key";
	
	public static final String SENDER_ID = "787730237419"; 
	 
    /**
     * Tag used on log messages.
     */
	public static final String TAG = "APPOTA";
 
	public static final String EXTRA_MESSAGE = "message";
    
    //public static final String deviceID = 
	public static final String SEARCH_QUERY = "searhc_query";
	public static final String MOVIE_URL = "movie_url";
	public static final String MOVIE_TITLE = "movie_title";
	public static final String MOVIE_EPISODE = "movie_episode";
	public static final String MOVIE_PATH = "movie_path";
	public static final String GREEN_TYM = "green_tym";
	public static final String USER_NAME = "user_name";
	public static final String FB_POST_MESSAGE = "fb_post_msg";
	public static final String FB_FRIEND_TAGS = "fb_friends_tags";
	public static final String FB_USER_IDS = "fb_ids";
	public static final String FB_USER_NAMES = "fb_names";
	public static final String SHARE_PICTURE = "share_pic";
	public static final String SHARE_NAME = "share_name";
	public static final String SHARE_LINK = "share_link";
	public static final String SHARE_MESSAGE = "share_message";
	public static final String IMAGES = "screen_shots";
	public static final String IMAGE_POSITION = "img_pos";
	public static final String DOWNLOAD_ID = "download_id";
	public static final String BUY_TYM = "Buy TYM";
	public static final String BUY_TYM_SUCCESS = "Buy TYM success";
	public static final String BANK_PAYMENT_DATA = "bank_data";
	public static final String APPSTOREVN = "AppStoreVn";
	public static final int API = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
	public static final String INSTALL_SUCCESS = "InstallSuccess";
	public static final String IS_MOVIE_PURCHASED = "is_purchased";
	public static final String MESSAGE = "msg";
	public static final String TUTOR_POS = "tutor_pos";
	public static final String IS_SHOW_START_BANNER = "show_banner";
	public static final String COLLECTIONS = "collections";
	public static final int LOGIN_GOOGLE_REQUEST_CODE = 98;
	public static final String KEY_EP = "veriosn_name";
	public static final int SPIN_REQUEST_CODE = 99;
	public static final String START_DOWNLOAD_ACTION = "StartDownload";
	public static final String DOWNLOAD_ALL_COLLECTION = "DownloadAllAtCollection";
	public static final String NEWSFEED_DOWNLOAD = "DownloadAtNewsFeed";
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static String DONT_SHOW_CONFIRM_SPIN_AGAIN ="_dont_show_confirm_spin";
}
