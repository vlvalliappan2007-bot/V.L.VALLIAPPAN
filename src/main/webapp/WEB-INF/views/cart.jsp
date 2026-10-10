<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Valliappan Mart Shopping Cart"/>
</jsp:include>

<div class="container-fluid px-lg-5 py-4">
    <div class="row g-4">
        
        <!-- Cart Items List -->
        <div class="col-lg-8">
            <div class="bg-white p-4 rounded shadow-sm">
                
                <div class="d-flex justify-content-between align-items-center border-bottom pb-3 mb-3">
                    <h3 class="fw-bold mb-0">Shopping Cart</h3>
                    <span class="text-muted small">Price</span>
                </div>

                <c:choose>
                    <c:when test="${empty cartItems}">
                        <div class="py-5 text-center">
                            <i class="bi bi-cart-x fs-1 text-muted d-block mb-3"></i>
                            <h4 class="fw-bold">Your Valliappan Mart Cart is empty.</h4>
                            <p class="text-muted">Explore today's featured electronics, apparel, and home essentials.</p>
                            <a href="${pageContext.request.contextPath}/shop" class="btn btn-add-to-cart w-auto px-4 mt-2">
                                Continue Shopping
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="item" items="${cartItems}">
                            <div class="row align-items-center border-bottom py-3 g-3">
                                <!-- Product Image -->
                                <div class="col-3 col-sm-2 text-center">
                                    <a href="${pageContext.request.contextPath}/product?id=${item.product.id}">
                                        <img src="${item.product.mainImage}" alt="${item.product.name}" class="img-fluid rounded" style="max-height: 110px; object-fit: contain;">
                                    </a>
                                </div>

                                <!-- Product Info & Controls -->
                                <div class="col-6 col-sm-7">
                                    <h6 class="fw-bold mb-1">
                                        <a href="${pageContext.request.contextPath}/product?id=${item.product.id}" class="text-dark text-decoration-none">
                                            ${item.product.name}
                                        </a>
                                    </h6>
                                    <div class="text-success small fw-semibold mb-1">In stock</div>
                                    <div class="small text-muted mb-2">Sold by: <span class="text-primary">${item.product.storeName}</span></div>
                                    
                                    <div class="d-flex align-items-center gap-3">
                                        <!-- Interactive AJAX Quantity Dropdown -->
                                        <div class="d-flex align-items-center gap-1">
                                            <label class="small text-muted">Qty:</label>
                                            <select class="form-select form-select-sm cart-item-qty-select" data-item-id="${item.id}" style="width: 70px;">
                                                <c:forEach begin="1" end="10" var="q">
                                                    <option value="${q}" ${item.quantity == q ? 'selected' : ''}>${q}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <span class="text-muted">|</span>
                                        <!-- Remove link -->
                                        <a href="${pageContext.request.contextPath}/cart/remove?id=${item.id}" class="text-danger small text-decoration-none">
                                            <i class="bi bi-trash3 me-1"></i>Delete
                                        </a>
                                    </div>
                                </div>

                                <!-- Item Price in INR ₹ -->
                                <div class="col-3 col-sm-3 text-end">
                                    <span class="fs-5 fw-bold text-dark">₹<fmt:formatNumber value="${item.itemTotal}" pattern="#,##,###"/></span>
                                    <c:if test="${item.quantity > 1}">
                                        <div class="text-muted small">₹<fmt:formatNumber value="${item.product.price}" pattern="#,##,###"/> each</div>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>

        <!-- Right Summary Box -->
        <div class="col-lg-4">
            <div class="bg-white p-4 rounded shadow-sm sticky-top" style="top: 20px;">
                
                <!-- Free delivery banner -->
                <div class="alert alert-success d-flex align-items-center py-2 mb-3 small" role="alert">
                    <i class="bi bi-check-circle-fill fs-5 me-2 text-success"></i>
                    <div>Your order qualifies for <strong>FREE Delivery</strong> across India!</div>
                </div>

                <h5 class="fw-bold mb-3 border-bottom pb-2">Order Summary</h5>

                <!-- Promo Code Form -->
                <c:if test="${not empty sessionScope.couponSuccess}">
                    <div class="alert alert-success alert-dismissible fade show small py-2 mb-2">
                        ${sessionScope.couponSuccess}
                        <c:remove var="couponSuccess" scope="session"/>
                    </div>
                </c:if>
                <c:if test="${not empty sessionScope.couponError}">
                    <div class="alert alert-danger alert-dismissible fade show small py-2 mb-2">
                        ${sessionScope.couponError}
                        <c:remove var="couponError" scope="session"/>
                    </div>
                </c:if>

                <div class="mb-3">
                    <c:choose>
                        <c:when test="${not empty promoCode}">
                            <div class="d-flex justify-content-between align-items-center bg-light p-2 rounded border">
                                <span class="small fw-semibold text-success"><i class="bi bi-tag-fill me-1"></i>Code applied: ${promoCode}</span>
                                <a href="${pageContext.request.contextPath}/cart/remove-coupon" class="btn btn-sm btn-outline-danger py-0 px-2">Remove</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/cart/apply-coupon" method="post" class="d-flex gap-2">
                                <input type="text" name="couponCode" class="form-control form-control-sm text-uppercase" placeholder="Enter promo code (e.g. AMAZON10)">
                                <button type="submit" class="btn btn-outline-dark btn-sm text-nowrap">Apply</button>
                            </form>
                            <div class="text-muted small mt-1">Try promo code: <code class="fw-bold">AMAZON10</code> (10% off) or <code class="fw-bold">VALLIAPPAN500</code></div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Price Breakdown Table -->
                <div class="d-flex justify-content-between mb-2 small text-muted">
                    <span>Items Subtotal:</span>
                    <span id="cartSubtotalDisplay" class="text-dark fw-semibold">₹<fmt:formatNumber value="${subtotal}" pattern="#,##,###"/></span>
                </div>

                <div class="d-flex justify-content-between mb-2 small text-success">
                    <span>Promo Discount:</span>
                    <span id="cartDiscountDisplay" class="fw-semibold">-₹<fmt:formatNumber value="${discount}" pattern="#,##,###"/></span>
                </div>

                <div class="d-flex justify-content-between mb-2 small text-muted">
                    <span>Shipping & Handling:</span>
                    <span id="cartShippingDisplay" class="text-dark fw-semibold">${shipping == 0 ? 'FREE' : '₹' += shipping}</span>
                </div>

                <div class="d-flex justify-content-between mb-3 small text-muted">
                    <span>Estimated GST (18% included):</span>
                    <span class="text-dark">₹<fmt:formatNumber value="${tax}" pattern="#,##,###"/></span>
                </div>

                <hr>

                <div class="d-flex justify-content-between align-items-baseline mb-4">
                    <span class="fs-5 fw-bold text-dark">Order Total:</span>
                    <span id="cartTotalDisplay" class="fs-4 fw-bold text-danger">₹<fmt:formatNumber value="${total}" pattern="#,##,###"/></span>
                </div>

                <c:choose>
                    <c:when test="${not empty cartItems}">
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-amazon-primary w-100 py-2 fs-6 fw-bold text-center d-block text-decoration-none">
                            Proceed to Buy (${cartItems.size()} items)
                        </a>
                    </c:when>
                    <c:otherwise>
                        <button disabled class="btn btn-secondary w-100 py-2">Proceed to Buy</button>
                    </c:otherwise>
                </c:choose>

                <div class="text-center small text-muted mt-3">
                    <i class="bi bi-shield-lock-fill text-warning me-1"></i> 100% Safe and Secure Checkout
                </div>

            </div>
        </div>

    </div>
</div>

<jsp:include page="common/footer.jsp"/>
