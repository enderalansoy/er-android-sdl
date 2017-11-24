package com.easyroute.Sdl;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.applink.security.AppLinkSecurityService;
import com.easyroute.R;
import com.easyroute.ui.activity.MainActivity;
import com.easyroute.ui.view.GoogleMapView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.RPCRequest;
import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.SdlProxyALM;
import com.smartdevicelink.proxy.TTSChunkFactory;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.interfaces.IProxyListenerALM;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.ListFiles;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SdlMsgVersion;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.TTSChunk;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.AppHMIType;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.Language;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SpeechCapabilities;
import com.smartdevicelink.proxy.rpc.enums.TouchType;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener;
import com.smartdevicelink.security.SdlSecurityBase;
import com.smartdevicelink.transport.BaseTransportConfig;
import com.smartdevicelink.transport.USBTransport;
import com.smartdevicelink.transport.USBTransportConfig;
import com.smartdevicelink.util.CorrelationIdGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by hp on 18.07.2017.
 */
public class AppLinkService extends Service implements IProxyListenerALM {

    private static SdlProxyALM _SdlProxy;
    OutputStream sdlOutStream;
    List<String> remoteFiles;
    private static final String ICON_FILENAME = "easy_route.png";

    public static OutputStream pcmAudioStream = null;
    public static VirtualDisplayEncoder vdEncoder;
    public static float firstX, firstY, lastX, lastY, changeX, changeY;
    int iconCorrelationId=0;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startProxy();
        return START_STICKY;
    }

    public static SdlProxyALM getSdlProxy() {
        return 	_SdlProxy;
    }



    public void startProxy() {
        if (_SdlProxy == null) {
            try {

                SharedPreferences settings = getSharedPreferences(Const.PREFS_NAME, 0);
                boolean isMediaApp = settings.getBoolean(Const.PREFS_KEY_ISMEDIAAPP, Const.PREFS_DEFAULT_ISMEDIAAPP);
                String appName = settings.getString(Const.PREFS_KEY_APPNAME, Const.PREFS_DEFAULT_APPNAME);
                String appSynonym1 = settings.getString(Const.PREFS_KEY_APPSYNONYM1, Const.PREFS_DEFAULT_APPSYNONYM1);
                String appSynonym2 = settings.getString(Const.PREFS_KEY_APPSYNONYM2, Const.PREFS_DEFAULT_APPSYNONYM2);
                String appTTSTextName = settings.getString(Const.PREFS_KEY_APP_TTS_TEXT, Const.PREFS_DEFAULT_APP_TTS_TEXT);
                String sNgnAppName = settings.getString(Const.PREFS_KEY_NGN_NAME, Const.PREFS_DEFAULT_NGN_NAME);
                String appTTSType = settings.getString(Const.PREFS_KEY_APP_TTS_TYPE, Const.PREFS_DEFAULT_APP_TTS_TYPE);
                SpeechCapabilities appTTSTextType = SpeechCapabilities.valueForString(appTTSType);
                Vector<TTSChunk> chunks = new Vector<TTSChunk>();
                TTSChunk ttsChunks = TTSChunkFactory.createChunk(appTTSTextType, appTTSTextName);
                chunks.add(ttsChunks);
                String appId = settings.getString(Const.PREFS_KEY_APPID, Const.PREFS_DEFAULT_APPID);
                Language lang = Language.valueOf(settings.getString(Const.PREFS_KEY_LANG, Const.PREFS_DEFAULT_LANG));
                Language hmiLang = Language.valueOf(settings.getString(Const.PREFS_KEY_HMILANG, Const.PREFS_DEFAULT_HMILANG));
                AppHMIType appHMIType = AppHMIType.valueOf(settings.getString(Const.PREFS_KEY_APP_HMI_TYPE, Const.PREFS_DEFAULT_APP_HMI_TYPE));
                String sAppResumeHash = settings.getString(Const.PREFS_KEY_APP_RESUME_HASH, null);
                Vector<AppHMIType> vrAppHMITypes = new Vector<AppHMIType>();
                vrAppHMITypes.add(appHMIType);
                Vector<String> vrSynonyms = new Vector<String>();
                vrSynonyms.add(appSynonym1);
                vrSynonyms.add(appSynonym2);
                SdlMsgVersion SdlMsgVersion = new SdlMsgVersion();
                SdlMsgVersion.setMajorVersion(2);
                SdlMsgVersion.setMinorVersion(2);

                BaseTransportConfig myTransport = null;

                UsbAccessory acc = null;

                UsbManager usbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
                UsbAccessory[] accessories = usbManager.getAccessoryList();
                if (accessories != null) {
                    for (UsbAccessory accessory : accessories) {
                        if (USBTransport.isAccessorySupported(accessory)) {
                            acc = accessory;
                            break;
                        }
                    }
                }

                myTransport = new USBTransportConfig(getApplicationContext(), acc, false, false);
//				myTransport = new TCPTransportConfig(12345, "127.0.0.1", true);

                List<Class<? extends SdlSecurityBase>> securityList = new ArrayList<Class<? extends SdlSecurityBase>>();
                securityList.add(AppLinkSecurityService.class);

                _SdlProxy = new SdlProxyALM((IProxyListenerALM)this,
						/*Sdl proxy configuration resources*/null,
						/*enable advanced lifecycle management true,*/
                        appName,
                        chunks,
						/*ngn media app*/sNgnAppName,
						/*vr synonyms*/null,
						/*is media app*/isMediaApp,
						/*SdlMsgVersion*/SdlMsgVersion,
						/*language desired*/lang,
						/*HMI Display Language Desired*/hmiLang,
						/*AppHMIType*/ vrAppHMITypes,
						/*App ID*/appId,
						/*autoActivateID*/null,
						/*callbackToUIThre1ad*/ false,
						/*preRegister*/ false,
						/*app resuming*/ sAppResumeHash,
                        myTransport);
                _SdlProxy.setSdlSecurityClassList(securityList);


            } catch (SdlException e) {
                e.printStackTrace();
                //error creating proxy, returned proxy = null
                if (_SdlProxy == null){

                    stopSelf();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (_SdlProxy != null) {
            try {
                _SdlProxy.dispose();
            } catch (SdlException e) {
                e.printStackTrace();
            }
            _SdlProxy = null;
        }
        super.onDestroy();
    }

    public static SdlProxyALM getProxyInstance() {
        return _SdlProxy;
    }

    @Override
    public void onOnHMIStatus(OnHMIStatus notification) {


        switch(notification.getHmiLevel()) {
            case HMI_FULL:
                if (notification.getFirstRun()) {
                    try {
                        _SdlProxy.subscribevehicledata(
								/*gps*/true,
								/*speed*/false,
								/*rpm*/false,
								/*fuelLevel*/false,
								/*fuelLevel_State*/false,
								/*instantFuelConsumption*/false,
								/*externalTemperature*/false,
								/*prndl*/false,
								/*tirePressure*/false,
								/*odometer*/false,
								/*beltStatus*/false,
								/*bodyInformation*/false,
								/*deviceStatus*/false,
								/*driverBraking*/false,
								/*correlationID*/1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                vdEncoder = new VirtualDisplayEncoder();
                sdlOutStream = _SdlProxy.startH264(true);
                if(sdlOutStream != null) {
                    try {
                        vdEncoder.init(getApplicationContext(), sdlOutStream, MyPresentation.class, _SdlProxy.getDisplayCapabilities().getScreenParams());
                        vdEncoder.setStreamingParams(240, 720, 480, 30, 512000, 100);
                        vdEncoder.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case HMI_LIMITED:
                vdEncoder = new VirtualDisplayEncoder();
                sdlOutStream = _SdlProxy.startH264(true);
                if(sdlOutStream != null) {
                    try {
                        vdEncoder.init(getApplicationContext(), sdlOutStream, MyPresentation.class, _SdlProxy.getDisplayCapabilities().getScreenParams());
                        vdEncoder.setStreamingParams(240, 720, 480, 30, 512000, 100);
                        vdEncoder.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            case HMI_BACKGROUND:
                try {
                    uploadImages();
                    vdEncoder.shutDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case HMI_NONE:
                try {
                    uploadImages();
                    vdEncoder.shutDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                uploadImages();
                return;
        }
    }
    private void sendIcon() throws SdlException {
        iconCorrelationId = CorrelationIdGenerator.generateId();
        uploadImage(R.mipmap.ic_launcher, ICON_FILENAME, iconCorrelationId, true);
    }
    private void uploadImages(){
        ListFiles listFiles = new ListFiles();
        listFiles.setOnRPCResponseListener(new OnRPCResponseListener() {
            @Override
            public void onResponse(int correlationId, RPCResponse response) {
                if(response.getSuccess()){
                    remoteFiles = ((ListFilesResponse) response).getFilenames();
                }

                // Check the mutable set for the AppIcon
                // If not present, upload the image
                if(remoteFiles== null || !remoteFiles.contains(AppLinkService.ICON_FILENAME)){
                    try {
                        sendIcon();
                    } catch (SdlException e) {
                        e.printStackTrace();
                    }
                }else{
                    // If the file is already present, send the SetAppIcon request
                    try {
                        _SdlProxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
                    } catch (SdlException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        this.sendRpcRequest(listFiles);

    }

    private void uploadImage(int resource, String imageName,int correlationId, boolean isPersistent) {
        PutFile putFile = new PutFile();
        putFile.setFileType(FileType.GRAPHIC_PNG);
        putFile.setSdlFileName(imageName);
        putFile.setCorrelationID(correlationId);
        putFile.setPersistentFile(isPersistent);
        putFile.setSystemFile(false);
        putFile.setBulkData(contentsOfResource(resource));

        try {
            _SdlProxy.sendRPCRequest(putFile);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }
    private void sendRpcRequest(RPCRequest request){
        request.setCorrelationID(CorrelationIdGenerator.generateId());
        try {
            _SdlProxy.sendRPCRequest(request);
        } catch (SdlException e) {
            e.printStackTrace();
        }
    }

    private byte[] contentsOfResource(int resource) {
        InputStream is = null;
        try {
            is = getResources().openRawResource(resource);
            ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
            final int buffersize = 4096;
            final byte[] buffer = new byte[buffersize];
            int available = 0;
            while ((available = is.read(buffer)) >= 0) {
                os.write(buffer, 0, available);
            }
            return os.toByteArray();
        } catch (IOException e) {
            Log.w("SDL Service", "Can't read icon file", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }






    @Override
    public void onProxyClosed(String info, Exception e, SdlDisconnectedReason reason) {

    }

    @Override
    public void onError(String info, Exception e) {}

    @Override
    public void onGenericResponse(GenericResponse response) {}

    @Override
    public void onOnCommand(OnCommand notification) {}

    @Override
    public void onAddCommandResponse(AddCommandResponse response) {}

    @Override
    public void onAddSubMenuResponse(AddSubMenuResponse response) {}

    @Override
    public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response) {}

    @Override
    public void onAlertResponse(AlertResponse response) {}

    @Override
    public void onDeleteCommandResponse(DeleteCommandResponse response) {}

    @Override
    public void onDeleteInteractionChoiceSetResponse(DeleteInteractionChoiceSetResponse response) {}

    @Override
    public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {}

    @Override
    public void onPerformInteractionResponse(PerformInteractionResponse response) {}

    @Override
    public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse response) {}

    @Override
    public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response) {}

    @Override
    public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {}

    @Override
    public void onShowResponse(ShowResponse response) {}

    @Override
    public void onSpeakResponse(SpeakResponse response) {}

    @Override
    public void onOnButtonEvent(OnButtonEvent notification) {}

    @Override
    public void onOnButtonPress(OnButtonPress notification) {}

    @Override
    public void onSubscribeButtonResponse(SubscribeButtonResponse response) {}

    @Override
    public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {}

    @Override
    public void onOnPermissionsChange(OnPermissionsChange notification) {}

    @Override
    public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse response) {}

    @Override
    public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse response) {}

    @Override
    public void onGetVehicleDataResponse(GetVehicleDataResponse response) {}

    @Override
    public void onReadDIDResponse(ReadDIDResponse response) {}

    @Override
    public void onGetDTCsResponse(GetDTCsResponse response) {}

    @Override
    public void onOnVehicleData(OnVehicleData notification) {}

    @Override
    public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response) {}

    @Override
    public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {}

    @Override
    public void onOnAudioPassThru(OnAudioPassThru notification) {}

    @Override
    public void onPutFileResponse(PutFileResponse response) {
        Log.i("", "onPutFileResponse from SDL");
        if(response.getCorrelationID() == iconCorrelationId){ //If we have successfully uploaded our icon, we want to set it
            try {
                _SdlProxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeleteFileResponse(DeleteFileResponse response) {}

    @Override
    public void onListFilesResponse(ListFilesResponse response) {
        if(response.getSuccess()){
            remoteFiles = response.getFilenames();
        }

        // Check the mutable set for the AppIcon
        // If not present, upload the image
        if(remoteFiles== null || !remoteFiles.contains(AppLinkService.ICON_FILENAME)){
            try {
                sendIcon();
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }else{
            // If the file is already present, send the SetAppIcon request
            try {
                _SdlProxy.setappicon(ICON_FILENAME, CorrelationIdGenerator.generateId());
            } catch (SdlException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSetAppIconResponse(SetAppIconResponse response) {}

    @Override
    public void onScrollableMessageResponse(ScrollableMessageResponse response) {}

    @Override
    public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {}

    @Override
    public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {}

    @Override
    public void onOnLanguageChange(OnLanguageChange notification) {}

    @Override
    public void onSliderResponse(SliderResponse response) {}

    @Override
    public void onOnDriverDistraction(OnDriverDistraction notification) {}

    @Override
    public void onOnTBTClientState(OnTBTClientState notification) {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onOnTouchEvent(OnTouchEvent notification) {

       // MainActivity.onTouchEvent(notification);


        float X = notification.getEvent().get(0).getTouchCoordinates().get(0).getX();
        float Y = notification.getEvent().get(0).getTouchCoordinates().get(0).getY();


        if (notification.getType() == TouchType.BEGIN) {
            firstX =  X;
            firstY =  Y;

        } else if (notification.getType() == TouchType.MOVE) {
            changeX = X;
            changeY = Y;

        } else if (notification.getType() == TouchType.END) {
            lastX =  X;
            lastY =  Y;

          /*  LatLng latLng = new LatLng((lastX-firstX)/10000,(lastY-firstY)/10000);
            MainActivity.mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,50));*/
        }
        if (X < 650)
            MyPresentation.touchEvent();
        else
            vdEncoder.handleTouchEvent(notification);
      //  MainActivity.mapView.scrollBy((int)(lastX-firstX)/100,(int)(lastY-firstY)/100);

    /*    LatLng latLng = new LatLng((lastX-firstX)/10000,(lastY-firstY)/10000);
        MainActivity.mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,20));*/

    }

    @Override
    public void onOnKeyboardInput(OnKeyboardInput notification) {}

    @Override
    public void onOnHashChange(OnHashChange notification) {}

    @Override
    public void onOnSystemRequest(OnSystemRequest notification) {}

    @Override
    public void onSystemRequestResponse(SystemRequestResponse response) {}

    @Override
    public void onDiagnosticMessageResponse(DiagnosticMessageResponse response) {}

    @Override
    public void onOnStreamRPC(OnStreamRPC notification) {}

    @Override
    public void onStreamRPCResponse(StreamRPCResponse response) {}

    @Override
    public void onOnLockScreenNotification(OnLockScreenStatus notification) {}

    @Override
    public void onAlertManeuverResponse(AlertManeuverResponse arg0) {}

    @Override
    public void onDialNumberResponse(DialNumberResponse arg0) {}

    @Override
    public void onSendLocationResponse(SendLocationResponse arg0) {}

    @Override
    public void onServiceDataACK(int arg0) {}

    public void endService(final OnServiceEnded serviceEnded) {
        if (serviceEnded.getSessionType().getName() == "PCM") {
            if (pcmAudioStream != null) {
                try {
                    pcmAudioStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pcmAudioStream = null;

            }
        }
    }



    @Override
    public void onServiceEnded(OnServiceEnded arg0) {
        endService(arg0);
    }

    public void serviceNacked(final OnServiceNACKed serviceNACKed) {
        if (serviceNACKed.getSessionType().getName() == "PCM") {
            if (pcmAudioStream != null) {
                try {
                    pcmAudioStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                pcmAudioStream = null;
            }
        }
    }

    @Override
    public void onServiceNACKed(OnServiceNACKed arg0) {
        serviceNacked(arg0);
    }

    @Override
    public void onShowConstantTbtResponse(ShowConstantTbtResponse arg0) {}

    @Override
    public void onUpdateTurnListResponse(UpdateTurnListResponse arg0) {}

    @Override
    public void onGetWayPointsResponse(GetWayPointsResponse arg0) {}

    @Override
    public void onOnWayPointChange(OnWayPointChange arg0) {}

    @Override
    public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse arg0) {}

    @Override
    public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse arg0) {}

}
