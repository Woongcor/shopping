package com.shopping.repository;

import com.shopping.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    //email 멤버의 주문내역을 최신날짜것 부터 보여주세요

    @Query(" select o from Order o" +
    " where o.member.email = :email " +
    " order by o.orderDate desc ")
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    
    //email의 멤버가 몇개를 주문했는지 개수를 구해주는 쿼리문 
    @Query(" select count(o) from Order o " +
    " where o.member.email = :email ")
    Long countOrder(@Param("email") String email);
}
