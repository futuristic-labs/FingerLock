package com.manusunny.fingerlock.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.manusunny.fingerlock.R;
import com.manusunny.fingerlock.elements.AppListAdapter;
import com.manusunny.fingerlock.model.App;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.manusunny.fingerlock.activity.MainActivity.appService;

public class AppFragment extends Fragment implements AbsListView.OnItemClickListener {

    private HashSet<String> lockedAppNames;
    private ArrayList<ApplicationInfo> lockedAppInfos;
    private HashSet<String> installedAppNames;

    public AppFragment() {
        /**
         * Mandatory empty constructor for the fragment manager to instantiate the
         * fragment (e.g. upon screen orientation changes).
         */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app, container, false);
        AbsListView mListViewLocked = (AbsListView) view.findViewById(R.id.list_locked);
        mListViewLocked.setAdapter(new AppListAdapter(getActivity(), lockedAppInfos));
        mListViewLocked.setOnItemClickListener(this);
        return view;
    }

    private void prepareList() {
        installedAppNames = new HashSet<>(0);
        lockedAppInfos = new ArrayList<>(0);
        PackageManager packageManager = getActivity().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resInfos = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfos) {
            installedAppNames.add(resolveInfo.activityInfo.packageName);
        }
//        appService.addApp("com.manusunny.fingerlock", "false", "false", "false");
//        appService.addApp(resInfos.get(1).activityInfo.packageName, "false", "false", "false");
//        appService.addApp(resInfos.get(2).activityInfo.packageName, "false", "false", "false");
//        appService.addApp(resInfos.get(3).activityInfo.packageName, "false", "false", "false");
//        installedAppNames.remove("com.manusunny.fingerlock");

        processPackageNames();
        prepareAppInfo(packageManager);
        Collections.sort(lockedAppInfos, new ApplicationInfo.DisplayNameComparator(packageManager));
    }

    private void processPackageNames() {
        ArrayList<App> allApps = appService.getAllApps();
        lockedAppNames = new HashSet<>();
        for (App app : allApps) {
            if (installedAppNames.contains(app.getPackageName())) {
                lockedAppNames.add(app.getPackageName());
                installedAppNames.remove(app.getPackageName());
            } else {
                appService.removeApp(app.getId());
            }
        }
    }

    private void prepareAppInfo(PackageManager packageManager) {
        for (String packageName : lockedAppNames) {
            try {
                lockedAppInfos.add(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
            } catch (PackageManager.NameNotFoundException e) {
                //Do Nothing
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
