package com.example.marcell.taskmanager.UI.EditTaskRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.marcell.taskmanager.R;

import java.util.ArrayList;
import java.util.List;

public class KeywordRVAdapter extends RecyclerView.Adapter<KeywordRVAdapter.KeywordViewHolder> {
    private final String emptyKeywordField = "";
    private List<String> keywords;

    public KeywordRVAdapter() {
        keywords = new ArrayList<>();
        keywords.add(emptyKeywordField);
    }

    public List<String> getKeywords() {
        //Remove emptyKeywordField from the end
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        if (keywords != null) {
            this.keywords = keywords;
            notifyDataSetChanged();
        }
    }

    private void addKeyword(String keyword) {
        keywords.add(keyword);
        notifyDataSetChanged();
    }

    private void removeKeyword(String keyword) {
        keywords.remove(keyword);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.keyword_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        if (position >= 0 && position < keywords.size()) {
            String keywordListItem = keywords.get(position);

//            if (keywordListItem.equals(emptyKeywordField)) {
//
//            } else {
                holder.showRemoveFrame();
                holder.keywordTextView.setText(keywordListItem);
//            }
        }else {
            holder.showAddFrame();
            holder.addKeywordEditText.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return keywords.size() + 1;
    }


    public class KeywordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final FrameLayout addFrameLayout;
        public final Button addButton;
        public final EditText addKeywordEditText;

        public final FrameLayout removeFrameLayout;
        public final Button removeButton;
        public final TextView keywordTextView;
        private final String TAG = KeywordViewHolder.class.getSimpleName();

        public KeywordViewHolder(View itemView) {
            super(itemView);

            addFrameLayout = (FrameLayout) itemView.findViewById(R.id.fl_new_keyword);
            addButton = (Button) itemView.findViewById(R.id.bt_add);
            addKeywordEditText = (EditText) itemView.findViewById(R.id.ed_new_keyword);

            removeFrameLayout = (FrameLayout) itemView.findViewById(R.id.fl_existing_keyword);
            removeButton = (Button) itemView.findViewById(R.id.bt_remove);
            keywordTextView = (TextView) itemView.findViewById(R.id.tv_keyword);

            addButton.setOnClickListener(this);
            removeButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.bt_add) {
                    if(adapterPosition >= keywords.size()) {
                        String newKeyword = addKeywordEditText.getText().toString();
                        if (!newKeyword.equals("")) {
                            addKeyword(newKeyword);
                        }
                    }

                } else if (v.getId() == R.id.bt_remove) {
                    String removeKeyword = keywordTextView.getText().toString();
                    removeKeyword(removeKeyword);
                }

            } else {
                Log.d(TAG, "onClick method: NO_POSITION in RecyclerView");
            }
        }

        private void showAddFrame() {
            addFrameLayout.setVisibility(View.VISIBLE);
            removeFrameLayout.setVisibility(View.GONE);
        }

        private void showRemoveFrame() {
            addFrameLayout.setVisibility(View.GONE);
            removeFrameLayout.setVisibility(View.VISIBLE);
        }
    }
}
