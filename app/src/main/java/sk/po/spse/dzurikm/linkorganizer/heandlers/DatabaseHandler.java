package sk.po.spse.dzurikm.linkorganizer.heandlers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import sk.po.spse.dzurikm.linkorganizer.models.Folder;
import sk.po.spse.dzurikm.linkorganizer.models.Link;
import sk.po.spse.dzurikm.linkorganizer.utils.FileUtils;

//


public class DatabaseHandler extends SQLiteOpenHelper {
    private static String DATABASE_PATH= "data/data/sk.po.spse.dzurikm.linkorganizer/databases/";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LinkOrganizer";

    // Tables
    private static final String TABLE_FOLDERS = "folders";
    private static final String TABLE_LINK = "links";

    // Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_FOLDER_ID = "folder_id";
    private static final String KEY_HREF = "href";
    private static final String KEY_COLOR_ID = "color_id";

    private static Context context;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.d("DATABASE","INITIAL");
        backwardsCompatibility();
        //3rd argument to be passed is CursorFactory instance
    }

    public void backwardsCompatibility(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_FOLDERS, new String[] { KEY_COLOR_ID }, null,
                   null, null, null, null, null);
            Log.d("EXPORT","DON'T EXTEND TABLES");
        }
        catch (Exception e){
            // TODO: tu urob aby rozsirilo tabulky , to je pripad kedy jednoducho nedokaze zobrat veci z db lebo tam nie je stplec
            String ALTER_LINK_TABLE = "ALTER TABLE " + TABLE_LINK + " ADD " + KEY_COLOR_ID + " INTEGER DEFAULT -1";
            String ALTER_FOLDER_TABLE = "ALTER TABLE " + TABLE_FOLDERS + " ADD " + KEY_COLOR_ID + " INTEGER DEFAULT -1";
            getWritableDatabase().execSQL(ALTER_LINK_TABLE);
            getWritableDatabase().execSQL(ALTER_FOLDER_TABLE);

            Log.d("EXPORT","EXTEND TABLES");
        }
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FOLDERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT" + "," + KEY_COLOR_ID + " INTEGER DEFAULT -1)";
        String CREATE_LINKS_TABLE = "CREATE TABLE " + TABLE_LINK + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESCRIPTION + " TEXT" + "," + KEY_HREF + " TEXT," + KEY_FOLDER_ID + " INTEGER, " + KEY_COLOR_ID + " INTEGER DEFAULT -1)";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_LINKS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LINK);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public int addFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, folder.getName()); // Contact Name
        values.put(KEY_DESCRIPTION, folder.getDescription()); // Contact Phone
        values.put(KEY_COLOR_ID,folder.getColorId()); // Folder Color ID

        // Inserting Row
        int id = (int) db.insert(TABLE_FOLDERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

        return id;
    }

    public int addLink(Link link) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, link.getName()); // Link Name
        values.put(KEY_DESCRIPTION, link.getDescription()); // Link description
        values.put(KEY_FOLDER_ID, link.getFolderId()); // Link id link is in
        values.put(KEY_HREF,link.getHref());
        values.put(KEY_COLOR_ID,link.getColorId());

        // Inserting Row
        int id = (int) db.insert(TABLE_LINK, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

        return id;
    }

    // code to get the single contact
    public Folder getFolder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FOLDERS, new String[] { KEY_ID,
                        KEY_NAME, KEY_DESCRIPTION,KEY_COLOR_ID }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        @SuppressLint("Range") Folder folder = new Folder(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(KEY_COLOR_ID)));
        // return contact
        return folder;
    }

    @SuppressLint("Range")
    public Link getLink(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_LINK, new String[] { KEY_ID,
                        KEY_NAME, KEY_DESCRIPTION, KEY_FOLDER_ID,KEY_COLOR_ID}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Link link = new Link(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(KEY_FOLDER_ID)));
        link.setColorId(cursor.getInt(cursor.getColumnIndex(KEY_COLOR_ID)));
        // return contact
        return link;
    }

    // code to get all contacts in a list view
    @SuppressLint("Range")
    public LinkedList<Folder> getAllFolders() {
        LinkedList<Folder> folderList = new LinkedList<Folder>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FOLDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Folder folder = new Folder();
                folder.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                folder.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                folder.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                folder.setColorId(cursor.getInt(cursor.getColumnIndex(KEY_COLOR_ID)));
                // Adding contact to list
                folderList.add(folder);
            } while (cursor.moveToNext());
        }

        // return contact list
        return folderList;
    }

    @SuppressLint("Range")
    public LinkedList<Link> getAllLinks(int id) {
        LinkedList<Link> linkList = new LinkedList<Link>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LINK + " WHERE " + KEY_FOLDER_ID + "=" + id ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Link link = new Link();
                link.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))));
                link.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                link.setHref(cursor.getString(cursor.getColumnIndex(KEY_HREF)));
                link.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                link.setFolderId(cursor.getInt(cursor.getColumnIndex(KEY_FOLDER_ID)));
                link.setColorId(cursor.getInt(cursor.getColumnIndex(KEY_COLOR_ID)));
                // Adding contact to list
                linkList.add(link);
            } while (cursor.moveToNext());
        }

        // return contact list
        return linkList;
    }

    // code to update the single contact
    public int updateFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, folder.getName());
        values.put(KEY_DESCRIPTION, folder.getDescription());
        values.put(KEY_COLOR_ID,folder.getColorId());

        // updating row
        return db.update(TABLE_FOLDERS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(folder.getId()) });
    }

    public int updateLink(Link link) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, link.getName());
        values.put(KEY_DESCRIPTION, link.getDescription());
        values.put(KEY_HREF,link.getHref());
        values.put(KEY_FOLDER_ID,link.getFolderId());
        values.put(KEY_COLOR_ID,link.getColorId());

        // updating row
        return db.update(TABLE_LINK, values, KEY_ID + " = ?",
                new String[] { String.valueOf(link.getId()) });
    }

    // Deleting single contact
    public void deleteFolder(Folder folder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FOLDERS, KEY_ID + " = ?",
                new String[] { String.valueOf(folder.getId()) });
        db.close();
    }

    public void deleteLink(Link link) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LINK, KEY_ID + " = ?",
                new String[] { String.valueOf(link.getId()) });
        db.close();
    }

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FOLDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public int getLinksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LINK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean importDatabase(Uri exportDB) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.

        close();
        /*try{
            SQLiteDatabase.openOrCreateDatabase(DocumentFile.fromSingleUri(context,exportDB).getUri().getEncodedPath(),null);
            return true;
        }catch (Exception e){

        }*/


        Uri url = DocumentFile.fromSingleUri(context,exportDB).getUri();
        File DB = new File("/data/data/" + context.getPackageName() + "/databases/" + DATABASE_NAME);

        if (true) {
            FileUtils.copyFile( (FileInputStream) context.getContentResolver().openInputStream(url) ,new FileOutputStream(DB));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            backwardsCompatibility();
            return true;
        }

        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean exportDatabase(Uri uri) throws IOException {
        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.

        String file = "LinkOrganizer.db";
        DocumentFile df = DocumentFile.fromTreeUri(context,uri).findFile(file);
        if ( df == null){
            df = DocumentFile.fromTreeUri(context,uri).createFile("application/vnd.sqlite3",file);
        }


        close();
        Uri url = df.getUri();
        Log.i("Path",url.getPath());
        File DB = new File("/data/data/" + context.getPackageName() + "/databases/" + DATABASE_NAME);

        if (df.canWrite()) {
            FileUtils.copyFile( new FileInputStream(DB), (FileOutputStream) context.getContentResolver().openOutputStream(url));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();

            return true;
        }


        return false;
    }

}