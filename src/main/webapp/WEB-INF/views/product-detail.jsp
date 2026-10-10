<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="${product.name} - Buy Online at Valliappan Mart"/>
</jsp:include>

<div class="container-fluid px-lg-5 py-4">
    <!-- Breadcrumbs -->
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb small">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/shop" class="text-decoration-none text-muted">Home</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/shop?category=${product.categoryId}" class="text-decoration-none text-muted">${product.categoryName}</a></li>
            <li class="breadcrumb-item active text-truncate" aria-current="page" style="max-width: 300px;">${product.name}</li>
        </ol>
    </nav>

    <div class="row g-4 bg-white p-4 rounded shadow-sm">
        
        <!-- Left: Image Gallery & Zoom Layout -->
        <div class="col-lg-5 col-md-6">
            <div class="d-flex gap-3">
                <!-- Thumbnail navigation -->
                <div class="d-flex flex-column">
                    <img src="${product.mainImage}" alt="Thumb Main" class="vm-gallery-thumb active" data-full="${product.mainImage}">
                    <c:if test="${not empty product.subImage1}">
                        <img src="${product.subImage1}" alt="Thumb 1" class="vm-gallery-thumb" data-full="${product.subImage1}">
                    </c:if>
                    <c:if test="${not empty product.subImage2}">
                        <img src="${product.subImage2}" alt="Thumb 2" class="vm-gallery-thumb" data-full="${product.subImage2}">
                    </c:if>
                </div>

                <!-- Main Showcase Image -->
                <div class="flex-grow-1 text-center position-relative border rounded p-3" style="min-height: 440px; display: flex; align-items: center; justify-content: center; background: #FAFAFA;">
                    <img id="mainProductImage" src="${product.mainImage}" alt="${product.name}" class="vm-gallery-main shadow-sm">
                </div>
            </div>
        </div>

        <!-- Middle: Product Specifications & Details -->
        <div class="col-lg-4 col-md-6 border-end-lg">
            <div class="text-muted small mb-1">Brand: <strong class="text-primary">${product.brand != null ? product.brand : 'Valliappan Originals'}</strong></div>
            <h4 class="fw-bold text-dark mb-2">${product.name}</h4>

            <!-- Star Rating breakdown header -->
            <div class="d-flex align-items-center mb-3">
                <span class="vm-stars me-2">
                    <i class="bi bi-star-fill"></i>
                    <i class="bi bi-star-fill"></i>
                    <i class="bi bi-star-fill"></i>
                    <i class="bi bi-star-fill"></i>
                    <i class="bi bi-star-half"></i>
                </span>
                <a href="#reviewsSection" class="text-decoration-none text-primary small me-2">${product.rating} out of 5 stars</a>
                <span class="text-muted small">&bull; ${product.ratingCount} ratings</span>
            </div>

            <hr class="my-3">

            <!-- Pricing Section in INR ₹ -->
            <div class="mb-3">
                <div class="d-flex align-items-baseline">
                    <span class="text-danger fs-4 fw-bold me-2">-${product.discountPercentage}%</span>
                    <span class="fs-5 text-dark fw-bold me-1">₹</span>
                    <span class="display-6 fw-bold text-dark"><fmt:formatNumber value="${product.price}" pattern="#,##,###"/></span>
                </div>
                <div class="text-muted small">
                    M.R.P.: <span class="text-decoration-line-through">₹<fmt:formatNumber value="${product.originalPrice}" pattern="#,##,###"/></span>
                </div>
                <div class="text-success small fw-semibold mt-1">Inclusive of all taxes & GST</div>
            </div>

            <div class="p-3 bg-light rounded mb-3">
                <div class="row text-center g-2 small">
                    <div class="col-4">
                        <i class="bi bi-arrow-repeat fs-4 text-warning d-block mb-1"></i>
                        <span>7 Days Replacement</span>
                    </div>
                    <div class="col-4">
                        <i class="bi bi-truck fs-4 text-warning d-block mb-1"></i>
                        <span>Free Fast Delivery</span>
                    </div>
                    <div class="col-4">
                        <i class="bi bi-shield-check fs-4 text-warning d-block mb-1"></i>
                        <span>1 Year Warranty</span>
                    </div>
                </div>
            </div>

            <!-- Description -->
            <div class="mb-4">
                <h6 class="fw-bold text-dark mb-2">About this item</h6>
                <p class="text-secondary small lh-lg" style="white-space: pre-line;">${product.description}</p>
            </div>

            <!-- Sold by Merchant profile link -->
            <div class="card p-3 border-light bg-light small">
                <div class="d-flex align-items-center justify-content-between">
                    <div>
                        <span class="text-muted">Sold by:</span> 
                        <strong class="text-dark">${product.storeName != null ? product.storeName : 'TechNova Retail Solutions'}</strong>
                        <span class="badge bg-success ms-1"><i class="bi bi-patch-check-fill me-1"></i>Verified Merchant</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Right: Amazon Buy Box -->
        <div class="col-lg-3 col-md-12">
            <div class="card p-4 border rounded shadow-sm">
                
                <div class="fs-4 fw-bold text-dark mb-2">
                    ₹<fmt:formatNumber value="${product.price}" pattern="#,##,###"/>
                </div>

                <div class="vm-prime-delivery mb-2">
                    <i class="bi bi-truck me-1"></i> FREE Delivery <strong>Tomorrow</strong>. Order within 4 hrs.
                </div>

                <div class="small text-muted mb-3">
                    <i class="bi bi-geo-alt me-1"></i> Delivering to <strong>Chennai 600020</strong>
                </div>

                <!-- Stock Availability Badge -->
                <div class="${product.stockBadgeClass} mb-3">
                    ${product.stockBadgeText}
                </div>

                <c:if test="${product.stockQuantity > 0}">
                    <!-- Quantity Selector -->
                    <div class="mb-3">
                        <label for="productQtySelect" class="form-label small fw-semibold text-muted">Quantity:</label>
                        <select id="productQtySelect" class="form-select form-select-sm">
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                        </select>
                    </div>

                    <!-- Dual Action Buttons -->
                    <div class="d-grid gap-2 mb-3">
                        <button type="button" class="btn btn-add-to-cart py-2" 
                                onclick="addToCartAjax(${product.id}, document.getElementById('productQtySelect').value)">
                            Add to Cart
                        </button>
                        <button type="button" class="btn btn-buy-now py-2" 
                                onclick="addToCartAjax(${product.id}, document.getElementById('productQtySelect').value, 'checkout')">
                            Buy Now
                        </button>
                    </div>
                </c:if>

                <div class="d-grid mb-3">
                    <button type="button" class="btn btn-outline-secondary btn-sm py-2" onclick="toggleWishlistAjax(${product.id}, this)">
                        <i class="bi ${isWishlisted ? 'bi-heart-fill text-danger' : 'bi-heart'} me-1"></i> 
                        ${isWishlisted ? 'Wishlisted' : 'Add to Wishlist'}
                    </button>
                </div>

                <div class="small text-muted border-top pt-3">
                    <div class="d-flex justify-content-between mb-1">
                        <span>Payment</span>
                        <span class="text-dark fw-semibold">Secure Transaction</span>
                    </div>
                    <div class="d-flex justify-content-between mb-1">
                        <span>Ships from</span>
                        <span class="text-dark fw-semibold">Valliappan Mart</span>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span>Sold by</span>
                        <span class="text-dark fw-semibold">${product.storeName}</span>
                    </div>
                </div>

            </div>
        </div>

    </div>

    <!-- Related Products in Category -->
    <c:if test="${not empty relatedProducts}">
        <div class="mt-5 bg-white p-4 rounded shadow-sm">
            <h5 class="fw-bold mb-4">Related Products You Might Like</h5>
            <div class="row row-cols-2 row-cols-md-4 g-3">
                <c:forEach var="rp" items="${relatedProducts}">
                    <div class="col">
                        <div class="vm-product-card">
                            <a href="${pageContext.request.contextPath}/product?id=${rp.id}" class="vm-product-img-box text-decoration-none">
                                <img src="${rp.mainImage}" alt="${rp.name}" class="vm-product-img">
                            </a>
                            <div class="small fw-semibold text-truncate mb-1">
                                <a href="${pageContext.request.contextPath}/product?id=${rp.id}" class="text-dark text-decoration-none">${rp.name}</a>
                            </div>
                            <div class="fw-bold text-dark mb-2">₹<fmt:formatNumber value="${rp.price}" pattern="#,##,###"/></div>
                            <button type="button" class="btn btn-add-to-cart btn-sm mt-auto" onclick="addToCartAjax(${rp.id}, 1)">Add to Cart</button>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>

    <!-- Customer Reviews & Rating Breakdown -->
    <div class="mt-5 bg-white p-4 rounded shadow-sm" id="reviewsSection">
        <h5 class="fw-bold mb-4 border-bottom pb-2">Customer Reviews & Ratings</h5>
        
        <div class="row g-4">
            <!-- Left: Breakdown -->
            <div class="col-md-4">
                <div class="d-flex align-items-center gap-2 mb-2">
                    <span class="vm-stars fs-5">
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-half"></i>
                    </span>
                    <span class="fs-4 fw-bold text-dark">${product.rating} out of 5</span>
                </div>
                <div class="text-muted small mb-4">${product.ratingCount} global customer ratings</div>

                <!-- Star percentage bars -->
                <div class="d-flex align-items-center mb-2 small">
                    <span class="me-2" style="width: 45px;">5 star</span>
                    <div class="progress flex-grow-1" style="height: 14px;">
                        <div class="progress-bar bg-warning" style="width: 82%;"></div>
                    </div>
                    <span class="ms-2 text-muted" style="width: 35px;">82%</span>
                </div>
                <div class="d-flex align-items-center mb-2 small">
                    <span class="me-2" style="width: 45px;">4 star</span>
                    <div class="progress flex-grow-1" style="height: 14px;">
                        <div class="progress-bar bg-warning" style="width: 12%;"></div>
                    </div>
                    <span class="ms-2 text-muted" style="width: 35px;">12%</span>
                </div>
                <div class="d-flex align-items-center mb-2 small">
                    <span class="me-2" style="width: 45px;">3 star</span>
                    <div class="progress flex-grow-1" style="height: 14px;">
                        <div class="progress-bar bg-warning" style="width: 4%;"></div>
                    </div>
                    <span class="ms-2 text-muted" style="width: 35px;">4%</span>
                </div>

                <hr class="my-4">

                <!-- Write a review call to action -->
                <h6 class="fw-bold mb-2">Review this product</h6>
                <p class="small text-muted mb-3">Share your thoughts with other customers across India.</p>
                <c:choose>
                    <c:when test="${not empty sessionScope.currentUser}">
                        <!-- Review form -->
                        <form action="${pageContext.request.contextPath}/review/add" method="post" class="card p-3 bg-light border-0">
                            <input type="hidden" name="productId" value="${product.id}">
                            
                            <div class="mb-2">
                                <label class="form-label small fw-semibold">Overall Rating</label>
                                <select name="rating" class="form-select form-select-sm">
                                    <option value="5">★★★★★ 5 - Excellent</option>
                                    <option value="4">★★★★☆ 4 - Good</option>
                                    <option value="3">★★★☆☆ 3 - Average</option>
                                    <option value="2">★★☆☆☆ 2 - Below Average</option>
                                    <option value="1">★☆☆☆☆ 1 - Poor</option>
                                </select>
                            </div>

                            <div class="mb-2">
                                <label class="form-label small fw-semibold">Headline</label>
                                <input type="text" name="headline" class="form-control form-control-sm" placeholder="What's most important to know?" required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label small fw-semibold">Written Review</label>
                                <textarea name="comment" class="form-control form-control-sm" rows="3" placeholder="What did you like or dislike?" required></textarea>
                            </div>

                            <button type="submit" class="btn btn-warning btn-sm fw-semibold">Submit Customer Review</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/auth/login?redirect=/product?id=${product.id}" class="btn btn-outline-dark btn-sm w-100">
                            Write a Customer Review
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Right: List of Reviews -->
            <div class="col-md-8">
                <c:choose>
                    <c:when test="${empty reviews}">
                        <div class="text-muted p-4 border rounded bg-light text-center">
                            <i class="bi bi-chat-square-text fs-2 d-block mb-2 text-secondary"></i>
                            No customer reviews yet. Be the first to review this product!
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex flex-column gap-3">
                            <c:forEach var="rev" items="${reviews}">
                                <div class="border-bottom pb-3">
                                    <div class="d-flex align-items-center mb-1">
                                        <i class="bi bi-person-circle fs-5 me-2 text-muted"></i>
                                        <span class="fw-semibold small text-dark">${rev.userName}</span>
                                        <span class="badge bg-warning text-dark ms-2 small">Verified Purchase</span>
                                    </div>
                                    <div class="d-flex align-items-center mb-1">
                                        <span class="vm-stars small me-2">
                                            <c:forEach begin="1" end="${rev.rating}"><i class="bi bi-star-fill"></i></c:forEach>
                                            <c:forEach begin="${rev.rating + 1}" end="5"><i class="bi bi-star"></i></c:forEach>
                                        </span>
                                        <strong class="small text-dark">${rev.headline}</strong>
                                    </div>
                                    <div class="text-muted small mb-2">Reviewed in India on <fmt:formatDate value="${rev.createdAt}" pattern="dd MMMM yyyy"/></div>
                                    <p class="text-dark small mb-0">${rev.comment}</p>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </div>
</div>

<jsp:include page="common/footer.jsp"/>
