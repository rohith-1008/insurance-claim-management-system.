document.addEventListener('DOMContentLoaded', function() {
    // Base URL for your API endpoints
    const BASE_API_URL = '/api';

    // --- Utility Functions ---

    /**
     * Fetches data from a given URL with authentication.
     * @param {string} url - The API endpoint.
     * @returns {Promise<Object|null>} - The JSON response data or null on error.
     */
    async function fetchData(url) {
        try {
            const response = await fetch(url, {
                headers: {
                    // Include any necessary headers, e.g., for CSRF if enabled, or content type
                    'Content-Type': 'application/json'
                }
            });
            if (response.ok) {
                // If the response is OK, parse JSON, otherwise throw an error
                return await response.json();
            } else if (response.status === 401 || response.status === 403) {
                // Redirect to login if unauthorized or forbidden
                window.location.href = '/login';
                return null;
            } else {
                const errorText = await response.text();
                console.error(`Error fetching ${url}: ${response.status} - ${errorText}`);
                alert(`Error fetching data: ${response.statusText}`);
                return null;
            }
        } catch (error) {
            console.error(`Network or generic error fetching ${url}:`, error);
            alert('An unexpected error occurred. Please try again.');
            return null;
        }
    }

    /**
     * Sends data to a given URL (POST/PUT/DELETE) with authentication.
     * @param {string} url - The API endpoint.
     * @param {string} method - HTTP method (POST, PUT, DELETE).
     * @param {Object|null} data - The data to send (for POST/PUT).
     * @returns {Promise<Object|null>} - The JSON response data or null on error.
     */
    async function sendData(url, method, data = null) {
        try {
            const options = {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                }
            };
            if (data) {
                options.body = JSON.stringify(data);
            }

            const response = await fetch(url, options);

            if (response.ok) {
                // For DELETE or success with no content, response.json() might fail.
                // Check content-type header or response status before parsing.
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.indexOf("application/json") !== -1) {
                    return await response.json();
                } else {
                    return {}; // Return empty object for successful no-content responses
                }
            } else if (response.status === 401 || response.status === 403) {
                window.location.href = '/login';
                return null;
            } else {
                const errorText = await response.text();
                console.error(`Error ${method} to ${url}: ${response.status} - ${errorText}`);
                alert(`Operation failed: ${response.statusText} - ${errorText}`);
                return null;
            }
        } catch (error) {
            console.error(`Network or generic error ${method} to ${url}:`, error);
            alert('An unexpected error occurred. Please try again.');
            return null;
        }
    }

    /**
     * Formats a date string to YYYY-MM-DD.
     * @param {string} dateString - The date string from the backend.
     * @returns {string} - Formatted date.
     */
    function formatDate(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toISOString().split('T')[0];
    }

    /**
     * Set the current active link in the sidebar
     */
    function setActiveSidebarLink() {
        const path = window.location.pathname;
        const links = {
            '/dashboard': 'dashboard-link',
            '/customers': 'customers-link',
            '/policies': 'policies-link',
            '/claims': 'claims-link'
        };

        for (const [url, id] of Object.entries(links)) {
            const linkElement = document.getElementById(id);
            if (linkElement) {
                if (path.startsWith(url)) { // Use startsWith for general matching
                    linkElement.classList.add('active');
                } else {
                    linkElement.classList.remove('active');
                }
            }
        }
    }

    /**
     * Fetch authenticated user information and display it.
     * This needs a backend endpoint that exposes user details.
     * For now, we'll assume a `/user` endpoint. If not available, we can
     * modify the backend to expose it or remove this feature.
     */
    async function fetchAuthenticatedUser() {
        const usernameSpan = document.getElementById('current-username');
        if (usernameSpan) {
            try {
                // Spring Security provides a /user endpoint if you're using default configurations
                // for principal exposure, or you can create your own controller.
                const user = await fetchData('/user'); // Or '/api/auth/current-user' etc.
                if (user && user.name) { // 'name' for OAuth2, 'principal.username' for form login
                    usernameSpan.textContent = user.name || user.principal?.username || 'User!';
                } else {
                    // Fallback for form login principal if /user doesn't expose 'name'
                    const principal = await fetchData('/user');
                    if (principal && principal.principal && principal.principal.username) {
                        usernameSpan.textContent = principal.principal.username;
                    } else {
                        usernameSpan.textContent = 'User!';
                    }
                }
            } catch (error) {
                console.error("Could not fetch authenticated user:", error);
                usernameSpan.textContent = 'User!'; // Fallback
            }
        }
    }


    // --- Dashboard Specific Logic ---

    if (window.location.pathname === '/dashboard') {
        async function fetchDashboardData() {
            const totalCustomersElem = document.getElementById('totalCustomers');
            const activePoliciesElem = document.getElementById('activePolicies');
            const pendingClaimsElem = document.getElementById('pendingClaims');
            const approvedClaimsElem = document.getElementById('approvedClaims');
            const recentActivitiesTableBody = document.getElementById('recentActivitiesTableBody');

            // Fetch Customers count
            const customers = await fetchData(`${BASE_API_URL}/customers`);
            if (customers) {
                totalCustomersElem.textContent = customers.length;
            }

            // Fetch Policies count (assume all are "active" for simplicity, or add status)
            const policies = await fetchData(`${BASE_API_URL}/policies`);
            if (policies) {
                activePoliciesElem.textContent = policies.length;
            }

            // Fetch Claims counts based on status
            const claims = await fetchData(`${BASE_API_URL}/claims`);
            if (claims) {
                const pendingCount = claims.filter(claim => claim.status === 'PENDING').length;
                const approvedCount = claims.filter(claim => claim.status === 'APPROVED').length;
                pendingClaimsElem.textContent = pendingCount;
                approvedClaimsElem.textContent = approvedCount;

                // Populate Recent Activities (last 5 claims/policies/customers)
                populateRecentActivities(customers, policies, claims);
            }
        }

        function populateRecentActivities(customers, policies, claims) {
            const activities = [];

            // Add recent customers
            customers.slice(-3).forEach(c => activities.push({ date: c.id ? formatDate(new Date().toISOString()) : '', activity: 'New Customer Added', details: `${c.firstName} ${c.lastName}` }));
            // Add recent policies
            policies.slice(-3).forEach(p => activities.push({ date: p.startDate, activity: 'Policy Added/Renewed', details: `#${p.policyNumber} (${p.policyType})` }));
            // Add recent claims
            claims.slice(-3).forEach(cl => activities.push({ date: cl.claimDate, activity: 'Claim Submitted', details: `#${cl.claimNumber} (${cl.status})` }));

            // Sort by date (most recent first)
            activities.sort((a, b) => new Date(b.date) - new Date(a.date));

            const tableBody = document.getElementById('recentActivitiesTableBody');
            tableBody.innerHTML = ''; // Clear existing rows

            // Display top 5 activities
            activities.slice(0, 5).forEach(activity => {
                const row = tableBody.insertRow();
                row.insertCell().textContent = formatDate(activity.date);
                row.insertCell().textContent = activity.activity;
                row.insertCell().textContent = activity.details;
            });
        }

        fetchDashboardData();
    }


    // --- Customer Specific Logic ---

    const customerModal = document.getElementById('customerModal');
    const customerForm = document.getElementById('customerForm');
    const customersTableBody = document.getElementById('customersTableBody');
    const modalTitle = document.getElementById('modalTitle');

    // Open/Close Customer Modal
    window.openCustomerModal = function(mode, customer = {}) {
        customerModal.style.display = 'flex';
        customerForm.reset(); // Clear previous form data

        document.getElementById('customerId').value = customer.id || '';
        document.getElementById('firstName').value = customer.firstName || '';
        document.getElementById('lastName').value = customer.lastName || '';
        document.getElementById('email').value = customer.email || '';
        document.getElementById('phoneNumber').value = customer.phoneNumber || '';

        if (mode === 'add') {
            modalTitle.textContent = 'Add New Customer';
        } else if (mode === 'edit') {
            modalTitle.textContent = 'Edit Customer';
        }
    };

    window.closeCustomerModal = function() {
        customerModal.style.display = 'none';
    };

    // Fetch Customers
    async function fetchCustomers() {
        const customers = await fetchData(`${BASE_API_URL}/customers`);
        if (customers) {
            renderCustomersTable(customers);
        }
    }

    // Render Customers Table
    function renderCustomersTable(customers) {
        customersTableBody.innerHTML = ''; // Clear existing rows
        customers.forEach(customer => {
            const row = customersTableBody.insertRow();
            row.insertCell().textContent = customer.id;
            row.insertCell().textContent = customer.firstName;
            row.insertCell().textContent = customer.lastName;
            row.insertCell().textContent = customer.email;
            row.insertCell().textContent = customer.phoneNumber;

            const actionsCell = row.insertCell();
            actionsCell.classList.add('action-buttons');

            const editButton = document.createElement('button');
            editButton.classList.add('button', 'edit-button');
            editButton.innerHTML = '<i class="fas fa-edit"></i> Edit';
            editButton.onclick = () => openCustomerModal('edit', customer);

            const deleteButton = document.createElement('button');
            deleteButton.classList.add('button', 'delete-button');
            deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i> Delete';
            deleteButton.onclick = () => deleteCustomer(customer.id);

            actionsCell.appendChild(editButton);
            actionsCell.appendChild(deleteButton);
        });
    }

    // Handle Customer Form Submission
    if (customerForm) {
        customerForm.addEventListener('submit', async function(event) {
            event.preventDefault();

            const id = document.getElementById('customerId').value;
            const customer = {
                firstName: document.getElementById('firstName').value,
                lastName: document.getElementById('lastName').value,
                email: document.getElementById('email').value,
                phoneNumber: document.getElementById('phoneNumber').value
            };

            let result;
            if (id) {
                // Edit existing customer
                result = await sendData(`${BASE_API_URL}/customers/${id}`, 'PUT', customer);
            } else {
                // Add new customer
                result = await sendData(`${BASE_API_URL}/customers`, 'POST', customer);
            }

            if (result) {
                alert('Customer saved successfully!');
                closeCustomerModal();
                fetchCustomers(); // Refresh the table
            }
        });
    }

    // Delete Customer
    async function deleteCustomer(id) {
        if (confirm('Are you sure you want to delete this customer?')) {
            const result = await sendData(`${BASE_API_URL}/customers/${id}`, 'DELETE');
            if (result !== null) { // result is null if there was an error
                alert('Customer deleted successfully!');
                fetchCustomers(); // Refresh the table
            }
        }
    }


    // --- Policy Specific Logic ---

    const policyModal = document.getElementById('policyModal');
    const policyForm = document.getElementById('policyForm');
    const policiesTableBody = document.getElementById('policiesTableBody');
    const policyModalTitle = document.getElementById('policyModalTitle');
    const policyCustomerIdSelect = document.getElementById('policyCustomerId');

    // Open/Close Policy Modal
    window.openPolicyModal = async function(mode, policy = {}) {
        policyModal.style.display = 'flex';
        policyForm.reset();

        document.getElementById('policyId').value = policy.id || '';
        document.getElementById('policyNumber').value = policy.policyNumber || '';
        document.getElementById('policyType').value = policy.policyType || '';
        document.getElementById('policyStartDate').value = formatDate(policy.startDate) || '';
        document.getElementById('policyEndDate').value = formatDate(policy.endDate) || '';
        document.getElementById('coverageAmount').value = policy.coverageAmount || '';

        // Pre-select customer if in edit mode
        if (policy.customer && policyCustomerIdSelect) {
            policyCustomerIdSelect.value = policy.customer.id;
        } else if (policyCustomerIdSelect) {
            policyCustomerIdSelect.value = ''; // Reset for add mode
        }

        if (mode === 'add') {
            policyModalTitle.textContent = 'Add New Policy';
        } else if (mode === 'edit') {
            policyModalTitle.textContent = 'Edit Policy';
        }
    };

    window.closePolicyModal = function() {
        policyModal.style.display = 'none';
    };

    // Fetch Policies
    async function fetchPolicies() {
        const policies = await fetchData(`${BASE_API_URL}/policies`);
        if (policies) {
            renderPoliciesTable(policies);
        }
    }

    // Render Policies Table
    function renderPoliciesTable(policies) {
        policiesTableBody.innerHTML = '';
        policies.forEach(policy => {
            const row = policiesTableBody.insertRow();
            row.insertCell().textContent = policy.id;
            row.insertCell().textContent = policy.policyNumber;
            row.insertCell().textContent = policy.customer ? `${policy.customer.firstName} ${policy.customer.lastName}` : 'N/A';
            row.insertCell().textContent = policy.policyType;
            row.insertCell().textContent = formatDate(policy.startDate);
            row.insertCell().textContent = formatDate(policy.endDate);
            row.insertCell().textContent = policy.coverageAmount.toLocaleString('en-IN', { style: 'currency', currency: 'INR' });

            const actionsCell = row.insertCell();
            actionsCell.classList.add('action-buttons');

            const editButton = document.createElement('button');
            editButton.classList.add('button', 'edit-button');
            editButton.innerHTML = '<i class="fas fa-edit"></i> Edit';
            editButton.onclick = () => openPolicyModal('edit', policy);

            const deleteButton = document.createElement('button');
            deleteButton.classList.add('button', 'delete-button');
            deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i> Delete';
            deleteButton.onclick = () => deletePolicy(policy.id);

            actionsCell.appendChild(editButton);
            actionsCell.appendChild(deleteButton);
        });
    }

    // Populate Customer Dropdown for Policies
    async function fetchCustomersForPolicyDropdown() {
        if (!policyCustomerIdSelect) return; // Only run if element exists

        const customers = await fetchData(`${BASE_API_URL}/customers`);
        if (customers) {
            policyCustomerIdSelect.innerHTML = '<option value="">Select Customer</option>'; // Default option
            customers.forEach(customer => {
                const option = document.createElement('option');
                option.value = customer.id;
                option.textContent = `${customer.firstName} ${customer.lastName} (ID: ${customer.id})`;
                policyCustomerIdSelect.appendChild(option);
            });
        }
    }

    // Handle Policy Form Submission
    if (policyForm) {
        policyForm.addEventListener('submit', async function(event) {
            event.preventDefault();

            const id = document.getElementById('policyId').value;
            const customerId = document.getElementById('policyCustomerId').value; // Get selected customer ID

            const policy = {
                policyNumber: document.getElementById('policyNumber').value,
                policyType: document.getElementById('policyType').value,
                startDate: document.getElementById('policyStartDate').value,
                endDate: document.getElementById('policyEndDate').value,
                coverageAmount: parseFloat(document.getElementById('coverageAmount').value)
            };

            let result;
            if (id) {
                // Edit existing policy
                result = await sendData(`${BASE_API_URL}/policies/${id}`, 'PUT', policy);
            } else {
                // Add new policy, associating with customer
                result = await sendData(`${BASE_API_URL}/policies/${customerId}`, 'POST', policy);
            }

            if (result) {
                alert('Policy saved successfully!');
                closePolicyModal();
                fetchPolicies(); // Refresh the table
            }
        });
    }

    // Delete Policy
    async function deletePolicy(id) {
        if (confirm('Are you sure you want to delete this policy? This will also affect associated claims!')) {
            const result = await sendData(`${BASE_API_URL}/policies/${id}`, 'DELETE');
            if (result !== null) {
                alert('Policy deleted successfully!');
                fetchPolicies(); // Refresh the table
                if (window.location.pathname === '/dashboard') fetchDashboardData(); // Update dashboard
                if (window.location.pathname === '/claims') fetchClaims(); // Update claims if relevant
            }
        }
    }


    // --- Claim Specific Logic ---

    const claimModal = document.getElementById('claimModal');
    const claimForm = document.getElementById('claimForm');
    const claimsTableBody = document.getElementById('claimsTableBody');
    const claimModalTitle = document.getElementById('claimModalTitle');
    const claimPolicyIdSelect = document.getElementById('claimPolicyId');

    // Open/Close Claim Modal
    window.openClaimModal = async function(mode, claim = {}) {
        claimModal.style.display = 'flex';
        claimForm.reset();

        document.getElementById('claimId').value = claim.id || '';
        document.getElementById('claimNumber').value = claim.claimNumber || '';
        document.getElementById('claimDate').value = formatDate(claim.claimDate) || '';
        document.getElementById('claimDescription').value = claim.description || '';
        document.getElementById('claimAmount').value = claim.claimAmount || '';
        document.getElementById('claimStatus').value = claim.status || 'PENDING';

        // Pre-select policy if in edit mode
        if (claim.policy && claimPolicyIdSelect) {
            claimPolicyIdSelect.value = claim.policy.id;
        } else if (claimPolicyIdSelect) {
            claimPolicyIdSelect.value = ''; // Reset for add mode
        }

        if (mode === 'add') {
            claimModalTitle.textContent = 'Submit New Claim';
        } else if (mode === 'edit') {
            claimModalTitle.textContent = 'Edit Claim';
        }
    };

    window.closeClaimModal = function() {
        claimModal.style.display = 'none';
    };

    // Fetch Claims
    async function fetchClaims() {
        const claims = await fetchData(`${BASE_API_URL}/claims`);
        if (claims) {
            renderClaimsTable(claims);
        }
    }

    // Render Claims Table
    function renderClaimsTable(claims) {
        claimsTableBody.innerHTML = '';
        claims.forEach(claim => {
            const row = claimsTableBody.insertRow();
            row.insertCell().textContent = claim.id;
            row.insertCell().textContent = claim.claimNumber;
            row.insertCell().textContent = claim.policy ? `${claim.policy.policyNumber} (ID: ${claim.policy.id})` : 'N/A';
            row.insertCell().textContent = formatDate(claim.claimDate);
            row.insertCell().textContent = claim.description;
            row.insertCell().textContent = claim.claimAmount.toLocaleString('en-IN', { style: 'currency', currency: 'INR' });
            row.insertCell().textContent = claim.status;

            const actionsCell = row.insertCell();
            actionsCell.classList.add('action-buttons');

            const editButton = document.createElement('button');
            editButton.classList.add('button', 'edit-button');
            editButton.innerHTML = '<i class="fas fa-edit"></i> Edit';
            editButton.onclick = () => openClaimModal('edit', claim);

            const deleteButton = document.createElement('button');
            deleteButton.classList.add('button', 'delete-button');
            deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i> Delete';
            deleteButton.onclick = () => deleteClaim(claim.id);

            actionsCell.appendChild(editButton);
            actionsCell.appendChild(deleteButton);
        });
    }

    // Populate Policy Dropdown for Claims
    async function fetchPoliciesForClaimDropdown() {
        if (!claimPolicyIdSelect) return; // Only run if element exists

        const policies = await fetchData(`${BASE_API_URL}/policies`);
        if (policies) {
            claimPolicyIdSelect.innerHTML = '<option value="">Select Policy</option>'; // Default option
            policies.forEach(policy => {
                const option = document.createElement('option');
                option.value = policy.id;
                option.textContent = `${policy.policyNumber} (Customer: ${policy.customer ? policy.customer.firstName + ' ' + policy.customer.lastName : 'N/A'})`;
                claimPolicyIdSelect.appendChild(option);
            });
        }
    }

    // Handle Claim Form Submission
    if (claimForm) {
        claimForm.addEventListener('submit', async function(event) {
            event.preventDefault();

            const id = document.getElementById('claimId').value;
            const policyId = document.getElementById('claimPolicyId').value; // Get selected policy ID

            const claim = {
                claimNumber: document.getElementById('claimNumber').value,
                claimDate: document.getElementById('claimDate').value,
                description: document.getElementById('claimDescription').value,
                claimAmount: parseFloat(document.getElementById('claimAmount').value),
                status: document.getElementById('claimStatus').value
            };

            let result;
            if (id) {
                // Edit existing claim
                result = await sendData(`${BASE_API_URL}/claims/${id}`, 'PUT', claim);
            } else {
                // Submit new claim, associating with policy
                result = await sendData(`${BASE_API_URL}/claims/${policyId}`, 'POST', claim);
            }

            if (result) {
                alert('Claim saved successfully!');
                closeClaimModal();
                fetchClaims(); // Refresh the table
            }
        });
    }

    // Delete Claim
    async function deleteClaim(id) {
        if (confirm('Are you sure you want to delete this claim?')) {
            const result = await sendData(`${BASE_API_URL}/claims/${id}`, 'DELETE');
            if (result !== null) {
                alert('Claim deleted successfully!');
                fetchClaims(); // Refresh the table
                if (window.location.pathname === '/dashboard') fetchDashboardData(); // Update dashboard
            }
        }
    }

    // --- Initializations on page load ---
    setActiveSidebarLink();
    fetchAuthenticatedUser(); // Attempt to fetch and display current user
});