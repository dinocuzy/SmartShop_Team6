package service;

import model.Product;
import model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service tạo câu trả lời dựa trên dữ liệu từ database Sử dụng Gemini AI để
 * sinh câu trả lời tự nhiên
 */
public class AnswerService {

    private static final String ANSWER_PROMPT_TEMPLATE
            = "Bạn là nhân viên tư vấn bán hàng chuyên nghiệp của SmartShop.\n"
            + "Nhiệm vụ: Tư vấn sản phẩm cho khách hàng dựa trên dữ liệu được cung cấp.\n\n"
            + "Yêu cầu:\n"
            + "- Trả lời ngắn gọn, thân thiện, chuyên nghiệp, nói chuyện lưu loát dễ hiểu\n"
            + "- Chỉ đề cập đến các sản phẩm có trong dữ liệu được cung cấp\n"
            + "- Nếu không có sản phẩm phù hợp, đề xuất các sản phẩm tương tự hoặc hỏi thêm thông tin\n"
            + "- Sử dụng tiếng Việt đúng chính tả\n"
            + "- Giữ câu trả lời trong 2-3 câu\n\n"
            + "Câu hỏi của khách hàng: \"{USER_MESSAGE}\"\n\n"
            + "Dữ liệu sản phẩm:\n{DATA}\n\n"
            + "Hãy trả lời câu hỏi của khách hàng:";

    /**
     * Tạo câu trả lời dựa trên dữ liệu sản phẩm (theo hướng dẫn)
     *
     * @param user User hiện tại (có thể null)
     * @param message Câu hỏi của khách hàng
     * @param products Danh sách sản phẩm
     * @param userId ID của user (để lấy thông tin đơn hàng, giỏ hàng)
     * @return ChatResponse chứa câu trả lời và danh sách sản phẩm
     */
    public ChatResponse answer(User user, String message, List<Product> products, Integer userId) {
        ChatResponse response = new ChatResponse();

        try {
            String prompt = buildPrompt(user, message, products, userId);
            String raw = util.GeminiClient.generateText(prompt);

            if (raw == null || raw.trim().isEmpty()) {
                response.setReply("Xin loi, he thong AI dang ban. Ban thu lai sau nhe.");
                response.setSuggestions(products != null ? products : new ArrayList<>());
                return response;
            }

            // Parse response từ Gemini (có thể là JSON hoặc text thuần)
            String text = extractTextFromGeminiResponse(raw);

            response.setReply(text);
            response.setSuggestions(products != null ? products : new ArrayList<>());

        } catch (Exception e) {
            System.err.println("Error in AnswerService.answer: " + e.getMessage());
            e.printStackTrace();
            response.setReply("Xin loi, he thong AI dang ban. Ban thu lai sau nhe.");
            response.setSuggestions(products != null ? products : new ArrayList<>());
        }

        return response;
    }

    /**
     * Overload method để backward compatibility
     */
    public ChatResponse answer(User user, String message, List<Product> products) {
        Integer userId = (user != null) ? user.getUserID() : null;
        return answer(user, message, products, userId);
    }

