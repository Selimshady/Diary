package com.example.diary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.db.AppDatabase;
import com.example.diary.db.Memory;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<Memory> memories;
    Context context;
    Activity mainActivity;

    public MyAdapter(Activity mainActivity,Context ct, List<Memory> memory) {
        this.mainActivity = mainActivity;
        context = ct;
        memories = memory;
    }

    public void setMemories(List<Memory> memories) {
        this.memories = memories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(memories.get(position).getTitle());
        holder.date.setText(memories.get(position).getDate());
        holder.location.setText(memories.get(position).getLocation());
        switch (memories.get(position).getEmotion())
        {
            case 0:
                holder.emotion.setImageResource(R.drawable.tiredface);
                break;
            case 1:
                holder.emotion.setImageResource(R.drawable.happyface);
                break;
            case 2:
                holder.emotion.setImageResource(R.drawable.sadface);
                break;
            case 3:
                holder.emotion.setImageResource(R.drawable.angryface);
                break;
            case 4:
                holder.emotion.setImageResource(R.drawable.lovingface);
        }

        holder.mainLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, MemoryPageActivity.class);
            intent.putExtra("id",memories.get(position).getMid());
            mainActivity.startActivityForResult(intent,79);
        });

        holder.mainLayout.setOnLongClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context,holder.mainLayout);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.shareButton:
                        share(position);
                        break;
                    case R.id.deleteButton:
                        delete(position);
                        break;
                }
                return false;
            });
            popupMenu.show();
            return true;
        });
    }

    private void share(int position)
    {
        String memo = memories.get(position).getTitle() + "\n" + memories.get(position).getLocation() + " - " +
                memories.get(position).getDate() + "\n" + memories.get(position).getMainText();
        memo += "Sent via Diary App";
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT,memo);
        context.startActivity(share);
    }
    private void delete(int position) {
        AppDatabase db;
        db = AppDatabase.getDbInstance(context);
        db.memoryDao().delete(memories.get(position));
        setMemories(db.memoryDao().getAllMemories());
    }


        @Override
    public int getItemCount() {
        return memories.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, date, location;
        ImageView emotion;
        ConstraintLayout mainLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
            emotion = itemView.findViewById(R.id.emotion);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
