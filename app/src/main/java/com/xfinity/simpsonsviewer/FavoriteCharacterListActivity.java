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

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of FavoriteCharacters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FavoriteCharacterDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FavoriteCharacterListActivity extends AppCompatActivity {

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
            System.out.println("Entra a getAllFavorites");
            adapter.characterEntities = dbHelper.getAllFavorites();
        }

        setContentView(R.layout.activity_character_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = (RecyclerView) findViewById(R.id.character_list);
        recyclerView.setAdapter(adapter);


        if (findViewById(R.id.character_detail_container) != null) {
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
                        arguments.putString(CharacterDetailFragment.FAVORITE,
                                holder.characterEntity.getIsFavorite());
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
                        intent.putExtra(CharacterDetailFragment.FAVORITE,
                                holder.characterEntity.getIsFavorite());

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
            //return result != null ? result.getRelatedTopics() : null;
            return dbHelper.getAllFavorites();

        }

        @Override
        protected void onPostExecute(List<CharacterEntity> characterEntities) {
            System.out.println("Post Excecute");
            adapter.characterEntities = characterEntities;// = new CharacterAdapter(characterEntities);
            recyclerView.setAdapter(adapter);
        }
    }

}
