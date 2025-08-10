package com.vs18.buildpc;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentAdminBinding;
import com.vs18.buildpc.databinding.FragmentComponentsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComponentsFragment extends Fragment {

    FragmentComponentsBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/components";
    List<String> componentList;
    ArrayAdapter<String> adapter;
    JSONArray componentJsonArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentComponentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(getContext());
        componentList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, componentList);
        binding.lvComponents.setAdapter(adapter);

        fetchComponent();

        binding.btnAddComponent.setOnClickListener(v -> {
            FragmentAddComponent fragmentAddComponent = new FragmentAddComponent();

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentAddComponent) // переконайся, що `flFragment` — ID твого контейнера у layout'і
                    .addToBackStack(null)
                    .commit();
        });
        binding.lvComponents.setOnItemClickListener((parent, view1, position, id) -> {
            showOptionsDialog(position);
        });
        binding.btnGenerateReport.setOnClickListener(v -> generateReport());
    }

    private void replaceFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void generateReport() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 50; // Початкова координата Y

        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Звіт про компоненти", 200, y, titlePaint);

        paint.setTextSize(14);
        y += 40; // Відступ після заголовку

        for (String component : componentList) {
            if (y > 750) { // Нова сторінка, якщо немає місця
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }

            // Розбиваємо кожен запис по рядках для кращої читабельності
            String[] lines = component.split("\n");
            for (String line : lines) {
                canvas.drawText(line, 50, y, paint);
                y += 25; // Відступ між рядками
            }

            y += 15; // Додатковий відступ між записами
            canvas.drawLine(40, y, 560, y, paint); // Горизонтальна лінія-роздільник
            y += 15;
        }

        pdfDocument.finishPage(page);

        File file = new File(getContext().getExternalFilesDir(null), "ComponentsReport.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
            Toast.makeText(getContext(), "PDF збережено: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPdf(file);
        } catch (IOException e) {
            Log.e("ComponentsActivity", "Помилка створення PDF: " + e.getMessage());
        }
    }

    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Відкрити PDF"));
    }

    private void fetchComponent(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    componentList.clear();
                    componentJsonArray = response;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject componentItem = response.getJSONObject(i);
                            String name = componentItem.getString("name");
                            String type = componentItem.getString("type");
                            String socket = componentItem.getString("socket");
                            String brand = componentItem.getString("brand");
                            double price = componentItem.getDouble("price");
                            String specs = componentItem.getString("specs");
                            componentList.add(name + "\n" + type + "\n" + socket + "\n" + brand + "\n" + price + "\n" + specs);
                        }
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        Log.e("ComponentsActivity", "Volley Помилка: " + e.getMessage());
                        Toast.makeText(getContext(), "Помилка отримання даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("ComponentsActivity", "Volley Помилка: " + error.getMessage());
            Toast.makeText(getContext(), "Помилка отримання даних", Toast.LENGTH_LONG).show();
        }
        );

        queue.add(jsonArrayRequest);
    }

    private void showOptionsDialog(int position){
        String selectedItem = componentList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Опції")
                .setMessage("Оберіть дію для: " + selectedItem)
                .setPositiveButton("Редагувати", (dialog, which) -> editComponent(position))
                .setNegativeButton("Видалити", (dialog, which) -> deleteComponent(position))
                .setNeutralButton("Скасувати", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void editComponent(int position){
        try{
            JSONObject componentItem = componentJsonArray.getJSONObject(position);

            Bundle bundle = new Bundle();
            bundle.putInt("id", componentItem.getInt("id"));
            bundle.putString("name", componentItem.getString("name"));
            bundle.putString("type", componentItem.getString("type"));
            bundle.putString("socket", componentItem.getString("socket"));
            bundle.putString("brand", componentItem.getString("brand"));
            bundle.putDouble("price", componentItem.getDouble("price"));
            bundle.putString("specs", componentItem.getString("specs"));

            FragmentAddComponent fragmentAddComponent = new FragmentAddComponent();
            fragmentAddComponent.setArguments(bundle);

// Fragment Transaction
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentAddComponent) // Заміни на твій ID контейнера
                    .addToBackStack(null)
                    .commit();
        }catch (JSONException e){
            Log.e("ComponentsActivity", "Помилка отримання елементу: " + e.getMessage());
        }
    }

    private void deleteComponent(int position){
        try{
            JSONObject componentItem = componentJsonArray.getJSONObject(position);
            int componentId = componentItem.getInt("id");

            String deleteUrl = BASE_URL + "/" + componentId;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                    response -> {
                        Toast.makeText(getContext(), "Компонент видалено!", Toast.LENGTH_LONG).show();
                        fetchComponent();
                    },
                    error -> {
                        Log.e("ComponentsActivity", "Помилка видалення: " + error.getMessage());
                        Toast.makeText(getContext(), "Помилка видалення!", Toast.LENGTH_LONG).show();
                    });

            queue.add(request);
        }catch (JSONException e){
            Log.e("ComponentsActivity", "Помилка JSON: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchComponent();
    }
}
