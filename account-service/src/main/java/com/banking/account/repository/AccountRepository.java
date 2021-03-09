package com.banking.account.repository;

import com.banking.account.model.Account;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends CassandraRepository<Account, UUID> {

    @AllowFiltering
    List<Account> findAllByCustomerId(UUID customerId);

}
