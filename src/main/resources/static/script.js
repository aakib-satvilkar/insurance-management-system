// ==========================================================
// Sawai Associates - Insurance Management System frontend
// Every table is populated from live REST calls to the Spring
// Boot backend. No hardcoded/mock data is used anywhere here.
// ==========================================================

const API_BASE = "/api";

function currentToken() {
    return document.getElementById("roleSelect").value;
}

function currentRoleLabel() {
    return currentToken().startsWith("admin") ? "ADMIN" : "AGENT";
}

async function apiCall(path, method = "GET", body = null) {
    const options = {
        method,
        headers: {
            "Content-Type": "application/json",
            "X-Auth-Token": currentToken()
        }
    };
    if (body) options.body = JSON.stringify(body);

    const response = await fetch(API_BASE + path, options);

    if (!response.ok) {
        let message = `Request failed (${response.status})`;
        try {
            const errBody = await response.json();
            message = errBody.message || message;
        } catch (e) { /* ignore parse errors on empty body */ }
        throw new Error(message);
    }

    if (response.status === 204) return null;
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function showToast(message, type = "success") {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => toast.classList.remove("show"), 3000);
}

// ---------------- Tabs ----------------
document.querySelectorAll(".tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));
        document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
        btn.classList.add("active");
        document.getElementById(btn.dataset.tab).classList.add("active");
    });
});

document.getElementById("roleSelect").addEventListener("change", () => {
    document.getElementById("roleBadge").textContent = currentRoleLabel();
});

// ---------------- Customers ----------------
async function loadCustomers(nameFilter = "") {
    try {
        const path = nameFilter ? `/customers/search?name=${encodeURIComponent(nameFilter)}` : "/customers";
        const customers = await apiCall(path);
        const tbody = document.getElementById("customerTableBody");
        tbody.innerHTML = customers.map(c => `
            <tr>
                <td>${c.customerId}</td>
                <td>${c.firstName} ${c.lastName}</td>
                <td>${c.email}</td>
                <td>${c.phoneNumber ?? "-"}</td>
                <td>${c.dateOfBirth ?? "-"}</td>
                <td><span class="status-pill ${c.accountStatus}">${c.accountStatus}</span></td>
                <td>
                    <a class="action-link edit" onclick='openCustomerForm(${JSON.stringify(c)})'>Edit</a>
                    <a class="action-link delete" onclick="deleteCustomer(${c.customerId})">Delete</a>
                </td>
            </tr>
        `).join("") || `<tr><td colspan="7">No customers found.</td></tr>`;
    } catch (err) {
        showToast(err.message, "error");
    }
}

document.getElementById("customerSearch").addEventListener("input", (e) => {
    loadCustomers(e.target.value.trim());
});

async function deleteCustomer(id) {
    if (!confirm("Delete this customer and all their policies?")) return;
    try {
        await apiCall(`/customers/${id}`, "DELETE");
        showToast("Customer deleted");
        loadCustomers();
    } catch (err) {
        showToast(err.message, "error");
    }
}

function openCustomerForm(customer = null) {
    const isEdit = !!customer;
    showModal(isEdit ? "Edit Customer" : "New Customer", `
        <label>First Name</label>
        <input name="firstName" required value="${customer?.firstName ?? ""}">
        <label>Last Name</label>
        <input name="lastName" required value="${customer?.lastName ?? ""}">
        <label>Email</label>
        <input name="email" type="email" required value="${customer?.email ?? ""}">
        <label>Phone Number</label>
        <input name="phoneNumber" value="${customer?.phoneNumber ?? ""}">
        <label>Date of Birth</label>
        <input name="dateOfBirth" type="date" value="${customer?.dateOfBirth ?? ""}">
        <label>Account Status</label>
        <select name="accountStatus">
            <option ${customer?.accountStatus === "ACTIVE" ? "selected" : ""}>ACTIVE</option>
            <option ${customer?.accountStatus === "INACTIVE" ? "selected" : ""}>INACTIVE</option>
        </select>
    `, async (data) => {
        if (isEdit) {
            await apiCall(`/customers/${customer.customerId}`, "PUT", data);
            showToast("Customer updated");
        } else {
            await apiCall("/customers", "POST", data);
            showToast("Customer created");
        }
        loadCustomers();
    });
}

// ---------------- Policies ----------------
let customersCache = [];

async function loadPolicies() {
    try {
        const [policies, customers] = await Promise.all([
            apiCall("/policies"),
            apiCall("/customers")
        ]);
        customersCache = customers;

        const nameFor = (id) => {
            const c = customers.find(cu => cu.customerId === id);
            return c ? `${c.firstName} ${c.lastName}` : `#${id}`;
        };

        const tbody = document.getElementById("policyTableBody");
        tbody.innerHTML = policies.map(p => `
            <tr>
                <td>${p.policyId}</td>
                <td>${p.policyNumber}</td>
                <td>${p.policyName}</td>
                <td>${p.policyType ?? "-"}</td>
                <td>₹${Number(p.premiumAmount).toLocaleString("en-IN")}</td>
                <td>${p.coverageTerm ?? "-"}</td>
                <td>${p.effectiveStartDate ?? "-"}</td>
                <td>${nameFor(p.customerId)}</td>
                <td>
                    <a class="action-link edit" onclick='openPolicyForm(${JSON.stringify(p)})'>Edit</a>
                    <a class="action-link delete" onclick="deletePolicy(${p.policyId})">Delete</a>
                </td>
            </tr>
        `).join("") || `<tr><td colspan="9">No policies found.</td></tr>`;
    } catch (err) {
        showToast(err.message, "error");
    }
}

