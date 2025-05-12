package com.example.android_sante;
import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    public static List<Credential> getUserCredentials(Context context) {
        String jsonString = readJsonFromFile(context, "credentials.json");
        Type listType = new TypeToken<List<Credential>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public static List<DataBody> getAllDataBody(Context context, String filename) {
        String jsonString = readJsonFromFile(context, filename);
        Type listType = new TypeToken<List<DataBody>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public static DataBody getDataBody(Context context, int id) {
        List<DataBody> dataBodies = getAllDataBody(context, "databody.json");
        for (DataBody dataBody : dataBodies) {
            if (dataBody.getId() == id) {
                return dataBody;
            }
        }
        return null;
    }

    public static List<Food> getFood(Context context) {
        String jsonString = readJsonFromFile(context, "food.json");
        Type listType = new TypeToken<List<Food>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public static List<Lunch> getAllLunch(Context context, String filename) {
        String jsonString = readJsonFromFile(context, filename);
        Type listType = new TypeToken<List<Lunch>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public static List<Lunch> getLunch(Context context, int id) {
        List<Lunch> lunches = getAllLunch(context, "lunch.json");
        for (Lunch lunch : lunches) {
            if (lunch.getId() == id) {
                return null;
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

    private static String readJsonFromFile(Context context, String filename) {
        String jsonString = "";
        try {
            FileInputStream fis = context.openFileInput(filename);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static void copyRawJsonToInternalStorage(Context context, int rawResourceId, String filename) {
        // Vérifier si le fichier existe déjà
        File file = context.getFileStreamPath(filename);
        if (file.exists()) {
            // Le fichier existe déjà, pas besoin de le copier
            return;
        }

        // Lire le fichier JSON depuis les ressources brutes
        InputStream inputStream = context.getResources().openRawResource(rawResourceId);
        String jsonString = "";

        try {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();

            jsonString = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Écrire le fichier JSON dans le stockage interne
        writeJsonToFile(context, jsonString, filename);
    }

    public static <T> void rewriteJsonFileInInternalStorage(Context context, String filename, List<T> newData) {
        // Convertir les nouvelles données en JSON
        Gson gson = new Gson();
        String newJsonString = gson.toJson(newData);
        System.out.println("LOG newJsonString: " + newJsonString);
        // Écrire le JSON dans le fichier
        writeJsonToFile(context, newJsonString, filename);
    }
}
