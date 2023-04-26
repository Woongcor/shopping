package com.shopping.controller;

import com.shopping.entity.Employee;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeTestController {
//  @RequestMapping("/employee")
    @GetMapping("/employee")
    public Employee test(){
        Employee bean = new Employee();
        bean.setAge(20);
        bean.setId("hwang");
        bean.setName("황민현");
        return bean;
    }
}


// Request 요청
// Mapping 서블릿 매핑
// GetMapping <-> PostMapping