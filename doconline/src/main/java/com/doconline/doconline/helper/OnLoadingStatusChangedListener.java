package com.doconline.doconline.helper;

/**
 * Created by admin on 2018-04-04.
 */

public interface OnLoadingStatusChangedListener {

    void showLoadingActivity();
    void hideProgressbarWithSuccess();
    void hideProgressbarWithFailure();
}
