package custombindings;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

public class CustomIconBindingAdapter {

    @BindingAdapter("jeffIconResource")
    public static void setIconRes(ImageView imageView,int resoureceId){
        imageView.setImageResource(resoureceId);
    }
}
