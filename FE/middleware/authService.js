export async function registerUser(axios, firstName, lastName, userName, email, password)
{
    return await axios.post(`api/Gateway/auth/register`,
        {
            firstName,
            lastName,
            userName,
            email,
            password
        });
}

export async function getSecuredEndpoint(axios)
{
    return await axios.get(`/api/Gateway/auth/secured`);
}

