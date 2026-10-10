<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Seller Merchant Central - Valliappan Mart"/>
</jsp:include>

<div class="container-fluid px-lg-5 py-4">
    
    <c:choose>
        <%-- Onboarding: "Become a Seller" registration flow --%>
        <c:when test="${empty seller}">
            <div class="card p-4 p-md-5 border-0 shadow-sm mx-auto my-4" style="max-width: 680px;">
                <div class="text-center mb-4">
                    <div class="badge bg-warning text-dark text-uppercase fs-6 px-3 py-2 mb-2">Grow Your Business</div>
                    <h2 class="fw-bold text-dark">Sell to Millions of Customers Across India</h2>
                    <p class="text-muted small">Register your merchant store on Valliappan Mart in just 2 minutes.</p>
                </div>

                <form action="${pageContext.request.contextPath}/seller/become" method="post">
                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-dark">Store / Company Name</label>
                        <input type="text" name="storeName" class="form-control" placeholder="e.g. Apex Tech Retails" required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-dark">Primary Category / Business Type</label>
                        <select name="businessType" class="form-select">
                            <option value="Consumer Electronics & Peripherals">Consumer Electronics & Peripherals</option>
                            <option value="Fashion, Apparel & Footwear">Fashion, Apparel & Footwear</option>
                            <option value="Home, Kitchen & Living">Home, Kitchen & Living</option>
                            <option value="Books, Publishing & Stationery">Books, Publishing & Stationery</option>
                            <option value="Fitness & Sports Equipment">Fitness & Sports Equipment</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label small fw-semibold text-dark">GSTIN / Tax Identification Number</label>
                        <input type="text" name="gstNumber" class="form-control" placeholder="33AABCT1334M1Z5" required>
                    </div>

                    <div class="mb-4">
                        <label class="form-label small fw-semibold text-dark">Store Description & Value Proposition</label>
                        <textarea name="storeDescription" class="form-control" rows="3" placeholder="Tell buyers about your quality standards, distribution warranty, and heritage..."></textarea>
                    </div>

                    <button type="submit" class="btn btn-warning w-100 py-3 fw-bold fs-6 shadow-sm">
                        Register Store & Access Merchant Central
                    </button>
                </form>
            </div>
        </c:when>

        <%-- Active Merchant Dashboard --%>
        <c:otherwise>
            <!-- Dashboard Header -->
            <div class="d-flex flex-wrap align-items-center justify-content-between mb-4 border-bottom pb-3">
                <div>
                    <span class="badge bg-success mb-1"><i class="bi bi-patch-check-fill me-1"></i>Verified Merchant</span>
                    <h2 class="fw-bold mb-0 text-dark">${seller.storeName}</h2>
                    <span class="text-muted small">GSTIN: <strong>${seller.gstNumber}</strong> &bull; Merchant Central Dashboard</span>
                </div>
                <div class="d-flex gap-2 mt-3 mt-sm-0">
                    <a href="${pageContext.request.contextPath}/seller/product/add" class="btn btn-warning fw-bold">
                        <i class="bi bi-plus-circle me-1"></i> Add New Product
                    </a>
                </div>
            </div>

            <!-- Revenue Analytics Metric Cards -->
            <div class="row g-3 mb-4">
                <div class="col-sm-6 col-xl-3">
                    <div class="card p-3 border-0 shadow-sm bg-white rounded">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small text-uppercase fw-semibold">Total Revenue (INR)</span>
                                <h3 class="fw-bold text-dark mt-1 mb-0">₹<fmt:formatNumber value="${totalRevenue}" pattern="#,##,###"/></h3>
                            </div>
                            <div class="rounded-circle bg-warning bg-opacity-25 p-3 text-warning">
                                <i class="bi bi-currency-rupee fs-3"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 col-xl-3">
                    <div class="card p-3 border-0 shadow-sm bg-white rounded">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small text-uppercase fw-semibold">Incoming Orders</span>
                                <h3 class="fw-bold text-dark mt-1 mb-0">${totalOrders}</h3>
                            </div>
                            <div class="rounded-circle bg-primary bg-opacity-10 p-3 text-primary">
                                <i class="bi bi-box-seam fs-3"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 col-xl-3">
                    <div class="card p-3 border-0 shadow-sm bg-white rounded">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small text-uppercase fw-semibold">Catalog Inventory</span>
                                <h3 class="fw-bold text-dark mt-1 mb-0">${totalProducts}</h3>
                            </div>
                            <div class="rounded-circle bg-success bg-opacity-10 p-3 text-success">
                                <i class="bi bi-tags fs-3"></i>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-sm-6 col-xl-3">
                    <div class="card p-3 border-0 shadow-sm bg-white rounded">
                        <div class="d-flex align-items-center justify-content-between">
                            <div>
                                <span class="text-muted small text-uppercase fw-semibold">Seller Rating</span>
                                <h3 class="fw-bold text-dark mt-1 mb-0">${seller.rating} ★</h3>
                            </div>
                            <div class="rounded-circle bg-info bg-opacity-10 p-3 text-info">
                                <i class="bi bi-award fs-3"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Tabbed Navigation: Products vs Orders -->
            <ul class="nav nav-tabs mb-4 border-bottom" id="sellerTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active fw-bold text-dark" id="products-tab" data-bs-toggle="tab" data-bs-target="#productsPane" type="button" role="tab">
                        <i class="bi bi-box me-1"></i> Inventory Catalog (${products.size()})
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link fw-bold text-dark" id="orders-tab" data-bs-toggle="tab" data-bs-target="#ordersPane" type="button" role="tab">
                        <i class="bi bi-cart-check me-1"></i> Incoming Customer Orders (${orderItems.size()})
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="sellerTabContent">
                
                <!-- Products Pane -->
                <div class="tab-pane fade show active" id="productsPane" role="tabpanel">
                    <div class="card border-0 shadow-sm rounded p-3 bg-white">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="fw-bold mb-0">Your Listed Products</h5>
                            <a href="${pageContext.request.contextPath}/seller/product/add" class="btn btn-sm btn-outline-warning text-dark fw-semibold">
                                + Add Product
                            </a>
                        </div>

                        <div class="table-responsive">
                            <table class="table table-hover align-middle small mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th style="width: 50px;">Img</th>
                                        <th>Title & Category</th>
                                        <th>Price (₹)</th>
                                        <th>Stock</th>
                                        <th>Status</th>
                                        <th>Rating</th>
                                        <th class="text-end">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="p" items="${products}">
                                        <tr>
                                            <td>
                                                <img src="${p.mainImage}" alt="${p.name}" class="rounded" style="width: 44px; height: 44px; object-fit: contain;">
                                            </td>
                                            <td>
                                                <div class="fw-bold text-dark">${p.name}</div>
                                                <span class="text-muted">${p.categoryName} &bull; ${p.brand}</span>
                                            </td>
                                            <td>
                                                <strong class="text-danger">₹<fmt:formatNumber value="${p.price}" pattern="#,##,###"/></strong>
                                                <div class="text-muted small text-decoration-line-through">₹<fmt:formatNumber value="${p.originalPrice}" pattern="#,##,###"/></div>
                                            </td>
                                            <td>
                                                <span class="badge ${p.stockQuantity > 5 ? 'bg-success' : 'bg-warning text-dark'}">
                                                    ${p.stockQuantity} units
                                                </span>
                                            </td>
                                            <td>
                                                <form action="${pageContext.request.contextPath}/seller/product/toggle-status" method="post" class="d-inline">
                                                    <input type="hidden" name="id" value="${p.id}">
                                                    <input type="hidden" name="active" value="${!p.active}">
                                                    <button type="submit" class="btn btn-sm ${p.active ? 'btn-success' : 'btn-secondary'} py-0 px-2" style="font-size: 11px;">
                                                        ${p.active ? 'Active' : 'Paused'}
                                                    </button>
                                                </form>
                                            </td>
                                            <td>
                                                <span class="text-warning">★ ${p.rating}</span> <span class="text-muted">(${p.ratingCount})</span>
                                            </td>
                                            <td class="text-end">
                                                <a href="${pageContext.request.contextPath}/product?id=${p.id}" target="_blank" class="btn btn-sm btn-outline-secondary py-1" title="View in Shop">
                                                    <i class="bi bi-box-arrow-up-right"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/seller/product/edit?id=${p.id}" class="btn btn-sm btn-outline-primary py-1" title="Edit">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <form action="${pageContext.request.contextPath}/seller/product/delete" method="post" class="d-inline" onsubmit="return confirm('Are you sure you want to delete this product?');">
                                                    <input type="hidden" name="id" value="${p.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger py-1" title="Delete">
                                                        <i class="bi bi-trash3"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Orders Pane -->
                <div class="tab-pane fade" id="ordersPane" role="tabpanel">
                    <div class="card border-0 shadow-sm rounded p-3 bg-white">
                        <h5 class="fw-bold mb-3">Incoming Orders for Your Products</h5>
                        
                        <c:choose>
                            <c:when test="${empty orderItems}">
                                <div class="text-center py-4 text-muted small">
                                    No orders received yet. Once customers purchase your items, order lines appear here.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle small mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>Line ID</th>
                                                <th>Product</th>
                                                <th>Qty</th>
                                                <th>Total (₹)</th>
                                                <th>Fulfillment Status</th>
                                                <th class="text-end">Update</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="oi" items="${orderItems}">
                                                <tr>
                                                    <td>#LINE-${oi.id}</td>
                                                    <td>
                                                        <div class="d-flex align-items-center gap-2">
                                                            <img src="${oi.productImage}" alt="${oi.productName}" class="rounded" style="width: 36px; height: 36px; object-fit: contain;">
                                                            <div>
                                                                <strong class="text-dark">${oi.productName}</strong>
                                                                <div class="text-muted small">₹<fmt:formatNumber value="${oi.unitPrice}" pattern="#,##,###"/> each</div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td>${oi.quantity}</td>
                                                    <td class="fw-bold text-dark">₹<fmt:formatNumber value="${oi.totalPrice}" pattern="#,##,###"/></td>
                                                    <td>
                                                        <form action="${pageContext.request.contextPath}/seller/order/update-status" method="post" class="d-flex align-items-center gap-2">
                                                            <input type="hidden" name="orderItemId" value="${oi.id}">
                                                            <select name="status" class="form-select form-select-sm" style="width: 130px;">
                                                                <option value="PROCESSING" ${oi.status == 'PROCESSING' ? 'selected' : ''}>Processing</option>
                                                                <option value="SHIPPED" ${oi.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                                                <option value="DELIVERED" ${oi.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                                                <option value="CANCELLED" ${oi.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                                            </select>
                                                            <button type="submit" class="btn btn-sm btn-outline-dark">Save</button>
                                                        </form>
                                                    </td>
                                                    <td class="text-end">
                                                        <span class="badge ${oi.status == 'DELIVERED' ? 'bg-success' : 'bg-warning text-dark'}">${oi.status}</span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>
                </div>

            </div>

        </c:otherwise>
    </c:choose>

</div>

<jsp:include page="common/footer.jsp"/>
