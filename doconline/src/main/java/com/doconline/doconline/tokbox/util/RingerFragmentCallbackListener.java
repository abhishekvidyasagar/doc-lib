package com.doconline.doconline.tokbox.util;


/**
 * Provides callbacks for call response events - accepted, rejected or ignore.
 */
public interface RingerFragmentCallbackListener {
    /**
     * Callback which gets called when the call is ignored.
     */
    void onCallIgnored();

    /**
     * Callback which gets called when the call is answered/accepted.
     */
    void onCallAccepted();

    /**
     * Callback which gets called when the call is rejected.
     */
    void onCallRejected();

    /**
     * Callback which gets called when the phone is ringing.
     */
    void onRinging();

    /**
     * Callback which gets called when call get disconnected
     */
    void onCallDisconnect();
}
