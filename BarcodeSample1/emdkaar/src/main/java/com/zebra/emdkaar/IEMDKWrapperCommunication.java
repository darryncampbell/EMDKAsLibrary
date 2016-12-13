package com.zebra.emdkaar;

import java.util.List;

/**
 * Created by darryn on 13/12/2016.
 */

//  Class to represent the interface from the scanner wrapper to the main application.
//  Mostly used to update the UI, *would not expect such close coupling in a real world scenario*.
public interface IEMDKWrapperCommunication {

    void setStatus(String status);
    void setData(String dataString);
    void setDefaultSpinner(int spinnerNumber);
    void setSpinnerAdapter(List<String> friendlyNameList);
    void asyncUpdate(boolean b);
}
