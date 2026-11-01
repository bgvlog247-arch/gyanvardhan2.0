package com.example.ui.screens.payment

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaymentStatus
import com.example.data.models.PurchaseType
import com.example.data.models.RazorpayOrder
import com.example.ui.locale.AppLanguage
import com.example.ui.locale.AppStrings
import com.example.ui.theme.*
import com.example.ui.viewmodels.EducationViewModel

val RazorpayNavy = Color(0xFF0C2340)
val RazorpayBlue = Color(0xFF0C73EB)
val RazorpayLightBg = Color(0xFFF3F8FE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutDialog(
    item: EducationViewModel.CheckoutItem,
    activeOrder: RazorpayOrder?,
    flowState: EducationViewModel.PaymentFlowState,
    isProcessing: Boolean,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSelectPurchaseType: (PurchaseType) -> Unit,
    onPay: (paymentMethod: String, simulationMode: PaymentStatus) -> Unit,
    onRetry: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
    var selectedSimulationMode by remember { mutableStateOf(PaymentStatus.SUCCESS) }
    var showAdvancedTestMode by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .testTag("razorpay_checkout_dialog")
        ) {
            when (flowState) {
                is EducationViewModel.PaymentFlowState.Success -> {
                    PaymentSuccessView(
                        order = flowState.order,
                        paymentId = flowState.paymentId,
                        receiptId = flowState.receiptId,
                        item = item,
                        currentLanguage = currentLanguage,
                        onDone = onDismiss
                    )
                }
                is EducationViewModel.PaymentFlowState.Failed -> {
                    PaymentFailedView(
                        order = flowState.order,
                        reason = flowState.reason,
                        currentLanguage = currentLanguage,
                        onRetry = onRetry,
                        onDismiss = onDismiss
                    )
                }
                is EducationViewModel.PaymentFlowState.Pending -> {
                    PaymentPendingView(
                        order = flowState.order,
                        message = flowState.message,
                        currentLanguage = currentLanguage,
                        onCheckStatus = onDismiss
                    )
                }
                is EducationViewModel.PaymentFlowState.Verifying -> {
                    PaymentVerifyingView(
                        orderId = flowState.orderId,
                        paymentMethod = flowState.paymentMethod,
                        currentLanguage = currentLanguage
                    )
                }
                else -> {
                    // Checkout Order Screen
                    CheckoutOrderView(
                        item = item,
                        activeOrder = activeOrder,
                        selectedMethod = selectedMethod,
                        onSelectMethod = { selectedMethod = it },
                        selectedSimulationMode = selectedSimulationMode,
                        onSelectSimulationMode = { selectedSimulationMode = it },
                        showAdvancedTestMode = showAdvancedTestMode,
                        onToggleAdvancedTestMode = { showAdvancedTestMode = !showAdvancedTestMode },
                        currentLanguage = currentLanguage,
                        onSelectPurchaseType = onSelectPurchaseType,
                        onConfirmPay = { onPay(selectedMethod, selectedSimulationMode) },
                        isProcessing = isProcessing
                    )
                }
            }
        }
    }
}

