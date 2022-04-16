package sk.po.spse.dzurikm.linkorganizer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.utils.ColorsUtil;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;

public class SettingsDialog extends BottomSheetDialogFragment {
    private static CardView colorPickerButton;
    private View rootView;
    private int color;
    private Context context;
    private FragmentManager fragmentManager;

    public SettingsDialog(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SettingsDialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openColorPicker(){
        ColorPickerDialog picker = new ColorPickerDialog(getContext());
        picker.setOnPickColorListener(new OnColorPickedListener() {
            @Override
            public void colorPicked(int color) {
                MainActivity.setCurrentFolderColor(context,fragmentManager,color);
                //Toast.makeText(getContext(), getContext().getString(R.string.To_apply_folder_color_change_you_need_to_restart_the_app),Toast.LENGTH_SHORT).show();
                SettingsDialog.refreshSelectedColor(context,color);
            }
        });
        picker.show(getParentFragmentManager(),"ColorPicker");

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog,style);
        rootView = View.inflate(getContext(), R.layout.settings_dialog_layput, null);
        dialog.setContentView(rootView);


        ((View) rootView.getParent()).setBackgroundColor(Color.TRANSPARENT);
        dialog.getWindow().setDimAmount(0.0f);

        colorPickerButton = (CardView) rootView.findViewById(R.id.folderColorPickerButton);
        colorPickerButton.setCardBackgroundColor(ColorsUtil.getCurrentFolderColor(context));

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
