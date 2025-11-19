package com.example.studenthandbook;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class DayScheduleFragment extends Fragment {

    private static final String ARG_DAY = "day";

    private RecyclerView recyclerView;
    private DayScheduleAdapter adapter;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAdd;
    private List<ClassItem> classList;
    private String currentDay;

    public static DayScheduleFragment newInstance(String day) {
        DayScheduleFragment fragment = new DayScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentDay = getArguments().getString(ARG_DAY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_schedule, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDay);
        fabAdd = view.findViewById(R.id.fabAddDay);
        databaseHelper = new DatabaseHelper(getContext());
        classList = new ArrayList<>();

        setupRecyclerView();
        setupClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClasses();
    }

    private void setupRecyclerView() {
        // Fix: Create OnClassClickListener properly
        DayScheduleAdapter.OnClassClickListener clickListener = new DayScheduleAdapter.OnClassClickListener() {
            @Override
            public void onClassClick(ClassItem classItem) {
                showEditDeleteDialog(classItem);
            }
        };

        adapter = new DayScheduleAdapter(classList, clickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> showAddClassDialog());
    }

    private void loadClasses() {
        classList.clear();
        if (currentDay != null) {
            classList.addAll(databaseHelper.getClassesByDay(currentDay));
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddClassDialog() {
        showClassDialog(null);
    }

    private void showEditDeleteDialog(ClassItem classItem) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showClassDialog(classItem);
                    } else {
                        deleteClass(classItem);
                    }
                })
                .show();
    }

    private void showClassDialog(ClassItem classItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);

        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);

        // Set up day spinner
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Pre-select current day for this fragment
        if (currentDay != null) {
            for (int i = 0; i < days.length; i++) {
                if (days[i].equals(currentDay)) {
                    spinnerDay.setSelection(i);
                    break;
                }
            }
        }

        // Pre-fill if editing
        if (classItem != null) {
            for (int i = 0; i < days.length; i++) {
                if (days[i].equals(classItem.getDay())) {
                    spinnerDay.setSelection(i);
                    break;
                }
            }
            etStartTime.setText(classItem.getStartTime());
            etEndTime.setText(classItem.getEndTime());
            etSubject.setText(classItem.getSubject());
            etLocation.setText(classItem.getLocation());
        }

        builder.setView(dialogView)
                .setTitle(classItem == null ? "Add Class" : "Edit Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String day = spinnerDay.getSelectedItem().toString();
                    String startTime = etStartTime.getText().toString();
                    String endTime = etEndTime.getText().toString();
                    String subject = etSubject.getText().toString();
                    String location = etLocation.getText().toString();

                    if (!startTime.isEmpty() && !endTime.isEmpty() && !subject.isEmpty()) {
                        if (classItem == null) {
                            // Add new class
                            ClassItem newClass = new ClassItem(day, startTime, endTime, subject, location);
                            databaseHelper.addClass(newClass);
                        } else {
                            // Update existing class
                            classItem.setDay(day);
                            classItem.setStartTime(startTime);
                            classItem.setEndTime(endTime);
                            classItem.setSubject(subject);
                            classItem.setLocation(location);
                            databaseHelper.updateClass(classItem);
                        }
                        loadClasses();
                    } else {
                        Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClass(ClassItem classItem) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    databaseHelper.deleteClass(classItem.getId());
                    loadClasses();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}