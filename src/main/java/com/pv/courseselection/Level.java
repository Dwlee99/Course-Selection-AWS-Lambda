package com.pv.courseselection;
public enum Level {
    ACADEMIC(1), COLLEGE_PREP(1), HONORS(1.125), ADVANCED_PLACEMENT(1.25);
    double _creditWeight;
    Level(double creditWeight){
        _creditWeight = creditWeight;
    }
    public static Level getNext(Level level, Grade grade){
        int startPos;
        if(level == ADVANCED_PLACEMENT){
            startPos = 3;
        }
        else if(level == HONORS){
            startPos = 2;
        }
        else if(level == COLLEGE_PREP){
            startPos = 1;
        }
        else{
            startPos = 0;
        }
        int endPos;
        if(grade.getScale() >= Grade.AMINUS.getScale()){
            endPos = startPos + 1;
        }
        else if(grade.getScale() >= Grade.BMINUS.getScale()){
            endPos = startPos;
        }
        else{
            endPos = startPos - 1;
        }
        endPos = (endPos > 3) ? 3 : endPos;
        endPos = (endPos < 0) ? 0 : endPos;
        return values()[endPos];
    }
}