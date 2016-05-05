package com.xfinity.simpsonsviewer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xfinity.simpsonsviewer.dummy.DummyContent;
import com.xfinity.simpsonsviewer.entity.RelatedTopic;
import com.xfinity.simpsonsviewer.entity.Result;
import com.xfinity.simpsonsviewer.service.DuckDuckService;

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
    private List<RelatedTopic> characters = new ArrayList<>();
    View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetCharactersTask().execute();
        setContentView(R.layout.activity_character_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = findViewById(R.id.character_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.character_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(characters));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.Character> characters = new ArrayList<DummyContent.Character>() {
        };

        public SimpleItemRecyclerViewAdapter(List<RelatedTopic> items) {
            for (RelatedTopic rt : items) {
                characters.add(new DummyContent.Character(rt.getFirstURL(), rt.getText(), rt.getIcon().getURL()));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.character_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = characters.get(position);
            holder.mIdView.setText(characters.get(position).id);
            holder.mContentView.setText(characters.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CharacterDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        CharacterDetailFragment fragment = new CharacterDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.character_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CharacterDetailActivity.class);
                        intent.putExtra(CharacterDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return characters.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.Character mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private class GetCharactersTask extends AsyncTask<Void, Void, List<RelatedTopic>> {

        @Override
        protected List<RelatedTopic> doInBackground(Void... params) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.DATA_API)
                    .addConverterFactory(create())
                    .build();
            DuckDuckService duckDuckService = retrofit.create(DuckDuckService.class);

            Call<Result> listCharacters = duckDuckService.listCharacters("simpsons characters");

            Result result = null;

            try {
                result = listCharacters.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result != null ? result.getRelatedTopics() : null;

        }

        @Override
        protected void onPostExecute(List<RelatedTopic> relatedTopics) {
            characters = relatedTopics;
            setupRecyclerView((RecyclerView) recyclerView);
        }
    }

}
