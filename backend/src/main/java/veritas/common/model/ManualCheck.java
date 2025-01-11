package veritas.common.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Document(collection = "manual_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualCheck {
    @Id
    private String id;
    private String query;
    private LocalDateTime checkedAt;
    private CrosscheckResult crosscheck;
    private Feedback feedback;
}