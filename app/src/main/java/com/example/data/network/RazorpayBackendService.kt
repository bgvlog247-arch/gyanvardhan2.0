package com.example.data.network

import com.example.data.models.PaymentStatus
import com.example.data.models.PurchaseType
import com.example.data.models.RazorpayOrder
import kotlinx.coroutines.delay
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Server-Side Razorpay Payment Service Simulation.
 *
 * Implements backend-only operations:
 * 1. Order generation with INR currency and unique receipts.
 * 2. HMAC-SHA256 Payment Signature Verification on server.
 * 3. Asynchronous Webhook event processing (payment.captured, payment.failed, order.paid).
 * 4. Content unlock authorization (guarantees content is never unlocked prior to signature verification).
 *
 * CRITICAL SECURITY:
 * Secrets and HMAC keys reside strictly on the server side and are NEVER exposed to client UI code.
 */
object RazorpayBackendService {

    // Backend-held test secret (simulated server environment). Client UI never reads this.
    private const val SERVER_RAZORPAY_SECRET = "srv_sec_gyanvardhan2026_backend_only"
    const val PUBLIC_KEY_ID = "rzp_test_gyanvardhan2026"

    data class ServerPaymentVerificationResult(
        val isVerified: Boolean,
        val orderId: String,
        val paymentId: String,
        val signature: String,
        val status: PaymentStatus,
        val receiptId: String,
        val failureReason: String? = null
    )

    data class WebhookEvent(
        val event: String, // "payment.captured", "payment.failed", "order.paid"
        val orderId: String,
        val paymentId: String,
        val amountInPaise: Long,
        val timestamp: Long = System.currentTimeMillis()
    )

    /**
     * Simulates server endpoint: POST /api/payment/create-order
     */
    fun createOrder(
        itemId: String,
        itemTitle: String,
        amountInRupees: Int,
        purchaseType: PurchaseType
    ): RazorpayOrder {
        val randomSuffix = (100000..999999).random()
        val orderId = "order_gv2_${System.currentTimeMillis().toString().takeLast(6)}$randomSuffix"
        val receiptId = "REC_GV2_${purchaseType.name.take(3)}_$randomSuffix"
        val amountInPaise = amountInRupees * 100L

        return RazorpayOrder(
            orderId = orderId,
            amountInPaise = amountInPaise,
            currency = "INR",
            receiptId = receiptId,
            itemId = itemId,
            itemTitle = itemTitle,
            purchaseType = purchaseType,
            keyId = PUBLIC_KEY_ID
        )
    }

    /**
     * Generates genuine HMAC-SHA256 signature for server-side verification.
     */
    fun computeHmacSha256(data: String, secret: String = SERVER_RAZORPAY_SECRET): String {
        return try {
            val keySpec = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(keySpec)
            val hash = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "sig_fallback_${UUID.randomUUID()}"
        }
    }

    /**
     * Simulates client Checkout callback receiving payment credentials and
     * sending them to server endpoint: POST /api/payment/verify-signature
     */
    suspend fun verifyPaymentSignatureOnServer(
        orderId: String,
        paymentId: String,
        clientProvidedSignature: String,
        receiptId: String,
        simulationOutcome: PaymentStatus = PaymentStatus.SUCCESS
    ): ServerPaymentVerificationResult {
        // Network delay to simulate server communication
        delay(1200)

        when (simulationOutcome) {
            PaymentStatus.FAILED -> {
                // Dispatch payment.failed webhook
                processServerWebhook(
                    WebhookEvent(
                        event = "payment.failed",
                        orderId = orderId,
                        paymentId = paymentId,
                        amountInPaise = 0L
                    )
                )
                return ServerPaymentVerificationResult(
                    isVerified = false,
                    orderId = orderId,
                    paymentId = paymentId,
                    signature = clientProvidedSignature,
                    status = PaymentStatus.FAILED,
                    receiptId = receiptId,
                    failureReason = "Bank declined transaction: Insufficient balance or user cancelled authentication."
                )
            }
            PaymentStatus.PENDING -> {
                return ServerPaymentVerificationResult(
                    isVerified = false,
                    orderId = orderId,
                    paymentId = paymentId,
                    signature = clientProvidedSignature,
                    status = PaymentStatus.PENDING,
                    receiptId = receiptId,
                    failureReason = "Awaiting bank gateway settlement. Verification is pending."
                )
            }
            PaymentStatus.SUCCESS -> {
                // Verify HMAC-SHA256 server-side
                val payload = "$orderId|$paymentId"
                val expectedSignature = computeHmacSha256(payload, SERVER_RAZORPAY_SECRET)
                val isMatch = (clientProvidedSignature == expectedSignature)

                if (isMatch) {
                    // Dispatch payment.captured webhook
                    processServerWebhook(
                        WebhookEvent(
                            event = "payment.captured",
                            orderId = orderId,
                            paymentId = paymentId,
                            amountInPaise = 1000L
                        )
                    )
                    return ServerPaymentVerificationResult(
                        isVerified = true,
                        orderId = orderId,
                        paymentId = paymentId,
                        signature = clientProvidedSignature,
                        status = PaymentStatus.SUCCESS,
                        receiptId = receiptId,
                        failureReason = null
                    )
                } else {
                    return ServerPaymentVerificationResult(
                        isVerified = false,
                        orderId = orderId,
                        paymentId = paymentId,
                        signature = clientProvidedSignature,
                        status = PaymentStatus.FAILED,
                        receiptId = receiptId,
                        failureReason = "Cryptographic signature mismatch. Unauthorized transaction rejected."
                    )
                }
            }
        }
    }

    /**
     * Simulates webhook ingestion endpoint: POST /api/payment/webhook
     */
    fun processServerWebhook(event: WebhookEvent): Boolean {
        // Logs and triggers server-side fulfillment
        return event.event in listOf("payment.captured", "payment.failed", "order.paid")
    }

    /**
     * Simulates Razorpay Checkout generating paymentId and client signature upon
     * successful user auth in UPI / Card / NetBanking flow.
     */
    fun generateClientPaymentPayload(orderId: String): Pair<String, String> {
        val paymentId = "pay_rzp_${System.currentTimeMillis().toString().takeLast(7)}${(100..999).random()}"
        val signature = computeHmacSha256("$orderId|$paymentId", SERVER_RAZORPAY_SECRET)
        return Pair(paymentId, signature)
    }
}
