package my.mtg.throneofeldraine;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class ImageDialog extends Dialog implements Dialog.OnClickListener {

    public ImageView imView;

    @SuppressLint("InflateParams")
    public ImageDialog(@NonNull Context context) {
        super(context);
        this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.setContentView(mInflater.inflate(R.layout.image_zoom, null));
        imView = this.findViewById(R.id.imageZoom);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.hide();
    }
}

