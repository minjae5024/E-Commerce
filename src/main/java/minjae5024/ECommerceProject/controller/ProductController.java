package minjae5024.ECommerceProject.controller;

import lombok.RequiredArgsConstructor;
import minjae5024.ECommerceProject.DTO.PageResponse;
import minjae5024.ECommerceProject.DTO.ProductResponse;
import minjae5024.ECommerceProject.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // 전체 조회 (페이징)
    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }
}