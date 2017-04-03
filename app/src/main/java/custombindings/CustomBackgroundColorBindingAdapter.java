package custombindings;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.widget.LinearLayout;

public class CustomBackgroundColorBindingAdapter {

    @BindingAdapter("jeffBgColorLinearLayout")
    public static void setBgColor(LinearLayout linearLayout,String bgColor){
        linearLayout.setBackgroundColor(Color.parseColor(bgColor));

    }
}
