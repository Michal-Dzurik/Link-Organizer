package sk.po.spse.dzurikm.linkorganizer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsDialog extends BottomSheetDialogFragment {
    private static CardView colorPickerButton;
    private View rootView;
    private int color;
    private Context context;

    public SettingsDialog(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openColorPicker(){
        ColorPicker picker = new ColorPicker(getContext());
        picker.show(getParentFragmentManager(),"ColorPicker");

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
        colorPickerButton.setCardBackgroundColor(MainActivity.getCurrentFolderColor(context));

        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openColorPicker();
            }
        });
    }

    public static void refreshSelectedColor(Context context,int res){
        colorPickerButton.setCardBackgroundColor(res);
    }

}
