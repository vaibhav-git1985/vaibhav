package com.vaibhav.ecom.productmicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import com.vaibhav.ecom.productmicroservice.dto.ProductDTO;
import com.vaibhav.ecom.productmicroservice.repository.ProductRepository;
import com.vaibhav.ecom.productmicroservice.utils.AppUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;


    public Flux<ProductDTO> getProducts(){
        return repository.findAll().map(AppUtils::entityToDto);
    }

    public Mono<ProductDTO> getProduct(String id){
        return repository.findById(id).map(AppUtils::entityToDto);
    }

    public Flux<ProductDTO> getProductInRange(double min,double max){
        return repository.findByPriceBetween(Range.closed(min,max));
    }

    public Mono<ProductDTO> saveProduct(Mono<ProductDTO> productDtoMono){
        System.out.println("service method called ...");
      return  productDtoMono.map(AppUtils::dtoToEntity)
                .flatMap(repository::insert)
                .map(AppUtils::entityToDto);
    }

    public Mono<ProductDTO> updateProduct(Mono<ProductDTO> productDtoMono,String id){
       return repository.findById(id)
                .flatMap(p->productDtoMono.map(AppUtils::dtoToEntity)
                .doOnNext(e->e.setId(id)))
                .flatMap(repository::save)
                .map(AppUtils::entityToDto);

    }

    public Mono<Void> deleteProduct(String id){
        return repository.deleteById(id);
    }
}
