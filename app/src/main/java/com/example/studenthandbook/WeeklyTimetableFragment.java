package com.example.studenthandbook;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeeklyTimetableFragment extends Fragment {

    private TableLayout timetableGrid;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAdd;
    private TextView tvCurrentClass, tvUpcomingClass;

    // Continuous hours from 7 AM to 6 PM
    private final String[] timeSlots = {
            "7:00", "8:00", "9:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00"
    };

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_timetable, container, false);

        timetableGrid = view.findViewById(R.id.timetableGrid);
        fabAdd = view.findViewById(R.id.fabAdd);
        tvCurrentClass = view.findViewById(R.id.tvCurrentClass);
        tvUpcomingClass = view.findViewById(R.id.tvUpcomingClass);
        databaseHelper = new DatabaseHelper(getContext());

        setupTimetable();
        setupClickListeners();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTimetable();
        updateCurrentAndUpcomingClasses();
    }

    private void setupTimetable() {
        // Clear existing rows first
        timetableGrid.removeAllViews();

        // Add header row
        addHeaderRow();

        // Add time slot rows
        for (int i = 0; i < timeSlots.length - 1; i++) {
            addTimeSlotRow(i);
        }
    }

    private void addHeaderRow() {
        TableRow headerRow = new TableRow(getContext());

        // Time header (smaller column)
        TextView timeHeader = createCell("TIME", true, true);
        timeHeader.setLayoutParams(new TableRow.LayoutParams(
                dpToPx(70), TableRow.LayoutParams.WRAP_CONTENT
        ));
        headerRow.addView(timeHeader);

        // Day headers
        String[] dayShort = {"MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (String day : dayShort) {
            TextView dayCell = createCell(day, true, true);
            headerRow.addView(dayCell);
        }

        timetableGrid.addView(headerRow);
    }

    private void addTimeSlotRow(int slotIndex) {
        TableRow row = new TableRow(getContext());

        // Time cell with vertical "to" format
        String startTime = timeSlots[slotIndex];
        String endTime = timeSlots[slotIndex + 1];

        LinearLayout timeLayout = new LinearLayout(getContext());
        timeLayout.setOrientation(LinearLayout.VERTICAL);
        timeLayout.setLayoutParams(new TableRow.LayoutParams(
                dpToPx(70), TableRow.LayoutParams.MATCH_PARENT
        ));
        timeLayout.setGravity(android.view.Gravity.CENTER);
        timeLayout.setPadding(dpToPx(2), dpToPx(4), dpToPx(2), dpToPx(4));
        timeLayout.setBackgroundColor(getResources().getColor(R.color.primary_green));

        TextView startTimeView = new TextView(getContext());
        startTimeView.setText(startTime);
        startTimeView.setTextColor(getResources().getColor(R.color.white));
        startTimeView.setTextSize(10);
        startTimeView.setGravity(android.view.Gravity.CENTER);

        TextView toView = new TextView(getContext());
        toView.setText("-");
        toView.setTextColor(getResources().getColor(R.color.white));
        toView.setTextSize(8);
        toView.setGravity(android.view.Gravity.CENTER);

        TextView endTimeView = new TextView(getContext());
        endTimeView.setText(endTime);
        endTimeView.setTextColor(getResources().getColor(R.color.white));
        endTimeView.setTextSize(10);
        endTimeView.setGravity(android.view.Gravity.CENTER);

        timeLayout.addView(startTimeView);
        timeLayout.addView(toView);
        timeLayout.addView(endTimeView);
        row.addView(timeLayout);

        // Day cells (initially empty)
        for (int i = 0; i < days.length; i++) {
            TextView dayCell = createCell("", false, false);
            dayCell.setTag(days[i] + "_" + slotIndex);
            row.addView(dayCell);
        }

        timetableGrid.addView(row);
    }

    private TextView createCell(String text, boolean isHeader, boolean isTimeHeader) {
        TextView cell = new TextView(getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, TableRow.LayoutParams.MATCH_PARENT, 1
        );
        params.setMargins(dpToPx(1), dpToPx(1), dpToPx(1), dpToPx(1));
        cell.setLayoutParams(params);

        cell.setText(text);
        cell.setPadding(dpToPx(2), dpToPx(4), dpToPx(2), dpToPx(4));
        cell.setGravity(android.view.Gravity.CENTER);
        cell.setTextSize(10);
        cell.setLineSpacing(0, 0.8f);

        if (isHeader) {
            cell.setBackgroundColor(getResources().getColor(R.color.primary_green));
            cell.setTextColor(getResources().getColor(R.color.white));
            cell.setTypeface(cell.getTypeface(), android.graphics.Typeface.BOLD);
        } else if (isTimeHeader) {
            cell.setBackgroundColor(getResources().getColor(R.color.primary_green));
            cell.setTextColor(getResources().getColor(R.color.white));
        } else {
            cell.setBackgroundColor(getResources().getColor(R.color.background_gray));
            cell.setTextColor(getResources().getColor(R.color.text_primary));
            cell.setMinHeight(dpToPx(60));
        }

        return cell;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void refreshTimetable() {
        List<ClassItem> allClasses = databaseHelper.getAllClasses();

        // Clear all cells first
        for (int i = 1; i < timetableGrid.getChildCount(); i++) {
            TableRow row = (TableRow) timetableGrid.getChildAt(i);
            for (int j = 1; j < row.getChildCount(); j++) {
                TextView cell = (TextView) row.getChildAt(j);
                cell.setText("");
                cell.setBackgroundColor(getResources().getColor(R.color.background_gray));
                cell.setOnClickListener(null);
            }
        }

        // Fill cells with classes
        for (ClassItem classItem : allClasses) {
            placeClassInTimetable(classItem);
        }
    }

    private void placeClassInTimetable(ClassItem classItem) {
        String startTime = classItem.getStartTime();
        String endTime = classItem.getEndTime();
        String day = classItem.getDay();

        // Find which time slots this class spans
        int startSlot = findTimeSlotIndex(startTime);
        int endSlot = findTimeSlotIndex(endTime);

        if (startSlot != -1 && endSlot != -1 && startSlot <= endSlot) {
            int dayIndex = getDayIndex(day);

            if (dayIndex != -1) {
                // Place class in all spanned time slots
                for (int slot = startSlot; slot < endSlot && slot < timeSlots.length - 1; slot++) {
                    int rowIndex = slot + 1; // +1 for header row

                    if (rowIndex < timetableGrid.getChildCount()) {
                        TableRow row = (TableRow) timetableGrid.getChildAt(rowIndex);
                        if (row != null && (dayIndex + 1) < row.getChildCount()) {
                            TextView cell = (TextView) row.getChildAt(dayIndex + 1); // +1 for time column

                            String cellText = classItem.getSubject();
                            if (classItem.getLocation() != null && !classItem.getLocation().isEmpty()) {
                                cellText += "\n" + classItem.getLocation();
                            }

                            cell.setText(cellText);
                            cell.setBackgroundColor(getResources().getColor(R.color.primary_yellow));
                            cell.setTextColor(getResources().getColor(R.color.text_primary));

                            // Add click listener for edit/delete
                            cell.setOnClickListener(v -> showEditDeleteDialog(classItem));
                        }
                    }
                }
            }
        }
    }

    private void updateCurrentAndUpcomingClasses() {
        String currentDay = getCurrentDay();
        String currentTime = getCurrentTime();

        List<ClassItem> todayClasses = databaseHelper.getClassesByDay(currentDay);

        ClassItem currentClass = null;
        ClassItem upcomingClass = null;

        for (ClassItem classItem : todayClasses) {
            if (isTimeBetween(currentTime, classItem.getStartTime(), classItem.getEndTime())) {
                currentClass = classItem;
            } else if (isTimeAfter(currentTime, classItem.getStartTime()) && upcomingClass == null) {
                upcomingClass = classItem;
            }
        }

        // Update current class card
        if (currentClass != null) {
            String currentText =  currentClass.getSubject() +
                    "\n" + currentClass.getStartTime() + " - " + currentClass.getEndTime() +
                    "\n " + (currentClass.getLocation() != null && !currentClass.getLocation().isEmpty() ?
                    currentClass.getLocation() : "No room specified");
            tvCurrentClass.setText(currentText);
            tvCurrentClass.setOnClickListener(v -> navigateToDay(currentDay));
        } else {
            tvCurrentClass.setText("No ongoing class\n Enjoy your free time!");
            tvCurrentClass.setOnClickListener(null);
        }

        // Update upcoming class card
        if (upcomingClass != null) {
            String upcomingText = " " + upcomingClass.getSubject() +
                    "\n" + upcomingClass.getStartTime() + " - " + upcomingClass.getEndTime() +
                    "\n" + (upcomingClass.getLocation() != null && !upcomingClass.getLocation().isEmpty() ?
                    upcomingClass.getLocation() : "No room specified");
            tvUpcomingClass.setText(upcomingText);
            tvUpcomingClass.setOnClickListener(v -> navigateToDay(currentDay));
        } else {
            tvUpcomingClass.setText("No upcoming class\n All done for today!");
            tvUpcomingClass.setOnClickListener(null);
        }
    }

    private String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Monday";
            case Calendar.TUESDAY: return "Tuesday";
            case Calendar.WEDNESDAY: return "Wednesday";
            case Calendar.THURSDAY: return "Thursday";
            case Calendar.FRIDAY: return "Friday";
            case Calendar.SATURDAY: return "Saturday";
            case Calendar.SUNDAY: return "Sunday";
            default: return "Monday";
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    private boolean isTimeBetween(String currentTime, String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date current = sdf.parse(currentTime);
            Date start = sdf.parse(normalizeTime(startTime));
            Date end = sdf.parse(normalizeTime(endTime));

            return current != null && start != null && end != null &&
                    current.after(start) && current.before(end);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTimeAfter(String currentTime, String targetTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date current = sdf.parse(currentTime);
            Date target = sdf.parse(normalizeTime(targetTime));

            return current != null && target != null && current.after(target);
        } catch (Exception e) {
            return false;
        }
    }

    private void navigateToDay(String day) {
        if (getActivity() instanceof ScheduleActivity) {
            ScheduleActivity scheduleActivity = (ScheduleActivity) getActivity();
            int dayIndex = getDayIndex(day) + 1; // +1 because position 0 is Weekly
            if (dayIndex >= 1 && dayIndex <= 6) {
                // Use the public method you created in ScheduleActivity
                scheduleActivity.navigateToTab(dayIndex);
            }
        }
    }

    private int findTimeSlotIndex(String time) {
        String normalizedTime = normalizeTime(time);

        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i].equals(normalizedTime)) {
                return i;
            }
        }

        for (int i = 0; i < timeSlots.length; i++) {
            if (normalizedTime.contains(timeSlots[i]) || timeSlots[i].contains(normalizedTime)) {
                return i;
            }
        }

        return -1;
    }

    private String normalizeTime(String time) {
        String normalized = time.trim()
                .replace("AM", "")
                .replace("PM", "")
                .replace("am", "")
                .replace("pm", "")
                .replace(" ", "")
                .toLowerCase();

        if (normalized.contains(":")) {
            String[] parts = normalized.split(":");
            if (parts.length == 2) {
                try {
                    int hour = Integer.parseInt(parts[0]);
                    if (time.toUpperCase().contains("PM") && hour != 12) {
                        hour += 12;
                    }
                    if (time.toUpperCase().contains("AM") && hour == 12) {
                        hour = 0;
                    }
                    return String.format("%02d:%s", hour, parts[1]);
                } catch (NumberFormatException e) {
                    return normalized;
                }
            }
        }

        return normalized;
    }

    private int getDayIndex(String day) {
        for (int i = 0; i < days.length; i++) {
            if (days[i].equalsIgnoreCase(day)) {
                return i;
            }
        }
        return -1;
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);

        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        etStartTime.setHint("e.g., 14:00 or 2:00 PM");
        etEndTime.setHint("e.g., 16:00 or 4:00 PM");

        builder.setView(dialogView)
                .setTitle("Add Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String day = spinnerDay.getSelectedItem().toString();
                    String startTime = etStartTime.getText().toString();
                    String endTime = etEndTime.getText().toString();
                    String subject = etSubject.getText().toString();
                    String location = etLocation.getText().toString();

                    if (!startTime.isEmpty() && !endTime.isEmpty() && !subject.isEmpty()) {
                        ClassItem newClass = new ClassItem(day, startTime, endTime, subject, location);
                        long result = databaseHelper.addClass(newClass);

                        if (result != -1) {
                            refreshTimetable();
                            updateCurrentAndUpcomingClasses();
                            Toast.makeText(getContext(), "Class added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add class", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditDeleteDialog(ClassItem classItem) {
        String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Action")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditClassDialog(classItem);
                    } else {
                        deleteClass(classItem);
                    }
                })
                .show();
    }

    private void showEditClassDialog(ClassItem classItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);

        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etLocation = dialogView.findViewById(R.id.etLocation);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

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

        builder.setView(dialogView)
                .setTitle("Edit Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String day = spinnerDay.getSelectedItem().toString();
                    String startTime = etStartTime.getText().toString();
                    String endTime = etEndTime.getText().toString();
                    String subject = etSubject.getText().toString();
                    String location = etLocation.getText().toString();

                    if (!startTime.isEmpty() && !endTime.isEmpty() && !subject.isEmpty()) {
                        classItem.setDay(day);
                        classItem.setStartTime(startTime);
                        classItem.setEndTime(endTime);
                        classItem.setSubject(subject);
                        classItem.setLocation(location);
                        int result = databaseHelper.updateClass(classItem);

                        if (result > 0) {
                            refreshTimetable();
                            updateCurrentAndUpcomingClasses();
                            Toast.makeText(getContext(), "Class updated successfully!", Toast.LENGTH_SHORT).show();
                        }
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
                    int result = databaseHelper.deleteClass(classItem.getId());
                    if (result > 0) {
                        refreshTimetable();
                        updateCurrentAndUpcomingClasses();
                        Toast.makeText(getContext(), "Class deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}