package com.easyroute.listener;

import com.easyroute.constant.Event;

public interface EventListener {

    void onEventReceive(Event event, Object data);
}
