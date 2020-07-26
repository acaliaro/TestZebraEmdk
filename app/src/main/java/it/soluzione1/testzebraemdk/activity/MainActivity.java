package it.soluzione1.testzebraemdk.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import it.soluzione1.testzebraemdk.MyCustomApplication;
import it.soluzione1.testzebraemdk.R;
import it.soluzione1.testzebraemdk.utility.Sdk;
import it.soluzione1.testzebraemdk.utility.Utility;
import it.soluzione1.testzebraemdk.utility.ZebrEMDKApiWrapperCallbacks;
import it.soluzione1.testzebraemdk.utility.ZebraEMDKApiWrapper;

public class MainActivity extends AppCompatActivity implements ZebrEMDKApiWrapperCallbacks {
    private static final Logger _logger = LoggerFactory.getLogger(MainActivity.class);
    private static final String TAG = MainActivity.class.getName();

    Sdk _sdk;
    Context _context;
    TextView _textViewStatus;
    TextView _textViewResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final int[] selectedProfile = {-1};

        _context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerProfiles = findViewById(R.id.spinner_profiles);
        Button buttonExecute = findViewById(R.id.button_execute);
        _textViewStatus = findViewById(R.id.text_view_status);
        _textViewResponse = findViewById(R.id.text_view_response);
        _textViewResponse.setMovementMethod(new ScrollingMovementMethod()); // For scrolling


        String[] profiles = null;
        try {
            profiles = Utility.readProfiles(MyCustomApplication.getAppContext());
        } catch (Exception ex) {

            Utility.logException(TAG, ex, _logger, "onCreate");
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, profiles);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerProfiles.setAdapter(arrayAdapter);

        spinnerProfiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedProfile[0] = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedProfile[0] = -1;
            }
        });

        String[] finalProfiles = profiles;
        buttonExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (_sdk == null || !_sdk.isSdkReady()) {
                    Toast.makeText(_context, getString(R.string.sdk_not_initialized), Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedProfile[0] == -1) {
                    Toast.makeText(_context, getString(R.string.profile_not_selected), Toast.LENGTH_LONG).show();
                    return;
                }

                _sdk.executeCommand(finalProfiles[selectedProfile[0]]);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        _logger.debug("onResume");

        // Inizializzo l'sdk
        if (_sdk == null) {
            if (Utility.isZebraDevice()) {
                _logger.debug("initSdk initialized ZebraEMDKApiWrapper");
                _sdk = new ZebraEMDKApiWrapper(this);
                _sdk.init();
            }
        } else
            _logger.debug("initSdk ZebraEMDKApiWrapper already initialized");
    }

    @Override
    protected void onPause() {
        super.onPause();

        _logger.debug("onPause");
        if (_sdk != null) {
            _sdk.release();
            _sdk = null;
        }

    }

    @Override
    public void onSdkInitEnd(boolean success, String message) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                _textViewStatus.setText(success ? "SDK Initialized" : "SDK NOT Initialized");
                _textViewResponse.setText(success ? "" : message);

                log(success);
            }
        });
    }

    private void log(boolean success) {
        if (success) {
            if (!_textViewStatus.getText().toString().equals(""))
                _logger.debug(_textViewStatus.getText().toString());

            if (!_textViewResponse.getText().toString().equals(""))
                _logger.debug(_textViewResponse.getText().toString());
        } else {
            if (!_textViewStatus.getText().toString().equals(""))
                _logger.error(_textViewStatus.getText().toString());

            if (!_textViewResponse.getText().toString().equals(""))
                _logger.error(_textViewResponse.getText().toString());
        }
    }

    @Override
    public void onSdkCommandExecuted(boolean success, String profileName, String message, String xml) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                _textViewStatus.setText(success ? profileName + ": Command executed" : profileName + ": Command failed");
                _textViewResponse.setText(message != null ? message + " - " + xml : xml);

                log(success);

            }
        });

    }

}
