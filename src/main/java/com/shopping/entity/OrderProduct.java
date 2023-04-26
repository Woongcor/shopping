package com.shopping.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_products")
@Getter @Setter 
// @ToString  StackOverflow 가 발생해서 오류가 뜰 수 있다

public class OrderProduct extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  //fetch 기본값이 eager이므로 lazy 지연으로 바꿔주기
    @JoinColumn(name = "products_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY) // 지연로딩
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; //수량
    //private LocalDateTime regDate; //작성 일자  , BaseEntity를 상속받아서 같은 내용있음
    //private LocalDateTime updateDate; //수정 일자


    public static OrderProduct createOrderProduct(Product product, int count){
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setCount(count);
        orderProduct.setOrderPrice(product.getPrice());

        product.removeStock(count);

        return orderProduct;
    }

    public int getTotalPrice(){  // 해당 상품의 판매 금액
        return orderPrice * count ;
    }
    
    public void cancel(){  // 주문 취소시 재고 상품 다시 + 시키기
        this.getProduct().addStock(count);
    }

}
