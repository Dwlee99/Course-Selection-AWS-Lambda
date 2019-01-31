package com.pv.courseselection;

import java.util.ArrayList;

public class School {
    private String _name; //the name of the school
    private String _emailExtension; //the @school.com for the school (ex: pvsd.org)
    private ArrayList<Course> _courses = new ArrayList<>(); //the courses that the school offers
    private YearRequirement[] _graduationRequirements = new YearRequirement[GradeYear.values().length];

    /**
     * Creates a school object
     * @param name the name of the school
     */
    public School(String name){
        _name = name;
        Subject[] subjects = Subject.values();
        for (int i = 0; i < _graduationRequirements.length; i++) {
            _graduationRequirements[i] = new YearRequirement(GradeYear.values()[i]);
        }
    }

    public ArrayList<Course> getCourses() {
        return _courses;
    }

    /**
     * Sets the credits value in the Requirement object that has the inputted subject in
     * _subjectRequirements to the inputted credits
     * @param subject the subject to set a credit requirement for
     * @param credits the amount of credits to be required
     */
    public void setRequirement(GradeYear year, Subject subject, double credits){
        for (int i = 0; i < _graduationRequirements.length; i++) {
            YearRequirement r = _graduationRequirements[i];
            if(r.getGradeYear() == year){
                r.setRequirement(subject, credits);
                return;
            }
        }
    }
    public Requirement getRequirement(GradeYear year, Subject subject){
        for (int i = 0; i < _graduationRequirements.length; i++) {
            YearRequirement r = _graduationRequirements[i];
            if(r.getGradeYear() == year){
                return r.getRequirement(subject);
            }
        }
        return null;
    }
    /**
     * Adds a course to the courses offered at the school
     * @param course the course to be added
     * @param courses any additional courses to be added
     */
    public void addCourse(Course course, Course ... courses){
        _courses.add(course);
        for (Course c: courses){
            _courses.add(c);
        }
    }
    public void addCourse(ArrayList<Course> courses){
        for(Course c: courses){
            _courses.add(c);
        }
    }
    public void initializePrereqs(){
        for(Course c: _courses){
            c.initializePrereqs(_courses);
        }
    }

    public ArrayList<Course> getCourses(String name){
        return Course.getCourses(name, _courses);
    }
    public ArrayList<Course> getCourses(Subject subject){
        return Course.getCourses(subject, _courses);
    }

    /**
     * Removes a course from the courses offered at the school
     * @param course the course to be removed
     * @return True if the course is removed, false if not
     */
    public boolean removeCourse(Course course){
        return _courses.remove(course);
    }

}