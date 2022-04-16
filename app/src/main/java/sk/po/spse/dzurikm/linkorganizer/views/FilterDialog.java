package sk.po.spse.dzurikm.linkorganizer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.utils.ColorsUtil;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnPositiveButtonClick;

public class FilterDialog extends Dialog {

    private Context context;
    private Spinner spinner;
    private MaterialButton positiveButton,negativeButton;
    private OnPositiveButtonClick onPositiveButtonClick;
    private int selected;

    public FilterDialog(@NonNull Context context, OnPositiveButtonClick onPositiveButtonClick) {
        super(context);
        this.context = context;
        this.onPositiveButtonClick = onPositiveButtonClick;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_dialog_layout);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setDimAmount(.5f);

        spinner = (Spinner) findViewById(R.id.spinner);
        positiveButton = (MaterialButton) findViewById(R.id.positiveButton);
        negativeButton = (MaterialButton) findViewById(R.id.negativeButton);

        positiveButton.getBackground().setTint(ColorsUtil.getCurrentFolderColor(context));
        positiveButton.setRippleColor(ColorStateList.valueOf(ColorsUtil.lighten(ColorsUtil.getCurrentFolderColor(context),.75f)));
        negativeButton.setRippleColor(ColorStateList.valueOf(ColorsUtil.lighten(context.getResources().getInteger(R.color.gentle_grey),.4f)));


        String[] paths = context.getResources().getStringArray(R.array.sort);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,context.getString(R.string.you_selected) + " " + paths[selected],Toast.LENGTH_SHORT).show();
                onPositiveButtonClick.callback(selected);
                FilterDialog.this.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog.this.dismiss();
            }
        });

    }

}
