package minjae5024.ECommerceProject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import minjae5024.ECommerceProject.DTO.PageResponse;
import minjae5024.ECommerceProject.DTO.ProductResponse;
import minjae5024.ECommerceProject.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Products", description = "상품 조회 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 목록", description = "검색/카테고리/페이지네이션/정렬 지원")
    @GetMapping
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] spec = sort.split(",");
        Sort s = (spec.length == 2)
                ? Sort.by(Sort.Direction.fromString(spec[1]), spec[0])
                : Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(size,100), s);
        return productService.list(q, category, pageable);
    }

    @Operation(summary = "상품 상세")
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }
}