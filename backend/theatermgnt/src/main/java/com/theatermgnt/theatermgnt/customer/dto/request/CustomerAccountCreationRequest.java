package com.theatermgnt.theatermgnt.customer.dto.request;

import com.theatermgnt.theatermgnt.account.dto.request.BaseAccountCreationRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAccountCreationRequest extends BaseAccountCreationRequest {}
