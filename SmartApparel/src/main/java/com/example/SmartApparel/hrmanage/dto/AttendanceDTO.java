package com.example.SmartApparel.hrmanage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceDTO {

    // Attendance ID
    private int attendanceId;

    // Date of attendance
    private Date date;

    // Time of check-in
    private Time inTime;

    // Time of check-out
    private Time outTime;

    // Employee ID
    private String empId;
}
