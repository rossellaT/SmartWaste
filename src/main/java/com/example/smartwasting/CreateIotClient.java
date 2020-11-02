package com.example.smartwasting;

import android.util.Log;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClient;

public class CreateIotClient {

    private AWSIot iotClient;
    private BasicAWSCredentials cred;

    public CreateIotClient() {
        //Set IoT Client
        cred = new BasicAWSCredentials("--",
                "--" );
        long startConnectingTime = System.nanoTime();
        iotClient = new AWSIotClient(cred);
        long finishConnectingTime = System.nanoTime();
        long timeElapsed = finishConnectingTime - startConnectingTime;
        Log.d("TIME ELAPSED AWSIotClient",""+timeElapsed/1000000 +"millisec");
        iotClient.setRegion(Region.getRegion("eu-central-1"));
    }



    public AWSIot getIotClient() {
        return iotClient;
    }

}
