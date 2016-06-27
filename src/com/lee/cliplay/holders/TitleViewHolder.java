package com.lee.cliplay.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lee.cliplay.R;

/**
 * Created by xl on 16/6/27.
 */
public class TitleViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    public TitleViewHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.title);
    }
    public void setTitle(String title) {
        this.title.setText(title);
    }
}
