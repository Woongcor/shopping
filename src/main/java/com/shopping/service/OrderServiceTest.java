package com.shopping.service;

import com.shopping.constant.OrderStatus;
import com.shopping.constant.ProductStatus;
import com.shopping.dto.OrderDto;
import com.shopping.entity.Member;
import com.shopping.entity.Order;
import com.shopping.entity.Product;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.OrderRepository;
import com.shopping.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@SpringBootTest
@Transactional  //테스트 끝난 후에 롤백됨
public class OrderServiceTest {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Product product = saveProduct();
        Member member = saveMember();


        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setProductId(product.getId());

        Long orderId = orderService.order(orderDto,member.getEmail());

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        //List<OrderProduct> orderProducts = order.getOrderProducts();

        int totoalPrice = orderDto.getCount()*product.getPrice();

        System.out.println("주문한 상품 총 가격 : " + totoalPrice);
        System.out.println("데이터 베이스 내 상품 가격 : " + order.getTotalPrice());

    }

    @Autowired
    ProductRepository productRepository;

    private Product saveProduct() {
        Product product = new Product();
        product.setName("남성 구두");
        product.setPrice(10000);
        product.setDescription("신기 편해요");
        product.setProductStatus(ProductStatus.SELL);
        product.setStock(100);


        return productRepository.save(product);
    }

    @Autowired
    MemberRepository memberRepository;

    private Member saveMember() {
        Member member = new Member();
        member.setEmail("gomgomi@naver.com");

        return memberRepository.save(member);
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder(){
        Product product = saveProduct();   //위에 saveProduct() 메서드 있음

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setProductId(product.getId());

        Member member = saveMember();

        Long orderId = orderService.order(orderDto, member.getEmail());  // 주문하기

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        orderService.cancelOrder(orderId);  // 주문 취소하기

        Assertions.assertEquals(OrderStatus.CANCEL, order.getOrderStatus());  //상품의 상태가 취소인지 확인하는 절차
        Assertions.assertEquals(100, product.getStock());  //재고가 111개인지 확인하는 절차
    }

}
