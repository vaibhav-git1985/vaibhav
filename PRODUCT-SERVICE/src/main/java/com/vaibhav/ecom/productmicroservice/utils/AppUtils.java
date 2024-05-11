package com.vaibhav.ecom.productmicroservice.utils;


import org.springframework.beans.BeanUtils;

import com.vaibhav.ecom.productmicroservice.dto.ProductDTO;
import com.vaibhav.ecom.productmicroservice.entity.Product;

public class AppUtils {


    public static ProductDTO entityToDto(Product product) {
    	ProductDTO productDto = new ProductDTO();
        BeanUtils.copyProperties(product, productDto);
        return productDto;
    }

    public static Product dtoToEntity(ProductDTO productDto) {
        Product product = new Product();
        BeanUtils.copyProperties(productDto, product);
        return product;
    }
}