package sk.po.spse.dzurikm.linkorganizer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsDialog extends BottomSheetDialogFragment {
    private CardView colorPickerButton;
    private View rootView;
    private int color;

    public static SettingsDialog newInstance() {
        SettingsDialog fragment = new SettingsDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openColorPicker(){
        // TODO: Make your own Color Picker and add just like 4 colors or so

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog,style);
        rootView = View.inflate(getContext(), R.layout.settings_dialog_layput, null);
        dialog.setContentView(rootView);

        //dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        dialog.getWindow().setDimAmount(0.0f);

        colorPickerButton = (CardView) rootView.findViewById(R.id.folderColorPickerButton);
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openColorPicker();
            }
        });
    }

}
