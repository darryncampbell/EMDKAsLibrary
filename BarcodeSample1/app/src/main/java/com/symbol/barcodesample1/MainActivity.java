/*
* Copyright (C) 2015-2016 Symbol Technologies LLC
* All rights reserved.
*/
package com.symbol.barcodesample1;

import java.util.List;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import com.zebra.emdkaar.EMDKWrapper;
import com.zebra.emdkaar.GenericScanningLibrary;
import com.zebra.emdkaar.IEMDKWrapperCommunication;


public class MainActivity extends Activity implements OnCheckedChangeListener, IEMDKWrapperCommunication {

    private TextView textViewData = null;
    private TextView textViewStatus = null;
    private CheckBox checkBoxEAN8 = null;
    private CheckBox checkBoxEAN13 = null;
    private CheckBox checkBoxCode39 = null;
    private CheckBox checkBoxCode128 = null;
    private CheckBox checkBoxContinuous = null;
    private Spinner spinnerScannerDevices = null;
    private Spinner spinnerTriggers = null;
    private int dataLength = 0;
    private String [] triggerStrings = {"HARD", "SOFT"};
    private GenericScanningLibrary scannerObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  Create an instance of the abstracted scanner object exposed from the aar
        //  per http://techdocs.zebra.com/emdk-for-android/6-0/guide/programming_practices/#emdkconcurrencyguidelines
        if(android.os.Build.MANUFACTURER.contains("Zebra Technologies") ||
                android.os.Build.MANUFACTURER.contains("Motorola Solutions") ) {
            scannerObj = new EMDKWrapper(this, getApplicationContext());
        }
        else
        {
            //  Instantiate an alternative scanning library for non-Zebra devices
            Toast.makeText(getApplicationContext(), "Scanner Logic unsupported on this device", Toast.LENGTH_LONG).show();
            setStatus("Scanner Logic unsupported on this device");
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setDefaultOrientation();

        textViewData = (TextView)findViewById(R.id.textViewData);
        textViewStatus = (TextView)findViewById(R.id.textViewStatus);
        checkBoxEAN8 = (CheckBox)findViewById(R.id.checkBoxEAN8);
        checkBoxEAN13 = (CheckBox)findViewById(R.id.checkBoxEAN13);
        checkBoxCode39 = (CheckBox)findViewById(R.id.checkBoxCode39);
        checkBoxCode128 = (CheckBox)findViewById(R.id.checkBoxCode128);
        checkBoxContinuous = (CheckBox)findViewById(R.id.checkBoxContinuous);
        spinnerScannerDevices = (Spinner)findViewById(R.id.spinnerScannerDevices);
        spinnerTriggers = (Spinner)findViewById(R.id.spinnerTriggers);

        checkBoxEAN8.setOnCheckedChangeListener(this);
        checkBoxEAN13.setOnCheckedChangeListener(this);
        checkBoxCode39.setOnCheckedChangeListener(this);
        checkBoxCode128.setOnCheckedChangeListener(this);

        addSpinnerScannerDevicesListener();
        populateTriggers();
        addSpinnerTriggersListener();
        addStartScanButtonListener();
        addStopScanButtonListener();
        addCheckBoxListener();

        textViewData.setSelected(true);
        textViewData.setMovementMethod(new ScrollingMovementMethod());
    }


    private void setDefaultOrientation(){

        WindowManager windowManager =  (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = 0;
        int height = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                width = dm.widthPixels;
                height = dm.heightPixels;
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
                width = dm.heightPixels;
                height = dm.widthPixels;
                break;
            default:
                break;
        }

        if(width > height){
            setContentView(R.layout.activity_main_landscape);
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scannerObj != null)
            scannerObj.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The application is in background
        if (scannerObj != null)
            scannerObj.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The application is in foreground
        if (scannerObj != null)
            scannerObj.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addSpinnerScannerDevicesListener() {

        spinnerScannerDevices.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int position, long arg3) {

                if (scannerObj != null)
                    scannerObj.selectedDeviceChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
    }

    private void addSpinnerTriggersListener() {

        spinnerTriggers.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                if (scannerObj != null)
                    scannerObj.selectedTriggerChanged(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void addStartScanButtonListener() {

        Button btnStartScan = (Button)findViewById(R.id.buttonStartScan);

        btnStartScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (scannerObj != null)
                    scannerObj.startScan(checkBoxContinuous.isChecked());
            }
        });
    }

    private void addStopScanButtonListener() {

        Button btnStopScan = (Button)findViewById(R.id.buttonStopScan);

        btnStopScan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (scannerObj != null)
                    scannerObj.stopScan();
            }
        });
    }

    private void addCheckBoxListener() {

        checkBoxContinuous.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (scannerObj != null)
                        scannerObj.setContinuousMode(true);
                }
                else {
                    if (scannerObj != null)
                        scannerObj.setContinuousMode(false);
                }
            }
        });

    }

    private void populateTriggers() {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, triggerStrings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTriggers.setAdapter(spinnerAdapter);
        spinnerTriggers.setSelection(0/*triggerIndex*/);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (scannerObj == null)
            return;
        scannerObj.setEAN8Decoder(checkBoxEAN8.isChecked());
        scannerObj.setEAN13Decoder(checkBoxEAN13.isChecked());
        scannerObj.setCode39Decoder(checkBoxCode39.isChecked());
        scannerObj.setCode128Decoder(checkBoxCode128.isChecked());
        scannerObj.setDecoders();
    }

    @Override
    public void setStatus(String status) {
        //  Received a status update from the scanner aar
        new AsyncStatusUpdate().execute(status);
    }

    @Override
    public void setData(String dataString) {
        //  Received a data update from the scanner aar
        new AsyncDataUpdate().execute(dataString);
    }

    @Override
    public void setDefaultSpinner(int spinnerNumber) {
        spinnerScannerDevices.setSelection(spinnerNumber);
    }

    @Override
    public void setSpinnerAdapter(List<String> friendlyNameList) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, friendlyNameList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerScannerDevices.setAdapter(spinnerAdapter);
    }

    @Override
    public void asyncUpdate(boolean b) {
        new AsyncUiControlUpdate().execute(b);
    }

    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        protected void onPostExecute(String result) {

            if (result != null) {
                if(dataLength ++ > 100) { //Clear the cache after 100 scans
                    textViewData.setText("");
                    dataLength = 0;
                }

                textViewData.append(result+"\n");


                ((View) findViewById(R.id.scrollView1)).post(new Runnable()
                {
                    public void run()
                    {
                        ((ScrollView) findViewById(R.id.scrollView1)).fullScroll(View.FOCUS_DOWN);
                    }
                });

            }
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {

            textViewStatus.setText("Status: " + result);
        }
    }

    private class AsyncUiControlUpdate extends AsyncTask<Boolean, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean bEnable) {

            //  Decoder logic is not implemented in this sample
            checkBoxEAN8.setEnabled(bEnable);
            checkBoxEAN13.setEnabled(bEnable);
            checkBoxCode39.setEnabled(bEnable);
            checkBoxCode128.setEnabled(bEnable);
            spinnerScannerDevices.setEnabled(bEnable);
            spinnerTriggers.setEnabled(bEnable);
        }

        @Override
        protected Boolean doInBackground(Boolean... arg0) {

            return arg0[0];
        }
    }

}

