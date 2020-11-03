package com.example.smartwasting;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.model.ListThingsRequest;
import com.amazonaws.services.iot.model.ListThingsResult;
import com.amazonaws.services.iot.model.ThingAttribute;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FindBin extends Thread {

    private Context context;
    private String category;
    private Location userLocation;
    private TextView resultView;
    private AWSIot iotClient;
    private BasicAWSCredentials cred;

    public FindBin(Context context, String category, Location userLocation, TextView resultView) {
        this.context = context;
        this.category = category;
        this.userLocation = userLocation;
        this.resultView = resultView;
        CreateIotClient cr = new CreateIotClient();
        iotClient = cr.getIotClient();
        cred = new BasicAWSCredentials("--",
                "--" );

    }

    public void run() {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            ListThingsRequest listThingsRequest = new ListThingsRequest();
            listThingsRequest.withAttributeName("category").withAttributeValue(category.toUpperCase());

            long startTime = System.nanoTime();
            ListThingsResult listThingsResult = iotClient.listThings(listThingsRequest);
            long finishTime = System.nanoTime();
            long timeElapsed = finishTime - startTime;
            Log.d("TIME listThings",""+timeElapsed/1000000 +"millisec");
            List<ThingAttribute> thingAttributes = listThingsResult.getThings();
            Map<String, String> attributes;
            double lat, lon;
            Location binLocation;
            List<ThingAttribute> nearestThings = new ArrayList<>();
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            String addressLine,thingName,latitude,longitude,fillLevel;
            try {
                for (ThingAttribute t : thingAttributes) {
                    attributes = t.getAttributes();
                    if(attributes.get("latitude") != null && attributes.get("longitude") != null) {
                        lat = Double.parseDouble(attributes.get("latitude"));
                        lon = Double.parseDouble(attributes.get("longitude"));
                        binLocation = new Location("");
                        binLocation.setLatitude(lat);
                        binLocation.setLongitude(lon);
                        if (binLocation.distanceTo(userLocation) < 500) {
                            Log.d("DISTANCE", t.getThingName());
                            nearestThings.add(t);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!nearestThings.isEmpty()) {
                    nearestThings.sort(new Comparator<ThingAttribute>() {
                        @Override
                        public int compare(ThingAttribute o1, ThingAttribute o2) {
                            return o1.getAttributes().get("fill_level").compareTo(o2.getAttributes().get("fill_level"));
                        }
                    });

                    ThingAttribute result = nearestThings.get(0);
                    latitude = result.getAttributes().get("latitude");
                    longitude = result.getAttributes().get("longitude");

                    if ( latitude != null &&  longitude != null) {
                        lat = Double.parseDouble(latitude);
                        lon = Double.parseDouble(longitude);
                        addresses = geocoder.getFromLocation(lat, lon, 1);
                        addressLine = addresses.get(0).getAddressLine(0);
                        thingName = result.getThingName();
                        fillLevel = result.getAttributes().get("fill_level");
                        if (Double.parseDouble(fillLevel) > 80)
                            resultView.setText("Vai a buttarlo qui:\n" + thingName + "\n" + addressLine + "\n" + "Affrettati perchè è quasi pieno!");
                        else
                            resultView.setText("Vai a buttarlo qui:\n" + thingName + "\n" + addressLine + "\nPieno al " + fillLevel.substring(0, 5) + "%\n");
                    }
                }
                else
                    resultView.append("Nessun cassonetto nelle vicinanze :-(");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
