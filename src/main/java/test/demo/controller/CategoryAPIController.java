package test.demo.controller;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import test.demo.entity.Category;
import test.demo.model.Response; // Đảm bảo bạn đã có class này
import test.demo.services.CategoryService;
import test.demo.services.IStorageService;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    // 1. Lấy tất cả danh mục
    @GetMapping
    public ResponseEntity<?> getAllCategory() {
        return new ResponseEntity<>(new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
    }

    // 2. Lấy 1 danh mục (Bạn dùng POST để lấy dữ liệu cũng được, dù chuẩn REST thường dùng GET)
    @PostMapping(path = "/getCategory")
    public ResponseEntity<?> getCategory(@RequestParam("id") Integer id) {
        Optional<Category> category = categoryService.findById(id);
        if (category.isPresent()) {
            return new ResponseEntity<>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Response(false, "Thất bại", null), HttpStatus.NOT_FOUND);
        }
    }

    // 3. Thêm mới (Có upload ảnh)
    @PostMapping(path = "/addCategory")
    public ResponseEntity<?> addCategory(
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        
        // Kiểm tra trùng tên (Cần đảm bảo Service có hàm findByCategoryName)
        Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
        
        if (optCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, "Category đã tồn tại", null));
        } else {
            Category category = new Category();
            category.setCategoryName(categoryName); // Đảm bảo Entity có field categoryName

            // Xử lý lưu ảnh
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                
                // Giả sử hàm getStorageFilename tạo tên file mới
                category.setImages(storageService.getStorageFilename(icon, uuString)); 
                storageService.store(icon, category.getImages());
            }

            categoryService.save(category);
            return new ResponseEntity<>(new Response(true, "Thêm thành công", category), HttpStatus.OK);
        }
    }

    // 4. Cập nhật
    @PutMapping(path = "/updateCategory")
    public ResponseEntity<?> updateCategory(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        
        Optional<Category> optCategory = categoryService.findById(categoryId);
        
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            Category category = optCategory.get();
            category.setCategoryName(categoryName);

            // Chỉ cập nhật ảnh nếu người dùng có gửi ảnh mới lên
            if (icon != null && !icon.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String uuString = uuid.toString();
                category.setImages(storageService.getStorageFilename(icon, uuString));
                storageService.store(icon, category.getImages());
            }

            categoryService.save(category);
            return new ResponseEntity<>(new Response(true, "Cập nhật thành công", category), HttpStatus.OK);
        }
    }

    // 5. Xóa
    @DeleteMapping(path = "/deleteCategory")
    public ResponseEntity<?> deleteCategory(@RequestParam("categoryId") Integer categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Không tìm thấy Category", null), HttpStatus.BAD_REQUEST);
        } else {
            // Cần đảm bảo Service có hàm delete
            categoryService.delete(optCategory.get()); 
            return new ResponseEntity<>(new Response(true, "Xóa thành công", optCategory.get()), HttpStatus.OK);
        }
    }
}