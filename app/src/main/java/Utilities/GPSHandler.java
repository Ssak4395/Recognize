package Utilities;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GPSHandler {

    private String location;
    private LocationRequest locationRequest;

    public GPSHandler()
    {

    }

    public static boolean isGPSEnablesd(Context context)
    {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        if(locationManager == null)
        {
            locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    public  String getLocation()
    {
        return location;
    }

    public void setLocation(String loc)
    {
        this.location = loc;
    }

}
