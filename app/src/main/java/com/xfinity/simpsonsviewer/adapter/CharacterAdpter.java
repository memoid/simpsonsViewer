package com.xfinity.simpsonsviewer.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xfinity.simpsonsviewer.CharacterDetailActivity;
import com.xfinity.simpsonsviewer.CharacterDetailFragment;
import com.xfinity.simpsonsviewer.R;
import com.xfinity.simpsonsviewer.entity.CharacterEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by memo on 5/17/16.
 */
public class CharacterAdpter extends RecyclerView.Adapter<CharacterAdpter.ViewHolder> {

    public List<CharacterEntity> characterEntities = new ArrayList<>();
    private boolean twoPane;
    private AppCompatActivity activity;

    public CharacterAdpter(AppCompatActivity activity, boolean twoPane) {
        this.twoPane = twoPane;
        this.activity = activity;
    }

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
                if (twoPane) {
                    System.out.println(":::::Entra a twopane adapter");
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
                    activity.getSupportFragmentManager().beginTransaction()
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
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);

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

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView textView;
        public final View view;
        public CharacterEntity characterEntity;

        public ViewHolder (View view) {
            super(view);
            this.view = view;
            textView = (TextView) view.findViewById(R.id.content);
        }

    }

}
