package com.doconline.doconline.helper;

import com.doconline.doconline.model.DiagnosticsUserAddress;

/**
 * Created by admin on 2018-03-19.
 */

public interface OnChangeDiagnosticsAddressListener extends OnPageSelection {

    void OnAddDiagnosticsAddress(DiagnosticsUserAddress address);
    void OnEditDiagnosticsAddress(int position);
    void OnDeleteDiagnosticsAddress(int position);
    void OnSetDiagnosticsAddressAsDefault(int position);
}
