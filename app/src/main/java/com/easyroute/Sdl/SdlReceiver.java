package com.easyroute.Sdl;

import android.content.Context;
import android.content.Intent;

import com.smartdevicelink.transport.SdlBroadcastReceiver;

/**
 * Created by hp on 20.07.2017.
 */


public class SdlReceiver extends SdlBroadcastReceiver {
    public SdlReceiver() {
    }


    @Override
    public Class<? extends SdlRouterService> defineLocalSdlRouterClass() {
        return com.easyroute.Sdl.SdlRouterService.class;
    }

    @Override
    public void onSdlEnabled(Context context, Intent intent) {
        intent.setClass(context,AppLinkService.class);
        context.startService(intent);
    }
}