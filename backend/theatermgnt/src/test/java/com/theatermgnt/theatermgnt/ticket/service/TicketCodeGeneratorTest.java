// package com.theatermgnt.theatermgnt.ticket.service;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import java.util.HashSet;
// import java.util.Set;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// class TicketCodeGeneratorTest {
//
//    private TicketCodeGenerator ticketCodeGenerator;
//
//    @BeforeEach
//    void setUp() {
//        ticketCodeGenerator = new TicketCodeGenerator();
//    }
//
//    // ================= GENERATE TESTS =================
//
//    @Test
//    void generate_returnsNonNullCode() {
//        // When
//        String code = ticketCodeGenerator.generate();
//
//        // Then
//        assertNotNull(code);
//    }
//
//    @Test
//    void generate_returnsNonEmptyCode() {
//        // When
//        String code = ticketCodeGenerator.generate();
//
//        // Then
//        assertFalse(code.isEmpty());
//    }
//
//    @Test
//    void generate_startsWithPrefix() {
//        // When
//        String code = ticketCodeGenerator.generate();
//
//        // Then
//        assertTrue(code.startsWith("TK-"));
//    }
//
//    @Test
//    void generate_correctFormat() {
//        // When
//        String code = ticketCodeGenerator.generate();
//
//        // Then
//        assertTrue(code.matches("TK-[A-F0-9]{8}"));
//    }
//
//    @Test
//    void generate_hasCorrectLength() {
//        // When
//        String code = ticketCodeGenerator.generate();
//
//        // Then
//        assertEquals(11, code.length()); // "TK-" (3) + 8 hex chars
//    }
//
//    @Test
//    void generate_allGeneratedCodesHaveCorrectLength() {
//        // When
//        for (int i = 0; i < 100; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            // Then
//            assertEquals(11, code.length());
//        }
//    }
//
//    @Test
//    void generate_allGeneratedCodesStartWithPrefix() {
//        // When
//        for (int i = 0; i < 100; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            // Then
//            assertTrue(code.startsWith("TK-"));
//        }
//    }
//
//    @Test
//    void generate_uppercaseOnlyAfterPrefix() {
//        // When
//        for (int i = 0; i < 50; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            // Then
//            String codeWithoutPrefix = code.substring(3);
//            assertTrue(codeWithoutPrefix.matches("[A-F0-9]{8}"));
//        }
//    }
//
//    @Test
//    void generate_producesUniqueCodes() {
//        // Given
//        Set<String> generatedCodes = new HashSet<>();
//
//        // When
//        for (int i = 0; i < 1000; i++) {
//            String code = ticketCodeGenerator.generate();
//            generatedCodes.add(code);
//        }
//
//        // Then
//        assertEquals(1000, generatedCodes.size());
//    }
//
//    @Test
//    void generate_highUniquenessRate() {
//        // Given
//        Set<String> generatedCodes = new HashSet<>();
//        int iterations = 10000;
//
//        // When
//        for (int i = 0; i < iterations; i++) {
//            String code = ticketCodeGenerator.generate();
//            generatedCodes.add(code);
//        }
//
//        // Then
//        double uniquenessRate = (double) generatedCodes.size() / iterations;
//        assertTrue(uniquenessRate > 0.99); // At least 99% unique
//    }
//
//    @Test
//    void generate_noNullCharacters() {
//        // When
//        for (int i = 0; i < 50; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            // Then
//            assertFalse(code.contains("\0"));
//            assertFalse(code.contains("null"));
//        }
//    }
//
//    @Test
//    void generate_validHexadecimalFormat() {
//        // When
//        for (int i = 0; i < 50; i++) {
//            String code = ticketCodeGenerator.generate();
//            String hexPart = code.substring(3); // Remove "TK-" prefix
//
//            // Then
//            assertDoesNotThrow(() -> Long.parseLong(hexPart, 16));
//        }
//    }
//
//    @Test
//    void generate_multipleCallsReturnDifferentCodes() {
//        // When
//        String code1 = ticketCodeGenerator.generate();
//        String code2 = ticketCodeGenerator.generate();
//        String code3 = ticketCodeGenerator.generate();
//
//        // Then
//        assertNotEquals(code1, code2);
//        assertNotEquals(code2, code3);
//        assertNotEquals(code1, code3);
//    }
//
//    @Test
//    void generate_consistentPrefixFormat() {
//        // When & Then
//        for (int i = 0; i < 100; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            assertEquals("TK-", code.substring(0, 3));
//            assertEquals(11, code.length());
//            assertTrue(code.charAt(2) == '-');
//        }
//    }
//
//    @Test
//    void generate_noSpecialCharactersInCode() {
//        // When
//        for (int i = 0; i < 50; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            // Then
//            assertTrue(code.matches("[TK\\-A-F0-9]+"));
//            assertFalse(code.contains(" "));
//            assertFalse(code.contains("_"));
//            assertFalse(code.contains("*"));
//        }
//    }
//
//    @Test
//    void generate_canBeUsedAsUniqueIdentifier() {
//        // Given
//        Set<String> codeSet = new HashSet<>();
//
//        // When
//        for (int i = 0; i < 1000; i++) {
//            String code = ticketCodeGenerator.generate();
//            boolean added = codeSet.add(code);
//
//            // Then
//            assertTrue(added, "Code should be unique: " + code);
//        }
//    }
//
//    @Test
//    void generate_performanceTest() {
//        // When
//        long startTime = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
//            ticketCodeGenerator.generate();
//        }
//        long endTime = System.currentTimeMillis();
//
//        // Then - Should generate 10000 codes in less than 1 second
//        assertTrue((endTime - startTime) < 1000);
//    }
//
//    @Test
//    void generate_returnsSingleString() {
//        // When
//        String code = ticketCodeGenerator.generate();
//
//        // Then
//        assertNotNull(code);
//        assertEquals(1, code.split("-").length); // Only one dash (separating prefix)
//    }
//
//    @Test
//    void generate_doesNotContainLowercaseHex() {
//        // When
//        for (int i = 0; i < 100; i++) {
//            String code = ticketCodeGenerator.generate();
//
//            // Then
//            String hexPart = code.substring(3);
//            assertFalse(hexPart.matches(".*[a-f].*"));
//        }
//    }
//
//    @Test
//    void generate_partiallyDeterministicByTime() {
//        // This test verifies that even though UUIDs are generated,
//        // the format is consistent across multiple generations
//        // When
//        String code1 = ticketCodeGenerator.generate();
//        String code2 = ticketCodeGenerator.generate();
//
//        // Then
//        assertEquals(code1.substring(0, 3), code2.substring(0, 3)); // Same prefix
//    }
//
//    @Test
//    void generate_edgeCasesHandling() {
//        // When & Then - No exceptions should be thrown
//        assertDoesNotThrow(() -> {
//            for (int i = 0; i < 100; i++) {
//                String code = ticketCodeGenerator.generate();
//                assertNotNull(code);
//            }
//        });
//    }
// }
