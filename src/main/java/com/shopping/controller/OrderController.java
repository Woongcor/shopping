package com.shopping.controller;

import com.shopping.dto.OrderDto;
import com.shopping.dto.OrderHistDto;
import com.shopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;


    // 이게 뭐지.?
    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult error, Principal principal){
    //Principal: 인증된 사용자를 나타내는 개체입니다
    //GPT: 요약하면 이 메서드는 주문 요청을 받고, 개체를 사용하여 주문 세부 정보의 유효성을 검사하고, HTTP 상태 코드 및 응답 헤더와 함께 클라이언트에 사용자 지정 응답을 반환합니다. 개체에는 유효성 검사 프로세스 중에 발생한 유효성 검사 오류가 포함되며 개체는 인증된 사용자를 나타냅니다.


        //p.151 Errors 인터페이스
        if(error.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = error.getFieldErrors();

            for (FieldError ferr : fieldErrors){
                sb.append(ferr.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);  //오류메세지가 sb.tostring()
        }

        String email = principal.getName();
        Long orderId = 0L;


        try{
            orderId =orderService.order(orderDto, email);
            
        }catch(Exception err){
            err.printStackTrace();
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<Long>(orderId, HttpStatus.OK);  // 지역변수는 초기화가 필요함
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of( page.isPresent() ? page.get() : 0,2);

        Page<OrderHistDto> orderHistDtoPage = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders",orderHistDtoPage);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "order/orderHist";
    }


    @PostMapping(value = "/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){
        String email = principal.getName();
        if(orderService.validateOrder(orderId,email)){
            orderService.cancelOrder(orderId);
            return new ResponseEntity<Long>(orderId, HttpStatus.OK);

        }else{
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
            //body: "주문 취소 권한이 없습니다" 이 문자열이라서 ResponseEntity의 <>가 String으로 기재해 줘야함,  다른거는 orderId가 Long이라서 괄호안이 Long!
        }

    }


}
