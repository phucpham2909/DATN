package com.ht_cinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ht_cinema.model.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
	Account findByEmailAndPassword(String email, String password);

	@Query("SELECT a FROM Account a WHERE a.fullname LIKE %?1% OR a.email LIKE %?1%")
	List<Account> searchByKeyword(String keyword);

	@Query("SELECT a FROM Account a WHERE a.fullname LIKE CONCAT('%', :keyword, '%') OR a.email LIKE CONCAT('%', :keyword, '%')")
	List<Account> searchByAccount(@Param("keyword") String keyword);

	Optional<Account> findByEmail(String email);

	@Query("SELECT a FROM Account a WHERE a.fullname LIKE %?1% OR a.email LIKE %?1%")
	Page<Account> findByKeyword(String keyword, Pageable pageable);
}
