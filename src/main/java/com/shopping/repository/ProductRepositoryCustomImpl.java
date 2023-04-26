package com.shopping.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopping.constant.ProductStatus;
import com.shopping.dto.MainProductDto;
import com.shopping.dto.ProductSearchDto;
import com.shopping.dto.QMainProductDto;
import com.shopping.entity.Product;
import com.shopping.entity.QProduct;
import com.shopping.entity.QProductImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{
    private JPAQueryFactory queryFactory;

    //Alt + Insert (생성자 생성하려고 단축키)


    public ProductRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
        //생성자를 생성해서 받고 객체를 quertFactory에 넣기 (? )
    }

    @Override   //관리자용 목록페이지
    public Page<Product> getAdminProductPage(ProductSearchDto searchDto, Pageable pageable) {
        QueryResults<Product> results = this.queryFactory
                .selectFrom(QProduct.product)
                .where(dateRange(searchDto.getSearchDateType()),  //전체기간 : dateRange 메소드
                        sellStatusCondition(searchDto.getProductStatus()),  // 화면 상태 : sellstatusCondition
                        searchByCondition(searchDto.getSearchBy(), searchDto.getSearchQuery()))  // 상품명, 검색어 조회 : searchByCondition
                .orderBy(QProduct.product.id.desc())
                .offset(pageable.getOffset())     //몇번째부터 시작하겠다
                .limit(pageable.getPageSize())   //몇개를 보여주겠다 - pagesize
                .fetchResults();


        List<Product> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainProductDto> getMainProductPage(ProductSearchDto searchDto, Pageable pageable) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
                
        
        QueryResults<MainProductDto> results = this.queryFactory
                .select(
                        new QMainProductDto(
                                product.id, 
                                product.name,
                                product.description,
                                productImage.imageUrl,
                                product.price
                        )
                )
                .from(productImage)
                .join(productImage.product, product)
                .where(productImage.repImageYesNo.eq("Y"))
                .where(likeCondition(searchDto.getSearchQuery()))
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())  //몇번째부터 시작하겠다
                .limit(pageable.getPageSize())  //몇개를 보여주겠다 - pagesize
                .fetchResults() ;


        List<MainProductDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);  //페이지 인터페이스는 객체 생성 못한다
    }

    //BooleanExpression 타입은 QueryDSL에서 사용하는 논리식을 나타내는 타입으로, 이를 활용하면 복잡한 쿼리를 간결하고 직관적으로 작성할 수 있습니다.

    private BooleanExpression likeCondition(String searchQuery) {
        // 검색 키워드가 널이 아니면 like 연산을 수행합니다.

        return StringUtils.isEmpty(searchQuery) ? null : QProduct.product.name.like("%" + searchQuery + "%") ;
    }


    // 상품명, 검색어 조회 : searchByCondition
    private BooleanExpression searchByCondition(String searchBy, String searchQuery) {
        if(StringUtils.equals("name",searchBy)){ //상품 이름(명)으로 검색
            return QProduct.product.name.like("%"+searchQuery + "%");

        }else if(StringUtils.equals("createdBy",searchBy)){  //상품 등록자로 검색
            return QProduct.product.createdBy.like("%"+searchQuery + "%");
        }


        return null;
    }

    // 화면 상태(판매상태<판매중,판매완료등> : sellstatusCondition
    private BooleanExpression sellStatusCondition(ProductStatus productStatus) {
        return productStatus == null ? null : QProduct.product.productStatus.eq(productStatus);
    }

    //전체기간 조회 : dateRange 메소드
    private BooleanExpression dateRange(String searchDateType) {
        // 특정 기간 내 조회 방식 : 1일, 1주, 1달
        LocalDateTime dateTime = LocalDateTime.now();
        if(StringUtils.equals("all",searchDateType) || searchDateType == null) { 
            return null;
        }else if(StringUtils.equals("1d",searchDateType)){
            dateTime = dateTime.minusDays(1);  // 현재시간에서 1일 뺌

        }else if(StringUtils.equals("1w",searchDateType)){
            dateTime = dateTime.minusWeeks(1);  // 현재시간에서 1주 뺌

        }else if(StringUtils.equals("1m",searchDateType)){
            dateTime = dateTime.minusMonths(1);  // 현재시간에서 1달 뺌

        }else if(StringUtils.equals("6m",searchDateType)){
            dateTime = dateTime.minusMonths(6);  // 현재시간에서 6달 뺌
        }
            
        return QProduct.product.regDate.after(dateTime);  //regDate 등록일
    }


}
