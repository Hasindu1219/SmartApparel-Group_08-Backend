package com.example.SmartApparel.Operations.repo;

import com.example.SmartApparel.Operations.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for accessing and managing Expense entities.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 */
public interface ExpenseRepo extends JpaRepository<Expense,Integer> {

    //Query for get Total amount of expenses..............................................................................................
    @Query(value = "SELECT SUM(amount) FROM expense", nativeQuery = true)
    double getTotalExpenseSum();


    //Query for get expense details among date range .....................................................................................
    @Query(value = "SELECT * FROM expense WHERE date BETWEEN 'start_date' AND 'end_date' ", nativeQuery = true)
    double getExpenseByDateRange(String start_date,String end_date);

    //Query for get expense details among date range orderd by date .....................................................................................
    @Query(value = "SELECT * FROM expense WHERE date BETWEEN 'start_date' AND 'end_date' order(date)", nativeQuery = true)
    double getExpenseByDateRangeOrderd(String start_date,String end_date);

}

