package sk.po.spse.dzurikm.linkorganizer.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.models.ColorSet;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnSelectChanged;

public class ColorPaletteAdapter extends ArrayAdapter<ColorSet> {
    private CardView prevSelected;
    private int currentColor;
    private boolean initFound = false;
    private OnSelectChanged listener;

    public ColorPaletteAdapter(@NonNull Context context, ArrayList<ColorSet> courseModelArrayList, OnSelectChanged listener) {
        super(context, 0, courseModelArrayList);
        this.listener = listener;
        currentColor = MainActivity.getCurrentFolderColor(context);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.color_palette_item, parent, false);

            ColorSet color = getItem(position);
            CardView colorContainer = listItemView.findViewById(R.id.color_container);
            CardView colorOverlay = listItemView.findViewById(R.id.overlay);

            Animation fadeOut = AnimationUtils.loadAnimation(getContext(),R.anim.color_palette_item_fade_out);

            Animation fadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.color_palette_item_fade_in);
            colorContainer.setCardBackgroundColor(color.getResource());
            Log.i("Hellop", "");


            if (currentColor == colorContainer.getCardBackgroundColor().getDefaultColor()){
                if (!initFound){
                    prevSelected = colorOverlay;
                    colorOverlay.setAlpha(1f);
                    colorOverlay.startAnimation(fadeIn);
                    initFound = true;
                }
            }

            System.out.println( color.getResource());

            colorContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CardView prev = prevSelected;
                    fadeOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            prev.setAlpha(0.08f);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    if (prev != null){

                        if (!prev.equals(colorOverlay)){
                            prev.startAnimation(fadeOut);

                            colorOverlay.setAlpha(1f);
                            colorOverlay.startAnimation(fadeIn);
                        }

                    }
                    else {
                        colorOverlay.setAlpha(1f);
                        colorOverlay.startAnimation(fadeIn);
                    }

                    prevSelected = colorOverlay;
                    currentColor = color.getResource();

                    listener.onChange(currentColor);

                }
            });
        }



        return listItemView;
    }

    public int getSelectedColor(){
        return currentColor;
    }
}