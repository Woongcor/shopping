package com.shopping.controller;

import com.shopping.dto.ProductFormDto;
import com.shopping.dto.ProductSearchDto;
import com.shopping.entity.Product;
import com.shopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductController {
    // http://localhost:8989/admin/products/new
    
    //상품 등록 페이지로 이동
    @GetMapping(value = "/admin/products/new")
    public String productForm(Model model){  //데이터를 넘겨주려면 model

        model.addAttribute("productFormDto", new ProductFormDto()) ;
        return "/product/prInsertForm" ;
    }

    private final ProductService productService ;  // 컨트롤러가 진행되려면 서비스에 저장해야함

    
    //상품등록이 되서 데이터로 저장되고 메인페이지로 움직임
    @PostMapping(value = "/admin/products/new")
    // @valid 유효성 검사-> 커멘드 객체에 하는데 커멘드 객체는 dto, 문제 생기면 bindingresult에 저장됨
    public String productNew(@Valid ProductFormDto dto, BindingResult error, Model model,
                             @RequestParam("productImageFile") List<MultipartFile> uploadedFile){  //데이터를 넘겨주려면 model
        System.out.println("여기로 오나요");
        if(error.hasErrors()){
            return "/product/prInsertForm";
        }

        if(uploadedFile.get(0).isEmpty() && dto.getId() == null){
            model.addAttribute("errorMessage", "첫 번째 이미지는 필수 입력 값입니다.");
            return "/product/prInsertForm";
        }

        try {
            productService.saveProduct(dto, uploadedFile);
        }catch (Exception err){
            err.printStackTrace();
            model.addAttribute("errorMessage", "예외가 발생했습니다.");
            return "/product/prInsertForm";
        }
        
        return "redirect:/";  //메인 페이지로 이동
    }


    @GetMapping(value = "/admin/product/{productId}")     //{} 안에 productId와 @PathVariable에서의 파라미터 () 안의 값이 동일해야함
    public String productDetail(@PathVariable("productId") Long productId, Model model){  //데이터를 넘겨주려면 model
        try{
                ProductFormDto dto = productService.getProductDetail(productId);
                model.addAttribute("productFormDto", dto);

        }catch (EntityNotFoundException err){
                model.addAttribute("errorMessage", "존재하지 않는 상품입니다.") ;
                model.addAttribute("productFormDto", new ProductFormDto()) ;
        }



            return "/product/prUpdateForm";
        }

    @PostMapping(value = "/admin/product/{productId}")
    public String productUpdate(@Valid ProductFormDto dto, BindingResult error, Model model,
                                    @RequestParam("productImageFile") List<MultipartFile> uploadedFile){

        String whenError = "/product/prUpdateForm";

        if(error.hasErrors()){
            return whenError;
        }

        if(uploadedFile.get(0).isEmpty() && dto.getId()== null){
            model.addAttribute("errorMessage", "첫 번째 이미지는 필수 입력 사항입니다.");
            return whenError;
        }

        try{
            productService.updateProduct(dto,uploadedFile);

        }catch (Exception err){
            model.addAttribute("errorMessage", "상품 수정 중에 오류가 발생하였습니다.");
            err.printStackTrace();
            return whenError;
        }

            return "redirect:/";  //메인 페이지로 이동
        }

    @GetMapping(value = {"/admin/products","/admin/products/{page}"})
    // "/admin/products/{page}" 에서 page는 옵션이야 그래서 밑에 @PathVariable("page") Optional<Integer> page 에 integer을 Optional로 기재해 줌
    public String productManage(ProductSearchDto dto, @PathVariable("page") Optional<Integer> page, Model model){

        //사용자가 page를 가져와서 존재하면 존재하는 페이지 가져오고 없으면 첫번째 페이지 가져오기 size는 3개만 보여주세요.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get(): 0,3);

        Page<Product> products = productService.getAdminProductPage(dto, pageable);

        model.addAttribute("products", products);
        model.addAttribute("searchDto", dto);  // 검색 조건 유지를 위하여
        model.addAttribute("maxPage", 5);  // 하단에 보여줄 최대 페이지 번호

        return "product/prList";
    }

    //일반 사용자가 상품을 클릭하여 상세 페이지로 이동하기
    @GetMapping(value = "/product/{productId}")
    public String productDetail(Model model, @PathVariable("productId") Long productId){
        ProductFormDto dto = productService.getProductDetail(productId);
        model.addAttribute("product", dto);

       return "product/prDetail";

    }


}
