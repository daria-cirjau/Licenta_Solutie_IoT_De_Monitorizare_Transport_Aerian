package com.licenta.monitorizareavioane.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.favoritesdb.Favorites;
import com.licenta.monitorizareavioane.mainpage.MainActivity;
import com.licenta.monitorizareavioane.mainpage.userprofile.Points;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.mongodb.App;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.sync.MutableSubscriptionSet;
import io.realm.mongodb.sync.Subscription;
import io.realm.mongodb.sync.SyncConfiguration;

public class LogInActivity extends AppCompatActivity {

    private App app;
    private static User user;
    private Realm realm;
    private LogInFragment logInFragment;
    private static SyncConfiguration config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Realm.init(this);
        app = new App("applicenta-rkblt");
        logInFragment = new LogInFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.containerLogin,
                logInFragment).commit();
    }

    private void configure() {
        config = LogInActivity.configureRealm(app.currentUser());
        realm = Realm.getInstance(config);
    }


    public void goToRegisterOnClick(View view) {
        RegisterFragment registerFragment = new RegisterFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.containerLogin, registerFragment)
                .addToBackStack("login")
                .commit();
    }

    public void logInOnClick(View view) {
        EditText editTextEmail = findViewById(R.id.logInFragmentEditTextEmail);
        EditText editTextPassword = findViewById(R.id.logInFragmentEditTextPassword);
        if (!isLoginInfoValid(editTextEmail, editTextPassword)) {
            return;
        }

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        Credentials credentials = Credentials.emailPassword(email, password);
        AtomicReference<User> atomicReference = new AtomicReference<User>();
        app.loginAsync(credentials, it -> {
            if (it.isSuccess()) {
                Log.v("AUTH", "Successfully authenticated.");
                atomicReference.set(app.currentUser());
                user = app.currentUser();
                configure();
                checkIfUserLoggedInToday();
                sendToMainPage();
            } else {
                Log.e("AUTH", it.getError().toString());
                Toast.makeText(LogInActivity.this, "Authentication failed",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIfUserLoggedInToday() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        long lastLoginTimeMillis = sharedPreferences.getLong("lastLoginTime", 0);
        System.out.println("LAST LOGIN IS " + lastLoginTimeMillis);
        if (!DateUtils.isToday(lastLoginTimeMillis)) {
            updatePoints();
        }
        setLastLoginToday(sharedPreferences);
    }

    private void setLastLoginToday(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastLoginTime", System.currentTimeMillis());
        editor.apply();
    }

    private boolean isLoginInfoValid(EditText editTextEmail, EditText editTextPassword) {
        if (editTextEmail.getText().toString().equals("")) {
            editTextEmail.setError(getString(R.string.email_field_can_not_be_empty));
            return false;
        }

        if (editTextPassword.getText().toString().equals("")) {
            editTextPassword.setError(getString(R.string.password_field_can_not_be_empty));
            return false;
        }
        return true;
    }

    public void registerOnClick(View view) {
        EditText editTextEmail = findViewById(R.id.registerFragmentEditTextEmail);
        EditText editTextPassword = findViewById(R.id.registerFragmentEditTextPassword);

        if (!isRegistrationInfoValid(editTextEmail, editTextPassword)) {
            return;
        }

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        Credentials credentials = Credentials.emailPassword(email, password);
        AtomicReference<User> atomicReference = new AtomicReference<>();
        app.getEmailPassword().registerUserAsync(email, password, it -> {
            if (it.isSuccess()) {
                Log.v("AUTH", getString(R.string.successfully_registered));
                Toast.makeText(LogInActivity.this, getString(R.string.successfully_registered),
                        Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.containerLogin,
                        logInFragment).commit();

            } else {
                Log.e("AUTH", it.getError().toString());
                Toast.makeText(LogInActivity.this, getString(R.string.registration_failed),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isRegistrationInfoValid(EditText editTextEmail, EditText editTextPassword) {
        if (!editTextEmail.getText().toString().contains("@")) {
            editTextEmail.setError(getString(R.string.the_email_is_not_correct));
            return false;
        }

        if (editTextPassword.getText().toString().equals("")) {
            editTextPassword.setError(getString(R.string.password_must_have_at_leat_6_characters));
            return false;
        }

        return true;
    }


    private void sendToMainPage() {
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private Points dailyReward(String userId) {
        Points userPoints = null;
        userPoints = realm.where(Points.class)
                .equalTo("_id", userId)
                .findFirst();

        if (userPoints != null) {
            int currentPoints = userPoints.getPoints() + 10;
            userPoints = new Points(user.getId(), currentPoints);
        } else {
            userPoints = new Points(user.getId(), 20);
        }
        return userPoints;
    }

    private void updatePoints() {
        Points points = dailyReward(app.currentUser().getId());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(points);
            }
        });
        showDailyReward();
    }


    private void showDailyReward() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static SyncConfiguration getConfig() {
        return config;
    }

    public static synchronized <T extends RealmModel> SyncConfiguration configureRealm(User currentUser) {
        return new SyncConfiguration.Builder(currentUser)
                .name("licenta-db")
                .initialSubscriptions(new SyncConfiguration.InitialFlexibleSyncSubscriptions() {
                    @Override
                    public void configure(Realm realm, MutableSubscriptionSet subscriptions) {
                        subscriptions.addOrUpdate(Subscription.create(
                                realm.where(Favorites.class)));
                        subscriptions.addOrUpdate(Subscription.create(
                                realm.where(Points.class)));
                    }
                })
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .build();
    }

    public static User getUser() {
        return user;
    }
}
