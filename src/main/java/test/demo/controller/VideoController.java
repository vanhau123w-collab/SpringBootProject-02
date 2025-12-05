package test.demo.controller;

import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import thêm cái này

import test.demo.entity.Video;
import test.demo.services.CategoryService;
import test.demo.services.VideoService;

@Controller
@RequestMapping("/admin/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("videos", videoService.findAll());
        return "video-ajax";
    }

    @GetMapping("/add")
    public String add(Model model) {
        Video video = new Video();
        video.setVideoId(UUID.randomUUID().toString().substring(0, 8)); 
        model.addAttribute("video", video);
        model.addAttribute("isEdit", false); 
        model.addAttribute("categories", categoryService.findAll()); 
        return "admin/videos/addOrEdit";
    }

    @PostMapping("/saveOrUpdate")
    public String save(Model model, @ModelAttribute("video") Video video, RedirectAttributes redirectAttributes) {
        try {
            videoService.save(video);
            redirectAttributes.addFlashAttribute("message", "Video đã được lưu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi lưu: " + e.getMessage());
        }
        return "redirect:/admin/videos";
    }

    // --- HÀM XÓA CÓ THÔNG BÁO ---
    @GetMapping("/delete/{videoId}")
    public String delete(@PathVariable("videoId") String videoId, RedirectAttributes redirectAttributes) {
        try {
            videoService.deleteById(videoId);
            // Xóa thành công thì báo câu này
            redirectAttributes.addFlashAttribute("message", "Đã xóa video có ID: " + videoId);
        } catch (Exception e) {
            e.printStackTrace(); // Vẫn in lỗi ra console để dev xem
            // Xóa thất bại thì báo câu này ra màn hình
            redirectAttributes.addFlashAttribute("message", "Không thể xóa video này! (Lý do: Đã có liên kết dữ liệu Foreign Key)");
        }
        return "redirect:/admin/videos";
    }

    // --- Hàm phòng hờ link lỗi ---
    @GetMapping({"/delete", "/delete/"})
    public String deleteWithoutId(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "Lỗi: ID video không hợp lệ!");
        return "redirect:/admin/videos";
    }

    @GetMapping("/edit/{videoId}")
    public String edit(Model model, @PathVariable("videoId") String videoId) {
        Optional<Video> optVideo = videoService.findById(videoId);
        if (optVideo.isPresent()) {
            Video video = optVideo.get();
            model.addAttribute("video", video);
            model.addAttribute("isEdit", true);
            model.addAttribute("categories", categoryService.findAll());
            return "admin/videos/addOrEdit";
        } else {
            return "redirect:/admin/videos";
        }
    }
}