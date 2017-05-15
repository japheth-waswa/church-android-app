package event;

public interface DataBindingCustomListener {

    public interface Presenter{
        void onNeedString(String stringData);
        void onSocialClick(String code,String stringData);
    }


    public interface View{
        void onNeedString(String stringData);
        void onSocialClick(String code,String stringData);
    }

}
