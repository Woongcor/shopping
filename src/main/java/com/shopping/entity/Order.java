package com.shopping.entity;


import com.shopping.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter @ToString
public class Order extends BaseEntity{  // 주문 엔터티

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    
    @ManyToOne // 작성하는 엔터티 기준으로 왼쪽은 작성 엔터티 오른쪽은 조인된 엔터티
    @JoinColumn(name = "id")
    private Member member;
    
    //컬렉션이어서 list 로 나옴, 양방향을 하기 위해서
    //mappedBy = "order"의 order는 OrderProduct 클래스에 들어 있는 변수의 이름입니다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)  //영속성 전이, orphanRemoval 한개가 삭제가 되면 고아객체가 발생되게끔
    private List<OrderProduct> orderProducts = new ArrayList<OrderProduct>();

    private LocalDateTime orderDate; // 주문 일자
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태
    
   // private LocalDateTime regDate; //작성 일자
   // private LocalDateTime updateDate; // 수정 일자


    public void addOrderProduct(OrderProduct orderProduct){
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);  // 내 자신이 주문이니깐..?!( 무슨말이야)
    }


    public static Order createOrder(Member member, List<OrderProduct> orderProductList){
        Order order = new Order();
        order.setMember(member); // 이 주문과 member가 연결되어있다는 걸 알려줘야 함

        for (OrderProduct bean : orderProductList){
            order.addOrderProduct(bean);
        }

        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }


    public int getTotalPrice(){
        int totalPrice = 0 ;

        for (OrderProduct bean : orderProducts){
            totalPrice += bean.getTotalPrice();
        }

        return totalPrice;
    }

    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for (OrderProduct bean : orderProducts){
            bean.cancel();
        }

    }
}
