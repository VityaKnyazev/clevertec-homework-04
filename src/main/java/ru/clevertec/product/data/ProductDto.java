package ru.clevertec.product.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductDto(

        /**
         * {@link ru.clevertec.product.entity.Product}
         */
        @NotNull(message = "Product name must be not null")
        @Pattern(regexp = "[А-Яа-я\\s]{5,10}", message = "Product name must contains " +
                "from 5 to 10 russian symbols or spaces")
        String name,


        /**
         * {@link ru.clevertec.product.entity.Product}
         */
        @Pattern(regexp = "[А-Яа-я\\s]{10,30}", message = "Product description must contains " +
                "from 10 to 30 russian symbols or spaces")
        String description,


        /**
         * {@link ru.clevertec.product.entity.Product}
         */
        @NotNull(message = "Product price must be not null")
        @Positive(message = "Product price must be positive")
        BigDecimal price) {
}
