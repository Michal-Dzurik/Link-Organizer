package sk.po.spse.dzurikm.linkorganizer.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import sk.po.spse.dzurikm.linkorganizer.R;

public class FilterDialog extends Dialog {

    private Context context;
    private Spinner spinner;
    private Button positiveButton,negativeButton;
    private OnPositiveButtonClick onPositiveButtonClick;
    private int selected;

    public FilterDialog(@NonNull Context context, OnPositiveButtonClick onPositiveButtonClick) {
        super(context);
        this.context = context;
        this.onPositiveButtonClick = onPositiveButtonClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filter_dialog_layout);

        spinner = (Spinner) findViewById(R.id.spinner);
        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);

        String[] paths = context.getResources().getStringArray(R.array.sort);

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                Toast.makeText(context,context.getString(R.string.you_selected) + paths[selected],Toast.LENGTH_SHORT).show();
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
