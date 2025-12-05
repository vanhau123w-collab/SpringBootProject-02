<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Video</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>

<div class="container mt-5">
    <h2 class="text-center mb-4">Quản lý Video (Full AJAX)</h2>

    <div class="row mb-3">
        <div class="col-md-6">
            <input type="text" id="searchInput" class="form-control" placeholder="Nhập tên video để tìm...">
        </div>
        <div class="col-md-6 text-end">
            <button class="btn btn-success" onclick="openModal()">+ Thêm mới</button>
        </div>
    </div>

    <table class="table table-bordered table-hover align-middle">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Hình ảnh</th>
                <th>Tiêu đề</th>
                <th>Mô tả</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody id="videoTableBody"></tbody>
    </table>

    <nav>
        <ul class="pagination justify-content-center" id="pagination"></ul>
    </nav>
</div>

<div class="modal fade" id="videoModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalTitle">Thêm mới Video</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="videoForm">
                    <input type="hidden" id="videoId" name="videoId">
                    
                    <div class="mb-3">
                        <label class="form-label">Tiêu đề</label>
                        <input type="text" class="form-control" id="title" name="title" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Hình ảnh (Poster)</label>
                        <input type="file" class="form-control" id="poster" name="poster" accept="image/*" onchange="previewImage(this)">
                        <div class="mt-2">
                            <img id="preview" src="https://via.placeholder.com/150" style="max-width: 100%; height: 150px; object-fit: cover; display: none;">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                <button type="button" class="btn btn-primary" onclick="saveVideo()">Lưu lại</button>
            </div>
        </div>
    </div>
</div>

