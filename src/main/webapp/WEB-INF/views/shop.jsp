<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Valliappan Mart: Deals on Electronics, Fashion, Home & Kitchen Essentials"/>
</jsp:include>

<!-- Amazon Hero Banner (Shown when not filtering) -->
<c:if test="${empty param.q && empty param.category && empty param.minPrice}">
    <div class="vm-hero-banner">
        <div class="container vm-hero-overlay-content">
            <div class="row align-items-center">
                <div class="col-lg-7 text-white">
                    <span class="badge bg-warning text-dark px-3 py-2 fw-bold text-uppercase mb-2">Great Indian Festival Deals</span>
                    <h1 class="display-5 fw-bold mb-3">Upgrade Your Lifestyle at Valliappan Mart</h1>
                    <p class="fs-5 opacity-90 mb-4">India's trusted multi-vendor destination for 5G smartphones, fashion, barista coffee makers, and fitness gear.</p>
                    <div class="d-flex gap-2">
                        <a href="#catalogSection" class="btn btn-warning px-4 py-2 fw-bold text-dark rounded-pill shadow">Explore Catalog</a>
                        <a href="${pageContext.request.contextPath}/seller/become" class="btn btn-outline-light px-4 py-2 fw-semibold rounded-pill">Sell With Us</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>

