package sk.po.spse.dzurikm.linkorganizer.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import java.net.URL;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.FolderContentActivity;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.FolderMenuListener;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;

public class FolderOptionMenuDialog extends Dialog {
    private View edit,delete;
    private TextView title;
    private Folder folder;

    private Context context;
    private FragmentManager fragmentManager;

    private FolderMenuListener listener;

    private int MAX_FOLDER_NAME_LENGTH = 30;
    private int MAX_FOLDER_DESCRIPTION_LENGTH = 35;

    public FolderOptionMenuDialog(@NonNull Context context, FragmentManager fragmentManager, Folder folder) {
        super(context);
        this.folder = folder;
        this.context = context;
        this.MAX_FOLDER_NAME_LENGTH = context.getResources().getInteger(R.integer.folder_heading_max_characters);
        this.MAX_FOLDER_DESCRIPTION_LENGTH = context.getResources().getInteger(R.integer.folder_description_max_characters);
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.folder_option_menu_dialog_layout);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.getWindow().setDimAmount(.5f);

        edit = getWindow().findViewById(R.id.editFolder);
        delete = getWindow().findViewById(R.id.deleteFolder);
        title = getWindow().findViewById(R.id.title);

        title.setText(folder.getName());

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEdit(view);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(view);
            }
        });


    }

    private boolean checkLinkText(String folderName,String folderDescription){
        if (folderName.equals("") || folderDescription.equals("")) {
            Toast.makeText(context,context.getString(R.string.You_didnt_fill_up_everything),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (folderName.length() > MAX_FOLDER_NAME_LENGTH) {
            //
            Toast.makeText(context,context.getString(R.string.folder_name_is_too_long_max) + MAX_FOLDER_NAME_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (folderDescription.length() > MAX_FOLDER_DESCRIPTION_LENGTH){
            //
            Toast.makeText(context,context.getString(R.string.folder_name_is_too_long_max) + MAX_FOLDER_DESCRIPTION_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void setListener(FolderMenuListener listener) {
        this.listener = listener;
    }
}
