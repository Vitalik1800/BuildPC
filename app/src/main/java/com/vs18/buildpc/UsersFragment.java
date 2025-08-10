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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.vs18.buildpc.databinding.FragmentAdminBinding;
import com.vs18.buildpc.databinding.FragmentUsersBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    FragmentUsersBinding binding;
    RequestQueue queue;
    private static final String BASE_URL = "http://10.0.2.2:3000/users/no_admin";
    List<String> usersList;
    ArrayAdapter<String> adapter;
    JSONArray usersJsonArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        queue = Volley.newRequestQueue(getContext());
        usersList = new ArrayList<>();

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, usersList);
        binding.lvUsers.setAdapter(adapter);

        fetchOrder();

        binding.btnGenerateReport.setOnClickListener(v -> generateReport());
    }

    private void fetchOrder(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL,
                null,
                response -> {
                    usersList.clear();
                    usersJsonArray = response;
                    try{
                        for(int i = 0; i < response.length(); i++){
                            JSONObject users = response.getJSONObject(i);
                            String name = users.getString("name");
                            String email = users.getString("email");
                            usersList.add(name + "\n" + email);
                        }
                        adapter.notifyDataSetChanged();
                    }catch (JSONException e){
                        Log.e("UsersActivity", "JSON Помилка: " + e.getMessage());
                        Toast.makeText(getContext(), "Помилка обробки даних", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            Log.e("UsersActivity", "Volley Помилка: " + error.getMessage());
            Toast.makeText(getContext(), "Помилка отримання даних", Toast.LENGTH_LONG).show();
        }
        );

        queue.add(jsonArrayRequest);
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
        canvas.drawText("Звіт про користувачів", 200, y, titlePaint);

        paint.setTextSize(14);
        y += 40; // Відступ після заголовку

        for (String book : usersList) {
            if (y > 750) { // Нова сторінка, якщо немає місця
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }

            // Розбиваємо кожен запис по рядках для кращої читабельності
            String[] lines = book.split("\n");
            for (String line : lines) {
                canvas.drawText(line, 50, y, paint);
                y += 25; // Відступ між рядками
            }

            y += 15; // Додатковий відступ між записами
            canvas.drawLine(40, y, 560, y, paint); // Горизонтальна лінія-роздільник
            y += 15;
        }

        pdfDocument.finishPage(page);

        File file = new File(getContext().getExternalFilesDir(null), "UsersReport.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
            Toast.makeText(getContext(), "PDF збережено: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPdf(file);
        } catch (IOException e) {
            Log.e("BooksActivity", "Помилка створення PDF: " + e.getMessage());
        }
    }


    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Відкрити PDF"));
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchOrder();
    }
}
