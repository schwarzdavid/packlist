package app.schwarzdavid.packlist.common.web.dto;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class ProblemDetailsDto {
    private String title;
    private Integer status;
    private @Nullable String type;
    private @Nullable String detail;
    private Map<String, @Valid ProblemDetailsFieldDto> fields = new HashMap<>();
    private Map<String, @Valid ProblemDetailsFieldDto> params = new HashMap<>();
}

