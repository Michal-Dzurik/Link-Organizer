package sk.po.spse.dzurikm.linkorganizer.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnPositiveButtonClick;

public class AlertDialog extends Dialog {
    private Context context;
    private TextView heading,text;
    private Button positiveButton,negativeButton;

    public AlertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog_layout);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.heading = getWindow().findViewById(R.id.heading);
        this.text = getWindow().findViewById(R.id.text);
        this.positiveButton = getWindow().findViewById(R.id.positiveButton);
        this.negativeButton = getWindow().findViewById(R.id.negativeButton);
    }

    public void setHeading(String heading) {
        this.heading.setText(heading);
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void setOnPositiveButtonClick(DialogInterface.OnClickListener l){
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l.onClick(AlertDialog.this,0);
            }
        });
    }
    public void setOnNegativeButtonClick(DialogInterface.OnClickListener l){
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                l.onClick(AlertDialog.this,0);
            }
        });
    }

    public void setPositiveButtonText(String text){
        this.positiveButton.setText(text);
    }

    public void setNegativeButtonText(String text){
        this.negativeButton.setText(text);
    }

}
