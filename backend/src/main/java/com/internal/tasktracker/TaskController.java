package com.internal.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Validate pagination parameters
        if (page < 1) {
            return ResponseEntity.badRequest()
                    .body("page must be greater than or equal to 1");
        }

        if (pageSize < 1 || pageSize > 100) {
            return ResponseEntity.badRequest()
                    .body("pageSize must be between 1 and 100");
        }

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse and validate status filter
        String normalizedStatus = null;

        if (status != null && !status.trim().isEmpty()) {
            try {
                normalizedStatus = TaskStatus
                        .valueOf(status.trim().toUpperCase())
                        .name();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body("Invalid status. Allowed values: OPEN, IN_PROGRESS, DONE");
            }
        }

        System.out.println(
                "[TaskController] q=\"" + query
                        + "\" status=" + normalizedStatus
                        + " page=" + page
                        + " pageSize=" + pageSize
        );

        // Fetch matching tasks
        List<Task> allResults =
                taskRepository.searchTasks(searchTerm, normalizedStatus);

        // Calculate pagination safely
        long startLong = (long) (page - 1) * pageSize;

        List<Task> pageResults;

        if (startLong >= allResults.size()) {
            pageResults = Collections.emptyList();
        } else {
            int start = (int) startLong;
            int end = Math.min(start + pageSize, allResults.size());

            pageResults = allResults.subList(start, end);
        }

        // Build API response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
