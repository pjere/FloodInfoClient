package org.pjeremie.floodinfo;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by pjere on 1/8/17 from http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
 *
 *  This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FloodInfo";

    private HandlerThread handlerThread ;
    private Handler handler ;

    private double mLatitude;
    private double mLongitude;
    private boolean isFlooded;
    private boolean isCaveFlooded;
    private boolean hasPower;
    private boolean hasWater;
    private boolean hasDrainage;
    private boolean hasGaz;
    private boolean hasTelephone;
    private int numImpacted;
    private ToggleButton FloodedtoggleButton;
    private ToggleButton FloodedCavetoggleButton;
    private ToggleButton PowertoggleButton;
    private ToggleButton EauPotabletoggleButton;
    private ToggleButton AssainissementtoggleButton;
    private ToggleButton GaztoggleButton;
    private ToggleButton TelephonetoggleButton;
    private Button button;
    private NumberPicker numberPicker;
    private GPSTracker gps;
    private String url = "https://home.pjeremie.org/floodinfo/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int i=0;
        FloodedtoggleButton = (ToggleButton) findViewById(R.id.FloodedtoggleButton);
        FloodedCavetoggleButton = (ToggleButton) findViewById(R.id.FloodedCavetoggleButton);
        PowertoggleButton = (ToggleButton) findViewById(R.id.PowertoggleButton);
        EauPotabletoggleButton = (ToggleButton) findViewById(R.id.EauPotabletoggleButton);
        AssainissementtoggleButton = (ToggleButton) findViewById(R.id.AssainissementtoggleButton);
        GaztoggleButton = (ToggleButton) findViewById(R.id.GaztoggleButton);
        TelephonetoggleButton = (ToggleButton) findViewById(R.id.TelephonetoggleButton);
        button = (Button) findViewById(R.id.button);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        addListenerOnButton();
        //Set the minimum value of NumberPicker
        numberPicker.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        numberPicker.setMaxValue(10);

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //Display the newly selected number from picker
                numImpacted = newVal;
            }
        });
        handlerThread = new HandlerThread("myHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }



    private void sendPost() throws Exception {

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        gps = new GPSTracker(MainActivity.this);
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        String urlParameters="";
        synchronized(this) {
            urlParameters = "isFlooded=" + isFlooded + "&isCaveFlooded=" + isCaveFlooded + "&hasPower=" + hasPower + "&hasWater=" + hasWater + "&hasDrainage=" + hasDrainage + "&hasTelephone=" + hasTelephone + "&hasGaz=" + hasGaz + "&numImpacted=" + numImpacted + "&mLatitude=" + mLatitude + "&mLongitude=" + mLongitude;
        }
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Log.i(TAG,urlParameters);
        Log.i(TAG,""+responseCode);
        con.disconnect();
        Log.i(TAG,"HasDisconnected");
    }




    public void addListenerOnButton() {
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                StringBuffer result = new StringBuffer();
                synchronized(this) {
                result.append("Inondé : ").append(FloodedtoggleButton.getText());
                result.append("\nCave Inondée : ").append(FloodedCavetoggleButton.getText());
                result.append("\nÉlectricité : ").append(PowertoggleButton.getText());
                result.append("\nGaz : ").append(GaztoggleButton.getText());
                result.append("\nTéléphone : ").append(TelephonetoggleButton.getText());
                result.append("\nAssainissement : ").append(AssainissementtoggleButton.getText());
                result.append("\nEau Potable : ").append(EauPotabletoggleButton.getText());
                result.append("\nImpactés : ").append(numImpacted);


                    isFlooded = FloodedtoggleButton.isChecked();
                    isCaveFlooded = FloodedCavetoggleButton.isChecked();
                    hasWater = EauPotabletoggleButton.isChecked();
                    hasPower = PowertoggleButton.isChecked();
                    hasGaz = GaztoggleButton.isChecked();
                    hasTelephone = TelephonetoggleButton.isChecked();
                    hasDrainage = AssainissementtoggleButton.isChecked();


                    // check if GPS enabled
                    if (gps.canGetLocation()) {

                        mLatitude = gps.getLatitude();
                        mLongitude = gps.getLongitude();


                        result.append("\nLongitude : ").append(mLongitude);
                        result.append("\nLatitude : ").append(mLatitude);
                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
                }
                handler.post(new Runnable(){
                    @Override
                    public void run() {

                            try {
                                sendPost();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                });

                //Toast Message for debugging purposes.
                Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_SHORT).show();

            }

        });
    }



}
