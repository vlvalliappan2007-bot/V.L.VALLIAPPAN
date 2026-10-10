// Valliappan Mart - Interactive JavaScript

document.addEventListener("DOMContentLoaded", function () {
    initSearchAutoSuggest();
    initCartQuantityHandlers();
    initProductGallery();
    initPaymentTabs();
});

// 1. Live Autosuggest Search
function initSearchAutoSuggest() {
    const searchInput = document.getElementById("vmSearchInput");
    const categorySelect = document.getElementById("vmSearchCategory");
    const suggestBox = document.getElementById("vmSuggestBox");
    if (!searchInput || !suggestBox) return;

    let debounceTimer;
    searchInput.addEventListener("input", function () {
        clearTimeout(debounceTimer);
        const query = this.value.trim();
        if (query.length < 2) {
            suggestBox.style.display = "none";
            suggestBox.innerHTML = "";
            return;
        }

        debounceTimer = setTimeout(() => {
            const catId = categorySelect ? categorySelect.value : "";
            const contextPath = window.APP_CONTEXT_PATH || "";
            const url = `${contextPath}/search?format=json&q=${encodeURIComponent(query)}&category=${encodeURIComponent(catId)}`;

            fetch(url)
                .then(res => res.json())
                .then(items => {
                    if (!items || items.length === 0) {
                        suggestBox.style.display = "none";
                        suggestBox.innerHTML = "";
                        return;
                    }

                    let html = "";
                    items.forEach(item => {
                        html += `
                            <a href="${contextPath}/product?id=${item.id}" class="vm-suggest-item">
                                <img src="${item.image}" alt="${item.name}" class="vm-suggest-thumb">
                                <div>
                                    <div class="fw-semibold text-truncate" style="max-width: 480px;">${item.name}</div>
                                    <div class="small text-muted">${item.category || ""} • <span class="text-danger fw-bold">₹${Number(item.price).toLocaleString('en-IN')}</span></div>
                                </div>
                            </a>
                        `;
                    });
                    suggestBox.innerHTML = html;
                    suggestBox.style.display = "block";
                })
                .catch(err => {
                    console.error("Search suggest error:", err);
                });
        }, 250);
    });

    document.addEventListener("click", function (e) {
        if (!searchInput.contains(e.target) && !suggestBox.contains(e.target)) {
            suggestBox.style.display = "none";
        }
    });
}

// 2. Interactive AJAX Cart Quantity Update
function initCartQuantityHandlers() {
    const quantitySelects = document.querySelectorAll(".cart-item-qty-select");
    quantitySelects.forEach(sel => {
        sel.addEventListener("change", function () {
            const itemId = this.getAttribute("data-item-id");
            const newQty = this.value;
            const contextPath = window.APP_CONTEXT_PATH || "";

            fetch(`${contextPath}/cart/update`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: `itemId=${encodeURIComponent(itemId)}&quantity=${encodeURIComponent(newQty)}&format=json`
            })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    // Update Cart badge
                    updateCartBadge(data.cartCount);
                    // Update totals on page
                    const subtotalEl = document.getElementById("cartSubtotalDisplay");
                    const discountEl = document.getElementById("cartDiscountDisplay");
                    const shippingEl = document.getElementById("cartShippingDisplay");
                    const totalEl = document.getElementById("cartTotalDisplay");

                    if (subtotalEl) subtotalEl.innerText = data.subtotal;
                    if (discountEl) discountEl.innerText = data.discount;
                    if (shippingEl) shippingEl.innerText = data.shipping;
                    if (totalEl) totalEl.innerText = data.total;

                    // Reload if item deleted (qty 0)
                    if (parseInt(newQty) === 0) {
                        window.location.reload();
                    }
                }
            })
            .catch(err => console.error("Cart update error:", err));
        });
    });
}

// 3. Add to Cart AJAX Helper
function addToCartAjax(productId, quantity, redirect) {
    const contextPath = window.APP_CONTEXT_PATH || "";
    fetch(`${contextPath}/cart/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: `productId=${encodeURIComponent(productId)}&quantity=${encodeURIComponent(quantity || 1)}&format=json`
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            updateCartBadge(data.cartCount);
            if (redirect === "cart") {
                window.location.href = `${contextPath}/cart`;
            } else if (redirect === "checkout") {
                window.location.href = `${contextPath}/checkout`;
            } else {
                showToast(data.message || "Item added to cart!");
            }
        }
    })
    .catch(err => {
        console.error("Add to cart error:", err);
    });
}

// 4. Wishlist AJAX Toggle
function toggleWishlistAjax(productId, btnEl) {
    const contextPath = window.APP_CONTEXT_PATH || "";
    fetch(`${contextPath}/wishlist/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: `productId=${encodeURIComponent(productId)}&format=json`
    })
    .then(res => {
        if (res.status === 401) {
            window.location.href = `${contextPath}/auth/login?redirect=/product?id=${productId}`;
            return null;
        }
        return res.json();
    })
    .then(data => {
        if (data) {
            updateWishlistBadge(data.wishlistCount);
            showToast(data.message);
            if (btnEl) {
                btnEl.classList.add("text-danger");
                btnEl.innerHTML = `<i class="bi bi-heart-fill"></i> Wishlisted`;
            }
        }
    })
    .catch(err => console.error("Wishlist error:", err));
}

// 5. Product Gallery Thumbnails
function initProductGallery() {
    const mainImg = document.getElementById("mainProductImage");
    const thumbs = document.querySelectorAll(".vm-gallery-thumb");
    if (!mainImg || thumbs.length === 0) return;

    thumbs.forEach(thumb => {
        thumb.addEventListener("mouseenter", function () {
            thumbs.forEach(t => t.classList.remove("active"));
            this.classList.add("active");
            mainImg.src = this.getAttribute("data-full");
        });
    });
}

// 6. Payment Method Selector in Checkout
function initPaymentTabs() {
    const radios = document.querySelectorAll("input[name='paymentMethod']");
    radios.forEach(r => {
        r.addEventListener("change", function () {
            document.querySelectorAll(".payment-panel").forEach(p => p.style.display = "none");
            const target = document.getElementById("paymentPanel-" + this.value);
            if (target) target.style.display = "block";
        });
    });
}

function updateCartBadge(count) {
    const el = document.getElementById("headerCartBadge");
    if (el) el.innerText = count;
}

function updateWishlistBadge(count) {
    const el = document.getElementById("headerWishlistBadge");
    if (el) el.innerText = count;
}

// Toast Alert
function showToast(message) {
    let container = document.getElementById("vmToastContainer");
    if (!container) {
        container = document.createElement("div");
        container.id = "vmToastContainer";
        container.style.position = "fixed";
        container.style.bottom = "24px";
        container.style.right = "24px";
        container.style.zIndex = "9999";
        document.body.appendChild(container);
    }

    const toast = document.createElement("div");
    toast.className = "alert alert-warning shadow-lg text-dark fw-semibold d-flex align-items-center mb-2";
    toast.style.borderRadius = "8px";
    toast.style.border = "1px solid #FF9900";
    toast.style.minWidth = "280px";
    toast.innerHTML = `<i class="bi bi-check-circle-fill text-success fs-5 me-2"></i> <span>${message}</span>`;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = "0";
        toast.style.transition = "opacity 0.4s";
        setTimeout(() => toast.remove(), 400);
    }, 2800);
}
