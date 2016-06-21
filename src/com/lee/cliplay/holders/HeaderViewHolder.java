package com.lee.cliplay.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lee.cliplay.R;

/**
 * Created by xl on 16/6/21.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private View itemView;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        this.textView = (TextView)itemView.findViewById(R.id.txtHeader);
    }

    public void setHeaderText(String text) {
        if(!text.equals("")) {
            textView.setText(text);
        }else {
            textView.setVisibility(View.GONE);
            itemView.setVisibility(View.GONE);
        }
    }
}
