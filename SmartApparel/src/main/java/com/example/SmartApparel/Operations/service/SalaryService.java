package com.example.SmartApparel.Operations.service;

import com.example.SmartApparel.Operations.dto.SalaryDTO;
import com.example.SmartApparel.Operations.entity.Attendance;
import com.example.SmartApparel.Operations.entity.Salary;
import com.example.SmartApparel.Operations.entity.SalaryParameter;
import com.example.SmartApparel.Operations.repo.AttendanceRepo;
import com.example.SmartApparel.Operations.repo.EmployeeRepo;
import com.example.SmartApparel.Operations.repo.SalaryParameterRepo;
import com.example.SmartApparel.Operations.repo.SalaryRepo;
import com.example.SmartApparel.Operations.util.VarList;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.List;

/**
 * Service class for managing Salary entities.
 */
@Service
@Transactional
public class SalaryService {

    @Autowired
    private SalaryRepo salaryRepo; // Repository for Salary entities
    @Autowired
    private ModelMapper modelMapper; // ModelMapper for mapping between entities and DTOs
    @Autowired
    private AttendanceRepo attendanceRepo; // Repository for Attendance entities
    @Autowired
    private EmployeeRepo employeeRepo; // Repository for Employee entities
    @Autowired
    private SalaryParameterRepo salaryParameterRepo; // Repository for SalaryParameter entities

    /**
     * Method to retrieve all salary records.
     *
     * @return List of SalaryDTOs representing all salary records.
     */
    public List<SalaryDTO> viewAllSalary() {
        List<Salary> salaryList = salaryRepo.findAll();
        return modelMapper.map(salaryList, new TypeToken<List<SalaryDTO>>() {
        }.getType());
    }

    /**
     * Method to search and return a salary record by its ID.
     *
     * @param salaryId The ID of the salary record to search for.
     * @return SalaryDTO representing the found salary record, or null if not found.
     */
    public SalaryDTO searchSalaryByID(int salaryId) {
        if (salaryRepo.existsById(salaryId)) {
            Salary salary = salaryRepo.findById(salaryId).orElse(null);
            return modelMapper.map(salary, SalaryDTO.class);
        } else {
            return null;
        }
    }

    /**
     * Method to add a new salary record.
     *
     * @param salaryDTO The SalaryDTO object representing the new salary record to be added.
     * @return A response string indicating success or duplication.
     */
    public String addNewSalary(SalaryDTO salaryDTO) {
        //getting count of salaries by employee id and yearNMonth which stored in the salaryDTO object
        int employeeSalaryCount = salaryRepo.getCalculatedSalaryCount(salaryDTO.getEmpId(), salaryDTO.getYearNMonth());
        System.out.println("#FROM addNewSalary(salaryDTO):- employeeSalaryCount: " + employeeSalaryCount);
        //checking whether exactly one row count is there
        if (employeeSalaryCount == 0) {
            System.out.println("#Final: salaryDTO(before save):" + salaryDTO);
            salaryRepo.save(modelMapper.map(salaryDTO, Salary.class));
            return VarList.RSP_SUCCESS;
        } else if (employeeSalaryCount == 1) {
            //getting salaryID of that row
            int salaryID = salaryRepo.getSalaryIdByEmpIdAndYearNMonth(salaryDTO.getEmpId(), salaryDTO.getYearNMonth());
            salaryDTO.setSalaryId(salaryID);
            System.out.println("#Final: salaryDTO(before save):" + salaryDTO);
            salaryRepo.save(modelMapper.map(salaryDTO, Salary.class));
            return VarList.RSP_SUCCESS;
        } else {
            return VarList.RSP_DUPLICATED;
        }
    }

    /**
     * Method to update a salary record.
     *
     * @param salaryDTO The SalaryDTO object representing the salary record to be updated.
     * @return A response string indicating success or failure.
     */
    public String updateSalary(SalaryDTO salaryDTO) {
        if (salaryRepo.existsById(salaryDTO.getSalaryId())) {
            salaryRepo.save(modelMapper.map(salaryDTO, Salary.class));
            return VarList.RSP_SUCCESS;
        } else {
            return VarList.RSP_NO_DATA_FOUND;
        }
    }

