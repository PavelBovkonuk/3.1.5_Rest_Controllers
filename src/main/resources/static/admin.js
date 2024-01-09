const url = "http://localhost:8080/api/admin";

showUsers()

function showUsers() {
    $('#AllUsers').empty()
    fetch("/api/admin")
        .then(response => response.json())
        .then(user => {
            user.forEach(user => {
                let userRoles = []
                user.roles.forEach(role => userRoles.push(role.name.toString().replace("ROLE_", " ")))
                const users = `$(
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.name}</td>
                        <td>${user.age}</td>
                        <td>${userRoles}</td>
                        <td> <button type="button" class="btn btn-info" data-toggle="modal"
                            data-target="#edit" onclick="editModal(${user.id})">Edit</button>
                        </td>
                        <td><button type="button" class="btn btn-danger" data-toggle="modal"
                            data-target="#delete" onclick="deleteModal(${user.id})">Delete</button>
                        </td>
                    </tr>)`;
                $('#AllUsers').append(users)
            })
        })
}


addNewUser()

function addNewUser() {
    let newUser = document.forms["addNewUser"];
    newUser.addEventListener("submit", (event) => {
        event.preventDefault();
        let roles = [];
        for (let i = 0; i < newUser.roles.options.length; i++) {
            if (newUser.roles.options[i].selected) roles.push({
                id: newUser.roles.options[i].value,
                name: newUser.roles.options[i].text
            });
        }
        fetch("api/admin", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: newUser.name.value,
                age: newUser.age.value,
                password: newUser.password.value,
                roles: roles
            })
        }).then(() => {
            newUser.reset();
            showUsers();
            $('#usersTable').click();
        });
    });
}

let edit = document.forms["edit"];

async function editModal(id) {
    $('#editModal').modal('show');
    let user = await fetch("api/admin/" + id)
        .then(response => response.json());
    edit.id.value = user.id;
    edit.name.value = user.name;
    edit.age.value = user.age;
    edit.password.value = user.password;
    edit.roles.value = user.roles;
}

editUser();

function editUser() {
    edit.addEventListener("submit", (event) => {
        event.preventDefault();
        let roles = [];
        for (let i = 0; i < edit.roles.options.length; i++) {
            if (edit.roles.options[i].selected) roles.push({
                id: edit.roles.options[i].value,
                name: "ROLE_" + edit.roles.options[i].text
            });
        }
        fetch("api/admin/" + edit.id.value, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                id: edit.id.value,
                name: edit.name.value,
                age: edit.age.value,
                password: edit.password.value,
                roles: roles
            })
        }).then(() => {
            showUsers()
            $('#closeEdit').click();
        });
    });
}

let deleteUserForm = document.forms["delete"];

async function deleteModal(id) {
    $('#deleteModal').modal('show');
    let user = await fetch("api/admin/" + id)
        .then(response => response.json());
    deleteUserForm.id.value = user.id;
    deleteUserForm.name.value = user.name;
    deleteUserForm.age.value = user.age;
    deleteUserForm.roles.value = user.roles;
}

deleteUser();

function deleteUser() {
    deleteUserForm.addEventListener("submit", (event) => {
        event.preventDefault();
        fetch("api/admin/" + deleteUserForm.id.value, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(() => {
            showUsers()
            $('#closeDelete').click();
        });
    });
}