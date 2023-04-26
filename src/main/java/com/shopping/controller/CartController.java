package com.shopping.controller;

import com.shopping.dto.CartDetailDto;
import com.shopping.dto.CartProductDto;
import com.shopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity addCart(@RequestBody @Valid CartProductDto cartProductDto, BindingResult error, Principal principal){
        // cartProductDto : 장바구니에 담을 상품의 정보
        if(error.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = new ArrayList<>();

            for (FieldError err : fieldErrors){
                sb.append(err.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST) ;
        }

        String email = principal.getName();
        Long cartProductId = 0L;
        try{
            cartProductId = cartService.addCart(cartProductDto,email);
            return new ResponseEntity<Long>(cartProductId,HttpStatus.OK);
        }catch (Exception err){
            err.printStackTrace();
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        String email = principal.getName();
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(email);

        model.addAttribute("cartProducts",cartDetailDtoList);

        return "cart/cartList";
    }

    @PatchMapping(value = "/cartProduct/{cartProductId}")
    public @ResponseBody ResponseEntity updateCartProduct(@PathVariable("cartProductId") Long cartProductId,int count, Principal principal){
        if(count <= 0){
            return  new ResponseEntity<String>("수량은 최소 1개 이상이어야 합니다.",HttpStatus.BAD_REQUEST) ;
        }
        String email = principal.getName(); // 카트에서 특정 상품의 수량 변경
        if(cartService.validateCartProduct(cartProductId, email) == false){ // 권한 체크
        }
        cartService.updateCartProductCount(cartProductId, count); // 장바구니 수량 변경하기
        return  new ResponseEntity<Long>(cartProductId,HttpStatus.OK) ;
    }

    @DeleteMapping(value = "/cartProduct/{cartProductId}")
    public @ResponseBody ResponseEntity deleteCartProduct(@PathVariable("cartProductId") Long cartProductId, Principal principal){
        String email = principal.getName(); // 카트에서 특정 상품 삭제
       if(cartService.validateCartProduct(cartProductId, email) == false){ // 권한 체크
           return  new ResponseEntity<String>("삭제 권한이 없습니다.",HttpStatus.FORBIDDEN) ;
       }
       cartService.deleteCartProduct(cartProductId); // 장바구니 상품 삭제하기
       return  new ResponseEntity<Long>(cartProductId,HttpStatus.OK) ;
    }


}
