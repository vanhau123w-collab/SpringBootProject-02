package test.demo.model;

public class Response {
    private Boolean status;
    private String message;
    private Object body;

    // 1. Constructor mặc định (Bắt buộc)
    public Response() {
    }

    // 2. Constructor đầy đủ tham số (Cái bạn đang cần dùng)
    public Response(Boolean status, String message, Object body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    // 3. Getters và Setters
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}