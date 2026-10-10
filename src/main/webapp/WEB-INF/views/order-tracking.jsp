<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Track Package - Valliappan Mart"/>
</jsp:include>

<div class="container py-5" style="max-width: 920px;">
    
    <div class="card p-4 p-md-5 border-0 shadow-sm rounded">
        
        <div class="d-flex flex-wrap justify-content-between align-items-center border-bottom pb-3 mb-4">
            <div>
                <h3 class="fw-bold text-dark mb-1">Track Your Package</h3>
                <span class="text-muted small">Order #${order.orderNumber} &bull; Carrier: <strong>Valliappan Express / Delhivery</strong></span>
            </div>
            <div class="mt-2 mt-sm-0">
                <span class="badge bg-warning text-dark fs-6 px-3 py-2">Tracking ID: ${order.trackingNumber}</span>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty order}">
                <div class="text-center py-5">
                    <i class="bi bi-box-seam fs-1 text-muted d-block mb-3"></i>
                    <h5>No active order found to track.</h5>
                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-warning mt-2">View Your Orders</a>
                </div>
            </c:when>
            <c:otherwise>
                
                <!-- Status Banner -->
                <div class="alert alert-info py-3 mb-4">
                    <div class="d-flex align-items-center">
                        <i class="bi bi-clock-history fs-3 me-3 text-primary"></i>
                        <div>
                            <h6 class="fw-bold mb-1">Expected Delivery: Tomorrow by 5:00 PM</h6>
                            <span class="small text-muted">Your package is moving through our Chennai Sort Facility.</span>
                        </div>
                    </div>
                </div>

                <!-- Multi-Step Progress Tracker Bar -->
                <div class="position-relative m-4">
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar bg-success" role="progressbar" style="width: 66%;" aria-valuenow="66" aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                    
                    <div class="d-flex justify-content-between position-relative" style="margin-top: -18px;">
                        <!-- Step 1 -->
                        <div class="text-center">
                            <div class="btn btn-sm btn-success rounded-pill" style="width: 32px; height: 32px; padding: 4px;"><i class="bi bi-check-lg"></i></div>
                            <div class="small fw-bold mt-2">Ordered</div>
                            <div class="text-muted" style="font-size: 11px;"><fmt:formatDate value="${order.createdAt}" pattern="dd MMM"/></div>
                        </div>

                        <!-- Step 2 -->
                        <div class="text-center">
                            <div class="btn btn-sm btn-success rounded-pill" style="width: 32px; height: 32px; padding: 4px;"><i class="bi bi-check-lg"></i></div>
                            <div class="small fw-bold mt-2">Packed</div>
                            <div class="text-muted" style="font-size: 11px;">Merchant Hub</div>
                        </div>

                        <!-- Step 3 -->
                        <div class="text-center">
                            <div class="btn btn-sm btn-success rounded-pill" style="width: 32px; height: 32px; padding: 4px;"><i class="bi bi-truck"></i></div>
                            <div class="small fw-bold mt-2">Shipped</div>
                            <div class="text-muted" style="font-size: 11px;">In Transit</div>
                        </div>

                        <!-- Step 4 -->
                        <div class="text-center">
                            <div class="btn btn-sm btn-secondary rounded-pill" style="width: 32px; height: 32px; padding: 4px;"><i class="bi bi-house-door"></i></div>
                            <div class="small fw-bold mt-2 text-muted">Delivered</div>
                            <div class="text-muted" style="font-size: 11px;">Expected Tomorrow</div>
                        </div>
                    </div>
                </div>

                <!-- Tracking Log Events -->
                <div class="mt-5">
                    <h6 class="fw-bold mb-3 border-bottom pb-2">Tracking History</h6>
                    <ul class="list-group list-group-flush small">
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0 py-2">
                            <div>
                                <span class="fw-semibold text-dark">Package arrived at local delivery station</span>
                                <div class="text-muted">Chennai OMR Logistics Hub</div>
                            </div>
                            <span class="text-muted">Today, 08:30 AM</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0 py-2">
                            <div>
                                <span class="fw-semibold text-dark">Package in transit with BlueDart / VM Logistics</span>
                                <div class="text-muted">Dispatched from Central Warehouse</div>
                            </div>
                            <span class="text-muted">Today, 02:15 AM</span>
                        </li>
                        <li class="list-group-item d-flex justify-content-between align-items-center px-0 py-2">
                            <div>
                                <span class="fw-semibold text-dark">Merchant picked and packed items</span>
                                <div class="text-muted">Fulfillment by TechNova</div>
                            </div>
                            <span class="text-muted">Yesterday, 07:45 PM</span>
                        </li>
                    </ul>
                </div>

                <!-- Delivery Address & Actions -->
                <div class="row g-3 mt-4 pt-3 border-top">
                    <div class="col-md-8 small">
                        <span class="text-muted d-block mb-1">Delivering to:</span>
                        <strong class="text-dark">${order.shippingAddress}</strong>
                    </div>
                    <div class="col-md-4 text-md-end">
                        <a href="${pageContext.request.contextPath}/order/invoice?orderNumber=${order.orderNumber}" target="_blank" class="btn btn-outline-dark btn-sm">
                            <i class="bi bi-file-earmark-text me-1"></i> View Tax Invoice
                        </a>
                    </div>
                </div>

            </c:otherwise>
        </c:choose>

    </div>

</div>

<jsp:include page="common/footer.jsp"/>
