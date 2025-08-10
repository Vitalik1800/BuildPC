package com.vs18.buildpc;

import android.annotation.*;
import android.content.*;
import android.os.Bundle;
import android.util.*;
import android.view.*;
import android.view.*;
import androidx.annotation.*;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.*;
import com.vs18.buildpc.databinding.ComponentCardBinding;
import java.util.*;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder>{

    FragmentActivity activity;
    private final List<Component> componentList;
    private final Context context;
    private List<Component> fullList;

    public ComponentAdapter(FragmentActivity activity, List<Component> componentList){
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.componentList = componentList;
        this.fullList = new ArrayList<>(componentList);
    }

    public void setFullList(List<Component> fullList) {
        this.fullList = fullList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text){
        text = text.toLowerCase(Locale.ROOT);
        componentList.clear();

        if(text.isEmpty()){
            componentList.addAll(fullList);
        } else{
            for (Component c : fullList){
                if(c.getName().toLowerCase().contains(text)
                    || c.getBrand().toLowerCase().contains(text)
                    || c.getType().toLowerCase().contains(text)
                    || String.valueOf(c.getPrice()).contains(text)){
                    componentList.add(c);
                }
            }
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComponentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ComponentCardBinding binding = ComponentCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ComponentViewHolder(binding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ComponentViewHolder holder, int position) {
        Component component = componentList.get(position);
        holder.binding.tvName.setText(component.getName());
        holder.binding.tvType.setText(component.getType());
        holder.binding.tvSocket.setText(component.getSocket());
        holder.binding.tvBrand.setText(component.getBrand());
        holder.binding.tvPrice.setText(String.format("%.2f грн", component.getPrice()));
        holder.binding.tvSpecs.setText(component.getSpecs());

        int userId = getUserId();
        Log.d("ComponentAdapter", "User ID: " + userId);

        holder.binding.btnConfig.setOnClickListener(v -> {
            if(userId != -1 && component.getId() > 0){
                /*Intent intent = new Intent(context, AddConfigActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("componentId", component.getId());
                intent.putExtra("name", component.getName());*/

                ArrayList<ComponentItem> componentListItem = new ArrayList<>();
                componentListItem.add(new ComponentItem(component.getId()));

                FragmentAddConfig fragmentAddConfig = new FragmentAddConfig();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", userId);
                bundle.putString("name", component.getName());
                bundle.putSerializable("componentList", componentListItem); // ComponentItem implements Serializable
                fragmentAddConfig.setArguments(bundle);

                activity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fragmentAddConfig)
                        .addToBackStack(null)
                        .commit();
            } else{
                Log.e("ComponentAdapter", "Invalid userId or componentId!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return componentList.size();
    }

    private int getUserId() {
        SharedPreferences preferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getInt("userId", -1);
    }

    public static class ComponentViewHolder extends RecyclerView.ViewHolder{
        final ComponentCardBinding binding;

        public ComponentViewHolder(ComponentCardBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