    /**
     * Method to delete a salary record by its ID.
     *
     * @param salaryId The ID of the salary record to be deleted.
     * @return A response string indicating success or failure.
     */
    public String deleteSalaryByID(int salaryId) {
        if (salaryRepo.existsById(salaryId)) {
            salaryRepo.deleteById(salaryId);
            System.out.println("inside delete service method");
            return VarList.RSP_SUCCESS;
        } else {
            return VarList.RSP_NO_DATA_FOUND;
        }
    }

    /**
     * Method to calculate salary for a specific employee and year-month.
     *
     * @param empId     The ID of the employee.
     * @param yearMonth The year and month for which the salary is calculated (in the format YYYY-MM).
     * @return A response string indicating success or failure.
     */
    public String calculateSalary(String empId, String yearMonth) {

        // Retrieve the attendance count for the given employee and month
//        int attCount = attendanceRepo.getAttendanceCount(yearMonth, empId);
        List<Attendance> attendanceList = attendanceRepo.getAttListByYearMonthAndEmpId(yearMonth, empId);
//        System.out.println("############in calculateSalary(" + yearMonth + "," + empId + ") function##########");
//        System.out.println("#attCount: " + attCount);
        // Retrieve the position of the employee
        String employeePosition = employeeRepo.getEmployeePosition(empId);
//        System.out.println("#employeePosition: " + employeePosition);
        // Retrieve the salary parameters based on the employee's position
        SalaryParameter salaryParameter = salaryParameterRepo.searchSalaryParamByPosition(employeePosition);
//        System.out.println("#salaryParameter: " + salaryParameter);

        SalaryDTO salaryDTO = new SalaryDTO();

        if (salaryParameter != null) {
            // Calculate basic salary for the month //float basicForMonth = (salaryParameter.getBasicSalary() / 26) * attCount;
//            BigDecimal basicForMonth = salaryParameter.getBasicSalary().divide(BigDecimal.valueOf(26), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(attCount));
            BigDecimal[] basicForMonth = calcMonthlyBasicSalaryOfEmployee(attendanceList,salaryParameter.getBasicSalary());
            // Calculate EPF contribution by the employee //float epfByEmployee = (basicForMonth * salaryParameter.getEpfByEmployee()) / 100;
            BigDecimal epfByEmployee = basicForMonth[0].multiply(salaryParameter.getEpfByEmployee()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            // Calculate EPF contribution by the company //float epfByCompany = (basicForMonth * salaryParameter.getEpfByCompany()) / 100;
            BigDecimal epfByCompany = basicForMonth[0].multiply(salaryParameter.getEpfByCompany()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            // Calculate ETF payment //float etfPayment = (basicForMonth * salaryParameter.getEtf()) / 100;
            BigDecimal etfPayment = basicForMonth[0].multiply(salaryParameter.getEtf()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            // Calculate net salary //float netSalary = basicForMonth - epfByEmployee;
            BigDecimal netSalary = basicForMonth[0].subtract(epfByEmployee).add(basicForMonth[1]).add(salaryParameter.getAllowance1()).add(salaryParameter.getAllowance2());

            // Create a SalaryDTO object with calculated values
            salaryDTO.setEmpId(empId);
            salaryDTO.setStatus("Pending");
            salaryDTO.setYearNMonth(yearMonth);
            salaryDTO.setBasic(basicForMonth[0]);
            salaryDTO.setOverTime(basicForMonth[1]);
            salaryDTO.setEpfByEmployee(epfByEmployee);//salaryDTO.setEpfByEmployee((float) (Math.ceil(epfByEmployee * 100) / 100));
            salaryDTO.setEpfByCompany(epfByCompany);//salaryDTO.setEpfByCompany((float) (Math.ceil(epfByCompany * 100) / 100));
            salaryDTO.setEtfPayment(etfPayment);//salaryDTO.setEtfPayment((float) (Math.ceil(etfPayment * 100) / 100));
            salaryDTO.setAllowance1(salaryParameter.getAllowance1());
            salaryDTO.setAllowance2(salaryParameter.getAllowance2());
            salaryDTO.setNetSalary(netSalary);//salaryDTO.setNetSalary((float) (Math.ceil(netSalary * 100) / 100));
        } else {
            salaryDTO.setEmpId(empId);
            salaryDTO.setStatus("Position not available");
            salaryDTO.setYearNMonth(yearMonth);
        }
// Add the new salary record
//        System.out.println("#salaryDTO: " + salaryDTO);
        String response = addNewSalary(salaryDTO);
//        System.out.println("#response from addNewSalary(salaryDTO): " + response);
//        System.out.println("##########################################################################");

        return response;
    }

    /**
     * Method to calculate salary for all employees for a specific year and month.
     *
     * @param yearMonth The year and month for which the salary is calculated (in the format YYYY-MM).
     * @return A response string indicating success or failure.
     */
    public String calculateSalaryForAll(String yearMonth) {
        // Retrieve all employee IDs
        List<String> employeeIdList = employeeRepo.getAllEmployeeId();

        // Displaying employee IDs (for debug purposes)
//        System.out.println("########################");
//        employeeIdList.forEach(id -> System.out.println(id));
//        System.out.println("########################");

        // Calculate salary for each employee
        int successCount = 0;
        for (String id : employeeIdList) {
            String response = calculateSalary(id, yearMonth);
            if (response.equals(VarList.RSP_SUCCESS)) {
                System.out.println("#employee: " + id + " in " + yearMonth + " successfully calculated the salary");
                successCount++;
            } else if (response.equals(VarList.RSP_DUPLICATED)) {
                System.out.println("#employee: " + id + " in " + yearMonth + " have duplicated salary");
            }
        }

        // Return success if at least one salary was successfully calculated, otherwise return duplication error
        if (successCount > 0) {
            return VarList.RSP_SUCCESS;
        } else {
            return VarList.RSP_DUPLICATED;
        }
    }

    public BigDecimal[] calcMonthlyBasicSalaryOfEmployee(List<Attendance> attendanceList, BigDecimal basicSalary) {
        //loop through attList
        for (Attendance attendance : attendanceList) {
            //verify whether outTime in attendance record is null
            if(attendance.getOutTime()==null){
                return new BigDecimal[] { BigDecimal.valueOf(0), BigDecimal.valueOf(0) };
            }
        }
        BigDecimal basicPerHour = basicSalary.divide(BigDecimal.valueOf(26),2,RoundingMode.HALF_UP).divide(BigDecimal.valueOf(8),2,RoundingMode.HALF_UP);
        BigDecimal basicForMonth = BigDecimal.valueOf(0);
        BigDecimal overTimeForMonth = BigDecimal.valueOf(0);

        for (Attendance attendance : attendanceList) {
            double workedHours;
            LocalTime inTime = attendance.getInTime().toLocalTime();
            LocalTime outTime = attendance.getOutTime().toLocalTime();

            if (inTime.isBefore(LocalTime.NOON) && outTime.isAfter(LocalTime.of(13, 0))) {
                // Deducting 1 hour of lunch break
                workedHours = attendance.getTimeDifferenceInHours() - 1;
            } else { // May be left AT or BEFORE 12 // May be present AT or AFTER 12
                workedHours = attendance.getTimeDifferenceInHours();
            }

            if(!attendance.getDayOfWeek().equals("SATURDAY") && !attendance.getDayOfWeek().equals("SUNDAY")){//Weekdays
                if (workedHours<= 8 ){
                    basicForMonth = basicForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(workedHours)));//basicForMonth+=basicPerHour*workedHours
                }else{//with over time calculation
                    basicForMonth = basicForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(8)));//basicForMonth+=basicPerHour*8
                    overTimeForMonth = overTimeForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(1.5)).multiply(BigDecimal.valueOf((workedHours-8))));//basicForMonth+=(basicPerHour*1.5)*(workedHours-8)
                }
            } else if (attendance.getDayOfWeek().equals("SATURDAY")){//SATURDAYS
                if (workedHours<=5 ){
                    basicForMonth = basicForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(workedHours)));//basicForMonth+=basicPerHour*workedHours
                }else{//with over time calculation
                    basicForMonth = basicForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(5)));//basicForMonth+=basicPerHour*5
                    overTimeForMonth = overTimeForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(1.5)).multiply(BigDecimal.valueOf((workedHours-5))));//basicForMonth+=(basicPerHour*1.5)*(workedHours-5)
                }
            }else{//SUNDAYS
                overTimeForMonth = overTimeForMonth.add(basicPerHour.multiply(BigDecimal.valueOf(1.5)).multiply(BigDecimal.valueOf((workedHours))));//basicForMonth+=(basicPerHour*1.5)*(workedHours)
            }
        }
        return new BigDecimal[] { basicForMonth, overTimeForMonth };
    }

}
