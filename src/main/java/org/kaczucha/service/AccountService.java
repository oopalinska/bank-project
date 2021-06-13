package org.kaczucha.service;

import lombok.RequiredArgsConstructor;
import org.kaczucha.controller.dto.AccountRequest;
import org.kaczucha.controller.dto.AccountResponse;
import org.kaczucha.repository.AccountRepository;
import org.kaczucha.repository.entity.Account;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper mapper;
    private final CurrencyService currencyService;

    public AccountResponse findById(final Long id) {
        return repository
                .findById(id)
                .map(mapper::map)
                .orElseThrow(() -> new IllegalArgumentException("Account with " + id + " not found"));
    }

    public void save(final AccountRequest accountRequest) {
        repository.save(mapper.map(accountRequest));
    }

        public void transfer(
            long fromAccountId,
            long toAccountId,
            String currency,
            double amount
    ) {
        validateAmount(amount);
        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException("fromEmail and toEmail cannot be equal!");
        }
        Account fromAccount = repository.getOne(fromAccountId);
        Account toAccount = repository.getOne(toAccountId);

        double convertedAmount = convertAmountIfNeeded(fromAccount, toAccount, currency, amount);

        if (fromAccount.getBalance() - amount >= 0) {
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            toAccount.setBalance(toAccount.getBalance() + convertedAmount);
        } else {
            throw new NoSufficientFundsException("Not enough funds!");
        }
        repository.save(fromAccount);
        repository.save(toAccount);
    }
    public void withdraw(
            final long fromAccountId,
            final double amount
    ) {
        validateAmount(amount);
        final Account account = repository.getOne(fromAccountId);
        if (amount > account.getBalance()) {
            throw new NoSufficientFundsException("Amount cannot be greater than balance!");
        }
        final double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        repository.save(account);
    }
        private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive!");
        }
    }
        private double convertAmountIfNeeded(Account fromAccount, Account toAccount, String currency, double amount) {
            final double fromAmount = currencyService.calculateExchangeRate(fromAccount.getCurrency(), currency) * amount;
            final double toAmount = currencyService.calculateExchangeRate(currency, toAccount.getCurrency()) * amount;
            return 0;
        }
}
