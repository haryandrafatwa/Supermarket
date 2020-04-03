package com.example.supermarket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRefs;
    private StorageReference dummyDispPict;
    private ProgressDialog mDialog;
    private Button btnDaftar;
    private EditText etEmail, etNama, etNoTelephone, etPasswordRegister;
    private TextView tv_masuk;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialize();
    }

    private void initialize(){
        setStatusBar();
        dummyDispPict = FirebaseStorage.getInstance().getReference("DisplayPictures/dummy").child("ic_user.png");
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        etEmail = (EditText) findViewById(R.id.et_email_register);
        etNama = (EditText) findViewById(R.id.et_nama_register);
        etNoTelephone = (EditText) findViewById(R.id.et_nomorhp_register);
        etPasswordRegister = (EditText) findViewById(R.id.et_pass_register);
        tv_masuk = findViewById(R.id.tv_masuk);
        btnDaftar = (Button) findViewById(R.id.btn_daftar);

        toolbar = findViewById(R.id.toolbar_register);
        setToolbar();

        int tintColorDark = ContextCompat.getColor(getApplicationContext(), R.color.colorInputText);
        int tintColorDrawable = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

        Drawable drawableVisibility = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility_off);
        drawableVisibility = DrawableCompat.wrap(drawableVisibility);
        DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);

        Drawable drawableLock = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_lock);
        drawableLock = DrawableCompat.wrap(drawableLock);
        DrawableCompat.setTint(drawableLock.mutate(), tintColorDrawable);

        drawableVisibility.setBounds(0, 0, drawableVisibility.getIntrinsicWidth(), drawableVisibility.getIntrinsicHeight());
        drawableLock.setBounds(0, 0, drawableLock.getIntrinsicWidth(), drawableLock.getIntrinsicHeight());

        etPasswordRegister.setCompoundDrawables(drawableLock, null, drawableVisibility, null);

        etPasswordRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (etPasswordRegister.getRight() - etPasswordRegister.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        int tintColorDark = ContextCompat.getColor(getApplicationContext(), R.color.colorInputText);
                        int tintColorDrawable = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);

                        Drawable drawableVisibility = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility_off);
                        drawableVisibility = DrawableCompat.wrap(drawableVisibility);
                        DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDrawable);

                        Drawable drawableVisibilityOff = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_visibility);
                        drawableVisibilityOff = DrawableCompat.wrap(drawableVisibilityOff);
                        DrawableCompat.setTint(drawableVisibilityOff.mutate(), tintColorDrawable);

                        Drawable drawableLock = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_lock);
                        drawableLock = DrawableCompat.wrap(drawableLock);
                        DrawableCompat.setTint(drawableLock.mutate(), tintColorDrawable);

                        drawableVisibility.setBounds(0, 0, drawableVisibility.getIntrinsicWidth(), drawableVisibility.getIntrinsicHeight());
                        drawableVisibilityOff.setBounds(0, 0, drawableVisibilityOff.getIntrinsicWidth(), drawableVisibilityOff.getIntrinsicHeight());
                        drawableLock.setBounds(0, 0, drawableLock.getIntrinsicWidth(), drawableLock.getIntrinsicHeight());

                        if (etPasswordRegister.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                            etPasswordRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            DrawableCompat.setTint(drawableVisibility.mutate(), tintColorDark);
                            etPasswordRegister.setCompoundDrawables(drawableLock, null, drawableVisibility, null);
                        } else {
                            etPasswordRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            etPasswordRegister.setCompoundDrawables(drawableLock, null, drawableVisibilityOff, null);

                        }
                        return true;
                    }
                }
                return false;
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!etEmail.getText().toString().matches(emailPattern)) {
                    etEmail.setTextColor(Color.RED);
                } else {
                    etEmail.setTextColor(getResources().getColor(R.color.colorInputText));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tv_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivity(LoginActivity.class);
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerWithEmailandPassword();
            }
        });
    }

    private void registerWithEmailandPassword()
    {
        final String email = etEmail.getText().toString();
        final String username = etNama.getText().toString();
        final String telephone = etNoTelephone.getText().toString();
        String password = etPasswordRegister.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(telephone) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Data harus diisi", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mDialog.setTitle("Daftar");
            mDialog.setCancelable(true);
            mDialog.setMessage("Tunggu sebentar .. ");
            mDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
                        HashMap userMap = new HashMap();
                        userMap.put("username",username);
                        userMap.put("name",username);
                        userMap.put("phonenumber",telephone);
                        userMap.put("email",email);
                        userMap.put("saldo",0);
                        dummyDispPict.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                userRefs.child("displayPicture").setValue(uri.toString());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.d("DISPLAY PICTURE FAILED","OMG");
                            }
                        });
                        userRefs.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(Task task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(RegisterActivity.this, "Daftar Berhasil", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                    setActivity(MainActivity.class);
                                }
                                else
                                {
                                    Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            }
                        });

                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setActivity(Class activity) {
        Intent mainIntent = new Intent(RegisterActivity.this, activity);
        startActivity(mainIntent);
        finish();
    }

    private void setStatusBar(){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
    }

}
