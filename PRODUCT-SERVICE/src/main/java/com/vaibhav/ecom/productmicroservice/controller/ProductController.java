package com.vaibhav.ecom.productmicroservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.vaibhav.ecom.productmicroservice.dto.ProductDTO;
import com.vaibhav.ecom.productmicroservice.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping
    public Flux<ProductDTO> getProducts(){
        return service.getProducts();
    }

    @GetMapping("/{id}")
    public Mono<ProductDTO> getProduct(@PathVariable String id){
        return service.getProduct(id);
    }

    @GetMapping("/product-range")
    public Flux<ProductDTO> getProductBetweenRange(@RequestParam("min") double min, @RequestParam("max")double max){
        return service.getProductInRange(min,max);
    }

    @PostMapping
    public Mono<ProductDTO> saveProduct(@RequestBody Mono<ProductDTO> productDtoMono){
        System.out.println("controller method called ...");
        return service.saveProduct(productDtoMono);
    }

    @PutMapping("/update/{id}")
    public Mono<ProductDTO> updateProduct(@RequestBody Mono<ProductDTO> productDtoMono,@PathVariable String id){
        return service.updateProduct(productDtoMono,id);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<Void> deleteProduct(@PathVariable String id){
        return service.deleteProduct(id);
    }



}
