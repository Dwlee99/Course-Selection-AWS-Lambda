package com.pv.courseselection;

import java.util.ArrayList;
import java.util.Comparator;

public class Course implements Comparable<Course> {

    private String _name; //name of the course
    private String _description; //description of the course
    private ArrayList<Course> _prerequisiteCourses = new ArrayList<>(); //the classes needed to be taken before this one
    private ArrayList<Course> _prerequisiteToCourses = new ArrayList<>();
    private double _credits; //the amount of credits the course is worth
    private Subject _subject; //the credit that this course will meet the requirement of
    private Level _level; //the level that the course is on (Academic, Prep, Honors, AP)
    private boolean _hasGIEP;
    private String _preReqString;
    private String _preReqToString;
    private int _code;
    private boolean _isCore;
    private GradeYear _minGrade;
    private GradeYear _maxGrade;
    /**
     * Creates a course object
     * @param name the name of the course
     * @param description the description of the course
     */

    public Course(String name, int code, Level level, boolean isCore, boolean hasGIEP,
                  Subject subject, double credits, GradeYear minGrade, GradeYear maxGrade,
                  String preReqs, String preReqTo, String description){
        _name = name;
        _code = code;
        _level = level;
        _isCore = isCore;
        _hasGIEP = hasGIEP;
        _subject = subject;
        _credits = credits;
        _minGrade = minGrade;
        _maxGrade = maxGrade;
        _preReqString = preReqs;
        _preReqToString = preReqTo;
        _description = description;

    }
    public boolean requiresGIEP() {
        return _hasGIEP;
    }

    /**
     * Sets the name of the course
     * @param name - new name of the course
     */
    public void setName(String name){
        _name = name;
    }

    /**
     * @return the name of the course
     */
    public String getName(){
        return _name;
    }

    public Level getLevel() {
        return _level;
    }

    /**
     * Sets the description of the course
     * @param description - new description of the course
     */
    public void setDescription(String description){
        _description = description;
    }

    /**
     * @return the description of the course
     */
    public String getDescription(){
        return _description;
    }
    /**
     * Sets the subject of the course
     * @param subject - the new subject classification
     */
    public void setSubject(Subject subject){
        _subject = subject;
    }

    public Subject getSubject(){
        return _subject;
    }
    /**
     * Adds a prerequisite to the course created
     * @param course the course to be added to the prerequisites
     * @param courses any additional courses to be added
     */
    public void addPrerequisite(Course course, Course ... courses){
        _prerequisiteCourses.add(course);
        for(Course c: courses){
            _prerequisiteCourses.add(c);
        }
    }
    public void addPrerequisite(ArrayList<Course> courses){
        for(Course c: courses){
            _prerequisiteCourses.add(c);
        }
    }

    public double getCredits() {
        return _credits;
    }

    /**
     * Removes a prerequisite course from the list
     * @param course the course to be removed
     * @return True if the course is removed, false if not
     */
    public boolean removePrerequisite(Course course){
        return _prerequisiteCourses.remove(course);
    }

