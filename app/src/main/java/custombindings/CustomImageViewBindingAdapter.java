package custombindings;


import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.japhethwaswa.church.R;

public class CustomImageViewBindingAdapter {

    @BindingAdapter("imageUrl")
    public static void setImage(ImageView view, String imageUrl){
        Glide
                .with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_loader)
                .error(R.drawable.ic_error)
                .into(view);
    }
    @BindingAdapter({"imageUrl","imagePlaceholder"})
    public static void setImage(ImageView view,String imageUrl,int imagePlaceholder){
        Glide
                .with(view.getContext())
                .load(imageUrl)
                .placeholder(imagePlaceholder)
                .error(R.drawable.ic_error)
                .into(view);
    }
    @BindingAdapter({"imageUrl","imagePlaceholder","imageError"})
    public static void setImage(ImageView view,String imageUrl,int imagePlaceholder,int imageError){
        Glide
                .with(view.getContext())
                .load(imageUrl)
                .placeholder(imagePlaceholder)
                .error(imageError)
                .into(view);
    }
}
