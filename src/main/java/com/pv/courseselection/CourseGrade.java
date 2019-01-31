package com.pv.courseselection;

import java.util.ArrayList;
import java.util.Collections;

public class CourseGrade {
    private Grade _grade;
    private Course _course;
    private Student _student;
    public CourseGrade(Course course, Grade grade, Student student){
        _course = course;
        _grade = grade;
        _student = student;
    }

    public Grade getGrade() {
        return _grade;
    }

    public Course getCourse() {
        return _course;
    }

    public ArrayList<Course> getNext(){
        ArrayList<Course> toReturn = new ArrayList<>();
        Level level =  Level.getNext(_course.getLevel(), _grade);
        if(_course.nextCourses() != null && !_course.nextCourses().isEmpty()){
            ArrayList<String> strings = new ArrayList<>();
            for(Course c: _course.nextCourses()){
                strings.add(c.getName());
            }
            ArrayList<Course> courses = new ArrayList<>();
            for(String s: strings){
                ArrayList<Course> tempCourses = Course.getCourses(s, _course.nextCourses(), level);
                boolean valid = false;
                int count = 0;
                while(tempCourses != null && count < tempCourses.size() && !valid){
                    valid = tempCourses.get(count).validatePrereqs(_student);
                    count++;
                }
                if(!valid){
                    tempCourses = Course.getCourses(s, _course.nextCourses(), _course.getLevel());
                }
                if(tempCourses != null) {
                    for (Course c : tempCourses) {
                        courses.add(c);
                    }
                }
            }
            Collections.sort(courses);
            for (int i = 0; i < courses.size(); i++) {
                Course c = courses.get(i);
                if(!_student.hasTaken(c) && c.validatePrereqs(_student)){
                    toReturn.add(c);
                }
            }
        }
        if(!toReturn.isEmpty())
            return toReturn;
        School school = _student.getSchool();
        ArrayList<Course> allCourses = school.getCourses();
        ArrayList<Course> toTake = Course.getCourses(_course.getSubject(), allCourses, level);
        Collections.sort(toTake);
        for (int i = 0; i < toTake.size(); i++) {
            Course c = toTake.get(i);
            if(!_student.hasTaken(c) && c.validatePrereqs(_student)) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }

}