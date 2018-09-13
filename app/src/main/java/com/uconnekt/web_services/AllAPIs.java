package com.uconnekt.web_services;

public class AllAPIs {

 /*Base URL*/

 private static final String URL_WITHOUT_LOGIN = "https://www.connektus.com.au/service/";
 //private static final String URL_WITHOUT_LOGIN = "http://www.dev.connektus.com.au/service/";
 private static final String URL_WITH_LOGIN = URL_WITHOUT_LOGIN + "users/";

 /*Authontication*/
 public static final String REGISTRATION = URL_WITHOUT_LOGIN +"registration";
 public static final String LOGIN = URL_WITHOUT_LOGIN +"login";
 public static final String FORGOT_PASS = URL_WITHOUT_LOGIN +"resetPassword";

 /*Business Side*/
 public static final String BUSINESS_PROFILE = URL_WITH_LOGIN +"updateBusinessProfile";
 public static final String BUSI_SEARCH_LIST = URL_WITH_LOGIN +"getIndivisualSearchList";
 public static final String VIEW = URL_WITH_LOGIN +"addView";
 public static final String INDI_PROFILE = URL_WITH_LOGIN +"getUserPersonalInfo?user_id=";
 public static final String MY_PROFILE = URL_WITH_LOGIN +"getMyProfile";
 public static final String PROFILE_UPDATE = URL_WITH_LOGIN +"profileUpdate";
 public static final String REQUEST_INTERVIEW = URL_WITHOUT_LOGIN +"requests/makeRequest";
 public static final String DELETE_INTERVIEW = URL_WITHOUT_LOGIN +"requests/deleteRequest";
 public static final String OFFER_OR_NOT = URL_WITHOUT_LOGIN +"requests/offerdNotOfferd";

 /*Comman APIs*/
 public static final String EMPLOYER_PROFILE = URL_WITH_LOGIN +"getDropdownList";
 public static final String SHOW_PREFILLED_DATA = URL_WITH_LOGIN +"getUserProfile";
 public static final String PROFILE = URL_WITH_LOGIN +"getPublicProfile?";
 public static final String LOGOUT = URL_WITH_LOGIN +"logout";
 public static final String NOTIFICATION = URL_WITH_LOGIN +"sendNotification";
 public static final String CHANGE_PASSWOWD = URL_WITH_LOGIN +"changePassword";
 public static final String RECOMMEND = URL_WITH_LOGIN +"addRecommends";
 public static final String FACOURITES = URL_WITH_LOGIN +"addFavourites";
 public static final String RECOMMENDS_LIST = URL_WITH_LOGIN +"getUserRecommends?user_id=";
 public static final String FAVOURITES_LIST = URL_WITH_LOGIN +"getUserFavourites?user_id=";
 public static final String FAVOURITES_LIST_BY_ME = URL_WITH_LOGIN +"getMyFavourites?user_id=";
 public static final String RECOMMENDS_LIST_BY_ME = URL_WITH_LOGIN +"getMyRecommands?user_id=";
 public static final String TRACK_PROCESS = URL_WITHOUT_LOGIN +"requests/processData";

 public static final String TANDC = URL_WITHOUT_LOGIN +"options/getTNC";
 public static final String BADGE_COUNT = URL_WITH_LOGIN +"badge_count_unread_views";
 public static final String UPDATE_NOTIFICATION = URL_WITH_LOGIN +"update_notification";

 /*Individual Side*/
 public static final String BASIC_INFO = URL_WITH_LOGIN +"updateBasicInfo";
 public static final String EXPERIENCE = URL_WITH_LOGIN +"updateExperience";
 public static final String RESUME_CV = URL_WITH_LOGIN +"updateResume";
 public static final String INDI_SEARCH_LIST = URL_WITH_LOGIN +"getBusinessSearchList";
 public static final String REVIEW = URL_WITH_LOGIN +"addReview";
 public static final String REVIEWS_LIST = URL_WITH_LOGIN +"getUserReviews?user_id=";
 public static final String VIEW_LIST = URL_WITH_LOGIN +"getUserView?user_id=";
 public static final String INDI_PROFILE_UPDATE = URL_WITH_LOGIN +"updateIndivisualProfile";
 public static final String A_D_REQUEST = URL_WITHOUT_LOGIN +"requests/acceptDeclinedRequest";

}
