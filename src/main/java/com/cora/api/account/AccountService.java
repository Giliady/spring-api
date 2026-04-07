package com.cora.api.account;

import com.cora.api.account.dto.AccountResponse;
import com.cora.api.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        var saved = accountRepository.save(new Account(request.name(), request.cpf()));
        return new AccountResponse(saved.getId(), saved.getName(), saved.getCpf());
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> list() {
        return accountRepository.findAll()
                .stream()
                .map(a -> new AccountResponse(a.getId(), a.getName(), a.getCpf()))
                .toList();
    }
}

