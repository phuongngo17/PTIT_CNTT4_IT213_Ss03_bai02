1. Phân tích lỗi cốt lõi

Đoạn code ban đầu:

@GetMapping("/api/v1/ai/stream")
public Flux<String> getStreamResponse(@RequestParam String message) {
return chatModel.stream(new Prompt(message))
.map(response -> response.getResult().getOutput().getText());
}

Lỗi chính: Method trả về Flux<String> nhưng @GetMapping không khai báo produces = MediaType.TEXT_EVENT_STREAM_VALUE.

Flux chỉ thể hiện rằng dữ liệu có khả năng được phát ra theo kiểu reactive. Nó không tự động biến HTTP response thành SSE.

Để client nhận dữ liệu từng phần dưới dạng Server-Sent Events, endpoint nên khai báo:

produces = MediaType.TEXT_EVENT_STREAM_VALUE

Ngoài ra, cần chú ý việc lấy text từ từng ChatResponse: một số chunk có thể không chứa text hoặc cấu trúc response có thể khác nhau. Nếu .getText() trả null, việc stream cũng có thể gặp vấn đề.

2. Tại sao Flux chưa đủ để tạo SSE?

Có thể hiểu theo chuỗi:

LLM
│
│ sinh từng chunk
▼
chatModel.stream(...)
│
│ Flux<ChatResponse>
▼
.map(...)
│
│ Flux<String>
▼
Spring WebFlux
│
│ cần biết response là SSE
▼
HTTP Response
│
│ data từng event
▼
Client

Flux<T> là một Publisher trong Reactive Streams.