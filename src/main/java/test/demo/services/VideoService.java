package test.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import test.demo.entity.Video;
import test.demo.repository.VideoRepository;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public void save(Video video) {
        videoRepository.save(video);
    }

    public void deleteById(String id) {
        videoRepository.deleteById(id);
    }

    public Optional<Video> findById(String id) {
        return videoRepository.findById(id);
    }

    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    public Page<Video> findByTitleContaining(String title, Pageable pageable) {
        return videoRepository.findByTitleContaining(title, pageable);
    }
}