    public boolean validatePrereqs(Student student) {
        int gradeNumber = student.getGradeYear().getGradeNumber();
        if (!(gradeNumber + 1 >= _minGrade.getGradeNumber() && gradeNumber + 1 <= _maxGrade.getGradeNumber())) {
            return false;
        }
        if (_prerequisiteCourses.isEmpty()) {
            return true;
        } else {
            for (int i = 0; i < _prerequisiteCourses.size(); i++) {
                Course toFind = _prerequisiteCourses.get(i);
                if (!student.hasTaken(toFind)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return  "name='" + _name + '\'' +
                ", description='" + _description + '\'' +
                ", credits=" + _credits +
                ", subject=" + _subject +
                ", level=" + _level +
                ", hasGIEP=" + _hasGIEP +
                ", code=" + _code +
                ", isCore=" + _isCore +
                ", minGrade=" + _minGrade +
                ", maxGrade=" + _maxGrade + "\n";
    }
    public ArrayList<Course> getPreprequisites(){
        return _prerequisiteCourses;
    }
    public static ArrayList<Course> getCourses(String name, ArrayList<Course> courses){
        ArrayList<Course> toReturn = new ArrayList<>();
        for (Course c: courses) {
            String courseName = c.getName().replaceAll("[-\\s]","");
            String findName = name.replaceAll("[-\\s]", "");
            if(!(findName.contains("Honors") || findName.contains("AP") || findName.contains("Advanced Placement"))){
                courseName = courseName.replaceAll("(Honors)|(Advanced\\sPlacement)|(AP)|(College Prep)|(Preparatory)|(Prep)|(Academic)", "");
            }
            if (courseName.toLowerCase().equals(findName.toLowerCase())) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }
    public static ArrayList<Course> getCourses(Course course, ArrayList<Course> courses){
        ArrayList<Course> toReturn = new ArrayList<>();
        for (Course c: courses) {
            String courseName = c.getName().replaceAll("[-\\s]","");
            String findName = course.getName().replaceAll("[-\\s]|(Honors)|(Advanced\\sPlacement)|(AP)|(College Prep)|(Preparatory)|(Prep)|(Academic)", "");
            if (courseName.toLowerCase().equals(findName.toLowerCase())) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }
    public static ArrayList<Course> getCourses(String name, ArrayList<Course> courses, Level level){
        ArrayList<Course> toReturn = new ArrayList<>();
        for (Course c: courses) {
            String courseName = c.getName().replaceAll("[-\\s]|(Honors)|(Advanced\\sPlacement)|(AP)|(College Prep)|(Preparatory)|(Prep)|(Academic)","");
            String findName = name.replaceAll("[-\\s]|(Honors)|(Advanced\\sPlacement)|(AP)|(College Prep)|(Preparatory)|(Prep)|(Academic)", "");
            if(courseName.toLowerCase().equals(findName.toLowerCase()) && level._creditWeight == c.getLevel()._creditWeight){
                toReturn.add(c);
            }
        }
        if(toReturn.isEmpty()){
            if(level == Level.ACADEMIC) {
                return getCourses(name, courses, Level.getNext(level, Grade.C));
            }
            else{
                return null;
            }
        }

        return toReturn;
    }
    public static ArrayList<Course> getCourses(Subject subject, ArrayList<Course> courses, Level level){
        ArrayList<Course> toReturn = new ArrayList<>();
        for (Course c: courses) {
            if(c.getSubject() == subject && level._creditWeight == c.getLevel()._creditWeight){
                toReturn.add(c);
            }
        }
        if(toReturn.isEmpty()){
            return getCourses(subject, courses, Level.getNext(level, Grade.C));
        }
        return toReturn;
    }
    public static ArrayList<Course> getCourses(Subject subject, ArrayList<Course> courses){
        ArrayList<Course> toReturn = new ArrayList<>();
        for (Course c: courses) {
            if(c.getSubject() == subject ){
                toReturn.add(c);
            }
        }
        return toReturn;
    }


    public void initializePrereqs(ArrayList<Course> courses) {
        String[] prereqs = _preReqString.split(",");
        String[] prereqTos = _preReqToString.split(",");
        for (int i = 0; i < prereqs.length; i++) {
            String prereq = prereqs[i];
            if(!prereq.equals("N/A")){
                ArrayList<Course> preReqList = getCourses(prereq, courses);
                if(!preReqList.isEmpty()) {
                    _prerequisiteCourses.add(preReqList.get(0));
                }
            }
        }
        for (int i = 0; i < prereqTos.length; i++) {
            String prereqTo = prereqTos[i];
            if(!prereqTo.equals("N/A")){
                ArrayList<Course> prereqToList = getCourses(prereqTo, courses);
                for(Course c: prereqToList){
                    _prerequisiteToCourses.add(c);
                }
            }
        }
    }
    public ArrayList<Course> nextCourses(){
        if(_prerequisiteToCourses.size() == 0){
            return null;
        }
        return _prerequisiteToCourses;
    }

    @Override
    public int compareTo(Course o) {
        return this.getSubject().compareTo(o.getSubject());
    }


}
