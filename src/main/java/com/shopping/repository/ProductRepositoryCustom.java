package com.shopping.repository;

import com.shopping.dto.MainProductDto;
import com.shopping.dto.ProductSearchDto;
import com.shopping.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    // 관리자만이 볼 수 있는 상품 페이지
    Page<Product> getAdminProductPage(ProductSearchDto searchDto, Pageable pageable);

    Page<MainProductDto> getMainProductPage(ProductSearchDto searchDto, Pageable pageable);
}
