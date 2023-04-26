package com.shopping.service;

import com.shopping.dto.CartDetailDto;
import com.shopping.dto.CartProductDto;
import com.shopping.entity.Cart;
import com.shopping.entity.CartProduct;
import com.shopping.entity.Member;
import com.shopping.entity.Product;
import com.shopping.repository.CartProductRepository;
import com.shopping.repository.CartRepository;
import com.shopping.repository.MemberRepository;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    // 장바구니에 상품을 담는 로직을 구현합니다
    //cartProductDto 객체 정보와 사용자의 email을 이용하여 카트에 상품을 추가합니다.
    //상품 id, 수량, 이메일 정보를 이용하여 카트 상품(CartProduct) Entity를 생성해 줍니다.
    //신규 '카트 상품'이면, 내 카트에 추가하고, 아니면 기존 수량에 값을 누적시켜 줍니다.
    public Long addCart(CartProductDto cartProductDto, String email){

        Member member = memberRepository.findByEmail(email);

        Long memberId = member.getId();
        
        Cart cart = cartRepository.findByMemberId(memberId);

        if(cart == null){  // 카트가 없으면  카트생성하기
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        Long productId = cartProductDto.getProductId();

        Product product = productRepository.findById(productId).orElseThrow(EntityNotFoundException::new);

        Long cartId = cart.getId();
        CartProduct savedCartProduct = cartProductRepository.findByCartIdAndProductId(cartId,productId);

        int count = cartProductDto.getCount();  // 담을 수량
        
        if(savedCartProduct != null){ // 카트에 이미 상품이 들어 있는 경우
            savedCartProduct.addCount(count);  // 상품 수량 누적

        }else{  // 해당 상품 처음 담는 경우
            savedCartProduct = CartProduct.createCartProduct(cart,product,count);
        }

        cartProductRepository.save(savedCartProduct);
        return savedCartProduct.getId();
    }

    @Transactional(readOnly = true)  //읽기전용 속성 (조회용)
    public List<CartDetailDto> getCartList(String email){
        
        Member member = memberRepository.findByEmail(email);
        
        Long memberId = member.getId();
        
        Cart cart = cartRepository.findByMemberId(memberId);

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
                
        if(cart==null){  // 방금 카트를 준비한 경우
            return cartDetailDtoList;   // return empty list

        }else{
            cartDetailDtoList = cartProductRepository.findCartDetailDtoList(cart.getId());
            return cartDetailDtoList;
        }
    }

    public void deleteCartProduct(Long cartProductId){
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(EntityNotFoundException::new) ;
        cartProductRepository.delete(cartProduct);

    }

    @Transactional(readOnly = true) // 이 회원이 수정/삭제 권한이 있는지 체크합니다.
    public boolean validateCartProduct(Long cartProductId, String email) {
        Member current = memberRepository.findByEmail(email) ; // 로그인한 사람

        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(EntityNotFoundException::new);
    
        Member saved = cartProduct.getCart().getMember() ; // 카트 소유자

        if(StringUtils.equals(current.getEmail(), saved.getEmail())){
            return  true ;
        }else {
            return  false ;
        }
    }
    // 이 상품(cartProductId)을 수량 count으로 변경할게요.
    public void  updateCartProductCount(Long cartProductId, int count){
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
                .orElseThrow(EntityNotFoundException::new) ;

        cartProduct.updateCount(count);
        cartProductRepository.save(cartProduct);

    }
}
