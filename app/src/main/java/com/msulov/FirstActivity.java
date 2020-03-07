package com.msulov;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msulov.Models.User;

public class FirstActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    FirebaseAuth auth;              // Для авторизации
    FirebaseDatabase db;            // Для подключения к базе данных
    DatabaseReference users;        // Для работы с табличками в базе данных

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();          // Запускаем авторизацию в базе данных
        db = FirebaseDatabase.getInstance();        // Подключаемся к базе данных
        users = db.getReference("Users");     // Даем название табличке

        FirebaseUser user = auth.getCurrentUser();
        if (User.unistalling) user = null;
        if (user != null) {
            startActivity(new Intent(FirstActivity.this, MainActivity.class));
            finish();
        }
        User.unistalling = false;

        setContentView(R.layout.activity_first);

        btnLogin = findViewById(R.id.button_login);
        btnRegister = findViewById(R.id.button_register);
        relativeLayout = findViewById(R.id.relative_layout);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginWindow();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterWindow();
            }
        });
    }

    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);  // Создаем диалоговое окно
        dialog.setTitle(R.string.registration);
        dialog.setMessage(R.string.dialog_message_register);

        LayoutInflater inflater = LayoutInflater.from(this);    // Создаем объект для получения нашего шаблона
        View registerWindow = inflater.inflate(R.layout.register_window, null);   // Передаем шаблон в другой obj

        dialog.setView(registerWindow);

        final EditText email = registerWindow.findViewById(R.id.emailField);
        final EditText password = registerWindow.findViewById(R.id.passwordField);
        final EditText name = registerWindow.findViewById(R.id.nameField);
        final EditText phone = registerWindow.findViewById(R.id.phoneField);

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(relativeLayout, R.string.dialog_error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(password.getText().toString().length() < 6) {
                    Snackbar.make(relativeLayout, R.string.dialog_password_error, Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(relativeLayout, R.string.dialog_error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (name.getText().toString().equals("Admin")) {
                    Snackbar.make(relativeLayout, "Вы не Admin", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (name.getText().toString().equals("Rm230801Admin")) {
                    name.setText("Admin");
                }

                if(TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(relativeLayout, R.string.dialog_error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Регистрация пользователя
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User();
                                user.setEmail(email.getText().toString());
                                user.setPassword(password.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPhone(phone.getText().toString());

                                users.child(auth.getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(relativeLayout, R.string.authorization_was_successful, Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(relativeLayout, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }

    private void showLoginWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);  // Создаем диалоговое окно
        dialog.setTitle(R.string.authorization);
        dialog.setMessage(R.string.dialog_message_login);

        LayoutInflater inflater = LayoutInflater.from(this);    // Создаем объект для получения нашего шаблона
        View loginWindow = inflater.inflate(R.layout.login_window, null);   // Передаем шаблон в другой obj

        dialog.setView(loginWindow);

        final EditText email = loginWindow.findViewById(R.id.emailField);
        final EditText password = loginWindow.findViewById(R.id.passwordField);

        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton(R.string.button_login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(relativeLayout, R.string.dialog_error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password.getText().toString())) {
                    Snackbar.make(relativeLayout, R.string.dialog_error, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(FirstActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(relativeLayout, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }
}