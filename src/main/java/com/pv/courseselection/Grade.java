package com.pv.courseselection;

public enum Grade {
    APLUS("A+", 4.3), A("A", 4.0), AMINUS("A-", 3.7), BPLUS("B+", 3.3), B("B", 3), BMINUS("B-", 2.7), CPLUS("C+", 2.3),
    C("C", 2), CMINUS("C-", 1.7), DPLUS("D+", 1.3), D("D", 1), DMINUS("D-", .7), X("X",0);
    private String _name;
    private double _scale;
    Grade(String name, double scale){
        _name = name;
        _scale = scale;
    }

    public String getName() {
        return _name;
    }

    public double getScale() {
        return _scale;
    }
    public static Grade getGrade(String value){
        for (Grade g: values()) {
            if(g.getName().equalsIgnoreCase(value.replaceAll(" ", ""))){
                return g;
            }
        }
        return Grade.X;
    }

}