async function deletePolicy(id) {
    if (!confirm("Delete this policy?")) return;
    try {
        await apiCall(`/policies/${id}`, "DELETE");
        showToast("Policy deleted");
        loadPolicies();
    } catch (err) {
        showToast(err.message, "error");
    }
}

async function openPolicyForm(policy = null) {
    if (customersCache.length === 0) {
        customersCache = await apiCall("/customers");
    }
    const isEdit = !!policy;
    const options = customersCache.map(c =>
        `<option value="${c.customerId}" ${policy?.customerId === c.customerId ? "selected" : ""}>
            ${c.firstName} ${c.lastName} (#${c.customerId})
        </option>`
    ).join("");

    showModal(isEdit ? "Edit Policy" : "New Policy", `
        <label>Policy Number</label>
        <input name="policyNumber" required value="${policy?.policyNumber ?? ""}">
        <label>Policy Name</label>
        <input name="policyName" required value="${policy?.policyName ?? ""}">
        <label>Policy Type</label>
        <input name="policyType" value="${policy?.policyType ?? ""}" placeholder="HEALTH / LIFE / MOTOR">
        <label>Premium Amount</label>
        <input name="premiumAmount" type="number" step="0.01" required value="${policy?.premiumAmount ?? ""}">
        <label>Coverage Term</label>
        <input name="coverageTerm" value="${policy?.coverageTerm ?? ""}" placeholder="e.g. 1 Year">
        <label>Effective Start Date</label>
        <input name="effectiveStartDate" type="date" value="${policy?.effectiveStartDate ?? ""}">
        <label>Customer</label>
        <select name="customerId" required>${options}</select>
    `, async (data) => {
        data.premiumAmount = parseFloat(data.premiumAmount);
        data.customerId = parseInt(data.customerId, 10);

        if (isEdit) {
            await apiCall(`/policies/${policy.policyId}`, "PUT", data);
            showToast("Policy updated");
        } else {
            await apiCall("/policies", "POST", data);
            showToast("Policy created");
        }
        loadPolicies();
    });
}

// ---------------- Leads ----------------
async function loadLeads() {
    try {
        const leads = await apiCall("/leads");
        const tbody = document.getElementById("leadTableBody");
        tbody.innerHTML = leads.map(l => `
            <tr>
                <td>${l.leadId}</td>
                <td>${l.prospectName}</td>
                <td>${l.contactInfo ?? "-"}</td>
                <td>${l.referralSource ?? "-"}</td>
                <td><span class="status-pill ${l.leadStatus}">${l.leadStatus}</span></td>
                <td>${l.assignedAgentName ?? "-"}</td>
                <td>
                    <a class="action-link edit" onclick='openLeadForm(${JSON.stringify(l)})'>Edit</a>
                    <a class="action-link delete" onclick="deleteLead(${l.leadId})">Delete</a>
                </td>
            </tr>
        `).join("") || `<tr><td colspan="7">No leads found.</td></tr>`;
    } catch (err) {
        showToast(err.message, "error");
    }
}

async function deleteLead(id) {
    if (!confirm("Delete this lead?")) return;
    try {
        await apiCall(`/leads/${id}`, "DELETE");
        showToast("Lead deleted");
        loadLeads();
    } catch (err) {
        showToast(err.message, "error");
    }
}

function openLeadForm(lead = null) {
    const isEdit = !!lead;
    showModal(isEdit ? "Edit Lead" : "New Lead", `
        <label>Prospect Name</label>
        <input name="prospectName" required value="${lead?.prospectName ?? ""}">
        <label>Contact Info</label>
        <input name="contactInfo" value="${lead?.contactInfo ?? ""}">
        <label>Referral Source</label>
        <input name="referralSource" value="${lead?.referralSource ?? ""}">
        <label>Lead Status</label>
        <select name="leadStatus">
            ${["NEW", "CONTACTED", "QUALIFIED", "LOST"].map(s =>
                `<option ${lead?.leadStatus === s ? "selected" : ""}>${s}</option>`).join("")}
        </select>
        <label>Assigned Agent Name</label>
        <input name="assignedAgentName" value="${lead?.assignedAgentName ?? ""}">
    `, async (data) => {
        if (isEdit) {
            await apiCall(`/leads/${lead.leadId}`, "PUT", data);
            showToast("Lead updated");
        } else {
            await apiCall("/leads", "POST", data);
            showToast("Lead created");
        }
        loadLeads();
    });
}

// ---------------- Modal helper ----------------
function showModal(title, fieldsHtml, onSubmit) {
    document.getElementById("modalTitle").textContent = title;
    const form = document.getElementById("modalForm");
    form.innerHTML = fieldsHtml + `
        <div class="modal-actions">
            <button type="button" class="btn-secondary" onclick="closeModal()">Cancel</button>
            <button type="submit" class="btn-primary">Save</button>
        </div>
    `;

    form.onsubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        try {
            await onSubmit(data);
            closeModal();
        } catch (err) {
            showToast(err.message, "error");
        }
    };

    document.getElementById("modalOverlay").classList.add("open");
}

function closeModal() {
    document.getElementById("modalOverlay").classList.remove("open");
}

// ---------------- Init ----------------
loadCustomers();
loadPolicies();
loadLeads();
