package sk.po.spse.dzurikm.linkorganizer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;

public class FolderSpinnerAdapter extends BaseAdapter {
    private Context context;
    private LinkedList<Folder> folders;

    public FolderSpinnerAdapter(Context context, LinkedList<Folder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @Override
    public int getCount() {
        return folders.size();
    }

    @Override
    public Folder getItem(int i) {
        return folders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return folders.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.folder_spinner_item_layout, null);
        TextView text = view.findViewById(R.id.text);
        text.setText(this.getItem(i).getName());
        return view;
    }
}
