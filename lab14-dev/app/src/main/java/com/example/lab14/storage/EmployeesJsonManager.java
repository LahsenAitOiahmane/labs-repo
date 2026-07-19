package com.example.lab14.storage;

import android.content.Context;

import com.example.lab14.model.Employee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class EmployeesJsonManager {

    public static final String JSON_FILE_NAME = "employees_data.json";

    private EmployeesJsonManager() {}

    public static void saveEmployees(Context context, List<Employee> employees) throws Exception {
        String jsonString = convertToJson(employees);
        try (var fos = context.openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(jsonString.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static List<Employee> loadEmployees(Context context) {
        try (var fis = context.openFileInput(JSON_FILE_NAME)) {
            byte[] bytes = new byte[fis.available()];
            int bytesRead = fis.read(bytes);
            if (bytesRead != -1) {
                String jsonString = new String(bytes, 0, bytesRead, StandardCharsets.UTF_8);
                return convertFromJson(jsonString);
            }
            return new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static boolean deleteEmployeesData(Context context) {
        return context.deleteFile(JSON_FILE_NAME);
    }

    private static String convertToJson(List<Employee> employees) throws Exception {
        JSONArray jsonArray = new JSONArray();
        for (Employee emp : employees) {
            JSONObject obj = new JSONObject();
            obj.put("employeeId", emp.employeeId);
            obj.put("fullName", emp.fullName);
            obj.put("department", emp.department);
            jsonArray.put(obj);
        }
        return jsonArray.toString();
    }

    private static List<Employee> convertFromJson(String jsonString) throws Exception {
        JSONArray jsonArray = new JSONArray(jsonString);
        List<Employee> employeeList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            employeeList.add(new Employee(
                    obj.getInt("employeeId"),
                    obj.getString("fullName"),
                    obj.getString("department")
            ));
        }
        return employeeList;
    }
}
