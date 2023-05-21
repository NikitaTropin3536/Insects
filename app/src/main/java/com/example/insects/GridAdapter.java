package com.example.insects;

import  android.graphics.drawable.Drawable;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.atomic.AtomicBoolean;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    private interface ViewHolderListener {
        void onLoadCompleted(CircleImageView circleImageView,
                             int adapterPosition);
        void onItemClicked(View view, int adapterPosition);
    }

    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    public GridAdapter(Fragment fragment) {
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);
        return new ImageViewHolder(view,
                requestManager,
                viewHolderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return ImageData.IMAGE_DRAWABLES.length;
    }

    private static class ViewHolderListenerImpl implements ViewHolderListener {

        private Fragment fragment;

        private AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }

        @Override
        public void onLoadCompleted(CircleImageView circleImageView,
                                    int adapterPosition) {
            if (MainActivity.currentPosition != adapterPosition) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        @Override
        public void onItemClicked(View view, int adapterPosition) {
            MainActivity.currentPosition = adapterPosition;

            ((TransitionSet ) fragment.getExitTransition()).excludeTarget(view, true);

            CircleImageView itransitioningView = view.findViewById(R.id.card_image);

            fragment.getFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addSharedElement(itransitioningView,
                            itransitioningView.getTransitionName())
                    .replace(R.id.fragment_container,
                            new ImagePagerFragment(),
                            ImagePagerFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CircleImageView circleImageView;
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;

        public ImageViewHolder(@NonNull View itemView,
                               RequestManager requestManager,
                               ViewHolderListener viewHolderListener) {
            super(itemView);

            this.circleImageView = itemView.findViewById(R.id.card_image);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;

            itemView.findViewById(R.id.card_view)
                    .setOnClickListener(this);
        }

        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            circleImageView.setTransitionName(String.valueOf(
                    ImageData.IMAGE_DRAWABLES[adapterPosition]));
        }

        void setImage(final int adapterPosition) {
            requestManager
                    .load(adapterPosition)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(circleImageView, adapterPosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(circleImageView, adapterPosition);
                            return false;
                        }
                    })
                    .into(circleImageView);
        }

        @Override
        public void onClick(View v) {
            viewHolderListener.onItemClicked(v, getAdapterPosition());
        }
    }
}