package sk.po.spse.dzurikm.linkorganizer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sk.po.spse.dzurikm.linkorganizer.R;
import sk.po.spse.dzurikm.linkorganizer.activities.FolderContentActivity;
import sk.po.spse.dzurikm.linkorganizer.activities.MainActivity;
import sk.po.spse.dzurikm.linkorganizer.models.Link;
import sk.po.spse.dzurikm.linkorganizer.views.AlertDialog;
import sk.po.spse.dzurikm.linkorganizer.views.ColorPickerDialog;
import sk.po.spse.dzurikm.linkorganizer.views.EditLinkDialog;
import sk.po.spse.dzurikm.linkorganizer.views.listeners.OnColorPickedListener;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.ViewHolder> {
    private List<Link> links;
    private LayoutInflater inflater;
    private Context context;
    private int originalSizeOfAdapter;
    private FragmentManager fragmentManager;
    // data is passed into the constructor
    public LinkAdapter(Context context, FragmentManager fragmentManager, List<Link> links) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.inflater = LayoutInflater.from(context);
        this.links = links;
        this.originalSizeOfAdapter = links.size();
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.link_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTitle().setText(links.get(position).getName());
        holder.getDescription().setText(links.get(position).getDescription() == null ? context.getString(R.string.no_description) : links.get(position).getDescription() );
        if (links.get(position).getColorId() != -1) holder.getColorCircle().setCardBackgroundColor(links.get(position).getColorId());
            else holder.getColorCircle().setCardBackgroundColor(FolderContentActivity.getCurrentFolderColor(context));
        holder.setLink(links.get(position));
        holder.setEditButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditLinkDialog dialog = new EditLinkDialog(context,fragmentManager, links.get(position));
                dialog.show();
            }
        });

        holder.getColorCircle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context,context.getString(R.string.select_link_color));
                colorPickerDialog.setOnPickColorListener(new OnColorPickedListener() {
                    @Override
                    public void colorPicked(int color) {
                        // Color is picked
                        Log.d("COLOR PICKED",String.valueOf(color));
                        Link link = links.get(position);
                        link.setColorId(color);
                        FolderContentActivity.editLink(link);
                    }
                });

                colorPickerDialog.show(fragmentManager,"Tag");
            }
        });

        if (originalSizeOfAdapter == position){
            originalSizeOfAdapter += 1;
            holder.getRoot().startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in_bottom));
        }

    }


    // total number of rows
    @Override
    public int getItemCount() {
        return links.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title,description;
        private CardView colorCircle;
        private Link link;
        private View root;
        private ImageButton editButton;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            colorCircle = itemView.findViewById(R.id.colorCircle);
            editButton = itemView.findViewById(R.id.editButton);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog dialog = new AlertDialog(context);
                    dialog.show();
                    dialog.setHeading(context.getString(R.string.Delete_Item));
                    dialog.setText(context.getString(R.string.Do_you_really_want_to_delete) + " " + title.getText().toString() + " " + context.getString(R.string.Link));
                    dialog.setOnNegativeButtonClick(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    dialog.setOnPositiveButtonClick(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            removeLink(link,root);
                            dialogInterface.dismiss();
                        }
                    });

                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("Links Name",link.getName());
                    Log.i("Links Href",link.getHref());
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getHref()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);

                }
            });


        }

        public ImageButton getEditButton() {
            return editButton;
        }

        public void setEditButtonOnClickListener(View.OnClickListener listener){
            editButton.setOnClickListener(listener);
        }

        public Link getLink() {
            return link;
        }

        public void setLink(Link link) {
            this.link = link;
        }

        public View getRoot() {
            return root;
        }

        public void setRoot(View root) {
            this.root = root;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public TextView getDescription() {
            return description;
        }

        public void setDescription(TextView description) {
            this.description = description;
        }

        public CardView getColorCircle() {
            return colorCircle;
        }

        public void setColorCircle(CardView colorCircle) {
            this.colorCircle = colorCircle;
        }
    }

    private void removeLink(Link link,View view) {
        view.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_out_up));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FolderContentActivity.removeLink(context,link);
            }
        }, 700);

    }

    // convenience method for getting data at click position
    Link getItem(int id) {
        return links.get(id);
    }

    public List<Link> getLinks() {
        return links;
    }
}
