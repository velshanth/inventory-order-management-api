package com.guvi.controller;

import com.guvi.dto.CreateProductRequest;
import com.guvi.model.ProductModel;
import com.guvi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductModel> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> categories,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Double lowStock,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ){
        return productService.getAll(name, categories, sort,lowStock, page, size);
    }

    @PostMapping
    public ResponseEntity<ProductModel> create(@Valid @RequestBody CreateProductRequest req){
        ProductModel created = productService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ProductModel update(@PathVariable String id, @Valid @RequestBody CreateProductRequest req){
        return productService.update(id,req);
    }

    @GetMapping("/active")
    public List<ProductModel>  getActive(){
        return productService.getActive();
    }


}
