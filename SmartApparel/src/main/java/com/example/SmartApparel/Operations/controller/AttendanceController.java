package com.example.SmartApparel.Operations.controller;

import com.example.SmartApparel.Operations.dto.AttendanceDTO;
import com.example.SmartApparel.Operations.dto.ResponseDTO;
import com.example.SmartApparel.Operations.service.AttendanceService;
import com.example.SmartApparel.Operations.util.VarList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

/**
 * Controller class for managing attendance.
 */
@RestController
@RequestMapping("/attendance")
@CrossOrigin
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ResponseDTO responseDTO;

    // Endpoint to view all attendance records
    @GetMapping("/view")
    public ResponseEntity viewAttendance(){
        try{
            // Retrieve all attendance records
            List<AttendanceDTO> attendanceDTOList=attendanceService.viewAllAttendance();

            // Check if there are no records
            if (attendanceDTOList.isEmpty()){
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("No records of Attendance");
            }
            else{
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successfully fetched all attendances");
            }
            // Set response content and return
            responseDTO.setContent(attendanceDTOList);
            return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);

        }catch (Exception ex){
            System.out.println("ERROR: "+ex.getMessage());

            // Handle errors
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(ex.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to search attendance by attendance id
    @GetMapping("/searchbyid/{attendanceId}")
    public ResponseEntity searchAttendanceById(@PathVariable int attendanceId){
        try{
            // Search for attendance records for the given attendance id
            AttendanceDTO attendanceDTO= attendanceService.searchAttendanceByID(attendanceId);

            // Check if no records found
            if(attendanceDTO==null){
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("No records of the attendance id");
            }else{
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successfully fetched the attendance details");
            }
            // Set response content and return
            responseDTO.setContent(attendanceDTO);
            return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);

        }catch (Exception ex){
            System.out.println("ERROR: "+ex.getMessage());

            // Handle errors
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(ex.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to search attendance by date
    @GetMapping("/searchbydate/{date}")
    public ResponseEntity searchAttendanceByDate(@PathVariable Date date){
        try{
            // Search for attendance records for the given date
            List<AttendanceDTO> attendanceDTOList = attendanceService.searchAttendanceByDate(date);

            // Check if no records found
            if(attendanceDTOList==null){
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("No records of the date");
            }else{
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successfully fetched the attendance details");
            }
            // Set response content and return
            responseDTO.setContent(attendanceDTOList);
            return new ResponseEntity(responseDTO, HttpStatus.ACCEPTED);

        }catch (Exception ex){
            System.out.println("ERROR: "+ex.getMessage());

            // Handle errors
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(ex.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to add new attendance record
    @PostMapping("/add")
    public ResponseEntity addAttendance(@RequestBody AttendanceDTO attendanceDTO){
        try{
            // Attempt to add new attendance record
            String response = attendanceService.addNewAttendance(attendanceDTO);

            // Check the response and set appropriate message
            if(response.equals(VarList.RSP_SUCCESS)){
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successfully added attendance");
                responseDTO.setContent(attendanceDTO);
                return new ResponseEntity(responseDTO,HttpStatus.ACCEPTED);
            }
            else{
                responseDTO.setCode(VarList.RSP_DUPLICATED);
                responseDTO.setMessage("attendance already exists for the Date and Employee ID");
                responseDTO.setContent(attendanceDTO);
                return new ResponseEntity(responseDTO,HttpStatus.CONFLICT);
            }
        }catch (Exception ex){
            System.out.println("ERROR: "+ex.getMessage());

            // Handle errors
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(ex.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to update attendance record
    @PutMapping("/update")
    public ResponseEntity updateAttendance(@RequestBody AttendanceDTO attendanceDTO){
        try{
            // Attempt to update attendance record
            String response = attendanceService.updateAttendance(attendanceDTO);

            // Check the response and set appropriate message
            if (response.equals(VarList.RSP_SUCCESS)){
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successfully updated the Attendance");
                responseDTO.setContent(attendanceDTO);
                return new ResponseEntity(responseDTO,HttpStatus.ACCEPTED);
            }
            else{
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("Not found such an Attendance details");
                responseDTO.setContent(attendanceDTO);
                return new ResponseEntity(responseDTO,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            System.out.println("ERROR: "+ex.getMessage());

            // Handle errors
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(ex.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Endpoint to delete attendance record
    @DeleteMapping("/delete/{attendanceId}")
    public ResponseEntity deleteAttendance(@PathVariable int attendanceId){
        try{
            // Attempt to delete attendance record
            String response = attendanceService.deleteAttendanceByID(attendanceId);
            if(response.equals(VarList.RSP_SUCCESS)){
                responseDTO.setCode(VarList.RSP_SUCCESS);
                responseDTO.setMessage("Successfully deleted the Attendance details");
                responseDTO.setContent(attendanceId);
                return new ResponseEntity(responseDTO,HttpStatus.ACCEPTED);
            }else{
                responseDTO.setCode(VarList.RSP_NO_DATA_FOUND);
                responseDTO.setMessage("Not found such attendance details");
                responseDTO.setContent("Attendance_ID: "+attendanceId);
                return new ResponseEntity(responseDTO,HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            System.out.println("ERROR: "+ex.getMessage());

            // Handle errors
            responseDTO.setCode(VarList.RSP_ERROR);
            responseDTO.setMessage(ex.getMessage());
            responseDTO.setContent(null);
            return new ResponseEntity(responseDTO,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
