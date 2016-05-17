package com.xfinity.simpsonsviewer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xfinity.simpsonsviewer.adapter.CharacterAdpter;
import com.xfinity.simpsonsviewer.entity.CharacterEntity;
import com.xfinity.simpsonsviewer.entity.DBCharacterHelper;

import java.util.ArrayList;
import java.util.List;

public class FavoriteCharacterListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    RecyclerView recyclerView;
    CharacterAdpter adapter;
    DBCharacterHelper dbHelper;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork!=null) {
            isConnected = activeNetwork.isConnectedOrConnecting();
        } else {
            isConnected = false;
        }

        setContentView(R.layout.activity_character_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.character_detail_container) != null) {
            mTwoPane = true;
        }

        dbHelper = new DBCharacterHelper(this);
        adapter = new CharacterAdpter(this, mTwoPane);


        if (dbHelper.getAllCharacters().isEmpty()) {
            new GetCharactersTask().execute();
        } else {
            adapter.characterEntities = dbHelper.getAllFavorites();
        }

        recyclerView = (RecyclerView) findViewById(R.id.character_list);
        recyclerView.setAdapter(adapter);


        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            adapter.characterEntities = dbHelper.searchCharacters(query);
            adapter.notifyDataSetChanged();
            //recyclerView.setAdapter(adapter);
        }

    }

    private class GetCharactersTask extends AsyncTask<Void, Void, List<CharacterEntity>> {

        @Override
        protected List<CharacterEntity> doInBackground(Void... params) {
            return dbHelper.getAllFavorites();

        }

        @Override
        protected void onPostExecute(List<CharacterEntity> characterEntities) {
            adapter.characterEntities = characterEntities;
            recyclerView.setAdapter(adapter);
        }
    }

}

