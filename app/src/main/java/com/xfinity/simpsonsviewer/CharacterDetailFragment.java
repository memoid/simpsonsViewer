package com.xfinity.simpsonsviewer;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A fragment representing a single Character detail screen.
 * This fragment is either contained in a {@link CharacterListActivity}
 * in two-pane mode (on tablets) or a {@link CharacterDetailActivity}
 * on handsets.
 */
public class CharacterDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String CHARACTER_NAME = "character_name";
    public static final String CHARACTER_DESCRIPTION = "character_description";
    public static final String CHARACTER_IMAGE = "character_image";

    /**
     * The dummy content this fragment is presenting.
     */
    private String characterName, characterDesc, characterImage;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CharacterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(CHARACTER_NAME)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            characterName = getArguments().get(CHARACTER_NAME).toString();
            characterDesc = getArguments().get(CHARACTER_DESCRIPTION).toString();
            characterImage = getArguments().get(CHARACTER_IMAGE).toString();


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(characterName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.character_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (!characterDesc.equals("")) {
            ((TextView) rootView.findViewById(R.id.textDesc)).setText(characterDesc);
        }
        if (!characterImage.equals("")) {
            Glide.with(getActivity()).load(characterImage).into((ImageView) rootView.findViewById(R.id.imageCharacter));
        } else {
            String url = "http://nerdreactor.com/wp-content/uploads/2012/12/Link.jpg";
            Glide.with(getActivity()).load(url).into((ImageView) rootView.findViewById(R.id.imageCharacter));
        }

        return rootView;
    }
}