<div class="container-fluid px-lg-4 py-4" id="catalogSection">
    
    <!-- Breadcrumbs / Results Info -->
    <div class="d-flex flex-wrap align-items-center justify-content-between mb-3 bg-white p-3 rounded shadow-sm">
        <div>
            <c:choose>
                <c:when test="${not empty param.q}">
                    <span class="text-muted">Results for </span><strong class="text-dark">"${param.q}"</strong>
                </c:when>
                <c:when test="${not empty currentCategoryId}">
                    <span class="text-muted">Category: </span>
                    <strong class="text-dark">
                        <c:forEach var="c" items="${categories}">
                            <c:if test="${c.id == currentCategoryId}">${c.name}</c:if>
                        </c:forEach>
                    </strong>
                </c:when>
                <c:otherwise>
                    <span class="text-muted">Showing all </span><strong class="text-dark">${products.size()} products</strong>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Sort Filter -->
        <div class="d-flex align-items-center gap-2">
            <label for="sortBySelect" class="small text-muted text-nowrap">Sort by:</label>
            <select id="sortBySelect" class="form-select form-select-sm" style="width: auto;" 
                    onchange="location.href='${pageContext.request.contextPath}/shop?q=${param.q}&category=${currentCategoryId}&minPrice=${currentMinPrice}&maxPrice=${currentMaxPrice}&sortBy=' + this.value;">
                <option value="" ${empty currentSortBy ? 'selected' : ''}>Featured</option>
                <option value="price_asc" ${currentSortBy == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                <option value="price_desc" ${currentSortBy == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                <option value="rating" ${currentSortBy == 'rating' ? 'selected' : ''}>Avg. Customer Review</option>
            </select>
        </div>
    </div>

    <div class="row g-4">
        <!-- Sidebar Filters -->
        <div class="col-lg-3 col-md-4">
            <div class="bg-white p-3 rounded shadow-sm mb-4">
                <h6 class="fw-bold mb-3 text-dark border-bottom pb-2">Department</h6>
                <ul class="list-unstyled mb-4">
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/shop" class="text-decoration-none ${empty currentCategoryId ? 'fw-bold text-warning' : 'text-dark'}">
                            All Categories
                        </a>
                    </li>
                    <c:forEach var="cat" items="${categories}">
                        <li class="mb-2 d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/shop?category=${cat.id}" 
                               class="text-decoration-none ${currentCategoryId == cat.id ? 'fw-bold text-warning' : 'text-dark'}">
                                ${cat.name}
                            </a>
                            <span class="badge bg-light text-muted">${cat.productCount}</span>
                        </li>
                    </c:forEach>
                </ul>

                <h6 class="fw-bold mb-3 text-dark border-bottom pb-2">Price (₹)</h6>
                <ul class="list-unstyled mb-3 small">
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/shop?category=${currentCategoryId}&maxPrice=1000" class="text-dark text-decoration-none">Under ₹1,000</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/shop?category=${currentCategoryId}&minPrice=1000&maxPrice=5000" class="text-dark text-decoration-none">₹1,000 - ₹5,000</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/shop?category=${currentCategoryId}&minPrice=5000&maxPrice=20000" class="text-dark text-decoration-none">₹5,000 - ₹20,000</a></li>
                    <li class="mb-2"><a href="${pageContext.request.contextPath}/shop?category=${currentCategoryId}&minPrice=20000" class="text-dark text-decoration-none">Over ₹20,000</a></li>
                </ul>

                <!-- Custom Price Filter Form -->
                <form action="${pageContext.request.contextPath}/shop" method="get" class="d-flex align-items-center gap-2">
                    <input type="hidden" name="category" value="${currentCategoryId}">
                    <input type="hidden" name="q" value="${param.q}">
                    <input type="number" name="minPrice" placeholder="₹ Min" class="form-control form-control-sm" value="${currentMinPrice}" style="width: 75px;">
                    <span>-</span>
                    <input type="number" name="maxPrice" placeholder="₹ Max" class="form-control form-control-sm" value="${currentMaxPrice}" style="width: 75px;">
                    <button type="submit" class="btn btn-sm btn-outline-dark">Go</button>
                </form>

                <h6 class="fw-bold mt-4 mb-3 text-dark border-bottom pb-2">Customer Reviews</h6>
                <ul class="list-unstyled mb-0 small">
                    <li class="mb-2">
                        <a href="${pageContext.request.contextPath}/shop?sortBy=rating" class="text-dark text-decoration-none d-flex align-items-center">
                            <span class="vm-stars me-2"><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star"></i></span> & Up
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- Products Grid -->
        <div class="col-lg-9 col-md-8">
            <c:choose>
                <c:when test="${empty products}">
                    <div class="bg-white p-5 rounded text-center shadow-sm">
                        <i class="bi bi-search fs-1 text-muted mb-3 d-block"></i>
                        <h4 class="fw-bold">No results found</h4>
                        <p class="text-muted">Try checking your spelling or use more general terms.</p>
                        <a href="${pageContext.request.contextPath}/shop" class="btn btn-add-to-cart w-auto px-4 mt-2">View All Products</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="row row-cols-1 row-cols-sm-2 row-cols-xl-3 g-3">
                        <c:forEach var="p" items="${products}">
                            <div class="col">
                                <div class="vm-product-card">
                                    
                                    <!-- Image Preview Box -->
                                    <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="vm-product-img-box text-decoration-none">
                                        <img src="${p.mainImage}" alt="${p.name}" class="vm-product-img" loading="lazy">
                                    </a>

                                    <!-- Product Info -->
                                    <div class="d-flex flex-column flex-grow-1">
                                        <div class="d-flex justify-content-between align-items-center mb-1">
                                            <span class="text-muted small">${p.brand != null ? p.brand : 'Valliappan Mart'}</span>
                                            <button type="button" class="btn btn-sm text-secondary p-0" title="Add to Wishlist" onclick="toggleWishlistAjax(${p.id}, this)">
                                                <i class="bi bi-heart"></i>
                                            </button>
                                        </div>

                                        <h6 class="fw-semibold mb-1 text-truncate-2" style="height: 42px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">
                                            <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="text-dark text-decoration-none">
                                                ${p.name}
                                            </a>
                                        </h6>

                                        <!-- Rating -->
                                        <div class="d-flex align-items-center mb-2">
                                            <span class="vm-stars small me-1">
                                                <i class="bi bi-star-fill"></i>
                                                <i class="bi bi-star-fill"></i>
                                                <i class="bi bi-star-fill"></i>
                                                <i class="bi bi-star-fill"></i>
                                                <i class="bi bi-star-half"></i>
                                            </span>
                                            <span class="small text-muted">(${p.ratingCount})</span>
                                        </div>

                                        <!-- Pricing in INR ₹ -->
                                        <div class="d-flex align-items-baseline mb-1">
                                            <span class="vm-price-symbol">₹</span>
                                            <span class="vm-price-whole"><fmt:formatNumber value="${p.price}" pattern="#,##,###"/></span>
                                            <c:if test="${p.originalPrice > p.price}">
                                                <span class="vm-price-strike">₹<fmt:formatNumber value="${p.originalPrice}" pattern="#,##,###"/></span>
                                                <span class="vm-discount-badge">-${p.discountPercentage}%</span>
                                            </c:if>
                                        </div>

                                        <!-- Prime delivery & Stock badge -->
                                        <div class="vm-prime-delivery mb-2">
                                            <i class="bi bi-truck me-1"></i> FREE Delivery by <strong>Tomorrow</strong>
                                        </div>
                                        <div class="${p.stockBadgeClass} small mb-3">
                                            ${p.stockBadgeText}
                                        </div>

                                        <!-- Dual Action Buttons -->
                                        <div class="mt-auto d-flex flex-column gap-2">
                                            <button type="button" class="btn btn-add-to-cart py-2" onclick="addToCartAjax(${p.id}, 1)">
                                                Add to Cart
                                            </button>
                                            <button type="button" class="btn btn-buy-now py-2" onclick="addToCartAjax(${p.id}, 1, 'checkout')">
                                                Buy Now
                                            </button>
                                        </div>

                                    </div>
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
