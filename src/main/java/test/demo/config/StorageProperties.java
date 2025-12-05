package test.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage") // Sửa lại prefix cho đúng chuẩn Spring Boot 3
public class StorageProperties {
	private String location = "uploads"; // Mặc định lưu vào thư mục uploads
	public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}