package sk.po.spse.dzurikm.linkorganizer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.FolderContentActivity;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.models.Folder;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements AdapterView.OnItemClickListener {
    private Context context;
    private List<String> mData;
    private LinkedList<Folder> folders;
    private LayoutInflater inflter;
    private int originalSizeOfAdapter;
    private View rootView;

    // data is passed into the constructor
    public FolderAdapter(Context context, LinkedList<Folder> folders) {
        this.context = context;
        this.folders = folders;

        originalSizeOfAdapter = folders.size();

        inflter = (LayoutInflater.from(context));
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflter.inflate(R.layout.folder_layout, parent, false);
        rootView = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getFolderName().setText(folders.get(position).getName());
        holder.getFolderDescription().setText(folders.get(position).getDescription());
        if (folders.get(position).getColorId() != -1) {
            holder.getFolderBackground().setCardBackgroundColor(folders.get(position).getColorId());
            holder.getFolderBookmark().setCardBackgroundColor(MainActivity.lighten(folders.get(position).getColorId(), 0.85F));
        }
        else {
            holder.getFolderBackground().setCardBackgroundColor(MainActivity.getCurrentFolderColor(context));
            holder.getFolderBookmark().setCardBackgroundColor(MainActivity.lighten(MainActivity.getCurrentFolderColor(context), 0.85F));
        }



        if (originalSizeOfAdapter == position){
            rootView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_bottom));
            originalSizeOfAdapter = position + 1;
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return folders.size();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        Intent i = new Intent(context, FolderContentActivity.class);
        i.putExtra("folder_id",folders.get(index).getId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);
        ((Activity) context).overridePendingTransition(R.anim.slide_in_enter_front,R.anim.slide_in_enter_back);
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderName,folderDescription;
        CardView folderBackground,folderBookmark;

        ViewHolder(View view) {
            super(view);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context,R.style.AlertDialog)
                            .setTitle(context.getString(R.string.Delete_Item))
                            .setMessage(context.getString(R.string.Do_you_really_want_to_delete) + " " + folders.get(getAdapterPosition()).getName() + " " + context.getString(R.string.Folder))
                            .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MainActivity.removeFolder(context,folders.get(getAdapterPosition()),view);
                                }
                            })
                            .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                    return false;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, FolderContentActivity.class);
                    i.putExtra("folder_id",folders.get(getAdapterPosition()).getId());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(i);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_enter_front,R.anim.slide_in_enter_back);
                }
            });

            folderName = (TextView) view.findViewById(R.id.folder_name);
            folderDescription = (TextView) view.findViewById(R.id.folder_description);
            folderBackground = (CardView) view.findViewById(R.id.folder_background);
            folderBookmark = (CardView) view.findViewById(R.id.folder_bookmark);

        }

        public CardView getFolderBookmark() {
            return folderBookmark;
        }

        public void setFolderBookmark(CardView folderBookmark) {
            this.folderBookmark = folderBookmark;
        }

        public CardView getFolderBackground() {
            return folderBackground;
        }

        public void setFolderBackground(CardView folderBackground) {
            this.folderBackground = folderBackground;
        }

        public TextView getFolderDescription() {
            return folderDescription;
        }

        public void setFolderDescription(TextView folderDescription) {
            this.folderDescription = folderDescription;
        }

        public TextView getFolderName() {
            return folderName;
        }

        public void setFolderName(TextView folderName) {
            this.folderName = folderName;
        }
    }

    // convenience method for getting data at click position
    Folder getItem(int id) {
        return folders.get(id);
    }

}