package sk.po.spse.dzurikm.linkorganizer.activities;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.adapters.FolderAdapter;
import sk.po.spse.dzurikm.linkorganizer.heandlers.DatabaseHandler;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;
import sk.po.spse.dzurikm.linkorganizer.models.Link;
import sk.po.spse.dzurikm.linkorganizer.views.AddLinkDialog;
import sk.po.spse.dzurikm.linkorganizer.views.BackupDialog;
import sk.po.spse.dzurikm.linkorganizer.views.ColorPickerDialog;
import sk.po.spse.dzurikm.linkorganizer.views.SettingsDialog;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;

public class MainActivity extends AppCompatActivity {
    private static RecyclerView foldersGridRecyclerView;
    private ImageButton addFolderButton,dismissAddFolderButton,approveAddFolderButton,backupButton,setFolderColorButton;
    private EditText folderNameInput,folderDescriptionInput;
    private FloatingActionButton moreOptionButton;

    private static LinkedList<Folder> folders;
    private ConstraintLayout addFolderBox, settingBox;
    private MotionLayout root;
    private SettingsDialog settingsDialog;
    private static CardView folderBackground,folderBookmark;

    private static DatabaseHandler databaseHandler;
    private static FolderAdapter adapter;

    private int MAX_FOLDER_NAME_LENGTH;
    private int MAX_FOLDER_DESCRIPTION_LENGTH;

    private BackupDialog backupDialog;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor sharedPreferencesEditor;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("Link-Organizer", 0); // 0 - for private mode
        sharedPreferencesEditor = sharedPreferences.edit();

        MAX_FOLDER_NAME_LENGTH = getResources().getInteger(R.integer.folder_heading_max_characters);
        MAX_FOLDER_DESCRIPTION_LENGTH = getResources().getInteger(R.integer.folder_description_max_characters);

        root = (MotionLayout) findViewById(R.id.root);

        root.transitionToEnd();

        foldersGridRecyclerView = (RecyclerView) findViewById(R.id.group_grid_layout);
        addFolderButton = (ImageButton) findViewById(R.id.addFolderButton);
        addFolderBox = (ConstraintLayout) findViewById(R.id.addFolderBox);
        backupButton = (ImageButton) findViewById(R.id.backupButton);

        dismissAddFolderButton = (ImageButton) findViewById(R.id.dismissAddFolderButton);
        approveAddFolderButton = (ImageButton) findViewById(R.id.approveAddFolderButton);
        setFolderColorButton = (ImageButton) findViewById(R.id.setFolderColorButton);

        folderNameInput = (EditText) findViewById(R.id.folderNameInput);
        folderDescriptionInput = (EditText) findViewById(R.id.folderDescriptionInput);

        moreOptionButton = (FloatingActionButton) findViewById(R.id.moreOptionButton);
        //moreOptionButton.setVisibility(View.GONE);
        settingBox = (ConstraintLayout) findViewById(R.id.settingsBox);

        folderBackground = (CardView) findViewById(R.id.folder_background);
        folderBookmark = (CardView) findViewById(R.id.folder_bookmark);

        databaseHandler = new DatabaseHandler(MainActivity.this);
        folders = (LinkedList<Folder>) databaseHandler.getAllFolders();

        isLinkShared(getIntent());

        if(folders.size() == 0){
            folders = new LinkedList<Folder>();
        }

        refreshFolderColor(getCurrentFolderColor(getApplicationContext()));

        checkPermissions();

        adapter = new FolderAdapter(MainActivity.this,MainActivity.this.getSupportFragmentManager(),folders);

        foldersGridRecyclerView.setAdapter(adapter);
        foldersGridRecyclerView.setNestedScrollingEnabled(false);

        settingsDialog = new SettingsDialog(MainActivity.this,MainActivity.this.getSupportFragmentManager());

        moreOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settingsDialog.show(getSupportFragmentManager(),"SettingsDialog");
            }
        });

        setFolderColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(MainActivity.this,getString(R.string.select_folder_color));
                colorPickerDialog.setOnPickColorListener(new OnColorPickedListener() {
                    @Override
                    public void colorPicked(int color) {
                        // Color is picked
                        Log.d("COLOR PICKED",String.valueOf(color));
                        folderBackground.setCardBackgroundColor(color);
                        folderBookmark.setCardBackgroundColor(lighten(color,.85F));
                    }
                });
                colorPickerDialog.show(getSupportFragmentManager(),"ColorPicker");
            }
        });

        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddFolderBox();
            }
        });

        approveAddFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String folderName = folderNameInput.getText().toString().trim(),
                        folderDescription = folderDescriptionInput.getText().toString().trim();
                if (!folderName.equals("") && !folderDescription.equals("")){
                    if (checkFolderText(folderName,folderDescription)){
                        addFolder(folderName,folderDescription,folderBackground.getCardBackgroundColor().getDefaultColor());
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,getString(R.string.You_didnt_fill_up_everything),Toast.LENGTH_SHORT).show();
                }
            }
        });

        dismissAddFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAddFolderBox();
            }
        });



        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backupDialog = new BackupDialog(MainActivity.this);
                backupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                backupDialog.show();
            }
        });

    }

    private void isLinkShared(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                AddLinkDialog addLinkDialog = new AddLinkDialog(MainActivity.this,getSupportFragmentManager(),sharedText);
                addLinkDialog.show();
            }

        }

    }

    public static void addLink(Link link) {
        int id = databaseHandler.addLink(link);
        if (id > -1) System.out.println("Success");
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.INTERNET},1);
        }
    }

    private boolean checkFolderText(String folderName,String folderDescription){
        if (folderName.length() > MAX_FOLDER_NAME_LENGTH) {
            Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.folder_name_is_too_long_max) + MAX_FOLDER_NAME_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (folderDescription.length() > MAX_FOLDER_DESCRIPTION_LENGTH){
            Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.folder_description_is_too_long_max) + MAX_FOLDER_DESCRIPTION_LENGTH + ")",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private void refreshDatabase(){
        folders = (LinkedList<Folder>) databaseHandler.getAllFolders();
        adapter = new FolderAdapter(MainActivity.this,MainActivity.this.getSupportFragmentManager(),folders);

        foldersGridRecyclerView.setAdapter(adapter);
    }

    private void addFolder(String folderName, String folderDescription,int color) {
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setDescription(folderDescription);
        folder.setColorId(color);

        int id = databaseHandler.addFolder(folder);

        if (id > -1){
            folder.setId(id);

            folders.add(folder);

            adapter.notifyDataSetChanged();

            root.clearFocus();
            folderNameInput.setText("");
            folderDescriptionInput.setText("");

            dismissAddFolderBox();

        }
        else{
            Toast.makeText(MainActivity.this,getString(R.string.Folder) + folder.getName() + getString(R.string.wasnt_created),Toast.LENGTH_SHORT).show();
        }

    }

    public static void editFolder(Folder folder) {
        databaseHandler.updateFolder(folder);
        int positionInList = getPositionOfFolder(folder);
        if (positionInList > -1){
            folders.set(positionInList,folder);
            adapter.notifyDataSetChanged();
        }
        else System.out.println("No folder like that ");

    }

    public static int getPositionOfFolder(Folder folder){
        for (int i = 0; i < folders.size(); i++) {
            if (folder.getId() == folders.get(i).getId()) return i;
        }

        return -1;
    }

    public static void removeFolder(Context context,Folder folder, View view){
        databaseHandler.deleteFolder(folder);
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_out_up));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                folders.remove(folder);
                adapter.notifyDataSetChanged();

            }
        }, 700);

        Toast.makeText(context,context.getString(R.string.Folder) + " " + folder.getName() + " " + context.getString(R.string.was_removed_successfully) +  "." ,Toast.LENGTH_LONG).show();
    }

    private void dismissAddFolderBox(){
        hideKeyboard();
        root.clearFocus();

        root.transitionToState(R.id.addFolderBoxHidden);
    }

    private void showAddFolderBox(){
        if (!addFolderBox.hasFocus()){
            addFolderBox.requestFocus();
        }

        Log.i("OK", String.valueOf(foldersGridRecyclerView.isFocused()));
        foldersGridRecyclerView.clearFocus();

        root.transitionToState(R.id.addFolderBoxShowed);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = MainActivity.this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(MainActivity.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void exportDatabase(Uri uri) throws IOException {
        if (uri != null){
            if (databaseHandler.exportDatabase(uri)){
                Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.operation_successful),Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.folder_is_not_valid),Toast.LENGTH_SHORT).show();
        }
    }

    private void importDatabase(Uri uri) throws IOException {
        if (uri != null){
            if (databaseHandler.importDatabase(uri)){
                Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.operation_successful),Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.file_is_not_valid),Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 9999:
                if (resultCode == -1){
                    Log.i("Test", "Result URI " + data.getData());
                    try {
                        exportDatabase(data.getData());
                        backupDialog.dismiss();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.export_was_unsuccessful),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.you_havent_chosen_any_folder),Toast.LENGTH_SHORT).show();
                break;
            case 9998:
                if (resultCode == -1){
                    Log.i("Test", "Result URI " + data.getData());
                    try {
                        importDatabase(data.getData());
                        refreshDatabase();
                        backupDialog.dismiss();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.import_was_unsuccessful),Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                else Toast.makeText(MainActivity.this,MainActivity.this.getString(R.string.you_havent_chosen_any_file),Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshFolders(MainActivity.this,MainActivity.this.getSupportFragmentManager());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isLinkShared(intent);
    }

    public static void refreshFolders(Context context, FragmentManager fragmentManager){
        LinkedList<Folder> foldersNew = databaseHandler.getAllFolders();
        if (!areTheSameLists(foldersNew,folders)){
            folders = foldersNew;
            adapter = new FolderAdapter(context,fragmentManager,folders);
            foldersGridRecyclerView.setAdapter(adapter);
        }
    }

    public static boolean areTheSameLists(List<?> list1, List<?> list2){
        if (list1.size() != list2.size()) return false;

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) return false;
        }

        return true;
    }

    public static int getCurrentFolderColor(Context context){
        return sharedPreferences.getInt("folder_color",ContextCompat.getColor(context, R.color.blue));
    }

    public static void setCurrentFolderColor(Context context,FragmentManager fragmentManager,int res){
        sharedPreferencesEditor.putInt("folder_color",res);
        sharedPreferencesEditor.commit();
        // when color changes
        refreshFolders(context,fragmentManager);
        refreshFolderColor(res);
    }

    public static void refreshFolderColor(int color){
        folderBackground.setCardBackgroundColor(color);
        folderBookmark.setCardBackgroundColor(lighten(color,.85F));
    }

    public static int lighten(int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }

    public static LinkedList<Folder> getAllFolders(){
        return databaseHandler.getAllFolders();
    }

    @Override
    public void onBackPressed() {
        if (root.getCurrentState() != R.id.addFolderBoxHidden){
            root.transitionToState(R.id.addFolderBoxHidden);
        }
        else {
            super.onBackPressed();
        }

    }
}