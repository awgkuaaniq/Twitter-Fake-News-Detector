package veritas.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrosscheckResult {
    private String title;
    private String content;
    private String source;
    private double probability;
}