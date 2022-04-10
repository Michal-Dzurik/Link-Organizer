package sk.po.spse.dzurikm.linkorganizer.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import java.net.URL;
import java.util.LinkedList;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.FolderContentActivity;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.adapters.FolderSpinnerAdapter;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;
import sk.po.spse.dzurikm.linkorganizer.models.Link;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;

public class AddLinkDialog extends Dialog {
    private Button positiveButton,negativeButton;
    private EditText nameInput,descriptionInput,hrefInput;
    private String link;
    private CardView colorCircle;
    private Spinner folderSpinner;

    private Context context;
    private FragmentManager fragmentManager;

    private int MAX_LINK_NAME_LENGTH = 30;
    private int MAX_LINK_DESCRIPTION_LENGTH = 35;

    private int folderIdSelected = -1;

    public AddLinkDialog(@NonNull Context context, FragmentManager fragmentManager, String link, int MAX_LINK_NAME_LENGTH, int MAX_LINK_DESCRIPTION_LENGTH) {
        super(context);
        this.link = link;
        this.context = context;
        this.MAX_LINK_NAME_LENGTH = MAX_LINK_NAME_LENGTH;
        this.MAX_LINK_DESCRIPTION_LENGTH = MAX_LINK_DESCRIPTION_LENGTH;
        this.fragmentManager = fragmentManager;
    }

    public AddLinkDialog(@NonNull Context context, FragmentManager fragmentManager, String link) {
        super(context);
        this.link = link;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_link_dialog_layout);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.getWindow().setDimAmount(.5f);

        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);

        nameInput = (EditText) findViewById(R.id.linkName);
        descriptionInput = (EditText) findViewById(R.id.linkDescription);
        hrefInput = (EditText) findViewById(R.id.linkHref);
        colorCircle = (CardView) findViewById(R.id.colorCircle);
        folderSpinner = (Spinner) findViewById(R.id.folder_spinner);

        hrefInput.setText(link);
        colorCircle.setCardBackgroundColor(MainActivity.getCurrentFolderColor(context));

        LinkedList<Folder> folders = MainActivity.getAllFolders();
        folders.addFirst(new Folder(0,context.getString(R.string.select_link_folder),"",0));
        folderSpinner.setAdapter(new FolderSpinnerAdapter(context,folders));
        folderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) folderIdSelected = -1;
                else folderIdSelected = folders.get(i).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                folderSpinner.setSelection(0);
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Link link = new Link();
                String name = nameInput.getText().toString().trim(),
                        description = descriptionInput.getText().toString().trim(),
                        href = hrefInput.getText().toString().trim();
                if (checkLinkText(name,description,href)){
                    if (folderIdSelected != -1){
                        AddLinkDialog.this.dismiss();

                        link.setName(name);
                        link.setDescription(description);
                        link.setHref(href);
                        link.setColorId(colorCircle.getCardBackgroundColor().getDefaultColor());
                        link.setFolderId(folderIdSelected);

                        MainActivity.addLink(link);
                    }
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLinkDialog.this.dismiss();
            }
        });

        colorCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context,context.getString(R.string.select_link_color));
                colorPickerDialog.setOnPickColorListener(new OnColorPickedListener() {
                    @Override
                    public void colorPicked(int color) {
                        // Color is picked
                        Log.d("COLOR PICKED",String.valueOf(color));
                        colorCircle.setCardBackgroundColor(color);
                    }
                });
                colorPickerDialog.show(fragmentManager,"ColorPicker");
            }
        });

    }

    private boolean checkLinkText(String linkName,String linkDescription,String linkHref){
        if (linkDescription.equals("") || linkName.equals("") || linkHref.equals("")) {
            Toast.makeText(context,context.getString(R.string.You_didnt_fill_up_everything),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (linkName.length() > MAX_LINK_NAME_LENGTH) {
            //
            Toast.makeText(context,context.getString(R.string.link_name_is_too_long_max) + MAX_LINK_NAME_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (linkDescription.length() > MAX_LINK_DESCRIPTION_LENGTH){
            //
            Toast.makeText(context,context.getString(R.string.link_description_is_too_long_max) + MAX_LINK_DESCRIPTION_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidHref(linkHref)){
            //
            Toast.makeText(context,context.getString(R.string.url_is_not_valid),Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private boolean isValidHref(String linkHref){
        try {
            new URL(linkHref).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }


}
