package com.easyroute.Sdl;

import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;

/**
 * Created by hp on 7.07.2017.
 */

public class Const {
    // Shared preference name for protocol properties
    public static final String PREFS_NAME = "cameraglpreviewtomp4prefs";

    // Key to pass a FileChooser filename via IntentHelper
    public static final String INTENTHELPER_KEY_FILECHOOSER_FILE = "IntentFileChooserFile";

    // Request id for Audio File FileChooserActivity
    public static final int REQUEST_AUDIO_FILE_CHOOSER = 11;

    // Protocol properties
    public static final String PREFS_KEY_ISMEDIAAPP = "isMediaApp";
    public static final String PREFS_KEY_APPNAME = "appName";

    public static final String PREFS_KEY_APPSYNONYM1 = "appSynonym1";
    public static final String PREFS_KEY_APPSYNONYM2 = "appSynonym2";

    public static final String PREFS_KEY_APP_TTS_TEXT = "appTTSName";
    public static final String PREFS_KEY_APP_TTS_TYPE = "appTTSType";

    public static final String PREFS_KEY_APPID = "appId";
    public static final String PREFS_KEY_NGN_NAME = "ngnName";

    public static final String PREFS_KEY_LANG = "desiredLang";
    public static final String PREFS_KEY_HMILANG = "desiredHMILang";
    public static final String PREFS_KEY_APP_HMI_TYPE = "desiredHMIType";

    public static final String PREFS_KEY_APP_RESUME_HASH = "appResumeHash";

    public static final String PREFS_KEY_FPS = "fps";
    public static final String PREFS_KEY_WIDTH = "width";
    public static final String PREFS_KEY_HEIGHT = "height";
    public static final String PREFS_KEY_BITRATE = "bitrate";
    public static final String PREFS_KEY_ENCRYPTED = "isEncrypted";

    // Default values
    public static final boolean PREFS_DEFAULT_ISMEDIAAPP = false;
    public static final String PREFS_DEFAULT_APPNAME = "Easy Route App";

    public static final String PREFS_DEFAULT_APPSYNONYM1 = "Synonym1";
    public static final String PREFS_DEFAULT_APPSYNONYM2 = "Synonym2";

    public static final String PREFS_DEFAULT_APP_TTS_TEXT = "Camera To GL Preview To MP4";
    public static final String PREFS_DEFAULT_APP_TTS_TYPE = SpeechCapabilities.TEXT.name();

    public static final String PREFS_DEFAULT_APPID = "3675868974";
    public static final String PREFS_DEFAULT_NGN_NAME = "Easy Route";

    public static final String PREFS_DEFAULT_LANG = Language.EN_US.name();
    public static final String PREFS_DEFAULT_APP_HMI_TYPE = "NAVIGATION";
    public static final String PREFS_DEFAULT_HMILANG = Language.EN_US.name();

    public static final int PREFS_DEFAULT_FPS = 30;
    public static final int PREFS_DEFAULT_WIDTH = 800;
    public static final int PREFS_DEFAULT_HEIGHT = 480;
    public static final int PREFS_DEFAULT_BITRATE = 500000;
    public static final boolean PREFS_DEFAULT_ENCRYPTED = true;
}

