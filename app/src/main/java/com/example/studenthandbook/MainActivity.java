package com.example.studenthandbook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // Set up bottom navigation
        setupBottomNavigation();

        // Set up About University cards
        setupAboutUniversityCards();

        // Set up Administration and Staff cards
        setupAdministrationCards();

        // Set up Academic Information cards
        setupAcademicCards();

        // Set up Student Life and Governance cards
        setupStudentLifeCards();

        // Set up Institutional Services cards
        setupInstitutionalCards();
    }

    private void setupBottomNavigation() {
        ImageButton navBookmark = findViewById(R.id.nav_bookmark);
        ImageButton navMap = findViewById(R.id.nav_map);
        ImageButton navHome = findViewById(R.id.nav_home);
        ImageButton navSchedule = findViewById(R.id.schedule);

        if (navBookmark != null) {
            navBookmark.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(intent);
            });
        }

        if (navMap != null) {
            navMap.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BulsuMapActivity.class);
                startActivity(intent);
            });
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                // Already on home, could scroll to top or refresh
            });
        }

        if (navSchedule != null) {
            navSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupAboutUniversityCards() {
        CardView historyBulsu = findViewById(R.id.history_bulsu);
        CardView cardContactUs = findViewById(R.id.card_contact_us);

        if (historyBulsu != null) {
            historyBulsu.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, HistoryofBulsu.class);
                startActivity(intent);
            });
        }

        if (cardContactUs != null) {
            cardContactUs.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, HymnMarch.class);
                startActivity(intent);
            });
        }
    }

    private void setupAdministrationCards() {
        CardView cardStudentCouncil = findViewById(R.id.card_student_council);
        CardView cardStudentAffairs = findViewById(R.id.card_student_affairs);
        CardView cardLibrary = findViewById(R.id.card_library);

        if (cardStudentCouncil != null) {
            cardStudentCouncil.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StudentOrg.class);
                startActivity(intent);
            });
        }

        if (cardStudentAffairs != null) {
            cardStudentAffairs.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StudentAffairs.class);
                startActivity(intent);
            });
        }

        if (cardLibrary != null) {
            cardLibrary.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InstitutionalServices.class);
                startActivity(intent);
            });
        }
    }

    private void setupAcademicCards() {
        CardView cardScholarships = findViewById(R.id.card_scholarships);
        CardView cardFinancialAid = findViewById(R.id.card_financial_aid);
        CardView cardAcademicCalendar = findViewById(R.id.card_academic_calendar);
        CardView cardEvents = findViewById(R.id.card_events);

        if (cardScholarships != null) {
            cardScholarships.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, GeneralProvisions.class);
                startActivity(intent);
            });
        }

        if (cardFinancialAid != null) {
            cardFinancialAid.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DownloadableForms.class);
                startActivity(intent);
            });
        }

        if (cardAcademicCalendar != null) {
            cardAcademicCalendar.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ExternalAffairs.class);
                startActivity(intent);
            });
        }

        if (cardEvents != null) {
            cardEvents.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InstitutionalServices.class);
                startActivity(intent);
            });
        }
    }

    private void setupStudentLifeCards() {
        CardView cardStudentOrgs = findViewById(R.id.card_student_orgs);
        CardView cardCampusActivities = findViewById(R.id.card_campus_activities);
        CardView cardStudentRights = findViewById(R.id.card_student_rights);
        CardView cardPolicies = findViewById(R.id.card_policies);
        CardView cardCodeConduct = findViewById(R.id.card_code_conduct);

        if (cardStudentOrgs != null) {
            cardStudentOrgs.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StudentOrg.class);
                startActivity(intent);
            });
        }

        if (cardCampusActivities != null) {
            cardCampusActivities.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StudentAffairs.class);
                startActivity(intent);
            });
        }

        if (cardStudentRights != null) {
            cardStudentRights.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ExternalAffairs.class);
                startActivity(intent);
            });
        }

        if (cardPolicies != null) {
            cardPolicies.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InstitutionalServices.class);
                startActivity(intent);
            });
        }

        if (cardCodeConduct != null) {
            cardCodeConduct.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StudentOrg.class);
                startActivity(intent);
            });
        }
    }

    private void setupInstitutionalCards() {
        CardView cardCampusMap = findViewById(R.id.card_campus_map);
        CardView cardInstitutionalServices = findViewById(R.id.card_institutional_services);

        if (cardCampusMap != null) {
            cardCampusMap.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DownloadableForms.class);
                startActivity(intent);
            });
        }

        if (cardInstitutionalServices != null) {
            cardInstitutionalServices.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, InstitutionalServices.class);
                startActivity(intent);
            });
        }
    }
}