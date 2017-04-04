package custombindings;

import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.widget.LinearLayout;

public class CustomBackgroundColorBindingAdapter {

    @BindingAdapter("jeffBgColorLinearLayout")
    public static void setBgColor(LinearLayout linearLayout,String bgColor){
        linearLayout.setBackgroundColor(Color.parseColor(bgColor));

    }

    @BindingAdapter("jeffBgColorCardView")
    public static void setBgColour(CardView cardView,String bgColor){
        cardView.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor(bgColor)));
    }
}
