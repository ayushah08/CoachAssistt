package Coach_Service.service;

import Coach_Service.dto.Login.CoachingLoginRequest;
import Coach_Service.dto.parents.Request;
import Coach_Service.dto.parents.Response;
import Coach_Service.dto.parents.ParentRecipientView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {
    private final RestClient restClient;
    @Value("${app.parent-service-url}")
    private String parentServiceUrl;

    public Response register(Request request, String authorization) {
        return restClient.post().uri(parentServiceUrl + "/parent/register").body(request)
                .header("Authorization", authorization)
                .contentType(MediaType.APPLICATION_JSON).retrieve().body(Response.class);
    }

    public Response login(CoachingLoginRequest request) {
        return restClient.post().uri(parentServiceUrl + "/parent/login").body(request)
                .contentType(MediaType.APPLICATION_JSON).retrieve().body(Response.class);
    }

    public List<ParentRecipientView> recipients(Long studentId, String authorization) {
        ParentRecipientView[] rows = restClient.get()
                .uri(parentServiceUrl + "/parent/students/{studentId}/recipients", studentId)
                .header("Authorization", authorization).retrieve().body(ParentRecipientView[].class);
        return rows == null ? List.of() : List.of(rows);
    }
}
