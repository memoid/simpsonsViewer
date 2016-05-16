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

public class FavoriteCharacterDetailFragment extends Fragment {

    public static final String CHARACTER_NAME = "character_name";
    public static final String CHARACTER_DESCRIPTION = "character_description";
    public static final String CHARACTER_IMAGE = "character_image";

    private String characterName, characterDesc, characterImage;

    public FavoriteCharacterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(CHARACTER_NAME)) {
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
