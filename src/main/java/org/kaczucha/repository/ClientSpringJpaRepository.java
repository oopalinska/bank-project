package org.kaczucha.repository;

import org.kaczucha.repository.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientSpringJpaRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c where c.email = :email")
    Client findByEmail(@Param("email") String email);

    void deleteByEmail(String email);
}