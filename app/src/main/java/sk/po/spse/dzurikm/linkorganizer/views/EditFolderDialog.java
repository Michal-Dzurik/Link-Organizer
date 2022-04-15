package sk.po.spse.dzurikm.linkorganizer.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.Listener;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;

public class EditFolderDialog extends Dialog {
    private Button positiveButton,negativeButton;
    private ImageButton setFolderColorButton;
    private EditText nameInput,descriptionInput;
    private Folder folder;
    private CardView folderBackground,folderBookmark;

    private Context context;
    private FragmentManager fragmentManager;

    private Listener afterPositiveButtonClick,afterNegativeButtonClick;

    private int MAX_FOLDER_NAME_LENGTH = 30;
    private int MAX_FOLDER_DESCRIPTION_LENGTH = 35;

    public EditFolderDialog(@NonNull Context context, FragmentManager fragmentManager, Folder folder) {
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
        setContentView(R.layout.edit_folder_dialog_layout);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.getWindow().setDimAmount(.5f);

        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);
        setFolderColorButton = (ImageButton) findViewById(R.id.setFolderColorButton);

        nameInput = (EditText) findViewById(R.id.folderNameInput);
        descriptionInput = (EditText) findViewById(R.id.folderDescriptionInput);
        folderBackground = (CardView) findViewById(R.id.folder_background);
        folderBookmark = (CardView)findViewById(R.id.folder_bookmark);

        nameInput.setText(folder.getName());
        descriptionInput.setText(folder.getDescription());

        if (folder.getColorId() != -1) {
            folderBackground.setCardBackgroundColor(folder.getColorId());
            folderBookmark.setCardBackgroundColor(MainActivity.lighten(folder.getColorId(), 0.85F));
        }
        else {
            folderBackground.setCardBackgroundColor(MainActivity.getCurrentFolderColor(context));
            folderBookmark.setCardBackgroundColor(MainActivity.lighten(MainActivity.getCurrentFolderColor(context), 0.85F));
        }

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString().trim(),
                        description = descriptionInput.getText().toString().trim();
                if (checkLinkText(name,description)){
                    EditFolderDialog.this.dismiss();

                    folder.setName(name);
                    folder.setDescription(description.equals("") ? null : description);
                    folder.setColorId(folderBackground.getCardBackgroundColor().getDefaultColor());

                    MainActivity.editFolder(folder);
                    EditFolderDialog.this.dismiss();
                    afterPositiveButtonClick.perform();
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditFolderDialog.this.dismiss();
                afterNegativeButtonClick.perform();
            }
        });

        setFolderColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context,context.getString(R.string.select_link_color));
                colorPickerDialog.setOnPickColorListener(new OnColorPickedListener() {
                    @Override
                    public void colorPicked(int color) {
                        // Color is picked
                        Log.d("COLOR PICKED",String.valueOf(color));
                        folderBackground.setCardBackgroundColor(color);
                        folderBookmark.setCardBackgroundColor(MainActivity.lighten(color,0.85f));
                    }
                });
                colorPickerDialog.show(fragmentManager,"ColorPicker");
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
            Toast.makeText(context,context.getString(R.string.folder_description_is_too_long_max) + MAX_FOLDER_DESCRIPTION_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }


    public void setAfterPositiveButtonClick(Listener listener) {
        afterPositiveButtonClick = listener;
    }

    public void setAfterNegativeButtonClick(Listener listener) {
        afterNegativeButtonClick = listener;
    }
}
