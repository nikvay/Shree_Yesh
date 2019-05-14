package com.shreeyesh.domain.network;

import com.shreeyesh.domain.module.HomeWorkModule;
import com.shreeyesh.domain.module.NotificationModule;
import com.shreeyesh.domain.module.SuccessModule;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST(EndApi.LOGIN)
    @FormUrlEncoded
    Call<SuccessModule> loginCall(@Field("login_type") String login_type,
                                  @Field("user_name") String user_name,
                                  @Field("password") String password,
                                  @Field("firebase_id") String firebase_id);

  /*  @Headers("Content-Type: application/json")
    @GET(EndApi.CLASS_LIST)
    Call<SuccessModule> classListCall();*/

    @POST(EndApi.CLASS_LIST)
    @FormUrlEncoded
    Call<SuccessModule> classListCall(@Field("user_id") String user_id,
                                      @Field("user_type") String user_type);


    @POST(EndApi.CLASS_STD_LIST)
    @FormUrlEncoded
    Call<SuccessModule> classStudentListModuleCall(@Field("class_id") String class_id,
                                                   @Field("division_id") String division_id);

    @POST(EndApi.SEND_NOTIFICATION)
    @FormUrlEncoded
    Call<SuccessModule> notificationModuleCall(@Field("user_id") String user_id,
                                               @Field("title") String title,
                                               @Field("description") String description,
                                               @Field("notification_type") String notification_type,
                                               @Field("teacher_id_array") JSONArray teacher_id_array,
                                               @Field("class_id") String class_id,
                                               @Field("student_id_array") JSONArray student_id_array,
                                               @Field("base") String base,
                                               @Field("extention") String extention,
                                               @Field("sub_type") String sub_type,
                                               @Field("user_type") String user_type);

    @GET(EndApi.TEACHER_LIST)
    Call<SuccessModule> teacherListModuleCall();


    @POST(EndApi.NOTIFICATION_LIST)
    @FormUrlEncoded
    Call<NotificationModule> notificationOneModuleCall(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("sub_type") String sub_type);

    @POST(EndApi.SEND_HOMEWORK)
    @FormUrlEncoded
    Call<SuccessModule> responseModuleCall(@Field("title") String title,
                                           @Field("img_name") String img_name,
                                           @Field("description") String description,
                                           @Field("given_date") String given_date,
                                           @Field("submission_date") String submission_date,
                                           @Field("division_id") JSONArray division_id,
                                           @Field("teacher_id") String teacher_id,
                                           @Field("class_id") String class_id);

    @POST(EndApi.CLASS_DIV)
    @FormUrlEncoded
    Call<SuccessModule> classDivisionModuleCall(@Field("class_id") String class_id);


    @POST(EndApi.HOME_WORK_STD_LIST)
    @FormUrlEncoded
    Call<HomeWorkModule> homeWorkStdModuleCall(@Field("student_id") String student_id);

    @POST(EndApi.HOME_WORK_TEACHER_LIST)
    @FormUrlEncoded
    Call<HomeWorkModule> homeWorkTeacherModuleCall(@Field("teacher_id") String teacher_id);

    @POST(EndApi.EVENT_ADD)
    @FormUrlEncoded
    Call<SuccessModule> evenAddCall(@Field("event_title") String event_title,
                                    @Field("event_date") String event_date,
                                    @Field("event_description") String event_description,
                                    @Field("location") String location,
                                    @Field("image") String image);

    @POST(EndApi.EVENT_LIST)
    @FormUrlEncoded
    Call<SuccessModule> evenListCall(@Field("user_id") String user_id);

    @POST(EndApi.EVENT_DELETE)
    @FormUrlEncoded
    Call<SuccessModule> evenDeleteCall(@Field("event_id") String event_id);

    @POST(EndApi.ATTENDANCE_ADD)
    @FormUrlEncoded
    Call<SuccessModule> attendanceAddCall(@Field("class_id") String class_id,
                                          @Field("division_id") String division_id,
                                          @Field("teacher_id") String teacher_id,
                                          @Field("present_id_array") JSONArray present_id_array,
                                          @Field("absent_id_array") JSONArray absent_id_array,
                                          @Field("date") String date);

    @POST(EndApi.ATTENDANCE_LIST)
    @FormUrlEncoded
    Call<HomeWorkModule> attendanceListCall(@Field("class_id") String class_id,
                                            @Field("division_id") String division_id,
                                            @Field("teacher_id") String teacher_id,
                                            @Field("date") String date);

    @GET(EndApi.Gallery_LIST)
    Call<SuccessModule> galleryListCall();

    @POST(EndApi.Gallery_ADD)
    @FormUrlEncoded
    Call<SuccessModule> galleryAddCall(@Field("title") String title,
                                       @Field("img_name") String img_name,
                                       @Field("user_id") String user_id);

    @POST(EndApi.STD_ATT_LIST_MONTH)
    @FormUrlEncoded
    Call<SuccessModule> studentAttMonthCall(@Field("month") String month,
                                            @Field("student_id") String student_id);

    @POST(EndApi.NOTES_UPLOAD)
    @FormUrlEncoded
    Call<SuccessModule> notesUploadCall(@Field("note_document") String note_document,
                                        @Field("note_title") String note_title,
                                        @Field("teacher_id") String teacher_id,
                                        @Field("division_id") String division_id,
                                        @Field("class_id") String class_id,
                                        @Field("extention") String extention);

    @POST(EndApi.NOTES_LIST_STD)
    @FormUrlEncoded
    Call<SuccessModule> notesListStdCall(@Field("student_id") String student_id);

    @POST(EndApi.DELETE_GALLERY)
    @FormUrlEncoded
    Call<SuccessModule> deleteGalleryCall(@Field("gallery_id") String gallery_id);

    @POST(EndApi.DELETE_HOME_WORK_TEACHER)
    @FormUrlEncoded
    Call<SuccessModule> deleteHomeWorkCall(@Field("homework_id") String homework_id,
                                           @Field("teacher_id") String teacher_id);

    @POST(EndApi.NOTES_LIST_TEACHER)
    @FormUrlEncoded
    Call<SuccessModule> notesListTeacherCall(@Field("teacher_id") String teacher_id);

    @POST(EndApi.NOTES_DELETE_TEACHER)
    @FormUrlEncoded
    Call<SuccessModule> notesDeleteTeacherCall(@Field("note_id") String note_id);

    @GET(EndApi.VIDEO_TUTORIALS)
    Call<SuccessModule> videoTutorialsListCall();

    @POST(EndApi.UPLOAD_TIME_TABLE)
    @FormUrlEncoded
    Call<SuccessModule> uploadTimeTableCall(@Field("user_id") String user_id,
                                            @Field("type") String type,
                                            @Field("class_id") String class_id,
                                            @Field("division_id") String division_id,
                                            @Field("title") String title,
                                            @Field("note_document") String note_document,
                                            @Field("extension") String extension);

    @POST(EndApi.TIME_TABLE_LIST)
    @FormUrlEncoded
    Call<SuccessModule> timeTableListCall(@Field("class_id") String class_id,
                                          @Field("division_id") String division_id,
                                          @Field("type") String type,
                                          @Field("teacher_id") String teacher_id,
                                          @Field("student_id") String student_id);

    @POST(EndApi.HOLIDAY_LIST)
    @FormUrlEncoded
    Call<SuccessModule> holidayListCall(@Field("user_id") String user_id);

    @POST(EndApi.STD_APPLY_LEAVE)
    @FormUrlEncoded
    Call<SuccessModule> studentApplyLeaveCall(@Field("student_id") String student_id,
                                              @Field("leave_subject") String leave_subject,
                                              @Field("leave_description") String leave_description,
                                              @Field("start_date") String start_date,
                                              @Field("end_date") String end_date);

    @POST(EndApi.LEAVE_LIST)
    @FormUrlEncoded
    Call<SuccessModule> leaveListCall(@Field("class_id") String class_id,
                                      @Field("teacher_id") String teacher_id,
                                      @Field("student_id") String student_id);

    @POST(EndApi.LEAVE_APPROVE)
    @FormUrlEncoded
    Call<SuccessModule> leaveApproveCall(@Field("leave_id") String leave_id,
                                         @Field("teacher_id") String teacher_id,
                                         @Field("leave_status") String leave_status,
                                         @Field("teacher_comment") String teacher_comment);

    @POST(EndApi.NOTIFICATION_TYPE)
    @FormUrlEncoded
    Call<SuccessModule> notificationTypeCall(@Field("user_id") String user_id);

    @POST(EndApi.SPLASH)
    @FormUrlEncoded
    Call<SuccessModule> splashCall(@Field("user_id") String user_id);

    @POST(EndApi.HOME_SPLASH)
    @FormUrlEncoded
    Call<SuccessModule> homeSplashCall(@Field("user_id") String user_id);

    @POST(EndApi.ENQUIRY_LIST)
    @FormUrlEncoded
    Call<SuccessModule> enquiryListCall(@Field("teacher_id") String teacher_id);

    @POST(EndApi.ENQUIRY_ADD)
    @FormUrlEncoded
    Call<SuccessModule> enquiryAddCall(@Field("full_name") String full_name,
                                       @Field("class_name") String class_name,
                                       @Field("phone_number") String phone_number,
                                       @Field("email_id") String email_id,
                                       @Field("address") String address,
                                       @Field("status") String status,
                                       @Field("remark") String remark,
                                       @Field("date") String date,
                                       @Field("teacher_id") String teacher_id);

    @POST(EndApi.LIBRARY_ADD)
    @FormUrlEncoded
    Call<SuccessModule> libraryAddCall(@Field("librarian_id") String librarian_id,
                                       @Field("user_id") String user_id,
                                       @Field("type") String type,
                                       @Field("class_id") String class_id,
                                       @Field("division_id") String division_id,
                                       @Field("book_id") String book_id,
                                       @Field("book_name") String book_name,
                                       @Field("issue_date") String issue_date,
                                       @Field("return_date") String return_date);

    @POST(EndApi.LIBRARY_LIST)
    @FormUrlEncoded
    Call<SuccessModule> libraryListCall(@Field("user_id") String user_id,
                                        @Field("type") String type);

    @POST(EndApi.LIBRARY_LIST_LIBRARIAN)
    @FormUrlEncoded
    Call<SuccessModule> libraryListLibrarianCall(@Field("user_id") String user_id);

    @POST(EndApi.RESULT_LIST)
    @FormUrlEncoded
    Call<SuccessModule> resultListCall(@Field("student_id") String student_id);

    @POST(EndApi.GROUP_LIST)
    @FormUrlEncoded
    Call<SuccessModule> grpListCall(@Field("user_id") String user_id,
                                    @Field("type") String type,
                                    @Field("class_id") String class_id,
                                    @Field("division_id") String division_id);

    @POST(EndApi.CHATTING_ADD)
    @FormUrlEncoded
    Call<SuccessModule> sendChatMessageCall(@Field("user_id") String user_id,
                                            @Field("user_type") String user_type,
                                            @Field("message") String message,
                                            @Field("group_id") String group_id);

    @POST(EndApi.CHATTING_LIST)
    @FormUrlEncoded
    Call<SuccessModule> chattingListCall(@Field("group_id") String group_id);

    @POST(EndApi.PROFILE_EDIT)
    @FormUrlEncoded
    Call<SuccessModule> editProfileCall(@Field("user_id") String user_id,
                                        @Field("user_type") String user_type,
                                        @Field("email_id") String email_id,
                                        @Field("contact_number") String contact_number,
                                        @Field("password") String password);


}
//title,img_name,description,given_date,submission_date,division_id,teacher_id,class_id