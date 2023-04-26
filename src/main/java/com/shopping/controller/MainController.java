package com.shopping.controller;

import com.shopping.dto.MainProductDto;
import com.shopping.dto.ProductSearchDto;
import com.shopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ProductService productService;


    @RequestMapping(value = "/")
    public String main(ProductSearchDto dto, Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,3);

        if(dto.getSearchQuery() == null){
            dto.setSearchQuery("");
        }  // page 2쪽을 눌렀더니 널값으로 오류뜨고 처리가 안되서 null값이어도 널값으로 두고 처리해달라는 if 구문을 추가함

        Page<MainProductDto> products = productService.getMainProductPage(dto,pageable);

        model.addAttribute("products", products);
        model.addAttribute("searchDto", dto);
        model.addAttribute("maxPage", 5);

        return "main" ;
    }
}
