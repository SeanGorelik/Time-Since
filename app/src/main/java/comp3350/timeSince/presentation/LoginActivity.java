package comp3350.timeSince.presentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import comp3350.timeSince.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonLoginOnClick(View v) {
        Intent userIntent = new Intent(LoginActivity.this, ViewEventActivity.class);
        LoginActivity.this.startActivity(userIntent);
    }
}