    /**
     * Extract text từ Gemini response (có thể là JSON hoặc text thuần)
     */
    private String extractTextFromGeminiResponse(String raw) {
        try {
            // Thử parse JSON trước
            Gson gson = new Gson();
            JsonObject root = gson.fromJson(raw, JsonObject.class);

            if (root.has("candidates")) {
                JsonArray candidates = root.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    if (candidate.has("content")) {
                        JsonObject content = candidate.getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts.size() > 0) {
                                JsonObject part = parts.get(0).getAsJsonObject();
                                if (part.has("text")) {
                                    return part.get("text").getAsString().trim();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Nếu không parse được JSON, trả về raw text
        }

        // Fallback: trả về raw text
        return raw.trim();
    }

    /**
     * Build prompt theo hướng dẫn
     */
    private String buildPrompt(User user, String msg, List<Product> products, Integer userId) {
        Gson gson = new Gson();
        String json = gson.toJson(products);

        // Lấy thông tin đơn hàng và giỏ hàng của user
        String userInfo = buildUserInfo(user, userId);

        return ""
                + "Ban la nhan vien tu van ban hang cua SmartShop.\n"
                + "- Xung ho 'minh' va 'ban'.\n"
                + "- Noi chuyen ngan gon, de hieu, tu nhien nhu dang chat voi ban be.\n"
                + "- Chi duoc dung thong tin trong danh sach san pham JSON ben duoi.\n"
                + "- Khong duoc bia them san pham hoac gia.\n\n"
                + "QUY TAC CHAO HOI:\n"
                + "- KHONG chao hoi lai neu khach hang khong chao truoc.\n"
                + "- KHONG bat dau cau tra loi bang 'Chao ban', 'Hi ban', 'Xin chao' neu day khong phai tin nhan dau tien.\n"
                + "- Chi tra loi truc tiep vao cau hoi cua khach hang.\n"
                + "- Neu khach hang chao, thi chao lai mot lan, sau do khong chao nua trong cuoc tro chuyen.\n\n"
                + "QUY TAC DINH DANG TEXT:\n"
                + "- KHONG su dung markdown: khong dung **bold**, *italic*, # heading, hay bullet points voi * hoac -\n"
                + "- KHONG su dung ky tu dac biet de format: khong dung **, *, #, -, _\n"
                + "- Viet text binh thuong, tu nhien: chi dung chu thuong, chu hoa, dau cham, dau phay, dau cham hoi, dau cham than\n"
                + "- Khi liet ke san pham: dung dau gach ngang (-) hoac so (1. 2. 3.) hoac chi viet thanh doan van tu nhien\n"
                + "- Vi du DUNG: 'MacBook Pro 14 inch M3 Pro voi chip M3 Pro man hinh Liquid Retina XDR'\n"
                + "- Vi du SAI: '* **MacBook Pro 14 inch M3 Pro:** Mạnh mẽ...' hoac '**MacBook Pro**'\n\n"
                + "Thong tin khach hang:\n" + userInfo + "\n\n"
                + "Danh sach san pham (JSON):\n" + json + "\n\n"
                + "Cau hoi cua khach: " + msg + "\n\n"
                + "QUY TAC HIEN THI SAN PHAM:\n"
                + "Khi co nhieu san pham, ban chi can gioi thieu ngan gon ve san pham.\n"
                + "Nếu muốn gợi ý thì phải nói là dưới đây là một số gợi ý cho bạn hoặc những câu tương tự.\n"
                + "He thong se tu dong hien thi product cards HTML cho nguoi dung.\n"
                + "Ban khong can mo ta chi tiet ve hinh anh, gia, hay nut bam - he thong se tu dong hien thi.\n\n"
                + "Hay: \n"
                + "- Neu co san pham, gioi thieu ngan gon 1-2 cau ve san pham (3-6 san pham) BANG TEXT BINH THUONG, KHONG DUNG MARKDOWN.\n"
                + "- Neu danh sach rong, giai thich va hoi them nhu cau.\n"
                + "- Ket thuc bang 1 cau hoi mo de tiep tuc tu van (neu can).\n"
                + "- KHONG can mo ta chi tiet ve UI, HTML, hay format - chi gioi thieu san pham mot cach tu nhien.\n"
                + "- NHO: Viet nhu dang noi chuyen binh thuong, khong dung bat ky ky tu markdown nao.\n"
                + "- QUAN TRONG: Khi tra loi ve lich su mua hang, chi duoc dua vao thong tin trong 'Thong tin khach hang' ben tren. Neu co don hang hoac gio hang, hay noi dung thong tin do. Neu khong co, hay noi rang chua co.\n"
                + "- QUAN TRONG: Tra loi truc tiep, khong chao hoi lai neu khach hang khong chao.";
    }

    /**
     * Xây dựng thông tin user bao gồm đơn hàng và giỏ hàng
     */
    private String buildUserInfo(User user, Integer userId) {
        StringBuilder info = new StringBuilder();

        if (user != null) {
            info.append("- Ten: ").append(user.getFullName() != null ? user.getFullName() : "Chua co ten").append("\n");
            info.append("- Email: ").append(user.getEmail() != null ? user.getEmail() : "Chua co email").append("\n");
        } else {
            info.append("- Trang thai: Chua dang nhap\n");
        }

        // Lấy thông tin đơn hàng
        if (userId != null && userId > 0) {
            try {
                orderservice.IOrderService orderService = new orderservice.OrderService();
                java.util.List<model.Order> orders = orderService.getOrdersByUser(userId);

                if (orders != null && !orders.isEmpty()) {
                    info.append("- So don hang: ").append(orders.size()).append("\n");
                    info.append("- Don hang gan nhat: ").append(orders.get(0).getOrderStatus() != null ? orders.get(0).getOrderStatus() : "Chua co trang thai").append("\n");
                } else {
                    info.append("- So don hang: 0 (chua co don hang nao)\n");
                }
            } catch (Exception e) {
                System.err.println("Error getting orders for user " + userId + ": " + e.getMessage());
                info.append("- So don hang: Khong the kiem tra\n");
            }

            // Lấy thông tin giỏ hàng
            try {
                cartservice.ICartService cartService = new cartservice.CartService();
                java.util.List<model.CartItemDB> cartItems = cartService.getCartItemsByUser(userId);

                if (cartItems != null && !cartItems.isEmpty()) {
                    info.append("- So san pham trong gio hang: ").append(cartItems.size()).append("\n");
                } else {
                    info.append("- So san pham trong gio hang: 0 (gio hang trong)\n");
                }
            } catch (Exception e) {
                System.err.println("Error getting cart items for user " + userId + ": " + e.getMessage());
                info.append("- So san pham trong gio hang: Khong the kiem tra\n");
            }
        } else {
            info.append("- So don hang: Chua dang nhap, khong the kiem tra\n");
            info.append("- So san pham trong gio hang: Chua dang nhap, khong the kiem tra\n");
        }

        return info.toString();
    }

    /**
     * Tạo câu trả lời dựa trên dữ liệu sản phẩm (backward compatibility)
     *
     * @param userMessage Câu hỏi của khách hàng
     * @param products Danh sách sản phẩm (List<Map<String, Object>>)
     * @return Câu trả lời từ AI
     */
    public String generateAnswer(String userMessage, List<Map<String, Object>> products) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Xin chào! Tôi có thể giúp gì cho bạn?";
        }

        try {
            // Format dữ liệu sản phẩm thành text
            String dataText = formatProductsData(products);

            // Tạo prompt
            String prompt = ANSWER_PROMPT_TEMPLATE
                    .replace("{USER_MESSAGE}", userMessage)
                    .replace("{DATA}", dataText);

            // Gọi Gemini API
            String response = util.GeminiClient.generateText(prompt);

            if (response == null || response.trim().isEmpty()) {
                return generateFallbackAnswer(userMessage, products);
            }

            return response.trim();

        } catch (Exception e) {
            System.err.println("Error in AnswerService.generateAnswer: " + e.getMessage());
            e.printStackTrace();
            return generateFallbackAnswer(userMessage, products);
        }
    }

    /**
     * Format danh sách sản phẩm thành text
     */
    private String formatProductsData(List<Map<String, Object>> products) {
        if (products == null || products.isEmpty()) {
            return "Không có sản phẩm nào phù hợp.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Danh sách sản phẩm:\n");

        int count = 0;
        for (Map<String, Object> product : products) {
            if (count >= 10) { // Giới hạn 10 sản phẩm
                break;
            }

            sb.append("\n").append(count + 1).append(". ");

            if (product.containsKey("productName")) {
                sb.append("Tên: ").append(product.get("productName"));
            }

            if (product.containsKey("price")) {
                Object price = product.get("price");
                if (price != null) {
                    sb.append(", Giá: ").append(formatPrice(price));
                }
            }

            if (product.containsKey("stock")) {
                Object stock = product.get("stock");
                if (stock != null) {
                    sb.append(", Tồn kho: ").append(stock);
                }
            }

            if (product.containsKey("categoryName")) {
                sb.append(", Danh mục: ").append(product.get("categoryName"));
            }

            count++;
        }

        return sb.toString();
    }

    /**
     * Format giá tiền
     */
    private String formatPrice(Object price) {
        if (price == null) {
            return "0 VNĐ";
        }

        try {
            double priceValue = 0;
            if (price instanceof Number) {
                priceValue = ((Number) price).doubleValue();
            } else {
                priceValue = Double.parseDouble(price.toString());
            }

            return String.format("%,.0f VNĐ", priceValue);
        } catch (Exception e) {
            return price.toString() + " VNĐ";
        }
    }

    /**
     * Tạo câu trả lời fallback (không dùng AI)
     */
    private String generateFallbackAnswer(String userMessage, List<Map<String, Object>> products) {
        if (products == null || products.isEmpty()) {
            return "Xin lỗi, hiện tại chúng tôi không có sản phẩm phù hợp với yêu cầu của bạn. "
                    + "Bạn có thể thử tìm kiếm với từ khóa khác hoặc liên hệ với chúng tôi để được tư vấn thêm.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Chúng tôi có ").append(products.size()).append(" sản phẩm phù hợp:\n\n");

        int count = 0;
        for (Map<String, Object> product : products) {
            if (count >= 5) {
                break;
            }

            if (product.containsKey("productName")) {
                sb.append("• ").append(product.get("productName"));

                if (product.containsKey("price")) {
                    Object price = product.get("price");
                    if (price != null) {
                        sb.append(" - ").append(formatPrice(price));
                    }
                }

                sb.append("\n");
            }

            count++;
        }

        if (products.size() > 5) {
            sb.append("\nVà còn ").append(products.size() - 5).append(" sản phẩm khác...");
        }

        return sb.toString();
    }
}
