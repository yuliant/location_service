package com.masrizal.android.locationservice;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.masrizal.android.locationservice.api.ApiClient;
import com.masrizal.android.locationservice.api.ApiInterface;
import com.masrizal.android.locationservice.model.Respon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationService extends Service {

    ApiInterface apiInterface;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null){
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                Log.d("LOCATION_UPDATE", latitude + ", " + longitude);

                apiInterface = ApiClient.getData().create(ApiInterface.class);
                Call<Respon> sendgps = apiInterface.SendGps(
                        "yourapi",
                        "0",
                        Double.toString(latitude),
                        Double.toString(longitude)
                );

                sendgps.enqueue(new Callback<Respon>() {
                    @Override
                    public void onResponse(Call<Respon> call, Response<Respon> response) {
                        try {

                            if (response.body().isStatus()) {
                                Log.d("status", response.body().isStatus() + "");

                                if (response.isSuccessful()) {
                                    Log.d("status", response.body().getMessage() + "");

                                } else if (response.code() == 400) {

                                }


                            } else {
                                Log.d("status", response.body().isStatus() + "");
                                Toast.makeText(getApplicationContext(), "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ex) {
                            Log.e("error", ex.getMessage());
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Respon> call, Throwable t) {

                    }
                });
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(20000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }

    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String action = intent.getAction();
            if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)){
                startLocationService();
            }else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)){
                stopLocationService();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
