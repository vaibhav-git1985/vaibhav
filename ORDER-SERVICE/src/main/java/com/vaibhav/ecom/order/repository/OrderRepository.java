package com.vaibhav.ecom.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vaibhav.ecom.order.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByUserSubOrderByCreatedAtDesc(String userSub);

	Optional<OrderEntity> findByIdAndUserSub(Long id, String userSub);
}
