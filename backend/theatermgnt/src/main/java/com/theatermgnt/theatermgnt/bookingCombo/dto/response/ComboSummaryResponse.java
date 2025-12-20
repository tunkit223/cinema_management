package com.theatermgnt.theatermgnt.bookingCombo.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ComboSummaryResponse {
    String comboId;
    String comboName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
}
