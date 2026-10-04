package Coach_Service.service;

import Coach_Service.dto.notices.CreateNoticeRequest;
import Coach_Service.dto.notices.NoticeView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service @RequiredArgsConstructor
public class NoticeService {
    private final RestClient restClient;
    @Value("${app.student-service-url}") private String studentServiceUrl;

    public List<NoticeView> coachingNotices(String authorization) {
        NoticeView[] notices = restClient.get().uri(studentServiceUrl + "/notices")
                .header("Authorization", authorization).retrieve().body(NoticeView[].class);
        return notices == null ? List.of() : List.of(notices);
    }

    public NoticeView create(CreateNoticeRequest request, String authorization) {
        return restClient.post().uri(studentServiceUrl + "/notices")
                .header("Authorization", authorization).contentType(MediaType.APPLICATION_JSON)
                .body(request).retrieve().body(NoticeView.class);
    }

    public List<NoticeView> myNotices(String authorization) {
        NoticeView[] notices = restClient.get().uri(studentServiceUrl + "/notices/me")
                .header("Authorization", authorization).retrieve().body(NoticeView[].class);
        return notices == null ? List.of() : List.of(notices);
    }
}
