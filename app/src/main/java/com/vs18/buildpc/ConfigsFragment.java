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
import com.google.android.material.tabs.TabLayout;
import com.vs18.buildpc.databinding.FragmentConfigsBinding;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigsFragment extends Fragment {

    FragmentConfigsBinding binding;
    private ArrayAdapter<String> adapterConfigs, adapterConfigComponents, adapterCompatibility;
    private final List<String> configsList = new ArrayList<>();
    private final List<String> configComponentsList = new ArrayList<>();
    private final List<String> compatibilityList = new ArrayList<>();
    private RequestQueue queue;

    private static final String URL_CONFIGS = "http://10.0.2.2:3000/configs";
    private static final String URL_CONFIG_COMPONENTS = "http://10.0.2.2:3000/config-components";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConfigsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapterConfigs = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, configsList);
        adapterConfigComponents = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, configComponentsList);
        adapterCompatibility = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, compatibilityList);

        binding.lvConfigs.setAdapter(adapterConfigs);
        binding.lvConfigComponents.setAdapter(adapterConfigComponents);
        binding.lvCompatibility.setAdapter(adapterCompatibility);

        queue = Volley.newRequestQueue(getContext());

        fetchConfigs();
        fetchConfigComponents();

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Configs"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Config Components"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Compatibility"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                binding.lvConfigs.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);
                binding.lvConfigComponents.setVisibility(pos == 1 ? View.VISIBLE : View.GONE);
                binding.lvCompatibility.setVisibility(pos == 2 ? View.VISIBLE : View.GONE);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.btnGenerateReport.setOnClickListener(v -> generateReport());
    }

    private void fetchConfigs() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_CONFIGS, null,
                response -> {
                    configsList.clear();
                    compatibilityList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject config = response.getJSONObject(i);
                            JSONObject user = config.getJSONObject("User");
                            String userEmail = user.getString("email");
                            String configName = config.getString("config_name");
                            configsList.add(userEmail + "\n" + configName);

                            if (user.has("CompatibilityRules")) {
                                for (int j = 0; j < user.getJSONArray("CompatibilityRules").length(); j++) {
                                    JSONObject rule = user.getJSONArray("CompatibilityRules").getJSONObject(j);
                                    JSONObject compA = rule.getJSONObject("componentA");
                                    JSONObject compB = rule.getJSONObject("componentB");
                                    String ruleText = "A: " + compA.getString("name") +
                                            " <-> B: " + compB.getString("name") +
                                            "\nСумісність: " + (rule.getInt("is_compatible") == 1 ? "✅ Так" : "❌ Ні");
                                    compatibilityList.add(ruleText);
                                }
                            }
                        }
                        adapterConfigs.notifyDataSetChanged();
                        adapterCompatibility.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("ConfigsFragment", "Error parsing configs: " + e.getMessage());
                        Toast.makeText(getContext(), "Помилка обробки configs", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ConfigsFragment", "Error fetching configs: " + error.getMessage());
                    Toast.makeText(getContext(), "Помилка мережі при завантаженні configs", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void fetchConfigComponents() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL_CONFIG_COMPONENTS, null,
                response -> {
                    configComponentsList.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            JSONObject config = item.getJSONObject("Config");
                            JSONObject component = item.getJSONObject("Component");

                            String configName = config.getString("config_name");
                            String componentName = component.getString("name");

                            configComponentsList.add(configName + " -> " + componentName);
                        }
                        adapterConfigComponents.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("ConfigsFragment", "Error parsing config components: " + e.getMessage());
                        Toast.makeText(getContext(), "Помилка обробки config components", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ConfigsFragment", "Error fetching config components: " + error.getMessage());
                    Toast.makeText(getContext(), "Помилка мережі при завантаженні config components", Toast.LENGTH_SHORT).show();
                });

        queue.add(request);
    }

    private void generateReport() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int y = 50;
        titlePaint.setTextSize(20);
        titlePaint.setFakeBoldText(true);

        int selectedTab = binding.tabLayout.getSelectedTabPosition();
        List<String> reportList;
        String reportTitle;

        switch (selectedTab) {
            case 0:
                reportList = configsList;
                reportTitle = "Звіт про конфігурації";
                break;
            case 1:
                reportList = configComponentsList;
                reportTitle = "Звіт про компоненти конфігурацій";
                break;
            case 2:
                reportList = compatibilityList;
                reportTitle = "Звіт про сумісність";
                break;
            default:
                return;
        }

        canvas.drawText(reportTitle, 150, y, titlePaint);
        paint.setTextSize(14);
        y += 40;

        for (String item : reportList) {
            if (y > 750) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }

            String[] lines = item.split("\n");
            for (String line : lines) {
                canvas.drawText(line, 50, y, paint);
                y += 25;
            }

            y += 15;
            canvas.drawLine(40, y, 560, y, paint);
            y += 15;
        }

        pdfDocument.finishPage(page);

        String fileName;
        switch (selectedTab) {
            case 0: fileName = "ConfigsReport.pdf"; break;
            case 1: fileName = "ConfigComponentsReport.pdf"; break;
            case 2: fileName = "CompatibilityReport.pdf"; break;
            default: fileName = "Report.pdf";
        }

        File file = new File(getContext().getExternalFilesDir(null), fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
            Toast.makeText(getContext(), "PDF збережено: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPdf(file);
        } catch (IOException e) {
            Log.e("ConfigsFragment", "Помилка створення PDF: " + e.getMessage());
            Toast.makeText(getContext(), "Помилка створення PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Відкрити PDF"));
    }
}
