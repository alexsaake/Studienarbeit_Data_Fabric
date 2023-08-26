export async function registerUser(axios, firstName, lastName, userName, email, password)
{
    return await axios.post(`api/Gateway/Auth/Register`,
        {
            firstName,
            lastName,
            userName,
            email,
            password
        });
}