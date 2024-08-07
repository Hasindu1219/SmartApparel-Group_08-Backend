package com.example.SmartApparel.Operations.service;


import com.example.SmartApparel.Operations.dto.ExpenseDTO;
import com.example.SmartApparel.Operations.entity.Expense;
import com.example.SmartApparel.Operations.repo.ExpenseRepo;
import com.example.SmartApparel.Operations.util.VarList;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Service class to handle business logic related to Expense operations.
 */

@Service
@Transactional
public class ExpenseService {
    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private ModelMapper modelMapper; // Injecting the ModelMapper for object mapping


    //for add expenses -------------------------------------------------------------------------------------------------------------------------
    public String saveExpense(ExpenseDTO expenseDTO){
        if (expenseRepo.existsById(expenseDTO.getExpense_ID())){
            return VarList.RSP_DUPLICATED;
        }else {
            expenseRepo.save(modelMapper.map(expenseDTO, Expense.class));
            return VarList.RSP_SUCCESS;
        }
    }


    //for update expenses ----------------------------------------------------------------------------------------------------------------------
    public String updateExpense(ExpenseDTO expenseDTO){
        if (expenseRepo.existsById(expenseDTO.getExpense_ID())){
            expenseRepo.save(modelMapper.map(expenseDTO,Expense.class));
            return VarList.RSP_SUCCESS;
        }else{
            return VarList.RSP_NO_DATA_FOUND;
        }
    }


    //for view expenses -------------------------------------------------------------------------------------------------------------------------
    public List<ExpenseDTO> viewExpense(){
        List<Expense> expenseList = expenseRepo.findAll();
        return modelMapper.map(expenseList, new TypeToken<ArrayList<ExpenseDTO>>(){}.getType());
    }


    //for search expense using Expense_id ------------------------------------------------------------------------------------------------------
    public ExpenseDTO searchExpense(int ExpenseId){ //need to search using customer name
        if (expenseRepo.existsById(ExpenseId)){
            Expense expense = expenseRepo.findById(ExpenseId).orElse(null);
            return modelMapper.map(expense, ExpenseDTO.class);
        }else {
            return null;
        }
    }


    //for delete expense using Expense_id -----------------------------------------------------------------------------------------------------
    public String deleteExpense(int ExpenseId){
        if (expenseRepo.existsById(ExpenseId)){
            expenseRepo.deleteById(ExpenseId);
            return VarList.RSP_SUCCESS;
        }else{
            return VarList.RSP_NO_DATA_FOUND;
        }
    }


    // To get sum of the Expenses ------------------------------------------------------------------------------------------------------------
    public double getTotalExpenseSum() {
        return expenseRepo.getTotalExpenseSum();
    }


}
