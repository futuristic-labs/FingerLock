package com.manusunny.fingerlock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.activity.AppDetailsActivity;
import com.manusunny.fingerlock.elements.AppListAdapter;
import com.manusunny.fingerlock.utilities.AppListingUtility;

public class LockedAppsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView mListViewLocked;
    private View view;
    private AppListingUtility appListingUtility;

    public LockedAppsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app, container, false);
        mListViewLocked = (AbsListView) view.findViewById(R.id.list_locked);
        loadLockedApps();
        return view;
    }

    private void loadLockedApps() {
        appListingUtility = AppListingUtility.getInstance(getActivity());
        mListViewLocked.setAdapter(new AppListAdapter(getActivity(), appListingUtility.lockedAppInfos));
        mListViewLocked.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent = new Intent(getActivity(), AppDetailsActivity.class);
        intent.putExtra("package", appListingUtility.lockedAppInfos.get(position).packageName);
        intent.putExtra("type", "locked");
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadLockedApps();
    }
}
