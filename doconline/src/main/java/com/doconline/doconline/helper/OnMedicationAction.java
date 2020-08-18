package com.doconline.doconline.helper;

/**
 * Created by chiranjit on 27/04/17.
 */

public interface OnMedicationAction
{
    void onMedicationDeleted(int index, int item_id);
    void onMedicationUpdated(int index, int item_id);
}