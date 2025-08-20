package minjae5024.ECommerceProject.service;

import lombok.RequiredArgsConstructor;
import minjae5024.ECommerceProject.DTO.PageResponse;
import minjae5024.ECommerceProject.DTO.ProductResponse;
import minjae5024.ECommerceProject.entity.Product;
import minjae5024.ECommerceProject.entity.ProductStatus;
import minjae5024.ECommerceProject.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public PageResponse<ProductResponse> list(String q, String category, Pageable pageable) {
        Page<Product> page;
        String active = ProductStatus.ACTIVE.name();

        if (category != null && !category.isBlank()) {
            page = productRepository.findByStatusAndCategory(active, category, pageable);
        } else if (q != null && !q.isBlank()) {
            page = productRepository
                    .findByStatusAndNameContainingIgnoreCaseOrStatusAndCategoryContainingIgnoreCase(
                            active, q, active, q, pageable);
        } else {
            page = productRepository.findAll(
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort())
            );
        }

        var content = page.getContent().stream().map(ProductResponse::from).collect(Collectors.toList());
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    public ProductResponse get(Long id) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: id=" + id));
        return ProductResponse.from(p);
    }
}