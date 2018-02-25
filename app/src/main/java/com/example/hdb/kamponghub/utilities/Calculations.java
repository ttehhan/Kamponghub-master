package com.example.hdb.kamponghub.utilities;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Calculations {

    public static String calcShopOpen(String timeStart, String timeEnd, String currentTime){
        //TODO: Calculate is shop open
        return "Open";

    }
    public static String calcTime(String timeStart, String timeEnd){
        if (timeStart.equals(timeEnd)){
            return "24hrs";
        }
        else{

            return (String.format("%s to %s",timeStart,timeEnd));
        }
    }
    public static String calcDistance(String currentDistance, String shopCoordinates){
        //TODO: Need to calculate distance based on current coordinates and shop coordinates
        return("1km");
    }

    public static String bitmapToBase64(Bitmap bitmap, int quality){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap base64ToBitmap(String base64string, int reqWidth, int reqHeight){

        byte[] decodedString = Base64.decode(base64string, Base64.DEFAULT);
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        return bitmap;


    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String calculateZone(String postalCode){
        String zone ="";
        String first2digits = postalCode.substring(0,2);
        switch (first2digits) {
            case "01":
                zone="South";
                break;
            case "02":
                zone="South";
                break;
            case "03":
                zone="South";
                break;
            case "04":
                zone="South";
                break;
            case "05":
                zone="South";
                break;
            case "06":
                zone="South";
                break;
            case "07":
                zone="South";
                break;
            case "08":
                zone="South";
                break;
            case "09":
                zone="South";
                break;
            case "10":
                zone="South";
                break;
            case "11":
                zone="South";
                break;
            case "12":
                zone="South";
                break;
            case "13":
                zone="South";
                break;
            case "14":
                zone="South";
                break;
            case "15":
                zone="South";
                break;
            case "16":
                zone="South";
                break;
            case "17":
                zone="Central";
                break;
            case "18":
                zone="Central";
                break;
            case "19":
                zone="Central";
                break;
            case "20":
                zone="Central";
                break;
            case "21":
                zone="Central";
                break;
            case "22":
                zone="Central";
                break;
            case "23":
                zone="Central";
                break;
            case "24":
                zone="Central";
                break;
            case "25":
                zone="Central";
                break;
            case "26":
                zone="Central";
                break;
            case "27":
                zone="Central";
                break;
            case "28":
                zone="Central";
                break;
            case "29":
                zone="Central";
                break;
            case "30":
                zone="Central";
                break;
            case "31":
                zone="Central";
                break;
            case "32":
                zone="Central";
                break;
            case "33":
                zone="Central";
                break;
            case "34":
                zone="Central";
                break;
            case "35":
                zone="Central";
                break;
            case "36":
                zone="Central";
                break;
            case "37":
                zone="Central";
                break;
            case "38":
                zone="Central";
                break;
            case "39":
                zone="Central";
                break;
            case "40":
                zone="Central";
                break;
            case "41":
                zone="Central";
                break;
            case "42":
                zone="East";
                break;
            case "43":
                zone="East";
                break;
            case "44":
                zone="East";
                break;
            case "45":
                zone="East";
                break;
            case "46":
                zone="East";
                break;
            case "47":
                zone="East";
                break;
            case "48":
                zone="East";
                break;
            case "49":
                zone="East";
                break;
            case "50":
                zone="East";
                break;
            case "51":
                zone="East";
                break;
            case "52":
                zone="East";
                break;
            case "53":
                zone="Central";
                break;
            case "54":
                zone="Central";
                break;
            case "55":
                zone="Central";
                break;
            case "56":
                zone="Central";
                break;
            case "57":
                zone="Central";
                break;
            case "58":
                zone="West";
                break;
            case "59":
                zone="West";
                break;
            case "60":
                zone="West";
                break;
            case "61":
                zone="West";
                break;
            case "62":
                zone="West";
                break;
            case "63":
                zone="West";
                break;
            case "64":
                zone="West";
                break;
            case "65":
                zone="West";
                break;
            case "66":
                zone="West";
                break;
            case "67":
                zone="West";
                break;
            case "68":
                zone="West";
                break;
            case "69":
                zone="West";
                break;
            case "70":
                zone="West";
                break;
            case "71":
                zone="West";
                break;
            case "72":
                zone="North";
                break;
            case "73":
                zone="North";
                break;
/*            case "74":
                zone="North";
                break;*/
            case "75":
                zone="North";
                break;
            case "76":
                zone="North";
                break;
            case "77":
                zone="North";
                break;
            case "78":
                zone="North";
                break;
            case "79":
                zone="North";
                break;
            case "80":
                zone="North";
                break;
            case "81":
                zone="East";
                break;
            case "82":
                zone="Central";
                break;
        }

        return zone;
    }

    public static LatLng getLatLngFromPostal(Context context, String postalCode){
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(postalCode, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    public static boolean checkPostalValid(String postalCode){
        boolean valid = false;

        return valid;
    }
}
