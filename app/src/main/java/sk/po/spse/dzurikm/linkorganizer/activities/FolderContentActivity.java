package sk.po.spse.dzurikm.linkorganizer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.adapters.LinkAdapter;
import sk.po.spse.dzurikm.linkorganizer.heandlers.DatabaseHandler;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;
import sk.po.spse.dzurikm.linkorganizer.models.Link;
import sk.po.spse.dzurikm.linkorganizer.utils.ColorsUtil;
import sk.po.spse.dzurikm.linkorganizer.views.ColorPickerDialog;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnPositiveButtonClick;
import sk.po.spse.dzurikm.linkorganizer.views.FilterDialog;

public class FolderContentActivity extends AppCompatActivity {
    private int folderId;
    private static DatabaseHandler databaseHandler;
    private static LinkedList<Link> links,originalLinks;

    private static RecyclerView linkRecyclerView;
    private static LinkAdapter linkAdapter;
    private static EditText headingView;
    private ImageButton addLinkButton,dismissAddLinkButton,approveAddLinkButton,searchLinkButton,autoInfoLoadButton;
    private ConstraintLayout addLinkBox;
    private MotionLayout root;
    private static EditText linkNameInput,linkDescriptionInput,linkHrefInput,searchLinkInput;
    private LinearLayout searchLinkBox;
    private FloatingActionButton filterButton;
    private CardView linkCircle;

