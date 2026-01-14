package com.theatermgnt.theatermgnt.notification.service;

import com.theatermgnt.theatermgnt.common.exception.AppException;
import com.theatermgnt.theatermgnt.common.exception.ErrorCode;
import com.theatermgnt.theatermgnt.notification.dto.request.NotificationTemplateRequest;
import com.theatermgnt.theatermgnt.notification.dto.response.NotificationTemplateResponse;
import com.theatermgnt.theatermgnt.notification.entity.NotificationTemplate;
import com.theatermgnt.theatermgnt.notification.mapper.NotificationTemplateMapper;
import com.theatermgnt.theatermgnt.notification.repository.NotificationTemplateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NotificationTemplateService {
    NotificationTemplateRepository templateRepository;
    NotificationTemplateMapper templateMapper;
    EmailTemplateFactory emailTemplateFactory;
    
    @Transactional
    public NotificationTemplateResponse createTemplate(NotificationTemplateRequest request) {
        log.info("Creating notification template with code: {}", request.getTemplateCode());
        
        // Check if template code already exists
        if (templateRepository.findByTemplateCode(request.getTemplateCode()).isPresent()) {
            throw new AppException(ErrorCode.TEMPLATE_ALREADY_EXISTS);
        }
        
        NotificationTemplate template = templateMapper.toEntity(request);
        NotificationTemplate saved = templateRepository.save(template);
        
        log.info("Template created successfully: {}", saved.getId());
        return templateMapper.toResponse(saved);
    }
    
    @Transactional
    public NotificationTemplateResponse updateTemplate(String id, NotificationTemplateRequest request) {
        log.info("Updating notification template: {}", id);
        
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEMPLATE_NOT_FOUND));
        
        // Check if new template code conflicts with another template
        templateRepository.findByTemplateCode(request.getTemplateCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new AppException(ErrorCode.TEMPLATE_ALREADY_EXISTS);
                    }
                });
        
        templateMapper.updateEntity(template, request);
        NotificationTemplate updated = templateRepository.save(template);
        
        log.info("Template updated successfully: {}", id);
        return templateMapper.toResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public NotificationTemplateResponse getTemplateById(String id) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEMPLATE_NOT_FOUND));
        return templateMapper.toResponse(template);
    }
    
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateByCode(String templateCode) {
        return templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new AppException(ErrorCode.TEMPLATE_NOT_FOUND));
    }
    
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(templateMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteTemplate(String id) {
        log.info("Deleting notification template: {}", id);
        
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEMPLATE_NOT_FOUND));
        
        templateRepository.delete(template);
        log.info("Template deleted successfully: {}", id);
    }
    
    /**
     * Render template with variables
     * Uses existing EmailTemplateFactory for HTML rendering
     */
    public String renderTemplate(String templateCode, Map<String, Object> variables) {
        log.debug("Rendering template: {} with variables", templateCode);
        
        NotificationTemplate template = getTemplateByCode(templateCode);
        
        // Use EmailTemplateFactory to render content
        // Note: EmailTemplateFactory uses EmailType enum, so we need to map templateCode to EmailType
        // For now, we'll use the contentTemplate directly with simple variable replacement
        String content = template.getContentTemplate();
        
        // Simple variable replacement for now
        // TODO: Enhance with more sophisticated template engine if needed
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                content = content.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        
        return content;
    }
    
    /**
     * Render title template with variables
     */
    public String renderTitle(String templateCode, Map<String, Object> variables) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        String title = template.getTitleTemplate();
        
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                title = title.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        
        return title;
    }
}
