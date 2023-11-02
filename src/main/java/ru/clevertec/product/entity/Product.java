package ru.clevertec.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "product")
public class Product {

    /**
     * Идентификатор продукта (генерируется базой)
     */
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    /**
     * Название продукта (не может быть null или пустым, содержит 5-10 символов(русский или пробелы))
     */
    @Column(nullable = false, length = 10)
    private String name;

    /**
     * Описание продукта(может быть null или 10-30 символов(русский и пробелы))
     */
    @Column(nullable = false, length = 30)
    private String description;

    /**
     * Не может быть null и должен быть положительным
     */
    @Column(nullable = false, precision = 9, scale = 3)
    private BigDecimal price;

    /**
     * Время создания, не может быть null(задаётся до сохранения и не обновляется)
     */
    @Column(nullable = false)
    private LocalDateTime created;
}
