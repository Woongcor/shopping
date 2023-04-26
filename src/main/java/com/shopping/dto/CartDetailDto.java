package com.shopping.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartDetailDto {
    private Long cartProductId;  // 장바구니 상품을 위한 아이디
    private String name;  //상품 이름
    private int price;  // 상품 금액
    private int count;  //구매하고자 하는 수량
    private String imageUrl;  // 상품 이미지 경로


    public CartDetailDto(Long cartProductId, String name, int price, int count, String imageUrl) {
        this.cartProductId = cartProductId;
        this.name = name;
        this.price = price;
        this.count = count;
        this.imageUrl = imageUrl;
    }
}
