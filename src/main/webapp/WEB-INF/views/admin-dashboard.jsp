<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Platform Administration - Valliappan Mart"/>
</jsp:include>

<div class="container-fluid px-lg-5 py-4">
    
    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <div>
            <span class="badge bg-danger text-uppercase px-3 py-1 mb-1">Administrative Control Center</span>
            <h2 class="fw-bold mb-0 text-dark">Platform Governance & Analytics</h2>
            <span class="text-muted small">Multi-vendor marketplace oversight, user moderation, merchant verification, and category configuration</span>
        </div>
    </div>

    <!-- Platform Revenue & System Analytics KPIs -->
    <div class="row g-3 mb-4">
        <div class="col-sm-6 col-xl-3">
            <div class="card p-3 border-0 shadow-sm bg-white rounded">
                <div class="d-flex align-items-center justify-content-between">
                    <div>
                        <span class="text-muted small text-uppercase fw-semibold">Platform GMV (Revenue)</span>
                        <h3 class="fw-bold text-dark mt-1 mb-0">₹<fmt:formatNumber value="${totalRevenue}" pattern="#,##,###"/></h3>
                    </div>
                    <div class="rounded-circle bg-warning bg-opacity-25 p-3 text-warning">
                        <i class="bi bi-wallet2 fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="card p-3 border-0 shadow-sm bg-white rounded">
                <div class="d-flex align-items-center justify-content-between">
                    <div>
                        <span class="text-muted small text-uppercase fw-semibold">Total Orders Processed</span>
                        <h3 class="fw-bold text-dark mt-1 mb-0">${totalOrders}</h3>
                    </div>
                    <div class="rounded-circle bg-primary bg-opacity-10 p-3 text-primary">
                        <i class="bi bi-cart-check fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="card p-3 border-0 shadow-sm bg-white rounded">
                <div class="d-flex align-items-center justify-content-between">
                    <div>
                        <span class="text-muted small text-uppercase fw-semibold">Registered Users</span>
                        <h3 class="fw-bold text-dark mt-1 mb-0">${totalUsers}</h3>
                    </div>
                    <div class="rounded-circle bg-info bg-opacity-10 p-3 text-info">
                        <i class="bi bi-people fs-3"></i>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-sm-6 col-xl-3">
            <div class="card p-3 border-0 shadow-sm bg-white rounded">
                <div class="d-flex align-items-center justify-content-between">
                    <div>
                        <span class="text-muted small text-uppercase fw-semibold">Live Products</span>
                        <h3 class="fw-bold text-dark mt-1 mb-0">${totalProducts}</h3>
                    </div>
                    <div class="rounded-circle bg-success bg-opacity-10 p-3 text-success">
                        <i class="bi bi-grid fs-3"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Administrative Tabs -->
    <ul class="nav nav-tabs mb-4 border-bottom" id="adminTabs" role="tablist">
        <li class="nav-item" role="presentation">
            <button class="nav-link ${empty param.tab || param.tab == 'users' ? 'active' : ''} fw-bold text-dark" id="users-tab" data-bs-toggle="tab" data-bs-target="#usersPane" type="button" role="tab">
                <i class="bi bi-person-gear me-1"></i> User Management (${users.size()})
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link ${param.tab == 'sellers' ? 'active' : ''} fw-bold text-dark" id="sellers-tab" data-bs-toggle="tab" data-bs-target="#sellersPane" type="button" role="tab">
                <i class="bi bi-patch-check me-1"></i> Merchant Verification (${sellers.size()})
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link ${param.tab == 'categories' ? 'active' : ''} fw-bold text-dark" id="categories-tab" data-bs-toggle="tab" data-bs-target="#categoriesPane" type="button" role="tab">
                <i class="bi bi-tags me-1"></i> Category Manager (${categories.size()})
            </button>
        </li>
        <li class="nav-item" role="presentation">
            <button class="nav-link ${param.tab == 'products' ? 'active' : ''} fw-bold text-dark" id="products-tab" data-bs-toggle="tab" data-bs-target="#productsPane" type="button" role="tab">
                <i class="bi bi-shield-check me-1"></i> Catalog Moderation (${products.size()})
            </button>
        </li>
    </ul>

    <div class="tab-content" id="adminTabContent">
        
        <!-- 1. User Management -->
        <div class="tab-pane fade ${empty param.tab || param.tab == 'users' ? 'show active' : ''}" id="usersPane" role="tabpanel">
            <div class="card border-0 shadow-sm rounded p-3 bg-white">
                <h5 class="fw-bold mb-3">All System Users</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle small mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>#ID</th>
                                <th>Name & Email</th>
                                <th>Phone</th>
                                <th>Current Role</th>
                                <th>Status</th>
                                <th>Change Role</th>
                                <th class="text-end">Account Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td>#${u.id}</td>
                                    <td>
                                        <strong>${u.name}</strong>
                                        <div class="text-muted small">${u.email}</div>
                                    </td>
                                    <td>${u.phone}</td>
                                    <td>
                                        <span class="badge ${u.admin ? 'bg-danger' : (u.seller ? 'bg-warning text-dark' : 'bg-secondary')}">
                                            ${u.role}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${u.status == 'ACTIVE' ? 'bg-success' : 'bg-danger'}">
                                            ${u.status}
                                        </span>
                                    </td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/admin/user/role" method="post" class="d-flex align-items-center gap-1">
                                            <input type="hidden" name="userId" value="${u.id}">
                                            <select name="role" class="form-select form-select-sm" style="width: 100px;">
                                                <option value="BUYER" ${u.role == 'BUYER' ? 'selected' : ''}>BUYER</option>
                                                <option value="SELLER" ${u.role == 'SELLER' ? 'selected' : ''}>SELLER</option>
                                                <option value="ADMIN" ${u.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-outline-dark">Set</button>
                                        </form>
                                    </td>
                                    <td class="text-end">
                                        <form action="${pageContext.request.contextPath}/admin/user/status" method="post" class="d-inline">
                                            <input type="hidden" name="userId" value="${u.id}">
                                            <input type="hidden" name="status" value="${u.status == 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'}">
                                            <button type="submit" class="btn btn-sm ${u.status == 'ACTIVE' ? 'btn-outline-danger' : 'btn-outline-success'} py-1 px-2" style="font-size: 11px;">
                                                ${u.status == 'ACTIVE' ? 'Suspend' : 'Activate'}
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

        <!-- 2. Merchant Verification -->
        <div class="tab-pane fade ${param.tab == 'sellers' ? 'show active' : ''}" id="sellersPane" role="tabpanel">
            <div class="card border-0 shadow-sm rounded p-3 bg-white">
                <h5 class="fw-bold mb-3">Merchant Verification & Approvals</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle small mb-0">
                        <thead class="table-light">
                            <tr>
                                <th>#ID</th>
                                <th>Store Name</th>
                                <th>Owner</th>
                                <th>Business Type</th>
                                <th>GSTIN</th>
                                <th>Rating</th>
                                <th>Status</th>
                                <th class="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${sellers}">
                                <tr>
                                    <td>#${s.id}</td>
                                    <td><strong class="text-dark">${s.storeName}</strong></td>
                                    <td>${s.ownerName} (${s.ownerEmail})</td>
                                    <td>${s.businessType}</td>
                                    <td><code>${s.gstNumber}</code></td>
                                    <td>★ ${s.rating}</td>
                                    <td>
                                        <span class="badge ${s.verified ? 'bg-success' : 'bg-warning text-dark'}">
                                            ${s.verified ? 'Verified Partner' : 'Pending Review'}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <form action="${pageContext.request.contextPath}/admin/seller/verify" method="post" class="d-inline">
                                            <input type="hidden" name="sellerId" value="${s.id}">
                                            <input type="hidden" name="verified" value="${!s.verified}">
                                            <button type="submit" class="btn btn-sm ${s.verified ? 'btn-outline-warning text-dark' : 'btn-success'} py-1 px-3">
                                                ${s.verified ? 'Revoke Approval' : 'Approve Merchant'}
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

        <!-- 3. Category Manager -->
        <div class="tab-pane fade ${param.tab == 'categories' ? 'show active' : ''}" id="categoriesPane" role="tabpanel">
            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm rounded p-3 bg-white">
                        <h6 class="fw-bold mb-3">Add New Category</h6>
                        <form action="${pageContext.request.contextPath}/admin/category/add" method="post">
                            <div class="mb-2">
                                <label class="form-label small fw-semibold">Category Name</label>
                                <input type="text" name="name" class="form-control form-control-sm" placeholder="e.g. Beauty & Personal Care" required>
                            </div>
                            <div class="mb-2">
                                <label class="form-label small fw-semibold">Slug (Optional)</label>
                                <input type="text" name="slug" class="form-control form-control-sm" placeholder="beauty-care">
                            </div>
                            <div class="mb-2">
                                <label class="form-label small fw-semibold">Icon Class</label>
                                <input type="text" name="iconClass" class="form-control form-control-sm" value="bi-stars" placeholder="bi-laptop, bi-bag, etc.">
                            </div>
                            <div class="mb-2">
                                <label class="form-label small fw-semibold">Banner Image URL</label>
                                <input type="url" name="bannerImage" class="form-control form-control-sm" placeholder="https://images.unsplash.com/...">
                            </div>
                            <div class="mb-3">
                                <label class="form-label small fw-semibold">Description</label>
                                <textarea name="description" class="form-control form-control-sm" rows="2"></textarea>
                            </div>
                            <button type="submit" class="btn btn-warning btn-sm w-100 fw-bold">Create Category</button>
                        </form>
                    </div>
                </div>

                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm rounded p-3 bg-white">
                        <h6 class="fw-bold mb-3">Existing Categories</h6>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle small mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>#ID</th>
                                        <th>Name</th>
                                        <th>Slug</th>
                                        <th>Products</th>
                                        <th class="text-end">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${categories}">
                                        <tr>
                                            <td>#${c.id}</td>
                                            <td><strong>${c.name}</strong></td>
                                            <td><code>${c.slug}</code></td>
                                            <td><span class="badge bg-secondary">${c.productCount} items</span></td>
                                            <td class="text-end">
                                                <form action="${pageContext.request.contextPath}/admin/category/delete" method="post" class="d-inline" onsubmit="return confirm('Delete this category?');">
                                                    <input type="hidden" name="id" value="${c.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-danger py-0 px-2" style="font-size: 11px;">
                                                        Delete
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
            </div>
        </div>

        <!-- 4. Platform Catalog Moderation -->
        <div class="tab-pane fade ${param.tab == 'products' ? 'show active' : ''}" id="productsPane" role="tabpanel">
            <div class="card border-0 shadow-sm rounded p-3 bg-white">
                <h5 class="fw-bold mb-3">Catalog Moderation (All Vendors)</h5>
                <div class="table-responsive">
                    <table class="table table-hover align-middle small mb-0">
                        <thead class="table-light">
                            <tr>
                                <th style="width: 44px;">Img</th>
                                <th>Product Name</th>
                                <th>Vendor / Store</th>
                                <th>Price (₹)</th>
                                <th>Stock</th>
                                <th>Visibility</th>
                                <th class="text-end">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${products}">
                                <tr>
                                    <td>
                                        <img src="${p.mainImage}" alt="${p.name}" class="rounded" style="width: 38px; height: 38px; object-fit: contain;">
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" target="_blank" class="text-dark fw-semibold text-decoration-none">
                                            ${p.name}
                                        </a>
                                        <div class="text-muted small">${p.categoryName}</div>
                                    </td>
                                    <td><span class="badge bg-light text-dark border">${p.storeName}</span></td>
                                    <td class="fw-bold text-danger">₹<fmt:formatNumber value="${p.price}" pattern="#,##,###"/></td>
                                    <td>${p.stockQuantity}</td>
                                    <td>
                                        <span class="badge ${p.active ? 'bg-success' : 'bg-secondary'}">
                                            ${p.active ? 'Visible' : 'Hidden'}
                                        </span>
                                    </td>
                                    <td class="text-end">
                                        <form action="${pageContext.request.contextPath}/admin/product/toggle" method="post" class="d-inline">
                                            <input type="hidden" name="id" value="${p.id}">
                                            <input type="hidden" name="active" value="${!p.active}">
                                            <button type="submit" class="btn btn-sm ${p.active ? 'btn-outline-danger' : 'btn-outline-success'} py-0 px-2" style="font-size: 11px;">
                                                ${p.active ? 'Unpublish' : 'Publish'}
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

    </div>

</div>

<jsp:include page="common/footer.jsp"/>
