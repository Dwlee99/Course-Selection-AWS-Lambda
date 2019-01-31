package com.pv.courseselection;

public class YearRequirement {

    private GradeYear _gradeYear;
    private Requirement[] _requirements = Requirement.getBlankRequirements();
    public YearRequirement(GradeYear gradeYear){
        _gradeYear = gradeYear;
        Subject[] subjects = Subject.values();
        for (int i = 0; i < _requirements.length; i++) {
            _requirements[i] = new Requirement(subjects[i]);
        }
    }
    public void setRequirement(Subject subject, double credits){
        for (int i = 0; i < _requirements.length; i++) {
            Requirement r = _requirements[i];
            if(r.getSubject() == subject){
                r.setCredits(credits);
                return;
            }
        }
    }
    public Requirement getRequirement(Subject subject){
        for (int i = 0; i < _requirements.length; i++) {
            Requirement r = _requirements[i];
            if(r.getSubject() == subject){
                return r;
            }
        }
        return null;
    }
    public GradeYear getGradeYear(){
        return _gradeYear;
    }
}