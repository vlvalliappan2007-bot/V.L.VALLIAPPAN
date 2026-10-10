<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Your Wishlist - Valliappan Mart"/>
</jsp:include>

<div class="container py-4" style="max-width: 960px;">
    
    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <div>
            <h3 class="fw-bold mb-0 text-dark">Your Wishlist</h3>
            <span class="text-muted small">Items saved for later purchases</span>
        </div>
        <span class="badge bg-warning text-dark fs-6">${wishlistItems.size()} Items</span>
    </div>

    <c:choose>
        <c:when test="${empty wishlistItems}">
            <div class="card p-5 text-center border-0 shadow-sm">
                <i class="bi bi-heart fs-1 text-muted d-block mb-3"></i>
                <h4 class="fw-bold">Your Wishlist is currently empty</h4>
                <p class="text-muted small">Explore trending products and click the heart icon to save items here.</p>
                <div>
                    <a href="${pageContext.request.contextPath}/shop" class="btn btn-add-to-cart w-auto px-4 mt-2">
                        Discover Products
                    </a>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row row-cols-1 row-cols-md-2 g-3">
                <c:forEach var="item" items="${wishlistItems}">
                    <div class="col">
                        <div class="card p-3 h-100 border rounded shadow-sm d-flex flex-row gap-3">
                            <a href="${pageContext.request.contextPath}/product?id=${item.product.id}" style="width: 110px; min-width: 110px; height: 110px;" class="d-flex align-items-center justify-content-center bg-light rounded p-2">
                                <img src="${item.product.mainImage}" alt="${item.product.name}" class="img-fluid" style="max-height: 100px; object-fit: contain;">
                            </a>
                            <div class="d-flex flex-column flex-grow-1">
                                <h6 class="fw-semibold mb-1 text-truncate-2" style="max-height: 40px; overflow: hidden;">
                                    <a href="${pageContext.request.contextPath}/product?id=${item.product.id}" class="text-dark text-decoration-none small">
                                        ${item.product.name}
                                    </a>
                                </h6>
                                <div class="fw-bold text-danger mb-2">₹<fmt:formatNumber value="${item.product.price}" pattern="#,##,###"/></div>
                                
                                <div class="mt-auto d-flex gap-2">
                                    <button type="button" class="btn btn-warning btn-sm fw-semibold flex-grow-1" onclick="addToCartAjax(${item.product.id}, 1)">
                                        <i class="bi bi-cart-plus me-1"></i> Add to Cart
                                    </button>
                                    <a href="${pageContext.request.contextPath}/wishlist/remove?productId=${item.product.id}" class="btn btn-outline-danger btn-sm" title="Remove">
                                        <i class="bi bi-trash3"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

</div>

<jsp:include page="common/footer.jsp"/>
