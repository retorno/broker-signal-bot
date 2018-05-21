package br.com.broker.signal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.broker.signal.model.Result;

public interface ResultRepository extends JpaRepository<Result, Long>{

}
