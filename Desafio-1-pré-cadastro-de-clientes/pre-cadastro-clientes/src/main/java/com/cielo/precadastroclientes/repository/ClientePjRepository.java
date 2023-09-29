package com.cielo.precadastroclientes.repository;

import com.cielo.precadastroclientes.model.ClientePj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClientePjRepository extends JpaRepository<ClientePj, Long> {
    boolean existsByCnpj(String cnpj);
    @Transactional
    void deleteByCnpj(String cnpj);
    ClientePj findByCnpj(String cnpj);
}
