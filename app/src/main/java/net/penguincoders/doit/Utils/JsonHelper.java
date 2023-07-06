package net.penguincoders.doit.Utils;

import android.content.Context;
import android.util.Log;

import net.penguincoders.doit.Model.ToDoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    private static final String TAG = "JsonHelper";
    private static final String JSON_FILE_NAME = "tasks.json";

    private Context context;

    public JsonHelper(Context context) {
        this.context = context;
    }

    public List<ToDoModel> loadTasksFromJson() {
        List<ToDoModel> taskList = new ArrayList<>();

        try {
            String jsonString = loadJsonFromFile();
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ToDoModel task = new ToDoModel();
                task.setId(jsonObject.getInt("id"));
                task.setTask(jsonObject.getString("task"));
                task.setStatus(jsonObject.getInt("status"));
                taskList.add(task);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON dosyası yüklenirken hata oluştu: " + e.getMessage());
        }

        return taskList;
    }

    public void saveTasksToJson(List<ToDoModel> taskList) {
        JSONArray jsonArray = new JSONArray();

        for (ToDoModel task : taskList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", task.getId());
                jsonObject.put("task", task.getTask());
                jsonObject.put("status", task.getStatus());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "JSON nesnesi oluşturulurken hata oluştu: " + e.getMessage());
            }
        }

        writeJsonToFile(jsonArray.toString());
    }

    public void close() {
        // Görevleri JSON dosyasına kaydetmeden önce kapatma işlemi yapılır
        List<ToDoModel> taskList = loadTasksFromJson();
        saveTasksToJson(taskList);

        // Ek temizlik veya kaynakları serbest bırakma işlemleri burada gerçekleştirilebilir
        // Örneğin, açık olan dosya veya veritabanı bağlantıları kapatılabilir

        // taskList ve context referanslarını temizleme
        taskList = null;
        context = null;
    }

    public void insertTask(ToDoModel task) {
        List<ToDoModel> taskList = loadTasksFromJson();
        taskList.add(task);
        saveTasksToJson(taskList);
    }

    public void updateTask(int taskId, String newTask) {
        List<ToDoModel> taskList = loadTasksFromJson();

        for (ToDoModel task : taskList) {
            if (task.getId() == taskId) {
                task.setTask(newTask);
                break;
            }
        }

        saveTasksToJson(taskList);
    }

    public void deleteTask(int position) {
        // JSON dosyasındaki ilgili pozisyondaki görevi silin
        List<ToDoModel> taskList = loadTasksFromJson();
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            saveTasksToJson(taskList);
        }
    }
    private boolean isFileExists() {
        String[] files = context.fileList();
        for (String file : files) {
            if (file.equals(JSON_FILE_NAME)) {
                return true;
            }
        }
        return false;
    }

    private String loadJsonFromFile() {
        String json = null;
        try {
            InputStream inputStream;
            if (isFileExists()) {
                inputStream = context.openFileInput(JSON_FILE_NAME);
            } else {
                inputStream = context.getAssets().open(JSON_FILE_NAME);
            }

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e(TAG, "JSON dosyası okunurken hata oluştu: " + e.getMessage());
        }
        return json;
    }


    private void writeJsonToFile(String json) {
        try {
            OutputStream outputStream = context.openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "JSON dosyasına yazılırken hata oluştu: " + e.getMessage());
        }
    }
}
