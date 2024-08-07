package io.bootify.library.envers.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DiffUtils {

    private final ObjectMapper objectMapper;

    public DiffUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String asUnifiedJsonDiff(Object left, Object right) {
        var unifiedDiffLines = asUnifiedJsonDiffList(left, right);
        return String.join("\n", unifiedDiffLines);
    }

    public List<String> asUnifiedJsonDiffList(Object left, Object right) {
        List<String> leftJson = Optional.ofNullable(left).map(this::toJsonLines).orElse(List.of());
        List<String> rightJson = Optional.ofNullable(right).map(this::toJsonLines).orElse(List.of());
        Patch<String> diff = com.github.difflib.DiffUtils.diff(leftJson, rightJson);

        return UnifiedDiffUtils.generateUnifiedDiff(
                "changes.json",
                "changes.json",
                leftJson,
                diff,
                Math.max(leftJson.size(), rightJson.size())
        );
    }

    private List<String> toJsonLines(Object value) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(value)
                    .lines()
                    .toList();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
