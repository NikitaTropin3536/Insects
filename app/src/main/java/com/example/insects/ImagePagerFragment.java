package com.example.insects;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.Map;

public class ImagePagerFragment extends Fragment {

    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewPager = (ViewPager) inflater.inflate(R.layout.fragment_pager,
                container, false);

        viewPager.setAdapter(new ImagePagerAdapter(this));

        viewPager.setCurrentItem(MainActivity.currentPosition);
        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset,
                                               int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        MainActivity.currentPosition = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                }
        );

        prepareSharedElementTransition();

        if (savedInstanceState == null) {
            postponeEnterTransition();
        }

        return viewPager;
    }

    private void prepareSharedElementTransition() {

        Transition transition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.image_shared_element_transition);

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
                Fragment currentFragment = (Fragment) viewPager.getAdapter()
                        .instantiateItem(viewPager, MainActivity.currentPosition);

                View view = currentFragment.getView();

                if (view == null) {
                    return;
                }

                sharedElements.put(
                        names.get(0),
                        view.findViewById(R.id.card_image)
                );
            }
        });
    }
}