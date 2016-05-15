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

import com.xfinity.simpsonsviewer.entity.CharacterEntity;
import com.xfinity.simpsonsviewer.entity.DBCharacterHelper;
import com.xfinity.simpsonsviewer.entity.RelatedTopic;
import com.xfinity.simpsonsviewer.entity.Result;
import com.xfinity.simpsonsviewer.service.DuckDuckService;
import com.xfinity.simpsonsviewer.util.Converter;

import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    RecyclerView recyclerView;
    CharacterAdapter adapter;
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


        dbHelper = new DBCharacterHelper(this);
        adapter = new CharacterAdapter();

        if (dbHelper.getAllCharacters().isEmpty()) {
            new GetCharactersTask().execute();
        } else {
            adapter.characterEntities = dbHelper.getAllCharacters();
        }

        setContentView(R.layout.activity_character_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = (RecyclerView) findViewById(R.id.character_list);
        recyclerView.setAdapter(adapter);


        if (findViewById(R.id.character_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

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

    public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

        private List<CharacterEntity> characterEntities = new ArrayList<>();

        public CharacterAdapter(){}

        /*public CharacterAdapter(List<CharacterEntity> characterEntities) {
            this.characterEntities.addAll(characterEntities);
        }*/

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.character_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.characterEntity = characterEntities.get(position);
            holder.textView.setText(holder.characterEntity.getName());

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CharacterDetailFragment.CHARACTER_NAME,
                                holder.characterEntity.getName());
                        arguments.putString(CharacterDetailFragment.CHARACTER_DESCRIPTION,
                                holder.characterEntity.getDescription());
                        arguments.putString(CharacterDetailFragment.CHARACTER_IMAGE,
                                holder.characterEntity.getUrl());
                        CharacterDetailFragment fragment = new CharacterDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.right_in, 0)
                                .replace(R.id.character_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CharacterDetailActivity.class);
                        intent.putExtra(CharacterDetailFragment.CHARACTER_NAME,
                                holder.characterEntity.getName());
                        intent.putExtra(CharacterDetailFragment.CHARACTER_DESCRIPTION,
                                holder.characterEntity.getDescription());
                        intent.putExtra(CharacterDetailFragment.CHARACTER_IMAGE,
                                holder.characterEntity.getUrl());

                        context.startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (characterEntities == null) {
                return 0;
            }
            return characterEntities.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView textView;
            public final View view;
            public CharacterEntity characterEntity;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textView = (TextView) view.findViewById(R.id.content);
            }

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


            //return result != null ? result.getRelatedTopics() : null;
            return dbHelper.getAllCharacters();

        }

        @Override
        protected void onPostExecute(List<CharacterEntity> characterEntities) {
            System.out.println("Post Excecute");
            adapter.characterEntities = characterEntities;// = new CharacterAdapter(characterEntities);
            recyclerView.setAdapter(adapter);
        }
    }

}
