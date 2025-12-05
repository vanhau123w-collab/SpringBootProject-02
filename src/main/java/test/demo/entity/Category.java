package test.demo.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore; // <--- 1. Thêm dòng này

@Entity
@Table(name="categories")
@NamedQuery(name="Category.findAll", query="SELECT c FROM Category c")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="categoryName", columnDefinition = "NVARCHAR(255)")
    private String categoryName;

    @Column(columnDefinition = "LONGTEXT")
    private String images;
    
    @Column(name="status")
    private int status;

    // Mapping ngược lại Video (Một Category có nhiều Video)
    // 2. Thêm @JsonIgnore để ngắt vòng lặp vô tận khi load API
    @JsonIgnore 
    @OneToMany(mappedBy = "category")
    private List<Video> videos;

    // --- Constructor Mặc định ---
    public Category() {
    }

    // --- GETTERS VÀ SETTERS THỦ CÔNG ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    @ManyToOne
    @JoinColumn(name = "username")
    private User user;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}