package com.example.engineerquiz1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private List<QuizListModel> quizListModels;
    private OnQuizListItemClicked onQuizListItemClicked;

    public QuizListAdapter(OnQuizListItemClicked onQuizListItemClicked)
    {
        this.onQuizListItemClicked = onQuizListItemClicked;
    }
    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {

        // PEMANGGILAN GAMBAR DARI FIREBASE STORAGE
        String imageUrl = quizListModels.get(position).getGambar();
        Glide
                .with(holder.itemView.getContext())
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(holder.listimage);

        // LOADER JUDUL KUIS
        holder.listTitle.setText(quizListModels.get(position).getNama());

        // LOADER DESKRIPSI KUIS
        String listDeskripsi = quizListModels.get(position).getDeskripsi();
        if (listDeskripsi.length() > 150)
        {
            listDeskripsi = listDeskripsi.substring(0, 150);
        }
        holder.listDesc.setText(listDeskripsi + "...");

        // LOADER LEVEL
        holder.listLevel.setText(quizListModels.get(position).getLevel());
    }

    @Override
    public int getItemCount() {
        if (quizListModels == null)
        {
            return 0;
        }
        else {
            return quizListModels.size();
        }
    }

    public void setQuizListModels(List<QuizListModel> quizListModels) {
        this.quizListModels = quizListModels;
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView listimage;
        private TextView listTitle;
        private TextView listDesc;
        private TextView listLevel;
        private Button listBtn;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);

            listimage = itemView.findViewById(R.id.list_image);
            listTitle = itemView.findViewById(R.id.list_title);
            listDesc = itemView.findViewById(R.id.list_desc);
            listLevel = itemView.findViewById(R.id.list_diff);
            listBtn = itemView.findViewById(R.id.list_btn);

            listBtn.setOnClickListener(this);

        }

        // ONCLICK BUTTON VIEW
        @Override
        public void onClick(View v) {
            onQuizListItemClicked.onItemClicked(getAdapterPosition());
        }
    }

    // INTERFACE BUTTON ACTION
    public interface OnQuizListItemClicked
    {
        void onItemClicked(int position);
    }
}
