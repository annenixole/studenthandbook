package com.example.studenthandbook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final String[] days;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String[] days) {
        super(fragmentActivity);
        this.days = days;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new WeeklyTimetableFragment(); // Use the new timetable fragment
        } else {
            return DayScheduleFragment.newInstance(days[position - 1]);
        }
    }

    @Override
    public int getItemCount() {
        return days.length + 1; // +1 for Weekly view at position 0
    }
}