# Hướng dẫn thêm Jakarta Activation

## Vấn đề
Jakarta Mail cần Jakarta Activation để hoạt động. Hiện tại project thiếu thư viện này.

## Giải pháp

### Bước 1: Tải Jakarta Activation
1. Truy cập: https://mvnrepository.com/artifact/jakarta.activation/jakarta.activation-api/2.1.2
2. Tải file JAR: **jakarta.activation-2.1.2.jar** (hoặc version 2.0.1)
3. Lưu file vào thư mục `lib/` của project

**Hoặc sử dụng Maven Central:**
- Direct download link: https://repo1.maven.org/maven2/jakarta/activation/jakarta.activation-api/2.1.2/jakarta.activation-api-2.1.2.jar

### Bước 2: Cập nhật cấu hình
Sau khi đã tải file, các file cấu hình đã được cập nhật tự động. Chỉ cần:
1. Build lại project
2. Deploy lại application

## Lưu ý
- Version 2.1.2 tương thích với Jakarta Mail 2.0.1
- File JAR phải được đặt tên đúng: `jakarta.activation-2.1.2.jar`

