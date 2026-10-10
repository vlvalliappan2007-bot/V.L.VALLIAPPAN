<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle != null ? pageTitle : "Valliappan Mart: Online Shopping in India - Low Prices on Electronics, Fashion & More"}</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Valliappan Mart Amazon Theme CSS -->
    <link href="${pageContext.request.contextPath}/static/css/amazon-theme.css" rel="stylesheet">
    <script>
        window.APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
    </script>
</head>
<body>

<!-- Amazon Primary Header (Navy #131921) -->
<header class="vm-navbar-main">
    <div class="container-fluid d-flex align-items-center justify-content-between p-0">
        
        <!-- Brand Logo -->
        <a href="${pageContext.request.contextPath}/shop" class="vm-logo-container">
            <div class="vm-logo-text">
                <span class="brand-part-1">Valliappan</span>
                <span class="brand-part-2">Mart</span>
            </div>
            <svg class="vm-logo-smile-svg" viewBox="0 0 120 18" fill="none">
                <path d="M5 5C38 18 82 18 115 5" stroke="#FF9900" stroke-width="3" stroke-linecap="round"/>
                <path d="M106 3L116 5L111 14" fill="#FF9900"/>
            </svg>
        </a>

        <!-- Delivery Location Indicator -->
        <div class="vm-nav-item d-none d-lg-flex ms-2 me-1">
            <div class="d-flex align-items-center">
                <i class="bi bi-geo-alt fs-5 me-1 text-white"></i>
                <div>
                    <span class="vm-nav-line-1 d-block">Delivering to India</span>
                    <span class="vm-nav-line-2">Chennai 600020</span>
                </div>
            </div>
        </div>

        <!-- Amazon-style Search Header -->
        <form action="${pageContext.request.contextPath}/search" method="get" class="vm-search-group">
            <select name="category" id="vmSearchCategory" class="vm-search-category-select">
                <option value="">All Categories</option>
                <c:forEach var="cat" items="${globalCategories}">
                    <option value="${cat.id}" ${param.category == cat.id ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>
            <input type="text" name="q" id="vmSearchInput" class="vm-search-input" 
                   placeholder="Search Valliappan Mart (e.g. 5G smartphone, headphones, coffee machine)" 
                   value="${param.q}" autocomplete="off">
            <button type="submit" class="vm-search-btn" title="Search">
                <i class="bi bi-search"></i>
            </button>
            <!-- Live Auto-suggest Dropdown -->
            <div id="vmSuggestBox" class="vm-autosuggest-box"></div>
        </form>

        <!-- Right Side Nav Items -->
        <div class="d-flex align-items-center gap-1">
            
            <!-- Currency/Language Flag -->
            <div class="vm-nav-item d-none d-md-flex">
                <span class="vm-nav-line-2">🇮🇳 IN ₹</span>
            </div>

            <!-- Multi-Tier User Account Menu -->
            <div class="dropdown">
                <a href="#" class="vm-nav-item dropdown-toggle text-decoration-none" data-bs-toggle="dropdown" aria-expanded="false">
                    <span class="vm-nav-line-1">
                        <c:choose>
                            <c:when test="${not empty sessionScope.currentUser}">
                                Hello, ${sessionScope.currentUser.name}
                            </c:when>
                            <c:otherwise>
                                Hello, Sign in
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <span class="vm-nav-line-2">Accounts & Lists</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow-lg p-2" style="min-width: 250px;">
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser}">
                            <li class="px-2 py-1 text-muted small">Signed in as <strong>${sessionScope.currentUser.email}</strong></li>
                            <li class="px-2 pb-2"><span class="badge bg-warning text-dark text-uppercase">${sessionScope.currentUser.role} ROLE</span></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile"><i class="bi bi-person-circle me-2"></i> Your Account & Addresses</a></li>
                            <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile#orders"><i class="bi bi-box-seam me-2"></i> Your Orders</a></li>
                            <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/wishlist"><i class="bi bi-heart me-2"></i> Your Wishlist</a></li>
                            
                            <c:if test="${sessionScope.currentUser.seller || sessionScope.currentUser.admin}">
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item py-2 text-primary fw-semibold" href="${pageContext.request.contextPath}/seller"><i class="bi bi-shop me-2"></i> Seller Merchant Dashboard</a></li>
                            </c:if>
                            
                            <c:if test="${sessionScope.currentUser.admin}">
                                <li><a class="dropdown-item py-2 text-danger fw-semibold" href="${pageContext.request.contextPath}/admin"><i class="bi bi-shield-lock me-2"></i> Platform Administration</a></li>
                            </c:if>

                            <c:if test="${sessionScope.currentUser.buyer}">
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item py-2 text-success" href="${pageContext.request.contextPath}/seller/become"><i class="bi bi-briefcase me-2"></i> Become a Seller</a></li>
                            </c:if>

                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item py-2 text-danger" href="${pageContext.request.contextPath}/auth/logout"><i class="bi bi-box-arrow-right me-2"></i> Sign Out</a></li>
                        </c:when>
                        <c:otherwise>
                            <li class="p-2 text-center">
                                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-add-to-cart w-100 py-1 mb-2">Sign in</a>
                                <span class="small text-muted">New customer? <a href="${pageContext.request.contextPath}/auth/register" class="text-primary text-decoration-none">Start here.</a></span>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item py-2" href="${pageContext.request.contextPath}/seller/become"><i class="bi bi-shop me-2"></i> Start Selling on Valliappan Mart</a></li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>

            <!-- Returns & Orders -->
            <a href="${pageContext.request.contextPath}/profile" class="vm-nav-item d-none d-sm-flex">
                <span class="vm-nav-line-1">Returns</span>
                <span class="vm-nav-line-2">& Orders</span>
            </a>

            <!-- Wishlist with Counter Badge -->
            <a href="${pageContext.request.contextPath}/wishlist" class="vm-nav-item position-relative d-none d-md-flex me-2" title="Wishlist">
                <i class="bi bi-heart fs-4 text-white"></i>
                <span id="headerWishlistBadge" class="vm-badge-counter">0</span>
            </a>

            <!-- Interactive Cart with Counter Badge -->
            <a href="${pageContext.request.contextPath}/cart" class="vm-nav-item position-relative d-flex flex-row align-items-center gap-1" title="Shopping Cart">
                <div class="position-relative">
                    <i class="bi bi-cart3 fs-3 text-white"></i>
                    <span id="headerCartBadge" class="vm-badge-counter">0</span>
                </div>
                <span class="vm-nav-line-2 align-self-end mb-1 d-none d-md-inline">Cart</span>
            </a>

        </div>
    </div>
</header>

<!-- Amazon Secondary Sub-Navbar (Navy #232F3E) -->
<nav class="vm-navbar-sub">
    <div class="container-fluid d-flex align-items-center gap-2 overflow-x-auto p-0">
        <a href="${pageContext.request.contextPath}/shop" class="vm-subnav-link fw-bold text-nowrap">
            <i class="bi bi-list me-1"></i> All
        </a>
        <c:forEach var="c" items="${globalCategories}">
            <a href="${pageContext.request.contextPath}/shop?category=${c.id}" class="vm-subnav-link text-nowrap">
                ${c.name}
            </a>
        </c:forEach>
        <span class="text-white-50 ms-auto d-none d-lg-inline">|</span>
        <a href="${pageContext.request.contextPath}/seller/become" class="vm-subnav-link text-warning fw-semibold text-nowrap d-none d-md-inline">
            <i class="bi bi-shop me-1"></i> Become a Seller
        </a>
        <a href="${pageContext.request.contextPath}/shop?sortBy=rating" class="vm-subnav-link text-nowrap d-none d-lg-inline">
            Amazon Bestsellers
        </a>
    </div>
</nav>

<script>
    // Fetch live badges on page load
    document.addEventListener("DOMContentLoaded", function() {
        const ctx = "${pageContext.request.contextPath}";
        fetch(ctx + "/cart/count")
            .then(r => r.json())
            .then(d => { if(d && d.count !== undefined) updateCartBadge(d.count); })
            .catch(e => {});

        <c:if test="${not empty sessionScope.currentUser}">
        fetch(ctx + "/wishlist/count")
            .then(r => r.json())
            .then(d => { if(d && d.count !== undefined) updateWishlistBadge(d.count); })
            .catch(e => {});
        </c:if>
    });
</script>
