package com.example.SmartApparel.Operations.repo;

import com.example.SmartApparel.Operations.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// Repository interface for Employee entity, extends JpaRepository
public interface EmployeeRepo extends JpaRepository<Employee,String> {

    @Query(value = "SELECT position FROM employee WHERE emp_id=?1", nativeQuery = true)
    String getEmployeePosition(String empId);
}
