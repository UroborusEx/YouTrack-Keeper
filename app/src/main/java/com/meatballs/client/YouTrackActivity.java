package com.meatballs.client;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.meatballs.data.RequestFailedException;
import com.meatballs.data.YouTrackDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YouTrackActivity extends ListActivity {
    private static final int SUCCESS_CODE = 200;
    public static final YouTrackDAO dao = new YouTrackDAO();
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private IssueListAdapter dataAdapter;
    private String query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.youtrack_prefrences, false);
        login();
        
        query = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.user_filter_preference), "");
        updateQuery(false, false);

        final ListView lv = getListView();

        dataAdapter = new IssueListAdapter(this, data);
        lv.setAdapter(dataAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                String issueId = dataAdapter.getIssueId(position);
                if (issueId != null) {
                    Uri data = dao.getIssueUri(issueId);
                    Intent viewIssue = new Intent()
                            .setClass(YouTrackActivity.this, IssueActivity.class)
                            .setAction(Intent.ACTION_VIEW)
                            .setData(data);
                    startActivity(viewIssue);
                } else {
                    Toast.makeText(YouTrackActivity.this, "Unknown issue", Toast.LENGTH_SHORT).show();
                }
            }
        });        
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            updateQuery(true, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.youtrack_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit_item:
                finish();
                return true;
            case R.id.update_item:
                updateQuery(true, true);
                return true;
            case R.id.filter_item:
                onSearchRequested();
                return true;
            case R.id.options_item:
                Intent preferencesIntent = new Intent().setClass(this, YouTrackPreference.class);
                startActivityForResult(preferencesIntent, SUCCESS_CODE);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // The preferences returned if the request code is what we had given
        // earlier in startSubActivity
        if (requestCode == SUCCESS_CODE) {
            login();
            updateQuery(true, true);
        }
    }

    @Override
    public boolean onSearchRequested() {
        startSearch(query, false, null, false);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void login() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            String login = preferences.getString(getString(R.string.user_name_preference), null);
            String pass = preferences.getString(getString(R.string.user_password_preference), null);
            String host = preferences.getString(getString(R.string.host_url_preference_title), null);
            dao.login(host, login, pass);
        } catch (RequestFailedException e) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Login failed");
            builder1.setMessage("Check your credentials and YouTrack URL");
            builder1.setCancelable(true);
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void updateQuery(boolean refreshView, boolean reload) {
        data.clear();
        try {
            data.addAll(dao.getIssues(query, 0, 20, reload));
        } catch (RequestFailedException ignore) {
            //ignore
        }
        if (refreshView) {
            dataAdapter.notifyDataSetChanged();
        }
    }

}
