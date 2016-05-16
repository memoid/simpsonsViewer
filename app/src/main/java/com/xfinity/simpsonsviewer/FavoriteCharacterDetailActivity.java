package com.xfinity.simpsonsviewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * An activity representing a single FavoriteCharacter detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FavoriteCharacterListActivity}.
 */
public class FavoriteCharacterDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(CharacterDetailFragment.CHARACTER_NAME,
                    getIntent().getStringExtra(CharacterDetailFragment.CHARACTER_NAME));
            arguments.putString(CharacterDetailFragment.CHARACTER_DESCRIPTION,
                    getIntent().getStringExtra(CharacterDetailFragment.CHARACTER_DESCRIPTION));
            arguments.putString(CharacterDetailFragment.CHARACTER_IMAGE,
                    getIntent().getStringExtra(CharacterDetailFragment.CHARACTER_IMAGE));
            arguments.putString(CharacterDetailFragment.FAVORITE,
                    getIntent().getStringExtra(CharacterDetailFragment.FAVORITE));
            CharacterDetailFragment fragment = new CharacterDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.character_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, CharacterListActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
}
