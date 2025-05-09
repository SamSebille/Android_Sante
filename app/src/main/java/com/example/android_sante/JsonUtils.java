package com.example.android_sante;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    public static List<Credential> getUserCredentials(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.crediential);
        String jsonString = "";

        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<List<Credential>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public static List<DataBody> getAllDataBody(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.databody);
        String jsonString = "";

        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<List<DataBody>>() {}.getType();

        return new Gson().fromJson(jsonString, listType);
    }

    public static DataBody getDataBody(Context context, int id) {
        List<DataBody> dataBodies = getAllDataBody(context);
        for (DataBody dataBody : dataBodies) {
            if (dataBody.getId() == id) {
                return dataBody;
            }
        }
        return null;
    }

    public static List<Food> getFood(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.food);
        String jsonString = "";

        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<List<Food>>() {}.getType();

        return new Gson().fromJson(jsonString, listType);
    }

    public static List<Lunch> getAllLunch(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.lunch);
        String jsonString = "";

        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Type listType = new TypeToken<List<Lunch>>() {}.getType();

        return new Gson().fromJson(jsonString, listType);
    }

    public static Lunch getLunch(Context context, int id) {
        List<Lunch> Lunches = getAllLunch(context);
        for (Lunch lunch : Lunches) {
            if (lunch.getId() == id) {
                return lunch;
            }
        }
        return null;
    }

    public static void writeJsonToFile(Context context, String json, String filename) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}