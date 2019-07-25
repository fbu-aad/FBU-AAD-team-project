package com.example.una;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.una.models.Charity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.OnClick;

public class CharityLoginActivity extends AppCompatActivity {

    @BindView(R.id.password) EditText password;
    @BindView(R.id.etCharityEin) EditText etCharityEin;
    @BindView(R.id.etCharityName) EditText etCharityName;
    @BindView(R.id.loginBtn) Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_login);
    }

    @OnClick(R.id.loginBtn)
    public void logInCharity() {
        Editable name = etCharityName.getText();
        Editable ein = etCharityEin.getText();
        if(name == null || name.toString() == "") {
            Toast.makeText(this, "Please input charity name", Toast.LENGTH_SHORT).show();

        } else if (ein == null || ein.toString() == "") {
            Toast.makeText(this, "Please input valid EIN", Toast.LENGTH_SHORT).show();

        } else {
            Charity charityUser = new Charity(ein.toString(), name.toString());

            Intent goToCharityHome = new Intent(this, CharityHomeActivity.class);
            goToCharityHome.putExtra("charity", Parcels.wrap(charityUser));
            startActivity(goToCharityHome);
        }
    }
}
