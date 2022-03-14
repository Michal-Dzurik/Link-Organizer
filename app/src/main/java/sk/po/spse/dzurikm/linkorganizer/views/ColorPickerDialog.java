package sk.po.spse.dzurikm.linkorganizer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.adapters.ColorPaletteAdapter;
import sk.po.spse.dzurikm.linkorganizer.models.ColorSet;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnPositiveButtonClick;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnSelectChanged;

public class ColorPickerDialog extends DialogFragment {
    private View rootView;
    int[] colorNumberArray;
    String[] colorNameArray;
    Context context;
    ArrayList<ColorSet> colorSetArrayList;
    int selectedColor;

    Button positiveButton,negativeButton;

    GridView color_grid;

    public ColorPickerDialog(Context context) {
        this.context = context;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        super.setupDialog(dialog,style);
        rootView = View.inflate(getContext(), R.layout.color_picker_dialog_layput, null);
        dialog.setContentView(rootView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        positiveButton = dialog.findViewById(R.id.positiveButton);
        negativeButton = dialog.findViewById(R.id.negativeButton);

        loadColors();

        color_grid = dialog.findViewById(R.id.color_grid);
        ColorPaletteAdapter adapter = new ColorPaletteAdapter(getContext(), colorSetArrayList, new OnSelectChanged() {
            @Override
            public void onChange(int res) {
                // change color
                selectedColor = res;
            }
        });
        color_grid.setAdapter(adapter);

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                MainActivity.setCurrentFolderColor(context,selectedColor);
                //Toast.makeText(getContext(), getContext().getString(R.string.To_apply_folder_color_change_you_need_to_restart_the_app),Toast.LENGTH_SHORT).show();
                SettingsDialog.refreshSelectedColor(context,selectedColor);
            }
        });

    }

    private void loadColors(){
        colorNumberArray = context.getResources().getIntArray(R.array.color_palette_values);
        //fill  colorNameArray with values from the colorNameArray Array in strings.xml
        colorNameArray = context.getResources().getStringArray(R.array.color_palette_keys);
        colorSetArrayList = new ArrayList<>();

        for (int i = 0; i < colorNumberArray.length; i++) {
            colorSetArrayList.add(new ColorSet(colorNameArray[i],colorNumberArray[i] ));
        }

    }
}
