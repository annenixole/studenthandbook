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
    
    // Available time options for spinners (30-minute intervals) - stored as 24-hour for matching
    private final String[] timeOptions = {
            "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00"
    };
    
    // Display versions with AM/PM for spinners
    private final String[] timeOptionsDisplay = {
            "7:00 AM", "7:30 AM", "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
            "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM", "2:00 PM", "2:30 PM",
            "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM", "5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM",
            "7:00 PM", "7:30 PM", "8:00 PM"
    };
    
    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

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
        DayScheduleAdapter.OnClassClickListener clickListener = classItem -> showEditDeleteDialog(classItem);

        adapter = new DayScheduleAdapter(classList, clickListener);
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
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
        if (getContext() == null) return;
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);

        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        Spinner spinnerStartTime = dialogView.findViewById(R.id.spinnerStartTime);
        Spinner spinnerEndTime = dialogView.findViewById(R.id.spinnerEndTime);
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etInstructor = dialogView.findViewById(R.id.etInstructor);

        // Set up day spinner
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Setup time spinners with display format (AM/PM)
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, timeOptionsDisplay);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartTime.setAdapter(timeAdapter);
        spinnerEndTime.setAdapter(timeAdapter);

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
            
            int startIndex = findTimeOptionIndex(classItem.getStartTime());
            int endIndex = findTimeOptionIndex(classItem.getEndTime());
            if (startIndex != -1 && startIndex < timeOptionsDisplay.length) {
                spinnerStartTime.setSelection(startIndex);
            }
            if (endIndex != -1 && endIndex < timeOptionsDisplay.length) {
                spinnerEndTime.setSelection(endIndex);
            }
            
            etSubject.setText(classItem.getSubject());
            etInstructor.setText(classItem.getInstructor() != null ? classItem.getInstructor() : "");
        } else {
            // Set default times (8:00 AM and 10:00 AM)
            int defaultStartIndex = findTimeOptionIndex("8:00");
            int defaultEndIndex = findTimeOptionIndex("10:00");
            if (defaultStartIndex != -1 && defaultStartIndex < timeOptionsDisplay.length) {
                spinnerStartTime.setSelection(defaultStartIndex);
            }
            if (defaultEndIndex != -1 && defaultEndIndex < timeOptionsDisplay.length) {
                spinnerEndTime.setSelection(defaultEndIndex);
            }
        }

        builder.setView(dialogView)
                .setTitle(classItem == null ? "Add Class" : "Edit Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String day = spinnerDay.getSelectedItem().toString();
                    // Convert display time back to 24-hour format for storage
                    int startIdx = spinnerStartTime.getSelectedItemPosition();
                    int endIdx = spinnerEndTime.getSelectedItemPosition();
                    String startTime = timeOptions[startIdx]; // Use 24-hour format from timeOptions
                    String endTime = timeOptions[endIdx]; // Use 24-hour format from timeOptions
                    String subject = etSubject.getText().toString().trim();
                    String instructor = etInstructor.getText().toString().trim();
                    
                    if (subject.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter course name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    if (endIdx <= startIdx) {
                        Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (classItem == null) {
                        // Add new class
                        ClassItem newClass = new ClassItem(day, startTime, endTime, subject, "");
                        newClass.setInstructor(instructor);
                        databaseHelper.addClass(newClass);
                    } else {
                        // Update existing class
                        classItem.setDay(day);
                        classItem.setStartTime(startTime);
                        classItem.setEndTime(endTime);
                        classItem.setSubject(subject);
                        classItem.setInstructor(instructor);
                        classItem.setLocation(""); // Remove location
                        databaseHelper.updateClass(classItem);
                    }
                    loadClasses();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private int findTimeOptionIndex(String time) {
        for (int i = 0; i < timeOptions.length; i++) {
            if (timeOptions[i].equals(time)) {
                return i;
            }
        }
        return -1;
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