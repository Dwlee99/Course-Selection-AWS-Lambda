package com.pv.courseselection;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;


import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;



public class ProxyWithStream implements RequestStreamHandler {
    JSONParser parser = new JSONParser();
    public static final String courseFile = "courseList.txt";
    public static final String schoolFile = "schoolList.txt";

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of ProxyWithStream");


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject responseJson = new JSONObject();
        String studentCourses;
        String yearsTaken;
        String grades;
        String responseCode = "200";
        String studentName;
        String studentGradeString;
        Student student = null;
        School school;

        try {
            JSONObject event = (JSONObject)parser.parse(reader);
            if (event.get("body") != null) {
                JSONObject body = (JSONObject)parser.parse((String)event.get("body"));
                boolean validBody = body.get("courses") != null;
                validBody = validBody && body.get("yearsTaken") != null;
                validBody = validBody && body.get("grades") != null;
                if (validBody){
                    studentCourses = (String)body.get("courses");
                    yearsTaken = (String)body.get("yearsTaken");
                    grades = (String)body.get("grades");
                    studentName = (String)body.get("studentName");
                    studentGradeString = (String)body.get("studentGrade");

                    ArrayList<Course> schoolCourses = getCourses(new File(courseFile));
                    logger.log("COURSES INITIALIZED");
                    school = getSchool(new File(schoolFile));
                    school.addCourse(schoolCourses);
                    school.initializePrereqs();
                    student = getStudent(studentName, studentCourses, yearsTaken, grades, studentGradeString, school);
                }

            }

            String recommendedClasses = getRecommendedClasses(student);
            JSONObject responseBody = new JSONObject();
            //responseBody.put("input", event.toJSONString());
            responseBody.put("recommendedClasses", recommendedClasses);

            JSONObject headerJson = new JSONObject();
            headerJson.put("Access-Control-Allow-Origin", "*");

            responseJson.put("isBase64Encoded", false);
            responseJson.put("statusCode", responseCode);
            responseJson.put("headers", headerJson);
            responseJson.put("body", responseBody.toString());

        } catch(ParseException pex) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", pex);
        }

        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toJSONString());
        writer.close();
    }

    private Student getStudent(String studentName, String studentCourses, String yearsTaken, String grades, String studentGradeString, School school) {
        GradeYear gradeYear;
        if(studentGradeString.equals("Freshman")){
            gradeYear = GradeYear.FRESHMAN;
        }
        else if(studentGradeString.equals("Sophomore")){
            gradeYear = GradeYear.SOPHOMORE;
        }
        else if(studentGradeString.equals("Junior")){
            gradeYear = GradeYear.JUNIOR;
        }
        else{
            gradeYear = GradeYear.SENIOR;
        }
        Student student = new Student(studentName, gradeYear, school);
        String[] coursesArray = studentCourses.replaceAll(" ", "").split(",");
        String[] yearsTakenArray = yearsTaken.replaceAll(" ", "").split(",");
        String[] gradesArray = grades.replaceAll(" ", "").split(",");
        ArrayList<CourseGrade> freshmanCourseGrades = new ArrayList<>();
        ArrayList<CourseGrade> sophomoreCourseGrades = new ArrayList<>();
        ArrayList<CourseGrade> juniorCourseGrades = new ArrayList<>();
        ArrayList<CourseGrade> seniorCourseGrades = new ArrayList<>();
        for (int i = 0; i < coursesArray.length; i++) {
            String courseName = coursesArray[i];
            String yearTaken = yearsTakenArray[i];
            String gradeInClass = gradesArray[i];
            try {
                Course course = school.getCourses(courseName).get(0);
                Grade grade = Grade.getGrade(gradeInClass);
                CourseGrade courseGrade = new CourseGrade(course, grade, student);
                if (yearTaken.equals("Freshman")) {
                    freshmanCourseGrades.add(courseGrade);
                } else if (yearTaken.equals("Sophomore")) {
                    sophomoreCourseGrades.add(courseGrade);
                } else if (yearTaken.equals("Junior")) {
                    juniorCourseGrades.add(courseGrade);
                } else {
                    seniorCourseGrades.add(courseGrade);
                }
            }
            catch(IndexOutOfBoundsException e){

            }
        }
        student.setFreshmanCourses(freshmanCourseGrades);
        student.setSophomoreCourses(sophomoreCourseGrades);
        student.setJuniorCourses(juniorCourseGrades);
        student.setSeniorCourses(seniorCourseGrades);
        return student;
    }

    private String getRecommendedClasses(Student student) {
        String recommendedClasses = "";
        if(student == null)
            return recommendedClasses;
        ArrayList<Course> courses = student.getNext();
        Collections.sort(courses);
        Subject startSubject = courses.get(0).getSubject();
        recommendedClasses = "--------" + "<br>" +
                startSubject + "<br>" +
                "--------" + "<br>";
        for(Course c: courses){
            Subject currentSubject = c.getSubject();
            if(currentSubject != startSubject){
                recommendedClasses += "--------" + "<br>" +
                        currentSubject + "<br>" +
                        "--------" + "<br>";
                startSubject = currentSubject;
            }
            recommendedClasses += c.getName() + "<br>";
        }
        recommendedClasses = recommendedClasses.replaceAll("_", " ");

        return recommendedClasses;
    }
    public static ArrayList<Course> getCourses(File courseFile) throws java.io.FileNotFoundException{
        ArrayList<Course> courses = new ArrayList<>(100);
        Scanner sc = new Scanner(courseFile);
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] args = line.split("\t");
            String courseName = args[0];
            int courseCode;
            try {
                courseCode = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                courseCode = 0;
            }
            Level courseLevel = Level.valueOf(args[2].replaceAll
                    (" ", "_").toUpperCase());
            boolean isCore = args[3].equals("Y");
            boolean hasGIEP = args[4].equals("Y");
            Subject courseSubject = Subject.valueOf(args[5].replaceAll
                    (" ", "_").toUpperCase());
            double courseCredit;
            try {
                courseCredit = Double.parseDouble(args[6]);
            } catch (NumberFormatException e) {
                courseCredit = 0;
            }
            GradeYear minGrade = GradeYear.valueOf(args[7].toUpperCase());
            GradeYear maxGrade = GradeYear.valueOf(args[8].toUpperCase());
            String preReqs = args[9].replaceAll("[\"\\s]", "");
            String preReqTo = args[10].replaceAll("[\"\\s]","");
            Course course = new Course(courseName, courseCode, courseLevel, isCore, hasGIEP,
                    courseSubject, courseCredit, minGrade, maxGrade, preReqs, preReqTo,
                    "");
            courses.add(course);
        }
        return courses;
    }
    public static School getSchool(File schoolFile) throws java.io.FileNotFoundException{
        Scanner sc = new Scanner(schoolFile);
        School toReturn;
        String line = sc.nextLine();
        String[] args = line.split("\t");
        String name = args[0];
        toReturn = new School(name);
        double freshCredit = Double.parseDouble(args[1]);
        toReturn.setRequirement(GradeYear.FRESHMAN, Subject.TOTAL, freshCredit);
        double sophCredit = Double.parseDouble(args[2]);
        toReturn.setRequirement(GradeYear.SOPHOMORE, Subject.TOTAL, sophCredit);
        double junCredit = Double.parseDouble(args[3]);
        toReturn.setRequirement(GradeYear.JUNIOR, Subject.TOTAL, junCredit);
        double senCredit = Double.parseDouble(args[4]);
        toReturn.setRequirement(GradeYear.SENIOR, Subject.TOTAL, senCredit);
        double engCredit = Double.parseDouble(args[5]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.ENGLISH, engCredit);
        double socStudiesCredit = Double.parseDouble(args[6]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.SOCIAL_STUDIES, socStudiesCredit);
        double sciCredit = Double.parseDouble(args[7]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.SCIENCE, sciCredit);
        double mathCredit = Double.parseDouble(args[8]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.MATHEMATICS, mathCredit);
        double sciMathCredit = Double.parseDouble(args[9]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.SCIENCE_AND_MATH, sciMathCredit);
        double electCredit = Double.parseDouble(args[10]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.ELECTIVES, electCredit);
        double humanitiesCredit = Double.parseDouble(args[11]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.HUMANITIES, humanitiesCredit);
        double techCredit = Double.parseDouble(args[12]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.TECHNOLOGY, techCredit);
        double healthCredit = Double.parseDouble(args[13]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.HEALTH, healthCredit);
        double physEdCredit = Double.parseDouble(args[14]);
        toReturn.setRequirement(GradeYear.GRADUATION, Subject.PHYSICAL_EDUCATION, physEdCredit);
        return toReturn;
    }
}

