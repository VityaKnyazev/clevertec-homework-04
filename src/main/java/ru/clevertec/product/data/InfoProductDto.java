package ru.clevertec.product.data;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record InfoProductDto(

        /**
         * Идентификатор не может быть null
         */
        @NotNull
        UUID uuid,

        /**
         * Имя продукта смотрите {@link ru.clevertec.product.entity.Product}
         */
        @NotEmpty
        @Pattern(regexp = "[A-Яa-я\\s\\t]{5,10}")
        String name,

        /**
         * Описание продукта не может быть null, может быть пустой строкой
         */
        @NotNull
        String description,

        /**
         * Стоимость не может быть null или негативным
         */
        @NotNull
        @PositiveOrZero
        BigDecimal price) {
}
