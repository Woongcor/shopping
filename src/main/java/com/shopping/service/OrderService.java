package com.shopping.service;

import com.shopping.dto.OrderDto;
import com.shopping.dto.OrderHistDto;
import com.shopping.dto.OrderProductDto;
import com.shopping.entity.*;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.OrderRepository;
import com.shopping.repository.ProductImageRepository;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;


    // email 정보와 주문 정보(OrderDto)를 이용하여 주문 로직을 구합니다.
    public Long order(OrderDto orderDto, String email){
        // 어떤 상품인가요 ?
        Product product = productRepository.findById(orderDto.getProductId()).orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);

        List<OrderProduct> orderProductList = new ArrayList<>();

        OrderProduct orderProduct = OrderProduct.createOrderProduct(product,orderDto.getCount());

        orderProductList.add(orderProduct);

        Order order = Order.createOrder(member,orderProductList);

        orderRepository.save(order);

        return order.getId();
    }
// public Long order 위에 내용 : 상품의 아이디로 어떤 상품인지 구별하고 멤버리파지토리에 이메일에 따라 멤버를 구분 후 주문상품리스트를 만들어서, 주문상품리스트에 주문상품을 추가함, 그리고 주문을 만드는 주문 객체를 불러와서 주문 리파지토리에 저장함, 그리고 주문에 주문번호를 불러옴 id라서 반환타입은 long이라서 long으로 처리한 것
    


    private final ProductImageRepository productImageRepository;

    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email,pageable);
        Long totalCount = orderRepository.countOrder(email);
        
        List<OrderHistDto> orderHistDtos =new ArrayList<>();
        
        for (Order order: orders){  // 주문 목록 반복
            OrderHistDto orderHistDto = new OrderHistDto(order);

            List<OrderProduct> orderProducts = order.getOrderProducts();

            for (OrderProduct bean : orderProducts){
                ProductImage productImage = productImageRepository.findByProductIdAndRepImageYesNo(bean.getProduct().getId(),"Y");

                OrderProductDto beanDto = new OrderProductDto(bean, productImage.getImageUrl());

                orderHistDto.addOrderProductDto(beanDto);

            }

            orderHistDtos.add(orderHistDto);
        }
        
        return new PageImpl<OrderHistDto>(orderHistDtos,pageable,totalCount);
    }
    

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        order.cancelOrder();
    }

    //로그인 한 사람과 이메일의 주소가 동일한지 검사합니다. (악의적인 마음을 가지고 나쁜짓을 할 수 있어서 혹시 몰라서 유효성검사)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);  //로그인 한 사람

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        
        Member savedMember = order.getMember();  // 주문한 사람

        if(StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return true;
        }else{
            return false;
        }
    }


}
