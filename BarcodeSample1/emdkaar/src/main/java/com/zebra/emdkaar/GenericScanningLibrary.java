package com.zebra.emdkaar;

/**
 * Created by darry on 16/12/2016.
 */
/**
 * REPLACE THIS WITH YOUR OWN COMMON SCANNING INTERFACE, THIS IS JUST AN EXAMPLE.  THIS INTERFACE IS VERY
 * TIGHTLY COUPLED WITH THE SAMPLE UI
 */
public abstract class GenericScanningLibrary {

    abstract public void selectedDeviceChanged(int position);
    abstract public void selectedTriggerChanged(int position);
    abstract public void startScan(boolean isContinuous);
    abstract public void setContinuousMode(boolean b);
    abstract public void onPause();
    abstract public void onDestroy();
    abstract public void onResume();
    abstract public void stopScan();
    abstract public void setEAN8Decoder(boolean checked);
    abstract public void setEAN13Decoder(boolean checked);
    abstract public void setCode39Decoder(boolean checked);
    abstract public void setCode128Decoder(boolean checked);
    abstract public void setDecoders();
}
