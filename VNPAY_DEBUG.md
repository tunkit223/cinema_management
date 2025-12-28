# VNPay Signature Issue - Debug Checklist

## Root Cause
VNPay returns "Sai chữ ký" (Invalid Signature) error

## Things to Check:

### 1. **CRITICAL: VNPay Credentials**
- Check if `VNPAY_TMN_CODE` and `VNPAY_HASH_SECRET` env vars are set correctly
- Current config defaults to literal string "SANDBOX" which is WRONG
- You need actual credentials from VNPay sandbox:
  ```
  VNPAY_TMN_CODE=your_actual_tmn_code
  VNPAY_HASH_SECRET=your_actual_hash_secret_from_vnpay
  ```

### 2. **Verify Backend Logs**
When creating payment, backend logs:
- ✅ VNPay Params (all parameters sent)
- ✅ Hash Data (the string used for hash calculation)
- ✅ Secure Hash (HMAC SHA512 result)
- ✅ Full Payment URL

Check these logs to ensure:
- `vnp_Amount` = price * 100 (in VND)
- `vnp_TmnCode` = your correct TMN code
- Hash looks correct format

### 3. **Fixed Issues in Code**
- ✅ PaymentController now returns ApiResponse wrapper
- ✅ Frontend has proper error logging
- ✅ VNPayUtil.getPaymentURL ensures URL encoding
- ✅ Hash calculation uses URLencoded params

### 4. **Test Steps**
1. Restart backend with correct env vars
2. Check backend logs when initiating payment
3. If signature still wrong, compare:
   - Your TMN code
   - Your Hash Secret
   - Parameter values match VNPay spec

### 5. **VNPay Integration Checklist**
- [ ] TMN code is correct
- [ ] Hash secret is correct  
- [ ] Amount is multiplied by 100
- [ ] All required parameters are present
- [ ] Parameters are properly URL-encoded
- [ ] Return URL is registered in VNPay merchant account
- [ ] IP address is whitelisted if needed
