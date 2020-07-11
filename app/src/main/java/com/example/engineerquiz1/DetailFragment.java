package com.example.engineerquiz1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;

public class DetailFragment extends Fragment implements View.OnClickListener {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private NavController navController;

    private QuizListViewModel quizListViewModel;
    private int position;

    private ImageView detailImage;
    private TextView detailTitle;
    private TextView detailDeskripsi;
    private TextView detailDiff;
    private TextView detailQuestion;
    private TextView detailscore;

    private Button detailStartBtn;
    private String quizId;
    private String userIdSekarang;

    // INISIASI AWAL JUMLAH PERTANYAAN
    private long totalQuestion = 0;
    private String quizNama;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        // AMBIL ARGUMENT DARI LIST FRAGMENT DIRECTION
        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition() ;

        // INISIALISASI UI ELEMENT
        detailImage = view.findViewById(R.id.detail_image);
        detailTitle = view.findViewById(R.id.detail_title);
        detailDeskripsi = view.findViewById(R.id.detail_desc);
        detailDiff = view.findViewById(R.id.detail_diff_text);
        detailQuestion = view.findViewById(R.id.detail_questions_text);
        detailscore = view.findViewById(R.id.detail_score_text);

        detailStartBtn = view.findViewById(R.id.detail_start_btn);
        detailStartBtn.setOnClickListener(this);

        // LOAD HASIL SEBELUMNYA
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // GET MODEL DATA
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

                Glide
                        .with(getContext())
                        .load(quizListModels.get(position).getGambar())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(detailImage);

                detailTitle.setText(quizListModels.get(position).getNama());
                detailDeskripsi.setText(quizListModels.get(position).getDeskripsi());
                detailDiff.setText(quizListModels.get(position).getLevel());
                detailQuestion.setText(quizListModels.get(position).getPertanyaan() + "");

                // MENGAMBIL POSISI QUIZ ID
                quizId = quizListModels.get(position).getQuiz_id();
                quizNama = quizListModels.get(position).getNama();

                // MENGAMBIL JUMLAH PERTANYAAN DARI QUIZLISTMODEL
                totalQuestion = quizListModels.get(position).getPertanyaan();

                // PANGGIL METHOD YANG AMBIL DATA HASIL SEBELUMNYA
                loadDataHasil();
            }
        });

    }

    // METHOD AMBIL DATA HASIL SEBELUMNA
    private void loadDataHasil() {
        firebaseFirestore.collection("QuizList")
                .document(quizId)
                .collection("Hasil")
//              .document(userIdSekarang).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                .document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()){
                        // UBAH DATA DARI FIRESTORE KEDALAM LONG DAN VARIABEL BARU
                        Long benar = document.getLong("benar");
                        Long salah = document.getLong("salah");
                        Long kosong = document.getLong("tdkterjawab");

                        // HITUNG TOTAL DAN PERSEN PROGRESS
                        Long total = benar + salah + kosong;
                        Long persen = (benar*100)/total;

                        detailscore.setText(persen + "%");
                    }
                    else {
                        // DOCUMENT DOESNT EXIST
                    }
                }
            }
        });


    }

    // METHOD ONCLICK BUTTON START
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // CASE BILA DITEKAN DETAIL START BTN, PERALIHAN KE FUNGSI NAVIGASI
            case R.id.detail_start_btn:
                DetailFragmentDirections.ActionDetailFragmentToQuizFragment action = DetailFragmentDirections.actionDetailFragmentToQuizFragment();

                // MEMBAWA DATA TOTAL PERTANYAAN
                action.setTotalPertanyaan(totalQuestion);
                action.setQuizNama(quizNama);
                action.setQuizId(quizId);
                navController.navigate(action);
                break;
        }
    }
}