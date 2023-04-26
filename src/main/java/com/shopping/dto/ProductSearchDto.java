package com.shopping.dto;

import com.shopping.constant.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductSearchDto {  //화면은 html문서고 html 문서에 딸린게 dto 필드 검색 관련 만들어주는 서치 dto


    private String searchDateType;  //전체기간의 날짜타입
    private ProductStatus productStatus;  //판매상태(전체)

    private String searchBy;  // 상품명
    private String searchQuery; //검색어를 입력해주세요

}
