package com.manusunny.fingerlock.elements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.activity.lock.PinActivity;

public class PinButtonAdaptor extends BaseAdapter {

    private final LayoutInflater inflater;
    private final PinActivity activity;

    public PinButtonAdaptor(PinActivity activity) {
        this.activity = activity;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (position == 10)
            return 0;
        return ((position + 1) % 10);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Button view;
        if (convertView == null) {
            view = (Button) inflater.inflate(R.layout.pin_input_button, null);

        } else {
            view = (Button) convertView;
        }
        if (position == 10) {
            view.setText("0");
        } else if (position == 9) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setText(String.valueOf((position + 1) % 10));
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.handleClick((Button) v);
            }
        });
        return view;
    }


}
