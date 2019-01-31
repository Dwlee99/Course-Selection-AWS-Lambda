package com.pv.courseselection;

public class Requirement {
    private Subject _subject;
    private double _credits;
    private static Requirement[] _blankRequirements = initializeRequirements();

    public Requirement(Subject subject){
        _subject = subject;
        _credits = 0;
    }
    public Requirement(Subject subject, double credits){
        _subject = subject;
        _credits = credits;
    }
    public void setCredits(double credits){
        _credits = credits;
    }
    public double getCredits(){
        return _credits;
    }
    public Subject getSubject(){
        return _subject;
    }
    private static Requirement[] initializeRequirements(){
        Requirement[] r = new Requirement[Subject.subjects.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = new Requirement(Subject.subjects[i]);
        }
        return r;
    }
    public static Requirement[] getBlankRequirements(){
        return _blankRequirements;
    }
}