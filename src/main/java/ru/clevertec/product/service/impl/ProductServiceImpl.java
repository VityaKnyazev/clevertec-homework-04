package ru.clevertec.product.service.impl;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.product.data.InfoProductDto;
import ru.clevertec.product.data.ProductDto;
import ru.clevertec.product.entity.Product;
import ru.clevertec.product.exception.ProductNotFoundException;
import ru.clevertec.product.mapper.ProductMapper;
import ru.clevertec.product.repository.ProductRepository;
import ru.clevertec.product.service.ProductService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    private final ProductMapper productMapperImpl;
    private final ProductRepository productRepository;

    @Override
    public InfoProductDto get(UUID uuid) {

        return productRepository.findById(uuid)
                     .map(product -> productMapperImpl.toInfoProductDto(product))
                     .filter(infoProductDto1 -> validatorFactory.getValidator()
                                                                .validate(infoProductDto1, InfoProductDto.class)
                                                                .stream()
                                                                .peek(constraint -> log.error(constraint.getMessage()))
                                                                .collect(Collectors.toSet())
                     .isEmpty())
                     .orElseThrow(() -> new ProductNotFoundException(uuid));
    }

    @Override
    public List<InfoProductDto> getAll() {

        return productRepository.findAll().stream()
                                          .map(product -> productMapperImpl.toInfoProductDto(product))
                                          .filter(infoProductDto1 -> validatorFactory.getValidator()
                                                  .validate(infoProductDto1, InfoProductDto.class)
                                                  .stream()
                                                  .peek(constraint -> log.error(constraint.getMessage()))
                                                  .collect(Collectors.toSet())
                                          .isEmpty())
                                          .toList();

    }

    @Override
    public UUID create(ProductDto productDto) {

        UUID uuid = null;

        if (productDto != null) {
            if (validatorFactory.getValidator()
                    .validate(productDto, ProductDto.class)
                    .stream()
                    .peek(constraint -> log.error(constraint.getMessage()))
                    .collect(Collectors.toSet())
                    .isEmpty()) {
                uuid = productRepository.save(productMapperImpl.toProduct(productDto)).getUuid();
            }
        }

        return uuid;
    }

    @Override
    public void update(UUID uuid, ProductDto productDto) {

        if (productDto != null && uuid != null) {
            if (validatorFactory.getValidator()
                    .validate(productDto, ProductDto.class)
                    .stream()
                    .peek(constraint -> log.error(constraint.getMessage()))
                    .collect(Collectors.toSet())
                    .isEmpty()) {

                productRepository.save(productMapperImpl.merge(Product.builder()
                        .uuid(uuid)
                        .build(), productDto));
            }
        }

    }

    @Override
    public void delete(UUID uuid) {
        productRepository.delete(uuid);
    }
}
