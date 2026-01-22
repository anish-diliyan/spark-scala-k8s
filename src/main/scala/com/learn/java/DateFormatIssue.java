package com.learn.java;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatIssue {
    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            //simpleDateFormat.parse("2026-01-21 00:00:00.000");
            simpleDateFormat.parse("20260119");
        } catch (Exception e) {
            System.out.println("Error occurred in parsing dates " + e);
        }
    }
}
