package model.dyno;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

public class FormValidation {
    public static boolean checkEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkInt(String num,int minlength,int maxlength){

        int value;


        try{

            if(num.isEmpty())
                return false;

            value = Integer.parseInt(num);

            if(minlength > 0){
                if(num.length() < minlength){
                    return false;
                }
            }

            if(maxlength > 0){
                if(num.length() > maxlength){
                    return false;
                }
            }

            return true;

        }catch (NumberFormatException e){
            Log.i("NumFormat","cannot format to integer");
            return false;
        }

    }
    public static boolean checkString(String num,int minlength,int maxlength){

            if(num.isEmpty())
                return false;

        if(minlength > 0){
            if(num.length() < minlength){
                return false;
            }
        }

        if(maxlength > 0){
            if(num.length() > maxlength){
                return false;
            }
        }

            return true;


    }
}
