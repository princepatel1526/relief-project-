import api from './api.js';
import { showToast, formatCurrency } from './utils.js';

export async function initDonationForm() {
  const form = document.getElementById('donation-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const btn = form.querySelector('[type="submit"]');
    btn.disabled = true;
    btn.textContent = 'Processing...';

    const amount = parseFloat(form.amount.value);
    if (!amount || amount < 1) {
      showToast('Please enter a valid amount (min ₹1)', 'error');
      btn.disabled = false;
      btn.textContent = 'Donate Now';
      return;
    }

    try {
      const orderData = await api.payments.createOrder({
        amount,
        donorName:   form.donorName.value,
        donorEmail:  form.donorEmail.value,
        donorPhone:  form.donorPhone.value,
        disasterId:  form.disasterId?.value || null,
        donationType: 'MONETARY',
        isAnonymous: form.isAnonymous?.checked || false,
        description: `Donation to Disaster Relief Fund`,
      });

      openRazorpayCheckout(orderData, btn);
    } catch (err) {
      showToast('Failed to initiate payment: ' + err.message, 'error');
      btn.disabled = false;
      btn.textContent = 'Donate Now';
    }
  });
}

function openRazorpayCheckout(orderData, submitBtn) {
  const options = {
    key:          orderData.keyId,
    amount:       orderData.amount * 100,
    currency:     orderData.currency,
    name:         'Disaster Relief Platform',
    description:  orderData.description || 'Donation',
    image:        '/images/logo.png',
    order_id:     orderData.orderId,
    prefill: {
      name:    orderData.donorName,
      email:   orderData.donorEmail,
      contact: orderData.donorPhone,
    },
    notes: {
      db_payment_id: orderData.paymentDbId,
    },
    theme: { color: '#dc2626' },

    handler: async (response) => {
      await handlePaymentSuccess(response, submitBtn);
    },

    modal: {
      ondismiss: () => {
        showToast('Payment cancelled', 'warning');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Donate Now';
      },
    },
  };

  const rzp = new window.Razorpay(options);
  rzp.on('payment.failed', (response) => {
    showToast('Payment failed: ' + response.error.description, 'error');
    submitBtn.disabled = false;
    submitBtn.textContent = 'Donate Now';
  });

  rzp.open();
}

async function handlePaymentSuccess(response, btn) {
  try {
    showToast('Verifying payment...', 'info');
    await api.payments.verify({
      razorpayOrderId:   response.razorpay_order_id,
      razorpayPaymentId: response.razorpay_payment_id,
      razorpaySignature: response.razorpay_signature,
    });

    showToast('Payment successful! Thank you for your donation.', 'success');
    document.getElementById('donation-success')?.classList.remove('hidden');
    document.getElementById('donation-form')?.classList.add('hidden');
  } catch (err) {
    showToast('Payment verification failed: ' + err.message, 'error');
  } finally {
    btn.disabled = false;
    btn.textContent = 'Donate Now';
  }
}
