package com.example.studenthandbook;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class WeeklyTimetableFragment extends Fragment {

    private TableLayout timetableGrid;
    private TableLayout timeColumnGrid;
    private DatabaseHelper databaseHelper;
    private FloatingActionButton fabAdd;
    private ScrollView timeColumnScroll;
    private ScrollView dayColumnsScroll;

    // Continuous hours from 7 AM to 8 PM
    private final String[] timeSlots = {
            "7:00", "8:00", "9:00", "10:00", "11:00", "12:00",
            "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
    };

    private final String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

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

    // Class colors array for differentiation
    private final int[] classBgColors = {
            R.color.class_color_1, R.color.class_color_2, R.color.class_color_3,
            R.color.class_color_4, R.color.class_color_5, R.color.class_color_6,
            R.color.class_color_7, R.color.class_color_8, R.color.class_color_9,
            R.color.class_color_10
    };

    private final int[] classTextColors = {
            R.color.class_text_1, R.color.class_text_2, R.color.class_text_3,
            R.color.class_text_4, R.color.class_text_5, R.color.class_text_6,
            R.color.class_text_7, R.color.class_text_8, R.color.class_text_9,
            R.color.class_text_10
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_timetable, container, false);

        timetableGrid = view.findViewById(R.id.timetableGrid);
        timeColumnGrid = view.findViewById(R.id.timeColumnGrid);
        timeColumnScroll = view.findViewById(R.id.timeColumnScroll);
        dayColumnsScroll = view.findViewById(R.id.dayColumnsScroll);
        fabAdd = view.findViewById(R.id.fabAdd);

        if (getContext() != null) {
        databaseHelper = new DatabaseHelper(getContext());
        }

        if (timetableGrid != null && timeColumnGrid != null) {
        setupTimetable();
            setupScrollSync();
        }

        if (fabAdd != null) {
        setupClickListeners();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshTimetable();
    }

    private void setupTimetable() {
        if (timetableGrid == null || timeColumnGrid == null || getContext() == null) {
            return;
        }

        // Clear existing rows first
        timetableGrid.removeAllViews();
        timeColumnGrid.removeAllViews();

        // Add header rows (separate for time column and day columns)
        addTimeColumnHeader();
        addDayColumnsHeader();

        // Add time slot rows
        for (int i = 0; i < timeSlots.length - 1; i++) {
            addTimeSlotRow(i);
        }
    }

    private void setupScrollSync() {
        if (timeColumnScroll == null || dayColumnsScroll == null) {
            return;
        }

        // Sync vertical scrolling between time column and day columns
        dayColumnsScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = dayColumnsScroll.getScrollY();
            if (timeColumnScroll.getScrollY() != scrollY) {
                timeColumnScroll.scrollTo(0, scrollY);
            }
        });

        timeColumnScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollY = timeColumnScroll.getScrollY();
            if (dayColumnsScroll.getScrollY() != scrollY) {
                dayColumnsScroll.scrollTo(0, scrollY);
            }
        });
    }

    private void addTimeColumnHeader() {
        TableRow headerRow = new TableRow(getContext());

        // Time header column (fixed)
        TextView timeHeader = createCell("TIME", true, true);
        timeHeader.setLayoutParams(new TableRow.LayoutParams(
                dpToPx(58), TableRow.LayoutParams.WRAP_CONTENT
        ));
        headerRow.addView(timeHeader);

        // Ensure header row has consistent height
        TableLayout.LayoutParams headerParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
        );
        headerRow.setLayoutParams(headerParams);
        timeColumnGrid.addView(headerRow);
    }

    private void addDayColumnsHeader() {
        TableRow headerRow = new TableRow(getContext());

        // Day headers - set fixed width to match day columns
        String[] dayShort = {"MON", "TUE", "WED", "THU", "FRI", "SAT"};
        for (String day : dayShort) {
            TextView dayCell = createCell(day, true, true);
            TableRow.LayoutParams cellParams = new TableRow.LayoutParams(
                    dpToPx(115), TableRow.LayoutParams.WRAP_CONTENT
            );
            cellParams.weight = 0; // No weight - use fixed width
            dayCell.setLayoutParams(cellParams);
            headerRow.addView(dayCell);
        }

        // Ensure header row has consistent height
        TableLayout.LayoutParams headerParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
        );
        headerRow.setLayoutParams(headerParams);
        timetableGrid.addView(headerRow);
    }

    private void addTimeSlotRow(int slotIndex) {
        // Create time column row (fixed)
        TableRow timeRow = new TableRow(getContext());
        String startTime = timeSlots[slotIndex];
        String endTime = timeSlots[slotIndex + 1];

        LinearLayout timeLayout = new LinearLayout(getContext());
        timeLayout.setOrientation(LinearLayout.VERTICAL);
        // Match exact height of day cells: 70dp minimum height
        TableRow.LayoutParams timeLayoutParams = new TableRow.LayoutParams(
                dpToPx(57), dpToPx(70) // Fixed height matching day cell minHeight
        );
        timeLayoutParams.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        timeLayout.setLayoutParams(timeLayoutParams);
        timeLayout.setGravity(android.view.Gravity.CENTER);
        timeLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8)); // Match day cell padding
        if (getContext() != null) {
            timeLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_green));
        }

        TextView startTimeView = new TextView(getContext());
        startTimeView.setText(formatTimeForDisplay(startTime));
        if (getContext() != null) {
            startTimeView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        startTimeView.setTextSize(9);
        startTimeView.setGravity(android.view.Gravity.CENTER);
        startTimeView.setLineSpacing(0, 0.8f);
        startTimeView.setPadding(dpToPx(2), 0, dpToPx(2), 0);

        TextView toView = new TextView(getContext());
        toView.setText("-");
        if (getContext() != null) {
            toView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        toView.setTextSize(6);
        toView.setGravity(android.view.Gravity.CENTER);
        toView.setPadding(0, dpToPx(-2), 0, dpToPx(-2));

        TextView endTimeView = new TextView(getContext());
        endTimeView.setText(formatTimeForDisplay(endTime));
        if (getContext() != null) {
            endTimeView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        endTimeView.setTextSize(9);
        endTimeView.setGravity(android.view.Gravity.CENTER);
        endTimeView.setLineSpacing(0, 0.8f);
        endTimeView.setPadding(dpToPx(2), 0, dpToPx(2), 0);

        timeLayout.addView(startTimeView);
        timeLayout.addView(toView);
        timeLayout.addView(endTimeView);
        timeRow.addView(timeLayout);

        // Ensure time row has same height as day row - match day row height naturally
        TableLayout.LayoutParams timeRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
        );
        timeRow.setLayoutParams(timeRowParams);
        timeRow.setMinimumHeight(dpToPx(0)); // Let it match naturally with day cells
        timeColumnGrid.addView(timeRow);

        // Create day columns row (scrollable)
        TableRow dayRow = new TableRow(getContext());
        for (String day : days) {
            TextView dayCell = createCell("", false, false);
            dayCell.setTag(day + "_" + slotIndex);
            dayRow.addView(dayCell);
        }

        // Ensure day row has same height as time row - account for cell height + margins
        // Day cells: 70dp minHeight + 2dp top margin + 2dp bottom margin = 74dp total
        TableLayout.LayoutParams dayRowParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT
        );
        dayRow.setLayoutParams(dayRowParams);
        dayRow.setMinimumHeight(dpToPx(0)); // Match day cell minHeight (70dp) + top/bottom margins (4dp)
        timetableGrid.addView(dayRow);
    }

    private TextView createCell(String text, boolean isHeader, boolean isTimeHeader) {
        TextView cell = new TextView(getContext());

        // Set minimum width for day columns to prevent text cutoff
        // Use fixed width for day cells instead of weight to allow horizontal scrolling
        int minWidth = isHeader || isTimeHeader ? TableRow.LayoutParams.WRAP_CONTENT : dpToPx(115);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                isHeader || isTimeHeader ? TableRow.LayoutParams.WRAP_CONTENT : minWidth,
                TableRow.LayoutParams.MATCH_PARENT
        );

        if (!isHeader && !isTimeHeader) {
            params.weight = 0; // No weight for day cells - use fixed width instead
        }

        params.setMargins(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
        cell.setLayoutParams(params);

        cell.setText(text);
        cell.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
        cell.setGravity(android.view.Gravity.CENTER);
        cell.setTextSize(11);
        cell.setLineSpacing(0, 1.0f);

        if (getContext() != null) {
        if (isHeader) {
                cell.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_green));
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            cell.setTypeface(cell.getTypeface(), android.graphics.Typeface.BOLD);
                cell.setTextSize(12);
        } else if (isTimeHeader) {
                cell.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.primary_green));
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else {
                cell.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_gray));
                cell.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                cell.setMinHeight(dpToPx(70));
                cell.setMinWidth(minWidth); // Ensure minimum width
            }
        }

        return cell;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void refreshTimetable() {
        if (databaseHelper == null || timetableGrid == null || getContext() == null) {
            return;
        }

        try {
        List<ClassItem> allClasses = databaseHelper.getAllClasses();

            if (allClasses == null) {
                return;
            }

            // Clear all day column cells first - reset margins and heights
            // Skip header row (index 0) and process data rows
        for (int i = 1; i < timetableGrid.getChildCount(); i++) {
            TableRow row = (TableRow) timetableGrid.getChildAt(i);
                if (row != null) {
                    // Day columns now start at index 0 (no time column in these rows)
                    for (int j = 0; j < row.getChildCount(); j++) {
                        View child = row.getChildAt(j);
                        if (child instanceof TextView) {
                            TextView cell = (TextView) child;
                cell.setText("");
                            cell.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_gray));
                cell.setOnClickListener(null);
                            cell.setMinHeight(dpToPx(64));
                            cell.setMinWidth(dpToPx(115)); // Maintain minimum width for day columns

                            // Reset margins to default and ensure width
                            TableRow.LayoutParams params = (TableRow.LayoutParams) cell.getLayoutParams();
                            if (params != null) {
                                params.width = dpToPx(115); // Set fixed width for day columns
                                params.weight = 0; // No weight - use fixed width
                                params.setMargins(dpToPx(3), dpToPx(3), dpToPx(3), dpToPx(3));
                                cell.setLayoutParams(params);
                            }
                        }
                    }
            }
        }

        // Fill cells with classes
        for (ClassItem classItem : allClasses) {
            placeClassInTimetable(classItem);
            }
        } catch (Exception e) {
            // Error handled silently - timetable will remain in previous state
        }
    }

    private void placeClassInTimetable(ClassItem classItem) {
        if (classItem == null || timetableGrid == null || getContext() == null) {
            return;
        }

        String startTime = classItem.getStartTime();
        String endTime = classItem.getEndTime();
        String day = classItem.getDay();

        if (startTime == null || endTime == null || day == null) {
            return;
        }

        // Find which time slots this class spans
        int startSlot = findTimeSlotIndex(startTime);
        int endSlot = findTimeSlotIndex(endTime);

        if (startSlot != -1 && endSlot != -1 && startSlot <= endSlot) {
            int dayIndex = getDayIndex(day);

            if (dayIndex != -1) {
                int spanRows = endSlot - startSlot;
                if (spanRows < 1) spanRows = 1;

                // Place class content only in the first (starting) row
                int startRowIndex = startSlot + 1; // +1 for header row

                if (startRowIndex < timetableGrid.getChildCount()) {
                    View rowView = timetableGrid.getChildAt(startRowIndex);
                    if (rowView instanceof TableRow) {
                        TableRow startRow = (TableRow) rowView;
                        if (dayIndex < startRow.getChildCount()) {
                            View cellView = startRow.getChildAt(dayIndex); // Day columns now start at index 0
                            if (cellView instanceof TextView) {
                                TextView firstCell = (TextView) cellView;

                                // Calculate color once for all cells in this class
                                int classId = classItem.getId() > 0 ? classItem.getId() : Math.abs(classItem.hashCode());
                                int classColorIndex = Math.abs(classId % classBgColors.length);
                                int classBgColor = classBgColors[classColorIndex];
                                int classTextColor = classTextColors[classColorIndex];

                                // Build content for first cell - always put full subject on first row
                                String firstCellText = "";
                                if (classItem.getSubject() != null) {
                                    firstCellText = classItem.getSubject(); // Full subject title, no splitting
                                }

                                firstCell.setText(firstCellText);
                                if (getContext() != null) {
                                    firstCell.setBackgroundColor(ContextCompat.getColor(getContext(), classBgColor));
                                    firstCell.setTextColor(ContextCompat.getColor(getContext(), classTextColor));
                                }
                                firstCell.setMaxLines(0); // No limit on lines - allow full text
                                firstCell.setEllipsize(null); // Don't truncate text
                                firstCell.setSingleLine(false); // Allow multiple lines
                                firstCell.setGravity(android.view.Gravity.CENTER | android.view.Gravity.TOP);
                                firstCell.setTextSize(13);
                                firstCell.setTypeface(firstCell.getTypeface(), android.graphics.Typeface.BOLD);
                                firstCell.setLineSpacing(dpToPx(2), 1.1f);
                                firstCell.setPadding(dpToPx(10), dpToPx(12), dpToPx(10), dpToPx(12));
                                firstCell.setMinWidth(dpToPx(115)); // Ensure minimum width for proper text display

                                // Remove bottom margin from first cell to connect with span cells (if spanning)
                                if (spanRows > 1) {
                                    TableRow.LayoutParams firstParams = (TableRow.LayoutParams) firstCell.getLayoutParams();
                                    if (firstParams != null) {
                                        firstParams.width = dpToPx(115); // Maintain fixed width
                                        firstParams.weight = 0; // No weight
                                        firstParams.setMargins(firstParams.leftMargin, firstParams.topMargin,
                                                               firstParams.rightMargin, 0);
                                        firstCell.setLayoutParams(firstParams);
                                    }
                                } else {
                                    // Ensure width even for single row cells
                                    TableRow.LayoutParams firstParams = (TableRow.LayoutParams) firstCell.getLayoutParams();
                                    if (firstParams != null) {
                                        firstParams.width = dpToPx(115);
                                        firstParams.weight = 0;
                                        firstCell.setLayoutParams(firstParams);
                                    }
                                }

                                // Add click listener for edit/delete
                                firstCell.setOnClickListener(v -> showEditDeleteDialog(classItem));

                                // Distribute content across spanning rows (for multi-row classes)
                                // Row 1: Full subject (already set above)
                                // Middle rows (if any): Instructor
                                // Last row: Room/Location
                                if (spanRows >= 2) {
                                    int spanIndex = 0; // Track which spanning row we're on (0-based, starting from row 2)
                                    int lastSpanIndex = spanRows - 1; // Last row index (0-based)

                                    for (int slot = startSlot + 1; slot < endSlot && slot < timeSlots.length - 1; slot++) {
                                        int rowIndex = slot + 1; // +1 for header row

                                        if (rowIndex < timetableGrid.getChildCount()) {
                                            View spanRowView = timetableGrid.getChildAt(rowIndex);
                                            if (spanRowView instanceof TableRow) {
                                                TableRow row = (TableRow) spanRowView;
                                                if (dayIndex < row.getChildCount()) {
                                                    View spanView = row.getChildAt(dayIndex);
                                                    if (spanView instanceof TextView) {
                                                        TextView spanCell = (TextView) spanView;

                                                        String spanText = "";

                                                        // Last row: Room/Location
                                                        if (spanIndex == lastSpanIndex) {
                                                            if (classItem.getLocation() != null && !classItem.getLocation().isEmpty()) {
                                                                spanText = classItem.getLocation();
                                                            }
                                                        } else if (spanRows > 2) {
                                                            // Middle rows: Instructor (show on first middle row if instructor exists)
                                                            if (spanIndex == 0 && classItem.getInstructor() != null && !classItem.getInstructor().isEmpty()) {
                                                                spanText = classItem.getInstructor();
                                                            }
                                                        } else {
                                                            // spanRows == 2: Row 2 can be instructor or room
                                                            if (classItem.getInstructor() != null && !classItem.getInstructor().isEmpty()) {
                                                                spanText = classItem.getInstructor();
                                                            } else if (classItem.getLocation() != null && !classItem.getLocation().isEmpty()) {
                                                                spanText = classItem.getLocation();
                                                            }
                                                        }

                                                        // Use same color as first cell
                                                        // Only set if we have text, otherwise keep cell empty for visual continuity
                                                        if (!spanText.isEmpty()) {
                                                            spanCell.setText(spanText);
                                                            if (getContext() != null) {
                                                                spanCell.setBackgroundColor(ContextCompat.getColor(getContext(), classBgColor));
                                                                spanCell.setTextColor(ContextCompat.getColor(getContext(), classTextColor));
                                                            }
                                                            spanCell.setMaxLines(0); // No limit on lines - allow full text
                                                            spanCell.setEllipsize(null); // Don't truncate text
                                                            spanCell.setSingleLine(false); // Allow multiple lines
                                                            spanCell.setGravity(android.view.Gravity.CENTER | android.view.Gravity.TOP);
                                                            spanCell.setTextSize(12);
                                                            spanCell.setTypeface(spanCell.getTypeface(), android.graphics.Typeface.NORMAL);
                                                            spanCell.setLineSpacing(dpToPx(2), 1.1f);
                                                            spanCell.setPadding(dpToPx(10), dpToPx(12), dpToPx(10), dpToPx(12));
                                                            spanCell.setMinWidth(dpToPx(115)); // Ensure minimum width
                                                        } else {
                                                            // Empty cell but same background for visual continuity
                                                            spanCell.setText("");
                                                            if (getContext() != null) {
                                                                spanCell.setBackgroundColor(ContextCompat.getColor(getContext(), classBgColor));
                                                            }
                                                            spanCell.setMinWidth(dpToPx(115)); // Maintain width even when empty
                                                        }

                                                        // Remove top and bottom margins to eliminate gaps and maintain width
                                                        TableRow.LayoutParams spanParams = (TableRow.LayoutParams) spanCell.getLayoutParams();
                                                        if (spanParams != null) {
                                                            spanParams.width = dpToPx(115); // Maintain fixed width
                                                            spanParams.weight = 0; // No weight
                                                            spanParams.setMargins(spanParams.leftMargin, 0,
                                                                                 spanParams.rightMargin, 0);
                                                            spanCell.setLayoutParams(spanParams);
                                                        }

                                                        // Make span cell also clickable
                                                        spanCell.setOnClickListener(v -> showEditDeleteDialog(classItem));

                                                        spanIndex++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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

    /**
     * Convert 24-hour time format to 12-hour format with AM/PM
     * @param time24 String in format "HH:mm" (e.g., "13:30")
     * @return String in format "H:mm AM/PM" (e.g., "1:30 PM")
     */
    private String formatTimeForDisplay(String time24) {
        if (time24 == null || time24.isEmpty()) {
            return "";
        }

        try {
            String[] parts = time24.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                String minutes = parts[1];
                String period = "AM";

                if (hour == 0) {
                    hour = 12; // Midnight
                } else if (hour == 12) {
                    period = "PM"; // Noon
                } else if (hour > 12) {
                    hour -= 12;
                    period = "PM";
                }

                return hour + ":" + minutes + " " + period;
            }
        } catch (NumberFormatException e) {
            // If parsing fails, return original time
            return time24;
        }

        return time24;
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> showAddClassDialog());
    }

    private void showAddClassDialog() {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);

        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        Spinner spinnerStartTime = dialogView.findViewById(R.id.spinnerStartTime);
        Spinner spinnerEndTime = dialogView.findViewById(R.id.spinnerEndTime);
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etInstructor = dialogView.findViewById(R.id.etInstructor);

        // Setup day spinner
        Context context = getContext();
        if (context == null) {
            return;
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Setup time spinners with display format (AM/PM)
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeOptionsDisplay);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartTime.setAdapter(timeAdapter);
        spinnerEndTime.setAdapter(timeAdapter);

        // Set default times (8:00 AM and 10:00 AM)
        int defaultStartIndex = findTimeOptionIndex("8:00");
        int defaultEndIndex = findTimeOptionIndex("10:00");
        if (defaultStartIndex != -1 && defaultStartIndex < timeOptionsDisplay.length) {
            spinnerStartTime.setSelection(defaultStartIndex);
        }
        if (defaultEndIndex != -1 && defaultEndIndex < timeOptionsDisplay.length) {
            spinnerEndTime.setSelection(defaultEndIndex);
        }

        builder.setView(dialogView)
                .setTitle("Add Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String day = spinnerDay.getSelectedItem().toString();
                    // Convert display time back to 24-hour format for storage
                    int startIndex = spinnerStartTime.getSelectedItemPosition();
                    int endIndex = spinnerEndTime.getSelectedItemPosition();
                    String startTime = timeOptions[startIndex]; // Use 24-hour format from timeOptions
                    String endTime = timeOptions[endIndex]; // Use 24-hour format from timeOptions
                    String subject = etSubject.getText().toString().trim();
                    String instructor = etInstructor.getText().toString().trim();

                    // Validate end time is after start time

                    if (subject.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter course name", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (endIndex <= startIndex) {
                        Toast.makeText(getContext(), "End time must be after start time", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ClassItem newClass = new ClassItem(day, startTime, endTime, subject, "");
                    newClass.setInstructor(instructor);
                        long result = databaseHelper.addClass(newClass);

                        if (result != -1) {
                            refreshTimetable();
                            Toast.makeText(getContext(), "Class added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to add class", Toast.LENGTH_SHORT).show();
                    }
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

    private void showEditDeleteDialog(ClassItem classItem) {
        if (getContext() == null) {
            return;
        }
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
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_class, null);

        Spinner spinnerDay = dialogView.findViewById(R.id.spinnerDay);
        Spinner spinnerStartTime = dialogView.findViewById(R.id.spinnerStartTime);
        Spinner spinnerEndTime = dialogView.findViewById(R.id.spinnerEndTime);
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etInstructor = dialogView.findViewById(R.id.etInstructor);

        // Setup day spinner
        Context context = getContext();
        if (context == null) {
            return;
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Setup time spinners with display format (AM/PM)
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, timeOptionsDisplay);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStartTime.setAdapter(timeAdapter);
        spinnerEndTime.setAdapter(timeAdapter);

        // Pre-fill values
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

        builder.setView(dialogView)
                .setTitle("Edit Class")
                .setPositiveButton("Save", (dialog, which) -> {
                    String day = spinnerDay.getSelectedItem().toString();
                    // Convert display time back to 24-hour format for storage
                    int startIdx = spinnerStartTime.getSelectedItemPosition();
                    int endIdx = spinnerEndTime.getSelectedItemPosition();
                    String editStartTime = timeOptions[startIdx]; // Use 24-hour format from timeOptions
                    String editEndTime = timeOptions[endIdx]; // Use 24-hour format from timeOptions
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

                        classItem.setDay(day);
                    classItem.setStartTime(editStartTime);
                    classItem.setEndTime(editEndTime);
                        classItem.setSubject(subject);
                    classItem.setInstructor(instructor);
                    classItem.setLocation(""); // Remove location
                        int result = databaseHelper.updateClass(classItem);

                        if (result > 0) {
                            refreshTimetable();
                            Toast.makeText(getContext(), "Class updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteClass(ClassItem classItem) {
        if (getContext() == null) {
            return;
        }
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result = databaseHelper.deleteClass(classItem.getId());
                    if (result > 0) {
                        refreshTimetable();
                        Toast.makeText(getContext(), "Class deleted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}