package com.doconline.doconline.helper;

/**
 * Created by chiranjit on 14/07/17.
 */

public interface OnDialogAction
{
    void onPositiveAction();
    void onNegativeAction();
    void onPositiveAction(int requestCode);
    void onPositiveAction(int requestCode, String data);
    void onNegativeAction(int requestCode);
}