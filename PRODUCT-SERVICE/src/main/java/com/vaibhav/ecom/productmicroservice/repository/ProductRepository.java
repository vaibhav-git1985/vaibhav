package com.vaibhav.ecom.productmicroservice.repository;

import org.springframework.data.domain.Range;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.vaibhav.ecom.productmicroservice.dto.ProductDTO;
import com.vaibhav.ecom.productmicroservice.entity.Product;

import reactor.core.publisher.Flux;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product,String> {
    Flux<ProductDTO> findByPriceBetween(Range<Double> priceRange);
}
