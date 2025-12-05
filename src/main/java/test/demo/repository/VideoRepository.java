package test.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import test.demo.entity.Video;
import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> { // <--- QUAN TRỌNG: String
    
    // Tìm kiếm theo tiêu đề (Phân trang)
    Page<Video> findByTitleContaining(String title, Pageable pageable);

    // Tìm kiếm thường (cho Service dùng nếu cần)
    List<Video> findByTitleContaining(String title);
}