
const API_BASE = "/api";

const currency = (amount) =>
    new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2
    }).format(Number(amount || 0));

async function fetchJSON(endpoint) {
    const response = await fetch(`${API_BASE}/${endpoint}`);

    if (!response.ok) {
        throw new Error(`Request failed: ${endpoint}`);
    }

    return response.json();
}

async function loadDashboard() {
    try {
        const data = await fetchJSON("dashboard");
        const cards = document.querySelectorAll(".summary-card h2");
        const descriptions = document.querySelectorAll(".summary-card span");

        if (cards.length >= 4) {
            cards[0].textContent = currency(data.invoiceTotal);
            cards[1].textContent = String(data.paymentCount);
            cards[2].textContent = currency(data.paidTotal);
            cards[3].textContent = String(data.overdueInvoiceCount);
        }

        if (descriptions.length >= 4) {
            descriptions[0].textContent = `${data.invoiceCount} invoices`;
            descriptions[1].textContent =
                `${data.unallocatedPaymentCount} unallocated or partially allocated`;
            descriptions[2].textContent =
                `${data.paidInvoiceCount} fully paid invoices`;
            descriptions[3].textContent = "Require attention";
        }
    } catch (error) {
        console.error("Dashboard loading failed:", error);
    }
}

async function loadInvoices() {
    try {
        const invoices = await fetchJSON("invoices");
        const tbody = document.querySelector("#invoices tbody");

        tbody.replaceChildren();

        invoices.forEach((invoice) => {
            const row = document.createElement("tr");

            [
                invoice.invoiceId,
                invoice.customerId,
                currency(invoice.amount),
                currency(invoice.paidAmount),
                invoice.status
            ].forEach((value, index) => {
                const cell = document.createElement("td");

                if (index === 4) {
                    const badge = document.createElement("span");
                    badge.className = "pill " +
                        statusClass(invoice.status);
                    badge.textContent = invoice.status;
                    cell.appendChild(badge);
                } else {
                    cell.textContent = value;
                }

                row.appendChild(cell);
            });

            tbody.appendChild(row);
        });
    } catch (error) {
        console.error("Invoice loading failed:", error);
    }
}

async function loadPayments() {
    try {
        const payments = await fetchJSON("payments");
        const panel = document.querySelector("#payments");

        panel.querySelectorAll(".payment-row").forEach((row) => row.remove());

        payments.forEach((payment) => {
            const row = document.createElement("div");
            row.className = "payment-row";

            const details = document.createElement("div");
            const id = document.createElement("strong");
            id.textContent = payment.paymentId;

            const customer = document.createElement("p");
            customer.textContent = payment.customerId;

            details.append(id, customer);

            const right = document.createElement("div");
            right.className = "payment-right";

            const amount = document.createElement("strong");
            amount.textContent = currency(payment.amount);

            const badge = document.createElement("span");
            badge.className = "pill " + statusClass(payment.status);
            badge.textContent = payment.status.replaceAll("_", " ");

            right.append(amount, badge);
            row.append(details, right);
            panel.appendChild(row);
        });
    } catch (error) {
        console.error("Payment loading failed:", error);
    }
}

async function loadTransactions() {
    try {
        const transactions = await fetchJSON("transactions");
        const panel = document.querySelector("#transactions");

        panel.querySelectorAll(".activity-item").forEach((item) => item.remove());

        transactions.forEach((transaction) => {
            const item = document.createElement("div");
            item.className = "activity-item";

            const dot = document.createElement("span");
            dot.className = "activity-dot";

            const details = document.createElement("div");
            const title = document.createElement("strong");
            title.textContent =
                `${transaction.paymentId || "N/A"} → ${transaction.invoiceId || "N/A"}`;

            const description = document.createElement("p");
            description.textContent =
                `${transaction.action} · ${currency(transaction.amount)}`;

            details.append(title, description);
            item.append(dot, details);
            panel.appendChild(item);
        });
    } catch (error) {
        console.error("Transaction loading failed:", error);
    }
}

function statusClass(status) {
    switch (status) {
        case "PAID":
        case "FULLY_ALLOCATED":
            return "paid";
        case "PARTIALLY_PAID":
        case "PARTIALLY_ALLOCATED":
            return "partial";
        case "OVERDUE":
        case "REJECTED":
            return "overdue";
        default:
            return "";
    }
}

const navigationLinks = document.querySelectorAll(".sidebar nav a");

navigationLinks.forEach((link) => {
    link.addEventListener("click", () => {
        navigationLinks.forEach((item) => item.classList.remove("active"));
        link.classList.add("active");
    });
});

async function initializeDashboard() {
    await Promise.all([
        loadDashboard(),
        loadInvoices(),
        loadPayments(),
        loadTransactions()
    ]);
}

initializeDashboard();


const clearingButton = document.getElementById("run-clearing-btn");
const clearingMessage = document.getElementById("clearing-message");

clearingButton.addEventListener("click", async () => {
    const confirmed = window.confirm(
        "Run payment clearing? This may update invoice and payment records in MySQL."
    );

    if (!confirmed) {
        return;
    }

    clearingButton.disabled = true;
    clearingMessage.textContent = "Processing payments...";

    try {
        const response = await fetch(`${API_BASE}/clearing/run`, {
            method: "POST"
        });

        const result = await response.json();

        if (!response.ok || !result.success) {
            throw new Error(result.message || "Clearing failed.");
        }

        clearingMessage.textContent = result.message;

        await initializeDashboard();

    } catch (error) {
        clearingMessage.textContent =
            `Unable to complete clearing: ${error.message}`;

        console.error(error);

    } finally {
        clearingButton.disabled = false;
    }
});
