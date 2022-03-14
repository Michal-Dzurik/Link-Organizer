package sk.po.spse.dzurikm.linkorganizer.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.net.URL;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.FolderContentActivity;
import sk.po.spse.dzurikm.linkorganizer.models.Link;

public class EditLinkDialog extends Dialog {
    private Button positiveButton,negativeButton;
    private EditText nameInput,descriptionInput,hrefInput;
    private Link link;

    private Context context;

    private int MAX_LINK_NAME_LENGTH = 30;
    private int MAX_LINK_DESCRIPTION_LENGTH = 35;

    public EditLinkDialog(@NonNull Context context, Link link,int MAX_LINK_NAME_LENGTH,int MAX_LINK_DESCRIPTION_LENGTH) {
        super(context);
        this.link = link;
        this.context = context;
        this.MAX_LINK_NAME_LENGTH = MAX_LINK_NAME_LENGTH;
        this.MAX_LINK_DESCRIPTION_LENGTH = MAX_LINK_DESCRIPTION_LENGTH;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_link_dialog_layout);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.getWindow().setDimAmount(.5f);

        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);

        nameInput = (EditText) findViewById(R.id.linkName);
        descriptionInput = (EditText) findViewById(R.id.linkDescription);
        hrefInput = (EditText) findViewById(R.id.linkHref);

        nameInput.setText(link.getName());
        descriptionInput.setText(link.getDescription());
        hrefInput.setText(link.getHref());

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameInput.getText().toString().trim(),
                        description = descriptionInput.getText().toString().trim(),
                        href = hrefInput.getText().toString().trim();
                if (checkLinkText(name,description,href)){
                    EditLinkDialog.this.dismiss();

                    link.setName(name);
                    link.setDescription(description);
                    link.setHref(href);

                    FolderContentActivity.editLink(link);
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditLinkDialog.this.dismiss();
            }
        });

    }

    private boolean checkLinkText(String linkName,String linkDescription,String linkHref){
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
