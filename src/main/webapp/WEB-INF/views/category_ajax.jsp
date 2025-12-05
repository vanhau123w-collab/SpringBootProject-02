<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý Category (AJAX)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
</head>
<body>

<div class="container mt-5">
    <h2 class="text-center">Quản lý Category bằng AJAX</h2>
    
    <button type="button" class="btn btn-primary mb-3" data-bs-toggle="modal" data-bs-target="#addModal">
        Thêm mới Category
    </button>

    <table class="table table-bordered table-striped" id="categoryTable">
        <thead>
            <tr>
                <th>ID</th>
                <th>Hình ảnh</th>
                <th>Tên Category</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            </tbody>
    </table>
</div>

<div class="modal fade" id="addModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Thêm Category mới</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <form id="formAddCategory">
            <div class="mb-3">
                <label class="form-label">Tên Category</label>
                <input type="text" class="form-control" name="categoryName" id="categoryName" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Hình ảnh</label>
                <input type="file" class="form-control" name="icon" id="icon">
            </div>
        </form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
        <button type="button" class="btn btn-primary" onclick="addCategory()">Lưu lại</button>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript">
    $(document).ready(function() {
        // Load danh sách ngay khi mở trang
        loadCategories();
    });

    // 1. Hàm Load danh sách
    function loadCategories() {
        $.ajax({
            url: '/api/category', // Gọi API GET
            type: 'GET',
            success: function(response) {
                // Xóa dữ liệu cũ trong bảng
                $('#categoryTable tbody').empty();

                // Lấy list từ response (tùy cấu trúc API của bạn trả về)
                // Code bạn viết trả về: {status: true, message: "...", body: [...data...]}
                var list = response.body; 

                $.each(list, function(index, item) {
                    var imagePath = "/images/" + item.images; // Đường dẫn ảnh (nếu đã cấu hình ResourceHandler)
                    
                    var row = '<tr>' +
                        '<td>' + item.id + '</td>' +
                        '<td><img src="' + imagePath + '" width="50" height="50" /></td>' + // Hiển thị ảnh
                        '<td>' + item.categoryName + '</td>' +
                        '<td>' +
                            '<button class="btn btn-danger btn-sm" onclick="deleteCategory(' + item.id + ')">Xóa</button>' +
                        '</td>' +
                    '</tr>';
                    $('#categoryTable tbody').append(row);
                });
            },
            error: function(error) {
                console.log("Lỗi load data:", error);
            }
        });
    }

    // 2. Hàm Thêm mới (Có upload file)
    function addCategory() {
        // Sử dụng FormData để gửi file
        var formData = new FormData();
        formData.append("categoryName", $("#categoryName").val());
        formData.append("icon", $("#icon")[0].files[0]); // Lấy file từ input

        $.ajax({
            url: '/api/category/addCategory',
            type: 'POST',
            data: formData,
            contentType: false, // Bắt buộc khi upload file
            processData: false, // Bắt buộc khi upload file
            success: function(response) {
                alert("Thêm thành công!");
                $('#addModal').modal('hide'); // Ẩn modal
                loadCategories(); // Load lại bảng
                $("#formAddCategory")[0].reset(); // Reset form
            },
            error: function(xhr) {
                alert("Lỗi: " + xhr.responseText);
            }
        });
    }

    // 3. Hàm Xóa
    function deleteCategory(id) {
        if(confirm("Bạn có chắc muốn xóa ID: " + id + "?")) {
            $.ajax({
                url: '/api/category/deleteCategory?categoryId=' + id,
                type: 'DELETE',
                success: function(response) {
                    alert("Đã xóa thành công!");
                    loadCategories();
                },
                error: function(error) {
                    alert("Xóa thất bại!");
                }
            });
        }
    }
</script>

</body>
</html>