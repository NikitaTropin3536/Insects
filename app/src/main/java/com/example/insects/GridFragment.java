package com.example.insects;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class GridFragment extends Fragment {

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_grid,
                container, false);

        recyclerView.setAdapter(new GridAdapter(this));

        prepareTransitions();
        postponeEnterTransition();

        return recyclerView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }

    private void scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);

                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                View viewAtPosition = layoutManager.findViewByPosition(MainActivity.currentPosition);

                if (viewAtPosition != null ||
                        layoutManager.isViewPartiallyVisible(viewAtPosition,
                                false, false)) {
                    recyclerView.post(
                            () -> layoutManager.scrollToPosition(MainActivity.currentPosition)
                    );
                }
            }
        });
    }

    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);

                RecyclerView.ViewHolder selectedViewHolder = recyclerView
                        .findViewHolderForAdapterPosition(MainActivity.currentPosition);

                if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                    return;
                }

                sharedElements.put(
                        names.get(0),
                        selectedViewHolder.itemView.findViewById(R.id.card_image)
                );
            }
        });
    }
}