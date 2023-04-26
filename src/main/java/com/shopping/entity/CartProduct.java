package com.shopping.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart_products")

@Getter @Setter @ToString
public class CartProduct extends BaseEntity{

    @Id
    @Column(name = "cart_product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;
    
    private int count; // 구매 개수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")  // cart 기본키의 column이름을 적어줘야 함
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "products_id")
    private Product product;  //product 기본키의 컬럼이름을 적어줘야함


  public static CartProduct createCartProduct(Cart cart,Product product, int count){
      CartProduct cartProduct = new CartProduct();
      cartProduct.setCart(cart);
      cartProduct.setProduct(product);
      cartProduct.setCount(count);

      return cartProduct;
  }

public void addCount(int count){
      this.count += count;
}

public void updateCount(int count){
        this.count = count;
  }



}
