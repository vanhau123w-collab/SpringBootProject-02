package test.demo.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import test.demo.entity.Category;
import test.demo.model.Response;
import test.demo.services.CategoryService;
import test.demo.services.IStorageService;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return new ResponseEntity<>(new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
    }

    @PostMapping(path = "/getCategory")
    public ResponseEntity<?> getCategory(@Validated @RequestParam("id") Integer id) { // Dùng Integer
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/addCategory")
    public ResponseEntity<?> addCategory(
            @Validated @RequestParam("categoryName") String categoryName,
            @Validated @RequestParam(value = "icon", required = false) MultipartFile icon) {
        
        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        if (optCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category đã tồn tại");
        } else {
            Category category = new Category();
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                // Lưu tên file vào entity
                category.setImages(storageService.getStorageFilename(icon, uuString));
                // Lưu file vật lý
                storageService.store(icon, category.getImages());
            }
            category.setCategoryName(categoryName);
            categoryService.save(category);
            return new ResponseEntity<>(new Response(true, "Thêm thành công", category), HttpStatus.OK);
        }
    }

    @PutMapping(path = "/updateCategory")
    public ResponseEntity<?> updateCategory(
            @Validated @RequestParam("categoryId") Integer categoryId, // Dùng Integer
            @Validated @RequestParam("categoryName") String categoryName,
            @Validated @RequestParam(value = "icon", required = false) MultipartFile icon) {
        
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            Category category = optCategory.get();
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                category.setImages(storageService.getStorageFilename(icon, uuString));
                storageService.store(icon, category.getImages());
            }
            category.setCategoryName(categoryName);
            categoryService.save(category);
            return new ResponseEntity<>(new Response(true, "Cập nhật thành công", category), HttpStatus.OK);
        }
    }

    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@Validated @RequestParam("categoryId") Integer categoryId) { // Dùng Integer
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            categoryService.delete(optCategory.get());
            return new ResponseEntity<>(new Response(true, "Xóa thành công", optCategory.get()), HttpStatus.OK);
        }
    }
}