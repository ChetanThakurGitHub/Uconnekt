package com.uconnekt.web_services;

public class AllAPIs {

    /*Base URL*/
    private static final String URL_WITHOUT_LOGIN = "http://uconnekt.com.au/service/";
    private static final String URL_WITH_LOGIN = URL_WITHOUT_LOGIN +"users/";

    /*Authontication*/
    public static final String REGISTRATION = URL_WITHOUT_LOGIN +"registration";
    public static final String LOGIN = URL_WITHOUT_LOGIN +"login";
    public static final String FORGOT_PASS = URL_WITHOUT_LOGIN +"resetPassword";

    /*Business Side*/
    public static final String BUSINESS_PROFILE = URL_WITH_LOGIN +"updateBusinessProfile";
    public static final String BUSI_SEARCH_LIST = URL_WITH_LOGIN +"getIndivisualSearchList";

    /*Comman APIs*/
    public static final String EMPLOYER_PROFILE = URL_WITH_LOGIN +"getDropdownList";


    /*Individual Side*/
    public static final String BASIC_INFO = URL_WITH_LOGIN +"updateBasicInfo";
    public static final String EXPERIENCE = URL_WITH_LOGIN +"updateExperience";
    public static final String RESUME_CV = URL_WITH_LOGIN +"updateResume";
    public static final String INDI_SEARCH_LIST = URL_WITH_LOGIN +"getBusinessSearchList";

}
