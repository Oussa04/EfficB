package org.meteor.efficaisse.utils;

public class Constants {


    public static String HTTP_CLIENT_ADDRESS="http://localhost:8081/";
    public static String getConfirmationLink(String activationCode){
        return HTTP_CLIENT_ADDRESS+"confirm?token="+activationCode;
    }
    
}
