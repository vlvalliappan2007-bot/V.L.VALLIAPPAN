<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="${isEdit ? 'Edit Product' : 'Add New Product'} - Valliappan Mart Merchant Central"/>
</jsp:include>

<div class="container py-4" style="max-width: 840px;">
    
    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <div>
            <h3 class="fw-bold mb-0 text-dark">${isEdit ? 'Edit Product' : 'Add New Product to Catalog'}</h3>
            <span class="text-muted small">Enter product specifications, INR ₹ pricing, inventory stock, and high-res image URLs</span>
        </div>
        <a href="${pageContext.request.contextPath}/seller" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Back to Merchant Dashboard
        </a>
    </div>

    <div class="card p-4 border-0 shadow-sm bg-white rounded">
        <form action="${pageContext.request.contextPath}/seller/product/save" method="post">
            <input type="hidden" name="id" value="${product.id}">

            <div class="row g-3">
                <div class="col-md-8">
                    <label class="form-label small fw-semibold text-dark">Product Title</label>
                    <input type="text" name="name" class="form-control" value="${product.name}" placeholder="e.g. Premium 5G Smartphone (256GB / 12GB RAM)" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label small fw-semibold text-dark">Brand Name</label>
                    <input type="text" name="brand" class="form-control" value="${product.brand}" placeholder="e.g. Zenith / AcoustiPro" required>
                </div>

                <div class="col-md-6">
                    <label class="form-label small fw-semibold text-dark">Category</label>
                    <select name="categoryId" class="form-select" required>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.id}" ${product.categoryId == c.id ? 'selected' : ''}>${c.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-3">
                    <label class="form-label small fw-semibold text-dark">Selling Price (₹ INR)</label>
                    <input type="number" step="0.01" name="price" class="form-control" value="${product.price}" placeholder="54999.00" required>
                </div>

                <div class="col-md-3">
                    <label class="form-label small fw-semibold text-dark">M.R.P. / Original (₹)</label>
                    <input type="number" step="0.01" name="originalPrice" class="form-control" value="${product.originalPrice}" placeholder="64999.00" required>
                </div>

                <div class="col-md-4">
                    <label class="form-label small fw-semibold text-dark">Stock Inventory Count</label>
                    <input type="number" name="stockQuantity" class="form-control" value="${product.stockQuantity != null ? product.stockQuantity : 15}" required>
                </div>

                <div class="col-md-8">
                    <label class="form-label small fw-semibold text-dark">Main Image URL</label>
                    <input type="url" name="mainImage" class="form-control" value="${product.mainImage}" placeholder="https://images.unsplash.com/..." required>
                </div>

                <div class="col-md-6">
                    <label class="form-label small fw-semibold text-dark">Secondary Image URL (Optional)</label>
                    <input type="url" name="subImage1" class="form-control" value="${product.subImage1}" placeholder="https://images.unsplash.com/...">
                </div>

                <div class="col-md-6">
                    <label class="form-label small fw-semibold text-dark">Tertiary Image URL (Optional)</label>
                    <input type="url" name="subImage2" class="form-control" value="${product.subImage2}" placeholder="https://images.unsplash.com/...">
                </div>

                <div class="col-12">
                    <label class="form-label small fw-semibold text-dark">Product Bullet Points & Detailed Description</label>
                    <textarea name="description" class="form-control" rows="5" placeholder="Highlight key technical specs, materials, warranty, and features..." required>${product.description}</textarea>
                </div>

                <div class="col-12 text-end pt-3 border-top">
                    <a href="${pageContext.request.contextPath}/seller" class="btn btn-outline-secondary me-2">Cancel</a>
                    <button type="submit" class="btn btn-warning fw-bold px-4">Save and Publish Product</button>
                </div>
            </div>
        </form>
    </div>

</div>

<jsp:include page="common/footer.jsp"/>
