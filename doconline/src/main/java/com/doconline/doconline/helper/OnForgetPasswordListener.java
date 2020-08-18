package com.doconline.doconline.helper;

/**
 * Created by chiranjit on 07/12/16.
 */

public interface OnForgetPasswordListener
{
    void onMobileNumber(String mobile_no);
    void onEmail(String email);
    void onMRNNo(String mrn_no);
}