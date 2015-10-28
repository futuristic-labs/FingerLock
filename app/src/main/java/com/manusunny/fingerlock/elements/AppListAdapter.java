package com.manusunny.fingerlock.elements;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.manusunny.fingerlock.R;

import java.util.ArrayList;

public class AppListAdapter extends ArrayAdapter {
    private final Activity context;
    ArrayList<ApplicationInfo> apps;

    public AppListAdapter(Activity context, ArrayList<ApplicationInfo> apps) {
        super(context, R.layout.app_list_item, apps);
        this.context = context;
        this.apps = apps;
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return apps.get(position);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ApplicationInfo applicationInfo = apps.get(position);
        RecyclerView.ViewHolder holder;

        if(view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.app_list_item, parent, false);
            holder = new RecyclerView.ViewHolder(view) {
                @Override
                public String toString() {
                    return super.toString();
                }
            };
            view.setTag(holder);
        }
        TextView txtTitle = (TextView) view.findViewById(R.id.app_list_item_title);
        ImageView imageView = (ImageView) view.findViewById(R.id.app_list_item_image);

        PackageManager packageManager = context.getPackageManager();
        txtTitle.setText(packageManager.getApplicationLabel(applicationInfo));
        int color = (position % 2 == 0) ? Color.parseColor("#ccddef") : Color.parseColor("#d6e4f2");
        view.setBackgroundColor(color);
        imageView.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
        return view;
    }
}
