package app.schwarzdavid.packlist.common.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class ProblemDetailsFieldDto {
    private String error;
    private Map<String, Object> params = new HashMap<>();
}

