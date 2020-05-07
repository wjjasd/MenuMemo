package com.wjjasd.google.menumemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {

    private List<MemoData> mMemoSet;
    private static View.OnClickListener onClickListener;
    private static View.OnLongClickListener longClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_table_memo, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    @Override
    public int getItemCount() {
        return mMemoSet == null ? 0 : mMemoSet.size();
    }

    public MemoData getMemo(int position) {

        return mMemoSet != null ? mMemoSet.get(position) : null;

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView textViewTable;
        public TextView textViewMenu;
        public View rootView;

        public ViewHolder(View v) {
            super(v);
            textViewTable = v.findViewById(R.id.tv_table_tableMemo);
            textViewMenu = v.findViewById(R.id.tv_menu_tableMemo);

            rootView = v;

            v.setClickable(true);
            v.setEnabled(true);
            v.setOnClickListener(onClickListener);
            v.setOnLongClickListener(longClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // - get element from your dataset at this position
        MemoData memo = mMemoSet.get(position);
        holder.textViewTable.setText(memo.getTableNo());
        holder.textViewMenu.setText(memo.getMenu());

        holder.rootView.setTag(position);

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MemoAdapter(List<MemoData> myMemoset, Context context, View.OnClickListener onClick, View.OnLongClickListener longClick) {
        mMemoSet = myMemoset;
        onClickListener = onClick;
        longClickListener = longClick;

    }


}
