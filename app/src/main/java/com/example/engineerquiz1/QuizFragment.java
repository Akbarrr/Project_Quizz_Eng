package com.example.engineerquiz1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class QuizFragment extends Fragment implements View.OnClickListener {

    private NavController navController;
    private static final String TAG = "QUIZ_FRAGMENT_LOG";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String quizId;

    private String userIdSekarang;
    private String quizNama;


    // AMBIL FIREBASE DATA QUESTION
    private List<QuestionModel> allQuestionList = new ArrayList<>();
    private long totalQuestionToAnswer = 5;
    private List<QuestionModel> questionToAnswer = new ArrayList<>();

    // UI ELEMENT
    private Button optionabutton;
    private Button optionbbutton;
    private Button optioncbutton;
    private Button nextbtn;
    private TextView quizTitle;
    private TextView questionfeedback;
    private TextView questiontext;
    private TextView questiontimer;
    private TextView questionnumber;
    private ProgressBar questionprogress;
    private ImageButton closebtn;


    private CountDownTimer countDownTimer;

    // LOGIKA INPUT JAWABAN
    private boolean bisajawab = false;

    // MENYIMPAN DATA PERTANYAAN SAAT ITU
    private int pertanyaansekarang = 0;

    // MENYIMPAN DATA JAWABAN BENAR DAN SALAH
    private int jawabanbenar = 0;
    private int jawabansalah = 0;

    // VARIABEL SOAL YG TIDAK TERJAWAB
    private int tidakterjawab = 0;


    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        firebaseAuth = FirebaseAuth.getInstance();

        // AMBIL USER ID
        if (firebaseAuth.getCurrentUser()!=null)
        {
            userIdSekarang = firebaseAuth.getCurrentUser().getUid();
        }
        else {
            // GO BACK TO HOME
        }

            // INISIASI ELEMEN UI
        quizTitle = view.findViewById(R.id.quiz_title);
        optionabutton = view.findViewById(R.id.quiz_option_one);
        optionbbutton = view.findViewById(R.id.quiz_option_two);
        optioncbutton = view.findViewById(R.id.quiz_option_three);
        nextbtn = view.findViewById(R.id.quiz_next_btn);
        questionfeedback = view.findViewById(R.id.quiz_question_feedback);
        questiontext = view.findViewById(R.id.quiz_question);
        questiontimer = view.findViewById(R.id.quiz_question_time);
        questionnumber = view.findViewById(R.id.quiz_question_no);
        questionprogress = view.findViewById(R.id.quiz_question_progress);
        //closebtn = view.findViewById(R.id.quiz_close_btn);

        // INISIALISASI FIRESTORE
        firebaseFirestore = FirebaseFirestore.getInstance();

        // MENGAMBIL DATA ARGUMEN QUIZID DARI NAVIGASI
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        quizNama = QuizFragmentArgs.fromBundle(getArguments()).getQuizNama();

        // MENGAMBIL TOTAL PERTANYAAN MENJADI TOTAL PERTANYAAN UNTUK DIJAWAB DENGAN ARGUMEN
        totalQuestionToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalPertanyaan();

        // AMBIL DATA DARI FIRESTORE DARI COLLECTION QUIZLIST DAN QUESTIONS
        firebaseFirestore
                .collection("QuizList")
                .document(quizId)
                .collection("Questions")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    // MENGAMBIL HASIL DARI QUESTIONMODEL CLASS
                    allQuestionList = task.getResult().toObjects(QuestionModel.class);
                    Log.d(TAG, "Question List : "+ allQuestionList.get(0).getQuestion());

                    // AMBIL QUESTION DARI MODEL
                    ambilQuestion();

                    // LOAD QUESTION DAN ANSWER KE UI
                    loadUI();

                }
                else
                {
                    quizTitle.setText("Error Loading Data!");
                }
            }
        });

        // ONCLICK BUTTON
        optionabutton.setOnClickListener(this);
        optionbbutton.setOnClickListener(this);
        optioncbutton.setOnClickListener(this);

        nextbtn.setOnClickListener(this);
    }


    // METHOD LOAD UI KEDALAM PERTANYAAN DAN JAWABAN
    private void loadUI() {
        quizTitle.setText(quizNama);
        //questionnumber.setText("1");
        questiontext.setText("First Question");

        // ENABLE OPSI DENGAN METHOD BARU
        enableOption();

        // LOAD PERTANYAAN KEDALAM UI
        loadPertanyaan(1);


    }

    // METHOD LOAD PERTANYAAN SESUAI URUTAN I
    private void loadPertanyaan(int questionnum) {
        questionnumber.setText(questionnum + "");

        // LOAD TEXT PERTANYAAN
        questiontext.setText(questionToAnswer.get(questionnum-1).getQuestion());

        // LOAD TEXT OPSI
        optionabutton.setText(questionToAnswer.get(questionnum-1).getOptiona());
        optionbbutton.setText(questionToAnswer.get(questionnum-1).getOptionb());
        optioncbutton.setText(questionToAnswer.get(questionnum-1).getOptionc());

        // METHOD TIMER
        startTimer(questionnum);

        // LOAD BISAJAWAB
        bisajawab = true;

        // PERTANYAAN YANG SEDANG DIJAWAB EQUALS NOMOR PERTANYAAN YANG TERSAJI
        pertanyaansekarang = questionnum;
    }

    // METHOD TIMER YG DIPANGGIL LOADPERTANYAAN()
    private void startTimer(int questionNumber) {

        // SET TIMER TEXT DI UI
        final Long timeToAnswer = questionToAnswer.get(questionNumber-1).getTimer();
        questiontimer.setText(timeToAnswer.toString());

        // TAMPILKAN TIMER PADA PROGRESSBAR
        questionprogress.setVisibility(View.VISIBLE);

        // START TIMER
        countDownTimer = new CountDownTimer(timeToAnswer*1000, 1 )
        {
            @Override
            public void onTick(long millisUntilFinished) {
                // UPDATE TIMER
                questiontimer.setText(millisUntilFinished/1000 + "");

                // PERSENTASI KEDALAM PROGRESSBAR
                Long percent = millisUntilFinished/(timeToAnswer*10);
                questionprogress.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                // WAKTU HABIS
                bisajawab = false;

                questionfeedback.setText("Waktu Habis!");
                questionfeedback.setTextColor(getResources().getColor(R.color.colorPrimary));
                tidakterjawab++;
                showNextBtn();
            }
        };

        countDownTimer.start();
    }

    // METHOD OPSI YG DIPANGGIL DI LOADUI
    private void enableOption() {
        optionabutton.setVisibility(View.VISIBLE);
        optionbbutton.setVisibility(View.VISIBLE);
        optioncbutton.setVisibility(View.VISIBLE);

        // ENABLE BUTTON
        optionabutton.setEnabled(true);
        optionbbutton.setEnabled(true);
        optioncbutton.setEnabled(true);

        // HIDE FEEDBACK TEXT DAN NEXT BTN
        questionfeedback.setVisibility(View.INVISIBLE);
        nextbtn.setVisibility(View.INVISIBLE);
    }

    // METHOD AMBIL QUESTION YG DIPANGGIL DARI MODEL
    private void ambilQuestion() {

        // PERULANGAN SESUAI JUMLAH PERTANYAAN
        for (int i=0; i < totalQuestionToAnswer; i++)
        {
            int randomNumber = getRandomInteger(allQuestionList.size(), 0);

            // SAJIKAN DATA PERTANYAAN DENGAN URUTAN RANDOMNUMBER
            questionToAnswer.add(allQuestionList.get(randomNumber));

            // MENGHAPUS PERTANYAAN YANG SUDAH DISAJIKAN OLEH RANDOMNUMBER
            allQuestionList.remove(randomNumber);
            Log.d(TAG, "Question : " + i + " " + questionToAnswer.get(i).getQuestion());


        }
    }

    // METHOD RANDOM UNTUK URUTAN SOAL
    public static int getRandomInteger(int maximum, int minimum)
    {
        return ((int) (Math.random()*(maximum-minimum))) + minimum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }


    // METHOD ONCLICK OPTION BUTTON
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.quiz_option_one:
                // METHOD JAWABAN TERPILIH DENGAN MENGAMBIL DATA TEXT OPSI TSB
                verifyAnswer(optionabutton);
                break;

            case R.id.quiz_option_two:
                verifyAnswer(optionbbutton);
                break;

            case R.id.quiz_option_three:
                verifyAnswer(optioncbutton);
                break;

            case R.id.quiz_next_btn:
                if (pertanyaansekarang == totalQuestionToAnswer)
                {
                    // TAMPILKAN HASIL DENGAN METHOD SUBMITHASIL()
                    submitHasil();
                }
                else {
                    pertanyaansekarang++;
                    loadPertanyaan(pertanyaansekarang);
                    // METHOD MENGEMBALIKAN KONDISI BUTTON
                    resetopsi();
                }
                break;
        }
    }

    // METHOD HASIL
    private void submitHasil() {
        HashMap<String, Object> hasilMap = new HashMap<>();
        hasilMap.put("benar", jawabanbenar);
        hasilMap.put("salah", jawabansalah);
        hasilMap.put("tdkterjawab", tidakterjawab);

        // TAMBAHKAN COLLECTION HASIL KE FIRESTORE
        firebaseFirestore.collection("QuizList")
                .document(quizId)
                .collection("Hasil")
                .document(userIdSekarang)
                .set(hasilMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            // GO TO HASILPAGE
                            QuizFragmentDirections.ActionQuizFragmentToResultFragment action = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                            action.setQuizId(quizId);
                            navController.navigate(action);
                        }
                        else {
                            // SHOW ERROR
                            quizTitle.setText(task.getException().getMessage());
                        }
                    }
                });
    }

    // METHOD UNTUK KSH KEMBALI KONDISI BUTTON DAN UI LAIN SETELAH BUTTON NEXT DITEKAN
    private void resetopsi() {
        optionabutton.setBackground(getResources().getDrawable(R.drawable.light_btn));
        optionbbutton.setBackground(getResources().getDrawable(R.drawable.light_btn));
        optioncbutton.setBackground(getResources().getDrawable(R.drawable.light_btn));

        optionabutton.setTextColor(getResources().getColor(R.color.colorLightText));
        optionbbutton.setTextColor(getResources().getColor(R.color.colorLightText));
        optioncbutton.setTextColor(getResources().getColor(R.color.colorLightText));

        questionfeedback.setVisibility(View.INVISIBLE);
        nextbtn.setVisibility(View.INVISIBLE);
        nextbtn.setEnabled(false);
    }

    private void verifyAnswer(Button btnJawabanTerpilih) {

        // CEK JAWABAN
        if (bisajawab)
        {
            btnJawabanTerpilih.setTextColor(getResources().getColor(R.color.colorDark));
            if (questionToAnswer.get(pertanyaansekarang-1).getAnswer().equals(btnJawabanTerpilih.getText()))
            {
                //JAWABAN BENAR
                //Log.d(TAG,"JAWABAN BENAR");
                jawabanbenar++;
                btnJawabanTerpilih.setBackground(getResources().getDrawable(R.drawable.jawaban_benar));

                // FEEDBACK
                questionfeedback.setText("Jawaban Benar!");
                questionfeedback.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
            else
            {
                //JAWABAN SALAH
                //Log.d(TAG,"JAWABAN SALAH");
                jawabansalah++;
                btnJawabanTerpilih.setBackground(getResources().getDrawable(R.drawable.jawaban_salah));

                // FEEDBACK
                questionfeedback.setText("Jawaban Salah! \n Jawaban Benar : "+questionToAnswer.get(pertanyaansekarang-1).getAnswer());
                questionfeedback.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            bisajawab = false;

            // HENTIKAN TIMER
            countDownTimer.cancel();

            // TAMPILKAN BUTTON NEXT
            showNextBtn();
        }
    }

    // METHOD BUTTON NEXT
    private void showNextBtn() {

        if (pertanyaansekarang == totalQuestionToAnswer)
        {
            nextbtn.setText("Submit Jawaban");
        }

        questionfeedback.setVisibility(View.VISIBLE);
        nextbtn.setVisibility(View.VISIBLE);
        nextbtn.setEnabled(true);
    }
}