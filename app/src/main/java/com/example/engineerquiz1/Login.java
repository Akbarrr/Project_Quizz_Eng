package com.example.engineerquiz1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends Fragment implements View.OnClickListener {

    //private NavController navController;


    // INISIASI UI
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button buttonLogin;
    private Button buttonSignup;
    private NavController navController;
    private ConstraintLayout containerLogin;
    private ConstraintLayout containerLoading;

    public Login()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //variabel tadi untuk memanggil fungsi
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

        // diatur sesuai id komponennya
        inputEmail = view.findViewById(R.id.inputEmail);
        inputEmail.requestFocus();
        inputPassword = view.findViewById(R.id.inputPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonSignup = view.findViewById(R.id.buttonSignup);


        containerLogin = view.findViewById(R.id.container_login);
        containerLoading = view.findViewById(R.id.container_loading);

        //nambahin method onClick, biar tombolnya bisa diklik
        buttonLogin.setOnClickListener(this);
        buttonSignup.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null)
        {
            containerLoading.setVisibility(View.INVISIBLE);
            containerLogin.setVisibility(View.VISIBLE);

        }
        else {
            navController.navigate(R.id.action_login_to_listFragment);
        }
    }

    // CEK DATA PENGGUNA YG SUDAH TERDAFTAR
    private void signIn()
    {
        Log.d(TAG, "Sign In");
        if (!validateForm())
        {
            return;
        }

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // AMBIL STRING EMAIL DAN TERAPKAN PADA METHOD SIGN IN WITH EMAIL & PASS
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, " Sign In : OnComplete :"+ task.isSuccessful());

                        if (task.isSuccessful())
                        {
                            navController.navigate(R.id.action_login_to_listFragment);
                        }
                        else {
                            Log.d(TAG, "Start Log : "+task.getException()); // TAMPILKAN TOAST BILA GAGAL
                        }
                    }
                });
    }


    public void signUp()
    {

        Log.d(TAG, "Sign Up");
        if (!validateForm())
        {
            return;
        }

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        //hideProgressDialog();

                        if (task.isSuccessful()) {
                            navController.navigate(R.id.action_login_to_listFragment);
                        } else {
                            Log.d(TAG, "Start Log : "+task.getException());
                        }
                    }
                });
    }
    // METHOD KALAU LOGIN DAN SIGNUP SUKSES
    private void onAuthSuccess(FirebaseUser user) {

        String username = usernameFromEmail(user.getEmail());

        writeNewUser(user.getUid(),username, user.getEmail());

         // PERPINDAHAN NAVIGASI
        //startActivity(new Intent(Login.this, ListFragment.class));
        navController.navigate(R.id.action_login_to_listFragment);
        //finish();
    }



    private String usernameFromEmail(String email) // METHOD UNTUK KONVERSI EMAIL KE UNAME
    {
        if (email.contains("@"))
        {
            return email.split("@")[0];
        }
        else {
            return email;
        }
    }

    private boolean validateForm()
    {
        boolean result = true;
        if (TextUtils.isEmpty(inputEmail.getText().toString()))
        {
            inputEmail.setError("Required");
            result  = false;
        }
        else {
            inputPassword.setError(null);
        }
        if (TextUtils.isEmpty(inputPassword.getText().toString())) {
            inputPassword.setError("Required");
            result = false;
        } else {
            inputPassword.setError(null);
        }
        return result;
    }

    private void writeNewUser(String userId, String name, String email)
    {
        User user = new User (name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.buttonLogin)
        {
            signIn();
        }
        else if (i == R.id.buttonSignup)
        {
            signUp();
        }
    }
}