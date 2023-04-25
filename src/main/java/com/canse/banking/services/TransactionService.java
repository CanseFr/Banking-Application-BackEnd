package com.canse.banking.services;

import com.canse.banking.dto.ContactDto;
import com.canse.banking.dto.TransactionDto;
import com.canse.banking.models.Transaction;

import java.util.List;

public interface TransactionService extends AbstractService<TransactionDto>{

    List<TransactionDto> findAllByUserId(Integer userId);

}
