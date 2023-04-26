package com.shopping.service;

import com.shopping.dto.MainProductDto;
import com.shopping.dto.ProductFormDto;
import com.shopping.dto.ProductImageDto;
import com.shopping.dto.ProductSearchDto;
import com.shopping.entity.Product;
import com.shopping.entity.ProductImage;
import com.shopping.repository.ProductImageRepository;
import com.shopping.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageService productImageService;

    // 상품 등록
    public Long saveProduct(ProductFormDto dto, List<MultipartFile> uploadedFile) throws Exception{
        Product product = dto.createProduct();
        productRepository.save(product);

        // 상품에 들어가는 각 이미지들
        // 컬렉션에 들어가는거는 size로 표시
        for (int i = 0; i <uploadedFile.size() ; i++) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);

            if(i==0){
                productImage.setRepImageYesNo("Y");
            }else{
                productImage.setRepImageYesNo("N");
            }

            productImageService.saveProductImage(productImage,uploadedFile.get(i));
        }

        return product.getId().longValue();  // product에 id가 integer로 되어있어서 long 타입으로 변경해줌
    }

    private final ProductImageRepository productImageRepository;

    //등록된 상품 정보를 읽어들입니다.
    public ProductFormDto getProductDetail(Long productId){

        // 주석은 내생각 정리
        List<ProductImage> productImageList = productImageRepository.findByProductIdOrderByIdAsc(productId);  // 엔터티로 일단 상품이미지를 불러옴
 
        List<ProductImageDto> productImageDtoList = new ArrayList<ProductImageDto>(); //dto에다가 리스트를 만들어 놓아서 가져오게끔 만들어 놓고
 
        for(ProductImage productImg : productImageList){   // for 문으로 5번 돌려서 이미지를 mapper함수를 통해 케터세터로 불러오고 위에 만들어놓은 dto 이미지리스트에다가 저장함
            ProductImageDto productImgDto = ProductImageDto.of(productImg);
            productImageDtoList.add(productImgDto);
        }

        Product product = productRepository.findById(productId)    // 상품정보에 대해 productid에 따라 정보를 불러옴
                .orElseThrow(EntityNotFoundException::new);

        ProductFormDto dto = ProductFormDto.of(product);    //mapper를 통해 게터 세터로 dto 이미지를 연결해줌

        dto.setProductImageDtoList(productImageDtoList); // productdto에  맨위에서 만들어놓은 productimagedtolist 내용을 추가해서 상품수정목록을 만들어줌

        return dto;
    }

    //상품 수정, 화면에 입력한 것으로 수정하기 위해서 dto에서 데이터를 넘겨줘야 함
    public Long updateProduct(ProductFormDto productFormDto, List<MultipartFile> uploadedFile) throws Exception {
        Product product = productRepository.findById(productFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        //화면에서 넘어 온 데이터를 Entity 에게 전달합니다.
        product.updateProduct(productFormDto);

        //5개의 이미지들에 대한 아이디 목록
        List<Long> productImageIds = productFormDto.getProductImageIds();

        for (int i = 0; i < uploadedFile.size() ; i++) {
            productImageService.updateProductImage(productImageIds.get(i), uploadedFile.get(i));

        }

        return product.getId();

    }

    public Page<Product> getAdminProductPage(ProductSearchDto dto, Pageable pageable){
        //상품 검색 조건 dto와 페이징 객체 pageable을 사용해서 페이징 객체를 구해줍니다.

        return productRepository.getAdminProductPage(dto,pageable);
    }

    public Page<MainProductDto> getMainProductPage(ProductSearchDto dto, Pageable pageable){
        return productRepository.getMainProductPage(dto,pageable);

    }

}
