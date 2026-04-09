package com.opsmonsters.hooknest.controllers.notification;

import com.opsmonsters.hooknest.constants.ResponseConstants;
import com.opsmonsters.hooknest.contracts.notification.NotificationChannelType;
import com.opsmonsters.hooknest.dto.request.notification.NotificationConfigRequest;
import com.opsmonsters.hooknest.dto.response.ResponseDto;
import com.opsmonsters.hooknest.dto.response.notification.NotificationPreferenceResponse;
import com.opsmonsters.hooknest.services.notification.NotificationPreferenceService;
import com.opsmonsters.hooknest.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/hooknest/notification")
@Tag(name = "Notification Preferences", description = "Manage notification channels and user preferences")
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;
    private final JwtUtil jwtUtil;

    public NotificationPreferenceController(NotificationPreferenceService preferenceService,
                                            JwtUtil jwtUtil) {
        this.preferenceService = preferenceService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences", description = "Retrieve current user's notification preferences")
    public ResponseEntity<ResponseDto<NotificationPreferenceResponse>> getPreferences() {
        UUID userId = getCurrentUserId();
        log.info("Getting notification preferences for user: {}", userId);

        NotificationPreferenceResponse preferences = preferenceService.getPreferences(userId);

        return ResponseEntity.ok(ResponseDto.success(preferences, ResponseConstants.SUCCESS));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update notification preferences", description = "Update notification preferences including channels, quiet hours, and priority")
    public ResponseEntity<ResponseDto<NotificationPreferenceResponse>> updatePreferences(
            @Valid @RequestBody NotificationConfigRequest request) {
        UUID userId = getCurrentUserId();
        log.info("Updating notification preferences for user: {}", userId);

        NotificationPreferenceResponse updated = preferenceService.updatePreferences(userId, request);

        return ResponseEntity.ok(ResponseDto.success(updated, ResponseConstants.SUCCESS));
    }

    @PostMapping("/config")
    @Operation(summary = "Update channel configuration", description = "Configure a specific notification channel (email, telegram, webhook)")
    public ResponseEntity<ResponseDto<Void>> updateChannelConfig(
            @Valid @RequestBody NotificationConfigRequest.ChannelConfig channelConfig) {
        UUID userId = getCurrentUserId();
        log.info("Updating channel config for user: {}, channel: {}", userId, channelConfig.channelType());

        preferenceService.updateChannelConfig(userId, channelConfig);

        return ResponseEntity.ok(ResponseDto.success(null, ResponseConstants.SUCCESS));
    }

    private UUID getCurrentUserId() {
        return UUID.fromString(jwtUtil.getCurrentUserId());
    }
}