    private int MAX_LINK_NAME_LENGTH;
    private int MAX_LINK_DESCRIPTION_LENGTH;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor sharedPreferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_content_layout);

        sharedPreferences = getSharedPreferences("Link-Organizer", 0); // 0 - for private mode
        sharedPreferencesEditor = sharedPreferences.edit();


        databaseHandler = new DatabaseHandler(FolderContentActivity.this);

        MAX_LINK_NAME_LENGTH = getResources().getInteger(R.integer.link_heading_max_characters);
        MAX_LINK_DESCRIPTION_LENGTH = getResources().getInteger(R.integer.link_description_max_characters);

        root = (MotionLayout) findViewById(R.id.root);

        linkRecyclerView = (RecyclerView) findViewById(R.id.linkList);
        addLinkButton = (ImageButton) findViewById(R.id.addLinkButton);
        addLinkBox = (ConstraintLayout) findViewById(R.id.addLinkBox);
        headingView = (EditText) findViewById(R.id.heading);

        dismissAddLinkButton = (ImageButton) findViewById(R.id.dismissAddLinkButton);
        approveAddLinkButton = (ImageButton) findViewById(R.id.approveAddLinkButton);
        linkNameInput = (EditText) findViewById(R.id.linkName);
        linkDescriptionInput = (EditText) findViewById(R.id.linkDescription);
        linkHrefInput = (EditText) findViewById(R.id.linkHref);
        autoInfoLoadButton = (ImageButton) findViewById(R.id.autoInfoLoadButton);

        searchLinkButton = (ImageButton) findViewById(R.id.searchLinkButton);
        searchLinkInput = (EditText) findViewById(R.id.link_search_bar_input);
        searchLinkBox = (LinearLayout) findViewById(R.id.link_search_bar);

        filterButton = (FloatingActionButton) findViewById(R.id.filterButton);
        linkCircle = (CardView) findViewById(R.id.colorCircle);

        Bundle bundle = getIntent().getExtras();
        folderId = bundle.getInt("folder_id");

        linkCircle.setCardBackgroundColor(ColorsUtil.getCurrentFolderColor(getApplicationContext()));
        filterButton.setBackgroundTintList(ColorStateList.valueOf(ColorsUtil.getCurrentFolderColor(getApplicationContext())));
        filterButton.setRippleColor(ColorsUtil.lighten(ColorsUtil.getCurrentFolderColor(getApplicationContext()),.3f));

        headingView.setText(databaseHandler.getFolder(folderId).getName());

        links = databaseHandler.getAllLinks(folderId);
        originalLinks = links;
        if (links.size() != 0){
            linkAdapter = new LinkAdapter(FolderContentActivity.this,getSupportFragmentManager(),links);
            linkRecyclerView.setAdapter(linkAdapter);
        }

        linkCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(FolderContentActivity.this,getString(R.string.select_link_color));
                colorPickerDialog.setOnPickColorListener(new OnColorPickedListener() {
                    @Override
                    public void colorPicked(int color) {
                        // Color is picked
                        Log.d("COLOR PICKED",String.valueOf(color));
                        linkCircle.setCardBackgroundColor(color);
                    }
                });
                colorPickerDialog.show(getSupportFragmentManager(),"ColorPicker");
            }
        });

        headingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headingView.setBackgroundResource(R.drawable.changing_folder_name_background);
            }
        });

        headingView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    headingView.setBackgroundResource(R.drawable.changing_folder_name_background);
                }
                else headingView.setBackgroundResource(R.drawable.default_folder_name_background);
            }
        });

        headingView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (!headingView.getText().toString().trim().equals("")){
                        Folder folder = databaseHandler.getFolder(folderId);

                        if (!headingView.getText().toString().trim().equals(folder.getName())){
                            folder.setName(headingView.getText().toString().trim());
                            databaseHandler.updateFolder(folder);
                            Toast.makeText(FolderContentActivity.this, FolderContentActivity.this.getString(R.string.folder_edited_successfully),Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(FolderContentActivity.this, FolderContentActivity.this.getString(R.string.you_havent_changed_anything),Toast.LENGTH_SHORT).show();
                        headingView.setBackgroundResource(R.drawable.default_folder_name_background);
                        root.clearFocus();
                    }
                    else {
                        Toast.makeText(FolderContentActivity.this, FolderContentActivity.this.getString(R.string.operation_unsuccessful),Toast.LENGTH_SHORT).show();
                    }


                    return true;
                }
                return false;
            }
        });

        addLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLinkBox();

            }
        });

        approveAddLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String linkName = linkNameInput.getText().toString().trim(),
                        linkDescription = linkDescriptionInput.getText().toString().trim(),
                        linkHref = linkHrefInput.getText().toString().trim();
                if (!linkName.equals("") && !linkHref.equals("")){
                    if (checkLinkText(linkName,linkDescription,linkHref)){
                        addLink(linkName,linkDescription.equals("") ? null : linkDescription,linkHref,linkCircle.getCardBackgroundColor().getDefaultColor());
                    }
                }
                else{
                    Toast.makeText(FolderContentActivity.this,getString(R.string.You_didnt_fill_up_everything),Toast.LENGTH_SHORT).show();
                }
            }
        });

        dismissAddLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAddLinkBox();
            }
        });

        searchLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show search bar
                root.clearFocus();
                searchLinkBox.setVisibility(searchLinkBox.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });


        searchLinkInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputValue = searchLinkInput.getText().toString().trim();

                if (inputValue.length() >= 1){
                    filterLinks(inputValue);
                }
                else{

                    resetLinks();
                }
            }
        });

        autoInfoLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = linkHrefInput.getText().toString().trim();

                if (isValidHref(url)){
                    RequestQueue queue = Volley.newRequestQueue(FolderContentActivity.this);

                    StringRequest request = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
                                    try {
                                        autoURLInfoDone(sk.po.spse.dzurikm.linkorganizer.utils.AutoURLInfoUtil.getLink(url,response));
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    queue.add(request);

                }
                else Toast.makeText(FolderContentActivity.this, FolderContentActivity.this.getString(R.string.valid_url_isnt_provided),Toast.LENGTH_SHORT).show();

            }
        });

        filterButton.setImageResource(R.drawable.ic_filter);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Filter
                FilterDialog filterDialog = new FilterDialog(FolderContentActivity.this, new OnPositiveButtonClick() {
                    @Override
                    public void callback(int selected) {
                        switch (selected){
                            case 1:
                                // by name ASC
                                if (links.size() > 0){
                                    Collections.sort(links, Comparator.comparingInt( obj -> (int) deAccent(obj.getName()).charAt(0)));

                                    linkAdapter.notifyDataSetChanged();
                                    linkRecyclerView.invalidate();
                                }
                                break;
                            case 2:
                                // by name DESC
                                if (links.size() > 0){
                                    Collections.sort(links, Comparator.comparingInt( obj -> (int) deAccent(obj.getName()).charAt(0)));
                                    linkAdapter.notifyDataSetChanged();
                                    Collections.reverse(links);
                                    linkRecyclerView.invalidate();
                                }

                                break;
                            case 3:
                                // by description ASC
                                if(links.size() > 0){

                                    Collections.sort(links, Comparator.comparingInt( obj -> (int) deAccent(obj.getDescription() == null ? "z" : obj.getDescription()).charAt(0)));
                                    linkAdapter.notifyDataSetChanged();
                                    linkRecyclerView.invalidate();
                                }
                                break;
                            case 4:
                                // by description DESC
                                if (links.size() > 0){
                                    Collections.sort(links, Comparator.comparingInt( obj -> (int) deAccent(obj.getDescription() == null ? "z" : obj.getDescription()).charAt(0)));
                                    linkAdapter.notifyDataSetChanged();
                                    Collections.reverse(links);
                                    linkRecyclerView.invalidate();
                                }
                                break;
                            default:
                                // none
                                links = originalLinks;
                                linkAdapter.notifyDataSetChanged();
                                linkRecyclerView.invalidate();
                                break;
                        }
                    }
                });
                filterDialog.show();
            }
        });


    }

    public String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    private void filterLinks(String term){
        term = term.toLowerCase();
        LinkedList<Link> filteredList = new LinkedList<>();
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).getName().toLowerCase().contains(term) || links.get(i).getDescription().toLowerCase().contains(term)){
                filteredList.add(links.get(i));
            }
        }

        linkRecyclerView.setAdapter(new LinkAdapter(FolderContentActivity.this,getSupportFragmentManager(),filteredList));

    }

    private void resetLinks(){
        linkRecyclerView.setAdapter(new LinkAdapter(FolderContentActivity.this,getSupportFragmentManager(),links));
    }

    private boolean listsAreTheSame(List<?> original,List<?> newList){

        if (original.size() != newList.size()) return false;

        for (int i = 0; i < original.size(); i++) {
            if (!original.get(i).equals(newList.get(i))) return false;
        }

        return true;
    }


    private boolean checkLinkText(String linkName,String linkDescription,String linkHref){
        if (linkName.length() > MAX_LINK_NAME_LENGTH) {
            // Link name length check
            Toast.makeText(FolderContentActivity.this,FolderContentActivity.this.getString(R.string.link_name_is_too_long_max) + MAX_LINK_NAME_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (linkDescription.length() > MAX_LINK_DESCRIPTION_LENGTH){
            // Link description length check
            Toast.makeText(FolderContentActivity.this,FolderContentActivity.this.getString(R.string.link_description_is_too_long_max) + MAX_LINK_DESCRIPTION_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isValidHref(linkHref)){
            // Link href validation
            Toast.makeText(FolderContentActivity.this,FolderContentActivity.this.getString(R.string.url_is_not_valid),Toast.LENGTH_SHORT).show();
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

    private void addLink(String linkName, String linkDescription,String linkHref,int color) {
        Link link = new Link();
        link.setName(linkName);
        link.setDescription(linkDescription);
        link.setHref(linkHref);
        link.setFolderId(folderId);
        link.setColorId(color);

        int id = databaseHandler.addLink(link);

        if (id > -1){
            link.setId(id);

            links.add(link);
            linkAdapter = new LinkAdapter(FolderContentActivity.this,getSupportFragmentManager(),links);
            linkRecyclerView.setAdapter(linkAdapter);

            linkAdapter.notifyDataSetChanged();

            linkNameInput.setText("");
            linkDescriptionInput.setText("");
            linkHrefInput.setText("");

            dismissAddLinkBox();
        }
        else{
            Toast.makeText(FolderContentActivity.this,getString(R.string.Folder) + link.getName() + getString(R.string.wasnt_created),Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissAddLinkBox(){
        hideKeyboard();
        root.clearFocus();

        root.transitionToState(R.id.addLinkBoxHidden);
    }

    private void showAddLinkBox() {
        root.clearFocus();
        root.transitionToState(R.id.addLinkBoxShowed);
    }

    public void hideKeyboard() {
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (root.getCurrentState() != R.id.addLinkBoxHidden){
            root.transitionToState(R.id.addLinkBoxHidden);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_exit_back, R.anim.slide_in_exit_front);
    }

    public static void removeLink(Context context,Link link){
        databaseHandler.deleteLink(link);

        links.remove(link);
        linkAdapter.notifyDataSetChanged();


        Toast.makeText(context,context.getString(R.string.Link) + " " +  link.getName() + " " + context.getString(R.string.was_removed_successfully) +  "." ,Toast.LENGTH_LONG).show();
    }


    public static void editLink(Link link){
        databaseHandler.updateLink(link);
        linkAdapter.notifyDataSetChanged();
    }

    public void autoURLInfoDone(Link link){
        if (link == null)
            Toast.makeText(FolderContentActivity.this,FolderContentActivity.this.getString(R.string.site_is_not_supported),Toast.LENGTH_SHORT).show();
        else {
            linkNameInput.setText(link.getName());
            linkDescriptionInput.setText(link.getDescription());
        }
    }
}