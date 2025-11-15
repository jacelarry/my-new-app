# Quick Test Guide - Import with Automatic Message Composition

## Testing the New Feature

### Test 1: Regular SMS from Excel/CSV
1. Open the app
2. Click **Import** button on main screen
3. Click **Import from Excel/CSV** button
4. Select **"Regular SMS"** option → Click **Next**
5. Choose a CSV file from your device
6. Wait for contacts to import (shows toast with count)
7. **Compose Regular SMS dialog appears automatically**:
   - Type a message (e.g., "Hello from SMS Automation!")
   - Watch character counter update
   - Click **Send** button
8. **Confirmation dialog appears**:
   - Review message preview and recipient count
   - Click **Send Now**
9. **Progress dialog shows**:
   - Real-time sending status
   - Success/fail counts
10. **Results dialog shows**:
    - Final success/fail summary
    - Click **OK**

### Test 2: Interactive SMS with STK Push from Excel/CSV
1. Open the app
2. Click **Import** button on main screen
3. Click **Import from Excel/CSV** button
4. Select **"Interactive SMS with STK Push"** option → Click **Next**
5. Choose a CSV file from your device
6. Wait for contacts to import
7. **Compose Interactive SMS dialog appears automatically**:
   - Type a message (e.g., "Pay for premium SMS bundle")
   - Enter Till Number (e.g., `174379`)
   - Enter Amount per contact (e.g., `50`)
   - Click **Send** button
8. **Confirmation dialog appears**:
   - Shows message preview
   - Shows Till Number and Amount
   - Shows total amount calculation
   - Click **Send Now**
9. **Progress dialog shows**:
   - Processing SMS + STK Push for each contact
   - Real-time success/fail counts
10. **Results dialog shows**:
    - Combined success/fail summary
    - Click **OK**

### Test 3: Cancel Button
1. Import contacts from Excel/CSV
2. In compose dialog, type some text
3. Click **Cancel** button
4. Verify: Toast shows "Message cancelled"
5. Verify: Imported contacts are cleared

### Test 4: Empty Message Validation
1. Import contacts from Excel/CSV
2. In compose dialog, leave message field empty
3. Click **Send** button
4. Verify: Toast shows "Message cannot be empty"

### Test 5: Missing Till Number/Amount Validation
1. Import contacts with Interactive SMS option
2. In compose dialog:
   - Type a message
   - Leave Till Number or Amount empty
3. Click **Send** button
4. Verify: Toast shows "Please enter Till Number and Amount"

## Sample CSV File for Testing

Create a file named `test_contacts.csv`:

```csv
Name,Phone
Test User 1,0712345678
Test User 2,0723456789
Test User 3,0734567890
Test User 4,0745678901
Test User 5,0756789012
```

## Expected Behavior

### Regular SMS:
- ✅ Message sent to all contacts
- ✅ 500ms delay between messages
- ✅ Progress updates in real-time
- ✅ Success/fail counts shown

### Interactive SMS with STK Push:
- ✅ SMS sent first
- ✅ STK Push triggered second
- ✅ Both must succeed for success count
- ✅ 1000ms delay between contacts
- ✅ Combined results shown

## Troubleshooting

### Issue: STK Push not appearing on phone
- **Check**: Till Number is correct
- **Check**: Phone number is in correct format (254XXXXXXXXX)
- **Check**: Daraja API credentials are valid
- **Check**: Using Safaricom SIM card for testing

### Issue: SMS not sending
- **Note**: `sendSmsMessage()` is currently a placeholder
- **Action**: Integrate with actual SMS API provider

### Issue: App crashes on import
- **Check**: CSV file format is correct (Name,Phone)
- **Check**: No special characters in phone numbers
- **Check**: File is not corrupted

### Issue: Character counter not updating
- **Action**: Type in the message field
- **Check**: Message field has focus

## Next Steps After Testing

1. ✅ If Regular SMS works → Integrate real SMS API
2. ✅ If STK Push works → Test with actual payments
3. ✅ If dialogs work → Consider adding more fields (e.g., Account Reference)
4. ✅ If successful → Test with large CSV file (1000+ contacts)
5. ✅ If ready → Switch to production Daraja credentials

## Date Created
November 13, 2025

