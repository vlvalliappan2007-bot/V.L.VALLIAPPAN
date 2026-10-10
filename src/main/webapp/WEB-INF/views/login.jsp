<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Sign-In - Valliappan Mart"/>
</jsp:include>

<div class="container py-5" style="max-width: 440px;">
    
    <!-- Central Login Card -->
    <div class="card p-4 shadow-sm border rounded">
        <h3 class="fw-bold mb-3 text-dark">Sign in</h3>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger py-2 small mb-3">
                <i class="bi bi-exclamation-triangle-fill me-1"></i> ${errorMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/login" method="post">
            <input type="hidden" name="redirect" value="${param.redirect}">

            <div class="mb-3">
                <label for="emailInput" class="form-label small fw-semibold text-dark">Email or mobile phone number</label>
                <input type="email" name="email" id="emailInput" class="form-control" value="${enteredEmail != null ? enteredEmail : param.email}" required autofocus>
            </div>

            <div class="mb-3">
                <div class="d-flex justify-content-between">
                    <label for="passwordInput" class="form-label small fw-semibold text-dark">Password</label>
                    <a href="#" class="small text-decoration-none text-primary">Forgot password?</a>
                </div>
                <input type="password" name="password" id="passwordInput" class="form-control" required>
            </div>

            <button type="submit" class="btn btn-amazon-primary w-100 py-2 fw-bold mb-3 shadow-sm">
                Sign In
            </button>
        </form>

        <p class="small text-muted mb-3" style="font-size: 11px;">
            By continuing, you agree to Valliappan Mart's Conditions of Use and Privacy Notice.
        </p>

        <!-- Demo Accounts Quick Fill Pill Buttons -->
        <div class="bg-light p-3 rounded border small mb-3">
            <div class="fw-bold text-muted mb-2" style="font-size: 11px;">1-CLICK DEMO CREDENTIALS:</div>
            <div class="d-flex flex-column gap-1">
                <button type="button" class="btn btn-sm btn-outline-dark text-start" 
                        onclick="fillCreds('admin@valliappanmart.com', 'admin123')">
                    <strong>Admin:</strong> admin@valliappanmart.com (Pass: admin123)
                </button>
                <button type="button" class="btn btn-sm btn-outline-primary text-start" 
                        onclick="fillCreds('seller@valliappanmart.com', 'seller123')">
                    <strong>Seller:</strong> seller@valliappanmart.com (Pass: seller123)
                </button>
                <button type="button" class="btn btn-sm btn-outline-success text-start" 
                        onclick="fillCreds('buyer@valliappanmart.com', 'buyer123')">
                    <strong>Buyer:</strong> buyer@valliappanmart.com (Pass: buyer123)
                </button>
            </div>
        </div>

    </div>

    <!-- Divider to Register -->
    <div class="position-relative text-center my-4">
        <hr>
        <span class="position-absolute top-50 start-50 translate-middle bg-light px-3 small text-muted">New to Valliappan Mart?</span>
    </div>

    <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-outline-secondary w-100 py-2 small fw-semibold shadow-sm">
        Create your Valliappan Mart account
    </a>

</div>

<script>
    function fillCreds(email, pass) {
        document.getElementById('emailInput').value = email;
        document.getElementById('passwordInput').value = pass;
    }
</script>

<jsp:include page="common/footer.jsp"/>
