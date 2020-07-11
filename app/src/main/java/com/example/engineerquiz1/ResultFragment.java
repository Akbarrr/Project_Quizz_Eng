package com.example.engineerquiz1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResultFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private NavController navController;

    private String userIdSekarang;
    private String quizId;


    private TextView hasilbenar;
    private TextView hasilsalah;
    private TextView hasilkosong;
    private TextView hasilpersen;
    private ProgressBar hasilprogress;
    private Button hasilhomebtn;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();

        // AMBIL USER ID
        if (firebaseAuth.getCurrentUser()!= null)
        {
            userIdSekarang = firebaseAuth.getCurrentUser().getUid();
        }
        else {
            // BACK TO HOME
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        // AMBIL ARGUMEN DARI RESULT FRAGMENT - QUIZ FRAGMENT
        quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();

        // INISIASI UI ELEMEN
        hasilbenar = view.findViewById(R.id.result_correct_text);
        hasilsalah = view.findViewById(R.id.result_wrong_text);
        hasilkosong = view.findViewById(R.id.result_missed_text);

        hasilhomebtn = view.findViewById(R.id.result_home_btn);
        hasilhomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // PERPINDAHAN NAVIGASI KE LIST FRAGMENT
                navController.navigate(R.id.action_resultFragment_to_listFragment);
            }
        });

        hasilpersen = view.findViewById(R.id.result_percent);
        hasilprogress = view.findViewById(R.id.result_progress);

        // AMBIL HASIL QUIZ
        firebaseFirestore.collection("QuizList")
                .document(quizId)
                .collection("Hasil")
                .document(userIdSekarang).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot hasil = task.getResult();

                    // UBAH DATA DARI FIRESTORE KEDALAM LONG DAN VARIABEL BARU
                    Long benar = hasil.getLong("benar");
                    Long salah = hasil.getLong("salah");
                    Long kosong = hasil.getLong("tdkterjawab");

                    hasilbenar.setText(benar.toString());
                    hasilsalah.setText(salah.toString());
                    hasilkosong.setText(kosong.toString());

                    // HITUNG TOTAL DAN PERSEN PROGRESS
                    Long total = benar + salah + kosong;
                    Long persen = (benar*100)/total;

                    // SET TEXT KEDALAM UI
                    hasilpersen.setText(persen + "%");
                    hasilprogress.setProgress(persen.intValue());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);


    }
}