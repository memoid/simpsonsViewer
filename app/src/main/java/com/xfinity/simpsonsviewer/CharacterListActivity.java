package com.xfinity.simpsonsviewer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetCharactersTask().execute();
        setContentView(R.layout.activity_character_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        recyclerView = (RecyclerView) findViewById(R.id.character_list);


        if (findViewById(R.id.character_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.ViewHolder> {

        private List<RelatedTopic> relatedTopics = new ArrayList<>();

        public CharacterAdapter(List<RelatedTopic> relatedTopics) {
            this.relatedTopics.addAll(relatedTopics);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.character_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.relatedTopic = relatedTopics.get(position);
            holder.textView.setText(new Converter().convertName(holder.relatedTopic.getText()));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(CharacterDetailFragment.CHARACTER_TEXT,
                                holder.relatedTopic.getText());
                        arguments.putString(CharacterDetailFragment.CHARACTER_IMAGE,
                                holder.relatedTopic.getIcon().getURL());
                        CharacterDetailFragment fragment = new CharacterDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.character_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, CharacterDetailActivity.class);
                        intent.putExtra(CharacterDetailFragment.CHARACTER_TEXT,
                                holder.relatedTopic.getText());
                        intent.putExtra(CharacterDetailFragment.CHARACTER_IMAGE,
                                holder.relatedTopic.getIcon().getURL());

                        context.startActivity(intent);
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return relatedTopics.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView textView;
            public final View view;
            public RelatedTopic relatedTopic;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                textView = (TextView) view.findViewById(R.id.content);
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

            Call<Result> listCharacters = duckDuckService.listCharacters(
                    getString(R.string.character_url)
            );

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
            adapter = new CharacterAdapter(relatedTopics);
            recyclerView.setAdapter(adapter);
        }
    }

}
