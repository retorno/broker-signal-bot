package br.com.broker.signal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.broker.signal.model.Shopping;

public interface ShoppingRepository extends JpaRepository<Shopping, Long>{

}
