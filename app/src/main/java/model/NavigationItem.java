package model;

import com.japhethwaswa.church.R;

public class NavigationItem {

    public static String getBgColor(int i) {
        switch (i) {
            case 0:
                return "#CC5200";
            case 1:
                return "#E4006B";
            case 2:
                return "#659933";
            case 3:
                return "#884444";
            case 4:
                return "#0E4C00";
            case 5:
                return "#00A3CC";
            case 6:
                return "#B0B200";
            case 7:
                return "#980000";
            case 8:
                return "#985D00";
            case 9:
                return "#400098";
            case 10:
                return "#724C26";
            case 11:
                return "#B600E4";
            default:
                return "#B600E4";
        }
    }

    //item title home
    public static String homeTitles(int i){
        switch (i) {
            case 0:
                return "Sermons";
            case 1:
                return "Events";
            case 2:
                return "Schedule";
            case 3:
                return "Bible";
            case 4:
                return "Hymn Book";
            case 5:
                return "Service Book";
            case 6:
                return "Donate";
            case 7:
                return "Gallery";
            case 8:
                return "Videos";
            case 9:
                return "News Feed";
            case 10:
                return "Connect";
            case 11:
                return "Login";
            default:
                return null;
        }
    }
    //item title navigation
    public static String navTitle(int i){
        switch (i) {
            case 0:
                return "Home";
            case 1:
                return "Sermons";
            case 2:
                return "Events";
            case 3:
                return "Schedule";
            case 4:
                return "Bible";
            case 5:
                return "Hymn Book";
            case 6:
                return "Service Book";
            case 7:
                return "Donate";
            case 8:
                return "Gallery";
            case 9:
                return "Videos";
            case 10:
                return "News Feed";
            case 11:
                return "Connect";
            case 12:
                return "Login";
            default:
                return null;
        }
    }

    //item icon home
    public static int homeIcon(int i){
        switch (i) {
            case 0:
                return R.drawable.ic_sermon_nav;
            case 1:
                return R.drawable.ic_event_nav;
            case 2:
                return R.drawable.ic_schedule_nav;
            case 3:
                return R.drawable.ic_bible_nav;
            case 4:
                return R.drawable.ic_hymn_nav;
            case 5:
                return R.drawable.ic_service_nav;
            case 6:
                return R.drawable.ic_donate_nav;
            case 7:
                return R.drawable.ic_gallery_nav;
            case 8:
                return R.drawable.ic_video_nav;
            case 9:
                return R.drawable.ic_news_nav;
            case 10:
                return R.drawable.ic_chat_nav;
            case 11:
                return R.drawable.ic_login_nav;
            default:
                return 0;
        }
    }
    //item icon navigation
    public static int navIcon(int i){
        switch (i) {
            case 0:
                return R.drawable.ic_home_nav;
            case 1:
                return R.drawable.ic_sermon_nav;
            case 2:
                return R.drawable.ic_event_nav;
            case 3:
                return R.drawable.ic_schedule_nav;
            case 4:
                return R.drawable.ic_bible_nav;
            case 5:
                return R.drawable.ic_hymn_nav;
            case 6:
                return R.drawable.ic_service_nav;
            case 7:
                return R.drawable.ic_donate_nav;
            case 8:
                return R.drawable.ic_gallery_nav;
            case 9:
                return R.drawable.ic_video_nav;
            case 10:
                return R.drawable.ic_news_nav;
            case 11:
                return R.drawable.ic_chat_nav;
            case 12:
                return R.drawable.ic_login_nav;
            default:
                return 0;
        }
    }

}
