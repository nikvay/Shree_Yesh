package com.shreeyesh.domain.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SuccessModule {
    private String msg;
    private String error_code;
    private String img_base_url;
    private String school_name;

    @SerializedName("user_details")
    private ArrayList<LoginModule> user_details;

    @SerializedName("splash_screen")
    private ArrayList<SplashModule> splashModuleArrayList;

    @SerializedName("division_list")
    private ArrayList<ClassDivisionModule> classDivisionModuleArrayList;

    @SerializedName("class_list")
    private ArrayList<ClassListModule> classListModuleArrayArrayList;

    @SerializedName("student_list")
    private ArrayList<StudentListModule> studentListModuleArrayArrayList;

    @SerializedName("teacher_list")
    private ArrayList<TeacherListModule> teacherListModuleArrayArrayList;

    @SerializedName("event_list")
    private ArrayList<EventModule> eventModuleArrayList;

    @SerializedName("gallery_list")
    private ArrayList<GalleryModule> galleryModuleArrayList;

    @SerializedName("student_attendances_list")
    private ArrayList<StudentPAListModule> studentPAListModuleArrayList;

    @SerializedName("note_list")
    private ArrayList<NotesModule> notesModuleArrayList;

    @SerializedName("teacher_note_list")
    private ArrayList<NotesModule> notesTeacherModuleArrayList;

    @SerializedName("video_list")
    private ArrayList<VideoListModule> videoListModuleArrayList;

    @SerializedName("timetable_list")
    private ArrayList<TimeTableModule> timeTableModuleArrayList;

    @SerializedName("acadamic_calender_details")
    private ArrayList<HolidayListModule> holidayListModuleArrayList;

    @SerializedName("leave_list")
    private ArrayList<LeaveListModule> leaveListModuleArrayList;

    @SerializedName("public_notification_type")
    private ArrayList<NotificationTypeModule> notificationTypeModuleArrayList;

    @SerializedName("splash_images")
    private ArrayList<HomeImagesModule> homeImagesModuleArrayList;

    @SerializedName("inquiry_details")
    private ArrayList<EnquiryModule> enquiryModuleArrayList;

    @SerializedName("library_entery_details")
    private ArrayList<LibraryRecordModule> libraryRecordModuleArrayList;

    @SerializedName("student_library_details")
    private ArrayList<LibraryStudentModule> libraryStudentModuleArrayList;

    @SerializedName("teacher_library_details")
    private ArrayList<LibraryTeacherModule> libraryTeacherModuleArrayList;

    @SerializedName("test_list")
    private ArrayList<ResultExamNameModule> resultExamNameModuleArrayList;

    @SerializedName("group_list")
    private ArrayList<ChatGroupListModule> chatGroupListModuleArrayList;

     @SerializedName("chat_list")
    private ArrayList<ChattingModule> chattingModuleArrayList;

    public ArrayList<ChattingModule> getChattingModuleArrayList() {
        return chattingModuleArrayList;
    }

    public void setChattingModuleArrayList(ArrayList<ChattingModule> chattingModuleArrayList) {
        this.chattingModuleArrayList = chattingModuleArrayList;
    }

    public ArrayList<ChatGroupListModule> getChatGroupListModuleArrayList() {
        return chatGroupListModuleArrayList;
    }

    public void setChatGroupListModuleArrayList(ArrayList<ChatGroupListModule> chatGroupListModuleArrayList) {
        this.chatGroupListModuleArrayList = chatGroupListModuleArrayList;
    }

    public ArrayList<ResultExamNameModule> getResultExamNameModuleArrayList() {
        return resultExamNameModuleArrayList;
    }

    public void setResultExamNameModuleArrayList(ArrayList<ResultExamNameModule> resultExamNameModuleArrayList) {
        this.resultExamNameModuleArrayList = resultExamNameModuleArrayList;
    }

    public ArrayList<LoginModule> getUser_details() {
        return user_details;
    }

    public void setUser_details(ArrayList<LoginModule> user_details) {
        this.user_details = user_details;
    }

    public ArrayList<SplashModule> getSplashModuleArrayList() {
        return splashModuleArrayList;
    }

    public void setSplashModuleArrayList(ArrayList<SplashModule> splashModuleArrayList) {
        this.splashModuleArrayList = splashModuleArrayList;
    }

    public ArrayList<ClassDivisionModule> getClassDivisionModuleArrayList() {
        return classDivisionModuleArrayList;
    }

    public void setClassDivisionModuleArrayList(ArrayList<ClassDivisionModule> classDivisionModuleArrayList) {
        this.classDivisionModuleArrayList = classDivisionModuleArrayList;
    }

    public ArrayList<ClassListModule> getClassListModuleArrayArrayList() {
        return classListModuleArrayArrayList;
    }

    public void setClassListModuleArrayArrayList(ArrayList<ClassListModule> classListModuleArrayArrayList) {
        this.classListModuleArrayArrayList = classListModuleArrayArrayList;
    }

    public ArrayList<StudentListModule> getStudentListModuleArrayArrayList() {
        return studentListModuleArrayArrayList;
    }

    public void setStudentListModuleArrayArrayList(ArrayList<StudentListModule> studentListModuleArrayArrayList) {
        this.studentListModuleArrayArrayList = studentListModuleArrayArrayList;
    }


    public ArrayList<TeacherListModule> getTeacherListModuleArrayArrayList() {
        return teacherListModuleArrayArrayList;
    }

    public void setTeacherListModuleArrayArrayList(ArrayList<TeacherListModule> teacherListModuleArrayArrayList) {
        this.teacherListModuleArrayArrayList = teacherListModuleArrayArrayList;
    }


    public ArrayList<EventModule> getEventModuleArrayList() {
        return eventModuleArrayList;
    }

    public void setEventModuleArrayList(ArrayList<EventModule> eventModuleArrayList) {
        this.eventModuleArrayList = eventModuleArrayList;
    }

    public ArrayList<GalleryModule> getGalleryModuleArrayList() {
        return galleryModuleArrayList;
    }

    public void setGalleryModuleArrayList(ArrayList<GalleryModule> galleryModuleArrayList) {
        this.galleryModuleArrayList = galleryModuleArrayList;
    }

    public ArrayList<StudentPAListModule> getStudentPAListModuleArrayList() {
        return studentPAListModuleArrayList;
    }

    public void setStudentPAListModuleArrayList(ArrayList<StudentPAListModule> studentPAListModuleArrayList) {
        this.studentPAListModuleArrayList = studentPAListModuleArrayList;
    }

    public ArrayList<NotesModule> getNotesModuleArrayList() {
        return notesModuleArrayList;
    }

    public void setNotesModuleArrayList(ArrayList<NotesModule> notesModuleArrayList) {
        this.notesModuleArrayList = notesModuleArrayList;
    }

    public ArrayList<NotesModule> getNotesTeacherModuleArrayList() {
        return notesTeacherModuleArrayList;
    }

    public void setNotesTeacherModuleArrayList(ArrayList<NotesModule> notesTeacherModuleArrayList) {
        this.notesTeacherModuleArrayList = notesTeacherModuleArrayList;
    }

    public ArrayList<VideoListModule> getVideoListModuleArrayList() {
        return videoListModuleArrayList;
    }

    public void setVideoListModuleArrayList(ArrayList<VideoListModule> videoListModuleArrayList) {
        this.videoListModuleArrayList = videoListModuleArrayList;
    }

    public ArrayList<TimeTableModule> getTimeTableModuleArrayList() {
        return timeTableModuleArrayList;
    }

    public void setTimeTableModuleArrayList(ArrayList<TimeTableModule> timeTableModuleArrayList) {
        this.timeTableModuleArrayList = timeTableModuleArrayList;
    }

    public ArrayList<HolidayListModule> getHolidayListModuleArrayList() {
        return holidayListModuleArrayList;
    }

    public void setHolidayListModuleArrayList(ArrayList<HolidayListModule> holidayListModuleArrayList) {
        this.holidayListModuleArrayList = holidayListModuleArrayList;
    }

    public ArrayList<LeaveListModule> getLeaveListModuleArrayList() {
        return leaveListModuleArrayList;
    }

    public void setLeaveListModuleArrayList(ArrayList<LeaveListModule> leaveListModuleArrayList) {
        this.leaveListModuleArrayList = leaveListModuleArrayList;
    }

    public ArrayList<NotificationTypeModule> getNotificationTypeModuleArrayList() {
        return notificationTypeModuleArrayList;
    }

    public void setNotificationTypeModuleArrayList(ArrayList<NotificationTypeModule> notificationTypeModuleArrayList) {
        this.notificationTypeModuleArrayList = notificationTypeModuleArrayList;
    }

    public ArrayList<HomeImagesModule> getHomeImagesModuleArrayList() {
        return homeImagesModuleArrayList;
    }

    public void setHomeImagesModuleArrayList(ArrayList<HomeImagesModule> homeImagesModuleArrayList) {
        this.homeImagesModuleArrayList = homeImagesModuleArrayList;
    }

    public ArrayList<EnquiryModule> getEnquiryModuleArrayList() {
        return enquiryModuleArrayList;
    }

    public void setEnquiryModuleArrayList(ArrayList<EnquiryModule> enquiryModuleArrayList) {
        this.enquiryModuleArrayList = enquiryModuleArrayList;
    }

    public ArrayList<LibraryRecordModule> getLibraryRecordModuleArrayList() {
        return libraryRecordModuleArrayList;
    }

    public void setLibraryRecordModuleArrayList(ArrayList<LibraryRecordModule> libraryRecordModuleArrayList) {
        this.libraryRecordModuleArrayList = libraryRecordModuleArrayList;
    }

    public ArrayList<LibraryStudentModule> getLibraryStudentModuleArrayList() {
        return libraryStudentModuleArrayList;
    }

    public void setLibraryStudentModuleArrayList(ArrayList<LibraryStudentModule> libraryStudentModuleArrayList) {
        this.libraryStudentModuleArrayList = libraryStudentModuleArrayList;
    }

    public ArrayList<LibraryTeacherModule> getLibraryTeacherModuleArrayList() {
        return libraryTeacherModuleArrayList;
    }

    public void setLibraryTeacherModuleArrayList(ArrayList<LibraryTeacherModule> libraryTeacherModuleArrayList) {
        this.libraryTeacherModuleArrayList = libraryTeacherModuleArrayList;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }


    public String getImg_base_url() {
        return img_base_url;
    }

    public void setImg_base_url(String img_base_url) {
        this.img_base_url = img_base_url;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }


}
