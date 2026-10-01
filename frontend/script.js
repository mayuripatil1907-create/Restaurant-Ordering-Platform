console.log("Restaurant Ordering Platform loaded successfully!");

// ================= CART =================

let cart = JSON.parse(localStorage.getItem("cart")) || [];

function addToCart(name, price) {

    const existingItem = cart.find(item => item.name === name);

    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({
            name: name,
            price: price,
            quantity: 1
        });
    }

    localStorage.setItem("cart", JSON.stringify(cart));

    alert(name + " added to cart!");

    updateCartCount();
}

function updateCartCount() {

    const cartCount = document.getElementById("cart-count");

    if (cartCount) {

        let totalQuantity = 0;

        cart.forEach(item => {
            totalQuantity += item.quantity;
        });

        cartCount.textContent = totalQuantity;
    }
}

function calculateTotal() {

    let total = 0;

    cart.forEach(item => {
        total += item.price * item.quantity;
    });

    return total;
}

function displayCart() {

    const cartContainer =
        document.getElementById("cart-items");

    const totalElement =
        document.getElementById("cart-total");

    if (!cartContainer) {
        return;
    }

    cartContainer.innerHTML = "";

    if (cart.length === 0) {

        cartContainer.innerHTML =
            "<p>Your cart is empty.</p>";

        if (totalElement) {
            totalElement.textContent = "₹0";
        }

        return;
    }

    cart.forEach((item, index) => {

        const div = document.createElement("div");

        div.className = "cart-item";

        div.innerHTML = `
            <div>
                <h3>${item.name}</h3>
                <p>₹${item.price} × ${item.quantity}</p>
            </div>

            <div>
                <button onclick="decreaseQuantity(${index})">−</button>

                <span>${item.quantity}</span>

                <button onclick="increaseQuantity(${index})">+</button>

                <button onclick="removeFromCart(${index})">
                    Remove
                </button>
            </div>
        `;

        cartContainer.appendChild(div);
    });

    if (totalElement) {
        totalElement.textContent =
            "₹" + calculateTotal();
    }
}

function increaseQuantity(index) {

    cart[index].quantity++;

    saveCart();
}

function decreaseQuantity(index) {

    if (cart[index].quantity > 1) {

        cart[index].quantity--;

    } else {

        cart.splice(index, 1);
    }

    saveCart();
}

function removeFromCart(index) {

    cart.splice(index, 1);

    saveCart();
}

function saveCart() {

    localStorage.setItem(
        "cart",
        JSON.stringify(cart)
    );

    displayCart();
    updateCartCount();
}

function clearCart() {

    cart = [];

    localStorage.removeItem("cart");

    displayCart();
    updateCartCount();
}

// ================= PLACE ORDER =================

async function placeOrder() {

    if (cart.length === 0) {

        alert("Your cart is empty!");

        return;
    }

    const userId =
        localStorage.getItem("user_id");

    if (!userId) {

        alert("Please login before placing an order.");

        window.location.href = "login.html";

        return;
    }

    const totalAmount =
        calculateTotal();

    const data =
        "user_id=" + encodeURIComponent(userId) +
        "&total_amount=" + encodeURIComponent(totalAmount);

    try {

        const response = await fetch(
            "http://localhost:8080/order",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body: data
            }
        );

        const result =
            await response.text();

        alert(result);

        if (result === "Order placed successfully!") {

            clearCart();
        }

    } catch (error) {

        alert("Java server connection failed!");

        console.error(error);
    }
}


// ================= RESERVATION =================

function makeReservation() {

    const name =
        document.getElementById("customer-name")?.value;

    const date =
        document.getElementById("reservation-date")?.value;

    const time =
        document.getElementById("reservation-time")?.value;

    const guests =
        document.getElementById("guests")?.value;

    if (!name || !date || !time || !guests) {

        alert("Please fill all reservation details.");

        return;
    }

    alert(
        "Reservation submitted successfully!\n\n" +
        "Name: " + name +
        "\nDate: " + date +
        "\nTime: " + time +
        "\nGuests: " + guests
    );
}


// ================= LOGIN =================

async function loginUser(event) {

    event.preventDefault();

    const email =
        document.getElementById("login-email").value;

    const password =
        document.getElementById("login-password").value;

    const data =
        "email=" + encodeURIComponent(email) +
        "&password=" + encodeURIComponent(password);

    try {

        const response = await fetch(
            "http://localhost:8080/login",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body: data
            }
        );

        const result = await response.text();

        alert(result);

        if (result.startsWith("Login successful!")) {

            const parts = result.split("|");

            if (parts.length > 1) {

                const userId =
                    parts[1].split("=")[1];

                localStorage.setItem(
                    "user_id",
                    userId
                );
            }

            window.location.href =
                "http://127.0.0.1:5500/frontend/customer_dashboard.html";
        }

    } catch (error) {

        alert("Java server connection failed!");

        console.error(error);
    }
}


// ================= REGISTER =================

async function registerUser(event) {

    event.preventDefault();

    const name =
        document.getElementById("register-name").value;

    const email =
        document.getElementById("register-email").value;

    const password =
        document.getElementById("register-password").value;

    const data =
        "name=" + encodeURIComponent(name) +
        "&email=" + encodeURIComponent(email) +
        "&password=" + encodeURIComponent(password);

    try {

        const response = await fetch(
            "http://localhost:8080/register",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body: data
            }
        );

        const result = await response.text();

        alert(result);

        if (result === "Registration successful!") {

            window.location.href =
                "login.html";
        }

    } catch (error) {

        alert("Java server connection failed!");

        console.error(error);
    }
}


// ================= PAGE LOAD =================

displayCart();
updateCartCount();

async function makeReservation() {

    const userId = localStorage.getItem("user_id");

    if (!userId) {
        alert("Please login before making a reservation.");
        window.location.href = "login.html";
        return;
    }

    const tableId =
        document.getElementById("table-id").value;

    const reservationDate =
        document.getElementById("reservation-date").value;

    const reservationTime =
        document.getElementById("reservation-time").value;

    const guests =
        document.getElementById("guests").value;

    const data =
        "user_id=" + encodeURIComponent(userId) +
        "&table_id=" + encodeURIComponent(tableId) +
        "&reservation_date=" + encodeURIComponent(reservationDate) +
        "&reservation_time=" + encodeURIComponent(reservationTime) +
        "&number_of_guests=" + encodeURIComponent(guests);

    try {

        const response = await fetch(
            "http://localhost:8080/reservation",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body: data
            }
        );

        const result = await response.text();

        alert(result);

    } catch (error) {

        alert("Java server connection failed!");

        console.error(error);
    }
}