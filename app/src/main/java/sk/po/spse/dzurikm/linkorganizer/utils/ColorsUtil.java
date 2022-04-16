package sk.po.spse.dzurikm.linkorganizer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import sk.po.spse.dzurikm.linkorganizer.R;

public class ColorsUtil {
    private static SharedPreferences sharedPreferences;

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

    public static int getCurrentFolderColor(Context context){
        if (sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("Link-Organizer", 0);
        }
        return sharedPreferences.getInt("folder_color", ContextCompat.getColor(context, R.color.blue));
    }
}
