package com.pv.courseselection;

public enum GradeYear {
    FRESHMAN(9, "Freshman"), SOPHOMORE(10, "Sophomore"), JUNIOR(11, "Junior"), SENIOR(12, "Senior"), GRADUATION(0, "Graduation");
    private int _gradeNumber;
    private String _name;
    GradeYear(int gradeNumber, String name){
        _gradeNumber = gradeNumber;
        _name = name;
    }

    public int getGradeNumber() {
        return _gradeNumber;
    }

    public String getName() {
        return _name;
    }
}
