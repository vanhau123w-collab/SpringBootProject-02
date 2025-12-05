package test.demo.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import test.demo.entity.Video;
import test.demo.model.Response;
import test.demo.services.IStorageService; // Service lưu file (như bên Category)
import test.demo.services.VideoService;

@RestController
@RequestMapping("/api/video")
public class VideoAPIController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private IStorageService storageService; // Cần service này để lưu ảnh

    // 1. LẤY DANH SÁCH (Giữ nguyên)
    @GetMapping
    public ResponseEntity<?> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Video> pageVideo = videoService.findByTitleContaining(keyword, pageable);
        return ResponseEntity.ok(new Response(true, "Thành công", pageVideo));
    }

    // 2. THÊM MỚI (Dùng POST + MultipartFile)
    @PostMapping("/add")
    public ResponseEntity<?> createVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {
        
        try {
            Video video = new Video();
            video.setVideoId(UUID.randomUUID().toString()); // Tự sinh ID
            video.setTitle(title);
            video.setDescription(description);

            // Xử lý lưu ảnh
            if (poster != null && !poster.isEmpty()) {
                String filename = storageService.getStorageFilename(poster, video.getVideoId());
                storageService.store(poster, filename); // Lưu file vào ổ cứng
                video.setPoster(filename); // Lưu tên file vào DB
            } else {
                video.setPoster("default.jpg"); // Ảnh mặc định nếu không chọn
            }

            videoService.save(video);
            return ResponseEntity.ok(new Response(true, "Thêm thành công", video));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(false, e.getMessage(), null));
        }
    }

    // 3. CẬP NHẬT (Dùng POST để tránh lỗi upload file với PUT)
    @PostMapping("/update")
    public ResponseEntity<?> updateVideo(
            @RequestParam("videoId") String videoId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "poster", required = false) MultipartFile poster) {
        
        Optional<Video> optVideo = videoService.findById(videoId);
        if (optVideo.isPresent()) {
            Video video = optVideo.get();
            video.setTitle(title);
            video.setDescription(description);

            // Nếu có chọn ảnh mới thì mới cập nhật ảnh
            if (poster != null && !poster.isEmpty()) {
                String filename = storageService.getStorageFilename(poster, video.getVideoId());
                storageService.store(poster, filename);
                video.setPoster(filename);
            }

            videoService.save(video);
            return ResponseEntity.ok(new Response(true, "Cập nhật thành công", video));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, "Không tìm thấy video", null));
        }
    }

    // 4. XÓA (Giữ nguyên)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteVideo(@RequestParam("id") String id) {
        if (videoService.findById(id).isPresent()) {
            videoService.deleteById(id);
            return ResponseEntity.ok(new Response(true, "Xóa thành công", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(false, "Không tìm thấy video", null));
    }
}