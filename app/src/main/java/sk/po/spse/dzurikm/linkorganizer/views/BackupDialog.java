package sk.po.spse.dzurikm.linkorganizer.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;

public class BackupDialog extends Dialog {

    private Context context;
    private Dialog dialog;
    private ImageButton backupButton,importButton;

    public BackupDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.backup_dialog_layout);

        this.getWindow().setDimAmount(.5f);

        backupButton = (ImageButton) findViewById(R.id.exportDatabaseButton);
        importButton = (ImageButton) findViewById(R.id.importDatabaseButton);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    ((MainActivity) context).startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
                }
            }
        });
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");

                ((MainActivity) context).startActivityForResult(chooseFile, 9998);

            }
        });

    }

}
