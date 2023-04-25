package com.canse.banking.services.impl;

import com.canse.banking.dto.TransactionDto;
import com.canse.banking.models.Transaction;
import com.canse.banking.models.TransactionType;
import com.canse.banking.repositories.TransactionRepository;
import com.canse.banking.services.TransactionService;
import com.canse.banking.validators.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final ObjectValidator<TransactionDto> validator;


    @Override
    public Integer save(TransactionDto dto) {
        validator.validate(dto);
        Transaction transaction = TransactionDto.toEntity(dto);
        transaction.setAmount(transaction.getAmount().multiply(BigDecimal.valueOf(transactionType(transaction.getType()))));
        return repository.save(transaction).getId();
    }

    @Override
    public List<TransactionDto> findAll() {
        return repository.findAll().stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDto findById(Integer id) {
        return repository.findById(id)
                .map(TransactionDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Cette id n'existe pas : " + id));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private int transactionType(TransactionType type){
        return TransactionType.TRANSFERT == type ? -1 : 1;
    }


    public List<TransactionDto> findAllByUserId(Integer userId) {
        return repository.findAllByUserId(userId)
                .stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }
}
