package com.augment.golden.bulbcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import java.util.HashSet;

public class Common {

    public static String getSafeString(String string){
        if(string == null) return "";

        return string.trim();
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static String convertByteArrToHex(byte[] bytes){
        String message = "";
        if(bytes.length > 0)
            for(int i = 0; i < bytes[0]; i++)
                message += byteToHex(bytes[i]);

        return message;
    }

    public static byte[] getSubArray(byte[] arr, int start, int end){

        byte[] newArr = new byte[(end - start)];
        int j = 0;
        for(int i = start; i < end; i++)
            newArr[j++] = arr[i];

        return newArr;
    }

    public static boolean isValidJsonObject(String json){
        try{
            JSONObject object = new JSONObject(json);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static void clearAll(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearAllBulbs(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("groupIds", new HashSet<>());
        editor.putStringSet("lifxGroups", new HashSet<>());
        editor.putStringSet("groupIds", new HashSet<>());
        editor.putStringSet("macAddresses", new HashSet<>());

        editor.apply();
    }
}
