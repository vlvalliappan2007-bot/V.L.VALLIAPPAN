<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tax Invoice - ${order.orderNumber} - Valliappan Mart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #F8F9FA;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
            color: #212529;
        }
        .invoice-card {
            background: #FFFFFF;
            max-width: 860px;
            margin: 30px auto;
            padding: 40px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.06);
            border: 1px solid #E0E0E0;
            border-radius: 6px;
        }
        @media print {
            body { background: #FFFFFF; }
            .invoice-card { box-shadow: none; border: none; margin: 0; padding: 0; width: 100%; max-width: 100%; }
            .no-print { display: none !important; }
        }
    </style>
</head>
<body>

<div class="container">
    
    <!-- Top toolbar (No print) -->
    <div class="d-flex justify-content-between align-items-center my-3 max-w-860 mx-auto no-print" style="max-width: 860px;">
        <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-left me-1"></i> Back to Orders
        </a>
        <button onclick="window.print()" class="btn btn-warning fw-bold btn-sm">
            <i class="bi bi-printer me-1"></i> Print / Download Invoice
        </button>
    </div>

    <!-- Official Tax Invoice Document -->
    <div class="invoice-card">
        
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-start border-bottom pb-3 mb-4">
            <div>
                <h3 class="fw-bold mb-0 text-dark">Tax Invoice / Bill of Supply</h3>
                <span class="text-muted small">Original for Recipient</span>
            </div>
            <div class="text-end">
                <h4 class="fw-bold text-dark mb-0"><span style="color:#131921;">Valliappan</span> <span style="color:#FF9900;">Mart</span></h4>
                <div class="small text-muted">GSTIN: 33AABCV8942M1Z0</div>
                <div class="small text-muted">CIN: U72200TN2026PTC109842</div>
            </div>
        </div>

        <!-- Order & Invoice metadata -->
        <div class="row g-3 mb-4 small">
            <div class="col-6">
                <div><strong>Invoice Number:</strong> INV-VM-${order.id}-2026</div>
                <div><strong>Order Number:</strong> ${order.orderNumber}</div>
                <div><strong>Order Date:</strong> <fmt:formatDate value="${order.createdAt}" pattern="dd-MM-yyyy HH:mm"/></div>
                <div><strong>Payment Method:</strong> ${order.paymentMethod}</div>
            </div>
            <div class="col-6 text-end">
                <div><strong>Carrier Tracking ID:</strong> ${order.trackingNumber}</div>
                <div><strong>Place of Supply:</strong> Tamil Nadu (33)</div>
                <div><strong>Invoice Date:</strong> <fmt:formatDate value="${order.createdAt}" pattern="dd-MM-yyyy"/></div>
            </div>
        </div>

        <!-- Addresses -->
        <div class="row g-3 mb-4 small bg-light p-3 rounded">
            <div class="col-md-6">
                <div class="text-muted text-uppercase fw-bold mb-1" style="font-size: 11px;">Sold By (Merchant)</div>
                <strong>TechNova Retail Solutions / UrbanStyle India</strong>
                <div>Authorized Multi-Vendor Partner for Valliappan Mart</div>
                <div>GSTIN: 33AABCT1334M1Z5</div>
            </div>
            <div class="col-md-6 border-start-md">
                <div class="text-muted text-uppercase fw-bold mb-1" style="font-size: 11px;">Shipping & Billing Address (Buyer)</div>
                <strong>${order.userName}</strong>
                <div>${order.shippingAddress}</div>
                <div>Email: ${order.userEmail}</div>
            </div>
        </div>

        <!-- Items Table -->
        <table class="table table-bordered table-sm small mb-4 align-middle">
            <thead class="table-light">
                <tr>
                    <th style="width: 40px;">#</th>
                    <th>Description</th>
                    <th class="text-center" style="width: 80px;">HSN</th>
                    <th class="text-end" style="width: 100px;">Unit Price (₹)</th>
                    <th class="text-center" style="width: 60px;">Qty</th>
                    <th class="text-end" style="width: 80px;">Tax (18%)</th>
                    <th class="text-end" style="width: 120px;">Total (₹)</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${order.items}" varStatus="status">
                    <tr>
                        <td class="text-center">${status.index + 1}</td>
                        <td>
                            <strong class="text-dark">${item.productName}</strong>
                            <div class="text-muted small">Sold by: ${item.sellerStoreName}</div>
                        </td>
                        <td class="text-center text-muted">85171200</td>
                        <td class="text-end"><fmt:formatNumber value="${item.unitPrice}" pattern="#,##,###.00"/></td>
                        <td class="text-center">${item.quantity}</td>
                        <td class="text-end">18% GST</td>
                        <td class="text-end fw-semibold"><fmt:formatNumber value="${item.totalPrice}" pattern="#,##,###.00"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- Totals calculation -->
        <div class="row justify-content-end mb-4 small">
            <div class="col-md-5">
                <div class="d-flex justify-content-between mb-1">
                    <span>Subtotal:</span>
                    <span>₹<fmt:formatNumber value="${order.subtotal}" pattern="#,##,###.00"/></span>
                </div>
                <c:if test="${order.discount > 0}">
                    <div class="d-flex justify-content-between mb-1 text-success">
                        <span>Discount Coupon:</span>
                        <span>-₹<fmt:formatNumber value="${order.discount}" pattern="#,##,###.00"/></span>
                    </div>
                </c:if>
                <div class="d-flex justify-content-between mb-1">
                    <span>Shipping Charges:</span>
                    <span>${order.shippingCost == 0 ? 'FREE' : '₹' += order.shippingCost}</span>
                </div>
                <div class="d-flex justify-content-between mb-1 text-muted">
                    <span>IGST / CGST+SGST (18%):</span>
                    <span>₹<fmt:formatNumber value="${order.tax}" pattern="#,##,###.00"/></span>
                </div>
                <hr class="my-2">
                <div class="d-flex justify-content-between fs-6 fw-bold">
                    <span>Grand Total:</span>
                    <span class="text-danger">₹<fmt:formatNumber value="${order.totalAmount}" pattern="#,##,###.00"/></span>
                </div>
            </div>
        </div>

        <!-- Seal & Signatory -->
        <div class="d-flex justify-content-between align-items-end pt-4 border-top small text-muted">
            <div>
                <div>Whether tax is payable on reverse charge basis: <strong>No</strong></div>
                <div>Payment Confirmation: <strong>COMPLETED (${order.paymentMethod})</strong></div>
            </div>
            <div class="text-end">
                <div class="mb-2">For <strong>Valliappan Mart India Pvt Ltd</strong></div>
                <div class="border d-inline-block px-3 py-1 text-success fw-bold rounded border-success" style="letter-spacing: 1px;">
                    DIGITALLY AUTHORIZED
                </div>
                <div class="mt-1" style="font-size: 11px;">Authorized Signatory</div>
            </div>
        </div>

    </div>

</div>

</body>
</html>
