package com.vs18.buildpc;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConfigsAdapter extends RecyclerView.Adapter<ConfigsAdapter.ConfigsViewHolder> {
    List<Config> configsList;

    public ConfigsAdapter(List<Config> configsList) {
        this.configsList = configsList;
    }

    @NonNull
    @Override
    public ConfigsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_config, parent, false);
        return new ConfigsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ConfigsViewHolder holder, int position) {
        Config config = configsList.get(position);
        holder.configId.setText("Конфігурація №" + config.getConfigId());
        holder.configName.setText("Назва " + config.getConfigName());

        // Форматування дати перед відображенням
        String formattedDate = formatDate(config.getCreatedAt());
        holder.createdAt.setText("Дата: " + formattedDate);

        StringBuilder componentsText = new StringBuilder();
        for (Component component : config.getComponents()) {
            componentsText.append(component.getName());
        }
        holder.components.setText(componentsText.toString());
    }

    @Override
    public int getItemCount() {
        return configsList.size();
    }

    static class ConfigsViewHolder extends RecyclerView.ViewHolder {
        TextView configId, configName, createdAt, components;

        public ConfigsViewHolder(@NonNull View itemView) {
            super(itemView);
            configId = itemView.findViewById(R.id.config_id);
            configName = itemView.findViewById(R.id.config_name);
            createdAt = itemView.findViewById(R.id.textCreatedAt);
            components = itemView.findViewById(R.id.textComponents);
        }
    }

    // Метод для форматування дати
    private String formatDate(String rawDate) {
        try {
            // Вхідний формат (припустимо, що сервер повертає "yyyy-MM-dd'T'HH:mm:ss")
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

            Date date = inputFormat.parse(rawDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return rawDate; // Якщо сталася помилка, повертаємо оригінальний рядок
        }
    }
}
