package com.example.studenthandbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DayScheduleAdapter extends RecyclerView.Adapter<DayScheduleAdapter.ViewHolder> {
    private List<ClassItem> classList;
    private OnClassClickListener onClassClickListener;

    public interface OnClassClickListener {
        void onClassClick(ClassItem classItem);
    }

    public DayScheduleAdapter(List<ClassItem> classList, OnClassClickListener onClassClickListener) {
        this.classList = classList;
        this.onClassClickListener = onClassClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem classItem = classList.get(position);
        holder.tvTime.setText(classItem.getStartTime() + " - " + classItem.getEndTime());
        holder.tvSubject.setText(classItem.getSubject());
        holder.tvLocation.setText(classItem.getLocation());

        holder.itemView.setOnClickListener(v -> {
            if (onClassClickListener != null) {
                onClassClickListener.onClassClick(classItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public void updateClasses(List<ClassItem> classes) {
        classList.clear();
        classList.addAll(classes);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvSubject, tvLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}