package com.example.engineerquiz1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


public class ListFragment extends Fragment implements QuizListAdapter.OnQuizListItemClicked, View.OnClickListener {

    private NavController navController;

    private RecyclerView listView;
    private QuizListViewModel quizListViewModel;
    private QuizListAdapter adapter;
    private FirebaseAuth firebaseAuth;

    private Button buttonlogout;

    private TextView textUsername;

    private ProgressBar listprogress;
    private Animation fadeinAnim;
    private Animation fadeoutAnim;

    public ListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.list_view);
        listprogress = view.findViewById(R.id.list_progress);
        adapter = new QuizListAdapter(this);
        navController = Navigation.findNavController(view);

        textUsername = view.findViewById(R.id.textUsername);

        buttonlogout = view.findViewById(R.id.buttonLogout);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setHasFixedSize(true);
        listView.setAdapter(adapter);

        fadeinAnim = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
        fadeoutAnim = AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);

        buttonlogout.setOnClickListener(this);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizListModelData().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModels) {

                // LOAD LISTVIEW AND ANIMATION
                listView.startAnimation(fadeinAnim);
                listprogress.startAnimation(fadeoutAnim);

                adapter.setQuizListModels(quizListModels);
                adapter.notifyDataSetChanged();
            }
        });

        
    }



    @Override
    public void onItemClicked(int position) {
        // PASS ARGUMENT DARI LIST KE DETAIL
        ListFragmentDirections.ActionListFragmentToDetailFragment action = ListFragmentDirections.actionListFragmentToDetailFragment();
        action.setPosition(position);
        navController.navigate(action);

    }

    @Override
    public void onClick(View v) {
        FirebaseAuth.getInstance().signOut();
        navController.navigate(R.id.action_listFragment_to_login);
    }
}