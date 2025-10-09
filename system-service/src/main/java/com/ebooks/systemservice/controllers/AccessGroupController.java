package com.ebooks.systemservice.controllers;

import com.ebooks.commonservice.dtos.AccessGroupRequestDto;
import com.ebooks.commonservice.dtos.AccessGroupResponseDto;
import com.ebooks.systemservice.services.AccessGroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/access-groups")
public class AccessGroupController {

    private static final Logger logger = LoggerFactory.getLogger(AccessGroupController.class);

    @Autowired
    private AccessGroupService accessGroupService;

    @PostMapping
    public ResponseEntity<?> createAccessGroup(@Valid @RequestBody AccessGroupRequestDto requestDTO) {
        try {
            logger.info("Received request to create access group: {}", requestDTO.getName());
            AccessGroupResponseDto responseDTO = accessGroupService.createAccessGroup(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            logger.error("Error creating access group: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAccessGroups() {
        try {
            logger.info("Received request to fetch all access groups");
            List<AccessGroupResponseDto> accessGroups = accessGroupService.getAllAccessGroups();
            return ResponseEntity.ok(accessGroups);
        } catch (Exception e) {
            logger.error("Error fetching access groups: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccessGroupById(@PathVariable Long id) {
        try {
            logger.info("Received request to fetch access group with id: {}", id);
            AccessGroupResponseDto responseDTO = accessGroupService.getAccessGroupById(id);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error fetching access group with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAccessGroupsByType(@PathVariable String type) {
        try {
            logger.info("Received request to fetch access groups by type: {}", type);
            List<AccessGroupResponseDto> accessGroups = accessGroupService.getAccessGroupsByType(type);
            return ResponseEntity.ok(accessGroups);
        } catch (Exception e) {
            logger.error("Error fetching access groups by type {}: {}", type, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccessGroup(@PathVariable Long id, @Valid @RequestBody AccessGroupRequestDto requestDTO) {
        try {
            logger.info("Received request to update access group with id: {}", id);
            AccessGroupResponseDto responseDTO = accessGroupService.updateAccessGroup(id, requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error updating access group with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccessGroup(@PathVariable Long id) {
        try {
            logger.info("Received request to delete access group with id: {}", id);
            accessGroupService.deleteAccessGroup(id);
            return ResponseEntity.ok().body(createSuccessResponse("Access group deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting access group with id {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message);
    }

    private SuccessResponse createSuccessResponse(String message) {
        return new SuccessResponse(message);
    }

    private static class ErrorResponse {
        private String error;
        private boolean success = false;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private static class SuccessResponse {
        private String message;
        private boolean success = true;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}