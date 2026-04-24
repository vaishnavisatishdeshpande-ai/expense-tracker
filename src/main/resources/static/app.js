let currentSort = null; // null | asc | desc
let debounceTimer;

// 🔁 ADD EXPENSE (same as before, unchanged logic)
async function addExpense() {
    const amountValue = document.getElementById("amount").value;
    const category = document.getElementById("category").value;
    const description = document.getElementById("description").value;
    const date = document.getElementById("date").value;

    if (!amountValue || isNaN(amountValue) || parseInt(amountValue) <= 0) {
        alert("Amount must be a positive number");
        return;
    }

    if (!category) {
        alert("Category is required");
        return;
    }

    if (!date) {
        alert("Date is required");
        return;
    }

    const data = {
        amount: parseInt(amountValue),
        category,
        description,
        date
    };

    try {
        const res = await fetch("/expenses", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Idempotency-Key": crypto.randomUUID()
            },
            body: JSON.stringify(data)
        });

        if (!res.ok) {
            const errorData = await res.json();
            alert(Object.values(errorData).join(", "));
            return;
        }

        // Clear form
        document.getElementById("amount").value = "";
        document.getElementById("category").value = "";
        document.getElementById("description").value = "";
        document.getElementById("date").value = "";

        loadExpenses();

    } catch {
        alert("Something went wrong");
    }
}

// 🔍 LOAD EXPENSES (enhanced)
async function loadExpenses() {

    const status = document.getElementById("status");
    status.innerText = "Loading...";

    let url = "/expenses";

    const category = document.getElementById("filterCategory").value;

    const params = [];

    if (category) params.push(`category=${category}`);

    if (currentSort === "desc") {
        params.push("sort=date_desc");
    }

    if (params.length > 0) {
        url += "?" + params.join("&");
    }

    try {
        const res = await fetch(url);
        const data = await res.json();

        let expenses = data.expenses;

        // 🔁 ASC sorting handled on frontend
        if (currentSort === "asc") {
            expenses = expenses.sort((a, b) => new Date(a.date) - new Date(b.date));
        }

        renderExpenses(expenses, data.total);

        status.innerText = "";

    } catch {
        status.innerText = "Failed to load expenses";
    }
}

// 🧾 RENDER
function renderExpenses(expenses, total) {

    const list = document.getElementById("list");
    list.innerHTML = "";

    if (expenses.length === 0) {
        list.innerHTML = `<div class="empty">No expenses found</div>`;
    } else {
        expenses.forEach(e => {
            const div = document.createElement("div");
            div.className = "expense-item";
            div.innerHTML = `
                <strong>${e.category}</strong> — ₹${e.amount}<br/>
                <small>${e.description || ""}</small><br/>
                <small>${e.date}</small>
            `;
            list.appendChild(div);
        });
    }

    document.getElementById("total").innerText = total;
}

// 🔄 SORT TOGGLE
function toggleSort() {
    if (currentSort === null) currentSort = "desc";
    else if (currentSort === "desc") currentSort = "asc";
    else currentSort = "desc";

    loadExpenses();
}

// 🔍 LIVE FILTER (debounced)
function handleFilterInput() {
    clearTimeout(debounceTimer);

    debounceTimer = setTimeout(() => {
        loadExpenses();
    }, 300);
}

// 🚀 INIT
document.getElementById("filterCategory").addEventListener("input", handleFilterInput);

// Initial load
loadExpenses();