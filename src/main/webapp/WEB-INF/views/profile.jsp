<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Your Account - Valliappan Mart"/>
</jsp:include>

<div class="container py-4" style="max-width: 1040px;">
    
    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <div>
            <h2 class="fw-bold mb-1 text-dark">Your Account</h2>
            <div class="text-muted small">Manage your profile, delivery addresses, and track recent orders.</div>
        </div>
        <span class="badge bg-warning text-dark text-uppercase fs-6 px-3 py-2">${sessionScope.currentUser.role} ROLE</span>
    </div>

    <c:if test="${not empty sessionScope.profileSuccess}">
        <div class="alert alert-success alert-dismissible fade show mb-4">
            <i class="bi bi-check-circle-fill me-2"></i> ${sessionScope.profileSuccess}
            <c:remove var="profileSuccess" scope="session"/>
        </div>
    </c:if>

    <!-- Top Account Quick Cards -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card p-3 h-100 border rounded shadow-sm bg-white">
                <div class="d-flex align-items-center gap-3">
                    <i class="bi bi-box-seam fs-1 text-warning"></i>
                    <div>
                        <h6 class="fw-bold mb-1"><a href="#ordersSection" class="text-dark text-decoration-none">Your Orders</a></h6>
                        <span class="small text-muted">Track, return, or buy things again</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3 h-100 border rounded shadow-sm bg-white">
                <div class="d-flex align-items-center gap-3">
                    <i class="bi bi-geo-alt fs-1 text-warning"></i>
                    <div>
                        <h6 class="fw-bold mb-1"><a href="#addressesSection" class="text-dark text-decoration-none">Your Addresses</a></h6>
                        <span class="small text-muted">Edit addresses for orders & gifts</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card p-3 h-100 border rounded shadow-sm bg-white">
                <div class="d-flex align-items-center gap-3">
                    <i class="bi bi-heart fs-1 text-warning"></i>
                    <div>
                        <h6 class="fw-bold mb-1"><a href="${pageContext.request.contextPath}/wishlist" class="text-dark text-decoration-none">Your Wishlist</a></h6>
                        <span class="small text-muted">Explore saved products</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Personal Profile Section -->
    <div class="card mb-4 border rounded shadow-sm bg-white">
        <div class="card-header bg-light fw-bold">Login & Security Settings</div>
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/profile/update" method="post" class="row g-3">
                <div class="col-md-4">
                    <label class="form-label small fw-semibold">Name</label>
                    <input type="text" name="name" class="form-control" value="${sessionScope.currentUser.name}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label small fw-semibold">Email Address</label>
                    <input type="email" class="form-control" value="${sessionScope.currentUser.email}" readonly disabled>
                </div>
                <div class="col-md-4">
                    <label class="form-label small fw-semibold">Mobile Number</label>
                    <input type="text" name="phone" class="form-control" value="${sessionScope.currentUser.phone}">
                </div>
                <div class="col-12 text-end">
                    <button type="submit" class="btn btn-warning fw-semibold px-4">Save Changes</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Multi-Address Book Section -->
    <div class="card mb-4 border rounded shadow-sm bg-white" id="addressesSection">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <span class="fw-bold">Your Delivery Address Book</span>
            <button class="btn btn-sm btn-outline-dark" type="button" data-bs-toggle="collapse" data-bs-target="#addAddressForm">
                <i class="bi bi-plus-lg me-1"></i> Add Address
            </button>
        </div>
        <div class="card-body p-4">
            
            <!-- Collapsible Add Address Form -->
            <div class="collapse mb-4" id="addAddressForm">
                <form action="${pageContext.request.contextPath}/profile/address/add" method="post" class="bg-light p-3 rounded border">
                    <h6 class="fw-bold mb-3">Add a New Indian Address</h6>
                    <div class="row g-2">
                        <div class="col-md-6 mb-2">
                            <label class="form-label small fw-semibold">Full Name</label>
                            <input type="text" name="fullName" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-md-6 mb-2">
                            <label class="form-label small fw-semibold">Mobile Number</label>
                            <input type="text" name="phone" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-12 mb-2">
                            <label class="form-label small fw-semibold">Flat, House no., Building, Apartment</label>
                            <input type="text" name="addressLine1" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-12 mb-2">
                            <label class="form-label small fw-semibold">Area, Colony, Street, Sector</label>
                            <input type="text" name="addressLine2" class="form-control form-control-sm">
                        </div>
                        <div class="col-md-4 mb-2">
                            <label class="form-label small fw-semibold">Town/City</label>
                            <input type="text" name="city" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-md-4 mb-2">
                            <label class="form-label small fw-semibold">State</label>
                            <input type="text" name="state" class="form-control form-control-sm" value="Tamil Nadu" required>
                        </div>
                        <div class="col-md-4 mb-2">
                            <label class="form-label small fw-semibold">PIN Code</label>
                            <input type="text" name="pincode" class="form-control form-control-sm" required>
                        </div>
                        <div class="col-12 mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="isDefault" id="checkDefault">
                                <label class="form-check-label small" for="checkDefault">Set as my default address</label>
                            </div>
                        </div>
                        <div class="col-12">
                            <button type="submit" class="btn btn-warning btn-sm fw-semibold">Save Address</button>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Existing Addresses Grid -->
            <c:choose>
                <c:when test="${empty addresses}">
                    <p class="text-muted small mb-0">No saved addresses found. Add an address to speed up checkout.</p>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-1 row-cols-md-2 g-3">
                        <c:forEach var="a" items="${addresses}">
                            <div class="col">
                                <div class="border rounded p-3 h-100 ${a.default ? 'border-warning bg-light' : ''}">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <strong class="text-dark">${a.fullName}</strong>
                                        <c:if test="${a.default}">
                                            <span class="badge bg-warning text-dark small">Default</span>
                                        </c:if>
                                    </div>
                                    <p class="text-secondary small mb-3">${a.formattedAddress}</p>
                                    <div class="d-flex gap-2 align-items-center small">
                                        <c:if test="${!a.default}">
                                            <form action="${pageContext.request.contextPath}/profile/address/default" method="post" class="d-inline">
                                                <input type="hidden" name="id" value="${a.id}">
                                                <button type="submit" class="btn btn-link p-0 small text-decoration-none">Set as Default</button>
                                            </form>
                                            <span class="text-muted">|</span>
                                        </c:if>
                                        <a href="${pageContext.request.contextPath}/profile/address/delete?id=${a.id}" class="text-danger text-decoration-none">Delete</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>

    <!-- Orders Section -->
    <div class="card mb-4 border rounded shadow-sm bg-white" id="ordersSection">
        <div class="card-header bg-light fw-bold">Order History & Invoices</div>
        <div class="card-body p-4">
            <c:choose>
                <c:when test="${empty orders}">
                    <div class="text-center py-4">
                        <i class="bi bi-bag-check fs-1 text-muted d-block mb-2"></i>
                        <p class="text-muted small">You haven't placed any orders yet.</p>
                        <a href="${pageContext.request.contextPath}/shop" class="btn btn-warning btn-sm">Start Shopping</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="d-flex flex-column gap-3">
                        <c:forEach var="ord" items="${orders}">
                            <div class="border rounded overflow-hidden">
                                <!-- Order Header -->
                                <div class="bg-light p-3 d-flex flex-wrap justify-content-between align-items-center small border-bottom">
                                    <div class="d-flex gap-4">
                                        <div>
                                            <span class="text-muted d-block">ORDER PLACED</span>
                                            <strong><fmt:formatDate value="${ord.createdAt}" pattern="dd MMMM yyyy"/></strong>
                                        </div>
                                        <div>
                                            <span class="text-muted d-block">TOTAL</span>
                                            <strong>₹<fmt:formatNumber value="${ord.totalAmount}" pattern="#,##,###"/></strong>
                                        </div>
                                        <div>
                                            <span class="text-muted d-block">SHIP TO</span>
                                            <strong>${sessionScope.currentUser.name}</strong>
                                        </div>
                                    </div>
                                    <div class="text-end mt-2 mt-sm-0">
                                        <span class="text-muted d-block">ORDER # ${ord.orderNumber}</span>
                                        <a href="${pageContext.request.contextPath}/order/invoice?orderNumber=${ord.orderNumber}" target="_blank" class="text-primary text-decoration-none fw-semibold">
                                            View Tax Invoice
                                        </a>
                                    </div>
                                </div>

                                <!-- Order Items Body -->
                                <div class="p-3">
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <div>
                                            <span class="badge bg-success me-2">${ord.orderStatus}</span>
                                            <span class="text-muted small">Tracking: <strong>${ord.trackingNumber}</strong></span>
                                        </div>
                                        <a href="${pageContext.request.contextPath}/order/track?orderNumber=${ord.orderNumber}" class="btn btn-warning btn-sm fw-semibold">
                                            Track Package
                                        </a>
                                    </div>

                                    <c:forEach var="item" items="${ord.items}">
                                        <div class="d-flex align-items-center justify-content-between py-2 border-top">
                                            <div class="d-flex align-items-center gap-3">
                                                <img src="${item.productImage}" alt="${item.productName}" class="rounded" style="width: 50px; height: 50px; object-fit: contain;">
                                                <div>
                                                    <div class="fw-semibold small">${item.productName}</div>
                                                    <div class="text-muted small">Qty: ${item.quantity} &bull; ₹<fmt:formatNumber value="${item.unitPrice}" pattern="#,##,###"/></div>
                                                </div>
                                            </div>
                                            <a href="${pageContext.request.contextPath}/product?id=${item.productId}#reviewsSection" class="btn btn-outline-secondary btn-sm">
                                                Write a Review
                                            </a>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</div>

<jsp:include page="common/footer.jsp"/>
