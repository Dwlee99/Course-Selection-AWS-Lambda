package com.pv.courseselection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Student {
    private String _name;
    private GradeYear _gradeYear;
    private School _school;
    private ArrayList<CourseGrade> _freshmanCourses = new ArrayList<>();
    private ArrayList<CourseGrade> _sophomoreCourses = new ArrayList<>();
    private ArrayList<CourseGrade> _juniorCourses = new ArrayList<>();
    private ArrayList<CourseGrade> _seniorCourses = new ArrayList<>();
    private ArrayList<Course> _allTaken = new ArrayList<>();
    public Student(String name, GradeYear gradeYear, School school){
        _name = name;
        _gradeYear = gradeYear;
        _school = school;
    }
    public String getName() {
        return _name;
    }


    public GradeYear getGradeYear() {
        return _gradeYear;
    }

    public void setGradeYear(GradeYear _gradeYear) {
        this._gradeYear = _gradeYear;
    }

    public School getSchool() {
        return _school;
    }

    public ArrayList<CourseGrade> getFreshmanCourses() {
        return _freshmanCourses;
    }

    public void setFreshmanCourses(ArrayList<CourseGrade> freshmanCourses) {
        _freshmanCourses = freshmanCourses;
    }
    public void addFreshmanCourse(CourseGrade courseGrade){
        _freshmanCourses.add(courseGrade);
    }

    public ArrayList<CourseGrade> getSophomoreCourses() {
        return _sophomoreCourses;
    }

    public void setSophomoreCourses(ArrayList<CourseGrade> sophomoreCourses) {
        _sophomoreCourses = sophomoreCourses;
    }
    public void addSophomoreCourse(CourseGrade courseGrade){
        _sophomoreCourses.add(courseGrade);
    }
    public ArrayList<CourseGrade> getJuniorCourses() {
        return _juniorCourses;
    }

    public void setJuniorCourses(ArrayList<CourseGrade> juniorCourses) {
        _juniorCourses = juniorCourses;
    }
    public void addJuniorCourse(CourseGrade courseGrade){
        _juniorCourses.add(courseGrade);
    }
    public ArrayList<CourseGrade> getSeniorCourses() {
        return _seniorCourses;
    }

    public void setSeniorCourses(ArrayList<CourseGrade> seniorCourses) {
        _seniorCourses = seniorCourses;
    }

    public void addSeniorCourse(CourseGrade courseGrade){
        _seniorCourses.add(courseGrade);
    }

    public ArrayList<Course> getNext() {
        ArrayList<Course> toReturn = new ArrayList<>();
        if(_gradeYear == GradeYear.FRESHMAN){
            for (int i = 0; i < _freshmanCourses.size(); i++) {
                Subject subject = _freshmanCourses.get(i).getCourse().getSubject();
                if(!(subject == Subject.HEALTH || subject == Subject.PHYSICAL_EDUCATION)) {
                    ArrayList<Course> tempCourses = _freshmanCourses.get(i).getNext();
                    toReturn.addAll(tempCourses);
                }
            }
        }
        else if(_gradeYear == GradeYear.SOPHOMORE){
            for (int i = 0; i < _sophomoreCourses.size(); i++) {
                Subject subject = _sophomoreCourses.get(i).getCourse().getSubject();
                if(!(subject == Subject.HEALTH || subject == Subject.PHYSICAL_EDUCATION)) {
                    ArrayList<Course> tempCourses = _sophomoreCourses.get(i).getNext();
                    toReturn.addAll(tempCourses);
                }
            }
        }
        else if(_gradeYear == GradeYear.JUNIOR){
            for (int i = 0; i < _juniorCourses.size(); i++) {
                Subject subject = _juniorCourses.get(i).getCourse().getSubject();
                if(!(subject == Subject.HEALTH || subject == Subject.PHYSICAL_EDUCATION)) {
                    ArrayList<Course> tempCourses = _juniorCourses.get(i).getNext();
                    toReturn.addAll(tempCourses);
                }
            }
        }
        else if(_gradeYear == GradeYear.SENIOR){

        }
        if(!meetsPhysEdReq()){
            CourseGrade physEd = new CourseGrade(_school.getCourses(Subject.PHYSICAL_EDUCATION).get(0), Grade.APLUS, this);
            toReturn.addAll(physEd.getNext());
        }
        if(!meetsHealthReq()) {
            CourseGrade health = new CourseGrade(_school.getCourses(Subject.HEALTH).get(0), Grade.APLUS, this);
            toReturn.addAll(health.getNext());
        }
        if(!meetsTechReq()) {
            ArrayList<Course> techCourses = _school.getCourses(Subject.TECHNOLOGY);
            for(Course c: techCourses){
                if(c.validatePrereqs(this)){
                    toReturn.add(c);
                }
            }
        }

        Set<Course> set = new LinkedHashSet<>(toReturn);
        toReturn.clear();
        toReturn.addAll(set);
        Collections.sort(toReturn);
        return toReturn;
    }

    private boolean meetsTechReq() {
        return meetsReq(Subject.TECHNOLOGY);
    }
    private boolean meetsReq(Subject subject){
        Requirement r = _school.getRequirement(GradeYear.GRADUATION, subject);
        ArrayList<Course> allTaken = getAllTaken();
        double credits = r.getCredits();
        int count = 0;
        boolean meetsReq = false;
        while(count < allTaken.size() && !meetsReq){
            Course c = allTaken.get(count);
            Subject s = c.getSubject();
            if(s == subject){
                credits -= c.getCredits();
                meetsReq = credits <= 0;
            }
            count++;
        }
        return meetsReq;
    }
    private boolean meetsHealthReq() {
        return meetsReq(Subject.HEALTH);    }

    private boolean meetsPhysEdReq() {
        return meetsReq(Subject.PHYSICAL_EDUCATION);
    }

    public boolean hasTaken(Course course) {
        for(Course c: getAllTaken()){
            if(c.equals(course)){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Course> getAllTaken(){
        if(!_allTaken.isEmpty()){
            return _allTaken;
        }
        for(CourseGrade c: _freshmanCourses){
            _allTaken.add(c.getCourse());
        }
        for(CourseGrade c: _sophomoreCourses){
            _allTaken.add(c.getCourse());
        }
        for(CourseGrade c: _juniorCourses){
            _allTaken.add(c.getCourse());
        }
        for(CourseGrade c: _seniorCourses){
            _allTaken.add(c.getCourse());
        }
        return _allTaken;
    }
}