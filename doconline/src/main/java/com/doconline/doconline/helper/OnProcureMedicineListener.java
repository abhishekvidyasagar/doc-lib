package com.doconline.doconline.helper;

import com.doconline.doconline.model.Address;
import com.doconline.doconline.model.Medicine;

import java.util.ArrayList;

/**
 * Created by cbug on 26/9/17.
 */

public interface OnProcureMedicineListener extends OnPageSelection
{
    void onMedicineFound(ArrayList<Medicine> medicines);
    void onDeliveryAddress(Address mAddress);
    void onOrderId(String mOrderId);
}