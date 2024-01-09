getUser();

function getUser() {
    fetch("api/user")
        .then(response => response.json())
        .then(user => {
            let userRoles = []
            user.roles.forEach(role => userRoles.push(role.name.toString().replace("ROLE_", " ")))
            const info = `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.age}</td>
                <td>${userRoles}</td>
            </tr>`;
            $('#userInfo').append(info);
        })
}