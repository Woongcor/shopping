package com.shopping.service;

import com.shopping.entity.ProductImage;
import com.shopping.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductImageService {
    @Value("${productImageLocation}")
    private String productImageLocation;  //상품 이미지가 업로드 되는 경로
    
    private final ProductImageRepository productImageRepository;
    private final FileService fileService;


    //상품 이미지 정보 저장
    public void saveProductImage(ProductImage productImage, MultipartFile uploadedFile)throws Exception{

        String oriImageName = uploadedFile.getOriginalFilename();
        String imageName = "";
        String imageUrl = "";

        System.out.println("productImageLocation : " + productImageLocation);

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImageName)){  //not null, (널이 아니면) 이름이 있으면 업로드 하자
            imageName = fileService.uploadFile(productImageLocation,oriImageName,uploadedFile.getBytes());  //uuid + 확장자
            imageUrl = "/images/product/" + imageName;
            System.out.println("imageUrl : " + imageUrl);
        }


        productImage.updateProductImage(oriImageName,imageName,imageUrl);
        productImageRepository.save(productImage);
    }

    public void updateProductImage(Long productImageId, MultipartFile uploadFile) throws Exception{
        if(!uploadFile.isEmpty()){  //업로드 할 이미지가 있으면
            ProductImage previousImage = productImageRepository.findById(productImageId).orElseThrow(EntityNotFoundException::new);

            if(!StringUtils.isEmpty(previousImage.getImageName())){
                fileService.deleteFile(productImageLocation +"/" + previousImage.getImageName());
            }

            String oriImageName = uploadFile.getOriginalFilename();
            String imageName = fileService.uploadFile(productImageLocation, oriImageName, uploadFile.getBytes());

            String imageUrl = "/images/product/" + imageName ;

            previousImage.updateProductImage(oriImageName, imageName, imageUrl);
        }


    }

}
