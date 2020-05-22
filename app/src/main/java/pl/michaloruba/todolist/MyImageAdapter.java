package pl.michaloruba.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class MyImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Bitmap> smallImages;

    MyImageAdapter(Context callingActivityContext, ArrayList<Bitmap> thumbnails) {
        context = callingActivityContext;
        smallImages = thumbnails;
    }
    public int getCount() {
        return smallImages.size();
    }

    public Object getItem(int position) {
        return smallImages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams( new GridView.LayoutParams(200, 150) );
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setBackground(context.getDrawable(R.drawable.image_border));
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(smallImages.get(position));
        return imageView;
    }
}
