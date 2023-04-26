package com.shopping.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "carts")

@Getter @Setter @ToString
public class Cart extends BaseEntity{

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    @OneToOne(fetch = FetchType.LAZY) //즉시 로딩 이었는데 fetch를 추가해서 지연로딩으로 바꿈   ( b,c,d 까지 다 나온다..? 무슨말하신거지?)
    @JoinColumn(name = "id")  // member의 pk와 조인 
    private Member member;



    //회원하고 연동해주는 creatCart 메서드 생성
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);

        return cart;
    }

}