@Composable
fun CheckoutOrderView(
    item: EducationViewModel.CheckoutItem,
    activeOrder: RazorpayOrder?,
    selectedMethod: String,
    onSelectMethod: (String) -> Unit,
    selectedSimulationMode: PaymentStatus,
    onSelectSimulationMode: (PaymentStatus) -> Unit,
    showAdvancedTestMode: Boolean,
    onToggleAdvancedTestMode: () -> Unit,
    currentLanguage: AppLanguage,
    onSelectPurchaseType: (PurchaseType) -> Unit,
    onConfirmPay: () -> Unit,
    isProcessing: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Razorpay Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RazorpayNavy,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "R",
                            color = RazorpayBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Razorpay Checkout",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Key: ${activeOrder?.keyId ?: "rzp_test_gyanvardhan2026"} (INR)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = SaffronGold.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "TEST MODE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SaffronGold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subscription vs One-Time Plan Selector (for Courses)
        if (item.isCourse) {
            Text(
                text = if (currentLanguage == AppLanguage.HINDI) "योजना चुनें (Choose Access Plan)" else "Choose Access Plan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlanChoiceCard(
                    title = if (currentLanguage == AppLanguage.HINDI) "आजीवन संपूर्ण एक्सेस" else "Full Lifetime Access",
                    subtitle = "₹${item.amount}",
                    badge = "BEST VALUE",
                    isSelected = item.purchaseType == PurchaseType.ONE_TIME,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectPurchaseType(PurchaseType.ONE_TIME) }
                )
                PlanChoiceCard(
                    title = if (currentLanguage == AppLanguage.HINDI) "मासिक सदस्यता" else "Monthly Subscription",
                    subtitle = "₹${item.monthlyPrice}/mo",
                    badge = "CANCEL ANYTIME",
                    isSelected = item.purchaseType == PurchaseType.MONTHLY_SUBSCRIPTION,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectPurchaseType(PurchaseType.MONTHLY_SUBSCRIPTION) }
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Order Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = if (currentLanguage == AppLanguage.HINDI) item.titleHi else item.titleEn,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (activeOrder != null) {
                    Text(
                        text = "Order ID: ${activeOrder.orderId}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Base Price", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "₹${item.amount}", fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Educational GST (0%)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "₹0", fontSize = 12.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Total Amount (INR)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "₹${item.amount}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = AppStrings.get("select_payment", currentLanguage),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Payment Methods
        PaymentOptionTile(
            title = "UPI (Google Pay, PhonePe, Paytm, BHIM, VPA)",
            subtitle = "Zero convenience fee • Instant verified unlock",
            icon = Icons.Default.AccountBalanceWallet,
            isSelected = selectedMethod.startsWith("UPI"),
            onClick = { onSelectMethod("UPI (Google Pay / PhonePe)") }
        )
        PaymentOptionTile(
            title = "Debit & Credit Cards",
            subtitle = "Visa, MasterCard, RuPay, Maestro",
            icon = Icons.Default.CreditCard,
            isSelected = selectedMethod.startsWith("Debit"),
            onClick = { onSelectMethod("Debit / Credit Card") }
        )
        PaymentOptionTile(
            title = "Net Banking",
            subtitle = "SBI, HDFC, ICICI, Axis, PNB & 50+ Banks",
            icon = Icons.Default.AccountBalance,
            isSelected = selectedMethod.startsWith("Net"),
            onClick = { onSelectMethod("Net Banking") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Advanced Simulation Trigger (Razorpay Test Mode outcomes)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleAdvancedTestMode() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Test Gateway Simulation: ${selectedSimulationMode.name}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (showAdvancedTestMode) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showAdvancedTestMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SimulationButton(
                    label = "Success",
                    isSelected = selectedSimulationMode == PaymentStatus.SUCCESS,
                    color = EmeraldSuccess,
                    onClick = { onSelectSimulationMode(PaymentStatus.SUCCESS) }
                )
                SimulationButton(
                    label = "Decline/Fail",
                    isSelected = selectedSimulationMode == PaymentStatus.FAILED,
                    color = CoralError,
                    onClick = { onSelectSimulationMode(PaymentStatus.FAILED) }
                )
                SimulationButton(
                    label = "Pending OTP",
                    isSelected = selectedSimulationMode == PaymentStatus.PENDING,
                    color = SaffronGold,
                    onClick = { onSelectSimulationMode(PaymentStatus.PENDING) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pay Button
        Button(
            onClick = onConfirmPay,
            enabled = !isProcessing,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RazorpayBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("razorpay_pay_button")
        ) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${AppStrings.get("pay_securely", currentLanguage)} ₹${item.amount}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun PlanChoiceCard(
    title: String,
    subtitle: String,
    badge: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) RazorpayLightBg else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RazorpayBlue)) else null
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isSelected) RazorpayBlue else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = badge,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = subtitle, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (isSelected) RazorpayBlue else MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun PaymentOptionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) RazorpayLightBg else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(RazorpayBlue)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) RazorpayBlue else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = RazorpayBlue)
            )
        }
    }
}

@Composable
fun RowScope.SimulationButton(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, color) else null,
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Composable
fun PaymentVerifyingView(
    orderId: String,
    paymentMethod: String,
    currentLanguage: AppLanguage
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(56.dp), color = RazorpayBlue, strokeWidth = 4.dp)
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = if (currentLanguage == AppLanguage.HINDI) "सर्वर पर हस्ताक्षर सत्यापित हो रहा है..." else "Verifying HMAC-SHA256 Signature...",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Order: $orderId • $paymentMethod",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please do not press back or close the application.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PaymentSuccessView(
    order: RazorpayOrder,
    paymentId: String,
    receiptId: String,
    item: EducationViewModel.CheckoutItem,
    currentLanguage: AppLanguage,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(EmeraldSuccess),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = AppStrings.get("payment_success", currentLanguage),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (currentLanguage == AppLanguage.HINDI) "सामग्री तुरंत अनलॉक कर दी गई है!" else "Signature verified on server. Content unlocked!",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Razorpay Payment ID:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = paymentId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RazorpayBlue)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Receipt No:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = receiptId, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Amount Paid:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "₹${order.amountInPaise / 100} INR", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldSuccess)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Plan Type:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = if (order.purchaseType == PurchaseType.MONTHLY_SUBSCRIPTION) "Monthly Subscription" else "Lifetime One-Time", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Start Learning Now 🎉", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PaymentFailedView(
    order: RazorpayOrder?,
    reason: String,
    currentLanguage: AppLanguage,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CoralError),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (currentLanguage == AppLanguage.HINDI) "भुगतान विफल रहा" else "Payment Failed",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CoralError
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = reason,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CoralError.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Common Troubleshooting:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoralError
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "• Check account balance / daily UPI limit", fontSize = 10.sp)
                Text(text = "• Try using an alternate payment method (Card or Net Banking)", fontSize = 10.sp)
                Text(text = "• Any debited money is auto-refunded within 48 hours by Razorpay", fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Cancel")
            }
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = RazorpayBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Retry Payment")
            }
        }
    }
}

@Composable
fun PaymentPendingView(
    order: RazorpayOrder,
    message: String,
    currentLanguage: AppLanguage,
    onCheckStatus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(SaffronGold),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = NavyDark, modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (currentLanguage == AppLanguage.HINDI) "भुगतान प्रक्रियाधीन है" else "Payment Pending Confirmation",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Order: ${order.orderId}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Amount: ₹${order.amountInPaise / 100}", fontSize = 11.sp)
                Text(text = "We will notify you via Push Notification as soon as the bank confirms the payment webhook.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCheckStatus,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Got It, I'll Wait", color = NavyDark, fontWeight = FontWeight.Bold)
        }
    }
}
