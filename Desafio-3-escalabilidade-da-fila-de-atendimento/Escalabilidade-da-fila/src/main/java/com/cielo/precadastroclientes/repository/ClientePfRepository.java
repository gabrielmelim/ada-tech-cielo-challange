package com.cielo.precadastroclientes.repository;

import com.cielo.precadastroclientes.model.ClientePf;
import com.cielo.precadastroclientes.model.ClientePj;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ClientePfRepository extends JpaRepository<ClientePf, Long> {
    boolean existsByCpf(String cnpj);
    @Transactional
    void deleteByCpf(String cnpj);
    ClientePf findByCpf(String cnpj);
}
