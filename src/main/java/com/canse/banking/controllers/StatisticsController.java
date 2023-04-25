package com.canse.banking.controllers;

import com.canse.banking.dto.TransactionSumDetails;
import com.canse.banking.services.StatistiqueService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statisctics")
@RequiredArgsConstructor
@Tag(name = "statisctics")
public class StatisticsController {

    private final StatistiqueService service;

    @GetMapping("/sum-by-date/{user-id}")
    public ResponseEntity<List<TransactionSumDetails>> findSumTransactionsByDate(
                    @PathVariable("user-id") Integer userId ,
                    @RequestParam("start-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                    @RequestParam("end-date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return ResponseEntity.ok(service.findSumTransactionsByDate(startDate,endDate, userId));
    };

    @GetMapping("/account-balance/{user-id}")
    public ResponseEntity<BigDecimal>  getAccountBalance(@PathVariable("user-id") Integer userId){
        return ResponseEntity.ok(service.getAccountBalance(userId));
    };

    @GetMapping("/highest-transfer/{user-id}")
    public ResponseEntity<BigDecimal>  highestTransfert(@PathVariable("user-id") Integer userId){
        return ResponseEntity.ok(service.highestTransfert(userId));
    };

    @GetMapping("/highest-deposit/{user-id}")
    public ResponseEntity<BigDecimal>  highestDeposit(@PathVariable("user-id") Integer userId){
        return ResponseEntity.ok(service.highestDeposit(userId))
;    };

}
