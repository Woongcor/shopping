package com.shopping.dto;

import com.shopping.constant.OrderStatus;
import com.shopping.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class OrderHistDto {
    private Long orderId;       //주문 아이디 
    private String orderDate;   // 주문 일자
    private OrderStatus orderStatus;  // 주문 상태 정보

    //주문 상품 리스트
    private List<OrderProductDto> orderProductDtoList = new ArrayList<>();

    public void addOrderProductDto(OrderProductDto orderProductDto){
        orderProductDtoList.add(orderProductDto);
    }

    //생성자
    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        // orderDate< 스트링이라서 get orderDate로 가져오면 localtime 이라서 string으로 format해줘야함
        String pattern = "yyyy-MM-dd HH:mm";
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern(pattern));

        this.orderStatus = order.getOrderStatus();

    }
}
