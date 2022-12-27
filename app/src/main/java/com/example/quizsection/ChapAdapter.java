package com.example.quizsection;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ChapAdapter extends BaseAdapter {

    private List<String> chapList;

    public ChapAdapter(List<String> chapList) {
        this.chapList = chapList;
    }

    @Override
    public int getCount() {
        return chapList.size();
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
                Intent intent = new Intent(viewGroup.getContext(), QuizQuestions.class);
                intent.putExtra("CHAP_ID", i+1);
                viewGroup.getContext().startActivity(intent);
            }
        });

        ((TextView) v.findViewById(R.id.itemName)).setText(chapList.get(i));

        return v;
    }
}
