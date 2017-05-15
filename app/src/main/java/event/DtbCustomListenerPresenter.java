package event;


public class DtbCustomListenerPresenter implements DataBindingCustomListener.Presenter {

    private DataBindingCustomListener.View view;

    public DtbCustomListenerPresenter(DataBindingCustomListener.View view) {
        this.view = view;
    }

    @Override
    public void onNeedString(String stringData) {
        view.onNeedString(stringData);
    }

    @Override
    public void onSocialClick(String code, String stringData) {
        view.onSocialClick(code,stringData);
    }
}
