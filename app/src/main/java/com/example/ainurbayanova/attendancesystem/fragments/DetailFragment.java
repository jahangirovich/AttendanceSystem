package com.example.ainurbayanova.attendancesystem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ainurbayanova.attendancesystem.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;



public class DetailFragment extends Fragment {
    View view;
    CarouselView carouselView;
    TextView name;
    TextView desc;
    TextView place;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        initWidgets();
        initCarousel();
        return view;
    }

    public void initWidgets() {
        carouselView = view.findViewById(R.id.carouselView);
        name = view.findViewById(R.id.name);
        desc = view.findViewById(R.id.desc);
        place = view.findViewById(R.id.place);
    }

    public void initCarousel() {
        carouselView.setPageCount(5);
        carouselView.setViewListener(new ViewListener() {
            @Override
            public View setViewForPosition(int position) {
                View carousel_view = LayoutInflater.from(getActivity()).inflate(R.layout.design_of_carousel_image, null);
                ImageView imageView = carousel_view.findViewById(R.id.image);
                Glide.with(getActivity())
                        .load("https://thumbs-prod.si-cdn.com/gBlfvWEC9NMf2sk6n8LlxPggLQE=/800x600/filters:no_upscale()/https://public-media.si-cdn.com/filer/c2/35/c235a6aa-8985-40e6-ad19-0e46269867c5/biddulph-grange-4.jpg")
                        .into(imageView);
                return carousel_view;
            }
        });
    }
}
