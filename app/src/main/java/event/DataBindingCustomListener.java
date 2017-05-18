package event;

public interface DataBindingCustomListener {

    public interface Presenter{
        void onNeedString(String stringData);
        void onSocialClick(String code,String stringData);
        void onCommentClick();
    }


    public interface View{
        void onNeedString(String stringData);
        void onSocialClick(String code,String stringData);
        void onCommentClick();
    }

}
