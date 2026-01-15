// package com.theatermgnt.theatermgnt.ticket.service;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;
//
// import java.util.Map;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// @ExtendWith(MockitoExtension.class)
// class QrGeneratorTest {
//
//    @Mock
//    private ObjectMapper objectMapper;
//
//    @InjectMocks
//    private QrGenerator qrGenerator;
//
//    private String ticketCode;
//
//    @BeforeEach
//    void setUp() {
//        ticketCode = "TK-12345678";
//    }
//
//    // ================= GENERATE QR CONTENT TESTS =================
//
//    @Test
//    void generateQrContent_success() throws Exception {
//        // Given
//        String expectedJson = "{\"type\":\"TICKET\",\"ticketCode\":\"TK-12345678\"}";
//        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn(expectedJson);
//
//        // When
//        String result = qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(expectedJson, result);
//    }
//
//    @Test
//    void generateQrContent_createCorrectPayload() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertEquals("TICKET", payload.get("type"));
//            assertEquals(ticketCode, payload.get("ticketCode"));
//            return "{\"type\":\"TICKET\",\"ticketCode\":\"TK-12345678\"}";
//        });
//
//        // When
//        String result = qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        assertNotNull(result);
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_returnsJsonString() throws Exception {
//        // Given
//        String expectedJson = "{\"type\":\"TICKET\",\"ticketCode\":\"TK-ABC12345\"}";
//        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn(expectedJson);
//
//        // When
//        String result = qrGenerator.generateQrContent("TK-ABC12345");
//
//        // Then
//        assertTrue(result.contains("\"type\""));
//        assertTrue(result.contains("\"ticketCode\""));
//        assertTrue(result.contains("TICKET"));
//    }
//
//    @Test
//    void generateQrContent_includesToTypeField() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertTrue(payload.containsKey("type"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_includesTicketCodeField() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertTrue(payload.containsKey("ticketCode"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_typeIsAlwaysTicket() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertEquals("TICKET", payload.get("type"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent("ANY_CODE");
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_preservesTicketCode() throws Exception {
//        // Given
//        String customCode = "TK-CUSTOM123";
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertEquals(customCode, payload.get("ticketCode"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(customCode);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_throwsRuntimeException_whenObjectMapperFails() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenThrow(new Exception("Serialization error"));
//
//        // When & Then
//        assertThrows(RuntimeException.class, () -> qrGenerator.generateQrContent(ticketCode));
//    }
//
//    @Test
//    void generateQrContent_wrapsJsonProcessingException() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class)))
//                .thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("JSON error") {});
//
//        // When & Then
//        RuntimeException exception =
//                assertThrows(RuntimeException.class, () -> qrGenerator.generateQrContent(ticketCode));
//        assertEquals("Failed to generate QR content", exception.getMessage());
//    }
//
//    @Test
//    void generateQrContent_callsObjectMapperOnce() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{}");
//
//        // When
//        qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        verify(objectMapper, times(1)).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_multipleCallsWithDifferentCodes() throws Exception {
//        // Given
//        String code1 = "TK-CODE1";
//        String code2 = "TK-CODE2";
//        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{}");
//
//        // When
//        qrGenerator.generateQrContent(code1);
//        qrGenerator.generateQrContent(code2);
//
//        // Then
//        verify(objectMapper, times(2)).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_handlesNullTicketCode() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertNull(payload.get("ticketCode"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(null);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_handlesEmptyString() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn("{}");
//
//        // When
//        String result = qrGenerator.generateQrContent("");
//
//        // Then
//        assertNotNull(result);
//    }
//
//    @Test
//    void generateQrContent_handlesSpecialCharactersInCode() throws Exception {
//        // Given
//        String specialCode = "TK-!@#$%^&*()";
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertEquals(specialCode, payload.get("ticketCode"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(specialCode);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_handlesLongTicketCode() throws Exception {
//        // Given
//        String longCode = "TK-" + "A".repeat(1000);
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertEquals(longCode, payload.get("ticketCode"));
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(longCode);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_payloadHasExactlyTwoFields() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//            Map<String, Object> payload = invocation.getArgument(0);
//            assertEquals(2, payload.size());
//            return "";
//        });
//
//        // When
//        qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        verify(objectMapper).writeValueAsString(any(Map.class));
//    }
//
//    @Test
//    void generateQrContent_returnsNonEmpty() throws Exception {
//        // Given
//        String jsonResponse = "{\"type\":\"TICKET\",\"ticketCode\":\"TK-12345678\"}";
//        when(objectMapper.writeValueAsString(any(Map.class))).thenReturn(jsonResponse);
//
//        // When
//        String result = qrGenerator.generateQrContent(ticketCode);
//
//        // Then
//        assertFalse(result.isEmpty());
//        assertNotNull(result);
//    }
//
//    @Test
//    void generateQrContent_exceptionMessageAccurate() throws Exception {
//        // Given
//        when(objectMapper.writeValueAsString(any(Map.class))).thenThrow(new Exception("Custom serialization error"));
//
//        // When
//        RuntimeException exception =
//                assertThrows(RuntimeException.class, () -> qrGenerator.generateQrContent(ticketCode));
//
//        // Then
//        assertEquals("Failed to generate QR content", exception.getMessage());
//        assertNotNull(exception.getCause());
//    }
//
//    @Test
//    void generateQrContent_typeFieldValueAlwaysTicket() throws Exception {
//        // When - Call multiple times
//        for (int i = 0; i < 5; i++) {
//            when(objectMapper.writeValueAsString(any(Map.class))).thenAnswer(invocation -> {
//                Map<String, Object> payload = invocation.getArgument(0);
//                assertEquals("TICKET", payload.get("type"));
//                return "";
//            });
//
//            qrGenerator.generateQrContent("CODE_" + i);
//        }
//
//        // Then
//        verify(objectMapper, times(5)).writeValueAsString(any(Map.class));
//    }
// }
