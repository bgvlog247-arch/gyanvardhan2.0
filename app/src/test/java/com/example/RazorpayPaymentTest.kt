package com.example

import com.example.data.models.PaymentStatus
import com.example.data.models.PurchaseType
import com.example.data.network.RazorpayBackendService
import com.example.data.security.AuthResult
import com.example.data.security.OwnerAuthService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RazorpayPaymentTest {

    @Test
    fun testRazorpayOrderCreation_inrCurrencyAndPaise() {
        val order = RazorpayBackendService.createOrder(
            itemId = "c_ssc_gd",
            itemTitle = "SSC GD 2026 Batch",
            amountInRupees = 499,
            purchaseType = PurchaseType.ONE_TIME
        )

        assertEquals("INR", order.currency)
        assertEquals(49900L, order.amountInPaise)
        assertTrue(order.orderId.startsWith("order_gv2_"))
        assertTrue(order.receiptId.startsWith("REC_GV2_"))
        assertEquals(RazorpayBackendService.PUBLIC_KEY_ID, order.keyId)
    }

    @Test
    fun testRazorpayOrderCreation_monthlySubscription() {
        val order = RazorpayBackendService.createOrder(
            itemId = "c_railway_d",
            itemTitle = "Railway Monthly Pass",
            amountInRupees = 199,
            purchaseType = PurchaseType.MONTHLY_SUBSCRIPTION
        )

        assertEquals("INR", order.currency)
        assertEquals(19900L, order.amountInPaise)
        assertEquals(PurchaseType.MONTHLY_SUBSCRIPTION, order.purchaseType)
    }

    @Test
    fun testServerHmacSignatureVerification_success() = runBlocking {
        val order = RazorpayBackendService.createOrder(
            itemId = "test_item_01",
            itemTitle = "Demo Item",
            amountInRupees = 100,
            purchaseType = PurchaseType.ONE_TIME
        )

        val (paymentId, signature) = RazorpayBackendService.generateClientPaymentPayload(order.orderId)

        val result = RazorpayBackendService.verifyPaymentSignatureOnServer(
            orderId = order.orderId,
            paymentId = paymentId,
            clientProvidedSignature = signature,
            receiptId = order.receiptId,
            simulationOutcome = PaymentStatus.SUCCESS
        )

        assertTrue(result.isVerified)
        assertEquals(PaymentStatus.SUCCESS, result.status)
        assertNull(result.failureReason)
    }

    @Test
    fun testServerHmacSignatureVerification_bankFailureDeclined() = runBlocking {
        val order = RazorpayBackendService.createOrder(
            itemId = "test_item_02",
            itemTitle = "Failed Item",
            amountInRupees = 300,
            purchaseType = PurchaseType.ONE_TIME
        )

        val (paymentId, signature) = RazorpayBackendService.generateClientPaymentPayload(order.orderId)

        val result = RazorpayBackendService.verifyPaymentSignatureOnServer(
            orderId = order.orderId,
            paymentId = paymentId,
            clientProvidedSignature = signature,
            receiptId = order.receiptId,
            simulationOutcome = PaymentStatus.FAILED
        )

        assertFalse(result.isVerified)
        assertEquals(PaymentStatus.FAILED, result.status)
        assertNotNull(result.failureReason)
    }

    @Test
    fun testSingleOwnerAdminStrictProtection() {
        // Owner admin authorization
        val ownerResult = OwnerAuthService.authenticateOwnerAdmin("bgvlog247@gmail.com", "88219")
        assertTrue(ownerResult is AuthResult.Success)

        // Non-owner rejection
        val studentResult = OwnerAuthService.authenticateOwnerAdmin("student@example.com", "88219")
        assertTrue(studentResult is AuthResult.Denied)

        // Wrong token rejection
        val wrongTokenResult = OwnerAuthService.authenticateOwnerAdmin("bgvlog247@gmail.com", "wrong_token")
        assertTrue(wrongTokenResult is AuthResult.Denied)
    }
}
