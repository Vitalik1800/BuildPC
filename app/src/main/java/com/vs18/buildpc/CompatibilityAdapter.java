package com.vs18.buildpc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompatibilityAdapter extends RecyclerView.Adapter<CompatibilityAdapter.CompatibilityViewHolder>{

    List<CompatibilityRule> compatibilityList;

    public CompatibilityAdapter(List<CompatibilityRule> compatibilityList){
        this.compatibilityList = compatibilityList;
    }

    @NonNull
    @Override
    public CompatibilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new CompatibilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompatibilityViewHolder holder, int position) {
        CompatibilityRule rule = compatibilityList.get(position);
        holder.ruleId.setText("Порівняння № " + rule.getRuleId());
        if (rule.isIs_compatible()) {
            holder.is_compatible.setText("✅ Сумісні компоненти");
        } else {
            holder.is_compatible.setText("❌ Несумісні компоненти");
        }

        StringBuilder componentsText = new StringBuilder();
        for (Component component : rule.getComponents()) {
            componentsText.append(component.getName()).append(" ");
        }
        holder.components.setText(componentsText.toString());
    }

    @Override
    public int getItemCount() {
        return compatibilityList.size();
    }

    static class CompatibilityViewHolder extends RecyclerView.ViewHolder{
        TextView ruleId, is_compatible, components;

        public CompatibilityViewHolder(@NonNull View itemView){
            super(itemView);
            ruleId = itemView.findViewById(R.id.rule_id);
            is_compatible = itemView.findViewById(R.id.is_compatible);
            components = itemView.findViewById(R.id.textComponents);
        }
    }

}
