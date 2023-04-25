package com.canse.banking.services.impl;

import com.canse.banking.dto.AccountDto;
import com.canse.banking.exceptions.OperationNonPermittedException;
import com.canse.banking.models.Account;
import com.canse.banking.repositories.AccountRepository;
import com.canse.banking.services.AccountService;
import com.canse.banking.validators.ObjectValidator;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final ObjectValidator<AccountDto> validator;

    @Override
    public Integer save(AccountDto dto) {

        validator.validate(dto);
        Account account = AccountDto.toEntity(dto);

        boolean userHasAlreadyAnAccount = repository.findByUserId(account.getUser().getId()).isPresent();
        if (userHasAlreadyAnAccount && account.getUser().isActive()){
            throw new OperationNonPermittedException(
                    "Selected user has already an active account",
                    "Create account",
                    "Account service",
                    "Account creation"
            );
        }

        if (dto.getId() == null){
            account.setIban(generateRandomIban());
        }
        return repository.save(account).getId();
    }

    @Override
    public List<AccountDto> findAll() {
        return repository.findAll()
                .stream()
                .map(AccountDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public AccountDto findById(Integer id) {
        return repository.findById(id)
                .map(AccountDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("No account was found whith ID : " + id));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private String generateRandomIban(){
        String iban = Iban.random(CountryCode.FR).toFormattedString();
        System.out.println("Iban Generé -> " + iban);

        boolean ibanExist = repository.findByIban(iban).isPresent();

        if (ibanExist){
            generateRandomIban();
        }

        return iban;
    }
}
