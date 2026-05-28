# Project Rules

- Backend dùng ApiResponse:
{
  "status": 200,
  "code": "SUCCESS",
  "message": "Success",
  "data": {}
}
- Frontend dùng React + Vite + Tailwind + Axios
- Không hard-code baseURL, dùng VITE_API_BASE_URL
- Lỗi hiển thị từ err.response.data.message
- AccessToken lưu trong localStorage
- RefreshToken lưu trong HttpOnly Secure Cookie
- File API đặt trong src/api
- Page đặt trong src/pages
- Component dùng PascalCase
- Không đổi cấu trúc folder nếu không được yêu cầu
- Tổng quan dự án: App làm bài Quiz online