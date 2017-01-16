package bsr.server.utils;

/**
 * Created by Impresyjna on 16.01.2017.
 */
public abstract class AccountNumberAuthUtil {

    public static String calculateChecksum(String number){
        number = number.replace(" ", "");
        String nr = number + "252100";
        int modulo = 0;
        for(int i=0; i<nr.length(); i++){
            modulo = (10*modulo + Character.getNumericValue(nr.charAt(i)))%97;
        }
        modulo = 98-modulo;
        if(modulo<10){
            return "0"+String.valueOf(modulo)+number;
        }else{
            return String.valueOf(modulo)+number;
        }
    }

    public static Boolean checkChecksum(String number){
        number = number.replace(" ", "");
        String nr = "";
        for(int i=0; i<number.length(); i++){
            nr += Character.getNumericValue(number.charAt(i));
        }
        nr += "2521" + Character.getNumericValue(nr.charAt(0)) + Character.getNumericValue(nr.charAt(1));
        nr = nr.substring(2, nr.length());
        int modulo = 0;
        for(int i=0; i<nr.length(); i++){
            modulo = (10*modulo + Character.getNumericValue(nr.charAt(i)))%97;
        }

        if(modulo==1){
            return true;
        }else{
            return false;
        }
    }

}
