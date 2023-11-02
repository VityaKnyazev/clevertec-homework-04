package ru.clevertec.product.data;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(

        /**
         * {@link ru.clevertec.product.entity.Product}
         */
        String name,


        /**
         * {@link ru.clevertec.product.entity.Product}
         */
        String description,


        /**
         * {@link ru.clevertec.product.entity.Product}
         */
        BigDecimal price) {
}
