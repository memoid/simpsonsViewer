package com.xfinity.simpsonsviewer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xfinity.simpsonsviewer.adapter.CharacterAdpter;
import com.xfinity.simpsonsviewer.entity.CharacterEntity;
import com.xfinity.simpsonsviewer.entity.DBCharacterHelper;
import com.xfinity.simpsonsviewer.entity.RelatedTopic;
import com.xfinity.simpsonsviewer.entity.Result;
import com.xfinity.simpsonsviewer.service.DuckDuckService;
import com.xfinity.simpsonsviewer.util.Converter;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

import static retrofit2.converter.gson.GsonConverterFactory.create;

/**
 * An activity representing a list of Characters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CharacterDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CharacterListActivity extends AppCompatActivity {

    private boolean mTwoPane, isGrid;
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
            adapter.characterEntities = dbHelper.getAllCharacters();
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
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case R.id.grid_toggle:
                isGrid = !isGrid;
                adapter.isImage = !isGrid;
                //supportInvalidateOptionsMenu();
                recyclerView.setLayoutManager(isGrid ? new LinearLayoutManager(this) : new GridLayoutManager(this, 3));
                adapter.notifyDataSetChanged();
                return true;
            case R.id.favorites:
                startActivity(new Intent(this, FavoriteCharacterListActivity.class));
            default:
                return false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
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

            if (isConnected) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.DATA_API)
                        .addConverterFactory(create())
                        .build();
                DuckDuckService duckDuckService = retrofit.create(DuckDuckService.class);

                Call<Result> listCharacters = duckDuckService.listCharacters(
                        getString(R.string.character_url)
                );

                Result result = null;

                try {
                    result = listCharacters.execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (RelatedTopic relatedTopic : result.getRelatedTopics()) {
                    dbHelper.insert(new Converter().convertName(relatedTopic.getText()),
                            new Converter().convertDescription(relatedTopic.getText()),
                            relatedTopic.getIcon().getURL());
                }

            }


            return dbHelper.getAllCharacters();

        }

        @Override
        protected void onPostExecute(List<CharacterEntity> characterEntities) {
            adapter.characterEntities = characterEntities;
            recyclerView.setAdapter(adapter);
        }
    }

}
