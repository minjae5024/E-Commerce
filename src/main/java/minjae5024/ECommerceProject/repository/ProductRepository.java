package minjae5024.ECommerceProject.repository;

import minjae5024.ECommerceProject.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatusAndCategory(String status, String category, Pageable pageable);

    Page<Product> findByStatusAndNameContainingIgnoreCaseOrStatusAndCategoryContainingIgnoreCase(
            String s1, String nameLike, String s2, String categoryLike, Pageable pageable
    );
}