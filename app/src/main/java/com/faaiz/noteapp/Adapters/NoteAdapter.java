package com.faaiz.noteapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.faaiz.noteapp.Model.Note;
import com.faaiz.noteapp.OnNotesClickListener;
import com.faaiz.noteapp.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> implements Filterable {

    List<Note> list;
    Context context;
    OnNotesClickListener onNotesClickListener;
    List<Note> listFull;

    public NoteAdapter(List<Note> list, Context context, OnNotesClickListener onNotesClickListener) {
        this.list = list;
        this.context = context;
        this.onNotesClickListener = onNotesClickListener;
        listFull = new ArrayList<>();
        listFull.addAll(list);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item,parent,false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.noteTitle.setText(list.get(position).getTitle());
        holder.noteTitle.setSelected(true);

        holder.noteDetail.setText(list.get(position).getContent());

        holder.noteTime.setText(list.get(position).getDate());
        holder.noteTime.setSelected(true);

        if(list.get(position).isPinned()){
            holder.pinIcon.setImageResource(R.drawable.pin);
        }else{
            holder.pinIcon.setImageResource(0);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNotesClickListener.setOnNotesClick(list.get(position));
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onNotesClickListener.setOnNotesLongClick(list.get(position), holder.container);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filterNotes;
    }

    private Filter filterNotes = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String initials = charSequence.toString().toLowerCase().trim();
            List<Note> filteredList = new ArrayList<>();
            if(charSequence.length()!=0 || charSequence!=null){
                for(Note note : listFull){
                    if(note.getTitle().toLowerCase().contains(initials) || note.getContent().toLowerCase().contains(initials)){
                        filteredList.add(note);
                    }
                }
            }
            for(Note n: filteredList) System.out.println(n.getTitle() + " " + n.getContent());
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list = (List<Note>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class NoteViewHolder extends RecyclerView.ViewHolder{

        CardView container;
        TextView noteTitle, noteDetail, noteTime;
        ImageView pinIcon;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.notesContainer);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteDetail = itemView.findViewById(R.id.noteDetails);
            noteTime = itemView.findViewById(R.id.noteTime);
            pinIcon = itemView.findViewById(R.id.pinIcon);
        }
    }
}
