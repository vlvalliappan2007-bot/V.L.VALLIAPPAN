<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Create Account - Valliappan Mart"/>
</jsp:include>

<div class="container py-5" style="max-width: 440px;">
    
    <div class="card p-4 shadow-sm border rounded">
        <h3 class="fw-bold mb-3 text-dark">Create Account</h3>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger py-2 small mb-3">
                <i class="bi bi-exclamation-triangle-fill me-1"></i> ${errorMessage}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/auth/register" method="post">
            
            <div class="mb-3">
                <label for="nameInput" class="form-label small fw-semibold text-dark">Your name</label>
                <input type="text" name="name" id="nameInput" class="form-control" placeholder="First and last name" required autofocus>
            </div>

            <div class="mb-3">
                <label for="phoneInput" class="form-label small fw-semibold text-dark">Mobile number</label>
                <div class="input-group">
                    <span class="input-group-text small bg-light">IN +91</span>
                    <input type="tel" name="phone" id="phoneInput" class="form-control" placeholder="9876543210" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="emailInput" class="form-label small fw-semibold text-dark">Email (optional or for verification)</label>
                <input type="email" name="email" id="emailInput" class="form-control" placeholder="you@domain.com" required>
            </div>

            <div class="mb-3">
                <label for="passwordInput" class="form-label small fw-semibold text-dark">Password</label>
                <input type="password" name="password" id="passwordInput" class="form-control" placeholder="At least 6 characters" minlength="6" required>
                <div class="form-text small" style="font-size: 11px;"><i class="bi bi-info-circle me-1"></i> Passwords must be at least 6 characters.</div>
            </div>

            <div class="mb-3">
                <label for="confirmPassInput" class="form-label small fw-semibold text-dark">Re-enter password</label>
                <input type="password" name="confirmPassword" id="confirmPassInput" class="form-control" minlength="6" required>
            </div>

            <div class="mb-3">
                <label class="form-label small fw-semibold text-dark">Account Type</label>
                <div class="d-flex gap-3">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="role" id="roleBuyer" value="BUYER" checked>
                        <label class="form-check-label small" for="roleBuyer">Customer / Buyer</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" name="role" id="roleSeller" value="SELLER">
                        <label class="form-check-label small" for="roleSeller">Merchant / Seller</label>
                    </div>
                </div>
            </div>

            <button type="submit" class="btn btn-amazon-primary w-100 py-2 fw-bold mb-3 shadow-sm">
                Create Account
            </button>
        </form>

        <p class="small text-muted mb-3" style="font-size: 11px;">
            By creating an account, you agree to Valliappan Mart's Conditions of Use and Privacy Notice.
        </p>

        <hr class="my-3">

        <div class="small">
            Already have an account? 
            <a href="${pageContext.request.contextPath}/auth/login" class="text-primary fw-semibold text-decoration-none">Sign in <i class="bi bi-caret-right-fill"></i></a>
        </div>

    </div>

</div>

<jsp:include page="common/footer.jsp"/>
