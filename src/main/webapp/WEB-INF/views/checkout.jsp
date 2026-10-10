<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Checkout - Valliappan Mart"/>
</jsp:include>

<div class="container py-4" style="max-width: 1080px;">
    
    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <h2 class="fw-bold mb-0">Secure Checkout</h2>
        <span class="text-muted small"><i class="bi bi-shield-lock-fill text-warning me-1"></i> 256-Bit SSL Encrypted</span>
    </div>

    <c:if test="${not empty sessionScope.checkoutError}">
        <div class="alert alert-danger mb-4">
            ${sessionScope.checkoutError}
            <c:remove var="checkoutError" scope="session"/>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/checkout/place-order" method="post">
        <div class="row g-4">
            
            <!-- Left: Checkout Steps -->
            <div class="col-lg-8">
                
                <!-- STEP 1: Select or Add Delivery Address -->
                <div class="card mb-4 shadow-sm border-0">
                    <div class="card-header bg-navy text-white d-flex align-items-center" style="background-color: #131921;">
                        <span class="badge bg-warning text-dark me-2">1</span>
                        <h5 class="mb-0 fw-bold text-white">Select a Delivery Address</h5>
                    </div>
                    <div class="card-body p-4">
                        
                        <c:choose>
                            <c:when test="${not empty addresses}">
                                <div class="mb-3">
                                    <c:forEach var="addr" items="${addresses}" varStatus="status">
                                        <div class="form-check p-3 border rounded mb-2 ${addr.default ? 'bg-light border-warning' : ''}">
                                            <input class="form-check-input" type="radio" name="addressId" id="addr_${addr.id}" value="${addr.id}" ${status.first ? 'checked' : ''} onchange="toggleNewAddressForm(false)">
                                            <label class="form-check-label w-100" for="addr_${addr.id}">
                                                <div class="d-flex justify-content-between align-items-center">
                                                    <strong>${addr.fullName}</strong>
                                                    <c:if test="${addr.default}">
                                                        <span class="badge bg-warning text-dark small">Default Address</span>
                                                    </c:if>
                                                </div>
                                                <div class="text-secondary small mt-1">${addr.formattedAddress}</div>
                                            </label>
                                        </div>
                                    </c:forEach>

                                    <div class="form-check p-3 border rounded">
                                        <input class="form-check-input" type="radio" name="addressId" id="addr_new" value="new" onchange="toggleNewAddressForm(true)">
                                        <label class="form-check-label fw-semibold" for="addr_new">
                                            + Add a new delivery address
                                        </label>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="addressId" value="new">
                            </c:otherwise>
                        </c:choose>

                        <!-- New Address Input Form (Hidden unless selected or no address exists) -->
                        <div id="newAddressContainer" style="${empty addresses ? 'display: block;' : 'display: none;'}" class="bg-light p-3 rounded border mt-3">
                            <h6 class="fw-bold mb-3">Add a New Indian Address</h6>
                            <div class="row g-2">
                                <div class="col-md-6 mb-2">
                                    <label class="form-label small fw-semibold">Full Name</label>
                                    <input type="text" name="fullName" class="form-control form-control-sm" placeholder="Valliappan V" value="${sessionScope.currentUser.name}">
                                </div>
                                <div class="col-md-6 mb-2">
                                    <label class="form-label small fw-semibold">Mobile Number</label>
                                    <input type="text" name="phone" class="form-control form-control-sm" placeholder="+91 9876543210" value="${sessionScope.currentUser.phone}">
                                </div>
                                <div class="col-12 mb-2">
                                    <label class="form-label small fw-semibold">Flat, House no., Building, Apartment</label>
                                    <input type="text" name="addressLine1" class="form-control form-control-sm" placeholder="Plot No. 42, 3rd Cross">
                                </div>
                                <div class="col-12 mb-2">
                                    <label class="form-label small fw-semibold">Area, Street, Sector, Village</label>
                                    <input type="text" name="addressLine2" class="form-control form-control-sm" placeholder="Gandhi Nagar, Adyar">
                                </div>
                                <div class="col-md-4 mb-2">
                                    <label class="form-label small fw-semibold">Town/City</label>
                                    <input type="text" name="city" class="form-control form-control-sm" placeholder="Chennai">
                                </div>
                                <div class="col-md-4 mb-2">
                                    <label class="form-label small fw-semibold">State</label>
                                    <input type="text" name="state" class="form-control form-control-sm" placeholder="Tamil Nadu" value="Tamil Nadu">
                                </div>
                                <div class="col-md-4 mb-2">
                                    <label class="form-label small fw-semibold">6-digit PIN Code</label>
                                    <input type="text" name="pincode" class="form-control form-control-sm" placeholder="600020">
                                </div>
                            </div>
                        </div>

                    </div>
                </div>

                <!-- STEP 2: Payment Method Simulation -->
                <div class="card mb-4 shadow-sm border-0">
                    <div class="card-header text-white d-flex align-items-center" style="background-color: #131921;">
                        <span class="badge bg-warning text-dark me-2">2</span>
                        <h5 class="mb-0 fw-bold text-white">Select a Payment Method</h5>
                    </div>
                    <div class="card-body p-4">
                        
                        <!-- UPI Option -->
                        <div class="form-check p-3 border rounded mb-3">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="pay_upi" value="UPI" checked>
                            <label class="form-check-label w-100" for="pay_upi">
                                <div class="d-flex align-items-center justify-content-between">
                                    <strong><i class="bi bi-phone me-2 text-primary"></i>UPI / Instant QR (Google Pay, PhonePe, Paytm, BHIM)</strong>
                                    <span class="badge bg-success">Instant Confirmation</span>
                                </div>
                            </label>
                            
                            <div id="paymentPanel-UPI" class="payment-panel mt-3 ps-3 border-start border-3 border-warning">
                                <div class="row align-items-center g-3">
                                    <div class="col-md-7">
                                        <label class="form-label small fw-semibold">Enter your UPI ID / VPA</label>
                                        <div class="input-group input-group-sm">
                                            <input type="text" class="form-control" placeholder="username@oksbi / username@paytm" value="valliappan@okhdfcbank">
                                            <button class="btn btn-outline-secondary" type="button">Verify</button>
                                        </div>
                                        <span class="small text-muted d-block mt-1">Accept the collect request in your UPI App upon submission.</span>
                                    </div>
                                    <div class="col-md-5 text-center">
                                        <div class="p-2 border rounded bg-light d-inline-block">
                                            <i class="bi bi-qr-code fs-1 text-dark"></i>
                                            <div class="small text-muted">Scan with any UPI App</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Credit / Debit Card Option -->
                        <div class="form-check p-3 border rounded mb-3">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="pay_card" value="CREDIT_CARD">
                            <label class="form-check-label w-100" for="pay_card">
                                <strong><i class="bi bi-credit-card me-2 text-warning"></i>Credit or Debit Card (Visa, MasterCard, RuPay)</strong>
                            </label>

                            <div id="paymentPanel-CREDIT_CARD" class="payment-panel mt-3 ps-3 border-start border-3 border-warning" style="display: none;">
                                <div class="row g-2">
                                    <div class="col-12">
                                        <label class="form-label small fw-semibold">Card Number</label>
                                        <input type="text" class="form-control form-control-sm" placeholder="4532 •••• •••• 8821">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label small fw-semibold">Name on Card</label>
                                        <input type="text" class="form-control form-control-sm" placeholder="Valliappan V">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label small fw-semibold">Expiry (MM/YY)</label>
                                        <input type="text" class="form-control form-control-sm" placeholder="12/28">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label small fw-semibold">CVV</label>
                                        <input type="password" maxlength="4" class="form-control form-control-sm" placeholder="•••">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Cash on Delivery -->
                        <div class="form-check p-3 border rounded">
                            <input class="form-check-input" type="radio" name="paymentMethod" id="pay_cod" value="COD">
                            <label class="form-check-label w-100" for="pay_cod">
                                <strong><i class="bi bi-cash-stack me-2 text-success"></i>Cash on Delivery (COD)</strong>
                            </label>

                            <div id="paymentPanel-COD" class="payment-panel mt-2 ps-3 border-start border-3 border-warning small text-muted" style="display: none;">
                                Pay via Cash, UPI, or Card at your doorstep when the delivery partner arrives.
                            </div>
                        </div>

                    </div>
                </div>

                <!-- STEP 3: Review Items -->
                <div class="card mb-4 shadow-sm border-0">
                    <div class="card-header text-white d-flex align-items-center" style="background-color: #131921;">
                        <span class="badge bg-warning text-dark me-2">3</span>
                        <h5 class="mb-0 fw-bold text-white">Review Items and Delivery</h5>
                    </div>
                    <div class="card-body p-4">
                        <div class="vm-prime-delivery mb-3">
                            <i class="bi bi-truck me-1"></i> Guaranteed Delivery: <strong>Tomorrow, 11 AM - 5 PM</strong>
                        </div>
                        <c:forEach var="item" items="${cartItems}">
                            <div class="d-flex align-items-center justify-content-between border-bottom py-2">
                                <div class="d-flex align-items-center gap-3">
                                    <img src="${item.product.mainImage}" alt="${item.product.name}" class="rounded" style="width: 50px; height: 50px; object-fit: contain;">
                                    <div>
                                        <div class="fw-semibold small">${item.product.name}</div>
                                        <div class="text-muted small">Qty: ${item.quantity} &bull; Sold by: ${item.product.storeName}</div>
                                    </div>
                                </div>
                                <div class="fw-bold text-dark">₹<fmt:formatNumber value="${item.itemTotal}" pattern="#,##,###"/></div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

            </div>

            <!-- Right: Order Summary Box -->
            <div class="col-lg-4">
                <div class="card p-4 rounded shadow-sm sticky-top border-0 bg-white" style="top: 20px;">
                    
                    <button type="submit" class="btn btn-amazon-primary w-100 py-3 fs-5 fw-bold mb-3 shadow-sm">
                        Place Your Order
                    </button>
                    
                    <p class="small text-muted text-center mb-3">
                        By placing your order, you agree to Valliappan Mart's conditions of use and privacy policy.
                    </p>

                    <h6 class="fw-bold border-bottom pb-2 mb-3">Order Summary</h6>

                    <div class="d-flex justify-content-between mb-2 small text-muted">
                        <span>Items Subtotal:</span>
                        <span class="text-dark fw-semibold">₹<fmt:formatNumber value="${subtotal}" pattern="#,##,###"/></span>
                    </div>

                    <div class="d-flex justify-content-between mb-2 small text-success">
                        <span>Promotion / Discount:</span>
                        <span class="fw-semibold">-₹<fmt:formatNumber value="${discount}" pattern="#,##,###"/></span>
                    </div>

                    <div class="d-flex justify-content-between mb-2 small text-muted">
                        <span>Delivery Charge:</span>
                        <span class="text-dark fw-semibold">${shipping == 0 ? 'FREE' : '₹' += shipping}</span>
                    </div>

                    <div class="d-flex justify-content-between mb-3 small text-muted">
                        <span>Taxes (18% GST included):</span>
                        <span class="text-dark">₹<fmt:formatNumber value="${tax}" pattern="#,##,###"/></span>
                    </div>

                    <hr>

                    <div class="d-flex justify-content-between align-items-baseline mb-4">
                        <span class="fs-5 fw-bold text-dark">Order Total:</span>
                        <span class="fs-4 fw-bold text-danger">₹<fmt:formatNumber value="${total}" pattern="#,##,###"/></span>
                    </div>

                    <div class="bg-light p-3 rounded small text-muted">
                        <div class="fw-bold text-dark mb-1"><i class="bi bi-patch-check-fill text-success me-1"></i> 100% Purchase Protection</div>
                        We guarantee safe payment simulation, tamper-proof packaging, and prompt customer support.
                    </div>

                </div>
            </div>

        </div>
    </form>

</div>

<script>
    function toggleNewAddressForm(show) {
        const container = document.getElementById("newAddressContainer");
        if (container) {
            container.style.display = show ? "block" : "none";
        }
    }
</script>

<jsp:include page="common/footer.jsp"/>