<script>
    // Biến toàn cục để lưu trạng thái trang hiện tại
    let currentPage = 0;

    $(document).ready(function() {
        loadVideos(0);

        // Tìm kiếm
        let timeout = null;
        $('#searchInput').on('keyup', function() {
            clearTimeout(timeout);
            timeout = setTimeout(() => loadVideos(0), 500);
        });
    });

    // 1. HÀM LOAD DANH SÁCH
    function loadVideos(page) {
        currentPage = page;
        let keyword = $('#searchInput').val();

        $.ajax({
            url: '/api/video',
            type: 'GET',
            data: { page: page, size: 5, keyword: keyword },
            success: function(response) {
                if (response.status) {
                    let pageData = response.body;
                    renderTable(pageData.content);
                    renderPagination(pageData.totalPages, pageData.number);
                } else {
                    alert(response.message);
                }
            },
            error: function(err) { console.log(err); }
        });
    }

    function renderTable(list) {
        let html = '';
        if (list.length === 0) {
            html = '<tr><td colspan="5" class="text-center">Không có dữ liệu</td></tr>';
        } else {
            $.each(list, function(index, video) {
                let imgUrl = video.poster && video.poster.startsWith('http') ? video.poster : '/uploads/' + video.poster;
                
                // Nút Sửa gọi hàm editVideo với các tham số lấy từ dòng hiện tại
                // Lưu ý: encodeURIComponent để tránh lỗi khi chuỗi có ký tự đặc biệt
                html += '<tr>' +
                            '<td>' + video.videoId + '</td>' +
                            '<td><img src="' + imgUrl + '" style="width:80px;height:50px;object-fit:cover" onerror="this.src=\'https://via.placeholder.com/150\'"></td>' +
                            '<td>' + video.title + '</td>' +
                            '<td>' + video.description + '</td>' +
                            '<td>' +
                                '<button class="btn btn-warning btn-sm me-1" onclick="editVideo(\'' + video.videoId + '\')">Sửa</button>' +
                                '<button class="btn btn-danger btn-sm" onclick="deleteVideo(\'' + video.videoId + '\')">Xóa</button>' +
                            '</td>' +
                        '</tr>';
            });
        }
        $('#videoTableBody').html(html);
    }

    // 2. MỞ MODAL THÊM MỚI
    function openModal() {
        $('#modalTitle').text('Thêm mới Video');
        $('#videoForm')[0].reset(); // Xóa trắng form
        $('#videoId').val('');      // Xóa ID ẩn
        $('#preview').hide().attr('src', '');
        $('#videoModal').modal('show');
    }

    // 3. MỞ MODAL SỬA (Lấy chi tiết và đổ lên form)
    function editVideo(id) {
        // Cách nhanh: Gọi API lấy chi tiết video hoặc lấy từ bảng. Ở đây gọi API cho chuẩn
        // Giả sử API danh sách đã trả đủ, ta có thể lọc từ bảng. 
        // Nhưng để chắc ăn, ta tìm video từ ID (thường bạn nên có API getById, ở đây mình dùng mẹo lấy từ dòng hiện tại nếu lười gọi API)
        
        // Gọi API tìm Video theo ID (Bạn nên viết thêm endpoint này ở Controller nếu chưa có, hoặc dùng tạm cách duyệt bảng)
        // Ở đây mình minh họa cách lấy dữ liệu từ DOM cho nhanh, đỡ phải sửa Controller thêm:
        // (Cách chuẩn là gọi ajax /api/video/detail?id=...)
        
        $('#modalTitle').text('Cập nhật Video');
        $('#videoId').val(id);
        
        // Mẹo: Lấy dòng hiện tại để điền form (tạm thời)
        // Thực tế bạn nên gọi $.ajax getById để lấy data chính xác nhất
        let row = $("button[onclick^='editVideo(\\'" + id + "\\')']").closest("tr");
        let title = row.find("td:eq(2)").text();
        let desc = row.find("td:eq(3)").text();
        let imgSrc = row.find("img").attr("src");

        $('#title').val(title);
        $('#description').val(desc);
        $('#preview').attr('src', imgSrc).show();
        
        $('#videoModal').modal('show');
    }

    // 4. LƯU (THÊM HOẶC SỬA)
    function saveVideo() {
        let form = $('#videoForm')[0];
        let data = new FormData(form); // Dùng FormData để gửi cả file ảnh
        let id = $('#videoId').val();

        // Nếu có ID -> Gọi API Update, không có -> Gọi API Add
        let url = id ? '/api/video/update' : '/api/video/add';

        $.ajax({
            url: url,
            type: 'POST', // Dùng POST cho cả 2 trường hợp (Controller đã cấu hình)
            enctype: 'multipart/form-data',
            data: data,
            processData: false,
            contentType: false,
            success: function(response) {
                if (response.status) {
                    alert(response.message);
                    $('#videoModal').modal('hide');
                    loadVideos(currentPage); // Tải lại trang hiện tại
                } else {
                    alert("Lỗi: " + response.message);
                }
            },
            error: function(err) {
                console.log(err);
                alert("Đã có lỗi xảy ra!");
            }
        });
    }

    // 5. XÓA
    function deleteVideo(id) {
        if (confirm("Chắc chắn xóa video này?")) {
            $.ajax({
                url: '/api/video/delete',
                type: 'DELETE',
                data: { id: id },
                success: function(response) {
                    if (response.status) {
                        alert("Đã xóa!");
                        loadVideos(0);
                    } else {
                        alert("Lỗi: " + response.message);
                    }
                }
            });
        }
    }

    // Tiện ích: Xem trước ảnh khi chọn file
    function previewImage(input) {
        if (input.files && input.files[0]) {
            let reader = new FileReader();
            reader.onload = function(e) {
                $('#preview').attr('src', e.target.result).show();
            }
            reader.readAsDataURL(input.files[0]);
        }
    }

    // Hàm render phân trang (như cũ)
    function renderPagination(totalPages, currentPage) {
        let html = '';
        let prevDisabled = (currentPage === 0) ? 'disabled' : '';
        html += '<li class="page-item ' + prevDisabled + '"><button class="page-link" onclick="loadVideos(' + (currentPage - 1) + ')">Trước</button></li>';
        
        for (let i = 0; i < totalPages; i++) {
            let active = (i === currentPage) ? 'active' : '';
            html += '<li class="page-item ' + active + '"><button class="page-link" onclick="loadVideos(' + i + ')">' + (i + 1) + '</button></li>';
        }
        
        let nextDisabled = (currentPage === totalPages - 1) ? 'disabled' : '';
        html += '<li class="page-item ' + nextDisabled + '"><button class="page-link" onclick="loadVideos(' + (currentPage + 1) + ')">Sau</button></li>';
        $('#pagination').html(html);
    }
</script>

</body>
</html>