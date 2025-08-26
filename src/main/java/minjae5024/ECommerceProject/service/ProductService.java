package minjae5024.ECommerceProject.service;

import lombok.RequiredArgsConstructor;
import minjae5024.ECommerceProject.dto.PageResponse;
import minjae5024.ECommerceProject.dto.ProductResponse;
import minjae5024.ECommerceProject.entity.Product;
import minjae5024.ECommerceProject.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // 단건 조회
    public ProductResponse getProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .category(product.getCategory())
                        .brand(product.getBrand())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .status(product.getStatus())
                        .build())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    // 전체 조회 (페이지네이션)
    public PageResponse<ProductResponse> getProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> products = productRepository.findAll(pageable);

        List<ProductResponse> productResponses = products.getContent().stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .category(product.getCategory())
                        .brand(product.getBrand())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .status(product.getStatus())
                        .build())
                .toList();

        return PageResponse.<ProductResponse>builder()
                .content(productResponses)
                .pageNumber(products.getNumber())
                .pageSize(products.getSize())
                .totalElements(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .last(products.isLast())
                .build();
    }
}