package com.example.quizsection;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SubjGridAdapter extends BaseAdapter {

    private List<String> subjList;

    public SubjGridAdapter(List<String> subjList) {
        this.subjList = subjList;
    }

    @Override
    public int getCount() {
        return subjList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View v;

        if(view == null)
        {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_layout, viewGroup, false);
        }
        else
        {
            v = view;
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(viewGroup.getContext(), QuizChapters.class);
                intent.putExtra("Subjects", subjList.get(i));
                intent.putExtra("SUB_ID", i+1);
                viewGroup.getContext().startActivity(intent);
            }
        });

        ((TextView) v.findViewById(R.id.itemName)).setText(subjList.get(i));

        return v;
    }
}
